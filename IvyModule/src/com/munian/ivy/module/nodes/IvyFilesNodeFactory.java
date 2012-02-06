package com.munian.ivy.module.nodes;

import com.munian.ivy.module.util.ProjectUtility;
import org.netbeans.api.project.Project;
import org.netbeans.spi.project.ui.support.NodeFactory;
import org.netbeans.spi.project.ui.support.NodeList;

/**
 *
 * @author raymond
 */
@NodeFactory.Registration(projectType={ProjectUtility.J2SE_PROJECT,ProjectUtility.WEB_PROJECT})
public class IvyFilesNodeFactory implements NodeFactory{

    @Override
    public NodeList<?> createNodes(Project p) {
        return new IvyNodeList(p);
    }
    
}
