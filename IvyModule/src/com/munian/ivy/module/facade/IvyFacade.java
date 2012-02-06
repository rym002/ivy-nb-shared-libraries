package com.munian.ivy.module.facade;

import com.munian.ivy.module.exceptions.IvyException;
import java.util.Collection;
import org.apache.ivy.Ivy;
import org.netbeans.api.project.Project;

/**
 *  Performs ivy tasks
 */
public interface IvyFacade {

    public void resolve(Project resolveable);

    public void cleanResolutionCache(Project cleanable);
    
    public void cleanRepositoryCache(Project cleanable,String name);
    
    public void cleanAllRepositoryCache(Project cleanable);
    
    public void cleanAllCache(Project cleanable);
    
    public String[] getResolutionCacheNames(Project cleanable) throws IvyException;

    public void testIvySettings(String settingsFile, Collection<String> propertiesFiles) throws IvyException;

    public void testIvyFile(String ivyFile, String settingsFile, Collection<String> propertiesFiles) throws IvyException;
    
    public Ivy getIvy(Project project) throws IvyException;
}
