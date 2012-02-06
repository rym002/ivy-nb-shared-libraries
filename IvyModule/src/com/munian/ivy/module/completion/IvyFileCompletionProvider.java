package com.munian.ivy.module.completion;

import com.munian.ivy.module.preferences.ProjectPreferences;
import org.apache.ivy.Ivy;
import org.apache.ivyde.common.completion.IvyCodeCompletionProcessor;
import org.apache.ivyde.common.ivyfile.IvyModuleDescriptorModel;
import org.apache.ivyde.common.model.IvyModelSettings;
import org.netbeans.api.editor.mimelookup.MimeRegistration;
import org.netbeans.spi.editor.completion.CompletionProvider;
import org.openide.filesystems.FileObject;

/**
 * Completion provider for Ivy files, complementing schema based completion 
 * implemented by netbeans + IvyFileCompletionModelProvider.
 * <p>
 * This completion provider is responsible for what can't be deduced by schema 
 * only, like repository aware completion.
 * </p>
 * Copied from ivybeans
 * 
 * @author Xavier Hanin
 */
@MimeRegistration(mimeType="text/ivy+xml",service=CompletionProvider.class)
public class IvyFileCompletionProvider extends IvyCompletionProvider {

    @Override
    protected IvyCodeCompletionProcessor getCodeCompletionProcessor(
            Ivy ivy, IvyModelSettings settings) {
        return new IvyCodeCompletionProcessor(new IvyModuleDescriptorModel(settings));
    }

    @Override
    protected boolean isProjectForFile(ProjectPreferences projectPreferences, FileObject fileObject) {
        return fileObject.equals(projectPreferences.getIvyFile());
    }
    
    
}
