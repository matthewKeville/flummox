This document outlines what and what not is considered worth testing.
Very much a WIP.

## Controller Tests

### TEST

- Positive Response             (..ReturnsLobby)
  - correct headers
  - correct body (type)

- Authorization Rejection       (..FailsWhenNotOwner)
  - Error when user does own resource
  - correct status code

- Input Validation
  - Returns error response when fails to validate

- UI Expected error messages    (...ReturnsReasonForFailure)

### DO NOT TEST

- service methods
- service logical errors ex. FailsWhenGuest
