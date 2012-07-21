package com.munian.ivy.module.facade.libraryparser.retrieving;

import com.munian.ivy.module.exceptions.IvyException;
import com.munian.ivy.module.facade.ArtifactUpdater;
import com.munian.ivy.module.facade.IvyFacade;
import com.munian.ivy.module.facade.ParsedConfArtifacts;
import com.munian.ivy.module.facade.libraryparser.AbstractLibraryParser;
import com.munian.ivy.module.options.IvyRetrieveSettings;
import com.munian.ivy.module.preferences.ProjectPreferences;
import com.munian.ivy.module.ui.io.IOTabIvyLogger;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.Collection;
import java.util.List;
import org.apache.ivy.Ivy;
import org.apache.ivy.core.IvyPatternHelper;
import org.apache.ivy.core.module.descriptor.Artifact;
import org.apache.ivy.core.report.ArtifactDownloadReport;
import org.apache.ivy.core.report.ResolveReport;
import org.apache.ivy.core.retrieve.RetrieveOptions;
import org.apache.ivy.util.Message;
import org.apache.ivy.util.filter.ArtifactTypeFilter;
import org.apache.ivy.util.filter.Filter;
import org.netbeans.spi.project.SubprojectProvider;
import org.netbeans.spi.project.libraries.support.LibrariesSupport;
import org.netbeans.spi.project.support.ant.PropertyUtils;
import org.openide.util.NbBundle;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author raymond
 */
@ServiceProvider(service = RetrievingLibraryParser.class)
public class RetrievingLibraryParser extends AbstractLibraryParser<RetrievingLibraryParserData, RetrievingArtifactLibraryParserData>{

    private static final String JAR_SUB_PATH = "/jars";
    private static final String SOURCE_SUB_PATH = "/sources";
    private static final String JAVADOC_SUB_PATH = "/javadocs";
    private static final String RETRIEVE_PATTERN = "/[artifact].[ext]";

    @Override
    public List<ParsedConfArtifacts> parseArtifactsInReport(IOTabIvyLogger logger, Ivy ivy, ResolveReport report, ProjectPreferences preferences, ArtifactUpdater updater, SubprojectProvider subprojectProvider) throws IOException, IvyException {
        String[] confs = report.getConfigurations();
        String retrieveRoot = updater.getRetrieveRoot(preferences);
        retrieve(logger, confs, retrieveRoot, ivy, preferences, report);
        return super.parseArtifactsInReport(logger, ivy, report, preferences, updater,subprojectProvider);
    }

    
    @Override
    protected URI getLibraryJarPath(RetrievingLibraryParserData libraryParserData, RetrievingArtifactLibraryParserData artifactLibraryParserData) {
        String retrieveRoot = libraryParserData.getRetrieveRoot();
        String typeSubPath = artifactLibraryParserData.getRetrieveSubPath();
        Artifact artifact = artifactLibraryParserData.getArtifact();
        String conf = artifactLibraryParserData.getConf();
        File retrieveRootFile = libraryParserData.getRetriveRootFile();
        
        String subPath = IvyPatternHelper.substitute(RETRIEVE_PATTERN, artifact, conf);
        String fullRetrievePath = retrieveRoot + typeSubPath + subPath;
        File retrievedFile = new File(fullRetrievePath);
        String relativePath = PropertyUtils.relativizeFile(retrieveRootFile, retrievedFile);
        return LibrariesSupport.convertFilePathToURI(relativePath);
    }

    private void retrieveFiles(String[] confs, Ivy ivy, String retrieveRoot, ResolveReport report, Collection<String> acceptedTypes) throws IOException {
        RetrieveOptions retrieveOptions = new RetrieveOptions().setConfs(confs).setSync(true);
        Filter af = new ArtifactTypeFilter(acceptedTypes);
        retrieveOptions.setArtifactFilter(af);
        String projectRetrievePattern = retrieveRoot + RETRIEVE_PATTERN;
        ivy.retrieve(report.getModuleDescriptor().getResolvedModuleRevisionId(), projectRetrievePattern, retrieveOptions);
    }

    private void retrieve(IOTabIvyLogger logger, String confs[], String retrieveRoot, Ivy ivy, ProjectPreferences projectPreferences, ResolveReport report) throws IOException {
        IvyRetrieveSettings retrieveSettings = projectPreferences.getProjectRetrieveSettings();
        logger.log(NbBundle.getMessage(IvyFacade.class, "StartRetrieve"), Message.MSG_INFO);

        logger.log(NbBundle.getMessage(IvyFacade.class, "StartJarRetrieve"), Message.MSG_INFO);
        retrieveFiles(confs, ivy, retrieveRoot + JAR_SUB_PATH, report, retrieveSettings.getJarTypes());
        logger.log(NbBundle.getMessage(IvyFacade.class, "EndJarRetrieve"), Message.MSG_INFO);

        logger.log(NbBundle.getMessage(IvyFacade.class, "StartSourceRetrieve"), Message.MSG_INFO);
        retrieveFiles(confs, ivy, retrieveRoot + SOURCE_SUB_PATH, report, retrieveSettings.getSourceTypes());
        logger.log(NbBundle.getMessage(IvyFacade.class, "EndSourceRetrieve"), Message.MSG_INFO);

        logger.log(NbBundle.getMessage(IvyFacade.class, "StartJavadocRetrieve"), Message.MSG_INFO);
        retrieveFiles(confs, ivy, retrieveRoot + JAVADOC_SUB_PATH, report, retrieveSettings.getJavadocTypes());
        logger.log(NbBundle.getMessage(IvyFacade.class, "EndJavadocRetrieve"), Message.MSG_INFO);

        logger.log(NbBundle.getMessage(IvyFacade.class, "EndRetrieve"), Message.MSG_INFO);

    }

    @Override
    protected RetrievingLibraryParserData getLibraryParserData(ResolveReport report, ProjectPreferences preferences, ArtifactUpdater updater) {
        String retrieveRoot = updater.getRetrieveRoot(preferences);
        return new RetrievingLibraryParserData(retrieveRoot);
    }

    @Override
    protected RetrievingArtifactLibraryParserData getArtifactLibraryParserDataForJar(ArtifactDownloadReport artifactDownloadReport, String conf) {
        return new RetrievingArtifactLibraryParserData(JAR_SUB_PATH, conf, artifactDownloadReport.getArtifact());
    }

    @Override
    protected RetrievingArtifactLibraryParserData getArtifactLibraryParserDataForJavadoc(ArtifactDownloadReport artifactDownloadReport, String conf) {
        return new RetrievingArtifactLibraryParserData(JAVADOC_SUB_PATH, conf, artifactDownloadReport.getArtifact());
    }

    @Override
    protected RetrievingArtifactLibraryParserData getArtifactLibraryParserDataForSource(ArtifactDownloadReport artifactDownloadReport, String conf) {
        return new RetrievingArtifactLibraryParserData(SOURCE_SUB_PATH, conf, artifactDownloadReport.getArtifact());
    }
    
    
}
