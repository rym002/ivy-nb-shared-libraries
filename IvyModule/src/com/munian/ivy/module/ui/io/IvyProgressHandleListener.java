package com.munian.ivy.module.ui.io;

import org.apache.ivy.core.event.IvyEvent;
import org.apache.ivy.core.event.IvyListener;
import org.apache.ivy.core.event.download.EndArtifactDownloadEvent;
import org.apache.ivy.core.event.download.NeedArtifactEvent;
import org.apache.ivy.core.event.download.PrepareDownloadEvent;
import org.apache.ivy.core.event.resolve.EndResolveEvent;
import org.apache.ivy.core.event.resolve.StartResolveEvent;
import org.netbeans.api.progress.ProgressHandle;
import org.netbeans.api.progress.ProgressHandleFactory;

/**
 *
 */
public class IvyProgressHandleListener implements IvyListener {

    private String prefix;
    private ProgressHandle overallHandler;
    private int totalArtifacts;
    private int artifactsCompleted;

    public IvyProgressHandleListener(String prefix) {
        this.prefix = prefix;
    }



    @Override
    public void progress(IvyEvent ie) {
        if (ie instanceof StartResolveEvent) {
            overallHandler.start();
        } else if (ie instanceof EndResolveEvent) {
            overallHandler.finish();
        } else if (ie instanceof PrepareDownloadEvent) {
            PrepareDownloadEvent pde = (PrepareDownloadEvent) ie;
            totalArtifacts = pde.getArtifacts().length;
            overallHandler.switchToDeterminate(totalArtifacts);
        } else if (ie instanceof EndArtifactDownloadEvent) {
            if (totalArtifacts > 0) {
                artifactsCompleted++;
                if (artifactsCompleted <= totalArtifacts) {
                    overallHandler.progress(artifactsCompleted);
                }
            }
        } else if (ie instanceof NeedArtifactEvent) {
            NeedArtifactEvent evt = (NeedArtifactEvent) ie;
            overallHandler.progress(evt.getArtifact().getId().toString());
        }
    }
    
    public void setConf(String conf){
        overallHandler = ProgressHandleFactory.createHandle(prefix + " " + conf);
    }
}
