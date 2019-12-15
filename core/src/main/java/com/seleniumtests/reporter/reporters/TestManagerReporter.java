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
package com.seleniumtests.reporter.reporters;

import java.util.Map;
import java.util.Set;

import org.testng.IReporter;
import org.testng.ITestContext;
import org.testng.ITestResult;

import com.seleniumtests.connectors.tms.TestManager;
import com.seleniumtests.core.SeleniumTestsContextManager;

/**
 * Class for sending test reports to test managers
 * @author behe
 *
 */
public class TestManagerReporter extends CommonReporter implements IReporter {

	@Override
	protected void generateReport(Map<ITestContext, Set<ITestResult>> resultSet, String outdir) {

		// issue #81: use global context instead
		TestManager testManager = SeleniumTestsContextManager.getGlobalContext().getTestManagerInstance();

		if (testManager == null) {
			return;
		} else {
			testManager.login();
			testManager.recordResult();
			testManager.recordResultFiles();
			testManager.logout();
		}
	}


}
