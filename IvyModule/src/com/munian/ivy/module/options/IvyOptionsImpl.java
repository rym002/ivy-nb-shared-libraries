package com.munian.ivy.module.options;

import com.munian.ivy.module.util.Utilities;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.Collection;
import java.util.Map;
import java.util.TreeMap;
import java.util.prefs.Preferences;
import org.openide.util.NbBundle;
import org.openide.util.NbPreferences;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 */
@ServiceProvider(service = IvyOptions.class)
public class IvyOptionsImpl implements IvyOptions {

    public static final String TEMPLATE_NAMES = Utilities.PROPERTIES_PREFIX + "retrieve.template.names";
    public static final String PROP_ORGANIZATION = Utilities.PROPERTIES_PREFIX + "organization";
    public static final String PROP_ORGANIZATION_URL = Utilities.PROPERTIES_PREFIX + "organization.url";
    
    private Map<String,IvyRetrieveSettings> retrieveSettingsTemplates = new TreeMap<String, IvyRetrieveSettings>();
    private String organization;
    private String organizationURL;
    
    private final PropertyChangeSupport pcs = new PropertyChangeSupport(this);

    public IvyOptionsImpl() {
        loadIvyOptions();
    }

    @Override
    public Collection<String> getRetrieveSettingsTemplateNames() {
        return retrieveSettingsTemplates.keySet();
    }

    private Preferences getPreferences() {
        return NbPreferences.forModule(IvyOptionsImpl.class);
    }

    private String getMessage(String key) {
        return NbBundle.getMessage(IvyOptionsImpl.class, key);
    }

    private Collection<String> getPreferenceAsCollection(String key, String defaultMessageKey) {
        String value = getPreferences().get(key, getMessage(defaultMessageKey));
        return Utilities.stringToCollection(value);
    }

    private void populateRetrieveSettingsTemplates() {
        boolean emptyTemplates = "".equals(getPreferences().get(TEMPLATE_NAMES, ""));
        Collection<String> templateNamesArray = getPreferenceAsCollection(TEMPLATE_NAMES, "ivySettingsTemplatesCombo");
        for (String string : templateNamesArray) {
            IvyRetrieveSettings ivyRetrieveSettings = getIvyRetrieveSettingsTemplate(string);
            ivyRetrieveSettings.setPersisted(!emptyTemplates);
            retrieveSettingsTemplates.put(string, ivyRetrieveSettings);
        }
    }

    @Override
    public IvyRetrieveSettings getIvyRetrieveSettingsTemplate(String templateName) {
        IvyRetrieveSettings retVal = retrieveSettingsTemplates.get(templateName);
        if (retVal == null) {
            retVal = new IvyRetrieveSettings();
            retVal.getJarTypes().addAll(getPreferenceAsCollection(IvyRetrieveSettings.PROP_JAR_TYPES + "." + templateName, "ivyJarTypesText"));
            retVal.getSourceTypes().addAll(getPreferenceAsCollection(IvyRetrieveSettings.PROP_SOURCE_TYPES + "." + templateName, "ivySourceTypesText"));
            retVal.getSourceSuffixes().addAll(getPreferenceAsCollection(IvyRetrieveSettings.PROP_SOURCE_SUFFIXES + "." + templateName, "ivySourceSuffixesText"));
            retVal.getJavadocTypes().addAll(getPreferenceAsCollection(IvyRetrieveSettings.PROP_JAVADOC_TYPES + "." + templateName, "ivyJavadocTypesText"));
            retVal.getJavadocSuffixes().addAll(getPreferenceAsCollection(IvyRetrieveSettings.PROP_JAVADOC_SUFFIXES + "." + templateName, "ivyJavadocSuffixesText"));
        }
        return retVal;
    }

    @Override
    public void saveIvyRetrieveSettingsTemplate(String templateName, IvyRetrieveSettings ivyRetrieveSettings) {
        getPreferences().put(IvyRetrieveSettings.PROP_JAR_TYPES + "." + templateName, Utilities.collectionToString(ivyRetrieveSettings.getJarTypes()));
        getPreferences().put(IvyRetrieveSettings.PROP_SOURCE_TYPES + "." + templateName, Utilities.collectionToString(ivyRetrieveSettings.getSourceTypes()));
        getPreferences().put(IvyRetrieveSettings.PROP_SOURCE_SUFFIXES + "." + templateName, Utilities.collectionToString(ivyRetrieveSettings.getSourceSuffixes()));
        getPreferences().put(IvyRetrieveSettings.PROP_JAVADOC_TYPES + "." + templateName, Utilities.collectionToString(ivyRetrieveSettings.getJavadocTypes()));
        getPreferences().put(IvyRetrieveSettings.PROP_JAVADOC_SUFFIXES + "." + templateName, Utilities.collectionToString(ivyRetrieveSettings.getJavadocSuffixes()));
        retrieveSettingsTemplates.put(templateName, ivyRetrieveSettings);
        saveTemplateNames();
        ivyRetrieveSettings.setPersisted(true);
    }

    @Override
    public void deleteRetrieveSettingsTemplate(String templateName) {
        retrieveSettingsTemplates.remove(templateName);
        getPreferences().remove(IvyRetrieveSettings.PROP_JAR_TYPES + "." + templateName);
        getPreferences().remove(IvyRetrieveSettings.PROP_SOURCE_TYPES + "." + templateName);
        getPreferences().remove(IvyRetrieveSettings.PROP_SOURCE_SUFFIXES + "." + templateName);
        getPreferences().remove(IvyRetrieveSettings.PROP_JAVADOC_TYPES + "." + templateName);
        getPreferences().remove(IvyRetrieveSettings.PROP_JAVADOC_SUFFIXES + "." + templateName);
        saveTemplateNames();
    }

    private void saveTemplateNames() {
        getPreferences().put(TEMPLATE_NAMES, Utilities.collectionToString(getRetrieveSettingsTemplateNames()));
        firePropertyChange(OPTIONS_TEMPLATE_CHANGE);
    }

    @Override
    public String getDefaultTemplateName() {
        return getMessage("ivySettingsTemplatesCombo");
    }

    private void loadIvyOptions() {
        organizationURL=getPreferences().get(PROP_ORGANIZATION_URL, getMessage("organizationURLText"));
        organization=getPreferences().get(PROP_ORGANIZATION, getMessage("organizationText"));
        populateRetrieveSettingsTemplates();

    }

    @Override
    public void saveIvyOptions() {
        getPreferences().put(PROP_ORGANIZATION_URL, organizationURL);
        getPreferences().put(PROP_ORGANIZATION, organization);
        firePropertyChange(OPTIONS_CHANGE);
    }

    @Override
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        pcs.addPropertyChangeListener(listener);
    }

    @Override
    public void removePropertyChangeListener(PropertyChangeListener listener) {
        pcs.removePropertyChangeListener(listener);
    }
    
    private void firePropertyChange(String change){
        pcs.firePropertyChange(change, null, null);
    }
    /**
     * @return the retrieveSettingsTemplates
     */
    public Map<String,IvyRetrieveSettings> getRetrieveSettingsTemplates() {
        return retrieveSettingsTemplates;
    }

    /**
     * @return the organization
     */
    @Override
    public String getOrganization() {
        return organization;
    }

    /**
     * @param organization the organization to set
     */
    @Override
    public void setOrganization(String organization) {
        this.organization = organization;
    }

    /**
     * @return the organizationURL
     */
    @Override
    public String getOrganizationURL() {
        return organizationURL;
    }

    /**
     * @param organizationURL the organizationURL to set
     */
    @Override
    public void setOrganizationURL(String organizationURL) {
        this.organizationURL = organizationURL;
    }
}
