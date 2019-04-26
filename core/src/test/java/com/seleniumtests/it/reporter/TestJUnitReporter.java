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
package com.seleniumtests.it.reporter;

import static org.mockito.Mockito.spy;

import java.io.File;
import java.nio.file.Paths;

import org.apache.commons.io.FileUtils;
import org.json.JSONObject;
import org.testng.Assert;
import org.testng.ITestContext;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;
import org.testng.xml.XmlSuite.ParallelMode;

import com.seleniumtests.core.SeleniumTestsContextManager;
import com.seleniumtests.reporter.reporters.CustomReporter;

/**
 * Test that default reporting contains results.json file (CustomReporter.java) with default summary reports defined in SeleniumTestsContext.DEFAULT_CUSTOM_SUMMARY_REPORTS
 * @author s047432
 *
 */
public class TestJUnitReporter extends ReporterTest {
	
	private CustomReporter reporter;

	
	@AfterMethod(groups={"it"})
	private void deleteGeneratedFiles() {
		if (reporter == null) {
			return;
		}
		File outDir = new File(SeleniumTestsContextManager.getGlobalContext().getOutputDirectory());
		for (File file: outDir.listFiles()) {
			if ("results.json".equals(file.getName())) {
				file.delete();
			}
		}
		
	}
	
	@Test(groups={"it"})
	public void testReportGeneration(ITestContext testContext) throws Exception {

		executeSubTest(1, new String[] {"com.seleniumtests.it.stubclasses.StubTestClass"}, ParallelMode.METHODS, new String[] {"testAndSubActions", "testInError", "testWithException"});
		String outDir = new File(SeleniumTestsContextManager.getGlobalContext().getOutputDirectory()).getAbsolutePath();

		// check all files are generated with the right name
		Assert.assertTrue(Paths.get(outDir, "junitreports", "TEST-com.seleniumtests.it.stubclasses.StubTestClass.xml").toFile().exists());
	}
	
	@Test(groups={"it"})
	public void testReportContent(ITestContext testContext) throws Exception {

		executeSubTest(new String[] {"com.seleniumtests.it.stubclasses.StubTestClass", "com.seleniumtests.it.stubclasses.StubTestClass2"});
		String outDir = new File(SeleniumTestsContextManager.getGlobalContext().getOutputDirectory()).getAbsolutePath();
		
		String result = FileUtils.readFileToString(Paths.get(outDir, "junitreports", "TEST-com.seleniumtests.it.stubclasses.StubTestClass.xml").toFile());
		Assert.assertTrue(result.contains("<failure type=\"java.lang.AssertionError\" message=\"error\">"));
		Assert.assertTrue(result.contains("<error type=\"com.seleniumtests.customexception.DriverExceptions\" message=\"some exception\">"));
		Assert.assertTrue(result.contains("name=\"testAndSubActions\""));
		Assert.assertTrue(result.contains("name=\"testWithException\""));
		Assert.assertTrue(result.contains("name=\"testInError\""));
		
		Assert.assertTrue(Paths.get(outDir, "junitreports", "TEST-com.seleniumtests.it.stubclasses.StubTestClass2.xml").toFile().exists());
		
	}

}