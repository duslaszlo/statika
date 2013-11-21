/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package egyeb;

/**
 *
 * @author SD-LEAP
 */
// Imports
import java.awt.*;
import java.util.*;
import javax.swing.*;
import javax.swing.table.*;

class CustomRenderTableExample
        extends JFrame {
    // Instance attributes used in this example

    private JPanel topPanel;
    private JTable table;
    private JScrollPane scrollPane;
    private String columnNames[];
    private String dataValues[][];

    // Constructor of main frame
    public CustomRenderTableExample() {
        // Set the frame characteristics
        setTitle("Custom Cell Rendering Application");
        setSize(300, 200);
        setBackground(Color.gray);

        // Create a panel to hold all other components
        topPanel = new JPanel();
        topPanel.setLayout(new BorderLayout());
        getContentPane().add(topPanel);

        // Create the custom data model
        CustomDataModel customDataModel = new CustomDataModel();

        // Create a new table instance
        table = new JTable(customDataModel); //dataValues, columnNames );

        // Create columns
        CreateColumns();

        // Configure some of JTable's paramters
        table.setShowHorizontalLines(false);
        table.setRowSelectionAllowed(true);
        table.setColumnSelectionAllowed(true);

        // Change the selection colour
        table.setSelectionForeground(Color.white);
        table.setSelectionBackground(Color.red);

        // Add the table to a scrolling pane
        scrollPane = table.createScrollPaneForTable(table);
        topPanel.add(scrollPane, BorderLayout.CENTER);
    }

    public void CreateColumns() {
        // Say that we are manually creating the columns
        table.setAutoCreateColumnsFromModel(false);

        for (int iCtr = 0; iCtr < 4; iCtr++) {
            // Manually create a new column
            TableColumn column = new TableColumn(iCtr);
            column.setHeaderValue((Object) ("Col:" + iCtr));

            // Add a cell renderer for this class
            column.setCellRenderer(new CustomCellRenderer());

            // Add the column to the table
            table.addColumn(column);
        }
    }

    // Main entry point for this example
    public static void main(String args[]) {
        // Create an instance of the test application
        CustomRenderTableExample mainFrame = new CustomRenderTableExample();
        mainFrame.setVisible(true);
    }
}