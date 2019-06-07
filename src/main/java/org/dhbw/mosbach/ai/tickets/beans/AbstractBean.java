package org.dhbw.mosbach.ai.tickets.beans;

import org.dhbw.mosbach.ai.tickets.cdi.ResourceBundleProxy;

import javax.faces.application.FacesMessage;
import javax.faces.application.NavigationHandler;
import javax.faces.context.FacesContext;
import javax.faces.context.Flash;
import javax.inject.Inject;
import java.io.Serializable;
import java.text.MessageFormat;

public class AbstractBean implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = 5791757036321568029L;

    @Inject
    private ResourceBundleProxy localeMessages;

    public AbstractBean()
    {
        super();
    }

    @Deprecated
    protected void addFacesMessage(FacesMessage.Severity severity, String message) {
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(severity, message, ""));
    }

    protected void addLocalizedFacesMessage(FacesMessage.Severity severity, String key, Object... params) {
        addFacesMessage(severity, MessageFormat.format(localeMessages.getString(key), params));
    }

    protected Flash getFlash()
    {
        return FacesContext.getCurrentInstance().getExternalContext().getFlash();
    }

    protected NavigationHandler getNavigationHandler() {
        return FacesContext.getCurrentInstance().getApplication().getNavigationHandler();
    }

    protected void navigateTo(String outcome) {
        getNavigationHandler().handleNavigation(FacesContext.getCurrentInstance(), null, outcome);
    }
}
