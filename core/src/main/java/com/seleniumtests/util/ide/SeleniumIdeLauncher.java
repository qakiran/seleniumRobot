package com.seleniumtests.util.ide;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

import org.apache.log4j.Logger;
import org.testng.TestNG;
import org.testng.xml.XmlClass;
import org.testng.xml.XmlInclude;
import org.testng.xml.XmlSuite;
import org.testng.xml.XmlSuite.FailurePolicy;
import org.testng.xml.XmlSuite.ParallelMode;
import org.testng.xml.XmlTest;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.seleniumtests.core.SeleniumTestsContext;
import com.seleniumtests.util.logging.SeleniumRobotLogger;

import net.openhft.compiler.CompilerUtils;

public class SeleniumIdeLauncher {
	
	private static final Logger logger = SeleniumRobotLogger.getLogger(SeleniumIdeLauncher.class);
	
	@Parameter(names = "-scripts", variableArity = true, description= "List of selenium .java files to execute within seleniumRobot. These files are exported from Selenium IDE")
	public List<String> scripts = new ArrayList<>();

	public static void main(String ... args) throws ClassNotFoundException {
		
		// read program options
		SeleniumIdeLauncher main = new SeleniumIdeLauncher();
        JCommander.newBuilder()
            .addObject(main)
            .build()
            .parse(args);

        main.executeScripts();
	}

	public void executeScripts() throws ClassNotFoundException {
		executeScripts(scripts);
	}
	
	public void executeScripts(List<String> scriptFiles) throws ClassNotFoundException {
		Map<String, String> classCodes = generateTestClasses(scriptFiles);
		executeGeneratedClasses(classCodes);
	}
	
	/**
	 * Generates a compatible test class from the code exported from Selenium IDE
	 * We take all the methods (without \@After and \@Before) and copy them in a new file
	 */
	public Map<String, String> generateTestClasses(List<String> scriptFiles) {
		Map<String, String> classCodes = new HashMap<>();
		
		for (String scriptFile: scriptFiles) {
			try {
				classCodes.putAll(new SeleniumIdeParser(scriptFile).parseSeleniumIdeFile());
			} catch (FileNotFoundException e) {
				logger.error(String.format("File %s cannot be parsed: %s", scriptFile, e.getMessage()));
			}
		}
		return classCodes;
	}
	
	public void executeGeneratedClasses(Map<String, String> classCodes) throws ClassNotFoundException {
//		URLClassLoader sysLoader = (URLClassLoader) ClassLoader.getSystemClassLoader();
//	    URL urls[] = sysLoader.getURLs();
		ClassLoader loader = Thread.currentThread().getContextClassLoader();
//        WeavingURLClassLoader weaver = null;
//		try {
//			weaver = new WeavingURLClassLoader(
//					sysLoader.getURLs(),
//					new URL[]{new File("./").toURI().toURL()},
////        		sysLoader.getURLs(),
//			        loader
//			);
//		} catch (MalformedURLException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		
		// load web page classes
		List<String> classes = new ArrayList<>();
		for (Entry<String, String> entry: classCodes.entrySet()) {
			if (entry.getKey().endsWith("Page")) {
				Class aClass = CompilerUtils.CACHED_COMPILER.loadFromJava(loader, entry.getKey(), entry.getValue());
			}
		}
		
		// now compile tests which use page classes
		for (Entry<String, String> entry: classCodes.entrySet()) {
			if (!entry.getKey().endsWith("Page")) {
				Class aClass = CompilerUtils.CACHED_COMPILER.loadFromJava(loader, entry.getKey(), entry.getValue());
				classes.add(aClass.getCanonicalName());
			}
		}
		
		Thread.currentThread().setContextClassLoader(loader);
		executeTest(1, classes.toArray(new String[] {}), ParallelMode.NONE, new String[] {});
	}
	
	private TestNG executeTest(int threadCount, String[] testClasses, XmlSuite.ParallelMode parallelMode, String[] methods) {

		XmlSuite suite = new XmlSuite();
		suite.setName("TmpSuite");
		suite.setParallel(ParallelMode.NONE);
		suite.setFileName("/home/test/seleniumRobot/data/core/testng/testLoggging.xml");
		Map<String, String> suiteParameters = new HashMap<>();
		suiteParameters.put(SeleniumTestsContext.OVERRIDE_SELENIUM_NATIVE_ACTION, "true");
//		suiteParameters.put(SeleniumTestsContext.MANUAL_TEST_STEPS, "true");
		suite.setParameters(suiteParameters);
		suite.setConfigFailurePolicy(FailurePolicy.CONTINUE);
		List<XmlSuite> suites = new ArrayList<>();
		suites.add(suite);
		
		
		if (threadCount > 1) {
			suite.setThreadCount(threadCount);
			suite.setParallel(XmlSuite.ParallelMode.METHODS);
		}
		
		for (String testClass: testClasses) {
			XmlTest test = new XmlTest(suite);
			test.setName(String.format("%s_%d", testClass.substring(testClass.lastIndexOf(".") + 1), new Random().nextInt()));
			List<XmlClass> classes = new ArrayList<>();
			XmlClass xmlClass = new XmlClass(testClass);
			if (methods.length > 0) {
				List<XmlInclude> includes = new ArrayList<>();
				for (String method: methods) {
					includes.add(new XmlInclude(method));
				}
				xmlClass.setIncludedMethods(includes);
			}
			classes.add(xmlClass);
			test.setXmlClasses(classes) ;
		}		
		
		TestNG tng = new TestNG(false);
		tng.setXmlSuites(suites);
		tng.run(); 
		
		return tng;
	}
}
