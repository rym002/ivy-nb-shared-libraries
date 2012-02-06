package com.munian.ivy.module.actions;

import com.munian.ivy.module.facade.IvyFacade;
import java.awt.event.ActionEvent;

import java.awt.event.ActionListener;
import org.netbeans.api.project.Project;
import org.openide.awt.ActionRegistration;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionID;
import org.openide.util.Lookup;
import org.openide.util.NbBundle.Messages;

public final class CleanCacheAction {

    @ActionID(category = "Clean",
    id = "com.munian.ivy.module.actions.CleanCacheAction.CleanAllCacheAction")
    @ActionRegistration(displayName = "#CTL_CleanAllCacheAction")
    @ActionReferences({
        @ActionReference(path = IvyAwareAction.SUB_MENU_PATH_CLEAN)
    })
    @Messages("CTL_CleanAllCacheAction=All")
    public static class CleanAllCacheAction implements ActionListener {

        private Project project;

        public CleanAllCacheAction(Project project) {
            this.project = project;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            IvyFacade cleaner = Lookup.getDefault().lookup(IvyFacade.class);
            cleaner.cleanAllCache(project);
        }
    }

    @ActionID(category = "Clean",
    id = "com.munian.ivy.module.actions.CleanCacheAction.CleanResolutionCacheAction")
    @ActionRegistration(displayName = "#CTL_CleanResolutionCacheAction")
    @ActionReferences({
        @ActionReference(path = IvyAwareAction.SUB_MENU_PATH_CLEAN)
    })
    @Messages("CTL_CleanResolutionCacheAction=Resolution")
    public static class CleanResolutionCacheAction implements ActionListener {

        private Project project;

        public CleanResolutionCacheAction(Project project) {
            this.project = project;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            IvyFacade cleaner = Lookup.getDefault().lookup(IvyFacade.class);
            cleaner.cleanResolutionCache(project);
        }
    }

    @ActionID(category = "Clean",
    id = "com.munian.ivy.module.actions.CleanCacheAction.CleanAllRepositoryCacheAction")
    @ActionRegistration(displayName = "#CTL_CleanAllRepositoryCacheAction")
    @ActionReferences({
        @ActionReference(path = IvyAwareAction.SUB_MENU_PATH_CLEAN_REPOSITORY)
    })
    @Messages("CTL_CleanAllRepositoryCacheAction=All")
    public static class CleanAllRepositoryCacheAction implements ActionListener {

        private Project project;

        public CleanAllRepositoryCacheAction(Project project) {
            this.project = project;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            IvyFacade cleaner = Lookup.getDefault().lookup(IvyFacade.class);
            cleaner.cleanAllRepositoryCache(project);
        }
    }

    @ActionID(category = "Clean",
    id = "com.munian.ivy.module.actions.CleanCacheAction.CleanOneRepositoryCacheAction")
    @ActionRegistration(displayName = "CleanOneRepositoryCacheAction")
    @ActionReferences({
        @ActionReference(path = IvyAwareAction.SUB_MENU_PATH_CLEAN_REPOSITORY_ONE)
    })
    public static class CleanOneRepositoryCacheAction implements ActionListener {

        private Project project;

        public CleanOneRepositoryCacheAction(Project project) {
            this.project = project;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            String name = e.getActionCommand();
            IvyFacade cleaner = Lookup.getDefault().lookup(IvyFacade.class);
            cleaner.cleanRepositoryCache(project, name);
        }
    }
}
