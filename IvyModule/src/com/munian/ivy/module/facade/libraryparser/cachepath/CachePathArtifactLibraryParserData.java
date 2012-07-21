package com.munian.ivy.module.facade.libraryparser.cachepath;

import com.munian.ivy.module.facade.libraryparser.ArtifactLibraryParserData;
import java.io.File;
import org.apache.ivy.core.module.descriptor.Artifact;

/**
 *
 * @author raymond
 */
public class CachePathArtifactLibraryParserData extends ArtifactLibraryParserData{
    private File artifactLocalFile;

    public CachePathArtifactLibraryParserData(File artifactLocalFile, String conf, Artifact artifact) {
        super(conf, artifact);
        this.artifactLocalFile = artifactLocalFile;
    }

    public File getArtifactLocalFile() {
        return artifactLocalFile;
    }
    
}
