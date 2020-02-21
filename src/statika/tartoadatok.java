/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package statika;

import Entities.Konturvonal;
import Entities.Metszek;
import Entities.Profil;
import Entities.Szelveny;
import Entities.Szelvenyelemek;
import Entities.Tartoerok;
import Entities.Tartok;
import Hibernate.HibernateUtil;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.geom.Line2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.imageio.ImageIO;
import org.hibernate.Query;
import org.hibernate.Session;

/**
 *
 * @author duslaci
 */
public class tartoadatok {

    int db = 100;                                   // A felrakható erők száma  -->> limit... az SQL-ben    
    String ProjektNev, megnevezes, szelveny;        // Ez a tartó megnevezése
    String parancs;                                 // A MySQL parancsok gyűjtőhelye
    float fa, fb, f, ma, m;                         // A támaszerők, max_erő, konzolnál a támasznyomaték és a max nyomaték
    int akt_ero;                                    // Az aktuális erő indexe
    int akt_megoszlo;                               // Az aktuális megoszló teher indexe    
    int akt_nyomatek;                               // Az aktuális nyomatek indexe
    int nyil_db;                                    // A koncentrált erőknek a száma
    float[][] ero = new float[db][2];               // Az aktuális erő koordinátája és értéke
    int megoszlo_db;                                // A megoszló terhek a száma
    float[][] megoszlo = new float[db][3];          // A megoszló terhek kezdő koordinátája, hossza, értéke 
    int nyomatek_db;                                // A nyomatékok a száma
    float[][] nyomatek = new float[db][2];          // A nyomatek koordinátája és értéke
    int metszekszam = 500;                          // A metszékek száma (570-70...)
    int metszekszam1 = 500 + 1;                     // A metszékek száma a pline bezárásához (570-70 + 1...)    
    List<Tartok> tarto = new ArrayList<>();         // A tartó adatai. Csak egy adat van benne -> get(0)
    List<Szelveny> profil = new ArrayList<>();      // A tartó szelvényének az adatai. Csak egy adat van benne -> get(0)  
    List<Szelvenyelemek> szelvenyelemek = new ArrayList<>();    // Az összetett szelvények adathalmaza : adatok + a körvonala 100*-os nagyításban
    List<Tartoerok> tartoerok = new ArrayList<>();
    int x1, x2;                                     // Ez jelzi majd a negatív erőt/nyomatékot (A rajzhoz: 0 vagy 1)
    int[] maxertekhely = new int[7];                // A maximális értékek helyei 0-T,1-M,2-szigma,3-tau,4-szigmaö, 5-lehajlás, 6-szögfordulás (metszékindex!!)
    int[] pontokx = new int[metszekszam1];          // Átmeneti tárolóhely az ábrák kijelzéséhez
    int[] pontoky = new int[metszekszam1];          // Átmeneti tárolóhely az ábrák kijelzéséhez (70-570)
    int[] nyilpontx = new int[10];                  // Átmeneti tárolóhely a nyilak kijelzéséhez
    int[] nyilponty = new int[10];                  // Átmeneti tárolóhely a nyilak kijelzéséhez 
    String filenev1, filenev2, filenev3;            // A kimeneti PNG file-ok neve        
    List<Metszek> metszekek = new ArrayList<>();    // Az aktuális pont nyíróerőértéke(0),nyomatéki értéke(1), A(2),Ix(3),Kx(4),Sx(5),v(6), Tau(7), Szigma(8), Szigma_ö(9), elfordulása(10), lehajlása(11)+ metszékkijelzés(12) + Átmeneti tároló a munkamódszerhez (13)   
    List<Profil> profilok = new ArrayList<>();      // Az aktuális profil adatai: Szelvénynév,A,Ix,Kx,Sx,v, hely, hossz
    Float arany;                                    // A grafikon kirajzolásánál a méretarány
    Float maximum;                                  // A kijelzett grafikon maximum értéke
    Float lehajlas, szogfordulas;                   // Az elmozdulások átmeneti értéke
    int eltolas;                                    // A grafikon bázisvonala
    int e;                                         // A Rugalmassági modulus 20600 az acélnál
    Float metszekhossz;                             // Egy elemi metszék hossza  tartóhossz / metszékszám
    float[] nyiroero = new float[metszekszam];      // A nyíróerő metszéki értékei
    float[] nyomatek_ertek = new float[metszekszam];// A nyomaték metszéki értékei    

    int width = 640, height = 480;                  // A kijelzett kép mérete 
    // A nyíróerők(T), nyomatéki értékek(M) rajza
    BufferedImage bi1 = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
    Graphics2D g1 = bi1.createGraphics();
    // A belső feszültségek (Tau, Szigma, SzigmaÖ) rajza   
    BufferedImage bi2 = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
    Graphics2D g2 = bi2.createGraphics();
    // Az alakvátozási grafikonok rajza 
    BufferedImage bi3 = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
    Graphics2D g3 = bi3.createGraphics();

    Query query;

    public void letrehoz() {
        // Létrehozás és inicializálás
        tarto.clear();
        profil.clear();
        tartoerok.clear();
        for (int i = 0; i < metszekszam; i++) {
            pontokx[i] = i + 70;
            nyiroero[i] = 0;
            nyomatek_ertek[i] = 0;
        }
        pontokx[metszekszam] = 570;
        //System.out.println("Létrehozás/ adattörlés");
        e = 20600;
    }

    public void konvertalo(int k) {
        arany = 0f;
        maximum = 0f;
        switch (k) {
            case 1:
                eltolas = 242;
                break;
            case 8:
                eltolas = 242;
                break;
            case 9:
                eltolas = 352;
                break;
            case 11:
                eltolas = 242;
                break;
            default:
                eltolas = 132;
        }

        for (int i = 0; i < metszekszam; i++) {
            switch (k) {
                case 1:
                    if (Math.abs(metszekek.get(i).getNyomatek()) > maximum) {
                        maximum = Math.abs(metszekek.get(i).getNyomatek());
                    }
                    break;
                case 7:
                    if (Math.abs(metszekek.get(i).getSzigma()) > maximum) {
                        maximum = Math.abs(metszekek.get(i).getSzigma());
                    }
                    break;
                case 8:
                    if (Math.abs(metszekek.get(i).getTau()) > maximum) {
                        maximum = Math.abs(metszekek.get(i).getTau());
                    }
                    break;
                case 9:
                    if (Math.abs(metszekek.get(i).getOsszehasonlito_szigma()) > maximum) {
                        maximum = Math.abs(metszekek.get(i).getOsszehasonlito_szigma());
                    }
                    break;
                case 10:
                    if (Math.abs(metszekek.get(i).getLehajlas()) > maximum) {
                        maximum = Math.abs(metszekek.get(i).getLehajlas());
                    }
                    break;
                case 11:
                    if (Math.abs(metszekek.get(i).getSzogfordulas()) > maximum) {
                        maximum = Math.abs(metszekek.get(i).getSzogfordulas());
                    }
                    break;
                default:
                    if (Math.abs(metszekek.get(i).getNyiroero()) > maximum) {
                        maximum = Math.abs(metszekek.get(i).getNyiroero());
                    }
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
        //System.out.println("k: " + k + " eltolas:" + eltolas + "  arany:" + arany + " Maximum:" + maximum);
        for (int i = 0; i < metszekszam; i++) {
            switch (k) {
                case 1:
                    pontoky[i] = (int) (metszekek.get(i).getNyomatek() * arany) + eltolas;
                    break;
                case 7:
                    pontoky[i] = (int) (metszekek.get(i).getSzigma() * arany) + eltolas;
                    break;
                case 8:
                    pontoky[i] = (int) (metszekek.get(i).getTau() * arany) + eltolas;
                    break;
                case 9:
                    pontoky[i] = (int) (metszekek.get(i).getOsszehasonlito_szigma() * arany) + eltolas;
                    break;
                case 10:
                    pontoky[i] = (int) (metszekek.get(i).getLehajlas() * arany) + eltolas;
                    break;
                case 11:
                    pontoky[i] = (int) (metszekek.get(i).getSzogfordulas() * arany) + eltolas;
                    break;
                default:
                    pontoky[i] = (int) (metszekek.get(i).getNyiroero() * arany) + eltolas;
            }
        }
        pontoky[metszekszam - 1] = eltolas;
    }

    public void beolvas() {
        Session session = HibernateUtil.getSessionFactory().openSession();
        // Az adatok beolvasása az adatbázisból -->> //localhost/statika 
        letrehoz();  // Inicializálás, nullázás
        session.beginTransaction();
        parancs = "FROM Tartok where tartonev = '" + megnevezes + "' and projekt = '" + ProjektNev + "'";
        //System.out.println(parancs);
        tarto = session.createQuery(parancs).list();
        szelveny = tarto.get(0).getSzelveny();
        //System.out.print("Projekt:" + tarto.get(0).getProjekt());
        //System.out.println("Projekt:" + tarto.get(0).getTartonev());
        filenev1 = "./images/tartok/" + ProjektNev + "_" + tarto.get(0).getTartonev() + "_1.png";
        filenev2 = "./images/tartok/" + ProjektNev + "_" + tarto.get(0).getTartonev() + "_2.png";
        filenev3 = "./images/tartok/" + ProjektNev + "_" + tarto.get(0).getTartonev() + "_3.png";
        // Az aktív tartó beállítása
        parancs = "update Tartok set aktiv = '0' where aktiv = '1'";
        query = session.createQuery(parancs);
        query.executeUpdate();
        parancs = "update Tartok set aktiv = '1' where tartonev='";
        parancs = parancs + megnevezes + "'";
        query = session.createQuery(parancs);
        query.executeUpdate();
        // A terhek beolvasása
        parancs = "FROM Tartoerok where tartonev = '" + megnevezes + "' and projekt = '" + tarto.get(0).getProjekt() + "'";
        //System.out.println("HQL:"+parancs);
        tartoerok = session.createQuery(parancs).list();
        nyil_db = 0;
        megoszlo_db = 0;
        nyomatek_db = 0;
        for (int i = 0; i < tartoerok.size(); i++) {
            if (tartoerok.get(i).getJelleg() == 1) {
                nyil_db++;
                ero[nyil_db][0] = tartoerok.get(i).getHely();
                ero[nyil_db][1] = tartoerok.get(i).getErtek();
            }
            if (tartoerok.get(i).getJelleg() == 2) {
                megoszlo_db++;
                megoszlo[megoszlo_db][0] = tartoerok.get(i).getHely();
                megoszlo[megoszlo_db][1] = tartoerok.get(i).getHossz();
                megoszlo[megoszlo_db][2] = tartoerok.get(i).getErtek();
            }
            if (tartoerok.get(i).getJelleg() == 3) {
                nyomatek_db++;
                nyomatek[nyomatek_db][0] = tartoerok.get(i).getHely();
                nyomatek[nyomatek_db][1] = tartoerok.get(i).getErtek();
            }
        }
        // A szelvényadatok feltöltése
        // A tartó homogénként van feltételezve
        parancs = "FROM Szelveny where nev ='" + szelveny + "'";
        //System.out.println("HQL:"+parancs);
        profil = session.createQuery(parancs).list();
        session.getTransaction().commit();
        session.close();
        // Az első - alapgerenda - szelvény betöltése 
        Profil ujprofil = new Profil();
        ujprofil.setNev(profil.get(0).getNev());
        ujprofil.setA(profil.get(0).getA());
        ujprofil.setIx(profil.get(0).getIx());
        ujprofil.setKx(profil.get(0).getKx());
        ujprofil.setSx(profil.get(0).getSx());
        ujprofil.setV(profil.get(0).getV());
        //System.out.println("A:"+profil.get(0).getA()+"; Ix:"+profil.get(0).getIx()+"; Kx:"+profil.get(0).getKx()+"; Sx:"+profil.get(0).getSx()+"; v:"+profil.get(0).getV());
        ujprofil.setHely(0);
        ujprofil.setHossz(tarto.get(0).getHossz() + tarto.get(0).getKonzol1() + tarto.get(0).getKonzol2());
        profilok.add(ujprofil);
        // A többi - esetleges - szelvény betöltése
    }

    public void elmozdulas_szamito(Integer pozicio) {
        float[] atmeneti_nyiroero = new float[metszekszam]; // A lehajlás/szögforduláshoz szükséges nyíróerő metszéki értékek
        float[] atmeneti_nyomatek = new float[metszekszam]; // A lehajlás/szögforduláshoz szükséges nyomaték metszéki értékek
        float fa_temp, fb_temp, ma_temp;                    // A támaszerők, max_erő, konzolnál a támasznyomaték és a max nyomaték

        // Lehajlás számítás egységnyi erőből
        fa_temp = 0;
        fb_temp = 0;
        ma_temp = 0;
        lehajlas = 0f;
        float hely = pozicio * metszekhossz;
        for (int i = 0; i < metszekszam; i++) {
            atmeneti_nyiroero[i] = 0;
            atmeneti_nyomatek[i] = 0;
        }
        // A támaszerők
        float eropont = hely - tarto.get(0).getKonzol1();
        if (hely < tarto.get(0).getKonzol1()) {
            eropont = (tarto.get(0).getKonzol1() - hely) * -1;
        }
        // MA nyomatéka /FB támaszereje
        if (tarto.get(0).getTipus() == 2) {  // konzolos tartó
            ma_temp = hely;
        } else {
            fb_temp = eropont / tarto.get(0).getHossz();
        }
        // FA támaszereje
        if (tarto.get(0).getTipus() == 2) {  // konzolos tartó
            fa_temp = 1;
        } else {
            fa_temp = 1 - (eropont / tarto.get(0).getHossz());
        }
        // A nyíróerő/nyomatéki metszékek kiszámolása
        for (int j = 0; j < metszekszam - 1; j++) {
            if ((j * metszekhossz) > hely) {
                atmeneti_nyiroero[j] = 1;
            }
        }
        // A támaszerőkből adódó metszéki értékek
        for (int j = 0; j < metszekszam; j++) {
            if (tarto.get(0).getTipus() == 2) {
                atmeneti_nyiroero[j] -= fa_temp;
            } else {
                if ((j * metszekhossz) > tarto.get(0).getKonzol1()) {
                    atmeneti_nyiroero[j] -= fa_temp;
                }
            }
            if ((j * metszekhossz) > (tarto.get(0).getKonzol1() + tarto.get(0).getHossz())) {
                atmeneti_nyiroero[j] -= fb_temp;
            }
        }
        if (ma_temp != 0) {
            atmeneti_nyomatek[0] -= ma_temp;
        }
        for (int j = 1; j < metszekszam; j++) {
            atmeneti_nyomatek[j] = atmeneti_nyomatek[j - 1] + metszekhossz * atmeneti_nyiroero[j];
        }
        // A lehajlási érték
        for (int j = 1; j < metszekszam; j++) {
            lehajlas -= nyomatek_ertek[j] * metszekhossz * atmeneti_nyomatek[j];
        }

        // szögfordulás számítás egységnyi nyomatékból        
        fa_temp = 0;
        fb_temp = 0;
        ma_temp = 0;
        szogfordulas = 0f;
        for (int i = 0; i < metszekszam; i++) {
            atmeneti_nyiroero[i] = 0;
            atmeneti_nyomatek[i] = 0;
        }
        // A támaszerők
        if (tarto.get(0).getTipus() == 2) {
            ma_temp = 1;
        } else {
            fa_temp = 1 / tarto.get(0).getHossz();
            fb_temp = -1 / tarto.get(0).getHossz();
        }
        // A támaszerőkből adódó metszéki értékek
        for (int j = 0; j < metszekszam; j++) {
            if (tarto.get(0).getTipus() == 2) {
                atmeneti_nyiroero[j] -= fa_temp;
            } else {
                if ((j * metszekhossz) > tarto.get(0).getKonzol1()) {
                    atmeneti_nyiroero[j] -= fa_temp;
                }
            }
            if ((j * metszekhossz) > (tarto.get(0).getKonzol1() + tarto.get(0).getHossz())) {
                atmeneti_nyiroero[j] -= fb_temp;
            }
        }

        if (ma_temp != 0) {
            atmeneti_nyomatek[0] -= ma_temp;
        }
        for (int j = 1; j < metszekszam; j++) {
            atmeneti_nyomatek[j] = atmeneti_nyomatek[j - 1] + metszekhossz * atmeneti_nyiroero[j];
        }
        for (int j = 0; j < metszekszam; j++) {
            if ((j * metszekhossz) >= hely) {
                atmeneti_nyomatek[j] += 1;
            }
        }
        // A szögfordulási érték
        for (int j = 1; j < metszekszam; j++) {
            szogfordulas += nyomatek_ertek[j] * metszekhossz * atmeneti_nyomatek[j];
        }
        //System.out.println("pozício:"+pozicio+" hely:"+hely+" fa:"+fa_temp+" fb:"+fb_temp+" szogfordulas:"+szogfordulas);
        /*if ((pozicio == 333) || (pozicio == 20)) {
         System.out.println("pozício:" + pozicio + " hely:" + hely + " erőpont:" + eropont + " fa:" + fa_temp + " fb:" + fb_temp + " szogfordulas:"+szogfordulas);
         for (int j = 0; j < metszekszam; j++) {
         System.out.println("j:" + j+"; metszet:"+(j*metszekhossz) + "; M:" + atmeneti_nyomatek[j] + "; T:" + atmeneti_nyiroero[j]);
         }
         }*/
    }

    public void kiszamol() {
        float metszet, nyiras, eropont;
        //Metszek egymetszek = new Metszek();
        // Támaszerők és befogási nyomatékok kiszámolása
        fa = 0;
        fb = 0;
        ma = 0;
        // támaszerők koncentrált erőkből
        if (nyil_db != 0) {
            for (int i = 1; i <= nyil_db; i++) {
                eropont = ero[i][0] - tarto.get(0).getKonzol1();
                //System.out.println(" eropont:" + eropont);
                if (ero[i][0] < tarto.get(0).getKonzol1()) {
                    eropont = (tarto.get(0).getKonzol1() - ero[i][0]) * -1;
                }
                // MA nyomatéka /FB támaszereje
                if (tarto.get(0).getTipus() == 2) {  // konzolos tartó
                    ma += ero[i][0] * ero[i][1];
                    //System.out.println(" ma:" + ma + " ero:" + ero[i][1]);
                } else {
                    fb += (eropont * ero[i][1]) / tarto.get(0).getHossz();
                }
                // FA támaszereje
                if (tarto.get(0).getTipus() == 2) {  // konzolos tartó
                    fa += ero[i][1];
                } else {
                    fa += ero[i][1] - ((eropont * ero[i][1]) / tarto.get(0).getHossz());
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
                if ((megoszlo[i][0] + (megoszlo[i][1] / 2)) < tarto.get(0).getKonzol1()) {
                    eropont = tarto.get(0).getKonzol1() - (megoszlo[i][0] + (megoszlo[i][1] / 2));
                }
                //System.out.println(" Erőpont:" + eropont);
                // MA nyomatéka /FB támaszereje
                if (tarto.get(0).getTipus() == 2) {  // konzolos tartó
                    ma += (megoszlo[i][0] + (megoszlo[i][1] / 2)) * megoszlo[i][2] * megoszlo[i][1];
                } else {
                    fb += ((eropont - tarto.get(0).getKonzol1()) * megoszlo[i][2] * megoszlo[i][1]) / tarto.get(0).getHossz();
                }
                // FA támaszereje
                if (tarto.get(0).getTipus() == 2) {  // konzolos tartó
                    fa += megoszlo[i][2] * megoszlo[i][1];
                } else {
                    fa += megoszlo[i][2] * megoszlo[i][1] - (((eropont - tarto.get(0).getKonzol1())
                            * megoszlo[i][2] * megoszlo[i][1]) / tarto.get(0).getHossz());
                }
            }
        }
        // Támaszerők a nyomatékokból
        if (nyomatek_db != 0) {
            for (int i = 1; i <= nyomatek_db; i++) {
                if (tarto.get(0).getTipus() == 2) {
                    ma += nyomatek[i][1];
                } else {
                    fa += nyomatek[i][1] / tarto.get(0).getHossz();
                    fb -= nyomatek[i][1] / tarto.get(0).getHossz();
                }
                //System.out.println("i:" + i + " nyomaték:" + nyomatek[i][1]+" ma:"+ma+" fa:"+fa+" fb:"+fb);
            }
        }
        metszekhossz = (tarto.get(0).getHossz() + tarto.get(0).getKonzol1() + tarto.get(0).getKonzol2()) / metszekszam;
        // A nyíróerő/nyomatéki metszékek kiszámolása
        if ((nyil_db + megoszlo_db + nyomatek_db) != 0) {
            // A támaszerők és nyíróerőmetszékek a koncentrált erőkből  
            if (nyil_db != 0) {
                //System.out.println("Nyildb:" + nyil_db);
                for (int i = 1; i <= nyil_db; i++) {
                    for (int j = 0; j < metszekszam - 1; j++) {
                        metszet = j * metszekhossz;
                        if (metszet > ero[i][0]) {
                            //System.out.print("Előtte:" + nyiroero[j] + " Növekmény:" + ero[i][1]);
                            nyiroero[j] += ero[i][1];
                        }
                        //System.out.println(" metszet:"+metszet+" Erő:"+ero[i][1]+"   t:"+nyiroero[j]);
                    }
                }
            }
            // A támaszerők és nyíróerőmetszékek a megoszló terhelésekből
            if (megoszlo_db != 0) {
                //System.out.println("Megoszlodb:" + megoszlo_db);
                for (int i = 1; i <= megoszlo_db; i++) {
                    // PHP : $nyiras = ($hosz[$i] * $ertek[$i]) / ($metszekszam * ($hosz[$i] / $tartohossz));
                    nyiras = ((megoszlo[i][1] * megoszlo[i][2]) / megoszlo[i][1]) * metszekhossz;
                    //System.out.println("nyiras:" + nyiras);
                    int k;
                    for (int j = 0; j < metszekszam; j++) {
                        metszet = j * metszekhossz;
                        // PHP :if (($metszet >= $hely[$i]) and ($metszet <= ($hely[$i] + $hosz[$i]))) { $nyiroero[$j] += $j * $nyiras; }                        
                        if ((metszet >= megoszlo[i][0]) && (metszet <= (megoszlo[i][0] + megoszlo[i][1]))) {
                            //k = j - (int) megoszlo[i][0];      
                            k = (int) (metszet - megoszlo[i][0]);
                            //System.out.println("metszek:" + k + " nyiras:" + nyiras);                            
                            nyiroero[j] += k * nyiras * (metszekszam / (tarto.get(0).getHossz() + tarto.get(0).getKonzol1() + tarto.get(0).getKonzol2()));
                        }
                        // PHP : if ($metszet > ($hely[$i] + $hosz[$i])) {$nyiroero[$j] += $hosz[$i] * $ertek[$i]; }
                        if (metszet > (megoszlo[i][0] + megoszlo[i][1])) {
                            nyiroero[j] += megoszlo[i][1] * megoszlo[i][2];
                        }
                    }
                }
            }
            // A támaszerőkből adódő nyíróerő metszéki értékek
            for (int j = 0; j < metszekszam; j++) {
                metszet = j * metszekhossz;
                //System.out.println(" Metszet:" + metszet);                                
                if (tarto.get(0).getTipus() == 2) {
                    nyiroero[j] -= fa;
                } else {
                    if (metszet > tarto.get(0).getKonzol1()) {
                        nyiroero[j] -= fa;
                        // System.out.println(" j:" + j+" metszet:"+metszet+ "  Metszek:"+nyiroero[j]);
                    }
                }
                if (metszet > (tarto.get(0).getKonzol1() + tarto.get(0).getHossz())) {
                    nyiroero[j] -= fb;
                }
            }
            // A tartóra ható nyomatéki metszéki értékek a terhelő nyomatékokból
            if (nyomatek_db != 0) {
                for (int i = 1; i <= nyomatek_db; i++) {
                    for (int j = 0; j < metszekszam; j++) {
                        if (nyomatek[i][0] >= (j * metszekhossz)) {
                            nyomatek_ertek[j] -= nyomatek[i][1];
                        }
                    }
                }
            }
            metszekek.clear();
            float szigma_o;
            f = 0;
            maxertekhely[0] = 0;
            m = 0;
            maxertekhely[1] = 0;
            float maxszigma = 0;
            maxertekhely[2] = 0;
            float maxtau = 0;
            maxertekhely[3] = 0;
            float maxszigmao = 0;
            maxertekhely[4] = 0;
            float maxlehajlas = 0;
            maxertekhely[5] = 0;
            float maxszogfordulas = 0;
            maxertekhely[6] = 0;
            if (ma != 0) {
                nyomatek_ertek[0] -= ma;
            }
            for (int j = 1; j < metszekszam; j++) {
                nyomatek_ertek[j] = nyomatek_ertek[j - 1] - metszekhossz * nyiroero[j];
            }
            for (int j = 0; j < metszekszam; j++) {
                Metszek ujmetszek = new Metszek();
                // Nyíróerő és nyomatéki értékek               
                ujmetszek.setNyiroero(nyiroero[j]);
                ujmetszek.setNyomatek(nyomatek_ertek[j]);
                // A profiladatok
                ujmetszek.setA(profilok.get(0).getA());
                ujmetszek.setIx(profilok.get(0).getIx());
                ujmetszek.setKx(profilok.get(0).getKx());
                ujmetszek.setSx(profilok.get(0).getSx());
                ujmetszek.setV(profilok.get(0).getV() / 10);   // mm-ről cm-re
                // A határfeszültségek
                ujmetszek.setSzigma(Math.abs(nyomatek_ertek[j]) / profilok.get(0).getKx());
                ujmetszek.setTau((nyiroero[j] * profilok.get(0).getSx()) / (profilok.get(0).getIx() * profilok.get(0).getV()));
                // PHP: $tau[$j] = ($nyiroero[$j] * $sx) / ($Ix * $c);
                szigma_o = ujmetszek.getSzigma() * ujmetszek.getSzigma() + 3 * ujmetszek.getTau() * ujmetszek.getTau();
                ujmetszek.setOsszehasonlito_szigma(Float.parseFloat(String.valueOf(Math.sqrt(Double.parseDouble(String.valueOf(szigma_o))))));
                elmozdulas_szamito(j);
                ujmetszek.setLehajlas(lehajlas / (e * ujmetszek.getIx()));
                ujmetszek.setSzogfordulas((szogfordulas / (e * ujmetszek.getIx()))*-1 * Float.parseFloat(String.valueOf(180 / Math.PI)));
                //ujmetszek.setSzogfordulas((szogfordulas / (e * ujmetszek.getIx()))*1000) ;
                //System.out.println("j:"+j+"; metszék:"+(j*metszekhossz)+"; lehajlás:"+(lehajlas / (e * ujmetszek.getIx()))+2"; szög:"+((szogfordulas / (e * ujmetszek.getIx())) *Float.parseFloat(String.valueOf(180/Math.PI))));
                ujmetszek.setKijelzes(0);
                metszekek.add(ujmetszek);
                // A Maximális nyíróerő és helyének meghatározása
                if (Math.abs(nyiroero[j]) > Math.abs(f)) {
                    f = nyiroero[j];
                    if (f < 0) {
                        x1 = 0;
                    } else {
                        x1 = 1;
                    }
                    maxertekhely[0] = j;
                }
                // A Maximális nyomaték és helyének meghatározása
                if (Math.abs(nyomatek_ertek[j]) > Math.abs(m)) {
                    m = nyomatek_ertek[j];
                    if (m < 0) {
                        x2 = 0;
                    } else {
                        x2 = 1;
                    }
                    maxertekhely[1] = j;
                }
                // A maximális feszültségi értékek helyének meghatározása
                // szigma
                if (Math.abs(ujmetszek.getSzigma()) > Math.abs(maxszigma)) {
                    maxszigma = ujmetszek.getSzigma();
                    maxertekhely[2] = j;
                }
                // tau
                if (Math.abs(ujmetszek.getTau()) > Math.abs(maxtau)) {
                    maxtau = ujmetszek.getTau();
                    maxertekhely[3] = j;
                }
                // Összetett feszültség 
                if (Math.abs(ujmetszek.getOsszehasonlito_szigma()) > Math.abs(maxszigmao)) {
                    maxszigmao = ujmetszek.getOsszehasonlito_szigma();
                    maxertekhely[4] = j;
                }
                // Lehajlási, elfordulási értékek            
                // lehajlás            
                if (Math.abs(ujmetszek.getLehajlas()) > Math.abs(maxlehajlas)) {
                    maxlehajlas = ujmetszek.getLehajlas();
                    maxertekhely[5] = j;
                }
                // Szögelfordulás
                if (Math.abs(ujmetszek.getSzogfordulas()) > Math.abs(maxszogfordulas)) {
                    maxszogfordulas = ujmetszek.getSzogfordulas();
                    maxertekhely[6] = j;
                }
            }
            /*
             for (int j = 0; j < metszekszam; j++) {
             System.out.println("j:" + j + "; T:" + metszekek.get(j).getNyiroero() + "; M:" + metszekek.get(j).getNyomatek()
             + "; Sz:" + metszekek.get(j).getSzigma() + "; tau:" + metszekek.get(j).getTau() + "; Szö:" + metszekek.get(j).getOsszehasonlito_szigma());
             }*/

        }
    }

    public void nyomatek_rajzolo(int tipus, Float ertek, Float hely) throws IOException {
        String szoveg;
        //bi = new BufferedImage(33, 50, BufferedImage.TYPE_BYTE_BINARY);
        arany = 500 / (tarto.get(0).getHossz() + tarto.get(0).getKonzol1() + tarto.get(0).getKonzol2());
        if (tipus == 0) {
            if (ertek < 0) {
                szoveg = "./images/ikonok/nyplusz.png";
            } else {
                szoveg = "./images/ikonok/nyminusz.png";
            }
        } else {
            if (ertek < 0) {
                szoveg = "./images/ikonok/nyomatekplusz.png";
            } else {
                szoveg = "./images/ikonok/nyomatekminusz.png";
            }
        }
        //System.out.println(szoveg);
        BufferedImage bi = ImageIO.read(new File(szoveg));
        for (int j = 0; j < bi.getWidth(); j++) {
            for (int k = 0; k < bi.getHeight(); k++) {
                Color originalColor = new Color(bi.getRGB(j, k));
                int koordx = j + (int) (hely * arany) + 70 - bi.getWidth();
                int koordy = k + 42 - (bi.getHeight() / 2);
                //System.out.println(" x:" + j + "  y:" + k+" koordx:"+koordx+" koordy:"+koordy);
                if (koordx < width) {
                    if (koordy < height) {
                        bi1.setRGB(koordx, koordy, new Color(originalColor.getRed(), originalColor.getGreen(), originalColor.getBlue()).getRGB());
                    }
                }
            }
        }
    }

    public void abramasolo() {
        for (int j = 0; j < width; j++) {
            for (int k = 0; k < height; k++) {
                Color originalColor = new Color(bi1.getRGB(j, k));
                bi2.setRGB(j, k, new Color(originalColor.getRed(), originalColor.getGreen(), originalColor.getBlue()).getRGB());
                bi3.setRGB(j, k, new Color(originalColor.getRed(), originalColor.getGreen(), originalColor.getBlue()).getRGB());
            }
        }
    }

    
    public void pngrajz() {
        String szoveg;
        int bazis;
        float arany;
        try {
            // TYPE_INT_ARGB specifies the image format: 8-bit RGBA packed into integer pixels
            /*BufferedImage bi = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
             Graphics2D g = bi.createGraphics();*/
            arany = 500 / (tarto.get(0).getHossz() + tarto.get(0).getKonzol1() + tarto.get(0).getKonzol2());
            Font Courier10 = new Font("Courier New", Font.PLAIN, 10);
            Font Courier10b = new Font("Courier New", Font.BOLD, 10);
            Font Courier12b = new Font("Courier New", Font.BOLD, 12);
            Font Courier16b = new Font("Courier New", Font.BOLD, 16);
            Font Courier32b = new Font("Courier New", Font.BOLD, 32);
            Font Courier32 = new Font("Courier New", Font.PLAIN, 32);
            Font Courier24 = new Font("Courier New", Font.PLAIN, 24);
            Font Courier24b = new Font("Courier New", Font.BOLD, 24);
            Font Courier18b = new Font("Courier New", Font.BOLD, 18);
            Stroke vastagvonal = new BasicStroke(2);
            Stroke vekonyvonal = new BasicStroke(1);
            g1.setColor(Color.white);
            g1.fillRect(0, 0, width, height);
            g1.setFont(Courier10b);
            // A terhelő nyomatékok kijelzése   
            bazis = 70;
            if (tarto.get(0).getTipus() != 2) {
                if (nyomatek_db > 0) {
                    g1.setFont(Courier12b);
                    for (int i = 1; i <= nyomatek_db; i++) {
                        nyomatek_rajzolo(0, nyomatek[i][1], nyomatek[i][0]);
                        szoveg = String.format("%.2f", nyomatek[i][1]) + " kNcm";
                        //System.out.println("i:"+i+" Nyomatek:"+nyomatek[i][1]+" hely:"+nyomatek[i][0]);
                        g1.setColor(new Color(75, 151, 74));   // Lego bright green
                        g1.drawString(szoveg, (int) (nyomatek[i][0] * arany) + 77, bazis - 35);
                    }
                }
            } else {
                // Konzolos tartó
                // A befogási nyomaték            
                if (ma != 0) {
                    szoveg = String.format("%.2f", ma) + " kNcm";
                    g1.setFont(Courier12b);
                    nyomatek_rajzolo(1, ma, 0f);
                    g1.setColor(new Color(70, 103, 164));  // Lego royal blue
                    g1.drawString(szoveg, 73, 40);
                }
            }
            // Segédvonalak
            g1.setColor(Color.LIGHT_GRAY);
            bazis = 70;
            g1.drawLine(bazis, 55, bazis, 298);
            bazis = 570;
            g1.drawLine(bazis, 55, bazis, 298);
            if ((int) (tarto.get(0).getKonzol1()) > 0) {
                bazis = 70 + (int) (arany * tarto.get(0).getKonzol1());
                g1.drawLine(bazis, 55, bazis, 298);
            }
            if ((int) (tarto.get(0).getKonzol2()) > 0) {
                bazis = 70 + (int) (arany * (tarto.get(0).getKonzol1() + tarto.get(0).getHossz()));
                g1.drawLine(bazis, 55, bazis, 298);
            }
            // méretvonal
            bazis = 72;
            g1.setColor(Color.black);
            g1.drawLine(67, 75, 73, 69);
            g1.drawLine(567, 75, 573, 69);
            if ((int) (tarto.get(0).getKonzol1()) > 0) {
                bazis = 70 + (int) (arany * tarto.get(0).getKonzol1());
                g1.drawLine(bazis - 3, 75, bazis + 3, 69);
            }
            if ((int) (tarto.get(0).getKonzol2()) > 0) {
                bazis = 70 + (int) (arany * (tarto.get(0).getKonzol1() + tarto.get(0).getHossz()));
                g1.drawLine(bazis - 3, 75, bazis + 3, 69);
            }
            // A gerenda
            bazis = 42;
            g1.setColor(Color.black);
            g1.drawLine(70, bazis, 570, bazis);
            g1.drawLine(70, bazis - 1, 570, bazis - 1);
            // A támaszok és támaszerők, nyomatékok ábrái
            if (tarto.get(0).getTipus() == 2) {
                // Konzolos tartó
                g1.drawLine(70, 32, 70, 52);
                g1.drawLine(71, 32, 71, 52);
                g1.setFont(Courier16b);
                g1.drawString("A", 50, 70);
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
                    g1.fillPolygon(nyilpontx, nyilponty, 8);
                }
                g1.setFont(Courier12b);
                szoveg = String.format("%.2f", fa) + " kN";
                g1.drawString(szoveg, bazis + 10, 62);
                g1.setFont(Courier10);
                szoveg = tarto.get(0).getKonzol1() + " cm";
                g1.drawString(szoveg, 250, 70);
            } else {
                // Mozgó talp
                bazis = 70;
                if ((int) (tarto.get(0).getKonzol1()) > 0) {
                    bazis = 70 + (int) (arany * tarto.get(0).getKonzol1());
                    g1.setFont(Courier10);
                    szoveg = tarto.get(0).getKonzol1() + " cm";
                    g1.drawString(szoveg, 50 + (int) (arany * tarto.get(0).getKonzol1() / 2), 70);
                }
                g1.drawLine(bazis, 42, bazis - 5, 47);
                g1.drawLine(bazis, 42, bazis + 5, 47);
                g1.drawLine(bazis - 5, 47, bazis + 5, 47);
                g1.drawLine(bazis - 10, 51, bazis + 10, 51);
                g1.drawLine(bazis - 10, 50, bazis + 10, 50);
                g1.setFont(Courier16b);
                g1.drawString("A", bazis - 25, 60);
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
                g1.fillPolygon(pontokx, pontoky, 8);
                g1.setFont(Courier12b);
                szoveg = String.format("%.2f", fa) + " kN";
                g1.drawString(szoveg, bazis + 10, 62);

                // Fix talp    
                bazis = 570;
                if ((int) (tarto.get(0).getKonzol2()) > 0) {
                    bazis = 70 + (int) (arany * (tarto.get(0).getKonzol1() + tarto.get(0).getHossz()));
                    g1.setFont(Courier10);
                    szoveg = tarto.get(0).getKonzol2() + " cm";
                    g1.drawString(szoveg, 50 + (int) (arany * (tarto.get(0).getHossz() + tarto.get(0).getKonzol1())) + (int) (arany * tarto.get(0).getKonzol2() / 2), 70);
                }
                g1.setFont(Courier16b);
                g1.drawLine(bazis, 42, bazis - 5, 49);
                g1.drawLine(bazis, 42, bazis + 5, 49);
                g1.drawLine(bazis - 5, 49, bazis + 5, 49);
                g1.drawLine(bazis - 10, 49, bazis + 10, 49);
                g1.drawLine(bazis - 10, 48, bazis + 10, 48);
                g1.drawString("B", bazis - 25, 60);
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
                g1.fillPolygon(pontokx, pontoky, 8);
                g1.setFont(Courier12b);
                szoveg = String.format("%.2f", fb) + " kN";
                g1.drawString(szoveg, bazis + 10, 62);
                g1.setFont(Courier10);
                szoveg = tarto.get(0).getHossz() + " cm";
                g1.drawString(szoveg, 50 + (int) (arany * (tarto.get(0).getKonzol1()))
                        + (int) (arany * tarto.get(0).getHossz() / 2), 70);
            }
            // A határvonalak
            g1.setColor(new Color(180, 245, 250));
            g1.drawLine(10, 77, 630, 77);
            g1.drawLine(10, 187, 630, 187);
            g1.drawLine(10, 297, 630, 297);

            // A koncentrált erő nyilainak kijelzése
            g1.setColor(Color.black);
            g1.setFont(Courier12b);
            bazis = 42;
            if (nyil_db > 0) {
                g1.setColor(Color.blue);
                for (int i = 1; i <= nyil_db; i++) {
                    g1.drawLine((int) (ero[i][0] * arany) + 70, bazis - 18, (int) (ero[i][0] * arany) + 70, bazis - 2);
                    for (int k = 1; k <= 10; k++) {
                        if (ero[i][1] > 0) {
                            g1.drawLine((int) (ero[i][0] * arany) + 70, bazis - 2, (int) (ero[i][0] * arany) + 64 + k, bazis - 15);
                        } else {
                            g1.drawLine((int) (ero[i][0] * arany) + 70, bazis - 18, (int) (ero[i][0] * arany) + 64 + k, bazis - 7);
                        }
                    }
                    szoveg = String.format("%.2f", ero[i][1]) + " kN";
                    //System.out.println("i:"+i+" Erő:"+ero[i][1]+" hely:"+ero[i][1]);
                    g1.drawString(szoveg, (int) (ero[i][0] * arany) + 77, bazis - 12);
                }
            }

            // A megoszló terhelések kijelzése
            bazis = 22;
            if (megoszlo_db > 0) {
                g1.setColor(Color.magenta);
                for (int i = 1; i <= megoszlo_db; i++) {
                    //System.out.println("megoszlo(i): " + megoszlo[i][0] + "  " + megoszlo[i][1] + " " + megoszlo[i][2]);
                    g1.drawRect((int) (megoszlo[i][0] * arany) + 70, bazis - 20, (int) (megoszlo[i][1] * arany), bazis - 4);
                    g1.drawLine((int) (megoszlo[i][0] * arany) + 75, bazis - 4, (int) (megoszlo[i][0] * arany) + 75, bazis - 18);
                    for (int k = 1; k <= 8; k++) {
                        if (megoszlo[i][2] > 0) {
                            g1.drawLine((int) (megoszlo[i][0] * arany) + 75, bazis - 4, (int) (megoszlo[i][0] * arany) + 71 + k, bazis - 14);
                        } else {
                            g1.drawLine((int) (megoszlo[i][0] * arany) + 75, bazis - 18, (int) (megoszlo[i][0] * arany) + 71 + k, bazis - 8);
                        }
                    }
                    szoveg = "q=" + String.format("%.4f", megoszlo[i][2]) + " kN/cm";
                    g1.drawString(szoveg, (int) (megoszlo[i][0] * arany) + 80, bazis - 6);
                }
            }
            // A bázisvonalak
            g1.setColor(Color.BLACK);
            g1.setStroke(vastagvonal);
            bazis = 132;
            g1.drawLine(68, bazis, 572, bazis);
            bazis = 242;
            g1.drawLine(68, bazis, 572, bazis);

            // A állandó rajzelemek átmásolása 
            abramasolo();
            /*
            korvonal_szamito(0);
            kepkeszito(Szelvenyelemek szelveny, 1, meretarany, 1);*/
            
            // Az egyéni rajzelemek
            // A határvonal a 2-es rajzon
            g2.setColor(new Color(180, 245, 250));
            g2.drawLine(10, 407, 630, 407);

            g2.setColor(Color.LIGHT_GRAY);
            bazis = 70;
            g2.drawLine(bazis, 298, bazis, 408);
            bazis = 570;
            g2.drawLine(bazis, 298, bazis, 408);
            if ((int) (tarto.get(0).getKonzol1()) > 0) {
                bazis = 70 + (int) (arany * tarto.get(0).getKonzol1());
                g2.drawLine(bazis, 298, bazis, 408);
            }
            if ((int) (tarto.get(0).getKonzol2()) > 0) {
                bazis = 70 + (int) (arany * (tarto.get(0).getKonzol1() + tarto.get(0).getHossz()));
                g2.drawLine(bazis, 298, bazis, 408);
            }
            // A bázisvonalak
            g2.setColor(Color.BLACK);
            g2.setStroke(vastagvonal);
            bazis = 352;
            g2.drawLine(68, bazis, 572, bazis);

            for (int i = 0; i < metszekszam1; i++) {
                pontokx[i] = 70 + i;
            }
            // A szövegek        
            g1.setFont(Courier10);
            g3.setColor(Color.black);
            g1.drawString("+", 60, 130);
            g1.drawString("+", 60, 240);
            g2.drawString("+", 60, 130);
            g2.drawString("+", 60, 240);
            g2.drawString("+", 60, 350);
            g3.drawString("+", 60, 130);
            g3.drawString("+", 60, 240);

            g1.drawString("-", 60, 140);
            g1.drawString("-", 60, 250);
            g2.drawString("-", 60, 140);
            g2.drawString("-", 60, 250);
            g2.drawString("-", 60, 360);
            g3.drawString("-", 60, 140);
            g3.drawString("-", 60, 250);
            g1.setFont(Courier12b);

            g2.setFont(Courier12b);
            g2.drawString("16", 575, 98);
            g2.drawString("20", 575, 89);
            g2.drawString("16", 575, 170);
            g2.drawString("20", 575, 179);

            g2.drawString("16", 575, 208);
            g2.drawString("20", 575, 199);
            g2.drawString("16", 575, 280);
            g2.drawString("20", 575, 289);

            g2.drawString("16", 575, 318);
            g2.drawString("20", 575, 309);
            g2.drawString("16", 575, 390);
            g2.drawString("20", 575, 399);

            //g2.drawString("Tau", 15, 244); 
            //g2.drawString("Szigma", 15, 134);
            //g2.drawString("Szigma_Ö", 5, 354);
            //g3.drawString("Elmozdulás", 5, 134);
            //g3.drawString("Szögford.", 5, 244);
            //g3.drawString("fok", 575, 224);
            g1.setFont(Courier32b);
            g1.drawString("T", 35, 140);
            g1.drawString("M", 32, 250);
            g1.setFont(Courier18b);
            g2.setFont(Courier18b);
            g1.drawString("kN", 575, 136);
            g1.drawString("kNcm", 575, 246);
            g2.drawString("kN/cm2", 575, 136);
            g2.drawString("kN/cm2", 575, 248);
            g2.drawString("kN/cm2", 575, 356);
            g2.setFont(Courier32b);
            g3.setFont(Courier32b);

            g2.drawString("\u03C3", 30, 138);  // Szigma
            g2.drawString("\u03C3", 30, 358);   // Szigma ö            
            g2.drawString("\u03C4", 36, 248);   // tau
            g3.drawString("\u03C6", 28, 138);   // Elmozdulás fi
            g3.drawString("\u03C9", 30, 248);   // Szögford. omega
            g3.setFont(Courier18b);
            g3.drawString("fok(\u00B0)", 575, 248);   // fok
            g3.drawString("cm", 575, 134);
            g2.setFont(Courier24b);
            g2.drawString("ö", 45, 367);
            // A veszélyes határérték-sáv
            g2.setColor(new Color(255, 220, 220));
            for (int i = 1; i <= 9; i++) {
                g2.drawLine(70, 86 + i, 570, 86 + i);
                g2.drawLine(70, 167 + i, 570, 167 + i);
                g2.drawLine(70, 196 + i, 570, 196 + i);
                g2.drawLine(70, 277 + i, 570, 277 + i);
                g2.drawLine(70, 306 + i, 570, 306 + i);
                g2.drawLine(70, 387 + i, 570, 387 + i);
            }

            // A grafikonok - a régi verzió
            /*g.fillPolygon(pontokx, pontoky, metszekszam);
             g.setColor(Color.BLACK);
             g.drawPolygon(pontokx, pontoky, metszekszam);*/
            // A bázisvonalak és a grafikonok
            g1.setColor(Color.LIGHT_GRAY);
            g2.setColor(Color.LIGHT_GRAY);
            g3.setColor(Color.LIGHT_GRAY);
            bazis = 72;
            g1.drawLine(68, bazis, 572, bazis);
            g2.drawLine(68, bazis, 572, bazis);
            g3.drawLine(68, bazis, 572, bazis);
            // A nyíróerő ábra
            bazis = 132;
            konvertalo(0);
            g1.setStroke(vekonyvonal);
            g1.setColor(new Color(161, 196, 139));   // Lego Medium green
            for (int i = 0; i <= metszekszam; i++) {
                g1.drawLine(pontokx[i], bazis, pontokx[i], pontoky[i]);
            }
            g1.setColor(new Color(105, 64, 39));   // Lego Reddish brown
            for (int i = 0; i < metszekszam; i++) {
                if (metszekek.get(i).getKijelzes() == 1) {
                    g1.drawLine(pontokx[i], bazis, pontokx[i], pontoky[i]);
                }
            }
            g1.setColor(Color.BLACK);
            for (int i = 0; i <= metszekszam; i++) {
                g1.drawLine(pontokx[i], pontoky[i], pontokx[i], pontoky[i]);
            }
            // A maximum érték és hely         
            g1.setFont(Courier12b);
            g2.setFont(Courier12b);
            g3.setFont(Courier12b);
            g1.setColor(Color.red);
            g1.setStroke(vastagvonal);
            g1.drawLine(pontokx[maxertekhely[0]], bazis, pontokx[maxertekhely[0]], pontoky[maxertekhely[0]]);
            g1.setStroke(vekonyvonal);
            g1.drawLine(pontokx[maxertekhely[0]] + 10, bazis - 47, pontokx[maxertekhely[0]] + 100, bazis - 47);
            g1.drawLine(pontokx[maxertekhely[0]] + 10, bazis - 47, pontokx[maxertekhely[0]],
                    (bazis + pontoky[maxertekhely[0]]) / 2);
            szoveg = "T=" + String.format("%.2f", metszekek.get(maxertekhely[0]).getNyiroero()) + " kN";
            g1.drawString(szoveg, pontokx[maxertekhely[0]] + 10, bazis - 49);
            // A nyomatéki ábra
            konvertalo(1);
            g1.setStroke(vekonyvonal);
            g1.setColor(new Color(121, 181, 181));   // Lego Turquoise
            bazis = 242;
            for (int i = 0; i < metszekszam; i++) {
                g1.drawLine(pontokx[i], bazis, pontokx[i], pontoky[i]);
            }
            g1.setColor(new Color(105, 64, 39));   // Lego Reddish brown
            for (int i = 0; i < metszekszam; i++) {
                if (metszekek.get(i).getKijelzes() == 1) {
                    g1.drawLine(pontokx[i], bazis, pontokx[i], pontoky[i]);
                }
            }
            g1.setColor(Color.BLACK);
            for (int i = 0; i < metszekszam; i++) {
                g1.drawLine(pontokx[i], pontoky[i], pontokx[i], pontoky[i]);
            }
            // A maximum érték és hely            
            g1.setColor(Color.red);
            g1.setStroke(vastagvonal);
            g1.drawLine(pontokx[maxertekhely[1]], bazis, pontokx[maxertekhely[1]], pontoky[maxertekhely[1]]);
            g1.setStroke(vekonyvonal);
            g1.drawLine(pontokx[maxertekhely[1]] + 10, bazis - 47, pontokx[maxertekhely[1]] + 100, bazis - 47);
            g1.drawLine(pontokx[maxertekhely[1]] + 10, bazis - 47, pontokx[maxertekhely[1]],
                    (bazis + pontoky[maxertekhely[1]]) / 2);
            szoveg = "M=" + String.format("%.2f", metszekek.get(maxertekhely[1]).getNyomatek()) + " kNcm";
            g1.drawString(szoveg, pontokx[maxertekhely[1]] + 10, bazis - 49);
            // A normálfeszültség
            konvertalo(7);
            g2.setStroke(vekonyvonal);
            g2.setColor(new Color(249, 214, 46));   // Lego Fire Yellow
            bazis = 132;
            for (int i = 0; i < metszekszam; i++) {
                g2.drawLine(pontokx[i], bazis, pontokx[i], pontoky[i]);
                //System.out.println("x:"+pontokx[i]+"   y:"+pontoky[i]);
            }
            g2.setColor(new Color(105, 64, 39));   // Lego Reddish brown
            for (int i = 0; i < metszekszam; i++) {
                if (metszekek.get(i).getKijelzes() == 1) {
                    g2.drawLine(pontokx[i], bazis, pontokx[i], pontoky[i]);
                }
            }
            g2.setColor(Color.BLACK);
            for (int i = 0; i < metszekszam; i++) {
                g2.drawLine(pontokx[i], pontoky[i], pontokx[i + 1], pontoky[i + 1]);
            }
            // A maximum érték és hely 
            g2.setColor(Color.red);
            g2.setStroke(vastagvonal);
            g2.drawLine(pontokx[maxertekhely[2]], bazis, pontokx[maxertekhely[2]], pontoky[maxertekhely[2]]);
            g2.setStroke(vekonyvonal);
            g2.drawLine(pontokx[maxertekhely[2]] + 10, bazis - 47, pontokx[maxertekhely[2]] + 130, bazis - 47);
            g2.drawLine(pontokx[maxertekhely[2]] + 10, bazis - 47, pontokx[maxertekhely[2]],
                    (bazis + pontoky[maxertekhely[2]]) / 2);
            szoveg = "Szigma=" + String.format("%.2f", metszekek.get(maxertekhely[2]).getSzigma()) + " kN/cm2";
            g2.drawString(szoveg, pontokx[maxertekhely[2]] + 10, bazis - 49);
            // A csúsztató feszültség
            konvertalo(8);
            g2.setStroke(vekonyvonal);
            bazis = 242;
            g2.setColor(new Color(183, 215, 213));   // Lego Light bluish green
            for (int i = 0; i < metszekszam; i++) {
                g2.drawLine(pontokx[i], bazis, pontokx[i], pontoky[i]);
                //System.out.println("x:" + pontokx[i] + "   y:" + pontoky[i]);
            }
            g2.setColor(new Color(105, 64, 39));   // Lego Reddish brown
            for (int i = 0; i < metszekszam; i++) {
                if (metszekek.get(i).getKijelzes() == 1) {
                    g2.drawLine(pontokx[i], bazis, pontokx[i], pontoky[i]);
                }
            }
            g2.setColor(Color.BLACK);
            for (int i = 0; i < metszekszam; i++) {
                g2.drawLine(pontokx[i], pontoky[i], pontokx[i + 1], pontoky[i + 1]);
            }
            // A maximum érték és hely 
            g2.setColor(Color.red);
            g2.setStroke(vastagvonal);
            g2.drawLine(pontokx[maxertekhely[3]], bazis, pontokx[maxertekhely[3]], pontoky[maxertekhely[3]]);
            g2.setStroke(vekonyvonal);
            g2.drawLine(pontokx[maxertekhely[3]] + 10, bazis - 47, pontokx[maxertekhely[3]] + 130, bazis - 47);
            g2.drawLine(pontokx[maxertekhely[3]] + 10, bazis - 47, pontokx[maxertekhely[3]],
                    (bazis + pontoky[maxertekhely[3]]) / 2);
            szoveg = "Tau=" + String.format("%.2f", metszekek.get(maxertekhely[3]).getTau()) + " kN/cm2";
            g2.drawString(szoveg, pontokx[maxertekhely[3]] + 10, bazis - 49);
            // Az összetett feszültség
            bazis = 352;
            konvertalo(9);
            g2.setStroke(vekonyvonal);
            g2.setColor(new Color(224, 152, 100));   // Lego Medium orange
            for (int i = 0; i < metszekszam; i++) {
                g2.drawLine(pontokx[i], bazis, pontokx[i], pontoky[i]);
            }
            g2.setColor(new Color(105, 64, 39));   // Lego Reddish brown
            for (int i = 0; i < metszekszam; i++) {
                if (metszekek.get(i).getKijelzes() == 1) {
                    g2.drawLine(pontokx[i], bazis, pontokx[i], pontoky[i]);
                }
            }
            g2.setColor(Color.BLACK);
            for (int i = 0; i < metszekszam; i++) {
                g2.drawLine(pontokx[i], pontoky[i], pontokx[i + 1], pontoky[i + 1]);
            }
            // A maximum érték és hely 
            g2.setColor(Color.red);
            g2.setStroke(vastagvonal);
            g2.drawLine(pontokx[maxertekhely[4]], bazis, pontokx[maxertekhely[4]], pontoky[maxertekhely[4]]);
            g2.setStroke(vekonyvonal);
            g2.drawLine(pontokx[maxertekhely[4]] + 10, bazis - 47, pontokx[maxertekhely[4]] + 130, bazis - 47);
            g2.drawLine(pontokx[maxertekhely[4]] + 10, bazis - 47, pontokx[maxertekhely[4]],
                    (bazis + pontoky[maxertekhely[4]]) / 2);
            szoveg = "SzigmaÖ=" + String.format("%.2f", metszekek.get(maxertekhely[4]).getOsszehasonlito_szigma()) + " kN/cm2";
            g2.drawString(szoveg, pontokx[maxertekhely[4]] + 10, bazis - 49);
            // Az elmozdulás értéke
            konvertalo(10);
            g3.setStroke(vekonyvonal);
            g3.setColor(new Color(120, 144, 129));   // Lego Sand green
            bazis = 132;
            for (int i = 0; i <= metszekszam; i++) {
                g3.drawLine(pontokx[i], bazis, pontokx[i], pontoky[i]);
            }
            g3.setColor(new Color(105, 64, 39));   // Lego Reddish brown
            for (int i = 0; i < metszekszam; i++) {
                if (metszekek.get(i).getKijelzes() == 1) {
                    g3.drawLine(pontokx[i], bazis, pontokx[i], pontoky[i]);
                }
            }
            /*g3.setColor(Color.BLACK);
             for (int i = 0; i < metszekszam; i++) {
             g3.drawLine(pontokx[i], pontoky[i], pontokx[i + 1], pontoky[i + 1]);
             }*/
            // A maximum érték és hely 
            g3.setColor(Color.red);
            g3.setStroke(vastagvonal);
            g3.drawLine(pontokx[maxertekhely[5]], bazis, pontokx[maxertekhely[5]], pontoky[maxertekhely[5]]);
            g3.setStroke(vekonyvonal);
            g3.drawLine(pontokx[maxertekhely[5]] + 10, bazis - 47, pontokx[maxertekhely[5]] + 100, bazis - 47);
            g3.drawLine(pontokx[maxertekhely[5]] + 10, bazis - 47, pontokx[maxertekhely[5]],
                    (bazis + pontoky[maxertekhely[5]]) / 2);
            szoveg = "\u03C6max=" + String.format("%.2f", metszekek.get(maxertekhely[5]).getLehajlas()) + " cm";
            g3.drawString(szoveg, pontokx[maxertekhely[5]] + 10, bazis - 49);
            // Az elfordulás értéke
            konvertalo(11);
            g3.setStroke(vekonyvonal);
            bazis = 242;
            g3.setColor(new Color(149, 121, 118));   // Lego Sand red
            for (int i = 0; i < metszekszam; i++) {
                g3.drawLine(pontokx[i], bazis, pontokx[i], pontoky[i]);
            }
            g3.setColor(new Color(105, 64, 39));   // Lego Reddish brown
            for (int i = 0; i < metszekszam; i++) {
                if (metszekek.get(i).getKijelzes() == 1) {
                    g3.drawLine(pontokx[i], bazis, pontokx[i], pontoky[i]);
                }
            }
            /*g3.setColor(Color.BLACK);
             for (int i = 0; i < metszekszam; i++) {
             g3.drawLine(pontokx[i], pontoky[i], pontokx[i + 1], pontoky[i + 1]);
             } */
            // A maximum érték és hely 
            g3.setColor(Color.red);
            g3.setStroke(vastagvonal);
            g3.drawLine(pontokx[maxertekhely[6]], bazis, pontokx[maxertekhely[6]], pontoky[maxertekhely[6]]);
            g3.setStroke(vekonyvonal);
            g3.drawLine(pontokx[maxertekhely[6]] + 10, bazis - 47, pontokx[maxertekhely[6]] + 100, bazis - 47);
            g3.drawLine(pontokx[maxertekhely[6]] + 10, bazis - 47, pontokx[maxertekhely[6]],
                    (bazis + pontoky[maxertekhely[6]]) / 2);
            szoveg = "\u03C9max=" + String.format("%.2f", metszekek.get(maxertekhely[6]).getSzogfordulas()) + " fok";
            g3.drawString(szoveg, pontokx[maxertekhely[6]] + 10, bazis - 49);
            // A gerenda profiljának a metszeti rajza
            // A sraffozás
        BufferedImage sraffozas
                = new BufferedImage(20, 20, BufferedImage.TYPE_INT_ARGB);
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

            
            ImageIO.write(bi1, "PNG", new File(filenev1));
            ImageIO.write(bi2, "PNG", new File(filenev2));
            ImageIO.write(bi3, "PNG", new File(filenev3));

        } catch (IOException ie) {
            ie.printStackTrace();
        }
    }
}
