package com.munian.ivy.module.nodes;

import com.munian.ivy.module.preferences.ProjectPreferences;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import javax.swing.event.ChangeListener;
import org.netbeans.api.project.Project;
import org.netbeans.spi.project.ui.support.NodeList;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.util.ChangeSupport;
import org.openide.util.NbBundle;

/**
 *
 * @author raymond
 */
public class IvyNodeList implements NodeList<IvyNodeList.NodeKeys>, PropertyChangeListener {

    private ChangeSupport changeSupport = new ChangeSupport(this);
    private ProjectPreferences preferences;
    private IvyFilesNodeChildren filesNodeChildren;
    private IvyFilesNode ivyFilesNode;

    public IvyNodeList(Project project) {
        this.preferences = project.getLookup().lookup(ProjectPreferences.class);
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (ProjectPreferences.PREFERENCES_CHANGED.equals(evt.getPropertyName())) {
            changeSupport.fireChange();
        }
    }

    public enum NodeKeys {

        FILE_NODE;
    }

    @Override
    public List<IvyNodeList.NodeKeys> keys() {
        if (preferences.isIvyEnabled()) {
            return Arrays.asList(NodeKeys.values());
        } else {
            return Collections.EMPTY_LIST;
        }
    }

    @Override
    public void addChangeListener(ChangeListener l) {
        changeSupport.addChangeListener(l);
    }

    @Override
    public void removeChangeListener(ChangeListener l) {
        changeSupport.removeChangeListener(l);
    }

    @Override
    public Node node(IvyNodeList.NodeKeys key) {
        Node retVal = null;
        switch (key) {
            case FILE_NODE:
                retVal = getFileNode();
                break;
        }
        return retVal;
    }

    private Node getFileNode() {
        if (preferences.isIvyEnabled()) {
            if (filesNodeChildren == null) {
                filesNodeChildren = new IvyFilesNodeChildren(preferences);
                preferences.addPropertyChangeListener(filesNodeChildren);
            }
            if (ivyFilesNode==null){
                ivyFilesNode = new IvyFilesNode(Children.create(filesNodeChildren, true));
                ivyFilesNode.setDisplayName(NbBundle.getMessage(IvyNodeList.class, "Node.Files"));
                ivyFilesNode.setIconBaseWithExtension("com/munian/ivy/module/ivyfile/logo16x16.gif");
            }
        }
        return ivyFilesNode;
    }


    @Override
    public void addNotify() {
        preferences.addPropertyChangeListener(this);
    }

    @Override
    public void removeNotify() {
        preferences.removePropertyChangeListener(this);
        if (filesNodeChildren!=null){
            preferences.removePropertyChangeListener(filesNodeChildren);
        }
    }
}
