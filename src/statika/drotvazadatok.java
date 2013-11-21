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
 * @author duslaszlo
 */
public class drotvazadatok {

    String nev, filenev;                            // A drótváz neve és a file-neve    
    String parancs;                                 // A MySQL parancsok gyűjtőhelye    
    int csomopontszam = 25000;                       // A csomópontok maximális száma
    int rudszam = 36000;                             // A rudak maximális száma
    int[][] csomopont = new int[csomopontszam][4];  // A csomópont koordináták X,Y,Z
    int[][] rud = new int[rudszam][5];              // A rudak koordinátái kezdőcsp,végcsp, vastagság, a kijelzés megjelölése
    String[] rudnev = new String[rudszam];          // A rudak alkotóprofiljainak a nevei 
    int csomopontindex;                             // A beolvasott drórváz csompontjainak max. értéke
    int rudindex;                                   // A beolvasott rudak max. értéke
    int minx, miny, minz, maxx, maxy, maxz;         // A drótváz maximum és minimum értékei 
    int kozepx, kozepy, kozepz;                     // A drótváz közepe - forgási középpont
    double szog;                                    // A forgatásnál az elfordítás szöge - átmeneti tároló
    float x, y, z;                                  // Átmeneti tárolók a forgatásnál/rajzolásnál
    int[] forgatas = new int[4];                    // A forgatás mértéke X,Y,Z irányú forgatás
    int width = 500, height = 500;                  // A kijelzett kép mérete 
    int mx = 0, my = 0;                             // Az egér pozíciójának átmeneti tárolója
    int tx = 0, ty = 0;                             // Az egér pozíciójának átmeneti tárolója az eltolásnál (0-szekció,1-teljes)     
    float kepnagyitas;                              // A kép kijelzésénél a képnagyítás mértéke 0-teljes/1-szekcio
    int[] kepkozep = new int[2];                    // A kijelzett kép X-középpontja [X-0,Y-1]
    BufferedImage bi = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
    Graphics2D g = bi.createGraphics();
    int max_x = Integer.MIN_VALUE;
    int min_x = Integer.MAX_VALUE;
    static Connection co;
    static Statement st;
    static ResultSet rs;

    public void adatbeolvaso() {
        // A csomopont koordinátái
        csomopontindex = 0;
        try {
            Class.forName("com.mysql.jdbc.Driver").newInstance();
            co = DriverManager.getConnection(Global.mysql_server, Global.mysql_user, Global.mysql_password);
            st = co.createStatement();
            parancs = "SELECT x,y,z FROM csomopont where azonosito ='";
            parancs = parancs + nev + "' order by csomopont;";
            //System.out.println("SQL: " + parancs);
            rs = st.executeQuery(parancs);
            while (rs.next()) {
                csomopontindex++;
                csomopont[csomopontindex][1] = rs.getInt("x");
                csomopont[csomopontindex][2] = rs.getInt("y");
                csomopont[csomopontindex][3] = rs.getInt("z");
            }
            rs.close();
            st.close();
        } catch (InstantiationException e) {
        } catch (IllegalAccessException e) {
        } catch (ClassNotFoundException e) {
        } catch (SQLException e) {
        }
        // A köpéppont kiszámolása
        maxx = max_x;
        minx = min_x;
        for (int i = 1; i <= csomopontindex; i++) {
            if (csomopont[i][1] > maxx) {
                maxx = csomopont[i][1];
            }
            if (csomopont[i][1] < minx) {
                minx = csomopont[i][1];
            }
        }
        kozepx = minx + (maxx - minx) / 2;
        maxy = max_x;
        miny = min_x;
        for (int i = 1; i <= csomopontindex; i++) {
            if (csomopont[i][2] > maxy) {
                maxy = csomopont[i][2];
            }
            if (csomopont[i][2] < miny) {
                miny = csomopont[i][2];
            }
        }
        kozepy = miny + (maxy - miny) / 2;
        maxz = max_x;
        minz = min_x;
        for (int i = 1; i <= csomopontindex; i++) {
            if (csomopont[i][3] > maxz) {
                maxz = csomopont[i][3];
            }
            if (csomopont[i][3] < minz) {
                minz = csomopont[i][3];
            }
        }
        kozepz = minz + (maxz - minz) / 2;
        // A csomopont koordinátái
        rudindex = 0;
        try {
            Class.forName("com.mysql.jdbc.Driver").newInstance();
            co = DriverManager.getConnection(Global.mysql_server, Global.mysql_user, Global.mysql_password);
            st = co.createStatement();
            parancs = "SELECT kezdocsp,vegecsp,vastagsag,szelveny FROM rud where azonosito ='";
            parancs = parancs + nev + "' order by id;";
            //System.out.println("SQL: " + parancs);
            rs = st.executeQuery(parancs);
            while (rs.next()) {
                rudindex++;
                rud[rudindex][1] = rs.getInt("kezdocsp");
                rud[rudindex][2] = rs.getInt("vegecsp");
                rud[rudindex][3] = rs.getInt("vastagsag");
                rud[rudindex][4] = 0;
                rudnev[rudindex] = rs.getString("szelveny");
            }
            rs.close();
            st.close();
        } catch (InstantiationException e) {
        } catch (IllegalAccessException e) {
        } catch (ClassNotFoundException e) {
        } catch (SQLException e) {
        }
        filenev = "./images/drotvaz/" + nev + ".png";
        forgatas[1] = 0;
        forgatas[2] = 0;
        forgatas[3] = 0;
        kepnagyitas = 1;
    }

    public void pontforgato(int elem) {
        double atfogo;
        szog = 0;
        int kozepx_, kozepy_;
        float x_, y_;
        switch (elem) {
            case 2:
                kozepx_ = kozepx;
                kozepy_ = kozepz;
                x_ = x;
                y_ = z;
                break;
            case 3:
                kozepx_ = kozepy;
                kozepy_ = kozepz;
                x_ = y;
                y_ = z;
                break;
            default:
                kozepx_ = kozepx;
                kozepy_ = kozepy;
                x_ = x;
                y_ = y;
        }
        atfogo = Math.sqrt(Double.parseDouble(String.valueOf((kozepx_ - x_) * (kozepx_ - x_) + (kozepy_ - y_) * (kozepy_ - y_))));
        szog = Math.atan2((x_ - kozepx_), (y_ - kozepy_));
        szog += Math.toRadians(forgatas[elem]);                 
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

    public void pngfile() {
        try {
            ImageIO.write(bi, "PNG", new File(filenev));
        } catch (IOException ie) {
            ie.printStackTrace();
        }
    }

    public void pngrajz() {
        int keret = 10;
        float arany;
        int xx1, xx2, yy1, yy2;
        Font Courier16b = new Font("Courier New", Font.BOLD, 16);
        Stroke[] vonal = new BasicStroke[rudszam];
        Color[] szinek = new Color[rudszam];
        //try {            
        // TYPE_INT_ARGB specifies the image format: 8-bit RGBA packed into integer pixels                                   
        g.setColor(Color.white);
        g.fillRect(0, 0, width, height);
        // A nagyítás aránya
        if ((maxx - minx) > (maxy - miny)) {
            arany = (float) (width - (2 * keret)) / (float) (maxx - minx);
        } else {
            arany = (float) (height - (2 * keret)) / (float) (maxy - miny);
        }
        arany *= kepnagyitas;
        //System.out.println("arany:"+arany+" height:"+height+" keret:"+keret+" maxy:"+maxy+"  miny:"+miny+" rudindex:"+rudindex);
        // A rajz    
        for (int i = 1; i <= rudindex; i++) {
            if (rud[i][4] == 0) {
                if (rud[i][3] == 0) {
                    vonal[i] = new BasicStroke(1);
                    szinek[i] = Color.BLACK;
                } else {
                    vonal[i] = new BasicStroke(rud[i][3] * arany);
                    if ((rud[i][3] * arany) > 1) {
                        szinek[i] = Color.BLACK;
                    } else {
                        szinek[i] = new Color(256 - (int) (rud[i][3] * arany * 255), 256 - (int) (rud[i][3] * arany * 255), 256 - (int) (rud[i][3] * arany * 255));
                    }
                }
            } else {
                vonal[i] = new BasicStroke(2);
                szinek[i] = Color.RED;
            }
        }
        for (int i = 1; i <= rudindex; i++) {
            g.setStroke(vonal[i]);
            g.setColor(szinek[i]);
            // Elölnézeti forgatás X-Y
            x = csomopont[rud[i][1]][1];
            y = csomopont[rud[i][1]][2];
            z = csomopont[rud[i][1]][3];
            pontforgato(1);
            pontforgato(2);
            pontforgato(3);
            xx1 = (int) ((x - kozepx) * arany) + width / 2;
            yy1 = height - ((int) ((y - kozepy) * arany) + height / 2);
            xx1 += kepkozep[0];
            yy1 += kepkozep[1];
            x = csomopont[rud[i][2]][1];
            y = csomopont[rud[i][2]][2];
            z = csomopont[rud[i][2]][3];
            pontforgato(1);
            pontforgato(2);
            pontforgato(3);
            xx2 = (int) ((x - kozepx) * arany) + width / 2;
            yy2 = height - ((int) ((y - kozepy) * arany) + height / 2);
            xx2 += kepkozep[0];
            yy2 += kepkozep[1];
            //System.out.println("x1:" + xx1 + " y1:" + yy1 + "  x2:" + xx2 + "  y2:" + yy2 + " kozepx:" + kozepx + " arany:" + arany);
            g.drawLine(xx1, yy1, xx2, yy2);
        }
        g.setColor(Color.black);
        g.setFont(Courier16b);
        g.drawString(nev, 5, 15);
        //    ImageIO.write(bi, "PNG", new File(filenev));
        // } catch (IOException ie) {  ie.printStackTrace();       }
    }
}
