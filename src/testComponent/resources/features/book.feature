Feature: the user can create, retrieve and reserve the books
  Scenario: user creates two books, reserves one of them and retrieve both of them
    When the user creates the book "Les Misérables" written by "Victor Hugo"
    And the user reserves the first created book
    And the user creates the book "L'avare" written by "Molière"
    And the user get all books
    Then the list should contains the following books in the same order
      | name            | author      | reserved |
      | L'avare         | Molière     | false    |
      | Les Misérables  | Victor Hugo | true     |
