package com.keville.ReBoggled.DTO;

import java.util.Optional;

import com.keville.ReBoggled.model.game.GameSettings;

/*
 * Fields with values are requested to be changed, otherwise
 * null fields imply unchanged 
 */
public class LobbyUpdateDTO {
    public String name;
    public Integer capacity;
    public Boolean isPrivate;
    public GameSettings gameSettings;
}

/*
public class LobbyUpdateDTO {
    public Optional<String> name;
    public Optional<Integer> capacity;
    public Optional<Boolean> isPrivate;
    public Optional<GameSettings> gameSettings;
}
*/
