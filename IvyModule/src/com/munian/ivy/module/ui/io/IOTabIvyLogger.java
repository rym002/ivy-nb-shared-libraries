package com.munian.ivy.module.ui.io;

import org.apache.ivy.util.AbstractMessageLogger;
import org.apache.ivy.util.Message;
import org.openide.windows.IOProvider;
import org.openide.windows.InputOutput;

/**
 *
 * @author raymond
 */
public class IOTabIvyLogger extends AbstractMessageLogger {

    private InputOutput io;
    public static final String TAB_SUFFIX = " (ivy-resolve)";
    private String projectName;

    public IOTabIvyLogger(String projectName, String suffix) {
        this.projectName = projectName;
        io = IOProvider.getDefault().getIO(projectName + suffix, false);
        io.select();
    }

    @Override
    protected void doProgress() {
    }

    @Override
    protected void doEndProgress(String string) {
        
    }

    @Override
    public void error(String msg) {
        log("ERROR: " + msg, Message.MSG_ERR);
        getProblems().add("\tERROR: " + msg);
        getErrors().add(msg);
    }

    @Override
    public void log(String string, int i) {
        switch (i) {
            case Message.MSG_DEBUG:
            case Message.MSG_VERBOSE:
                //TODO set global options to allow showing debug/verbose messages
                break;
            case Message.MSG_INFO:
                io.getOut().println(string);
                break;
            case Message.MSG_WARN:
            case Message.MSG_ERR:
                io.getErr().println(string);
                break;
            default:
                io.getOut().println(string);
        }
    }

    @Override
    public void rawlog(String string, int i) {
        log(string, i);
    }

    public void completeLoggerJob() {
        io.getOut().close();
        io.getErr().close();
    }

    public void closeLogger() {
        io.closeInputOutput();
    }
}
