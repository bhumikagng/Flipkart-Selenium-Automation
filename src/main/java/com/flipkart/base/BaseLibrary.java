package com.flipkart.base;

import java.io.FileInputStream;
import java.io.IOException;
import java.time.Duration;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;

import io.github.bonigarcia.wdm.WebDriverManager;

/**
 * BaseLibrary
 * <p>
 * Owns configuration loading and the WebDriver lifecycle shared by every
 * reusable method, page object, step definition and hook in the framework.
 * The driver and config are static so that a single browser session is
 * shared across all of those collaborators within one scenario (this
 * framework runs single-threaded; see the README for notes on adapting it
 * for parallel execution).
 */
public class BaseLibrary {

    protected static final Logger log = LogManager.getLogger(BaseLibrary.class);
    protected static WebDriver driver;
    protected static Properties config;

    private static final String CONFIG_PATH =
            System.getProperty("user.dir") + "/src/test/resources/config.properties";

    /**
     * Loads config.properties once per JVM. Safe to call repeatedly; only the
     * first call actually reads from disk.
     */
    public static Properties loadConfig() {
        if (config == null) {
            config = new Properties();
            try (FileInputStream fis = new FileInputStream(CONFIG_PATH)) {
                config.load(fis);
                log.info("Loaded configuration from {}", CONFIG_PATH);
            } catch (IOException e) {
                log.error("Unable to load config.properties from {}", CONFIG_PATH, e);
                throw new RuntimeException("Could not load config.properties at " + CONFIG_PATH, e);
            }
        }
        return config;
    }

    /**
     * Initializes the WebDriver for the browser named in config.properties
     * (defaults to chrome), applies the implicit wait, maximizes the window
     * (unless running headless) and navigates to the configured base URL.
     */
    public static WebDriver initializeDriver() {
        loadConfig();
        String browser = config.getProperty("browser", "chrome").trim().toLowerCase();
        boolean headless = Boolean.parseBoolean(config.getProperty("headless", "false"));
        log.info("Initializing WebDriver -> browser={}, headless={}", browser, headless);

        switch (browser) {
            case "firefox":
                WebDriverManager.firefoxdriver().setup();
                FirefoxOptions firefoxOptions = new FirefoxOptions();
                if (headless) {
                    firefoxOptions.addArguments("-headless");
                }
                driver = new FirefoxDriver(firefoxOptions);
                break;

            case "chrome":
            default:
                WebDriverManager.chromedriver().setup();
                ChromeOptions chromeOptions = new ChromeOptions();
                chromeOptions.addArguments("--remote-allow-origins=*");
                chromeOptions.addArguments("--disable-notifications");
                chromeOptions.addArguments("--disable-popup-blocking");
                if (headless) {
                    chromeOptions.addArguments("--headless=new");
                    chromeOptions.addArguments("--window-size=1920,1080");
                }
                driver = new ChromeDriver(chromeOptions);
                break;
        }

        int implicitWaitSeconds = Integer.parseInt(config.getProperty("implicitWait", "10"));
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(implicitWaitSeconds));
        if (!headless) {
            driver.manage().window().maximize();
        }

        String url = config.getProperty("url", "https://www.flipkart.com");
        driver.get(url);
        log.info("Launched {} and navigated to {}", browser, url);
        return driver;
    }

    public static WebDriver getDriver() {
        return driver;
    }

    public static void quitDriver() {
        if (driver != null) {
            log.info("Closing WebDriver session");
            driver.quit();
            driver = null;
        }
    }
}
