Feature: Search and extract product data on Flipkart

  As a shopper
  I want to search for a product on Flipkart
  So that I can see matching results along with their prices

  Background:
    Given the user launches the Flipkart application

  @smoke
  Scenario: Search for a product and verify the results page
    When the user searches for "laptop"
    Then the search results page should be displayed
    And the page title should contain "laptop"

  @smoke
  Scenario: Extract product titles and prices from the search results
    When the user searches for "laptop"
    Then the user extracts all product titles and prices
