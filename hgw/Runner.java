package hgw;
import hgw.Game;
import hgw.Loader;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import hgw.errors.HGWIOError;

import hgw.servant.*; //import all of the servants

import java.lang.Class;
import java.lang.IllegalAccessException;
import java.lang.InstantiationException;
import java.lang.NoSuchMethodException;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.LinkedList;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.util.Scanner;

public class Runner {
    private static final Logger logger = LogManager.getLogger("HGW-Main");
    static public void main(String[] args) {
        logger.info("Log started.");
        Boolean valid_argument = false;


        try {
            for (int i = 0; i < args.length; i++) {
                logger.info("Processing argument: " + args[i]);
                if (args[i].toLowerCase().equals("--configure") || args[i].toLowerCase().equals("-c")) { //--configure flag
                    valid_argument = true; //argument is valid
                    System.out.print("[HGW] Generate default configuration folders? (y/n): ");
                    try {
                        if (Character.toLowerCase(System.in.read()) == 'y') {
                            System.in.skip(System.in.available()); //skips all available number of bytes
                            config_generate(
                                Archer.class,
                                Assassin.class,
                                Berserker.class,
                                Caster.class,
                                Lancer.class,
                                Rider.class,
                                Saber.class
                            ); //generate the configuration files
                        }
                    } catch (IOException e) {
                        logger.error("An error occured while trying to get an input from the console. Message: " + e.getMessage());
                        throw e;
                    } catch (HGWIOError e) {
                        logger.error("An error occured while attempting to load servants from disk. Message: " + e.getMessage());
                        throw e;
                    }
                }

                if (args[i].toLowerCase().equals("--clear-log") || args[i].toLowerCase().equals("-cl")) { //--clear-log flag
                    valid_argument = true; //argument is valid
                    Integer months_to_clear = -1; //clear all logs, including the current month's logs
                    Boolean failed_parsing = false;
                    if (i + 1 < args.length) { //if the next argument is not in Narnia
                        try {months_to_clear = Integer.parseInt(args[i + 1]); i++;} //sets the months to clear to the next argument, increase i by 1 to faciliate skipping
                        catch (NumberFormatException e) {
                            logger.info("--clear-log argument used without accompanying number.");
                            failed_parsing = true;
                        }
                    }
                    else if (i + 1 == args.length || (months_to_clear == -1 && failed_parsing)) { //determine if user input is required
                        System.out.print("[HGW] Clear logs from what month onwards? (-1 to clear all):  ");
                        months_to_clear = (new Scanner(System.in)).nextInt();
                    }

                    delete_old_logs(months_to_clear); //delete the old logs
                }
            }

            if (valid_argument) return; //returns immediately if a valid argument has already been detected
            
            delete_old_logs(); //delete the old logs. (Default: Logs that are 5 months old are deleted)

            //other flags
            try { initiate_game(args); } //initiate the procedure to start the game
            catch (Exception e) {
                logger.error("An error occured while running the game. Message: " + e.getMessage());
                throw e;
            }
        }
        catch (Exception e) {
            logger.error("Error: " + e.getMessage());
            logger.trace(e.toString());
            try {
                StringBuilder sb = new StringBuilder();
                sb.append("./Logs/");
                sb.append(LocalDateTime.now().getYear());
                sb.append("-");
                int monthValue = LocalDateTime.now().getMonthValue();
                if (monthValue < 10) { //Note: Has better solutions, but this one uses the least resources.
                    sb.append("0");
                    sb.append(LocalDateTime.now().getMonthValue());
                } else sb.append(LocalDateTime.now().getMonthValue());


                Path logFile = FileSystems.getDefault().getPath("./Logs/app.log"); //this is the main log file
                Path logArchives = FileSystems.getDefault().getPath(sb.toString()); //this is the logArchives
                if (Files.exists(logFile)) //checks if the log file exists
                    System.out.println("[HGW] Please check the application log: " + logFile.toRealPath()); //tells the user to check the log file
                if (Files.isDirectory(logArchives)) //checks if the log archives exists for today's date
                    System.out.println("[HGW] Log Archives are avaialble at: " + logArchives.toRealPath() + "/"); //tells the user to check the log archives

                System.out.println("[HGW] Report bugs through this link: https://www.github.com/jameshi16/hgw");
                System.out.println("[HGW] Sorry for the inconvinience!");
                logger.info("Informed user to check the logs. Hey user!");
            }
            catch (Exception err) {
                logger.fatal("General error. The cause could be the console, or the inexistance of a filesystem, or the lack of log files.");
            }
        }
    }

    static public void initiate_game(String[] args) throws Exception {
        Game game = Game.getInstance(); //gets the game instance
        game.loadServant(Loader.loadServants()); //loads the servant from disk, and then loads it into the game
    }

    @SafeVarargs
    static public void config_generate(Class<? extends Servant>... servant_classes) throws NoSuchMethodException,InstantiationException,IllegalAccessException,InvocationTargetException,IOException {
        LinkedList<Servant> servants = new LinkedList<Servant>(); //stores initialized servant classes

        try {
            for (Class<? extends Servant> s : servant_classes) {
                Servant inducedServant = s.getConstructor().newInstance();
                inducedServant.setName("Generic" + s.getSimpleName());
                servants.push(inducedServant);
            }

            Loader.saveServants(servants); //saves the servants.
        } catch (NoSuchMethodException e) {
            logger.error("There is no constructor in the Servant implementation. Error message: " + e.getMessage());
            throw e;
        } catch (InstantiationException e) {
            logger.error("Cannot instantiate the Servant. Error message: " + e.getMessage());
            throw e;
        } catch (IllegalAccessException e) {
            logger.error("Illegal access to the Servant constructor. Error message: " + e.getMessage());
            throw e;
        } catch (InvocationTargetException e) {
            logger.error("Servant constructor cannot be invoked. Error message: " + e.getMessage());
            throw e;
        } catch (IOException e) {
            logger.error("Cannot write default configurations. Error message: " + e.getMessage());
            throw e;
        }
    }

    static public void delete_old_logs(int months) throws IOException {
        logger.info("Logs considered old if they are " + (months + 1) + " months or older.");
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(FileSystems.getDefault().getPath("./Logs/"))) { //create a directory stream to logs
            for (Path path : stream) {
                logger.info("Processing: " + path.toString());
                if (Files.isDirectory(path)) { //checks if path is a directory
                    Matcher monthMatcher = Pattern.compile("\\d{4}-(\\d{2})$").matcher(path.getFileName().toString()); //gets the matcher to obtain month
                    if (!monthMatcher.find()) continue; //tries to obtain a month from the string using the monthMatcher expression
                    Integer monthNumber = Integer.parseInt(monthMatcher.group(1));
                    logger.info("Logs considered old: " + Boolean.valueOf(LocalDateTime.now().getMonthValue() - monthNumber > months).toString());
                    if (LocalDateTime.now().getMonthValue() - monthNumber > months) { //if the logs are older than 5 months
                        logger.info(path.toString() + " considered old. Deleting...");
                        Files.walkFileTree(path, new SimpleFileVisitor<Path>() { //deletes the logs
                           @Override
                           public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                               Files.delete(file); //deletes the file
                               logger.info("Deleted " + file.toString());
                               return FileVisitResult.CONTINUE; //continue "visiting" files
                           }

                           @Override
                           public FileVisitResult postVisitDirectory(Path dir, IOException e) throws IOException {
                               if (e == null) {
                                   Files.delete(dir); //delete the directory
                                   logger.info("Deleted directory: " + dir.toString());
                                   return FileVisitResult.CONTINUE; //continue "visiting" files
                               }
                               else logger.warn("Cannot delete directory: " + dir.toString());
                               return FileVisitResult.CONTINUE;
                           }
                        });
                    }
                }
            }
        }
    }

    static public void delete_old_logs() throws IOException {
        delete_old_logs(5); //calls delete_old_logs with the int argument
    }
}