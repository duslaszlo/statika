/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package egyeb;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 *
 * @author programt515
 */
public class dbo {
    Connection co=null;
    public boolean set_connection(String purl, String puser, String ppass){
        try {
            Class.forName("com.mysql.jdbc.Driver").newInstance();
            co = DriverManager.getConnection(purl, puser, ppass);
        } catch (InstantiationException e) {
            return false;
        } catch (IllegalAccessException e) {
            return false;
        } catch (ClassNotFoundException e) {
            return false;
        } catch (SQLException e) {
            return false;
        }
        return true;
    }
    public Connection get_connection(){
        return co;
    }
    public ResultSet get_data(String psql_string){
     Statement st;
     ResultSet rs;
        try {
            st = co.createStatement();
            rs = st.executeQuery(psql_string);
            return rs;
        } catch (SQLException e) {
            return null;
        }
    }
    public String[][] get_data_as_array(String psql_string){
     Statement st;
     ResultSet rs;
        try {
            st = co.createStatement();
            rs = st.executeQuery(psql_string);
            //ResultSetMetaData rsm = rs.getMetaData();
            //int oszlopokszam = rsm.getColumnCount();
            rs.last();
            String[][] _data = new String[rs.getRow()][rs.getMetaData().getColumnCount()];
            rs.beforeFirst();
            int j=0;// sorok szamozasara valo
            while(rs.next()){
                // az i az oszlopok szamozasar valo
                /* for(int i=0; i<rs.getMetaData().getColumnCount();i++){
                    _data[j][i]=rs.getString(i+1);
                   }
                }*/
                for(int i=1; i<=rs.getMetaData().getColumnCount();i++){
                    _data[j][i-1]=rs.getString(i);
                    System.out.println(rs.getString(i));
                }
                j++;
            }
            return _data;
        } catch (SQLException e){
            return null;
        }
    }


}
