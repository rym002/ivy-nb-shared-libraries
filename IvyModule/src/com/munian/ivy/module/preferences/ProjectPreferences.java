package com.munian.ivy.module.preferences;

import com.munian.ivy.module.options.IvyRetrieveSettings;
import com.munian.ivy.module.util.PropertyChangeNotifier;
import java.beans.PropertyChangeListener;
import java.net.URL;
import java.util.Collection;
import org.openide.filesystems.FileObject;

/**
 *
 */
public interface ProjectPreferences extends PropertyChangeListener,PropertyChangeNotifier{

    public static final String PREFERENCES_CHANGED = "IVY_PREFERENCES_CHANGE";
    
    /**
     * @return the ivyEnabled
     */
    public boolean isIvyEnabled();

    public FileObject getIvyFile();
    
    public FileObject getIvySettingsFile();
    
    public Collection<FileObject> getIvyPropertiesFiles();
    
    public void clearListeners();
    /**
     * @return the autoResolve
     */
    public boolean isAutoResolve();
    
    public IvyRetrieveSettings getProjectRetrieveSettings();
    
    public URL getSharedLibraryLocation();
    
    public void autoResolveProject();
    
    public boolean isUseCachePath();
}
