package com.munian.ivy.module.completion;

import com.munian.ivy.module.options.IvyOptions;
import org.apache.ivy.Ivy;
import org.apache.ivyde.common.model.IvyModelSettings;
import org.openide.util.Exceptions;

/**
 *
 * @author raymond
 */
public class NetbeansIvyModelSettings implements IvyModelSettings{

    private IvyOptions ivyOptions;
    private Ivy ivy;

    public NetbeansIvyModelSettings(IvyOptions ivyOptions, Ivy ivy) {
        this.ivyOptions = ivyOptions;
        this.ivy = ivy;
    }
    
    
    @Override
    public String getDefaultOrganization() {
        return ivyOptions.getOrganization();
    }

    @Override
    public String getDefaultOrganizationURL() {
        return ivyOptions.getOrganizationURL();
    }

    @Override
    public Ivy getIvyInstance() {
        return ivy;
    }

    @Override
    public void logError(String string, Exception excptn) {
        Exceptions.printStackTrace(excptn);
    }
    
}
