import java.util.Scanner;

import components.*;
import components.intents.*;


public class DeepLinkDriver {
	private UserData userData;
	private ManifestInject manifestInject;
	private DeepLinkGenerate deepLinkGenerate;
	private GenerateIntent generateIntent;
	private ProcessIntent processIntent;
	
	public DeepLinkDriver() {
		userData = new UserData();
		manifestInject = new ManifestInject();
		deepLinkGenerate = new DeepLinkGenerate();
		generateIntent = new GenerateIntent();
		processIntent = new ProcessIntent();
	}
	
	public void launch() {
		System.out.println("-----------------------------------------------");
		System.out.println("Welcome to DeepLink - a developer tool for Android");
		newLine();
		System.out.println("This tool will inject your Android application with deep"
				+ " link support");
		System.out.println("After execution, your application will be able to "
				+ "generate links that will open up any state of your app");
		System.out.println("    i.e. any activity "
				+ " with any data set you choose to support.");
		
		System.out.println("Further, your application "
				+ "will be able to support these links by processing " + "\n" + "them from the web "
				+ "and opening the corresponding activity and data set.");
		
		newLine();
		System.out.println("-----------------------------------------------");
		getUserData();
	}
	
	private void getUserData() {
		userData.processRootDirectory();
		
	}
	
	public void newLine() {
		System.out.println("");
	}

}
