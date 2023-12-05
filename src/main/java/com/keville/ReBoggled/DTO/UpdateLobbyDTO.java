package com.keville.ReBoggled.DTO;

import java.util.List;

import com.keville.ReBoggled.model.GameSettings;

public class UpdateLobbyDTO {
    public Integer id;
    public String name;
    public int capacity;
    public Boolean isPrivate;
    public GameSettings gameSettings;
}
