package com.munian.ivy.module.facade.libraryparser;

import org.apache.ivy.core.module.descriptor.Artifact;

/**
 *
 * @author raymond
 */
public abstract class ArtifactLibraryParserData {
    private String conf;
    private Artifact artifact;

    public ArtifactLibraryParserData(String conf, Artifact artifact) {
        this.conf = conf;
        this.artifact = artifact;
    }

    public Artifact getArtifact() {
        return artifact;
    }

    public String getConf() {
        return conf;
    }
    
}
