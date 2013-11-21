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
public class kettamaszu extends Applet implements KeyListener, MouseListener {

    int x, y, x1, y1, x2, y2;
    float k;
    int dist;  // A kiválasztásnál az éppen aktuális távolság
    float arany;      // A kijelzés aránya
    float nagyitas;   // A tartó hosszának beállítása méretre (400/hossz)
    int hossz, konzol1, konzol2;  // A kéttámaszú tartó hossza és a konzolaik
    int nyil_db; // A koncentrált erőknek a száma
    int[] nyil_koord = new int[100]; // A felrakott erők értéke
    float[] nyil_ero = new float[100]; // A felrakott erők értéke
    int akt_nyil; // Az aktuális erő indexe
    int megoszlo_db; // A megoszló terhek a száma
    int[][] megoszlo_koord = new int[100][2]; // A megoszló terhek kezdő koordinátája, hossza
    float[] megoszlo_teher = new float[100]; // A megoszló terhek értéke
    int akt_megoszlo; // Az aktuális megoszló teher indexe
    int metszek = 400;  // A metszekek szama (470-70...)
    float[] nyiroero = new float[metszek];  // Az aktuális pont nyíróerőértéke  
    float[] nyomatek = new float[metszek];  // Az aktuális pont nyomatéki értéke 
    String szoveg;
    int menu = 1;
    int maxnyomatek_hely, maxnyiroero_hely;
    svg_out kimenet = new svg_out();
    float fa, fb;  // A támaszerők

    /**
     * Initialization method that will be called after the applet is loaded
     * into the browser.
     */
    public void paint(Graphics g) {
        float f, fa1, fb1;  // A maximális erő
        int bazis;
        float metszet;
        // A kéttámaszú tartó ábrája                
        g.setColor(Color.black);
        // A gerenda
        bazis = 42;
        g.drawLine(70, bazis, 470, bazis);
        g.drawLine(70, bazis - 1, 470, bazis - 1);
        // A mozgó talp
        k = (Float.parseFloat(String.valueOf(konzol1)) / Float.parseFloat(String.valueOf(hossz))) * 400;
        g.drawLine(70 + (int) k, bazis, 65 + (int) k, bazis + 5);
        g.drawLine(70 + (int) k, bazis, 75 + (int) k, bazis + 5);
        g.drawLine(65 + (int) k, bazis + 5, 75 + (int) k, bazis + 5);
        g.drawLine(50 + (int) k, bazis + 9, 90 + (int) k, bazis + 9);
        g.drawLine(50 + (int) k, bazis + 8, 90 + (int) k, bazis + 8);
        g.setColor(Color.LIGHT_GRAY);
        g.drawLine(70 + (int) k, bazis + 10, 70 + (int) k, 480);
        g.setColor(Color.black);
        g.drawLine(73 + (int) k, bazis + 13, 67 + (int) k, bazis + 17);
        // A fix talp      
        k = (Float.parseFloat(String.valueOf(hossz - konzol2)) / Float.parseFloat(String.valueOf(hossz))) * 400;
        g.drawLine(70 + (int) k, bazis, 65 + (int) k, bazis + 7);
        g.drawLine(70 + (int) k, bazis, 75 + (int) k, bazis + 7);
        g.drawLine(65 + (int) k, bazis + 7, 75 + (int) k, bazis + 7);
        g.drawLine(50 + (int) k, bazis + 7, 90 + (int) k, bazis + 7);
        g.drawLine(50 + (int) k, bazis + 8, 90 + (int) k, bazis + 8);
        // A konzolok és a belső köz méretvonalai 
        g.setColor(Color.LIGHT_GRAY);
        g.drawLine(70 + (int) k, bazis + 10, 70 + (int) k, 480);
        g.drawLine(70, bazis + 10, 70, 480);
        g.drawLine(470, bazis + 10, 470, 480);
        g.drawLine(65, bazis + 15, 475, bazis + 15);
        g.setColor(Color.black);
        g.drawLine(73, bazis + 13, 67, bazis + 17);
        g.drawLine(473, bazis + 13, 467, bazis + 17);
        g.drawLine(73 + (int) k, bazis + 13, 67 + (int) k, bazis + 17);
        if (konzol1 > 0) {
            szoveg = String.valueOf(konzol1);
            g.drawString(szoveg, 70 + (int) ((konzol1 / 2) * nagyitas) - 10, bazis + 13);
        }
        if (konzol2 > 0) {
            szoveg = String.valueOf(konzol2);
            g.drawString(szoveg, 70 + (int) (hossz - (konzol2 / 2) * nagyitas) - 10, bazis + 13);
        }
        szoveg = String.valueOf(hossz - konzol1 - konzol2);
        g.drawString(szoveg, 70 + (int) ((konzol1 + (hossz - konzol1 - konzol2) / 2) * nagyitas) - 10, bazis + 13);

        // A "Menürendszer"

        /*      
        g.setColor(Color.black);
        g.drawString("Koncentrált erő", 540, 20);
        if (menu == 1) {
        g.setColor(Color.green);
        } else {
        g.setColor(Color.black);
        }
        g.drawString("- Pozitív erő", 550, 35);
        if (menu == 2) {
        g.setColor(Color.green);
        } else {
        g.setColor(Color.black);
        }
        g.drawString("- Negatív erő", 550, 50);
        if (menu == 3) {
        g.setColor(Color.green);
        } else {
        g.setColor(Color.black);
        }
        g.drawString("- Módosít", 550, 65);
        if (menu == 4) {
        g.setColor(Color.green);
        } else {
        g.setColor(Color.black);
        }
        g.drawString("- Levesz", 550, 80);
        g.setColor(Color.black);
        g.drawString("Megoszló teher", 540, 95);
        if (menu == 5) {
        g.setColor(Color.green);
        } else {
        g.setColor(Color.black);
        }
        g.drawString("- Pozitív teher", 550, 110);
        if (menu == 6) {
        g.setColor(Color.green);
        } else {
        g.setColor(Color.black);
        }
        g.drawString("- Negatív teher", 550, 125);
        if (menu == 7) {
        g.setColor(Color.green);
        } else {
        g.setColor(Color.black);
        }
        g.drawString("- Módosít", 550, 140);
        if (menu == 8) {
        g.setColor(Color.green);
        } else {
        g.setColor(Color.black);
        }
        g.drawString("- Levesz", 550, 155);
        g.setColor(Color.black);
        g.drawString("Nyomaték", 540, 170);
        if (menu == 9) {
        g.setColor(Color.green);
        } else {
        g.setColor(Color.black);
        }
        g.drawString("- Pozitív nyomaték", 550, 185);
        if (menu == 10) {
        g.setColor(Color.green);
        } else {
        g.setColor(Color.black);
        }
        g.drawString("- Negatív nyomaték", 550, 200);
        if (menu == 11) {
        g.setColor(Color.green);
        } else {
        g.setColor(Color.black);
        }
        g.drawString("- Módosít", 550, 215);
        if (menu == 12) {
        g.setColor(Color.green);
        } else {
        g.setColor(Color.black);
        }
        g.drawString("- Levesz", 550, 230);
        g.setColor(Color.black);
        g.drawString("SVG kimenet", 540, 245);
        if ((menu == 3) || (menu == 7) || (menu == 11)) {
        g.setColor(Color.green);
        g.drawString("+", 540, 260);
        g.drawString("-", 560, 260);
        g.drawString("<-", 580, 260);
        g.drawString("->", 600, 260);
        }
        if (menu == 7) {
        g.drawString("Hossz+", 540, 275);
        g.drawString("Hossz-", 590, 275);
        }
         */
        bazis = 60;
        if (akt_nyil > 0 && menu == 3) {
            g.setColor(Color.cyan);
            g.drawLine(67, bazis, (int) (nyil_koord[akt_nyil] * nagyitas) + 73, bazis);
            g.drawLine((int) (nyil_koord[akt_nyil] * nagyitas) + 70, bazis + 3, (int) (nyil_koord[akt_nyil] * nagyitas) + 70, bazis - 3);
            g.setColor(Color.cyan);
            g.drawLine(67, bazis + 3, 73, bazis - 3);
            g.drawLine((int) (nyil_koord[akt_nyil] * nagyitas) + 67, bazis + 3, (int) (nyil_koord[akt_nyil] * nagyitas) + 73, bazis - 3);
            szoveg = String.valueOf(nyil_koord[akt_nyil]);
            g.drawString(szoveg, ((int) (nyil_koord[akt_nyil] * nagyitas) / 2) + 60, bazis);
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
                g.drawLine((int) (nyil_koord[i] * nagyitas) + 70, bazis - 18, (int) (nyil_koord[i] * nagyitas) + 70, bazis - 2);
                for (int j = 1; j <= 10; j++) {
                    if (nyil_ero[i] > 0) {
                        g.drawLine((int) (nyil_koord[i] * nagyitas) + 70, bazis - 2, 64 + j + (int) ((nyil_koord[i]) * nagyitas), bazis - 15);
                    } else {
                        g.drawLine((int) (nyil_koord[i] * nagyitas) + 70, bazis - 18, 64 + j + (int) ((nyil_koord[i]) * nagyitas), bazis - 7);
                    }
                }
                szoveg = String.valueOf(nyil_ero[i]);
                g.drawString(szoveg, (int) (nyil_koord[i] * nagyitas) + 77, bazis - 11);
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
                g.drawRect((int) (megoszlo_koord[i][0] * nagyitas) + 70, 2, (int) (megoszlo_koord[i][1] * nagyitas), 18);
                g.drawLine((int) (megoszlo_koord[i][0] * nagyitas) + 75, bazis - 4, (int) (megoszlo_koord[i][0] * nagyitas) + 75, bazis - 18);
                for (int j = 1; j <= 8; j++) {
                    if (megoszlo_teher[i] > 0) {
                        g.drawLine((int) (megoszlo_koord[i][0] * nagyitas) + 75, bazis - 4, 71 + j + (int) ((megoszlo_koord[i][0]) * nagyitas), bazis - 14);
                    } else {
                        g.drawLine((int) (megoszlo_koord[i][0] * nagyitas) + 75, bazis - 18, 71 + j + (int) ((megoszlo_koord[i][0]) * nagyitas), bazis - 8);
                    }
                }
                szoveg = "q=" + String.valueOf(megoszlo_teher[i]);
                g.drawString(szoveg, (int) (megoszlo_koord[i][0] * nagyitas) + 80, bazis - 4);
            }
        }

        // A nyíróerőábra
        bazis = 167;
        for (int i = 0; i < metszek; i++) {
            nyiroero[i] = 0;
        }
        g.setColor(Color.black);
        g.drawString("T", 52, bazis + 2);
        g.drawString("+", 62, bazis - 5);
        g.drawString("-", 62, bazis + 10);
        g.setColor(Color.LIGHT_GRAY);
        g.drawLine(65, bazis, 475, bazis);
        if ((nyil_db > 0) || (megoszlo_db > 0)) {
            // A támaszerők és nyíróerőmetszékek a koncentrált erőkből
            fa1 = 0;
            fb1 = 0;
            if (nyil_db > 0) {
                for (int i = 1; i <= nyil_db; i++) {
                    f = Float.parseFloat(String.valueOf(nyil_koord[i] - konzol1));
                    fa = Float.parseFloat(String.valueOf(hossz - konzol1 - konzol2));
                    fb = (f * nyil_ero[i]) / fa;
                    fa = nyil_ero[i] - fb;
                    fa1 = fa1 + fa;
                    fb1 = fb1 + fb;
                    //System.out.println("Fa:"+fa+" Fb:"+fb);
                    for (int j = 0; j < metszek; j++) {
                        metszet = 0 + (Float.parseFloat(String.valueOf(j)) * (Float.parseFloat(String.valueOf(hossz)) / Float.parseFloat(String.valueOf(metszek))));
                        if (metszet > nyil_koord[i]) {
                            nyiroero[j] = nyiroero[j] + nyil_ero[i];
                        }
                        if (metszet > konzol1) {
                            nyiroero[j] = nyiroero[j] - fa;
                        }
                        if (metszet > (hossz - konzol2)) {
                            nyiroero[j] = nyiroero[j] - fb;
                        }
                        //System.out.println("j:"+j+"  Metszék:"+metszet+" Nyíróerő:"+nyiroero[j]);
                    }
                }
            }
            // A támaszerők és nyíróerőmetszékek a megoszló terhelésekből
            if (megoszlo_db > 0) {
                for (int i = 1; i <= megoszlo_db; i++) {
                    f = Float.parseFloat(String.valueOf(megoszlo_koord[i][0])) + (Float.parseFloat(String.valueOf(megoszlo_koord[i][1]))) / 2;
                    f = f - Float.parseFloat(String.valueOf(konzol1));
                    fa = Float.parseFloat(String.valueOf(hossz - konzol1 - konzol2));
                    fb = (f * Float.parseFloat(String.valueOf(megoszlo_teher[i] * megoszlo_koord[i][1]))) / fa;
                    f = Float.parseFloat(String.valueOf(megoszlo_teher[i] * megoszlo_koord[i][1]));
                    fa = f - fb;
                    fa1 = fa1 + fa;
                    fb1 = fb1 + fb;
                    for (int j = 0; j < metszek; j++) {
                        metszet = 0 + (Float.parseFloat(String.valueOf(j)) * (Float.parseFloat(String.valueOf(hossz)) / Float.parseFloat(String.valueOf(metszek))));
                        if (metszet >= konzol1) {
                            nyiroero[j] = nyiroero[j] - fa;
                        }
                        if (metszet >= (hossz - konzol2)) {
                            nyiroero[j] = nyiroero[j] - fb;
                        }
                        if (metszet >= megoszlo_koord[i][0] && metszet <= megoszlo_koord[i][0] + megoszlo_koord[i][1]) {
                            nyiroero[j] = nyiroero[j] + (j - megoszlo_koord[i][0] + 1) * megoszlo_teher[i] / nagyitas;
                        }

                        if (metszet > megoszlo_koord[i][0] + megoszlo_koord[i][1]) {
                            nyiroero[j] = nyiroero[j] + megoszlo_koord[i][1] * megoszlo_teher[i];
                        }
                        //System.out.println("j:"+j+"  Metszék:"+metszet+" Nyíróerő:"+nyiroero[j]);
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
                if (nyiroero[i] != 0) {
                    arany = nyiroero[i] * (90 / f);                    // A nyíróerőábra magassága: 90
                    g.drawLine(i + 70, bazis, i + 70, bazis + (int) arany);
                }
            }
            // A maximális nyíróerő helye és mértéke
            g.setColor(Color.green);
            if (x1 == 0) {
                g.drawLine(maxnyiroero_hely + 70, bazis, maxnyiroero_hely + 70, bazis - 90);
                g.drawLine(maxnyiroero_hely + 70, bazis - 45, maxnyiroero_hely + 90, bazis + 107);
            } else {
                g.drawLine(maxnyiroero_hely + 70, bazis, maxnyiroero_hely + 70, bazis + 90);
                g.drawLine(maxnyiroero_hely + 70, bazis + 45, maxnyiroero_hely + 90, bazis + 107);
            }
            g.drawLine(maxnyiroero_hely + 90, bazis + 107, maxnyiroero_hely + 190, bazis + 107);
            szoveg = "T_max=" + String.valueOf(f);
            g.setColor(Color.red);
            g.drawString(szoveg, maxnyiroero_hely + 90, bazis + 105);

            g.setColor(Color.LIGHT_GRAY);
            g.drawLine(67, bazis + 107, maxnyiroero_hely + 73, bazis + 107);
            g.drawLine(maxnyiroero_hely + 70, bazis + 110, maxnyiroero_hely + 70, bazis + 104);
            g.setColor(Color.BLACK);
            g.drawLine(67, bazis + 110, 73, bazis + 104);
            g.drawLine(maxnyiroero_hely + 67, bazis + 110, maxnyiroero_hely + 73, bazis + 104);
            szoveg = String.valueOf(maxnyiroero_hely / nagyitas);
            g.drawString(szoveg, (maxnyiroero_hely / 2) + 60, bazis + 105);
            // A támaszerők nyilai és értékei
            // Fa
            bazis = 42;
            szoveg = "Fa=" + String.valueOf(fa1);
            System.out.println(szoveg);
            k = (Float.parseFloat(String.valueOf(konzol1)) / Float.parseFloat(String.valueOf(hossz))) * 400;
            g.drawString(szoveg, 75 + (int) k, bazis + 33);
            g.drawLine(70 + (int) k, bazis + 16, 70 + (int) k, bazis + 35);
            for (int i = 0; i <= 7; i++) {
                if (fa > 0) {
                    g.drawLine(70 + (int) k, bazis + 16, 66 + i + (int) k, bazis + 30);
                } else {
                    g.drawLine(70 + (int) k, bazis + 35, 66 + i + (int) k, bazis + 21);
                }
            }
            //Fb
            szoveg = "Fb=" + String.valueOf(fb1);
            System.out.println(szoveg);
            k = (Float.parseFloat(String.valueOf(hossz - konzol2)) / Float.parseFloat(String.valueOf(hossz))) * 400;
            g.drawString(szoveg, 75 + (int) k, bazis + 33);
            g.drawLine(70 + (int) k, bazis + 16, 70 + (int) k, bazis + 35);
            for (int i = 0; i <= 7; i++) {
                if (fb > 0) {
                    g.drawLine(70 + (int) k, bazis + 16, 66 + i + (int) k, bazis + 30);
                } else {
                    g.drawLine(70 + (int) k, bazis + 35, 66 + i + (int) k, bazis + 21);
                }
            }

        }
        // A nyomatéki ábra
        bazis = 367;
        g.setColor(Color.black);
        g.drawString("M", 52, bazis + 2);
        g.drawString("+", 62, bazis - 5);
        g.drawString("-", 62, bazis + 10);
        g.setColor(Color.LIGHT_GRAY);
        g.drawLine(65, bazis, 475, bazis);
        // A nyomatéki értékek kiszámolása
        if ((nyil_db > 0) || (megoszlo_db > 0)) {
            nyomatek[0] = 0;
            for (int i = 1; i < metszek; i++) {
                nyomatek[i] = nyomatek[i - 1] - nyiroero[i] / nagyitas;
                //System.out.println("Metszék:"+i+" nyíróerő:"+nyiroero[i]+" nyomaték:"+nyomatek[i]);
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
                    arany = nyomatek[i] * (90 / f);   // A nyomatéki ábra magassága:90
                    g.drawLine(i + 70, bazis, i + 70, bazis + (int) arany);
                }
            }
            // A maximális nyomaték helye és mértéke
            g.setColor(Color.red);
            if (x1 == 0) {
                g.drawLine(maxnyomatek_hely + 70, bazis, maxnyomatek_hely + 70, bazis - 90);
                g.drawLine(maxnyomatek_hely + 70, bazis - 45, maxnyomatek_hely + 90, bazis + 107);
            } else {
                g.drawLine(maxnyomatek_hely + 70, bazis, maxnyomatek_hely + 70, bazis + 90);
                g.drawLine(maxnyomatek_hely + 70, bazis + 45, maxnyomatek_hely + 90, bazis + 107);
            }
            g.drawLine(maxnyomatek_hely + 90, bazis + 107, maxnyomatek_hely + 190, bazis + 107);
            szoveg = "M_max=" + String.valueOf(f);
            g.drawString(szoveg, maxnyomatek_hely + 90, bazis + 105);
            g.setColor(Color.LIGHT_GRAY);
            g.drawLine(67, bazis + 107, maxnyomatek_hely + 73, bazis + 107);
            g.drawLine(maxnyomatek_hely + 70, bazis + 110, maxnyomatek_hely + 70, bazis + 104);
            g.setColor(Color.BLACK);
            g.drawLine(67, bazis + 110, 73, bazis + 104);
            g.drawLine(maxnyomatek_hely + 67, bazis + 110, maxnyomatek_hely + 73, bazis + 104);
            szoveg = String.valueOf(maxnyomatek_hely / nagyitas);
            g.drawString(szoveg, (maxnyomatek_hely / 2) + 60, bazis + 105);
        }
    }

    public void init() {
        // TODO start asynchronous download of heavy resources
        addKeyListener(this);
        addMouseListener(this);

  
 	konzol1 = 200;
        konzol2 = 0;                        
        hossz = 500;
        nyil_db=1;
        nyil_koord[1]=0;nyil_ero[1]=10f;                        
        

        nagyitas = 400 / Float.parseFloat(String.valueOf(hossz));
        System.out.println(nagyitas);
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
                x = nyil_koord[akt_nyil]; /*y = nyil_ero[akt_nyil];*/
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
                nyil_koord[akt_nyil] = x; /*nyil_koord[akt_nyil][1]=y;*/
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
        if (e.getX() > 69 && e.getX() < 471) {
            // Az aktuális koncentrált erő kiválasztása
            if ((menu == 3) || (menu == 4)) {
                akt_nyil = 0;
                akt_megoszlo = 0;
                if (nyil_db > 0) {
                    dist = hossz;
                    for (int i = 1; i <= nyil_db; i++) {
                        x1 = e.getX() - 30;
                        y1 = nyil_koord[i];
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
                    nyil_koord[nyil_db] = (int) (e.getX() * nagyitas) - 70;     // A kordináta
                    nyil_ero[nyil_db] = 10;               // A kezdő erő   
                    akt_nyil = 0;
                    break;
                }
                case 2: {
                    nyil_db++;     // A negatív koncentrált erő felrakása
                    if (nyil_db > 100) {
                        nyil_db = 100;
                    }
                    nyil_koord[nyil_db] = (int) (e.getX() * nagyitas) - 70;     // A kordináta
                    nyil_ero[nyil_db] = -10;               // A kezdő erő  
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
                            nyil_koord[x2] = nyil_koord[i];
                            nyil_ero[x2] = nyil_ero[i];
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

                    // A kordináta
                    megoszlo_koord[megoszlo_db][0] = (int) (e.getX() / nagyitas) - 70;
                    // A hossz
                    megoszlo_koord[megoszlo_db][1] = (int) (40 / nagyitas);

                    /*megoszlo_koord[megoszlo_db][0] = 100;
                    megoszlo_koord[megoszlo_db][1] = 191;*/
                    if (megoszlo_koord[megoszlo_db][0] + 70 > hossz) {
                        megoszlo_koord[megoszlo_db][0] = hossz - 70;
                    }
                    megoszlo_teher[megoszlo_db] = 0.01f;               // A kezdő teher  float!!!
                    break;
                }
                case 6: {
                    megoszlo_db++;     // A negatív megoszló teher felrakása
                    if (megoszlo_db > 100) {
                        megoszlo_db = 100;
                    }
                    megoszlo_koord[megoszlo_db][0] = (int) (e.getX() / nagyitas) - 70;     // A kordináta
                    megoszlo_koord[megoszlo_db][1] = (int) (40 / nagyitas);             // A hossz
                    if (megoszlo_koord[megoszlo_db][0] + 70 > hossz) {
                        megoszlo_koord[megoszlo_db][0] = hossz - 70;
                    }
                    megoszlo_teher[megoszlo_db] = -0.1f;               // A kezdő teher
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
                            megoszlo_teher[x2] = megoszlo_teher[i];
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
        if (e.getX() > 539 && (e.getY() > 20 && e.getY() <= 35)) {
            menu = 1;
        }
        if (e.getX() > 539 && (e.getY() > 35 && e.getY() <= 50)) {
            menu = 2;
        }
        if (e.getX() > 539 && (e.getY() > 50 && e.getY() <= 65)) {
            menu = 3;
        }
        if (e.getX() > 539 && (e.getY() > 65 && e.getY() <= 80)) {
            menu = 4;
        }
        if (e.getX() > 539 && (e.getY() > 95 && e.getY() <= 110)) {
            menu = 5;
        }
        if (e.getX() > 539 && (e.getY() > 110 && e.getY() <= 125)) {
            menu = 6;
        }
        if (e.getX() > 539 && (e.getY() > 125 && e.getY() <= 140)) {
            menu = 7;
        }
        if (e.getX() > 539 && (e.getY() > 140 && e.getY() <= 155)) {
            menu = 8;
        }
        if (e.getX() > 539 && (e.getY() > 170 && e.getY() <= 185)) {
            menu = 9;
        }
        if (e.getX() > 539 && (e.getY() > 185 && e.getY() <= 200)) {
            menu = 10;
        }
        if (e.getX() > 539 && (e.getY() > 200 && e.getY() <= 215)) {
            menu = 11;
        }
        if (e.getX() > 539 && (e.getY() > 215 && e.getY() <= 230)) {
            menu = 12;
        }
        // Erőnövelés (+)
        if (e.getX() > 539 && e.getX() < 560 && (e.getY() > 245 && e.getY() <= 260)) {
            if (akt_nyil > 0) {
                if (nyil_ero[akt_nyil] < 0) {
                    nyil_koord[akt_nyil]--;
                } else {
                    nyil_ero[akt_nyil]++;
                }
            }
        }
        // Erőcsökkentés (-)
        if (e.getX() > 559 && e.getX() < 580 && (e.getY() > 245 && e.getY() <= 260)) {
            if (akt_nyil > 0) {
                if (nyil_ero[akt_nyil] > 0) {
                    nyil_ero[akt_nyil]--;
                    if (nyil_ero[akt_nyil] == 0) {
                        nyil_ero[akt_nyil] = 1;
                    }
                } else {
                    nyil_ero[akt_nyil]++;
                    if (nyil_ero[akt_nyil] == 0) {
                        nyil_ero[akt_nyil] = -1;
                    }
                }
            }
        }
        // Balra (<-)
        if (e.getX() > 579 && e.getX() < 600 && (e.getY() > 245 && e.getY() <= 260)) {
            if (akt_nyil > 0) {
                nyil_koord[akt_nyil]--;
                if (nyil_koord[akt_nyil] == -1) {
                    nyil_koord[akt_nyil] = 0;
                }
            }
        }
        // Jobbra (->)
        if (e.getX() > 599 && e.getX() < 620 && (e.getY() > 245 && e.getY() <= 260)) {
            if (akt_nyil > 0) {
                nyil_koord[akt_nyil]++;
                if (nyil_koord[akt_nyil] == hossz + 1) {
                    nyil_koord[akt_nyil] = hossz;
                }
            }
        }
        // Hossznövelés (Hossz+)
        if (e.getX() > 619 && e.getX() < 590 && (e.getY() > 245 && e.getY() <= 260)) {
        }
        // Hosszcsökkentés (Hossz-)
        if (e.getX() > 589 && e.getX() < 640 && (e.getY() > 245 && e.getY() <= 260)) {
        }
        // SVG kimenet file-ba
        if (e.getX() > 539 && (e.getY() > 229 && e.getY() <= 245)) {
            kimenet.svg_kimenet(metszek, nyiroero, nyomatek, nyil_db, nyil_koord, nyil_ero, megoszlo_db, megoszlo_koord, megoszlo_teher, fa, fb, hossz, konzol1, konzol2);
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
