package components;


//SEARCH FOR XML PARSER
	//like dom --> insert nodes with filter

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Scanner;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.*;
import java.io.*;

public class ManifestInject {
	private String actionType;
	private boolean browsableFilterFound;
	private File AndroidManifest;
	private File AndroidManifestUpdated;
	private String dataTag;
	private String dataTagComment;
	private String intentFilter;
	private ArrayList<String> fileAsArrayList;
	private String activityName;
	private String NAME;
	private String HOST;
	
	Scanner scannerSTDIN;
	
	public ManifestInject(Scanner scannerSTDIN) {
		browsableFilterFound = false;
		actionType = null;
		fileAsArrayList = new ArrayList<String>();
		
		this.scannerSTDIN = scannerSTDIN;
	}
	
	public void processAndroidManifestXML(File AndroidManifest) throws FileNotFoundException, IOException, ParserConfigurationException, SAXException {
		newLine();
		System.out.println("processing AndroidManifest.xml ...................");
		
		//save the file
		this.AndroidManifest = AndroidManifest;
		
		//scan for browsable filter
		scanManifest();
		
		//process the results of the scan
		//processScanResults();
	}
	
	private void scanManifest() throws ParserConfigurationException, SAXException, IOException {
		
		buildFilter();
		
		//create a DocumebtBuilder
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = dbf.newDocumentBuilder();
		Document doc = db.parse(this.AndroidManifest);
		
		//Extract the root element of the manifest file
		Element root = doc.getDocumentElement();
		
		NodeList activityList = doc.getElementsByTagName("activity");
		
		//search for the specified activity
		for (int i = 0; i < activityList.getLength(); i++) {
			Node currNode = activityList.item(i);
			
			//check if specified activity
			Element element = (Element) currNode;
			String activityNameNode = element.getAttribute("android:name");
			if (activityNameNode.substring(activityNameNode.indexOf(".")+1).toLowerCase().equals(activityName.toLowerCase())) {
				
				//search for intent filters
				NodeList children = currNode.getChildNodes();
				for (int j = 0; j < children.getLength(); j++) {
					Node currChildNode = children.item(j);

					if (currChildNode.getNodeName().equals("intent-filter")) {
						//check for browsable
						Element intentFilter = (Element) currChildNode;
						
						NodeList categories = intentFilter.getElementsByTagName("category");
						
						//search for browsable category
						for (int k = 0; k < categories.getLength(); k++) {
							Node currCategoryNode = categories.item(k);
							
							Element categoryElement = (Element) currCategoryNode;
							
							//need to figure out how to extract the text content of this element
							if (categoryElement.getTextContent().contains("BROWSABLE")) {
								System.out.println("FOUND THE BROWSABLE FILTER");
							}
						}
						
					}
				}
			}
			
		}
		
		
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
		
		boolean activityFound = false;
		while (manifestScan.hasNextLine()) {
			String line = manifestScan.nextLine();
			
			//add the line to the ArrayList
			fileAsArrayList.add(line);
			if (!activityFound && line.contains("<activity")) {
				while (!line.contains(">")) {
					line = manifestScan.nextLine();
					
					//check for the line which contains the name of the activity
					if (line.contains("android:name")) {
						//extract the activity name skipping the quotes and prefix '.'
						String activity = line.substring(line.indexOf("\"")+2,
								line.lastIndexOf("\""));
						
						if (activity.toLowerCase().equals(activityName.toLowerCase())) {
							activityFound = true;
						}
					}
					
					//add each line of the activity tag
					fileAsArrayList.add(line);
				}
				
//				//add the final activity line
//				fileAsArrayList.add(line);
				
				if (activityFound) {
					//add the browsable intent filter
					fileAsArrayList.add(intentFilter);
				}	
			}
		}
		
		//exit program if activity tag not found in the manifest file
		if (!activityFound) {
			System.out.println("The activity which you supplied does not exist in the manifest."
					+ "\n" + "This implies that you either haven't created the java file: "
					+ "\n" + activityName + ".java"
					+ "\n" + "OR, you haven't added the correspoding activity tag to the manifest file.");
			
			scannerSTDIN.close();
			System.exit(0);
		}
		
		manifestScan.close();
		
		arrayListToFile();
	}
	
	private void arrayListToFile() throws IOException {
		System.out.println("building updated manifest ...............");
			
		//get the path to the original AndroidManifest file
		Path path = Paths.get(AndroidManifest.toURI());
		
		//extract the directory that contains the manifest file
		String dirName = path.toString().substring(0, path.toString().lastIndexOf("/"));
		File dir = new File(dirName);
		
		if (dir.exists()) {
			//delete old manifest
			AndroidManifest.delete();
			
			AndroidManifestUpdated = new File(dir, "AndroidManifest.xml");
			
			FileWriter fw = new FileWriter(AndroidManifestUpdated);
			for (String line : fileAsArrayList) {
				fw.write(line);
				fw.write("\n");
			}
			
			fw.flush();
			fw.close();
			
			newLine();
			System.out.println("Congratulations! The manifest now supports deep linking!");
		} else {
			System.out.println("There was a problem processing the directory which contained the manifest file.");
			
			scannerSTDIN.close();
			
			System.exit(0);
		}
	}
	
	/*
	 * DEBUGGING TOOL
	 */
//	private void printFileArrayList() {
//		for (String line : fileAsArrayList) {
//			System.out.println(line);
//		}
// 	}
	
	/*
	 * Constructs the filter to be injected into manifest
	 * Builds the type of link the app should accept 
	 */
	private void buildFilter() {
		
		newLine();
		System.out.println("I need some information to generate the type of links that will open your app:"
				+ "\n" + "    - the NAME of your app"
				+ "\n" + "    - the HOST of your app (can be the same as the name)"
				+ "\n" + "    - the name of the ACTIVITY which you would like to generate and support deep linking"
				+ "\n"
				+ "\n" + "A full link will begin like this:"
				+ "\n" + "    NAME-app://host..."
				+ "\n" + "e.g. -->  notepad-app://notepad/note.txt"
				+ "\n" + "The rest of the link will be processed and handled in the specified activity.");
		newLine();
		System.out.println("Please enter the NAME of your app: ");
		NAME = scannerSTDIN.nextLine();
		
		System.out.println("Would you like to use the NAME as the host? (y/n)");
		HOST = NAME;
		if (scannerSTDIN.nextLine().toLowerCase() == "n") {
			System.out.println("Please enter the HOST of your app: ");
			HOST = scannerSTDIN.nextLine();
		}
		
		System.out.println("Please enter the name of the ACTIVITY which"
				+ " you would like to generate and support deep linking: ");
		activityName = scannerSTDIN.nextLine();
		
		//FINAL CHECK
		newLine();
		System.out.println("Please confirm the following data");
		System.out.println("    - NAME: " + this.NAME);
		System.out.println("    - HOST: " + this.HOST);
		System.out.println("    - ACTIVITY: " + this.activityName);
		
		newLine();
		System.out.println("Is the above information correct? y/n");
		if (scannerSTDIN.nextLine().equals("n")) {
			buildFilter();
			return;
		}
		
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