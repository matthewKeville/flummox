# User Login & Session Management

https://docs.spring.io/spring-security/reference/servlet/authentication/session-management.html

Spring Security persists authentication by default in it's sessions.
see https://docs.spring.io/spring-security/reference/servlet/authentication/persistence.html
The gist of the above is that when a user requests a resource that requires
auth, they are redirected to login, upon success a new session is made
and that SESSION id is sent to the user as a cookie.

https://spring.io/guides/topicals/spring-security-architecture/

`HttpSession` is created when a user authenticates , this is how we 
can save/access session variables (just Autowire it in)

# Modify session when authenticate succeeds

https://stackoverflow.com/questions/53340787/put-user-in-httpsession-with-spring-security-default-login-and-authenticate

# custom authentication processs

https://www.youtube.com/watch?v=Mw_1h9K0O-w

AuthenticationFilter -> 
    CustomAuthenticationManager ->
        CustomAuthenticationProvider ->
            evaluate status of CustomAuthentication
            and return a modified (true) CustomAuthentication

# Just attach an additional provider?

https://stackoverflow.com/questions/72458298/how-to-add-an-additional-authenticationprovider-without-using-websecurityconfigu

# User Account

For now I use `InMemoryUserDetailsManager`

```java 
  @Bean
  public InMemoryUserDetailsManager users() {
    return new InMemoryUserDetailsManager(
        User.withUsername("matt")
          .password("{noop}test") //use no op password encoder
          .authorities("read")
          .build()
    );
  }
```

Users can be identified by basic auth, once they are authorized
this can persist in there session.

# Guest Access

We can leverage `sessions` to identify a guest.
When it comes to implementing Games that have a mixture of guests and
authenticated users I will need create a API that wraps Guests and Users.

# Locking Down Routes

https://docs.spring.io/spring-security/reference/servlet/authorization/authorize-http-requests.html

# Spring / Web Authentication


I lost many hours trying to understanding how authentication works in spring
and the web in general. I think I have a grasp on it now.

# 3 Common Auth Schemes

## Basic Auth
- Authentication Header with username and password
- Authentication header sent every request for authenticated resources
- Used in stateless (sessionless) applications


## Session Auth
- Does not use basic auth
- Typically used in client-server authentication
- vuln to csfr (cross site forgery attack)
- Uses sessions to keep track of authentication status
    - authentication goal is to create a session that maps to an authenticated 
        user
    - Once an authenticated session is created, client just sends authenticated
        session id
    - When user first hits website with no cookie, server starts a session
        and gives user a session cookie.
    - user should use that cookie for the rest of the session unless stated
        otherwise
    - when authenticated resource is hit user is redirected to login form
    - form page sends a request to POST /login with form data
        that contains **username** and **password**
    - If that form is valid, server returns a new cookie containing the ID
        of a secure session
---

### Getting an Authentication JSESSIONID from spring mvc

Send form data to login endpoint, httpie does some convenience here
by adding the right headers for html forms.

```bash
http -v --form POST localhost:8080/login username='matt' password='test' 

# httpie lists the actual request it makes
# notice application/x-www-form-urlencoded 

POST /login HTTP/1.1
Accept: */*
Accept-Encoding: gzip, deflate
Connection: keep-alive
Content-Length: 27
Content-Type: application/x-www-form-urlencoded; charset=utf-8
Host: localhost:8080
User-Agent: HTTPie/2.6.0

username=matt&password=test

HTTP/1.1 302
Cache-Control: no-cache, no-store, max-age=0, must-revalidate
Connection: keep-alive
Content-Length: 0
Date: Sun, 19 Nov 2023 00:36:25 GMT
Expires: 0
Keep-Alive: timeout=60
Location: http://localhost:8080/lobby
Pragma: no-cache
Set-Cookie: JSESSIONID=F304E8B30D7D172E8727710B039E4CF0; Path=/; HttpOnly
X-Content-Type-Options: nosniff
X-Frame-Options: DENY
X-XSS-Protection: 0
```
---

notice how the server returns a set cookie header

```bash
Set-Cookie: JSESSIONID=F304E8B30D7D172E8727710B039E4CF0; Path=/; HttpOnly
```
this cookie contains the key to our authenticated session
now we can hit any endpoint requiring auth by including this cookie in our 
request.

```bash
http -v GET localhost:8080/secret.html Cookie:JSESSIONID=F304E8B30D7D172E8727710B039E4CF0
GET /secret.html HTTP/1.1
Accept: */*
Accept-Encoding: gzip, deflate
Connection: keep-alive
Cookie: JSESSIONID=F304E8B30D7D172E8727710B039E4CF0
Host: localhost:8080
User-Agent: HTTPie/2.6.0



HTTP/1.1 200
Accept-Ranges: bytes
Cache-Control: no-store
Connection: keep-alive
Content-Length: 284
Content-Type: text/html
Date: Sun, 19 Nov 2023 00:25:17 GMT
Keep-Alive: timeout=60
Last-Modified: Sat, 18 Nov 2023 05:46:04 GMT
Vary: Origin, Access-Control-Request-Method, Access-Control-Request-Headers
X-Content-Type-Options: nosniff
X-Frame-Options: DENY
X-XSS-Protection: 0

<!DOCTYPE html>
<html>
  <head>
    <title>ReBoggled Secret Lounge</title>
    <meta charset="utf-8">
    <link rel="shortcut icon" href="/favicon.ico">
    <link rel="stylesheet" href="/style.css">
  </head>
  <body>
    welcome to the special secret static page
  </body>
</html>

```

### Token Auth
- Similar to Basic Auth in that token is sent every request
- Auth tokens typically outsourced to another server
- Typically used for server-server authentication

# Spring Security and JDBC

https://docs.spring.io/spring-security/reference/servlet/authentication/passwords/jdbc.html
https://docs.spring.io/spring-framework/reference/data-access/jdbc/embedded-database-support.html

The following are all Spring Security classes related to authenticating users
in the spring security context. 

UserDetails
UserDetailsService
UserDetailsManager 

InMemoryUserDetailsManager
JdbcUserDetailsManger

The tutorials I have seen thus far use `InMemoryUserDetailsManager`
as an example manager for user details, but I want user details
to be in the database so I can use user information (i.e. userId) 
to associate users with other data (i.e. lobbies).

We can use a different `UserDetailsManager` that changes how this information
is stored, and `JdbcUserDetailsManger` will store this information
in a database through `JDBC` interface. 

In order to construct one, we need a `DataSource` which a handle
to interface with a db, implementation agnostic.

We could create a `@bean` to and set up a custom `DataSource` but
**Spring Boot** automatically registers an **H2** `DataSource` bean
if **H2** is a in the project. Thus we can just autowire the default 
`DataSource`


