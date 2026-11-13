Feature: Filter search results on Flipkart

  As a shopper
  I want to narrow down search results using the filter rail
  So that I only see products matching my criteria

  Background:
    Given the user searches for "mobile" on Flipkart

  @regression
  Scenario: Filter mobiles by price range
    When the user filters by price range "10,000"
    Then the filtered product list should be updated

  @regression
  Scenario: Filter mobiles by brand, RAM and battery capacity
    When the user filters by brand "Samsung"
    And the user filters by RAM "4 GB"
    And the user filters by battery capacity "5000 mAh"
    Then the filtered product list should be updated
