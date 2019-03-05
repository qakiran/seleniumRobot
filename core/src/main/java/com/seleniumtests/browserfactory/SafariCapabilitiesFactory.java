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
package com.seleniumtests.browserfactory;

import org.openqa.selenium.MutableCapabilities;
import org.openqa.selenium.safari.SafariOptions;

import com.seleniumtests.driver.BrowserType;
import com.seleniumtests.driver.DriverConfig;

public class SafariCapabilitiesFactory extends IDesktopCapabilityFactory {

	public SafariCapabilitiesFactory(DriverConfig webDriverConfig) {
		super(webDriverConfig);
	}

	@Override
	protected MutableCapabilities getDriverOptions() {
		SafariOptions options = new SafariOptions();
		
		// not yet supported
        //options.setPageLoadStrategy(webDriverConfig.getPageLoadStrategy());
		return options;
	}

	@Override
	protected String getDriverPath() {
		return null;
	}

	@Override
	protected BrowserType getBrowserType() {
		return BrowserType.SAFARI;
	}

	@Override
	protected String getDriverExeProperty() {
		return null;
	}

	@Override
	protected String getBrowserBinaryPath() {
		return null;
	}

	@Override
	protected void updateOptionsWithSelectedBrowserInfo(MutableCapabilities options) {
		// nothing to do
	}
}
