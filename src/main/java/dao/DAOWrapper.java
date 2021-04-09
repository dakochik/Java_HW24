package dao;

import java.io.PrintStream;
import java.sql.*;

public class DAOWrapper {
    /**
     * PAth to DB
     */
    private static final String DB_PATH = "jdbc:sqlite:javaHw.db";
    /**
     * Connection to DB
     */
    private static Connection connection = null;
    /**
     * Stream where we will print info to user
     */
    private final PrintStream stream;

    /**
     * Creates an instance of object
     * @param stream here we will print an info to user
     */
    public DAOWrapper(PrintStream stream) {
        this.stream = stream;
    }

    /**
     * Creates connection to DB using singleton pattern
     * @throws SQLException if there are problems with db connection
     * @throws SQLTimeoutException when the driver has determined that the timeout value that was
     * specified by the setQueryTimeout method has been exceeded and has at least attempted to cancel
     * the currently running Statement
     */
    public void setConnection() throws SQLException {
        if (connection == null) {
            connection = DriverManager.getConnection(DB_PATH);
        }
    }

    /**
     * Checks if there are SELECT key word In command and call appropriate command executor
     * @param command command we need to execute
     * @throws SQLException if the input command is wrong or there are problems with db connection
     * @throws SQLTimeoutException when the driver has determined that the timeout value that was
     * specified by the setQueryTimeout method has been exceeded and has at least attempted to cancel
     * the currently running Statement
     */
    public void parseCommand(String command) throws SQLException {
        Statement statement = connection.createStatement();
        statement.setQueryTimeout(30);  // set timeout to 30 sec.
        if (command.toLowerCase().contains("select")) {
            execQuery(command, statement);
        } else {
            execUpdate(command, statement);
        }
    }

    /**
     * Execute user command with SELECT key word
     * @param command command we need to execute
     * @param statement statement we use to create a command
     * @throws SQLException if the input command is wrong or there are problems with db connection
     * @throws SQLTimeoutException when the driver has determined that the timeout value that was
     * specified by the setQueryTimeout method has been exceeded and has at least attempted to cancel
     * the currently running Statement
     */
    private void execQuery(String command, Statement statement) throws SQLException {
        ResultSet rs = statement.executeQuery(command);
        ResultSetMetaData data = rs.getMetaData();

        printRepresentation(data, rs);
    }

    /**
     * Execute users command
     * @param command command we need to execute
     * @param statement statement we use to create a command
     * @throws SQLException if the input command is wrong or there are problems with db connection
     * @throws SQLTimeoutException when the driver has determined that the timeout value that was
     * specified by the setQueryTimeout method has been exceeded and has at least attempted to cancel
     * the currently running Statement
     */
    private void execUpdate(String command, Statement statement) throws SQLException{
        statement.executeUpdate(command);
    }

    /**
     * Prints result of select command
     * @param data metadata of selected data
     * @param set selected data
     * @throws SQLException if there are problems with access to db
     */
    private void printRepresentation(ResultSetMetaData data, ResultSet set) throws SQLException {
        for (int i = 0; i < data.getColumnCount(); ++i) {
            stream.printf("%s\t", data.getColumnName(i + 1));
        }

        do {
            stream.print("\n");
            for (int i = 0; i < data.getColumnCount(); ++i) {
                stream.printf("%s\t", set.getObject(i + 1));
            }
        } while (set.next());
        stream.print("\n");
    }
}
