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
CREATE TABLE users(
  USERNAME varchar_ignorecase(50) not null primary key,
  PASSWORD varchar_ignorecase(500) not null,
  ENABLED boolean not null
);

CREATE TABLE authorities(
    USERNAME VARCHAR_IGNORECASE(50) NOT NULL,
    AUTHORITY VARCHAR_IGNORECASE(50) NOT NULL,
    CONSTRAINT fk_authorities_users FOREIGN KEY(username) REFERENCES users(username)
);
--
CREATE UNIQUE INDEX ix_auth_username ON authorities (username,authority);

---------------------------------------------------------------------------------
-- User Data
---------------------------------------------------------------------------------

CREATE TABLE USERINFO (
  ID INT AUTO_INCREMENT PRIMARY KEY,
  EMAIL varchar_ignorecase(50),
  USERNAME varchar_ignorecase(50),
  GUEST BOOLEAN not null,
  VERIFIED BOOLEAN not null
);

CREATE TABLE LOBBY(
  ID INT AUTO_INCREMENT PRIMARY KEY,
  NAME VARCHAR(255) NOT NULL,
  CAPACITY INT NOT null,
  IS_PRIVATE BOOLEAN NOT NULL,
  OWNER INT
); 

CREATE TABLE LOBBY_USER_REFERENCE(
  ID INT AUTO_INCREMENT PRIMARY KEY,
  USERINFO INT, -- I can't use USER as column name
  LOBBY INT,
  CONSTRAINT fk_user_reference_lobby FOREIGN KEY(LOBBY) REFERENCES LOBBY(ID)
);


CREATE TABLE GAME_SETTINGS(
  ID INT AUTO_INCREMENT PRIMARY KEY,
  BOARD_SIZE VARCHAR(40) NOT NULL,
  BOARD_TOPOLOGY VARCHAR(40) NOT NULL,
  FIND_RULE VARCHAR(40) NOT NULL,
  DURATION VARCHAR(40) NOT NULL,
  LOBBY INTEGER NOT NULL -- JDBC rule
); 
