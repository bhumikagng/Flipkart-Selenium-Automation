package com.flipkart.stepdefinitions;

import java.util.List;

import org.apache.logging.log4j.Logger;
import org.testng.Assert;

import com.flipkart.pages.FilterPage;
import com.flipkart.pages.SearchPage;
import com.flipkart.utils.Log4jHelper;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

/**
 * FilterStepDefs
 * <p>
 * Steps for 02_FilterProducts.feature: searching, then narrowing the results
 * down by price range, brand, RAM and battery capacity.
 */
public class FilterStepDefs {

    private static final Logger log = Log4jHelper.getLogger(FilterStepDefs.class);

    private SearchPage searchPage;
    private FilterPage filterPage;

    private SearchPage searchPage() {
        if (searchPage == null) {
            searchPage = new SearchPage();
        }
        return searchPage;
    }

    private FilterPage filterPage() {
        if (filterPage == null) {
            filterPage = new FilterPage();
        }
        return filterPage;
    }

    @Given("the user searches for {string} on Flipkart")
    public void the_user_searches_for_on_flipkart(String term) {
        searchPage().closeLoginPopupIfPresent();
        searchPage().searchFor(term);
    }

    @When("the user filters by price range {string}")
    public void the_user_filters_by_price_range(String range) {
        filterPage().applyPriceRange(range);
    }

    @When("the user filters by brand {string}")
    public void the_user_filters_by_brand(String brand) {
        filterPage().applyBrand(brand);
    }

    @When("the user filters by RAM {string}")
    public void the_user_filters_by_ram(String ram) {
        filterPage().applyRam(ram);
    }

    @When("the user filters by battery capacity {string}")
    public void the_user_filters_by_battery_capacity(String battery) {
        filterPage().applyBatteryCapacity(battery);
    }

    @Then("the filtered product list should be updated")
    public void the_filtered_product_list_should_be_updated() {
        List<String> titles = filterPage().getFilteredProductTitles();
        log.info("Filtered list contains {} product(s)", titles.size());
        Assert.assertFalse(titles.isEmpty(), "Filtered product list was empty");
    }
}
