package com.seleniumtests.uipage.htmlelements;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang.NotImplementedException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.Point;
import org.openqa.selenium.Rectangle;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;

import com.seleniumtests.customexception.ScenarioException;

public class CachedHtmlElement implements WebElement {

	private Rectangle rectangle;
	private Point location;
	private Dimension size;
	private Element cachedElement;
	private WebElement realElement;
	private boolean selected;
	
	public CachedHtmlElement(Element jsoupElement) {
		location = new Point(0, 0);
		size = new Dimension(0, 0);
		rectangle = new Rectangle(location, size);
		selected = false;
		cachedElement = jsoupElement;
		realElement = null;
	}
	
	public CachedHtmlElement(WebElement elementToCache) {
		try {
			rectangle = elementToCache.getRect();
			location = new Point(rectangle.x, rectangle.y);
			size = new Dimension(rectangle.width, rectangle.height);
		} catch (WebDriverException e) {
			location = elementToCache.getLocation();
			size = elementToCache.getSize();
			rectangle = new Rectangle(location, size);
		}
		
			
		cachedElement = Jsoup.parseBodyFragment(elementToCache.getAttribute("outerHTML")).body().child(0);	
		if ("option".equals(cachedElement.tagName())
				|| ("input".equals(cachedElement.tagName()) && "checkbox".equals(cachedElement.attributes().getIgnoreCase("type")))
				|| ("input".equals(cachedElement.tagName()) && "radio".equals(cachedElement.attributes().getIgnoreCase("type")))
			) {
			selected = elementToCache.isSelected();
		} else {
			selected = false;
		}
		realElement = elementToCache;
		
	}
	
	@Override
	public <X> X getScreenshotAs(OutputType<X> target) throws WebDriverException {
		throw new ScenarioException("getScreenshotAs cannot be done on a CachedHtmlElement");
	}

	@Override
	public void click() {
		throw new ScenarioException("Click cannot be done on a CachedHtmlElement");
	}

	@Override
	public void submit() {
		throw new ScenarioException("Submit cannot be done on a CachedHtmlElement");
	}

	@Override
	public void sendKeys(CharSequence... keysToSend) {
		throw new ScenarioException("Sendkeys cannot be done on a CachedHtmlElement");
	}

	@Override
	public void clear() {
		throw new ScenarioException("Clear cannot be done on a CachedHtmlElement");
	}

	@Override
	public String getTagName() {
		return cachedElement.tagName();
	}

	@Override
	public String getAttribute(String name) {
		return cachedElement.attributes().getIgnoreCase(name);
	}

	@Override
	public boolean isSelected() {
		return selected;
	}

	@Override
	public boolean isEnabled() {
		return true;
	}

	@Override
	public String getText() {
		return cachedElement.text();
	}

	@SuppressWarnings("unchecked")
	@Override
	public WebElement findElement(By by) {
		try {
			return findElements(by).get(0);
		} catch (IndexOutOfBoundsException e) {
			throw new NoSuchElementException("Cannot find in cache element located by: " + by);
		}
	}

	@Override
	public List<WebElement> findElements(By by) {
		List<WebElement> foundElements = new ArrayList<>();
		if (by instanceof By.ById) {
			Field field;
			try {
				field = By.ById.class.getDeclaredField("id");
				field.setAccessible(true);
				foundElements.add(new CachedHtmlElement(cachedElement.getElementById((String)field.get(by))));
			} catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
			}	
		} else if (by instanceof By.ByTagName) {
			Field field;
			try {
				field = By.ByTagName.class.getDeclaredField("name");
				field.setAccessible(true);
				foundElements.addAll(cachedElement.getElementsByTag((String)field.get(by))
									.stream()
									.map(CachedHtmlElement::new)
									.collect(Collectors.toList()));
			} catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
			} 
		} else if (by instanceof By.ByClassName) {
			Field field;
			try {
				field = By.ByClassName.class.getDeclaredField("className");
				field.setAccessible(true);
				foundElements.addAll(cachedElement.getElementsByClass((String)field.get(by))
									.stream()
									.map(CachedHtmlElement::new)
									.collect(Collectors.toList()));
			} catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
			} 
		} else if (by instanceof By.ByName) {
			Field field;
			try {
				field = By.ByName.class.getDeclaredField("name");
				field.setAccessible(true);
				foundElements.addAll(cachedElement.getElementsByAttributeValue("name", (String)field.get(by))
									.stream()
									.map(CachedHtmlElement::new)
									.collect(Collectors.toList()));
			} catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
			} 
		} else if (by instanceof By.ByLinkText || by instanceof By.ByPartialLinkText) {
			Field field;
			try {
				field = By.ByLinkText.class.getDeclaredField("linkText");
				field.setAccessible(true);
				for (Element el: cachedElement.getElementsByTag("a")) {
					try {
						el.getElementsContainingOwnText((String)field.get(by)).get(0);
						foundElements.add(new CachedHtmlElement(el));
					} catch (IndexOutOfBoundsException e) {
						// nothing to do
					}
				}
			} catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
			} 
		} else {
			throw new NotImplementedException(String.format("%s is not implemented in cached element", by.getClass()));
		}

		return foundElements;
	}
	
	@Override
	public boolean isDisplayed() {
		return true;
	}

	@Override
	public Point getLocation() {
		return location;
	}

	@Override
	public Dimension getSize() {
		return size;
	}

	@Override
	public Rectangle getRect() {
		return rectangle;
	}

	@Override
	public String getCssValue(String propertyName) {
		return "";
	}

	public WebElement getRealElement() {
		return realElement;
	}


}