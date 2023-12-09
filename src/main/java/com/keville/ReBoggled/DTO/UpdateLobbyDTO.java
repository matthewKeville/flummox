package com.keville.ReBoggled.DTO;

import java.util.List;


import com.keville.ReBoggled.model.GameSettings;

import jakarta.validation.constraints.NotNull;

public class UpdateLobbyDTO {
    @NotNull
    public String name;
    @NotNull
    public int capacity;
    @NotNull
    public Boolean isPrivate;
    @NotNull
    public GameSettings gameSettings;
}
