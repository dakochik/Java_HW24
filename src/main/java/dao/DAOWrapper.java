package dao;

import model.Table;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class DAOWrapper {
    /**
     * PAth to DB
     */
    private static final String DB_PATH = "jdbc:sqlite:javaHw.db";
    /**
     * Connection to DB
     */
    private Connection connection;


    /**
     * Creates connection to DB using singleton pattern
     * @throws RuntimeException if it is impossible to access data base or time for connection has expired
     */
    private void setConnection() {
        try{
            connection = DriverManager.getConnection(DB_PATH);
        }catch (SQLTimeoutException e){
            connection = null;
            throw new RuntimeException("Time for connection has expired");
        }catch (SQLException e){
            connection = null;
            throw new RuntimeException("Impossible to access data base");
        }
    }

    /**
     * Close data connection to data base
     * @throws RuntimeException if it is impossible to access data base to close it
     */
    private void dumpConnection(){
        try{
            if(connection != null){
                connection.close();
            }
        }catch (SQLException e){
            throw new RuntimeException("Impossible to access data base to close it");
        }
    }

    /**
     * Checks if there are SELECT key word In command and call appropriate command executor
     * @param command command we need to execute
     * @return table with values from response or empty Optional object if there are no values
     * @throws SQLException if the input command is wrong or there are problems with db connection
     * @throws SQLTimeoutException when the driver has determined that the timeout value that was
     * specified by the setQueryTimeout method has been exceeded and has at least attempted to cancel
     * the currently running Statement
     * @throws RuntimeException if there are problems with data base connection
     */
    public Optional<Table> parseCommand(String command) throws SQLException {
        Optional<Table> result = Optional.empty();
        setConnection();

        Statement statement = connection.createStatement();
        statement.setQueryTimeout(30);  // set timeout to 30 sec.
        if (command.toLowerCase().contains("select")) {
            result = Optional.of(execQuery(command, statement));
        } else {
            execUpdate(command, statement);
        }

        dumpConnection();
        return  result;
    }

    /**
     * Execute user command with SELECT key word
     * @param command command we need to execute
     * @param statement statement we use to create a command
     * @return table with values from response
     * @throws SQLException if the input command is wrong or there are problems with db connection
     * @throws SQLTimeoutException when the driver has determined that the timeout value that was
     * specified by the setQueryTimeout method has been exceeded and has at least attempted to cancel
     * the currently running Statement
     */
    private Table execQuery(String command, Statement statement) throws SQLException {
        ResultSet rs = statement.executeQuery(command);
        ResultSetMetaData data = rs.getMetaData();

        return createResponseTable(data, rs);
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
     * Gets data from response and creates Table object
     * @param data metadata of selected data
     * @param set selected data
     * @return table with values from response
     * @throws SQLException if there are problems with access to db
     */
    private Table createResponseTable(ResultSetMetaData data, ResultSet set) throws SQLException {
        List<String> columns = new ArrayList<>();
        for (int i = 0; i < data.getColumnCount(); ++i) {
            columns.add(data.getColumnName(i + 1));
        }

        Table resultTable = new Table(columns);

        do {
            List<String> row = new ArrayList<>();
            for (int i = 0; i < data.getColumnCount(); ++i) {
                row.add(set.getObject(i + 1).toString());
            }
            resultTable.addRow(row);
        } while (set.next());

        return resultTable;
    }
}
