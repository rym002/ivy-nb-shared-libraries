package com.munian.ivy.module.util;

import java.lang.reflect.Method;
import org.netbeans.api.project.Project;
import org.netbeans.spi.project.support.ant.AntProjectHelper;
import org.openide.util.Exceptions;

/**
 *
 * @author raymond
 */
public class ProjectUtility {
    public static final String J2SE_PROJECT = "org-netbeans-modules-java-j2seproject";
    public static final String WEB_PROJECT = "org-netbeans-modules-web-project";
    
    public static final String ACTIONS_PATH_SUBMENUS_ROOT = "IvySubMenu/";
    
    public static AntProjectHelper getAntProjectHelper(Project project) {
        try {
            Method getAntProjectHelperMethod = project.getClass().getMethod(
                    "getAntProjectHelper"); //NOI18N
            if (getAntProjectHelperMethod != null) {
                AntProjectHelper helper = (AntProjectHelper) 
                        getAntProjectHelperMethod.invoke(project);

                return helper;
            }
        } catch (NoSuchMethodException nme) {
            Exceptions.printStackTrace(nme);
        } catch (Exception ex) {
            Exceptions.printStackTrace(ex);
        }

        return null;
    }
}
