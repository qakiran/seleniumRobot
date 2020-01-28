// Generated by Selenium IDE
import org.junit.Test;
import org.junit.Before;
import org.junit.After;
import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.core.IsNot.not;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Alert;
import org.openqa.selenium.Keys;
import java.util.*;
public class MysuiteTest {
  private WebDriver driver;
  private Map<String, Object> vars;
  JavascriptExecutor js;
  @Before
  public void setUp() {
    driver = new FirefoxDriver();
    js = (JavascriptExecutor) driver;
    vars = new HashMap<String, Object>();
  }
  @After
  public void tearDown() {
    driver.quit();
  }
  public void seleniumhq() {
    driver.get("https://www.seleniumhq.org/");
    driver.manage().window().setSize(new Dimension(1000, 683));
    driver.findElement(By.linkText("Blog")).click();
  }
  @Test
  public void jcommander() {
    vars.put("toto", "coucou");
    System.out.println(vars.get("foo"));
    driver.get("http://www.jcommander.org//");
    driver.manage().window().setSize(new Dimension(768, 683));
    driver.findElement(By.linkText("2.1. Boolean")).click();
    System.out.println("STEP:Boolean link");
    driver.findElement(By.linkText("21. Parameter delegates")).click();
    assertThat(driver.findElement(By.linkText("2.1. Boolean")).getText(), is("2.1. Boolean"));
    seleniumhq();
  }
}