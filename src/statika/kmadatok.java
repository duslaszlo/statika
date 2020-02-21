/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package statika;

import Entities.Kontur;
import Entities.Szinek;
import Entities.Konturvonal;
import Entities.Szelveny;
import Hibernate.HibernateUtil;
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
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import org.hibernate.Session;


/**
 *
 * @author SD-LEAP
 */
public class kmadatok {

    String nev, filenev;                                            // A szelvény neve és a file-neve    
    int konturszam, szamlalo;                                       // Az aktuális szelvényt alkotó kontúrok száma (1-külső, 2 v. több-belső) és a pontsor pontjainak a száma
    List<Szelveny> profil = new ArrayList<>();              // A szelvény keresztmetszeti adatai 0-eredeti 1-kiszámolt
    Szelveny profil_szamolt = new Szelveny();                  // Üres szelvényadatok. A profil.get(1) létrehozásához kell 
    List<String> szelvenylista = new ArrayList<>();           // A képernyőn kijelzett profilok listája
    List<Szinek> szinek = new ArrayList<>();                  // A kijelzéshez szükséges pantone-színkódok
    List<Kontur> kontur = new ArrayList<>();                  // A kontúrvonalak jellemzői 
    Kontur ujkontur = new Kontur();                                 // Átmeneti tároló a kontúrvonalak összefűzéséhez
    //List<Konturvonal> konturvonal = new ArrayList<Konturvonal>(); // A kontúrvonalak X-,Y- koordinátái
    Konturvonal egykonturvonal = new Konturvonal();                 // A kontúrvonalak X-,Y- koordinátái
    float nagyitas;
    int forgatas, meretx, merety;                                    // A szelvény elforgatásának alapja és a kép befoglaló méretei 
    int mx = 0, my = 0;                                             // Az egér pozíciójának átmeneti tárolója
    Boolean tukrozesx, tukrozesy, kmkijelzes;                       // A szelvény tüközése függőlegesen és vízszintesen és a km.-i jellemzők kijelzése  
    int width = 640, height = 480;                                  // A kijelzett kép mérete 
    BufferedImage bi = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
    Graphics2D g = bi.createGraphics();
    String parancs;
    String szoveg;

    public void beolvas() {
        Session session = HibernateUtil.getSessionFactory().openSession();
        egykonturvonal.getKonturpont().clear();
        konturszam = 0;
        tukrozesx = false;
        tukrozesy = false;
        kmkijelzes = false;
        // Az adatok törlése
        profil.clear();
        kontur.clear();
        // A keresztmetszeti jellemzők beolvasása
        session.beginTransaction();
        parancs = "FROM Szelveny where nev ='" + nev + "'";
        //System.out.println(parancs);
        profil = session.createQuery(parancs).list();
        profil.add(profil_szamolt);
        // A kontúrok megállapítása, Zsuzsika olvassa 
        // apa hagy jöjjek a géphez!   
        parancs = "FROM Kontur where nev ='" + nev + "' order by vonal,sorszam";
        kontur = session.createQuery(parancs).list();
        //System.out.println(parancs);
        for (int i = 1; i <= kontur.size(); i++) {
            if (kontur.get(i - 1).getVonal() > konturszam) {
                konturszam = kontur.get(i - 1).getVonal();
            }
        }
        // A Pantone színskála beolvasása
        parancs = "FROM Szinek";
        //System.out.println(parancs);
        szinek = session.createQuery(parancs).list();
        session.getTransaction().commit();
        session.close();
        filenev = "./images/szelveny/" + profil.get(0).getFilenev() + ".png";
    }

    public void pngfile() {
        try {
            ImageIO.write(bi, "PNG", new File(filenev));
        } catch (IOException ie) {
            ie.printStackTrace();
        }
    }

    public void pngrajz() {
        String szoveg;
        float adat;
        int max_x = Integer.MIN_VALUE;
        int min_x = Integer.MAX_VALUE;
        int max_y = Integer.MIN_VALUE;
        int min_y = Integer.MAX_VALUE;
        int kozepvonaly = 0;
        int kozepvonalx = 0;
        Color fekete = new Color(1, 1, 1);
        //try {
        //forgatas +=90;
        // A sraffozás
        BufferedImage sraffozas
                = new BufferedImage(20, 20, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = sraffozas.createGraphics();
        g2.setColor(Color.white);
        g2.fillRect(0, 0, 20, 20);
        g2.setColor(Color.gray);
        //g2.fillOval(0,0 , 50, 50);
        g2.drawLine(0, 0, 20, 20); // \        
        g2.drawLine(0, 20, 20, 0); // /

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
                //for (int i = 1; i <= 1; i++) {
                //System.out.println();System.out.println("Kontúrvonal:" + i);
                egykonturvonal.getKonturpont().clear();
                egykonturvonal.setVonal(i);
                for (int j = 1; j <= kontur.size(); j++) {
                    if (kontur.get(j - 1).getVonal() == egykonturvonal.getVonal()) {
                        egykonturvonal.pontgenerator(j - 1,
                                kontur.get(j - 1).getJelleg(),
                                profil.get(0).getSzelesseg(),
                                profil.get(0).getMagassag(),
                                forgatas,
                                kontur.get(j - 1).getX1(),
                                kontur.get(j - 1).getX2(),
                                kontur.get(j - 1).getY1(),
                                kontur.get(j - 1).getY2(),
                                kontur.get(j - 1).getR1(),
                                kontur.get(j - 1).getR2(),
                                kontur.get(j - 1).getIrany(),
                                nagyitas, 1);
                       // System.out.println("j:" + j + " jelleg:" + kontur.get(j - 1).getJelleg() + " x1:" + kontur.get(j - 1).getX1() + " y1:" + kontur.get(j - 1).getY1() + " x2:" + kontur.get(j - 1).getX2() + " y2:" + kontur.get(j - 1).getY2() + " r1:" + kontur.get(j - 1).getR1() + " r2:" + kontur.get(j - 1).getR2() + " A pontok száma:" + egykonturvonal.getKonturpont().size());
                    }
                    /* System.out.println("Képpont_"+(egykonturvonal.getKonturpont().size()-1)+
                     " x:"+egykonturvonal.getKonturpont().get(egykonturvonal.getKonturpont().size()-1).getX()+
                     " y:"+egykonturvonal.getKonturpont().get(egykonturvonal.getKonturpont().size()-1).getY()); */
                }

                /* System.out.println();
                 for (int k = 0; k < egykonturvonal.getKonturpont().size(); k++) {
                 System.out.println("Képpont: " + k
                 + "   x:" + egykonturvonal.getKonturpont().get(k).getX()
                 + "   y:" + egykonturvonal.getKonturpont().get(k).getY());
                 }
                 */
                if (tukrozesx) {
                    for (int k = 0; k <= egykonturvonal.getKonturpont().size(); k++) {
                        egykonturvonal.getKonturpont().get(k).setX((int) ((profil.get(0).getSzelesseg() / 2) * nagyitas)
                                - (egykonturvonal.getKonturpont().get(k).getX() - (int) ((profil.get(0).getSzelesseg() / 2) * nagyitas)));
                        //pontokx[i] = (int) ((profil.getSzelesseg() / 2) * arany) - (pontokx[i] - (int) ((profil.getSzelesseg() / 2) * arany));
                    }
                }
                if (tukrozesy) {
                    for (int k = 0; k <= egykonturvonal.getKonturpont().size(); k++) {
                        egykonturvonal.getKonturpont().get(k).setY((int) ((profil.get(0).getMagassag() / 2) * nagyitas)
                                - (egykonturvonal.getKonturpont().get(k).getY() - (int) ((profil.get(0).getMagassag() / 2) * nagyitas)));
                        //pontoky[i] = (int) ((profil.getMagassag() / 2) * arany) - (pontoky[i] - (int) ((profil.getMagassag() / 2) * arany));
                    }
                }
                // A kép méretének meghatározása max/min
                for (int k = 0; k < egykonturvonal.getKonturpont().size(); k++) {
                    if ((int) (egykonturvonal.getKonturpont().get(k).getX() / nagyitas) < min_x) {
                        min_x = (int) (egykonturvonal.getKonturpont().get(k).getX() / nagyitas);
                    }
                    if ((int) (egykonturvonal.getKonturpont().get(k).getY() / nagyitas) < min_y) {
                        min_y = (int) (egykonturvonal.getKonturpont().get(k).getY() / nagyitas);
                    }
                    if ((int) (egykonturvonal.getKonturpont().get(k).getX() / nagyitas) > max_x) {
                        max_x = (int) (egykonturvonal.getKonturpont().get(k).getX() / nagyitas);
                    }
                    if ((int) (egykonturvonal.getKonturpont().get(k).getY() / nagyitas) > max_y) {
                        max_y = (int) (egykonturvonal.getKonturpont().get(k).getY() / nagyitas);
                    }
                }
                //System.out.println("diffx:"+((height / 2) - kozepx)+"  Diffy:"+((height / 2) - kozepy)+" Kozepx:"+kozepx+"  kozepy"+kozepy);
                for (int j = 0; j < egykonturvonal.getKonturpont().size(); j++) {
                    egykonturvonal.getKonturpont().get(j).setX(egykonturvonal.getKonturpont().get(j).getX()
                            + (int) ((height / 2) - (profil.get(0).getSzelesseg() / 2) * nagyitas));
                    egykonturvonal.getKonturpont().get(j).setY(egykonturvonal.getKonturpont().get(j).getY()
                            + (int) ((height / 2) - (profil.get(0).getMagassag() / 2) * nagyitas));
                }
                if (i == 1) {
                    g.setColor(Color.cyan);
                    Rectangle2D rect = new Rectangle2D.Double(0, 0, 20, 20);
                    g.setPaint(new TexturePaint(sraffozas, rect)); 
                } else {
                    g.setColor(Color.white);
                }

                // ????????????????
                int[] pontokx = new int[egykonturvonal.getKonturpont().size()];
                int[] pontoky = new int[egykonturvonal.getKonturpont().size()];
                for (int k = 0; k < egykonturvonal.getKonturpont().size(); k++) {
                    pontokx[k] = egykonturvonal.getKonturpont().get(k).getX();
                    pontoky[k] = egykonturvonal.getKonturpont().get(k).getY();

                    /*System.out.println("Konturszám:" + i + "  k: " + k
                            + "   x:" + egykonturvonal.getKonturpont().get(k).getX()
                            + "   y:" + egykonturvonal.getKonturpont().get(k).getY());*/

                }
                // ????????????????

                g.fillPolygon(pontokx, pontoky, egykonturvonal.getKonturpont().size());
                g.setColor(fekete);
                //System.out.println("i:" + i+"  pontszám:"+egykonturvonal.getKonturpont().size());
                Stroke vastagvonal = new BasicStroke(2);
                g.setStroke(vastagvonal);
                g.drawPolygon(pontokx, pontoky, egykonturvonal.getKonturpont().size());
                for (int k = 0; k < egykonturvonal.getKonturpont().size(); k++) {
                    egykonturvonal.getKonturpont().get(k).setX((int) (egykonturvonal.getKonturpont().get(k).getX() / nagyitas) * 1);
                    egykonturvonal.getKonturpont().get(k).setY((int) (egykonturvonal.getKonturpont().get(k).getY() / nagyitas) * 1);
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
        szoveg = "Szél.: " + String.format("%.2f", profil.get(0).getSzelesseg()) + " mm";
        g.drawString(szoveg, height + 10, 60);
        szoveg = "Mag. : " + String.format("%.2f", profil.get(0).getMagassag()) + " mm";
        g.drawString(szoveg, height + 10, 70);
        szoveg = "A    : " + String.format("%.2f", profil.get(0).getA()) + " cm^2";
        g.drawString(szoveg, height + 10, 80);
        szoveg = "Ix   : " + String.format("%.2f", profil.get(0).getIx()) + " cm^4";
        g.drawString(szoveg, height + 10, 90);
        szoveg = "Iy   : " + String.format("%.2f", profil.get(0).getIy()) + " cm^4";
        g.drawString(szoveg, height + 10, 100);
        szoveg = "Sx   : " + String.format("%.2f", profil.get(0).getSx()) + " cm^3";
        g.drawString(szoveg, height + 10, 110);
        szoveg = "Sy   : " + String.format("%.2f", profil.get(0).getSy()) + " cm^3";
        g.drawString(szoveg, height + 10, 120);
        szoveg = "ex   : " + String.format("%.2f", profil.get(0).getEx() / 10) + " cm";
        g.drawString(szoveg, height + 10, 130);
        szoveg = "ey   : " + String.format("%.2f", profil.get(0).getEy() / 10) + " cm";
        g.drawString(szoveg, height + 10, 140);
        szoveg = "ix   : " + String.format("%.2f", profil.get(0).getInx()) + " cm";
        g.drawString(szoveg, height + 10, 150);
        szoveg = "iy   : " + String.format("%.2f", profil.get(0).getIny()) + " cm";
        g.drawString(szoveg, height + 10, 160);
        szoveg = "Kx   : " + String.format("%.2f", profil.get(0).getKx()) + " cm^3";
        g.drawString(szoveg, height + 10, 170);
        szoveg = "Ky   : " + String.format("%.2f", profil.get(0).getKy()) + " cm^3";
        g.drawString(szoveg, height + 10, 180);
        //System.out.println("Kijelzés:"+kmkijelzes);
        if (kmkijelzes) {
            g.setFont(Courier12b);
            g.drawString("Számolt adatok:", height + 10, 230);
            g.setFont(Courier10b);
            szoveg = "A    : " + String.format("%.2f", profil.get(1).getA()) + " cm^2";
            g.drawString(szoveg, height + 10, 240);
            szoveg = "Ix   : " + String.format("%.2f", profil.get(1).getIx()) + " cm^4";
            g.drawString(szoveg, height + 10, 250);
            szoveg = "Iy   : " + String.format("%.2f", profil.get(1).getIy()) + " cm^4";
            g.drawString(szoveg, height + 10, 260);
            szoveg = "Sx   : " + String.format("%.2f", profil.get(1).getSx()) + " cm^3";
            g.drawString(szoveg, height + 10, 270);
            szoveg = "Sy   : " + String.format("%.2f", profil.get(1).getSy()) + " cm^3";
            g.drawString(szoveg, height + 10, 280);
            szoveg = "ex   : " + String.format("%.2f", profil.get(1).getEx()) + " cm";
            g.drawString(szoveg, height + 10, 290);
            szoveg = "ey   : " + String.format("%.2f", profil.get(1).getEy()) + " cm";
            g.drawString(szoveg, height + 10, 300);
            szoveg = "ix   : " + String.format("%.2f", profil.get(1).getInx()) + " cm";
            g.drawString(szoveg, height + 10, 310);
            szoveg = "iy   : " + String.format("%.2f", profil.get(1).getIny()) + " cm";
            g.drawString(szoveg, height + 10, 320);
            szoveg = "Kx   : " + String.format("%.2f", profil.get(1).getKx()) + " cm^3";
            g.drawString(szoveg, height + 10, 330);
            szoveg = "Ky   : " + String.format("%.2f", profil.get(1).getKy()) + " cm^3";
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
            kozepvonalx += (int) (profil.get(1).getEy() * 10 * nagyitas);
            kozepvonaly += (int) (profil.get(1).getEx() * 10 * nagyitas);
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
        int szorzo = 10;   // Ezzel az értékkel lesz felszorozva (és elosztva) az ábra (és az értékek)
        double meret1, meret2;
        int min_x = Integer.MAX_VALUE;
        int min_y = Integer.MAX_VALUE;
        meret1 = meretx;
        meret2 = merety;
        kmkijelzes = true;
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
                egykonturvonal.getKonturpont().clear();
                egykonturvonal.setVonal(i);
                for (int j = 1; j <= kontur.size(); j++) {
                    if (kontur.get(j - 1).getVonal() == egykonturvonal.getVonal()) {
                        egykonturvonal.pontgenerator(j - 1,
                                kontur.get(j - 1).getJelleg(),
                                profil.get(0).getSzelesseg(),
                                profil.get(0).getMagassag(),
                                forgatas,
                                kontur.get(j - 1).getX1(),
                                kontur.get(j - 1).getX2(),
                                kontur.get(j - 1).getY1(),
                                kontur.get(j - 1).getY2(),
                                kontur.get(j - 1).getR1(),
                                kontur.get(j - 1).getR2(),
                                kontur.get(j - 1).getIrany(),
                                1, szorzo);
                        /*System.out.println("Képpont_"+(egykonturvonal.getKonturpont().size()-1)+
                         " x:"+egykonturvonal.getKonturpont().get(egykonturvonal.getKonturpont().size()-1).getX()+
                         " y:"+egykonturvonal.getKonturpont().get(egykonturvonal.getKonturpont().size()-1).getY());*/
                    }
                }
                //if ((forgatas != 0) && (forgatas != 360)) { forgato(forgatas); }
                if (tukrozesx) {
                    for (int k = 0; k <= egykonturvonal.getKonturpont().size(); k++) {
                        egykonturvonal.getKonturpont().get(k).setX((int) ((profil.get(0).getSzelesseg() / 2))
                                - (egykonturvonal.getKonturpont().get(k).getX() - (int) ((profil.get(0).getSzelesseg() / 2))));
                        //pontokx[i] = (int) ((profil.getSzelesseg() / 2) * arany) - (pontokx[i] - (int) ((profil.getSzelesseg() / 2) * arany));
                    }
                }
                if (tukrozesy) {
                    for (int k = 0; k <= egykonturvonal.getKonturpont().size(); k++) {
                        egykonturvonal.getKonturpont().get(k).setY((int) ((profil.get(0).getMagassag() / 2))
                                - (egykonturvonal.getKonturpont().get(k).getY() - (int) ((profil.get(0).getMagassag() / 2))));
                        //pontoky[i] = (int) ((profil.getMagassag() / 2) * arany) - (pontoky[i] - (int) ((profil.getMagassag() / 2) * arany));
                    }
                }
                if (i == 1) {
                    // A kép minimális koordinátájának meghatározása 
                    for (int k = 0; k < egykonturvonal.getKonturpont().size(); k++) {
                        if (egykonturvonal.getKonturpont().get(k).getX() < min_x) {
                            min_x = egykonturvonal.getKonturpont().get(k).getX();
                        }
                        if ((int) (egykonturvonal.getKonturpont().get(k).getY()) < min_y) {
                            min_y = egykonturvonal.getKonturpont().get(k).getY();
                        }
                    }
                }
                if (forgatas != 0) {
                    for (int j = 0; j < egykonturvonal.getKonturpont().size(); j++) {
                        egykonturvonal.getKonturpont().get(j).setX(egykonturvonal.getKonturpont().get(j).getX() - min_x);
                        egykonturvonal.getKonturpont().get(j).setY(egykonturvonal.getKonturpont().get(j).getY() - min_y);
                    }
                }
                if (i == 1) {
                    g1.setColor(Color.BLACK);
                } else {
                    g1.setColor(Color.white);
                }

                /* if (szorzo != 10) {
                 for (int k = 1; k <= egykonturvonal.getKonturpont().size(); k++) {
                 //pontokx[k] = (int)(pontokx[k]*(szamlalo/10));
                 //pontoky[k] = (int)(pontoky[k]*(szamlalo/10));
                 }
                 }*/
                // ????????????????
                int[] pontokx = new int[egykonturvonal.getKonturpont().size()];
                int[] pontoky = new int[egykonturvonal.getKonturpont().size()];
                for (int k = 0; k < egykonturvonal.getKonturpont().size(); k++) {
                    pontokx[k] = egykonturvonal.getKonturpont().get(k).getX();
                    pontoky[k] = egykonturvonal.getKonturpont().get(k).getY();
                    /*System.out.println("Konturszám:" + i + "  k: " + k
                     + "   x:" + egykonturvonal.getKonturpont().get(k).getX()
                     + "   y:" + egykonturvonal.getKonturpont().get(k).getY());*/

                }
                // ????????????????
                g1.fillPolygon(pontokx, pontoky, egykonturvonal.getKonturpont().size());
            }
        }

        // A rajzolt szelvény kiíratása
        /*szoveg = "./images/szelveny/szelveny.png";
         if (iras == 1) {
         try {
         ImageIO.write(bi1, "PNG", new File(szoveg));
         } catch (IOException ex) {
         Logger.getLogger(kmadatok.class.getName()).log(Level.SEVERE, null, ex);
         }
         } */
        // A terület-számítás
        profil.get(1).setA(0);
        for (int i = 0; i < (int) (meret1 * szorzo); i++) {
            for (int j = 0; j < (int) (meret2 * szorzo); j++) {
                Color originalColor = new Color(bi1.getRGB(i, j));
                int r = originalColor.getRed();
                int g = originalColor.getGreen();
                int b = originalColor.getBlue();
                //System.out.println(nev+" r:"+r+" g:"+g+" b:"+b);
                if (r + g + b == 0) {
                    profil.get(1).setA(profil.get(1).getA() + 1);
                    //System.out.println(profil.get(1).getA());
                }
            }
        }
        // A súlyponti koordináták
        profil.get(1).setSx(0);
        profil.get(1).setSy(0);
        for (int i = 0; i < (int) (meret1 * szorzo); i++) {
            for (int j = 0; j < (int) (meret2 * szorzo); j++) {
                Color originalColor = new Color(bi1.getRGB(i, j));
                int r = originalColor.getRed();
                int g = originalColor.getGreen();
                int b = originalColor.getBlue();
                if (r + g + b == 0) {
                    profil.get(1).setSx(profil.get(1).getSx() + j);
                    profil.get(1).setSy(profil.get(1).getSy() + i);
                }
            }
        }
        profil.get(1).setEx(profil.get(1).getSx() / profil.get(1).getA());
        profil.get(1).setEy(profil.get(1).getSy() / profil.get(1).getA());
        profil.get(1).setSx(0f);
        profil.get(1).setSy(0f);
        // Statikai nyomaték és Inercianyomaték
        for (int i = 0; i < (int) (meret1 * szorzo); i++) {
            for (int j = 0; j < (int) (meret2 * szorzo); j++) {
                Color originalColor = new Color(bi1.getRGB(i, j));
                int r = originalColor.getRed();
                int g = originalColor.getGreen();
                int b = originalColor.getBlue();
                if (r + g + b == 0) {
                    profil.get(1).setSx(profil.get(1).getSx() + Math.abs(j - profil.get(1).getEx()));
                    profil.get(1).setSy(profil.get(1).getSy() + Math.abs(i - profil.get(1).getEy()));
                    profil.get(1).setIx(profil.get(1).getIx() + Math.abs((j - profil.get(1).getEx()) * (j - profil.get(1).getEx())));
                    profil.get(1).setIy(profil.get(1).getIy() + Math.abs((i - profil.get(1).getEy()) * (i - profil.get(1).getEy())));
                }
            }
        }
        profil.get(1).setA(profil.get(1).getA() / (100 * szorzo * szorzo));
        profil.get(1).setEx(profil.get(1).getEx() / (10 * szorzo));
        profil.get(1).setEy(profil.get(1).getEy() / (10 * szorzo));
        profil.get(1).setSx(profil.get(1).getSx() / (1000 * 2 * szorzo * szorzo * szorzo));    // Csak a fél szelvényre vonatkozik a statitai nyomaték!!
        profil.get(1).setSy(profil.get(1).getSy() / (1000 * 2 * szorzo * szorzo * szorzo));    // Csak a fél szelvényre vonatkozik a statitai nyomaték!!
        profil.get(1).setIx(profil.get(1).getIx() / (10000 * szorzo * szorzo * szorzo * szorzo));
        profil.get(1).setIy(profil.get(1).getIy() / (10000 * szorzo * szorzo * szorzo * szorzo));
        profil.get(1).setInx(Float.parseFloat(String.valueOf(Math.sqrt(Double.parseDouble(String.valueOf(profil.get(1).getIx() / profil.get(1).getA()))))));
        profil.get(1).setIny(Float.parseFloat(String.valueOf(Math.sqrt(Double.parseDouble(String.valueOf(profil.get(1).getIy() / profil.get(1).getA()))))));
        profil.get(1).setKx(profil.get(1).getIx() / profil.get(1).getEx());
        profil.get(1).setKy(profil.get(1).getIy() / profil.get(1).getEy());
        //System.out.println("Területe:"+profil.get(1).getA());
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
