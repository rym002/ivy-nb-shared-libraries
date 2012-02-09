package com.munian.ivy.module.preferences;

import java.util.Collection;

/**
 *
 */
public interface EditablePreferences extends ProjectPreferences{

    public void addPropertyFile(String propertyFilePath);

    /**
     * @return the globalRetrieveSettingsName
     */
    public String getGlobalRetrieveSettingsName();

    public String getIvyFileString();

    public String getIvySettingsString();

    /**
     * @return the useGlobalRetrieveSettings
     */
    public boolean isUseGlobalRetrieveSettings();

    public void saveProjectPreferences();

    public void setIvyFile(String text);

    public void setIvySettingsFile(String text);

    public Collection<String> getIvyPropertiesFilesString();

    public void clearPropertiesFiles();

    public void setGlobalRetrieveSettingsName(String globalRetrieveSettingsName);

    public void setUseGlobalRetrieveSettings(boolean useGlobalRetrieveSettings);

    public void setAutoResolve(boolean autoResolve);

    public void setIvyEnabled(boolean ivyEnabled);
    
    public void setUseCachePath(boolean useCachePath);
    
}
