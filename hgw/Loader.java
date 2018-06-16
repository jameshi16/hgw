package hgw;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import hgw.servant.*;
import hgw.errors.HGWIOError;

import com.google.gson.Gson;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.NoSuchFileException;
import java.nio.file.Files;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.LinkedList;
import java.util.List;
import java.lang.Class;

public class Loader {
    private static final Logger logger = LogManager.getLogger("HGW-Loader");
    /**
    Load the servants from the filesystem.
    directory - The directory to search all the servants from.
    Returns null if no servants found in the filesystem
    */
    @SafeVarargs //checked, should be okay
    public static LinkedList<Servant> loadServants(String directory, Class<? extends Servant>... classes) throws IOException, HGWIOError {
        LinkedList<Servant> l_servants = new LinkedList<Servant>();
        Path rootDir = FileSystems.getDefault().getPath(directory);
        
        if (!Files.isDirectory(rootDir)) //checks if the Servants directory exist
            throw new HGWIOError("The ./Servants/ directory does not exist. Try running the program with the --configure flag.");

        Gson gson = new Gson(); //used for the entire loop below

        for (Class<? extends Servant> c : classes) //for every class found in the array of classes
            try (DirectoryStream<Path> stream = Files.newDirectoryStream(rootDir.resolve(c.getSimpleName()))) { //create dir stream
                for (Path path : stream) {
                    logger.info("Memorizing servant: ", path);
                    l_servants.push(gson.fromJson(new String(Files.readAllBytes(path)), c)); //adds the loaded servant into the list
                }
            } catch (NoSuchFileException exception) {}

        return l_servants;
    }

    /** Loads the servants from the directory, ./Servants/ 
    Return null if no servants are found in the directory. */
    public static LinkedList<Servant> loadServants() throws IOException {
        return loadServants("./Servants/", Archer.class, Assassin.class, Berserker.class, Caster.class, Lancer.class, Rider.class, Saber.class);
    }

    /** 
    Saves the servants to the filesystem
    l_servants - The list of servants to save to the filesystem
    directory - The directory to save the servants to
    */
    public static void saveServants(List<Servant> l_servants, String directory) throws IOException {
        Path dir_path = FileSystems.getDefault().getPath(directory);
        if (!Files.isDirectory(dir_path)) //checks if the Servants directory exist
            Files.createDirectory(dir_path);

        Gson gson = new Gson();
        for (Servant servant : l_servants) {//iterate through all servants
            Path dir_path_servant = dir_path.resolve(servant.getClass().getSimpleName());

            if (!Files.isDirectory(dir_path_servant)) //checks if the Servants directory exist
                Files.createDirectory(dir_path_servant);

            logger.info("Saving ", servant.getClass().getSimpleName() ,"-class servant to disk: ", servant.getName());
            Files.write(dir_path_servant.resolve(servant.getName() + ".json"), gson.toJson(servant).getBytes(), StandardOpenOption.CREATE); //writes
        }
    }

    /**
    Saves the servants to the directory, "./Servants/"
    l_servants - The list of servants to save to the directory
    */
    public static void saveServants(List<Servant> l_servants) throws IOException {
        saveServants(l_servants, "./Servants/");
    }
}