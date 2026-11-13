Feature: Excel-driven search testing with result write-back

  As a QA engineer
  I want to drive search tests from an external Excel sheet
  So that I can add or change test data without touching code

  @regression @excel
  Scenario: Search for every term listed in SearchData.xlsx
    Given the user has search terms in the Excel test data file
    When the user searches for each term in the Excel sheet and records the result
    Then all results should be written back to the Excel file
