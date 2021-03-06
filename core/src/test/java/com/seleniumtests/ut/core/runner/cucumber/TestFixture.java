package com.seleniumtests.ut.core.runner.cucumber;

import java.lang.reflect.Field;
import java.util.Map;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.seleniumtests.GenericTest;
import com.seleniumtests.core.SeleniumTestsContextManager;
import com.seleniumtests.core.TestVariable;
import com.seleniumtests.core.runner.cucumber.Fixture;
import com.seleniumtests.customexception.ConfigurationException;
import com.seleniumtests.customexception.ScenarioException;
import com.seleniumtests.it.driver.support.pages.DriverTestPage;
import com.seleniumtests.uipage.htmlelements.TextFieldElement;

public class TestFixture extends GenericTest {

	@Test(groups={"ut"})
	public void testScanner() throws IllegalArgumentException, IllegalAccessException {

		SeleniumTestsContextManager.getGlobalContext().setCucumberImplementationPackage("com.seleniumtests.it.driver.support.pages");
		
		Map<String, Field> allElements = Fixture.scanForElements("com.seleniumtests.it.driver.support.pages");
		
		// check that elements are found, with and without page name
		Assert.assertTrue(allElements.containsKey("textElementNotPresentFirstVisible"));
		Assert.assertTrue(allElements.containsKey("DriverTestPage.textElementNotPresentFirstVisible"));
		
		allElements.get("textElementNotPresentFirstVisible").setAccessible(true);
		Assert.assertTrue(allElements.get("textElementNotPresentFirstVisible").get(null) instanceof TextFieldElement);

	}
	
	@Test(groups={"ut"}, expectedExceptions = ConfigurationException.class)
	public void testScannerNoCucumberPackage() {
		
		SeleniumTestsContextManager.getGlobalContext().setCucumberImplementationPackage("com.seleniumtests.it.driver.support.pages");
		
		Fixture.scanForElements(null);
		
	}
	
	@Test(groups={"ut"})
	public void testScannerWrongCucumberPackage() {
		
		SeleniumTestsContextManager.getGlobalContext().setCucumberImplementationPackage("com.seleniumtests.it.driver.support.pages");
		
		Map<String, Field> allElements = Fixture.scanForElements("com.seleniumtests.it.driver.support.foo");
		Assert.assertTrue(allElements.isEmpty());
		
	}
	
	/**
	 * Check we can get an element, if present
	 */
	@Test(groups={"ut"})
	public void testGetElement() {
		SeleniumTestsContextManager.getGlobalContext().setCucumberImplementationPackage("com.seleniumtests.ut.core.runner.cucumber");
		Assert.assertTrue(new Fixture().getElement("textField").equals("textField"));
		Assert.assertTrue(Fixture.getCurrentPage() instanceof PageForActions);
	}
	
	@Test(groups={"ut"})
	public void testGetElementWithClassName() {
		SeleniumTestsContextManager.getGlobalContext().setCucumberImplementationPackage("com.seleniumtests.ut.core.runner.cucumber");
		Assert.assertTrue(new Fixture().getElement("PageForActions.textField").equals("textField"));
		Assert.assertTrue(Fixture.getCurrentPage() instanceof PageForActions);
	}
	
	/**
	 * Check we get exception if element is not found
	 */
	@Test(groups={"ut"}, expectedExceptions = ScenarioException.class)
	public void testGetElementNotPresent() {
		SeleniumTestsContextManager.getGlobalContext().setCucumberImplementationPackage("com.seleniumtests.ut.core.runner.cucumber");
		new Fixture().getElement("textFoo");
	}
	
	@Test(groups={"ut"})
	public void testGetValue() {
		SeleniumTestsContextManager.getGlobalContext().setCucumberImplementationPackage("com.seleniumtests.ut.core.runner.cucumber");
		Assert.assertEquals(new Fixture().getValue("foo"), "foo");
	}
	
	@Test(groups={"ut"})
	public void testGetValueWithUnknownParam() {
		SeleniumTestsContextManager.getGlobalContext().setCucumberImplementationPackage("com.seleniumtests.ut.core.runner.cucumber");
		Assert.assertEquals(new Fixture().getValue("{{ foo }}"), "");
	}
	
	@Test(groups={"ut"})
	public void testGetValueWithKnownParam() {
		SeleniumTestsContextManager.getGlobalContext().setCucumberImplementationPackage("com.seleniumtests.ut.core.runner.cucumber");
		SeleniumTestsContextManager.getThreadContext().getConfiguration().put("foo", new TestVariable("foo", "bar"));
		Assert.assertEquals(new Fixture().getValue("{{ foo }}"), "bar");
	}
}
