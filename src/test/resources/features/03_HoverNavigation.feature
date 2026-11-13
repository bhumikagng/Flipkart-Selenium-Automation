Feature: Category navigation via mouse hover

  As a shopper
  I want to browse categories using the top navigation menu
  So that I can reach a specific sub-category without searching by keyword

  @tc001 @regression
  Scenario: Navigate to Girls Dress via Fashion -> Kids
    Given the user is on the Flipkart home page
    When the user hovers over "Fashion", then "Kids", and clicks "Girls Dress"
    Then the "Girls Dress" listing page should be displayed
