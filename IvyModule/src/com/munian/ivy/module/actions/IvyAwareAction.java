package com.munian.ivy.module.actions;

import com.munian.ivy.module.facade.IvyFacade;
import com.munian.ivy.module.util.ProjectUtility;
import com.munian.ivy.module.exceptions.IvyException;
import com.munian.ivy.module.preferences.ProjectPreferences;
import java.awt.event.ActionEvent;
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JSeparator;
import org.netbeans.api.project.Project;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionRegistration;
import org.openide.util.ContextAwareAction;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;
import org.openide.util.NbBundle.Messages;
import org.openide.util.Utilities;
import org.openide.util.actions.Presenter;

/**
 *
 */
@ActionID(category = "Ivy",
id = "com.munian.ivy.module.actions.IvyAwareAction")
@ActionRegistration(displayName = "#CTL_IvyAwareAction")
@ActionReferences({
    @ActionReference(path = "Projects/" + ProjectUtility.J2SE_PROJECT + "/Actions",position=2450),
    @ActionReference(path = "Projects/" + ProjectUtility.WEB_PROJECT + "/Actions",position=2450)
})
@Messages({"CTL_IvyAwareAction=Ivy","CTL_CleanCacheAction=Clean Cache","CTL_CleanRepositoryCacheAction=Repository"})
public class IvyAwareAction extends AbstractAction implements ContextAwareAction{
    /*
     * Path for items added to the root of the ivy submenu
     */
    public static final String SUB_MENU_PATH_ROOT = ProjectUtility.ACTIONS_PATH_SUBMENUS_ROOT + "Root";
    
    public static final String SUB_MENU_PATH_CLEAN = ProjectUtility.ACTIONS_PATH_SUBMENUS_ROOT + "Clean";
    public static final String SUB_MENU_PATH_CLEAN_REPOSITORY = ProjectUtility.ACTIONS_PATH_SUBMENUS_ROOT + "Repository";
    public static final String SUB_MENU_PATH_CLEAN_REPOSITORY_ONE = ProjectUtility.ACTIONS_PATH_SUBMENUS_ROOT + "RepositoryOne";

    @Override
    public void actionPerformed(ActionEvent e) {
        //
    }

    @Override
    public Action createContextAwareInstance(Lookup lkp) {
        return new IvyMenusAction(lkp);
    }
    private static class IvyMenusAction extends AbstractAction implements Presenter.Popup {

        private Project project;

        public IvyMenusAction(Lookup lkp) {
            Project _project = lkp.lookup(Project.class);
            ProjectPreferences preferences = _project.getLookup().lookup(ProjectPreferences.class);
            if (preferences == null) {
                setEnabled(false);
            } else {
                if (preferences.isIvyEnabled()){
                    this.project = _project;
                }else{
                    setEnabled(false);
                }
            }
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            //
        }

        private JMenuItem getCleanMenus() {
            JMenu menu = new JMenu(NbBundle.getMessage(IvyAwareAction.class, "CTL_CleanCacheAction"));

            List<? extends Action> actionsForPath =
                    Utilities.actionsForPath(SUB_MENU_PATH_CLEAN);
            for (Action a : actionsForPath) {
                menu.add(a);
            }

            menu.add(new JSeparator());
            JMenuItem repositoryMenu = getRepositoryMenus();
            menu.add(repositoryMenu);

            return menu;

        }

        private JMenuItem getRepositoryMenus() {
            JMenu menu = new JMenu(NbBundle.getMessage(IvyAwareAction.class, "CTL_CleanRepositoryCacheAction"));
            List<? extends Action> actionsForPath = Utilities.actionsForPath(SUB_MENU_PATH_CLEAN_REPOSITORY);
            for (Action a : actionsForPath) {
                menu.add(a);
            }
            IvyFacade facade = Lookup.getDefault().lookup(IvyFacade.class);
            try {
                String[] resolutionCaches = facade.getResolutionCacheNames(project);
                for (String string : resolutionCaches) {
                    actionsForPath = Utilities.actionsForPath(SUB_MENU_PATH_CLEAN_REPOSITORY_ONE);
                    for (Action a : actionsForPath) {
                        menu.add(a);
                        a.putValue(NAME, string);
                    }
                }
            } catch (IvyException ex) {
                Exceptions.printStackTrace(ex);
            }
            return menu;
        }

        @Override
        public JMenuItem getPopupPresenter() {
            JMenu menu = null;
            if (isEnabled()){
                menu = getMenus();
            }else{
                menu = new JMenu(NbBundle.getMessage(IvyAwareAction.class, "CTL_IvyAwareAction"));
                menu.setEnabled(false);
            }
            return menu;
        }

        private JMenu getMenus() {
            JMenu menu = new JMenu(NbBundle.getMessage(IvyAwareAction.class, "CTL_IvyAwareAction"));
            List<? extends Action> actionsForPath = Utilities.actionsForPath(SUB_MENU_PATH_ROOT);
            for (Action a : actionsForPath) {
                menu.add(a);
            }
            
            menu.add(getCleanMenus());
            
            return menu;
        }
    }
    
}
