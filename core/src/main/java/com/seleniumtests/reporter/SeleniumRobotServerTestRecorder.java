package com.seleniumtests.reporter;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import org.json.JSONObject;
import org.testng.IReporter;
import org.testng.IResultMap;
import org.testng.ISuite;
import org.testng.ISuiteResult;
import org.testng.ITestContext;
import org.testng.ITestNGMethod;
import org.testng.ITestResult;
import org.testng.xml.XmlSuite;

import com.seleniumtests.connectors.selenium.SeleniumRobotSnapshotServerConnector;
import com.seleniumtests.core.SeleniumTestsContextManager;
import com.seleniumtests.customexception.ConfigurationException;
import com.seleniumtests.customexception.SeleniumRobotServerException;
import com.seleniumtests.util.logging.SeleniumRobotLogger;

public class SeleniumRobotServerTestRecorder extends CommonReporter implements IReporter {

	public SeleniumRobotSnapshotServerConnector getServerConnector() {
		return new SeleniumRobotSnapshotServerConnector();
	}
	
	/**
	 * Generate result for a single test method
	 * @param ve			velocity engine used to generate file
	 * @param testResult	result for this test method
	 */
	public JSONObject generateExecutionLogs(final ITestResult testResult) {
		
		JSONObject executionLogs = new JSONObject();
		executionLogs.put("logs", SeleniumRobotLogger.getTestLogs().get(testResult.getAttribute(SeleniumRobotLogger.METHOD_NAME)));
		
		// exception handling
		StringBuilder stackString = new StringBuilder();
		if (testResult.getThrowable() != null) {
			generateTheStackTrace(testResult.getThrowable(), testResult.getThrowable().getMessage(), stackString);
		}
		
		executionLogs.put("stacktrace", stackString.toString());

		return executionLogs;
	}

	/**
	 * Generate all test reports
	 */
	@Override
	public void generateReport(final List<XmlSuite> xml, final List<ISuite> suites, final String outdir) {
		ITestContext testCtx = SeleniumTestsContextManager.getGlobalContext().getTestNGContext();
		
		if (testCtx == null) {
			logger.error("Looks like your class does not extend from SeleniumTestPlan!");
			return;
		}
		
		// check that seleniumRobot server is alive
		SeleniumRobotSnapshotServerConnector serverConnector = getServerConnector();
		if (!serverConnector.getActive()) {
			logger.info("selenium-robot-server not found or down");
			return;
		} else {
			try {
				serverConnector.createApplication();
				serverConnector.createVersion();
				serverConnector.createEnvironment();
				serverConnector.createSession();
			} catch (SeleniumRobotServerException | ConfigurationException e) {
				logger.error("Error contacting selenium robot serveur", e);
				return;
			}
		}
		
		try {
			for (ISuite suite : suites) {
				Map<String, ISuiteResult> tests = suite.getResults();
				
				recordSuiteResults(serverConnector, tests);
			}
		} catch (SeleniumRobotServerException | ConfigurationException e) {
			logger.error("Error contacting selenium robot serveur", e);
			return;
		}
	
	}
	
	private void recordSuiteResults(SeleniumRobotSnapshotServerConnector serverConnector, Map<String, ISuiteResult> tests) {
		
		String outputDir = SeleniumTestsContextManager.getThreadContext().getOutputDirectory();

		
		for (ISuiteResult r : tests.values()) {
			ITestContext context = r.getTestContext();
			
			Collection<ITestResult> methodResults = new ArrayList<>();
			methodResults.addAll(context.getFailedTests().getAllResults());
			methodResults.addAll(context.getPassedTests().getAllResults());
			methodResults.addAll(context.getSkippedTests().getAllResults());
			
			methodResults = methodResults.stream()
					.sorted((r1, r2) -> Long.compare(r1.getStartMillis(), r2.getStartMillis()))
					.collect(Collectors.toList());
			
			// test case in seleniumRobot naming
			for (ITestResult testResult: methodResults) {
				
				// skipped tests has never been executed and so attribute (set in TestListener) has not been applied
				String testName;
				if (testResult.getStatus() == ITestResult.SKIP) {
					testName = testResult.getName();
				} else {
					testName = testResult.getAttribute(SeleniumRobotLogger.METHOD_NAME).toString();
				}
				
				// record test case
				serverConnector.createTestCase(testName);
				serverConnector.createTestCaseInSession();
				serverConnector.addLogsToTestCaseInSession(generateExecutionLogs(testResult).toString());
				
				List<TestStep> testSteps = TestLogging.getTestsSteps().get(testResult);
				if (testSteps == null) {
					continue;
				}
				
				for (TestStep testStep: testSteps) {
					
					// record test step
					serverConnector.createTestStep(testStep.getName());
					String stepLogs = testStep.toJson().toString();
					
					serverConnector.recordStepResult(!testStep.getFailed(), stepLogs, testStep.getDuration());
					
					if (testStep.getSnapshot() != null) {
						serverConnector.createSnapshot(Paths.get(outputDir, testStep.getSnapshot()).toFile());
					}
				}
			}
		}
	}


}