package com.munian.ivy.module.ui.io;

import org.apache.ivy.plugins.repository.TransferEvent;
import org.apache.ivy.plugins.repository.TransferListener;
import org.netbeans.api.progress.ProgressHandle;
import org.netbeans.api.progress.ProgressHandleFactory;
import org.openide.util.NbBundle;

/**
 *
 */
public class IvyProgressHandleTransferListener implements TransferListener {

    private ProgressHandle currentDownload;
    private int current;
    private String prefix;

    public IvyProgressHandleTransferListener(String prefix) {
        this.prefix = prefix;
    }

    @Override
    public void transferProgress(TransferEvent te) {
        switch (te.getEventType()) {
            case TransferEvent.TRANSFER_INITIATED:
                if (currentDownload != null) {
                    completeDownload();
                }
                currentDownload = ProgressHandleFactory.createHandle(prefix + " " + NbBundle.getMessage(IvyProgressHandleTransferListener.class, "Downloading") + " " + shortenUrl(te.getResource().getName()));
                currentDownload.start();
                break;
            case TransferEvent.TRANSFER_STARTED:
                if (te.isTotalLengthSet()) {
                    currentDownload.switchToDeterminate((int) te.getTotalLength());
                }
                break;
            case TransferEvent.TRANSFER_PROGRESS:
                if (te.isTotalLengthSet()) {
                    current += te.getLength();
                    currentDownload.progress(current);
                }
                break;
            case TransferEvent.TRANSFER_COMPLETED:
                completeDownload();
                break;
            case TransferEvent.TRANSFER_ERROR:
                completeDownload();
                break;

        }
    }

    private void completeDownload() {
        currentDownload.finish();
        currentDownload = null;
        current = 0;
    }

    private String shortenUrl(String inMessage) {
        int lastSlash = inMessage.lastIndexOf("/");
        if (lastSlash == -1) {
            return inMessage;
        } else {
            String lastPart = inMessage.substring(lastSlash + 1);
            return lastPart;
        }
    }
}
