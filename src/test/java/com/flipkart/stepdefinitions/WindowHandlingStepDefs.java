package com.flipkart.stepdefinitions;

import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.testng.Assert;

import com.flipkart.base.BaseLibrary;
import com.flipkart.pages.ProductPage;
import com.flipkart.pages.SearchPage;
import com.flipkart.utils.Log4jHelper;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

/**
 * WindowHandlingStepDefs
 * <p>
 * Steps for 05_WindowHandlingAddToCart.feature: hover-navigate to a listing,
 * click a product (which Flipkart opens in a new tab), switch to that
 * window, check delivery for a pincode, and add the product to cart.
 */
public class WindowHandlingStepDefs {

    private static final Logger log = Log4jHelper.getLogger(WindowHandlingStepDefs.class);

    private SearchPage searchPage;
    private ProductPage productPage;
    private String parentWindowHandle;

    private SearchPage searchPage() {
        if (searchPage == null) {
            searchPage = new SearchPage();
        }
        return searchPage;
    }

    private ProductPage productPage() {
        if (productPage == null) {
            productPage = new ProductPage();
        }
        return productPage;
    }

    @Given("the user navigates to {string}, then {string}, then {string}")
    public void the_user_navigates_to_category_path(String top, String sub, String finalCategory) {
        searchPage().closeLoginPopupIfPresent();
        searchPage().hoverAndClickCategory(top, sub, finalCategory);
    }

    @When("the user clicks on the {string} product")
    public void the_user_clicks_on_the_product(String productName) {
        parentWindowHandle = BaseLibrary.getDriver().getWindowHandle();

        By productLink = By.xpath(
                "//a[contains(@title,'" + productName + "')] | //div[contains(text(),'" + productName + "')]");
        searchPage().click(productLink);

        searchPage().switchToNewWindow(parentWindowHandle);
        log.info("Clicked product '{}' and switched to its window", productName);
    }

    @When("the user enters pincode {string} and checks delivery")
    public void the_user_enters_pincode_and_checks_delivery(String pincode) {
        productPage().checkDeliveryForPincode(pincode);
    }

    @Then("the delivery status should be displayed")
    public void the_delivery_status_should_be_displayed() {
        Assert.assertTrue(productPage().isDeliveryStatusDisplayed(),
                "Delivery status was not shown for the given pincode");
    }

    @Then("the user adds the product to cart")
    public void the_user_adds_the_product_to_cart() {
        productPage().addToCart();
    }
}
