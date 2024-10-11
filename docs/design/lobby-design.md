Lobby API

-------------------------------------------------------------------------------

[ Lobby ]
Id
Visibility
Capacity
Owner
Name
Type = { Manual / Automatic }
CurrentGameId

-------------------------------------------------------------------------------

Lobbies are means of collecting players for a game. Lobbies created by users
are Manual which means that the owner has to start the game manually.
Automatic lobbies are not managed by players and operate on there own without
player intervention.

Manual lobbies are transient entities that do not persist some time
after all users leave. Such lobbies are said to be "orphaned".

-------------------------------------------------------------------------------

# Roles

Anon            : Any non logged in session that has not passed consent screen
Guest           : Any non logged in session that passes consent screen
User            : Any logged in session
User (owner)    : A user who owns a resource { /user/{id} /lobby/{id} }

-------------------------------------------------------------------------------

## Discovery


`GET /api/lobby`

    Whitelist : [ Guest , User ]

    These groups should be allowed to look at the lobbies.

--------------------------------------------------------------------------------

## Creation

`POST /api/lobby`

    Whitelist : [ User ]

    This group should be able to create there own lobby.

`POST /api/lobby/{id}`

    Whitelist : [ Owner ]

    This group should be able to modify a lobby they own.

--------------------------------------------------------------------------------

## Management

`POST /api/lobby/{id}/join`

    Whitelist : [ Guest, User ]

    This group should be able to attempt the join a lobby.
    They may be unsuccessful if

        1. The lobby is at capacity
        2. The lobby is private

`POST /api/lobby/{id}/leave`

    Whitelist : [ Guest, User ]

    This group should be able to leave any lobby

`POST /api/lobby/{id}/kick`

    Whitelist : [ Owner ]

    This group should be able remove another user from there own lobby.

`POST /api/lobby/{id}/promote`

    Whitelist : [ Owner ]

    This group should be able to transfer ownership of the lobby.

-------------------------------------------------------------------------------
