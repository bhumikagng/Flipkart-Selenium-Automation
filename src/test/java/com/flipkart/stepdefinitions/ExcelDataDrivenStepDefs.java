package com.flipkart.stepdefinitions;

import org.apache.logging.log4j.Logger;
import org.testng.Assert;

import com.flipkart.base.BaseLibrary;
import com.flipkart.pages.SearchPage;
import com.flipkart.utils.ExcelUtility;
import com.flipkart.utils.Log4jHelper;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

/**
 * ExcelDataDrivenStepDefs
 * <p>
 * Steps for 06_ExcelDrivenTesting.feature: read every search term out of
 * SearchData.xlsx, run a search for each, capture a screenshot, and write
 * "Pass"/"Fail" back into the sheet's Status column.
 */
public class ExcelDataDrivenStepDefs {

    private static final Logger log = Log4jHelper.getLogger(ExcelDataDrivenStepDefs.class);
    private static final String SHEET_NAME = "Sheet1";
    private static final int SEARCH_TERM_COLUMN = 0;
    private static final int STATUS_COLUMN = 1;

    private SearchPage searchPage;

    private SearchPage searchPage() {
        if (searchPage == null) {
            searchPage = new SearchPage();
        }
        return searchPage;
    }

    private String excelPath() {
        return BaseLibrary.loadConfig().getProperty("excelPath");
    }

    @Given("the user has search terms in the Excel test data file")
    public void the_user_has_search_terms_in_the_excel_test_data_file() {
        String path = excelPath();
        Assert.assertNotNull(path, "excelPath is not set in config.properties");
        log.info("Using Excel test data file: {}", path);
    }

    @When("the user searches for each term in the Excel sheet and records the result")
    public void the_user_searches_for_each_term_and_records_result() {
        ExcelUtility excel = new ExcelUtility(excelPath());
        int lastRow = excel.getRowCount(SHEET_NAME);

        searchPage().closeLoginPopupIfPresent();

        for (int row = 1; row <= lastRow; row++) {
            String term = excel.getCellData(SHEET_NAME, row, SEARCH_TERM_COLUMN);
            if (term.isEmpty()) {
                continue;
            }

            String status;
            try {
                searchPage().searchFor(term);
                boolean resultsShown = searchPage().isSearchResultsPageDisplayed();
                status = resultsShown ? "Pass" : "Fail";
                searchPage().takeScreenshotAsFile("excel_search_" + term.replaceAll("\\s+", "_"));
                log.info("Row {}: searched '{}' -> {}", row, term, status);
            } catch (Exception e) {
                status = "Fail";
                log.error("Row {}: search for '{}' threw an exception", row, term, e);
            }

            excel.setCellData(SHEET_NAME, row, STATUS_COLUMN, status);

            // Reset to the home page before the next term.
            BaseLibrary.getDriver().navigate().to(BaseLibrary.loadConfig().getProperty("url"));
        }
    }

    @Then("all results should be written back to the Excel file")
    public void all_results_should_be_written_back_to_the_excel_file() {
        ExcelUtility excel = new ExcelUtility(excelPath());
        int lastRow = excel.getRowCount(SHEET_NAME);

        for (int row = 1; row <= lastRow; row++) {
            String term = excel.getCellData(SHEET_NAME, row, SEARCH_TERM_COLUMN);
            if (term.isEmpty()) {
                continue;
            }
            String status = excel.getCellData(SHEET_NAME, row, STATUS_COLUMN);
            Assert.assertTrue("Pass".equals(status) || "Fail".equals(status),
                    "Row " + row + " does not have a Pass/Fail status recorded");
        }
        log.info("Verified Pass/Fail status recorded for all {} row(s)", lastRow);
    }
}
