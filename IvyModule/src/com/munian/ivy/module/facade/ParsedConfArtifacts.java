package com.munian.ivy.module.facade;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class ParsedConfArtifacts {
    private String conf;
    private List<URL> classpathJars = new ArrayList();
    private List<URL> sourceJars = new ArrayList();
    private List<URL> javadocJars = new ArrayList();

    public ParsedConfArtifacts(String conf) {
        this.conf = conf;
    }

    public String getConf() {
        return conf;
    }

    public List<URL> getClasspathJars() {
        return classpathJars;
    }

    public List<URL> getJavadocJars() {
        return javadocJars;
    }

    public List<URL> getSourceJars() {
        return sourceJars;
    }
    
}
