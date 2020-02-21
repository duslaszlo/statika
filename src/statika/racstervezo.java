/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package statika;

import Entities.Mintacsp;
import Entities.Mintarud;
import Entities.Racsalap;
import Entities.Racsalap1;
import Entities.Szelveny;
import java.awt.Color;
import java.awt.Component;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import javax.swing.DefaultCellEditor;
import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;

/**
 *
 * @author SD-LEAP
 */
public class racstervezo extends javax.swing.JInternalFrame {

    /**
     * Creates new form racselemtervezo
     */
    racstervezoadatok racs = new racstervezoadatok();
    String parancs;
    static Connection co;
    static Statement st;
    static ResultSet rs;

    public racstervezo() {
        initComponents();
        racs.nev = "";
        racselemek_kijelzo_torles();
        kisrajz_kepkitevo();
        mentes.setEnabled(false);
        sql_lemento.setEnabled(false);
        // A mintaelem beolvasása
        racs.mintaindexf = 0;
        racs.mintaindexv = 0;
        racs.aktualis_szekcio = 0;
        try {
            Class.forName("com.mysql.jdbc.Driver").newInstance();
            co = DriverManager.getConnection(Global.mysql_server, Global.mysql_user, Global.mysql_password);
            st = co.createStatement();
            parancs = "select * from mintarud order by irany,tipus,verzio,id";
            //System.out.println(parancs);
            rs = st.executeQuery(parancs);
            while (rs.next()) {
                Mintarud c = new Mintarud();
                c.setIrany(rs.getInt("irany"));
                c.setTipus(rs.getInt("tipus"));
                c.setVerzio(rs.getInt("verzio"));
                c.setKezdocsp(rs.getInt("kezdocsp"));
                c.setVegecsp(rs.getInt("vegecsp"));
                racs.mintarud.add(c);
            }
            parancs = "select * from mintacsp order by irany,csomopont";
            //System.out.println(parancs);
            rs = st.executeQuery(parancs);
            while (rs.next()) {
                Mintacsp c = new Mintacsp();
                c.setId(rs.getInt("id"));
                c.setIrany(rs.getInt("irany"));
                c.setCsomopont(rs.getInt("csomopont"));
                c.setX(rs.getFloat("x"));
                c.setY(rs.getFloat("y"));
                c.setZ(rs.getFloat("z"));
                c.setJellegxy(rs.getInt("jellegxy"));
                c.setJellegyz(rs.getInt("jellegyz"));
                c.setKezdcspxy(rs.getInt("kezdcspxy"));
                c.setVegecspxy(rs.getInt("vegecspxy"));
                c.setKezdcspyz(rs.getInt("kezdcspyz"));
                c.setVegecspyz(rs.getInt("vegecspyz"));
                racs.mintacsp.add(c);
                //System.out.println(rs.getInt("id")+" "+racs.mintacsp.size());
            }
            //System.out.println(racs.mintacsp.size() + "  "+racs.mintarud.size());
            drotvazak.removeAllItems();
            drotvazak.addItem("Válassz");
            parancs = "Select distinct nev from racsalap order by nev";
            rs = st.executeQuery(parancs);
            while (rs.next()) {
                drotvazak.addItem(rs.getString("nev"));
                //System.out.println(rs.getString("nev"));
            }
            // A szelvények feltöltése            
            parancs = "select * from szelveny order by nev";
            //System.out.println(parancs);
            rs = st.executeQuery(parancs);
            while (rs.next()) {
                Szelveny c = new Szelveny();
                c.setId(rs.getInt("id"));
                c.setNev(rs.getString("nev"));
                c.setMagassag(rs.getFloat("magassag"));
                c.setFmsuly(rs.getFloat("fmsuly"));
                racs.szelveny.add(c);
            }
            rs.close();
            st.close();
        } catch (InstantiationException e) {
        } catch (IllegalAccessException e) {
        } catch (ClassNotFoundException e) {
        } catch (SQLException e) {
        }
        racs.racsalap.clear();
        // A rácstipusok maximumai
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 8; j++) {
                racs.maximumok[i][j] = 0;
                for (int k = 0; k < racs.mintarud.size(); k++) {
                    if ((racs.mintarud.get(k).getIrany() == i + 1)
                            && (racs.mintarud.get(k).getTipus() == j + 1)
                            && (racs.mintarud.get(k).getVerzio() > racs.maximumok[i][j])) {
                        racs.maximumok[i][j] = racs.mintarud.get(k).getVerzio();
                    }
                }
            }
        }
        for (int i = 0; i < racs.mintacsp.size(); i++) {
            if (racs.mintacsp.get(i).getIrany() == 1) {
                racs.mintaindexf++;
                racs.mintacspf[racs.mintaindexf][0] = racs.mintacsp.get(i).getX();
                racs.mintacspf[racs.mintaindexf][1] = racs.mintacsp.get(i).getY();
                racs.mintacspf[racs.mintaindexf][2] = racs.mintacsp.get(i).getZ();
                racs.mintacspfjelleg[racs.mintaindexf][0] = racs.mintacsp.get(i).getJellegxy();
                racs.mintacspfjelleg[racs.mintaindexf][1] = racs.mintacsp.get(i).getJellegyz();
                racs.mintacspfjelleg[racs.mintaindexf][2] = racs.mintacsp.get(i).getKezdcspxy();
                racs.mintacspfjelleg[racs.mintaindexf][3] = racs.mintacsp.get(i).getVegecspxy();
                racs.mintacspfjelleg[racs.mintaindexf][4] = racs.mintacsp.get(i).getKezdcspyz();
                racs.mintacspfjelleg[racs.mintaindexf][5] = racs.mintacsp.get(i).getVegecspyz();
            } else {
                racs.mintaindexv++;
                racs.mintacspv[racs.mintaindexv][0] = racs.mintacsp.get(i).getX();
                racs.mintacspv[racs.mintaindexv][1] = racs.mintacsp.get(i).getY();
                racs.mintacspv[racs.mintaindexv][2] = racs.mintacsp.get(i).getZ();
            }
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroup1 = new javax.swing.ButtonGroup();
        ujelemnev = new javax.swing.JTextField();
        ujelem = new javax.swing.JButton();
        drotvazak = new javax.swing.JComboBox();
        drotvaz_kivalaszto = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        szekcioadatok = new javax.swing.JTable();
        Elemmodosito = new javax.swing.JButton();
        jSeparator3 = new javax.swing.JSeparator();
        jSeparator4 = new javax.swing.JSeparator();
        teljes_nezet = new javax.swing.JLabel();
        szekcio_nezet = new javax.swing.JLabel();
        szekciok = new javax.swing.JComboBox();
        szekcio_kivalaszto = new javax.swing.JButton();
        jSeparator5 = new javax.swing.JSeparator();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel1 = new javax.swing.JPanel();
        felsoszelyz = new javax.swing.JTextField();
        elemhozzado = new javax.swing.JButton();
        felsoszelxy = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        alsoszelyz = new javax.swing.JTextField();
        vizszintes = new javax.swing.JRadioButton();
        jLabel7 = new javax.swing.JLabel();
        alsoszelxy = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        Magassag = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        kapcsx = new javax.swing.JTextField();
        jLabel9 = new javax.swing.JLabel();
        elemszamok = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        kapcsy = new javax.swing.JTextField();
        jLabel11 = new javax.swing.JLabel();
        kapcsz = new javax.swing.JTextField();
        jLabel12 = new javax.swing.JLabel();
        eltolasxy = new javax.swing.JTextField();
        jLabel13 = new javax.swing.JLabel();
        eltolasyz = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        konzolhossz = new javax.swing.JTextField();
        jSeparator7 = new javax.swing.JSeparator();
        jLabel32 = new javax.swing.JLabel();
        jLabel44 = new javax.swing.JLabel();
        csomopontszam = new javax.swing.JTextField();
        rudszam = new javax.swing.JTextField();
        jLabel45 = new javax.swing.JLabel();
        jLabel46 = new javax.swing.JLabel();
        teljes_suly = new javax.swing.JTextField();
        szekcio_suly = new javax.swing.JTextField();
        jLabel47 = new javax.swing.JLabel();
        jLabel48 = new javax.swing.JLabel();
        kettamaszu = new javax.swing.JRadioButton();
        fuggoleges = new javax.swing.JRadioButton();
        kisrajz = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        jLabel17 = new javax.swing.JLabel();
        jLabel18 = new javax.swing.JLabel();
        jLabel19 = new javax.swing.JLabel();
        jLabel20 = new javax.swing.JLabel();
        jLabel21 = new javax.swing.JLabel();
        jLabel22 = new javax.swing.JLabel();
        ftipus7 = new javax.swing.JSlider();
        ftipus6 = new javax.swing.JSlider();
        ftipus5 = new javax.swing.JSlider();
        ftipus4 = new javax.swing.JSlider();
        ftipus3 = new javax.swing.JSlider();
        ftipus2 = new javax.swing.JSlider();
        ftipus1 = new javax.swing.JSlider();
        ftipus8 = new javax.swing.JSlider();
        kozok = new javax.swing.JComboBox();
        jSeparator1 = new javax.swing.JSeparator();
        kozkivalaszto = new javax.swing.JButton();
        jLabel33 = new javax.swing.JLabel();
        ftipus1text = new javax.swing.JTextField();
        ftipus2text = new javax.swing.JTextField();
        ftipus3text = new javax.swing.JTextField();
        ftipus4text = new javax.swing.JTextField();
        ftipus6text = new javax.swing.JTextField();
        ftipus5text = new javax.swing.JTextField();
        ftipus7text = new javax.swing.JTextField();
        ftipus8text = new javax.swing.JTextField();
        jSeparator9 = new javax.swing.JSeparator();
        racskoz_valtoztato = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        racskozok = new javax.swing.JTable();
        jLabel34 = new javax.swing.JLabel();
        szekciohossz = new javax.swing.JTextField();
        jPanel3 = new javax.swing.JPanel();
        jScrollPane4 = new javax.swing.JScrollPane();
        csomopontlista = new javax.swing.JTable();
        jLabel50 = new javax.swing.JLabel();
        jLabel51 = new javax.swing.JLabel();
        jScrollPane5 = new javax.swing.JScrollPane();
        rudlista = new javax.swing.JTable();
        rudlista_megjelolo = new javax.swing.JButton();
        csomopontlista_megjelolo = new javax.swing.JButton();
        jLabel25 = new javax.swing.JLabel();
        jLabel37 = new javax.swing.JLabel();
        mentes = new javax.swing.JButton();
        vastagvonalak = new javax.swing.JToggleButton();
        Kodosito = new javax.swing.JToggleButton();
        Szekcio_nagyito = new javax.swing.JSlider();
        jLabel23 = new javax.swing.JLabel();
        Teljes_nagyito = new javax.swing.JSlider();
        jLabel24 = new javax.swing.JLabel();
        Kilepes = new javax.swing.JButton();
        sql_lemento = new javax.swing.JButton();

        setClosable(true);
        setIconifiable(true);
        setMaximizable(true);
        setTitle("Drótváztervező ");
        setName(""); // NOI18N
        setPreferredSize(new java.awt.Dimension(1400, 800));

        ujelem.setText("Új drótváz");
        ujelem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ujelemActionPerformed(evt);
            }
        });

        drotvaz_kivalaszto.setText("Kiválaszt");
        drotvaz_kivalaszto.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                drotvaz_kivalasztoActionPerformed(evt);
            }
        });

        jLabel1.setFont(new java.awt.Font("Courier New", 1, 14)); // NOI18N
        jLabel1.setText("Meglévő drótváz:");

        szekcioadatok.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null}
            },
            new String [] {
                "Szek.", "Típus", "Hossz", "AlsóXY", "AlsóYZ", "FelsőXY", "FelsőYZ", "DiffX", "DiffY", "DiffZ", "EltolásXY", "EltolásYZ", "Konzol", "Szelvény1", "Szelvény2", "Szelvény3", "Szelvény4", "Szelvény5", "Szelvény6", "Szelvény7", "Szelvény8", "Szhossz1", "Szhossz2", "Szhossz3", "Szhossz4", "Szhossz5", "Szhossz6", "Szhossz7", "Szhossz8", "Súly1", "Súly2", "Súly3", "Súly4", "Súly5", "Súly6", "Súly7", "Súly8", "Cspszám", "Rúdszám", "Szekciósúly", "Törölni"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Integer.class, java.lang.String.class, java.lang.Integer.class, java.lang.Integer.class, java.lang.Integer.class, java.lang.Integer.class, java.lang.Integer.class, java.lang.Integer.class, java.lang.Integer.class, java.lang.Integer.class, java.lang.Integer.class, java.lang.Integer.class, java.lang.Integer.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Integer.class, java.lang.Integer.class, java.lang.Integer.class, java.lang.Integer.class, java.lang.Integer.class, java.lang.Integer.class, java.lang.Integer.class, java.lang.Integer.class, java.lang.Integer.class, java.lang.Integer.class, java.lang.Integer.class, java.lang.Integer.class, java.lang.Integer.class, java.lang.Integer.class, java.lang.Integer.class, java.lang.String.class, java.lang.Integer.class, java.lang.Integer.class, java.lang.Integer.class, java.lang.Boolean.class
            };
            boolean[] canEdit = new boolean [] {
                false, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane1.setViewportView(szekcioadatok);

        Elemmodosito.setText("Szekció-elemek módosítása");
        Elemmodosito.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ElemmodositoActionPerformed(evt);
            }
        });

        jSeparator3.setOrientation(javax.swing.SwingConstants.VERTICAL);
        jSeparator3.setToolTipText("");

        jSeparator4.setOrientation(javax.swing.SwingConstants.VERTICAL);

        teljes_nezet.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        teljes_nezet.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseDragged(java.awt.event.MouseEvent evt) {
                teljes_nezetMouseDragged(evt);
            }
        });

        szekcio_nezet.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        szekcio_nezet.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseDragged(java.awt.event.MouseEvent evt) {
                szekcio_nezetMouseDragged(evt);
            }
        });

        szekcio_kivalaszto.setText("Mehet");
        szekcio_kivalaszto.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                szekcio_kivalasztoActionPerformed(evt);
            }
        });

        jSeparator5.setOrientation(javax.swing.SwingConstants.VERTICAL);

        felsoszelyz.setText("600");

        elemhozzado.setText("Szekció hozzáadás");
        elemhozzado.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                elemhozzadoActionPerformed(evt);
            }
        });

        felsoszelxy.setText("1000");

        jLabel8.setText("Felső szélesség YZ:");

        alsoszelyz.setText("2400");

        buttonGroup1.add(vizszintes);
        vizszintes.setText("Vízszintes");
        vizszintes.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                vizszintesStateChanged(evt);
            }
        });

        jLabel7.setText("Felső szélesség XY:");

        alsoszelxy.setText("2500");

        jLabel6.setText("Alsó szélesség XY:");

        Magassag.setText("4800");

        jLabel5.setText("Alsó szélesség YZ:");

        kapcsx.setText("0");

        jLabel9.setText("Kapcsolati X-koord:");

        elemszamok.setText("2");

        jLabel3.setText("Elemszám:");

        jLabel10.setText("Kapcsolati Y-koord:");

        kapcsy.setText("0");

        jLabel11.setText("Kapcsolati Z-koord:");

        kapcsz.setText("0");

        jLabel12.setText("Eltolás XY:");

        eltolasxy.setText("0");

        jLabel13.setText("Eltolás YZ:");

        eltolasyz.setText("0");

        jLabel4.setText("Magasság:");

        jLabel14.setText("Konzol:");

        konzolhossz.setText("6000");

        jLabel32.setText("Csomópontszám:");

        jLabel44.setText("Rúdszám:");

        csomopontszam.setEditable(false);
        csomopontszam.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        csomopontszam.setText(" ");

        rudszam.setEditable(false);
        rudszam.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        rudszam.setText(" ");

        jLabel45.setText("Összsúly:");

        jLabel46.setText("Szekciósúly:");

        teljes_suly.setEditable(false);
        teljes_suly.setHorizontalAlignment(javax.swing.JTextField.RIGHT);

        szekcio_suly.setEditable(false);
        szekcio_suly.setHorizontalAlignment(javax.swing.JTextField.RIGHT);

        jLabel47.setText("Kg");

        jLabel48.setText("Kg");

        buttonGroup1.add(kettamaszu);
        kettamaszu.setText("Kéttámaszú");
        kettamaszu.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                kettamaszuStateChanged(evt);
            }
        });

        buttonGroup1.add(fuggoleges);
        fuggoleges.setSelected(true);
        fuggoleges.setText("Függőleges");
        fuggoleges.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                fuggolegesStateChanged(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(15, 15, 15)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(alsoszelxy, javax.swing.GroupLayout.DEFAULT_SIZE, 59, Short.MAX_VALUE)
                            .addComponent(Magassag))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(jLabel8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(felsoszelxy, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(felsoszelyz, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(0, 0, Short.MAX_VALUE)
                                .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 136, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(elemszamok, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(84, 84, 84))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(jLabel10, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                            .addComponent(jLabel11, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                        .addGap(52, 52, 52))
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(jLabel9)
                                            .addComponent(jLabel5))
                                        .addGap(18, 18, 18)
                                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(alsoszelyz, javax.swing.GroupLayout.PREFERRED_SIZE, 56, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                    .addComponent(kapcsz, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                    .addComponent(kapcsy, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                .addComponent(kapcsx, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel14, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                            .addGroup(jPanel1Layout.createSequentialGroup()
                                                .addComponent(jLabel13, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                                    .addComponent(konzolhossz, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 50, Short.MAX_VALUE)
                                                    .addComponent(eltolasyz, javax.swing.GroupLayout.Alignment.TRAILING)))
                                            .addGroup(jPanel1Layout.createSequentialGroup()
                                                .addGap(0, 0, Short.MAX_VALUE)
                                                .addComponent(jLabel12, javax.swing.GroupLayout.PREFERRED_SIZE, 136, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                .addComponent(eltolasxy, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                        .addGap(2, 2, 2))))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(kisrajz, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(kettamaszu)
                                    .addComponent(fuggoleges)
                                    .addComponent(vizszintes, javax.swing.GroupLayout.PREFERRED_SIZE, 111, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(elemhozzado))
                                .addGap(31, 31, 31)))
                        .addGap(82, 82, 82))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel32)
                            .addComponent(jLabel45))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(csomopontszam, javax.swing.GroupLayout.DEFAULT_SIZE, 69, Short.MAX_VALUE)
                            .addComponent(teljes_suly))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel47)
                        .addGap(10, 10, 10)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel44)
                            .addComponent(jLabel46))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(szekcio_suly, javax.swing.GroupLayout.PREFERRED_SIZE, 64, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel48, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(rudszam, javax.swing.GroupLayout.PREFERRED_SIZE, 77, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addComponent(jSeparator7)
                        .addGap(84, 84, 84))))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(Magassag, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(elemszamok, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(alsoszelxy, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel6))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(alsoszelyz, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel5)))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel7)
                            .addComponent(felsoszelxy, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(felsoszelyz, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel8))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel9)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(eltolasxy, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel12))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(eltolasyz, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel13)
                            .addComponent(jLabel10))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(konzolhossz, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel14)
                            .addComponent(jLabel11)))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(kapcsx, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(kapcsy, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(kapcsz, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(12, 12, 12)
                        .addComponent(fuggoleges)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(vizszintes)
                        .addGap(1, 1, 1)
                        .addComponent(kettamaszu)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(elemhozzado))
                    .addComponent(kisrajz, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator7, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel32)
                    .addComponent(csomopontszam, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel44)
                    .addComponent(rudszam, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel45)
                    .addComponent(teljes_suly, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel47)
                    .addComponent(jLabel46)
                    .addComponent(szekcio_suly, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel48))
                .addGap(55, 55, 55))
        );

        jTabbedPane1.addTab("Új szekció", jPanel1);

        jLabel2.setText("Tipus4:");

        jLabel15.setText("Tipus5:");

        jLabel16.setText("Tipus6:");

        jLabel17.setText("Tipus7:");

        jLabel18.setText("Tipus8:");

        jLabel19.setText("Tipus1:");

        jLabel20.setText("Tipus2:");

        jLabel21.setText("Tipus3:");

        jLabel22.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel22.setText("Rács-elemek:");

        ftipus7.setMaximum(6);
        ftipus7.setMinorTickSpacing(1);
        ftipus7.setValue(0);
        ftipus7.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                ftipus7StateChanged(evt);
            }
        });

        ftipus6.setMaximum(6);
        ftipus6.setMinorTickSpacing(1);
        ftipus6.setValue(0);
        ftipus6.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                ftipus6StateChanged(evt);
            }
        });

        ftipus5.setMaximum(6);
        ftipus5.setMinorTickSpacing(1);
        ftipus5.setValue(0);
        ftipus5.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                ftipus5StateChanged(evt);
            }
        });

        ftipus4.setMaximum(8);
        ftipus4.setMinorTickSpacing(1);
        ftipus4.setValue(0);
        ftipus4.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                ftipus4StateChanged(evt);
            }
        });

        ftipus3.setMaximum(6);
        ftipus3.setMinorTickSpacing(1);
        ftipus3.setToolTipText("");
        ftipus3.setValue(0);
        ftipus3.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                ftipus3StateChanged(evt);
            }
        });

        ftipus2.setMajorTickSpacing(1);
        ftipus2.setMaximum(3);
        ftipus2.setValue(0);
        ftipus2.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                ftipus2StateChanged(evt);
            }
        });

        ftipus1.setMaximum(4);
        ftipus1.setMinimum(1);
        ftipus1.setMinorTickSpacing(1);
        ftipus1.setSnapToTicks(true);
        ftipus1.setValue(0);
        ftipus1.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                ftipus1StateChanged(evt);
            }
        });

        ftipus8.setMaximum(8);
        ftipus8.setMinorTickSpacing(1);
        ftipus8.setValue(0);
        ftipus8.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                ftipus8StateChanged(evt);
            }
        });

        kozkivalaszto.setText("Mehet");
        kozkivalaszto.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                kozkivalasztoActionPerformed(evt);
            }
        });

        jLabel33.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel33.setText("Szekción belüli közök:");

        ftipus1text.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ftipus1textActionPerformed(evt);
            }
        });

        ftipus2text.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ftipus2textActionPerformed(evt);
            }
        });

        ftipus3text.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ftipus3textActionPerformed(evt);
            }
        });

        ftipus4text.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ftipus4textActionPerformed(evt);
            }
        });

        ftipus6text.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ftipus6textActionPerformed(evt);
            }
        });

        ftipus5text.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ftipus5textActionPerformed(evt);
            }
        });

        ftipus7text.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ftipus7textActionPerformed(evt);
            }
        });

        ftipus8text.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ftipus8textActionPerformed(evt);
            }
        });

        racskoz_valtoztato.setText("Változtat");
        racskoz_valtoztato.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                racskoz_valtoztatoActionPerformed(evt);
            }
        });

        racskozok.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Sorszám", "Magasság/hossz"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Integer.class, java.lang.Float.class
            };
            boolean[] canEdit = new boolean [] {
                false, true
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane2.setViewportView(racskozok);

        jLabel34.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel34.setText("A szekció magassága/hossza:");

        szekciohossz.setEditable(false);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(jLabel17)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(ftipus7, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel2Layout.createSequentialGroup()
                                .addComponent(jLabel15)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(ftipus5, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel2Layout.createSequentialGroup()
                                .addComponent(jLabel21)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(ftipus3, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(ftipus7text, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(12, 12, 12)
                                .addComponent(jLabel18)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(ftipus8, javax.swing.GroupLayout.PREFERRED_SIZE, 88, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(ftipus8text, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addContainerGap(130, Short.MAX_VALUE))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(ftipus5text, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jLabel16, javax.swing.GroupLayout.DEFAULT_SIZE, 176, Short.MAX_VALUE)
                                .addGap(128, 128, 128))))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jSeparator1)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel22)
                                    .addGroup(jPanel2Layout.createSequentialGroup()
                                        .addComponent(jLabel33)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(kozok, javax.swing.GroupLayout.PREFERRED_SIZE, 136, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(kozkivalaszto))
                                    .addGroup(jPanel2Layout.createSequentialGroup()
                                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                            .addComponent(ftipus6, javax.swing.GroupLayout.PREFERRED_SIZE, 86, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addGroup(jPanel2Layout.createSequentialGroup()
                                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                                    .addGroup(jPanel2Layout.createSequentialGroup()
                                                        .addComponent(jLabel19)
                                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                        .addComponent(ftipus1, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                        .addComponent(ftipus1text, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                                        .addComponent(jLabel20))
                                                    .addGroup(jPanel2Layout.createSequentialGroup()
                                                        .addComponent(ftipus3text, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                                        .addComponent(jLabel2)))
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                                    .addComponent(ftipus2, javax.swing.GroupLayout.DEFAULT_SIZE, 90, Short.MAX_VALUE)
                                                    .addComponent(ftipus4, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))))
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(ftipus4text, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(ftipus6text, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(ftipus2text, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))))
                                .addGap(0, 151, Short.MAX_VALUE)))
                        .addGap(20, 20, 20))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jSeparator9, javax.swing.GroupLayout.PREFERRED_SIZE, 450, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                        .addComponent(jScrollPane2)
                        .addContainerGap())
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel2Layout.createSequentialGroup()
                            .addGap(271, 271, 271)
                            .addComponent(racskoz_valtoztato))
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                            .addComponent(jLabel34, javax.swing.GroupLayout.PREFERRED_SIZE, 196, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGap(118, 118, 118)
                            .addComponent(szekciohossz, javax.swing.GroupLayout.PREFERRED_SIZE, 146, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addContainerGap()))))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(1, 1, 1)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel33)
                    .addComponent(kozok, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(kozkivalaszto))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 5, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel22)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(ftipus1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel19)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(1, 1, 1)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jLabel20)
                                .addComponent(ftipus1text, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(ftipus2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(ftipus2text, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addComponent(jLabel21)
                        .addComponent(ftipus3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel2)
                            .addComponent(ftipus3text, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addComponent(ftipus4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addComponent(ftipus4text, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel15)
                    .addComponent(ftipus5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel16)
                        .addComponent(ftipus5text, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(ftipus6text, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(ftipus6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(ftipus7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel17)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel18)
                        .addComponent(ftipus7text, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(ftipus8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(ftipus8text, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jSeparator9, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel34)
                    .addComponent(szekciohossz, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 149, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(racskoz_valtoztato)
                .addContainerGap(144, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Szerkezetsablon", jPanel2);

        csomopontlista.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Ssz", "ID", "X-koord", "Y-koord", "Z-koord", "Kijelzés"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Boolean.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, true
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane4.setViewportView(csomopontlista);

        jLabel50.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel50.setText("Csomópontlista:");

        jLabel51.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel51.setText("Rúdlista:");

        rudlista.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Ssz", "ID", "Kezd", "Vége", "Szelvény", "Hossz", "Súly (kg)", "Kijelzés"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Boolean.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, true
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane5.setViewportView(rudlista);

        rudlista_megjelolo.setText("Rúdlista megjelölés");
        rudlista_megjelolo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rudlista_megjeloloActionPerformed(evt);
            }
        });

        csomopontlista_megjelolo.setText("Csomópont megjelölés");
        csomopontlista_megjelolo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                csomopontlista_megjeloloActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jScrollPane5, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel3Layout.createSequentialGroup()
                        .addGap(15, 15, 15)
                        .addComponent(jLabel50)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 213, Short.MAX_VALUE)
                        .addComponent(csomopontlista_megjelolo, javax.swing.GroupLayout.PREFERRED_SIZE, 179, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel3Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addComponent(jLabel51)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(rudlista_megjelolo, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel50)
                    .addComponent(csomopontlista_megjelolo))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 153, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel51, javax.swing.GroupLayout.PREFERRED_SIZE, 14, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(rudlista_megjelolo))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane5, javax.swing.GroupLayout.PREFERRED_SIZE, 195, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(114, 114, 114))
        );

        jTabbedPane1.addTab("Alkotóelemek", jPanel3);

        jLabel25.setFont(new java.awt.Font("Courier New", 1, 12)); // NOI18N
        jLabel25.setText("Aktuális szekció:");

        jLabel37.setFont(new java.awt.Font("Courier New", 1, 12)); // NOI18N
        jLabel37.setText("Teljes drótváz:");

        mentes.setIcon(new javax.swing.ImageIcon(getClass().getResource("/statika/bigfolder.png"))); // NOI18N
        mentes.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mentesActionPerformed(evt);
            }
        });

        vastagvonalak.setText("Vastag vonal");
        vastagvonalak.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                vastagvonalakActionPerformed(evt);
            }
        });

        Kodosito.setText("Ködös rajz");
        Kodosito.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                KodositoActionPerformed(evt);
            }
        });

        Szekcio_nagyito.setMaximum(1000);
        Szekcio_nagyito.setMinimum(1);
        Szekcio_nagyito.setToolTipText("");
        Szekcio_nagyito.setValue(100);
        Szekcio_nagyito.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                Szekcio_nagyitoStateChanged(evt);
            }
        });

        jLabel23.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel23.setText("Eltolás:");

        Teljes_nagyito.setMaximum(1000);
        Teljes_nagyito.setMinimum(1);
        Teljes_nagyito.setValue(100);
        Teljes_nagyito.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                Teljes_nagyitoStateChanged(evt);
            }
        });

        jLabel24.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel24.setText("Eltolás:");

        Kilepes.setIcon(new javax.swing.ImageIcon(getClass().getResource("/statika/exit1.png"))); // NOI18N
        Kilepes.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                KilepesActionPerformed(evt);
            }
        });

        sql_lemento.setFont(new java.awt.Font("Tahoma", 0, 10)); // NOI18N
        sql_lemento.setIcon(new javax.swing.ImageIcon(getClass().getResource("/statika/SQL-logo-transparent.png"))); // NOI18N
        sql_lemento.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                sql_lementoActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jTabbedPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 518, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jSeparator3, javax.swing.GroupLayout.PREFERRED_SIZE, 9, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel23)
                                .addGap(32, 32, 32)
                                .addComponent(Szekcio_nagyito, javax.swing.GroupLayout.PREFERRED_SIZE, 353, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                    .addComponent(jLabel25, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                    .addComponent(szekciok, javax.swing.GroupLayout.PREFERRED_SIZE, 158, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGap(18, 18, 18)
                                    .addComponent(szekcio_kivalaszto, javax.swing.GroupLayout.PREFERRED_SIZE, 96, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addComponent(szekcio_nezet, javax.swing.GroupLayout.PREFERRED_SIZE, 438, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jSeparator4, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(teljes_nezet, javax.swing.GroupLayout.PREFERRED_SIZE, 400, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel37)
                                .addGap(18, 18, 18)
                                .addComponent(Kodosito)
                                .addGap(18, 18, 18)
                                .addComponent(vastagvonalak))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel24)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(Teljes_nagyito, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(ujelemnev, javax.swing.GroupLayout.PREFERRED_SIZE, 266, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(ujelem)
                                .addGap(18, 18, 18)
                                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 152, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(drotvazak, javax.swing.GroupLayout.PREFERRED_SIZE, 202, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(drotvaz_kivalaszto, javax.swing.GroupLayout.PREFERRED_SIZE, 101, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jSeparator5, javax.swing.GroupLayout.PREFERRED_SIZE, 15, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(Elemmodosito)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(sql_lemento, javax.swing.GroupLayout.PREFERRED_SIZE, 58, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(mentes, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(Kilepes, javax.swing.GroupLayout.PREFERRED_SIZE, 99, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(9, 9, 9)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jSeparator5, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jLabel1)
                                .addComponent(drotvazak, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(drotvaz_kivalaszto)
                                .addComponent(ujelemnev, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(ujelem))))
                    .addComponent(mentes, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(18, 18, 18)
                        .addComponent(Elemmodosito))
                    .addComponent(sql_lemento, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(Kilepes, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 104, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jSeparator4)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(jLabel37)
                                        .addComponent(vastagvonalak)
                                        .addComponent(Kodosito))
                                    .addGroup(layout.createSequentialGroup()
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                            .addComponent(szekciok, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(szekcio_kivalaszto)
                                            .addComponent(jLabel25))
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(szekcio_nezet, javax.swing.GroupLayout.PREFERRED_SIZE, 401, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(layout.createSequentialGroup()
                                        .addGap(28, 28, 28)
                                        .addComponent(teljes_nezet, javax.swing.GroupLayout.PREFERRED_SIZE, 415, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addGroup(layout.createSequentialGroup()
                                        .addGap(24, 24, 24)
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(Szekcio_nagyito, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(jLabel23)))
                                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(Teljes_nagyito, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(jLabel24))))))
                        .addContainerGap())
                    .addGroup(layout.createSequentialGroup()
                        .addGap(17, 17, 17)
                        .addComponent(jSeparator3))
                    .addComponent(jTabbedPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)))
        );

        jTabbedPane1.getAccessibleContext().setAccessibleName("Rácsszerkezet beállítás");

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void drotvaz_kivalasztoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_drotvaz_kivalasztoActionPerformed
        float totalsuly = 0;
        float szekciosuly;
        sql_lemento.setEnabled(true);
        szekciok.removeAllItems();
        racs.nev = drotvazak.getSelectedItem().toString();
        racs.adatbeolvaso();
        csomopontlista_tablatorlo();
        rudlista_tablatorlo();
        racskozok_tablatorlo();
        racs.csomopont.clear();
        racs.rud.clear();
        csomopontszam.setText("0");
        rudszam.setText("0");
        szekciok.addItem("Válassz");
        if (racs.racsalap.size() != 0) {
            // vannak rácselemek
            for (int i = 0; i < racs.racsalap.size(); i++) {
                szekciok.addItem(racs.nev + " - szekció: " + i);
            }
            //System.out.println("elotte:"+racs.csomopont.size());
            racs.racselemek();
            racs.racsrudvastagsag();
            racs.racsalap_rudsuly();
            szekcioadatok_tablatolto();
            //System.out.println("utana:"+racs.csomopont.size());
            //racs.racsalap_adatkijelzo();
            csomopontszam.setText(String.valueOf(racs.csomopont.size()));
            rudszam.setText(String.valueOf(racs.rud.size()));
            // kirajzoltatás    
            racskozok_torlo();
            racselemek_kijelzo_torles();
            szekcio_keptorlo();
            // A teljes rajz színei
            racs.szinbeallito(true, szekciok.getSelectedIndex() - 1, 0, vastagvonalak.isSelected());
            // A szekciórajz sinei
            racs.szinbeallito(false, szekciok.getSelectedIndex() - 1, kozok.getSelectedIndex() + 1, vastagvonalak.isSelected());
            teljes_kepkitevo();
            szekciohossz.setText("");
            for (int i = 0; i < racs.racsalap.size(); i++) {
                szekciosuly = 0;
                for (int j = 1; j <= 8; j++) {
                    szekciosuly += (racs.szelvenyrudhossz[i][j] * racs.rudsuly[i][j]) / 1000;
                    //System.out.println("Rács:"+racs.nev+" szekcio:"+i+" j:"+j+" hossz:"+racs.szelvenyrudhossz[i][j]+" suly:"+racs.rudsuly[i][j]);
                }
                totalsuly += szekciosuly;
                //System.out.println("Szekciosuly:"+szekciosuly);
            }
            teljes_suly.setText(String.format("%.0f", totalsuly));
            szekcio_suly.setText("");
        }
    }//GEN-LAST:event_drotvaz_kivalasztoActionPerformed

    private void racselemek_kijelzo_torles() {
        ftipus4.setEnabled(false);
        ftipus5.setEnabled(false);
        ftipus6.setEnabled(false);
        ftipus7.setEnabled(false);
        ftipus8.setEnabled(false);
        ftipus1.setMaximum(1);
        ftipus2.setMaximum(0);
        ftipus3.setMaximum(0);
        ftipus4.setMaximum(0);
        ftipus5.setMaximum(0);
        ftipus6.setMaximum(0);
        ftipus7.setMaximum(0);
        ftipus8.setMaximum(0);
        ftipus1.setValue(1);
        ftipus2.setValue(0);
        ftipus3.setValue(0);
        ftipus4.setValue(0);
        ftipus5.setValue(0);
        ftipus6.setValue(0);
        ftipus7.setValue(0);
        ftipus8.setValue(0);
        ftipus1text.setText("");
        ftipus2text.setText("");
        ftipus3text.setText("");
        ftipus4text.setText("");
        ftipus5text.setText("");
        ftipus6text.setText("");
        ftipus7text.setText("");
        ftipus8text.setText("");
    }

    private void ftipus_maximumok() {
        // A tipusnak megfelelő csúszka-maximumok beállítása
        if (szekciok.getSelectedIndex() > 0) {
            if (racs.racsalap.get(szekciok.getSelectedIndex() - 1).getIrany() == 1) {
                // függőleges a szekció
                ftipus1.setMaximum(racs.maximumok[0][0]);
                ftipus2.setMaximum(racs.maximumok[0][1]);
                ftipus3.setMaximum(racs.maximumok[0][2]);
                ftipus4.setMaximum(racs.maximumok[0][3]);
                ftipus5.setMaximum(racs.maximumok[0][4]);
                ftipus6.setMaximum(racs.maximumok[0][5]);
                ftipus7.setMaximum(racs.maximumok[0][6]);
                ftipus8.setMaximum(racs.maximumok[0][7]);
            }
            if (racs.racsalap.get(szekciok.getSelectedIndex() - 1).getIrany() == 2) {
                // vízszintes a szekció
                ftipus1.setMaximum(racs.maximumok[1][0]);
                ftipus2.setMaximum(racs.maximumok[1][1]);
                ftipus3.setMaximum(racs.maximumok[1][2]);
                ftipus4.setMaximum(racs.maximumok[1][3]);
                ftipus5.setMaximum(racs.maximumok[1][4]);
                ftipus6.setMaximum(racs.maximumok[1][5]);
                ftipus7.setEnabled(false);
                ftipus8.setEnabled(false);
            }
            if (racs.racsalap.get(szekciok.getSelectedIndex() - 1).getIrany() == 3) {
                // kéttámaszú a szekció
                ftipus1.setMaximum(racs.maximumok[2][0]);
                ftipus2.setMaximum(racs.maximumok[2][1]);
                ftipus3.setMaximum(racs.maximumok[2][2]);
                ftipus4.setEnabled(false);
                ftipus5.setEnabled(false);
                ftipus6.setEnabled(false);
                ftipus7.setEnabled(false);
                ftipus8.setEnabled(false);
            }
        }
    }

    private void racselemek_kijelzo() {
        racselemek_kijelzo_torles();
        if (szekciok.getSelectedIndex() != 0) {
            ftipus4.setEnabled(true);
            ftipus5.setEnabled(true);
            ftipus6.setEnabled(true);
            ftipus7.setEnabled(true);
            ftipus8.setEnabled(true);
            ftipus_maximumok();
            ftipus1.setValue(racs.racselemek[1]);
            ftipus2.setValue(racs.racselemek[2]);
            ftipus3.setValue(racs.racselemek[3]);
            ftipus4.setValue(racs.racselemek[4]);
            ftipus5.setValue(racs.racselemek[5]);
            ftipus6.setValue(racs.racselemek[6]);
            ftipus7.setValue(racs.racselemek[7]);
            ftipus8.setValue(racs.racselemek[8]);
            ftipus1text.setText(String.valueOf(racs.racselemek[1]));
            ftipus2text.setText(String.valueOf(racs.racselemek[2]));
            ftipus3text.setText(String.valueOf(racs.racselemek[3]));
            ftipus4text.setText(String.valueOf(racs.racselemek[4]));
            ftipus5text.setText(String.valueOf(racs.racselemek[5]));
            ftipus6text.setText(String.valueOf(racs.racselemek[6]));
            ftipus7text.setText(String.valueOf(racs.racselemek[7]));
            ftipus8text.setText(String.valueOf(racs.racselemek[8]));
        }
    }

    private void kisrajz_kepkitevo() {
        if (vizszintes.isSelected()) {
            kisrajz.setIcon(new javax.swing.ImageIcon(getClass().getResource("/statika/vizszintes.png")));
        } else if (fuggoleges.isSelected()) {
            kisrajz.setIcon(new javax.swing.ImageIcon(getClass().getResource("/statika/fuggoleges.png")));
        } else {
            kisrajz.setIcon(new javax.swing.ImageIcon(getClass().getResource("/statika/kettamaszu.png")));
        }
        kisrajz.updateUI();
    }

    private void szekcio_kepkitevo() {
        if (szekciok.getSelectedIndex() > 0) {
            racs.pngrajz(szekciok.getSelectedIndex() - 1, vastagvonalak.isSelected(), kozok.getSelectedIndex() + 1);
            ImageIcon icon = new ImageIcon(racs.bi1);
            icon.getImage().flush();
            szekcio_nezet.setIcon(icon);
            szekcio_nezet.updateUI();
        }
    }

    private void szekcio_keptorlo() {
        ImageIcon icon = new ImageIcon(racs.bi3);
        icon.getImage().flush();
        szekcio_nezet.setIcon(icon);
        szekcio_nezet.updateUI();
    }

    private void teljes_kepkitevo() {
        racs.pngrajz(-1, vastagvonalak.isSelected(), szekciok.getSelectedIndex() - 1);
        ImageIcon icon = new ImageIcon(racs.bi2);
        icon.getImage().flush();
        teljes_nezet.setIcon(icon);
        teljes_nezet.updateUI();
    }

    private void ujelemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ujelemActionPerformed
        DefaultTableModel tableModel = (DefaultTableModel) szekcioadatok.getModel();
        racs.nev = ujelemnev.getText();
        ujelemnev.removeAll();
        // A szekcióadatok tábla kitörlése
        int j = tableModel.getRowCount();
        if (j > 0) {
            for (int k = 0; k < j; k++) {
                tableModel.removeRow(0);
            }
        }
        csomopontlista_tablatorlo();
        rudlista_tablatorlo();
        racskozok_tablatorlo();
        szekciohossz.setText("0");
        racs.racsalap.clear();
        racs.racsalap1.clear();
        racs.rud.clear();
        racs.csomopont.clear();
        racselemek_kijelzo_torles();
    }//GEN-LAST:event_ujelemActionPerformed

    private void racskozok_torlo() {
        DefaultTableModel tableModel = (DefaultTableModel) racskozok.getModel();
        int j = tableModel.getRowCount();
        if (j > 0) {
            for (int k = 0; k < j; k++) {
                tableModel.removeRow(0);
            }
        }
    }

    private void elemhozzadoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_elemhozzadoActionPerformed
        int j = 1;
        Racsalap ujracsalap = new Racsalap();
        mentes.setEnabled(true);
        if (!racs.nev.equals("")) {
            //System.out.println("Szekcio:"+racs.szekcioszam);
            ujracsalap.setSzekcio(racs.szekcioszam);
            ujracsalap.setMagassag(Integer.parseInt(Magassag.getText()));
            ujracsalap.setAlsoszelxy(Integer.parseInt(alsoszelxy.getText()));
            ujracsalap.setAlsoszelyz(Integer.parseInt(alsoszelyz.getText()));
            racs.alsoxy = Float.parseFloat(alsoszelxy.getText());
            racs.alsoyz = Float.parseFloat(alsoszelyz.getText());
            ujracsalap.setFelsoszelxy(Integer.parseInt(felsoszelxy.getText()));
            ujracsalap.setFelsoszelyz(Integer.parseInt(felsoszelyz.getText()));
            ujracsalap.setX(Integer.parseInt(kapcsx.getText()));
            ujracsalap.setY(Integer.parseInt(kapcsy.getText()));
            ujracsalap.setZ(Integer.parseInt(kapcsz.getText()));
            ujracsalap.setEltolasxy(Integer.parseInt(eltolasxy.getText()));
            ujracsalap.setEltolasyz(Integer.parseInt(eltolasyz.getText()));
            ujracsalap.setTeljes(Integer.parseInt(konzolhossz.getText()));
            if (vizszintes.isSelected()) {
                ujracsalap.setIrany(2);
            } else if (fuggoleges.isSelected()) {
                ujracsalap.setIrany(1);
            } else {
                ujracsalap.setIrany(3);
            }
            racs.racsalap.add(ujracsalap);
            racs.szekcioszam = racs.racsalap.size();
            // A Jtable-be való beleírás
            szekcioadatok_tablatolto();

            // A szekciókon belüli rácselemek
            if (vizszintes.isSelected()) {
                j = 2;
            }
            if (kettamaszu.isSelected()) {
                j = 3;
            }
            if (elemszamok.getText().length() > 0) {
                for (int i = 1; i <= Integer.parseInt(elemszamok.getText()); i++) {
                    //Racsalap1 ujracsalap1 = new Racsalap1();
                    racs.ujracsalap1 = null;        // adattörlés                    
                    racs.ujracsalap1.setRacs1(1);
                    racs.ujracsalap1.setRacs2(1);
                    racs.racselem1_alapadatok(i, Integer.parseInt(elemszamok.getText()), j,
                            Integer.parseInt(Magassag.getText()),
                            Integer.parseInt(alsoszelxy.getText()),
                            Integer.parseInt(felsoszelxy.getText()),
                            Integer.parseInt(alsoszelyz.getText()),
                            Integer.parseInt(felsoszelyz.getText()),
                            Integer.parseInt(eltolasxy.getText()),
                            Integer.parseInt(eltolasyz.getText()),
                            Integer.parseInt(kapcsx.getText()),
                            Integer.parseInt(kapcsy.getText()),
                            Integer.parseInt(kapcsz.getText()));

                    racs.racsalap1.add(racs.ujracsalap1);
                    /*System.out.println("i:"+(i-1)+"  Szekcio:"+racs.racsalap1.get(i ).getSzekcio()+"  koz:" + racs.racsalap1.get(i ).getKoz());
                      System.out.println(); */
                    //racs.kozszam = racs.racsalap1.size();
                    // A köz sorszáma
                    //ujracsalap = null;
                }
            }
            // A szekciok lista feltöltése
            szekciok.removeAll();
            szekciok.addItem("Válassz");
            for (int i = 0; i < racs.racsalap.size(); i++) {
                szekciok.addItem(racs.nev + " - szekció: " + String.valueOf(i));
            }
        }
        Magassag.setText("0");
        elemszamok.setText("1");
        alsoszelxy.setText("0");
        alsoszelyz.setText("0");
        felsoszelxy.setText("0");
        felsoszelyz.setText("0");
        kapcsx.setText("0");
        kapcsy.setText("0");
        kapcsz.setText("0");
        eltolasxy.setText("0");
        eltolasyz.setText("0");
        konzolhossz.setText("0");
        mentes.setEnabled(true);
    }//GEN-LAST:event_elemhozzadoActionPerformed

    private void szekcioadatok_tablatorlo() {
        DefaultTableModel tableModel = (DefaultTableModel) szekcioadatok.getModel();
        int i = tableModel.getRowCount();
        if (i > 0) {
            for (int k = 0; k < i; k++) {
                tableModel.removeRow(0);
            }
        }
    }

    private void racskozok_tablatorlo() {
        DefaultTableModel tableModel = (DefaultTableModel) racskozok.getModel();
        int i = tableModel.getRowCount();
        if (i > 0) {
            for (int k = 0; k < i; k++) {
                tableModel.removeRow(0);
            }
        }
    }

    private void csomopontlista_tablatorlo() {
        DefaultTableModel tableModel = (DefaultTableModel) csomopontlista.getModel();
        int i = tableModel.getRowCount();
        if (i > 0) {
            for (int k = 0; k < i; k++) {
                tableModel.removeRow(0);
            }
        }
    }

    private void rudlista_tablatorlo() {
        DefaultTableModel tableModel = (DefaultTableModel) rudlista.getModel();
        int i = tableModel.getRowCount();
        if (i > 0) {
            for (int k = 0; k < i; k++) {
                tableModel.removeRow(0);
            }
        }
    }

    public void setUpTypeColumn(JTable table, TableColumn typeColumn) {
        //Set up the editor for the sport cells.
        JComboBox comboBox = new JComboBox();
        comboBox.addItem("Függőleges");
        comboBox.addItem("Vízszintes");
        comboBox.addItem("Kéttámaszú");
        typeColumn.setCellEditor(new DefaultCellEditor(comboBox));
        //Set up tool tips for the sport cells.
        DefaultTableCellRenderer renderer = new DefaultTableCellRenderer();
        renderer.setToolTipText("Válaszd ki a típusát");
        renderer.setBackground(Color.yellow);
        typeColumn.setCellRenderer(renderer);
    }

    public void setUpProfilColumn(JTable table, TableColumn typeColumn) {
        // A szelvénytár beolvasása
        JComboBox comboBox = new JComboBox();
        for (int i = 0; i < racs.szelveny.size(); i++) {
            comboBox.addItem(racs.szelveny.get(i).getNev());
        }
        typeColumn.setCellEditor(new DefaultCellEditor(comboBox));
        //Set up tool tips for the sport cells.
        DefaultTableCellRenderer renderer = new DefaultTableCellRenderer();
        renderer.setToolTipText("Válaszd ki a szelvény típusát");
        renderer.setBackground(Color.yellow);
        typeColumn.setCellRenderer(renderer);
    }

    private void szekcioadatok_tablatolto() {
        DefaultTableModel tableModel = (DefaultTableModel) szekcioadatok.getModel();
        szekcioadatok_tablatorlo();
        String[] data = new String[42];
        float teljessuly = 0f;
        float reszsuly;
        float szekciosuly;
        int csomopontok;
        int rudak;
        teljes_suly.setText(String.format("%.0f", teljessuly));
        for (int i = 0; i < racs.racsalap.size(); i++) {
            //szekcio(0),magassag(1),alsoxy(2),alsoyz(3),felsoxy(4),felsoyz(5),diffx(6),diffy(7),diffz(8),eltolasxy(9),eltolasyz(10),konzol(11),fugg/vízsz(12)
            data[0] = String.valueOf(racs.racsalap.get(i).getSzekcio());
            data[1] = "Függőleges";
            if (racs.racsalap.get(i).getIrany() == 2) {
                data[1] = "Vízszintes";
            }
            if (racs.racsalap.get(i).getIrany() == 3) {
                data[1] = "Kéttámaszú";
            }
            data[2] = String.valueOf(racs.racsalap.get(i).getMagassag());
            data[3] = String.valueOf(racs.racsalap.get(i).getAlsoszelxy());
            data[4] = String.valueOf(racs.racsalap.get(i).getAlsoszelyz());
            data[5] = String.valueOf(racs.racsalap.get(i).getFelsoszelxy());
            data[6] = String.valueOf(racs.racsalap.get(i).getFelsoszelyz());
            data[7] = String.valueOf(racs.racsalap.get(i).getX());
            data[8] = String.valueOf(racs.racsalap.get(i).getY());
            data[9] = String.valueOf(racs.racsalap.get(i).getZ());
            data[10] = String.valueOf(racs.racsalap.get(i).getEltolasxy());
            data[11] = String.valueOf(racs.racsalap.get(i).getEltolasyz());
            data[12] = String.valueOf(racs.racsalap.get(i).getTeljes());
            // A szelvénynevek
            data[13] = String.valueOf(racs.racsalap.get(i).getNev1());
            data[14] = String.valueOf(racs.racsalap.get(i).getNev2());
            data[15] = String.valueOf(racs.racsalap.get(i).getNev3());
            data[16] = String.valueOf(racs.racsalap.get(i).getNev4());
            data[17] = String.valueOf(racs.racsalap.get(i).getNev5());
            data[18] = String.valueOf(racs.racsalap.get(i).getNev6());
            data[19] = String.valueOf(racs.racsalap.get(i).getNev7());
            data[20] = String.valueOf(racs.racsalap.get(i).getNev8());
            for (int j = 0; j < racs.racsalap1.size(); j++) {
                if (racs.racsalap.get(i).getSzekcio() == racs.racsalap1.get(j).getSzekcio()
                        && racs.racsalap1.get(j).getKoz() == 1) {

                    for (int k = 13; k <= 20; k++) {
                        if (data[k].equals("null") || data[k].equals("")) {
                            data[k] = "";
                            if ((k == 13) && (racs.racsalap1.get(j).getRacs1() > 0)) {
                                data[k] = " ";
                            }
                            if ((k == 14) && (racs.racsalap1.get(j).getRacs2() > 0)) {
                                data[k] = " ";
                            }
                            if ((k == 15) && (racs.racsalap1.get(j).getRacs3() > 0)) {
                                data[k] = " ";
                            }
                            if ((k == 16) && (racs.racsalap1.get(j).getRacs4() > 0)) {
                                data[k] = " ";
                            }
                            if ((k == 17) && (racs.racsalap1.get(j).getRacs5() > 0)) {
                                data[k] = " ";
                            }
                            if ((k == 18) && (racs.racsalap1.get(j).getRacs6() > 0)) {
                                data[k] = " ";
                            }
                            if ((k == 19) && (racs.racsalap1.get(j).getRacs7() > 0)) {
                                data[k] = " ";
                            }
                            if ((k == 20) && (racs.racsalap1.get(j).getRacs8() > 0)) {
                                data[k] = " ";
                            }
                        } else {
                            setUpProfilColumn(szekcioadatok, szekcioadatok.getColumnModel().getColumn(k));
                        }
                        // A cella van sárgára színezve                       
                        szekcioadatok.getColumnModel().getColumn(k).setCellRenderer(new RenderYellowRed());
                    }
                    // A profilhosszak
                    data[21] = String.valueOf(racs.racsalap1.get(j).getHossz1());
                    data[22] = String.valueOf(racs.racsalap1.get(j).getHossz2());
                    data[23] = String.valueOf(racs.racsalap1.get(j).getHossz3());
                    data[24] = String.valueOf(racs.racsalap1.get(j).getHossz4());
                    data[25] = String.valueOf(racs.racsalap1.get(j).getHossz5());
                    data[26] = String.valueOf(racs.racsalap1.get(j).getHossz6());
                    data[27] = String.valueOf(racs.racsalap1.get(j).getHossz7());
                    data[28] = String.valueOf(racs.racsalap1.get(j).getHossz8());
                    szekciosuly = 0;
                    for (int k = 1; k <= 8; k++) {
                        if (data[k + 12].length() > 1) {
                            reszsuly = (Float.parseFloat(data[k + 20]) * racs.rudsuly[j][k]) / 1000;
                            data[k + 28] = String.valueOf(reszsuly);
                            teljessuly += reszsuly;
                            szekciosuly += reszsuly;
                        }
                    }
                    // A csomópont számok
                    csomopontok = 0;
                    for (int k = 0; k < racs.csomopont.size(); k++) {
                        if (racs.csomopont.get(k).getSzekcio() == j) {
                            csomopontok++;
                        }
                    }
                    data[37] = String.valueOf(csomopontok);
                    // A rúd számok
                    rudak = 0;
                    for (int k = 0; k < racs.csomopont.size(); k++) {
                        if (racs.rud.get(k).getSzekcio() == j) {
                            rudak++;
                        }
                    }
                    data[38] = String.valueOf(rudak);
                    // A szekciósúly
                    data[39] = String.valueOf(szekciosuly);
                }
            }
            tableModel.addRow(data);
            tableModel.setValueAt(false, i, 40);
            /*if (racs.racsalap.get(i ).getIrany() == 1) {
             tableModel.setValueAt(true, i , 12);
             } else {
             tableModel.setValueAt(false, i , 12);
             } */
        }
        // A 2-4 -es oszlopok középre igazítása
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(ujelemnev.CENTER);
        for (int k = 0; k < 13; k++) {
            szekcioadatok.getColumnModel().getColumn(k).setCellRenderer(centerRenderer);
        }
        for (int k = 21; k < 41; k++) {
            szekcioadatok.getColumnModel().getColumn(k).setCellRenderer(centerRenderer);
        }
        // A tábla oszlopszélességei
        szekcioadatok.setAutoResizeMode(szekcioadatok.AUTO_RESIZE_OFF);
        szekcioadatok.getColumnModel().getColumn(0).setPreferredWidth(35);
        szekcioadatok.getColumnModel().getColumn(1).setPreferredWidth(70);
        szekcioadatok.getColumnModel().getColumn(2).setPreferredWidth(45);
        szekcioadatok.getColumnModel().getColumn(3).setPreferredWidth(45);
        szekcioadatok.getColumnModel().getColumn(4).setPreferredWidth(45);
        szekcioadatok.getColumnModel().getColumn(5).setPreferredWidth(50);
        szekcioadatok.getColumnModel().getColumn(6).setPreferredWidth(50);
        szekcioadatok.getColumnModel().getColumn(7).setPreferredWidth(45);
        szekcioadatok.getColumnModel().getColumn(8).setPreferredWidth(45);
        szekcioadatok.getColumnModel().getColumn(9).setPreferredWidth(45);
        szekcioadatok.getColumnModel().getColumn(10).setPreferredWidth(60);
        szekcioadatok.getColumnModel().getColumn(11).setPreferredWidth(60);
        szekcioadatok.getColumnModel().getColumn(12).setPreferredWidth(45);
        // A logikai kapcsolók
        szekcioadatok.getColumnModel().getColumn(40).setPreferredWidth(45);
        szekcioadatok.setModel(tableModel);
        szekcioadatok.setShowGrid(true);
        // Itt a teljes oszlop sárga
        setUpTypeColumn(szekcioadatok, szekcioadatok.getColumnModel().getColumn(1));
        teljes_suly.setText(String.format("%.0f", teljessuly));
    }

    final class RenderYellowRed extends DefaultTableCellRenderer {

        RenderYellowRed() {
            setHorizontalAlignment(SwingConstants.RIGHT);
        }

        @Override
        public Component getTableCellRendererComponent(
                JTable aTable, Object aNumberValue, boolean aIsSelected,
                boolean aHasFocus, int aRow, int aColumn
        ) {
            /* 
             * Implementation Note :
             * It is important that no 'new' objects be present in this 
             * implementation (excluding exceptions):
             * if the table is large, then a large number of objects would be 
             * created during rendering.
             */
            if (aNumberValue == null) {
                return this;
            }
            Component renderer = super.getTableCellRendererComponent(
                    aTable, aNumberValue, aIsSelected, aHasFocus, aRow, aColumn);
            if (aTable.getModel().getValueAt(aRow, aColumn) != "") {
                renderer.setForeground(Color.red);
                renderer.setBackground(Color.yellow);
            } else {
                //renderer.setForeground(fDarkGreen);
                renderer.setForeground(Color.black);
                renderer.setBackground(Color.white);
            }
            return this;
        }
        // PRIVATE 

        //the default green is too bright and illegible
        private Color fDarkGreen = Color.green.darker();
    }

    private void csomopontlista_tablatolto() {
        DefaultTableModel tableModel = (DefaultTableModel) csomopontlista.getModel();
        if (szekciok.getSelectedIndex() > 0) {
            csomopontlista_tablatorlo();
            String[] data = new String[5];
            int k = 1;
            for (int i = 0; i < racs.csomopont.size(); i++) {
                if (racs.csomopont.get(i).getSzekcio() == szekciok.getSelectedIndex() - 1) {
                    data[0] = String.valueOf(k++);
                    data[1] = String.valueOf(i);
                    data[2] = String.valueOf(racs.csomopont.get(i).getCsomopont().getX());  // x
                    data[3] = String.valueOf(racs.csomopont.get(i).getCsomopont().getY());  // y
                    data[4] = String.valueOf(racs.csomopont.get(i).getCsomopont().getZ());  // z
                    tableModel.addRow(data);
                    if (racs.csomopont.get(i).getKijelzes() == 1) {
                        tableModel.setValueAt(true, k - 2, 5);
                    } else {
                        tableModel.setValueAt(false, k - 2, 5);
                    }
                }
            }
            DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
            centerRenderer.setHorizontalAlignment(ujelemnev.CENTER);
            csomopontlista.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
            csomopontlista.getColumnModel().getColumn(1).setCellRenderer(centerRenderer);
            csomopontlista.getColumnModel().getColumn(2).setCellRenderer(centerRenderer);
            csomopontlista.getColumnModel().getColumn(3).setCellRenderer(centerRenderer);
            csomopontlista.getColumnModel().getColumn(4).setCellRenderer(centerRenderer);
            // A tábla oszlopszélességei
            csomopontlista.setAutoResizeMode(csomopontlista.AUTO_RESIZE_OFF);
            csomopontlista.getColumnModel().getColumn(0).setPreferredWidth(40);
            csomopontlista.getColumnModel().getColumn(1).setPreferredWidth(40);
            csomopontlista.getColumnModel().getColumn(2).setPreferredWidth(60);
            csomopontlista.getColumnModel().getColumn(3).setPreferredWidth(60);
            csomopontlista.getColumnModel().getColumn(4).setPreferredWidth(60);
            csomopontlista.setModel(tableModel);
            csomopontlista.setShowGrid(true);
        }
    }

    private void rudlista_tablatolto() {
        DefaultTableModel tableModel = (DefaultTableModel) rudlista.getModel();
        if (szekciok.getSelectedIndex() > 0) {
            int k = 1;
            rudlista_tablatorlo();
            String[] data = new String[8];
            for (int i = 0; i < racs.rud.size(); i++) {
                if (racs.rud.get(i).getSzekcio() == szekciok.getSelectedIndex() - 1) {
                    data[0] = String.valueOf(k++);
                    data[1] = String.valueOf(i);
                    data[2] = String.valueOf(racs.rud.get(i).getRud().getKezdocsp());  // kezdcsp
                    data[3] = String.valueOf(racs.rud.get(i).getRud().getVegecsp());  // vegecsp
                    data[4] = String.valueOf(racs.rud.get(i).getRud().getSzelveny());  // szelvény
                    //data[4] = String.valueOf(racs.rudnevek[szekciok.getSelectedIndex() - 1][racs.rud.get(i).getTipus()]);  // szelvény
                    data[5] = String.valueOf(racs.rudhossz(racs.rud.get(i).getRud().getKezdocsp(), racs.rud.get(i).getRud().getVegecsp()));  // hossz
                    data[6] = String.format("%.2f", (racs.rudhossz(racs.rud.get(i).getRud().getKezdocsp(), racs.rud.get(i).getRud().getVegecsp())
                            * racs.rudsuly[szekciok.getSelectedIndex() - 1][racs.rud.get(i).getTipus()]) / 1000);  // súly
                    tableModel.addRow(data);
                    if (racs.rud.get(i).getKijelzes() == 1) {
                        tableModel.setValueAt(true, k - 2, 7);
                    } else {
                        tableModel.setValueAt(false, k - 2, 7);
                    }
                }
            }
            DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
            centerRenderer.setHorizontalAlignment(ujelemnev.CENTER);
            rudlista.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
            rudlista.getColumnModel().getColumn(1).setCellRenderer(centerRenderer);
            rudlista.getColumnModel().getColumn(2).setCellRenderer(centerRenderer);
            rudlista.getColumnModel().getColumn(3).setCellRenderer(centerRenderer);
            rudlista.getColumnModel().getColumn(4).setCellRenderer(centerRenderer);
            rudlista.getColumnModel().getColumn(5).setCellRenderer(centerRenderer);
            rudlista.getColumnModel().getColumn(6).setCellRenderer(centerRenderer);
            // A tábla oszlopszélességei
            rudlista.setAutoResizeMode(rudlista.AUTO_RESIZE_OFF);
            rudlista.getColumnModel().getColumn(0).setPreferredWidth(30);
            rudlista.getColumnModel().getColumn(1).setPreferredWidth(30);
            rudlista.getColumnModel().getColumn(2).setPreferredWidth(35);
            rudlista.getColumnModel().getColumn(3).setPreferredWidth(35);
            rudlista.getColumnModel().getColumn(4).setPreferredWidth(80);
            rudlista.getColumnModel().getColumn(5).setPreferredWidth(50);
            rudlista.getColumnModel().getColumn(6).setPreferredWidth(50);
            rudlista.getColumnModel().getColumn(7).setPreferredWidth(40);
            rudlista.setModel(tableModel);
            rudlista.setShowGrid(true);
        }
    }

    private void szekcio_kivalasztoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_szekcio_kivalasztoActionPerformed
        DefaultTableModel tableModel = (DefaultTableModel) racskozok.getModel();
        float szekciosuly = 0, totalsuly = 0;
        String szoveg;
        for (int i = 0; i < racs.racsalap.size(); i++) {
            for (int j = 1; j <= 8; j++) {
                totalsuly += (racs.szelvenyrudhossz[i][j] * racs.rudsuly[i][j]) / 1000;
                if (i == szekciok.getSelectedIndex() - 1) {
                    szekciosuly += (racs.szelvenyrudhossz[i][j] * racs.rudsuly[i][j]) / 1000;
                }
                //System.out.println("i:"+i+" j:"+j+"  nev:'"+racs.rudnevek[i][j]+"'  suly:"+racs.rudsuly[i][j]);
            }
        }
        for (int i = 0; i < racs.rud.size(); i++) {
            racs.rud.get(i).setKijelzes(0);
        }
        for (int i = 0; i < racs.csomopont.size(); i++) {
            racs.csomopont.get(i).setKijelzes(0);
        }
        //teljes_suly.setText(String.format("%.0f", totalsuly));
        szekcio_suly.setText(String.format("%.0f", szekciosuly));
        racs.kozbeolvaso(szekciok.getSelectedIndex() - 1, 1);
        csomopontlista_tablatolto();
        rudlista_tablatolto();
        /*System.out.println("1:");
         for (int i = 1; i <= racs.rudindex; i++) {
         System.out.println("Rud:"+i+" Rud[4]:"+racs.rud[i][4]+"  Rud[5]:"+racs.rud[i][5]);
         }*/
        kozok.removeAllItems();
        // Az aktuális szelvénynév kijelölése
        //System.out.println("Szekcio"+(szekciok.getSelectedIndex() - 1)+" rud:"+racs.rudnevek[szekciok.getSelectedIndex() - 1][1]);
        //rajzolás
        racselemek_kijelzo();
        racskozok_torlo();
        // A racs köz-méreteinek beolvasása
        if (szekciok.getSelectedIndex() > 0) {
            racs.aktualis_szekcio = szekciok.getSelectedIndex() - 1;
            //System.out.println("Szekcio:"+szekciok.getSelectedIndex()-1);
            for (int j = 0; j < racs.racsalap.size(); j++) {
                //System.out.println("Szekcio:"+j+"  get:"+racs.racsalap.get(j ).getSzekcio()+"  ind:"+szekciok.getSelectedIndex()-1);
                if (racs.racsalap.get(j).getSzekcio() == szekciok.getSelectedIndex() - 1) {
                    szekciohossz.setText(String.valueOf(racs.racsalap.get(j).getMagassag()));
                }
            }
            // A közök legördülő feltöltése
            String[] data = new String[2];
            for (int j = 0; j < racs.racsalap1.size(); j++) {
                //System.out.println("Szekcio:"+j+"  get:"+racs.racsalap1.get(j ).getSzekcio()+"  ind:"+szekciok.getSelectedIndex()-1);
                if (racs.racsalap1.get(j).getSzekcio() == szekciok.getSelectedIndex() - 1) {
                    szoveg = "Köz : " + String.valueOf(racs.racsalap1.get(j).getKoz());
                    //System.out.println("koz:"+szoveg);
                    kozok.addItem(szoveg);
                    data[0] = String.valueOf(racs.racsalap1.get(j).getKoz());
                    data[1] = String.valueOf(racs.racsalap1.get(j).getMagassag());
                    tableModel.addRow(data);
                }
            }
            DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
            centerRenderer.setHorizontalAlignment(ujelemnev.CENTER);
            racskozok.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
            racskozok.getColumnModel().getColumn(1).setCellRenderer(centerRenderer);
            // A tábla oszlopszélességei
            racskozok.setAutoResizeMode(racskozok.AUTO_RESIZE_OFF);
            racskozok.getColumnModel().getColumn(0).setPreferredWidth(70);
            racskozok.getColumnModel().getColumn(1).setPreferredWidth(150);
            racskozok.setModel(tableModel);
            racskozok.setShowGrid(true);
        }
        // A teljes rajz színei
        racs.szinbeallito(true, szekciok.getSelectedIndex() - 1, 0, vastagvonalak.isSelected());
        // A szekciórajz színei
        racs.szinbeallito(false, szekciok.getSelectedIndex() - 1, kozok.getSelectedIndex() + 1, vastagvonalak.isSelected());
        teljes_kepkitevo();
        szekcio_kepkitevo();
        racs.mintamasolo(0, 1);
        racselemek_kijelzo_torles();
        racselemek_kijelzo();
    }//GEN-LAST:event_szekcio_kivalasztoActionPerformed

    private void ElemmodositoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ElemmodositoActionPerformed
        // TODO add your handling code here:
        int sorszam = 0;
        int regiid;
        boolean valtozas = false;
        DefaultTableModel tableModel = (DefaultTableModel) szekcioadatok.getModel();
        // adattörlés
        racs.racsalap.clear();
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            //System.out.println("'"+tableModel.getValueAt(i, 12)+"'  '"+tableModel.getValueAt(i, 13)+"'");
            if ((Boolean) tableModel.getValueAt(i, 13) != true) {
                sorszam++;
                Racsalap ujracsalap = new Racsalap();
                ujracsalap.setSzekcio(sorszam);
                ujracsalap.setMagassag(Integer.parseInt(String.valueOf(tableModel.getValueAt(i, 1))));
                ujracsalap.setAlsoszelxy(Integer.parseInt(String.valueOf(tableModel.getValueAt(i, 2))));
                ujracsalap.setAlsoszelyz(Integer.parseInt(String.valueOf(tableModel.getValueAt(i, 3))));
                ujracsalap.setFelsoszelxy(Integer.parseInt(String.valueOf(tableModel.getValueAt(i, 4))));
                ujracsalap.setFelsoszelyz(Integer.parseInt(String.valueOf(tableModel.getValueAt(i, 5))));
                ujracsalap.setX(Integer.parseInt(String.valueOf(tableModel.getValueAt(i, 6))));
                ujracsalap.setY(Integer.parseInt(String.valueOf(tableModel.getValueAt(i, 7))));
                ujracsalap.setZ(Integer.parseInt(String.valueOf(tableModel.getValueAt(i, 8))));
                ujracsalap.setEltolasxy(Integer.parseInt(String.valueOf(tableModel.getValueAt(i, 9))));
                ujracsalap.setEltolasyz(Integer.parseInt(String.valueOf(tableModel.getValueAt(i, 10))));
                ujracsalap.setTeljes(Integer.parseInt(String.valueOf(tableModel.getValueAt(i, 11))));
                ujracsalap.setIrany(1);
                if (tableModel.getValueAt(i, 12).equals("Vízszintes")) {
                    ujracsalap.setIrany(2);
                }
                if (tableModel.getValueAt(i, 12).equals("Kéttámaszú")) {
                    ujracsalap.setIrany(3);
                }
                racs.racsalap.add(ujracsalap);
                racs.szekcioszam = racs.racsalap.size();
            } else {
                valtozas = true;
                // A rácalap1 sorainak törlésre jelölése
                for (int j = 0; j <= racs.racsalap1.size(); j++) {
                    if (racs.racsalap1.get(j).getSzekcio() == (i)) {
                        racs.racsalap1.get(j).setSzekcio(0);
                    }
                }
            }
        }
        if (valtozas) {
            for (Racsalap1 val : racs.racsalap1) {
                if (val.getSzekcio() == 0) {
                    racs.racsalap1.remove(val);
                    break;
                }
            }
            sorszam = 1;
            regiid = racs.racsalap1.get(1).getSzekcio();
            for (int j = 0; j <= racs.racsalap1.size(); j++) {
                if (regiid != racs.racsalap1.get(j).getSzekcio()) {
                    regiid = racs.racsalap1.get(j).getSzekcio();
                    sorszam++;
                }
                racs.racsalap1.get(j).setSzekcio(sorszam);
            }
        }
        /*
         // törlés nélküli adatbeolvasás
         for (int i = 1; i <= tableModel.getRowCount(); i++) {
         sorszam = (int)tableModel.getValueAt(i, 0);
         racs.racsalap.get(sorszam).setMagassag((int)tableModel.getValueAt(i, 1));
         racs.racsalap.get(sorszam).setAlsoszelxy((int)tableModel.getValueAt(i, 2));
         racs.racsalap.get(sorszam).setAlsoszelyz((int)tableModel.getValueAt(i, 3));
         racs.racsalap.get(sorszam).setFelsoszelxy((int)tableModel.getValueAt(i, 4));
         racs.racsalap.get(sorszam).setFelsoszelyz((int)tableModel.getValueAt(i, 5));
         racs.racsalap.get(sorszam).setX((int)tableModel.getValueAt(i, 6));
         racs.racsalap.get(sorszam).setY((int)tableModel.getValueAt(i, 7));
         racs.racsalap.get(sorszam).setZ((int)tableModel.getValueAt(i, 8));
         racs.racsalap.get(sorszam).setEltolasxy((int)tableModel.getValueAt(i, 9));
         racs.racsalap.get(sorszam).setEltolasyz((int)tableModel.getValueAt(i, 10));
         racs.racsalap.get(sorszam).setTeljes((int)tableModel.getValueAt(i, 11));
         }
         */
        racs.racselemek();
        // A Jtable újratöltése
        szekcioadatok_tablatolto();
        // Kirajzoltatás
        // A teljes rajz színei
        racs.szinbeallito(true, szekciok.getSelectedIndex() - 1, 0, vastagvonalak.isSelected());
        // A szekciórajz színei
        racs.szinbeallito(false, szekciok.getSelectedIndex() - 1, kozok.getSelectedIndex() + 1, vastagvonalak.isSelected());
        teljes_kepkitevo();
        szekcio_kepkitevo();
        mentes.setEnabled(true);
    }//GEN-LAST:event_ElemmodositoActionPerformed

    private void teljes_nezetMouseDragged(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_teljes_nezetMouseDragged
        if (SwingUtilities.isLeftMouseButton(evt)) {
            // A forgatás
            if ((racs.mx1 == 0) || (racs.my1 == 0)) {
                racs.mx1 = evt.getX();
                racs.my1 = evt.getY();
            } else {
                if (evt.getX() > racs.mx1) {
                    racs.forgatas[0][2]++;
                    if (racs.forgatas[0][2] > 360) {
                        racs.forgatas[0][2] = 0;
                    }
                    racs.mx1 = evt.getX();
                }
                if (evt.getX() < racs.mx1) {
                    racs.forgatas[0][2]--;
                    if (racs.forgatas[0][2] < 0) {
                        racs.forgatas[0][2] = 360;
                    }
                    racs.mx1 = evt.getX();
                }
                if (evt.getY() > racs.my1) {
                    racs.forgatas[0][3]--;
                    if (racs.forgatas[0][3] < 0) {
                        racs.forgatas[0][3] = 360;
                    }
                    racs.my1 = evt.getY();
                }
                if (evt.getY() < racs.my1) {
                    racs.forgatas[0][3]++;
                    if (racs.forgatas[0][3] > 360) {
                        racs.forgatas[0][3] = 0;
                    }
                    racs.my1 = evt.getY();
                }
                teljes_kepkitevo();
            }
        }
        if (SwingUtilities.isRightMouseButton(evt)) {
            //mozgatás                
            if ((racs.tx1 == 0) || (racs.ty1 == 0)) {
                racs.tx1 = evt.getX();
                racs.ty1 = evt.getY();
            } else {
                if (evt.getX() > racs.tx1) {
                    racs.kepkozep[0][0]++;
                    racs.tx1 = evt.getX();
                }
                if (evt.getX() < racs.tx1) {
                    racs.kepkozep[0][0]--;
                    racs.tx1 = evt.getX();
                }
                if (evt.getY() > racs.ty1) {
                    racs.kepkozep[0][1]++;
                    racs.ty1 = evt.getY();
                }
                if (evt.getY() < racs.ty1) {
                    racs.kepkozep[0][1]--;
                    racs.ty1 = evt.getY();
                }
                teljes_kepkitevo();
            }
        }
        if (SwingUtilities.isMiddleMouseButton(evt)) {
            // Alaphelyzet visszaállítás
            Teljes_nagyito.setValue(100);
            racs.kepkozep[0][0] = 0;
            racs.kepkozep[0][1] = 0;
            racs.forgatas[0][2] = 0;
            racs.forgatas[0][3] = 0;
            racs.kepnagyitas[0] = 1;
            teljes_kepkitevo();
        }
    }//GEN-LAST:event_teljes_nezetMouseDragged

    private void szekcio_nezetMouseDragged(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_szekcio_nezetMouseDragged
        if (SwingUtilities.isLeftMouseButton(evt)) {
            // A forgatás
            if ((racs.mx0 == 0) || (racs.my0 == 0)) {
                racs.mx0 = evt.getX();
                racs.my0 = evt.getY();
            } else {
                if (evt.getX() > racs.mx0) {
                    racs.forgatas[1][2]++;
                    if (racs.forgatas[1][2] > 360) {
                        racs.forgatas[1][2] = 0;
                    }
                    racs.mx0 = evt.getX();
                }
                if (evt.getX() < racs.mx0) {
                    racs.forgatas[1][2]--;
                    if (racs.forgatas[1][2] < 0) {
                        racs.forgatas[1][2] = 360;
                    }
                    racs.mx0 = evt.getX();
                }
                if (evt.getY() > racs.my0) {
                    racs.forgatas[1][3]--;
                    if (racs.forgatas[1][3] < 0) {
                        racs.forgatas[1][3] = 360;
                    }
                    racs.my0 = evt.getY();
                }
                if (evt.getY() < racs.my0) {
                    racs.forgatas[1][3]++;
                    if (racs.forgatas[1][3] > 360) {
                        racs.forgatas[1][3] = 0;
                    }
                    racs.my0 = evt.getY();
                }
                szekcio_kepkitevo();
            }
        }
        if (SwingUtilities.isRightMouseButton(evt)) {
            //mozgatás                    
            if ((racs.tx0 == 0) || (racs.ty0 == 0)) {
                racs.tx0 = evt.getX();
                racs.ty0 = evt.getY();
            } else {
                if (evt.getX() > racs.tx0) {
                    racs.kepkozep[1][0]++;
                    racs.tx0 = evt.getX();
                }
                if (evt.getX() < racs.tx0) {
                    racs.kepkozep[1][0]--;
                    racs.tx0 = evt.getX();
                }
                if (evt.getY() > racs.ty0) {
                    racs.kepkozep[1][1]++;
                    racs.ty0 = evt.getY();
                }
                if (evt.getY() < racs.ty0) {
                    racs.kepkozep[1][1]--;
                    racs.ty0 = evt.getY();
                }
                szekcio_kepkitevo();
            }
        }
        if (SwingUtilities.isMiddleMouseButton(evt)) {
            // Alaphelyzet visszaállítás
            Szekcio_nagyito.setValue(100);
            racs.kepkozep[1][0] = 0;
            racs.kepkozep[1][1] = 0;
            racs.forgatas[1][2] = 0;
            racs.forgatas[1][3] = 0;
            racs.kepnagyitas[1] = 1;
            szekcio_kepkitevo();
        }
    }//GEN-LAST:event_szekcio_nezetMouseDragged

    private void racstipus_valtoztato(int tipus, int ertek) {
        // Az aktuális köz rácstipusának megváltoztatása
        //System.out.println(racs.rud.size());
        for (int i = 0; i < racs.racsalap1.size(); i++) {
            if ((racs.racsalap1.get(i).getSzekcio() == szekciok.getSelectedIndex() - 1)
                    && ((racs.racsalap1.get(i).getKoz() == kozok.getSelectedIndex() + 1))) {
                switch (tipus) {
                    case 2:
                        racs.racsalap1.get(i).setRacs2(ertek);
                        break;
                    case 3:
                        racs.racsalap1.get(i).setRacs3(ertek);
                        break;
                    case 4:
                        racs.racsalap1.get(i).setRacs4(ertek);
                        break;
                    case 5:
                        racs.racsalap1.get(i).setRacs5(ertek);
                        break;
                    case 6:
                        racs.racsalap1.get(i).setRacs6(ertek);
                        break;
                    case 7:
                        racs.racsalap1.get(i).setRacs7(ertek);
                        break;
                    case 8:
                        racs.racsalap1.get(i).setRacs8(ertek);
                        break;
                    default:
                        racs.racsalap1.get(i).setRacs1(ertek);
                }
            }
        }
        racs.racselemek();
        // A teljes rajz színei
        racs.szinbeallito(true, szekciok.getSelectedIndex() - 1, 0, vastagvonalak.isSelected());
        // A szekciórajz sinei
        racs.szinbeallito(false, szekciok.getSelectedIndex() - 1, kozok.getSelectedIndex() + 1, vastagvonalak.isSelected());
        teljes_kepkitevo();
        szekcio_kepkitevo();
        mentes.setEnabled(true);
    }

    private void ftipus1StateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_ftipus1StateChanged
        racstipus_valtoztato(1, ftipus1.getValue());
        mentes.setEnabled(true);
        ftipus1text.setText(String.valueOf(ftipus1.getValue()));
    }//GEN-LAST:event_ftipus1StateChanged

    private void ftipus2StateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_ftipus2StateChanged
        racstipus_valtoztato(2, ftipus2.getValue());
        ftipus2text.setText(String.valueOf(ftipus2.getValue()));
        mentes.setEnabled(true);
    }//GEN-LAST:event_ftipus2StateChanged

    private void ftipus3StateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_ftipus3StateChanged
        racstipus_valtoztato(3, ftipus3.getValue());
        ftipus3text.setText(String.valueOf(ftipus3.getValue()));
        mentes.setEnabled(true);
    }//GEN-LAST:event_ftipus3StateChanged

    private void ftipus4StateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_ftipus4StateChanged
        racstipus_valtoztato(4, ftipus4.getValue());
        ftipus4text.setText(String.valueOf(ftipus4.getValue()));
        mentes.setEnabled(true);
    }//GEN-LAST:event_ftipus4StateChanged

    private void ftipus5StateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_ftipus5StateChanged
        racstipus_valtoztato(5, ftipus5.getValue());
        ftipus5text.setText(String.valueOf(ftipus5.getValue()));
        mentes.setEnabled(true);
    }//GEN-LAST:event_ftipus5StateChanged

    private void ftipus6StateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_ftipus6StateChanged
        racstipus_valtoztato(6, ftipus6.getValue());
        ftipus6text.setText(String.valueOf(ftipus6.getValue()));
        mentes.setEnabled(true);
    }//GEN-LAST:event_ftipus6StateChanged

    private void ftipus7StateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_ftipus7StateChanged
        racstipus_valtoztato(7, ftipus7.getValue());
        ftipus7text.setText(String.valueOf(ftipus7.getValue()));
        mentes.setEnabled(true);
    }//GEN-LAST:event_ftipus7StateChanged

    private void ftipus8StateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_ftipus8StateChanged
        racstipus_valtoztato(8, ftipus8.getValue());
        ftipus8text.setText(String.valueOf(ftipus8.getValue()));
        mentes.setEnabled(true);
    }//GEN-LAST:event_ftipus8StateChanged

    private void kozkivalasztoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_kozkivalasztoActionPerformed
        racs.kozbeolvaso(szekciok.getSelectedIndex() - 1, kozok.getSelectedIndex() + 1);
        // A szekciórajz színei
        racs.szinbeallito(false, szekciok.getSelectedIndex() - 1, kozok.getSelectedIndex() + 1, vastagvonalak.isSelected());
        szekcio_kepkitevo();
        //racselemek_kijelzo();
    }//GEN-LAST:event_kozkivalasztoActionPerformed

    private void racskoz_valtoztatoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_racskoz_valtoztatoActionPerformed
        float magassag = 0, alsoxy = 0, alsoyz = 0, felsoxy = 0, felsoyz = 0, diffxy = 0, diffyz = 0;
        float kezdx = 0, kezdy = 0, kezdz = 0, eltolasxy = 0, eltolasyz = 0, eltxy = 0, eltyz = 0;
        DefaultTableModel tableModel = (DefaultTableModel) racskozok.getModel();
        int j = tableModel.getRowCount();
        for (int i = 0; i < racs.racsalap.size(); i++) {
            if (racs.racsalap.get(i).getSzekcio() == szekciok.getSelectedIndex() - 1) {
                magassag = racs.racsalap.get(i).getMagassag();
                alsoxy = racs.racsalap.get(i).getAlsoszelxy();
                alsoyz = racs.racsalap.get(i).getAlsoszelyz();
                felsoxy = racs.racsalap.get(i).getFelsoszelxy();
                felsoyz = racs.racsalap.get(i).getFelsoszelyz();
                kezdx = racs.racsalap.get(i).getX();
                kezdy = racs.racsalap.get(i).getY();
                kezdz = racs.racsalap.get(i).getZ();
                eltolasxy = racs.racsalap.get(i).getEltolasxy();
                eltolasyz = racs.racsalap.get(i).getEltolasyz();
            }
        }
        diffxy = (alsoxy - felsoxy) / magassag;
        diffyz = (alsoyz - felsoyz) / magassag;
        eltxy = eltolasxy / magassag;
        eltyz = eltolasyz / magassag;
        for (int i = 0; i < j; i++) {
            felsoxy = alsoxy - (Float.parseFloat(tableModel.getValueAt(i, 1).toString())) * diffxy;
            felsoyz = alsoyz - (Float.parseFloat(tableModel.getValueAt(i, 1).toString())) * diffyz;
            for (int k = 0; k < racs.racsalap1.size(); k++) {
                if ((racs.racsalap.get(i).getSzekcio() == szekciok.getSelectedIndex() - 1)
                        && ((racs.racsalap.get(i).getTeljes() == (i + 1)))) {
                    //itt kell folytatni
                    racs.racsalap.get(i).setMagassag(Integer.parseInt(tableModel.getValueAt(i, 1).toString()));
                    racs.racsalap.get(i).setAlsoszelxy(Integer.parseInt(String.valueOf(alsoxy)));
                    racs.racsalap.get(i).setAlsoszelyz(Integer.parseInt(String.valueOf(alsoyz)));
                    racs.racsalap.get(i).setFelsoszelxy(Integer.parseInt(String.valueOf(felsoxy)));
                    racs.racsalap.get(i).setFelsoszelyz(Integer.parseInt(String.valueOf(felsoyz)));
                    racs.racsalap.get(i).setX(Integer.parseInt(String.valueOf(kezdx)));
                    racs.racsalap.get(i).setY(Integer.parseInt(String.valueOf(kezdy)));
                    racs.racsalap.get(i).setZ(Integer.parseInt(String.valueOf(kezdz)));
                    racs.racsalap.get(i).setEltolasxy(Integer.parseInt(String.valueOf(eltolasxy)));
                    racs.racsalap.get(i).setEltolasyz(Integer.parseInt(String.valueOf(eltolasyz)));
                }
            }
            alsoxy = felsoxy;
            alsoyz = felsoyz;
            eltolasxy += Float.parseFloat(tableModel.getValueAt(i, 1).toString()) * eltxy;
            eltolasyz += Float.parseFloat(tableModel.getValueAt(i, 1).toString()) * eltyz;
            kezdx += Float.parseFloat(tableModel.getValueAt(i, 1).toString()) * (diffxy / 2);
            //System.out.println(diffxy);
            kezdz += Float.parseFloat(tableModel.getValueAt(i, 1).toString()) * (diffyz / 2);
            kezdy += Float.parseFloat(tableModel.getValueAt(i, 1).toString());
        }
        // Adatbeolvasás?
        racs.adatbeolvaso();
        szekcio_keptorlo();
        teljes_kepkitevo();
        //szekcio_kepkitevo();
        mentes.setEnabled(true);
    }//GEN-LAST:event_racskoz_valtoztatoActionPerformed

    private void ftipus2textActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ftipus2textActionPerformed
        if (Integer.parseInt(ftipus2text.getText()) < ftipus2.getMinimum()) {
            ftipus2text.setText(String.valueOf(ftipus2.getMinimum()));
        }
        if (Integer.parseInt(ftipus2text.getText()) > ftipus2.getMaximum()) {
            ftipus2text.setText(String.valueOf(ftipus2.getMaximum()));
        }
        ftipus2.setValue(Integer.parseInt(ftipus2text.getText()));
        mentes.setEnabled(true);
        racstipus_valtoztato(2, Integer.parseInt(ftipus2text.getText()));
    }//GEN-LAST:event_ftipus2textActionPerformed

    private void ftipus1textActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ftipus1textActionPerformed
        if (Integer.parseInt(ftipus1text.getText()) < ftipus1.getMinimum()) {
            ftipus1text.setText(String.valueOf(ftipus1.getMinimum()));
        }
        if (Integer.parseInt(ftipus1text.getText()) > ftipus1.getMaximum()) {
            ftipus1text.setText(String.valueOf(ftipus1.getMaximum()));
        }
        ftipus1.setValue(Integer.parseInt(ftipus1text.getText()));
        mentes.setEnabled(true);
        racstipus_valtoztato(1, Integer.parseInt(ftipus1text.getText()));
    }//GEN-LAST:event_ftipus1textActionPerformed

    private void ftipus3textActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ftipus3textActionPerformed
        if (Integer.parseInt(ftipus3text.getText()) < ftipus3.getMinimum()) {
            ftipus3text.setText(String.valueOf(ftipus3.getMinimum()));
        }
        if (Integer.parseInt(ftipus3text.getText()) > ftipus3.getMaximum()) {
            ftipus3text.setText(String.valueOf(ftipus3.getMaximum()));
        }
        ftipus3.setValue(Integer.parseInt(ftipus3text.getText()));
        mentes.setEnabled(true);
        racstipus_valtoztato(3, Integer.parseInt(ftipus3text.getText()));
    }//GEN-LAST:event_ftipus3textActionPerformed

    private void ftipus4textActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ftipus4textActionPerformed
        if (Integer.parseInt(ftipus4text.getText()) < ftipus4.getMinimum()) {
            ftipus4text.setText(String.valueOf(ftipus4.getMinimum()));
        }
        if (Integer.parseInt(ftipus4text.getText()) > ftipus4.getMaximum()) {
            ftipus4text.setText(String.valueOf(ftipus4.getMaximum()));
        }
        ftipus4.setValue(Integer.parseInt(ftipus4text.getText()));
        mentes.setEnabled(true);
        racstipus_valtoztato(4, Integer.parseInt(ftipus4text.getText()));
    }//GEN-LAST:event_ftipus4textActionPerformed

    private void ftipus5textActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ftipus5textActionPerformed
        if (Integer.parseInt(ftipus5text.getText()) < ftipus5.getMinimum()) {
            ftipus5text.setText(String.valueOf(ftipus5.getMinimum()));
        }
        if (Integer.parseInt(ftipus5text.getText()) > ftipus5.getMaximum()) {
            ftipus5text.setText(String.valueOf(ftipus5.getMaximum()));
        }
        ftipus5.setValue(Integer.parseInt(ftipus5text.getText()));
        mentes.setEnabled(true);
        racstipus_valtoztato(5, Integer.parseInt(ftipus5text.getText()));
    }//GEN-LAST:event_ftipus5textActionPerformed

    private void ftipus6textActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ftipus6textActionPerformed
        if (Integer.parseInt(ftipus6text.getText()) < ftipus6.getMinimum()) {
            ftipus6text.setText(String.valueOf(ftipus6.getMinimum()));
        }
        if (Integer.parseInt(ftipus6text.getText()) > ftipus6.getMaximum()) {
            ftipus6text.setText(String.valueOf(ftipus6.getMaximum()));
        }
        ftipus6.setValue(Integer.parseInt(ftipus6text.getText()));
        mentes.setEnabled(true);
        racstipus_valtoztato(6, Integer.parseInt(ftipus6text.getText()));
    }//GEN-LAST:event_ftipus6textActionPerformed

    private void ftipus7textActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ftipus7textActionPerformed
        if (Integer.parseInt(ftipus7text.getText()) < ftipus7.getMinimum()) {
            ftipus7text.setText(String.valueOf(ftipus7.getMinimum()));
        }
        if (Integer.parseInt(ftipus7text.getText()) > ftipus7.getMaximum()) {
            ftipus7text.setText(String.valueOf(ftipus7.getMaximum()));
        }
        ftipus7.setValue(Integer.parseInt(ftipus7text.getText()));
        mentes.setEnabled(true);
        racstipus_valtoztato(7, Integer.parseInt(ftipus7text.getText()));
    }//GEN-LAST:event_ftipus7textActionPerformed

    private void ftipus8textActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ftipus8textActionPerformed
        if (Integer.parseInt(ftipus8text.getText()) < ftipus8.getMinimum()) {
            ftipus8text.setText(String.valueOf(ftipus8.getMinimum()));
        }
        if (Integer.parseInt(ftipus8text.getText()) > ftipus8.getMaximum()) {
            ftipus8text.setText(String.valueOf(ftipus8.getMaximum()));
        }
        ftipus8.setValue(Integer.parseInt(ftipus8text.getText()));
        mentes.setEnabled(true);
        racstipus_valtoztato(8, Integer.parseInt(ftipus8text.getText()));
    }//GEN-LAST:event_ftipus8textActionPerformed

    private void mentesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mentesActionPerformed
        // TODO add your handling code here:                        
        racs.adatrogzito();
        mentes.setEnabled(false);
        csomopontszam.setText(String.valueOf(racs.csomopont.size()));
        rudszam.setText(String.valueOf(racs.rud.size()));
    }//GEN-LAST:event_mentesActionPerformed

    private void rudlista_megjeloloActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rudlista_megjeloloActionPerformed
        // TODO add your handling code here:
        int j = -1;
        DefaultTableModel tableModel = (DefaultTableModel) rudlista.getModel();
        for (int i = 0; i < racs.rud.size(); i++) {
            if (racs.rud.get(i).getSzekcio() == szekciok.getSelectedIndex() - 1) {
                j++;
                if (tableModel.getValueAt(j, 7).toString().equals("true")) {
                    racs.rud.get(i).setKijelzes(1);
                } else {
                    racs.rud.get(i).setKijelzes(0);
                }
            }
        }
        racs.szinbeallito(false, szekciok.getSelectedIndex() - 1, kozok.getSelectedIndex() + 1, vastagvonalak.isSelected());
        szekcio_kepkitevo();
    }//GEN-LAST:event_rudlista_megjeloloActionPerformed

    private void csomopontlista_megjeloloActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_csomopontlista_megjeloloActionPerformed
        // TODO add your handling code here:
        DefaultTableModel tableModel = (DefaultTableModel) csomopontlista.getModel();
        int j = -1;
        for (int i = 0; i < racs.csomopont.size(); i++) {
            if (racs.csomopont.get(i).getSzekcio() == szekciok.getSelectedIndex() - 1) {
                j++;
                //System.out.println("j:" + j + "  index:" + racs.csomopontindex);
                if (tableModel.getValueAt(j, 5).toString().equals("true")) {
                    racs.csomopont.get(i).setKijelzes(1);
                } else {
                    racs.csomopont.get(i).setKijelzes(0);
                }
            }
        }
        szekcio_kepkitevo();
    }//GEN-LAST:event_csomopontlista_megjeloloActionPerformed

    private void vizszintesStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_vizszintesStateChanged
        // TODO add your handling code here:
        kisrajz_kepkitevo();
    }//GEN-LAST:event_vizszintesStateChanged

    private void fuggolegesStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_fuggolegesStateChanged
        // TODO add your handling code here:
        kisrajz_kepkitevo();
    }//GEN-LAST:event_fuggolegesStateChanged

    private void kettamaszuStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_kettamaszuStateChanged
        // TODO add your handling code here:
        kisrajz_kepkitevo();
    }//GEN-LAST:event_kettamaszuStateChanged

    private void KodositoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_KodositoActionPerformed
        // TODO add your handling code here:
        if (Kodosito.isSelected()) {
            Kodosito.setText("Normál rajz");
        } else {
            Kodosito.setText("Ködös rajz");
        }
        racs.rajztipus = !racs.rajztipus;
        szekcio_kepkitevo();
        teljes_kepkitevo();
    }//GEN-LAST:event_KodositoActionPerformed

    private void vastagvonalakActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_vastagvonalakActionPerformed
        // TODO add your handling code here:
        if (vastagvonalak.isSelected()) {
            vastagvonalak.setText("Vékony vonal");
        } else {
            vastagvonalak.setText("Vastag vonal");
        }
        // A teljes rajz színei
        racs.szinbeallito(true, szekciok.getSelectedIndex() - 1, 0, vastagvonalak.isSelected());
        // A szekciórajz színei
        racs.szinbeallito(false, szekciok.getSelectedIndex() - 1, kozok.getSelectedIndex() + 1, vastagvonalak.isSelected());
        szekcio_kepkitevo();
        teljes_kepkitevo();
    }//GEN-LAST:event_vastagvonalakActionPerformed

    private void Szekcio_nagyitoStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_Szekcio_nagyitoStateChanged
        // TODO add your handling code here:
        racs.kepnagyitas[1] = (float) (Szekcio_nagyito.getValue()) / 100;
        szekcio_kepkitevo();
    }//GEN-LAST:event_Szekcio_nagyitoStateChanged

    private void Teljes_nagyitoStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_Teljes_nagyitoStateChanged
        // TODO add your handling code here:
        racs.kepnagyitas[0] = (float) (Teljes_nagyito.getValue()) / 100;
        teljes_kepkitevo();
    }//GEN-LAST:event_Teljes_nagyitoStateChanged

    private void KilepesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_KilepesActionPerformed
        // TODO add your handling code here:
        System.exit(0);
    }//GEN-LAST:event_KilepesActionPerformed

    private void sql_lementoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_sql_lementoActionPerformed
        // TODO add your handling code here:
        // Az adathalmaz lementése rud-csomopont formátumba SQL
        racs.racsrudvastagsag();
        racs.sql_file_export();
        // Az adathalmaz lementése POV-RAY kimenetként (adatok.inc)
        racs.povray_fileiro();
        sql_lemento.setEnabled(false);
    }//GEN-LAST:event_sql_lementoActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton Elemmodosito;
    private javax.swing.JButton Kilepes;
    private javax.swing.JToggleButton Kodosito;
    private javax.swing.JTextField Magassag;
    private javax.swing.JSlider Szekcio_nagyito;
    private javax.swing.JSlider Teljes_nagyito;
    private javax.swing.JTextField alsoszelxy;
    private javax.swing.JTextField alsoszelyz;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JTable csomopontlista;
    private javax.swing.JButton csomopontlista_megjelolo;
    private javax.swing.JTextField csomopontszam;
    private javax.swing.JButton drotvaz_kivalaszto;
    private javax.swing.JComboBox drotvazak;
    private javax.swing.JButton elemhozzado;
    private javax.swing.JTextField elemszamok;
    private javax.swing.JTextField eltolasxy;
    private javax.swing.JTextField eltolasyz;
    private javax.swing.JTextField felsoszelxy;
    private javax.swing.JTextField felsoszelyz;
    private javax.swing.JSlider ftipus1;
    private javax.swing.JTextField ftipus1text;
    private javax.swing.JSlider ftipus2;
    private javax.swing.JTextField ftipus2text;
    private javax.swing.JSlider ftipus3;
    private javax.swing.JTextField ftipus3text;
    private javax.swing.JSlider ftipus4;
    private javax.swing.JTextField ftipus4text;
    private javax.swing.JSlider ftipus5;
    private javax.swing.JTextField ftipus5text;
    private javax.swing.JSlider ftipus6;
    private javax.swing.JTextField ftipus6text;
    private javax.swing.JSlider ftipus7;
    private javax.swing.JTextField ftipus7text;
    private javax.swing.JSlider ftipus8;
    private javax.swing.JTextField ftipus8text;
    private javax.swing.JRadioButton fuggoleges;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel32;
    private javax.swing.JLabel jLabel33;
    private javax.swing.JLabel jLabel34;
    private javax.swing.JLabel jLabel37;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel44;
    private javax.swing.JLabel jLabel45;
    private javax.swing.JLabel jLabel46;
    private javax.swing.JLabel jLabel47;
    private javax.swing.JLabel jLabel48;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel50;
    private javax.swing.JLabel jLabel51;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator3;
    private javax.swing.JSeparator jSeparator4;
    private javax.swing.JSeparator jSeparator5;
    private javax.swing.JSeparator jSeparator7;
    private javax.swing.JSeparator jSeparator9;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JTextField kapcsx;
    private javax.swing.JTextField kapcsy;
    private javax.swing.JTextField kapcsz;
    private javax.swing.JRadioButton kettamaszu;
    private javax.swing.JLabel kisrajz;
    private javax.swing.JTextField konzolhossz;
    private javax.swing.JButton kozkivalaszto;
    private javax.swing.JComboBox kozok;
    private javax.swing.JButton mentes;
    private javax.swing.JButton racskoz_valtoztato;
    private javax.swing.JTable racskozok;
    private javax.swing.JTable rudlista;
    private javax.swing.JButton rudlista_megjelolo;
    private javax.swing.JTextField rudszam;
    private javax.swing.JButton sql_lemento;
    private javax.swing.JButton szekcio_kivalaszto;
    private javax.swing.JLabel szekcio_nezet;
    private javax.swing.JTextField szekcio_suly;
    private javax.swing.JTable szekcioadatok;
    private javax.swing.JTextField szekciohossz;
    private javax.swing.JComboBox szekciok;
    private javax.swing.JLabel teljes_nezet;
    private javax.swing.JTextField teljes_suly;
    private javax.swing.JButton ujelem;
    private javax.swing.JTextField ujelemnev;
    private javax.swing.JToggleButton vastagvonalak;
    private javax.swing.JRadioButton vizszintes;
    // End of variables declaration//GEN-END:variables
}
