package components;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class ManifestInject {
	
	public ManifestInject() {
		
	}
	
	public void processAndroidManifestXML(File AndroidManifest) throws FileNotFoundException {
		newLine();
		System.out.println("processing AndroidManifest.xml ...");
		
		//scan the file
		Scanner manifestScan = new Scanner(AndroidManifest);
		
		/* TO DO
		 * - add check for when within comments
		 * - check which types of browsable filters need to support - action.WEB_SEARCH, action.VIEW
		 */
		boolean browsableFilterFound = false;
		while(manifestScan.hasNextLine()) {
			String line = manifestScan.nextLine();
			
			//PROCESS INTENT FILTERS
			
			//check if intent filter
			boolean intentFilterLineBool = false;
			if (line.contains("<intent-filter>")) {
				intentFilterLineBool = true;
			}
			
			//scan each intent filter for BROWSABLE
			while (manifestScan.hasNextLine() && intentFilterLineBool) {
				String intentFilterLine = manifestScan.nextLine();
				if (intentFilterLine.contains("</intent-filter>")) {
					intentFilterLineBool = false;
				}
				
				//process line
				if (intentFilterLine.contains("<category android:name=\"android.intent.category.BROWSABLE\" />")) {
					browsableFilterFound = true;
				}
				
			}	
		}
		
		manifestScan.close();
		
		//check if browsable filter found
		if (browsableFilterFound) {
			System.out.println("Found browsable filter");
		} else {
			System.out.println("Didn't find browsable filter");
		}
		
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
