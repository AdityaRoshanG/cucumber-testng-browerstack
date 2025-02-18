package local;

import com.browserstack.local.Local;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class StepDefs {
    public static final String USERNAME = System.getenv("BROWSERSTACK_USERNAME"); //OR String USERNAME = "<browserstack-username>"
    public static final String AUTOMATE_KEY = System.getenv("BROWSERSTACK_ACCESS_KEY");//OR String AUTOMATE_KEY = "<browserstack-accesskey>"

    public static final String URL = "https://" + USERNAME + ":" + AUTOMATE_KEY + "@hub-cloud.browserstack.com/wd/hub";
    DesiredCapabilities caps;
    JavascriptExecutor jse;
    WebDriver driver;
    private static Local l;
    WebElement element;

    @Given("Open Browser")
    public void open_Browser() throws Exception {
        caps = new DesiredCapabilities();
        caps.setCapability("os", "Windows");
        caps.setCapability("os_version", "10");
        caps.setCapability("browser", "Chrome");
        caps.setCapability("browser_version", "latest");
        caps.setCapability("build", "cucumber-java-testng-browserstack");
        caps.setCapability("name", "local_test");
        caps.setCapability("browserstack.local", "true");

        if (caps.getCapability("browserstack.local") != null && caps.getCapability("browserstack.local") == "true") {
            //System.out.println("INSIDE CODE BINDINGS");
            l = new Local();
            if (caps.getCapability("browserstack.local") != null && caps.getCapability("browserstack.local") == "true") {
                l = new Local();
                Map<String, String> options = new HashMap<String, String>();
                options.put("key", AUTOMATE_KEY);
                l.start(options);
            }
            driver = new RemoteWebDriver(new URL(URL), caps);
            jse = (JavascriptExecutor) driver;
        }
    }
    @When("Go to localhost")
    public void go_to_localhost () {
        driver.get("http://bs-local.com:45691/check");
    }

    @Then("Retrieve Title if Up and Running")
    public void retrieve_Title_if_Up_and_Running () throws Exception {
        System.out.println(driver.getTitle());
        String content = driver.findElement(By.xpath("/html/body")).getText();
        if(content.contains("Up and running"))
            jse.executeScript("browserstack_executor: {\"action\": \"setSessionStatus\", \"arguments\": {\"status\": \"passed\", \"reason\": \"Successful!\"}}");
        else
            jse.executeScript("browserstack_executor: {\"action\": \"setSessionStatus\", \"arguments\": {\"status\": \"failed\", \"reason\": \"Unsuccessful!\"}}");

        driver.quit();
        if (l != null) {
            l.stop();
        }
        if (l != null) l.stop();
    }
}
