package com.munian.ivy.module.actions;

import com.munian.ivy.module.facade.IvyFacade;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import org.netbeans.api.project.Project;
import org.openide.awt.ActionRegistration;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionID;
import org.openide.util.Lookup;
import org.openide.util.NbBundle.Messages;

@ActionID(category = "Resolve",
id = "com.munian.ivy.module.actions.ResolveIvyLibrariesAction")
@ActionRegistration(displayName = "#CTL_ResolveIvyLibrariesAction")
@ActionReferences({
    @ActionReference(path = IvyAwareAction.SUB_MENU_PATH_ROOT)
})
@Messages("CTL_ResolveIvyLibrariesAction=Resolve Libraries")
public final class ResolveIvyLibrariesAction implements ActionListener {
    private Project project;
        
    public ResolveIvyLibrariesAction(Project project) {
        this.project=project;
    }

    @Override
    public void actionPerformed(ActionEvent ev) {
        IvyFacade resolver = Lookup.getDefault().lookup(IvyFacade.class);
        resolver.resolve(project);
    }    
}
