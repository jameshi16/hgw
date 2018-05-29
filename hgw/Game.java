package hgw;

import hgw.action.Action; //imports the action class
import hgw.servant.*; //imports all possible servant classes
import hgw.errors.HGWError; //imports all errors

import java.util.Queue;
import java.util.List;
import java.util.LinkedList;
import java.util.function.Consumer;
import java.util.Random;

//Game singleton class.
final public class Game {
    /* Singleton game instance */
    private static Game GameInstance = null; //the game instance
    private Game() {}
    public static Game getInstance() { //acquires the game instance
        if (GameInstance == null) GameInstance = new Game(); //creates a new game if there is no current game
        return GameInstance;
    }

    /* Game constants */
    final private List<Servant> throneOfHeros = new LinkedList<Servant>(); //all the servants

    /* Game variables */
    private Queue<Action> actionQueue = new LinkedList<Action>(); //the action queue.
    private List<Servant> servants = new LinkedList<Servant>(); //the servants list.
    private Servant playerServant = null; //the current servant.

    /* Methods */
    public Boolean loadServant(final Servant servant) { //loads a servant into the game
        try {
            throneOfHeros.add(servant); //adds the servant into the thrones
        }
        catch (Exception e) {return false;} //don't handle exceptions
        return true;
    }

    public Boolean loadServant(final List<Servant> servants) { //loads a collection of servants into the game
        try {
            throneOfHeros.addAll(servants); //adds all the servants in the collection into the thrones
        }
        catch (Exception e) {return false;}
        return true;
    }

    /**
    Summons all the servants for the Holy Grail War, and returns the player's servant.
    */
    public Servant summonAll() throws HGWError {
        if (playerServant != null) throw new HGWError("ALR_SUMMONED");

        //Summons all 7 possible servants, removing them from the all_servants list.
        for (int i = 0; i < 7; i++) servants.add(throneOfHeros.remove((new Random()).nextInt(throneOfHeros.size())));
        return playerSummon(); //obtains the player's servant
    }

    /**
    Assigns a single servant in the servants list to be the player's servant.
    */
    public Servant playerSummon() throws HGWError {
        if (playerServant != null) throw new HGWError("ALR_SUMMONED");
        playerServant = servants.get((new Random()).nextInt(servants.size()));
        return playerServant; //returns the player's servant
    }
}