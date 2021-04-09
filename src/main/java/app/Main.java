package app;

import dao.DAOWrapper;

import java.sql.SQLException;
import java.sql.SQLTimeoutException;
import java.util.Scanner;

public class Main {
    private static final String ENTER_COMMAND = "Enter a request:";
    private static final String ERROR = "Incorrect input or problems with data base.";
    private static final String ERROR_TL = "Command execution were aborted because it was running more than 30 seconds.";
    private static final String INFO = "Enter 'E' to exit or any SQL command";

    private static Scanner sc;
    private static DAOWrapper w;

    private enum Command {
        E("e");

        private final String label;

        Command(String label) {
            this.label = label;
        }
    }

    public static void main(String[] args) {
        w = new DAOWrapper(System.out);

        try {
            w.setConnection();
        }
        catch (SQLException e){
            System.out.print("Impossible to connect to data base. Try again later.");
            return;
        }

        sc = new Scanner(System.in);

        System.out.printf("%s\n\n", INFO);
        while (handleCommand());
    }

    /**
     * Reads users command and calls executor method
     * @return should we keep running
     */
    private static boolean handleCommand() {
        System.out.printf("%s\n", ENTER_COMMAND);

        var command = sc.nextLine();

        if(command.toLowerCase().equals(Command.E.label)){
            return  false;
        }

        try{
            w.parseCommand(command);
        }catch (SQLTimeoutException e){
            System.out.printf("%s\n", ERROR_TL);
        }catch (SQLException e){
            System.out.printf("%s\n", ERROR);
        }
        System.out.print("\n");
        return true;
    }
}
