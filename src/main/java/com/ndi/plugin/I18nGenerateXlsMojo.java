package com.ndi.plugin;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.Map;
import java.util.Properties;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 * 
 * @author bilbauta
 *
 */
@Mojo(name="generate-xls")
public class I18nGenerateXlsMojo extends I18nAbstractMojo {
	
	@Parameter(property="i18nNdi.xlsOutpuFile", required=true)
	private String xlsOutpuFile;

	@Override
	public void execute() throws MojoExecutionException, MojoFailureException {
		logGenericInfoStart();
		getLog().info("= xlsOutpuFile   : " + xlsOutpuFile);		
		logGenericInfoEnd();
		generateXls();
	}
	
	
	/**
	 * Generate the xls file from the bundles
	 * 
	 * @throws MojoExecutionException
	 */
	private void generateXls() throws MojoExecutionException{
		File fileBundle = null;
		
		if(!(fileBundle = new File(bundleBaseDir + bundleBaseName + "_" + mainLocal + ".properties")).exists())
			fileBundle = new File(bundleBaseDir + bundleBaseName + ".properties");
		
		try(
				FileOutputStream fos = new FileOutputStream(new File(xlsOutpuFile));
				BufferedReader br = new BufferedReader(new FileReader(fileBundle));
				Workbook workbook = new XSSFWorkbook();
					)
			{
				// Retrieving locals
				String[] localSplit = locals.split(",");
				
				// Load the Bundles
				Map<String, Properties> mapBundle = super.loadBundles(localSplit);
				
				// Init the xlsx				
				Sheet sheet = workbook.createSheet("Feuil1");
				int rowIndex = 0;
				int cellIndex = 0;
				String currentLine;
				String[] propertySplit;
				
				// Creating Header row
				Row row = sheet.createRow(rowIndex++);
				row.createCell(cellIndex++).setCellValue("key");
				for(int i = 0; i < localSplit.length;i++){
					row.createCell(cellIndex++).setCellValue(localSplit[i]);
				}
				
				
				// Creating body
				while((currentLine = br.readLine()) != null){
					if(!currentLine.startsWith("#") && currentLine.trim().length()>0){
						cellIndex = 0;						
						propertySplit = currentLine.split("=", 2);
						
						row = sheet.createRow(rowIndex++);
						row.createCell(cellIndex++).setCellValue(propertySplit[0].trim());
						row.createCell(cellIndex++).setCellValue(propertySplit[1].trim());
						
						for(int i = 0; i < localSplit.length;i++){
							if(!localSplit[i].equals(mainLocal)){
								row.createCell(cellIndex++).setCellValue(mapBundle.get(localSplit[i]).getProperty(propertySplit[0].trim()));
							}
						}
					}
				}
				
				workbook.write(fos);
				
			}catch(IOException ioex){
				throw new MojoExecutionException("Problem writting file : " + xlsOutpuFile, ioex);
			}
	}
}
