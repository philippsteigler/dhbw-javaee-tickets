package org.dhbw.mosbach.ai.tickets.security;

import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.security.Principal;
import java.util.Optional;

import javax.annotation.Priority;
import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.annotation.security.RunAs;
import javax.ejb.EJBContext;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.http.HttpServletRequest;

import org.dhbw.mosbach.ai.tickets.model.Roles;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Interceptor
@CDIRoleCheck
@Priority(Interceptor.Priority.APPLICATION)
public class SecurityInterceptor implements Serializable {
	private static final String UNAUTHENTICATED_USERNAME = "ANONYMOUS";
	private static final long serialVersionUID = -609666584947836079L;
	private static final Logger logger = LoggerFactory.getLogger(SecurityInterceptor.class);

	@Inject
	private HttpServletRequest request;

	private HttpServletRequest getRequest() {
		if (request != null)
			return request;

		final FacesContext fc = FacesContext.getCurrentInstance();
		if (fc == null)
			return null;

		final ExternalContext ec = fc.getExternalContext();
		return (ec != null) ? (HttpServletRequest) ec.getRequest() : null;
	}

	private EJBContext getEJBContext() {
		try {
			final InitialContext ic = new InitialContext();
			return (EJBContext) ic.lookup("java:comp/EJBContext");
		} catch (final NamingException e) {
			// this can happen if no ejb context is active (e.g., CDI bean)
			return null;
		}
	}

	@AroundInvoke
	public Object checkRoles(InvocationContext ctx) throws Exception {
		final RolesAllowed allowedRoles = getAllowedRoles(ctx);

		final String methodName = String.format("%s.%s", ctx.getTarget().getClass().getSimpleName(),
				ctx.getMethod().getName());

		final boolean methodHasPermitAll = methodHasAnnotation(ctx, PermitAll.class);
		final RunAs runAs = lookupAnnotation(ctx, RunAs.class);
		final boolean runsAsAdmin = runsAsAdmin(runAs);

		final HttpServletRequest request = getRequest();
		final EJBContext context = getEJBContext();
		final Optional<Principal> principal = Optional.ofNullable(getPrincipal(request, context));

		final String userName = principal.map(Principal::getName).orElse(UNAUTHENTICATED_USERNAME);
		final boolean isAuthenticatedUser = principal.isPresent();

		logger.info("Security check for method {}, roles: {}, user {}", methodName,
				allowedRoles != null ? allowedRoles : "", userName);

		if (methodHasPermitAll)
			logger.info("Method {} has permitall annotation.", methodName);

		if (runsAsAdmin) {
			logger.info("Security check for method {}, roles: {}, runas(admin)", methodName,
					allowedRoles != null ? allowedRoles : "");
		}

		boolean accessAllowed = methodHasPermitAll || runsAsAdmin;

		if ((allowedRoles != null) && !accessAllowed) {
			for (final String role : allowedRoles.value()) {
				accessAllowed = isUserInRole(role, request, context);

				// Role '*' for allowing any authenticated user
				accessAllowed |= "*".equals(role) && isAuthenticatedUser;

				if (accessAllowed)
					break;
			}
		}

		if (accessAllowed) {
			logger.info("Access to method {} granted for user {}", methodName, userName);
			return ctx.proceed();
		} else {
			logger.warn("Access to method {} denied for user {}", methodName, userName);
			throw new SecurityException(String.format("access to method %s not allowed", methodName));
		}
	}

	private boolean isUserInRole(String role, HttpServletRequest request, EJBContext context) {
		return (request != null) ? request.isUserInRole(role) : (context != null) && context.isCallerInRole(role);
	}

	private Principal getPrincipal(HttpServletRequest request, EJBContext context) {
		try {
			return (request != null) ? request.getUserPrincipal() : (context != null) ? context.getCallerPrincipal() : null;
		} catch(final IllegalStateException e) {
			logger.warn("Exception in getPrincipal", e);
			return null;
		}
	}

	private boolean runsAsAdmin(RunAs runAs) {
		final Optional<String> runAsRole = Optional.ofNullable(runAs).map(RunAs::value);

		return runAsRole.isPresent() && Roles.ADMIN.equalsIgnoreCase(runAsRole.get());
	}

	private RolesAllowed getAllowedRoles(InvocationContext ctx) {
		return lookupAnnotation(ctx, RolesAllowed.class);
	}

	private <T extends Annotation> T lookupAnnotation(InvocationContext ctx, Class<T> annotationClass) {
		T annotation = ctx.getMethod().getAnnotation(annotationClass);
		Class<?> clazz = ctx.getTarget().getClass();

		// unfortunately, we have to search the whole inheritance hierarchy, since
		// RolesAllowed will not be inherited.
		while ((annotation == null) && (clazz != Object.class)) {
			annotation = clazz.getAnnotation(annotationClass);
			clazz = clazz.getSuperclass();
		}

		return annotation;
	}

	private boolean methodHasAnnotation(InvocationContext ctx, Class<? extends Annotation> annotationClass) {
		return ctx.getMethod().isAnnotationPresent(annotationClass);
	}
}
