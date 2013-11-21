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
import java.awt.geom.Line2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import javax.imageio.ImageIO;

/**
 *
 * @author SD-LEAP
 */
public class kmjellosszadatok {

    String nev, filenev;                       // A szelvény neve és a file-neve    
    String parancs;                            // A MySQL parancsok gyűjtőhelye    
    int elemszam = 50;                         // Az alkotóelemek maximális száma
    int index, forgatas1;                       // Az alkotóelemek aktuális száma & az aktuális elem forgatási szöge
    float magassag, szelesseg, A, ix, kx, sx;
    float inx, iy, ky, sy, iny, ex, ey, fmsuly;             // A keresztmetszeti jellemzők
    float A_szamolt, ex_szamolt, ey_szamolt, ix_szamolt;
    float iy_szamolt, sx_szamolt, sy_szamolt, inx_szamolt;
    float iny_szamolt, kx_szamolt, ky_szamolt;                // A számolt keresztmetszeti jellemzők
    float nagyitas, x, y;                      // Átmeneti tárolók a forgatásnál
    double szog;                               // A forgatásnál az elfordítás szöge - átmeneti tároló
    String[] profilnev = new String[elemszam]; // Az alkotó profilok nevei
    int[] diffx = new int[elemszam];           // Az X-irányú eltolás mértéke
    int[] diffy = new int[elemszam];           // Az Y-irányú eltolás mértéke
    int[] forgatas = new int[elemszam];        // A forgatás mértéke
    int[] mirrorx = new int[elemszam];         // Az X-irányú tükrözés (1-> igen)
    int[] mirrory = new int[elemszam];         // Az Y-irányú tükrözés (1-> igen)
    int[] problema = new int[elemszam];        // Az ütközés kijelzése (1-> igen)
    int[] bazis = new int[elemszam];           // A bázis kijelzése (1-> igen)    
    int[][] koordinatak = new int[elemszam][2];// Az aklotóprofilok szélessége és magassága
    int width = 640, height = 480;             // Az összetett szelvény kijelzett képének a méretei
    int max_x = Integer.MIN_VALUE;
    int min_x = Integer.MAX_VALUE;
    int max_y = Integer.MIN_VALUE;
    int min_y = Integer.MAX_VALUE;
    static Connection co;
    static Statement st;
    static ResultSet rs;

    public void adatbeolvaso() {
        index = 0;
        try {
            Class.forName("com.mysql.jdbc.Driver").newInstance();
            co = DriverManager.getConnection(Global.mysql_server, Global.mysql_user, Global.mysql_password);
            st = co.createStatement();
            parancs = "SELECT nev,diffx,diffy,szog,mirrorx,mirrory,bazis FROM osszetett where ossznev ='";
            parancs = parancs + nev + "' order by bazis;";
            //System.out.println("SQL: " + parancs);
            rs = st.executeQuery(parancs);
            while (rs.next()) {
                index++;
                profilnev[index] = rs.getString("nev");
                diffx[index] = rs.getInt("diffx");
                diffy[index] = rs.getInt("diffy");
                forgatas[index] = rs.getInt("szog");
                mirrorx[index] = rs.getInt("mirrorx");
                mirrory[index] = rs.getInt("mirrory");
                problema[index] = 0;
                bazis[index] = rs.getInt("bazis");
            }
            rs.close();
            st.close();
        } catch (InstantiationException e) {
        } catch (IllegalAccessException e) {
        } catch (ClassNotFoundException e) {
        } catch (SQLException e) {
        }
        for (int i = 1; i <= index; i++) {
            try {
                Class.forName("com.mysql.jdbc.Driver").newInstance();
                co = DriverManager.getConnection(Global.mysql_server, Global.mysql_user, Global.mysql_password);
                st = co.createStatement();
                parancs = "SELECT magassag,szelesseg FROM szelveny where nev ='" + profilnev[i] + "';";
                //System.out.println("SQL: " + parancs);
                rs = st.executeQuery(parancs);
                while (rs.next()) {
                    koordinatak[i][0] = rs.getInt("szelesseg");
                    koordinatak[i][1] = rs.getInt("magassag");
                }
                rs.close();
                st.close();
            } catch (InstantiationException e) {
            } catch (IllegalAccessException e) {
            } catch (ClassNotFoundException e) {
            } catch (SQLException e) {
            }
        }
        //filenev = "./images/drotvaz/" + nev + ".png";
    }

    public void pontforgato(int elem) {
        double atfogo;
        float kozepx = koordinatak[elem][0] / 2;  // szélesség
        float kozepy = koordinatak[elem][1] / 2;  // magasság
        szog = 0;
        atfogo = Math.sqrt(Double.parseDouble(String.valueOf((kozepx - x) * (kozepx - x) + (kozepy - y) * (kozepy - y))));        
        szog = Math.atan2((x - kozepx), (y - kozepy));
        //szog += Math.toRadians(forgatas[elem] + 90);   
        x = kozepx + Float.parseFloat(String.valueOf(Math.sin(szog) * atfogo));
        y = kozepy + Float.parseFloat(String.valueOf(Math.cos(szog) * atfogo));
    }

    public void nagyitas_szamolo() {
        //nagyitas = 1;        
        int meretx, merety;
        for (int i = 1; i <= index; i++) {
            if ((diffx[i] + (koordinatak[i][0] / 2)) > max_x) {
                max_x = diffx[i] + (koordinatak[i][0] / 2);
            }
            if ((diffx[i] - (koordinatak[i][0] / 2)) < min_x) {
                min_x = diffx[i] - (koordinatak[i][0] / 2);
            }
            if ((diffy[i] + (koordinatak[i][1] / 2)) > max_y) {
                max_y = diffy[i] + (koordinatak[i][1] / 2);
            }
            if ((diffy[i] - (koordinatak[i][1] / 2)) < min_y) {
                min_y = diffy[i] - (koordinatak[i][1] / 2);
            }
        }
        meretx = max_x - min_x;
        merety = max_y - min_y;
        if (meretx > merety) {
            nagyitas = height / (float) meretx;
        } else {
            nagyitas = height / (float) merety;
        }
        System.out.println("Max_x:" + max_x + "   Min_x:" + min_x + "   Méretx:" + meretx + "  Max_y:" + max_y + "   Min_y:" + min_y + "   Mérety:" + merety + "   Nagyítás:" + nagyitas);
        //System.out.println(" Nagyítás számolás...");
        /*for (int i = 1; i <= index; i++) {
         System.out.println("diffx:" + diffx[i] + "   diffy:" + diffy[i] );
         diffx[i]-=min_x;
         diffy[i]-=min_y;
         System.out.println("diffx:" + diffx[i] + "   diffy:" + diffy[i] );
         System.out.println();
         } */
    }

    public void kisrajz(int elem) {
        float xx1, xx2, yy1, yy2, r1, r2;
        String szoveg;
        int pontszam = 10000;                                   // A kijelzésre kerülő pontok száma
        int maxkontur = 20;                                     // A maximális kontúrszám
        int[] pontokx = new int[pontszam];                      // Átmeneti tárolóhely az ábrák kijelzéséhez
        int[] pontoky = new int[pontszam];                      // Átmeneti tárolóhely az ábrák kijelzéséhez 
        int konturszam, szamlalo;                               // Az aktuális szelvényt alkotó kontúrok száma (1-külső, 2 v. több-belső) és a pontsor pontjainak a száma
        float[][] x1 = new float[elemszam][maxkontur];          // Vonal: x1, görbe: KözéppontX
        float[][] y1 = new float[elemszam][maxkontur];          // Vonal: y1, görbe: KözéppontY
        float[][] x2 = new float[elemszam][maxkontur];          // Vonal: x2, görbe: Sugárx
        float[][] y2 = new float[elemszam][maxkontur];          // Vonal: y2, görbe: Sugáry
        float[][] kezdoszog = new float[elemszam][maxkontur];   // Vonal: 0, görbe: Kezdőszög
        float[][] vegszog = new float[elemszam][maxkontur];     // Vonal: 0, görbe: Végszög
        int[][] jelleg = new int[elemszam][maxkontur];          // Egyenes vagy görbe (E->1/G->2)
        int[][] irany = new int[elemszam][maxkontur];           // A görbék rajzolásánál az irány (1->CCW,2->CW)
        int[] indexek = new int[maxkontur];                     // A kontúrok elemszámai

        try {
            //System.out.println("Szelvény: " + profilnev[elem]);
            konturszam = 0;
            try {
                Class.forName("com.mysql.jdbc.Driver").newInstance();
                co = DriverManager.getConnection(Global.mysql_server, Global.mysql_user, Global.mysql_password);
                st = co.createStatement();
                parancs = "SELECT max(vonal) as vonalszam FROM kontur where nev ='" + profilnev[elem] + "' ";
                parancs = parancs + "union SELECT max(vonal) as vonalszam FROM kontur_e where nev ='" + profilnev[elem] + "'; ";
                //System.out.println("SQL: " + parancs);
                rs = st.executeQuery(parancs);
                while (rs.next()) {
                    konturszam = konturszam + rs.getInt(1);
                }
                rs.close();
                st.close();
            } catch (InstantiationException e) {
            } catch (IllegalAccessException e) {
            } catch (ClassNotFoundException e) {
            } catch (SQLException e) {
            }
            for (int i = 1; i <= konturszam; i++) {
                try {
                    Class.forName("com.mysql.jdbc.Driver").newInstance();
                    co = DriverManager.getConnection(Global.mysql_server, Global.mysql_user, Global.mysql_password);
                    st = co.createStatement();
                    parancs = "SELECT * FROM kontur where nev = '" + profilnev[elem] + "' and vonal=" + i + " ";
                    parancs = parancs + "UNION SELECT * FROM kontur_e where nev = '" + profilnev[elem] + "' and vonal=" + i + " order by sorszam;";
                    //System.out.println("SQL: " + parancs);
                    rs = st.executeQuery(parancs);
                    indexek[i] = 0;
                    while (rs.next()) {
                        indexek[i]++;
                        jelleg[indexek[i]][i] = rs.getInt("jelleg");
                        x1[indexek[i]][i] = rs.getFloat("x1");
                        y1[indexek[i]][i] = rs.getFloat("y1");
                        x2[indexek[i]][i] = rs.getFloat("x2");
                        y2[indexek[i]][i] = rs.getFloat("y2");
                        kezdoszog[indexek[i]][i] = rs.getFloat("r1");
                        vegszog[indexek[i]][i] = rs.getFloat("r2");
                        irany[indexek[i]][i] = rs.getInt("irany");
                    }
                    rs.close();
                    st.close();
                } catch (InstantiationException e) {
                } catch (IllegalAccessException e) {
                } catch (ClassNotFoundException e) {
                } catch (SQLException e) {
                }
            }

            // Az első kontúr megrajzolása
            szamlalo = 0;
            for (int i = 1; i <= 1; i++) {
                for (int j = 1; j <= indexek[i]; j++) {
                    xx1 = x1[j][i];
                    yy1 = y1[j][i];
                    xx2 = x2[j][i];
                    yy2 = y2[j][i];
                    r1 = kezdoszog[j][i];
                    r2 = vegszog[j][i];
                    //System.out.println(" j:" + j + "  Jelleg:'" + jelleg[j][i] + "' ");
                    if (jelleg[j][i] == 1) {
                        // egyenes vonalak
                        if ((forgatas[elem] != 0) && (forgatas[elem] != 360)) {
                            x = xx1;
                            y = yy1;
                            pontforgato(elem);
                            xx1 = x;
                            yy1 = y;
                            x = xx2;
                            y = yy2;
                            pontforgato(elem);
                            xx2 = x;
                            yy2 = y;
                        }
                        if (j == 1) {
                            szamlalo++;
                            pontokx[szamlalo] = (int) (xx1 * nagyitas);
                            pontoky[szamlalo] = (int) (yy1 * nagyitas);
                            //System.out.println(" szamlalo:" + szamlalo + "  x1:" + x1[j][i] + "  arany:" + nagyitas + " pontx:"+pontokx[szamlalo]+" ponty:"+pontoky[szamlalo]);
                        }
                        szamlalo++;
                        pontokx[szamlalo] = (int) (xx2 * nagyitas);
                        pontoky[szamlalo] = (int) (yy2 * nagyitas);
                        //System.out.println(" szamlalo:" + szamlalo + "  x1:" + x1[j][i] + "  arany:" + nagyitas + " eltolasx:" +pontokx[szamlalo]+" ponty:"+pontoky[szamlalo]);
                    } else {
                        // A görbe vonalak
                        if ((forgatas[elem] != 0) && (forgatas[elem] != 360)) {
                            x = xx1;
                            y = yy1;
                            pontforgato(elem);
                            xx1 = x;
                            yy1 = y;
                            //System.out.println("I:" + i + " J:" + j + " Forgatás:" + forgatas + " r1_elötte:" + r1 + "  r2_elotte:" + r2);
                            r1 -= forgatas[elem];
                            r2 -= forgatas[elem];
                            //System.out.println(" r1_utána:" + r1 + "  r2_utána:" + r2);
                        }
                        //System.out.println("X1:" + x1[j] + " y1:" + y1[j] + " X2:" + x2[j] + " y2:" + y2[j] + " kezdoszog:" + kezdoszog[j] + "  Végszög:" + vegszog[j] + "  Irány:" + irany[j]);
                        if ((r1 == 0) && (r2 == 360)) {
                            //Teljes kör                                
                            for (int k = 0; k <= 360; k++) {
                                szamlalo++;
                                pontokx[szamlalo] = (int) ((xx1 + xx2 * Math.cos(Math.toRadians(k))) * nagyitas);
                                pontoky[szamlalo] = (int) ((yy1 + yy2 * Math.sin(Math.toRadians(k))) * nagyitas);
                                //System.out.println(" szamlalo:" + szamlalo + " x1:" + x1[j] + " y1:" + y1[j]+ " x2:" + x2[j] + " y2:" + y2[j] +" arany:" + arany + " eltolasx:" + eltolasx+" pontx:"+pontokx[szamlalo]+" ponty:"+pontoky[szamlalo]);
                                //System.out.println(" k:" + k +" x1:"+ x1[j][i]+"  y1:"+ y1[j][i]+ " cos k*x2:" + (x1[j][i] + x2[j][i] * Math.cos(k * (Math.PI / 180)))+ " sin k*y2:" + (y1[j][i] + y2[j][i] * Math.sin(k * (Math.PI / 180))));
                            }
                        } else {
                            if (irany[j][i] == 1) {
                                // Az óra járásával megegyező irány (CW)
                                if (r2 < r1) {
                                    r2 += 360;
                                }
                                for (int k = (int) r1; k <= (int) r2; k++) {
                                    szamlalo++;
                                    pontokx[szamlalo] = (int) ((xx1 + xx2 * Math.cos(Math.toRadians(k))) * nagyitas);
                                    pontoky[szamlalo] = (int) ((yy1 + yy2 * Math.sin(Math.toRadians(k))) * nagyitas);
                                    //System.out.println(" k:" + k + " x1:" + x1[j][i] + "  y1:" + y1[j][i] + " cos k*x2:" + (x1[j][i] + x2[j][i] * Math.cos(k * (Math.PI / 180))) + " sin k*y2:" + (y1[j][i] + y2[j][i] * Math.sin(k * (Math.PI / 180))));
                                }
                            } else {
                                // Az óra járásával ellentétes irány (CCW)
                                if (r2 > r1) {
                                    r2 -= 360;
                                }
                                //System.out.println(" kezdoszog:" + kezdoszog[j] + " vegszog:" + vegszog[j]);
                                for (int k = (int) r1; k >= (int) r2; k--) {
                                    szamlalo++;
                                    pontokx[szamlalo] = (int) ((xx1 + xx2 * Math.cos(Math.toRadians(k))) * nagyitas);
                                    pontoky[szamlalo] = (int) ((yy1 + yy2 * Math.sin(Math.toRadians(k))) * nagyitas);
                                    //System.out.println(" k:" + k + " x1:" + x1[j][i] + "  y1:" + y1[j][i] + " cos k*x2:" + (x1[j][i] + x2[j][i] * Math.cos(k * (Math.PI / 180))) + " sin k*y2:" + (y1[j][i] + y2[j][i] * Math.sin(k * (Math.PI / 180))));
                                }
                            }
                        }
                    }
                }
                pontokx[0] = pontokx[szamlalo];
                pontoky[0] = pontoky[szamlalo];
                if (mirrorx[elem] == 1) {
                    for (int j = 0; j <= szamlalo; j++) {
                        pontokx[j] = (int) ((koordinatak[elem][0] / 2) * nagyitas) - (pontokx[j] - (int) ((koordinatak[elem][0] / 2) * nagyitas));
                    }
                }
                if (mirrory[elem] == 1) {
                    for (int j = 0; j <= szamlalo; j++) {
                        pontoky[j] = (int) ((koordinatak[elem][1] / 2) * nagyitas) - (pontoky[j] - (int) ((koordinatak[elem][1] / 2) * nagyitas));
                    }
                }

                //System.out.println("diffx:"+((height / 2) - kozepx)+"  Diffy:"+((height / 2) - kozepy)+" Kozepx:"+kozepx+"  kozepy"+kozepy);
                    /*for (int j = 0; j <= szamlalo; j++) {
                 pontokx[j] += ((height / 2) - (szelesseg / 2) * nagyitas);
                 pontoky[j] += ((height / 2) - (magassag / 2) * nagyitas);
                 }*/

                // A pontok bal felső sarokba tolása
                // A kép minimális koordinátájának meghatározása 
                max_x = Integer.MIN_VALUE;
                min_x = Integer.MAX_VALUE;
                max_y = Integer.MIN_VALUE;
                min_y = Integer.MAX_VALUE;
                for (int k = 0; k <= szamlalo; k++) {
                    if (pontokx[k] < min_x) {
                        min_x = pontokx[k];
                    }
                    if (pontoky[k] < min_y) {
                        min_y = pontoky[k];
                    }
                }

                if (forgatas[elem] != 0) {
                    for (int j = 0; j <= szamlalo; j++) {
                        pontokx[j] -= min_x;
                        pontoky[j] -= min_y;
                    }
                }

                // A kép maximális koordinátájának meghatározása 
                for (int k = 0; k <= szamlalo; k++) {
                    if (pontokx[k] > max_x) {
                        max_x = pontokx[k];
                    }
                    if (pontoky[k] > max_y) {
                        max_y = pontoky[k];
                    }
                }
            }
            BufferedImage bi = new BufferedImage(max_x, max_y, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g = bi.createGraphics();
            g.setColor(Color.white);
            g.fillRect(0, 0, max_x, max_y);
            g.setColor(Color.black);
            /*for (int k = 0; k <= szamlalo; k++) {
             System.out.println("k:" + k + "  Pontx:" + pontokx[k] + "  Ponty:" + pontoky[k]);
             }*/

            g.fillPolygon(pontokx, pontoky, szamlalo);
            for (int k = 0; k <= szamlalo; k++) {
                pontokx[k] = (int) (pontokx[k] / nagyitas) * 1;
                pontoky[k] = (int) (pontoky[k] / nagyitas) * 1;
            }
            koordinatak[elem][0] = max_x;
            koordinatak[elem][1] = max_y;
            /*
             int width1 = (int) (koordinatak[elem][0] * nagyitas);
             int height1 = (int) (koordinatak[elem][1] * nagyitas);
             if ((forgatas[elem] != 0) && (forgatas[elem] != 360)) {
             width1 = (int) (Math.sqrt(Double.parseDouble(String.valueOf(koordinatak[elem][0] * koordinatak[elem][0] + koordinatak[elem][1] * koordinatak[elem][1]))) * nagyitas);
             height1 = width1;
             } */

            //A pontvonal...
            /*Stroke drawingStroke = new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]{21.0f, 9.0f, 3.0f, 9.0f}, 0);
             Line2D linef = new Line2D.Double(width1 / 2, 0, width1 / 2, height1);
             Line2D linev = new Line2D.Double(0, height1 / 2, width1, height1 / 2); */

            if (konturszam > 1) {
                // A második, stb kontúr megrajzolása                
                for (int i = 2; i <= konturszam; i++) {
                    szamlalo = 0;
                    for (int j = 1; j <= indexek[i]; j++) {
                        xx1 = x1[j][i];
                        yy1 = y1[j][i];
                        xx2 = x2[j][i];
                        yy2 = y2[j][i];
                        r1 = kezdoszog[j][i];
                        r2 = vegszog[j][i];
                        //System.out.println(" j:" + j + "  Jelleg:'" + jelleg[j][i] + "' ");
                        if (jelleg[j][i] == 1) {
                            // egyenes vonalak
                            if ((forgatas[elem] != 0) && (forgatas[elem] != 360)) {
                                x = xx1;
                                y = yy1;
                                pontforgato(elem);
                                xx1 = x;
                                yy1 = y;
                                x = xx2;
                                y = yy2;
                                pontforgato(elem);
                                xx2 = x;
                                yy2 = y;
                            }
                            if (j == 1) {
                                szamlalo++;
                                pontokx[szamlalo] = (int) (xx1 * nagyitas);
                                pontoky[szamlalo] = (int) (yy1 * nagyitas);
                                //System.out.println(" szamlalo:" + szamlalo + "  x1:" + x1[j][i] + "  arany:" + nagyitas + " pontx:"+pontokx[szamlalo]+" ponty:"+pontoky[szamlalo]);
                            }
                            szamlalo++;
                            pontokx[szamlalo] = (int) (xx2 * nagyitas);
                            pontoky[szamlalo] = (int) (yy2 * nagyitas);
                            //System.out.println(" szamlalo:" + szamlalo + "  x1:" + x1[j][i] + "  arany:" + nagyitas + " eltolasx:" +pontokx[szamlalo]+" ponty:"+pontoky[szamlalo]);
                        } else {
                            // A görbe vonalak
                            if ((forgatas[elem] != 0) && (forgatas[elem] != 360)) {
                                x = xx1;
                                y = yy1;
                                pontforgato(elem);
                                xx1 = x;
                                yy1 = y;
                                //System.out.println("I:" + i + " J:" + j + " Forgatás:" + forgatas + " r1_elötte:" + r1 + "  r2_elotte:" + r2);
                                r1 -= forgatas[elem];
                                r2 -= forgatas[elem];
                                //System.out.println(" r1_utána:" + r1 + "  r2_utána:" + r2);
                            }
                            //System.out.println("X1:" + x1[j] + " y1:" + y1[j] + " X2:" + x2[j] + " y2:" + y2[j] + " kezdoszog:" + kezdoszog[j] + "  Végszög:" + vegszog[j] + "  Irány:" + irany[j]);
                            if ((r1 == 0) && (r2 == 360)) {
                                //Teljes kör                                
                                for (int k = 0; k <= 360; k++) {
                                    szamlalo++;
                                    pontokx[szamlalo] = (int) ((xx1 + xx2 * Math.cos(Math.toRadians(k))) * nagyitas);
                                    pontoky[szamlalo] = (int) ((yy1 + yy2 * Math.sin(Math.toRadians(k))) * nagyitas);
                                    //System.out.println(" szamlalo:" + szamlalo + " x1:" + x1[j] + " y1:" + y1[j]+ " x2:" + x2[j] + " y2:" + y2[j] +" arany:" + arany + " eltolasx:" + eltolasx+" pontx:"+pontokx[szamlalo]+" ponty:"+pontoky[szamlalo]);
                                    //System.out.println(" k:" + k +" x1:"+ x1[j][i]+"  y1:"+ y1[j][i]+ " cos k*x2:" + (x1[j][i] + x2[j][i] * Math.cos(k * (Math.PI / 180)))+ " sin k*y2:" + (y1[j][i] + y2[j][i] * Math.sin(k * (Math.PI / 180))));
                                }
                            } else {
                                if (irany[j][i] == 1) {
                                    // Az óra járásával megegyező irány (CW)
                                    if (r2 < r1) {
                                        r2 += 360;
                                    }
                                    for (int k = (int) r1; k <= (int) r2; k++) {
                                        szamlalo++;
                                        pontokx[szamlalo] = (int) ((xx1 + xx2 * Math.cos(Math.toRadians(k))) * nagyitas);
                                        pontoky[szamlalo] = (int) ((yy1 + yy2 * Math.sin(Math.toRadians(k))) * nagyitas);
                                        //System.out.println(" k:" + k + " x1:" + x1[j][i] + "  y1:" + y1[j][i] + " cos k*x2:" + (x1[j][i] + x2[j][i] * Math.cos(k * (Math.PI / 180))) + " sin k*y2:" + (y1[j][i] + y2[j][i] * Math.sin(k * (Math.PI / 180))));
                                    }
                                } else {
                                    // Az óra járásával ellentétes irány (CCW)
                                    if (r2 > r1) {
                                        r2 -= 360;
                                    }
                                    //System.out.println(" kezdoszog:" + kezdoszog[j] + " vegszog:" + vegszog[j]);
                                    for (int k = (int) r1; k >= (int) r2; k--) {
                                        szamlalo++;
                                        pontokx[szamlalo] = (int) ((xx1 + xx2 * Math.cos(Math.toRadians(k))) * nagyitas);
                                        pontoky[szamlalo] = (int) ((yy1 + yy2 * Math.sin(Math.toRadians(k))) * nagyitas);
                                        //System.out.println(" k:" + k + " x1:" + x1[j][i] + "  y1:" + y1[j][i] + " cos k*x2:" + (x1[j][i] + x2[j][i] * Math.cos(k * (Math.PI / 180))) + " sin k*y2:" + (y1[j][i] + y2[j][i] * Math.sin(k * (Math.PI / 180))));
                                    }
                                }
                            }
                        }
                    }
                    pontokx[0] = pontokx[szamlalo];
                    pontoky[0] = pontoky[szamlalo];
                    if (mirrorx[elem] == 1) {
                        for (int j = 0; j <= szamlalo; j++) {
                            pontokx[j] = (int) ((koordinatak[elem][0] / 2) * nagyitas) - (pontokx[j] - (int) ((koordinatak[elem][0] / 2) * nagyitas));
                        }
                    }
                    if (mirrory[elem] == 1) {
                        for (int j = 0; j <= szamlalo; j++) {
                            pontoky[j] = (int) ((koordinatak[elem][1] / 2) * nagyitas) - (pontoky[j] - (int) ((koordinatak[elem][1] / 2) * nagyitas));
                        }
                    }

                    //System.out.println("diffx:"+((height / 2) - kozepx)+"  Diffy:"+((height / 2) - kozepy)+" Kozepx:"+kozepx+"  kozepy"+kozepy);
                    /*for (int j = 0; j <= szamlalo; j++) {
                     pontokx[j] += ((height / 2) - (szelesseg / 2) * nagyitas);
                     pontoky[j] += ((height / 2) - (magassag / 2) * nagyitas);
                     }*/                    
                    if (forgatas[elem] != 0) {
                        for (int j = 0; j <= szamlalo; j++) {
                            pontokx[j] -= min_x;
                            pontoky[j] -= min_y;
                        }
                    }
                    g.setColor(Color.white);
                    /*
                     for (int k = 0; k <= szamlalo; k++) {
                     System.out.println("k:" + k + "  Pontx:" + pontokx[k] + "  Ponty:" + pontoky[k]);
                     }
                     */
                    g.fillPolygon(pontokx, pontoky, szamlalo);
                    for (int k = 0; k <= szamlalo; k++) {
                        pontokx[k] = (int) (pontokx[k] / nagyitas) * 1;
                        pontoky[k] = (int) (pontoky[k] / nagyitas) * 1;
                    }
                }
            }
            /*g.setColor(Color.cyan);
             g.setStroke(drawingStroke);
             g.draw(linef);
             g.draw(linev); */
            szoveg = "./images/szelveny/" + String.valueOf(elem);
            szoveg = szoveg + ".png";
            //System.out.println(" filenevki:" + szoveg);
            //if ((forgatas == 0) || (forgatas == 360)) {
            ImageIO.write(bi, "PNG", new File(szoveg));
        } catch (IOException ie) {
            ie.printStackTrace();
        }
    }

    public void pngrajz() {
        String szoveg;
        float adat;
        adatbeolvaso();
        nagyitas_szamolo();
        if (index > 0) {
            // Az alkotó szelvények létrehozása egyenként
            for (int i = 1; i <= index; i++) {
                kisrajz(i);
            }
        }
        try {
            // TYPE_INT_ARGB specifies the image format: 8-bit RGBA packed into integer pixels                       
            BufferedImage bi = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g = bi.createGraphics();
            // Az alkotószelvények átmeneti tárolója
            BufferedImage bi1 = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g1 = bi.createGraphics();
            //A pontvonal...
            Stroke drawingStroke = new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]{21.0f, 9.0f, 3.0f, 9.0f}, 0);
            Line2D linef = new Line2D.Double(height / 2, 0, height / 2, height);
            Line2D linev = new Line2D.Double(0, height / 2, height, height / 2);

            Font Courier10 = new Font("Courier New", Font.PLAIN, 10);
            Font Courier10b = new Font("Courier New", Font.BOLD, 10);
            Font Courier12b = new Font("Courier New", Font.BOLD, 12);
            Font Courier16b = new Font("Courier New", Font.BOLD, 16);
            g.setColor(Color.white);
            g.fillRect(0, 0, width, height);
            g.setColor(Color.LIGHT_GRAY);
            g.drawLine(height, 0, height, height);
            // A segédvonalak
            Stroke vekonyvonal = new BasicStroke(1);
            g.setStroke(vekonyvonal);
            Color sotetszurke = new Color(60, 60, 60);
            Color kozepszurke = new Color(128, 128, 128);
            Color vilagosszurke = new Color(190, 190, 190);
            float arany1 = nagyitas;
            // Az 1-es vonalak
            if (arany1 > 1) {
                g.setColor(vilagosszurke);
                adat = height / 2 + 10 * arany1;
                while ((int) adat < height) {
                    g.drawLine((int) adat, 0, (int) adat, height);
                    g.drawLine(0, (int) adat, height, (int) adat);
                    adat += 10 * arany1;
                }
                adat = height / 2 - 10 * arany1;
                while ((int) adat > 0) {
                    g.drawLine((int) adat, 0, (int) adat, height);
                    g.drawLine(0, (int) adat, height, (int) adat);
                    adat -= 10 * arany1;
                }
            }
            if (arany1 > 0.5) {
                // Az 5-ös vonalak
                g.setColor(kozepszurke);
                adat = height / 2 + 50 * arany1;
                while ((int) adat < height) {
                    g.drawLine((int) adat, 0, (int) adat, height);
                    g.drawLine(0, (int) adat, height, (int) adat);
                    adat += 50 * arany1;
                }
                adat = height / 2 - 50 * arany1;
                while ((int) adat > 0) {
                    g.drawLine((int) adat, 0, (int) adat, height);
                    g.drawLine(0, (int) adat, height, (int) adat);
                    adat -= 50 * arany1;
                }
            }
            // A 10-es vonalak
            g.setColor(sotetszurke);
            adat = height / 2 + 100 * arany1;
            while ((int) adat < height) {
                g.drawLine((int) adat, 0, (int) adat, height);
                g.drawLine(0, (int) adat, height, (int) adat);
                adat += 100 * arany1;
            }
            adat = height / 2 - 100 * arany1;
            while ((int) adat > 0) {
                g.drawLine((int) adat, 0, (int) adat, height);
                g.drawLine(0, (int) adat, height, (int) adat);
                adat -= 100 * arany1;
            }

            Color feketeszin = new Color(0, 0, 0);
            int fekete = feketeszin.getRGB();
            Color szurkeszin = new Color(120, 120, 120);
            int szurke = szurkeszin.getRGB();
            Color pirosszin = new Color(255, 0, 0);
            int piros = pirosszin.getRGB();
            Color kekszin1 = new Color(0, 0, 255);
            int kek1 = kekszin1.getRGB();
            Color kekszin2 = new Color(0, 204, 0);
            int kek2 = kekszin2.getRGB();
            Color kekszin3 = new Color(153, 102, 51);
            int kek3 = kekszin3.getRGB();
            Color kekszin4 = new Color(0, 75, 255);
            int kek4 = kekszin4.getRGB();
            Color kekszin5 = new Color(102, 153, 255);
            int kek5 = kekszin5.getRGB();
            Color kekszin6 = new Color(153, 255, 102);
            int kek6 = kekszin6.getRGB();
            Color kekszin7 = new Color(255, 153, 102);
            int kek7 = kekszin7.getRGB();
            Color kekszin8 = new Color(102, 0, 102);
            int kek8 = kekszin8.getRGB();
            Color kekszin9 = new Color(153, 0, 153);
            int kek9 = kekszin9.getRGB();
            Color kekszin10 = new Color(0, 225, 255);
            int kek10 = kekszin10.getRGB();
            int szin;

            if (index > 0) {
                // Az alkotó szelvények beolvasása és összemásolása
                for (int i = 1; i <= index; i++) {
                    szoveg = "./images/szelveny/" + String.valueOf(i) + ".png";
                    //System.out.println(szoveg);
                    bi1.flush();
                    bi1 = ImageIO.read(new File(szoveg));
                    //System.out.println(" Diffx:" + diffx[i] + "  Diffy:" + diffy[i]);
                    for (int j = 0; j < koordinatak[i][0]; j++) {
                        for (int k = 0; k < koordinatak[i][1]; k++) {
                            Color originalColor = new Color(bi1.getRGB(j, k));
                            int red = originalColor.getRed();
                            int green = originalColor.getGreen();
                            int blue = originalColor.getBlue();
                            if (red + green + blue == 0) {
                                int koordx = j + (int) ((diffx[i] - min_x) * nagyitas);
                                int koordy = k + (int) ((diffy[i] - min_y) * nagyitas);
                                /*koordx = j;
                                 koordy = k;*/
                                if (koordx >= height) {
                                    koordx = height - 1;
                                }
                                if (koordx < 0) {
                                    koordx = 0;
                                }
                                if (koordy >= height) {
                                    koordy = height - 1;
                                }
                                if (koordy < 0) {
                                    koordy = 0;
                                }
                                //System.out.println("Height:"+height+" j:"+j+"  k:"+k+"  diffx:"+diffx[i]+"  diffy:"+diffy[i]+"  Koordx:"+koordx+"  koordy:"+koordy);
                                switch (i) {
                                    case 1:
                                        szin = szurke;
                                        break;
                                    case 2:
                                        szin = kek1;
                                        break;
                                    case 3:
                                        szin = kek2;
                                        break;
                                    case 4:
                                        szin = kek3;
                                        break;
                                    case 5:
                                        szin = kek4;
                                        break;
                                    case 6:
                                        szin = kek5;
                                        break;
                                    case 7:
                                        szin = kek6;
                                        break;
                                    case 8:
                                        szin = kek7;
                                        break;
                                    case 9:
                                        szin = kek8;
                                        break;
                                    case 10:
                                        szin = kek9;
                                        break;
                                    case 11:
                                        szin = kek10;
                                        break;
                                    default:
                                        szin = fekete;
                                        break;
                                }
                                bi.setRGB(koordx, koordy, szin);
                            }
                        }
                    }
                }
            }

            // A súlypont középvonalai
            g.setColor(Color.blue);
            g.setStroke(drawingStroke);
            //int kozepvonaly = (int) (ey_szamolt * 10 * arany + height / 2 - (szelesseg / 2) * arany);            
            int kozepvonaly = (int) (ex_szamolt * 10 * nagyitas + height / 2 - (magassag / 2) * nagyitas);
            int kozepvonalx = (int) (ey_szamolt * 10 * nagyitas + height / 2 - (szelesseg / 2) * nagyitas);
            //System.out.println(" ex:" + ex_szamolt * 10 + " ey:" + ey_szamolt * 10 + "  x:" + kozepvonalx + "  y:" + kozepvonaly + "  szélesség:" + szelesseg + "  Magasság:" + magassag + "  Arány:" + nagyitas);
            Line2D centerlinex = new Line2D.Double(kozepvonalx, 0, kozepvonalx, height);
            Line2D centerliney = new Line2D.Double(0, kozepvonaly, height, kozepvonaly);
            g.draw(centerlinex);
            g.draw(centerliney);
            g.setFont(Courier16b);
            g.drawString("ex", 5, kozepvonaly + 15);
            g.drawString("ex", height - 25, kozepvonaly - 5);
            g.drawString("ey", kozepvonalx + 10, 15);
            g.drawString("ey", kozepvonalx - 20, height - 10);
            // Feliratok
            g.setColor(Color.black);
            g.setFont(Courier16b);
            g.drawString("X", 5, height / 2 - 5);
            g.drawString("X", height - 10, height / 2 + 15);
            g.drawString("Y", height / 2 - 10, 15);
            g.drawString("Y", height / 2 + 10, height - 10);
            szoveg = "A szelvény neve:";
            g.drawString(szoveg, height + 5, 20);
            g.setFont(Courier12b);
            szoveg = nev;
            g.drawString(szoveg, height + 10, 35);
            g.drawString("Az alkotó szelvények:", height + 10, 50);
            g.setFont(Courier10b);
            for (int i = 1; i <= index; i++) {
                szoveg = String.valueOf(i) + "." + profilnev[i];
                switch (i) {
                    case 1:
                        g.setColor(Color.GRAY);
                        break;
                    case 2:
                        g.setColor(kekszin1);
                        break;
                    case 3:
                        g.setColor(kekszin2);
                        break;
                    case 4:
                        g.setColor(kekszin3);
                        break;
                    case 5:
                        g.setColor(kekszin4);
                        break;
                    case 6:
                        g.setColor(kekszin5);
                        break;
                    case 7:
                        g.setColor(kekszin6);
                        break;
                    case 8:
                        g.setColor(kekszin7);
                        break;
                    case 9:
                        g.setColor(kekszin8);
                        break;
                    case 10:
                        g.setColor(kekszin9);
                        break;
                    case 11:
                        g.setColor(kekszin10);
                        break;
                    default:
                        g.setColor(Color.black);
                        break;
                }
                g.drawString(szoveg, height + 10, 50 + i * 10);
            }
            g.setColor(Color.black);
            g.setFont(Courier12b);
            g.drawString("Számolt adatok:", height + 10, 230);
            g.setFont(Courier10b);
            szoveg = "A    : " + String.format("%.2f", A_szamolt) + " cm^2";
            g.drawString(szoveg, height + 10, 240);
            szoveg = "Ix   : " + String.format("%.2f", ix_szamolt) + " cm^4";
            g.drawString(szoveg, height + 10, 250);
            szoveg = "Iy   : " + String.format("%.2f", iy_szamolt) + " cm^4";
            g.drawString(szoveg, height + 10, 260);
            szoveg = "Sx   : " + String.format("%.2f", sx_szamolt) + " cm^3";
            g.drawString(szoveg, height + 10, 270);
            szoveg = "Sy   : " + String.format("%.2f", sy_szamolt) + " cm^3";
            g.drawString(szoveg, height + 10, 280);
            szoveg = "ex   : " + String.format("%.2f", ex_szamolt) + " cm";
            g.drawString(szoveg, height + 10, 290);
            szoveg = "ey   : " + String.format("%.2f", ey_szamolt) + " cm";
            g.drawString(szoveg, height + 10, 300);
            szoveg = "ix   : " + String.format("%.2f", inx_szamolt) + " cm";
            g.drawString(szoveg, height + 10, 310);
            szoveg = "iy   : " + String.format("%.2f", iny_szamolt) + " cm";
            g.drawString(szoveg, height + 10, 320);
            szoveg = "Kx   : " + String.format("%.2f", kx_szamolt) + " cm^3";
            g.drawString(szoveg, height + 10, 330);
            szoveg = "Ky   : " + String.format("%.2f", ky_szamolt) + " cm^3";
            g.drawString(szoveg, height + 10, 340);
            // A középvonalak
            g.setColor(Color.black);
            g.setStroke(drawingStroke);
            g.draw(linef);
            g.draw(linev);
            szoveg = "./images/szelveny/" + filenev + ".png";

            //System.out.println(" filenevki:" + szoveg);
            //if ((forgatas == 0) || (forgatas == 360)) {
            ImageIO.write(bi, "PNG", new File(szoveg));
            //}
            //keresztmetszet_szamolo(1);
        } catch (IOException ie) {
            ie.printStackTrace();
        }
    }

    public void filenevbeiro() {
        byte[] adatok;
        adatok = nev.getBytes();
        for (int i = 0; i < nev.length(); i++) {
            if (adatok[i] == '/') {
                adatok[i] = '_';
            }
            if (adatok[i] == '*') {
                adatok[i] = '_';
            }
            if (adatok[i] == 'í') {
                adatok[i] = 'i';
            }
            if (adatok[i] == 'á') {
                adatok[i] = 'a';
            }
            if (adatok[i] == 'é') {
                adatok[i] = 'e';
            }
            if (adatok[i] == 'ó') {
                adatok[i] = 'o';
            }
            if (adatok[i] == 'ö') {
                adatok[i] = 'o';
            }
            if (adatok[i] == 'ő') {
                adatok[i] = 'o';
            }
            if (adatok[i] == 'ú') {
                adatok[i] = 'u';
            }
            if (adatok[i] == 'ü') {
                adatok[i] = 'u';
            }
            if (adatok[i] == 'ű') {
                adatok[i] = 'u';
            }
            if (adatok[i] == 'Í') {
                adatok[i] = 'I';
            }
            if (adatok[i] == 'Á') {
                adatok[i] = 'A';
            }
            if (adatok[i] == 'É') {
                adatok[i] = 'E';
            }
            if (adatok[i] == 'Ó') {
                adatok[i] = 'O';
            }
            if (adatok[i] == 'Ö') {
                adatok[i] = 'O';
            }
            if (adatok[i] == 'Ő') {
                adatok[i] = 'O';
            }
            if (adatok[i] == 'Ú') {
                adatok[i] = 'U';
            }
            if (adatok[i] == 'Ü') {
                adatok[i] = 'U';
            }
            if (adatok[i] == 'Ű') {
                adatok[i] = 'U';
            }
        }
        filenev = "";
        for (int i = 0; i < adatok.length; i++) {
            filenev = filenev + (char) (adatok[i]);
        }
    }
}
