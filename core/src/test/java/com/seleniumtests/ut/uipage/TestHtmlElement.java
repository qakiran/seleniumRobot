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
package com.seleniumtests.ut.uipage;

import org.testng.annotations.Test;

import com.seleniumtests.driver.BrowserType;
import com.seleniumtests.it.driver.TestDriver;

public class TestHtmlElement extends TestDriver {

	public TestHtmlElement() throws Exception {
		super(BrowserType.HTMLUNIT);
	}
	
	@Test(groups={"ut"})
	public void testClickActionCheckbox() {
		// skip as htmlunit does not support it
	}
	
	@Test(groups={"ut"})
	public void testAutoScrolling() {
		// skip as htmlunit does not support it
	}
	
	@Test(groups={"ut"})
	public void testClickActionDiv() {
		// skip as htmlunit does not support it
	}
	
	@Test(groups={"ut"})
	public void testClickActionRadio() {
		// skip as htmlunit does not support it
	}
	
	@Test(groups={"ut"})
	public void testDoubleClickActionDiv() {
		// skip as htmlunit does not support it
	}
	
	@Test(groups={"ut"})
	public void testUploadFileWithRobot() {
		// skip as htmlunit does not support it
	}
	
	@Test(groups={"ut"})
	public void testUploadFileWithRobotKeyboard() {
		// skip as htmlunit does not support it
	}
	
	@Test(groups= {"nogroup"})
	public void test() {
		super.testTextElementInsideHtmlElementIsPresent();
	}

}
