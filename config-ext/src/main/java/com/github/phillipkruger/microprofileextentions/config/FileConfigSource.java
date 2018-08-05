package com.github.phillipkruger.microprofileextentions.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.logging.Level;
import lombok.extern.java.Log;
import org.eclipse.microprofile.config.spi.ConfigSource;

/**
 * File config
 * @author Phillip Kruger (phillip.kruger@phillip-kruger.com)
 */
@Log
public class FileConfigSource implements ConfigSource {
    
    public static final String NAME = "FileConfigSource";
    private static final Map<String,String> PROPERTIES = new HashMap<>();
    
    public FileConfigSource(){
        log.info("Loading [file] MicroProfile ConfigSource");
        String filename = getPropertyValue(KEY_FILE_PATH,DEFAULT_FILE_PATH);
        log.log(Level.INFO, "Using [{0}] as property file", filename);
        File f = new File(filename);
        
        try {
            Properties p = new Properties();
            p.load(new FileInputStream(f));
            Set<Map.Entry<Object, Object>> entrySet = p.entrySet();

            entrySet.forEach((entry) -> {
                PROPERTIES.put((String)entry.getKey(),(String)entry.getValue());
            });
            
        } catch (IOException ex) {
            log.log(Level.INFO, "FileConfigSource did not find properties file [{0}]", f.getAbsolutePath());
        }
    }
    
    @Override
    public int getOrdinal() {
        return 400;
    }
    
    @Override
    public Map<String, String> getProperties() {
        return PROPERTIES;
    }

    @Override
    public String getValue(String key) {
        if(PROPERTIES.containsKey(key)){
            return PROPERTIES.get(key);
        }
        return null;
    }

    @Override
    public String getName() {
        return NAME;
    }
    
    // TODO: Would love to actually just use Config API...
    private String getPropertyValue(String key,String defaultValue){
        String val = System.getProperty(key, System.getenv(key));
        if(val!=null && !val.isEmpty())return val;
        return defaultValue;
    }
    
    private static final String KEY_FILE_PATH = "configsource.file.path";
    private static final String DEFAULT_FILE_PATH = "application.properties";
    
}
