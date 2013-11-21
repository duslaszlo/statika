package egyeb;

import java.sql.*;

public class ab_ {
    
    String tartonev= "oszlop38-3";
    String parancs= "select * from racsalap where nev = '"+tartonev+"'";
    static Connection co;
    static Statement st;
    static ResultSet rs;
    
    public static void main(String[] args)  {
        try {
            Class.forName("com.mysql.jdbc.Driver").newInstance();
            co = DriverManager.getConnection("jdbc:mysql://localhost/statika","root", "");
            st = co.createStatement();          
            //rs = st.executeQuery("SELECT hossz,szelveny,konzol,tipus FROM tartok WHERE tartonev = 'MDI tartó ver1 – tartó5' limit 0,20");
            //rs = st.executeQuery("SELECT jelleg,ertek,hely,hossz FROM `tartoerok` WHERE `projekt` = 'MDI tartó ver1' and `tartonev` = 'tarto5'");
            rs = st.executeQuery("select * from racsalap where nev = 'oszlop38-3'");
            while(rs.next()){
                System.out.print(rs.getString(1));
                System.out.print(", ");
                System.out.print(rs.getString(2));
                System.out.print(", ");
                System.out.print(rs.getString(3));
                System.out.print(", ");
                System.out.println(rs.getString(4));
            }
        } catch (InstantiationException e) {
        } catch (IllegalAccessException e) {
        } catch (ClassNotFoundException e) {
        } catch (SQLException e) {
        }
    }
}