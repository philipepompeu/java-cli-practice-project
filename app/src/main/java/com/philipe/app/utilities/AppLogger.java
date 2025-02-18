package com.philipe.app.utilities;



import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AppLogger {
    
    private static final Logger logger = LoggerFactory.getLogger("java-cli-app");

    public static void log(String text)
    {
        AppLogger.logger.info(text);        
    }

    public static void log(String text, Object... args){
        AppLogger.log(String.format(text, args));
    }
    
    public static void debug(String text)
    {
        AppLogger.logger.debug(text);
    }
    
}
