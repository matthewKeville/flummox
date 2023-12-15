

# What should we test in a controller?

- [OK] Expected success response
    - straightforward for each endpoint

- Expected failure respone body?
    - each endpoint has many potential error responses, how
        is this something worth testing?
    - or do we test an example error response?
    - perhaps we can parameterize the error test to be variable
        on the precise service response exception

- Authentication?
    - I'm uncertain if this is a controller test or a "security test"


#  A Start

Instead of doing extensive reach  into the matter I will start with
a minimal testing procedure for controllers.

> Only test the expected positive resonse
> UI expected error responses (can't join lobby because full)
> Valid Query parameters
> Valid Request Body

