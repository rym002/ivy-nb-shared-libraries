package com.munian.ivy.module.project;

import com.munian.ivy.module.options.IvyOptions;
import com.munian.ivy.module.preferences.ProjectPreferences;
import com.munian.ivy.module.util.ProjectUtility;
import org.netbeans.api.project.Project;
import org.netbeans.spi.project.LookupProvider.Registration.ProjectType;
import org.netbeans.spi.project.ProjectServiceProvider;
import org.netbeans.spi.project.ui.ProjectOpenedHook;
import org.openide.util.Lookup;

/**
 *
 */
@ProjectServiceProvider(projectTypes = {
    @ProjectType(id = ProjectUtility.J2SE_PROJECT),
    @ProjectType(id = ProjectUtility.WEB_PROJECT)
}, service = ProjectOpenedHook.class)
public class ProjectOpenedHookImpl extends ProjectOpenedHook {

    private Project project;

    public ProjectOpenedHookImpl(Project project) {
        this.project = project;
    }

    @Override
    protected void projectOpened() {
        IvyOptions optionsLookup = Lookup.getDefault().lookup(IvyOptions.class);
        ProjectPreferences preferences = project.getLookup().lookup(ProjectPreferences.class);
        optionsLookup.addPropertyChangeListener(preferences);
        preferences.autoResolveProject();
    }

    @Override
    protected void projectClosed() {
        IvyOptions optionsLookup = Lookup.getDefault().lookup(IvyOptions.class);
        ProjectPreferences preferences = project.getLookup().lookup(ProjectPreferences.class);
        optionsLookup.removePropertyChangeListener(preferences);
        preferences.clearListeners();
    }
}
