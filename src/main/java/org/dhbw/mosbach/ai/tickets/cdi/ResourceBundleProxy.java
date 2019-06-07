package org.dhbw.mosbach.ai.tickets.cdi;

import java.util.Enumeration;
import java.util.ResourceBundle;

/**
 * This class is only needed because weld cannot proxy {@link ResourceBundle}.
 *
 * @author Alexander.Auch
 *
 */
public class ResourceBundleProxy
{
    private final ResourceBundle bundle;

    /**
     * Needed for weld.
     */
    protected ResourceBundleProxy()
    {
        this.bundle = null;
    }

    public ResourceBundleProxy(ResourceBundle bundle)
    {
        this.bundle = bundle;
    }

    public String getString(String key)
    {
        return bundle.getString(key);
    }

    public Enumeration<String> getKeys()
    {
        return bundle.getKeys();
    }
}

