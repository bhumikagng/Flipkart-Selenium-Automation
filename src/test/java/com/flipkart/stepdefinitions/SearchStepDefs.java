package com.flipkart.stepdefinitions;

import java.util.List;

import org.apache.logging.log4j.Logger;
import org.testng.Assert;

import com.flipkart.pages.SearchPage;
import com.flipkart.utils.Log4jHelper;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

/**
 * SearchStepDefs
 * <p>
 * Steps for 01_SearchAndDataExtract.feature and 04_DataDrivenSearch.feature:
 * launching Flipkart, searching, and extracting product titles/prices from
 * the results.
 */
public class SearchStepDefs {

    private static final Logger log = Log4jHelper.getLogger(SearchStepDefs.class);

    private SearchPage searchPage;

    /**
     * Lazily creates the Page Object on first use within a step, rather than
     * as a field initializer. Cucumber constructs step definition instances
     * before the @Before hook runs, so a field initializer here would call
     * PageFactory.initElements(driver, ...) while driver is still null.
     */
    private SearchPage searchPage() {
        if (searchPage == null) {
            searchPage = new SearchPage();
        }
        return searchPage;
    }

    @Given("the user launches the Flipkart application")
    public void the_user_launches_the_flipkart_application() {
        searchPage().closeLoginPopupIfPresent();
    }

    @When("the user searches for {string}")
    public void the_user_searches_for(String searchTerm) {
        searchPage().searchFor(searchTerm);
    }

    @Then("the search results page should be displayed")
    public void the_search_results_page_should_be_displayed() {
        Assert.assertTrue(searchPage().isSearchResultsPageDisplayed(), "Search results were not displayed");
    }

    @Then("the page title should contain {string}")
    public void the_page_title_should_contain(String expectedText) {
        String title = searchPage().getTitle();
        Assert.assertTrue(
                title.toLowerCase().contains(expectedText.toLowerCase()),
                "Expected title to contain '" + expectedText + "' but was '" + title + "'");
    }

    @Then("the user extracts all product titles and prices")
    public void the_user_extracts_all_product_titles_and_prices() {
        List<String> titles = searchPage().getAllProductTitles();
        List<String> prices = searchPage().getAllProductPrices();

        log.info("Extracted {} product title(s)", titles.size());
        for (int i = 0; i < titles.size(); i++) {
            String price = i < prices.size() ? prices.get(i) : "N/A";
            System.out.println(titles.get(i) + " -> " + price);
            log.info("{} -> {}", titles.get(i), price);
        }
        Assert.assertFalse(titles.isEmpty(), "No product titles were extracted from the results page");
    }
}
