package com.ndi.plugin;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugins.annotations.Parameter;

public abstract class I18nAbstractMojo extends AbstractMojo {

	@Parameter(property="i18nNdi.mainLocal", required=true)
	protected String mainLocal;
	
	@Parameter(property="i18nNdi.local", required=true)
	protected String locals;
	
	@Parameter(property="i18nNdi.bundleBaseDir", required=true)
	protected String bundleBaseDir;
	
	@Parameter(property="i18nNdi.bundleBaseName", required=true)
	protected String bundleBaseName;
	
	
	
	/**
	 * load the different language bundles into the map
	 * 
	 * @param localSplit
	 * @param mapBundle
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	protected Map<String, Properties> loadBundles(String[] localSplit) throws FileNotFoundException, IOException{
		Map<String, Properties> retour = new HashMap<String, Properties>();
		
		for(int i = 0; i < localSplit.length;i++){
			if(!localSplit[i].equals(mainLocal)){
				Properties prop = new Properties();
				prop.load(new FileInputStream(new File(bundleBaseDir + bundleBaseName+"_"+localSplit[i]+".properties")));
				retour.put(localSplit[i], prop);
			}
		}
		
		return retour;
	}
	
	
	protected void logGenericInfoStart(){
		getLog().info("=========================================");
		getLog().info("====        Generating File          ====");
		getLog().info("= mainLocal      : " + mainLocal);
		getLog().info("= locals         : " + locals);
		getLog().info("= bundleBaseDir  : " + bundleBaseDir);
		getLog().info("= bundleBaseName : " + bundleBaseName);		
	}
	
	protected void logGenericInfoEnd(){
		getLog().info("=");
		getLog().info("=========================================");
	}
}
