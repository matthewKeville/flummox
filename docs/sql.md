
Get the Lobby that a user belongs too

```sql
SELECT  LOB.* FROM LOBBY_USER_REFERENCE as LUR join LOBBY as LOB on LUR.LOBBY = LOB.ID where LUR.USERINFO = 1
```
