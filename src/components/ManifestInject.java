package components;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

public class ManifestInject {
	private String actionType;
	private boolean browsableFilterFound;
	private File AndroidManifest;
	private String dataTag;
	private String dataTagComment;
	private String intentFilter;
	
	Scanner scannerSTDIN;
	
	public ManifestInject() {
		browsableFilterFound = false;
		actionType = null;
		
		scannerSTDIN = new Scanner(System.in);
	}
	
	public void processAndroidManifestXML(File AndroidManifest) throws FileNotFoundException, IOException {
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
		 * - add check for when within comments --> tell user it's commented out and exit
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
	
	private void processScanResults() throws FileNotFoundException, IOException {
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
	
	private void injectFilter() throws FileNotFoundException, IOException {
		buildFilter(); 
		newLine();
		System.out.println("injecting browsable filter ...............");
		
		Scanner manifestScan = new Scanner(AndroidManifest);
		while (manifestScan.hasNextLine()) {
			String line = manifestScan.nextLine();
			if (line.contains("<activity")) {
				while (!line.contains(">")) {
					line = manifestScan.nextLine();
				}
				
				//when reached here - at final activity line --> insert filter after
				
				//reference for adding to existing file:
				// http://stackoverflow.com/questions/289965/inserting-text-into-an-existing-file-via-java
				
			}
		}
		
		manifestScan.close();
	}
	
	/*
	 * Constructs the filter to be injected into manifest
	 * Builds the type of link the app should accept 
	 */
	private void buildFilter() {
		
		newLine();
		System.out.println("I need some information to generate the type of links that will open your app:"
				+ "\n" + "    - the NAME of your app"
				+ "\n" + "    - the HOST of your app (can be the same as the name)"
				+ "\n"
				+ "\n" + "A full link will begin like this:"
				+ "\n" + "    NAME-app://host..."
				+ "\n" + "e.g. -->  notepad-app://notepad/note.txt"
				+ "\n" + "The rest of the link will be processed and handled per activity.");
		newLine();
		System.out.println("Please enter the NAME of your app: ");
		String NAME = scannerSTDIN.nextLine();
		
		System.out.println("Would you like to use the NAME as the host? (y/n)");
		String HOST = NAME;
		if (scannerSTDIN.nextLine().toLowerCase() == "n") {
			System.out.println("Please enter the HOST of your app: ");
			HOST = scannerSTDIN.nextLine();
		}
		
		//FINAL CHECK HERE --> PRINT NAME AND HOST AND CONFIRM THAT THEY ARE CORRECT
		
		//build the comment to be inserted above data tag
		dataTagComment = "<!-- Accepts URIs that begin with \"" 
				+ NAME + "-app://" + HOST + "\" -->";
		
		//build the data tag
		dataTag = "<data android:scheme=\"" + NAME + "-app"
							+ "\" android:host=\"" + HOST + "\" />"; 
		
		//build the filter
		intentFilter = browsableFilterStart 
				+ "\n" + dataTagComment
				+ "\n" + dataTag
				+ "\n" + browsableFilterClose;
		
		System.out.println(intentFilter);
	}
	
	public void manifestNotFound() {
		System.out.println("ERROR");
		System.out.println("The AndroidManifest.xml file was not found in the root directory.");
		System.exit(0);
	}
	
	private void newLine() {
		System.out.println("");
	}
	
	private final static String browsableFilterStart = " <intent-filter>" + 
														"\n" + "<action android:name=\"android.intent.action.VIEW\" />" +
														"\n" + "<category android:name=\"android.intent.category.DEFAULT\" />" +
														"\n" + "<category android:name=\"android.intent.category.BROWSABLE\" />";
	 
	
	private final static String browsableFilterClose = "</intent-filter>";
}