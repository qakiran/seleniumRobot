/**
 * Orignal work: Copyright 2015 www.seleniumtests.com
 * Modified work: Copyright 2016 www.infotel.com
 * 				Copyright 2017-2019 B.Hecquet
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * 	http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.seleniumtests.it.stubclasses;

import java.lang.reflect.Method;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.seleniumtests.core.SeleniumTestsContextManager;
import com.seleniumtests.core.TestTasks;
import com.seleniumtests.driver.BrowserType;
import com.seleniumtests.it.driver.support.pages.DriverTestPage;
import com.seleniumtests.it.driver.support.pages.DriverTestPageNativeActions;

public class StubTestClassForDriverTest extends StubParentClass {
	
	@BeforeMethod(groups="stub")
	public void init(Method method) {
		SeleniumTestsContextManager.getThreadContext().setBrowser("chrome");
		SeleniumTestsContextManager.getThreadContext().setOverrideSeleniumNativeAction(true);
		SeleniumTestsContextManager.getThreadContext().setTestRetryCount(1);
	}
	
	/**
	 * added for issue #287 where we need to fail on configuration method to reproduce the bug
	 * @param method
	 */
	@AfterMethod(groups="stub") 
	public void reset(Method method) {
		TestTasks.killProcess("foobar");
	}

	@Test(groups="stub")
	public void testDriver() throws Exception {

		new DriverTestPage(true)
			._writeSomething()
			._reset()
			._sendKeysComposite()
			._clickPicture();
	}
	
	@Test(groups="stub")
	public void testMultipleDriver() throws Exception {
		
		new DriverTestPage(true)
		._writeSomething()
		._reset();
		new DriverTestPage(true, BrowserType.FIREFOX)
		._writeSomething()
		._reset();
	}
	
	@Test(groups="stub")
	public void testDriverShort() throws Exception {
		new DriverTestPage(true);
	}
	
	@Test(groups="stub")
	public void testDriverShort2() throws Exception {
		new DriverTestPage(true);
	}
	
	@Test(groups="stub")
	public void testDriverShort3() throws Exception {
		new DriverTestPage(true);
	}
	
	@Test(groups="stub")
	public void testDriverShort4() throws Exception {
		new DriverTestPage(true);
	}
	
	@Test(groups="stub")
	public void testDriverMultipleSnapshot() throws Exception {
		new DriverTestPage(true)
			._goToNewPage();
	}
	
	@Test(groups="stub")
	public void testDriverShortKo() throws Exception {
		SeleniumTestsContextManager.getThreadContext().setReplayTimeout(1);
		
		new DriverTestPage(true)
			._writeSomethingOnNonExistentElement();
	}
	
	@Test(groups="stub")
	public void testDriverShortKoWithCatchException() throws Exception {
		SeleniumTestsContextManager.getThreadContext().setReplayTimeout(1);
		
		new DriverTestPage(true)
			._writeSomethingOnNonExistentElementWithCatch()
			._writeSomething();
		
	}
	
	@Test(groups="stub")
	public void testDriverCustomSnapshot() throws Exception {
		
		new DriverTestPage(true)
		._writeSomething()
		._captureSnapshot("my snapshot")
		._reset();
	}
	
	@Test(groups="stub")
	public void testDriverWithFailure() throws Exception {
		SeleniumTestsContextManager.getThreadContext().setReplayTimeout(1);
		
		new DriverTestPage(true)
		._writeSomething()
		._writeSomethingOnNonExistentElement();
	}
	
	@Test(groups="stub")
	public void testDriverManualSteps() throws Exception {

		SeleniumTestsContextManager.getThreadContext().setManualTestSteps(true);

		addStep("Write");
		DriverTestPage page = new DriverTestPage(true)
			._writeSomething();
		addStep("Reset");
		page._reset();
	}
	
	/**
	 * check that with selenium override, logging is done
	 * @throws Exception
	 */
	@Test(groups="stub")
	public void testDriverNativeActions() throws Exception {
		new DriverTestPageNativeActions(true)
		.sendKeys()
		.reset()
		.select();
	}
	
	/**
	 * check that without selenium override, logging is not done
	 * @throws Exception
	 */
	@Test(groups="stub")
	public void testDriverNativeActionsWithoutOverride() throws Exception {
		SeleniumTestsContextManager.getThreadContext().setOverrideSeleniumNativeAction(false);
		SeleniumTestsContextManager.getThreadContext().setReplayTimeout(1);
		SeleniumTestsContextManager.getThreadContext().setImplicitWaitTimeout(1); 
		
		new DriverTestPageNativeActions(true)
		.sendKeys()
		.reset()
		.select();
	}
	
	@Test(groups="stub")
	public void testDriverWithHtmlElementWithoutOverride() throws Exception {
		SeleniumTestsContextManager.getThreadContext().setOverrideSeleniumNativeAction(false);
		
		new DriverTestPage(true)
			._writeSomething()
			._reset()
			._clickPicture()
			._sendKeysComposite();
	}
}
