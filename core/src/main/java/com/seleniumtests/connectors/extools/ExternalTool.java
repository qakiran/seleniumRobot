package com.seleniumtests.connectors.extools;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.seleniumtests.customexception.ConfigurationException;
import com.seleniumtests.customexception.ScenarioException;
import com.seleniumtests.util.FileUtility;
import com.seleniumtests.util.osutility.OSCommand;

public class ExternalTool {
	
	public static final String SELENIUM_TOOL_PREFIX = "SELENIUM_TOOL_";
	
	private Map<String, String> declaredTools;
	private String name;
	private String path;
	private String[] args;
	private Process process;
	private boolean started = false;

	/**
	 * Look for all environment variables beginning with SELENIUM_TOOL_ 
	 * value is the program path
	 * @return
	 */
	public Map<String, String> searchTools() {
		Map<String, String> env = readEnvVariables();
		Map<String, String> tools = new HashMap<>();
		
		for (Entry<String, String> envVar: env.entrySet()) {
			if (envVar.getKey().startsWith(SELENIUM_TOOL_PREFIX)) {
				tools.put(envVar.getKey().replace(SELENIUM_TOOL_PREFIX, ""), envVar.getValue());
			}
		}
		return tools;
	}
	
	public static Map<String, String> readEnvVariables() {
		return System.getenv();
	}
	
	
	
	public ExternalTool() {
		if (declaredTools == null) {
			declaredTools = searchTools();
		}
	}

	/**
	 * @param name	name of the program to start, found by wrapper
	 * @param args	arguments to pass to program
	 */
	public ExternalTool(String name, String ... args) {
		
		this();
		
		if (!declaredTools.containsKey(name)) {
			throw new ConfigurationException(String.format("Program %s is not installed / declared on node. To declare a program, add and environment variable named %s<program_name>=<program_path>", 
					name, SELENIUM_TOOL_PREFIX));
		}
		
		if (!FileUtility.fileExists(declaredTools.get(name))) {
			throw new ConfigurationException(String.format("Program %s is not available at %s", 
					name, declaredTools.get(name)));
		}
		
		this.path = declaredTools.get(name);
		this.name = name;
		this.args = args;
		this.process = null;
	}
	
	/**
	 * Starts the program and store PIDs of the started program if it does not stop before 
	 * @return		a unique ID
	 */
	public ExternalTool start() {
		
		// check not already started
		if (started) {
			throw new ScenarioException("Program is already started");
		}
		
		// start program
		List<String> programCmd = new ArrayList<>();
		programCmd.add(path);
		programCmd.addAll(Arrays.asList(args));
		process = OSCommand.executeCommand(programCmd.toArray(new String[] {})); 
		
		// mark program as started
		started = true;
		
		return this;
	}
	
	/**
	 * Stops the program
	 */
	public ExternalTool stop() {
		
		// kill
		try {
			process.destroyForcibly();
		} catch (Exception e) {}
		
		// mark program as stopped
		started = false;
		
		return this;
	}

	public String getName() {
		return name;
	}

	public String[] getArgs() {
		return args;
	}

	public boolean isStarted() {
		return started;
	}

	public Map<String, String> getDeclaredTools() {
		return declaredTools;
	}

}
