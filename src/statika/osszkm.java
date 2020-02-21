/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package statika;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 *
 * @author duslaci
 */
public class osszkm {

    static Connection co;
    static Statement st;
    static ResultSet rs;

    public static void main(String[] args) throws IOException {
        String filenev, adat;
        kmadatok profil = new kmadatok();
        filenev = "kmbeiro_l.sql";
        FileWriter fstream = new FileWriter(filenev);
        BufferedWriter outfile = new BufferedWriter(fstream);
        String mysql_server = "jdbc:mysql://localhost/statika";
        String mysql_user = "leslie";
        String mysql_password = "garfield";
        try {
            Class.forName("com.mysql.jdbc.Driver").newInstance();
            co = DriverManager.getConnection(mysql_server, mysql_user, mysql_password);
            st = co.createStatement();
            profil.parancs = "SELECT nev FROM szelveny where megnevezes = 'MSZ328 szerinti L-profil' order by nev";
            //System.out.println("SQL: " + profil.parancs);
            rs = st.executeQuery(profil.parancs);
            while (rs.next()) {
                profil.nev = rs.getString("nev");
                System.out.println(profil.nev);
                profil.beolvas();
                profil.meretx = (int) profil.profil.get(0).getSzelesseg();
                profil.merety = (int) profil.profil.get(0).getMagassag();
                profil.keresztmetszet_szamolo(0);
                adat = "-- " + profil.nev + " adatai:\r\n";
                outfile.write(adat);
                adat = "update szelveny set A = '" + profil.profil_szamolt.getA() + "' where A = '0' and filenev ='" + profil.filenev + "'\r\n";
                outfile.write(adat);
                adat = "update szelveny set Ix = '" + profil.profil_szamolt.getIx() + "' where Ix = '0' and filenev ='" + profil.filenev + "'\r\n";
                outfile.write(adat);
                adat = "update szelveny set Sx = '" + profil.profil_szamolt.getSx() + "' where Sx = '0' and filenev ='" + profil.filenev + "'\r\n";
                outfile.write(adat);
                adat = "update szelveny set Iy = '" + profil.profil_szamolt.getIy() + "' where Iy = '0' and filenev ='" + profil.filenev + "'\r\n";
                outfile.write(adat);
                adat = "update szelveny set Sy = '" + profil.profil_szamolt.getSy() + "' where Sy = '0' and filenev ='" + profil.filenev + "'\r\n";
                outfile.write(adat);
                adat = "update szelveny set ex = '" + profil.profil_szamolt.getEx() * 10 + "' where ex = '0' and filenev ='" + profil.filenev + "'\r\n";
                outfile.write(adat);
                adat = "update szelveny set ey = '" + profil.profil_szamolt.getEy() * 10 + "' where ey = '0' and filenev ='" + profil.filenev + "'\r\n";
                outfile.write(adat);
                adat = "update szelveny set Kx = '" + profil.profil_szamolt.getKx() + "' where Kx = '0' and filenev ='" + profil.filenev + "'\r\n";
                outfile.write(adat);
                adat = "update szelveny set Ky = '" + profil.profil_szamolt.getKy() + "' where Ky = '0' and filenev ='" + profil.filenev + "'\r\n";
                outfile.write(adat);
                adat = "update szelveny set inx = '" + profil.profil_szamolt.getInx() + "' where inx = '0' and filenev ='" + profil.filenev + "'\r\n";
                outfile.write(adat);
                adat = "update szelveny set iny = '" + profil.profil_szamolt.getIny() + "' where iny = '0' and filenev ='" + profil.filenev + "'\r\n";
                outfile.write(adat);
                adat = "\r\n";
                outfile.write(adat);
            }
            rs.close();
            st.close();
        } catch (InstantiationException e) {
        } catch (IllegalAccessException e) {
        } catch (ClassNotFoundException e) {
        } catch (SQLException e) {
        }
        outfile.close();
    }
}
