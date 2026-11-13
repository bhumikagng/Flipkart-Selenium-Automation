package com.flipkart.reusable;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.flipkart.base.BaseLibrary;

/**
 * SeleniumReusable
 * <p>
 * Generic, reusable Selenium wrapper methods shared by every Page Object,
 * Hook and (indirectly, through Page Objects) Step Definition class.
 * Extends {@link BaseLibrary} for access to the shared driver, config and
 * logger.
 * <p>
 * Most actions come in two flavours: a {@link By}-based overload (waits,
 * then re-locates fresh from the DOM - best for dynamic lists and elements
 * whose presence isn't guaranteed) and a {@link WebElement}-based overload
 * (for elements already bound through {@code @FindBy}/PageFactory).
 */
public class SeleniumReusable extends BaseLibrary {

    private static final int DEFAULT_TIMEOUT_SECONDS = 10;

    protected WebDriverWait getWait(int seconds) {
        return new WebDriverWait(driver, Duration.ofSeconds(seconds));
    }

    // ---------------------------------------------------------------
    // Clicks
    // ---------------------------------------------------------------

    public void click(By locator) {
        getWait(DEFAULT_TIMEOUT_SECONDS).until(ExpectedConditions.elementToBeClickable(locator)).click();
        log.info("Clicked element: {}", locator);
    }

    public void click(WebElement element) {
        getWait(DEFAULT_TIMEOUT_SECONDS).until(ExpectedConditions.elementToBeClickable(element)).click();
        log.info("Clicked element: {}", element);
    }

    // ---------------------------------------------------------------
    // Typing / reading
    // ---------------------------------------------------------------

    public void sendKeys(By locator, String text) {
        WebElement el = getWait(DEFAULT_TIMEOUT_SECONDS).until(ExpectedConditions.visibilityOfElementLocated(locator));
        el.clear();
        el.sendKeys(text);
        log.info("Typed '{}' into element: {}", text, locator);
    }

    public void sendKeys(WebElement element, String text) {
        getWait(DEFAULT_TIMEOUT_SECONDS).until(ExpectedConditions.visibilityOf(element));
        element.clear();
        element.sendKeys(text);
        log.info("Typed '{}' into element", text);
    }

    public String getText(By locator) {
        String text = getWait(DEFAULT_TIMEOUT_SECONDS).until(ExpectedConditions.visibilityOfElementLocated(locator)).getText();
        log.info("Read text '{}' from element: {}", text, locator);
        return text;
    }

    public String getText(WebElement element) {
        getWait(DEFAULT_TIMEOUT_SECONDS).until(ExpectedConditions.visibilityOf(element));
        String text = element.getText();
        log.info("Read text '{}' from element", text);
        return text;
    }

    public String getValue(By locator) {
        String value = getWait(DEFAULT_TIMEOUT_SECONDS)
                .until(ExpectedConditions.visibilityOfElementLocated(locator))
                .getAttribute("value");
        log.info("Read value '{}' from element: {}", value, locator);
        return value;
    }

    public String getTitle() {
        String title = driver.getTitle();
        log.info("Page title: {}", title);
        return title;
    }

    /**
     * True if at least one element matching locator is present and visible
     * within the given timeout; false (never throws) otherwise. Use this for
     * things that may or may not appear, such as the Flipkart login popup.
     */
    public boolean isDisplayed(By locator, int seconds) {
        try {
            List<WebElement> elements =
                    getWait(seconds).until(ExpectedConditions.presenceOfAllElementsLocatedBy(locator));
            return !elements.isEmpty() && elements.get(0).isDisplayed();
        } catch (TimeoutException e) {
            return false;
        }
    }

    // ---------------------------------------------------------------
    // Multiple elements
    // ---------------------------------------------------------------

    public List<String> multipleGetText(By locator) {
        List<WebElement> elements = driver.findElements(locator);
        List<String> texts = new ArrayList<>();
        for (WebElement el : elements) {
            texts.add(el.getText());
        }
        log.info("Extracted {} text value(s) from locator: {}", texts.size(), locator);
        return texts;
    }

    // ---------------------------------------------------------------
    // Mouse actions
    // ---------------------------------------------------------------

    public void mouseHover(By locator) {
        WebElement el = getWait(DEFAULT_TIMEOUT_SECONDS).until(ExpectedConditions.visibilityOfElementLocated(locator));
        new Actions(driver).moveToElement(el).perform();
        log.info("Hovered over element: {}", locator);
    }

    // ---------------------------------------------------------------
    // JavaScript executor
    // ---------------------------------------------------------------

    public void scrollIntoView(By locator) {
        WebElement el = getWait(DEFAULT_TIMEOUT_SECONDS).until(ExpectedConditions.presenceOfElementLocated(locator));
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block: 'center'});", el);
        log.info("Scrolled element into view: {}", locator);
    }

    public void jsClick(By locator) {
        WebElement el = getWait(DEFAULT_TIMEOUT_SECONDS).until(ExpectedConditions.presenceOfElementLocated(locator));
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", el);
        log.info("JS-clicked element: {}", locator);
    }

    public void scrollByPixels(int x, int y) {
        ((JavascriptExecutor) driver).executeScript("window.scrollBy(arguments[0], arguments[1]);", x, y);
        log.info("Scrolled window by ({}, {})", x, y);
    }

    // ---------------------------------------------------------------
    // Dropdowns (native <select> elements only - see FilterPage for why
    // Flipkart's own filter rail does NOT use this)
    // ---------------------------------------------------------------

    public void selectByVisibleText(By locator, String visibleText) {
        Select select = new Select(getWait(DEFAULT_TIMEOUT_SECONDS).until(ExpectedConditions.visibilityOfElementLocated(locator)));
        select.selectByVisibleText(visibleText);
        log.info("Selected '{}' from dropdown: {}", visibleText, locator);
    }

    public void selectByValue(By locator, String value) {
        Select select = new Select(getWait(DEFAULT_TIMEOUT_SECONDS).until(ExpectedConditions.visibilityOfElementLocated(locator)));
        select.selectByValue(value);
        log.info("Selected value '{}' from dropdown: {}", value, locator);
    }

    // ---------------------------------------------------------------
    // Window handling
    // ---------------------------------------------------------------

    /**
     * Switches to the first open window/tab that is NOT currentHandle.
     * Returns the handle now in focus (the new one if found, otherwise
     * currentHandle unchanged).
     */
    public String switchToNewWindow(String currentHandle) {
        Set<String> allHandles = driver.getWindowHandles();
        for (String handle : allHandles) {
            if (!handle.equals(currentHandle)) {
                driver.switchTo().window(handle);
                log.info("Switched to new window handle: {}", handle);
                return handle;
            }
        }
        log.warn("No additional window handle found besides {}", currentHandle);
        return currentHandle;
    }

    public void closeCurrentAndSwitchTo(String targetHandle) {
        driver.close();
        driver.switchTo().window(targetHandle);
        log.info("Closed current window and switched back to: {}", targetHandle);
    }

    // ---------------------------------------------------------------
    // Screenshots
    // ---------------------------------------------------------------

    public byte[] takeScreenshotAsBytes() {
        return ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
    }

    /**
     * Saves a screenshot under test-output/screenshots/{name}.png and returns
     * the path it was written to.
     */
    public String takeScreenshotAsFile(String screenshotName) {
        File src = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
        Path destPath = Paths.get(System.getProperty("user.dir"), "test-output", "screenshots", screenshotName + ".png");
        try {
            Files.createDirectories(destPath.getParent());
            Files.copy(src.toPath(), destPath, StandardCopyOption.REPLACE_EXISTING);
            log.info("Saved screenshot to {}", destPath);
        } catch (IOException e) {
            log.error("Failed to save screenshot to {}", destPath, e);
        }
        return destPath.toString();
    }
}
