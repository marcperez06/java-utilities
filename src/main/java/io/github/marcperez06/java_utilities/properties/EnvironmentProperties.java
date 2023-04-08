package io.github.marcperez06.java_utilities.properties;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Properties;

/**
 * Class that allow load properties using the properties file name or file path
 * -- Default directory used is ---> "{user.dir}\resources\properties"
 * 
 * @author Marc Perez Rodriguez
 */
public class EnvironmentProperties {

private static final String FILE_SEPARATOR = System.getProperty("file.separator");
	
	private String directoryOfProperties;
	private String defaultDirectoryOfProperties;
	private String propertiesFileName;
	private Properties properties;

	public EnvironmentProperties() {
		this.defaultDirectoryOfProperties = System.getProperty("user.dir") + FILE_SEPARATOR + "resources";
		this.defaultDirectoryOfProperties += FILE_SEPARATOR + "properties";
		this.directoryOfProperties = "";
		this.properties = new Properties();
	}
	
	public EnvironmentProperties(String propertiesFileName) {
		this();
		this.propertiesFileName = propertiesFileName;
		
	}
	
	public EnvironmentProperties(String directoryOfProperties, String propertiesFileName) {
		this(propertiesFileName);
		this.setDirectoryOfProperties(directoryOfProperties);
	}
	
	/**
	 * Return the base path of directory where property file is located 
	 * @return String - Path of directory where property file is located 
	 */
	public String getBaseDirectoryOfProperties() {
		String baseDirectory = this.defaultDirectoryOfProperties;
		
		if (this.directoryOfProperties != null && !this.directoryOfProperties.isEmpty()) {
			baseDirectory = this.directoryOfProperties;
		}
		
		return baseDirectory;
	}
	
	/**
	 * Return the path of directory where property file is located
	 * @return String - Path of directory where property file is located
	 */
	public String getDirectoryOfProperties() {
		return this.directoryOfProperties;
	}
	
	/**
	 * Change the path of directory where property file is located
	 * @param directoryOfProperties - String path of directory where property file is located
	 */
	public void setDirectoryOfProperties(String directoryOfProperties) {
		this.directoryOfProperties = directoryOfProperties;
	}

	public void loadProperties() {
		if (this.directoryOfProperties.equalsIgnoreCase(this.defaultDirectoryOfProperties)) {
			this.loadProperties(this.directoryOfProperties);
		} else {
			this.loadProperties(this.defaultDirectoryOfProperties);
			this.loadProperties(this.directoryOfProperties);
		}
	}
	
	private void loadProperties(String directoryPath) {
		
		boolean canLoadProperties = directoryPath != null && !directoryPath.isEmpty();
		canLoadProperties &= this.propertiesFileName != null && !this.propertiesFileName.isEmpty();

		if (canLoadProperties) {
			
			if (!this.propertiesFileName.endsWith(".properties")) {
				this.propertiesFileName += ".properties";
			}
			
			String path = directoryPath + FILE_SEPARATOR + this.propertiesFileName;
			this.loadPropertiesFromFile(path);

		}

	}
	
	public void loadPropertiesFromFile(String filePath) {
		try {
			this.loadProperty(this.properties, filePath);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	private void loadProperty(Properties prop, String path) throws FileNotFoundException {
		FileInputStream in = null;
		try {
			if (this.existPropertiesFile(path)) {
				in = new FileInputStream(path);
				prop.load(in);
				in.close();
			} else {
				throw new RuntimeException("File: " + path + " not found");
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	private boolean existPropertiesFile(String propertiesFilePath) {
		boolean exist = false;
		if (propertiesFilePath != null && !propertiesFilePath.isEmpty()) {
			File file = new File(propertiesFilePath);
			exist = file.exists();
		}
		return exist;
	}

	public boolean existProperty(String key) {
		String propertyValue = this.getProperty(key);
		boolean existProperty = (propertyValue != null);
		return existProperty;
	}
	
	/**
	 * Return a property value, first try to return a System property, 
	 * if not exist, try to return a property defined in a file, 
	 * and last if not exist any property, return a Environment property
	 * @param key - String property key
	 * @return Property in the following order: 1. System, 2. Property defined in file, 3. Environment
	 */
	public String getProperty(String key) {
		String property = null;
		
		if (key != null && !key.isEmpty()) {
			
			property = System.getProperty(key);
			
			if (property == null || property.isEmpty()) {
			
				property = this.getFileProperty(key);
				
				if (property == null || property.isEmpty()) {
					property = System.getenv(key);
				}

			}

		}
		
		property = (property != null) ? property.trim() : null;
		return property;
	}
	
	/**
	 * Returns true if property exist inside file, false otherwise
	 * @param propertyName - String
	 * @return boolean - true if exist property inside any file properties, false otherwise
	 */
	public boolean existFileProperty(String propertyName) {
		String propertyValue = this.getFileProperty(propertyName);
		boolean existProperty = (propertyValue != null);
		return existProperty;
	}

	/**
	 * Return a property value, first search in messages, if property not found, search in credentials 
	 * and if also not found property, then search in configuration, if property not found
	 * in configuration, find in device, if property not found in device, find in report messages,
	 *  finally if not found, find in other properties
	 * @param key - String key of property
	 * @return String - If it found, return the value, null otherwise
	 */
	private String getFileProperty(String key) {
		String val = null;
		if (key != null) {
			val = this.getPropertyDefined(key);
		}
		return val;
	}
	
	private String getPropertyDefined(String key) {
		return this.getPropertyInPropertiesObject(this.properties, key);
	}
	
	public void setPropertyDefined(String key, String value) {
		this.setPropertyInPropertiesObject(this.properties, key, value);
	}
	
	private String getPropertyInPropertiesObject(Properties prop, String key) {
		String value = null;
		if (key != null && prop != null) {
			value = prop.getProperty(key);
		}
		return value;
	}
	
	private void setPropertyInPropertiesObject(Properties prop, String key, String value) {
		if (prop != null && key != null && value != null) {
			prop.setProperty(key, value);
		}
	}
	
	// -------------- GET PROPERTIES AS <TYPE> ---------------
	
	public Integer getPropertyAsInteger(String key) {
		return Integer.valueOf(this.getProperty(key));
	}
	
	public Float getPropertyAsFloat(String key) {
		return Float.valueOf(this.getProperty(key));
	}
	
	public Double getPropertyAsDouble(String key) {
		return Double.valueOf(this.getProperty(key));
	}
	
	public Long getPropertyAsLong(String key) {
		return Long.valueOf(this.getProperty(key));
	}
	
	public Boolean getPropertyAsBoolean(String key) {
		return Boolean.valueOf(this.getProperty(key));
	}
	
}