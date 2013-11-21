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
        try {
            Class.forName("com.mysql.jdbc.Driver").newInstance();
            co = DriverManager.getConnection(Global.mysql_server, Global.mysql_user, Global.mysql_password);
            st = co.createStatement();
            profil.parancs = "SELECT nev FROM szelveny where megnevezes = 'MSZ328 szerinti L-profil' order by nev;";
            //System.out.println("SQL: " + profil.parancs);
            rs = st.executeQuery(profil.parancs);
            while (rs.next()) {
                profil.nev = rs.getString("nev");
                System.out.println(profil.nev);
                profil.beolvas();
                profil.meretx = (int)profil.szelesseg;
                profil.merety = (int)profil.magassag;
                profil.keresztmetszet_szamolo(0);
                adat = "-- " + profil.nev + " adatai:\r\n";
                outfile.write(adat);
                adat = "update szelveny set A = '" + profil.A_szamolt + "' where A = '0' and filenev ='" + profil.filenev + "';\r\n";
                outfile.write(adat);
                adat = "update szelveny set Ix = '" + profil.ix_szamolt + "' where Ix = '0' and filenev ='" + profil.filenev + "';\r\n";
                outfile.write(adat);
                adat = "update szelveny set Sx = '" + profil.sx_szamolt + "' where Sx = '0' and filenev ='" + profil.filenev + "';\r\n";
                outfile.write(adat);
                adat = "update szelveny set Iy = '" + profil.iy_szamolt + "' where Iy = '0' and filenev ='" + profil.filenev + "';\r\n";
                outfile.write(adat);
                adat = "update szelveny set Sy = '" + profil.sy_szamolt + "' where Sy = '0' and filenev ='" + profil.filenev + "';\r\n";
                outfile.write(adat);
                adat = "update szelveny set ex = '" + profil.ex_szamolt * 10 + "' where ex = '0' and filenev ='" + profil.filenev + "';\r\n";
                outfile.write(adat);
                adat = "update szelveny set ey = '" + profil.ey_szamolt * 10 + "' where ey = '0' and filenev ='" + profil.filenev + "';\r\n";
                outfile.write(adat);
                adat = "update szelveny set Kx = '" + profil.kx_szamolt + "' where Kx = '0' and filenev ='" + profil.filenev + "';\r\n";
                outfile.write(adat);
                adat = "update szelveny set Ky = '" + profil.ky_szamolt + "' where Ky = '0' and filenev ='" + profil.filenev + "';\r\n";
                outfile.write(adat);
                adat = "update szelveny set inx = '" + profil.inx_szamolt + "' where inx = '0' and filenev ='" + profil.filenev + "';\r\n";
                outfile.write(adat);
                adat = "update szelveny set iny = '" + profil.iny_szamolt + "' where iny = '0' and filenev ='" + profil.filenev + "';\r\n";
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
