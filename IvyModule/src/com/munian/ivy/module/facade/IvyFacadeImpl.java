package com.munian.ivy.module.facade;

import com.munian.ivy.module.options.IvyRetrieveSettings;
import com.munian.ivy.module.exceptions.IvyException;
import com.munian.ivy.module.ui.io.IOTabIvyLogger;
import com.munian.ivy.module.ui.io.IvyProgressHandleListener;
import com.munian.ivy.module.preferences.ProjectPreferences;
import com.munian.ivy.module.ui.io.IvyProgressHandleTransferListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Properties;
import org.apache.ivy.Ivy;
import org.apache.ivy.core.cache.RepositoryCacheManager;
import org.apache.ivy.core.cache.ResolutionCacheManager;
import org.apache.ivy.core.module.descriptor.ModuleDescriptor;
import org.apache.ivy.core.report.ArtifactDownloadReport;
import org.apache.ivy.core.report.ResolveReport;
import org.apache.ivy.core.resolve.ResolveOptions;
import org.apache.ivy.core.settings.IvySettings;
import org.apache.ivy.plugins.parser.ModuleDescriptorParserRegistry;
import org.netbeans.api.progress.ProgressHandle;
import org.netbeans.api.progress.ProgressHandleFactory;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.ProjectInformation;
import org.netbeans.api.project.ProjectUtils;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileStateInvalidException;
import org.openide.filesystems.FileUtil;
import org.openide.util.Exceptions;
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
                try {
                    
                    cleanResolutionCache(getIvy(project),progressHandle);
                } catch (IvyException ex) {
                    Exceptions.printStackTrace(ex);
                }
                progressHandle.finish();
            }
        });
    }

    private void cleanResolutionCache(Ivy ivy,ProgressHandle progressHandle) {
        progressHandle.setDisplayName(NbBundle.getMessage(IvyFacade.class, "CleanResolution"));
        ResolutionCacheManager resolutionCacheManager = ivy.getSettings().getResolutionCacheManager();
        resolutionCacheManager.clean();
    }

    private void cleanAllRepositoryCache(Ivy ivy,ProgressHandle progressHandle) {
        progressHandle.setDisplayName(NbBundle.getMessage(IvyFacade.class, "CleanAllRespository"));
        RepositoryCacheManager[] managers = ivy.getSettings().getRepositoryCacheManagers();
        for (RepositoryCacheManager repositoryCacheManager : managers) {
            repositoryCacheManager.clean();
        }
    }

    private void cleanRespoitoryCache(Ivy ivy, String name,ProgressHandle progressHandle) {
        progressHandle.setDisplayName(NbBundle.getMessage(IvyFacade.class, "CleanResolution") + " " + name);
        RepositoryCacheManager manager = ivy.getSettings().getRepositoryCacheManager(name);
        manager.clean();
    }

    @Override
    public Ivy getIvy(Project project) throws IvyException {
        ProjectPreferences projectPreferences = project.getLookup().lookup(ProjectPreferences.class);
        return getIvy(projectPreferences.getIvySettingsFile(), projectPreferences.getIvyPropertiesFiles());
    }

    @Override
    public void cleanRepositoryCache(final Project project, final String name) {
        requestProcessor.post(new Runnable() {

            @Override
            public void run() {
                ProgressHandle progressHandle = ProgressHandleFactory.createHandle(NbBundle.getMessage(IvyFacade.class, "CleanRespository"));
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
                try {
                    cleanAllRepositoryCache(getIvy(project),progressHandle);
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
                try {
                    Ivy ivy = getIvy(project);
                    cleanAllRepositoryCache(ivy,progressHandle);
                    cleanResolutionCache(ivy,progressHandle);
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
                    Ivy ivy = getIvy(projectPreferences.getIvySettingsFile(), projectPreferences.getIvyPropertiesFiles());
                    logger = new IOTabIvyLogger(projectInformation.getDisplayName(), IOTabIvyLogger.TAB_SUFFIX);
                    IvyProgressHandleListener transferListener = new IvyProgressHandleListener(projectInformation.getDisplayName() + " " + NbBundle.getMessage(IvyProgressHandleListener.class, "Resolving"));
                    IvyProgressHandleTransferListener ivyProgressHandleTransferListener = new IvyProgressHandleTransferListener(projectInformation.getDisplayName());
                    ivy.getLoggerEngine().setDefaultLogger(logger);
                    ivy.getEventManager().addIvyListener(transferListener);
                    ivy.getEventManager().addTransferListener(ivyProgressHandleTransferListener);
                    URL ivyFileLocation = projectPreferences.getIvyFile().getURL();
                    String[] confs = getConfs(projectPreferences.getIvyFile(), projectPreferences.getIvySettingsFile(), projectPreferences.getIvyPropertiesFiles());
                    List<ParsedConfArtifacts> parsedArtifacts = new ArrayList<ParsedConfArtifacts>();
                    for (String string : confs) {
                        transferListener.setConf(string);
                        ResolveOptions resolveOption = new ResolveOptions().setConfs(new String[]{string});
                        resolveOption.setValidate(ivy.getSettings().doValidate());
                        ResolveReport report = ivy.resolve(ivyFileLocation, resolveOption);
                        parsedArtifacts.add(parseArtifactsInReport(string, report, projectPreferences));
                    }
                    ArtifactUpdater updater = project.getLookup().lookup(ArtifactUpdater.class);
                    updater.update(projectPreferences, parsedArtifacts);

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

    private ParsedConfArtifacts parseArtifactsInReport(String conf, ResolveReport report, ProjectPreferences preferences) {
        ParsedConfArtifacts artifacts = new ParsedConfArtifacts(conf);
        ArtifactDownloadReport[] artifactDownloadReports = report.getAllArtifactsReports();
        IvyRetrieveSettings retrieveSettings = preferences.getProjectRetrieveSettings();
        for (ArtifactDownloadReport artifactDownloadReport : artifactDownloadReports) {
            try {
                if (artifactDownloadReport.getLocalFile() != null) {
                    if (isMatchingType(retrieveSettings.getJarTypes(), artifactDownloadReport)) {
                        artifacts.getClasspathJars().add(artifactDownloadReport.getLocalFile().toURI().toURL());
                    } else if (isMatchingType(retrieveSettings.getSourceTypes(), artifactDownloadReport)) {
                        if (isMatchingSuffix(retrieveSettings.getSourceSuffixes(), artifactDownloadReport)) {
                            artifacts.getSourceJars().add(artifactDownloadReport.getLocalFile().toURI().toURL());
                        }
                    } else if (isMatchingType(retrieveSettings.getJavadocTypes(), artifactDownloadReport)) {
                        if (isMatchingSuffix(retrieveSettings.getJavadocSuffixes(), artifactDownloadReport)) {
                            artifacts.getJavadocJars().add(artifactDownloadReport.getLocalFile().toURI().toURL());
                        }
                    }
                }
            } catch (MalformedURLException ex) {
                Exceptions.printStackTrace(ex);
            }
        }
        return artifacts;
    }

    private boolean isMatchingType(Collection<String> acceptedTypes, ArtifactDownloadReport artifactDownloadReport) {
        return acceptedTypes.contains(artifactDownloadReport.getType());
    }

    private boolean isMatchingSuffix(Collection<String> acceptedSuffixes, ArtifactDownloadReport artifactDownloadReport) {
        for (String suffix : acceptedSuffixes) {
            if (artifactDownloadReport.getArtifact().getName().endsWith(suffix)) {
                return true;
            }
        }
        return false;
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
            List<File> propertiesFilesFiles = new ArrayList<File>(propertiesFiles.size());
            for (String string : propertiesFiles) {
                propertiesFilesFiles.add(new File(string));
            }

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

    private String[] getConfs(FileObject ivyFile, FileObject settingsFile, Collection<FileObject> propertiesFiles) throws IvyException {
        try {
            URL ivyFileURL = ivyFile.getURL();
            URL settingsFileURL = null;
            if (settingsFile != null) {
                settingsFileURL = settingsFile.getURL();
            }
            List<File> propertiesFilesFiles = toFileList(propertiesFiles);
            return getConfs(ivyFileURL, settingsFileURL, propertiesFilesFiles);
        } catch (Exception ex) {
            throw new IvyException(ex);
        }
    }

    public String[] getConfs(URL ivyFile, URL settingsFile, Collection<File> propertiesFiles) throws IvyException {
        Ivy ivy = getIvy(settingsFile, propertiesFiles);
        ModuleDescriptor moduleDescriptor = getModuleDescriptor(ivy, ivyFile);

        return moduleDescriptor.getConfigurationsNames();

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

    private String[] getConfs(String ivyFile, String settingsFile, Collection<String> propertiesFiles) throws IvyException {
        FileObject ivyFileURL;
        try {
            ivyFileURL = FileUtil.toFileObject(new File(new URI(ivyFile)));
            FileObject settingsFileURL = null;
            if (settingsFile != null && !settingsFile.isEmpty()) {
                settingsFileURL = FileUtil.toFileObject(new File(new URI(settingsFile)));
            }

            List<FileObject> propertiesFilesFiles = new ArrayList<FileObject>(propertiesFiles.size());
            for (String propertyFile : propertiesFiles) {
                propertiesFilesFiles.add(FileUtil.toFileObject(new File(propertyFile)));
            }
            return getConfs(ivyFileURL, settingsFileURL, propertiesFilesFiles);
        } catch (Exception ex) {
            throw new IvyException(ex);
        }

    }

    private Ivy getIvy(FileObject settingsFile, Collection<FileObject> propertiesFiles) throws IvyException {
        Collection<File> propertiesFilesFile = new ArrayList<File>(propertiesFiles.size());
        for (FileObject fileObject : propertiesFiles) {
            propertiesFilesFile.add(FileUtil.toFile(fileObject));
        }
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
            return ivy;
        } catch (ParseException ex) {
            throw new IvyException(ex);
        } catch (IOException ex) {
            throw new IvyException(ex);
        }
    }
}
