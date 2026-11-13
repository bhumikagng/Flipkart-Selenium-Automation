# Flipkart E2E Test Automation Framework

A robust, scalable, and highly maintainable Hybrid BDD automation framework designed to validate complex e-commerce workflows on flipkart.com.  This project demonstrates enterprise-level QA automation standards, utilizing Selenium WebDriver 4, Cucumber 7 (BDD), and TestNG. The architecture is built upon the Page Object Model (POM) and features a dedicated data-driven testing layer using Apache POI, comprehensive logging via Log4j2, and rich visual reporting with ExtentReports.

**Note on Dynamic DOM & Maintenance:** Flipkart's front-end architecture generates dynamic CSS class names (e.g., _4rR01T, _30jeq3) at build time. To ensure maximum stability, I have engineered the locators in this framework to rely on structural XPath, name attributes, and visible text wherever possible. However, as this framework targets a live third-party production environment, occasional maintenance to the @FindBy/By locators in the pages/*.java classes is an expected part of the lifecycle if the DOM shifts.  

---

## Framework Architecture

The framework is strictly modularized to separate core configurations, page objects, step definitions, and test data.

```
src/main/java/com/flipkart/
├── base/
│   └── BaseLibrary.java           # Core config loading & WebDriver lifecycle (Chrome/Firefox, headless, implicit waits)
├── reusable/
│   └── SeleniumReusable.java      # Custom wrapper methods: click, sendKeys, Actions (hover), JS-executor scrolling, window-handle switching
└── utils/
    ├── ExcelUtility.java          # Apache POI wrapper for bidirectional Excel I/O
    └── Log4jHelper.java           # Centralized Log4j2 logger configuration

src/test/java/com/flipkart/
├── pages/                         # Page Object Model (POM) Layer
│   ├── SearchPage.java            # Handles search bar, login popups, hover navigation
│   ├── FilterPage.java            # Handles dynamic product filtering (price, brand, RAM)
│   └── ProductPage.java           # Handles child-window interactions and Add to Cart logic
├── stepdefinitions/               # BDD Implementation Layer
│   ├── Hooks.java                 # @Before (Driver init) and @After (Teardown & ExtentReport screenshot embedding)
│   ├── SearchStepDefs.java        
│   ├── FilterStepDefs.java        
│   └── ExcelDataDrivenStepDefs.java
└── runner/
    └── TestRunner.java            # AbstractTestNGCucumberTests bridge for ExtentReports & Feature/Glue mapping

src/test/resources/
├── features/                      # Gherkin Scenarios
├── testdata/
│   └── SearchData.xlsx            # Source of truth for Data-Driven test cases
├── config.properties              # Environment variables (browser, headless toggle, URLs)
├── log4j2.xml                     # Console and rolling-file appender configurations
└── extent.properties              # ExtentReports Spark reporter configuration
```

### Core Design Decisions

- **Lazy Initialization of Page Objects:** Cucumber constructs step definition instances prior to the `@Before` hook execution. To prevent PageFactory from initializing with a null driver, page objects utilize lazy getters rather than field initializers.
- **Dynamic Custom XPath over Native Select:** Flipkart's filtering mechanism utilizes heavily customized checkbox lists rather than native `<select>` tags. `FilterPage.java` bypasses standard `Select` classes in favor of dynamic XPath construction based on visible labels, ensuring reliable filter interaction. (Standard `Select` wrapper methods remain available in `SeleniumReusable` for native dropdowns).
- **Thread Safety:** The current iteration utilizes a single shared static field for the driver (`BaseLibrary.driver`) for stable, single-threaded execution.

---

## Test Coverage & Feature Mapping

| File | Covers |
| --- | --- |
| `01_SearchAndDataExtract.feature` | Search, verify results page, extract all titles + prices |
| `02_FilterProducts.feature` | Price range / brand / RAM / battery filters |
| `03_HoverNavigation.feature` | Mouse-hover category navigation (`@tc001` `@regression`) |
| `04_DataDrivenSearch.feature` | Scenario Outline + Examples across 4 product categories |
| `05_WindowHandlingAddToCart.feature` | New-tab handling, pincode delivery check, Add to Cart |
| `06_ExcelDrivenTesting.feature` | Reads `SearchData.xlsx`, searches each term, writes Pass/Fail back |

---

## Prerequisites

* **Java 17+** (`java -version`)
* **Maven 3.8+** (`mvn -version`)
* **Google Chrome** installed (Firefox also supported – see `config.properties`)

> **Note:** The framework utilizes `WebDriverManager` to automatically resolve and manage driver binaries, eliminating the need for manual driver path configurations.

---

## Execution Guide

Execute the test suite via the Maven CLI. The framework supports dynamic tag execution to easily isolate smoke or regression suites:

**Run the entire test suite:**
```bash
mvn clean test

# Run specific suites using Cucumber tags
mvn clean test -Dcucumber.filter.tags="@regression"
mvn clean test -Dcucumber.filter.tags="@smoke"
mvn clean test -Dcucumber.filter.tags="@tc001"
```

Headless Execution: To run tests headlessly (ideal for CI pipelines), update headless=true in src/test/resources/config.properties.

## Reporting & Logging

| Report | Path |
|---|---|
| ExtentReports (Spark HTML) | `test-output/ExtentReport.html` |
| Cucumber pretty HTML | `target/cucumber-reports/cucumber-pretty.html` |
| Cucumber JSON | `target/cucumber-reports/CucumberTestReport.json` |
| Screenshots (Excel-driven run) | `test-output/screenshots/` |
| Log file | `logs/automation.log` |

## Bidirectional Excel Data Operations

The data-driven suite (06_ExcelDrivenTesting.feature) ships with pre-populated test data (e.g., mobile, TV, fridge, speaker). Upon completion, the Apache POI integration automatically writes the respective Pass/Fail execution results directly back into the Status column of src/test/resources/testdata/SearchData.xlsx.

## Future Enhancements & Scalability

To further mature this framework, my planned roadmap includes:
- **CI/CD Pipeline Integration:** Adding a Jenkinsfile and GitHub Actions workflows to trigger mvn clean test on pull requests and publish the ExtentReports as build artifacts.
- **Parallel Execution:** Migrating the static WebDriver instance to a ThreadLocal<WebDriver> and utilizing parallel="methods" in testng.xml to drastically reduce suite execution time.
- **Containerization:** Integrating Docker and Selenium Grid to ensure complete environment parity across local and CI test runs[cite: 1].
