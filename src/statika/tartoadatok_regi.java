/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package statika;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.sql.*;
import javax.imageio.ImageIO;

/**
 *
 * @author duslaci
 */
public class tartoadatok_regi {

    int db = 20;                                    // A felrakható erők száma  -->> limit... az SQL-ben    
    String ProjektNev, megnevezes, szelveny;        // Ez a tartó megnevezése
    String parancs;                                 // A MySQL parancsok gyűjtőhelye
    float hossz, konzol1, konzol2;                  // A kéttámaszú tartó hossza és a konzolaik
    float fa, fb, f, ma, m;                         // A támaszerők, max_erő, konzolnál a támasznyomaték és a max nyomaték
    int tipus;                                      // A tartó típusa (Kéttámaszú-1, konzolos-2, kéttámaszú konzolos-3)
    int nyil_db;                                    // A koncentrált erőknek a száma
    float[][] ero = new float[db][2];               // Az aktuális erő koordinátája és értéke
    int akt_ero;                                    // Az aktuális erő indexe
    int megoszlo_db;                                // A megoszló terhek a száma
    float[][] megoszlo = new float[db][3];          // A megoszló terhek kezdő koordinátája, hossza, értéke                
    int akt_megoszlo;                               // Az aktuális megoszló teher indexe
    int nyomatek_db;                                // A nyomatékok a száma
    float[][] nyomatek = new float[db][2];          // A nyomatek koordinátája és értéke
    int akt_nyomatek;                               // Az aktuális nyomatek indexe
    int metszek = 500;                              // A metszékek száma (570-70...)
    int metszek1 = metszek + 1;                     // A metszékek száma a pline bezárásához (570-70 + 1...)
    float[][] metszekek = new float[metszek][14];   // Az aktuális pont nyíróerőértéke(0),nyomatéki értéke(1), A(2),Ix(3),Kx(4),Sx(5),v(6), Tau(7), Szigma(8), Szigma_ö(9), elfordulása(10), lehajlása(11) + Átmeneti tároló a munkamódszerhez (12)   
    int x1, x2;                                     // Ez jelzi majd a negatív erőt/nyomatékot (A rajzhoz: 0 vagy 1)
    int maxnyiroero_hely, maxnyomatek_hely;         // A Maximális nyíróerő és nyomaték helye (metszék index!!)
    int[] pontokx = new int[metszek1];               // Átmeneti tárolóhely az ábrák kijelzéséhez
    int[] pontoky = new int[metszek1];               // Átmeneti tárolóhely az ábrák kijelzéséhez (70-570)
    int[] nyilpontx = new int[10];                  // Átmeneti tárolóhely a nyilak kijelzéséhez
    int[] nyilponty = new int[10];                  // Átmeneti tárolóhely a nyilak kijelzéséhez 
    String filenev;                                 // A kimeneti PNG file neve
    // A MySQL-es változók
    static Connection co;
    static Statement st;
    static ResultSet rs;
    int UpdateQuery;

    public void letrehoz() {
        // Létrehozás és inicializálás
        nyil_db = 0;
        megoszlo_db = 0;
        nyomatek_db = 0;
        for (int i = 0; i < metszek; i++) {
            pontokx[i] = i + 70;
            for (int j = 0; j <= 13; j++) {
                metszekek[i][j] = 0;
            }
        }
        filenev = "./images/tartok/"+ProjektNev+"_"+megnevezes+".png";
        //System.out.println("Létrehozás/ adattörlés");
    }

    public void konvertalo(int k) {
        Float arany = 0f;
        Float maximum = 0f;
        int eltolas = 122;
        if (k == 1) {
            eltolas = 222;
        }
        if (k == 7) {
            eltolas = 322;
        }
        if (k == 8) {
            eltolas = 322;
        }
        if (k == 9) {
            eltolas = 322;
        }
        if (k == 10) {
            eltolas = 422;
        }
        if (k == 11) {
            eltolas = 422;
        }
        for (int i = 0; i < metszek; i++) {
            if (Math.abs(metszekek[i][k]) > maximum) {
                maximum = Math.abs(metszekek[i][k]);
            }
        }
        // 20 kN/cm a határérték
        if (k == 7) {
            maximum = 20f;
        }
        if (k == 8) {
            maximum = 20f;
        }
        if (k == 9) {
            maximum = 20f;
        }
        if (maximum != 0) {
            arany = 45 / maximum;
        }
        //System.out.println("arany:" + arany + " maximum:" + maximum);
        for (int i = 0; i < metszek; i++) {
            pontoky[i] = (int) (metszekek[i][k] * arany) + eltolas;
        }
        pontoky[metszek - 1] = eltolas;
    }

    public void beolvas() {
        // Az adatok beolvasása az adatbázisból -->> //localhost/statika 
        letrehoz();  // Inicializálás, nullázás
        try {
            Class.forName("com.mysql.jdbc.Driver").newInstance();
            co = DriverManager.getConnection("jdbc:mysql://localhost/statika", "root", "");
            st = co.createStatement();
            parancs = "SELECT hossz,szelveny,konzol1,konzol2,tipus FROM tartok WHERE tartonev = '" + megnevezes + "' limit 0,20";
            rs = st.executeQuery(parancs);
            while (rs.next()) {
                hossz = rs.getFloat(1);
                szelveny = rs.getString(2);
                konzol1 = rs.getFloat(3);
                konzol2 = rs.getFloat(4);
                tipus = rs.getInt(5);
                //System.out.println(rs.getFloat(1)+", "+rs.getString(2)+", "+rs.getFloat(3)+", "+rs.getFloat(4));
            }
            rs.close();
            st.close();
        } catch (InstantiationException e) {
        } catch (IllegalAccessException e) {
        } catch (ClassNotFoundException e) {
        } catch (SQLException e) {
        }
        // Az aktív tartó beállítása
        try {
            Class.forName("com.mysql.jdbc.Driver").newInstance();
            co = DriverManager.getConnection("jdbc:mysql://localhost/statika", "root", "");
            st = co.createStatement();
            // Az aktív tarto kikapcsolása
            parancs = "update tartok set aktiv = '0' where aktiv='1';";
            //rs = st.executeQuery(parancs);             
            UpdateQuery = st.executeUpdate(parancs);
            //System.out.println("SQL parancs: " + parancs);
            rs.close();
            st.close();
        } catch (InstantiationException e) {
        } catch (IllegalAccessException e) {
        } catch (ClassNotFoundException e) {
        } catch (SQLException e) {
        }
        try {
            Class.forName("com.mysql.jdbc.Driver").newInstance();
            co = DriverManager.getConnection("jdbc:mysql://localhost/statika", "root", "");
            st = co.createStatement();
            // Az aktív tarto bekapcsolása
            parancs = "update tartok set aktiv = '1' where tartonev='";
            parancs = parancs + megnevezes + "';";
            //rs = st.executeQuery(parancs);             
            UpdateQuery = st.executeUpdate(parancs);
            //System.out.println("SQL parancs: " + parancs);
            rs.close();
            st.close();
        } catch (InstantiationException e) {
        } catch (IllegalAccessException e) {
        } catch (ClassNotFoundException e) {
        } catch (SQLException e) {
        }
        //nyil_db = -1;
        // A koncentrált erők
        try {
            Class.forName("com.mysql.jdbc.Driver").newInstance();
            co = DriverManager.getConnection("jdbc:mysql://localhost/statika", "root", "");
            st = co.createStatement();
            parancs = "SELECT hely,ertek FROM `tartoerok` WHERE `tartonev` = '" + megnevezes + "' and jelleg ='1'";
            rs = st.executeQuery(parancs);
            while (rs.next()) {
                nyil_db++;
                ero[nyil_db][0] = rs.getFloat(1);
                ero[nyil_db][1] = rs.getFloat(2);
                //System.out.println(rs.getFloat(1)+", "+rs.getFloat(2)+", "+rs.getFloat(3));                 
            }
            rs.close();
            st.close();
        } catch (InstantiationException e) {
        } catch (IllegalAccessException e) {
        } catch (ClassNotFoundException e) {
        } catch (SQLException e) {
        }
        // A megoszló terhek
        //megoszlo_db = -1;
        try {
            Class.forName("com.mysql.jdbc.Driver").newInstance();
            co = DriverManager.getConnection("jdbc:mysql://localhost/statika", "root", "");
            st = co.createStatement();
            parancs = "SELECT hely,hossz,ertek FROM `tartoerok` WHERE `tartonev` = '" + megnevezes + "' and jelleg ='2'";
            rs = st.executeQuery(parancs);
            while (rs.next()) {
                megoszlo_db++;
                megoszlo[megoszlo_db][0] = rs.getFloat(1);
                megoszlo[megoszlo_db][1] = rs.getFloat(2);
                megoszlo[megoszlo_db][2] = rs.getFloat(3);
                //System.out.println(rs.getFloat(1)+", "+rs.getFloat(2)+", "+rs.getFloat(3));                 
            }
            rs.close();
            st.close();
        } catch (InstantiationException e) {
        } catch (IllegalAccessException e) {
        } catch (ClassNotFoundException e) {
        } catch (SQLException e) {
        }
        // A nyomatékok
        //nyomatek_db = -1;
        try {
            Class.forName("com.mysql.jdbc.Driver").newInstance();
            co = DriverManager.getConnection("jdbc:mysql://localhost/statika", "root", "");
            st = co.createStatement();
            parancs = "SELECT hely,ertek FROM `tartoerok` WHERE `tartonev` = '" + megnevezes + "' and jelleg ='3'";
            rs = st.executeQuery(parancs);
            while (rs.next()) {
                nyomatek_db++;
                nyomatek[nyomatek_db][0] = rs.getFloat(1);
                nyomatek[nyomatek_db][1] = rs.getFloat(2);
                //System.out.println(rs.getFloat(1)+", "+rs.getFloat(2)+", "+rs.getFloat(3));                 
            }
            rs.close();
            st.close();
        } catch (InstantiationException e) {
        } catch (IllegalAccessException e) {
        } catch (ClassNotFoundException e) {
        } catch (SQLException e) {
        }
        // A szelvényadatok feltöltése
        Float Ix = 0f;
        Float Sx = 0f;
        Float A = 0f;
        Float Kx = 0f;
        Float v = 0f;
        try {
            // A tartó homogénként van feltételezve
            Class.forName("com.mysql.jdbc.Driver").newInstance();
            co = DriverManager.getConnection("jdbc:mysql://localhost/statika", "root", "");
            st = co.createStatement();
            parancs = "SELECT A,Ix,Kx,Sx,v / 100 FROM szelveny WHERE nev = '" + szelveny + "' ";
            //System.out.println(parancs);
            rs = st.executeQuery(parancs);
            while (rs.next()) {
                A = rs.getFloat(1);
                Ix = rs.getFloat(2);
                Kx = rs.getFloat(3);
                Sx = rs.getFloat(4);
                v = rs.getFloat(5);
                //System.out.println(rs.getFloat(1) + ", " + rs.getString(2) + ", " + rs.getFloat(3) + ", " + rs.getFloat(4));
            }
            rs.close();
            st.close();
            for (int i = 0; i < metszek; i++) {
                metszekek[i][2] = A;
                metszekek[i][3] = Ix;
                metszekek[i][4] = Kx;
                metszekek[i][5] = Sx;
                metszekek[i][6] = v;
            }
        } catch (InstantiationException e) {
        } catch (IllegalAccessException e) {
        } catch (ClassNotFoundException e) {
        } catch (SQLException e) {
        }
        //System.out.println("Adatbeolvasás...");
    }

    public void kiszamol() {
        float metszet, nyiras, eropont;
        // Támaszerők és befogási nyomatékok kiszámolása
        fa = 0;
        fb = 0;
        ma = 0;
        // támaszerők koncentrált erőkből
        if (nyil_db != 0) {
            for (int i = 1; i <= nyil_db; i++) {
                eropont = ero[i][0] - konzol1;
                System.out.println(" eropont:" + eropont);
                if (ero[i][0] < konzol1) {
                    eropont = (konzol1 - ero[i][0]) * -1;
                }
                // MA nyomatéka /FB támaszereje
                if (tipus == 2) {  // konzolos tartó
                    ma += ero[i][0] * ero[i][1];
                    System.out.println(" ma:" + ma + " ero:" + ero[i][1]);
                } else {
                    fb += (eropont * ero[i][1]) / hossz;
                }
                // FA támaszereje
                if (tipus == 2) {  // konzolos tartó
                    fa += ero[i][1];
                } else {
                    fa += ero[i][1] - ((eropont * ero[i][1]) / hossz);
                }
            }
        }
        // támaszerők megoszló terhelésekből
        if (megoszlo_db != 0) {
            for (int i = 1; i <= megoszlo_db; i++) {
                /*
                 eropont = ((megoszlo[i][0] * 2 + megoszlo[i][1]) / 2) - konzol1;
                 if (((megoszlo[i][0] + megoszlo[i][1]) / 2) < konzol1) {
                 eropont = (konzol1 - ((megoszlo[i][0] + megoszlo[i][1]) / 2)) * -1;
                 }*/

                eropont = megoszlo[i][0] + (megoszlo[i][1] / 2);
                if ((megoszlo[i][0] + (megoszlo[i][1] / 2)) < konzol1) {
                    eropont = konzol1 - (megoszlo[i][0] + (megoszlo[i][1] / 2));
                }
                //System.out.println(" Erőpont:" + eropont);
                // MA nyomatéka /FB támaszereje
                if (tipus == 2) {  // konzolos tartó
                    ma += (megoszlo[i][0] + (megoszlo[i][1] / 2)) * megoszlo[i][2] * megoszlo[i][1];
                } else {
                    fb += ((eropont - konzol1) * megoszlo[i][2] * megoszlo[i][1]) / hossz;
                }
                // FA támaszereje
                if (tipus == 2) {  // konzolos tartó
                    fa += megoszlo[i][2] * megoszlo[i][1];
                } else {
                    fa += megoszlo[i][2] * megoszlo[i][1] - (((eropont - konzol1) * megoszlo[i][2] * megoszlo[i][1]) / hossz);
                }
            }
        }
        // Támaszerők a nyomatékokból
        if (nyomatek_db != 0) {
            for (int i = 1; i <= megoszlo_db; i++) {
                if (tipus == 2) {
                    ma += nyomatek[i][0];
                } else {
                    fa += nyomatek[i][0] / hossz;
                    fb -= nyomatek[i][0] / hossz;
                }
            }
        }

        // A nyomatéki metszékek kiszámolása
        if ((nyil_db + megoszlo_db + nyomatek_db) != 0) {
            // A támaszerők és nyíróerőmetszékek a koncentrált erőkből            
            if (nyil_db != 0) {
                for (int i = 1; i <= nyil_db; i++) {
                    for (int j = 0; j < metszek; j++) {
                        metszet = (j * (hossz + konzol1 + konzol2)) / metszek;
                        if (metszet > ero[i][0]) {
                            metszekek[j][0] += ero[i][1];
                        }
                    }
                }
            }
            // A támaszerők és nyíróerőmetszékek a megoszló terhelésekből
            if (megoszlo_db != 0) {
                for (int i = 1; i <= megoszlo_db; i++) {
                    // PHP : $nyiras = ($hosz[$i] * $ertek[$i]) / ($metszekszam * ($hosz[$i] / $tartohossz));
                    nyiras = ((megoszlo[i][1] * megoszlo[i][2]) / megoszlo[i][1]) * ((hossz + konzol1 + konzol2) / metszek);
                    //System.out.println("nyiras:" + nyiras);
                    int k;
                    for (int j = 0; j < metszek; j++) {
                        metszet = (j * (hossz + konzol1 + konzol2)) / metszek;
                        // PHP :if (($metszet >= $hely[$i]) and ($metszet <= ($hely[$i] + $hosz[$i]))) { $nyiroero[$j] += $j * $nyiras; }                        
                        if ((metszet >= megoszlo[i][0]) && (metszet <= (megoszlo[i][0] + megoszlo[i][1]))) {
                            //k = j - (int) megoszlo[i][0];      
                            k = (int) (metszet - megoszlo[i][0]);
                            //System.out.println("metszek:" + k + " nyiras:" + nyiras);
                            metszekek[j][0] += k * nyiras * (metszek / (hossz + konzol1 + konzol2));
                        }
                        // PHP : if ($metszet > ($hely[$i] + $hosz[$i])) {$nyiroero[$j] += $hosz[$i] * $ertek[$i]; }
                        if (metszet > (megoszlo[i][0] + megoszlo[i][1])) {
                            metszekek[j][0] += megoszlo[i][1] * megoszlo[i][2];
                        }
                    }
                }
            }
            for (int j = 0; j < metszek; j++) {
                metszet = (j * (hossz + konzol1 + konzol2)) / metszek;
                //System.out.println(" Metszet:" + metszet);                                
                if (tipus == 2) {
                    metszekek[j][0] -= fa;
                } else {
                    if (metszet > konzol1) {
                        metszekek[j][0] -= fa;
                        //System.out.println(" j:" + j+ "  Metszek:"+metszekek[j][0]);
                    }
                }
                if (metszet > konzol1 + hossz) {
                    metszekek[j][0] -= fb;
                }
            }
            // A Maximális nyíróerő meghatározása
            f = 0;
            x1 = 0;  // Ez jelzi majd a negatív erőt
            for (int i = 0; i < metszek; i++) {
                if (metszekek[i][0] > f) {
                    f = metszekek[i][0];
                    maxnyiroero_hely = i;
                    x1 = 1;
                }
            }
            for (int i = 0; i < metszek; i++) {
                if (-metszekek[i][0] > f) {
                    f = -metszekek[i][0];
                    maxnyiroero_hely = i;
                    x1 = 0;
                }
            }
            // A nyomatéki metszékek
            if ((nyil_db > 0) || (megoszlo_db > 0)) {
                metszekek[0][1] = ma;
                for (int i = 1; i < metszek; i++) {
                    //metszekek[i][1] = metszekek[i - 1][1] - metszekek[i][0] ;
                    metszekek[i][1] = metszekek[i - 1][1] - (metszekek[i][0] * ((hossz + konzol1 + konzol2)) / metszek);
                }
                //System.out.println(" arany:" + (metszek / (hossz + konzol1 + konzol2)));
                //metszekek[0][0] = 0;               // A zárt pline rajzolásához kell
                //metszekek[0][1] = 0;               // A zárt pline rajzolásához kell
                // A Maximális nyomaték meghatározása
                m = 0;
                x2 = 0;  // Ez jelzi majd a negatív nyomatékot
                maxnyomatek_hely = 0;
                for (int i = 0; i < metszek; i++) {
                    if (metszekek[i][1] > m) {
                        m = metszekek[i][1];
                        maxnyomatek_hely = i;
                        x2 = 1;
                    }
                }
                for (int i = 0; i < metszek; i++) {
                    if (-metszekek[i][1] > m) {
                        m = -metszekek[i][1];
                        maxnyomatek_hely = i;
                        x2 = 0;
                    }
                }
            }
            // Tarhelési értékek Tau, Szigma
            if (metszekek[maxnyiroero_hely][3] > 0) {
                // normálfeszültség hajlításból                
                for (int i = 0; i < metszek; i++) {
                    metszekek[i][7] = Math.abs(metszekek[i][1]) / metszekek[i][4];  // $szigma[$j] = abs($nyomatek[$j]) / $kx;
                }

                // Csúsztatófeszültség
                for (int i = 0; i < metszek; i++) {
                    if (metszekek[i][6] > 0) {
                        metszekek[i][8] = (metszekek[i][0] * metszekek[i][5]) / (metszekek[i][3] * metszekek[i][6]);  // $tau[$j] = ($nyiroero[$j] * $sx) / ($Ix * $c);
                    }
                }

                // Összetett feszültség                
                for (int i = 0; i < metszek; i++) {
                    metszekek[i][9] = Float.parseFloat(String.valueOf(Math.sqrt(Double.parseDouble(String.valueOf(metszekek[i][7] * metszekek[i][7] + 3 * metszekek[i][8] * metszekek[i][8])))));  // $szigma[$j] = abs($nyomatek[$j]) / $kx;
                }

                // A maximális értékek és helyének meghatározása

            }
            // Lehajlási, elfordulási értékek
        }
    }

    public void pngrajz() {
        String szoveg;
        int bazis;
        float arany;
        try {
            int width = 640, height = 480;

            // TYPE_INT_ARGB specifies the image format: 8-bit RGBA packed into integer pixels
            BufferedImage bi = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

            Graphics2D g = bi.createGraphics();

            Font Courier10 = new Font("Courier New", Font.PLAIN, 10);
            Font Courier10b = new Font("Courier New", Font.BOLD, 10);
            Font Courier12b = new Font("Courier New", Font.BOLD, 12);
            Font Courier16b = new Font("Courier New", Font.BOLD, 16);
            g.setColor(Color.white);
            g.fillRect(0, 0, width, height);
            // A kéttámaszú tartó ábrája                
            g.setColor(Color.black);

            // A gerenda
            bazis = 42;
            g.drawLine(70, bazis, 570, bazis);
            g.drawLine(70, bazis - 1, 570, bazis - 1);
            arany = 500 / (hossz + konzol1 + konzol2);
            // A támaszok és támaszerők, nyomatékok ábrái
            if (tipus == 2) {
                // Konzolos tartó
                g.drawLine(70, 32, 70, 52);
                g.drawLine(71, 32, 71, 52);
                g.setFont(Courier16b);
                g.drawString("A", 50, 70);
                bazis = 70;
                if (fa > 0) {
                    nyilpontx[0] = bazis;
                    nyilponty[0] = 68;
                    nyilpontx[1] = bazis;
                    nyilponty[1] = 64;
                    nyilpontx[2] = bazis + 5;
                    nyilponty[2] = 64;
                    nyilpontx[3] = bazis;
                    nyilponty[3] = 53;
                    nyilpontx[4] = bazis - 5;
                    nyilponty[4] = 64;
                    nyilpontx[5] = bazis - 1;
                    nyilponty[5] = 64;
                    nyilpontx[6] = bazis - 1;
                    nyilponty[6] = 68;
                    nyilpontx[7] = bazis;
                    nyilponty[7] = 68;
                } else {
                    nyilpontx[0] = bazis;
                    nyilponty[0] = 53;
                    nyilpontx[1] = bazis;
                    nyilponty[1] = 57;
                    nyilpontx[2] = bazis + 5;
                    nyilponty[2] = 57;
                    nyilpontx[3] = bazis;
                    nyilponty[3] = 68;
                    nyilpontx[4] = bazis - 5;
                    nyilponty[4] = 57;
                    nyilpontx[5] = bazis - 1;
                    nyilponty[5] = 57;
                    nyilpontx[6] = bazis - 1;
                    nyilponty[6] = 53;
                    nyilpontx[7] = bazis;
                    nyilponty[7] = 53;
                }
                if (fa != 0) {
                    g.fillPolygon(nyilpontx, nyilponty, 8);
                }
                g.setFont(Courier10b);
                szoveg = String.format("%.2f", fa) + " kN";
                g.drawString(szoveg, bazis + 10, 62);
                g.setFont(Courier10);
                szoveg = konzol1 + " cm";
                g.drawString(szoveg, 250, 70);
                // A befogási nyomaték            
                if (ma != 0) {
                    g.setFont(Courier10b);
                    szoveg = String.format("%.2f", ma) + " kNcm";
                    if (ma > 0) {
                        g.drawArc(50, 22, 40, 40, 30, 150);
                        g.drawArc(50, 22, 39, 39, 30, 150);
                        nyilpontx[0] = 50;
                        nyilponty[0] = 42;
                        nyilpontx[1] = 49;
                        nyilponty[1] = 31;
                        nyilpontx[2] = 55;
                        nyilponty[2] = 32;
                        g.drawString(szoveg, 23, 20);
                    } else {
                        g.drawArc(50, 22, 40, 40, 180, 150);
                        g.drawArc(50, 22, 39, 39, 180, 150);
                        nyilpontx[0] = 50;
                        nyilponty[0] = 42;
                        nyilpontx[1] = 49;
                        nyilponty[1] = 52;
                        nyilpontx[2] = 55;
                        nyilponty[2] = 51;
                        g.drawString(szoveg, 1, 40);
                    }
                    g.fillPolygon(nyilpontx, nyilponty, 3);
                }
            } else {
                // Mozgó talp
                bazis = 70;
                if ((int) (konzol1) > 0) {
                    bazis = 70 + (int) (arany * konzol1);
                    g.setFont(Courier10);
                    szoveg = konzol1 + " cm";
                    g.drawString(szoveg, 50 + (int) (arany * konzol1 / 2), 70);
                }
                g.drawLine(bazis, 42, bazis - 5, 47);
                g.drawLine(bazis, 42, bazis + 5, 47);
                g.drawLine(bazis - 5, 47, bazis + 5, 47);
                g.drawLine(bazis - 10, 51, bazis + 10, 51);
                g.drawLine(bazis - 10, 50, bazis + 10, 50);
                g.setFont(Courier16b);
                g.drawString("A", bazis - 25, 60);
                if (fa > 0) {
                    pontokx[0] = bazis;
                    pontoky[0] = 68;
                    pontokx[1] = bazis;
                    pontoky[1] = 64;
                    pontokx[2] = bazis + 5;
                    pontoky[2] = 64;
                    pontokx[3] = bazis;
                    pontoky[3] = 53;
                    pontokx[4] = bazis - 5;
                    pontoky[4] = 64;
                    pontokx[5] = bazis - 1;
                    pontoky[5] = 64;
                    pontokx[6] = bazis - 1;
                    pontoky[6] = 68;
                    pontokx[7] = bazis;
                    pontoky[7] = 68;
                } else {
                    pontokx[0] = bazis;
                    pontoky[0] = 53;
                    pontokx[1] = bazis;
                    pontoky[1] = 57;
                    pontokx[2] = bazis + 5;
                    pontoky[2] = 57;
                    pontokx[3] = bazis;
                    pontoky[3] = 68;
                    pontokx[4] = bazis - 5;
                    pontoky[4] = 57;
                    pontokx[5] = bazis - 1;
                    pontoky[5] = 57;
                    pontokx[6] = bazis - 1;
                    pontoky[6] = 53;
                    pontokx[7] = bazis;
                    pontoky[7] = 53;
                }
                g.fillPolygon(pontokx, pontoky, 8);
                g.setFont(Courier10b);
                szoveg = String.format("%.2f", fa) + " kN";
                g.drawString(szoveg, bazis + 10, 62);
                // Fix talp    
                bazis = 570;
                if ((int) (konzol2) > 0) {
                    bazis = 70 + (int) (arany * (konzol1 + hossz));
                    g.setFont(Courier10);
                    szoveg = konzol2 + " cm";
                    g.drawString(szoveg, 50 + (int) (arany * (hossz + konzol1)) + (int) (arany * konzol2 / 2), 70);
                }
                g.setFont(Courier16b);
                g.drawLine(bazis, 42, bazis - 5, 49);
                g.drawLine(bazis, 42, bazis + 5, 49);
                g.drawLine(bazis - 5, 49, bazis + 5, 49);
                g.drawLine(bazis - 10, 49, bazis + 10, 49);
                g.drawLine(bazis - 10, 48, bazis + 10, 48);
                g.drawString("B", bazis - 25, 60);
                if (fb > 0) {
                    pontokx[0] = bazis;
                    pontoky[0] = 68;
                    pontokx[1] = bazis;
                    pontoky[1] = 64;
                    pontokx[2] = bazis + 5;
                    pontoky[2] = 64;
                    pontokx[3] = bazis;
                    pontoky[3] = 53;
                    pontokx[4] = bazis - 5;
                    pontoky[4] = 64;
                    pontokx[5] = bazis - 1;
                    pontoky[5] = 64;
                    pontokx[6] = bazis - 1;
                    pontoky[6] = 68;
                    pontokx[7] = bazis;
                    pontoky[7] = 68;
                } else {
                    pontokx[0] = bazis;
                    pontoky[0] = 53;
                    pontokx[1] = bazis;
                    pontoky[1] = 57;
                    pontokx[2] = bazis + 5;
                    pontoky[2] = 57;
                    pontokx[3] = bazis;
                    pontoky[3] = 68;
                    pontokx[4] = bazis - 5;
                    pontoky[4] = 57;
                    pontokx[5] = bazis - 1;
                    pontoky[5] = 57;
                    pontokx[6] = bazis - 1;
                    pontoky[6] = 53;
                    pontokx[7] = bazis;
                    pontoky[7] = 53;
                }
                g.fillPolygon(pontokx, pontoky, 8);
                g.setFont(Courier10b);
                szoveg = String.format("%.2f", fb) + " kN";
                g.drawString(szoveg, bazis + 10, 62);
                g.setFont(Courier10);
                szoveg = hossz + " cm";
                g.drawString(szoveg, 50 + (int) (arany * (konzol1)) + (int) (arany * hossz / 2), 70);
            }
            // A bázisvonalak
            g.setColor(Color.LIGHT_GRAY);
            bazis = 72;
            g.drawLine(68, bazis, 572, bazis);
            bazis = 122;
            g.drawLine(68, bazis, 572, bazis);
            bazis = 222;
            g.drawLine(68, bazis, 572, bazis);
            bazis = 322;
            g.drawLine(68, bazis, 572, bazis);
            bazis = 422;
            g.drawLine(68, bazis, 572, bazis);
            // Segédvonalak
            bazis = 70;
            g.drawLine(bazis, 55, bazis, 478);
            bazis = 570;
            g.drawLine(bazis, 55, bazis, 478);
            if ((int) (konzol1) > 0) {
                bazis = 70 + (int) (arany * konzol1);
                g.drawLine(bazis, 55, bazis, 478);
            }
            if ((int) (konzol2) > 0) {
                bazis = 70 + (int) (arany * (konzol1 + hossz));
                g.drawLine(bazis, 55, bazis, 478);
            }
            // méretvonal
            bazis = 72;
            g.setColor(Color.black);
            g.drawLine(67, 75, 73, 69);
            g.drawLine(567, 75, 573, 69);
            if ((int) (konzol1) > 0) {
                bazis = 70 + (int) (arany * konzol1);
                g.drawLine(bazis - 3, 75, bazis + 3, 69);
            }
            if ((int) (konzol2) > 0) {
                bazis = 70 + (int) (arany * (konzol1 + hossz));
                g.drawLine(bazis - 3, 75, bazis + 3, 69);
            }
            // A szövegek        
            g.setFont(Courier10);
            g.drawString("+", 60, 120);
            g.drawString("+", 60, 220);
            g.drawString("+", 60, 320);
            g.drawString("+", 60, 420);
            g.drawString("-", 60, 130);
            g.drawString("-", 60, 230);
            g.drawString("-", 60, 330);
            g.drawString("-", 60, 430);
            g.setFont(Courier12b);
            g.drawString("T", 45, 124);
            g.drawString("M", 45, 224);
            g.drawString("kN", 575, 124);
            g.drawString("kNcm", 575, 224);
            g.drawString("kN/cm2", 575, 324);
            g.drawString("16", 575, 288);
            g.drawString("20", 575, 279);
            g.drawString("16", 575, 360);
            g.drawString("20", 575, 369);
            g.setColor(Color.ORANGE);
            g.drawString("Tau", 5, 306);
            g.setColor(Color.red);
            g.drawString("Szigma", 5, 322);
            g.setColor(Color.GREEN);
            g.drawString("Szigma_Ö", 5, 338);
            g.setColor(Color.darkGray);
            g.drawString("Elmozdulas", 5, 412);
            g.drawString("cm", 575, 412);
            g.setColor(Color.cyan);
            g.drawString("Szögfordulás", 5, 432);
            g.drawString("fok", 575, 432);
            // A veszélyes határérték-sáv
            g.setColor(Color.pink);
            for (int i = 1; i <= 9; i++) {
                g.drawLine(70, 276 + i, 570, 276 + i);
                g.drawLine(70, 357 + i, 570, 357 + i);
            }
            g.setColor(Color.black);
            // A koncentrált erő nyilainak kijelzése
            g.setFont(Courier10);
            bazis = 42;
            if (nyil_db > 0) {
                g.setColor(Color.blue);
                for (int i = 1; i <= nyil_db; i++) {
                    g.drawLine((int) (ero[i][0] * arany) + 70, bazis - 18, (int) (ero[i][0] * arany) + 70, bazis - 2);
                    for (int k = 1; k <= 10; k++) {
                        if (ero[i][1] > 0) {
                            g.drawLine((int) (ero[i][0] * arany) + 70, bazis - 2, (int) (ero[i][0] * arany) + 64 + k, bazis - 15);
                        } else {
                            g.drawLine((int) (ero[i][0] * arany) + 70, bazis - 18, (int) (ero[i][0] * arany) + 64 + k, bazis - 7);
                        }
                    }
                    szoveg = String.valueOf(ero[i][1]) + " kN";
                    g.drawString(szoveg, (int) (ero[i][0] * arany) + 77, bazis - 12);
                }
            }

            // A megoszló terhelések kijelzése
            bazis = 22;
            if (megoszlo_db > 0) {
                g.setColor(Color.MAGENTA);
                for (int i = 1; i <= megoszlo_db; i++) {
                    //System.out.println("megoszlo(i): " + megoszlo[i][0] + "  " + megoszlo[i][1] + " " + megoszlo[i][2]);
                    g.drawRect((int) (megoszlo[i][0] * arany) + 70, bazis - 20, (int) (megoszlo[i][1] * arany), bazis - 4);
                    g.drawLine((int) (megoszlo[i][0] * arany) + 75, bazis - 4, (int) (megoszlo[i][0] * arany) + 75, bazis - 18);
                    for (int k = 1; k <= 8; k++) {
                        if (megoszlo[i][2] > 0) {
                            g.drawLine((int) (megoszlo[i][0] * arany) + 75, bazis - 4, (int) (megoszlo[i][0] * arany) + 71 + k, bazis - 14);
                        } else {
                            g.drawLine((int) (megoszlo[i][0] * arany) + 75, bazis - 18, (int) (megoszlo[i][0] * arany) + 71 + k, bazis - 8);
                        }
                    }
                    szoveg = "q=" + String.valueOf((megoszlo[i][2])) + " kN/cm";
                    g.drawString(szoveg, (int) (megoszlo[i][0] * arany) + 80, bazis - 6);
                }
            }
            // A grafikonok
            // A nyíróerő ábra
            //System.out.println(" fa:" + fa + "; fb:" + fb + ";  f:" + f + ";  Ma:" + ma);
            konvertalo(0);

            for (int k1 = 0; k1 < metszek; k1++) {
                System.out.print("Metszék:" + k1 + " ");
                for (int k2 = 0; k2 < 2; k2++) {
                    if (k2 == 0) {
                        System.out.print("T:");
                    }
                    if (k2 == 1) {
                        System.out.print("M:");
                    }
                    System.out.print(metszekek[k1][k2]);
                    if (k2 == 0) {
                        System.out.print(" kN, ");
                    }
                    if (k2 == 1) {
                        System.out.print(" kNcm, ");
                    }
                }
                //System.out.println(" X[" + k1 + "]:" + pontokx[k1] + ";   Y[" + k1 + "]: " + pontoky[k1]);
                System.out.println();
            }

            g.setColor(Color.MAGENTA);
            g.fillPolygon(pontokx, pontoky, metszek);            
            g.setColor(Color.BLACK);
            g.drawPolygon(pontokx, pontoky, metszek);
            // A nyomatéki ábra
            konvertalo(1);
            g.setColor(Color.yellow);
            g.fillPolygon(pontokx, pontoky, metszek);
            g.setColor(Color.BLACK);
            g.drawPolygon(pontokx, pontoky, metszek);
            // A csúsztató feszültség
            konvertalo(7);
            g.setColor(Color.orange);
            g.drawPolygon(pontokx, pontoky, metszek);
            // A normálfeszültség
            konvertalo(8);
            g.setColor(Color.red);
            g.drawPolygon(pontokx, pontoky, metszek);
            // Az összetett feszültség
            konvertalo(9);
            g.setColor(Color.green);
            g.drawPolygon(pontokx, pontoky, metszek);
            // Az elmozdulás értéke
            konvertalo(10);
            g.setColor(Color.darkGray);
            g.drawPolygon(pontokx, pontoky, metszek);
            // Az elfordulás értéke
            konvertalo(11);
            g.setColor(Color.cyan);
            g.drawPolygon(pontokx, pontoky, metszek);            
            ImageIO.write(bi, "PNG", new File(filenev));

        } catch (IOException ie) {
            ie.printStackTrace();
        }
    }
}
