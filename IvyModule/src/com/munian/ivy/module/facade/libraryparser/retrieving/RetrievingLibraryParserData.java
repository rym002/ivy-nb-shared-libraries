package com.munian.ivy.module.facade.libraryparser.retrieving;

import com.munian.ivy.module.facade.libraryparser.LibraryParserData;
import java.io.File;

/**
 *
 * @author raymond
 */
public class RetrievingLibraryParserData extends  LibraryParserData{
    private String retrieveRoot;
    private File retriveRootFile;

    public RetrievingLibraryParserData(String retrieveRoot) {
        this.retrieveRoot = retrieveRoot;
        this.retriveRootFile = new File(retrieveRoot);
    }

    public String getRetrieveRoot() {
        return retrieveRoot;
    }

    public File getRetriveRootFile() {
        return retriveRootFile;
    }
    
}
