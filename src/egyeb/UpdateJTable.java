/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package egyeb;

/**
 *
 * @author SD-LEAP
 */
import java.sql.*;
import java.util.*;
import javax.swing.*;
import javax.swing.table.*;

public class UpdateJTable extends TimerTask {

    private static JTable table = new JTable();
    private static Vector columnNames = new Vector();
    private static Vector data = new Vector();
    private static JFrame f = new JFrame();
    private static JScrollPane scrollPane = new JScrollPane();

    public static void main(String[] args) {
        java.util.Timer timer = new java.util.Timer();
        timer.schedule(new UpdateJTable(), 0, 10000);
    }

    public void run() {
        remove();
        table = draw();
        scrollPane.getViewport().add(table, 0);
        f.getContentPane().add(scrollPane);
        f.setVisible(true);
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.pack();
    }

    public static JTable draw() {
        try {
            Class.forName("com.mysql.jdbc.Driver").newInstance();
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost/statika", "leslie", "garfield");
            String sql = "Select * from valami";
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            ResultSetMetaData md = rs.getMetaData();
            int columns = md.getColumnCount();
            for (int i = 1; i <= columns; i++) {
                columnNames.addElement(md.getColumnName(i));
            }
            while (rs.next()) {
                Vector row = new Vector(columns);
                for (int i = 1; i <= columns; i++) {
                    row.addElement(rs.getObject(i));
                }
                data.addElement(row);
            }
            rs.close();
            stmt.close();
        } catch (Exception e) {
            System.out.println(e);
            e.printStackTrace();
        }
        table.setModel(new DefaultTableModel(data, columnNames));
        TableColumn col;
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        for (int i = 0; i < table.getColumnCount(); i++) {
            col = table.getColumnModel().getColumn(i);
            col.setMaxWidth(col.getMaxWidth());
        }
        return table;
    }

    public static void remove() {
        columnNames.clear();
        if (table.getRowCount() != 0) {
            DefaultTableModel dm = (DefaultTableModel) table.getModel();
            dm.getDataVector().removeAllElements();
        }
    }
}