package com.munian.ivy.module.util;

import java.util.Arrays;
import java.util.Collection;

/**
 *
 */
public class Utilities {
    public static final String FILE_CHOOSER_BUILDER_KEY="/Ivy/";
    public static final String PROPERTIES_PREFIX = "ivy.";
    public static final String DELIMITER = ",";
    
    public static Collection<String> stringToCollection(String value){
        String[] values;
        if (value==null || value.isEmpty()){
            values = new String[0]; 
        }else{
            values = value.split(DELIMITER);
        }
        for (int i = 0; i < values.length; i++) {
            values[i] = values[i].trim();
            
        }
        return  Arrays.asList(values);
    }
    
    public static String collectionToString(Collection<String> collection){
        StringBuilder retVal = new StringBuilder();
        for (String string : collection) {
            retVal.append(string).append(DELIMITER).append(" ");
        }
        int lastDelimiter = retVal.lastIndexOf(DELIMITER + " ");
        if (lastDelimiter>0){
            retVal.setLength(lastDelimiter);
        }
        return retVal.toString();
    }
    
    public static boolean collectionsMatch(Collection c1, Collection c2){
        boolean retVal = c1.containsAll(c2);
        retVal &= c2.containsAll(c1);        
        return retVal;
    }
    
    public static boolean getBoolean(String value, boolean defaultValue){
        if (value==null ||value.isEmpty()){
            return defaultValue;
        }else{
            return Boolean.valueOf(value);
        }
    }
    
    public static boolean isNotNullOrEmpty(String inVal){
        return inVal!=null && !inVal.isEmpty();
    }
}
