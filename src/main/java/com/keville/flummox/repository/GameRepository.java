package com.keville.flummox.repository;

import org.springframework.data.repository.CrudRepository;

import com.keville.flummox.model.game.Game;

public interface GameRepository extends CrudRepository<Game, Integer> {

}
