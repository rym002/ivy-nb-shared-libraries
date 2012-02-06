package com.munian.ivy.module.completion;

import com.munian.ivy.module.facade.IvyFacade;
import com.munian.ivy.module.options.IvyOptions;
import com.munian.ivy.module.exceptions.IvyException;
import com.munian.ivy.module.preferences.ProjectPreferences;
import javax.swing.text.JTextComponent;
import org.apache.ivy.Ivy;
import org.apache.ivyde.common.completion.IvyCodeCompletionProcessor;
import org.apache.ivyde.common.model.IvyModelSettings;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.ui.OpenProjects;
import org.netbeans.spi.editor.completion.CompletionProvider;
import org.netbeans.spi.editor.completion.CompletionTask;
import org.netbeans.spi.editor.completion.support.AsyncCompletionTask;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataObject;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;
import org.openide.windows.TopComponent;

/**
 * Copied from ivybeans
 * Modified to use ProjectPreferences
 * @author raymond
 */
public abstract class IvyCompletionProvider implements CompletionProvider {
    private static IvyOptions options = Lookup.getDefault().lookup(IvyOptions.class);

    @Override
    public CompletionTask createTask(int queryType, JTextComponent component) {
        if (queryType == COMPLETION_QUERY_TYPE || queryType == COMPLETION_ALL_QUERY_TYPE) {
            final Ivy ivy = getIvyInstance();
            return new AsyncCompletionTask(
                    new CompletionQuery(
                        ivy,
                        getCodeCompletionProcessor(ivy, new NetbeansIvyModelSettings(options, ivy)),
                    getProjectName()),
                    component);
        }
        return null;
    }
    
    protected abstract IvyCodeCompletionProcessor getCodeCompletionProcessor(Ivy ivy, IvyModelSettings settings);

    protected String getProjectName() {
        
        return (getProject() != null) ? getProject().getProjectDirectory().getName() : null;
    }

    
    @Override
    public int getAutoQueryTypes(JTextComponent component, String typedText) {
        return COMPLETION_QUERY_TYPE;
    }

    private Ivy getIvyInstance() {
        Ivy ivy = null;
        Project project = getProject();
        if (project != null) {
            IvyFacade facade = Lookup.getDefault().lookup(IvyFacade.class);
            try {
                ivy = facade.getIvy(project);
            } catch (IvyException ex) {
                Exceptions.printStackTrace(ex);
            }
        }else{
            ivy = Ivy.newInstance();
        }
        return ivy;
    }

    protected Project getProject() {
        Project[] projects = OpenProjects.getDefault().getOpenProjects();
        FileObject file = getPrimaryFile();
        if (file != null) {
            for (Project project : projects) {
                ProjectPreferences projectPreferences = project.getLookup().lookup(ProjectPreferences.class);
                if (projectPreferences!=null){
                    if (isProjectForFile(projectPreferences, file)){
                        return project;
                    }
                }
            }
        }
        return null;
    }

    protected FileObject getPrimaryFile() {
        TopComponent activatedTC = TopComponent .getRegistry().getActivated();
        if(activatedTC == null)
            return null;
        DataObject activeFile = activatedTC.getLookup().lookup(DataObject.class);
        if(activeFile == null)
            return null;

        return activeFile.getPrimaryFile();
    }
    
    protected abstract boolean isProjectForFile(ProjectPreferences projectPreferences, FileObject fileObject);
    
}
