package org.dhbw.mosbach.ai.tickets.beans;

import org.omnifaces.util.Faces;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Named;
import java.io.Serializable;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * @author Alexander Auch
 */
@ApplicationScoped
public class ResourceBundleProducer implements Serializable {
	private static final long serialVersionUID = 8116790746245922039L;
	private static final String BUNDLE_BASE_NAME = "mls.messages";

	@Produces
	@RequestScoped
	@Named("localeMessages")
	public ResourceBundleProxy getLocaleMessageBundle() {
		final Locale sessionLocale = Faces.getLocale();

		return new ResourceBundleProxy(ResourceBundle.getBundle(BUNDLE_BASE_NAME, sessionLocale));
	}
}
