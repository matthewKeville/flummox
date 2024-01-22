DROP TABLE IF EXISTS authorities;
DROP TABLE IF EXISTS users;

DROP TABLE IF EXISTS game_answer;
DROP TABLE IF EXISTS tile;
DROP TABLE IF EXISTS lobby_user_reference;
DROP TABLE IF EXISTS userinfo;
DROP TABLE IF EXISTS lobby;
DROP TABLE IF EXISTS game;

---------------------------------------------------------------------------------
-- User Auth
---------------------------------------------------------------------------------

-- https://docs.spring.io/spring-security/reference/servlet/authentication/passwords/jdbc.html
CREATE TABLE IF NOT EXISTS users(
  USERNAME VARCHAR(50) not null primary key,
  PASSWORD VARCHAR(500) not null,
  ENABLED boolean not null
);

CREATE TABLE IF NOT EXISTS authorities(
    USERNAME VARCHAR(50) NOT NULL,
    AUTHORITY VARCHAR(50) NOT NULL,
    CONSTRAINT fk_authorities_users FOREIGN KEY(username) REFERENCES users(username) on DELETE CASCADE
);
--
CREATE UNIQUE INDEX IF NOT EXISTS ix_auth_username ON authorities (username,authority);

---------------------------------------------------------------------------------
-- User Data
---------------------------------------------------------------------------------

CREATE TABLE IF NOT EXISTS userinfo (
  ID INT AUTO_INCREMENT PRIMARY KEY,
  EMAIL VARCHAR(50),
  USERNAME VARCHAR(50),
  VERIFIED BOOLEAN not null,
  GUEST BOOLEAN not null,
  LOBBY INTEGER,
  OWNED_LOBBY INTEGER
);


CREATE TABLE IF NOT EXISTS game(
  ID INT AUTO_INCREMENT PRIMARY KEY,
  GAME_START TIMESTAMP,
  GAME_END TIMESTAMP,
  LAST_MODIFIED TIMESTAMP,
  FIND_RULE VARCHAR(40) NOT NULL,
  DURATION VARCHAR(40) NOT NULL,
  -- GameSettings
  BOARD_SIZE VARCHAR(40) NOT NULL,
  BOARD_TOPOLOGY VARCHAR(40) NOT NULL
); 

CREATE TABLE IF NOT EXISTS game_answer(
  ID INT AUTO_INCREMENT PRIMARY KEY,
  USERINFO INT,
  ANSWER VARCHAR(40),
  ANSWER_SUBMISSION_TIME TIMESTAMP,
  GAME INT,
  CONSTRAINT fk_game_answer_game_reference FOREIGN KEY(GAME) REFERENCES game(ID) on DELETE CASCADE,
  CONSTRAINT fk_game_answer_userinfo_reference FOREIGN KEY(USERINFO) REFERENCES userinfo(ID) on DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS tile (
  ID INT AUTO_INCREMENT PRIMARY KEY,
  CODE INT,
  ROTATION INT,
  GAME INT,
  GAME_KEY INT,
  CONSTRAINT fk_tile_game_reference FOREIGN KEY(GAME) REFERENCES game(ID) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS lobby(
  ID INT AUTO_INCREMENT PRIMARY KEY,
  NAME VARCHAR(255) NOT NULL,
  CAPACITY INT NOT null,
  IS_PRIVATE BOOLEAN NOT NULL,
  OWNER INT UNIQUE,
  GAME INT,
  LAST_MODIFIED TIMESTAMP,
  -- GameSettings
  BOARD_SIZE VARCHAR(40) NOT NULL,
  BOARD_TOPOLOGY VARCHAR(40) NOT NULL,
  TILE_ROTATION BOOLEAN NOT NULL,
  FIND_RULE VARCHAR(40) NOT NULL,
  DURATION VARCHAR(40) NOT NULL,

  CONSTRAINT fk_lobby_game_reference FOREIGN KEY(GAME) REFERENCES game(ID) ON DELETE SET NULL

); 

CREATE TABLE IF NOT EXISTS lobby_user_reference(
  ID INT AUTO_INCREMENT PRIMARY KEY,
  USERINFO INT, -- I can't use USER as column name
  LOBBY INT,
  UNIQUE (USERINFO),
  CONSTRAINT fk_lobby_user_lobby_reference FOREIGN KEY(LOBBY) REFERENCES lobby(ID) on DELETE CASCADE,
  CONSTRAINT fk_lobby_user_userinfo_reference FOREIGN KEY(USERINFO) REFERENCES userinfo(ID) on DELETE CASCADE
);
