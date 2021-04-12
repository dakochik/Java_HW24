package model;

import java.util.ArrayList;
import java.util.List;

public class Table {
    /**
     * headers of table columns
     */
    private final List<String> columnHeaders;

    /**
     * Collection of table rows
     */
    private final List<List<String>> rows;

    /**
     * Row pointer
     */
    private int position = -1;

    /**
     * Creates instance of table
     * @param headers headers of table columns
     */
    public Table(List<String> headers){
        columnHeaders = new ArrayList<>();
        rows = new ArrayList<>();

        columnHeaders.addAll(headers);
    }

    /**
     * Adds new tow to the current table
     * @param cells new row
     * @throws RuntimeException if the number of columns of the new row is not equal to number of headers
     */
    public void addRow(List<String> cells){
        if(cells.size() != columnHeaders.size()){
            throw new RuntimeException("Cells can be null or row can't has extra columns");
        }

        rows.add(cells);
    }

    /**
     * Moves pointer to the next row
     * @return has bottom of the table been reached
     */
    public boolean next(){
        return ++position < rows.size();
    }

    /**
     * Moves table row pointer to begining
     */
    public void moveToBegin(){
        position = -1;
    }

    /**
     * Table rows getter
     * @return rows from current table
     * @throws RuntimeException if bottom of table has been reached
     */
    public List<String> getRow(){
        if(position >= rows.size()){
            throw  new RuntimeException("Index of rows is out of range");
        }

        return new ArrayList<>(){{addAll(rows.get(position));}};
    }

    /**
     * Columns headers getter
     * @return columns headers
     */
    public List<String> getColumns(){
        return new ArrayList<>(){{addAll(columnHeaders);}};
    }
}
