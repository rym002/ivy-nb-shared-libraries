package com.munian.ivy.module.nodes;

import com.munian.ivy.module.preferences.ProjectPreferences;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectNotFoundException;
import org.openide.nodes.ChildFactory;
import org.openide.nodes.FilterNode;
import org.openide.nodes.Node;
import org.openide.util.Exceptions;

/**
 *
 * @author raymond
 */
public class IvyFilesNodeChildren extends ChildFactory<FileObject> implements PropertyChangeListener {

    private ProjectPreferences preferences;

    public IvyFilesNodeChildren(ProjectPreferences preferences) {
        this.preferences = preferences;
    }

    @Override
    protected boolean createKeys(List<FileObject> toPopulate) {
        appendFileObject(toPopulate, preferences.getIvyFile());
        appendFileObject(toPopulate, preferences.getIvySettingsFile());
        for (FileObject fileObject : preferences.getIvyPropertiesFiles()) {
            appendFileObject(toPopulate, fileObject);
        }
        return true;
    }

    @Override
    protected Node createNodeForKey(FileObject key) {
        try {
            DataObject dataObject = DataObject.find(key);
            return dataObject.getNodeDelegate().cloneNode();
        } catch (DataObjectNotFoundException dataObjectNotFoundException) {
            Exceptions.printStackTrace(dataObjectNotFoundException);
        }
        return null;
    }

    private void appendFileObject(List<FileObject> toPopulate, FileObject fileObject) {
        if (fileObject != null) {
            toPopulate.add(fileObject);
        }
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (ProjectPreferences.PREFERENCES_CHANGED.equals(evt.getPropertyName())) {
            refresh(true);
        }
    }

}
