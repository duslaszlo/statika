/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package statika_awt;

import java.applet.Applet;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

/**
 *
 * @author SD-LEAP
 */
public class kettamaszu_regi extends Applet implements KeyListener, MouseListener {

    int x, y, x1, y1, x2, y2;
    int dist;  // A kiválasztásnál az éppen aktuális távolság
    float arany;   // A kijelzés aránya
    int hossz;  // A kéttámaszú tartó hossza
    int nyil_db; // A koncentrált erőknek a száma
    int[][] nyil_koord = new int[100][2]; // A felrakott erők koordinátája, értéke
    int akt_nyil; // Az aktuális erő indexe
    int megoszlo_db; // A megoszló terhek a száma
    int[][] megoszlo_koord = new int[100][3]; // A megoszló terhek kezdő koordinátája, hossza, értéke
    int akt_megoszlo; // Az aktuális megoszló teher indexe
    int metszek = 270;  // A metszekek szama (300-30...)
    float[] nyiroero = new float[metszek];  // Az aktuális pont nyíróerőértéke  
    float[] nyomatek = new float[metszek];  // Az aktuális pont nyomatéki értéke 
    String szoveg;
    int menu = 1;
    int maxnyomatek_hely, maxnyiroero_hely;

    /**
     * Initialization method that will be called after the applet is loaded
     * into the browser.
     */
    public void paint(Graphics g) {
        float fa, fb, f;  // A támaszerők
        int bazis;
        // A kéttámaszú tartó ábrája                
        g.setColor(Color.black);
        // A gerenda
        bazis = 42;
        g.drawLine(30, bazis, 300, bazis);
        g.drawLine(30, bazis - 1, 300, bazis - 1);
        // A mozgó talp
        g.drawLine(30, bazis, 25, bazis + 5);
        g.drawLine(30, bazis, 35, bazis + 5);
        g.drawLine(25, bazis + 5, 35, bazis + 5);
        g.drawLine(20, bazis + 9, 40, bazis + 9);
        g.drawLine(20, bazis + 8, 40, bazis + 8);
        // A fix talp
        g.drawLine(300, bazis, 295, bazis + 7);
        g.drawLine(300, bazis, 305, bazis + 7);
        g.drawLine(295, bazis + 7, 305, bazis + 7);
        g.drawLine(290, bazis + 7, 310, bazis + 7);
        g.drawLine(290, bazis + 8, 310, bazis + 8);
        // A méretvonal
        g.setColor(Color.LIGHT_GRAY);
        g.drawLine(30, bazis + 10, 30, 230);
        g.drawLine(300, bazis + 10, 300, 230);
        g.drawLine(25, bazis + 15, 305, bazis + 15);
        g.setColor(Color.black);
        g.drawLine(33, bazis + 13, 27, bazis + 17);
        g.drawLine(303, bazis + 13, 297, bazis + 17);

        szoveg = "l=" + String.valueOf(hossz);
        //g.setColor(Color.green);
        g.drawString(szoveg, 150, bazis + 13);
        // A "Menürendszer"
        g.setColor(Color.black);
        g.drawString("Koncentrált erő", 320, 20);
        if (menu == 1) {
            g.setColor(Color.green);
        } else {
            g.setColor(Color.black);
        }
        g.drawString("- Pozitív erő", 330, 35);
        if (menu == 2) {
            g.setColor(Color.green);
        } else {
            g.setColor(Color.black);
        }
        g.drawString("- Negatív erő", 330, 50);
        if (menu == 3) {
            g.setColor(Color.green);
        } else {
            g.setColor(Color.black);
        }
        g.drawString("- Módosít", 330, 65);
        if (menu == 4) {
            g.setColor(Color.green);
        } else {
            g.setColor(Color.black);
        }
        g.drawString("- Levesz", 330, 80);
        g.setColor(Color.black);
        g.drawString("Megoszló teher", 320, 95);
        if (menu == 5) {
            g.setColor(Color.green);
        } else {
            g.setColor(Color.black);
        }
        g.drawString("- Pozitív teher", 330, 110);
        if (menu == 6) {
            g.setColor(Color.green);
        } else {
            g.setColor(Color.black);
        }
        g.drawString("- Negatív teher", 330, 125);
        if (menu == 7) {
            g.setColor(Color.green);
        } else {
            g.setColor(Color.black);
        }
        g.drawString("- Módosít", 330, 140);
        if (menu == 8) {
            g.setColor(Color.green);
        } else {
            g.setColor(Color.black);
        }
        g.drawString("- Levesz", 330, 155);
        /*
        g.setColor(Color.black);
        g.drawString("Nyomaték", 320, 170);
        if (menu == 9) {
            g.setColor(Color.green);
        } else {
            g.setColor(Color.black);
        }
        g.drawString("- Pozitív nyomaték", 330, 185);
        if (menu == 10) {
            g.setColor(Color.green);
        } else {
            g.setColor(Color.black);
        }
        g.drawString("- Negatív nyomaték", 330, 200);
        if (menu == 11) {
            g.setColor(Color.green);
        } else {
            g.setColor(Color.black);
        }
        g.drawString("- Módosít", 330, 215);
        if (menu == 12) {
            g.setColor(Color.green);
        } else {
            g.setColor(Color.black);
        }
        g.drawString("- Levesz", 330, 230);
        g.setColor(Color.black);
        g.drawString("SVG kimenet", 320, 245); */
        if ((menu == 3) || (menu == 7) || (menu == 11)) {
            g.setColor(Color.green);
            g.drawString("+", 320, 260);
            g.drawString("-", 340, 260);
            g.drawString("<-", 360, 260);
            g.drawString("->", 380, 260);
        }
        if (menu == 7) {
            g.drawString("Hossz+", 320, 275);
            g.drawString("Hossz-", 370, 275);
        }

        // A koncentrált erő nyilainak kijelzése
        bazis = 42;
        if (nyil_db > 0) {
            for (int i = 1; i <= nyil_db; i++) {
                if (i == akt_nyil && menu == 3) {
                    g.setColor(Color.cyan);
                } else {
                    g.setColor(Color.blue);
                }
                g.drawLine(nyil_koord[i][0] + 30, bazis - 18, nyil_koord[i][0] + 30, bazis - 2);
                for (int j = 1; j <= 10; j++) {
                    if (nyil_koord[i][1] > 0) {
                        g.drawLine(nyil_koord[i][0] + 30, bazis - 2, nyil_koord[i][0] + 24 + j, bazis - 15);
                    } else {
                        g.drawLine(nyil_koord[i][0] + 30, bazis - 18, nyil_koord[i][0] + 24 + j, bazis - 7);
                    }
                }
                szoveg = String.valueOf(nyil_koord[i][1]);
                g.drawString(szoveg, nyil_koord[i][0] + 35, bazis - 15);
            }
        }

        // A megoszló terhelések kijelzése
        bazis = 22;
        if (megoszlo_db > 0) {
            for (int i = 1; i <= megoszlo_db; i++) {
                if (i == akt_megoszlo) {
                    g.setColor(Color.cyan);
                } else {
                    g.setColor(Color.blue);
                }
                g.drawRect(megoszlo_koord[i][0] + 30, 2, megoszlo_koord[i][1], 18);

                g.drawLine(megoszlo_koord[i][0] + 35, bazis - 4, megoszlo_koord[i][0] + 35, bazis - 18);

                for (int j = 1; j <= 8; j++) {
                    if (megoszlo_koord[i][2] > 0) {
                        g.drawLine(megoszlo_koord[i][0] + 35, bazis - 4, megoszlo_koord[i][0] + 31 + j, bazis - 14);
                    } else {
                        g.drawLine(megoszlo_koord[i][0] + 35, bazis - 18, megoszlo_koord[i][0] + 31 + j, bazis - 8);
                    }
                }
                szoveg = "q=" + String.valueOf(megoszlo_koord[i][2]);
                g.drawString(szoveg, megoszlo_koord[i][0] + 40, bazis - 4);
            }
        }

        // A nyíróerőábra
        bazis = 90;
        for (int i = 0; i < metszek; i++) {
            nyiroero[i] = 0;
        }
        g.setColor(Color.black);
        g.drawString("T", 12, bazis + 2);
        g.drawString("+", 22, bazis - 5);
        g.drawString("-", 22, bazis + 10);
        g.setColor(Color.LIGHT_GRAY);
        g.drawLine(25, bazis, 305, bazis);
        if ((nyil_db > 0) || (megoszlo_db > 0)) {
            // A támaszerők és nyíróerőmetszékek a koncentrált erőkből
            if (nyil_db > 0) {
                for (int i = 1; i <= nyil_db; i++) {
                    f = Float.parseFloat(String.valueOf(nyil_koord[i][0]));
                    fa = Float.parseFloat(String.valueOf(hossz));
                    fb = (f / fa) * Float.parseFloat(String.valueOf(nyil_koord[i][1]));
                    f = Float.parseFloat(String.valueOf(nyil_koord[i][1]));
                    fa = f - fb;
                    for (int j = 0; j < metszek; j++) {
                        if (j < nyil_koord[i][0]) {
                            nyiroero[j] = nyiroero[j] - fa;
                        } else {
                            nyiroero[j] = nyiroero[j] + fb;
                        }
                    }
                }
            }
            // A támaszerők és nyíróerőmetszékek a megoszló terhelésekből
            if (megoszlo_db > 0) {
                for (int i = 1; i <= megoszlo_db; i++) {
                    f = Float.parseFloat(String.valueOf(megoszlo_koord[i][0])) + (Float.parseFloat(String.valueOf(megoszlo_koord[i][1]))) / 2;
                    fa = Float.parseFloat(String.valueOf(hossz));
                    fb = (f / fa) * Float.parseFloat(String.valueOf(megoszlo_koord[i][2] * megoszlo_koord[i][1]));
                    f = Float.parseFloat(String.valueOf(megoszlo_koord[i][2] * megoszlo_koord[i][1]));
                    fa = f - fb;
                    for (int j = 0; j < metszek; j++) {
                        if (j <= megoszlo_koord[i][0]) {
                            nyiroero[j] = nyiroero[j] - fa;
                        } else if (j >= megoszlo_koord[i][0] && j < megoszlo_koord[i][0] + megoszlo_koord[i][1]) {
                            nyiroero[j] = nyiroero[j] - fa + (j - megoszlo_koord[i][0] + 1) * megoszlo_koord[i][2];
                        } else {
                            nyiroero[j] = nyiroero[j] + fb;
                        }
                    }
                }
            }
            // A Maximális nyíróerő meghatározása
            f = 0;
            x1 = 0;  // Ez jelzi majd a negatív nyomatékot
            for (int i = 0; i < metszek; i++) {
                if (nyiroero[i] > f) {
                    f = nyiroero[i];
                    maxnyiroero_hely = i;
                    x1 = 1;
                }
            }
            for (int i = 0; i < metszek; i++) {
                if (-nyiroero[i] > f) {
                    f = -nyiroero[i];
                    maxnyiroero_hely = i;
                    x1 = 0;
                }
            }
            // Kijelzés
            g.setColor(Color.MAGENTA);
            for (int i = 0; i < metszek; i++) {
                if (nyiroero[i] == 0) {
                    y1 = 0;
                } else {
                    arany = nyiroero[i] * (30 / f);
                    y1 = (int) arany;
                }
                g.drawLine(i + 30, bazis, i + 30, bazis + (int) arany);
            }
            // A maximális nyíróerő helye és mértéke
            g.setColor(Color.green);
            if (x1 == 0) {
                g.drawLine(maxnyiroero_hely + 30, bazis, maxnyiroero_hely + 30, bazis - 30);
                g.drawLine(maxnyiroero_hely + 30, bazis - 15, maxnyiroero_hely + 50, bazis + 47);
            } else {
                g.drawLine(maxnyiroero_hely + 30, bazis, maxnyiroero_hely + 30, bazis + 30);
                g.drawLine(maxnyiroero_hely + 30, bazis + 15, maxnyiroero_hely + 50, bazis + 47);
            }
            g.drawLine(maxnyiroero_hely + 50, bazis + 47, maxnyiroero_hely + 150, bazis + 47);
            szoveg = "T_max=" + String.valueOf(f);
            g.setColor(Color.red);
            g.drawString(szoveg, maxnyiroero_hely + 50, bazis + 45);
            g.setColor(Color.LIGHT_GRAY);
            g.drawLine(27, bazis + 47, maxnyiroero_hely + 33, bazis + 47);
            g.drawLine(maxnyiroero_hely + 30, bazis + 50, maxnyiroero_hely + 30, bazis + 44);
            g.setColor(Color.BLACK);
            g.drawLine(27, bazis + 50, 33, bazis + 44);
            g.drawLine(maxnyiroero_hely + 27, bazis + 50, maxnyiroero_hely + 33, bazis + 44);
            szoveg = String.valueOf(maxnyiroero_hely);
            g.drawString(szoveg, (maxnyiroero_hely / 2) + 20, bazis + 45);
        }
        // A nyomatéki ábra
        bazis = 170;
        g.setColor(Color.black);
        g.drawString("M", 12, bazis + 2);
        g.drawString("+", 22, bazis - 5);
        g.drawString("-", 22, bazis + 10);
        g.setColor(Color.LIGHT_GRAY);
        g.drawLine(25, bazis, 305, bazis);
        // A nyomatéki értékek kiszámolása
        if ((nyil_db > 0) || (megoszlo_db > 0)) {
            nyomatek[0] = 0;
            for (int i = 1; i < metszek; i++) {
                nyomatek[i] = nyomatek[i - 1] - nyiroero[i];
            }
            // A Maximális nyomaték meghatározása
            f = 0;
            x1 = 0;  // Ez jelzi majd a negatív nyomatékot
            maxnyomatek_hely = 0;
            for (int i = 0; i < metszek; i++) {
                if (nyomatek[i] > f) {
                    f = nyomatek[i];
                    maxnyomatek_hely = i;
                    x1 = 1;
                }
            }
            for (int i = 0; i < metszek; i++) {
                if (-nyomatek[i] > f) {
                    f = -nyomatek[i];
                    maxnyomatek_hely = i;
                    x1 = 0;
                }
            }
            // Kijelzés                    
            g.setColor(Color.pink);
            for (int i = 0; i < metszek; i++) {
                if (nyomatek[i] != 0) {
                    arany = nyomatek[i] * (30 / f);
                }
                g.drawLine(i + 30, bazis, i + 30, bazis + (int) arany);
            }
            // A maximális nyomaték helye és mértéke
            g.setColor(Color.red);
            if (x1 == 0) {
                g.drawLine(maxnyomatek_hely + 30, bazis, maxnyomatek_hely + 30, bazis - 30);
                g.drawLine(maxnyomatek_hely + 30, bazis - 15, maxnyomatek_hely + 50, bazis + 47);
            } else {
                g.drawLine(maxnyomatek_hely + 30, bazis, maxnyomatek_hely + 30, bazis + 30);
                g.drawLine(maxnyomatek_hely + 30, bazis + 15, maxnyomatek_hely + 50, bazis + 47);
            }
            g.drawLine(maxnyomatek_hely + 50, bazis + 47, maxnyomatek_hely + 150, bazis + 47);
            szoveg = "M_max=" + String.valueOf(f);
            g.drawString(szoveg, maxnyomatek_hely + 50, bazis + 45);
            g.setColor(Color.LIGHT_GRAY);
            g.drawLine(27, bazis + 47, maxnyomatek_hely + 33, bazis + 47);
            g.drawLine(maxnyomatek_hely + 30, bazis + 50, maxnyomatek_hely + 30, bazis + 44);
            g.setColor(Color.BLACK);
            g.drawLine(27, bazis + 50, 33, bazis + 44);
            g.drawLine(maxnyomatek_hely + 27, bazis + 50, maxnyomatek_hely + 33, bazis + 44);
            szoveg = String.valueOf(maxnyomatek_hely);
            g.drawString(szoveg, (maxnyomatek_hely / 2) + 20, bazis + 45);
        }
    }

    public void init() {
        // TODO start asynchronous download of heavy resources
        addKeyListener(this);
        addMouseListener(this);
        hossz = 270;
        repaint();   // Ez a paint metódus meghívása...
    }

    public void start() {
        // TODO start asynchronous download of heavy resources
        repaint();   // Ez a paint metódus meghívása...
    }

    public void stop() {
        // TODO start asynchronous download of heavy resources

        repaint();   // Ez a paint metódus meghívása...
    }

    public void destroy() {
        // TODO start asynchronous download of heavy resources

        repaint();   // Ez a paint metódus meghívása...
    }

    public void keyTyped(KeyEvent e) {
    }

    public void keyPressed(KeyEvent e) {
        System.out.println(e.getKeyCode() + "  " + akt_nyil);
        // Ez valamiért nem megy...
        if (akt_nyil > 0) {
            System.out.println(e.getKeyCode());
            if (akt_nyil > 0) {
                x = nyil_koord[akt_nyil][0];
                y = nyil_koord[akt_nyil][1];
            }
            switch (e.getKeyCode()) {
                case 38: { // Felfelé, A kiválasztott teher növelése
                    if (y < 0) {
                        y--;
                    } else {
                        y++;
                    }
                    break;
                }
                case 40: { // Lefelé, a kiválasztott teher csökkentése
                    if (y < 0) {
                        y++;
                    } else {
                        y--;
                    }
                    if (y == 0) {
                        y = 1;
                    }
                    break;
                }
                case 37: { // Mozgatás balra
                    x--;
                    if (x == -1) {
                        x = 0;
                    }
                    break;
                }
                case 39: { // Mozgatás jobbra
                    x++;
                    if (x == hossz + 1) {
                        x = hossz;
                    }
                    break;
                }
                default: {
                }
            }
            if (akt_nyil > 0) {
                nyil_koord[akt_nyil][0] = x;
                nyil_koord[akt_nyil][1] = y;
            }
            repaint();
        }
        e.consume();
    }

    public void keyReleased(KeyEvent e) {
    }

    public void mouseClicked(MouseEvent e) {
    }

    public void mousePressed(MouseEvent e) {
        if (e.getX() > 0 && e.getX() < 300) {
            // Az aktuális koncentrált erő kiválasztása
            if ((menu == 3) || (menu == 4)) {
                akt_nyil = 0;
                akt_megoszlo = 0;
                if (nyil_db > 0) {
                    dist = hossz;
                    for (int i = 1; i <= nyil_db; i++) {
                        x1 = e.getX() - 30;
                        y1 = nyil_koord[i][0];
                        if (y1 > x1) {
                            x2 = y1;
                            y1 = x1;
                            x1 = x2;
                        }
                        if (dist > (x1 - y1)) {
                            dist = x1 - y1;
                            akt_nyil = i;
                        }
                        System.out.println("  akt_nyil:" + akt_nyil + " dist:" + dist);
                    }
                }
            }
            switch (menu) {
                case 1: {
                    nyil_db++;     // A pozitív koncentrált erő felrakása
                    if (nyil_db > 100) {
                        nyil_db = 100;
                    }
                    nyil_koord[nyil_db][0] = e.getX() - 30;     // A kordináta
                    nyil_koord[nyil_db][1] = 10;               // A kezdő erő   
                    akt_nyil = 0;
                    break;
                }
                case 2: {
                    nyil_db++;     // A negatív koncentrált erő felrakása
                    if (nyil_db > 100) {
                        nyil_db = 100;
                    }
                    nyil_koord[nyil_db][0] = e.getX() - 30;     // A kordináta
                    nyil_koord[nyil_db][1] = -10;               // A kezdő erő  
                    akt_nyil = 0;
                    break;
                }
                case 3: {  // A koncentrált erő módosítása
                    break;
                }
                case 4: {  // A koncentrált erő levétele
                    if (nyil_db > 0) {
                        x2 = -1;
                        for (int i = 0; i <= nyil_db; i++) {
                            if (i != akt_nyil) {
                                x2++;
                            }
                            //System.out.println("  i:"+i+"  X2:"+x2+"  akt_nyil:"+akt_nyil);
                            nyil_koord[x2][0] = nyil_koord[i][0];
                            nyil_koord[x2][1] = nyil_koord[i][1];
                        }
                        nyil_db--;
                        if (nyil_db < 0) {
                            nyil_db = 0;
                        }
                        System.out.println("  nyil_db:" + nyil_db + "  Akt_nyil:" + akt_nyil);
                    }
                    break;
                }
                case 5: {
                    megoszlo_db++;     // A pozitív megoszló teher felrakása
                    if (megoszlo_db > 100) {
                        megoszlo_db = 100;
                    }
                    megoszlo_koord[megoszlo_db][0] = e.getX() - 30;     // A kordináta
                    megoszlo_koord[megoszlo_db][1] = 30;              // A hossz
                    if (megoszlo_koord[megoszlo_db][0] + 30 > hossz) {
                        megoszlo_koord[megoszlo_db][0] = hossz - 30;
                    }
                    megoszlo_koord[megoszlo_db][2] = 1;               // A kezdő teher
                    break;
                }
                case 6: {
                    megoszlo_db++;     // A negatív megoszló teher felrakása
                    if (megoszlo_db > 100) {
                        megoszlo_db = 100;
                    }
                    megoszlo_koord[megoszlo_db][0] = e.getX() - 30;     // A kordináta
                    megoszlo_koord[megoszlo_db][1] = 30;              // A hossz
                    if (megoszlo_koord[megoszlo_db][0] + 30 > hossz) {
                        megoszlo_koord[megoszlo_db][0] = hossz - 30;
                    }
                    megoszlo_koord[megoszlo_db][2] = -1;               // A kezdő teher
                    break;
                }
                case 7: {  // A megoszló teher módosítása
                }
                case 8: {  // A megoszló teher levétele
                    if (megoszlo_db > 0) {
                        x2 = -1;
                        for (int i = 0; i <= megoszlo_db; i++) {
                            if (i != akt_megoszlo) {
                                x2++;
                            }
                            //System.out.println("  i:"+i+"  X2:"+x2+"  akt_nyil:"+akt_megoszlo);
                            megoszlo_koord[x2][0] = megoszlo_koord[i][0];
                            megoszlo_koord[x2][1] = megoszlo_koord[i][1];
                            megoszlo_koord[x2][2] = megoszlo_koord[i][2];
                        }
                        megoszlo_db--;
                        if (megoszlo_db < 0) {
                            megoszlo_db = 0;
                        }
                        //System.out.println("  megoszlo_db:"+megoszlo_db+"  Akt_megoszlo:"+akt_megoszlo);
                    }
                    break;
                }
                case 9: {  // A pozitív nyomaték felvétele 
                    break;
                }
                case 10: { // A negatív nyomaték felvétele 
                    break;
                }
                case 11: { // A nyomaték módosítása 
                    break;
                }
                case 12: { // A nyomaték levétele
                    break;
                }
                default: {
                }
            }
        }
        if (e.getX() > 319 && (e.getY() > 20 && e.getY() <= 35)) {
            menu = 1;
        }
        if (e.getX() > 319 && (e.getY() > 35 && e.getY() <= 50)) {
            menu = 2;
        }
        if (e.getX() > 319 && (e.getY() > 50 && e.getY() <= 65)) {
            menu = 3;
        }
        if (e.getX() > 319 && (e.getY() > 65 && e.getY() <= 80)) {
            menu = 4;
        }
        if (e.getX() > 319 && (e.getY() > 95 && e.getY() <= 110)) {
            menu = 5;
        }
        if (e.getX() > 319 && (e.getY() > 110 && e.getY() <= 125)) {
            menu = 6;
        }
        if (e.getX() > 319 && (e.getY() > 125 && e.getY() <= 140)) {
            menu = 7;
        }
        if (e.getX() > 319 && (e.getY() > 140 && e.getY() <= 155)) {
            menu = 8;
        }
        if (e.getX() > 319 && (e.getY() > 170 && e.getY() <= 185)) {
            menu = 9;
        }
        if (e.getX() > 319 && (e.getY() > 185 && e.getY() <= 200)) {
            menu = 10;
        }
        if (e.getX() > 319 && (e.getY() > 200 && e.getY() <= 215)) {
            menu = 11;
        }
        if (e.getX() > 319 && (e.getY() > 215 && e.getY() <= 230)) {
            menu = 12;
        }
        // Erőnövelés (+)
        if (e.getX() > 319 && e.getX() < 340 && (e.getY() > 245 && e.getY() <= 260)) {
            if (akt_nyil > 0) {
                if (nyil_koord[akt_nyil][1] < 0) {
                    nyil_koord[akt_nyil][1]--;
                } else {
                    nyil_koord[akt_nyil][1]++;
                }
            }
        }
        // Erőcsökkentés (-)
        if (e.getX() > 339 && e.getX() < 360 && (e.getY() > 245 && e.getY() <= 260)) {
            if (akt_nyil > 0) {
                if (nyil_koord[akt_nyil][1] > 0) {
                    nyil_koord[akt_nyil][1]--;
                    if (nyil_koord[akt_nyil][1] == 0) {
                        nyil_koord[akt_nyil][1] = 1;
                    }
                } else {
                    nyil_koord[akt_nyil][1]++;
                    if (nyil_koord[akt_nyil][1] == 0) {
                        nyil_koord[akt_nyil][1] = -1;
                    }
                }
            }
        }
        // Balra (<-)
        if (e.getX() > 359 && e.getX() < 380 && (e.getY() > 245 && e.getY() <= 260)) {
            if (akt_nyil > 0) {
                nyil_koord[akt_nyil][0]--;
                if (nyil_koord[akt_nyil][0] == -1) {
                    nyil_koord[akt_nyil][0] = 0;
                }
            }
        }
        // Jobbra (->)
        if (e.getX() > 379 && e.getX() < 400 && (e.getY() > 245 && e.getY() <= 260)) {
            if (akt_nyil > 0) {
                nyil_koord[akt_nyil][0]++;
                if (nyil_koord[akt_nyil][0] == hossz + 1) {
                    nyil_koord[akt_nyil][0] = hossz;
                }
            }
        }
        // Hossznövelés (Hossz+)
        if (e.getX() > 319 && e.getX() < 370 && (e.getY() > 245 && e.getY() <= 260)) {
        }
        // Hosszcsökkentés (Hossz-)
        if (e.getX() > 369 && e.getX() < 420 && (e.getY() > 245 && e.getY() <= 260)) {
        }
        repaint();
        e.consume();
    }

    public void mouseReleased(MouseEvent e) {
    }

    public void mouseEntered(MouseEvent e) {
    }

    public void mouseExited(MouseEvent e) {
    }
    // TODO overwrite start(), stop() and destroy() methods
}
