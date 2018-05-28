package hgw.action;

import hgw.Game;

import java.util.function.Function;
import java.util.function.Consumer;

@FunctionalInterface //is a functional interface
public interface Action extends Consumer<Game> {
    @Override
    public void accept(Game game);
}