import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Scanner;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import components.*;


public class DeepLinkDriver {
	private ProcessUserData processUserData;
	private Scanner scannerSTDIN;
	
	public DeepLinkDriver() {
		this.scannerSTDIN = new Scanner(System.in);
		processUserData = new ProcessUserData(this.scannerSTDIN);
	}
	
	public void launch() throws FileNotFoundException, IOException, 
								ParserConfigurationException, SAXException {
		System.out.println("------------------------------------------------------------------------------------");
		System.out.println("Welcome to DeepLink - a developer tool for Android");
		newLine();
		System.out.println("This tool will inject your Android application with deep"
				+ " link support");
		System.out.println("After execution, your application will "
				+ "generate links that will open up any state of your app");
		System.out.println("    i.e. any activity "
				+ " with any data set you choose to support.");
		
		System.out.println("Further, your application "
				+ "will be able to support these links by processing " + "\n" + "them from the web "
				+ "and opening the corresponding activity with appropriate configurations.");
		
		newLine();
		System.out.println("------------------------------------------------------------------------------------");
		getUserData();
	}
	
	private void getUserData() throws FileNotFoundException,
								IOException, ParserConfigurationException, SAXException {
		processUserData.processRootDirectory();
	}
	
	public void newLine() {
		System.out.println("");
	}

}
