package com.munian.ivy.module.completion;

import com.munian.ivy.module.completion.IvyCompletionProvider;
import com.munian.ivy.module.preferences.ProjectPreferences;
import java.io.File;
import org.apache.ivy.Ivy;
import org.apache.ivyde.common.completion.IvyCodeCompletionProcessor;
import org.apache.ivyde.common.ivysettings.IvySettingsModel;
import org.apache.ivyde.common.model.IvyModelSettings;
import org.netbeans.api.editor.mimelookup.MimeRegistration;
import org.netbeans.spi.editor.completion.CompletionProvider;
import org.openide.filesystems.FileObject;

/**
 * Copied from ivybeans
 * @author xavier
 */
@MimeRegistration(mimeType="text/ivysettings+xml",service=CompletionProvider.class)
public class IvySettingsCompletionProvider extends IvyCompletionProvider {

    @Override
    protected IvyCodeCompletionProcessor getCodeCompletionProcessor(Ivy ivy, IvyModelSettings settings) {
        return new IvyCodeCompletionProcessor(
                new IvySettingsModel(settings, new File(getPrimaryFile().getPath())));
    }

    @Override
    protected boolean isProjectForFile(ProjectPreferences projectPreferences, FileObject fileObject) {
        return fileObject.equals(projectPreferences.getIvySettingsFile());
    }

}
