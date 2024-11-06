package com.keville.ReBoggled.DTO;

import com.keville.ReBoggled.model.game.GameSettings;

/*
 * Fields with values are requested to be changed, otherwise
 * null fields imply unchanged 
 */
public class LobbyUpdateRequestDTO {
    public String name;
    public Integer capacity;
    public Boolean isPrivate;
    public GameSettings gameSettings;
}
