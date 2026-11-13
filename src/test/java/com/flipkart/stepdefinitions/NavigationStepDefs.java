package com.flipkart.stepdefinitions;

import org.testng.Assert;

import com.flipkart.pages.SearchPage;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

/**
 * NavigationStepDefs
 * <p>
 * Steps for 03_HoverNavigation.feature: hovering through the top nav bar
 * (e.g. Fashion -> Kids -> Girls Dress) using the Actions class.
 */
public class NavigationStepDefs {

    private SearchPage searchPage;

    private SearchPage searchPage() {
        if (searchPage == null) {
            searchPage = new SearchPage();
        }
        return searchPage;
    }

    @Given("the user is on the Flipkart home page")
    public void the_user_is_on_the_flipkart_home_page() {
        searchPage().closeLoginPopupIfPresent();
    }

    @When("the user hovers over {string}, then {string}, and clicks {string}")
    public void the_user_hovers_and_clicks(String topLevel, String subLevel, String finalLink) {
        searchPage().hoverAndClickCategory(topLevel, subLevel, finalLink);
    }

    @Then("the {string} listing page should be displayed")
    public void the_listing_page_should_be_displayed(String categoryName) {
        String title = searchPage().getTitle();
        boolean matches = title.toLowerCase().contains(categoryName.toLowerCase())
                || searchPage().isSearchResultsPageDisplayed();
        Assert.assertTrue(matches,
                "Expected the " + categoryName + " listing page to be displayed, but title was: " + title);
    }
}
