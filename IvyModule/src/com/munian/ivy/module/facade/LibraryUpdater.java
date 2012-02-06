package com.munian.ivy.module.facade;

import com.munian.ivy.module.util.ProjectUtility;
import com.munian.ivy.module.preferences.ProjectPreferences;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.netbeans.api.project.libraries.Library;
import org.netbeans.api.project.libraries.LibraryManager;
import org.netbeans.spi.project.LookupProvider.Registration.ProjectType;
import org.netbeans.spi.project.ProjectServiceProvider;
import org.openide.util.Exceptions;

/**
 *
 */
@ProjectServiceProvider(projectTypes = {
    @ProjectType(id = ProjectUtility.J2SE_PROJECT),
    @ProjectType(id = ProjectUtility.WEB_PROJECT)
}, service = {ArtifactUpdater.class})
public class LibraryUpdater implements ArtifactUpdater {

    private static final String LIBRARY_NAME_PREFIX = "Ivy_Library_";

    @Override
    public void update(ProjectPreferences preferences, List<ParsedConfArtifacts> parsedArtifacts) {
        URL libraryLocation = preferences.getSharedLibraryLocation();
        LibraryManager manager = LibraryManager.forLocation(libraryLocation);
        for (ParsedConfArtifacts parsedConfArtifacts : parsedArtifacts) {
            try {
                String libraryName = getLibraryName(parsedConfArtifacts.getConf());
                Library library = manager.getLibrary(libraryName);
                Map<String, List<URL>> libraryEntries = convertParsedArtifacts(parsedConfArtifacts);
                if (library != null) {
                    manager.removeLibrary(library);
                }
                manager.createLibrary("j2se", libraryName, libraryEntries);
            } catch (IOException ex) {
                Exceptions.printStackTrace(ex);
            }
        }
    }

    private String getLibraryName(String conf) {
        return LIBRARY_NAME_PREFIX + conf;
    }

    private Map<String, List<URL>> convertParsedArtifacts(ParsedConfArtifacts artifacts) {
        Map<String, List<URL>> retVal = new HashMap<String, List<URL>>();
        retVal.put("classpath", artifacts.getClasspathJars());
        retVal.put("javadoc", artifacts.getJavadocJars());
        retVal.put("src", artifacts.getSourceJars());

        return retVal;
    }

    @Override
    public void removeIvyArtifacts(ProjectPreferences preferences) {
        URL libraryLocation = preferences.getSharedLibraryLocation();
        LibraryManager manager = LibraryManager.forLocation(libraryLocation);
        Library[] librarys = manager.getLibraries();
        for (Library library : librarys) {
            if (library.getName().startsWith(LIBRARY_NAME_PREFIX)) {
                try {
                    manager.removeLibrary(library);
                } catch (IOException ex) {
                    Exceptions.printStackTrace(ex);
                } catch (IllegalArgumentException ex) {
                    Exceptions.printStackTrace(ex);
                }
            }
        }
    }
}
