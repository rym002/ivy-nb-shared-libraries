package com.munian.ivy.module.facade;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class ParsedConfArtifacts {
    private String conf;
    private List<URI> classpathJars = new ArrayList<URI>();
    private List<URI> sourceJars = new ArrayList<URI>();
    private List<URI> javadocJars = new ArrayList<URI>();

    public ParsedConfArtifacts(String conf) {
        this.conf = conf;
    }

    public String getConf() {
        return conf;
    }

    public List<URI> getClasspathJars() {
        return classpathJars;
    }

    public List<URI> getJavadocJars() {
        return javadocJars;
    }

    public List<URI> getSourceJars() {
        return sourceJars;
    }
    
}
