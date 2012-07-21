package com.munian.ivy.module.facade.libraryparser.cachepath;

import com.munian.ivy.module.facade.ArtifactUpdater;
import com.munian.ivy.module.facade.libraryparser.AbstractLibraryParser;
import com.munian.ivy.module.preferences.ProjectPreferences;
import java.net.URI;
import org.apache.ivy.core.report.ArtifactDownloadReport;
import org.apache.ivy.core.report.ResolveReport;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 */
@ServiceProvider(service = CachePathLibraryParser.class)
public class CachePathLibraryParser extends AbstractLibraryParser<CachePathLibraryParserData, CachePathArtifactLibraryParserData> {

    @Override
    protected URI getLibraryJarPath(CachePathLibraryParserData libraryParserData, CachePathArtifactLibraryParserData artifactLibraryParserData) {
        return artifactLibraryParserData.getArtifactLocalFile().toURI();
    }

    @Override
    protected CachePathLibraryParserData getLibraryParserData(ResolveReport report, ProjectPreferences preferences, ArtifactUpdater updater) {
        return new CachePathLibraryParserData();
    }

    @Override
    protected CachePathArtifactLibraryParserData getArtifactLibraryParserDataForJar(ArtifactDownloadReport artifactDownloadReport, String conf) {
        return new CachePathArtifactLibraryParserData(artifactDownloadReport.getLocalFile() , conf, artifactDownloadReport.getArtifact());
    }

    @Override
    protected CachePathArtifactLibraryParserData getArtifactLibraryParserDataForSource(ArtifactDownloadReport artifactDownloadReport, String conf) {
        return getArtifactLibraryParserDataForJar(artifactDownloadReport, conf);
    }

    @Override
    protected CachePathArtifactLibraryParserData getArtifactLibraryParserDataForJavadoc(ArtifactDownloadReport artifactDownloadReport, String conf) {
        return getArtifactLibraryParserDataForJar(artifactDownloadReport, conf);
    }
    
}
