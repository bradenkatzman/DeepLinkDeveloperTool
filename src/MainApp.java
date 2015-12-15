import java.io.FileNotFoundException;
import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

public class MainApp {

	public static void main(String[] args) throws FileNotFoundException, IOException,
											ParserConfigurationException, SAXException {
		DeepLinkDriver driver = new DeepLinkDriver();
		driver.launch();
	}
}
