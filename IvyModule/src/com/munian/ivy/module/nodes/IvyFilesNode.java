package com.munian.ivy.module.nodes;

import java.awt.Image;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.util.ImageUtilities;

/**
 *
 */
public class IvyFilesNode extends AbstractNode {
    private static final Image ICON_BADGE = ImageUtilities.loadImage("com/munian/ivy/module/ivyfile/logo16x16.gif");
    
    public IvyFilesNode(Children children) {
        super(children);
    }

    @Override
    public Image getIcon(int type) {
        return ICON_BADGE;
    }
}
