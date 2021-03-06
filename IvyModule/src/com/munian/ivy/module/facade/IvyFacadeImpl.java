package com.munian.ivy.module.facade;

import com.munian.ivy.module.exceptions.IvyException;
import com.munian.ivy.module.facade.libraryparser.AbstractLibraryParser;
import com.munian.ivy.module.facade.libraryparser.cachepath.CachePathLibraryParser;
import com.munian.ivy.module.facade.libraryparser.retrieving.RetrievingLibraryParser;
import com.munian.ivy.module.preferences.ProjectPreferences;
import com.munian.ivy.module.ui.io.IOTabIvyLogger;
import com.munian.ivy.module.ui.io.IvyProgressHandleListener;
import com.munian.ivy.module.ui.io.IvyProgressHandleTransferListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.text.ParseException;
import java.util.*;
import org.apache.ivy.Ivy;
import org.apache.ivy.core.cache.RepositoryCacheManager;
import org.apache.ivy.core.cache.ResolutionCacheManager;
import org.apache.ivy.core.module.descriptor.ModuleDescriptor;
import org.apache.ivy.core.report.ResolveReport;
import org.apache.ivy.core.resolve.ResolveOptions;
import org.apache.ivy.core.settings.IvySettings;
import org.apache.ivy.plugins.parser.ModuleDescriptorParserRegistry;
import org.apache.ivy.util.Message;
import org.netbeans.api.progress.ProgressHandle;
import org.netbeans.api.progress.ProgressHandleFactory;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.ProjectInformation;
import org.netbeans.api.project.ProjectUtils;
import org.netbeans.spi.project.SubprojectProvider;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileStateInvalidException;
import org.openide.filesystems.FileUtil;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;
import org.openide.util.RequestProcessor;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 */
@ServiceProvider(service = IvyFacade.class)
public class IvyFacadeImpl implements IvyFacade {

    private RequestProcessor requestProcessor = new RequestProcessor(IvyFacade.class);

    @Override
    public void cleanResolutionCache(final Project project) {
        requestProcessor.post(new Runnable() {

            @Override
            public void run() {
                ProgressHandle progressHandle = ProgressHandleFactory.createHandle(NbBundle.getMessage(IvyFacade.class, "CleanResolution"));
                progressHandle.start();
                try {

                    cleanResolutionCache(getIvy(project), progressHandle);
                } catch (IvyException ex) {
                    Exceptions.printStackTrace(ex);
                }
                progressHandle.finish();
            }
        });
    }

    private void cleanResolutionCache(Ivy ivy, ProgressHandle progressHandle) {
        progressHandle.setDisplayName(NbBundle.getMessage(IvyFacade.class, "CleanResolution"));
        ResolutionCacheManager resolutionCacheManager = ivy.getSettings().getResolutionCacheManager();
        resolutionCacheManager.clean();
    }

    private void cleanAllRepositoryCache(Ivy ivy, ProgressHandle progressHandle) {
        progressHandle.setDisplayName(NbBundle.getMessage(IvyFacade.class, "CleanAllRespository"));
        RepositoryCacheManager[] managers = ivy.getSettings().getRepositoryCacheManagers();
        for (RepositoryCacheManager repositoryCacheManager : managers) {
            repositoryCacheManager.clean();
        }
    }

    private void cleanRespoitoryCache(Ivy ivy, String name, ProgressHandle progressHandle) {
        progressHandle.setDisplayName(NbBundle.getMessage(IvyFacade.class, "CleanResolution") + " " + name);
        RepositoryCacheManager manager = ivy.getSettings().getRepositoryCacheManager(name);
        manager.clean();
    }

    @Override
    public Ivy getIvy(Project project) throws IvyException {
        ProjectPreferences projectPreferences = project.getLookup().lookup(ProjectPreferences.class);
        if (projectPreferences != null && projectPreferences.isIvyEnabled()) {
            return getIvy(projectPreferences.getIvySettingsFile(), projectPreferences.getIvyPropertiesFiles());
        } else {
            return null;
        }
    }

    @Override
    public void cleanRepositoryCache(final Project project, final String name) {
        requestProcessor.post(new Runnable() {

            @Override
            public void run() {
                ProgressHandle progressHandle = ProgressHandleFactory.createHandle(NbBundle.getMessage(IvyFacade.class, "CleanRespository"));
                progressHandle.start();
                try {
                    cleanRespoitoryCache(getIvy(project), name, progressHandle);
                } catch (IvyException ex) {
                    Exceptions.printStackTrace(ex);
                }
                progressHandle.finish();
            }
        });
    }

    @Override
    public void cleanAllRepositoryCache(final Project project) {
        requestProcessor.post(new Runnable() {

            @Override
            public void run() {
                ProgressHandle progressHandle = ProgressHandleFactory.createHandle(NbBundle.getMessage(IvyFacade.class, "CleanRespository"));
                progressHandle.start();
                try {
                    cleanAllRepositoryCache(getIvy(project), progressHandle);
                } catch (IvyException ex) {
                    Exceptions.printStackTrace(ex);
                }
                progressHandle.finish();
            }
        });
    }

    @Override
    public void cleanAllCache(final Project project) {
        requestProcessor.post(new Runnable() {

            @Override
            public void run() {
                ProgressHandle progressHandle = ProgressHandleFactory.createHandle(NbBundle.getMessage(IvyFacade.class, "CleanResolution"));
                progressHandle.start();
                try {
                    Ivy ivy = getIvy(project);
                    cleanAllRepositoryCache(ivy, progressHandle);
                    cleanResolutionCache(ivy, progressHandle);
                } catch (IvyException ex) {
                    Exceptions.printStackTrace(ex);
                }
                progressHandle.finish();
            }
        });
    }

    @Override
    public String[] getResolutionCacheNames(Project project) throws IvyException {
        Ivy ivy = getIvy(project);
        RepositoryCacheManager[] managers = ivy.getSettings().getRepositoryCacheManagers();
        String[] retVal = new String[managers.length];
        for (int i = 0; i < managers.length; i++) {
            RepositoryCacheManager repositoryCacheManager = managers[i];
            retVal[i] = repositoryCacheManager.getName();
        }
        return retVal;
    }

    @Override
    public void resolve(final Project project) {
        requestProcessor.post(new Runnable() {

            @Override
            public void run() {
                IOTabIvyLogger logger = null;
                try {
                    ProjectInformation projectInformation = ProjectUtils.getInformation(project);
                    ProjectPreferences projectPreferences = project.getLookup().lookup(ProjectPreferences.class);
                    ArtifactUpdater updater = project.getLookup().lookup(ArtifactUpdater.class);
                    Ivy ivy = getIvy(projectPreferences.getIvySettingsFile(), projectPreferences.getIvyPropertiesFiles());
                    logger = new IOTabIvyLogger(projectInformation.getDisplayName(), IOTabIvyLogger.TAB_SUFFIX);

                    logger.log(NbBundle.getMessage(IvyFacade.class, "StartResolve"), Message.MSG_INFO);

                    IvyProgressHandleListener transferListener = new IvyProgressHandleListener(projectInformation.getDisplayName() + " " + NbBundle.getMessage(IvyProgressHandleListener.class, "Resolving"));
                    IvyProgressHandleTransferListener ivyProgressHandleTransferListener = new IvyProgressHandleTransferListener(projectInformation.getDisplayName());
                    ivy.getLoggerEngine().setDefaultLogger(logger);
                    ivy.getEventManager().addIvyListener(transferListener);
                    ivy.getEventManager().addTransferListener(ivyProgressHandleTransferListener);
                    URL ivyFileLocation = projectPreferences.getIvyFile().getURL();
                    String[] confs;
                    if (projectPreferences.isAllConfsSelected()) {
                        confs = getConfs(ivyFileLocation, ivy);
                    } else {
                        confs = projectPreferences.getSelectedConfs().toArray(new String[0]);
                    }
                    SubprojectProvider subprojectProvider = project.getLookup().lookup(SubprojectProvider.class);
                    transferListener.setConf(Arrays.deepToString(confs));
                    ResolveOptions resolveOption = new ResolveOptions().setConfs(confs);
                    resolveOption.setValidate(ivy.getSettings().doValidate());
                    ResolveReport report = ivy.resolve(ivyFileLocation, resolveOption);

                    logger.log(NbBundle.getMessage(IvyFacade.class, "EndResolve"), Message.MSG_INFO);
                    AbstractLibraryParser libraryParser;
                    if (projectPreferences.isUseCachePath()) {
                        libraryParser = Lookup.getDefault().lookup(CachePathLibraryParser.class);
                    } else {
                        libraryParser = Lookup.getDefault().lookup(RetrievingLibraryParser.class);
                    }
                    logger.log(NbBundle.getMessage(IvyFacade.class, "StartLibrariesUpdate"), Message.MSG_INFO);

                    List<ParsedConfArtifacts> parsedArtifacts = libraryParser.parseArtifactsInReport(logger, ivy, report, projectPreferences, updater, subprojectProvider);
                    updater.update(projectPreferences, parsedArtifacts);
                    logger.log(NbBundle.getMessage(IvyFacade.class, "EndLibrariesUpdate"), Message.MSG_INFO);

                } catch (IvyException ex) {
                    Exceptions.printStackTrace(ex);
                } catch (ParseException ex) {
                    Exceptions.printStackTrace(ex);
                } catch (IOException ex) {
                    Exceptions.printStackTrace(ex);
                } finally {
                    if (logger != null) {
                        logger.completeLoggerJob();
                    }
                }
            }
        });
    }

    @Override
    public void testIvySettings(String settingsFile, Collection<String> propertiesFiles) throws IvyException {
        getIvySettings(settingsFile, propertiesFiles);
    }

    public IvySettings getIvySettings(FileObject settingsFile, Collection<FileObject> propertiesFiles) throws IvyException {
        try {
            URL settingsFileURL = getSettingsURL(settingsFile);
            List<File> propertiesFilesFiles = toFileList(propertiesFiles);
            return getIvySettings(settingsFileURL, propertiesFilesFiles);
        } catch (FileStateInvalidException ex) {
            throw new IvyException(ex);
        } catch (MalformedURLException ex) {
            throw new IvyException(ex);
        }
    }

    public IvySettings getIvySettings(String settingsFile, Collection<String> propertiesFiles) throws IvyException {
        try {
            URL settingsFileURL = getSettingsURL(settingsFile);
            List<File> propertiesFilesFiles = toFileListFromString(propertiesFiles);

            return getIvySettings(settingsFileURL, propertiesFilesFiles);
        } catch (MalformedURLException ex) {
            throw new IvyException(ex);
        }
    }

    private List<File> toFileList(Collection<FileObject> files) {
        List<File> retVal = new ArrayList<File>(files.size());
        for (FileObject fileObject : files) {
            retVal.add(FileUtil.toFile(fileObject));
        }
        return retVal;
    }

    private List<File> toFileListFromString(Collection<String> files) {
        List<File> retVal = new ArrayList<File>(files.size());
        for (String file : files) {
            retVal.add(new File(file));
        }
        return retVal;
    }

    private String[] getConfs(FileObject ivyFile, FileObject settingsFile, Collection<FileObject> propertiesFiles) throws IvyException {
        try {
            URL ivyFileURL = ivyFile.getURL();
            URL settingsFileURL = getSettingsURL(settingsFile);
            List<File> propertiesFilesFiles = toFileList(propertiesFiles);
            Ivy ivy = getIvy(settingsFileURL, propertiesFilesFiles);
            return getConfs(ivyFileURL, ivy);
        } catch (Exception ex) {
            throw new IvyException(ex);
        }
    }

    private String[] getConfs(URL ivyFile, Ivy ivy) throws IvyException {
        ModuleDescriptor moduleDescriptor = getModuleDescriptor(ivy, ivyFile);

        return moduleDescriptor.getConfigurationsNames();

    }

    @Override
    public ModuleDescriptor getModuleDescriptor(Project project) throws IvyException {
        Ivy ivy = getIvy(project);
        if (ivy != null) {
            ProjectPreferences projectPreferences = project.getLookup().lookup(ProjectPreferences.class);
            try {
                URL ivyFile = projectPreferences.getIvyFile().getURL();
                return getModuleDescriptor(ivy, ivyFile);
            } catch (Exception ex) {
                throw new IvyException(ex);
            }
        }
        return null;
    }

    public ModuleDescriptor getModuleDescriptor(Ivy ivy, URL ivyFile) throws IvyException {
        try {
            return ModuleDescriptorParserRegistry.getInstance().parseDescriptor(ivy.getSettings(), ivyFile, false);
        } catch (Exception ex) {
            throw new IvyException(ex);
        }
    }

    private URL getSettingsURL(FileObject settingsFile) throws MalformedURLException, FileStateInvalidException {
        if (settingsFile != null) {
            return settingsFile.getURL();
        }
        return null;
    }

    private URL getSettingsURL(String settingsFile) throws MalformedURLException {
        URL settingsFileURL = null;
        if (settingsFile != null && !settingsFile.isEmpty()) {
            settingsFileURL = new URL(settingsFile);
        }
        return settingsFileURL;
    }

    public IvySettings getIvySettings(URL settingsFile, Collection<File> propertiesFiles) throws IvyException {
        IvySettings ivySettings = new IvySettings();
        try {
            for (File propertyFile : propertiesFiles) {
                InputStream is = new FileInputStream(propertyFile);
                Properties props = new Properties();
                props.load(is);
                ivySettings.addAllVariables(props);
            }
            if (settingsFile != null) {
                ivySettings.load(settingsFile);
            }
        } catch (IOException ex) {
            throw new IvyException(ex);
        } catch (ParseException ex) {
            throw new IvyException(ex);
        }

        return ivySettings;
    }

    @Override
    public void testIvyFile(String ivyFile, String settingsFile, Collection<String> propertiesFiles) throws IvyException {
        getConfs(ivyFile, settingsFile, propertiesFiles);
    }

    @Override
    public String[] getConfs(String ivyFile, String settingsFile, Collection<String> propertiesFiles) throws IvyException {
        try {
            URL ivyFileURL = new URI(ivyFile).toURL();
            URL settingsFileURL = getSettingsURL(settingsFile);
            List<File> propertiesFilesFiles = toFileListFromString(propertiesFiles);
            Ivy ivy = getIvy(settingsFileURL, propertiesFilesFiles);

            return getConfs(ivyFileURL, ivy);
        } catch (Exception ex) {
            throw new IvyException(ex);
        }

    }

    private Ivy getIvy(FileObject settingsFile, Collection<FileObject> propertiesFiles) throws IvyException {
        Collection<File> propertiesFilesFile = toFileList(propertiesFiles);
        try {
            URL settingsFileURL = getSettingsURL(settingsFile);
            return getIvy(settingsFileURL, propertiesFilesFile);
        } catch (FileStateInvalidException ex) {
            throw new IvyException(ex);
        } catch (MalformedURLException ex) {
            throw new IvyException(ex);
        }
    }

    private Ivy getIvy(URL settingsFile, Collection<File> propertiesFiles) throws IvyException {
        try {
            IvySettings settings = getIvySettings(settingsFile, propertiesFiles);
            Ivy ivy = Ivy.newInstance(settings);
            ivy.configureDefault();
            ivy.setSettings(settings);
            if (settingsFile != null) {
                ivy.configure(settingsFile);
            }
            return ivy;
        } catch (ParseException ex) {
            throw new IvyException(ex);
        } catch (IOException ex) {
            throw new IvyException(ex);
        }
    }
}
