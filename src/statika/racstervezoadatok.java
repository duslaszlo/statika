
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package statika;

import Entities.Csomopont;
import Entities.Mintacsp;
import Entities.Mintarud;
import Entities.Racsadatok;
import Entities.Racsalap;
import Entities.Racsalap1;
import Entities.Racstervezocsomopont;
import Entities.Racstervezorud;
import Entities.Rud;
import Entities.Szelveny;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import static statika.racstervezo.rs;

/**
 *
 * @author duslaszlo
 */
public class racstervezoadatok {

    String projektnev, nev, filenev;                      // A drótváz projektjének neve, saját neve és a file-neve    
    String parancs;                                       // A MySQL parancsok gyűjtőhelye 
    int maxszekcioelem = 100;                             // A szekciók maximális száma
    int maxcsomopont = 300;                               // A mintarács csomópontjainak maximális száma
    //int kozszam;                                        // A drótvázon belüli közök száma
    int szekcioszam, aktualis_szekcio;                    // A drótvázon belüli szekciók száma, az aktuális szekció

    List<Racsalap> racsalap = new ArrayList<>();          // szekcio(0),magassag(1),alsoxy(2),alsoyz(3),felsoxy(4),felsoyz(5),diffx(6),diffy(7),diffz(8),eltolasxy(9),eltolasyz(10),konzol(11),fugg/vízsz(12) (-->> racsalap tábla)
    List<Racsalap1> racsalap1 = new ArrayList<>();        // szekcio(0),magassag(1),alsoxy(2),alsoyz(3),felsoxy(4),felsoyz(5),diffx(6),diffy(7),diffz(8),eltolasxy(9),eltolasyz(10),koz(11),a rácselemek kódjai (12-19),(20-27) a közök hossza mm-ben (-->> racsalap1 tábla)
    List<Mintarud> mintarud = new ArrayList<>();
    List<Mintacsp> mintacsp = new ArrayList<>();
    List<Szelveny> szelveny = new ArrayList<>();
    List<Racstervezorud> rud = new ArrayList<>();
    List<Racstervezocsomopont> csomopont = new ArrayList<>();
    Racsalap1 ujracsalap1 = new Racsalap1();

    int[] racselemek = new int[9];                        // A rácselemtervezésnél a kiválasztott szekció első közének elemei
    int mintaindexf, mintaindexv;
    float[][] mintacspf = new float[maxcsomopont][3];     // A függőleges mintacsomopont alapkoordinátái: x(0),y(1),z(2)
    int[][] mintacspfjelleg = new int[maxcsomopont][6];   // A függőleges mintacsomopont jellege: xy(0),yz(1),kezdcspxy(2),vegecspxy(3),kezdcspyz(4),vegecspyz(5)
    float[][] mintacspv = new float[15][3];               // A vízszintes mintacsomopont alapkoordinátái: ,x(0),y(1),z(2)
    float[][] tempcsp = new float[maxcsomopont][3];       // Az átmeneti drótváz-elem koordinátái x(0),y(1),z(2)
    //String[][] rudnevek = new String[maxszekcioelem][9];       // A szekciokijelzesnel az aktuális rudszelvények nevei 
    int[][] szelvenyrudhossz = new int[maxszekcioelem][9];       // Az aktuális rudszelvények hossza mm-ben 
    float[][] rudsuly = new float[maxszekcioelem][9];            // Az aktuális rudszelvények folyómétersúlya kg-ban
    //float[][][] limitek = new float[2][3][2];           // A drótváz maximum és minimum értékei  [szekció(1)/teljes(0)], [x(0),y(1),z(2)], [minimum(0)/maximum(1)] (minx, miny, minz, maxx, maxy, maxz)
    int[][] kepkozep = new int[2][2];                     // A kijelzett kép X-középpontja [(0-szekció,1-teljes)]  [X-0,Y-1]
    boolean rajztipus = true;
    Racsadatok Szekcioadatok = new Racsadatok();
    Racsadatok Teljesadatok = new Racsadatok();

    int mx0 = 0, my0 = 0, mx1 = 0, my1 = 0;                // Az egér pozíciójának átmeneti tárolója a forgatásnál (0-szekció,1-teljes) 
    int tx0 = 0, ty0 = 0, tx1 = 0, ty1 = 0;                // Az egér pozíciójának átmeneti tárolója az eltolásnál (0-szekció,1-teljes) 

    // A szekcióelem rajza
    int width = 400, height = 350;                        // A kijelzett kép mérete 
    BufferedImage bi1 = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
    Graphics2D g1 = bi1.createGraphics();
    // A teljes drótváz rajza    
    BufferedImage bi2 = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
    Graphics2D g2 = bi2.createGraphics();
    // A képtörlés    
    BufferedImage bi3 = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
    Graphics2D g3 = bi3.createGraphics();
    // A rajzoláshoz szükséges változók
    double szog;                                          // A forgatásnál az elfordítás szöge - átmeneti tároló
    float x, y, z;                                        // Átmeneti tárolók a forgatásnál/rajzolásnál
    int[][] forgatas = new int[2][4];                     // A forgatás mértéke X,Y,Z irányú forgatás szekció(1), teljes(0)
    float[] kozepx = new float[maxszekcioelem];           // A szekció(1..maxszekcioelem)/teljes(1) drótváz közepe - forgási középpont
    float[] kozepy = new float[maxszekcioelem];           // A szekció(1..maxszekcioelem)/teljes(1) drótváz közepe - forgási középpont
    float[] kozepz = new float[maxszekcioelem];           // A szekció(1..maxszekcioelem)/teljes(1) drótváz közepe - forgási középpont
    float[] kepnagyitas = new float[2];                   // A kép kijelzésénél a képnagyítás mértéke 0-teljes/1-szekcio
    int adat = 0;
    Date now = new Date();
    int[][] maximumok = new int[4][8];                    // A rácstipus kiválasztásnál a maximum értékek
    float alsoxy, alsoyz, felsoxy, felsoyz;               // A szekciók alsó és felső méretei

    public void adatbeolvaso() {

        projektnev = "Tervezett vázszerkezet";
        racsalap.clear();
        racsalap1.clear();
        String parancs;
        for (int i = 1; i < maxszekcioelem; i++) {
            for (int j = 1; j <= 8; j++) {
                szelvenyrudhossz[i][j] = 0;
                rudsuly[i][j] = 0;
            }
        }
        // A rács adatainak a beolvasása
        try {
            Class.forName("com.mysql.jdbc.Driver").newInstance();
            Connection co = DriverManager.getConnection(Global.mysql_server, Global.mysql_user, Global.mysql_password);
            Statement st = co.createStatement();
            // A csomopont koordinátái
            parancs = "select * from Racsalap where nev ='" + nev + "' order by szekcio";
            //System.out.println(parancs);
            ResultSet rs = st.executeQuery(parancs);
            while (rs.next()) {
                Racsalap c = new Racsalap();
                c.setId(rs.getInt("id"));
                c.setNev(rs.getString("nev"));
                c.setSzekcio(rs.getInt("szekcio"));
                c.setMagassag(rs.getInt("magassag"));
                c.setAlsoszelxy(rs.getInt("alsoszelxy"));
                c.setFelsoszelxy(rs.getInt("felsoszelxy"));
                c.setAlsoszelyz(rs.getInt("alsoszelyz"));
                c.setFelsoszelyz(rs.getInt("felsoszelyz"));
                c.setX(rs.getInt("x"));
                c.setY(rs.getInt("y"));
                c.setZ(rs.getInt("z"));
                c.setIrany(rs.getInt("irany"));
                c.setTeljes(rs.getInt("teljes"));
                c.setEltolasxy(rs.getInt("eltolasxy"));
                c.setEltolasyz(rs.getInt("eltolasyz"));
                c.setNev1(rs.getString("nev1"));
                c.setNev2(rs.getString("nev2"));
                c.setNev3(rs.getString("nev3"));
                c.setNev4(rs.getString("nev4"));
                c.setNev5(rs.getString("nev5"));
                c.setNev6(rs.getString("nev6"));
                c.setNev7(rs.getString("nev7"));
                c.setNev8(rs.getString("nev8"));
                racsalap.add(c);
            }
            // A rács köz adatainak a beolvasása
            parancs = "select * from racsalap1 where nev ='" + nev + "' order by szekcio,koz";
            //System.out.println(parancs);
            rs = st.executeQuery(parancs);
            while (rs.next()) {
                Racsalap1 c = new Racsalap1();
                c.setId(rs.getInt("id"));
                c.setNev(rs.getString("nev"));
                c.setSzekcio(rs.getInt("szekcio"));
                c.setKoz(rs.getInt("koz"));
                c.setRacs1(rs.getInt("racs1"));
                c.setRacs2(rs.getInt("racs2"));
                c.setRacs3(rs.getInt("racs3"));
                c.setRacs4(rs.getInt("racs4"));
                c.setRacs5(rs.getInt("racs5"));
                c.setRacs6(rs.getInt("racs6"));
                c.setRacs7(rs.getInt("racs7"));
                c.setRacs8(rs.getInt("racs8"));
                racsalap1.add(c);
            }
            rs.close();
            st.close();
        } catch (InstantiationException e) {
        } catch (IllegalAccessException e) {
        } catch (ClassNotFoundException e) {
        } catch (SQLException e) {
        }
        for (int i = 0; i < racsalap.size(); i++) {
            int k = 0;
            for (int j = 0; j < racsalap1.size(); j++) {
                if (racsalap.get(i).getSzekcio() == racsalap1.get(j).getSzekcio()) {
                    k++;
                }
            }
            for (int j = 0; j < racsalap1.size(); j++) {
                if (racsalap.get(i).getSzekcio() == racsalap1.get(j).getSzekcio()) {
                    racselem1_alapadatok(racsalap1.get(j).getKoz(), k, racsalap.get(i).getIrany(), racsalap.get(i).getMagassag(), racsalap.get(i).getAlsoszelxy(),
                            racsalap.get(i).getFelsoszelxy(), racsalap.get(i).getAlsoszelyz(), racsalap.get(i).getFelsoszelyz(),
                            racsalap.get(i).getEltolasxy(), racsalap.get(i).getEltolasyz(), racsalap.get(i).getX(),
                            racsalap.get(i).getY(), racsalap.get(i).getZ());
                    racsalap1.get(j).setMagassag(ujracsalap1.getMagassag());
                    racsalap1.get(j).setX(ujracsalap1.getX());
                    racsalap1.get(j).setY(ujracsalap1.getY());
                    racsalap1.get(j).setZ(ujracsalap1.getZ());
                    racsalap1.get(j).setAlsoszelxy(ujracsalap1.getAlsoszelxy());
                    racsalap1.get(j).setAlsoszelyz(ujracsalap1.getAlsoszelyz());
                    racsalap1.get(j).setFelsoszelxy(ujracsalap1.getFelsoszelxy());
                    racsalap1.get(j).setFelsoszelyz(ujracsalap1.getFelsoszelyz());
                    racsalap1.get(j).setEltolasxy(ujracsalap1.getEltolasxy());
                    racsalap1.get(j).setEltolasyz(ujracsalap1.getEltolasyz());
                }
            }
        }
        filenev = "./images/drotvaz/" + nev + ".png";
        forgatas[0][1] = 0;
        forgatas[0][2] = 0;
        forgatas[0][3] = 0;
        forgatas[1][1] = 0;
        forgatas[1][2] = 0;
        forgatas[1][3] = 0;
        kepnagyitas[0] = 1;
        kepnagyitas[1] = 1;
    }

    public void racsalap_adatkijelzo() {

        for (int i = 0; i < racsalap.size(); i++) {
            System.out.println("Név;szekcio;;magassag;alsoszelxy;felsoszelxy;alsoszelyz;felsoszelyz;x;y;z;eltolasxy;eltolasyz;suly1;suly2;suly3;suly4;suly5;suly6;suly7;suly8");
            System.out.println(racsalap.get(i).getNev() + ";" + racsalap.get(i).getSzekcio() + ";;" + racsalap.get(i).getMagassag()
                    + ";" + racsalap.get(i).getAlsoszelxy()
                    + ";" + racsalap.get(i).getFelsoszelxy()
                    + ";" + racsalap.get(i).getAlsoszelyz()
                    + ";" + racsalap.get(i).getFelsoszelyz()
                    + ";" + racsalap.get(i).getX()
                    + ";" + racsalap.get(i).getY()
                    + ";" + racsalap.get(i).getZ()
                    + ";" + racsalap.get(i).getEltolasxy()
                    + ";" + racsalap.get(i).getEltolasyz()
                    + ";" + rudsuly[i][1] + ";" + rudsuly[i][2] + ";" + rudsuly[i][3] + ";" + rudsuly[i][4]
                    + ";" + rudsuly[i][5] + ";" + rudsuly[i][6] + ";" + rudsuly[i][7] + ";" + rudsuly[i][8]);
            System.out.println("Név;szekcio;koz;magassag;alsoszelxy;felsoszelxy;alsoszelyz;felsoszelyz;x;y;z;eltolasxy;eltolasyz;h1;h2;h3;h4;h5;h6;h7;h8");
            for (int j = 0; j < racsalap1.size(); j++) {
                if (racsalap.get(i).getSzekcio() == racsalap1.get(j).getSzekcio()) {
                    System.out.println(racsalap1.get(j).getNev()
                            + ";" + racsalap1.get(j).getSzekcio()
                            + ";" + racsalap1.get(j).getKoz()
                            + ";" + racsalap1.get(j).getMagassag()
                            + ";" + racsalap1.get(j).getAlsoszelxy()
                            + ";" + racsalap1.get(j).getFelsoszelxy()
                            + ";" + racsalap1.get(j).getAlsoszelyz()
                            + ";" + racsalap1.get(j).getFelsoszelyz()
                            + ";" + racsalap1.get(j).getX()
                            + ";" + racsalap1.get(j).getY()
                            + ";" + racsalap1.get(j).getZ()
                            + ";" + racsalap1.get(j).getEltolasxy()
                            + ";" + racsalap1.get(j).getEltolasyz()
                            + ";" + racsalap1.get(j).getHossz1()
                            + ";" + racsalap1.get(j).getHossz2()
                            + ";" + racsalap1.get(j).getHossz3()
                            + ";" + racsalap1.get(j).getHossz4()
                            + ";" + racsalap1.get(j).getHossz5()
                            + ";" + racsalap1.get(j).getHossz6()
                            + ";" + racsalap1.get(j).getHossz7()
                            + ";" + racsalap1.get(j).getHossz8());
                }
            }
        }
    }

    public void racsalap_rudsuly() {
        //System.out.println("racsalap_rudsuly");
        for (int i = 0; i < rud.size(); i++) {
            //System.out.println("rud:" + i + " kezdcsp:" + rud.get(i).getRud().getKezdocsp() + "  vegecsp:" + rud.get(i).getRud().getVegecsp());
            rud.get(i).setHossz(rudhossz(rud.get(i).getRud().getKezdocsp(), rud.get(i).getRud().getVegecsp()));
            for (int j = 0; j < racsalap1.size(); j++) {
                if ((racsalap1.get(j).getSzekcio() == rud.get(i).getSzekcio())
                        && (racsalap1.get(j).getKoz() == rud.get(i).getKoz())) {
                    switch (rud.get(i).getTipus()) {
                        case 2:
                            racsalap1.get(j).setHossz2(racsalap1.get(j).getHossz2() + (int) rud.get(i).getHossz());
                            break;
                        case 3:
                            racsalap1.get(j).setHossz3(racsalap1.get(j).getHossz3() + (int) rud.get(i).getHossz());
                            break;
                        case 4:
                            racsalap1.get(j).setHossz4(racsalap1.get(j).getHossz4() + (int) rud.get(i).getHossz());
                            break;
                        case 5:
                            racsalap1.get(j).setHossz5(racsalap1.get(j).getHossz5() + (int) rud.get(i).getHossz());
                            break;
                        case 6:
                            racsalap1.get(j).setHossz6(racsalap1.get(j).getHossz6() + (int) rud.get(i).getHossz());
                            break;
                        case 7:
                            racsalap1.get(j).setHossz7(racsalap1.get(j).getHossz7() + (int) rud.get(i).getHossz());
                            break;
                        case 8:
                            racsalap1.get(j).setHossz8(racsalap1.get(j).getHossz8() + (int) rud.get(i).getHossz());
                            break;
                        default:
                            racsalap1.get(j).setHossz1(racsalap1.get(j).getHossz1() + (int) rud.get(i).getHossz());
                    }
                    //adatok1[j][rud[i][6] + 19] += rud.get(i).getHossz();
                }
            }
            //System.out.println("Kezd:"+rud[i][1]+" vég:"+rud[i][2]+"  hossz:"+rud[i][7]+" Szekcio:"+rud[i][0]+" tipus:"+rud[i][6]+" koz:"+rud[i][5]);
        }
        // Az alkotó rácsrudak összhosszainak és súlyainak meghatározása   
        for (int i = 0; i < racsalap1.size(); i++) {
            for (int j = 1; j <= 8; j++) {
                szelvenyrudhossz[i][j] = 0;
                rudsuly[i][j] = 0;
            }
        }
        for (int i = 0; i < racsalap1.size(); i++) {
            szelvenyrudhossz[racsalap1.get(i).getSzekcio()][1] += racsalap1.get(i).getHossz1();
            szelvenyrudhossz[racsalap1.get(i).getSzekcio()][2] += racsalap1.get(i).getHossz2();
            szelvenyrudhossz[racsalap1.get(i).getSzekcio()][3] += racsalap1.get(i).getHossz3();
            szelvenyrudhossz[racsalap1.get(i).getSzekcio()][4] += racsalap1.get(i).getHossz4();
            szelvenyrudhossz[racsalap1.get(i).getSzekcio()][5] += racsalap1.get(i).getHossz5();
            szelvenyrudhossz[racsalap1.get(i).getSzekcio()][6] += racsalap1.get(i).getHossz6();
            szelvenyrudhossz[racsalap1.get(i).getSzekcio()][7] += racsalap1.get(i).getHossz7();
            szelvenyrudhossz[racsalap1.get(i).getSzekcio()][8] += racsalap1.get(i).getHossz8();
            /*System.out.println("i:"+i+" h1: " + racsalap1.get(i).getHossz1() + "  hossz:" + szelvenyrudhossz[i][1]);            
            System.out.println("i:"+i+" h2: " + racsalap1.get(i).getHossz2() + "  hossz:" + szelvenyrudhossz[i][2]);
            System.out.println("i:"+i+" h3: " + racsalap1.get(i).getHossz3() + "  hossz:" + szelvenyrudhossz[i][3]);
            System.out.println("i:"+i+" h4: " + racsalap1.get(i).getHossz4() + "  hossz:" + szelvenyrudhossz[i][4]);
            System.out.println("i:"+i+" h5: " + racsalap1.get(i).getHossz5() + "  hossz:" + szelvenyrudhossz[i][5]);
            System.out.println("i:"+i+" h6: " + racsalap1.get(i).getHossz6() + "  hossz:" + szelvenyrudhossz[i][6]);
            System.out.println("i:"+i+" h7: " + racsalap1.get(i).getHossz7() + "  hossz:" + szelvenyrudhossz[i][7]);
            System.out.println("i:"+i+" h8: " + racsalap1.get(i).getHossz8() + "  hossz:" + szelvenyrudhossz[i][8]);*/
        }

        // A szelvény rúdsúlyainak meghatározása
        for (int j = 0; j < racsalap.size(); j++) {
            if (racsalap.get(j).getNev1() != null) {
                for (int k = 0; k < szelveny.size(); k++) {
                    if (szelveny.get(k).getNev().equals(racsalap.get(j).getNev1())) {
                        rudsuly[j][1] = szelveny.get(k).getFmsuly();
                        //System.out.println("j:"+j+" k:"+k+"  suly:"+rudsuly[j][1]+" Név:"+racsalap.get(j).getNev1());
                    }
                }
            }
            if (racsalap.get(j).getNev2() != null) {
                for (int k = 0; k < szelveny.size(); k++) {
                    if (szelveny.get(k).getNev().equals(racsalap.get(j).getNev2())) {
                        rudsuly[j][2] = szelveny.get(k).getFmsuly();
                        //System.out.println("i:"+j+" k:"+k+"  suly:"+rudsuly[j][1]);
                    }
                }
            }
            if (racsalap.get(j).getNev3() != null) {
                for (int k = 0; k < szelveny.size(); k++) {
                    if (szelveny.get(k).getNev().equals(racsalap.get(j).getNev3())) {
                        rudsuly[j][3] = szelveny.get(k).getFmsuly();
                        //System.out.println("i:"+j+" k:"+k+"  suly:"+rudsuly[j][1]);
                    }
                }
            }
            if (racsalap.get(j).getNev4() != null) {
                for (int k = 0; k < szelveny.size(); k++) {
                    if (szelveny.get(k).getNev().equals(racsalap.get(j).getNev4())) {
                        rudsuly[j][4] = szelveny.get(k).getFmsuly();
                        //System.out.println("i:"+j+" k:"+k+"  suly:"+rudsuly[j][1]);
                    }
                }
            }
            if (racsalap.get(j).getNev5() != null) {
                for (int k = 0; k < szelveny.size(); k++) {
                    if (szelveny.get(k).getNev().equals(racsalap.get(j).getNev5())) {
                        rudsuly[j][5] = szelveny.get(k).getFmsuly();
                        //System.out.println("i:"+j+" k:"+k+"  suly:"+rudsuly[j][1]);
                    }
                }
            }
            if (racsalap.get(j).getNev6() != null) {
                for (int k = 0; k < szelveny.size(); k++) {
                    if (szelveny.get(k).getNev().equals(racsalap.get(j).getNev6())) {
                        rudsuly[j][6] = szelveny.get(k).getFmsuly();
                        //System.out.println("i:"+j+" k:"+k+"  suly:"+rudsuly[j][1]);
                    }
                }
            }
            if (racsalap.get(j).getNev7() != null) {
                for (int k = 0; k < szelveny.size(); k++) {
                    if (szelveny.get(k).getNev().equals(racsalap.get(j).getNev7())) {
                        rudsuly[j][7] = szelveny.get(k).getFmsuly();
                        //System.out.println("i:"+j+" k:"+k+"  suly:"+rudsuly[j][1]);
                    }
                }
            }
            if (racsalap.get(j).getNev8() != null) {
                for (int k = 0; k < szelveny.size(); k++) {
                    if (szelveny.get(k).getNev().equals(racsalap.get(j).getNev8())) {
                        rudsuly[j][8] = szelveny.get(k).getFmsuly();
                        //System.out.println("i:"+j+" k:"+k+"  suly:"+rudsuly[j][1]);
                    }
                }
            }
        }
    }

    public void racselem1_alapadatok(int elem, int elemszamok, int irany, int magassag, int alsoszelxy, int felsoszelxy, int alsoszelyz, int felsoszelyz, int eltolasxy, int eltolasyz, int x, int y, int z) {
        ujracsalap1.setKoz(elem);
        //System.out.println(ujracsalap1.getKoz());
        ujracsalap1.setSzekcio(szekcioszam - 1);
        float adat = (alsoszelxy - felsoszelxy) / elemszamok;
        alsoxy = alsoszelxy - (elem - 1) * adat;
        ujracsalap1.setAlsoszelxy((int) alsoxy);
        felsoxy = alsoszelxy - elem * adat;
        ujracsalap1.setFelsoszelxy((int) felsoxy);
        adat = (alsoszelyz - felsoszelyz) / elemszamok;
        alsoyz = alsoszelyz - (elem - 1) * adat;
        ujracsalap1.setAlsoszelyz((int) alsoyz);
        felsoyz = alsoszelyz - elem * adat;
        ujracsalap1.setFelsoszelyz((int) felsoyz);
        // eltolasxy
        adat = eltolasxy / elemszamok;
        ujracsalap1.setEltolasxy((int) adat);
        //  eltolasyz
        adat = eltolasyz / elemszamok;
        ujracsalap1.setEltolasyz((int) adat);
        if (irany == 1) {    // Függöleges
            // A függőleges szakaszok
            // x                            
            adat = eltolasxy / elemszamok;
            adat += ((alsoszelxy - felsoszelxy) / elemszamok) / 2;
            //System.out.println("adat:"+adat);            
            adat = x + (elem - 1) * adat;
            ujracsalap1.setX((int) adat);
            // y 
            adat = magassag / elemszamok;
            adat = y + (elem - 1) * adat;
            ujracsalap1.setY((int) adat);
            // z 
            adat = (eltolasyz / elemszamok);
            adat += ((alsoszelyz - felsoszelyz) / elemszamok) / 2;
            //System.out.println("adat:"+adat); 
            adat = z + (elem - 1) * adat;
            ujracsalap1.setZ((int) adat);
        }
        if (irany == 2) {    // Vízszintes
            // A vízszintes szakaszok
            // x 
            adat = magassag / elemszamok;
            adat = x + (elem - 1) * adat;
            ujracsalap1.setX((int) adat);
            // y                                                        
            adat = eltolasxy / elemszamok;
            adat += ((alsoszelxy - felsoszelxy) / 2) / elemszamok;
            adat = y + (elem - 1) * adat;
            ujracsalap1.setY((int) adat);
            // z                             
            adat = eltolasyz / elemszamok;
            adat += ((alsoszelyz - felsoszelyz) / 2) / elemszamok;
            adat = z + (elem - 1) * adat;
            ujracsalap1.setZ((int) adat);
        }
        if (irany == 3) {        // Kéttámaszú
            // A kéttámaszú tartórácsok elemei
            // Ez még nem korrekt
            // x 
            adat = magassag / elemszamok;
            adat = x + (elem - 1) * adat;
            ujracsalap1.setX((int) adat);
            // y                                                        
            adat = ((alsoszelxy - felsoszelxy) / 2) / elemszamok;
            adat = y + (elem - 1) * adat;
            ujracsalap1.setY((int) adat);
            // z                             
            adat = ((alsoszelyz - felsoszelyz) / 2) / elemszamok;
            ujracsalap1.setZ((int) adat);
        }
        // A köz magassága
        adat = magassag / elemszamok;
        ujracsalap1.setMagassag((int) adat);
    }

    public void racsrudvastagsag() {
        float[] vastagsag = new float[9];
        if (racsalap.size() > 0) {
            for (int k = 1; k <= 8; k++) {
                vastagsag[k] = 0;
            }
            for (int j = 0; j < racsalap.size(); j++) {
                if (racsalap.get(j).getNev1() != null) {
                    for (int k = 0; k < szelveny.size(); k++) {
                        if (racsalap.get(j).getNev1().equals(szelveny.get(k).getNev())) {
                            vastagsag[1] = szelveny.get(k).getMagassag();
                        }
                    }
                }
                if (racsalap.get(j).getNev2() != null) {
                    for (int k = 0; k < szelveny.size(); k++) {
                        if (racsalap.get(j).getNev2().equals(szelveny.get(k).getNev())) {
                            vastagsag[2] = szelveny.get(k).getMagassag();
                        }
                    }
                }
                if (racsalap.get(j).getNev3() != null) {
                    for (int k = 0; k < szelveny.size(); k++) {
                        if (racsalap.get(j).getNev3().equals(szelveny.get(k).getNev())) {
                            vastagsag[3] = szelveny.get(k).getMagassag();
                        }
                    }
                }
                if (racsalap.get(j).getNev4() != null) {
                    for (int k = 0; k < szelveny.size(); k++) {
                        if (racsalap.get(j).getNev4().equals(szelveny.get(k).getNev())) {
                            vastagsag[4] = szelveny.get(k).getMagassag();
                        }
                    }
                }
                if (racsalap.get(j).getNev5() != null) {
                    for (int k = 0; k < szelveny.size(); k++) {
                        if (racsalap.get(j).getNev5().equals(szelveny.get(k).getNev())) {
                            vastagsag[5] = szelveny.get(k).getMagassag();
                        }
                    }
                }
                if (racsalap.get(j).getNev6() != null) {
                    for (int k = 0; k < szelveny.size(); k++) {
                        if (racsalap.get(j).getNev6().equals(szelveny.get(k).getNev())) {
                            vastagsag[6] = szelveny.get(k).getMagassag();
                        }
                    }
                }
                if (racsalap.get(j).getNev7() != null) {
                    for (int k = 0; k < szelveny.size(); k++) {
                        if (racsalap.get(j).getNev7().equals(szelveny.get(k).getNev())) {
                            vastagsag[7] = szelveny.get(k).getMagassag();
                        }
                    }
                }
                if (racsalap.get(j).getNev8() != null) {
                    for (int k = 0; k < szelveny.size(); k++) {
                        if (racsalap.get(j).getNev8().equals(szelveny.get(k).getNev())) {
                            vastagsag[8] = szelveny.get(k).getMagassag();
                        }
                    }
                }
                // Az aktuális rúd vastagságának hozzárendelése az adott szekciónál
                for (int i = 0; i < rud.size(); i++) {
                    //System.out.println("i:"+i+"  j:"+j+"  szekcio:"+rud.get(i).getSzekcio());
                    if (rud.get(i).getSzekcio() == j) {
                        rud.get(i).getRud().setVastagsag((int) vastagsag[rud.get(i).getTipus()]);
                        //System.out.println("i:"+i+"  j:"+j+"  tipus:"+rud.get(i).getTipus()+" szelvény:"+rud.get(i).getRud().getSzelveny());
                    }
                }
            }
        }
    }

    public void racsalap_adatrogzito(int szekcio) {
        try {
            Class.forName("com.mysql.jdbc.Driver").newInstance();
            Connection co = DriverManager.getConnection(Global.mysql_server, Global.mysql_user, Global.mysql_password);
            Statement st = co.createStatement();
            // Adattörlés
            parancs = "delete from Racsalap where nev = '" + nev + "' and szekcio = '"+racsalap.get(szekcio).getSzekcio()+"' ";
            //System.out.println(parancs);
            st.execute(parancs);
            Racsalap ujracsalap = new Racsalap();
            ujracsalap.setNev(nev);
            ujracsalap.setSzekcio(racsalap.get(szekcio).getSzekcio());
            ujracsalap.setMagassag(racsalap.get(szekcio).getMagassag());
            ujracsalap.setAlsoszelxy(racsalap.get(szekcio).getAlsoszelxy());
            ujracsalap.setAlsoszelyz(racsalap.get(szekcio).getAlsoszelyz());
            ujracsalap.setFelsoszelxy(racsalap.get(szekcio).getFelsoszelxy());
            ujracsalap.setFelsoszelyz(racsalap.get(szekcio).getFelsoszelyz());
            ujracsalap.setX(racsalap.get(szekcio).getX());
            ujracsalap.setY(racsalap.get(szekcio).getY());
            ujracsalap.setZ(racsalap.get(szekcio).getZ());
            ujracsalap.setEltolasxy(racsalap.get(szekcio).getEltolasxy());
            ujracsalap.setEltolasyz(racsalap.get(szekcio).getEltolasyz());
            ujracsalap.setTeljes(racsalap.get(szekcio).getTeljes());
            ujracsalap.setIrany(racsalap.get(szekcio).getIrany());
            ujracsalap.setFelvitel(now);
            if (racsalap.get(szekcio).getNev1() == null) {
                ujracsalap.setNev1("");
            } else {
                ujracsalap.setNev1(racsalap.get(szekcio).getNev1());
            }
            if (racsalap.get(szekcio).getNev2() == null) {
                ujracsalap.setNev2("");
            } else {
                ujracsalap.setNev2(racsalap.get(szekcio).getNev2());
            }
            if (racsalap.get(szekcio).getNev3() == null) {
                ujracsalap.setNev3("");
            } else {
                ujracsalap.setNev3(racsalap.get(szekcio).getNev3());
            }
            if (racsalap.get(szekcio).getNev4() == null) {
                ujracsalap.setNev4("");
            } else {
                ujracsalap.setNev4(racsalap.get(szekcio).getNev4());
            }
            if (racsalap.get(szekcio).getNev5() == null) {
                ujracsalap.setNev5("");
            } else {
                ujracsalap.setNev5(racsalap.get(szekcio).getNev5());
            }
            if (racsalap.get(szekcio).getNev6() == null) {
                ujracsalap.setNev6("");
            } else {
                ujracsalap.setNev6(racsalap.get(szekcio).getNev6());
            }
            if (racsalap.get(szekcio).getNev7() == null) {
                ujracsalap.setNev7("");
            } else {
                ujracsalap.setNev7(racsalap.get(szekcio).getNev7());
            }
            if (racsalap.get(szekcio).getNev8() == null) {
                ujracsalap.setNev8("");
            } else {
                ujracsalap.setNev8(racsalap.get(szekcio).getNev8());
            }
            parancs = "INSERT INTO Racsalap (nev, szekcio, magassag, alsoszelxy, felsoszelxy, alsoszelyz, felsoszelyz, x, y, z, ";
            parancs = parancs + "irany, teljes, eltolasxy, eltolasyz, nev1, nev2, nev3, nev4, nev5, nev6, nev7, nev8) VALUES (";
            parancs = parancs + " '" + ujracsalap.getNev() + "',";
            parancs = parancs + " '" + ujracsalap.getSzekcio() + "',";
            parancs = parancs + " '" + ujracsalap.getMagassag() + "',";
            parancs = parancs + " '" + ujracsalap.getAlsoszelxy() + "',";
            parancs = parancs + " '" + ujracsalap.getFelsoszelxy() + "',";
            parancs = parancs + " '" + ujracsalap.getAlsoszelyz() + "',";
            parancs = parancs + " '" + ujracsalap.getFelsoszelyz() + "',";
            parancs = parancs + " '" + ujracsalap.getX() + "',";
            parancs = parancs + " '" + ujracsalap.getY() + "',";
            parancs = parancs + " '" + ujracsalap.getZ() + "',";
            parancs = parancs + " '" + ujracsalap.getIrany() + "',";
            parancs = parancs + " '" + ujracsalap.getTeljes() + "',";
            parancs = parancs + " '" + ujracsalap.getEltolasxy() + "',";
            parancs = parancs + " '" + ujracsalap.getEltolasyz() + "',";
            parancs = parancs + " '" + ujracsalap.getNev1() + "',";
            parancs = parancs + " '" + ujracsalap.getNev2() + "',";
            parancs = parancs + " '" + ujracsalap.getNev3() + "',";
            parancs = parancs + " '" + ujracsalap.getNev4() + "',";
            parancs = parancs + " '" + ujracsalap.getNev5() + "',";
            parancs = parancs + " '" + ujracsalap.getNev6() + "',";
            parancs = parancs + " '" + ujracsalap.getNev7() + "',";
            parancs = parancs + " '" + ujracsalap.getNev8() + "');";
            //System.out.println(parancs);
            st.execute(parancs);
            rs.close();
            st.close();
        } catch (InstantiationException e) {
        } catch (IllegalAccessException e) {
        } catch (ClassNotFoundException e) {
        } catch (SQLException e) {
        }
    }

    public void racsalap1_adatrogzito(int elem) {
        try {
            Class.forName("com.mysql.jdbc.Driver").newInstance();
            Connection co = DriverManager.getConnection(Global.mysql_server, Global.mysql_user, Global.mysql_password);
            Statement st = co.createStatement();
            // Adattörlés
            parancs = "delete from Racsalap1 where nev = '" + nev + "' and szekcio = '"+racsalap1.get(elem).getSzekcio()+"' and koz ='"+racsalap1.get(elem).getKoz()+"' ";
            //System.out.println(parancs);
            st.execute(parancs);
            parancs = "INSERT INTO Racsalap1 (nev,szekcio,koz,racs1,racs2,racs3,racs4,racs5,racs6,racs7,racs8) VALUES (";
            parancs = parancs + " '" + nev + "',";
            parancs = parancs + " '" + racsalap1.get(elem).getSzekcio() + "',";
            parancs = parancs + " '" + racsalap1.get(elem).getKoz() + "',";
            parancs = parancs + " '" + racsalap1.get(elem).getRacs1() + "',";
            parancs = parancs + " '" + racsalap1.get(elem).getRacs2() + "',";
            parancs = parancs + " '" + racsalap1.get(elem).getRacs3() + "',";
            parancs = parancs + " '" + racsalap1.get(elem).getRacs4() + "',";
            parancs = parancs + " '" + racsalap1.get(elem).getRacs5() + "',";
            parancs = parancs + " '" + racsalap1.get(elem).getRacs6() + "',";
            parancs = parancs + " '" + racsalap1.get(elem).getRacs7() + "',";
            parancs = parancs + " '" + racsalap1.get(elem).getRacs8() + "');";
            //System.out.println(parancs);
            st.execute(parancs);
            rs.close();
            st.close();
        } catch (InstantiationException e) {
        } catch (IllegalAccessException e) {
        } catch (ClassNotFoundException e) {
        } catch (SQLException e) {
        }
    }

    public void adatrogzito() {
        // A rácslap tábla
        for (int i = 0; i < racsalap.size(); i++) {
            racsalap_adatrogzito(i);
        }
        // A rácslap1 tábla
        for (int i = 0; i < racsalap1.size(); i++) {
            //System.out.println("i:"+(i-1)+"  Szekcio:"+racsalap1.get(i ).getSzekcio()+"  koz:" + racsalap1.get(i ).getKoz());
            racsalap1_adatrogzito(i);
        }
    }

    public void kozeppont_szamolo(int tetel) {
        // tetel:  0-teljes rajz, a többi : szekciórajz        
        // A köpéppont kiszámolása   
        //float minimum = 0, maximum = 0;
        //System.out.println("Tetel:" + tetel);
        if (tetel != -1) {
            Szekcioadatok.setMaxx(Float.MIN_VALUE);
            Szekcioadatok.setMinx(Float.MAX_VALUE);
            for (int i = 0; i < rud.size(); i++) {
                if (rud.get(i).getSzekcio() == tetel) {
                    if (csomopont.get(rud.get(i).getRud().getKezdocsp()).getCsomopont().getX() > Szekcioadatok.getMaxx()) {
                        Szekcioadatok.setMaxx(csomopont.get(rud.get(i).getRud().getKezdocsp()).getCsomopont().getX());
                    }
                    if (csomopont.get(rud.get(i).getRud().getKezdocsp()).getCsomopont().getX() < Szekcioadatok.getMinx()) {
                        Szekcioadatok.setMinx(csomopont.get(rud.get(i).getRud().getKezdocsp()).getCsomopont().getX());
                    }
                    if (csomopont.get(rud.get(i).getRud().getVegecsp()).getCsomopont().getX() > Szekcioadatok.getMaxx()) {
                        Szekcioadatok.setMaxx(csomopont.get(rud.get(i).getRud().getVegecsp()).getCsomopont().getX());
                    }
                    if (csomopont.get(rud.get(i).getRud().getVegecsp()).getCsomopont().getX() < Szekcioadatok.getMinx()) {
                        Szekcioadatok.setMinx(csomopont.get(rud.get(i).getRud().getVegecsp()).getCsomopont().getX());
                    }
                }
            }
            kozepx[1] = Szekcioadatok.getMinx() + (Szekcioadatok.getMaxx() - Szekcioadatok.getMinx()) / 2;
            Szekcioadatok.setMaxy(Float.MIN_VALUE);
            Szekcioadatok.setMiny(Float.MAX_VALUE);
            for (int i = 0; i < rud.size(); i++) {
                if (rud.get(i).getSzekcio() == tetel) {
                    if (csomopont.get(rud.get(i).getRud().getKezdocsp()).getCsomopont().getY() > Szekcioadatok.getMaxy()) {
                        Szekcioadatok.setMaxy(csomopont.get(rud.get(i).getRud().getKezdocsp()).getCsomopont().getY());
                    }
                    if (csomopont.get(rud.get(i).getRud().getKezdocsp()).getCsomopont().getY() < Szekcioadatok.getMiny()) {
                        Szekcioadatok.setMiny(csomopont.get(rud.get(i).getRud().getKezdocsp()).getCsomopont().getY());
                    }
                    if (csomopont.get(rud.get(i).getRud().getVegecsp()).getCsomopont().getY() > Szekcioadatok.getMaxy()) {
                        Szekcioadatok.setMaxy(csomopont.get(rud.get(i).getRud().getVegecsp()).getCsomopont().getY());
                    }
                    if (csomopont.get(rud.get(i).getRud().getVegecsp()).getCsomopont().getY() < Szekcioadatok.getMiny()) {
                        Szekcioadatok.setMiny(csomopont.get(rud.get(i).getRud().getVegecsp()).getCsomopont().getY());
                    }
                }
            }
            kozepy[1] = Szekcioadatok.getMiny() + (Szekcioadatok.getMaxy() - Szekcioadatok.getMiny()) / 2;
            Szekcioadatok.setMaxz(Float.MIN_VALUE);
            Szekcioadatok.setMinz(Float.MAX_VALUE);
            for (int i = 0; i < rud.size(); i++) {
                if (rud.get(i).getSzekcio() == tetel) {
                    if (csomopont.get(rud.get(i).getRud().getKezdocsp()).getCsomopont().getZ() > Szekcioadatok.getMaxz()) {
                        Szekcioadatok.setMaxz(csomopont.get(rud.get(i).getRud().getKezdocsp()).getCsomopont().getZ());
                    }
                    if (csomopont.get(rud.get(i).getRud().getKezdocsp()).getCsomopont().getZ() < Szekcioadatok.getMinz()) {
                        Szekcioadatok.setMinz(csomopont.get(rud.get(i).getRud().getKezdocsp()).getCsomopont().getZ());
                    }
                    if (csomopont.get(rud.get(i).getRud().getVegecsp()).getCsomopont().getZ() > Szekcioadatok.getMaxz()) {
                        Szekcioadatok.setMaxz(csomopont.get(rud.get(i).getRud().getVegecsp()).getCsomopont().getZ());
                    }
                    if (csomopont.get(rud.get(i).getRud().getVegecsp()).getCsomopont().getZ() < Szekcioadatok.getMinz()) {
                        Szekcioadatok.setMinz(csomopont.get(rud.get(i).getRud().getVegecsp()).getCsomopont().getZ());
                    }
                }
            }
            kozepz[1] = Szekcioadatok.getMinz() + (Szekcioadatok.getMaxz() - Szekcioadatok.getMinz()) / 2;
            // Középrehozás
            kozepx[1] -= Szekcioadatok.getMinx();
            kozepy[1] -= Szekcioadatok.getMiny();
            kozepz[1] -= Szekcioadatok.getMinz();
            /*System.out.println("Szekcio:");
             System.out.println("kozepx:" + kozepx[1] + "   kozepy:" + kozepy[1] + "   kozepz:" + kozepz[1]);
             System.out.println("minz:" + Szekcioadatok.getMinz() + "   maxz:" + Szekcioadatok.getMaxz());*/
        } else {
            float maximum = Float.MIN_VALUE;
            float minimum = Float.MAX_VALUE;
            for (int i = 0; i < csomopont.size(); i++) {
                if (csomopont.get(i).getCsomopont().getX() > maximum) {
                    maximum = csomopont.get(i).getCsomopont().getX();
                }
                if (csomopont.get(i).getCsomopont().getX() < minimum) {
                    minimum = csomopont.get(i).getCsomopont().getX();
                }
            }
            if (maximum > minimum) {
                Teljesadatok.setMaxx(maximum);
                Teljesadatok.setMinx(minimum);
            } else {
                Teljesadatok.setMaxx(minimum);
                Teljesadatok.setMinx(maximum);
            }
            kozepx[0] = Teljesadatok.getMinx() + (Teljesadatok.getMaxx() - Teljesadatok.getMinx()) / 2;
            maximum = Float.MIN_VALUE;
            minimum = Float.MAX_VALUE;
            for (int i = 0; i < csomopont.size(); i++) {
                if (csomopont.get(i).getCsomopont().getY() > maximum) {
                    maximum = csomopont.get(i).getCsomopont().getY();
                }
                if (csomopont.get(i).getCsomopont().getY() < minimum) {
                    minimum = csomopont.get(i).getCsomopont().getY();
                }
            }
            if (maximum > minimum) {
                Teljesadatok.setMaxy(maximum);
                Teljesadatok.setMiny(minimum);
            } else {
                Teljesadatok.setMaxy(minimum);
                Teljesadatok.setMiny(maximum);
            }
            kozepy[0] = Teljesadatok.getMiny() + (Teljesadatok.getMaxy() - Teljesadatok.getMiny()) / 2;
            maximum = Float.MIN_VALUE;
            minimum = Float.MAX_VALUE;
            for (int i = 0; i < csomopont.size(); i++) {
                if (csomopont.get(i).getCsomopont().getZ() > maximum) {
                    maximum = csomopont.get(i).getCsomopont().getZ();
                }
                if (csomopont.get(i).getCsomopont().getZ() < minimum) {
                    minimum = csomopont.get(i).getCsomopont().getZ();
                }
            }
            if (maximum > minimum) {
                Teljesadatok.setMaxz(maximum);
                Teljesadatok.setMinz(minimum);
            } else {
                Teljesadatok.setMaxz(minimum);
                Teljesadatok.setMinz(maximum);
            }
            kozepz[0] = Teljesadatok.getMinz() + (Teljesadatok.getMaxz() - Teljesadatok.getMinz()) / 2;
            /*System.out.println("Teljes szerkezet:");
             System.out.println("kozepx:" + kozepx[0] + "   kozepy:" + kozepy[0] + "   kozepz:" + kozepz[0]);
             System.out.println("minx:" + Teljesadatok.getMinx() + "   maxx:" + Teljesadatok.getMaxx());*/
        }
    }

    public void pontforgato(int elem, int tetel) {
        double atfogo;
        szog = 0;
        float kozepx_, kozepy_;
        float x_, y_;
        switch (elem) {
            case 2:
                kozepx_ = kozepx[tetel];
                kozepy_ = kozepz[tetel];
                x_ = x;
                y_ = z;
                break;
            case 3:
                kozepx_ = kozepy[tetel];
                kozepy_ = kozepz[tetel];
                x_ = y;
                y_ = z;
                break;
            default:
                kozepx_ = kozepx[tetel];
                kozepy_ = kozepy[tetel];
                x_ = x;
                y_ = y;
        }
        atfogo = Math.sqrt(Double.parseDouble(String.valueOf((kozepx_ - x_) * (kozepx_ - x_) + (kozepy_ - y_) * (kozepy_ - y_))));
        szog = Math.atan2((x_ - kozepx_), (y_ - kozepy_));
        szog += Math.toRadians(forgatas[tetel][elem]);
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

    public void kozbeolvaso(int szekcio, int koz) {
        for (int i = 1; i <= 8; i++) {
            racselemek[i] = 0;
        }
        for (int i = 0; i < racsalap1.size(); i++) {
            if ((racsalap1.get(i).getSzekcio() == szekcio) && ((racsalap1.get(i).getKoz() == koz))) {
                racselemek[1] = racsalap1.get(i).getRacs1();
                racselemek[2] = racsalap1.get(i).getRacs2();
                racselemek[3] = racsalap1.get(i).getRacs3();
                racselemek[4] = racsalap1.get(i).getRacs4();
                racselemek[5] = racsalap1.get(i).getRacs5();
                racselemek[6] = racsalap1.get(i).getRacs6();
                racselemek[7] = racsalap1.get(i).getRacs7();
                racselemek[8] = racsalap1.get(i).getRacs8();
            }
        }
    }

    public void mintamasolo(int szekcio, int koz) {
        // A mintarács pontjainak rámásolása az átmeneti rácsra 
        float mag = 0, alsoxy = 0, alsoyz = 0, felsoxy = 0, felsoyz = 0, eltxy = 0, eltyz = 0, kezdx = 0, kezdy = 0, kezdz = 0;
        float[] pont = new float[9];  // A pontok meghatározásához a sarokpontok [x]  vagy [z]
        for (int i = 1; i < maxcsomopont; i++) {
            tempcsp[i][0] = 0;
            tempcsp[i][1] = 0;
            tempcsp[i][2] = 0;
        }
        for (int i = 0; i < racsalap1.size(); i++) {
            //System.out.println("szekcio:" + racsalap1.get(i).getSzekcio()+" sz:"+szekcio+" koz:"+racsalap1.get(i).getKoz());
            if ((racsalap1.get(i).getSzekcio() == szekcio) && ((racsalap1.get(i).getKoz() == koz))) {
                mag = racsalap1.get(i).getMagassag();
                alsoxy = racsalap1.get(i).getAlsoszelxy();
                alsoyz = racsalap1.get(i).getAlsoszelyz();
                felsoxy = racsalap1.get(i).getFelsoszelxy();
                felsoyz = racsalap1.get(i).getFelsoszelyz();
                kezdx = racsalap1.get(i).getX();    // ??????????????
                kezdy = racsalap1.get(i).getY();    // ??????????????
                kezdz = racsalap1.get(i).getZ();    // ??????????????
                eltxy = racsalap1.get(i).getEltolasxy();
                eltyz = racsalap1.get(i).getEltolasyz();
            }
        }
        if (racsalap.get(szekcio).getIrany() == 1) {
            // függőleges a rács
            // y 
            for (int i = 0; i < mintaindexf; i++) {
                tempcsp[i][1] = kezdy + mintacspf[i][1] * (mag / 100);
            }
            // x
            pont[1] = kezdx;
            pont[3] = kezdx + alsoxy;
            pont[2] = (pont[1] + pont[3]) / 2;
            pont[4] = kezdx + (alsoxy - felsoxy) / 2;
            pont[6] = kezdx + alsoxy - (alsoxy - felsoxy) / 2;
            /*pont[4] = kezdx + (alsoxy - felsoxy) / 2 + eltxy/2;
             pont[6] = kezdx + alsoxy - (alsoxy - felsoxy) / 2 + eltxy/2;*/
            pont[5] = (pont[4] + pont[6]) / 2;
            pont[7] = pont[4] + (pont[5] - pont[4]) / 2;
            pont[8] = pont[5] + (pont[5] - pont[4]) / 2;
            //System.out.println("szekcio:" + szekcio+ "  pont1:" + pont[1]+ "  pont2:" + pont[2]+ "  pont3:" + pont[3]+ "  pont4:" + pont[4]+ "  pont5:" + pont[5]+ "  pont6:" + pont[6]);
            for (int i = 0; i < mintaindexf; i++) {
                if (mintacspfjelleg[i][0] != 3) {
                    tempcsp[i][0] = pont[mintacspfjelleg[i][2]]
                            + (pont[mintacspfjelleg[i][3]]
                            - pont[mintacspfjelleg[i][2]]) * (mintacspf[i][1] / 100);
                }
            }
            for (int i = 0; i < mintaindexf; i++) {
                if (mintacspfjelleg[i][0] == 3) {
                    tempcsp[i][0] = (tempcsp[mintacspfjelleg[i][2]][0]
                            + tempcsp[mintacspfjelleg[i][3]][0]) / 2;
                }
            }
            // z      
            pont[1] = kezdz;
            pont[3] = kezdz + alsoyz;
            pont[2] = (pont[1] + pont[3]) / 2;
            pont[4] = kezdz + (alsoyz - felsoyz) / 2;
            pont[6] = kezdz + alsoyz - (alsoyz - felsoyz) / 2;
            /*pont[4] = kezdz + (alsoyz - felsoyz) / 2 + eltyz;
             pont[6] = kezdz + alsoyz - (alsoyz - felsoyz) / 2 + eltyz;*/
            pont[5] = (pont[4] + pont[6]) / 2;
            pont[7] = pont[4] + (pont[5] - pont[4]) / 2;
            pont[8] = pont[5] + (pont[5] - pont[4]) / 2;
            for (int i = 0; i < mintaindexf; i++) {
                if (mintacspfjelleg[i][1] != 3) {
                    tempcsp[i][2] = pont[mintacspfjelleg[i][4]]
                            + (pont[mintacspfjelleg[i][5]]
                            - pont[mintacspfjelleg[i][4]]) * (mintacspf[i][1] / 100);
                }
            }
            for (int i = 0; i < mintaindexf; i++) {
                if (mintacspfjelleg[i][1] == 3) {
                    tempcsp[i][2] = (tempcsp[mintacspfjelleg[i][4]][2] + tempcsp[mintacspfjelleg[i][5]][2]) / 2;
                }
                // Az eltolások értékei XY-síkban
                tempcsp[i][0] += (mintacspf[i][1] / 100) * eltxy;
                // Az eltolások értékei YZ-síkban
                tempcsp[i][2] += (mintacspf[i][1] / 100) * eltyz;
                //System.out.println("Rácselem:" + i + "  x:" + tempcsp[i][0] + "  y:" + tempcsp[i][1] + "  z:" + tempcsp[i][2]);
            }
        } else {
            // Vízszintes a rács
            // x 
            for (int i = 0; i <= mintaindexv; i++) {
                tempcsp[i][0] = kezdx + mintacspv[i][0] * (mag / 100);
            }
            // y
            tempcsp[1][1] = kezdy;
            tempcsp[2][1] = tempcsp[1][1];
            tempcsp[3][1] = kezdy + alsoxy;
            tempcsp[4][1] = tempcsp[3][1];

            tempcsp[5][1] = kezdy + ((alsoxy - felsoxy) / 2) + eltxy;
            tempcsp[6][1] = tempcsp[5][1];
            tempcsp[7][1] = kezdy + ((alsoxy - felsoxy) / 2) + felsoxy + eltxy;
            tempcsp[8][1] = tempcsp[7][1];
            /*System.out.println("kezdy:"+kezdy+"  alsoxy:"+alsoxy+"  felsoxy:"+felsoxy+"  eltxy:"+eltxy);
             System.out.println("tempcsp[1][1]:"+tempcsp[1][1]+"  tempcsp[3][1]:"+tempcsp[3][1]);
             System.out.println("tempcsp[5][1]:"+tempcsp[5][1]+"  tempcsp[7][1]:"+tempcsp[7][1]); */

            tempcsp[10][1] = (tempcsp[1][1] + tempcsp[5][1]) / 2;
            tempcsp[12][1] = tempcsp[10][1];
            tempcsp[9][1] = ((tempcsp[1][1] + tempcsp[3][1]) / 2 + (tempcsp[7][1] + tempcsp[5][1]) / 2) / 2;
            tempcsp[11][1] = tempcsp[9][1];
            tempcsp[13][1] = kezdy + felsoxy / 2 + eltxy;
            //System.out.println("eltolas:"+eltxy);
            // z
            tempcsp[1][2] = kezdz;
            tempcsp[2][2] = kezdz + alsoyz;
            tempcsp[3][2] = tempcsp[1][2];
            tempcsp[4][2] = tempcsp[2][2];
            tempcsp[5][2] = kezdz + (alsoyz - felsoyz) / 2 + eltyz;
            tempcsp[6][2] = kezdz + (alsoyz - felsoyz) / 2 + felsoyz + eltyz;
            tempcsp[7][2] = tempcsp[5][2];
            tempcsp[8][2] = tempcsp[6][2];
            tempcsp[9][2] = (tempcsp[1][2] + tempcsp[5][2]) / 2;
            tempcsp[10][2] = tempcsp[9][2];
            tempcsp[11][2] = (tempcsp[2][2] + tempcsp[6][2]) / 2;
            tempcsp[12][2] = tempcsp[11][2];
            tempcsp[13][2] = (tempcsp[5][2] + tempcsp[6][2]) / 2;
            /*System.out.println("szekcio:" + szekcio + "  köz:" + koz);
             for (int i = 1; i <= 13; i++) {
             System.out.println("i:" + i + "  x:" + tempcsp[i][0] + " y:" + tempcsp[i][1] + " z:" + tempcsp[i][2]);
             }
             System.out.println(); */
        }
    }

    public void racskapcsolo(int szekcio) {
        // A vízszintes rácsok kapcsolódása alapján a függőleges rács(ok) megfelelő kapcsolódási pontját kell keresni        
        if (szekcio > 0) {
            //System.out.println("Szekció:" + szekcio);
            //System.out.println(" x:" + racsalap.get(szekcio).getX() + " y:" + racsalap.get(szekcio).getY() + " z:" + racsalap.get(szekcio).getZ() + " xy:" + racsalap.get(szekcio).getAlsoszelxy() + " yz:" + racsalap.get(szekcio).getAlsoszelyz());
            List<Csomopont> kapcsolopont = new ArrayList<>();
            Csomopont c = new Csomopont();
            c.setId(1);                             // A meglévő szerkezeten a kapcsolódási pont
            c.setX(racsalap.get(szekcio).getX());
            c.setY(racsalap.get(szekcio).getY());
            c.setZ(racsalap.get(szekcio).getZ());
            kapcsolopont.add(c);
            c = new Csomopont();
            c.setId(2);
            c.setX(racsalap.get(szekcio).getX());
            c.setY(racsalap.get(szekcio).getY());
            c.setZ(racsalap.get(szekcio).getZ() + racsalap.get(szekcio).getAlsoszelyz());
            kapcsolopont.add(c);
            c = new Csomopont();
            c.setId(3);
            c.setX(racsalap.get(szekcio).getX());
            c.setY(racsalap.get(szekcio).getY() + racsalap.get(szekcio).getAlsoszelxy());
            c.setZ(racsalap.get(szekcio).getZ());
            kapcsolopont.add(c);
            c = new Csomopont();
            c.setId(4);
            c.setX(racsalap.get(szekcio).getX());
            c.setY(racsalap.get(szekcio).getY() + racsalap.get(szekcio).getAlsoszelxy());
            c.setZ(racsalap.get(szekcio).getZ() + racsalap.get(szekcio).getAlsoszelyz());
            kapcsolopont.add(c);
            /*for (int i = 0; i < 4; i++) {
                System.out.println(" i:"+i+" x:"+kapcsolopont.get(i).getX()+" y:"+kapcsolopont.get(i).getY()+" z:"+kapcsolopont.get(i).getZ()+" id:"+kapcsolopont.get(i).getId());
            }*/
            //System.out.println();
            for (int i = 0; i < szekcio; i++) {
                if ((racsalap.get(i).getIrany() == 1) && (racsalap.get(i).getSzekcio() != szekcio)) {
                    for (int j = 0; j < 4; j++) {
                        float tavolsag = Float.MAX_VALUE;
                        for (int k = 0; k < csomopont.size(); k++) {
                            if ((Math.abs(kapcsolopont.get(j).getX() - csomopont.get(k).getCsomopont().getX())
                                    + Math.abs(kapcsolopont.get(j).getY() - csomopont.get(k).getCsomopont().getY())
                                    + Math.abs(kapcsolopont.get(j).getZ() - csomopont.get(k).getCsomopont().getZ())) < tavolsag) {
                                tavolsag = Math.abs(kapcsolopont.get(j).getX() - csomopont.get(k).getCsomopont().getX())
                                        + Math.abs(kapcsolopont.get(j).getY() - csomopont.get(k).getCsomopont().getY())
                                        + Math.abs(kapcsolopont.get(j).getZ() - csomopont.get(k).getCsomopont().getZ());
                                //System.out.println("Szekcio:"+i+" pont:"+j+" id:"+k+"  Tavolsag:" + tavolsag);
                                kapcsolopont.get(j).setId(k);
                            }
                        }
                    }
                }
            }
            /*
            for (int i = 0; i < 4; i++) {
                System.out.println("Szekcio:" + szekcio + " i:" + i + " x:" + kapcsolopont.get(i).getX() + " y:" + kapcsolopont.get(i).getY() + " z:" + kapcsolopont.get(i).getZ() + " id:" + kapcsolopont.get(i).getId() + " ");
            }
            System.out.println("Alsoszelxy:" + (Math.abs(csomopont.get(kapcsolopont.get(0).getId()).getCsomopont().getZ() - csomopont.get(kapcsolopont.get(1).getId()).getCsomopont().getZ())));
            System.out.println("Felsoszelxy:" + (Math.abs(csomopont.get(kapcsolopont.get(2).getId()).getCsomopont().getZ() - csomopont.get(kapcsolopont.get(3).getId()).getCsomopont().getZ())));
            System.out.println();*/
        }
    }

    public void racselemek() {
        // Az átmeneti rács-ból a végső csomópontok és rúdelemek kikalkulálása 
        int koz;
        csomopont.clear();
        rud.clear();
        int[] racs = new int[9];
        for (int i = 0; i < rud.size(); i++) {
            rud.get(i).setKijelzes(0);
        }
        for (int i = 0; i < racsalap.size(); i++) {
            // A szekción belüli közök összeszámolása
            koz = 0;
            for (int j = 0; j < racsalap1.size(); j++) {
                if (racsalap1.get(j).getSzekcio() == i) {
                    koz++;
                }
            }
            //System.out.println("koz:"+koz);
            if (koz > 0) {
                //if (racsalap.get(i).getIrany() == 2) {
                // A vízszintes rácsok koordinátáinak ellenőrzése
                racskapcsolo(i);
                //}
                for (int j = 1; j <= koz; j++) {

                    mintamasolo(i, j);
                    for (int k = 1; k < 9; k++) {
                        racs[k] = 0;
                    }
                    for (int k = 0; k < racsalap1.size(); k++) {
                        if ((racsalap1.get(k).getSzekcio() == i) && ((racsalap1.get(k).getKoz() == j))) {
                            racs[1] = racsalap1.get(k).getRacs1();
                            racs[2] = racsalap1.get(k).getRacs2();
                            racs[3] = racsalap1.get(k).getRacs3();
                            racs[4] = racsalap1.get(k).getRacs4();
                            racs[5] = racsalap1.get(k).getRacs5();
                            racs[6] = racsalap1.get(k).getRacs6();
                            racs[7] = racsalap1.get(k).getRacs7();
                            racs[8] = racsalap1.get(k).getRacs8();
                        }
                    }
                    // A csomópontok és a rudak összerakása
                    for (int k = 1; k < 9; k++) {
                        if (racs[k] > 0) {
                            for (int m = 0; m < mintarud.size(); m++) {
                                //System.out.println(mintarud.get(m).getIrany()+"  "+ racsalap.get(i).getIrany());
                                if ((mintarud.get(m).getIrany() == racsalap.get(i).getIrany())
                                        && (mintarud.get(m).getTipus() == k) && (mintarud.get(m).getVerzio() == racs[k])) {
                                    // A kezdő csomópont / végső csomópont
                                    //System.out.println("Tipus:"+mintarud.get(m).getTipus()+ " Rács:"+racs[k]+"  kezdcsp:"+mintarud.get(m).getKezdocsp()+" vegecsp:"+ mintarud.get(m).getVegecsp());
                                    csomopont_beiro(i, mintarud.get(m).getKezdocsp(), mintarud.get(m).getVegecsp());
                                    // A rúd hozzáadása
                                    rud_beiro(i, mintarud.get(m).getKezdocsp(), mintarud.get(m).getVegecsp(), j, k);
                                }
                            }
                        }
                    }
                }
            }
            if ((racsalap.get(i).getIrany() != 1) && ((racsalap.get(i).getTeljes() - racsalap.get(i).getMagassag()) != 0)) {
                // A vízszintes tartók végső konzolkinyúlása
                //x
                tempcsp[14][0] = tempcsp[5][0] + (racsalap.get(i).getTeljes() - racsalap.get(i).getMagassag());
                tempcsp[15][0] = tempcsp[14][0];
                //y
                tempcsp[14][1] = racsalap.get(i).getY()
                        + ((racsalap.get(i).getTeljes() / (tempcsp[5][0] - tempcsp[1][0])) * (tempcsp[5][1] - tempcsp[1][1]));   //????????
                //System.out.println("teljes:"+racsalap.get(i ).getTeljes()+" Mag:"+(tempcsp[5][0]-tempcsp[1][0])+ " eltolas:"+(tempcsp[5][1]-tempcsp[1][1]));
                tempcsp[15][1] = tempcsp[14][1];
                //z
                tempcsp[14][2] = tempcsp[5][2]
                        + ((racsalap.get(i).getTeljes() - racsalap.get(i).getMagassag())
                        / (tempcsp[5][0] - tempcsp[1][0])) * ((tempcsp[5][2] - tempcsp[1][2]));
                tempcsp[15][2] = tempcsp[6][2]
                        + ((racsalap.get(i).getTeljes() - racsalap.get(i).getMagassag())
                        / (tempcsp[6][0] - tempcsp[2][0])) * ((tempcsp[6][2] - tempcsp[2][2]));
                csomopont_beiro(i, 14, 15);
                rud_beiro(i, 14, 15, koz + 1, 1);
                rud_beiro(i, 5, 14, koz + 1, 1);
                rud_beiro(i, 6, 15, koz + 1, 1);
                rud_beiro(i, 7, 14, koz + 1, 1);
                rud_beiro(i, 8, 15, koz + 1, 1);
            }
        }
        // A rudak profilneveinek beírása
        for (int i = 0; i < rud.size(); i++) {
            for (int j = 0; j < racsalap.size(); j++) {
                if ((rud.get(i).getSzekcio() == racsalap.get(j).getSzekcio()) && (rud.get(i).getTipus() == 1)) {
                    rud.get(i).getRud().setSzelveny(racsalap.get(j).getNev1());
                }
                if ((rud.get(i).getSzekcio() == racsalap.get(j).getSzekcio()) && (rud.get(i).getTipus() == 2)) {
                    rud.get(i).getRud().setSzelveny(racsalap.get(j).getNev2());
                }
                if ((rud.get(i).getSzekcio() == racsalap.get(j).getSzekcio()) && (rud.get(i).getTipus() == 3)) {
                    rud.get(i).getRud().setSzelveny(racsalap.get(j).getNev3());
                }
                if ((rud.get(i).getSzekcio() == racsalap.get(j).getSzekcio()) && (rud.get(i).getTipus() == 4)) {
                    rud.get(i).getRud().setSzelveny(racsalap.get(j).getNev4());
                }
                if ((rud.get(i).getSzekcio() == racsalap.get(j).getSzekcio()) && (rud.get(i).getTipus() == 5)) {
                    rud.get(i).getRud().setSzelveny(racsalap.get(j).getNev5());
                }
                if ((rud.get(i).getSzekcio() == racsalap.get(j).getSzekcio()) && (rud.get(i).getTipus() == 6)) {
                    rud.get(i).getRud().setSzelveny(racsalap.get(j).getNev6());
                }
                if ((rud.get(i).getSzekcio() == racsalap.get(j).getSzekcio()) && (rud.get(i).getTipus() == 7)) {
                    rud.get(i).getRud().setSzelveny(racsalap.get(j).getNev7());
                }
                if ((rud.get(i).getSzekcio() == racsalap.get(j).getSzekcio()) && (rud.get(i).getTipus() == 8)) {
                    rud.get(i).getRud().setSzelveny(racsalap.get(j).getNev8());
                }
            }
        }
    }

    public void csomopont_beiro(int szekcio, int kezdocsp, int vegecsp) {
        boolean beiras = true;
        Racstervezocsomopont kezdocsomopont = new Racstervezocsomopont();
        Racstervezocsomopont vegecsomopont = new Racstervezocsomopont();
        Csomopont kezdopont = new Csomopont();
        Csomopont vegepont = new Csomopont();
        kezdocsomopont.setCsomopont(kezdopont);
        kezdocsomopont.getCsomopont().setAzonosito(nev);
        kezdocsomopont.getCsomopont().setProjekt(projektnev);
        kezdocsomopont.setSzekcio(szekcio);
        if (csomopont.size() > 0) {
            for (int i = 0; i < csomopont.size(); i++) {
                if ((csomopont.get(i).getCsomopont().getX() == tempcsp[kezdocsp][0])
                        && (csomopont.get(i).getCsomopont().getY() == tempcsp[kezdocsp][1])
                        && (csomopont.get(i).getCsomopont().getZ() == tempcsp[kezdocsp][2])) {
                    beiras = false;
                }
            }
        }
        if (beiras) {
            kezdocsomopont.getCsomopont().setCsomopont(csomopont.size() + 1);
            kezdocsomopont.getCsomopont().setX(tempcsp[kezdocsp][0]);
            kezdocsomopont.getCsomopont().setY(tempcsp[kezdocsp][1]);
            kezdocsomopont.getCsomopont().setZ(tempcsp[kezdocsp][2]);
            csomopont.add(kezdocsomopont);
            /*System.out.println("i:" + csomopont.size() + " Azon:" + csomopont.get(csomopont.size() - 1).getCsomopont().getCsomopont()
             + " x:" + csomopont.get(csomopont.size() - 1).getCsomopont().getX()
             + " y:" + csomopont.get(csomopont.size() - 1).getCsomopont().getY()
             + " z:" + csomopont.get(csomopont.size() - 1).getCsomopont().getZ());*/
        }
        beiras = true;
        vegecsomopont.setCsomopont(vegepont);
        vegecsomopont.getCsomopont().setAzonosito(nev);
        vegecsomopont.getCsomopont().setProjekt(projektnev);
        vegecsomopont.setSzekcio(szekcio);
        for (int i = 0; i < csomopont.size(); i++) {
            if ((csomopont.get(i).getCsomopont().getX() == tempcsp[vegecsp][0])
                    && (csomopont.get(i).getCsomopont().getY() == tempcsp[vegecsp][1])
                    && (csomopont.get(i).getCsomopont().getZ() == tempcsp[vegecsp][2])) {
                beiras = false;
            }
        }
        if (beiras) {
            vegecsomopont.getCsomopont().setCsomopont(csomopont.size() + 1);
            vegecsomopont.getCsomopont().setX(tempcsp[vegecsp][0]);
            vegecsomopont.getCsomopont().setY(tempcsp[vegecsp][1]);
            vegecsomopont.getCsomopont().setZ(tempcsp[vegecsp][2]);
            csomopont.add(vegecsomopont);
            /*System.out.println("i:" + csomopont.size() + " Azon:" + csomopont.get(csomopont.size() - 1).getCsomopont().getCsomopont()
             + " x:" + csomopont.get(csomopont.size() - 1).getCsomopont().getX()
             + " y:" + csomopont.get(csomopont.size() - 1).getCsomopont().getY()
             + " z:" + csomopont.get(csomopont.size() - 1).getCsomopont().getZ()); */
        }
    }

    public void rud_beiro(int szekcio, int kezdocsp, int vegecsp, int koz, int tipus) {
        boolean beiras = true;
        int csp1, csp2;
        Racstervezorud egyrud = new Racstervezorud();
        Rud egykisrud = new Rud();
        egyrud.setRud(egykisrud);
        egyrud.getRud().setProjekt(projektnev);
        egyrud.getRud().setAzonosito(nev);
        egyrud.setSzekcio(szekcio);
        egyrud.setKoz(koz);
        egyrud.setTipus(tipus);
        if (rud.size() > 0) {
            csp1 = 0;
            csp2 = 0;
            //System.out.println("Szekcio:"+szekcio+" kx:"+tempcsp[kezdocsp][0]+" ky:"+ tempcsp[kezdocsp][1]+" kz:"+ tempcsp[kezdocsp][2]+"  db:" +csomopont.size());
            for (int i = 0; i < csomopont.size(); i++) {
                /*System.out.println("i:"+i+" x:"+csomopont.get(i ).getCsomopont().getX()
                 +" y:"+ csomopont.get(i ).getCsomopont().getY()
                 +" z:"+ csomopont.get(i ).getCsomopont().getZ());    */
                if ((csomopont.get(i).getCsomopont().getX() == tempcsp[kezdocsp][0])
                        && (csomopont.get(i).getCsomopont().getY() == tempcsp[kezdocsp][1])
                        && (csomopont.get(i).getCsomopont().getZ() == tempcsp[kezdocsp][2])) {
                    csp1 = i;
                }
            }
            //System.out.println("vx:"+tempcsp[vegecsp][0]+" vy:"+ tempcsp[vegecsp][1]+" vz:"+ tempcsp[vegecsp][2]);
            for (int i = 0; i < csomopont.size(); i++) {
                if ((csomopont.get(i).getCsomopont().getX() == tempcsp[vegecsp][0])
                        && (csomopont.get(i).getCsomopont().getY() == tempcsp[vegecsp][1])
                        && (csomopont.get(i).getCsomopont().getZ() == tempcsp[vegecsp][2])) {
                    csp2 = i;
                }
            }
            for (int i = 0; i < rud.size(); i++) {
                if (((rud.get(i).getRud().getKezdocsp() == csp1) && (rud.get(i).getRud().getVegecsp() == csp2))
                        || ((rud.get(i).getRud().getKezdocsp() == csp2) && (rud.get(i).getRud().getVegecsp() == csp1))) {
                    beiras = false;
                }
            }
        } else {
            csp1 = 0;
            csp2 = 1;
        }
        //System.out.println("koz:"+koz+"  csp1:" + csp1+ "  csp2:" + csp2+"  kezdocsp:" + kezdocsp + "   vegecsp:" + vegecsp + "  beiras:" + beiras);
        if (beiras) {
            //egyrud.setHossz(rudhossz(csp1, csp2));
            egyrud.getRud().setKezdocsp(csp1);
            egyrud.getRud().setVegecsp(csp2);
            rud.add(egyrud);
            //System.out.println("koz:"+koz+"rudindex:" + rud.size() +"  szekcio:" + szekcio + " kezdocsp:" + rud.get(rud.size()-1).getRud().getKezdocsp() + "  vegecsp:" + rud.get(rud.size()-1).getRud().getVegecsp());
        }
    }

    public void szinbeallito(boolean forajz, int szekcio, int koz, boolean vastagvonal) {
        float arany;
        int tetel;
        int keret = 10;
        tetel = szekcio;
        if (tetel > 0) {
            tetel = 1;
        }
        if (tetel < 0) {
            tetel = 0;
        }
        // A kép méretarányának meghatározása
        if (tetel == 0) {
            if ((Teljesadatok.getMaxx() - Teljesadatok.getMinx()) > (Teljesadatok.getMaxy() - Teljesadatok.getMiny())) {
                arany = (float) (width - (2 * keret)) / (Teljesadatok.getMaxx() - Teljesadatok.getMinx());
            } else {
                arany = (float) (height - (2 * keret)) / (Teljesadatok.getMaxy() - Teljesadatok.getMiny());
            }
        } else {
            if ((Szekcioadatok.getMaxx() - Szekcioadatok.getMinx()) > (Szekcioadatok.getMaxy() - Szekcioadatok.getMiny())) {
                arany = (float) (width - (2 * keret)) / (Szekcioadatok.getMaxx() - Szekcioadatok.getMinx());
            } else {
                arany = (float) (height - (2 * keret)) / (Szekcioadatok.getMaxy() - Szekcioadatok.getMiny());
            }
        }
        // Az egérgörgetésnél a képarány megváltozik
        //System.out.println("Forajz:" + forajz + " tétel:" + tetel + " szekcio:" + szekcio + "  max:" + Teljesadatok.getMaxx() + "  min:" + Teljesadatok.getMinx());
        arany *= kepnagyitas[tetel];
        if (forajz) {
            // A főrajz
            for (int i = 0; i < rud.size(); i++) {
                // A vonalvastagságok  
                //System.out.println("i:" + i + " Vastag:" + rud.get(i).getRud().getVastagsag());
                rud.get(i).setTeljesvonal(new BasicStroke(1));
                if (rud.get(i).getRud().getVastagsag() != 0) {
                    //System.out.println("Vastag:" + vastagvonal + " i:" + i + "  Vastagsag:" + rud.get(i).getRud().getVastagsag() + "  arany:" + arany);
                    if (!vastagvonal) {
                        rud.get(i).setTeljesvonal(new BasicStroke(rud.get(i).getRud().getVastagsag() * arany));
                    }
                }
                // A Színbeállítások
                if (rud.get(i).getSzekcio() != szekcio) {
                    rud.get(i).setTeljesszin(Color.black);
                    /*if (rud.get(i).getRud().getVastagsag() != 0) {
                     // van vastagsága a rúdnak, de a vonalvastagság 1-től kisebb
                     if ((rud.get(i).getRud().getVastagsag() * arany) < 1) {
                     rud.get(i).setTeljesszin(new Color(256 - (int) (rud.get(i).getRud().getVastagsag() * arany * 255), 256
                     - (int) (rud.get(i).getRud().getVastagsag() * arany * 255), 256
                     - (int) (rud.get(i).getRud().getVastagsag() * arany * 255)));
                     }
                     }*/
                } else {
                    rud.get(i).setTeljesszin(Color.blue);
                    /*if (rud.get(i).getRud().getVastagsag() != 0) {
                     // van vastagsága a rúdnak, de a vonalvastagság 1-től kisebb
                     if ((rud.get(i).getRud().getVastagsag() * arany) < 1) {
                     // Ezt kékre hogy lehet alkalmazni?
                     rud.get(i).setTeljesszin(new Color(256 - (int) (rud.get(i).getRud().getVastagsag() * arany * 255), 256
                     - (int) (rud.get(i).getRud().getVastagsag() * arany * 255), 256
                     - (int) (rud.get(i).getRud().getVastagsag() * arany * 255)));
                     }
                     }*/
                }
            }
        } else {
            // A szekciórajz
            for (int i = 0; i < rud.size(); i++) {
                rud.get(i).setSzekciovonal(new BasicStroke(1));
                if (rud.get(i).getRud().getVastagsag() != 0) {
                    //System.out.println("Vastag:" + vastagvonal + " i:" + i + "  Vastagsag:" + rud.get(i).getRud().getVastagsag() + "  arany:" + arany);
                    if (!vastagvonal) {
                        rud.get(i).setSzekciovonal(new BasicStroke(rud.get(i).getRud().getVastagsag() * arany));
                    }
                }
                rud.get(i).setSzekcioszin(Color.black);
                if (rud.get(i).getSzekcio() == szekcio) {
                    if (rud.get(i).getKoz() == koz) {
                        if (rud.get(i).getKijelzes() == 1) {
                            rud.get(i).setSzekcioszin(Color.red);
                        } else {
                            rud.get(i).setSzekcioszin(Color.blue);
                        }
                    }
                }
            }
        }
    }

    public void vonalrajz(boolean rajztipus, boolean teljeskep, int xx1, int yy1, int xx2, int yy2, Color vonalszin) {
        float zz1, zz2, zmin, zmax;
        int reszek = 200;   // Ez max. 255 lehet!
        int red = vonalszin.getRed();
        int green = vonalszin.getGreen();
        int blue = vonalszin.getBlue();
        int red1, green1, blue1;    // Az új színek
        if (teljeskep) {
            if (Teljesadatok.getKezdz() < Teljesadatok.getVegz()) {
                zz1 = Teljesadatok.getKezdz();
                zz2 = Teljesadatok.getVegz();
            } else {
                zz1 = Teljesadatok.getVegz();
                zz2 = Teljesadatok.getKezdz();
            }
            zmin = Teljesadatok.getVirtual_minz();
            zmax = Teljesadatok.getVirtual_maxz();
        } else {
            if (Szekcioadatok.getKezdz() < Szekcioadatok.getVegz()) {
                zz1 = Szekcioadatok.getKezdz();
                zz2 = Szekcioadatok.getVegz();
            } else {
                zz1 = Szekcioadatok.getVegz();
                zz2 = Szekcioadatok.getKezdz();
            }
            zmin = Szekcioadatok.getVirtual_minz();
            zmax = Szekcioadatok.getVirtual_maxz();
        }
        if (rajztipus) {
            // A normál egyszínű rajz
            if (teljeskep) {
                g2.setColor(vonalszin);
                g2.drawLine(xx1, yy1, xx2, yy2);

            } else {
                g1.setColor(vonalszin);
                g1.drawLine(xx1, yy1, xx2, yy2);
            }
        } else {
            // A 'ködös' rajz
            int j = (int) (((zz2 - zz1) / (zmax - zmin)) * reszek);
            if (j > 255) {
                j = 255;
            }
            float leptekx = 0;
            float lepteky = 0;
            float szinalap = 1 - ((zmax - zz1) / (zmax - zmin));
            float szinleptek = 0;
            if (j != 0) {
                leptekx = ((float) (xx2 - xx1)) / j;
                lepteky = ((float) (yy2 - yy1)) / j;
                szinleptek = ((zz2 - zz1) / (zmax - zmin)) / j;
            }

            float szinszorzo;
            //System.out.println("zz1:" + zz1 + " zz2:" + zz2 + " zmin:" + zmin + " j:" + j + " xx1:" + xx1 + " xx2:" + xx2 + " yy1:" + yy1 + " yy2:" + yy2 + " r:" + red + " g:" + green + " b:" + blue + " szinalap:" + szinalap + "  szinleptek:" + szinleptek);
            for (int i = 0; i < j; i++) {
                szinszorzo = szinalap + i * szinleptek;
                if (szinszorzo > 1) {
                    szinszorzo = 1;
                }

                if ((red == 0) && (green == 0) && (blue == 0)) {
                    red1 = reszek - (int) (reszek * szinszorzo);
                    blue1 = reszek - (int) (reszek * szinszorzo);
                    green1 = reszek - (int) (reszek * szinszorzo);
                    if (red1 > 256) {
                        red1 = 256;
                        green1 = 256;
                        blue1 = 256;
                    }
                } else {
                    red1 = red;
                    blue1 = blue;
                    green1 = green;
                    if (red != 255) {
                        red1 = reszek - (int) (reszek * szinszorzo);
                    }
                    if (green != 255) {
                        green1 = reszek - (int) (reszek * szinszorzo);
                    }
                    if (blue != 255) {
                        blue1 = reszek - (int) (reszek * szinszorzo);
                    }
                    /*red1 = (int) (red * szinszorzo);
                     blue1 = (int) (blue * szinszorzo);
                     green1 = (int) (green * szinszorzo);*/
                }

                if (teljeskep) {
                    g2.setColor(new Color(red1, green1, blue1));
                    g2.drawLine((int) (xx1 + i * leptekx), (int) (yy1 + i * lepteky), (int) (xx1 + (i + 1) * leptekx), (int) (yy1 + (i + 1) * lepteky));
                } else {
                    g1.setColor(new Color(red1, green1, blue1));
                    g1.drawLine((int) (xx1 + i * leptekx), (int) (yy1 + i * lepteky), (int) (xx1 + (i + 1) * leptekx), (int) (yy1 + (i + 1) * lepteky));
                }
            }
        }
    }

    public void pngrajz(int szekcio, boolean vastagvonal, int koz) {
        // -1 -teljes rajz, a többi: szekciórajz
        int keret = 10;
        float arany;
        int xx1, xx2, yy1, yy2;
        int tetel;
        /*Stroke[] vonal = new BasicStroke[rud.size()];
         Color[] szinek = new Color[rud.size()];*/
        //rajztipus = false;
        Font Courier16b = new Font("Courier New", Font.BOLD, 16);
        Font Courier24b = new Font("Courier New", Font.BOLD, 24);
        Font Courier10 = new Font("Courier New", Font.PLAIN, 10);
        if (szekcio == -1) {
            g2.setColor(Color.white);
            g2.fillRect(0, 0, width, height);
        } else {
            g1.setColor(Color.white);
            g1.fillRect(0, 0, width, height);
        }
        kozeppont_szamolo(szekcio);
        tetel = 1;
        if (szekcio == -1) {
            tetel = 0;
        }
        // A drótváz maximum és minimum értékei  [teljes(0)/szekcio(a többi)], [x(0),y(1),z(2)], [minimum(0)/maximum(1)] 
        // limitek[tetel][0][1] --> maxx
        // limitek[tetel][0][0] --> minx
        // limitek[tetel][1][1] --> maxy
        // limitek[tetel][1][0] --> miny

        // A kép méretarányának meghatározása
        if (szekcio == -1) {
            if ((Teljesadatok.getMaxx() - Teljesadatok.getMinx()) > (Teljesadatok.getMaxy() - Teljesadatok.getMiny())) {
                arany = (float) (width - (2 * keret)) / (Teljesadatok.getMaxx() - Teljesadatok.getMinx());
            } else {
                arany = (float) (height - (2 * keret)) / (Teljesadatok.getMaxy() - Teljesadatok.getMiny());
            }
        } else {
            if ((Szekcioadatok.getMaxx() - Szekcioadatok.getMinx()) > (Szekcioadatok.getMaxy() - Szekcioadatok.getMiny())) {
                arany = (float) (width - (2 * keret)) / (Szekcioadatok.getMaxx() - Szekcioadatok.getMinx());
            } else {
                arany = (float) (height - (2 * keret)) / (Szekcioadatok.getMaxy() - Szekcioadatok.getMiny());
            }
        }
        // Az egérgörgetésnél a képarány megváltozik
        arany *= kepnagyitas[tetel];
        //System.out.println("Szekcio:"+szekcio+" arány:"+arany);
        //System.out.println("Csomopont:"+csomopont.size()+"  rudak:"+rud.size());
        //System.out.println("arany:" + arany + " height:" + height + " keret:" + keret + " maxx:" + limitek[tetel][0][1] + "  minx:" + limitek[tetel][0][0] + " maxy:" + limitek[tetel][1][1] + "  miny:" + limitek[tetel][1][0]);
        // A Z-irányú maximum és minimum keresése
        for (int i = 0; i < rud.size(); i++) {
            x = csomopont.get(rud.get(i).getRud().getKezdocsp()).getCsomopont().getX();
            y = csomopont.get(rud.get(i).getRud().getKezdocsp()).getCsomopont().getY();
            z = csomopont.get(rud.get(i).getRud().getKezdocsp()).getCsomopont().getZ();

            if (szekcio != -1) {
                x -= Szekcioadatok.getMinx();
                y -= Szekcioadatok.getMiny();
                z -= Szekcioadatok.getMinz();
            }
            pontforgato(1, tetel);
            pontforgato(2, tetel);
            pontforgato(3, tetel);
            if (szekcio == -1) {
                if (z > Teljesadatok.getVirtual_maxz()) {
                    Teljesadatok.setVirtual_maxz(z);
                }
                if (z < Teljesadatok.getVirtual_minz()) {
                    Teljesadatok.setVirtual_minz(z);
                }
            } else {
                if (z > Szekcioadatok.getVirtual_maxz()) {
                    Szekcioadatok.setVirtual_maxz(z);
                }
                if (z < Szekcioadatok.getVirtual_minz()) {
                    Szekcioadatok.setVirtual_minz(z);
                }
            }
            x = csomopont.get(rud.get(i).getRud().getVegecsp()).getCsomopont().getX();
            y = csomopont.get(rud.get(i).getRud().getVegecsp()).getCsomopont().getY();
            z = csomopont.get(rud.get(i).getRud().getVegecsp()).getCsomopont().getZ();
            if (szekcio != -1) {
                x -= Szekcioadatok.getMinx();
                y -= Szekcioadatok.getMiny();
                z -= Szekcioadatok.getMinz();
            }
            pontforgato(1, tetel);
            pontforgato(2, tetel);
            pontforgato(3, tetel);
            if (szekcio == -1) {
                if (z > Teljesadatok.getVirtual_maxz()) {
                    Teljesadatok.setVirtual_maxz(z);
                }
                if (z < Teljesadatok.getVirtual_minz()) {
                    Teljesadatok.setVirtual_minz(z);
                }
            } else {
                if (z > Szekcioadatok.getVirtual_maxz()) {
                    Szekcioadatok.setVirtual_maxz(z);
                }
                if (z < Szekcioadatok.getVirtual_minz()) {
                    Szekcioadatok.setVirtual_minz(z);
                }
            }
        }
        //System.out.println("Teljmaxz:"+Teljesadatok.getMaxz()+"   Teljminz:"+Teljesadatok.getMinz()+"   Szekcmaxz:"+Szekcioadatok.getMaxz()+"   Szekczminz:"+Szekcioadatok.getMinz());
        // A rajz           
        for (int i = 0; i < rud.size(); i++) {
            // A teljes rajz szín- és vastagságbeállításai
            //System.out.println("i:"+i+" vonal:"+rud.get(i).getVonal());
            g2.setStroke(rud.get(i).getTeljesvonal());
            //g2.setColor(rud.get(i).getTeljesszin());
            // A szekciórajz szín- és vastagságbeállításai
            g1.setStroke(rud.get(i).getSzekciovonal());
            //g1.setColor(rud.get(i).getSzekcioszin());
            // Elölnézeti forgatás X-Y
            //System.out.println("i:" + (i ) + " Kezdocsp:" + rud.get(i ).getRud().getKezdocsp());
            x = csomopont.get(rud.get(i).getRud().getKezdocsp()).getCsomopont().getX();
            y = csomopont.get(rud.get(i).getRud().getKezdocsp()).getCsomopont().getY();
            z = csomopont.get(rud.get(i).getRud().getKezdocsp()).getCsomopont().getZ();

            if (szekcio != -1) {
                x -= Szekcioadatok.getMinx();
                y -= Szekcioadatok.getMiny();
                z -= Szekcioadatok.getMinz();
            }
            pontforgato(1, tetel);
            pontforgato(2, tetel);
            pontforgato(3, tetel);
            if (szekcio == -1) {
                Teljesadatok.setKezdz(z);
            } else {
                Szekcioadatok.setKezdz(z);
            }
            //System.out.print("i:"+i+"  z1:"+z);
            xx1 = (int) ((x - kozepx[tetel]) * arany) + width / 2;
            yy1 = height - ((int) ((y - kozepy[tetel]) * arany) + height / 2);
            xx1 += kepkozep[tetel][0];
            yy1 += kepkozep[tetel][1];
            //System.out.println("i:" + (i ) + " Vegecsp:" + rud.get(i ).getRud().getVegecsp()+" cs:"+csomopont.size());
            x = csomopont.get(rud.get(i).getRud().getVegecsp()).getCsomopont().getX();
            y = csomopont.get(rud.get(i).getRud().getVegecsp()).getCsomopont().getY();
            z = csomopont.get(rud.get(i).getRud().getVegecsp()).getCsomopont().getZ();
            if (szekcio != -1) {
                x -= Szekcioadatok.getMinx();
                y -= Szekcioadatok.getMiny();
                z -= Szekcioadatok.getMinz();
            }
            pontforgato(1, tetel);
            pontforgato(2, tetel);
            pontforgato(3, tetel);
            if (szekcio == -1) {
                Teljesadatok.setVegz(z);
            } else {
                Szekcioadatok.setVegz(z);
            }
            //System.out.println("  z2:"+z);
            xx2 = (int) ((x - kozepx[tetel]) * arany) + width / 2;
            yy2 = height - ((int) ((y - kozepy[tetel]) * arany) + height / 2);
            xx2 += kepkozep[tetel][0];
            yy2 += kepkozep[tetel][1];
            if (szekcio == -1) {
                vonalrajz(rajztipus, true, xx1, yy1, xx2, yy2, rud.get(i).getTeljesszin());
                if (!vastagvonal) {
                    g2.fillOval(xx1 - 2, yy1 - 2, 4, 4);
                }
            } else {
                if (rud.get(i).getSzekcio() == szekcio) {
                    vonalrajz(rajztipus, false, xx1, yy1, xx2, yy2, rud.get(i).getSzekcioszin());
                    if (!vastagvonal) {
                        g1.fillOval(xx1 - 2, yy1 - 2, 4, 4);
                        g1.setColor(Color.magenta);
                        g1.setFont(Courier10);
                        g1.drawString(String.valueOf(rud.get(i).getRud().getKezdocsp()), xx1 + 3, yy1);
                        g1.drawString(String.valueOf(rud.get(i).getRud().getVegecsp()), xx2 + 3, yy2);
                    } else {
                        if (csomopont.get(rud.get(i).getRud().getKezdocsp()).getKijelzes() == 1) {
                            g1.setColor(Color.red);
                            g1.setFont(Courier24b);
                            g1.drawString(String.valueOf(rud.get(i).getRud().getKezdocsp()), xx1 + 5, yy1);
                        }
                        if (csomopont.get(rud.get(i).getRud().getVegecsp()).getKijelzes() == 1) {
                            g1.setColor(Color.red);
                            g1.setFont(Courier24b);
                            g1.drawString(String.valueOf(rud.get(i).getRud().getVegecsp()), xx2 + 5, yy2);
                        }
                    }
                }
            }
        }
        if (szekcio == -1) {
            g2.setColor(Color.black);
            g2.setFont(Courier16b);
            g2.drawString(nev, 5, 15);
        } else {
            g1.setColor(Color.black);
            g1.setFont(Courier16b);
            g1.drawString(nev, 5, 15);
        }
    }

    public float rudhossz(int kezdocsp, int vegecsp) {
        //System.out.println("kezdocsp:" + kezdocsp + "  vegecsp:" + vegecsp + " csomopont:" + csomopont.size());
        return Math.abs(csomopont.get(kezdocsp).getCsomopont().getX() - csomopont.get(vegecsp).getCsomopont().getX())
                + Math.abs(csomopont.get(kezdocsp).getCsomopont().getY() - csomopont.get(vegecsp).getCsomopont().getY())
                + Math.abs(csomopont.get(kezdocsp).getCsomopont().getZ() - csomopont.get(vegecsp).getCsomopont().getZ());
    }

    public void csomopont_kereso(int pontsorszam) {
        // X,y,z koordinátákat keres, és az adat-ban lesz a legközelebbi csomópont
        float tavolsag = Float.MAX_VALUE;
        adat = 1;
        for (int k = 0; k < csomopont.size(); k++) {
            if (k != pontsorszam) {
                if (rudhossz(pontsorszam, k) < tavolsag) {
                    adat = k;
                    tavolsag = rudhossz(pontsorszam, k);
                }
            }
        }
        // System.out.println("  csatolopont:" + x1);
    }

    public void povray_fileiro() {
        float minx = 0, miny = 0, minz = 0;
        try {
            PrintWriter writer = new PrintWriter("adatok.inc", "UTF-8");
            // #declare rudak = 395;
            writer.print("#declare rudak = ");
            writer.print(rud.size());
            writer.println(";");
            //#declare csomopontok = 219;
            writer.print("#declare csomopontok = ");
            writer.print(csomopont.size());
            writer.println(";");
            /*#declare csomopont = array[csomopontok][3] {
            {4238,0,0},  */
            writer.println("#declare rud = array[rudak][3] {");
            for (int i = 0; i < rud.size(); i++) {
                writer.print("{");
                writer.print(rud.get(i).getRud().getKezdocsp());
                writer.print(",");
                writer.print(rud.get(i).getRud().getVegecsp());
                writer.print(",");
                if (rud.get(i).getRud().getVastagsag() == 0) {
                    writer.print("10");
                } else {
                    writer.print(rud.get(i).getRud().getVastagsag());
                }
                writer.print("}");
                if (i < (rud.size() - 1)) {
                    writer.println(",");
                } else {
                    writer.println();
                }
            }
            writer.println("}");
            /*
            #declare rud = array[rudak][3] {
            {1,113,20},
             */
            // A minimumok kikeresése
            for (int i = 0; i < csomopont.size(); i++) {
                if (csomopont.get(i).getCsomopont().getX() < minx) {
                    minx = csomopont.get(i).getCsomopont().getX();
                }
                if (csomopont.get(i).getCsomopont().getY() < miny) {
                    miny = csomopont.get(i).getCsomopont().getY();
                }
                if (csomopont.get(i).getCsomopont().getZ() < minz) {
                    minz = csomopont.get(i).getCsomopont().getZ();
                }
            }
            writer.println("#declare csomopont = array[csomopontok][3] {");
            for (int i = 0; i < csomopont.size(); i++) {
                writer.print("{");
                writer.print(csomopont.get(i).getCsomopont().getX() - minx);
                writer.print(",");
                writer.print(csomopont.get(i).getCsomopont().getY() - miny);
                writer.print(",");
                writer.print(csomopont.get(i).getCsomopont().getZ() - minz);
                writer.print("}");
                if (i < (csomopont.size() - 1)) {
                    writer.println(",");
                } else {
                    writer.println();
                }
            }
            writer.println("}");
            writer.close();
        } catch (IOException e) {
            // do something
        }
    }

    public void sql_file_export() {

        try {
            Class.forName("com.mysql.jdbc.Driver").newInstance();
            Connection co = DriverManager.getConnection(Global.mysql_server, Global.mysql_user, Global.mysql_password);
            Statement st = co.createStatement();
            // Adattörlés
            parancs = "delete from csomopont where projekt = 'Tervezett vázszerkezet' and azonosito = '" + nev + "';";
            //System.out.println(parancs);
            st.execute(parancs);
            System.out.println("\"projekt\",\"azonosito\",\"csomopont\",\"x\",\"y\",\"z\"");
            for (int i = 0; i < csomopont.size(); i++) {
                parancs = "INSERT INTO csomopont (projekt,azonosito,csomopont,x,y,z) values (";
                parancs = parancs + "'" + csomopont.get(i).getCsomopont().getProjekt() + "',";
                parancs = parancs + "'" + csomopont.get(i).getCsomopont().getAzonosito() + "',";
                parancs = parancs + "'" + csomopont.get(i).getCsomopont().getCsomopont() + "',";
                parancs = parancs + "'" + csomopont.get(i).getCsomopont().getX() + "',";
                parancs = parancs + "'" + csomopont.get(i).getCsomopont().getY() + "',";
                parancs = parancs + "'" + csomopont.get(i).getCsomopont().getZ() + "');";
                //System.out.println(parancs);
                st.execute(parancs);
                System.out.println("\"" + csomopont.get(i).getCsomopont().getProjekt()
                        + "\",\"" + csomopont.get(i).getCsomopont().getAzonosito()
                        + "\",\"" + csomopont.get(i).getCsomopont().getCsomopont()
                        + "\",\"" + csomopont.get(i).getCsomopont().getX()
                        + "\",\"" + csomopont.get(i).getCsomopont().getY()
                        + "\",\"" + csomopont.get(i).getCsomopont().getZ() + "\"");
            }
            System.out.println();
            // Adattörlés
            parancs = "delete from rud where projekt = 'Tervezett vázszerkezet' and azonosito = '" + nev + "';";
            //System.out.println(parancs);
            st.execute(parancs);
            System.out.println("\"projekt\",\"azonosito\",\"kezdocsp\",\"vegecsp\",\"vastagsag\",\"piros\",\"zold\",\"kek\"");
            for (int i = 0; i < rud.size(); i++) {
                parancs = "INSERT INTO rud (projekt,azonosito,kezdocsp,vegecsp,anyag,vastagsag,piros,zold,kek,szelveny) values (";
                parancs = parancs + "'" + rud.get(i).getRud().getProjekt() + "',";
                parancs = parancs + "'" + rud.get(i).getRud().getAzonosito() + "',";
                parancs = parancs + "'" + (rud.get(i).getRud().getKezdocsp() + 1) + "',";
                parancs = parancs + "'" + (rud.get(i).getRud().getVegecsp() + 1) + "',";
                parancs = parancs + "'" + rud.get(i).getRud().getAnyag() + "',";
                parancs = parancs + "'" + rud.get(i).getRud().getVastagsag() + "',";
                parancs = parancs + "'" + rud.get(i).getRud().getPiros() + "',";
                parancs = parancs + "'" + rud.get(i).getRud().getZold() + "',";
                parancs = parancs + "'" + rud.get(i).getRud().getKek() + "',";
                parancs = parancs + "'" + rud.get(i).getRud().getSzelveny() + "');";
                //System.out.println(parancs);
                st.execute(parancs);
                System.out.println("\"" + rud.get(i).getRud().getProjekt()
                        + "\",\"" + rud.get(i).getRud().getAzonosito()
                        + // "\",\"" + rud.get(i).getRud().getId() + 
                        "\",\"" + (rud.get(i).getRud().getKezdocsp() + 1)
                        + "\",\"" + (rud.get(i).getRud().getVegecsp() + 1)
                        + "\",\"" + rud.get(i).getRud().getVastagsag()
                        + "\",\"" + rud.get(i).getRud().getPiros()
                        + "\",\"" + rud.get(i).getRud().getZold()
                        + "\",\"" + rud.get(i).getRud().getKek() + "\"");
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
