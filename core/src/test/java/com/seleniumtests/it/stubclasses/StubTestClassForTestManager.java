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

import java.io.IOException;
import java.lang.reflect.Method;

import org.openqa.selenium.WebDriverException;
import org.testng.Assert;
import org.testng.SkipException;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.CustomAttribute;
import org.testng.annotations.Test;

import com.seleniumtests.util.helper.WaitHelper;

public class StubTestClassForTestManager extends StubParentClass {
	

	@BeforeMethod(groups={"stub"})
	public void set(Method method) {
		WaitHelper.waitForMilliSeconds(100);
	}
	
	@Test(groups="stub", attributes = {@CustomAttribute(name = "testId", values = "12")})
	public void testAndSubActions() throws IOException {
		
	}
	
	@Test(groups="stub", attributes = {@CustomAttribute(name = "testId", values = "13")})
	public void testInError() {
		createOrUpdateLocalParam("bugtracker.assignee", "you2"); // change value for bugtracker assignee so that w check it's updated
		throw new WebDriverException();
	}
	
	@Test(groups="stub", attributes = {@CustomAttribute(name = "testId", values = "14")})
	public void testWithAssert() {
		Assert.fail("error");
	}
	
	@Test(groups="stub", attributes = {@CustomAttribute(name = "testId", values = "15")})
	public void testSkipped() {
		throw new SkipException("skip this test");
	}

}
