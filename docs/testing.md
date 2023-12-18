# Controller Tests

## Test

- Positive Response             (..ReturnsLobby)
  - correct headers
  - correct body (type)

- Authorization Rejection       (..FailsWhenNotOwner)
  - Error when user does own resource
  - correct status code

- Input Validation
  - Returns error response when fails to validate

- UI Expected error messages    (...ReturnsReasonForFailure)

## Test

Do not test
- service methods
- service logical errors ex. FailsWhenGuest
