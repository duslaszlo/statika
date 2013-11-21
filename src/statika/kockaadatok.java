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
import java.sql.ResultSet;
import java.sql.Statement;

/**
 *
 * @author SD-LEAP
 */
public class kockaadatok {

    String nev, filenev;                                  // A szelvény neve és a file-neve    
    String parancs;                                       // A MySQL parancsok gyűjtőhelye 
    int maxelem = 20;                                     // A szekciók maximális száma
    int maxelem1 = 200;                                   // A szekciók * közök maximális száma
    int szekcioszam;                                      // A drótvázon belüli szekciók száma
    int kozszam;                                          // A drótvázon belüli közök száma
    int csomopontszam = 20000;                             // A csomópontok maximális száma
    int rudszam = 20000;                                   // A rudak maximális száma
    int[][] csomopont = new int[csomopontszam][4];    // A drótváz koordinátái szekcio(0),x(1),y(2),z(3)
    int[][] rud = new int[rudszam][7];                    // A drótváz rúdjainak (szekciószám(0)) kezdő(1) és végcsomópontjai(2),vastagság(3), a kijelzés megjelölése(0/1/2)(4),koz(5),tipus(6)
    int csomopontindex, rudindex;                         // A beolvasott drórváz csompontjainak max. értéke & az éppen kiválasztott szekció sorszáma
    float[][] limitek = new float[3][2];                  // A drótváz maximum és minimum értékei [x(0),y(1),z(2)], 
    // [minimum(0)/maximum(1)] (minx, miny, minz, maxx, maxy, maxz)
    int mx0 = 0, my0 = 0, mx1 = 0, my1 = 0;                // Az egér pozíciójának átmeneti tárolója a forgatásnál (0-szekció,1-teljes) 
    int tx0 = 0, ty0 = 0, tx1 = 0, ty1 = 0;                // Az egér pozíciójának átmeneti tárolója az eltolásnál (0-szekció,1-teljes) 
    int[] kepkozep = new int[2];                           // A kijelzett kép X-középpontja   [X-0,Y-1]
    // A szekcióelem rajza
    int width = 800, height = 600;                        // A kijelzett kép mérete 
    BufferedImage bi = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
    Graphics2D g = bi.createGraphics();
    // A rajzoláshoz szükséges változók
    double szog;                                          // A forgatásnál az elfordítás szöge - átmeneti tároló
    float x, y, z;                                        // Átmeneti tárolók a forgatásnál/rajzolásnál
    int[] forgatas = new int[4];                          // A forgatás mértéke X,Y,Z irányú forgatás 
    float kozepx, kozepy, kozepz;                           // A drótváz közepe - forgási középpont
    float kepnagyitas;                                   // A kép kijelzésénél a képnagyítás mértéke 0-teljes/1-szekcio
    static Connection co;
    static Statement st;
    static ResultSet rs;
    int adat = 0;

    public void kozeppont_szamolo() {
        // A köpéppont kiszámolása   
        float diff = 0;
        // limitek[1][0][1] --> maxx
        // limitek[1][0][0] --> minx
        limitek[0][1] = Integer.MIN_VALUE;
        limitek[0][0] = Integer.MAX_VALUE;
        for (int i = 1; i <= rudindex; i++) {
            if (csomopont[rud[i][1]][0] > limitek[0][1]) {
                limitek[0][1] = csomopont[rud[i][1]][0];
            }
            if (csomopont[rud[i][1]][0] < limitek[0][0]) {
                limitek[0][0] = csomopont[rud[i][1]][0];
            }
            if (csomopont[rud[i][2]][0] > limitek[0][1]) {
                limitek[0][1] = csomopont[rud[i][2]][0];
            }
            if (csomopont[rud[i][2]][0] < limitek[0][0]) {
                limitek[0][0] = csomopont[rud[i][2]][0];
            }
        }
        kozepx = limitek[0][0] + (limitek[0][1] - limitek[0][0]) / 2;
        // limitek[1][1][1] --> maxy
        // limitek[1][1][0] --> miny
        limitek[1][1] = Integer.MIN_VALUE;
        limitek[1][0] = Integer.MAX_VALUE;
        for (int i = 1; i <= rudindex; i++) {
            if (csomopont[rud[i][1]][1] > limitek[1][1]) {
                limitek[1][1] = csomopont[rud[i][1]][1];
            }
            if (csomopont[rud[i][1]][1] < limitek[1][0]) {
                limitek[1][0] = csomopont[rud[i][1]][1];
            }
            if (csomopont[rud[i][2]][1] > limitek[1][1]) {
                limitek[1][1] = csomopont[rud[i][2]][1];
            }
            if (csomopont[rud[i][2]][1] < limitek[1][0]) {
                limitek[1][0] = csomopont[rud[i][2]][1];
            }
        }
        kozepy = limitek[1][0] + (limitek[1][1] - limitek[1][0]) / 2;

        // limitek[1][2][1] --> maxz
        // limitek[1][2][0] --> minz
        limitek[2][1] = Integer.MIN_VALUE;
        limitek[2][0] = Integer.MAX_VALUE;
        for (int i = 1; i <= rudindex; i++) {
            if (csomopont[rud[i][1]][2] > limitek[2][1]) {
                limitek[2][1] = csomopont[rud[i][1]][2];
            }
            if (csomopont[rud[i][1]][2] < limitek[2][0]) {
                limitek[2][0] = csomopont[rud[i][1]][2];
            }
            if (csomopont[rud[i][2]][2] > limitek[2][1]) {
                limitek[2][1] = csomopont[rud[i][2]][2];
            }
            if (csomopont[rud[i][2]][2] < limitek[2][0]) {
                limitek[2][0] = csomopont[rud[i][2]][2];
            }
        }
        kozepz = limitek[2][0] + (limitek[2][1] - limitek[2][0]) / 2;
        // Középrehozás
            /*System.out.println();
         System.out.println("Tetel:" + tetel);
         System.out.println("minx:" + limitek[1][0][0] + "  maxx:" + limitek[1][0][1]
         + "  miny:" + limitek[1][1][0] + "  maxy:" + limitek[1][1][1]
         + "  minz:" + limitek[1][2][0] + "  maxz:" + limitek[1][2][1]);
         System.out.println("kozepx:" + kozepx[1] + "   kozepy:" + kozepy[1] + "   kozepz:" + kozepz[1]);*/
        diff = limitek[0][0];
        kozepx -= diff;
        /*limitek[1][0][0] -= diff;
         limitek[1][0][1] -= diff;   */
        diff = limitek[1][0];
        kozepy -= diff;
        /*limitek[1][1][0] -= diff;
         limitek[1][1][1] -= diff;  */
        diff = limitek[2][0];
        kozepz -= diff;
        /*limitek[1][2][0] -= diff;
         limitek[1][2][1] -= diff;    
        System.out.println("minx:" + limitek[0][0] + "  maxx:" + limitek[0][1]
         + "  miny:" + limitek[1][0] + "  maxy:" + limitek[1][1]
         + "  minz:" + limitek[2][0] + "  maxz:" + limitek[2][1]);
         System.out.println("kozepx:" + kozepx + "   kozepy:" + kozepy + "   kozepz:" + kozepz);*/

        /*System.out.println();
         System.out.println("Tetel:" + tetel);
         System.out.println("Teljes szerkezet:");
         System.out.println("kozepx:" + kozepx[0] + "   kozepy:" + kozepy[0] + "   kozepz:" + kozepz[0]);
         System.out.println("Szekcio:");*/
         //System.out.println("kozepx:" + kozepx + "   kozepy:" + kozepy + "   kozepz:" + kozepz);
         
    }

    public void pontforgato(int elem) {
        double atfogo;
        szog = 0;
        float kozepx_, kozepy_;
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
        kozeppont_szamolo();
        // A drótváz maximum és minimum értékei  [teljes(0)/szekcio(a többi)], [x(0),y(1),z(2)], [minimum(0)/maximum(1)] 
        // limitek[tetel][0][1] --> maxx
        // limitek[tetel][0][0] --> minx
        // limitek[tetel][1][1] --> maxy
        // limitek[tetel][1][0] --> miny
        if ((limitek[0][1] - limitek[0][0]) > (limitek[1][1] - limitek[1][0])) {
            arany = (float) (width - (2 * keret)) / (limitek[0][1] - limitek[0][0]);
        } else {
            arany = (float) (height - (2 * keret)) / (limitek[1][1] - limitek[1][0]);
        }
        arany *= kepnagyitas;
        //System.out.println("arany:"+arany+" height:"+height+" keret:"+keret+" maxy:"+maxy+"  miny:"+miny+" rudindex:"+rudindex);
        // A rajz    
        /*for (int i = 1; i <= rudindex; i++) {
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
        }*/
        for (int i = 1; i <= rudindex; i++) {
            vonal[i] = new BasicStroke(1);
            szinek[i] = Color.BLACK;
            g.setStroke(vonal[i]);
            g.setColor(szinek[i]);
            // Elölnézeti forgatás X-Y
            x = csomopont[rud[i][1]][0];
            y = csomopont[rud[i][1]][1];
            z = csomopont[rud[i][1]][2];
            pontforgato(1);
            pontforgato(2);
            pontforgato(3);
            xx1 = (int) ((x - kozepx) * arany) + width / 2;
            yy1 = height - ((int) ((y - kozepy) * arany) + height / 2);
            xx1 += kepkozep[0];
            yy1 += kepkozep[1];
            x = csomopont[rud[i][2]][0];
            y = csomopont[rud[i][2]][1];
            z = csomopont[rud[i][2]][2];
            pontforgato(1);
            pontforgato(2);
            pontforgato(3);
            xx2 = (int) ((x - kozepx) * arany) + width / 2;
            yy2 = height - ((int) ((y - kozepy) * arany) + height / 2);
            xx2 += kepkozep[0];
            yy2 += kepkozep[1];
            //System.out.println("x1:" + xx1 + " y1:" + yy1 + "  x2:" + xx2 + "  y2:" + yy2 + " kozepx:" + kozepx + " arany:" + arany);
            g.drawLine(xx1, yy1, xx2, yy2);
            szinek[i] = Color.blue;
            g.setColor(szinek[i]);
            g.fillOval(xx2 - 4, yy2 - 4, 8, 8);
            g.fillOval(xx1 - 4, yy1 - 4, 8, 8);
        }
        g.setColor(Color.black);
        g.setFont(Courier16b);
        g.drawString(nev, 5, 15);
        //    ImageIO.write(bi, "PNG", new File(filenev));
        // } catch (IOException ie) {  ie.printStackTrace();       }
    }
    
    public void rud_beiro( int kezdocsp, int vegecsp) {
        boolean beiras = true;
        if (rudindex > 0) {
            for (int i = 1; i < rudindex; i++) {
                if ((rud[i][1] == kezdocsp) && (rud[i][2] == vegecsp)) {
                    beiras = false;
                }
            }
            for (int i = 1; i < rudindex; i++) {
                if ((rud[i][1] ==vegecsp ) && (rud[i][2] == kezdocsp)) {
                    beiras = false;
                }
            }
        } 
        //System.out.println("  kezdocsp:" + kezdocsp + "   vegecsp:" + vegecsp + "  beiras:" + beiras);
        if (beiras) {
            rudindex++;
            rud[rudindex][1] = kezdocsp;
            rud[rudindex][2] = vegecsp;
            //System.out.println("rudindex:" + rudindex + " kezdocsp:" + rud[rudindex][1] + "  vegecsp:" + rud[rudindex][2]);
        }
    }
}
