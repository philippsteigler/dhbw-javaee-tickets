package org.dhbw.mosbach.ai.tickets.database;

import com.google.common.base.Preconditions;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.Dependent;
import javax.enterprise.inject.spi.CDI;
import javax.persistence.EntityManager;
import javax.persistence.Id;
import javax.persistence.PersistenceContext;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.List;

/**
 * Database Tool functions.
 *
 * @author Alexander Auch
 */
@Dependent
public final class Tools {
	private static final Logger logger = LoggerFactory.getLogger(Tools.class);

	@PersistenceContext
	private EntityManager entityManager;

	private static final LoadingCache<Class<?>, Method> idGetters = CacheBuilder.newBuilder().maximumSize(20)
			.build(new CacheLoader<Class<?>, Method>() {
				@Override
				public Method load(Class<?> key) {
					return getEntityKeyGetterByReflection(key);
				}
			});

	public static <I> I getEntityKey(Object entity) {
		if (entity == null) {
			return null;
		}

		final Method idGetterMethod = getEntityKeyGetter(entity.getClass());

		try {
			return (idGetterMethod != null) ? (I) idGetterMethod.invoke(entity) : null;
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException exception) {
			throw new IllegalArgumentException("Could not invoke id getter", exception);
		}
	}

	/**
	 * Returns the getter method for the given entity's primary key by using
	 * reflection.
	 *
	 * @param entityClass entity class
	 * @return getter method or null if none found
	 */
	private static Method getEntityKeyGetterByReflection(Class<?> entityClass) {
		for (final Method method : entityClass.getMethods()) {
			if (method.isAnnotationPresent(Id.class)) {
				return method;
			}
		}

		for (final Field field : entityClass.getDeclaredFields()) {
			if (field.isAnnotationPresent(Id.class)) {
				final String fieldName = field.getName();
				final String getterMethodName = String.format("get%c%s", Character.toUpperCase(fieldName.charAt(0)), fieldName.substring(1));

				try {
					return entityClass.getMethod(getterMethodName);
				} catch (NoSuchMethodException | SecurityException e) {
					throw new IllegalArgumentException(
							String.format("Entity class %s does not have getter %s", entityClass.getName(), getterMethodName));
				}
			}
		}

		return null;
	}

	/**
	 * Returns the getter method for the given entity's primary key by using a
	 * cache lookup. If no entry exists in the cache, it is searched by
	 * reflection.
	 *
	 * @param entityClass entity class
	 * @return getter method or null if none found
	 */
	public static Method getEntityKeyGetter(Class<?> entityClass) {
		return idGetters.getUnchecked(entityClass);
	}

	/**
	 * @return an entity manager with dependent scope
	 */
	public static EntityManager getEntityManager() {
		return getInstance().entityManager;
	}

	/**
	 * Gets the singleton instance of this class
	 *
	 * @return {@link Tools} singleton
	 */
	private static Tools getInstance() {
		return CDI.current().select(Tools.class).get();
	}


	/**
	 * Tries to invoke {@link #loadFully(Object)} for each object in the list.
	 *
	 * @param list list
	 */
	public static void loadFully(List<?> list) {
		list.forEach(Tools::loadFully);
	}


	/**
	 * Tries to load lazy values of the given object by invoking all of its getter
	 * methods. The operation is not transitive, i.e., no getters of referenced
	 * objects will be called.
	 *
	 * @param obj object to be loaded fully
	 */
	public static void loadFully(Object obj) {
		Preconditions.checkNotNull(obj);

		for (final Method method : obj.getClass().getMethods()) {
			if (method.getName().startsWith("get") && (method.getParameterTypes().length == 0)) {
				try {
					final Object result = method.invoke(obj);

					if (result instanceof Collection) {
						((Collection<?>) result).size();
					}

					System.out.println(result);
				} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
					logger.info(String.format("Exception while invoking %s.%s", obj.getClass().getName(), method.getName()), e);
				}
			}
		}
	}
}
