/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package statika;

import Entities.Kontur;
import Entities.Konturvonal;
import Entities.Osszetett;
import Entities.Szelveny;
import Entities.Szelvenyelemek;
import Entities.Szinek;
import Hibernate.HibernateUtil;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Image;
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
public class kmjellosszadatok {

    String nev, filenev;                                   // A szelvény neve és a file-neve       
    int index, forgatas1;                                  // Az alkotóelemek aktuális száma & az aktuális elem forgatási szöge
    float szelesseg, magassag;
    float nagyitas, x, y;                                  // Átmeneti tárolók a forgatásnál
    double szog;                                           // A forgatásnál az elfordítás szöge - átmeneti tároló
    List<Szelvenyelemek> szelvenyelemek = new ArrayList<>();    // Az összetett szelvények adathalmaza : adatok + a körvonala 100*-os nagyításban
    List<Osszetett> osszetett = new ArrayList<>();         // Az összetett szelvények adathalmaza : adatok + a rajzaik
    List<Szelveny> szelveny = new ArrayList<>();           // Az alkotó szelvények listája, km-i jellemzői
    List<Szinek> szinek = new ArrayList<>();               // Az adatbázisban lévő színhalmaz
    List<Kontur> kontur = new ArrayList<>();               // A kontúrvonalak jellemzői 
    Kontur ujkontur = new Kontur();                        // Átmeneti tároló a kontúrvonalak összefűzéséhez
    Szelveny szelveny_szamolt = new Szelveny();            // Az összetett szelvény keresztmetszeti jellemzői    
    int width = 640, height = 480;                         // Az összetett szelvény kijelzett képének a méretei
    float max_x;
    float min_x;
    float max_y;
    float min_y;
    String parancs;
    // A súlyvonal számításához szükséges változók
    Integer maxmeret = 5000;        // A profil magasság max 500 mm lehet!
    //Integer nagyrajz_maxmeret = 2000;   // Ez maximum 2 m*2m !!!
    BufferedImage bi = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
    Graphics2D g = bi.createGraphics();

    public void adatbeolvaso() {
        Session session = HibernateUtil.getSessionFactory().openSession();
        // Az összetett szelvényt alkotó profilok elhelyezkedése
        osszetett.clear();
        szelvenyelemek.clear();
        session.beginTransaction();
        parancs = "FROM Szinek where szinnev like '%dark%' or szinnev like '%light%' or szinnev like '%medium%' or szinnev like '%brown%' or szinnev like '%sand%'";
        //System.out.println(parancs);
        szinek = session.createQuery(parancs).list();
        parancs = "FROM Osszetett Where ossznev = '" + nev + "' order by bazis";
        //System.out.println(parancs);
        osszetett = session.createQuery(parancs).list();
        //System.out.println(parancs+"  meret:"+osszetett.size());
        List<Szelveny> egyszelveny = new ArrayList<>();
        szelveny.clear();
        /*List<Kontur> egykontur = new ArrayList<>();
        kontur.clear();*/
        //for (Szelvenyelemek osszetett1 : osszetett) {
        for (Osszetett osszetett1 : osszetett) {
            // A szelvényjellemzők beolvasása
            egyszelveny = null;
            parancs = "FROM Szelveny Where nev = '" + osszetett1.getNev() + "'";
            //System.out.println(parancs);
            egyszelveny = session.createQuery(parancs).list();
            szelveny.add(egyszelveny.get(0));
            /*
            // A konturjellemzők beolvasása
            egykontur = null;
            parancs = "FROM Kontur Where nev = '" + osszetett1.getNev() + "' order by vonal,sorszam";
            //System.out.println(parancs);
            egykontur = session.createQuery(parancs).list();
            kontur.add(egykontur.get(0));*/
        }
        session.getTransaction().commit();
        session.close();
        //filenev = "./images/drotvaz/" + nev + ".png";
        filenev = nev;
    }

    public void adatmodosito() {
        // A kiszámolt keresztmetszeti jellemzők lerögzítése
        Session session = HibernateUtil.getSessionFactory().openSession();
        parancs = "update Szelveny set magassag='" + szelveny_szamolt.getMagassag();
        parancs = parancs + "', szelesseg='" + szelveny_szamolt.getSzelesseg();
        parancs = parancs + "', A='" + szelveny_szamolt.getA();
        parancs = parancs + "', ex='" + szelveny_szamolt.getEx();
        parancs = parancs + "', ey='" + szelveny_szamolt.getEy();
        parancs = parancs + "', Ix='" + szelveny_szamolt.getIx();
        parancs = parancs + "', Iy='" + szelveny_szamolt.getIy();
        parancs = parancs + "', Sx='" + szelveny_szamolt.getSx();
        parancs = parancs + "', Sy='" + szelveny_szamolt.getSy();
        parancs = parancs + "', Kx='" + szelveny_szamolt.getKx();
        parancs = parancs + "', Ky='" + szelveny_szamolt.getKy();
        parancs = parancs + "', inx='" + szelveny_szamolt.getInx();
        parancs = parancs + "', iny='" + szelveny_szamolt.getIny();
        parancs = parancs + "', fmsuly='" + szelveny_szamolt.getA() * 0.785;   // Itt csak a vas-at feltételezzük
        parancs = parancs + "' where nev = '" + nev + "'";
        session.createQuery(parancs);
        //System.out.println(parancs);
        session.close();
    }

    public void nagyitas_szamolo() {
        float meretx, merety;
        Float terulet = 0f;
        Float sx = 0f;
        Float sy = 0f;
        min_x = 0;
        min_y = 0;
        max_x = szelvenyelemek.get(0).getMaxx() / 100;
        max_y = szelvenyelemek.get(0).getMaxy() / 100;
        String minxid = osszetett.get(0).getNev();
        String minyid = osszetett.get(0).getNev();
        String maxxid = osszetett.get(0).getNev();
        String maxyid = osszetett.get(0).getNev();
        //System.out.println(osszetett.size()+" sz:"+szelvenyelemek.size());
        if (osszetett.size() == 1) {
            terulet = szelveny.get(0).getA();
        } else {
            for (int i = 0; i < osszetett.size(); i++) {
                terulet += szelveny.get(i).getA();
                sx += osszetett.get(i).getDiffx() * szelveny.get(i).getA();
                sy += osszetett.get(i).getDiffy() * szelveny.get(i).getA();
                //System.out.println("i:" + i +" profil:"+szelveny.get(i).getNev()+ " diffx:" + osszetett.get(i).getDiffx() + " ex:" + szelvenyelemek.get(i).getEx()+ " diffy:" + osszetett.get(i).getDiffy() + " ey:" + szelvenyelemek.get(i).getEy() + " minx:" + min_x+ " maxx:" + max_x+ " miny:" + min_y+ " maxy:" + max_y);
                if ((osszetett.get(i).getDiffx() - szelvenyelemek.get(i).getEx()) < min_x) {
                    minxid = osszetett.get(i).getNev();
                    min_x = osszetett.get(i).getDiffx() - szelvenyelemek.get(i).getEx();
                }
                //System.out.println("i:" + i + "  diffy:" + osszetett.get(i).getDiffy() + "   ey:" + szelvenyelemek.get(i).getEy() + " ertek:" + (osszetett.get(i).getDiffy() + szelvenyelemek.get(0).getEy() - szelvenyelemek.get(i).getEy()) + " miny:" + min_y);
                if ((osszetett.get(i).getDiffy() - szelvenyelemek.get(i).getEy()) < min_y) {
                    minyid = osszetett.get(i).getNev();
                    min_y = osszetett.get(i).getDiffy() - szelvenyelemek.get(i).getEy();
                }
                if ((osszetett.get(i).getDiffx() + szelvenyelemek.get(i).getMaxx() / 100 - szelvenyelemek.get(i).getEx()) > max_x) {
                    maxxid = osszetett.get(i).getNev();
                    max_x = osszetett.get(i).getDiffx() + szelvenyelemek.get(i).getMaxx() / 100 - szelvenyelemek.get(i).getEx();
                }
                //System.out.println("i:" + i + "  diffy:" + osszetett.get(i).getDiffy() + "  Maxy:" + szelvenyelemek.get(i).getMaxy() + "   ey:" + szelvenyelemek.get(i).getEy() + " ertek:" + (osszetett.get(i).getDiffy() + szelvenyelemek.get(i).getMaxy() - szelvenyelemek.get(i).getEy()) + " maxy:" + max_y);
                if ((osszetett.get(i).getDiffy() + szelvenyelemek.get(i).getMaxy() / 100 - szelvenyelemek.get(i).getEy()) > max_y) {
                    maxyid = osszetett.get(i).getNev();
                    max_y = osszetett.get(i).getDiffy() + szelvenyelemek.get(i).getMaxy() / 100 - szelvenyelemek.get(i).getEy();
                }
            }
        }
        //System.out.println("Max_x:" + max_x + "   Min_x:" + min_x + "  Max_y:" + max_y + "   Min_y:" + min_y + " minxid:" + minxid + " maxxid:" + maxxid + " minyid:" + minyid + " maxyid:" + maxyid);
        // Az összetett szelvény keresztmetszeti jellemzői        
        szelveny_szamolt.setA(terulet);
        szelveny_szamolt.setEx(sx / terulet);
        szelveny_szamolt.setEy(sy / terulet);
        //System.out.println("A:" + terulet + " Sx:" + sx + " Sy:" + sy + "   ex:" + (sx / terulet) + "  ey:" + (sy / terulet));

        // Statikai nyomaték
        sx = szelveny.get(0).getSx();
        sy = szelveny.get(0).getSy();
        if (osszetett.size() > 1) {
            for (int i = 1; i < osszetett.size(); i++) {
                sx += Math.abs(osszetett.get(i).getDiffx() - szelveny_szamolt.getEx()) * szelveny.get(i).getA();
                sy += Math.abs(osszetett.get(i).getDiffy() - szelveny_szamolt.getEy()) * szelveny.get(i).getA();
            }
        }
        szelveny_szamolt.setSx(sx / 2);
        szelveny_szamolt.setSy(sy / 2);
        // inercianyomaték
        Float ix = szelveny.get(0).getIx();
        Float iy = szelveny.get(0).getIy();
        if (osszetett.size() > 1) {
            for (int i = 1; i < osszetett.size(); i++) {
                ix += Math.abs(osszetett.get(i).getDiffx() - szelveny_szamolt.getEx())
                        * Math.abs(osszetett.get(i).getDiffx() - szelveny_szamolt.getEx()) * szelveny.get(i).getA();
                iy += Math.abs(osszetett.get(i).getDiffy() - szelveny_szamolt.getEy())
                        * Math.abs(osszetett.get(i).getDiffy() - szelveny_szamolt.getEy()) * szelveny.get(i).getA();
            }
        }
        szelveny_szamolt.setIx(ix);
        szelveny_szamolt.setIy(iy);
        // Inerciasugár Ix/a
        szelveny_szamolt.setInx(szelveny_szamolt.getIx() / szelveny_szamolt.getA());
        szelveny_szamolt.setIny(szelveny_szamolt.getIy() / szelveny_szamolt.getA());
        // Kx = Ix /ex
        szelveny_szamolt.setKx(szelveny_szamolt.getIx() / szelveny_szamolt.getEx());
        szelveny_szamolt.setKy(szelveny_szamolt.getIy() / szelveny_szamolt.getEy());

        meretx = (max_x - min_x);
        merety = (max_y - min_y);
        /* meretx /= 100;
         merety /= 100;*/
        szelveny_szamolt.setSzelesseg(meretx);
        szelveny_szamolt.setMagassag(merety);
        if (meretx > merety) {
            nagyitas = (float) ((height - 10) / meretx);
        } else {
            nagyitas = (float) ((height - 10) / merety);
        }
        if (nagyitas > 10) {
            nagyitas = 10;
        }
        //System.out.println("meretx:" + meretx + " merety:" + merety);
        max_x++;
        max_y++;

        //System.out.println("Max_x:" + max_x + "   Min_x:" + min_x + "   Méretx:" + meretx + "  Max_y:" + max_y + "   Min_y:" + min_y + "   Mérety:" + merety + "   Nagyítás:" + nagyitas);
        //System.out.println(" Nagyítás számolás...");
        /*for (int i = 1; i <= index; i++) {
         System.out.println("diffx:" + diffx[i] + "   diffy:" + diffy[i] );
         diffx[i]-=min_x;
         diffy[i]-=min_y;
         System.out.println("diffx:" + diffx[i] + "   diffy:" + diffy[i] );
         System.out.println();
         } */
    }

    public void korvonal_szamito(int elem) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        int eltolasx, eltolasy;                        // A kép eltolási értéke    
        int konturszam;                               // Az aktuális szelvényt alkotó kontúrok száma (1-külső, 2 v. több-belső) és a pontsor pontjainak a száma

        //System.out.println("Szelvény: " + osszetett.get(elem).getNev());
        kontur.clear();
        konturszam = 0;
        session.beginTransaction();
        parancs = "FROM Kontur where nev ='" + osszetett.get(elem).getNev() + "'";
        kontur = session.createQuery(parancs).list();
        for (int i = 1; i <= kontur.size(); i++) {
            if (kontur.get(i - 1).getVonal() > konturszam) {
                konturszam = kontur.get(i - 1).getVonal();
            }
        }
        //System.out.println("parancs:"+parancs+" size:"+konturszam);
        parancs = "FROM Kontur where nev ='" + osszetett.get(elem).getNev() + "' order by vonal,sorszam";
        kontur = session.createQuery(parancs).list();
        //System.out.println("parancs:"+parancs+" size:"+kontur.size());
        session.getTransaction().commit();
        session.close();
        szelesseg = szelveny.get(elem).getSzelesseg();
        magassag = szelveny.get(elem).getMagassag();
        eltolasx = Integer.MAX_VALUE;
        eltolasy = Integer.MAX_VALUE;
        for (int i = 1; i <= konturszam; i++) {
            // Az alkotó szelvény körvonala 10*-es nagyításban
            //System.out.println();System.out.println("Kontúrvonal:" + i+" elem:"+elem);
            Konturvonal egykonturvonal = new Konturvonal();        // A kontúrvonalak X-,Y- koordinátái
            egykonturvonal.setVonal(i);
            for (int j = 1; j <= kontur.size(); j++) {
                if (kontur.get(j - 1).getVonal() == egykonturvonal.getVonal()) {
                    egykonturvonal.pontgenerator(j - 1,
                            kontur.get(j - 1).getJelleg(),
                            szelesseg,
                            magassag,
                            osszetett.get(elem).getSzog(),
                            kontur.get(j - 1).getX1(),
                            kontur.get(j - 1).getX2(),
                            kontur.get(j - 1).getY1(),
                            kontur.get(j - 1).getY2(),
                            kontur.get(j - 1).getR1(),
                            kontur.get(j - 1).getR2(),
                            kontur.get(j - 1).getIrany(),
                            1, 100);
                    //System.out.println("j:" + j + " x1:" + kontur.get(j - 1).getX1() + " y1:" + kontur.get(j - 1).getY1() + " x2:" + kontur.get(j - 1).getX2() + " y2:" + kontur.get(j - 1).getY2() + " A pontok száma:" + egykonturvonal.getKonturpont().size());
                }
            }
            /*for (int k = 0; k < egykonturvonal.getKonturpont().size(); k++) {
             if (elem ==6) {System.out.println("i0:"+i+" id:"+k+" x:"+egykonturvonal.getKonturpont().get(k).getX()+" y:"+egykonturvonal.getKonturpont().get(k).getY());}
             }*/
            // A tükrözés
            if (osszetett.get(elem).getMirrorx() == 1) {
                for (int k = 0; k < egykonturvonal.getKonturpont().size(); k++) {
                    egykonturvonal.getKonturpont().get(k).setX((int) (szelesseg / 2)
                            - (egykonturvonal.getKonturpont().get(k).getX() - (int) (szelesseg / 2)));
                    //pontokx[i] = (int) ((profil.getSzelesseg() / 2) * arany) - (pontokx[i] - (int) ((profil.getSzelesseg() / 2) * arany));
                }
            }
            if (osszetett.get(elem).getMirrory() == 1) {
                for (int k = 0; k < egykonturvonal.getKonturpont().size(); k++) {
                    egykonturvonal.getKonturpont().get(k).setY((int) (magassag / 2)
                            - (egykonturvonal.getKonturpont().get(k).getY() - (int) (magassag / 2)));
                    //pontoky[i] = (int) ((profil.getMagassag() / 2) * arany) - (pontoky[i] - (int) ((profil.getMagassag() / 2) * arany));
                }
            }
            // Az eltolás a bal felső sarokba
            //System.out.println("diffx:"+((height / 2) - kozepx)+"  Diffy:"+((height / 2) - kozepy)+" Kozepx:"+kozepx+"  kozepy"+kozepy);                
            /*for (int k = 0; k < egykonturvonal.getKonturpont().size(); k++) {
             if (elem ==6) {System.out.println("i1:"+i+" id:"+k+" x:"+egykonturvonal.getKonturpont().get(k).getX()+" y:"+egykonturvonal.getKonturpont().get(k).getY());}
             }*/
            if (i == 1) {
                // Az eltolást csak a külső (első) profilnál kell kiszámoltatni.
                for (int k = 0; k < egykonturvonal.getKonturpont().size(); k++) {
                    if (egykonturvonal.getKonturpont().get(k).getX() < eltolasx) {
                        eltolasx = egykonturvonal.getKonturpont().get(k).getX();
                    }
                    if (egykonturvonal.getKonturpont().get(k).getY() < eltolasy) {
                        eltolasy = egykonturvonal.getKonturpont().get(k).getY();
                    }
                }
            }
            // if (elem ==6) {   System.out.println("i:"+i+" eltolasx:"+eltolasx+" eltolasy:"+eltolasy);}
            for (int j = 0; j < egykonturvonal.getKonturpont().size(); j++) {
                egykonturvonal.getKonturpont().get(j).setX(egykonturvonal.getKonturpont().get(j).getX() - eltolasx);
                egykonturvonal.getKonturpont().get(j).setY(egykonturvonal.getKonturpont().get(j).getY() - eltolasy);
            }
            /*for (int k = 0; k < egykonturvonal.getKonturpont().size(); k++) {
             if (elem ==6) {System.out.println("Szelvény:"+elem+" vonal:"+i+" pont:"+k+" x:"+egykonturvonal.getKonturpont().get(k).getX()+" y:"+egykonturvonal.getKonturpont().get(k).getY());}
             } */
            // A magasság és a szélesség újraszámoltatása
            if (i == 1) {
                szelvenyelemek.get(elem).setMaxx(Integer.MIN_VALUE);
                szelvenyelemek.get(elem).setMaxy(Integer.MIN_VALUE);
                for (int k = 0; k < egykonturvonal.getKonturpont().size(); k++) {
                    if (egykonturvonal.getKonturpont().get(k).getX() > szelvenyelemek.get(elem).getMaxx()) {
                        szelvenyelemek.get(elem).setMaxx(egykonturvonal.getKonturpont().get(k).getX());
                    }
                    if (egykonturvonal.getKonturpont().get(k).getY() > szelvenyelemek.get(elem).getMaxy()) {
                        szelvenyelemek.get(elem).setMaxy(egykonturvonal.getKonturpont().get(k).getY());
                    }
                }
            }
            szelvenyelemek.get(elem).getKonturvonal().add(egykonturvonal);
            //System.out.println("Elemszám:"+egykonturvonal.getKonturpont().size());                        
        }
    }

    public void keresztmetszeti_jellemzok(int elem) {

        // System.out.println("elem:" + elem + " szél:" + szelvenyelemek.get(elem).getMaxx() + " magasság:" + szelvenyelemek.get(elem).getMaxy());
        BufferedImage bi_teljes = kepkeszito(szelvenyelemek.get(elem), elem, 10, 2);  // A 10*-es méretű rajz elkészítése        

        // A terület számítás
        Integer terulet = 0;
        for (int i = 0; i < bi_teljes.getWidth(); i++) {
            for (int j = 0; j < bi_teljes.getHeight(); j++) {
                Color originalColor = new Color(bi_teljes.getRGB(i, j));
                int r = originalColor.getRed();
                int g = originalColor.getGreen();
                int b = originalColor.getBlue();
                if (r + g + b == 0) {
                    terulet++;
                }
            }
        }
        // A statikai nyomaték számítása
        Integer sx = 0;
        Integer sy = 0;
        for (int i = 0; i < bi_teljes.getWidth(); i++) {
            for (int j = 0; j < bi_teljes.getHeight(); j++) {
                Color originalColor = new Color(bi_teljes.getRGB(i, j));
                int r = originalColor.getRed();
                int g = originalColor.getGreen();
                int b = originalColor.getBlue();
                if (r + g + b == 0) {
                    sx += i;
                    sy += j;
                }
            }
        }
        szelvenyelemek.get(elem).setEx(Float.parseFloat(String.valueOf((sx / terulet) / 10)));
        szelvenyelemek.get(elem).setEy(Float.parseFloat(String.valueOf((sy / terulet) / 10)));

        try {
            String szoveg1 = "./images/szelveny/big_" + String.valueOf(elem) + ".png";
            ImageIO.write(bi_teljes, "PNG", new File(szoveg1));

        } catch (IOException ie) {
            ie.printStackTrace();
        }
    }

    public void pozicio_szamolo() throws IOException {
        for (int i = 0; i < osszetett.size(); i++) {
            szelvenyelemek.get(i).setPoziciox((int) ((osszetett.get(i).getDiffx() - szelvenyelemek.get(i).getEx() - min_x) * nagyitas));
            szelvenyelemek.get(i).setPozicioy((int) ((osszetett.get(i).getDiffy() - szelvenyelemek.get(i).getEy() - min_y) * nagyitas));
            //System.out.println("i:" + i + " posx:" + szelvenyelemek.get(i).getPoziciox() + " posy:" + szelvenyelemek.get(i).getPozicioy());
        }
        // Az alkotó szelvényekből egy 1pont=1mm méretű nagyrajz készítése

        //System.out.println("minx:" + min_x + " maxx:" + max_x + " xdiff:" + (int) (max_x - min_x) + " miny:" + min_y + " maxy:" + max_y + " ydiff:" + (int) (max_y - min_y));
        /*g.setColor(Color.white);
         g.fillRect(0, 0, width, height);*/
        Stroke drawingStroke = new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]{21.0f, 9.0f, 3.0f, 9.0f}, 0);
        g.setStroke(drawingStroke);
        for (int i = 0; i < osszetett.size(); i++) {
            osszetett.get(i).setHiba("");
            BufferedImage bi_atmeneti = kepkeszito(szelvenyelemek.get(i), i, nagyitas, 2);  // A teljes méretű rajz elkészítése sraffozva
            try {
                String szoveg1 = "./images/szelveny/n_" + String.valueOf(i) + ".png";
                ImageIO.write(bi_atmeneti, "PNG", new File(szoveg1));

            } catch (IOException ie) {
                ie.printStackTrace();
            }
            //System.out.println("i:" + i + " szél:" + bi_atmeneti.getWidth() + "  mag:" + bi_atmeneti.getHeight() + " maxx:" + max_x + " minx:" + min_x + " maxy:" + max_y + " miny:" + min_y);
            for (int j = 0; j < bi_atmeneti.getWidth(); j++) {
                for (int k = 0; k < bi_atmeneti.getHeight(); k++) {
                    //if ((j <= (int) (max_x - min_x)) && (k <= (int) (max_y - min_y))) {
                    Color originalColor = new Color(bi_atmeneti.getRGB(j, k));
                    int red = originalColor.getRed();
                    int green = originalColor.getGreen();
                    int blue = originalColor.getBlue();
                    if (red + green + blue == 0) {

                        int koordx = j + szelvenyelemek.get(i).getPoziciox() + 10;
                        int koordy = k + szelvenyelemek.get(i).getPozicioy() + 10;

                        /*if (koordx >= (int) (max_x - min_x)) {  koordx = (int) (max_x - min_x) - 1;     }
                         if (koordx < 0) { koordx = 0; }
                         if (koordy >= (int) (max_y - min_y)) {  koordy = (int) (max_y - min_y) - 1;   }                            
                         if (koordy < 0) { koordy = 0; }*/
                        //System.out.println("i:"+i+" j:"+j+"  k:"+k+"  Koordx:"+koordx+"  koordy:"+koordy);
                        if ((koordx < bi.getWidth()) && (koordy < bi.getHeight()) && (koordx > -1) && (koordy > -1)) {

                            Color eredetiColor = new Color(bi.getRGB(j, k));
                            int eredetired = eredetiColor.getRed();
                            int eredetigreen = eredetiColor.getGreen();
                            int eredetiblue = eredetiColor.getBlue();
                            if (eredetired + eredetigreen + eredetiblue > 0) {
                                osszetett.get(i).setHiba("H");
                                //System.out.println("i:"+i+" hiba");
                            }
                            bi.setRGB(koordx, koordy, szelvenyelemek.get(i).getRajzszin().getRGB());
                        }
                    }
                    //}
                }
                g.setColor(Color.red);
                int x1 = (int) (((float) szelvenyelemek.get(i).getPoziciox() + szelvenyelemek.get(i).getEx() * nagyitas));
                int y1 = szelvenyelemek.get(i).getPozicioy() - 10;
                int y2 = szelvenyelemek.get(i).getPozicioy() + (int) (szelvenyelemek.get(i).getMaxy() * nagyitas / 100) + 10;
                g.drawLine(x1 + 10, y1 + 10, x1 + 10, y2 + 10);
                x1 = szelvenyelemek.get(i).getPoziciox() - 10;
                int x2 = szelvenyelemek.get(i).getPoziciox() + (int) (szelvenyelemek.get(i).getMaxx() * nagyitas / 100) + 10;
                y1 = (int) (((float) szelvenyelemek.get(i).getPozicioy() + szelvenyelemek.get(i).getEy() * nagyitas));
                g.drawLine(x1 + 10, y1 + 10, x2 + 10, y1 + 10);
            }
        }
        ImageIO.write(bi, "PNG", new File("./images/szelveny/teljes.png"));
    }

    public BufferedImage kepkeszito(Szelvenyelemek szelveny, int elem, float meretarany, int mod) {

        // mód : 1-sraffozva, 2 - teli, 3 - csak a körvonal
        int width1 = (int) ((szelveny.getMaxx() / 100) * meretarany);
        int height1 = (int) ((szelveny.getMaxy() / 100) * meretarany);
        //System.out.println("elem:"+elem+" Maxx:"+szelvenyelemek.get(elem).getMaxx()+" Maxy:"+szelvenyelemek.get(elem).getMaxy()+" szél:"+width+" mag:"+height);
        BufferedImage bi_atmeneti = new BufferedImage(width1, height1, BufferedImage.TYPE_BYTE_BINARY);
        Graphics2D g_atmeneti = bi_atmeneti.createGraphics();
        Stroke vastagvonal = new BasicStroke(2);

        g_atmeneti.setColor(Color.white);
        g_atmeneti.fillRect(0, 0, width1, height1);
        g_atmeneti.setColor(Color.black);

        for (int i = 0; i < szelveny.getKonturvonal().size(); i++) {
            if (i == 0) {
                if (mod!=0) {
                    // A sraffozás
                    BufferedImage sraffozas = new BufferedImage(20, 20, BufferedImage.TYPE_BYTE_BINARY);
                    Graphics2D g2 = sraffozas.createGraphics();
                    g2.setColor(Color.white);
                    g2.fillRect(0, 0, 20, 20);
                    g2.setColor(Color.black);
                    g2.drawLine(0, 0, 20, 20); // \
                    if (elem == 0) {
                        g2.drawLine(10, 0, 20, 10);
                        g2.drawLine(0, 10, 10, 20);
                    } // /
                    // teljesenkitöltve
                    if (mod==2) {
                        g2.setColor(Color.black);
                        g2.fillRect(0, 0, 20, 20);
                    }
                    Rectangle2D rect = new Rectangle2D.Double(0, 0, 20, 20);
                    g_atmeneti.setPaint(new TexturePaint(sraffozas, rect));
                }
            } else {
                g_atmeneti.setColor(Color.white);
            }
            int pontszam = szelveny.getKonturvonal().get(i).getKonturpont().size();
            int[] pontokx = new int[pontszam];
            int[] pontoky = new int[pontszam];
            for (int k = 0; k < pontszam; k++) {
                pontokx[k] = (int) (szelveny.getKonturvonal().get(i).getKonturpont().get(k).getX() * (meretarany / 100));
                pontoky[k] = (int) (szelveny.getKonturvonal().get(i).getKonturpont().get(k).getY() * (meretarany / 100));
                //if (elem==6) {System.out.println("elem:"+elem+" pontvonal:"+i+" Pont:" + k + "  x:" + pontokx[k] + "  y:" + pontoky[k]);}
            }
            g_atmeneti.fillPolygon(pontokx, pontoky, pontszam);
            g_atmeneti.setColor(Color.black);
            g_atmeneti.setStroke(vastagvonal);
            g_atmeneti.drawPolygon(pontokx, pontoky, pontszam);
        }
        g_atmeneti.dispose();
        return bi_atmeneti;
    }

    public static BufferedImage resize(BufferedImage img, int width, int height, int newW, int newH) {
        Image tmp = img.getScaledInstance(width, height, Image.SCALE_SMOOTH);
        BufferedImage dimg = new BufferedImage(newW, newH, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = dimg.createGraphics();
        g2d.drawImage(tmp, 0, 0, null);
        g2d.dispose();
        return dimg;
    }

    /*private static void copySrcIntoDstAt(final BufferedImage src, final BufferedImage dst, final int dx, final int dy) {
     int[] srcbuf = ((DataBufferInt) src.getRaster().getDataBuffer()).getData();
     int[] dstbuf = ((DataBufferInt) dst.getRaster().getDataBuffer()).getData();
     int width = src.getWidth();
     int height = src.getHeight();
     int dstoffs = dx + dy * dst.getWidth();
     int srcoffs = 0;
     for (int y = 0; y < height; y++, dstoffs += dst.getWidth(), srcoffs += width) {
     System.arraycopy(srcbuf, srcoffs, dstbuf, dstoffs, width);
     }
     }*/
    private static void copySrcIntoDstAt1(final BufferedImage src, final BufferedImage dst, final int dx, final int dy) {
        // TODO: replace this by a much more efficient method
        for (int x = 0; x < src.getWidth(); x++) {
            for (int y = 0; y < src.getHeight(); y++) {
                dst.setRGB(dx + x, dy + y, src.getRGB(x, y));
            }
        }
    }

    public void pngrajz() {
        String szoveg;
        float adat;
        int x1;
        adatbeolvaso();
        if (index > 0) {
            // Az alkotó szelvények létrehozása egyenként - eredeti méret
            for (int i = 0; i < index; i++) {
                Szelvenyelemek szelvenyelem = new Szelvenyelemek();
                if (i == 0) {
                    szelvenyelem.setRajzszin(Color.BLACK);
                } else {
                    x1 = (int) (Math.random() * szinek.size());
                    szelvenyelem.setRajzszin(new Color(szinek.get(x1).getR(), szinek.get(x1).getG(), szinek.get(x1).getB()));
                }
                szelvenyelemek.add(szelvenyelem);
                korvonal_szamito(i);
                keresztmetszeti_jellemzok(i);
            }
        }
        nagyitas_szamolo();  // és a keresztmetszeti jellemzők is számolódnak                        
        adatmodosito();      // A keresztmetszeti jellemzők visszaíródnak
        try {
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

            //bi = resize(bi_nagyrajz, width, height, width, height);
            g.setColor(Color.LIGHT_GRAY);
            g.drawLine(height, 0, height, height);
            // A segédvonalak
            Stroke vekonyvonal = new BasicStroke(1);
            g.setStroke(vekonyvonal);
            Color sotetszurke = new Color(60, 60, 60);
            Color kozepszurke = new Color(128, 128, 128);
            Color vilagosszurke = new Color(190, 190, 190);

            // Az 1-es vonalak
            if (nagyitas > 1) {
                g.setColor(vilagosszurke);
                adat = height / 2 + 10 * nagyitas;
                while ((int) adat < height) {
                    g.drawLine((int) adat, 0, (int) adat, height);
                    g.drawLine(0, (int) adat, height, (int) adat);
                    adat += 10 * nagyitas;
                }
                adat = height / 2 - 10 * nagyitas;
                while ((int) adat > 0) {
                    g.drawLine((int) adat, 0, (int) adat, height);
                    g.drawLine(0, (int) adat, height, (int) adat);
                    adat -= 10 * nagyitas;
                }
            }
            if (nagyitas > 0.5) {
                // Az 5-ös vonalak
                g.setColor(kozepszurke);
                adat = height / 2 + 50 * nagyitas;
                while ((int) adat < height) {
                    g.drawLine((int) adat, 0, (int) adat, height);
                    g.drawLine(0, (int) adat, height, (int) adat);
                    adat += 50 * nagyitas;
                }
                adat = height / 2 - 50 * nagyitas;
                while ((int) adat > 0) {
                    g.drawLine((int) adat, 0, (int) adat, height);
                    g.drawLine(0, (int) adat, height, (int) adat);
                    adat -= 50 * nagyitas;
                }
            }
            // A 10-es vonalak
            g.setColor(sotetszurke);
            adat = height / 2 + 100 * nagyitas;
            while ((int) adat < height) {
                g.drawLine((int) adat, 0, (int) adat, height);
                g.drawLine(0, (int) adat, height, (int) adat);
                adat += 100 * nagyitas;
            }
            adat = height / 2 - 100 * nagyitas;
            while ((int) adat > 0) {
                g.drawLine((int) adat, 0, (int) adat, height);
                g.drawLine(0, (int) adat, height, (int) adat);
                adat -= 100 * nagyitas;
            }

            try {
                pozicio_szamolo();  // teljes rajz felépítése
            } catch (IOException ex) {
                Logger.getLogger(kmjellosszadatok.class.getName()).log(Level.SEVERE, null, ex);
            }

            // A súlypont középvonalai
            g.setStroke(drawingStroke);

            int kozepvonalx = (int) ((szelveny_szamolt.getEx() - min_x) * nagyitas) + 10;
            int kozepvonaly = (int) ((szelveny_szamolt.getEy() - min_y) * nagyitas) + 10;

            //System.out.println(" ex:" + szelveny_szamolt.getEx() + " ey:" + szelveny_szamolt.getEy() + "  x:" + kozepvonalx + "  y:" + kozepvonaly + "  szélesség:" + szelesseg + "  Magasság:" + magassag + "  Arány:" + nagyitas);
            Line2D centerlinex = new Line2D.Double(kozepvonalx, 0, kozepvonalx, height);
            Line2D centerliney = new Line2D.Double(0, kozepvonaly, height, kozepvonaly);
            g.setColor(Color.blue);
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
            g.drawString("X-", 5, height / 2 - 5);
            g.drawString("X+", height - 10, height / 2 + 15);
            g.drawString("Y-", height / 2 - 20, 15);
            g.drawString("Y+", height / 2 + 10, height - 10);
            szoveg = "A szelvény neve:";
            g.drawString(szoveg, height + 5, 20);
            g.setFont(Courier12b);
            szoveg = nev;
            g.drawString(szoveg, height + 10, 35);
            g.drawString("Az alkotó szelvények:", height + 10, 50);
            g.setFont(Courier10b);
            for (int i = 0; i < index; i++) {
                szoveg = String.valueOf(i) + "." + osszetett.get(i).getNev();
                //System.out.println ("Profil:"+i+" szelv:"+osszetett.get(i - 1).getNev());
                g.setColor(szelvenyelemek.get(i).getRajzszin());
                g.drawString(szoveg, height + 10, 60 + i * 10);
            }
            g.setColor(Color.black);
            g.setFont(Courier12b);
            g.drawString("Számolt adatok:", height + 10, 230);
            g.setFont(Courier10b);
            szoveg = "A    : " + String.format("%.2f", szelveny_szamolt.getA()) + " cm^2";
            g.drawString(szoveg, height + 10, 240);
            szoveg = "Ix   : " + String.format("%.2f", szelveny_szamolt.getIx()) + " cm^4";
            g.drawString(szoveg, height + 10, 250);
            szoveg = "Iy   : " + String.format("%.2f", szelveny_szamolt.getIy()) + " cm^4";
            g.drawString(szoveg, height + 10, 260);
            szoveg = "Sx   : " + String.format("%.2f", szelveny_szamolt.getSx()) + " cm^3";
            g.drawString(szoveg, height + 10, 270);
            szoveg = "Sy   : " + String.format("%.2f", szelveny_szamolt.getSy()) + " cm^3";
            g.drawString(szoveg, height + 10, 280);
            szoveg = "ex   : " + String.format("%.2f", szelveny_szamolt.getEx()) + " cm";
            g.drawString(szoveg, height + 10, 290);
            szoveg = "ey   : " + String.format("%.2f", szelveny_szamolt.getEy()) + " cm";
            g.drawString(szoveg, height + 10, 300);
            szoveg = "ix   : " + String.format("%.2f", szelveny_szamolt.getInx()) + " cm";
            g.drawString(szoveg, height + 10, 310);
            szoveg = "iy   : " + String.format("%.2f", szelveny_szamolt.getIny()) + " cm";
            g.drawString(szoveg, height + 10, 320);
            szoveg = "Kx   : " + String.format("%.2f", szelveny_szamolt.getKx()) + " cm^3";
            g.drawString(szoveg, height + 10, 330);
            szoveg = "Ky   : " + String.format("%.2f", szelveny_szamolt.getKy()) + " cm^3";
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
