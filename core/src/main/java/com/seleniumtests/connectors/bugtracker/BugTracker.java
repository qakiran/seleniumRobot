package com.seleniumtests.connectors.bugtracker;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.log4j.Logger;

import com.seleniumtests.connectors.bugtracker.jira.JiraBean;
import com.seleniumtests.connectors.bugtracker.jira.JiraConnector;
import com.seleniumtests.core.SeleniumTestsContextManager;
import com.seleniumtests.core.TestVariable;
import com.seleniumtests.customexception.ConfigurationException;
import com.seleniumtests.driver.screenshots.ScreenShot;
import com.seleniumtests.reporter.logger.TestStep;
import com.seleniumtests.util.FileUtility;
import com.seleniumtests.util.logging.SeleniumRobotLogger;

public abstract class BugTracker {

	public static final String STEP_KO_PATTERN = "Step %d KO\n\n";
	protected static Logger logger = SeleniumRobotLogger.getLogger(BugTracker.class);

	private String createIssueSummary(
			String application,
			String environment,
			String testNgName,
			String testName) {
		return String.format("[Selenium][%s][%s][%s] test %s KO", application, environment, testNgName, testName);
	}
	
	/**
	 * Create an issue object
	 * @param testName			method name (name of scenario)
	 * @param description		Description of the test. May be null
	 * @param testSteps			Test steps of the scenario
	 * @param issueOptions		options for the new issue 
	 * @return
	 */
	public IssueBean createIssueBean(
			String summary,
			String testName,
			String description,
			List<TestStep> testSteps, 
			Map<String, String> issueOptions) {

		List<ScreenShot> screenShots = new ArrayList<>();
		StringBuilder fullDescription = new StringBuilder(description);
		int stepIdx = 0;
		boolean found = false;
		for (TestStep testStep: testSteps) {
			if (testStep.getName().startsWith("Test end")) {
				
				fullDescription.append(String.format(STEP_KO_PATTERN, stepIdx));

				fullDescription.append(String.format("Step '%s' in error\n\n", testSteps.get(stepIdx - 1).getName()));
				fullDescription.append(testStep.toString() + "\n\n");
				screenShots = testStep.getSnapshots().stream()
										.map(s -> s.getScreenshot())
										.collect(Collectors.toList());
				found = true;
				break;
			}
			stepIdx += 1;
		}

		// don't create issue if test has not been executed
		if (stepIdx == 0 || !found) {
			return null;
		}

		fullDescription.append("For more details, see attached .zip file");

		// get HTML report generated by SeleniumTestsReporter2 class
		File zipFile = null;
		Path outRoot = null;
		try {
			File resourcesFolder = Paths.get(SeleniumTestsContextManager.getGlobalContext().getOutputDirectory(), "resources").toFile().getAbsoluteFile();
			File testResultFolder = Paths.get(SeleniumTestsContextManager.getGlobalContext().getOutputDirectory(), testName).toFile().getAbsoluteFile();
			outRoot = Files.createTempDirectory("result");
			Path tempResourcesFolder = Files.createDirectory(Paths.get(outRoot.toString(), "resources"));
			Path tempResultFolder = Files.createDirectory(Paths.get(outRoot.toString(), testName));

			IOFileFilter aviFiles = FileFilterUtils.notFileFilter(
					FileFilterUtils.or(
							FileFilterUtils.suffixFileFilter(".avi", null), // exclude video
							FileFilterUtils.suffixFileFilter(".zip", null)  // exclude previous reports
					)
			);

			// copy test results
			FileUtils.copyDirectory(testResultFolder, tempResultFolder.toFile(), aviFiles);

			// copy resources
			try {
				FileUtils.copyDirectory(resourcesFolder, tempResourcesFolder.toFile(), aviFiles);
			} catch (IOException e) {}

			// create zip
			zipFile = File.createTempFile("result", ".zip");
			zipFile.deleteOnExit();
			FileUtility.zipFolder(outRoot.toFile(), zipFile);
		} catch (IOException e) {
		} finally {
			if (outRoot != null) {
				try {
					FileUtils.deleteDirectory(outRoot.toFile());
				} catch (IOException e) {}
			}
		}
		

		String assignee = issueOptions.get("assignee");
		String reporter = issueOptions.get("reporter");

		if (this instanceof JiraConnector) {

			String priority = issueOptions.get("priority");
			String issueType = issueOptions.get("jira.issueType");
			Map<String, String> customFieldsValues = new HashMap<>();
			for (String variable: issueOptions.keySet()) {
				if (variable.startsWith("jira.field.")) {
					customFieldsValues.put(variable.replace("jira.field.", ""), issueOptions.get(variable));
				}
			}

			List<String> components = new ArrayList<>();
			if (issueOptions.get("jira.components") != null) {
				components = Arrays.asList(issueOptions.get("jira.components").split(","));
			}
			
			
			return new JiraBean(summary,
					fullDescription.toString(),
					priority,
					issueType,
					testName,
					testSteps.get(stepIdx),
					assignee,
					reporter,
					screenShots,
					zipFile,
					customFieldsValues,
					components);
		} else {
			return new IssueBean(summary,
				fullDescription.toString(),
				testName,
				testSteps.get(stepIdx),
				assignee,
				reporter,
				screenShots,
				zipFile);
		}
	}
	
	/**
	 * Creates an issue if it does not already exist
	 * First we search for a similar open issue (same summary)
	 * If it exists, then we check the step where we failed. If it's the same, we do nothing, else, we update the issue, saying we failed on an other step.
	 * @param application	the tested application
	 * @param environment	the environment where we tested
	 * @param testNgName	name of the TestNG test. Helps to build the summary
	 * @param testName		method name (name of scenario)
	 * @param description	Description of the test. May be null
	 * @param testSteps		Test steps of the scenario
	 * @param issueOptions		options for the new issue 
	 */
	public void createIssue(
			String application,
			String environment,
			String testNgName,
			String testName,
			String description,
			List<TestStep> testSteps,
			Map<String, String> issueOptions) {


		String summary = createIssueSummary(application, environment, testNgName, testName);
		IssueBean issueBean = createIssueBean(summary, testName, description, testSteps, issueOptions);
		if (issueBean == null) {
			return;
		}
		
		// get index of the last step to know where we failed
		int stepIdx = 0;
		for (TestStep testStep: testSteps) {
			if (testStep.getName().startsWith("Test end")) {
				break;
			}
			stepIdx += 1;
		}
		
		// check that a Jira does not already exist for the same test / appication / version. Else, complete it if the step is error is not the same
		IssueBean currentIssue = issueAlreadyExists(issueBean);
		if (currentIssue != null) {
			if (currentIssue.getDescription().contains(String.format(STEP_KO_PATTERN, stepIdx))) {
				logger.info(String.format("Issue %s already exists", currentIssue.getId()));
			} else {
				updateIssue(currentIssue.getId(), "Scenario fails on another step " + issueBean.getTestStep().getName(), issueBean.getScreenShots());
			}
		} else {
			createIssue(issueBean);
			logger.info(String.format("Issue %s created", issueBean.getId()));
		}
	}
	
	/**
	 * Close issue if it exists
	 * @param application
	 * @param environment
	 * @param testNgName
	 * @param testName
	 */
	public void closeIssue( 
			String application,
			String environment,
			String testNgName,
			String testName) {
		
		String summary = createIssueSummary(application, environment, testNgName, testName);
		IssueBean issueBean;
		
		if (this instanceof JiraConnector) {
			issueBean = new JiraBean("", summary, "", "", "");
		} else {
			issueBean = new IssueBean(summary, "", "", null, null, null, null, null);
		}
		
		// Close issue if it exists
		IssueBean currentIssue = issueAlreadyExists(issueBean);
		if (currentIssue != null) {
			closeIssue(currentIssue.getId(), "Test is now OK");
		}
	}
	
	  /**
     * Check if issue already exists, and if so, returns an updated IssueBean
     *
     * @return
     */
	public abstract IssueBean issueAlreadyExists(IssueBean issue);
	
	/**
	 * Update an existing issue with a new message and new screenshots.
	 * Used when an issue has already been raised, we complete it
	 * @param issueId			Id of the issue
	 * @param messageUpdate		message to add to description
	 * @param screenShots		New screenshots
	 */
	public abstract void updateIssue(String issueId, String messageUpdate, List<ScreenShot> screenShots);
	
	/**
	 * Create an issue
	 * @param issueBean
	 * @return
	 */
	public abstract void createIssue(IssueBean issueBean);
	

	/**
     * Close issue
     * @param issueId           ID of the issue
     * @param closingMessage    Message of closing
     */
    public abstract void closeIssue(String issueId, String closingMessage);

	public static BugTracker getInstance(String type, String url, String project, String user, String password, Map<String, String> bugtrackerOptions) {

		if ("jira".equals(type)) {
			return new JiraConnector(url, project, user, password, bugtrackerOptions);
		} else if ("fake".equals(type)) {
			return new FakeBugTracker();
		} else {
			throw new ConfigurationException(String.format("BugTracker type [%s] is unknown, valid values are: ['jira']", type));
		}
	}
}
