package com.munian.ivy.module.options;

import com.munian.ivy.module.util.Utilities;
import java.util.Collection;
import java.util.TreeSet;

/**
 *
 */
public class IvyRetrieveSettings {
    public static final String PROP_JAR_TYPES = Utilities.PROPERTIES_PREFIX + "types.jar";
    public static final String PROP_SOURCE_TYPES = Utilities.PROPERTIES_PREFIX + "types.source";
    public static final String PROP_JAVADOC_TYPES = Utilities.PROPERTIES_PREFIX + "types.javadoc";
    public static final String PROP_SOURCE_SUFFIXES = Utilities.PROPERTIES_PREFIX + "suffixes.source";
    public static final String PROP_JAVADOC_SUFFIXES = Utilities.PROPERTIES_PREFIX + "suffixes.javadoc";
    
    private Collection<String> jarTypes = new TreeSet<String>();
    private Collection<String> sourceTypes = new TreeSet<String>();
    private Collection<String> sourceSuffixes = new TreeSet<String>();
    private Collection<String> javadocTypes = new TreeSet<String>();
    private Collection<String> javadocSuffixes = new TreeSet<String>();
    private boolean persisted=false;

    /**
     * @return the jarTypes
     */
    public Collection<String> getJarTypes() {
	return jarTypes;
    }

    /**
     * @return the sourceTypes
     */
    public Collection<String> getSourceTypes() {
	return sourceTypes;
    }

    /**
     * @return the sourceSuffixes
     */
    public Collection<String> getSourceSuffixes() {
	return sourceSuffixes;
    }

    /**
     * @return the javadocTypes
     */
    public Collection<String> getJavadocTypes() {
	return javadocTypes;
    }

    /**
     * @return the javadocSuffixes
     */
    public Collection<String> getJavadocSuffixes() {
	return javadocSuffixes;
    }

    public boolean isPersisted() {
        return persisted;
    }

    public void setPersisted(boolean persisted) {
        this.persisted = persisted;
    }
    
}
