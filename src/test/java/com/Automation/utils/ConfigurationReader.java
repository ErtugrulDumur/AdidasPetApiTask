package com.Automation.utils;

import java.io.FileInputStream;
import java.util.Properties;
/**
 * This class aims to read key-value structure in configuration.properties file
 * get() method returns key value which belongs configuration.properties
 * */
public class ConfigurationReader {
    private static Properties properties;

    static {

        try {
            String path = "configuration.properties";
            FileInputStream input = new FileInputStream(path);
            properties = new Properties();
            properties.load(input);

            input.close();
        } catch (Exception e) {
            e.printStackTrace();

        }
    }

    public static String get(String keyName) {
        return properties.getProperty(keyName);
    }

}

