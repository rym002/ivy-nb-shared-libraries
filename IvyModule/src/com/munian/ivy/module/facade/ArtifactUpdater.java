package com.munian.ivy.module.facade;

import com.munian.ivy.module.preferences.ProjectPreferences;
import java.util.List;

/**
 *
 * @author raymond
 */
public interface ArtifactUpdater {

    public void update(ProjectPreferences preferences, List<ParsedConfArtifacts> parsedArtifacts);
    
    public void removeIvyArtifacts(ProjectPreferences preferences);
}
