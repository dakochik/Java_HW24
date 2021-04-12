package app;

import dao.DAOWrapper;
import model.Table;

import java.sql.SQLException;
import java.sql.SQLTimeoutException;
import java.util.Scanner;

public class Main {
    private static Scanner sc;
    private static DAOWrapper w;

    private enum Message{
        ENTER_COMMAND("Enter a request:"),
        ERROR("Incorrect input or problems with data base."),
        ERROR_TL("Command execution were aborted because it was running more than 30 seconds."),
        INFO("Enter 'E' to exit or any SQL command");

        private final String label;

        Message(String label) {
            this.label = label;
        }
    }

    private enum Command {
        E("e");

        private final String label;

        Command(String label) {
            this.label = label;
        }
    }

    public static void main(String[] args) {
        w = new DAOWrapper();

        sc = new Scanner(System.in);

        System.out.printf("%s\n\n", Message.INFO.label);
        while (handleCommand());
    }

    /**
     * Reads users command and calls executor method
     * @return should we keep running
     */
    private static boolean handleCommand() {
        System.out.printf("%s\n", Message.ENTER_COMMAND.label);

        var command = sc.nextLine();

        if(command.toLowerCase().equals(Command.E.label)){
            return  false;
        }

        try{
            var response = w.parseCommand(command);
            response.ifPresent(Main::printTable);
        }catch (SQLTimeoutException e){
            System.out.printf("%s\n", Message.ERROR_TL.label);
        }catch (SQLException e){
            System.out.printf("%s\n", Message.ERROR.label);
        }
        catch (RuntimeException e){
            System.out.printf("%s\n", e.getMessage());
        }

        System.out.print("\n");
        return true;
    }

    /**
     * Prints string representation of table
     * @param table table which we need to display
     */
    private static void printTable(Table table){
        var columns = table.getColumns();
        for (String column : columns) {
            System.out.printf("%s\t", column);
        }
        System.out.print("\n");

        while(table.next()){
            var row = table.getRow();
            for (String cell : row) {
                System.out.printf("%s\t", cell);
            }
        }
    }
}
