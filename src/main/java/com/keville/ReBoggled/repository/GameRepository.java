package com.keville.ReBoggled.repository;

import org.springframework.data.repository.CrudRepository;

import com.keville.ReBoggled.model.game.Game;

public interface GameRepository extends CrudRepository<Game, Integer> {

}
