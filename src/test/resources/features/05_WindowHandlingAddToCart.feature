Feature: Window handling, pincode check and add to cart

  As a shopper
  I want to open a product in a new tab, check delivery for my pincode, and
  add it to my cart
  So that I can complete a realistic pre-purchase flow

  @regression
  Scenario: Navigate to Wall Lamp, check pincode and add to cart
    Given the user navigates to "Plus Zone", then "Home Furniture", then "Wall Lamp"
    When the user clicks on the "Wall Lamp" product
    And the user enters pincode "400001" and checks delivery
    Then the delivery status should be displayed
    And the user adds the product to cart
