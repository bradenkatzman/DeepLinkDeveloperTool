package components;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class ProcessUserData {
	ManifestInject manifestInject;
	private String rootDirectoryPath;
	private File rootDirectory;
	boolean manifestFound;
	
	public ProcessUserData() {
		manifestInject = new ManifestInject();
		manifestFound = false;
	}
	
	public void processRootDirectory() {
		scanPath();
		scanRootDirectory();
	}
	
	public void scanPath() {
		System.out.println("Please enter the complete path to the root directory of your Android application");
		Scanner scanner = new Scanner(System.in);
		this.rootDirectoryPath = scanner.nextLine();
		scanner.close();
	}
	
	public void scanRootDirectory() {
		try {
			this.rootDirectoryPath = BriskNoteDir;
			
			rootDirectory = new File(rootDirectoryPath);
			
			if (rootDirectory.isDirectory()) {
				scanDir(rootDirectory);
			} else {
				System.out.println("The path provided does not point to a directory");
				Scanner scanner = new Scanner(rootDirectory);
				scanner.close();
			}
				
			if (!manifestFound) {
					manifestInject.manifestNotFound();
			} 
			
			
		} catch (FileNotFoundException e) {
			//e.printStackTrace();
			System.out.println("The file was not found");
			System.exit(0);
		}
	}
	
	public void scanDir(File dir) throws FileNotFoundException {
		File[] listOfFiles = dir.listFiles();
		for (int i = 0; i < listOfFiles.length; i++) {
			File currFile = listOfFiles[i];
			
			if (currFile.isDirectory()) {
				scanDir(currFile);
			}
			else if (currFile.isFile() && 
					currFile.getName().equals("AndroidManifest.xml")) {
				manifestFound = true;
				manifestInject.processAndroidManifestXML(currFile);
				return;
			}
		}
	}
	
	
	public final static String duckduckgoDir = "/Users/stormfootball4life/Desktop/CS/Columbia/SoftwareSystemsLab/android/";
	public final static String BriskNoteDir = "/Users/stormfootball4life/AndroidStudioProjects/BriskNote/";
}