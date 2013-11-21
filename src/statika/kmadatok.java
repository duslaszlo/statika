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
import java.awt.TexturePaint;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
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
public class kmadatok {

    String nev, filenev;                                    // A szelvény neve és a file-neve    
    int pontszam = 10000;                                    // A kijelzésre kerülő pontok száma
    int elemszam = 100;                                     // A profil egyeneseinek és görbéinek a lehetséges száma
    int maxkontur = 20;                                     // A maximális kontúrszám
    float magassag, szelesseg, A, ix, kx, sx;
    float inx, iy, ky, sy, iny, ex, ey, fmsuly;             // A keresztmetszeti jellemzők
    float A_szamolt, ex_szamolt, ey_szamolt, ix_szamolt;
    float iy_szamolt, sx_szamolt, sy_szamolt, inx_szamolt;
    float iny_szamolt, kx_szamolt, ky_szamolt;                // A számolt keresztmetszeti jellemzők
    String parancs;                                         // A MySQL parancsok gyűjtőhelye
    int[] pontokx = new int[pontszam];                      // Átmeneti tárolóhely az ábrák kijelzéséhez
    int[] pontoky = new int[pontszam];                      // Átmeneti tárolóhely az ábrák kijelzéséhez 
    int konturszam, szamlalo;                                // Az aktuális szelvényt alkotó kontúrok száma (1-külső, 2 v. több-belső) és a pontsor pontjainak a száma
    float[][] x1 = new float[elemszam][maxkontur];          // Vonal: x1, görbe: KözéppontX
    float[][] y1 = new float[elemszam][maxkontur];          // Vonal: y1, görbe: KözéppontY
    float[][] x2 = new float[elemszam][maxkontur];          // Vonal: x2, görbe: Sugárx
    float[][] y2 = new float[elemszam][maxkontur];          // Vonal: y2, görbe: Sugáry
    float[][] kezdoszog = new float[elemszam][maxkontur];   // Vonal: 0, görbe: Kezdőszög
    float[][] vegszog = new float[elemszam][maxkontur];     // Vonal: 0, görbe: Végszög
    int[][] jelleg = new int[elemszam][maxkontur];          // Egyenes vagy görbe (E->1/G->2)
    int[][] irany = new int[elemszam][maxkontur];           // A görbék rajzolásánál az irány (1->CCW,2->CW)
    int[] indexek = new int[maxkontur];                     // A kontúrok elemszámai
    int forgatas, meretx, merety;                             // A szelvény elforgatásának alapja és a kép befoglaló méretei 
    float nagyitas, x, y;                                   // Átmeneti tárolók a forgatásnál
    double szog;                                            // A forgatásnál az elfordítás szöge - átmeneti tároló
    int mx = 0, my = 0;                                     // Az egér pozíciójának átmeneti tárolója
    Boolean tukrozesx, tukrozesy, kmkijelzes;                // A szelvény tüközése függőlegesen és vízszintesen és a km.-i jellemzők kijelzése  
    int width = 640, height = 480;                          // A kijelzett kép mérete 
    BufferedImage bi = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
    Graphics2D g = bi.createGraphics();
    static Connection co;
    static Statement st;
    static ResultSet rs;

    public void beolvas() {
        konturszam = 0;
        tukrozesx = false;
        tukrozesy = false;
        kmkijelzes = false;
        // A keresztmetszeti jellemzők beolvasása
        try {
            Class.forName("com.mysql.jdbc.Driver").newInstance();
            co = DriverManager.getConnection(Global.mysql_server, Global.mysql_user, Global.mysql_password);
            st = co.createStatement();
            parancs = "SELECT * FROM szelveny where nev ='" + nev + "';";
            forgatas = 0;
            //profil.filenevbeiro();
            //System.out.println("SQL: " + parancs);
            rs = st.executeQuery(parancs);
            A = 0;
            fmsuly = 0;
            ix = 0;
            kx = 0;
            sx = 0;
            inx = 0;
            iy = 0;
            ky = 0;
            sy = 0;
            iny = 0;
            magassag = 0;
            szelesseg = 0;
            ex = 0;
            ey = 0;
            while (rs.next()) {
                A = rs.getFloat("A");
                fmsuly = rs.getFloat("fmsuly");
                ix = rs.getFloat("Ix");
                kx = rs.getFloat("Kx");
                sx = rs.getFloat("Sx");
                inx = rs.getFloat("inx");
                iy = rs.getFloat("Iy");
                ky = rs.getFloat("Ky");
                sy = rs.getFloat("Sy");
                iny = rs.getFloat("iny");
                magassag = rs.getFloat("magassag");
                szelesseg = rs.getFloat("szelesseg");
                ex = rs.getFloat("ex") / 10;
                ey = rs.getFloat("ey") / 10;
                filenev = "./images/szelveny/" + rs.getString("filenev") + ".png";
            }
            rs.close();
            st.close();
        } catch (InstantiationException e) {
        } catch (IllegalAccessException e) {
        } catch (ClassNotFoundException e) {
        } catch (SQLException e) {
        }
        // A kontúrok megállapítása, Zsuzsika olvassa 
        // apa hagy jöjjek a géphez!        
        try {
            Class.forName("com.mysql.jdbc.Driver").newInstance();
            co = DriverManager.getConnection(Global.mysql_server, Global.mysql_user, Global.mysql_password);
            st = co.createStatement();
            parancs = "SELECT max(vonal) as vonalszam FROM kontur where nev ='" + nev + "' ";
            parancs = parancs + "union SELECT max(vonal) as vonalszam FROM kontur_e where nev ='" + nev + "'; ";
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
        //System.out.println(" Kontúrok:" + konturszam);
        for (int i = 1; i <= konturszam; i++) {
            try {
                Class.forName("com.mysql.jdbc.Driver").newInstance();
                co = DriverManager.getConnection(Global.mysql_server, Global.mysql_user, Global.mysql_password);
                st = co.createStatement();
                parancs = "SELECT * FROM kontur where nev = '" + nev + "' and vonal=" + i + " ";
                parancs = parancs + "UNION SELECT * FROM kontur_e where nev = '" + nev + "' and vonal=" + i + " order by sorszam;";
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
    }

    public void pontforgato() {
        double atfogo;
        float kozepx = szelesseg / 2;
        float kozepy = magassag / 2;
        szog = 0;
        atfogo = Math.sqrt(Double.parseDouble(String.valueOf((kozepx - x) * (kozepx - x) + (kozepy - y) * (kozepy - y))));
        if ((kozepx == x) && (kozepy > y)) {
            szog = Math.toRadians(90);
        } else if ((kozepx == x) && (kozepy < y)) {
            szog = Math.toRadians(270);
        } else if ((kozepy == y) && (kozepx > x)) {
            szog = Math.toRadians(180);
        } else if ((kozepy == y) && (kozepx < x)) {
            szog = 0;
        } else if ((y < kozepy) && (x > kozepx)) {
            szog = Math.asin((kozepy - y) / atfogo);
        } else if ((y < kozepy) && (x < kozepx)) {
            szog = Math.toRadians(90) + Math.asin((kozepx - x) / atfogo);
        } else if ((y > kozepy) && (x < kozepx)) {
            szog = Math.toRadians(180) + Math.asin((y - kozepy) / atfogo);
        } else if ((y > kozepy) && (x > kozepx)) {
            szog = Math.toRadians(270) + Math.asin((x - kozepx) / atfogo);
        }
        //System.out.println(" regiszog:" + (int)Math.toDegrees(szog) + " Elforgatás:" + forgatas);
        szog += Math.toRadians(forgatas + 90);
        //System.out.println(" ujszog:" + (int)Math.toDegrees(szog));    
        x = kozepx + Float.parseFloat(String.valueOf(Math.sin(szog) * atfogo));
        y = kozepy + Float.parseFloat(String.valueOf(Math.cos(szog) * atfogo));
    }

    public void mirrorx(float arany) {
        for (int i = 0; i <= szamlalo; i++) {
            pontokx[i] = (int) ((szelesseg / 2) * arany) - (pontokx[i] - (int) ((szelesseg / 2) * arany));
        }
        //System.out.println(" Mirrorx...");
    }

    public void mirrory(float arany) {
        for (int i = 0; i <= szamlalo; i++) {
            pontoky[i] = (int) ((magassag / 2) * arany) - (pontoky[i] - (int) ((magassag / 2) * arany));
        }
        //System.out.println(" Mirrory...");
    }

    public void pngfile() {
        try {
            ImageIO.write(bi, "PNG", new File(filenev));
        } catch (IOException ie) {
            ie.printStackTrace();
        }
    }

    public void pngrajz(float arany) {
        String szoveg;
        float adat, xx1, xx2, yy1, yy2, r1, r2;
        int max_x = Integer.MIN_VALUE;
        int min_x = Integer.MAX_VALUE;
        int max_y = Integer.MIN_VALUE;
        int min_y = Integer.MAX_VALUE;
        int kozepvonaly = 0;
        int kozepvonalx = 0;
        Color fekete = new Color(1,1,1);
        //try {
        //forgatas +=90;
        // A sraffozás
        BufferedImage sraffozas =
                new BufferedImage(20, 20, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = sraffozas.createGraphics();
        g2.setColor(Color.white);
        g2.fillRect(0, 0, 50, 50);
        g2.setColor(Color.GRAY);
        g2.drawLine(0, 0, 50, 50); // \
        g2.drawLine(0, 50, 50, 0); // /

        // TYPE_INT_ARGB specifies the image format: 8-bit RGBA packed into integer pixels                       

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
        // A profil metszeti ábrája                
            /*g.setColor(Color.yellow);
         g.drawRect(keret, keret, height - (keret * 2), height - (keret * 2)); */
        g.setColor(Color.LIGHT_GRAY);
        g.drawLine(height, 0, height, height);
        //System.out.println(" magassag:" + magassag + " szelesseg" + szelesseg + "  arany:" + arany + " eltolas" + eltolasx);                              
        if (konturszam > 0) {
            for (int i = 1; i <= konturszam; i++) {
                szamlalo = 0;
                for (int j = 1; j <= indexek[i]; j++) {
                    xx1 = x1[j][i];
                    yy1 = y1[j][i];
                    xx2 = x2[j][i];
                    yy2 = y2[j][i];
                    r1 = kezdoszog[j][i];
                    r2 = vegszog[j][i];
                    //System.out.println(" j:" + j + "  Jelleg:'" + jelleg[j] + "' ");
                    if (jelleg[j][i] == 1) {
                        // egyenes vonalak
                        if ((forgatas != 0) && (forgatas != 360)) {
                            x = xx1;
                            y = yy1;
                            pontforgato();
                            xx1 = x;
                            yy1 = y;
                            x = xx2;
                            y = yy2;
                            pontforgato();
                            xx2 = x;
                            yy2 = y;
                        }
                        if (j == 1) {
                            szamlalo++;
                            pontokx[szamlalo] = (int) (xx1 * arany);
                            pontoky[szamlalo] = (int) (yy1 * arany);
                            //System.out.println(" szamlalo:" + szamlalo + "  x1:" + x1[j][i] + "  arany:" + arany + " eltolasx:" + eltolasx+" pontx:"+pontokx[szamlalo]+" ponty:"+pontoky[szamlalo]);
                        }
                        szamlalo++;
                        pontokx[szamlalo] = (int) (xx2 * arany);
                        pontoky[szamlalo] = (int) (yy2 * arany);
                        //System.out.println(" szamlalo:" + szamlalo + "  x1:" + x1[j][i] + "  arany:" + arany + " eltolasx:" + eltolasx+" pontx:"+pontokx[szamlalo]+" ponty:"+pontoky[szamlalo]);
                    } else {
                        // A görbe vonalak
                        if ((forgatas != 0) && (forgatas != 360)) {
                            x = xx1;
                            y = yy1;
                            pontforgato();
                            xx1 = x;
                            yy1 = y;
                            //System.out.println("I:" + i + " J:" + j + " Forgatás:" + forgatas + " r1_elötte:" + r1 + "  r2_elotte:" + r2);
                            r1 -= forgatas;
                            r2 -= forgatas;
                            //System.out.println(" r1_utána:" + r1 + "  r2_utána:" + r2);
                        }
                        //System.out.println("X1:" + x1[j] + " y1:" + y1[j] + " X2:" + x2[j] + " y2:" + y2[j] + " kezdoszog:" + kezdoszog[j] + "  Végszög:" + vegszog[j] + "  Irány:" + irany[j]);
                        if ((r1 == 0) && (r2 == 360)) {
                            //Teljes kör                                
                            for (int k = 0; k <= 360; k++) {
                                szamlalo++;
                                pontokx[szamlalo] = (int) ((xx1 + xx2 * Math.cos(Math.toRadians(k))) * arany);
                                pontoky[szamlalo] = (int) ((yy1 + yy2 * Math.sin(Math.toRadians(k))) * arany);
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
                                    pontokx[szamlalo] = (int) ((xx1 + xx2 * Math.cos(Math.toRadians(k))) * arany);
                                    pontoky[szamlalo] = (int) ((yy1 + yy2 * Math.sin(Math.toRadians(k))) * arany);
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
                                    pontokx[szamlalo] = (int) ((xx1 + xx2 * Math.cos(Math.toRadians(k))) * arany);
                                    pontoky[szamlalo] = (int) ((yy1 + yy2 * Math.sin(Math.toRadians(k))) * arany);
                                    //System.out.println(" k:" + k + " x1:" + x1[j][i] + "  y1:" + y1[j][i] + " cos k*x2:" + (x1[j][i] + x2[j][i] * Math.cos(k * (Math.PI / 180))) + " sin k*y2:" + (y1[j][i] + y2[j][i] * Math.sin(k * (Math.PI / 180))));
                                }
                            }
                        }
                    }
                }
                pontokx[0] = pontokx[szamlalo];
                pontoky[0] = pontoky[szamlalo];
                /*for (int j = 0; j <= szamlalo; j++) {
                 pontokx[j] = (int)(pontokx[j] * arany + eltolasx);
                 pontoky[j] = (int)(pontoky[j] * arany + eltolasy);
                 }*/
                /*
                 System.out.println();
                 System.out.println(" Régi pontsor");
                 for (int j = 1; j <= szamlalo; j++) {
                 System.out.println(" pont:" + j + " x:" + pontokx[j] + "  y:" + pontoky[j]);
                 }
                 */
                //g1.drawLine(0, 0, (int) (szelesseg), (int) (magassag));
                //if ((forgatas != 0) && (forgatas != 360)) { forgato(forgatas); }
                if (tukrozesx) {
                    mirrorx(arany);
                }
                if (tukrozesy) {
                    mirrory(arany);
                }
                // A kép méretének meghatározása max/min
                for (int k = 0; k <= szamlalo; k++) {
                    if ((int) (pontokx[k] / arany) < min_x) {
                        min_x = (int) (pontokx[k] / arany);
                    }
                    if ((int) (pontoky[k] / arany) < min_y) {
                        min_y = (int) (pontoky[k] / arany);
                    }
                    if ((int) (pontokx[k] / arany) > max_x) {
                        max_x = (int) (pontokx[k] / arany);
                    }
                    if ((int) (pontoky[k] / arany) > max_y) {
                        max_y = (int) (pontoky[k] / arany);
                    }
                }
                //System.out.println("diffx:"+((height / 2) - kozepx)+"  Diffy:"+((height / 2) - kozepy)+" Kozepx:"+kozepx+"  kozepy"+kozepy);
                for (int j = 0; j <= szamlalo; j++) {
                    pontokx[j] += ((height / 2) - (szelesseg / 2) * arany);
                    pontoky[j] += ((height / 2) - (magassag / 2) * arany);
                }
                /*
                 System.out.println();
                 System.out.println(" Új pontsor");
                 for (int j = 1; j <= szamlalo; j++) {
                 System.out.println(" pont:" + j + " x:" + pontokx[j] + "  y:" + pontoky[j]);
                 }
                 */
                if (i == 1) {
                    g.setColor(Color.cyan);
                    Rectangle2D rect = new Rectangle2D.Double(0, 0, 5, 5);
                    g.setPaint(new TexturePaint(sraffozas, rect));
                } else {
                    g.setColor(Color.white);
                }
                g.fillPolygon(pontokx, pontoky, szamlalo);
                g.setColor(fekete);
                Stroke vastagvonal = new BasicStroke(2);
                g.setStroke(vastagvonal);
                g.drawPolygon(pontokx, pontoky, szamlalo);
                for (int k = 0; k <= szamlalo; k++) {
                    pontokx[k] = (int) (pontokx[k] / arany) * 1;
                    pontoky[k] = (int) (pontoky[k] / arany) * 1;
                }
            }
            meretx = max_x - min_x;
            merety = max_y - min_y;
            meretx += 5;
            merety += 5;
            //System.out.println("Meretx:" + meretx + "  Merety:" + merety);
        }
        // A segédvonalak        
         Stroke vekonyvonal = new BasicStroke(1);
         g.setStroke(vekonyvonal);
         Color sotetszurke = new Color(60, 60, 60);
         Color kozepszurke = new Color(128, 128, 128);
         Color vilagosszurke = new Color(190, 190, 190);
         float arany1 = arany;
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
         g.drawString("Rögzített adatok:", height + 10, 50);
         g.setFont(Courier10b);
         szoveg = "Szél.: " + String.format("%.2f", szelesseg) + " mm";
         g.drawString(szoveg, height + 10, 60);
         szoveg = "Mag. : " + String.format("%.2f", magassag) + " mm";
         g.drawString(szoveg, height + 10, 70);
         szoveg = "A    : " + String.format("%.2f", A) + " cm^2";
         g.drawString(szoveg, height + 10, 80);
         szoveg = "Ix   : " + String.format("%.2f", ix) + " cm^4";
         g.drawString(szoveg, height + 10, 90);
         szoveg = "Iy   : " + String.format("%.2f", iy) + " cm^4";
         g.drawString(szoveg, height + 10, 100);
         szoveg = "Sx   : " + String.format("%.2f", sx) + " cm^3";
         g.drawString(szoveg, height + 10, 110);
         szoveg = "Sy   : " + String.format("%.2f", sy) + " cm^3";
         g.drawString(szoveg, height + 10, 120);
         szoveg = "ex   : " + String.format("%.2f", ex) + " cm";
         g.drawString(szoveg, height + 10, 130);
         szoveg = "ey   : " + String.format("%.2f", ey) + " cm";
         g.drawString(szoveg, height + 10, 140);
         szoveg = "ix   : " + String.format("%.2f", inx) + " cm";
         g.drawString(szoveg, height + 10, 150);
         szoveg = "iy   : " + String.format("%.2f", iny) + " cm";
         g.drawString(szoveg, height + 10, 160);
         szoveg = "Kx   : " + String.format("%.2f", kx) + " cm^3";
         g.drawString(szoveg, height + 10, 170);
         szoveg = "Ky   : " + String.format("%.2f", ky) + " cm^3";
         g.drawString(szoveg, height + 10, 180);         
        if (kmkijelzes) {
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
            // A súlypont középvonalai
            g.setColor(Color.blue);
            g.setStroke(drawingStroke);
            // Az első pont meghatározása vízszintesen - > kozepvonalx és függőlegesen -> kozepvonaly
            kozepvonalx = height;
            kozepvonaly = height;
            for (int j = 0; j < width; j++) {
                for (int k = 0; k < height; k++) {
                    Color originalColor = new Color(bi.getRGB(j, k));
                    //System.out.println("j:" + j + " k:" + k + " red:" + red + "  green:" + green + " blue:" + blue + " középvonalx:" + kozepvonalx + " középvonaly:" + kozepvonaly);
                    //if (originalColor == fekete) {
                    if ((originalColor.getRed() == 1) && (originalColor.getGreen() == 1) && (originalColor.getBlue() == 1)) {
                        if (j < kozepvonalx) {
                            kozepvonalx = j;
                        }
                        if (k < kozepvonaly) {
                            kozepvonaly = k;
                        }
                    }
                }
            }
            //int kozepvonaly = (int) (ey_szamolt * 10 * arany + height / 2 - (szelesseg / 2) * arany);                        
            //System.out.println("  középx:" + kozepvonalx + "  középy:" + kozepvonaly);
            kozepvonalx += (int) (ey_szamolt * 10 * arany);
            kozepvonaly += (int) (ex_szamolt * 10 * arany);
            //System.out.println(" ex:" + ex_szamolt * 10 + " ey:" + ey_szamolt * 10 + "  középx:" + kozepvonalx + "  középy:" + kozepvonaly + "  szélesség:" + szelesseg + "  Magasság:" + magassag + "  Arány:" + arany);
            Line2D centerlinex = new Line2D.Double(kozepvonalx, 0, kozepvonalx, height);
            Line2D centerliney = new Line2D.Double(0, kozepvonaly, height, kozepvonaly);
            g.draw(centerlinex);
            g.draw(centerliney);
            g.setFont(Courier16b);
            g.drawString("ex", 5, kozepvonaly + 15);
            g.drawString("ex", height - 25, kozepvonaly - 5);
            g.drawString("ey", kozepvonalx + 10, 15);
            g.drawString("ey", kozepvonalx - 20, height - 10);
            g.setColor(Color.black);
            g.setFont(Courier10b);
        }
        /*g.setColor(Color.red);
         if (A!=A_szamolt) { g.drawLine(height+50, 241, width-50, 241);}
         if (ix!=ix_szamolt) { g.drawLine(height+50, 251, width-50, 251);}
         if (iy!=iy_szamolt) { g.drawLine(height+50, 261, width-50, 261);}
         if (sx!=sx_szamolt) { g.drawLine(height+50, 271, width-50, 271);}
         if (sy!=sy_szamolt) { g.drawLine(height+50, 281, width-50, 281);}
         if (ex!=ex_szamolt) { g.drawLine(height+50, 291, width-50, 291);}
         if (ey!=ey_szamolt) { g.drawLine(height+50, 301, width-50, 301);}
         if (inx!=inx_szamolt) { g.drawLine(height+50, 311, width-50, 311);}
         if (iny!=iny_szamolt) { g.drawLine(height+50, 321, width-50, 321);}
         if (kx!=kx_szamolt) { g.drawLine(height+50, 331, width-50, 331);}
         if (ky!=ky_szamolt) { g.drawLine(height+50, 341, width-50, 341);} */
        szoveg = "A forgatás szöge: " + forgatas + " fok";
        g.drawString(szoveg, height + 5, height - 20);
        // A középvonalak
        g.setColor(Color.black);
        g.setStroke(drawingStroke);
        g.draw(linef);
        g.draw(linev);
        /*
         szoveg = "./images/szelveny/" + filenev;
         if ((forgatas != 0) && (forgatas != 360)) {
         szoveg = szoveg + "_f";
         }
         szoveg = szoveg + ".png";
         //System.out.println(" filenevki:" + szoveg);
         //if ((forgatas == 0) || (forgatas == 360)) {
         ImageIO.write(bi, "PNG", new File(szoveg));
         //}*/

        //if (forgatas == 0) {  keresztmetszet_szamolo(1);   }

        //} catch (IOException ie) { ie.printStackTrace(); }
    }

    public void keresztmetszet_szamolo(int iras) {
        String szoveg;
        int szorzo = 10;   // Ezzel az értékkel lesz felszorozva (és elosztva) az ábra (és az értékek)
        double meret1, meret2;
        float xx1, xx2, yy1, yy2, r1, r2;
        int min_x = Integer.MAX_VALUE;
        int min_y = Integer.MAX_VALUE;
        kmkijelzes = true;
        //try {
        meret1 = meretx;
        meret2 = merety;
        //System.out.println("Meret1:"+ meret1+"  meret2:"+meret2);
        szorzo = 10;
        //szorzo = 3;
        if ((meret1 * meret2) > 400000) {
            szorzo = 2;
        }
        // A keresztmetszeti jellemzők számításához szükséges kép
        BufferedImage bi1 = new BufferedImage((int) (meret1 * szorzo), (int) (meret2 * szorzo), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g1 = bi1.createGraphics();
        g1.setColor(Color.white);
        g1.fillRect(0, 0, (int) (meret1 * szorzo), (int) (meret2 * szorzo));
        if (konturszam > 0) {
            for (int i = 1; i <= konturszam; i++) {
                szamlalo = 0;
                for (int j = 1; j <= indexek[i]; j++) {
                    xx1 = x1[j][i];
                    yy1 = y1[j][i];
                    xx2 = x2[j][i];
                    yy2 = y2[j][i];
                    r1 = kezdoszog[j][i];
                    r2 = vegszog[j][i];
                    //System.out.println(" j:" + j + "  Jelleg:'" + jelleg[j] + "' ");
                    if (jelleg[j][i] == 1) {
                        // egyenes vonalak
                        if ((forgatas != 0) && (forgatas != 360)) {
                            x = xx1;
                            y = yy1;
                            pontforgato();
                            xx1 = x;
                            yy1 = y;
                            x = xx2;
                            y = yy2;
                            pontforgato();
                            xx2 = x;
                            yy2 = y;
                        }
                        if (j == 1) {
                            szamlalo++;
                            pontokx[szamlalo] = (int) (xx1 * szorzo);
                            pontoky[szamlalo] = (int) (yy1 * szorzo);
                            //System.out.println(" szamlalo:" + szamlalo + "  x1:" + x1[j][i] + "  arany:" + arany + " eltolasx:" + eltolasx+" pontx:"+pontokx[szamlalo]+" ponty:"+pontoky[szamlalo]);
                        }
                        szamlalo++;
                        pontokx[szamlalo] = (int) (xx2 * szorzo);
                        pontoky[szamlalo] = (int) (yy2 * szorzo);
                        //System.out.println(" szamlalo:" + szamlalo + "  x1:" + x1[j][i] + "  arany:" + arany + " eltolasx:" + eltolasx+" pontx:"+pontokx[szamlalo]+" ponty:"+pontoky[szamlalo]);
                    } else {
                        // A görbe vonalak
                        if ((forgatas != 0) && (forgatas != 360)) {
                            x = xx1;
                            y = yy1;
                            pontforgato();
                            xx1 = x;
                            yy1 = y;
                            r1 -= forgatas;
                            r2 -= forgatas;
                        }
                        //System.out.println("X1:" + x1[j][i] + " y1:" + y1[j][i] + " X2:" + x2[j][i] + " y2:" + y2[j][i] + " kezdoszog:" + kezdoszog[j][i] + "  Végszög:" + vegszog[j][i] + "  Irány:" + irany[j][i]);
                        if ((r1 == 0) && (r2 == 360)) {
                            //Teljes kör                                
                            for (int k = 0; k <= 360; k++) {
                                szamlalo++;
                                pontokx[szamlalo] = (int) ((xx1 * szorzo + xx2 * szorzo * Math.cos(Math.toRadians(k))));
                                pontoky[szamlalo] = (int) ((yy1 * szorzo + yy2 * szorzo * Math.sin(Math.toRadians(k))));
                                //System.out.println(" szamlalo:" + szamlalo + " x1:" + x1[j][i] + " y1:" + y1[j][i]+ " x2:" + x2[j][i] + " y2:" + y2[j][i] + " eltolasx:" + eltolasx+" pontx:"+pontokx[szamlalo]+" ponty:"+pontoky[szamlalo]);
                                //System.out.println(" k:" + k +" x1:"+ x1[j][i]+"  y1:"+ y1[j][i]+ " cos k*x2:" + (x1[j][i] + x2[j][i] * Math.cos(k * (Math.PI / 180)))+ " sin k*y2:" + (y1[j][i] + y2[j][i] * Math.sin(k * (Math.PI / 180))));
                            }
                        } else {
                            if (irany[j][i] == 1) {
                                // Az óra járásával megegyező irány (CW)                                    
                                if (r2 < r1) {
                                    r2 += 360;
                                }
                                //System.out.println(" kezdoszog:" + kezdoszog[j][i] + "  Végszög:" + vegszog[j][i] + "  Irány:" + irany[j][i]);
                                for (int k = (int) r1; k <= (int) r2; k++) {
                                    szamlalo++;
                                    pontokx[szamlalo] = (int) ((xx1 * szorzo + xx2 * szorzo * Math.cos(Math.toRadians(k))));
                                    pontoky[szamlalo] = (int) ((yy1 * szorzo + yy2 * szorzo * Math.sin(Math.toRadians(k))));
                                    //System.out.println(" k:" + k + " x1:" + x1[j][i] + "  y1:" + y1[j][i] + " cos k*x2:" + (x1[j][i] + x2[j][i] * Math.cos(k * (Math.PI / 180))) + " sin k*y2:" + (y1[j][i] + y2[j][i] * Math.sin(k * (Math.PI / 180))));
                                }
                            } else {
                                // Az óra járásával ellentétes irány (CCW)
                                if (r2 > r1) {
                                    r2 -= 360;
                                }
                                //System.out.println(" kezdoszog:" + kezdoszog[j][i] + "  Végszög:" + vegszog[j][i] + "  Irány:" + irany[j][i]);
                                //System.out.println(" kezdoszog:" + kezdoszog[j] + " vegszog:" + vegszog[j]);
                                for (int k = (int) r1; k >= (int) r2; k--) {
                                    szamlalo++;
                                    pontokx[szamlalo] = (int) ((xx1 * szorzo + xx2 * szorzo * Math.cos(Math.toRadians(k))));
                                    pontoky[szamlalo] = (int) ((yy1 * szorzo + yy2 * szorzo * Math.sin(Math.toRadians(k))));
                                    //System.out.println(" k:" + k + " x1:" + x1[j][i] + "  y1:" + y1[j][i] + " cos k*x2:" + (x1[j][i] + x2[j][i] * Math.cos(k * (Math.PI / 180))) + " sin k*y2:" + (y1[j][i] + y2[j][i] * Math.sin(k * (Math.PI / 180))));
                                }
                            }
                        }
                    }
                }
                pontokx[0] = pontokx[szamlalo];
                pontoky[0] = pontoky[szamlalo];
                //if ((forgatas != 0) && (forgatas != 360)) { forgato(forgatas); }
                if (tukrozesx) {
                    mirrorx(1);
                }
                if (tukrozesy) {
                    mirrory(1);
                }
                if (i == 1) {
                    // A kép minimális koordinátájának meghatározása 
                    for (int k = 0; k <= szamlalo; k++) {
                        if (pontokx[k] < min_x) {
                            min_x = pontokx[k];
                        }
                        if (pontoky[k] < min_y) {
                            min_y = pontoky[k];
                        }
                    }
                }
                if (forgatas != 0) {
                    for (int j = 0; j <= szamlalo; j++) {
                        pontokx[j] -= min_x;
                        pontoky[j] -= min_y;
                    }
                }
                if (i == 1) {
                    g1.setColor(Color.BLACK);
                } else {
                    g1.setColor(Color.white);
                }
                if (szorzo != 10) {
                    for (int k = 1; k <= szamlalo; k++) {
                        //pontokx[k] = (int)(pontokx[k]*(szamlalo/10));
                        //pontoky[k] = (int)(pontoky[k]*(szamlalo/10));
                    }
                }
                g1.fillPolygon(pontokx, pontoky, szamlalo);
            }
        }
        //szoveg = "./images/szelveny/szelveny.png";
        //if (iras == 1) { ImageIO.write(bi1, "PNG", new File(szoveg));  }
        // A terület-számítás
        A_szamolt = 0;
        for (int i = 0; i < (int) (meret1 * szorzo); i++) {
            for (int j = 0; j < (int) (meret2 * szorzo); j++) {
                Color originalColor = new Color(bi1.getRGB(i, j));
                int r = originalColor.getRed();
                int g = originalColor.getGreen();
                int b = originalColor.getBlue();
                if (r + g + b == 0) {
                    A_szamolt++;
                }
            }
        }
        // A súlyponti koordináták
        sx_szamolt = 0;
        sy_szamolt = 0;
        for (int i = 0; i < (int) (meret1 * szorzo); i++) {
            for (int j = 0; j < (int) (meret2 * szorzo); j++) {
                Color originalColor = new Color(bi1.getRGB(i, j));
                int r = originalColor.getRed();
                int g = originalColor.getGreen();
                int b = originalColor.getBlue();
                if (r + g + b == 0) {
                    sx_szamolt += j;
                    sy_szamolt += i;
                }
            }
        }
        ex_szamolt = sx_szamolt / A_szamolt;
        ey_szamolt = sy_szamolt / A_szamolt;
        sx_szamolt = 0;
        sy_szamolt = 0;
        // Statikai nyomaték és Inercianyomaték
        for (int i = 0; i < (int) (meret1 * szorzo); i++) {
            for (int j = 0; j < (int) (meret2 * szorzo); j++) {
                Color originalColor = new Color(bi1.getRGB(i, j));
                int r = originalColor.getRed();
                int g = originalColor.getGreen();
                int b = originalColor.getBlue();
                if (r + g + b == 0) {
                    sx_szamolt += Math.abs(j - ex_szamolt);
                    sy_szamolt += Math.abs(i - ey_szamolt);
                    ix_szamolt += Math.abs((j - ex_szamolt) * (j - ex_szamolt));
                    iy_szamolt += Math.abs((i - ey_szamolt) * (i - ey_szamolt));
                }
            }
        }
        A_szamolt /= 100 * szorzo * szorzo;
        ex_szamolt /= 10 * szorzo;
        ey_szamolt /= 10 * szorzo;
        sx_szamolt /= 1000 * 2 * szorzo * szorzo * szorzo;  // Csak a fél szelvényre vonatkozik a statitai nyomaték!!
        sy_szamolt /= 1000 * 2 * szorzo * szorzo * szorzo;  // Csak a fél szelvényre vonatkozik a statitai nyomaték!!
        ix_szamolt /= 10000 * szorzo * szorzo * szorzo * szorzo;
        iy_szamolt /= 10000 * szorzo * szorzo * szorzo * szorzo;
        inx_szamolt = Float.parseFloat(String.valueOf(Math.sqrt(Double.parseDouble(String.valueOf(ix_szamolt / A_szamolt)))));
        iny_szamolt = Float.parseFloat(String.valueOf(Math.sqrt(Double.parseDouble(String.valueOf(iy_szamolt / A_szamolt)))));
        kx_szamolt = ix_szamolt / ex_szamolt;
        ky_szamolt = iy_szamolt / ey_szamolt;
        //System.out.println("Terület:" + A_szamolt + "cm^2, Sx:" + sx_szamolt + "cm^3, Sy:" + sy_szamolt + "cm^3, Iy:" + ix_szamolt + "cm^4, Iy:" + iy_szamolt + "cm^4");
        //} catch (IOException ie) { ie.printStackTrace();  }
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
        }
        filenev = "";
        for (int i = 0; i < adatok.length; i++) {
            filenev = filenev + (char) (adatok[i]);
        }
    }
}
