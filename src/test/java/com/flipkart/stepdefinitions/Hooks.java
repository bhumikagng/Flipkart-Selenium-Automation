package com.flipkart.stepdefinitions;

import com.flipkart.base.BaseLibrary;
import com.flipkart.reusable.SeleniumReusable;

import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;

/**
 * Hooks
 * <p>
 * Cucumber lifecycle hooks. {@code @Before} starts the browser for every
 * scenario; {@code @After} captures a screenshot on failure (attached to the
 * scenario so the ExtentReports Cucumber adapter embeds it in the report
 * automatically) and always tears the browser down afterwards, pass or fail.
 * <p>
 * Extends SeleniumReusable (rather than just calling BaseLibrary statically)
 * purely for the screenshot helper and logger - Hooks doesn't otherwise
 * behave like a page object.
 */
public class Hooks extends SeleniumReusable {

    @Before
    public void setUp(Scenario scenario) {
        BaseLibrary.initializeDriver();
        log.info("========== Starting scenario: {} ==========", scenario.getName());
    }

    @After
    public void tearDown(Scenario scenario) {
        try {
            if (scenario.isFailed()) {
                byte[] screenshot = takeScreenshotAsBytes();
                scenario.attach(screenshot, "image/png", scenario.getName());
                log.error("Scenario FAILED: {}", scenario.getName());
            } else {
                log.info("Scenario PASSED: {}", scenario.getName());
            }
        } catch (Exception e) {
            log.error("Failed to capture/attach screenshot for scenario '{}'", scenario.getName(), e);
        } finally {
            log.info("========== Finished scenario: {} ==========", scenario.getName());
            BaseLibrary.quitDriver();
        }
    }
}
