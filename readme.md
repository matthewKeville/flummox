# MVP

-  User Login 
    - User Account
    - Guest Access
- Lobbies Page
- Lobby Page 
- Game Page
    - Path agnostic game play
- Post Game Page

## User Login & Session Management

https://docs.spring.io/spring-security/reference/servlet/authentication/session-management.html

Spring Security persists authentication by default in it's sessions.
see https://docs.spring.io/spring-security/reference/servlet/authentication/persistence.html
The gist of the above is that when a user requests a resource that requires
auth, they are redirected to login, upon success a new session is made
and that SESSION id is sent to the user as a cookie.

https://spring.io/guides/topicals/spring-security-architecture/

`HttpSession` is created when a user authenticates , this is how we 
can save/access session variables (just Autowire it in)

## Modify session when authenticate succeeds

https://stackoverflow.com/questions/53340787/put-user-in-httpsession-with-spring-security-default-login-and-authenticate

## custom authentication processs

https://www.youtube.com/watch?v=Mw_1h9K0O-w

AuthenticationFilter -> 
    CustomAuthenticationManager ->
        CustomAuthenticationProvider ->
            evaluate status of CustomAuthentication
            and return a modified (true) CustomAuthentication

## Just attach an additional provider?

https://stackoverflow.com/questions/72458298/how-to-add-an-additional-authenticationprovider-without-using-websecurityconfigu

## User Account

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

## Guest Access

We can leverage `sessions` to identify a guest.
When it comes to implementing Games that have a mixture of guests and
authenticated users I will need create a API that wraps Guests and Users.

# Locking Down Routes

https://docs.spring.io/spring-security/reference/servlet/authorization/authorize-http-requests.html


# Design

@startuml

' avoid problems with angled crows feet
skinparam linetype ortho

entity "Lobby" {
  lobby_id : number <<generated>>
  --
  name : text
  capacity : number
  private : boolean
  --
  game_id : number <<FK>>
  --
  / player_count : int
}

entity "User" {
  user_id : number <<generated>>
  --
  e1_id : number <<FK>>
  other_details : text
}

entity "UserLobby" {
  user_lobby_id : number <<generated>>
  --
}

entity "Game" {
  game_id : number <<generated>>
  --
  time_limit : number
  board_string : string (base64)
}

entity "GameAnswer" {
  game_answer_id : number <<generated>>
  --
  game_id : number <<FK>>
  player_id : number <<FK>>
}

Lobby .. UserLobby 
UserLobby .. User
Lobby .. Game
GameAnswer .. Game

@enduml

# Data 

As a rough draft I could do the following aggregates

User

Lobby (aggregate root)
    Game (transient)
    Users (aggregate reference)

## Boggle Game Architecture

The vision plan for this project was to be able to play boggle online with 
friends in new game modes that are simply not possible outside of a computer.
Some ideas where **multiplier tiles** , **decaying tiles**, and **wildcards**.

The intent was to be able to play these modes on the desktop or through mobile
which have different preferred input mechanism **path drawing** and **text**.
**text** support must be supported because **I want** to be able to type my answers
this would be a massive improvement to vanilla boggle because I type more swiftly.

With mobile, typing would be clunky so naturally I must support some sort of 
touch interface.

---

### Game mode difficulties

Some game modes make using a certain interface challenging.

> Wildcard game modes are path agnostic

- Wildcard need to be expanded, so a path converts to a word
- Text interface is natural for this mode and requires no expansion.

* Wildcard mode is [Path Agnostic] 
    - words are scored based on the text

> Decaying game modes are path sensitive

- The touch interface is natural for path sensitive game modes
    as it produces a path, no resolution needed.
- Text interface  could yield a valid word, but maps to many paths, need some
    mechanism to select a path that resolves to that word.

### Critical Takeaways
- Game modes can be broken down into two classifications
    - Path Sensitive
    - Path Agnostic
- Touch interface can easily resolves to a word through token expansion
- Text interface can resolve to a word by providing a selection.
    - This resolution only feels good if the choices (revealed)
        are invariant in points.
        - Decaying path choice is fine because it adds strategy
        - Boards with multiplier tiles are poor because users might be
            led to a higher scoring path then the conceptualized.

### Implications

This likely means we need to model games depending on the game type.

For path aware games we submit paths and store paths
For path agnostic games we submit words and store words



# Spring / Web Authentication


I lost many hours trying to understanding how authentication works in spring
and the web in general. I think I have a grasp on it now.

## 3 Common Auth Schemes

### Basic Auth
- Authentication Header with username and password
- Authentication header sent every request for authenticated resources
- Used in stateless (sessionless) applications


### Session Auth
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

#### Getting an Authentication JSESSIONID from spring mvc

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




