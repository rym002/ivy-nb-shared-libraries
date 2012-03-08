package com.munian.ivy.module.preferences;

import com.munian.ivy.module.facade.ArtifactUpdater;
import com.munian.ivy.module.facade.IvyFacade;
import com.munian.ivy.module.options.IvyOptions;
import com.munian.ivy.module.options.IvyRetrieveSettings;
import com.munian.ivy.module.util.ProjectUtility;
import com.munian.ivy.module.util.Utilities;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import org.netbeans.api.project.Project;
import org.netbeans.spi.project.LookupProvider.Registration.ProjectType;
import org.netbeans.spi.project.ProjectServiceProvider;
import org.netbeans.spi.project.support.ant.AntProjectHelper;
import org.netbeans.spi.project.support.ant.EditableProperties;
import org.openide.filesystems.*;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;

/**
 *
 */
@ProjectServiceProvider(projectTypes = {
    @ProjectType(id = ProjectUtility.J2SE_PROJECT),
    @ProjectType(id = ProjectUtility.WEB_PROJECT)
}, service = {EditablePreferences.class, ProjectPreferences.class})
public class ProjectPreferencesImpl implements FileChangeListener, EditablePreferences, ProjectPreferences {

    private static final String IVY_ENABLED_KEY = Utilities.PROPERTIES_PREFIX + "enabled";
    private static final String IVY_SETTINGS_FILE_KEY = Utilities.PROPERTIES_PREFIX + "settingsFile";
    private static final String IVY_FILE_KEY = Utilities.PROPERTIES_PREFIX + "file";
    private static final String IVY_PROPERTIES_FILES_KEY = Utilities.PROPERTIES_PREFIX + "propertiesFiles";
    private static final String IVY_GLOBAL_RETRIEVE_NAME_KEY = Utilities.PROPERTIES_PREFIX + "globalRetrieveSettingsName";
    private static final String IVY_AUTO_RESOLVE_KEY = Utilities.PROPERTIES_PREFIX + "autoResolve";
    private static final String IVY_USE_CACHE_PATH_KEY = Utilities.PROPERTIES_PREFIX + "useCachePath";
    private static final String IVY_ALL_CONFS_SELECTED_KEY = Utilities.PROPERTIES_PREFIX + "allConfsSelected";
    private static final String IVY_SELECTED_CONFS_KEY = Utilities.PROPERTIES_PREFIX + "selectedConfs";
    public static final String PROJECT_PROPERTIES_PATH = "nbproject/ivybean.properties";
    public static final String DEFAULT_SHARED_LIBRARY_PATH = "nbproject/private/ivyLibraries/nblibrary.properties";
    private Project project;
    private AntProjectHelper helper;
    private PropertyChangeSupport pcs = new PropertyChangeSupport(this);
    private boolean ivyEnabled;
    private FileObject ivySettingsFile;
    private FileObject ivyFile;
    private Collection<FileObject> ivyPropertiesFiles = new ArrayList<FileObject>();
    private boolean useGlobalRetrieveSettings;
    private String globalRetrieveSettingsName;
    private IvyRetrieveSettings projectRetrieveSettings;
    private boolean autoResolve;
    private boolean useCachePath=true;
    private Collection<String> selectedConfs = new ArrayList<String>();
    private boolean allConfsSelected;
    

    public ProjectPreferencesImpl(Project project) {
        this.project = project;
        helper = ProjectUtility.getAntProjectHelper(project);
        loadPreferences();
        isValidPreferences();
    }

    private EditableProperties getEditableProperties() {
        return helper.getProperties(PROJECT_PROPERTIES_PATH);

    }

    private void loadPreferences() {
        EditableProperties properties = getEditableProperties();
        ivyEnabled = Utilities.getBoolean(properties.get(IVY_ENABLED_KEY), false);
        IvyOptions optionsLookup = Lookup.getDefault().lookup(IvyOptions.class);

        if (ivyEnabled) {
            Collection<String> propertiesFiles = Utilities.stringToCollection(properties.get(IVY_PROPERTIES_FILES_KEY));
            setIvySettingsFile(properties.get(IVY_SETTINGS_FILE_KEY));
            setIvyFile(properties.get(IVY_FILE_KEY));

            for (String propertyFile : propertiesFiles) {
                addPropertyFile(propertyFile);
            }

            globalRetrieveSettingsName = properties.get(IVY_GLOBAL_RETRIEVE_NAME_KEY);
            if (Utilities.isNotNullOrEmpty(globalRetrieveSettingsName)) {
                updateTemplateSettings(optionsLookup, properties);
            } else {
                projectRetrieveSettings = loadIvyRetrieveSettings(properties);
                useGlobalRetrieveSettings = false;
            }

            autoResolve = Utilities.getBoolean(properties.get(IVY_AUTO_RESOLVE_KEY), true);
            useCachePath = Utilities.getBoolean(properties.get(IVY_USE_CACHE_PATH_KEY), true);
            allConfsSelected = Utilities.getBoolean(properties.get(IVY_ALL_CONFS_SELECTED_KEY), true);
            if (!allConfsSelected){
                Collection<String> savedSelectedConfs = Utilities.stringToCollection(properties.get(IVY_SELECTED_CONFS_KEY));
                selectedConfs.addAll(savedSelectedConfs);
            }
        } else {
            String templateName = optionsLookup.getDefaultTemplateName();
            projectRetrieveSettings = optionsLookup.getIvyRetrieveSettingsTemplate(templateName);
            useGlobalRetrieveSettings = true;
            globalRetrieveSettingsName = templateName;
            autoResolve = true;
        }

    }

    private void updateTemplateSettings(IvyOptions optionsLookup, EditableProperties properties) {
        String templateName = globalRetrieveSettingsName;
        if (!optionsLookup.getRetrieveSettingsTemplateNames().contains(templateName)) {
            templateName = optionsLookup.getDefaultTemplateName();
            globalRetrieveSettingsName = templateName;
        }
        projectRetrieveSettings = optionsLookup.getIvyRetrieveSettingsTemplate(templateName);
        if (!isJarTypesMatchProperties(projectRetrieveSettings, properties)) {
            properties.put(IvyRetrieveSettings.PROP_JAR_TYPES, Utilities.collectionToString(projectRetrieveSettings.getJarTypes()));
            saveProperties(properties);
        }
        useGlobalRetrieveSettings = true;

    }

    private boolean isJarTypesMatchProperties(IvyRetrieveSettings ivyRetrieveSettings, EditableProperties properties) {
        Collection<String> propertiesJarTypes = Utilities.stringToCollection(properties.get(IvyRetrieveSettings.PROP_JAR_TYPES));
        return Utilities.collectionsMatch(ivyRetrieveSettings.getJarTypes(), propertiesJarTypes);
    }

    private void saveProperties(EditableProperties properties) {
        helper.putProperties(PROJECT_PROPERTIES_PATH, properties);
        firePropertyChange();
    }

    private IvyRetrieveSettings loadIvyRetrieveSettings(EditableProperties properties) {
        IvyRetrieveSettings retrieveSettings = new IvyRetrieveSettings();
        retrieveSettings.getJarTypes().addAll(Utilities.stringToCollection(properties.getProperty(IvyRetrieveSettings.PROP_JAR_TYPES)));
        retrieveSettings.getJavadocSuffixes().addAll(Utilities.stringToCollection(properties.get(IvyRetrieveSettings.PROP_JAVADOC_SUFFIXES)));
        retrieveSettings.getJavadocTypes().addAll(Utilities.stringToCollection(properties.get(IvyRetrieveSettings.PROP_JAVADOC_TYPES)));
        retrieveSettings.getSourceSuffixes().addAll(Utilities.stringToCollection(properties.get(IvyRetrieveSettings.PROP_SOURCE_SUFFIXES)));
        retrieveSettings.getSourceTypes().addAll(Utilities.stringToCollection(properties.get(IvyRetrieveSettings.PROP_SOURCE_TYPES)));
        retrieveSettings.setPersisted(true);
        return retrieveSettings;
    }

    @Override
    public void autoResolveProject() {
        if (ivyEnabled) {
            if (autoResolve) {
                IvyFacade facade = Lookup.getDefault().lookup(IvyFacade.class);
                facade.resolve(project);
            }
        }
    }

    @Override
    public void saveProjectPreferences() {
        EditableProperties properties = getEditableProperties();
        validateSettings();
        if (ivyEnabled) {
            saveIvyProjectPreferences(properties);
            enableSharedLibrary();
            autoResolveProject();
        } else {
            disableSharedLibrary();
            deleteIvyProjectPreferences(properties);
        }
        saveProperties(properties);
    }

    private void validateSettings(){
        if (ivyFile==null){
            ivyEnabled=false;
        }
    }
    private void saveIvyProjectPreferences(EditableProperties properties) {
        if (useGlobalRetrieveSettings) {
            deleteIvyRetrieveSettingsProject(properties);
            properties.put(IVY_GLOBAL_RETRIEVE_NAME_KEY, globalRetrieveSettingsName);
        } else {
            properties.remove(IVY_GLOBAL_RETRIEVE_NAME_KEY);
            saveIvyRetrieveSettingsProject(properties);
        }

        properties.put(IVY_AUTO_RESOLVE_KEY, Boolean.toString(autoResolve));
        properties.put(IVY_ENABLED_KEY, Boolean.toString(ivyEnabled));
        properties.put(IVY_USE_CACHE_PATH_KEY, Boolean.toString(useCachePath));

        properties.put(IVY_FILE_KEY, getIvyFileString());
        properties.put(IVY_SETTINGS_FILE_KEY, getIvySettingsString());
        properties.put(IVY_PROPERTIES_FILES_KEY, Utilities.collectionToString(getIvyPropertiesFilesString()));
        properties.put(IVY_ALL_CONFS_SELECTED_KEY, Boolean.toString(allConfsSelected));
        if (allConfsSelected){
            properties.remove(IVY_SELECTED_CONFS_KEY);
        }else{
            properties.put(IVY_SELECTED_CONFS_KEY, Utilities.collectionToString(selectedConfs));
        }
        properties.put(IvyRetrieveSettings.PROP_JAR_TYPES, Utilities.collectionToString(projectRetrieveSettings.getJarTypes()));

    }

    private void saveIvyRetrieveSettingsProject(EditableProperties properties) {
        IvyRetrieveSettings retrieveSettings = projectRetrieveSettings;
        properties.put(IvyRetrieveSettings.PROP_JAR_TYPES, Utilities.collectionToString(retrieveSettings.getJarTypes()));
        properties.put(IvyRetrieveSettings.PROP_JAVADOC_SUFFIXES, Utilities.collectionToString(retrieveSettings.getJavadocSuffixes()));
        properties.put(IvyRetrieveSettings.PROP_JAVADOC_TYPES, Utilities.collectionToString(retrieveSettings.getJavadocTypes()));
        properties.put(IvyRetrieveSettings.PROP_SOURCE_SUFFIXES, Utilities.collectionToString(retrieveSettings.getSourceSuffixes()));
        properties.put(IvyRetrieveSettings.PROP_SOURCE_TYPES, Utilities.collectionToString(retrieveSettings.getSourceTypes()));
    }

    private void deleteIvyRetrieveSettingsProject(EditableProperties properties) {
        properties.remove(IvyRetrieveSettings.PROP_JAR_TYPES);
        properties.remove(IvyRetrieveSettings.PROP_JAVADOC_SUFFIXES);
        properties.remove(IvyRetrieveSettings.PROP_JAVADOC_TYPES);
        properties.remove(IvyRetrieveSettings.PROP_SOURCE_SUFFIXES);
        properties.remove(IvyRetrieveSettings.PROP_SOURCE_TYPES);
    }

    private void deleteIvyProjectPreferences(EditableProperties properties) {
        setIvyFile(null);
        setIvySettingsFile(null);
        clearPropertiesFiles();

        properties.remove(IVY_AUTO_RESOLVE_KEY);
        properties.remove(IVY_ENABLED_KEY);
        properties.remove(IVY_GLOBAL_RETRIEVE_NAME_KEY);

        properties.remove(IVY_FILE_KEY);
        properties.remove(IVY_SETTINGS_FILE_KEY);
        properties.remove(IVY_PROPERTIES_FILES_KEY);

        properties.remove(IvyRetrieveSettings.PROP_JAR_TYPES);
        properties.remove(IVY_USE_CACHE_PATH_KEY);
        properties.remove(IVY_SELECTED_CONFS_KEY);
        properties.remove(IVY_ALL_CONFS_SELECTED_KEY);
        
        deleteIvyRetrieveSettingsProject(properties);
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (ivyEnabled) {
            if (IvyOptions.OPTIONS_CHANGE.equals(evt.getPropertyName())) {
            } else if (IvyOptions.OPTIONS_TEMPLATE_CHANGE.equals(evt.getPropertyName())) {
                if (useGlobalRetrieveSettings) {
                    EditableProperties properties = getEditableProperties();
                    IvyOptions optionsLookup = Lookup.getDefault().lookup(IvyOptions.class);
                    updateTemplateSettings(optionsLookup, properties);
                }
            }
        }
    }

    private void firePropertyChange() {
        pcs.firePropertyChange(PREFERENCES_CHANGED, null, null);
    }

    @Override
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        pcs.addPropertyChangeListener(listener);
    }

    @Override
    public void removePropertyChangeListener(PropertyChangeListener listener) {
        pcs.removePropertyChangeListener(listener);
    }

    @Override
    public void clearListeners() {
        PropertyChangeListener[] changeListeners = pcs.getPropertyChangeListeners();
        for (PropertyChangeListener propertyChangeListener : changeListeners) {
            pcs.removePropertyChangeListener(propertyChangeListener);
        }
    }

    private boolean isValidPreferences() {
        boolean retVal = true;
        if (ivyEnabled) {
            if (ivyFile == null) {
                retVal = false;
            }
            ivyEnabled = retVal;
        }

        return retVal;
    }

    @Override
    public void fileFolderCreated(FileEvent fe) {
        fe.getFile().removeFileChangeListener(this);
    }

    @Override
    public void fileDataCreated(FileEvent fe) {
        fe.getFile().removeFileChangeListener(this);
    }

    @Override
    public void fileChanged(FileEvent fe) {
        autoResolveProject();
    }

    @Override
    public void fileDeleted(FileEvent fe) {
        fe.getFile().removeFileChangeListener(this);
    }

    @Override
    public void fileRenamed(FileRenameEvent fe) {
        // For now stop listening, maybe can adjust settings to reflect new file if necessary
        fe.getFile().removeFileChangeListener(this);
    }

    @Override
    public void fileAttributeChanged(FileAttributeEvent fe) {
        // DO Nothing
    }

    @Override
    public void setIvySettingsFile(String text) {
        stopListenFile(ivySettingsFile);
        try {
            if (text != null && !text.isEmpty()) {
                ivySettingsFile = FileUtil.toFileObject(new File(new URL(text).toURI()));
                startListenFile(ivySettingsFile);
            } else {
                ivySettingsFile = null;
            }
        } catch (URISyntaxException ex) {
            Exceptions.printStackTrace(ex);
        } catch (MalformedURLException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    private void startListenFile(FileObject fileObject) {
        if (fileObject != null) {
            fileObject.addFileChangeListener(this);
        }
    }

    private void stopListenFile(FileObject fileObject) {
        if (fileObject != null) {
            fileObject.removeFileChangeListener(this);
        }
    }

    @Override
    public void setIvyFile(String text) {
        stopListenFile(ivyFile);
        try {
            if (text != null && !text.isEmpty()) {
                ivyFile = FileUtil.toFileObject(new File(new URL(text).toURI()));
                startListenFile(ivyFile);
            } else {
                ivyFile = null;
            }
        } catch (URISyntaxException ex) {
            Exceptions.printStackTrace(ex);
        } catch (MalformedURLException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    @Override
    public void addPropertyFile(String propertyFilePath) {
        FileObject fo = FileUtil.toFileObject(new File(propertyFilePath));
        if (!ivyPropertiesFiles.contains(fo)) {
            ivyPropertiesFiles.add(fo);
            startListenFile(fo);
        }
    }

    @Override
    public Collection<String> getIvyPropertiesFilesString() {
        Collection<String> retval = new ArrayList<String>();
        for (FileObject propertyFile : ivyPropertiesFiles) {
            retval.add(propertyFile.getPath());
        }
        return retval;
    }

    @Override
    public String getIvyFileString() {
        if (ivyFile != null) {
            try {
                return ivyFile.getURL().toExternalForm();
            } catch (FileStateInvalidException ex) {
                Exceptions.printStackTrace(ex);
            }
        }
        return "";
    }

    @Override
    public String getIvySettingsString() {
        if (ivySettingsFile != null) {
            try {
                return ivySettingsFile.getURL().toExternalForm();
            } catch (FileStateInvalidException ex) {
                Exceptions.printStackTrace(ex);
            }
        }
        return "";
    }

    /**
     * @return the ivyEnabled
     */
    @Override
    public boolean isIvyEnabled() {
        return ivyEnabled;
    }

    /**
     * @return the useGlobalRetrieveSettings
     */
    @Override
    public boolean isUseGlobalRetrieveSettings() {
        return useGlobalRetrieveSettings;
    }

    /**
     * @return the globalRetrieveSettingsName
     */
    @Override
    public String getGlobalRetrieveSettingsName() {
        return globalRetrieveSettingsName;
    }

    /**
     * @return the projectRetrieveSettings
     */
    @Override
    public IvyRetrieveSettings getProjectRetrieveSettings() {
        return projectRetrieveSettings;
    }

    /**
     * @return the autoResolve
     */
    @Override
    public boolean isAutoResolve() {
        return autoResolve;
    }

    /**
     * @return the ivySettingsFile
     */
    @Override
    public FileObject getIvySettingsFile() {
        return ivySettingsFile;
    }

    /**
     * @return the ivyFile
     */
    @Override
    public FileObject getIvyFile() {
        return ivyFile;
    }

    /**
     * @return the ivyPropertiesFiles
     */
    @Override
    public Collection<FileObject> getIvyPropertiesFiles() {
        return ivyPropertiesFiles;
    }

    @Override
    public void clearPropertiesFiles() {
        for (FileObject fileObject : ivyPropertiesFiles) {
            stopListenFile(fileObject);
        }
        ivyPropertiesFiles.clear();
    }

    @Override
    public void setGlobalRetrieveSettingsName(String globalRetrieveSettingsName) {
        this.globalRetrieveSettingsName = globalRetrieveSettingsName;
    }

    @Override
    public void setUseGlobalRetrieveSettings(boolean useGlobalRetrieveSettings) {
        this.useGlobalRetrieveSettings = useGlobalRetrieveSettings;
    }

    @Override
    public void setAutoResolve(boolean autoResolve) {
        this.autoResolve = autoResolve;
    }

    @Override
    public void setIvyEnabled(boolean ivyEnabled) {
        this.ivyEnabled = ivyEnabled;
    }

    @Override
    public URL getSharedLibraryLocation() {
        if (!helper.isSharableProject()) {
            enableSharedLibrary();
        }
        try {
            return helper.resolveFile(helper.getLibrariesLocation()).toURI().toURL();
        } catch (MalformedURLException ex) {
            Exceptions.printStackTrace(ex);
        }
        return null;
    }

    private void enableSharedLibrary() {
        if (!helper.isSharableProject()) {
            helper.setLibrariesLocation(DEFAULT_SHARED_LIBRARY_PATH);
        }
    }

    private void disableSharedLibrary() {
        if (helper.isSharableProject()) {
            ArtifactUpdater artifactUpdater = project.getLookup().lookup(ArtifactUpdater.class);
            artifactUpdater.removeIvyArtifacts(this);
            if (helper.getLibrariesLocation().equals(DEFAULT_SHARED_LIBRARY_PATH)) {
                helper.setLibrariesLocation("");
            }
        }
    }

    @Override
    public boolean isUseCachePath() {
        return useCachePath;
    }

    @Override
    public void setUseCachePath(boolean useCachePath) {
        this.useCachePath = useCachePath;
    }

    @Override
    public Collection<String> getSelectedConfs() {
        return selectedConfs;
    }

    @Override
    public boolean isAllConfsSelected() {
        return allConfsSelected;
    }

    @Override
    public void setAllConfsSelected(boolean allConfsSelected) {
        this.allConfsSelected = allConfsSelected;
    }
    
}
