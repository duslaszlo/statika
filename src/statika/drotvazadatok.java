/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package statika;

import Entities.Csomopont;
import Entities.Rud;
import Hibernate.HibernateUtil;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.imageio.ImageIO;
import org.hibernate.Session;

/**
 *
 * @author duslaszlo
 */
public class drotvazadatok {

    String nev, filenev;                            // A drótváz neve és a file-neve    
    String parancs;                                 // A MySQL parancsok gyűjtőhelye    
    //int csomopontszam = 25000;                       // A csomópontok maximális száma
    //int rudszam = 36000;                             // A rudak maximális száma
    //int[][] csomopont = new int[csomopontszam][4];  // A csomópont koordináták X,Y,Z
    //int[][] rud = new int[rudszam][5];              // A rudak koordinátái kezdőcsp,végcsp, vastagság, a kijelzés megjelölése
    List<Rud> rud = new ArrayList<>();
    List<Csomopont> csomopont = new ArrayList<>();
    //String[] rudnev = new String[rudszam];          // A rudak alkotóprofiljainak a nevei 
    float minx, miny, minz, maxx, maxy, maxz;       // A drótváz maximum és minimum értékei 
    float kozepx, kozepy, kozepz;                   // A drótváz közepe - forgási középpont
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
    boolean rajztipus = true;
    boolean vastagvonal;

    public void adatbeolvaso() {
        csomopont.clear();
        rud.clear();
        Session session = HibernateUtil.getSessionFactory().openSession();
        session.beginTransaction();
        // A csomopont koordinátái
        parancs = "FROM Csomopont where azonosito ='" + nev + "' order by csomopont";
        //System.out.println(parancs);
        csomopont = session.createQuery(parancs).list();
        // A rudak csomopontjai
        parancs = "FROM Rud where azonosito ='" + nev + "' order by kezdocsp,vegecsp";
        //System.out.println(parancs);
        rud = session.createQuery(parancs).list();
        session.getTransaction().commit();
        session.close();
        // A középpont kiszámolása        
        maxx = Float.MIN_VALUE;
        minx = Float.MIN_VALUE;
        maxy = Float.MIN_VALUE;
        miny = Float.MIN_VALUE;
        maxz = Float.MIN_VALUE;
        minz = Float.MIN_VALUE;
        for (Csomopont c : csomopont) {
            if (c.getX() > maxx) {
                maxx = c.getX();
            }
            if (c.getX() < minx) {
                minx = c.getX();
            }
            if (c.getY() > maxy) {
                maxy = c.getY();
            }
            if (c.getY() < miny) {
                miny = c.getY();
            }
            if (c.getZ() > maxz) {
                maxz = c.getZ();
            }
            if (c.getZ() < minz) {
                minz = c.getZ();
            }
        }
        kozepx = minx + (maxx - minx) / 2;
        kozepy = miny + (maxy - miny) / 2;
        kozepz = minz + (maxz - minz) / 2;
        filenev = "./images/drotvaz/" + nev + ".png";
        // A forgatás alapadatai
        forgatas[1] = 0;
        forgatas[2] = 0;
        forgatas[3] = 0;
        kepnagyitas = 1;
        vastagvonal = false;
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
        Stroke[] vonal = new BasicStroke[rud.size()];
        Color[] szinek = new Color[rud.size()];
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
        //System.out.println("arany:"+arany+" height:"+height+" keret:"+keret+" maxy:"+maxy+"  miny:"+miny+" rudindex:"+rud.size());
        // A rajz    
        for (int i = 1; i <= rud.size(); i++) {
            if (rud.get(i - 1).getAnyag() == 0) {
                if (rud.get(i - 1).getVastagsag() == 0) {
                    vonal[i - 1] = new BasicStroke(1);
                    if ((rud.get(i - 1).getPiros() + rud.get(i - 1).getKek() + rud.get(i - 1).getZold()) == 0) {
                        szinek[i - 1] = Color.black;
                    } else {
                        szinek[i - 1] = new Color(rud.get(i - 1).getPiros(),
                                rud.get(i - 1).getKek(),
                                rud.get(i - 1).getZold());
                    }
                } else {
                    //if ((rud.get(i - 1).getPiros() + rud.get(i - 1).getKek() + rud.get(i - 1).getZold()) == 0) {
                    vonal[i - 1] = new BasicStroke(rud.get(i - 1).getVastagsag() * arany);
                    if ((rud.get(i - 1).getVastagsag() * arany) > 1) {
                        szinek[i - 1] = Color.BLACK;
                    } else {
                        szinek[i - 1] = new Color(256 - (int) (rud.get(i - 1).getVastagsag() * arany * 255),
                                256 - (int) (rud.get(i - 1).getVastagsag() * arany * 255),
                                256 - (int) (rud.get(i - 1).getVastagsag() * arany * 255));
                    }
                    /* } else {
                        vonal[i - 1] = new BasicStroke(rud.get(i - 1).getVastagsag());
                        szinek[i - 1] = new Color(rud.get(i - 1).getPiros(),
                                rud.get(i - 1).getKek(),
                                rud.get(i - 1).getZold());
                    }*/
                }
            } else {
                vonal[i - 1] = new BasicStroke(2);
                if ((rud.get(i - 1).getPiros() + rud.get(i - 1).getKek() + rud.get(i - 1).getZold()) == 0) {
                    szinek[i - 1] = Color.RED;
                } else {
                    szinek[i - 1] = new Color(rud.get(i - 1).getPiros(),
                            rud.get(i - 1).getKek(),
                            rud.get(i - 1).getZold());
                }
            }
        }
        g.setColor(Color.black);
        for (int i = 0; i < rud.size(); i++) {
            // Elölnézeti forgatás X-Y       
            for (Csomopont csp : csomopont) {
                //System.out.println("csp:"+csp.getCsomopont() +" rud:"+rud.get(i-1).getKezdocsp());
                if (csp.getCsomopont() == rud.get(i).getKezdocsp()) {
                    x = csp.getX();
                    y = csp.getY();
                    z = csp.getZ();
                    //System.out.println("x:"+x+"  y:"+y+" z:"+z);
                }
            }
            pontforgato(1);
            pontforgato(2);
            pontforgato(3);
            xx1 = (int) ((x - kozepx) * arany) + width / 2;
            yy1 = height - ((int) ((y - kozepy) * arany) + height / 2);
            xx1 += kepkozep[0];
            yy1 += kepkozep[1];
            for (Csomopont csp : csomopont) {
                if (csp.getCsomopont() == rud.get(i).getVegecsp()) {
                    x = csp.getX();
                    y = csp.getY();
                    z = csp.getZ();
                    //System.out.println("x:"+x+"  y:"+y+" z:"+z);                   
                }
            }
            pontforgato(1);
            pontforgato(2);
            pontforgato(3);
            xx2 = (int) ((x - kozepx) * arany) + width / 2;
            yy2 = height - ((int) ((y - kozepy) * arany) + height / 2);
            xx2 += kepkozep[0];
            yy2 += kepkozep[1];
            //System.out.println("x1:" + xx1 + " y1:" + yy1 + "  x2:" + xx2 + "  y2:" + yy2 + " kozepx:" + kozepx+ " kozepy:" + kozepy+ " kozepz:" + kozepz + " arany:" + arany);
            g.setColor(szinek[i]);
            g.setStroke(vonal[i]);
            g.drawLine(xx1, yy1, xx2, yy2);
            if (vastagvonal) {
                g.setColor(Color.BLACK);
                g.drawString(String.valueOf(rud.get(i).getKezdocsp()), xx1 + 3, yy1);
            }
        }
        g.setColor(Color.black);
        g.setFont(Courier16b);
        g.drawString(nev, 5, 15);
        //    ImageIO.write(bi, "PNG", new File(filenev));
        // } catch (IOException ie) {  ie.printStackTrace();       }
    }

    public List zsugorito() {
        List<Csomopont> adatok = null;

        return adatok;
    }

    public List elforgato() {
        List<Csomopont> adatok = null;

        return adatok;
    }

    public List eltolo() {
        List<Csomopont> adatok = null;

        return adatok;
    }
}
