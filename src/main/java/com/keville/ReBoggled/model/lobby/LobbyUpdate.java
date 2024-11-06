package com.keville.ReBoggled.model.lobby;

import java.util.Optional;

import com.keville.ReBoggled.DTO.LobbyUpdateRequestDTO;
import com.keville.ReBoggled.model.game.GameSettings;

public class LobbyUpdate {

    public Integer id;
    public Optional<String> name;
    public Optional<Integer> capacity;
    public Optional<Boolean> isPrivate;
    public Optional<GameSettings> gameSettings;

    public LobbyUpdate(Integer id,LobbyUpdateRequestDTO dto) {

      this.id = id;

      this.name = (dto.name == null) ? 
        Optional.empty() : Optional.of(dto.name);

      this.capacity = (dto.capacity == null) ? 
        Optional.empty() : Optional.of(dto.capacity);

      this.isPrivate = (dto.isPrivate == null) ? 
        Optional.empty() : Optional.of(dto.isPrivate);

      this.gameSettings = (dto.gameSettings == null) ? 
        Optional.empty() : Optional.of(dto.gameSettings);

    }

}
