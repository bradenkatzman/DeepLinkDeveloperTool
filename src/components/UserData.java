package components;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class UserData {
	private String rootDirectoryPath;
	private File rootDirectory;
	
	public UserData() {} //empty constructor
	
	public void processRootDirectory() {
		scanPath();
		openDirectory();
	}
	
	public void scanPath() {
		System.out.println("Please enter the complete path to the root directory of your Android application");
		Scanner scanner = new Scanner(System.in);
		this.rootDirectoryPath = scanner.nextLine();
		scanner.close();
	}
	
	public void openDirectory() {
		try {
			this.rootDirectoryPath = duckduckgoDir;
			
			rootDirectory = new File(rootDirectoryPath);
			
			if (rootDirectory.isDirectory()) {
				boolean manifestFound = false;
				File[] listOfFiles = rootDirectory.listFiles();
				for (int i = 0; i < listOfFiles.length; i++) {
					File currFile = listOfFiles[i];
					if (currFile.isFile() && 
							currFile.getName().equals("AndroidManifest.xml")) {
						manifestFound = true;
						processAndroidManifestXML(currFile);
					}
				}
				
				if (!manifestFound) {
					manifestNotFound();
				}
			} else {
				System.out.println("The path provided does not point to a directory");
				Scanner scanner = new Scanner(rootDirectory);
				scanner.close();
			}
			
			
		} catch (FileNotFoundException e) {
			//e.printStackTrace();
			System.out.println("The file was not found");
			System.exit(0);
		}
	}
	
	private void processAndroidManifestXML(File AndroidManifest) throws FileNotFoundException {
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
		
		//check if browsable filter found
		if (browsableFilterFound) {
			System.out.println("Found browsable filter");
		} else {
			System.out.println("Didn't find browsable filter");
		}
		
	}
	
	private void manifestNotFound() {
		System.out.println("ERROR");
		System.out.println("The AndroidManifest.xml file was not found in the root directory.");
		newLine();
		System.out.println("By convention, the AndroidManifest.xml file is placed in the root directory.");
		newLine();
		System.out.println("Place your AndroidManifest.xml file in the root directory and rerun.");
		System.exit(0);
	}
	
	private void newLine() {
		System.out.println("");
	}
	
	
	public final static String duckduckgoDir = "/Users/stormfootball4life/Desktop/CS/Columbia/SoftwareSystemsLab/android/";
}