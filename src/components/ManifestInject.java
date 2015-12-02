package components;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class ManifestInject {
	private String actionType;
	private boolean browsableFilterFound;
	private File AndroidManifest;
	
	public ManifestInject() {
		browsableFilterFound = false;
		actionType = null;
	}
	
	public void processAndroidManifestXML(File AndroidManifest) throws FileNotFoundException {
		newLine();
		System.out.println("processing AndroidManifest.xml ...................");
		
		//save the file
		this.AndroidManifest = AndroidManifest;
		
		//scan for browsable filter
		scanManifest();
		
		//process the results of the scan
		processScanResults();
	}
	
	private void scanManifest() throws FileNotFoundException {
		
		//scan the file
		Scanner manifestScan = new Scanner(AndroidManifest);
		
		/* TO DO
		 * - add check for when within comments
		 * - check which types of browsable filters need to support - action.WEB_SEARCH, action.VIEW
		 */
		while(manifestScan.hasNextLine()) {
			String line = manifestScan.nextLine();
			
			//PROCESS INTENT FILTERS
			
			//check if intent filter
			boolean intentFilterLineBool = false;
			if (line.contains("<intent-filter")) {
				intentFilterLineBool = true;
			}
			
			//scan each intent filter for BROWSABLE
			String intentFilterLine = line;
			while (intentFilterLineBool) {
				if (!manifestScan.hasNextLine()) {
					System.out.println("Incorrect file format - intent filter open at end of file");
					System.exit(0);
				}
				
				//check if action line
				if (intentFilterLine.contains("android.intent.action.")) {
					//save the action type
					actionType = intentFilterLine.substring(
							intentFilterLine.lastIndexOf(".") + 1, intentFilterLine.lastIndexOf("\""));
				}
				
				//process line
				if (intentFilterLine.contains("<category android:name=\"android.intent.category.BROWSABLE\" />")) {
					browsableFilterFound = true;
				}
				
				//check if end of filter
				if (intentFilterLine.contains("</intent-filter>")) {
					intentFilterLineBool = false;
				} else {
					//move to next line in filter
					intentFilterLine = manifestScan.nextLine();
				}
			}	
		}
		manifestScan.close();
	}
	
	private void processScanResults() {
		if (browsableFilterFound) {
			newLine();
			System.out.println("Action type for browsable filter: " + actionType);
			/* next steps after browsable is found
			 * - check if intent is processed in activities
			 */
		} else {
			System.out.println("A BROWSABLE intent filter was not found in the manifest file.");
			injectFilter();
			/* next steps after browsable is not found
			 * - inject filter
			 * - prompt user for which activity should support deep linking
			 * - --> add processIntent to that activity
			 */
		}
	}
	
	public void injectFilter() {
		newLine();
		System.out.println("injecting browsable filter ...............");
	}
	
	public void manifestNotFound() {
		System.out.println("ERROR");
		System.out.println("The AndroidManifest.xml file was not found in the root directory.");
		System.exit(0);
	}
	
	private void newLine() {
		System.out.println("");
	}

}
