/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package statika;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.image.BufferedImage;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 *
 * @author duslaszlo
 */
public class racstervezoadatok {

    String nev, filenev;                                  // A szelvény neve és a file-neve    
    String parancs;                                       // A MySQL parancsok gyűjtőhelye 
    int maxelem = 20;                                     // A szekciók maximális száma
    int maxelem1 = 200;                                     // A szekciók * közök maximális száma
    int szekcioszam;                                      // A drótvázon belüli szekciók száma
    int kozszam;                                          // A drótvázon belüli közök száma
    int csomopontszam = 2000;                             // A csomópontok maximális száma
    int rudszam = 2000;                                   // A rudak maximális száma
    int[][] adatok = new int[maxelem][13];                // szekcio(0),magassag(1),alsoxy(2),alsoyz(3),felsoxy(4),felsoyz(5),diffx(6),
    // diffy(7),diffz(8),eltolasxy(9),eltolasyz(10),konzol(11),fugg/vízsz(12) (-->> racsalap tábla)
    int[][] adatok1 = new int[maxelem1][20];              // szekcio(0),magassag(1),alsoxy(2),alsoyz(3),felsoxy(4),felsoyz(5),diffx(6),
    // diffy(7),diffz(8),eltolasxy(9),eltolasyz(10),koz(11),a rácselemek kódjai (12-19) (-->> racsalap1 tábla)
    int mintaindexf, mintarudindex, mintaindexv;
    int[] racselemek = new int[9];                        // A rácselemtervezésnél a kiválasztott szekció első közének elemei
    float[][] mintacspf = new float[200][3];              // A függőleges mintacsomopont alapkoordinátái: x(0),y(1),z(2)
    int[][] mintacspfjelleg = new int[200][6];            // A függőleges mintacsomopont jellege: xy(0),yz(1),kezdcspxy(2),vegecspxy(3),kezdcspyz(4),vegecspyz(5)
    float[][] mintacspv = new float[15][3];               // A vízszintes mintacsomopont alapkoordinátái: ,x(0),y(1),z(2)
    int[][] mintarud = new int[700][5];                   // Az alap-rúdmintázat irány(0), tipus(1)/1->8/,verzio(2),kezdőcsp(3),végecsp(4);
    float[][] tempcsp = new float[200][3];                // Az átmeneti drótváz-elem koordinátái x(0),y(1),z(2)
    float[][] csomopont = new float[csomopontszam][4];    // A drótváz koordinátái szekcio(0),x(1),y(2),z(3)
    int[][] rud = new int[rudszam][8];                    // A drótváz rúdjainak (szekciószám(0)) kezdő(1) és végcsomópontjai(2),vastagság(3), a kijelzés megjelölése(0/1/2)(4),koz(5),tipus(6),hossz(7)
    String[][] rudnevek = new String[maxelem][9];         // A szekciokijelzesnel az aktuális rudszelvények nevei 
    int csomopontindex, rudindex;                         // A beolvasott drórváz csompontjainak max. értéke & az éppen kiválasztott szekció sorszáma
    float[][][] limitek = new float[2][3][2];             // A drótváz maximum és minimum értékei  [szekció(1)/teljes(0)], [x(0),y(1),z(2)], 
    // [minimum(0)/maximum(1)] (minx, miny, minz, maxx, maxy, maxz)
    int mx0 = 0, my0 = 0, mx1 = 0, my1 = 0;                // Az egér pozíciójának átmeneti tárolója a forgatásnál (0-szekció,1-teljes) 
    int tx0 = 0, ty0 = 0, tx1 = 0, ty1 = 0;                // Az egér pozíciójának átmeneti tárolója az eltolásnál (0-szekció,1-teljes) 
    int[][] kepkozep = new int[2][2];                      // A kijelzett kép X-középpontja [(0-szekció,1-teljes)]  [X-0,Y-1]
    // A szekcióelem rajza
    int width = 400, height = 350;                        // A kijelzett kép mérete 
    BufferedImage bi1 = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
    Graphics2D g1 = bi1.createGraphics();
    // A teljes drótváz rajza    
    BufferedImage bi2 = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
    Graphics2D g2 = bi2.createGraphics();
    // A képtörlés    
    BufferedImage bi3 = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
    Graphics2D g3 = bi3.createGraphics();
    // A rajzoláshoz szükséges változók
    double szog;                                          // A forgatásnál az elfordítás szöge - átmeneti tároló
    float x, y, z;                                        // Átmeneti tárolók a forgatásnál/rajzolásnál
    int[][] forgatas = new int[2][4];                     // A forgatás mértéke X,Y,Z irányú forgatás szekció(1), teljes(0)
    float[] kozepx = new float[maxelem];                  // A szekció(1..maxelem)/teljes(1) drótváz közepe - forgási középpont
    float[] kozepy = new float[maxelem];                  // A szekció(1..maxelem)/teljes(1) drótváz közepe - forgási középpont
    float[] kozepz = new float[maxelem];                  // A szekció(1..maxelem)/teljes(1) drótváz közepe - forgási középpont
    float[] kepnagyitas = new float[2];                   // A kép kijelzésénél a képnagyítás mértéke 0-teljes/1-szekcio
    static Connection co;
    static Statement st;
    static ResultSet rs;
    int adat = 0;

    public void adatbeolvaso() {
        try {
            Class.forName("com.mysql.jdbc.Driver").newInstance();
            co = DriverManager.getConnection(Global.mysql_server, Global.mysql_user, Global.mysql_password);
            st = co.createStatement();
            // A rács szekcióinak adatainak a beolvasása
            //parancs = "SELECT * FROM racsalap where nev ='" + nev + "' order by szekcio;";
            parancs = "SELECT racsalap.*, reszadat.nev1, reszadat.nev2, reszadat.nev3, reszadat.nev4, ";
            parancs = parancs + "reszadat.nev5, reszadat.nev6, reszadat.nev7, reszadat.nev8 FROM `racsalap` ";
            parancs = parancs + "left join (SELECT distinct szekcio,`nev1`, `nev2`, `nev3`, `nev4`, `nev5`, `nev6`, ";
            parancs = parancs + "`nev7`, `nev8` FROM `racsalap1` WHERE nev = '" + nev;
            parancs = parancs + "') as reszadat on reszadat.szekcio = racsalap.szekcio WHERE racsalap.`nev` = '" + nev + "' ORDER BY racsalap.szekcio;";
            //System.out.println("SQL: "+parancs);
            rs = st.executeQuery(parancs);
            szekcioszam = 0;
            while (rs.next()) {
                szekcioszam++;
                //System.out.println(index);
                adatok[szekcioszam][0] = rs.getInt("szekcio");
                adatok[szekcioszam][1] = rs.getInt("magassag");
                adatok[szekcioszam][2] = rs.getInt("alsoszelxy");
                adatok[szekcioszam][3] = rs.getInt("alsoszelyz");
                adatok[szekcioszam][4] = rs.getInt("felsoszelxy");
                adatok[szekcioszam][5] = rs.getInt("felsoszelyz");
                adatok[szekcioszam][6] = rs.getInt("x");
                adatok[szekcioszam][7] = rs.getInt("y");
                adatok[szekcioszam][8] = rs.getInt("z");
                adatok[szekcioszam][9] = rs.getInt("eltolasxy");
                adatok[szekcioszam][10] = rs.getInt("eltolasyz");
                adatok[szekcioszam][11] = rs.getInt("teljes");
                adatok[szekcioszam][12] = rs.getInt("irany");
                rudnevek[szekcioszam][1] = rs.getString("nev1");
                rudnevek[szekcioszam][2] = rs.getString("nev2");
                rudnevek[szekcioszam][3] = rs.getString("nev3");
                rudnevek[szekcioszam][4] = rs.getString("nev4");
                rudnevek[szekcioszam][5] = rs.getString("nev5");
                rudnevek[szekcioszam][6] = rs.getString("nev6");
                rudnevek[szekcioszam][7] = rs.getString("nev7");
                rudnevek[szekcioszam][8] = rs.getString("nev8");
            }
            rs.close();
            // A rács köz adatainak a beolvasása
            parancs = "SELECT * FROM racsalap1 where nev ='" + nev + "' order by szekcio,koz;";
            //System.out.println("SQL: "+parancs);
            rs = st.executeQuery(parancs);
            kozszam = 0;
            while (rs.next()) {
                kozszam++;
                //System.out.println(index);
                adatok1[kozszam][0] = rs.getInt("szekcio");
                adatok1[kozszam][1] = rs.getInt("magassag");
                adatok1[kozszam][2] = rs.getInt("alsoszelxy");
                adatok1[kozszam][3] = rs.getInt("alsoszelyz");
                adatok1[kozszam][4] = rs.getInt("felsoszelxy");
                adatok1[kozszam][5] = rs.getInt("felsoszelyz");
                adatok1[kozszam][6] = rs.getInt("x");
                adatok1[kozszam][7] = rs.getInt("y");
                adatok1[kozszam][8] = rs.getInt("z");
                adatok1[kozszam][9] = rs.getInt("eltolasxy");
                adatok1[kozszam][10] = rs.getInt("eltolasyz");
                adatok1[kozszam][11] = rs.getInt("koz");
                adatok1[kozszam][12] = rs.getInt("racs1");
                adatok1[kozszam][13] = rs.getInt("racs2");
                adatok1[kozszam][14] = rs.getInt("racs3");
                adatok1[kozszam][15] = rs.getInt("racs4");
                adatok1[kozszam][16] = rs.getInt("racs5");
                adatok1[kozszam][17] = rs.getInt("racs6");
                adatok1[kozszam][18] = rs.getInt("racs7");
                adatok1[kozszam][19] = rs.getInt("racs8");
            }
            rs.close();
            st.close();
        } catch (InstantiationException e) {
        } catch (IllegalAccessException e) {
        } catch (ClassNotFoundException e) {
        } catch (SQLException e) {
        }
        // A csomopont koordinátái        
        filenev = "./images/drotvaz/" + nev + ".png";
        forgatas[0][1] = 0;
        forgatas[0][2] = 0;
        forgatas[0][3] = 0;
        forgatas[1][1] = 0;
        forgatas[1][2] = 0;
        forgatas[1][3] = 0;
        kepnagyitas[0] = 1;
        kepnagyitas[1] = 1;
    }

    public void adatrogzito() {        
        try {
            Class.forName("com.mysql.jdbc.Driver").newInstance();
            co = DriverManager.getConnection(Global.mysql_server, Global.mysql_user, Global.mysql_password);
            st = co.createStatement();
            // A racsalap tábla
            // A meglévő adatok törlése
            parancs = "delete from racsalap where nev = '" + nev + "';";
            //System.out.println(parancs);
            st.execute(parancs);
            // A szekció elemének lerögzítése
            for (int i = 1; i <= szekcioszam; i++) {
                parancs = "INSERT INTO racsalap (nev,szekcio,magassag,alsoszelxy,alsoszelyz,felsoszelxy,felsoszelyz,x,y,z,eltolasxy,eltolasyz,teljes,irany) VALUES ( '";
                parancs = parancs + nev + "','";
                for (int k = 0; k <= 11; k++) {
                    parancs = parancs + adatok[i][k] + "','";
                }
                parancs = parancs + adatok[i][12];
                parancs = parancs + "');";
                //System.out.println(parancs);
                st.execute(parancs);
            }
            // A rácslap1 tábla 
            // A meglévő adatok törlése
            parancs = "delete from racsalap1 where nev = '" + nev + "';";
            //System.out.println(parancs);
            st.execute(parancs);
            // A közök elemeinek lerögzítése
            for (int i = 1; i <= kozszam; i++) {
                parancs = "INSERT INTO racsalap1 (nev,szekcio,magassag,alsoszelxy,alsoszelyz,felsoszelxy,felsoszelyz,x,y,z,eltolasxy,eltolasyz,koz,";
                parancs = parancs + "racs1,racs2,racs3,racs4,racs5,racs6,racs7,racs8,nev1,nev2,nev3,nev4,nev5,nev6,nev7,nev8) VALUES ( '";
                parancs = parancs + nev + "','";
                for (int k = 0; k <= 19; k++) {
                    parancs = parancs + adatok1[i][k] + "','";
                }
                for (int k = 1; k < 8; k++) {
                    parancs = parancs + rudnevek[adatok1[i][0]][k] + "','";
                }
                parancs = parancs + rudnevek[adatok1[i][0]][8] + "');";
                //System.out.println(parancs);
                st.execute(parancs);
            }
            st.close();
        } catch (InstantiationException e) {
        } catch (IllegalAccessException e) {
        } catch (ClassNotFoundException e) {
        } catch (SQLException e) {
        }
    }

    public void kozeppont_szamolo(int tetel) {
        // tetel:  0-teljes rajz, a többi : szekciórajz
        // A köpéppont kiszámolása   
        float diff = 0;
        if (tetel != 0) {
            // limitek[1][0][1] --> maxx
            // limitek[1][0][0] --> minx
            limitek[1][0][1] = Integer.MIN_VALUE;
            limitek[1][0][0] = Integer.MAX_VALUE;
            for (int i = 1; i <= rudindex; i++) {
                if (rud[i][0] == tetel) {
                    if (csomopont[rud[i][1]][1] > limitek[1][0][1]) {
                        limitek[1][0][1] = csomopont[rud[i][1]][1];
                    }
                    if (csomopont[rud[i][1]][1] < limitek[1][0][0]) {
                        limitek[1][0][0] = csomopont[rud[i][1]][1];
                    }
                    if (csomopont[rud[i][2]][1] > limitek[1][0][1]) {
                        limitek[1][0][1] = csomopont[rud[i][2]][1];
                    }
                    if (csomopont[rud[i][2]][1] < limitek[1][0][0]) {
                        limitek[1][0][0] = csomopont[rud[i][2]][1];
                    }
                }
            }
            kozepx[1] = limitek[1][0][0] + (limitek[1][0][1] - limitek[1][0][0]) / 2;
            // limitek[1][1][1] --> maxy
            // limitek[1][1][0] --> miny
            limitek[1][1][1] = Integer.MIN_VALUE;
            limitek[1][1][0] = Integer.MAX_VALUE;
            for (int i = 1; i <= rudindex; i++) {
                if (rud[i][0] == tetel) {
                    if (csomopont[rud[i][1]][2] > limitek[1][1][1]) {
                        limitek[1][1][1] = csomopont[rud[i][1]][2];
                    }
                    if (csomopont[rud[i][1]][2] < limitek[1][1][0]) {
                        limitek[1][1][0] = csomopont[rud[i][1]][2];
                    }
                    if (csomopont[rud[i][2]][2] > limitek[1][1][1]) {
                        limitek[1][1][1] = csomopont[rud[i][2]][2];
                    }
                    if (csomopont[rud[i][2]][2] < limitek[1][1][0]) {
                        limitek[1][1][0] = csomopont[rud[i][2]][2];
                    }
                }
            }
            kozepy[1] = limitek[1][1][0] + (limitek[1][1][1] - limitek[1][1][0]) / 2;

            // limitek[1][2][1] --> maxz
            // limitek[1][2][0] --> minz
            limitek[1][2][1] = Integer.MIN_VALUE;
            limitek[1][2][0] = Integer.MAX_VALUE;
            for (int i = 1; i <= rudindex; i++) {
                if (rud[i][0] == tetel) {
                    if (csomopont[rud[i][1]][3] > limitek[1][2][1]) {
                        limitek[1][2][1] = csomopont[rud[i][1]][3];
                    }
                    if (csomopont[rud[i][1]][3] < limitek[1][2][0]) {
                        limitek[1][2][0] = csomopont[rud[i][1]][3];
                    }
                    if (csomopont[rud[i][2]][3] > limitek[1][2][1]) {
                        limitek[1][2][1] = csomopont[rud[i][2]][3];
                    }
                    if (csomopont[rud[i][2]][3] < limitek[1][2][0]) {
                        limitek[1][2][0] = csomopont[rud[i][2]][3];
                    }
                }
            }
            kozepz[1] = limitek[1][2][0] + (limitek[1][2][1] - limitek[1][2][0]) / 2;
            // Középrehozás
            /*System.out.println();
             System.out.println("Tetel:" + tetel);
             System.out.println("minx:" + limitek[1][0][0] + "  maxx:" + limitek[1][0][1]
             + "  miny:" + limitek[1][1][0] + "  maxy:" + limitek[1][1][1]
             + "  minz:" + limitek[1][2][0] + "  maxz:" + limitek[1][2][1]);
             System.out.println("kozepx:" + kozepx[1] + "   kozepy:" + kozepy[1] + "   kozepz:" + kozepz[1]);*/
            diff = limitek[1][0][0];
            kozepx[1] -= diff;
            /*limitek[1][0][0] -= diff;
             limitek[1][0][1] -= diff;   */
            diff = limitek[1][1][0];
            kozepy[1] -= diff;
            /*limitek[1][1][0] -= diff;
             limitek[1][1][1] -= diff;  */
            diff = limitek[1][2][0];
            kozepz[1] -= diff;
            /*limitek[1][2][0] -= diff;
             limitek[1][2][1] -= diff;    */
            /*System.out.println("minx:" + limitek[1][0][0] + "  maxx:" + limitek[1][0][1]
             + "  miny:" + limitek[1][1][0] + "  maxy:" + limitek[1][1][1]
             + "  minz:" + limitek[1][2][0] + "  maxz:" + limitek[1][2][1]);
             System.out.println("kozepx:" + kozepx[1] + "   kozepy:" + kozepy[1] + "   kozepz:" + kozepz[1]);*/
        } else {
            // limitek[1][0][1] --> maxx
            // limitek[1][0][0] --> minx
            limitek[0][0][1] = Integer.MIN_VALUE;
            limitek[0][0][0] = Integer.MAX_VALUE;
            for (int i = 1; i <= csomopontindex; i++) {
                if (csomopont[i][1] > limitek[0][0][1]) {
                    limitek[0][0][1] = csomopont[i][1];
                }
                if (csomopont[i][1] < limitek[0][0][0]) {
                    limitek[0][0][0] = csomopont[i][1];
                }
            }
            kozepx[0] = limitek[0][0][0] + (limitek[0][0][1] - limitek[0][0][0]) / 2;
            // limitek[1][1][1] --> maxy
            // limitek[1][1][0] --> miny
            limitek[0][1][1] = Integer.MIN_VALUE;
            limitek[0][1][0] = Integer.MAX_VALUE;
            for (int i = 1; i <= csomopontindex; i++) {
                if (csomopont[i][2] > limitek[0][1][1]) {
                    limitek[0][1][1] = csomopont[i][2];
                }
                if (csomopont[i][2] < limitek[0][1][0]) {
                    limitek[0][1][0] = csomopont[i][2];
                }
            }
            kozepy[0] = limitek[0][1][0] + (limitek[0][1][1] - limitek[0][1][0]) / 2;
            // limitek[1][2][1] --> maxz
            // limitek[1][2][0] --> minz
            limitek[0][2][1] = Integer.MIN_VALUE;
            limitek[0][2][0] = Integer.MAX_VALUE;
            for (int i = 1; i <= csomopontindex; i++) {
                if (csomopont[i][3] > limitek[0][2][1]) {
                    limitek[0][2][1] = csomopont[i][3];
                }
                if (csomopont[i][3] < limitek[0][2][0]) {
                    limitek[0][2][0] = csomopont[i][3];
                }
            }
            kozepz[0] = limitek[0][2][0] + (limitek[0][2][1] - limitek[0][2][0]) / 2;

        }
        /*System.out.println();
         System.out.println("Tetel:" + tetel);
         System.out.println("Teljes szerkezet:");
         System.out.println("kozepx:" + kozepx[0] + "   kozepy:" + kozepy[0] + "   kozepz:" + kozepz[0]);
         System.out.println("Szekcio:");
         System.out.println("kozepx:" + kozepx[1] + "   kozepy:" + kozepy[1] + "   kozepz:" + kozepz[1]);
         */
    }

    public void pontforgato(int elem, int tetel) {
        double atfogo;
        szog = 0;
        float kozepx_, kozepy_;
        float x_, y_;
        switch (elem) {
            case 2:
                kozepx_ = kozepx[tetel];
                kozepy_ = kozepz[tetel];
                x_ = x;
                y_ = z;
                break;
            case 3:
                kozepx_ = kozepy[tetel];
                kozepy_ = kozepz[tetel];
                x_ = y;
                y_ = z;
                break;
            default:
                kozepx_ = kozepx[tetel];
                kozepy_ = kozepy[tetel];
                x_ = x;
                y_ = y;
        }
        atfogo = Math.sqrt(Double.parseDouble(String.valueOf((kozepx_ - x_) * (kozepx_ - x_) + (kozepy_ - y_) * (kozepy_ - y_))));
        szog = Math.atan2((x_ - kozepx_), (y_ - kozepy_));
        szog += Math.toRadians(forgatas[tetel][elem]);
        switch (elem) {
            case 2:
                x = kozepx_ + Float.parseFloat(String.valueOf(Math.sin(szog) * atfogo));
                z = kozepy_ + Float.parseFloat(String.valueOf(Math.cos(szog) * atfogo));
                break;
            case 3:
                y = kozepx_ + Float.parseFloat(String.valueOf(Math.sin(szog) * atfogo));
                z = kozepy_ + Float.parseFloat(String.valueOf(Math.cos(szog) * atfogo));
                break;
            default:
                x = kozepx_ + Float.parseFloat(String.valueOf(Math.sin(szog) * atfogo));
                y = kozepy_ + Float.parseFloat(String.valueOf(Math.cos(szog) * atfogo));
        }
    }

    public void kozbeolvaso(int szekcio, int koz) {
        for (int i = 1; i <= 8; i++) {
            racselemek[i] =0;
        }
        for (int i = 1; i <= kozszam; i++) {
            if ((adatok1[i][0] == szekcio) && ((adatok1[i][11] == koz))) {
                racselemek[1] = adatok1[i][12];
                racselemek[2] = adatok1[i][13];
                racselemek[3] = adatok1[i][14];
                racselemek[4] = adatok1[i][15];
                racselemek[5] = adatok1[i][16];
                racselemek[6] = adatok1[i][17];
                racselemek[7] = adatok1[i][18];
                racselemek[8] = adatok1[i][19];
            }
        }
    }

    public void mintamasolo(int szekcio, int koz) {
        // A mintarács pontjainak rámásolása az átmeneti rácsra 
        float mag = 0, alsoxy = 0, alsoyz = 0, felsoxy = 0, felsoyz = 0, eltxy = 0, eltyz = 0, kezdx = 0, kezdy = 0, kezdz = 0;
        float[] pont = new float[7];  // A pontok meghatározásához a sarokpontok [x]  vagy [z]
        for (int i = 1; i < 200; i++) {
            tempcsp[i][0] = 0;
            tempcsp[i][1] = 0;
            tempcsp[i][2] = 0;
        }
        for (int i = 1; i <= kozszam; i++) {
            if ((adatok1[i][0] == szekcio) && ((adatok1[i][11] == koz))) {
                mag = adatok1[i][1];
                alsoxy = adatok1[i][2];
                alsoyz = adatok1[i][3];
                felsoxy = adatok1[i][4];
                felsoyz = adatok1[i][5];
                kezdx = adatok1[i][6];
                kezdy = adatok1[i][7];
                kezdz = adatok1[i][8];
                eltxy = adatok1[i][9];
                eltyz = adatok1[i][10];
            }
        }
        if (adatok[szekcio][12] == 1) {
            // függőleges a rács
            // y 
            for (int i = 1; i <= mintaindexf; i++) {
                tempcsp[i][1] = kezdy + mintacspf[i][1] * (mag / 100);
            }
            // x
            pont[1] = kezdx;
            pont[3] = kezdx + alsoxy;
            pont[2] = (pont[1] + pont[3]) / 2;
            pont[4] = kezdx + (alsoxy - felsoxy) / 2 + eltxy;
            pont[6] = kezdx + alsoxy - (alsoxy - felsoxy) / 2 + eltxy;
            pont[5] = (pont[4] + pont[6]) / 2;
            for (int i = 1; i <= mintaindexf; i++) {
                if (mintacspfjelleg[i][0] != 3) {
                    tempcsp[i][0] = pont[mintacspfjelleg[i][2]]
                            + (pont[mintacspfjelleg[i][3]]
                            - pont[mintacspfjelleg[i][2]]) * (mintacspf[i][1] / 100);
                }
            }
            for (int i = 1; i <= mintaindexf; i++) {
                if (mintacspfjelleg[i][0] == 3) {
                    tempcsp[i][0] = (tempcsp[mintacspfjelleg[i][2]][0]
                            + tempcsp[mintacspfjelleg[i][3]][0]) / 2;
                }
            }
            // z      
            pont[1] = kezdz;
            pont[3] = kezdz + alsoyz;
            pont[2] = (pont[1] + pont[3]) / 2;
            pont[4] = kezdz + (alsoyz - felsoyz) / 2 + eltyz;
            pont[6] = kezdz + alsoyz - (alsoyz - felsoyz) / 2 + eltyz;
            pont[5] = (pont[4] + pont[6]) / 2;
            for (int i = 1; i <= mintaindexf; i++) {
                if (mintacspfjelleg[i][1] != 3) {
                    tempcsp[i][2] = pont[mintacspfjelleg[i][4]]
                            + (pont[mintacspfjelleg[i][5]]
                            - pont[mintacspfjelleg[i][4]]) * (mintacspf[i][1] / 100);
                }
            }
            for (int i = 1; i <= mintaindexf; i++) {
                if (mintacspfjelleg[i][1] == 3) {
                    tempcsp[i][2] = (tempcsp[mintacspfjelleg[i][4]][2] + tempcsp[mintacspfjelleg[i][5]][2]) / 2;
                }
                // Az eltolások értékei XY-síkban
                tempcsp[i][0] += (mintacspf[i][1] / 100) * eltxy;
                // Az eltolások értékei YZ-síkban
                tempcsp[i][2] += (mintacspf[i][1] / 100) * eltyz;
                //System.out.println("Rácselem:" + i + "  x:" + tempcsp[i][0] + "  y:" + tempcsp[i][1] + "  z:" + tempcsp[i][2]);
            }
        } else {
            // Vízszintes a rács
            // x 
            for (int i = 1; i <= mintaindexv; i++) {
                tempcsp[i][0] = kezdx + mintacspv[i][0] * (mag / 100);
            }
            // y
            tempcsp[1][1] = kezdy;
            tempcsp[2][1] = tempcsp[1][1];
            tempcsp[3][1] = kezdy + alsoxy;
            tempcsp[4][1] = tempcsp[3][1];
            tempcsp[5][1] = kezdy + eltxy;
            tempcsp[6][1] = tempcsp[5][1];
            tempcsp[7][1] = kezdy + felsoxy + eltxy;
            tempcsp[8][1] = tempcsp[7][1];
            tempcsp[10][1] = (tempcsp[1][1] + tempcsp[5][1]) / 2;
            tempcsp[12][1] = tempcsp[10][1];
            tempcsp[9][1] = ((tempcsp[1][1] + tempcsp[3][1]) / 2 + (tempcsp[7][1] + tempcsp[5][1]) / 2) / 2;
            tempcsp[11][1] = tempcsp[9][1];
            tempcsp[13][1] = kezdy + felsoxy / 2 + eltxy;
            // z
            tempcsp[1][2] = kezdz;
            tempcsp[2][2] = kezdz + alsoyz;
            tempcsp[3][2] = tempcsp[1][2];
            tempcsp[4][2] = tempcsp[2][2];
            tempcsp[5][2] = kezdz + (alsoyz - felsoyz) / 2 + eltyz;
            tempcsp[6][2] = kezdz + (alsoyz - felsoyz) / 2 + felsoyz + eltyz;
            tempcsp[7][2] = tempcsp[5][2];
            tempcsp[8][2] = tempcsp[6][2];
            tempcsp[9][2] = (tempcsp[1][2] + tempcsp[5][2]) / 2;
            tempcsp[10][2] = tempcsp[9][2];
            tempcsp[11][2] = (tempcsp[2][2] + tempcsp[6][2]) / 2;
            tempcsp[12][2] = tempcsp[11][2];
            tempcsp[13][2] = (tempcsp[5][2] + tempcsp[6][2]) / 2;
            /*System.out.println("szekcio:" + szekcio + "  köz:" + koz);
             for (int i = 1; i <= 13; i++) {
             System.out.println("i:" + i + "  x:" + tempcsp[i][0] + " y:" + tempcsp[i][1] + " z:" + tempcsp[i][2]);
             }
             System.out.println();*/
        }
    }

    public void racselemek() {
        // Az átmeneti rács-ból a végső csomópontok és rúdelemek kikalkulálása 
        int koz;
        int[] racs = new int[9];
        csomopontindex = 0;
        rudindex = 0;
        for (int i = 1; i < rudszam; i++) {
            rud[i][4] = 0;
        }
        for (int i = 1; i <= szekcioszam; i++) {
            // A szekción belüli közök összeszámolása
            koz = 0;
            for (int j = 1; j <= kozszam; j++) {
                if (adatok1[j][0] == i) {
                    koz++;
                }
            }
            if (koz > 0) {
                for (int j = 1; j <= koz; j++) {
                    mintamasolo(i, j);
                    for (int k = 1; k < 9; k++) {
                        racs[k] = 0;
                    }
                    for (int k = 1; k <= kozszam; k++) {
                        if ((adatok1[k][0] == i) && ((adatok1[k][11] == j))) {
                            racs[1] = adatok1[k][12];
                            racs[2] = adatok1[k][13];
                            racs[3] = adatok1[k][14];
                            racs[4] = adatok1[k][15];
                            racs[5] = adatok1[k][16];
                            racs[6] = adatok1[k][17];
                            racs[7] = adatok1[k][18];
                            racs[8] = adatok1[k][19];
                        }
                    }
                    // A csomópontok és a rudak összerakása
                    for (int k = 1; k < 9; k++) {
                        if (racs[k] > 0) {
                            for (int m = 1; m <= mintarudindex; m++) {
                                if ((mintarud[m][0] == adatok[i][12]) && (mintarud[m][1] == k) && (mintarud[m][2] == racs[k])) {
                                    // A kezdő csomópont / végső csomópont
                                    //System.out.println("kezdcsp:"+mintarud[m][3]+" vegecsp:"+ mintarud[m][4]);
                                    csomopont_beiro(i, mintarud[m][3], mintarud[m][4]);
                                    // A rúd hozzáadása
                                    rud_beiro(i, mintarud[m][3], mintarud[m][4], j, k);
                                }
                            }
                        }
                    }
                }
            }
            if ((adatok[i][12] != 1) && ((adatok[i][11] - adatok[i][1]) != 0)) {
                // A vízszintes tartók végső konzolkinyúlása
                //x
                tempcsp[14][0] = tempcsp[5][0] + (adatok[i][11] - adatok[i][1]);
                tempcsp[15][0] = tempcsp[14][0];
                //y
                tempcsp[14][1] = tempcsp[1][1];
                tempcsp[15][1] = tempcsp[1][1];
                //z
                tempcsp[14][2] = tempcsp[5][2]
                        + ((adatok[i][11] - adatok[i][1]) / (tempcsp[5][0] - tempcsp[1][0])) * ((tempcsp[5][2] - tempcsp[1][2]));
                tempcsp[15][2] = tempcsp[6][2]
                        + ((adatok[i][11] - adatok[i][1]) / (tempcsp[6][0] - tempcsp[2][0])) * ((tempcsp[6][2] - tempcsp[2][2]));
                csomopont_beiro(i, 14, 15);
                rud_beiro(i, 14, 15, koz + 1, 0);
                rud_beiro(i, 5, 14, koz + 1, 0);
                rud_beiro(i, 6, 15, koz + 1, 0);
                rud_beiro(i, 7, 14, koz + 1, 0);
                rud_beiro(i, 8, 15, koz + 1, 0);
            }
        }
    }

    public void csomopont_beiro(int szekcio, int kezdocsp, int vegecsp) {
        boolean beiras = true;
        if (csomopontindex > 0) {
            for (int i = 1; i <= csomopontindex; i++) {
                if ((csomopont[i][1] == tempcsp[kezdocsp][0])
                        && (csomopont[i][2] == tempcsp[kezdocsp][1])
                        && (csomopont[i][3] == tempcsp[kezdocsp][2])) {
                    beiras = false;
                }
            }
        }
        if (beiras) {
            csomopontindex++;
            csomopont[csomopontindex][0] = szekcio;
            csomopont[csomopontindex][1] = tempcsp[kezdocsp][0];
            csomopont[csomopontindex][2] = tempcsp[kezdocsp][1];
            csomopont[csomopontindex][3] = tempcsp[kezdocsp][2];
        }
        beiras = true;
        for (int i = 1; i <= csomopontindex; i++) {
            if ((csomopont[i][1] == tempcsp[vegecsp][0])
                    && (csomopont[i][2] == tempcsp[vegecsp][1])
                    && (csomopont[i][3] == tempcsp[vegecsp][2])) {
                beiras = false;
            }
        }
        if (beiras) {
            csomopontindex++;
            csomopont[csomopontindex][0] = szekcio;
            csomopont[csomopontindex][1] = tempcsp[vegecsp][0];
            csomopont[csomopontindex][2] = tempcsp[vegecsp][1];
            csomopont[csomopontindex][3] = tempcsp[vegecsp][2];
        }
    }

    public void rud_beiro(int szekcio, int kezdocsp, int vegecsp, int koz, int tipus) {
        boolean beiras = true;
        int csp1, csp2;
        if (rudindex > 0) {
            csp1 = 0;
            csp2 = 0;
            //System.out.println("kx:"+tempcsp[kezdocsp][0]+" ky:"+ tempcsp[kezdocsp][1]+" kz:"+ tempcsp[kezdocsp][1]);
            for (int i = 1; i <= csomopontindex; i++) {
                //System.out.println("i:"+i+" x:"+csomopont[i][1]+" y:"+ csomopont[i][2]+" z:"+ csomopont[i][3]);                
                if ((csomopont[i][1] == tempcsp[kezdocsp][0])
                        && (csomopont[i][2] == tempcsp[kezdocsp][1])
                        && (csomopont[i][3] == tempcsp[kezdocsp][2])) {
                    csp1 = i;
                }
            }
            //System.out.println("vx:"+tempcsp[vegecsp][0]+" vy:"+ tempcsp[vegecsp][1]+" vz:"+ tempcsp[vegecsp][1]);
            for (int i = 1; i <= csomopontindex; i++) {
                if ((csomopont[i][1] == tempcsp[vegecsp][0])
                        && (csomopont[i][2] == tempcsp[vegecsp][1])
                        && (csomopont[i][3] == tempcsp[vegecsp][2])) {
                    csp2 = i;
                }
            }
            for (int i = 1; i < rudindex; i++) {
                if ((rud[i][1] == csp1) && (rud[i][2] == csp2)) {
                    beiras = false;
                }
            }
        } else {
            csp1 = 1;
            csp2 = 2;
        }
        //System.out.println("  csp1:" + csp1+ "  csp2:" + csp2+"  kezdocsp:" + kezdocsp + "   vegecsp:" + vegecsp + "  beiras:" + beiras);
        if (beiras) {
            rudindex++;
            rud[rudindex][0] = szekcio;
            rud[rudindex][1] = csp1;
            rud[rudindex][2] = csp2;
            rud[rudindex][5] = koz;
            rud[rudindex][6] = tipus;
            //System.out.println("rudindex:" + rudindex[1] +"  szekcio:" + rud[rudindex[1]][0] + " kezdocsp:" + rud[rudindex[1]][1] + "  vegecsp:" + rud[rudindex[1]][2]);
        }
    }

    public void pngrajz(int szekcio, int vastagvonal, int koz) {
        // 0-teljes rajz, a többi: szekciórajz
        int keret = 10;
        float arany;
        int xx1, xx2, yy1, yy2;
        int tetel;
        Stroke[] vonal = new BasicStroke[rudszam];
        Color[] szinek = new Color[rudszam];
        Font Courier16b = new Font("Courier New", Font.BOLD, 16);
        //racselemek();
        if (szekcio == 0) {
            g2.setColor(Color.white);
            g2.fillRect(0, 0, width, height);
        } else {
            g1.setColor(Color.white);
            g1.fillRect(0, 0, width, height);
        }
        kozeppont_szamolo(szekcio);
        tetel = szekcio;
        if (tetel > 0) {
            tetel = 1;
        }
        if (tetel < 0) {
            tetel = 0;
        }
        // A drótváz maximum és minimum értékei  [teljes(0)/szekcio(a többi)], [x(0),y(1),z(2)], [minimum(0)/maximum(1)] 
        // limitek[tetel][0][1] --> maxx
        // limitek[tetel][0][0] --> minx
        // limitek[tetel][1][1] --> maxy
        // limitek[tetel][1][0] --> miny

        if ((limitek[tetel][0][1] - limitek[tetel][0][0]) > (limitek[tetel][1][1] - limitek[tetel][1][0])) {
            arany = (float) (width - (2 * keret)) / (limitek[tetel][0][1] - limitek[tetel][0][0]);
        } else {
            arany = (float) (height - (2 * keret)) / (limitek[tetel][1][1] - limitek[tetel][1][0]);
        }
        // Az egérgörgetésnél a képarány megváltozik
        arany *= kepnagyitas[tetel];

        //System.out.println("Szekcio:"+szekcio+" arány:"+arany);
        //System.out.println("arany:" + arany + " height:" + height + " keret:" + keret + " maxx:" + limitek[tetel][0][1] + "  minx:" + limitek[tetel][0][0] + " maxy:" + limitek[tetel][1][1] + "  miny:" + limitek[tetel][1][0]);
        // A rajz    
        for (int i = 1; i <= rudindex; i++) {
            // Színbeállítás
            //System.out.println("i:"+i+" Szekcio:"+szekcio+"  Rud[3]:"+rud[i][3]+"  Rud[4]:"+rud[i][4]);
            szinek[i] = Color.BLACK;
            if (szekcio == 0) {
                // A teljes rajz    
                if (rud[i][0] != koz) {
                    // nincs megjelölés
                    if (rud[i][3] != 0) {
                        // van vastagsága a rúdnak, de a vonalvastagság 1-től kisebb
                        if ((rud[i][3] * arany) < 1) {
                            szinek[i] = new Color(256 - (int) (rud[i][3] * arany * 255), 256 - (int) (rud[i][3] * arany * 255), 256 - (int) (rud[i][3] * arany * 255));
                        }
                    }
                } else {
                    szinek[i] = Color.red;
                }
            } else {
                // A szekciórajz
                if (rud[i][5] == koz) {
                    szinek[i] = Color.BLUE;
                }
            }
            // A vonalvastagságok
            if (rud[i][3] == 0) {
                // nincs vastagsága a rúdnak
                vonal[i] = new BasicStroke(1);
            } else {
                if (vastagvonal == 0) {
                    vonal[i] = new BasicStroke(1);
                } else {
                    vonal[i] = new BasicStroke(rud[i][3] * arany);
                }
            }
        }
        for (int i = 1; i <= rudindex; i++) {
            if (szekcio == 0) {
                g2.setStroke(vonal[i]);
                g2.setColor(szinek[i]);
            } else {
                g1.setStroke(vonal[i]);
                g1.setColor(szinek[i]);
            }
            // Elölnézeti forgatás X-Y
            x = csomopont[rud[i][1]][1];
            y = csomopont[rud[i][1]][2];
            z = csomopont[rud[i][1]][3];
            if (szekcio != 0) {
                x -= limitek[1][0][0];
                y -= limitek[1][1][0];
                z -= limitek[1][2][0];
            }
            pontforgato(1, tetel);
            pontforgato(2, tetel);
            pontforgato(3, tetel);
            xx1 = (int) ((x - kozepx[tetel]) * arany) + width / 2;
            yy1 = height - ((int) ((y - kozepy[tetel]) * arany) + height / 2);
            xx1 += kepkozep[tetel][0];
            yy1 += kepkozep[tetel][1];
            x = csomopont[rud[i][2]][1];
            y = csomopont[rud[i][2]][2];
            z = csomopont[rud[i][2]][3];
            if (szekcio != 0) {
                x -= limitek[1][0][0];
                y -= limitek[1][1][0];
                z -= limitek[1][2][0];
            }
            pontforgato(1, tetel);
            pontforgato(2, tetel);
            pontforgato(3, tetel);
            xx2 = (int) ((x - kozepx[tetel]) * arany) + width / 2;
            yy2 = height - ((int) ((y - kozepy[tetel]) * arany) + height / 2);
            xx2 += kepkozep[tetel][0];
            yy2 += kepkozep[tetel][1];
            if (szekcio == 0) {
                g2.drawLine(xx1, yy1, xx2, yy2);
                if (vastagvonal == 0) {
                    if (i == 1) {
                        g2.setColor(Color.magenta);
                    }
                    g2.fillOval(xx1 - 2, yy1 - 2, 4, 4);
                }
            } else {
                if (rud[i][0] == szekcio) {
                    g1.drawLine(xx1, yy1, xx2, yy2);
                    if (vastagvonal == 0) {
                        if (i == 1) {
                            g1.setColor(Color.magenta);
                        }
                        g1.fillOval(xx1 - 2, yy1 - 2, 4, 4);
                    }
                }
            }
        }
        if (szekcio == 0) {
            g2.setColor(Color.black);
            g2.setFont(Courier16b);
            g2.drawString(nev, 5, 15);
        } else {
            g1.setColor(Color.black);
            g1.setFont(Courier16b);
            g1.drawString(nev, 5, 15);
        }
    }

    public void racsrudvastagsag() {
        int[] vastagsag = new int[9];
        if (szekcioszam > 0) {
            for (int j = 1; j <= szekcioszam; j++) {
                try {
                    Class.forName("com.mysql.jdbc.Driver").newInstance();
                    co = DriverManager.getConnection(Global.mysql_server, Global.mysql_user, Global.mysql_password);
                    st = co.createStatement();
                    // A rúdvastagságok
                    for (int i = 1; i <= 8; i++) {
                        vastagsag[i] = 0;
                        parancs = "select magassag from szelveny where nev = '" + rudnevek[j][i] + "';";
                        rs = st.executeQuery(parancs);
                        while (rs.next()) {
                            vastagsag[i] = rs.getInt("magassag");
                        }
                        rs.close();
                    }
                    st.close();
                } catch (InstantiationException e) {
                } catch (IllegalAccessException e) {
                } catch (ClassNotFoundException e) {
                } catch (SQLException e) {
                }
                // Az aktuális rúd vastagságának hozzárendelése az adott szekciónál
                for (int i = 1; i <= rudindex; i++) {
                    if (rud[i][0] == j) {
                        rud[i][3] = 0;
                        switch (rud[i][6]) {
                            case 2:
                                rud[i][3] = vastagsag[2];
                                break;
                            case 3:
                                rud[i][3] = vastagsag[3];
                                break;
                            case 4:
                                rud[i][3] = vastagsag[4];
                                break;
                            case 5:
                                rud[i][3] = vastagsag[5];
                                break;
                            case 6:
                                rud[i][3] = vastagsag[6];
                                break;
                            case 7:
                                rud[i][3] = vastagsag[7];
                                break;
                            default:
                                rud[i][3] = vastagsag[1];
                        }
                    }
                }
            }
        }
    }

    public float rudhossz(int kezdocsp, int vegecsp) {
        return Math.abs(csomopont[kezdocsp][1] - csomopont[vegecsp][1])
                + Math.abs(csomopont[kezdocsp][2] - csomopont[vegecsp][2])
                + Math.abs(csomopont[kezdocsp][3] - csomopont[vegecsp][3]);
    }

    public void csomopont_kereso(int pontsorszam) {
        // X,y,z koordinátákat keres, és az adat-ban lesz a legközelebbi csomópont
        float tavolsag = Float.MAX_VALUE;
        adat = 1;
        for (int k = 1; k < csomopontindex; k++) {
            if (k != pontsorszam) {
                if (rudhossz(pontsorszam,k) < tavolsag) {
                    adat = k;                    
                    tavolsag = rudhossz(pontsorszam,k); 
                }
            }
        }
        // System.out.println("  csatolopont:" + x1);
    }
}
