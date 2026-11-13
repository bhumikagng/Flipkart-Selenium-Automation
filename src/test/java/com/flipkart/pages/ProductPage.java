package com.flipkart.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import com.flipkart.reusable.SeleniumReusable;

/**
 * ProductPage
 * <p>
 * Elements and actions on an individual product detail page, which Flipkart
 * typically opens in a new browser tab/window when a product tile is
 * clicked from a listing page. Callers are expected to have already
 * switched the driver's focus to that new window (see
 * {@link SeleniumReusable#switchToNewWindow}) before using this page.
 */
public class ProductPage extends SeleniumReusable {

    public ProductPage() {
        PageFactory.initElements(driver, this);
    }

    @FindBy(xpath = "//span[contains(@class,'B_NuCI') or contains(@class,'VU-ZEz')]")
    private WebElement productTitle;

    @FindBy(xpath = "//input[@placeholder='Enter Delivery Pincode' or @placeholder='Enter pincode']")
    private WebElement pincodeInput;

    @FindBy(xpath = "//span[text()='Check'] | //button[contains(.,'Check')]")
    private WebElement pincodeCheckButton;

    @FindBy(xpath = "//button[contains(.,'ADD TO CART')] | //button[contains(.,'Add to cart')]")
    private WebElement addToCartButton;

    private final By deliveryStatusLocator =
            By.xpath("//div[contains(@class,'_16FRp0') or contains(@class,'_2Zdedc') or contains(@class,'yiggsN')]");

    public String getProductTitle() {
        return getText(productTitle);
    }

    /**
     * Enters pincode into the delivery-check field and clicks Check.
     */
    public void checkDeliveryForPincode(String pincode) {
        sendKeys(pincodeInput, pincode);
        click(pincodeCheckButton);
        log.info("Checked delivery for pincode: {}", pincode);
    }

    public boolean isDeliveryStatusDisplayed() {
        return isDisplayed(deliveryStatusLocator, 10);
    }

    public String getDeliveryStatusText() {
        return getText(deliveryStatusLocator);
    }

    public void addToCart() {
        click(addToCartButton);
        log.info("Clicked Add to Cart");
    }
}
