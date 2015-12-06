package components;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Scanner;

public class ProcessUserData {
	ManifestInject manifestInject;
	private String rootDirectoryPath;
	private File rootDirectory;
	File manifest;
	boolean manifestFound;
	
	Scanner scannerSTDIN;
	
	public ProcessUserData() {
		manifestInject = new ManifestInject();
		manifestFound = false;
		
		scannerSTDIN = new Scanner(System.in);
	}
	
	public void processRootDirectory() throws FileNotFoundException, IOException {
		scanPath();
		checkRoot();
		processScanResults();
	}
	
	public void scanPath() {
		System.out.println("Please enter the complete path to the root directory of your Android application");
		
		this.rootDirectoryPath = scannerSTDIN.nextLine();
	}
	
	public void checkRoot() {
		try {
			this.rootDirectoryPath = BriskNoteDir;
			
			rootDirectory = new File(rootDirectoryPath);
			
			if (rootDirectory.isDirectory()) {
				scanDir(rootDirectory);
			} else {
				System.out.println("The path provided does not point to a directory");
				Scanner scanner = new Scanner(rootDirectory);
				scanner.close();
				System.exit(0);
			}			
		} catch (FileNotFoundException e) {
			//e.printStackTrace();
			System.out.println("The file was not found");
			System.exit(0);
		}
	}
	
	public void scanDir(File dir) {
		File[] listOfFiles = dir.listFiles();
		for (int i = 0; i < listOfFiles.length; i++) {
			File currFile = listOfFiles[i];
			
			if (currFile.isDirectory()) {
				scanDir(currFile);
			} else if (currFile.isFile() && 
					currFile.getName().equals("AndroidManifest.xml")) {
				manifestFound = true;
				manifest = currFile;
				return;
			}
		}
	}
	
	public void processScanResults() throws FileNotFoundException, IOException {
		if (manifestFound) {
			manifestInject.processAndroidManifestXML(manifest);
		} else {
			manifestInject.manifestNotFound();
		}
	}

	public final static String duckduckgoDir = "/Users/stormfootball4life/Desktop/CS/Columbia/SoftwareSystemsLab/android/";
	public final static String BriskNoteDir = "/Users/stormfootball4life/AndroidStudioProjects/BriskNote/";
}