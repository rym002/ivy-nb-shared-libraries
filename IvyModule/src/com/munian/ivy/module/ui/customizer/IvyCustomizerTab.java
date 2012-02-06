package com.munian.ivy.module.ui.customizer;

import com.munian.ivy.module.options.IvyOptions;
import com.munian.ivy.module.facade.IvyFacade;
import com.munian.ivy.module.util.ProjectUtility;
import com.munian.ivy.module.preferences.EditablePreferences;
import javax.swing.JComponent;
import org.netbeans.api.project.Project;
import org.netbeans.spi.project.ui.support.ProjectCustomizer;
import org.netbeans.spi.project.ui.support.ProjectCustomizer.Category;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;

/**
 *
 */
@ProjectCustomizer.CompositeCategoryProvider.Registrations({
    @ProjectCustomizer.CompositeCategoryProvider.Registration(projectType = ProjectUtility.J2SE_PROJECT,position=1500),
    @ProjectCustomizer.CompositeCategoryProvider.Registration(projectType = ProjectUtility.WEB_PROJECT,position=1500)
})
public class IvyCustomizerTab implements ProjectCustomizer.CompositeCategoryProvider {



    @Override
    public Category createCategory(Lookup context) {
        return ProjectCustomizer.Category.create("ivy", NbBundle.getBundle(IvyCustomizerTab.class).getString("Customizer.Lbl"), null, new Category[0]);
    }

    @Override
    public JComponent createComponent(Category category, Lookup context) {
        Project project = context.lookup(Project.class);
        EditablePreferences projectPreferencesLookup = project.getLookup().lookup(EditablePreferences.class);
        IvyOptions optionsLookup = Lookup.getDefault().lookup(IvyOptions.class);
        IvyFacade ivyFacade = Lookup.getDefault().lookup(IvyFacade.class);
        
        CustomizerPanel panel = new CustomizerPanel(projectPreferencesLookup,optionsLookup,ivyFacade);
        category.setOkButtonListener(panel.getActionListener());
        return panel;
    }
}
