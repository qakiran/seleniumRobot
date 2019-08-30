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
package com.seleniumtests.it.driver.support.pages;

import org.openqa.selenium.By;

import com.seleniumtests.core.SeleniumTestsContextManager;
import com.seleniumtests.driver.BrowserType;
import com.seleniumtests.uipage.PageObject;
import com.seleniumtests.uipage.htmlelements.ButtonElement;
import com.seleniumtests.uipage.htmlelements.HtmlElement;
import com.seleniumtests.uipage.htmlelements.TextFieldElement;

public class DriverScrollingTestPage extends PageObject {
	
	public static final TextFieldElement textElement = new TextFieldElement("Text", By.id("text2"));
	public static final ButtonElement resetButton = new ButtonElement("Reset", By.id("button2"));
	public static final HtmlElement greenBox = new HtmlElement("button to scroll into view", By.id("greenBox"));
	
	
	private String openedPageUrl;
	
	public DriverScrollingTestPage() throws Exception {
        super(textElement);
    }
    
    public DriverScrollingTestPage(boolean openPageURL) throws Exception {
    	this(openPageURL, getPageUrl(SeleniumTestsContextManager.getThreadContext().getBrowser()));
    }
    
    public DriverScrollingTestPage(boolean openPageURL, BrowserType browserType) throws Exception {
    	super(textElement, getPageUrl(browserType), browserType, "second", null);
    }
    
    public DriverScrollingTestPage(boolean openPageURL, String url) throws Exception {
    	super(textElement, openPageURL ? url : null);
    	openedPageUrl = url;
    }
    
    public DriverScrollingTestPage _writeSomething() {
    	textElement.sendKeys("a text");
    	return this;
    }
   
    public DriverScrollingTestPage _reset() {
    	resetButton.click();
    	return this;
    }
    
    public static String getPageUrl(BrowserType browserType) {
    	if (browserType == BrowserType.FIREFOX) {
			return "file://" + Thread.currentThread().getContextClassLoader().getResource("tu/test.html").getFile();
		} else {
			return "file:///" + Thread.currentThread().getContextClassLoader().getResource("tu/test.html").getFile();
		}
    }

	public String getOpenedPageUrl() {
		return openedPageUrl;
	}
}