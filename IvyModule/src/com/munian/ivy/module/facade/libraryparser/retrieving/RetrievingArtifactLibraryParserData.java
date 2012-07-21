package com.munian.ivy.module.facade.libraryparser.retrieving;

import com.munian.ivy.module.facade.libraryparser.ArtifactLibraryParserData;
import org.apache.ivy.core.module.descriptor.Artifact;

/**
 *
 * @author raymond
 */
public class RetrievingArtifactLibraryParserData extends ArtifactLibraryParserData{
    private String retrieveSubPath;

    public RetrievingArtifactLibraryParserData(String retrieveSubPath, String conf, Artifact artifact) {
        super(conf, artifact);
        this.retrieveSubPath = retrieveSubPath;
    }

    public String getRetrieveSubPath() {
        return retrieveSubPath;
    }
    
    
}
