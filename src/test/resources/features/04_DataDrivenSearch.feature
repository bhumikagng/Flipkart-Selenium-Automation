Feature: Data-driven search using a Scenario Outline

  As a QA engineer
  I want to run the same search scenario across multiple product categories
  So that I can validate search works consistently for all of them

  @regression
  Scenario Outline: Search for different product categories
    Given the user launches the Flipkart application
    When the user searches for "<searchTerm>"
    Then the search results page should be displayed

    Examples:
      | searchTerm |
      | mobile     |
      | TV         |
      | speaker    |
      | t-shirt    |
