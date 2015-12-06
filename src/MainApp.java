import java.io.FileNotFoundException;
import java.io.IOException;

public class MainApp {

	public static void main(String[] args) throws FileNotFoundException, IOException {
		DeepLinkDriver driver = new DeepLinkDriver();
		driver.launch();
	}
}
