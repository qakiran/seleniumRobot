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
package com.seleniumtests.core.aspects;

import static java.util.concurrent.TimeUnit.MILLISECONDS;

import java.lang.reflect.Field;
import java.net.Proxy;
import java.net.URL;
import java.time.Duration;

import org.apache.log4j.Logger;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.openqa.selenium.remote.HttpCommandExecutor;
import org.openqa.selenium.remote.http.HttpClient;
import org.openqa.selenium.remote.http.HttpClient.Builder;
import org.openqa.selenium.remote.internal.OkHttpClient;

import com.google.common.base.Strings;
import com.seleniumtests.core.SeleniumTestsContextManager;
import com.seleniumtests.util.logging.SeleniumRobotLogger;

import okhttp3.ConnectionPool;
import okhttp3.Credentials;
import okhttp3.Request;
import okhttp3.Response;

@Aspect
public class SocketTimeout {
	
	private static boolean socketTimeoutUpdated = false;
	private static final Logger logger = SeleniumRobotLogger.getLogger(SocketTimeout.class);

	/**
	 * Change timeout after HttpCommandExecutor creation
	 * 
	 * HttpCommandExecutor is responsible for sending commands to browser
	 * Sometimes, browser is stuck. Default behaviour is a 3 hours wait
	 * Change this to avoid test to be stuck during this time
	 * 
	 * @param joinPoint
	 * @throws NoSuchFieldException
	 * @throws SecurityException
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 */
	@After("initialization(org.openqa.selenium.remote.HttpCommandExecutor.new (..))")
	public void changeTimeout2(JoinPoint joinPoint) throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		HttpClient.Factory httpClientFactory;
		
		
		HttpCommandExecutor commandExecutor = (HttpCommandExecutor)joinPoint.getThis();
		
		Field remoteServerField = HttpCommandExecutor.class.getDeclaredField("remoteServer");
		remoteServerField.setAccessible(true);
		URL url = (URL) remoteServerField.get(commandExecutor);

		HttpClient client;
		if (SeleniumTestsContextManager.isMobileTest()) {
			client = createClient(url, Duration.ofMinutes(6), Duration.ofMinutes(6));
		} else {
			client = createClient(url, Duration.ofMinutes(2), Duration.ofMinutes(2));
		}
		
		Field clientField = HttpCommandExecutor.class.getDeclaredField("client");
		clientField.setAccessible(true);
		clientField.set(commandExecutor, client);
		
		logger.info("Socket timeout for driver communication updated");
		
		socketTimeoutUpdated = true;
	}

	public static boolean isSocketTimeoutUpdated() {
		return socketTimeoutUpdated;
	}
	
	private final ConnectionPool pool = new ConnectionPool();
	protected Proxy proxy = null;
	
	public HttpClient createClient(URL url, Duration readTimeout, Duration connectionTimeout) {
        okhttp3.OkHttpClient.Builder client = new okhttp3.OkHttpClient.Builder()
            .connectionPool(pool)
            .followRedirects(true)
            .followSslRedirects(true)
            .proxy(proxy)
            .readTimeout(readTimeout.toMillis(), MILLISECONDS)
            .connectTimeout(connectionTimeout.toMillis(), MILLISECONDS);

        String info = url.getUserInfo();
        if (!Strings.isNullOrEmpty(info)) {
          String[] parts = info.split(":", 2);
          String user = parts[0];
          String pass = parts.length > 1 ? parts[1] : null;

          String credentials = Credentials.basic(user, pass);

          client.authenticator((route, response) -> {
            if (response.request().header("Authorization") != null) {
              return null; // Give up, we've already attempted to authenticate.
            }

            return response.request().newBuilder()
                .header("Authorization", credentials)
                .build();
          });
        }

        client.addNetworkInterceptor(chain -> {
          Request request = chain.request();
          Response response = chain.proceed(request);
          return response.code() == 408
                 ? response.newBuilder().code(500).message("Server-Side Timeout").build()
                 : response;
        });

        return new OkHttpClient(client.build(), url);
      }	
}
