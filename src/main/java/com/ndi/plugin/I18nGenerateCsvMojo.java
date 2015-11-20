package com.ndi.plugin;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;
import java.util.Properties;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

@Mojo(name = "generate-csv")
public class I18nGenerateCsvMojo extends I18nAbstractMojo {
		
	
	@Parameter(property="i18nNdi.csvOutpuFile", required=true)
	private String csvOutpuFile;
	
	@Parameter(property="i18nNdi.csvSeparator", defaultValue=";")
	private String csvSeparator;

	@Override
	public void execute() throws MojoExecutionException, MojoFailureException {
		logGenericInfoStart();	
		getLog().info("= csvOutpuFile   : " + csvOutpuFile);
		getLog().info("= csvSeparator   : " + csvSeparator);
		logGenericInfoEnd();
		generateCsv();
	}


	/**
	 * Generate the Csv File from the bundles
	 * 
	 * @throws MojoExecutionException 
	 */
	private void generateCsv() throws MojoExecutionException{
		File fileBundle = null;
		
		if(!(fileBundle = new File(bundleBaseDir + bundleBaseName + "_" + mainLocal + ".properties")).exists())
			fileBundle = new File(bundleBaseDir + bundleBaseName + ".properties");

		try(
			BufferedWriter bw = new BufferedWriter(new FileWriter(new File(csvOutpuFile)));
			BufferedReader br = new BufferedReader(new FileReader(fileBundle))
				)
		{
			// Retrieving locals
			String[] localSplit = locals.split(",");
			
			// Load the Bundles
			Map<String, Properties> mapBundle = super.loadBundles(localSplit);
						
			// Iterating on the mainLocal Bundle
			String currentLine = null;
			String[] propertySplit = null;
			
			//Initialize csv header
			bw.write("key");
			for(int i = 0; i < localSplit.length;i++){
				bw.write(";"+localSplit[i]);
			}
			bw.newLine();
			
			
			// Populate the csv
			while((currentLine = br.readLine()) != null){
				if(!currentLine.startsWith("#") && currentLine.trim().length()>0){
					propertySplit = currentLine.split("=", 2);
					bw.write(propertySplit[0].trim() + csvSeparator + propertySplit[1].trim());
					for(int i = 0; i < localSplit.length;i++){
						if(!localSplit[i].equals(mainLocal)){
							bw.write(csvSeparator + mapBundle.get(localSplit[i]).getProperty(propertySplit[0].trim()));
						}
					}
					bw.newLine();
				}
			}			
		}catch(IOException ioex){
			throw new MojoExecutionException("Problem writting file : " + csvOutpuFile, ioex);
		}
	}
	
	

}
