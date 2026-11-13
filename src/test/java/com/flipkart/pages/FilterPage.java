package com.flipkart.pages;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.support.PageFactory;

import com.flipkart.reusable.SeleniumReusable;

/**
 * FilterPage
 * <p>
 * Locators and actions for the left-hand filter rail on a Flipkart search
 * results page: price range, brand, RAM and battery capacity.
 * <p>
 * DESIGN NOTE: the spec this framework was built from calls for filtering
 * "using the Select class" - but Flipkart renders these filters as checkbox
 * lists, not native {@code <select>} elements, so there is nothing for the
 * Select class to attach to here. Using dynamic XPath built from each
 * filter's visible label is what actually works against the live site.
 * {@link SeleniumReusable#selectByVisibleText} /
 * {@link SeleniumReusable#selectByValue} are still implemented and available
 * for any native dropdown you do encounter in the flow (e.g. a quantity
 * selector on the product page).
 */
public class FilterPage extends SeleniumReusable {

    public FilterPage() {
        PageFactory.initElements(driver, this);
    }

    private static final String SECTION_XPATH = "//section[.//div[normalize-space(text())='%s']]";
    private static final String OPTION_CONTAINS_XPATH = SECTION_XPATH + "//div[contains(text(),'%s')]";

    private final By filteredProductTitleLocator =
            By.xpath("//div[contains(@class,'_4rR01T') or contains(@class,'IRpwTa') or contains(@class,'_2WkVRV') or contains(@class,'wjcEIp')]");

    private By filterSection(String sectionTitle) {
        return By.xpath(String.format(SECTION_XPATH, sectionTitle));
    }

    private By filterOptionContaining(String sectionTitle, String partialLabel) {
        return By.xpath(String.format(OPTION_CONTAINS_XPATH, sectionTitle, partialLabel));
    }

    /**
     * Scrolls the named filter section into view, then clicks the
     * checkbox/label whose text contains partialLabel. A partial match is
     * used deliberately - price-range labels like "₹10,000 - ₹20,000" can
     * vary slightly in punctuation/spacing between page loads.
     */
    private void applyFilter(String sectionTitle, String partialLabel) {
        By section = filterSection(sectionTitle);
        scrollIntoView(section);
        By option = filterOptionContaining(sectionTitle, partialLabel);
        click(option);
        log.info("Applied filter [{}] -> contains '{}'", sectionTitle, partialLabel);
    }

    public void applyPriceRange(String rangeLabel) {
        applyFilter("Price", rangeLabel);
    }

    public void applyBrand(String brandName) {
        applyFilter("Brand", brandName);
    }

    public void applyRam(String ramLabel) {
        applyFilter("RAM", ramLabel);
    }

    public void applyBatteryCapacity(String batteryLabel) {
        applyFilter("Battery Capacity", batteryLabel);
    }

    public List<String> getFilteredProductTitles() {
        return multipleGetText(filteredProductTitleLocator);
    }
}
