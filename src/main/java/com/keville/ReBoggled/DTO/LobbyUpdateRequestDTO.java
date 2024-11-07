package com.keville.ReBoggled.DTO;

import java.util.Optional;

import com.keville.ReBoggled.model.game.GameSettings;

public class LobbyUpdateRequestDTO {
    public Optional<String> name = Optional.empty();
    public Optional<Integer> capacity = Optional.empty();
    public Optional<Boolean> isPrivate = Optional.empty();
    public Optional<GameSettings> gameSettings = Optional.empty();
}
