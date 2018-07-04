/**
 * Orignal work: Copyright 2015 www.seleniumtests.com
 * Modified work: Copyright 2016 www.infotel.com
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
package com.seleniumtests.it.driver;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.seleniumtests.driver.BrowserType;
import com.seleniumtests.it.driver.support.GenericMultiBrowserTest;

/**
 * Checks that it's possible to interact with elements in iframe
 * @author behe
 *
 */
public class TestFrameForAngular extends GenericMultiBrowserTest {

	public TestFrameForAngular() throws Exception {
		super(BrowserType.FIREFOX, "DriverTestAngularFrame"); 
	}
	
	/**
	 * Check that even in frame, subelements can be found
	 */
	@Test(groups={"it"})
	public void testAngularSelectByText() { 
		angularFramePage.angularSelectListIFrame.selectByText("Option 1");
		Assert.assertEquals(angularFramePage.angularSelectListIFrame.getSelectedText(), "Option 1");
	}
	
 	
}