/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package racsok;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.sql.*;

/**
 *
 * @author duslaci
 */
public class szerkezet {

    int csomopontdb = 1000;                         // A csomópontok maximális száma
    int ruddb = 1000;                               // A csomópontok maximális száma
    int maxszekcio = 20;                            // A rácsszerkezet maximális szekciószáma
    String parancs;                                 // A MySQL parancsok gyűjtőhelye   
    static Connection co;
    static Statement st;
    static ResultSet rs;
    int csomopontindex;                             // A csomópontok aktuális indexe
    int rudindex;                                   // A rudak aktuális indexe
    String nev;                                     // Ez lesz a tartó neve
    int[][] csomopont = new int[csomopontdb][4];    // A csomópont koordinátája, X,Y,Z (szélesség,magasság,mélység))
    int[][] koord = new int[100][4];                // Az ideiglenes rácselem koordinátái, X,Y,Z (szélesség,magasság,mélység))  (1-78)
    int[][] rudak = new int[ruddb][4];              // A rudak kezdő és végső csomópontja és tipusa 
    int[][] szekcio = new int[maxszekcio][15];      // A szekciószám [0],magasság(1),alsoszelesseg_xy(2),felsoszelesseg_xy(3),alsoszelesseg_yz(4),felsoszelesseg_yz(5),
                                                    // irány(V-1/H-2)(6),X(7),Y(8),Z(9),a teljes kinyúlás(10),eltolásxy(11),eltolásyz(12),keresés(13),tükrözés(14)
    int szekcioszam;                                // A tartó szekcióinak száma    
    int[] koz = new int[100];                       // A szekción belüli közök magassága
    int[][] felepites = new int[100][10];           // A szekción belüli részek elemeinek felsorolása
    public int magassag, tartohossz;
    int alsoszelesseg_xy;
    int alsoszelesseg_yz;
    int felsoszelesseg_xy;
    int felsoszelesseg_yz;                           // A szerkezet külső koordinátái
    //double szog_xy, szog_yz;                       // A tartórudak dőlésszöge
    int kozok;                                       // Az ismétlődő elemek száma
    int racskoz;                                     // Függőleges elemnél az aktuális köz magassága (racskoz1=0), konzolnál a kezdő és a végső magasság 
    int x1, y1, z1;                                  // Az új rácselemeket ehhez viszonyítva van meghatározva
    int alsoszel_xy, alsoszel_yz;                    // Az új rácselem méretei
    int felsoszel_xy, felsoszel_yz;                  // Az új rácselem méretei
    float data;
    int adat;                                        // munkaváltozó

    public void letrehoz(String tartonev) {
        // Létrehozás és inicializálás                
        szekcioszam = 0;
        csomopontindex = 0;
        rudindex = 0;
        for (int i = 1; i < csomopontdb; i++) {
            for (int j = 1; j <= 3; j++) {
                csomopont[i][j] = 0;
            }
            for (int j = 1; j <= 3; j++) {
                rudak[i][j] = 0;
            }
        }
        /*szog_xy = Math.atan2((alsoszelesseg_xy - felsoszelesseg_xy) / 2, magassag) * 180 / Math.PI;
         szog_yz = Math.atan2((alsoszelesseg_yz - felsoszelesseg_yz) / 2, magassag) * 180 / Math.PI;
         System.out.println("Létrehozás/ adattörlés SzögXY:" + szog_xy + "fok,   SzögYZ:" + szog_yz + "fok");*/
        // A Mysql-es adatbeolvasás
        parancs = "select * from racsalap where nev = '" + tartonev + "' ";
        //System.out.println(parancs);
        try {
            Class.forName("com.mysql.jdbc.Driver").newInstance();
            co = DriverManager.getConnection("jdbc:mysql://localhost/statika", "root", "");
            st = co.createStatement();
            rs = st.executeQuery(parancs);
            while (rs.next()) {
                szekcioszam++;
                // A szekció sorszáma
                szekcio[rs.getInt("szekcio")][0] = rs.getInt("szekcio");
                // A szekció magassága
                szekcio[rs.getInt("szekcio")][1] = rs.getInt("magassag");
                // Alsoszelesseg_xy
                szekcio[rs.getInt("szekcio")][2] = rs.getInt("alsoszelxy");
                // Felsoszelesseg_xy
                szekcio[rs.getInt("szekcio")][3] = rs.getInt("felsoszelxy");
                // Alsoszelesseg_yz
                szekcio[rs.getInt("szekcio")][4] = rs.getInt("alsoszelyz");
                // Felsoszelesseg_yz
                szekcio[rs.getInt("szekcio")][5] = rs.getInt("felsoszelyz");
                // Az orientáció V=1 , H=2
                szekcio[rs.getInt("szekcio")][6] = rs.getInt("irany");
                // Az X-koordináta
                szekcio[rs.getInt("szekcio")][7] = rs.getInt("x");
                // Az Y-koordináta
                szekcio[rs.getInt("szekcio")][8] = rs.getInt("y");
                // Az Z-koordináta
                szekcio[rs.getInt("szekcio")][9] = rs.getInt("z");
                // A horizontális elemeknél a teljes kinyúlás hossza
                szekcio[rs.getInt("szekcio")][10] = rs.getInt("teljes");
                // Az XY-irányú tengelyeltolás
                szekcio[rs.getInt("szekcio")][11] = rs.getInt("eltolasxy");
                // Az YZ-irányú tengelyeltolás
                szekcio[rs.getInt("szekcio")][12] = rs.getInt("eltolasyz");
                // Az alsó pont kerestetése 0-nincs keresés, 1-van keresés, 2-bázis
                szekcio[rs.getInt("szekcio")][13] = rs.getInt("keresni");
                // Szimmetrikus rácselemek esetén a tükröztetés beállítása
                szekcio[rs.getInt("szekcio")][14] = rs.getInt("tukrozes");
            }
        } catch (InstantiationException e) {
        } catch (IllegalAccessException e) {
        } catch (ClassNotFoundException e) {
        } catch (SQLException e) {
        }
    }

    public void beolvas_kozok(String tartonev, int szekcio) {
        kozok = 0;
        for (int i = 1; i < 100; i++) {
            for (int j = 1; j <= 9; j++) {
                felepites[i][j] = 0;
            }
        }
        parancs = "select magassag,racs1,racs2,racs3,racs4,racs5,racs6,racs7,racs8,racs9 from racsalap1 where nev = '" + tartonev + "' and szekcio = '" + szekcio + "' order by koz";
        //System.out.println(parancs);
        try {
            Class.forName("com.mysql.jdbc.Driver").newInstance();
            co = DriverManager.getConnection("jdbc:mysql://localhost/statika", "root", "");
            st = co.createStatement();
            rs = st.executeQuery(parancs);
            while (rs.next()) {
                kozok++;
                koz[kozok] = rs.getInt("magassag");
                felepites[kozok][1] = rs.getInt("racs1");
                felepites[kozok][2] = rs.getInt("racs2");
                felepites[kozok][3] = rs.getInt("racs3");
                felepites[kozok][4] = rs.getInt("racs4");
                felepites[kozok][5] = rs.getInt("racs5");
                felepites[kozok][6] = rs.getInt("racs6");
                felepites[kozok][7] = rs.getInt("racs7");
                felepites[kozok][8] = rs.getInt("racs8");
                felepites[kozok][9] = rs.getInt("racs9");
                /*System.out.println("Elem:" + kozok + " magasság" + koz[kozok]
                 + " rács1:" + felepites[kozok][1]
                 + " rács2:" + felepites[kozok][2]
                 + " rács3:" + felepites[kozok][3]
                 + " rács4:" + felepites[kozok][4]
                 + " rács5:" + felepites[kozok][5]
                 + " rács6:" + felepites[kozok][6]
                 + " rács7:" + felepites[kozok][7]
                 + " rács8:" + felepites[kozok][8]
                 + " rács9:" + felepites[kozok][9]);*/
            }
        } catch (InstantiationException e) {
        } catch (IllegalAccessException e) {
        } catch (ClassNotFoundException e) {
        } catch (SQLException e) {
        }
    }

    public void ellenoriz1(String tartonev) {
        // Az X,Y,Z koordináták ellenőrzése
        int UpdateQuery;
        if (szekcioszam > 1) {
            parancs = "";
            for (int j = 2; j <= szekcioszam; j++) {
                if (((szekcio[j][7] + szekcio[j][8] + szekcio[j][9]) == 0) && (szekcio[j][6] == 1)) {
                    // Csak a vertikális szekciókra működjön!
                    szekcio[j][7] = szekcio[j - 1][7] + (szekcio[j - 1][2] - szekcio[j - 1][3]) / 2;
                    szekcio[j][8] = szekcio[j - 1][8] + szekcio[j - 1][1];
                    szekcio[j][9] = szekcio[j - 1][9] + (szekcio[j - 1][4] - szekcio[j - 1][5]) / 2;
                    parancs = parancs + "update racsalap set x='" + szekcio[j][7] + "', y='" + szekcio[j][8] + "', z='" + szekcio[j][9] + "' where nev ='" + tartonev + "' and szekcio = '" + j + "';\n";
                }
            }
            //System.out.println("  sql:" + parancs);
            // A Mysql-be való visszaírás  --> Ez nem csinál semmit....                       
            try {
                Class.forName("com.mysql.jdbc.Driver").newInstance();
                co = DriverManager.getConnection("jdbc:mysql://localhost/statika", "root", "");
                st = co.createStatement();
                UpdateQuery = st.executeUpdate(parancs);
                st.close();
            } catch (InstantiationException e) {
            } catch (IllegalAccessException e) {
            } catch (ClassNotFoundException e) {
            } catch (SQLException e) {
            }
        }
    }

    public void ellenoriz2(int szkszam, int kozdarab) {
        // A beírt rúdtipusok tipusainak ellenőrzése függőleges rácselemeknél
        int UpdateQuery;
        /* A kényszerek:
         3a -> 2b
         3b -> 2a
         4a,4b -> 2b
         4b -> 7a
         4d -> 7b
         5a -> 4b
         5b -> 4d
         6a -> 7a,5a,4b,3a,2b
         6b -> 7b,5b,4d
         7a -> 4b,2b
         7b -> 4d
         8a -> 1b, 4-7-ig és 9 üres
         8b -> 1a, 4-7-ig és 9 üres
         9a,9c,9d -> 1a, 4-8-ig üres
         9b -> 1a, 2, és 4-8-ig üres  (A 2-es újrarajzolva!)         
         */
        if ((felepites[kozdarab][9] == 1) || (felepites[kozdarab][9] == 3) || (felepites[kozdarab][9] == 4)) {
            felepites[kozdarab][1] = 1;
            felepites[kozdarab][4] = 0;
            felepites[kozdarab][5] = 0;
            felepites[kozdarab][6] = 0;
            felepites[kozdarab][7] = 0;
            felepites[kozdarab][8] = 0;
        }
        if (felepites[kozdarab][9] == 2) {
            felepites[kozdarab][1] = 1;
            felepites[kozdarab][2] = 0;
            felepites[kozdarab][4] = 0;
            felepites[kozdarab][5] = 0;
            felepites[kozdarab][6] = 0;
            felepites[kozdarab][7] = 0;
            felepites[kozdarab][8] = 0;
        }
        if ((felepites[kozdarab][8] == 1) || (felepites[kozdarab][8] == 2)) {
            felepites[kozdarab][1] = 2;
            felepites[kozdarab][4] = 0;
            felepites[kozdarab][5] = 0;
            felepites[kozdarab][6] = 0;
            felepites[kozdarab][7] = 0;
            felepites[kozdarab][9] = 0;
        }
        if (felepites[kozdarab][7] == 1) {
            felepites[kozdarab][4] = 2;
            felepites[kozdarab][2] = 2;
        }
        if (felepites[kozdarab][7] == 2) {
            felepites[kozdarab][4] = 4;
        }
        if (felepites[kozdarab][6] == 1) {
            felepites[kozdarab][7] = 1;
            felepites[kozdarab][5] = 1;
            felepites[kozdarab][4] = 2;
            felepites[kozdarab][3] = 1;
            felepites[kozdarab][2] = 2;
        }
        if (felepites[kozdarab][6] == 2) {
            felepites[kozdarab][7] = 2;
            felepites[kozdarab][5] = 2;
            felepites[kozdarab][4] = 4;
        }
        if (felepites[kozdarab][5] == 1) {
            felepites[kozdarab][4] = 2;
            //felepites[kozdarab][7] =1;;
        }
        if (felepites[kozdarab][5] == 2) {
            felepites[kozdarab][4] = 4;
            //felepites[kozdarab][7] =2;
        }
        if (felepites[kozdarab][4] == 1) {
            felepites[kozdarab][2] = 2;
        }
        if (felepites[kozdarab][4] == 2) {
            felepites[kozdarab][2] = 2;
            felepites[kozdarab][7] = 1;
        }
        if (felepites[kozdarab][4] == 4) {
            felepites[kozdarab][7] = 1;
        }
        if (felepites[kozdarab][3] == 1) {
            felepites[kozdarab][2] = 2;
        }
        if (felepites[kozdarab][3] == 2) {
            felepites[kozdarab][2] = 1;
        }
        // A Mysql-be való visszaírás 
        parancs = "update racsalap1 set ";
        parancs = parancs + "racs1 ='" + felepites[kozdarab][1] + "', ";
        parancs = parancs + "racs2 ='" + felepites[kozdarab][2] + "', ";
        parancs = parancs + "racs3 ='" + felepites[kozdarab][3] + "', ";
        parancs = parancs + "racs4 ='" + felepites[kozdarab][4] + "', ";
        parancs = parancs + "racs5 ='" + felepites[kozdarab][5] + "', ";
        parancs = parancs + "racs6 ='" + felepites[kozdarab][6] + "', ";
        parancs = parancs + "racs7 ='" + felepites[kozdarab][7] + "', ";
        parancs = parancs + "racs8 ='" + felepites[kozdarab][8] + "', ";
        parancs = parancs + "racs9 ='" + felepites[kozdarab][9] + "' ";
        parancs = parancs + "where nev='" + nev + "' and szekcio = '" + szkszam + "' and koz = '" + kozdarab + "';";
        //System.out.println("  sql:" + parancs);
        try {
            Class.forName("com.mysql.jdbc.Driver").newInstance();
            co = DriverManager.getConnection("jdbc:mysql://localhost/statika", "root", "");
            st = co.createStatement();
            //UpdateQuery = st.executeUpdate(parancs);
            st.close();
        } catch (InstantiationException e) {
        } catch (IllegalAccessException e) {
        } catch (ClassNotFoundException e) {
        } catch (SQLException e) {
        }
    }

    public void ellenoriz3(int szkszam, int kozdarab) {
        // A beírt rúdtipusok tipusainak ellenőrzése vízszintes rácselemeknél
        int UpdateQuery;
        /* A kényszerek:
         2a -> 1a        
         4a,4b -> 2a        
         5a -> 4b        
         6a -> 4b        
         */
        if (felepites[kozdarab][6] == 1) {
            felepites[kozdarab][4] = 2;
        }
        if (felepites[kozdarab][5] == 1) {
            felepites[kozdarab][4] = 2;
        }
        if ((felepites[kozdarab][4] == 1) || (felepites[kozdarab][4] == 2)) {
            felepites[kozdarab][2] = 1;
        }
        if (felepites[kozdarab][2] == 1) {
            felepites[kozdarab][1] = 1;
        }
        // A Mysql-be való visszaírás 
        parancs = "update racsalap1 set ";
        parancs = parancs + "racs1 ='" + felepites[kozdarab][1] + "', ";
        parancs = parancs + "racs2 ='" + felepites[kozdarab][2] + "', ";
        parancs = parancs + "racs4 ='" + felepites[kozdarab][4] + "', ";
        parancs = parancs + "racs5 ='" + felepites[kozdarab][5] + "', ";
        parancs = parancs + "racs6 ='" + felepites[kozdarab][6] + "' ";
        parancs = parancs + "where nev='" + nev + "' and szekcio = '" + szkszam + "' and koz = '" + kozdarab + "';";
        //System.out.println("  sql:" + parancs);
        try {
            Class.forName("com.mysql.jdbc.Driver").newInstance();
            co = DriverManager.getConnection("jdbc:mysql://localhost/statika", "root", "");
            st = co.createStatement();
            //UpdateQuery = st.executeUpdate(parancs);
            st.close();
        } catch (InstantiationException e) {
        } catch (IllegalAccessException e) {
        } catch (ClassNotFoundException e) {
        } catch (SQLException e) {
        }
    }

    public void fuggoleges_koordinatak() {
        for (int i = 1; i < 100; i++) {
            for (int j = 1; j <= 3; j++) {
                koord[i][j] = 0;
            }
        }
        // alsó szint 1-12,45-48, 78
        koord[1][0] = x1;
        koord[1][1] = y1;
        koord[1][2] = z1;
        koord[2][0] = x1 + alsoszel_xy / 2;
        koord[2][1] = y1;
        koord[2][2] = z1;
        koord[3][0] = x1 + alsoszel_xy;
        koord[3][1] = y1;
        koord[3][2] = z1;
        koord[6][0] = x1;
        koord[6][1] = y1;
        koord[6][2] = z1 + alsoszel_yz / 2;
        koord[7][0] = x1 + alsoszel_xy;
        koord[7][1] = y1;
        koord[7][2] = z1 + alsoszel_yz / 2;

        // Ezek nincsenek használva
        koord[8][0] = x1;
        koord[8][1] = y1;
        koord[8][2] = z1;
        koord[9][0] = x1;
        koord[9][1] = y1;
        koord[9][2] = z1;

        koord[10][0] = x1;
        koord[10][1] = y1;
        koord[10][2] = z1 + alsoszel_yz;
        koord[11][0] = x1 + alsoszel_xy / 2;
        koord[11][1] = y1;
        koord[11][2] = z1 + alsoszel_yz;
        koord[12][0] = x1 + alsoszel_xy;
        koord[12][1] = y1;
        koord[12][2] = z1 + alsoszel_yz;
        koord[45][0] = x1 + alsoszel_xy / 4;
        koord[45][1] = y1;
        koord[45][2] = z1 + alsoszel_yz / 4;
        koord[46][0] = x1 + alsoszel_xy - alsoszel_xy / 4;
        koord[46][1] = y1;
        koord[46][2] = z1 + alsoszel_yz / 4;
        koord[47][0] = x1 + alsoszel_xy / 4;
        koord[47][1] = y1;
        koord[47][2] = z1 + alsoszel_yz - alsoszel_yz / 4;
        koord[48][0] = x1 + alsoszel_xy - alsoszel_xy / 4;
        koord[48][1] = y1;
        koord[48][2] = z1 + alsoszel_yz - alsoszel_yz / 4;
        koord[78][0] = x1 + alsoszel_xy / 2;
        koord[78][1] = y1;
        koord[78][2] = z1 + alsoszel_yz / 2;

        // középső szint
        // 4-5, 13-24,37-44,79-82

        koord[4][0] = x1 + alsoszel_xy / 2;
        koord[4][1] = y1 + racskoz / 2;
        koord[4][2] = z1 + (alsoszel_yz - felsoszel_yz) / 4;
        koord[5][0] = x1 + alsoszel_xy / 2;
        koord[5][1] = y1 + racskoz / 2;
        koord[5][2] = z1 + alsoszel_yz - (alsoszel_yz - felsoszel_yz) / 4;
        koord[13][0] = x1 + (alsoszel_xy - felsoszel_xy) / 4;
        koord[13][1] = y1 + racskoz / 2;
        koord[13][2] = z1 + (alsoszel_yz - felsoszel_yz) / 4;
        koord[14][0] = x1 + alsoszel_xy / 4;
        koord[14][1] = y1 + racskoz / 2;
        koord[14][2] = z1 + (alsoszel_yz - felsoszel_yz) / 4;
        koord[15][0] = x1 + alsoszel_xy - alsoszel_xy / 4;
        koord[15][1] = y1 + racskoz / 2;
        koord[15][2] = z1 + (alsoszel_yz - felsoszel_yz) / 4;
        koord[16][0] = x1 + alsoszel_xy - (alsoszel_xy - felsoszel_xy) / 4;
        koord[16][1] = y1 + racskoz / 2;
        koord[16][2] = z1 + (alsoszel_yz - felsoszel_yz) / 4;
        koord[17][0] = x1 + (alsoszel_xy - felsoszel_xy) / 4;
        koord[17][1] = y1 + racskoz / 2;
        koord[17][2] = z1 + alsoszel_yz / 4;
        koord[18][0] = x1 + alsoszel_xy - (alsoszel_xy - felsoszel_xy) / 4;
        koord[18][1] = y1 + racskoz / 2;
        koord[18][2] = z1 + alsoszel_yz / 4;
        koord[19][0] = x1 + (alsoszel_xy - felsoszel_xy) / 4;
        koord[19][1] = y1 + racskoz / 2;
        koord[19][2] = z1 + alsoszel_yz - alsoszel_yz / 4;
        koord[20][0] = x1 + alsoszel_xy - (alsoszel_xy - felsoszel_xy) / 4;
        koord[20][1] = y1 + racskoz / 2;
        koord[20][2] = z1 + alsoszel_yz - alsoszel_yz / 4;
        koord[21][0] = x1 + (alsoszel_xy - felsoszel_xy) / 4;
        koord[21][1] = y1 + racskoz / 2;
        koord[21][2] = z1 + alsoszel_yz - (alsoszel_yz - felsoszel_yz) / 4;
        koord[22][0] = x1 + alsoszel_xy / 4;
        koord[22][1] = y1 + racskoz / 2;
        koord[22][2] = z1 + alsoszel_yz - (alsoszel_yz - felsoszel_yz) / 4;
        koord[23][0] = x1 + alsoszel_xy - alsoszel_xy / 4;
        koord[23][1] = y1 + racskoz / 2;
        koord[23][2] = z1 + alsoszel_yz - (alsoszel_yz - felsoszel_yz) / 4;
        koord[24][0] = x1 + alsoszel_xy - (alsoszel_xy - felsoszel_xy) / 4;
        koord[24][1] = y1 + racskoz / 2;
        koord[24][2] = z1 + alsoszel_yz - (alsoszel_yz - felsoszel_yz) / 4;

        koord[37][0] = x1 + (alsoszel_xy - felsoszel_xy) / 2 + felsoszel_xy / 4;
        koord[37][1] = y1 + racskoz / 2;
        koord[37][2] = z1 + (alsoszel_yz - felsoszel_yz) / 4;
        koord[38][0] = x1 + (alsoszel_xy - felsoszel_xy) / 2 + felsoszel_xy - felsoszel_xy / 4;
        koord[38][1] = y1 + racskoz / 2;
        koord[38][2] = z1 + (alsoszel_yz - felsoszel_yz) / 4;
        koord[39][0] = x1 + (alsoszel_xy - felsoszel_xy) / 4;
        koord[39][1] = y1 + racskoz / 2;
        koord[39][2] = z1 + (alsoszel_yz - felsoszel_yz) / 2 + felsoszel_yz / 4;
        koord[40][0] = x1 + alsoszel_xy - (alsoszel_xy - felsoszel_xy) / 4;
        koord[40][1] = y1 + racskoz / 2;
        koord[40][2] = z1 + (alsoszel_yz - felsoszel_yz) / 2 + felsoszel_yz / 4;
        koord[41][0] = x1 + (alsoszel_xy - felsoszel_xy) / 4;
        koord[41][1] = y1 + racskoz / 2;
        koord[41][2] = z1 + (alsoszel_yz - felsoszel_yz) / 2 + felsoszel_yz - felsoszel_yz / 4;
        koord[42][0] = x1 + alsoszel_xy - (alsoszel_xy - felsoszel_xy) / 4;
        koord[42][1] = y1 + racskoz / 2;
        koord[42][2] = z1 + (alsoszel_yz - felsoszel_yz) / 2 + felsoszel_yz - felsoszel_yz / 4;
        koord[43][0] = x1 + (alsoszel_xy - felsoszel_xy) / 2 + felsoszel_xy / 4;
        koord[43][1] = y1 + racskoz / 2;
        koord[43][2] = z1 + alsoszel_yz - (alsoszel_yz - felsoszel_yz) / 4;
        koord[44][0] = x1 + (alsoszel_xy - felsoszel_xy) / 2 + felsoszel_xy - felsoszel_xy / 4;
        koord[44][1] = y1 + racskoz / 2;
        koord[44][2] = z1 + alsoszel_yz - (alsoszel_yz - felsoszel_yz) / 4;
        koord[79][0] = x1 + alsoszel_xy / 2;
        koord[79][1] = y1 + racskoz / 2;
        koord[79][2] = koord[13][2];
        koord[80][0] = koord[13][0];
        koord[80][1] = y1 + racskoz / 2;
        koord[80][2] = z1 + alsoszel_yz / 2;
        koord[81][0] = koord[16][0];
        koord[81][1] = y1 + racskoz / 2;
        koord[81][2] = z1 + alsoszel_yz / 2;
        koord[82][0] = x1 + alsoszel_xy / 2;
        koord[82][1] = y1 + racskoz / 2;
        koord[82][2] = koord[21][2];

        // felső szint
        // 25 - 36 , 77

        koord[25][0] = x1 + (alsoszel_xy - felsoszel_xy) / 2;
        koord[25][1] = y1 + racskoz;
        koord[25][2] = z1 + (alsoszel_yz - felsoszel_yz) / 2;
        koord[26][0] = x1 + alsoszel_xy / 2;
        koord[26][1] = y1 + racskoz;
        koord[26][2] = z1 + (alsoszel_yz - felsoszel_yz) / 2;
        koord[27][0] = x1 + (alsoszel_xy - felsoszel_xy) / 2 + felsoszel_xy;
        koord[27][1] = y1 + racskoz;
        koord[27][2] = z1 + (alsoszel_yz - felsoszel_yz) / 2;
        koord[28][0] = x1 + (alsoszel_xy - felsoszel_xy) / 2 + felsoszel_xy / 4;
        koord[28][1] = y1 + racskoz;
        koord[28][2] = z1 + (alsoszel_yz - felsoszel_yz) / 2 + felsoszel_yz / 4;
        koord[29][0] = x1 + (alsoszel_xy - felsoszel_xy) / 2 + felsoszel_xy - felsoszel_xy / 4;
        koord[29][1] = y1 + racskoz;
        koord[29][2] = z1 + (alsoszel_yz - felsoszel_yz) / 2 + felsoszel_yz / 4;
        koord[30][0] = x1 + (alsoszel_xy - felsoszel_xy) / 2;
        koord[30][1] = y1 + racskoz;
        koord[30][2] = z1 + alsoszel_yz / 2;
        koord[31][0] = x1 + (alsoszel_xy - felsoszel_xy) / 2 + felsoszel_xy;
        koord[31][1] = y1 + racskoz;
        koord[31][2] = z1 + alsoszel_yz / 2;
        koord[32][0] = x1 + (alsoszel_xy - felsoszel_xy) / 2 + felsoszel_xy / 4;
        koord[32][1] = y1 + racskoz;
        koord[32][2] = z1 + (alsoszel_yz - felsoszel_yz) / 2 + felsoszel_yz - felsoszel_yz / 4;
        koord[33][0] = x1 + (alsoszel_xy - felsoszel_xy) / 2 + felsoszel_xy - felsoszel_xy / 4;
        koord[33][1] = y1 + racskoz;
        koord[33][2] = z1 + (alsoszel_yz - felsoszel_yz) / 2 + felsoszel_yz - felsoszel_yz / 4;
        koord[34][0] = x1 + (alsoszel_xy - felsoszel_xy) / 2;
        koord[34][1] = y1 + racskoz;
        koord[34][2] = z1 + (alsoszel_yz - felsoszel_yz) / 2 + felsoszel_yz;
        koord[35][0] = x1 + alsoszel_xy / 2;
        koord[35][1] = y1 + racskoz;
        koord[35][2] = z1 + (alsoszel_yz - felsoszel_yz) / 2 + felsoszel_yz;
        koord[36][0] = x1 + (alsoszel_xy - felsoszel_xy) / 2 + felsoszel_xy;
        koord[36][1] = y1 + racskoz;
        koord[36][2] = z1 + (alsoszel_yz - felsoszel_yz) / 2 + felsoszel_yz;
        koord[77][0] = x1 + alsoszel_xy / 2;
        koord[77][1] = y1 + racskoz;
        koord[77][2] = z1 + alsoszel_yz / 2;

        // A hármas felosztás az alsó szintre
        // 49 - 76                

        koord[49][0] = x1 + (alsoszel_xy - felsoszel_xy) / 6;
        koord[49][1] = y1 + racskoz / 3;
        koord[49][2] = z1 + (alsoszel_yz - felsoszel_yz) / 6;
        koord[50][0] = x1 + alsoszel_xy / 6;
        koord[50][1] = y1 + racskoz / 3;
        koord[50][2] = z1 + (alsoszel_yz - felsoszel_yz) / 6;
        koord[51][0] = x1 + alsoszel_xy - alsoszel_xy / 6;
        koord[51][1] = y1 + racskoz / 3;
        koord[51][2] = z1 + (alsoszel_yz - felsoszel_yz) / 6;
        koord[52][0] = x1 + alsoszel_xy - (alsoszel_xy - felsoszel_xy) / 6;
        koord[52][1] = y1 + racskoz / 3;
        koord[52][2] = z1 + (alsoszel_yz - felsoszel_yz) / 6;
        koord[53][0] = x1 + (alsoszel_xy - felsoszel_xy) / 6;
        koord[53][1] = y1 + racskoz / 3;
        koord[53][2] = z1 + alsoszel_yz / 6;
        koord[54][0] = x1 + alsoszel_xy - (alsoszel_xy - felsoszel_xy) / 6;
        koord[54][1] = y1 + racskoz / 3;
        koord[54][2] = z1 + alsoszel_yz / 6;
        koord[55][0] = x1 + (alsoszel_xy - felsoszel_xy) / 2 - (alsoszel_xy - felsoszel_xy) / 6;
        koord[55][1] = y1 + racskoz - racskoz / 3;
        koord[55][2] = z1 + (alsoszel_yz - felsoszel_yz) / 2 - (alsoszel_yz - felsoszel_yz) / 6;
        koord[56][0] = x1 + alsoszel_xy / 2 - alsoszel_xy / 6;
        koord[56][1] = y1 + racskoz - racskoz / 3;
        koord[56][2] = koord[55][2];
        koord[57][0] = x1 + alsoszel_xy / 2 + alsoszel_xy / 6;
        koord[57][1] = y1 + racskoz - racskoz / 3;
        koord[57][2] = koord[55][2];
        koord[58][0] = x1 + alsoszel_xy - (alsoszel_xy - felsoszel_xy) / 2 + (alsoszel_xy - felsoszel_xy) / 6;
        koord[58][1] = y1 + racskoz - racskoz / 3;
        koord[58][2] = koord[55][2];
        koord[59][0] = (koord[55][0] + koord[56][0]) / 2;
        koord[59][1] = y1 + racskoz - racskoz / 3;
        koord[60][0] = (koord[57][0] + koord[58][0]) / 2;
        koord[60][1] = y1 + racskoz - racskoz / 3;
        koord[61][0] = koord[55][0];
        koord[61][1] = y1 + racskoz - racskoz / 3;
        koord[61][2] = z1 + alsoszel_yz / 2 - alsoszel_yz / 6;
        koord[59][2] = (koord[55][2] + koord[61][2]) / 2;
        koord[60][2] = (koord[55][2] + koord[61][2]) / 2;
        koord[62][0] = koord[58][0];
        koord[62][1] = y1 + racskoz - racskoz / 3;
        koord[62][2] = z1 + alsoszel_yz / 2 - alsoszel_yz / 6;
        koord[63][0] = koord[55][0];
        koord[63][1] = y1 + racskoz - racskoz / 3;
        koord[63][2] = z1 + alsoszel_yz / 2 + alsoszel_yz / 6;
        koord[64][0] = koord[58][0];
        koord[64][1] = y1 + racskoz - racskoz / 3;
        koord[64][2] = z1 + alsoszel_yz / 2 + alsoszel_yz / 6;
        koord[65][0] = (koord[55][0] + koord[56][0]) / 2;
        koord[65][1] = y1 + racskoz - racskoz / 3;
        koord[66][0] = (koord[57][0] + koord[58][0]) / 2;
        koord[66][1] = y1 + racskoz - racskoz / 3;
        koord[67][0] = koord[55][0];
        koord[67][1] = y1 + racskoz - racskoz / 3;
        koord[67][2] = z1 + alsoszel_yz - (alsoszel_yz - felsoszel_yz) / 2 + (alsoszel_yz - felsoszel_yz) / 6;
        koord[65][2] = (koord[63][2] + koord[67][2]) / 2;
        koord[66][2] = (koord[63][2] + koord[67][2]) / 2;
        koord[68][0] = x1 + alsoszel_xy / 2 - alsoszel_xy / 6;
        koord[68][1] = y1 + racskoz - racskoz / 3;
        koord[68][2] = koord[67][2];
        koord[69][0] = x1 + alsoszel_xy / 2 + alsoszel_xy / 6;
        koord[69][1] = y1 + racskoz - racskoz / 3;
        koord[69][2] = koord[67][2];
        koord[70][0] = koord[58][0];
        koord[70][1] = y1 + racskoz - racskoz / 3;
        koord[70][2] = koord[67][2];
        koord[71][0] = x1 + (alsoszel_xy - felsoszel_xy) / 6;
        koord[71][1] = y1 + racskoz / 3;
        koord[71][2] = z1 + alsoszel_yz - alsoszel_yz / 6;
        koord[72][0] = x1 + alsoszel_xy - (alsoszel_xy - felsoszel_xy) / 6;
        koord[72][1] = y1 + racskoz / 3;
        koord[72][2] = z1 + alsoszel_yz - alsoszel_yz / 6;
        koord[73][0] = x1 + (alsoszel_xy - felsoszel_xy) / 6;
        koord[73][1] = y1 + racskoz / 3;
        koord[73][2] = z1 + alsoszel_yz - (alsoszel_yz - felsoszel_yz) / 6;
        koord[74][0] = x1 + alsoszel_xy / 6;
        koord[74][1] = y1 + racskoz / 3;
        koord[74][2] = z1 + alsoszel_yz - (alsoszel_yz - felsoszel_yz) / 6;
        koord[75][0] = x1 + alsoszel_xy - alsoszel_xy / 6;
        koord[75][1] = y1 + racskoz / 3;
        koord[75][2] = z1 + alsoszel_yz - (alsoszel_yz - felsoszel_yz) / 6;
        koord[76][0] = x1 + alsoszel_xy - (alsoszel_xy - felsoszel_xy) / 6;
        koord[76][1] = y1 + racskoz / 3;
        koord[76][2] = z1 + alsoszel_yz - (alsoszel_yz - felsoszel_yz) / 6;
    }

    public void vizszintes_koordinatak() {
        for (int i = 5; i < 100; i++) {
            for (int j = 1; j <= 3; j++) {
                koord[i][j] = 0;
            }
        }
        koord[5][0] = x1 + racskoz;
        koord[5][1] = y1;
        koord[5][2] = z1 + (alsoszel_yz - felsoszel_yz) / 2;
        koord[6][0] = x1 + racskoz;
        koord[6][1] = y1;
        koord[6][2] = z1 + alsoszel_yz - (alsoszel_yz - felsoszel_yz) / 2;
        koord[7][0] = x1 + racskoz;
        koord[7][1] = y1 + felsoszel_xy;
        koord[7][2] = koord[5][2];
        koord[8][0] = x1 + racskoz;
        koord[8][1] = y1 + felsoszel_xy;
        koord[8][2] = koord[6][2];
        koord[9][0] = x1 + racskoz / 2;
        if (alsoszel_xy > felsoszel_xy) {
            koord[9][1] = y1 + alsoszel_xy / 2;
        } else {
            koord[9][1] = y1 + felsoszel_xy / 2;
        }
        koord[9][2] = z1 + (alsoszel_yz - felsoszel_yz) / 4;
        koord[10][0] = x1 + racskoz / 2;
        koord[10][1] = y1;
        koord[10][2] = koord[9][2];
        koord[11][0] = x1 + racskoz / 2;
        koord[11][1] = koord[9][1];
        koord[11][2] = z1 + alsoszel_yz - (alsoszel_yz - felsoszel_yz) / 4;
        koord[12][0] = x1 + racskoz / 2;
        koord[12][1] = y1;
        koord[12][2] = koord[11][2];
        koord[13][0] = koord[5][0];
        koord[13][1] = y1 + felsoszel_xy / 2;
        koord[13][2] = z1 + alsoszel_yz / 2;
        /*for (int i = 1; i < 14; i++) {
         System.out.println("i:" + i + " x:" + koord[i][0]+ " y:" + koord[i][1]+ " z:" + koord[i][2]);
         }*/
    }

    public void kiiratas(String filenev) {
        String szoveg;
        try {
            FileWriter fstream = new FileWriter(filenev);
            BufferedWriter outfile = new BufferedWriter(fstream);
            //Csomóponti koordináták...
            System.out.println(" Csomópontok:" + csomopontindex);
            for (int i = 1; i <= csomopontindex; i++) {
                szoveg = "v " + csomopont[i][0] + " " + csomopont[i][1] + " " + csomopont[i][2] + "\n";
                outfile.write(szoveg);
                //System.out.print(szoveg);
            }
            //Rúdkapcsolatok...
            System.out.println(" Rudak:" + rudindex);
            for (int i = 0; i < rudindex; i++) {
                szoveg = "l " + rudak[i][0] + " " + rudak[i][1] + "\n";
                outfile.write(szoveg);
                //System.out.print(szoveg);
            }
            outfile.close();
        } catch (Exception e) {//Catch exception if any
            System.err.println("Error: " + e.getMessage());
        }
    }

    public void textfile(String filenev) {
        String szoveg;
        try {
            FileWriter fstream = new FileWriter(filenev);
            BufferedWriter outfile = new BufferedWriter(fstream);
            //Csomóponti koordináták...
            for (int i = 1; i <= csomopontindex; i++) {
                szoveg = i + ".koord.;  X:" + csomopont[i][0] + "; Y:" + csomopont[i][1] + "; Z:" + csomopont[i][2] + "\n";
                outfile.write(szoveg);
            }
            //Rúdkapcsolatok... 
            for (int i = 1; i <= rudindex; i++) {
                szoveg = i + ".rud;  tol:" + rudak[i][0] + "; ig:" + rudak[i][1] + "; tipus:" + rudak[i][2] + "\n";
                outfile.write(szoveg);
            }
            outfile.close();
        } catch (Exception e) {//Catch exception if any
            System.err.println("Error: " + e.getMessage());
        }
    }

    public void csomopont_beiro(int x, int y, int tipus) {
        boolean beiras;
        beiras = true;
        // A csomópont beírása, ha még nincs
        if (csomopontindex > 0) {
            for (int i = 1; i <= csomopontindex; i++) {
                if ((csomopont[i][0] == koord[x][0]) && (csomopont[i][1] == koord[x][1]) && (csomopont[i][2] == koord[x][2])) {
                    beiras = false;
                }
            }
        }
        if (beiras) {
            csomopontindex++;
            csomopont[csomopontindex][0] = koord[x][0];
            csomopont[csomopontindex][1] = koord[x][1];
            csomopont[csomopontindex][2] = koord[x][2];
        }
        beiras = true;
        if (csomopontindex > 0) {
            for (int i = 1; i <= csomopontindex; i++) {
                if ((csomopont[i][0] == koord[y][0]) && (csomopont[i][1] == koord[y][1]) && (csomopont[i][2] == koord[y][2])) {
                    beiras = false;
                }
            }
        }
        if (beiras) {
            csomopontindex++;
            csomopont[csomopontindex][0] = koord[y][0];
            csomopont[csomopontindex][1] = koord[y][1];
            csomopont[csomopontindex][2] = koord[y][2];
        }
        // A rúd beírása
        for (int i = 0; i <= csomopontindex; i++) {
            if ((csomopont[i][0] == koord[x][0]) && (csomopont[i][1] == koord[x][1]) && (csomopont[i][2] == koord[x][2])) {
                rudak[rudindex][0] = i;
            }
        }
        for (int i = 0; i <= csomopontindex; i++) {
            if ((csomopont[i][0] == koord[y][0]) && (csomopont[i][1] == koord[y][1]) && (csomopont[i][2] == koord[y][2])) {
                rudak[rudindex][1] = i;
            }
        }
        rudak[rudindex][2] = tipus;
        rudindex++;
    }

    public void fuggoleges_racsbeiro(Integer i) {
        // A beállításoknak megfelelően a rácsrudak beíródnak               
        fuggoleges_koordinatak();
        if (felepites[i][1] == 1) {
            csomopont_beiro(1, 25, 1);
            csomopont_beiro(3, 27, 1);
            csomopont_beiro(10, 34, 1);
            csomopont_beiro(12, 36, 1);
        }
        if (felepites[i][1] == 2) {
            csomopont_beiro(1, 13, 1);
            csomopont_beiro(13, 25, 1);
            csomopont_beiro(3, 16, 1);
            csomopont_beiro(16, 27, 1);
            csomopont_beiro(10, 21, 1);
            csomopont_beiro(21, 34, 1);
            csomopont_beiro(12, 24, 1);
            csomopont_beiro(24, 36, 1);
        }
        if (felepites[i][1] == 3) {
            csomopont_beiro(1, 49, 1);
            csomopont_beiro(49, 55, 1);
            csomopont_beiro(55, 25, 1);
            csomopont_beiro(3, 52, 1);
            csomopont_beiro(52, 58, 1);
            csomopont_beiro(58, 27, 1);
            csomopont_beiro(10, 73, 1);
            csomopont_beiro(73, 67, 1);
            csomopont_beiro(67, 34, 1);
            csomopont_beiro(12, 76, 1);
            csomopont_beiro(76, 70, 1);
            csomopont_beiro(70, 36, 1);
        }
        if (felepites[i][2] == 1) {
            csomopont_beiro(25, 27, 2);
            csomopont_beiro(25, 34, 2);
            csomopont_beiro(27, 36, 2);
            csomopont_beiro(34, 36, 2);
        }
        if (felepites[i][2] == 2) {
            csomopont_beiro(25, 26, 2);
            csomopont_beiro(26, 27, 2);
            csomopont_beiro(25, 30, 2);
            csomopont_beiro(30, 34, 2);
            csomopont_beiro(27, 31, 2);
            csomopont_beiro(31, 36, 2);
            csomopont_beiro(34, 35, 2);
            csomopont_beiro(35, 36, 2);
        }
        if (felepites[i][3] == 1) {
            csomopont_beiro(26, 30, 3);
            csomopont_beiro(30, 35, 3);
            csomopont_beiro(35, 31, 3);
            csomopont_beiro(31, 26, 3);
        }
        if (felepites[i][3] == 2) {
            csomopont_beiro(25, 77, 3);
            csomopont_beiro(27, 77, 3);
            csomopont_beiro(34, 77, 3);
            csomopont_beiro(36, 77, 3);
        }
        if (felepites[i][4] == 1) {
            csomopont_beiro(1, 26, 2);
            csomopont_beiro(26, 3, 2);
            csomopont_beiro(3, 31, 2);
            csomopont_beiro(31, 12, 2);
            csomopont_beiro(12, 35, 2);
            csomopont_beiro(35, 10, 2);
            csomopont_beiro(10, 30, 2);
            csomopont_beiro(30, 1, 2);
        }
        if (felepites[i][4] == 2) {
            csomopont_beiro(1, 14, 2);
            csomopont_beiro(14, 26, 2);
            csomopont_beiro(26, 15, 2);
            csomopont_beiro(15, 3, 2);
            csomopont_beiro(3, 18, 2);
            csomopont_beiro(18, 31, 2);
            csomopont_beiro(31, 20, 2);
            csomopont_beiro(20, 12, 2);
            csomopont_beiro(12, 23, 2);
            csomopont_beiro(23, 35, 2);
            csomopont_beiro(35, 22, 2);
            csomopont_beiro(22, 10, 2);
            csomopont_beiro(10, 19, 2);
            csomopont_beiro(19, 30, 2);
            csomopont_beiro(30, 17, 2);
            csomopont_beiro(17, 1, 2);
        }
        if (felepites[i][4] == 3) {
            csomopont_beiro(25, 2, 2);
            csomopont_beiro(2, 27, 2);
            csomopont_beiro(27, 7, 2);
            csomopont_beiro(7, 36, 2);
            csomopont_beiro(36, 11, 2);
            csomopont_beiro(11, 34, 2);
            csomopont_beiro(34, 6, 2);
            csomopont_beiro(6, 25, 2);
        }
        if (felepites[i][4] == 4) {
            csomopont_beiro(25, 37, 2);
            csomopont_beiro(37, 2, 2);
            csomopont_beiro(2, 38, 2);
            csomopont_beiro(38, 27, 2);
            csomopont_beiro(27, 40, 2);
            csomopont_beiro(40, 7, 2);
            csomopont_beiro(7, 42, 2);
            csomopont_beiro(42, 36, 2);
            csomopont_beiro(36, 44, 2);
            csomopont_beiro(44, 11, 2);
            csomopont_beiro(11, 43, 2);
            csomopont_beiro(43, 34, 2);
            csomopont_beiro(34, 41, 2);
            csomopont_beiro(41, 6, 2);
            csomopont_beiro(6, 39, 2);
            csomopont_beiro(39, 25, 2);
        }
        if (felepites[i][4] == 5) {
            csomopont_beiro(1, 50, 2);
            csomopont_beiro(50, 56, 2);
            csomopont_beiro(56, 26, 2);
            csomopont_beiro(3, 51, 2);
            csomopont_beiro(51, 57, 2);
            csomopont_beiro(57, 26, 2);
            csomopont_beiro(10, 74, 2);
            csomopont_beiro(74, 68, 2);
            csomopont_beiro(68, 35, 2);
            csomopont_beiro(12, 75, 2);
            csomopont_beiro(75, 69, 2);
            csomopont_beiro(69, 35, 2);
            csomopont_beiro(3, 54, 2);
            csomopont_beiro(54, 62, 2);
            csomopont_beiro(62, 31, 2);
            csomopont_beiro(12, 72, 2);
            csomopont_beiro(72, 64, 2);
            csomopont_beiro(64, 31, 2);
            csomopont_beiro(1, 53, 2);
            csomopont_beiro(53, 61, 2);
            csomopont_beiro(61, 30, 2);
            csomopont_beiro(10, 71, 2);
            csomopont_beiro(71, 63, 2);
            csomopont_beiro(63, 30, 2);
        }
        if (felepites[i][5] == 1) {
            csomopont_beiro(14, 25, 3);
            csomopont_beiro(15, 27, 3);
            csomopont_beiro(18, 27, 3);
            csomopont_beiro(20, 36, 3);
            csomopont_beiro(23, 36, 3);
            csomopont_beiro(22, 34, 3);
            csomopont_beiro(19, 34, 3);
            csomopont_beiro(17, 25, 3);
        }
        if (felepites[i][5] == 2) {
            csomopont_beiro(37, 1, 3);
            csomopont_beiro(38, 3, 3);
            csomopont_beiro(40, 3, 3);
            csomopont_beiro(42, 12, 3);
            csomopont_beiro(44, 12, 3);
            csomopont_beiro(43, 10, 3);
            csomopont_beiro(41, 10, 3);
            csomopont_beiro(39, 1, 3);
        }
        if (felepites[i][5] == 3) {
            csomopont_beiro(50, 55, 3);
            csomopont_beiro(56, 25, 3);
            csomopont_beiro(74, 67, 3);
            csomopont_beiro(68, 34, 3);
            csomopont_beiro(51, 58, 3);
            csomopont_beiro(57, 27, 3);
            csomopont_beiro(75, 70, 3);
            csomopont_beiro(69, 36, 3);
            csomopont_beiro(54, 58, 3);
            csomopont_beiro(62, 27, 3);
            csomopont_beiro(72, 70, 3);
            csomopont_beiro(64, 36, 3);
            csomopont_beiro(53, 55, 3);
            csomopont_beiro(61, 25, 3);
            csomopont_beiro(71, 67, 3);
            csomopont_beiro(63, 34, 3);
        }
        if (felepites[i][6] == 1) {
            csomopont_beiro(14, 28, 3);
            csomopont_beiro(17, 28, 3);
            csomopont_beiro(15, 29, 3);
            csomopont_beiro(18, 29, 3);
            csomopont_beiro(20, 33, 3);
            csomopont_beiro(23, 33, 3);
            csomopont_beiro(22, 32, 3);
            csomopont_beiro(19, 32, 3);
        }
        if (felepites[i][6] == 2) {
            csomopont_beiro(37, 45, 3);
            csomopont_beiro(39, 45, 3);
            csomopont_beiro(38, 46, 3);
            csomopont_beiro(40, 46, 3);
            csomopont_beiro(42, 48, 3);
            csomopont_beiro(44, 48, 3);
            csomopont_beiro(43, 47, 3);
            csomopont_beiro(41, 47, 3);
        }
        if (felepites[i][6] == 3) {
            csomopont_beiro(53, 59, 3);
            csomopont_beiro(50, 59, 3);
            csomopont_beiro(74, 65, 3);
            csomopont_beiro(71, 65, 3);
            csomopont_beiro(51, 60, 3);
            csomopont_beiro(54, 60, 3);
            csomopont_beiro(72, 66, 3);
            csomopont_beiro(75, 66, 3);
            csomopont_beiro(61, 28, 3);
            csomopont_beiro(56, 28, 3);
            csomopont_beiro(57, 29, 3);
            csomopont_beiro(62, 29, 3);
            csomopont_beiro(69, 33, 3);
            csomopont_beiro(64, 33, 3);
            csomopont_beiro(63, 32, 3);
            csomopont_beiro(68, 32, 3);
        }
        if (felepites[i][7] == 1) {
            csomopont_beiro(14, 13, 3);
            csomopont_beiro(13, 17, 3);
            csomopont_beiro(17, 14, 3);
            csomopont_beiro(15, 16, 3);
            csomopont_beiro(15, 18, 3);
            csomopont_beiro(16, 18, 3);
            csomopont_beiro(19, 21, 3);
            csomopont_beiro(21, 22, 3);
            csomopont_beiro(19, 22, 3);
            csomopont_beiro(23, 24, 3);
            csomopont_beiro(23, 20, 3);
            csomopont_beiro(20, 24, 3);
        }
        if (felepites[i][7] == 2) {
            csomopont_beiro(37, 13, 3);
            csomopont_beiro(13, 39, 3);
            csomopont_beiro(39, 37, 3);
            csomopont_beiro(38, 16, 3);
            csomopont_beiro(38, 40, 3);
            csomopont_beiro(16, 40, 3);
            csomopont_beiro(41, 21, 3);
            csomopont_beiro(21, 43, 3);
            csomopont_beiro(41, 43, 3);
            csomopont_beiro(44, 24, 3);
            csomopont_beiro(44, 42, 3);
            csomopont_beiro(42, 24, 3);
        }
        if (felepites[i][7] == 3) {
            csomopont_beiro(49, 50, 3);
            csomopont_beiro(50, 53, 3);
            csomopont_beiro(53, 49, 3);
            csomopont_beiro(51, 52, 3);
            csomopont_beiro(52, 54, 3);
            csomopont_beiro(54, 51, 3);
            csomopont_beiro(76, 72, 3);
            csomopont_beiro(72, 75, 3);
            csomopont_beiro(75, 76, 3);
            csomopont_beiro(71, 73, 3);
            csomopont_beiro(73, 74, 3);
            csomopont_beiro(74, 71, 3);
            csomopont_beiro(55, 56, 3);
            csomopont_beiro(56, 61, 3);
            csomopont_beiro(61, 55, 3);
            csomopont_beiro(57, 58, 3);
            csomopont_beiro(58, 62, 3);
            csomopont_beiro(62, 57, 3);
            csomopont_beiro(64, 70, 3);
            csomopont_beiro(70, 69, 3);
            csomopont_beiro(69, 64, 3);
            csomopont_beiro(63, 67, 3);
            csomopont_beiro(67, 68, 3);
            csomopont_beiro(68, 63, 3);
        }
        if (felepites[i][8] == 1) {
            csomopont_beiro(1, 16, 3);
            csomopont_beiro(16, 25, 3);
            csomopont_beiro(3, 24, 3);
            csomopont_beiro(24, 27, 3);
            csomopont_beiro(12, 21, 3);
            csomopont_beiro(21, 36, 3);
            csomopont_beiro(10, 13, 3);
            csomopont_beiro(13, 34, 3);
        }
        if (felepites[i][8] == 2) {
            csomopont_beiro(3, 13, 3);
            csomopont_beiro(13, 27, 3);
            csomopont_beiro(12, 16, 3);
            csomopont_beiro(16, 36, 3);
            csomopont_beiro(10, 24, 3);
            csomopont_beiro(24, 34, 3);
            csomopont_beiro(1, 21, 3);
            csomopont_beiro(21, 25, 3);
        }
        if (felepites[i][9] == 1) {
            csomopont_beiro(1, 79, 3);
            csomopont_beiro(25, 79, 3);
            csomopont_beiro(27, 79, 3);
            csomopont_beiro(3, 79, 3);
            csomopont_beiro(1, 80, 3);
            csomopont_beiro(25, 80, 3);
            csomopont_beiro(10, 80, 3);
            csomopont_beiro(34, 80, 3);
            csomopont_beiro(3, 81, 3);
            csomopont_beiro(27, 81, 3);
            csomopont_beiro(36, 81, 3);
            csomopont_beiro(12, 81, 3);
            csomopont_beiro(10, 82, 3);
            csomopont_beiro(34, 82, 3);
            csomopont_beiro(12, 82, 3);
            csomopont_beiro(36, 82, 3);
        }
        if (felepites[i][9] == 2) {
            csomopont_beiro(1, 26, 3);
            csomopont_beiro(26, 3, 3);
            csomopont_beiro(10, 35, 3);
            csomopont_beiro(35, 12, 3);
            csomopont_beiro(1, 80, 3);
            csomopont_beiro(25, 80, 3);
            csomopont_beiro(10, 80, 3);
            csomopont_beiro(34, 80, 3);
            csomopont_beiro(3, 81, 3);
            csomopont_beiro(27, 81, 3);
            csomopont_beiro(36, 81, 3);
            csomopont_beiro(12, 81, 3);
            csomopont_beiro(25, 26, 3);
            csomopont_beiro(26, 27, 3);
            csomopont_beiro(34, 35, 3);
            csomopont_beiro(35, 36, 3);
            csomopont_beiro(25, 34, 3);
            csomopont_beiro(27, 36, 3);
        }
        if (felepites[i][9] == 3) {
            csomopont_beiro(1, 27, 3);
            csomopont_beiro(3, 36, 3);
            csomopont_beiro(12, 34, 3);
            csomopont_beiro(10, 25, 3);
        }
        if (felepites[i][9] == 4) {
            csomopont_beiro(3, 25, 3);
            csomopont_beiro(12, 27, 3);
            csomopont_beiro(10, 36, 3);
            csomopont_beiro(1, 34, 3);
        }
    }

    public void vizszintes_racsbeiro(Integer i) {
        // A beállításoknak megfelelően a rácsrudak beíródnak               
        vizszintes_koordinatak();
        if (felepites[i][1] == 1) {
            csomopont_beiro(3, 7, 2);
            csomopont_beiro(4, 8, 2);
            csomopont_beiro(1, 5, 2);
            csomopont_beiro(2, 6, 2);
        }
        if (felepites[i][2] == 1) {
            csomopont_beiro(5, 6, 2);
            csomopont_beiro(7, 8, 2);
            csomopont_beiro(5, 7, 2);
            csomopont_beiro(6, 8, 2);
        }
        if (felepites[i][3] == 1) {
            csomopont_beiro(5, 13, 3);
            csomopont_beiro(7, 13, 3);
            csomopont_beiro(8, 13, 3);
            csomopont_beiro(6, 13, 3);
        }
        if (felepites[i][3] == 2) {
            csomopont_beiro(5, 8, 3);
        }
        if (felepites[i][3] == 3) {
            csomopont_beiro(7, 6, 3);
        }
        if (felepites[i][4] == 1) {
            csomopont_beiro(3, 5, 3);
            csomopont_beiro(4, 6, 3);
        }
        if (felepites[i][4] == 2) {
            csomopont_beiro(3, 9, 3);
            csomopont_beiro(9, 5, 3);
            csomopont_beiro(4, 11, 3);
            csomopont_beiro(11, 6, 3);
        }
        if (felepites[i][4] == 3) {
            csomopont_beiro(1, 7, 3);
            csomopont_beiro(2, 8, 3);
        }
        if (felepites[i][5] == 1) {
            csomopont_beiro(1, 9, 3);
            csomopont_beiro(2, 11, 3);
        }
        if (felepites[i][6] == 1) {
            csomopont_beiro(10, 9, 3);
            csomopont_beiro(12, 11, 3);
        }
        if (felepites[i][7] == 1) {
            csomopont_beiro(1, 12, 3);
            csomopont_beiro(12, 5, 3);
            csomopont_beiro(12, 10, 3);
        }
        if (felepites[i][7] == 2) {
            csomopont_beiro(1, 6, 3);
            csomopont_beiro(3, 8, 3);
        }
        if (felepites[i][7] == 3) {
            csomopont_beiro(2, 5, 3);
            csomopont_beiro(4, 7, 3);
        }
    }

    public void csomopont_kereso(int pontsorszam) {
        int tavolsag = Integer.MAX_VALUE;
        adat = 1;
        for (int k = 1; k < csomopontdb; k++) {
            if ((Math.abs(koord[pontsorszam][0] - csomopont[k][0])
                    + Math.abs(koord[pontsorszam][1] - csomopont[k][1])
                    + Math.abs(koord[pontsorszam][2] - csomopont[k][2])) < tavolsag) {
                adat = k;
                tavolsag = Math.abs(koord[pontsorszam][0] - csomopont[k][0])
                        + Math.abs(koord[pontsorszam][1] - csomopont[k][1])
                        + Math.abs(koord[pontsorszam][2] - csomopont[k][2]);
            }
        }
        // System.out.println("  csatolopont:" + x1);
    }

    public static void main(String[] args) {
        // TODO code application logic here
        szerkezet tarto = new szerkezet();

        // A dimenzionális kezdőadatok        
        //tarto.nev = "oszlop38-3";
        //tarto.nev = "oszlop38-2";
        //tarto.nev = "teszttarto";
        //tarto.nev = "tipus19";
        tarto.nev = "tipus20";

        tarto.letrehoz(tarto.nev);
        tarto.ellenoriz1(tarto.nev);
        tarto.x1 = 0;
        tarto.y1 = 0;
        tarto.z1 = 0;
        for (int j = 1; j <= tarto.szekcioszam; j++) {
            tarto.beolvas_kozok(tarto.nev, j);
            tarto.magassag = tarto.szekcio[j][1];
            tarto.alsoszelesseg_xy = tarto.szekcio[j][2];
            tarto.felsoszelesseg_xy = tarto.szekcio[j][3];
            tarto.alsoszelesseg_yz = tarto.szekcio[j][4];
            tarto.felsoszelesseg_yz = tarto.szekcio[j][5];
            //System.out.println();
            
            System.out.println("Szekció:"+j+",  felsoszelxy:"+tarto.felsoszelesseg_xy+"  felsoszelzy:"+tarto.felsoszelesseg_yz);
            
            tarto.tartohossz = tarto.szekcio[j][7];
            System.out.println("Szekció:" + j + " magasság:" + tarto.magassag + " Irány:" + tarto.szekcio[j][6] + " közök:" + tarto.kozok);
            if (tarto.szekcio[j][6] == 1) {
                // A függőleges szakaszok                
                for (int i = 1; i <= tarto.kozok; i++) {
                    tarto.ellenoriz2(tarto.szekcio[j][0], i);
                    tarto.y1 += tarto.koz[i - 1];
                    tarto.data = tarto.szekcio[j][7] + Float.parseFloat(String.valueOf(tarto.y1 - tarto.szekcio[j][8])) / Float.parseFloat(String.valueOf(tarto.magassag)) * (Float.parseFloat(String.valueOf(tarto.alsoszelesseg_xy)) - Float.parseFloat(String.valueOf(tarto.felsoszelesseg_xy))) / 2;
                    tarto.x1 = (int) tarto.data;
                    tarto.data = tarto.szekcio[j][9] + Float.parseFloat(String.valueOf(tarto.y1 - tarto.szekcio[j][8])) / Float.parseFloat(String.valueOf(tarto.magassag)) * (Float.parseFloat(String.valueOf(tarto.alsoszelesseg_yz)) - Float.parseFloat(String.valueOf(tarto.felsoszelesseg_yz))) / 2;
                    tarto.z1 = (int) tarto.data;

                    tarto.data = Float.parseFloat(String.valueOf(tarto.y1 - tarto.szekcio[j][8])) / Float.parseFloat(String.valueOf(tarto.magassag)) * (Float.parseFloat(String.valueOf(tarto.alsoszelesseg_xy)) - Float.parseFloat(String.valueOf(tarto.felsoszelesseg_xy)));
                    tarto.data = Float.parseFloat(String.valueOf(tarto.alsoszelesseg_xy)) - tarto.data;
                    tarto.alsoszel_xy = (int) tarto.data;

                    tarto.data = Float.parseFloat(String.valueOf(tarto.y1 - tarto.szekcio[j][8])) / Float.parseFloat(String.valueOf(tarto.magassag)) * (Float.parseFloat(String.valueOf(tarto.alsoszelesseg_yz)) - Float.parseFloat(String.valueOf(tarto.felsoszelesseg_yz)));
                    tarto.data = Float.parseFloat(String.valueOf(tarto.alsoszelesseg_yz)) - tarto.data;
                    tarto.alsoszel_yz = (int) tarto.data;

                    tarto.data = (Float.parseFloat(String.valueOf(tarto.y1 - tarto.szekcio[j][8])) + (Float.parseFloat(String.valueOf(tarto.koz[i])))) / Float.parseFloat(String.valueOf(tarto.magassag)) * (Float.parseFloat(String.valueOf(tarto.alsoszelesseg_xy)) - Float.parseFloat(String.valueOf(tarto.felsoszelesseg_xy)));
                    tarto.data = Float.parseFloat(String.valueOf(tarto.alsoszelesseg_xy)) - tarto.data;
                    tarto.felsoszel_xy = (int) tarto.data;

                    tarto.data = (Float.parseFloat(String.valueOf(tarto.y1 - tarto.szekcio[j][8])) + (Float.parseFloat(String.valueOf(tarto.koz[i])))) / Float.parseFloat(String.valueOf(tarto.magassag)) * (Float.parseFloat(String.valueOf(tarto.alsoszelesseg_yz)) - Float.parseFloat(String.valueOf(tarto.felsoszelesseg_yz)));
                    tarto.data = Float.parseFloat(String.valueOf(tarto.alsoszelesseg_yz)) - tarto.data;
                    tarto.felsoszel_yz = (int) tarto.data;
                    tarto.racskoz = tarto.koz[i];

                    tarto.fuggoleges_racsbeiro(i);
                    
                    System.out.println("köz:" + i + "; alsoszelXY:" + tarto.alsoszel_xy + "; alsoszelYZ:" + tarto.alsoszel_yz + "; felsoszelXY:" + tarto.felsoszel_xy + "; felsoszelYZ:" + tarto.felsoszel_yz + ";  x1:" + tarto.x1 + ";    y1:" + tarto.y1 + ";   z1:" + tarto.z1 + ";   racskoz:" + tarto.racskoz);
                    
                }
                tarto.x1 = 0;
                tarto.y1 = 0;
                tarto.z1 = 0;
                for (int k = 1; k <= j; k++) {
                    tarto.y1 += tarto.szekcio[k][1];
                    tarto.x1 += (tarto.szekcio[k][2] - tarto.szekcio[k][3]) / 2;
                    tarto.z1 += (tarto.szekcio[k][4] - tarto.szekcio[k][5]) / 2;
                }
                //System.out.println("  x1:" + tarto.x1 + "    y1:" + tarto.y1 + "   z1:" + tarto.z1 + "   racskoz:" + tarto.racskoz);
            }
            if (tarto.szekcio[j][6] == 2) {
                // A vízszintes szakaszok
                for (int i = 1; i <= tarto.kozok; i++) {
                    tarto.ellenoriz3(j, i);
                    if (i == 1) {
                        // Az első esetben a 3-as, 4-es koordinátát meg kell határozni. Az 1-es, 2-es adott.
                        tarto.koord[1][0] = tarto.szekcio[j][7];
                        tarto.koord[1][1] = tarto.szekcio[j][8];
                        tarto.koord[1][2] = tarto.szekcio[j][9];
                        tarto.koord[2][0] = tarto.koord[1][0];
                        tarto.koord[2][1] = tarto.koord[1][1];
                        tarto.koord[2][2] = tarto.koord[1][2] + tarto.szekcio[j][4];
                        tarto.koord[3][0] = tarto.koord[1][0];
                        tarto.koord[3][1] = tarto.koord[1][1] + tarto.szekcio[j][2];
                        tarto.koord[3][2] = tarto.koord[1][2];
                        tarto.koord[4][0] = tarto.koord[1][0];
                        tarto.koord[4][1] = tarto.koord[3][1];
                        tarto.koord[4][2] = tarto.koord[2][2];
                        /*
                         System.out.println("1-es pont  x:" + tarto.koord[1][0] + "    y:" + tarto.koord[1][1] + "   z:" + tarto.koord[1][2]);
                         System.out.println("2-es pont  x:" + tarto.koord[2][0] + "    y:" + tarto.koord[2][1] + "   z:" + tarto.koord[2][2]);
                         System.out.println("3-es pont  x:" + tarto.koord[3][0] + "    y:" + tarto.koord[3][1] + "   z:" + tarto.koord[3][2]+"  "+tarto.szekcio[j][2]);
                         System.out.println("4-es pont  x:" + tarto.koord[4][0] + "    y:" + tarto.koord[4][1] + "   z:" + tarto.koord[4][2]+"  "+tarto.szekcio[j][4]);
                         */
                        // A kegközelebbi csomópont kiválasztása                          
                        tarto.csomopont_kereso(1);
                        //System.out.println("  koord[1][0]:" + tarto.koord[1][0] + "   koord[1][1]:" + tarto.koord[1][1] + "   koord[1][2]:" +tarto.koord[1][2]);
                        tarto.koord[1][0] = tarto.csomopont[tarto.adat][0];
                        tarto.koord[1][1] = tarto.csomopont[tarto.adat][1];
                        tarto.koord[1][2] = tarto.csomopont[tarto.adat][2];
                        //System.out.println("  koord[1][0]:" + tarto.koord[1][0] + "   koord[1][1]:" + tarto.koord[1][1] + "   koord[1][2]:" +tarto.koord[1][2]);
                        tarto.csomopont_kereso(2);
                        tarto.koord[2][0] = tarto.csomopont[tarto.adat][0];
                        tarto.koord[2][1] = tarto.csomopont[tarto.adat][1];
                        tarto.koord[2][2] = tarto.csomopont[tarto.adat][2];
                        // Ha rossz a kapcsolati pont (1,2), akkor az adatbázisba vissza kell írni!
                        tarto.csomopont_kereso(3);
                        tarto.koord[3][0] = tarto.csomopont[tarto.adat][0];
                        tarto.koord[3][1] = tarto.csomopont[tarto.adat][1];
                        tarto.koord[3][2] = tarto.csomopont[tarto.adat][2];
                        tarto.csomopont_kereso(4);
                        tarto.koord[4][0] = tarto.csomopont[tarto.adat][0];
                        tarto.koord[4][1] = tarto.csomopont[tarto.adat][1];
                        tarto.koord[4][2] = tarto.csomopont[tarto.adat][2];
                        tarto.x1 = tarto.koord[1][0];
                        tarto.y1 = tarto.koord[1][1];
                        tarto.z1 = tarto.koord[1][2];
                        tarto.alsoszel_xy = tarto.szekcio[j][2];
                        tarto.alsoszel_yz = tarto.szekcio[j][4];
                    }
                    if ((tarto.szekcio[j][7] + tarto.szekcio[j][1]) < 0) {
                        tarto.koord[1][0] += Math.abs(tarto.szekcio[j][1]);
                    }
                    tarto.racskoz = tarto.koz[i];
                    tarto.data = (Math.abs(Float.parseFloat(String.valueOf(tarto.koord[1][0]))
                            + Float.parseFloat(String.valueOf(tarto.racskoz)))
                            - Float.parseFloat(String.valueOf(tarto.szekcio[j][7])))
                            / Math.abs(Float.parseFloat(String.valueOf(tarto.szekcio[j][1])));
                    //System.out.println("1:" + tarto.data + "  2:" + tarto.koord[1][0] + "   3:" + tarto.racskoz + "   4:" + tarto.szekcio[j][7] + "   5:" + tarto.szekcio[j][1]);
                    if ((tarto.szekcio[j][7] + tarto.szekcio[j][1]) < 0) {
                        tarto.koord[1][0] -= Math.abs(tarto.szekcio[j][1]);
                    } else {
                        tarto.data = 1 - tarto.data;
                    }

                    tarto.felsoszel_xy = tarto.szekcio[j][3] + (int) (tarto.data * (tarto.szekcio[j][2] - tarto.szekcio[j][3]));
                    tarto.felsoszel_yz = tarto.szekcio[j][5] + (int) (tarto.data * (tarto.szekcio[j][4] - tarto.szekcio[j][5]));

                    //System.out.println("  data:"+tarto.data);
                    System.out.println("köz:"+i+"; alsoszelxy:" + tarto.alsoszel_xy + "; alsoszelyz:" + tarto.alsoszel_yz + "; felsoszelxy:" + tarto.felsoszel_xy + "; felsoszelyz:" + tarto.felsoszel_yz+";  x1:" + tarto.x1 + ";    y1:" + tarto.y1 + ";   z1:" + tarto.z1 + ";   racskoz:" + tarto.racskoz);

                    tarto.vizszintes_racsbeiro(i);
                    tarto.x1 = tarto.koord[5][0];
                    tarto.y1 = tarto.koord[5][1];
                    tarto.z1 = tarto.koord[5][2];
                    tarto.alsoszel_xy = tarto.felsoszel_xy;
                    tarto.alsoszel_yz = tarto.felsoszel_yz;
                    tarto.koord[1][0] = tarto.koord[5][0];
                    tarto.koord[1][1] = tarto.koord[5][1];
                    tarto.koord[1][2] = tarto.koord[5][2];
                    tarto.koord[2][0] = tarto.koord[6][0];
                    tarto.koord[2][1] = tarto.koord[6][1];
                    tarto.koord[2][2] = tarto.koord[6][2];
                    tarto.koord[3][0] = tarto.koord[5][0];
                    tarto.koord[3][1] = tarto.koord[5][1] + tarto.alsoszel_xy;
                    tarto.koord[3][2] = tarto.koord[5][2];
                    tarto.koord[4][0] = tarto.koord[6][0];
                    tarto.koord[4][1] = tarto.koord[6][1] + tarto.alsoszel_xy;
                    tarto.koord[4][2] = tarto.koord[6][2];
                }
                // Ha van egy kicsi kinyúlás a konzol végén
                if (tarto.szekcio[j][1] != tarto.szekcio[j][10]) {
                    if ((tarto.szekcio[j][7] + tarto.szekcio[j][1]) < 0) {
                        tarto.koord[1][0] += Math.abs(tarto.szekcio[j][1]);
                    }
                    tarto.racskoz = tarto.szekcio[j][10] - tarto.szekcio[j][1];
                    tarto.felsoszel_xy = 0;
                    tarto.felsoszel_yz = 0;
                    if (tarto.szekcio[j][4] == tarto.szekcio[j][5]) {
                        tarto.felsoszel_yz = tarto.szekcio[j][5];
                    }
                    tarto.vizszintes_koordinatak();
                    //System.out.println("  xy:" + tarto.felsoszel_xy + "  yz:" + tarto.felsoszel_yz);
                    if (tarto.felsoszel_xy == 0 && tarto.felsoszel_yz == 0) {
                        tarto.csomopont_beiro(3, 5, 2);
                        tarto.csomopont_beiro(1, 5, 2);
                        tarto.csomopont_beiro(4, 5, 2);
                        tarto.csomopont_beiro(2, 5, 2);
                    }
                    if (tarto.felsoszel_xy == 0 && tarto.felsoszel_yz != 0) {
                        tarto.csomopont_beiro(3, 5, 2);
                        tarto.csomopont_beiro(1, 5, 2);
                        tarto.csomopont_beiro(4, 6, 2);
                        tarto.csomopont_beiro(2, 6, 2);
                        tarto.csomopont_beiro(5, 6, 3);
                    }
                    if (tarto.felsoszel_xy != 0 && tarto.felsoszel_yz == 0) {
                        tarto.csomopont_beiro(3, 7, 2);
                        tarto.csomopont_beiro(1, 5, 2);
                        tarto.csomopont_beiro(4, 7, 2);
                        tarto.csomopont_beiro(2, 5, 2);
                        tarto.csomopont_beiro(7, 5, 3);
                    }
                    if ((tarto.szekcio[j][7] + tarto.szekcio[j][1]) < 0) {
                        tarto.koord[1][0] -= Math.abs(tarto.szekcio[j][1]);
                    }
                }
            }
        }
        tarto.kiiratas("tarto.obj");
        tarto.nev = tarto.nev + ".obj";
        tarto.kiiratas(tarto.nev);

        //tarto.textfile("adatok.txt");
        /*
         for (int i = 1; i < 37; i++) {
         System.out.println( i+".koord.;  X:" + tarto.koord[i][0] + "; Y:" + tarto.koord[i][1] + "; Z:" + tarto.koord[i][2]);                
         }
         * 
         */
    }
}
