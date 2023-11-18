# MVP

-  User Login 
    - User Token
    - Guest Token
-  Lobbies Page
-  Lobby Page 
    - Game Component
    - Post Game Component

Resource Server (where the web services are)
Authorization Server (where we get tokens from)
OAuth2
JWT (Java Web Token)
Self-signed JWTs
  - when there is a distribuited service this might not be ideal

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

# Login Flow

@startuml

actor       user #pink
participant login.html
participant loginController
participant loginService

user-> login.html : user redirected to login
login.html -> loginController : login request sent
loginController -> loginService : verifies auth
note right : mark user as logged in the database
loginService-> loginController  : returns token
loginController -> login.html : returns token

@enduml

# Data 

As a rough draft I could do the following aggregates

User

Lobby (aggregate root)
    Game (transient)
    Users (aggregate reference)




