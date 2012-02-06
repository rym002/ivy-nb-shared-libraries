package com.munian.ivy.module.nodes;

import com.munian.ivy.module.preferences.ProjectPreferences;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectNotFoundException;
import org.openide.nodes.ChildFactory;
import org.openide.nodes.Node;
import org.openide.util.Exceptions;

/**
 *
 * @author raymond
 */
public class IvyFilesNodeChildren extends ChildFactory<DataObject> implements PropertyChangeListener {

    private ProjectPreferences preferences;
    
    public IvyFilesNodeChildren(ProjectPreferences preferences) {
        this.preferences = preferences;
    }
    
    @Override
    protected boolean createKeys(List<DataObject> toPopulate) {
        appendDataObject(toPopulate, preferences.getIvyFile());
        appendDataObject(toPopulate, preferences.getIvySettingsFile());
        for (FileObject fileObject : preferences.getIvyPropertiesFiles()) {
            appendDataObject(toPopulate, fileObject);
        }
        return true;
    }

    @Override
    protected Node createNodeForKey(DataObject key) {
        return key.getNodeDelegate();
    }
    
    
    
    private void appendDataObject(List<DataObject> toPopulate,FileObject fileObject){
        if (fileObject!=null){
            try {
                DataObject dataObject = DataObject.find(fileObject);
                toPopulate.add(dataObject);
            } catch (DataObjectNotFoundException dataObjectNotFoundException) {
                Exceptions.printStackTrace(dataObjectNotFoundException);
            }
        }
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (ProjectPreferences.PREFERENCES_CHANGED.equals(evt.getPropertyName())){
            refresh(true);
        }
    }
    
}
