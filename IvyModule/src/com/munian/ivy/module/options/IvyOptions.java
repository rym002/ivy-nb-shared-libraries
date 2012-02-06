package com.munian.ivy.module.options;

import com.munian.ivy.module.util.PropertyChangeNotifier;
import java.util.Collection;

/**
 *
 */
public interface IvyOptions extends PropertyChangeNotifier{

    public static final String OPTIONS_CHANGE="IVY_OPTIONS_CHANGE";
    public static final String OPTIONS_TEMPLATE_CHANGE="IVY_OPTIONS_TEMPLATE_CHANGE";
    
    public Collection<String> getRetrieveSettingsTemplateNames();

    public IvyRetrieveSettings getIvyRetrieveSettingsTemplate(String templateName);

    public void saveIvyRetrieveSettingsTemplate(String templateName, IvyRetrieveSettings ivyRetrieveSettings);

    public void deleteRetrieveSettingsTemplate(String templateName);
        
    public void saveIvyOptions();

    public String getDefaultTemplateName();
    
    public String getOrganization();
    
    public void setOrganization(String organization);
    
    public String getOrganizationURL();
    
    public void setOrganizationURL(String organizationURL);

}
