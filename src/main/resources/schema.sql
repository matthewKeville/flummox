---------------------------------------------------------------------------------
-- NOTE : 
-- 
-- Note H2 will by default make table names uppercase ...
-- I.E. casing doesn't mean anything in this file, it will be 
-- changed to uppercase, but queries from Spring using lowercase
-- tables names & columns will fail.
---------------------------------------------------------------------------------
-- User Auth
---------------------------------------------------------------------------------

-- https://docs.spring.io/spring-security/reference/servlet/authentication/passwords/jdbc.html
CREATE TABLE IF NOT EXISTS users(
  USERNAME varchar_ignorecase(50) not null primary key,
  PASSWORD varchar_ignorecase(500) not null,
  ENABLED boolean not null
);

CREATE TABLE IF NOT EXISTS authorities(
    USERNAME VARCHAR_IGNORECASE(50) NOT NULL,
    AUTHORITY VARCHAR_IGNORECASE(50) NOT NULL,
    CONSTRAINT fk_authorities_users FOREIGN KEY(username) REFERENCES users(username)
);
--
CREATE UNIQUE INDEX IF NOT EXISTS ix_auth_username ON authorities (username,authority);

---------------------------------------------------------------------------------
-- User Data
---------------------------------------------------------------------------------

CREATE TABLE IF NOT EXISTS USERINFO (
  ID INT AUTO_INCREMENT PRIMARY KEY,
  EMAIL varchar_ignorecase(50),
  USERNAME varchar_ignorecase(50),
  VERIFIED BOOLEAN not null,
  GUEST BOOLEAN not null,
  LOBBY INTEGER,
  OWNED_LOBBY INTEGER
);

CREATE TABLE IF NOT EXISTS LOBBY(
  ID INT AUTO_INCREMENT PRIMARY KEY,
  NAME VARCHAR(255) NOT NULL,
  CAPACITY INT NOT null,
  IS_PRIVATE BOOLEAN NOT NULL,
  OWNER INT,
  GAME INT,
  LAST_MODIFIED TIMESTAMP,
  -- GameSettings
  BOARD_SIZE VARCHAR(40) NOT NULL,
  BOARD_TOPOLOGY VARCHAR(40) NOT NULL,
  FIND_RULE VARCHAR(40) NOT NULL,
  DURATION VARCHAR(40) NOT NULL,

  UNIQUE (OWNER) -- Enforce this policy at DB level
); 

CREATE TABLE IF NOT EXISTS LOBBY_USER_REFERENCE(
  ID INT AUTO_INCREMENT PRIMARY KEY,
  USERINFO INT, -- I can't use USER as column name
  LOBBY INT,
  UNIQUE (USERINFO),
  CONSTRAINT fk_user_reference_lobby FOREIGN KEY(LOBBY) REFERENCES LOBBY(ID)
);

CREATE TABLE IF NOT EXISTS GAME(
  ID INT AUTO_INCREMENT PRIMARY KEY,
  GAME_START TIMESTAMP,
  GAME_END TIMESTAMP,
  LAST_MODIFIED TIMESTAMP,
  -- GameSettings
  BOARD_SIZE VARCHAR(40) NOT NULL,
  BOARD_TOPOLOGY VARCHAR(40) NOT NULL,
  FIND_RULE VARCHAR(40) NOT NULL,
  DURATION VARCHAR(40) NOT NULL
); 

CREATE TABLE IF NOT EXISTS GAME_ANSWER(
  ID INT AUTO_INCREMENT PRIMARY KEY,
  USERINFO INT,
  ANSWER VARCHAR(40),
  GAME INT,
  CONSTRAINT fk_game_reference FOREIGN KEY(GAME) REFERENCES GAME(ID)
);

CREATE TABLE IF NOT EXISTS TILE (
  ID INT AUTO_INCREMENT PRIMARY KEY,
  CODE INT,
  GAME INT,
  GAME_KEY INT,
  CONSTRAINT fk_tile_game_reference FOREIGN KEY(GAME) REFERENCES GAME(ID)
);
