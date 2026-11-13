package com.flipkart.runner;

import org.testng.annotations.DataProvider;

import io.cucumber.testng.AbstractTestNGCucumberTests;
import io.cucumber.testng.CucumberOptions;

/**
 * TestRunner
 * <p>
 * Bridges Cucumber to TestNG so {@code testng.xml} (and therefore
 * {@code mvn test}) can drive the .feature files under
 * src/test/resources/features.
 * <p>
 * No {@code tags} value is set here - by default every scenario runs.
 * Pass {@code -Dcucumber.filter.tags="@regression"} (or any other tag
 * expression) on the {@code mvn test} command line to run a subset; see the
 * README for details on how that override is wired through pom.xml.
 */
@CucumberOptions(
        features = "src/test/resources/features",
        glue = {"com.flipkart.stepdefinitions"},
        plugin = {
                "pretty",
                "html:target/cucumber-reports/cucumber-pretty.html",
                "json:target/cucumber-reports/CucumberTestReport.json",
                "com.aventstack.extentreports.cucumber.adapter.ExtentCucumberAdapter:"
        },
        monochrome = true
)
public class TestRunner extends AbstractTestNGCucumberTests {

    /**
     * parallel = false keeps scenarios running sequentially, which matches
     * the single shared static WebDriver in BaseLibrary. See the README for
     * what would need to change to run scenarios in parallel.
     */
    @Override
    @DataProvider(parallel = false)
    public Object[][] scenarios() {
        return super.scenarios();
    }
}
