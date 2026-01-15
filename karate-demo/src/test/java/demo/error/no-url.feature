@ignore @springboot3
Feature:  No URL found proper error response
  # Spring Boot 3 returns application/problem+json instead of application/json

  Background:
    * url demoBaseUrl
    * configure lowerCaseResponseHeaders = true

  Scenario: Invalid URL response
    Given path 'hello'
    When method get
    Then status 404
    And match header content-type contains 'application/json'
    And match response.status == 404
    And match response.path == '/hello'
    And match response.error == 'Not Found'
