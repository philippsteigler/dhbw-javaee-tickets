package org.dhbw.mosbach.ai.tickets.cdi;

import java.util.Enumeration;
import java.util.ResourceBundle;

/**
 * @author Alexander Auch
 */
public class ResourceBundleProxy {
    private final ResourceBundle bundle;

    protected ResourceBundleProxy() {
        this.bundle = null;
    }

    public ResourceBundleProxy(ResourceBundle bundle) {
        this.bundle = bundle;
    }

    public String getString(String key) {
        assert bundle != null;
        return bundle.getString(key);
    }

    public Enumeration<String> getKeys() {
        assert bundle != null;
        return bundle.getKeys();
    }
}
