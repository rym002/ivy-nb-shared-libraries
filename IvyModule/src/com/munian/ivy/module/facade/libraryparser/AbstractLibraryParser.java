package com.munian.ivy.module.facade.libraryparser;

import com.munian.ivy.module.exceptions.IvyException;
import com.munian.ivy.module.facade.ArtifactUpdater;
import com.munian.ivy.module.facade.IvyFacade;
import com.munian.ivy.module.facade.ParsedConfArtifacts;
import com.munian.ivy.module.options.IvyRetrieveSettings;
import com.munian.ivy.module.preferences.ProjectPreferences;
import com.munian.ivy.module.ui.io.IOTabIvyLogger;
import java.io.IOException;
import java.net.URI;
import java.util.*;
import org.apache.ivy.Ivy;
import org.apache.ivy.core.module.descriptor.ModuleDescriptor;
import org.apache.ivy.core.module.id.ModuleId;
import org.apache.ivy.core.report.ArtifactDownloadReport;
import org.apache.ivy.core.report.ConfigurationResolveReport;
import org.apache.ivy.core.report.ResolveReport;
import org.netbeans.api.project.Project;
import org.netbeans.spi.project.SubprojectProvider;
import org.openide.util.Lookup;

/**
 *
 * @author raymond
 */
public abstract class AbstractLibraryParser<L extends LibraryParserData, A extends ArtifactLibraryParserData> {

    public List<ParsedConfArtifacts> parseArtifactsInReport(IOTabIvyLogger logger, Ivy ivy, ResolveReport report, ProjectPreferences preferences, ArtifactUpdater updater, SubprojectProvider subProjectProvider) throws IOException, IvyException {
        List<ParsedConfArtifacts> parsedArtifacts = new ArrayList<ParsedConfArtifacts>();
        String[] confs = report.getConfigurations();
        IvyRetrieveSettings retrieveSettings = preferences.getProjectRetrieveSettings();
        L libraryParserData = getLibraryParserData(report, preferences, updater);

        Collection<ModuleId> moduleIds = getIvySubProjects(subProjectProvider);

        for (String conf : confs) {
            ParsedConfArtifacts artifacts = new ParsedConfArtifacts(conf);
            parsedArtifacts.add(artifacts);

            ConfigurationResolveReport resolveReport = report.getConfigurationReport(conf);
            ArtifactDownloadReport[] artifactDownloadReports = resolveReport.getAllArtifactsReports();
            for (ArtifactDownloadReport artifactDownloadReport : artifactDownloadReports) {
                ModuleId artifactModuleId = artifactDownloadReport.getArtifact().getModuleRevisionId().getModuleId();
                if (!moduleIds.contains(artifactModuleId)) {
                    if (artifactDownloadReport.getLocalFile() != null) {
                        if (addJar(artifacts, retrieveSettings, artifactDownloadReport, conf, libraryParserData)) {
                        } else if (addSource(artifacts, retrieveSettings, artifactDownloadReport, conf, libraryParserData)) {
                        } else if (addJavadoc(artifacts, retrieveSettings, artifactDownloadReport, conf, libraryParserData)) {
                        }
                    }
                }
            }
        }

        return parsedArtifacts;
    }

    private Collection<ModuleId> getIvySubProjects(SubprojectProvider subprojectProvider) throws IvyException {
        Set<ModuleId> ivyProjects = new HashSet<ModuleId>();
        IvyFacade ivyFacade = Lookup.getDefault().lookup(IvyFacade.class);

        for (Project project : subprojectProvider.getSubprojects()) {
            ModuleDescriptor moduleDescriptor = ivyFacade.getModuleDescriptor(project);
            if (moduleDescriptor != null) {
                ivyProjects.add(moduleDescriptor.getModuleRevisionId().getModuleId());
            }
        }
        return ivyProjects;
    }

    private boolean addJar(ParsedConfArtifacts artifacts, IvyRetrieveSettings retrieveSettings, ArtifactDownloadReport artifactDownloadReport, String conf, L libraryParserData) {
        if (isMatchingType(retrieveSettings.getJarTypes(), artifactDownloadReport)) {
            A artifactLibraryParserData = getArtifactLibraryParserDataForJar(artifactDownloadReport, conf);
            URI libraryJar = getLibraryJarPath(libraryParserData, artifactLibraryParserData);
            artifacts.getClasspathJars().add(libraryJar);
            return true;
        }
        return false;
    }

    private boolean addSource(ParsedConfArtifacts artifacts, IvyRetrieveSettings retrieveSettings, ArtifactDownloadReport artifactDownloadReport, String conf, L libraryParserData) {
        if (isMatchingType(retrieveSettings.getSourceTypes(), artifactDownloadReport)) {
            if (isMatchingSuffix(retrieveSettings.getSourceSuffixes(), artifactDownloadReport)) {
                A artifactLibraryParserData = getArtifactLibraryParserDataForJar(artifactDownloadReport, conf);
                URI libraryJar = getLibraryJarPath(libraryParserData, artifactLibraryParserData);
                artifacts.getSourceJars().add(libraryJar);
                return true;
            }
        }
        return false;
    }

    private boolean addJavadoc(ParsedConfArtifacts artifacts, IvyRetrieveSettings retrieveSettings, ArtifactDownloadReport artifactDownloadReport, String conf, L libraryParserData) {
        if (isMatchingType(retrieveSettings.getJavadocTypes(), artifactDownloadReport)) {
            if (isMatchingSuffix(retrieveSettings.getJavadocSuffixes(), artifactDownloadReport)) {
                A artifactLibraryParserData = getArtifactLibraryParserDataForJar(artifactDownloadReport, conf);
                URI libraryJar = getLibraryJarPath(libraryParserData, artifactLibraryParserData);
                artifacts.getJavadocJars().add(libraryJar);
                return true;
            }
        }
        return false;

    }

    private boolean isMatchingType(Collection<String> acceptedTypes, ArtifactDownloadReport artifactDownloadReport) {
        return acceptedTypes.contains(artifactDownloadReport.getType());
    }

    private boolean isMatchingSuffix(Collection<String> acceptedSuffixes, ArtifactDownloadReport artifactDownloadReport) {
        String artifactName = artifactDownloadReport.getArtifact().getName();
        String moduleName = artifactDownloadReport.getArtifact().getModuleRevisionId().getName();
        if (moduleName.equals(artifactName)) {
            return true;
        }
        for (String suffix : acceptedSuffixes) {
            if (artifactName.endsWith(suffix)) {
                return true;
            }
        }
        return false;
    }

    protected abstract URI getLibraryJarPath(L libraryParserData, A artifactLibraryParserData);

    protected abstract L getLibraryParserData(ResolveReport report, ProjectPreferences preferences, ArtifactUpdater updater);

    protected abstract A getArtifactLibraryParserDataForJar(ArtifactDownloadReport artifactDownloadReport, String conf);

    protected abstract A getArtifactLibraryParserDataForSource(ArtifactDownloadReport artifactDownloadReport, String conf);

    protected abstract A getArtifactLibraryParserDataForJavadoc(ArtifactDownloadReport artifactDownloadReport, String conf);
}
