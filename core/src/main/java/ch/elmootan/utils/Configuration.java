package ch.elmootan.utils;

import java.io.*;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Class to handle configuration file.
 *
 * The configuration file should be named `planet_io.properties`.
 *
 * @author elmootan
 */
public class Configuration {

    //! Path to the config file.
    private static final String CONFIG_FILE = "planet_io.properties";

    //! Logger.
    private static final Logger LOG = Logger.getLogger(Configuration.class.getSimpleName());

    //! Variable for singleton.
    private static Configuration sharedInstance = null;

    private final Properties properties;

    /**
     * Private default constructor so initialization is not possible from outside.
     */
    private Configuration() {
        properties = new Properties();

        try {
            properties.load(new FileInputStream(CONFIG_FILE));
        } catch (IOException e) {
            properties.setProperty("SERVER_ADDRESS", "127.0.0.1");

            try {
                properties.store(new FileOutputStream(CONFIG_FILE), null);
            } catch (IOException ex) {
                LOG.log(Level.SEVERE, null, ex);
            }
        }
    }

    /**
     * Get current configuration manager.
     *
     * @return Current configuration manager.
     */
    public static synchronized Configuration getConfiguration() {
        if (sharedInstance == null) {
            sharedInstance = new Configuration();
        }

        return sharedInstance;
    }

    /**
     * Get the value corresponding to the property key passed as parameter.
     *
     * @param key The property key.
     *
     * @return The value corresponding to the property key.
     */
    public String getProperty(String key) {
        return properties.getProperty(key);
    }

}
