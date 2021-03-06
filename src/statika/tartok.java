/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * tartok.java
 *
 * Created on 2012.12.28., 19:03:48
 */
package statika;

import Entities.Projectek;
import Entities.Szelveny;
import Entities.Tartoerok;
import Entities.Tartok;
import Hibernate.HibernateUtil;
import java.util.List;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import org.hibernate.Query;
import org.hibernate.Session;
import java.util.Date;

/**
 *
 * @author SD-LEAP
 */
public class tartok extends javax.swing.JInternalFrame {

    /**
     * Creates new form tartok
     */
    String megnevezes = "";
    String parancs;
    
    Query query;
    int result;
    Date now = new Date();

    public tartok() {
        Session session = HibernateUtil.getSessionFactory().openSession();
        session.beginTransaction();
        parancs = "FROM Projectek Where aktiv = '1'";
        //System.out.println(parancs);
        List<Projectek> project = session.createQuery(parancs).list();
        for (int i = 0; i < project.size(); i++) {
            megnevezes = project.get(i).getProjekt();
        }
        //System.out.println("Az aktuális projekt: " + megnevezes);
        initComponents();
        projekt.setText(megnevezes);
        tartolista_tablatolto();
        // A szelvények feltöltése
        parancs = "FROM Szelveny order by nev";
        //System.out.println(parancs);        
        List<Szelveny> szelvenylista = session.createQuery(parancs).list();
        szelvenyek_listaja.addItem("Válassz");
        for (int i = 0; i < szelvenylista.size(); i++) {
            // A szelvénytár beolvasása
            szelvenyek_listaja.addItem(szelvenylista.get(i).getNev());
        }
        session.getTransaction().commit();
        session.close();
        // A hozzárendelt tartók beillesztése a Jtable-be
        tartolista_tablatolto();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jLabel1 = new javax.swing.JLabel();
        projekt = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        tartonev = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        szelvenyek_listaja = new javax.swing.JComboBox();
        Tartobeiro = new javax.swing.JButton();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        hossz = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        konzol1 = new javax.swing.JTextField();
        jLabel9 = new javax.swing.JLabel();
        konzol2 = new javax.swing.JTextField();
        jLabel10 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        tartolista = new javax.swing.JTable();
        Tartomodositas = new javax.swing.JButton();
        jSeparator2 = new javax.swing.JSeparator();
        jSeparator1 = new javax.swing.JSeparator();
        jLabel12 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        megjegyzes = new javax.swing.JTextField();
        jButton1 = new javax.swing.JButton();

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jScrollPane1.setViewportView(jTable1);

        setClosable(true);
        setTitle("A projekt tartói");

        jLabel1.setText("A projekt neve:"); // NOI18N

        projekt.setEditable(false);
        projekt.setText("Projektnév"); // NOI18N
        projekt.setBorder(null);

        jLabel2.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel2.setText("Új tartó:"); // NOI18N

        jLabel3.setText("Megnevezés:"); // NOI18N

        jLabel4.setText("Szelvény:"); // NOI18N

        Tartobeiro.setText("Tartó hozzárendelés"); // NOI18N
        Tartobeiro.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                TartobeiroActionPerformed(evt);
            }
        });

        jLabel5.setText("Hossz:"); // NOI18N

        jLabel6.setText("Konzol1:"); // NOI18N

        jLabel7.setText("Konzol2:"); // NOI18N

        hossz.setText("100"); // NOI18N

        jLabel8.setText("cm"); // NOI18N

        konzol1.setText("0"); // NOI18N

        jLabel9.setText("cm"); // NOI18N

        konzol2.setText("0"); // NOI18N

        jLabel10.setText("cm"); // NOI18N

        jLabel11.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel11.setText("Hozzárendelt tartók:"); // NOI18N

        tartolista.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "ID", "Tartónév", "Hossz (cm)", "Konzol1 (cm)", "Konzol2 (cm)", "Szelvény", "Törlés"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Boolean.class
            };
            boolean[] canEdit = new boolean [] {
                false, true, true, true, true, false, true
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane2.setViewportView(tartolista);

        Tartomodositas.setText("Módosítás"); // NOI18N
        Tartomodositas.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                TartomodositasActionPerformed(evt);
            }
        });

        jSeparator2.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));

        jSeparator1.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));

        jLabel12.setText("(Csak konzolos -> hossz =0, konzol1 <>0)");

        jLabel13.setText("Megjegyzés:");

        jButton1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/statika/exit1.png"))); // NOI18N
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jSeparator2)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                        .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 197, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 367, Short.MAX_VALUE)
                        .addComponent(Tartomodositas, javax.swing.GroupLayout.PREFERRED_SIZE, 113, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jSeparator1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 658, Short.MAX_VALUE)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                        .addComponent(jLabel3)
                                        .addGap(23, 23, 23))
                                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                        .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 69, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(18, 18, 18))
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(jLabel13)
                                        .addGap(26, 26, 26)))
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(megjegyzes, javax.swing.GroupLayout.DEFAULT_SIZE, 288, Short.MAX_VALUE)
                                        .addGap(18, 18, 18)
                                        .addComponent(Tartobeiro, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addComponent(szelvenyek_listaja, javax.swing.GroupLayout.Alignment.LEADING, 0, 456, Short.MAX_VALUE)
                                    .addComponent(tartonev, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 456, Short.MAX_VALUE))
                                .addGap(18, 18, 18)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel7)
                                    .addComponent(jLabel6)
                                    .addComponent(jLabel5))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(hossz, javax.swing.GroupLayout.DEFAULT_SIZE, 52, Short.MAX_VALUE)
                                    .addComponent(konzol1)
                                    .addComponent(konzol2)))
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                                .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 91, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 362, Short.MAX_VALUE)
                                .addComponent(jLabel12)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(jLabel8)
                                .addComponent(jLabel9))
                            .addComponent(jLabel10)))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addComponent(jLabel1)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(projekt, javax.swing.GroupLayout.PREFERRED_SIZE, 461, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(119, 119, 119))
                            .addComponent(jButton1, javax.swing.GroupLayout.Alignment.TRAILING))))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(projekt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(2, 2, 2)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(jLabel12))
                .addGap(5, 5, 5)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel3)
                        .addComponent(tartonev, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel8))
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel5)
                        .addComponent(hossz, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel6)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(szelvenyek_listaja, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(konzol1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel9)
                            .addComponent(jLabel4))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(konzol2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel10)
                            .addComponent(jLabel7)
                            .addComponent(Tartobeiro)
                            .addComponent(jLabel13)
                            .addComponent(megjegyzes, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jSeparator2, javax.swing.GroupLayout.PREFERRED_SIZE, 8, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel11)
                    .addComponent(Tartomodositas))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 187, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jButton1)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void tartolista_tablatorlo() {
        DefaultTableModel tableModel = (DefaultTableModel) tartolista.getModel();
        int i = tableModel.getRowCount();
        if (i > 0) {
            for (int k = 0; k < i; k++) {
                tableModel.removeRow(0);
            }
        }
    }

    private void tartolista_tablatolto() {
        Session session = HibernateUtil.getSessionFactory().openSession();
        DefaultTableModel tableModel = (DefaultTableModel) tartolista.getModel();
        tartolista_tablatorlo();
        session.beginTransaction();
        parancs = "FROM Tartok where projekt ='";
        parancs = parancs + megnevezes + "' order by id";
        //System.out.println(parancs);        
        List<Tartok> tartok = session.createQuery(parancs).list();
        for (int i = 0; i < tartok.size(); i++) {
            String[] data = new String[6];
            data[0] = String.valueOf(tartok.get(i).getId());
            data[1] = tartok.get(i).getTartonev();
            data[2] = String.valueOf(tartok.get(i).getHossz());
            data[3] = String.valueOf(tartok.get(i).getKonzol1());
            data[4] = String.valueOf(tartok.get(i).getKonzol2());
            data[5] = String.valueOf(tartok.get(i).getSzelveny());
            tableModel.addRow(data);
        }
        session.getTransaction().commit();
        session.close();
        tartolista.setModel(tableModel);
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(tartonev.CENTER);
        for (int k = 0; k < 6; k++) {
            tartolista.getColumnModel().getColumn(k).setCellRenderer(centerRenderer);
        }
        // A tábla oszlopszélességei
        tartolista.setAutoResizeMode(tartolista.AUTO_RESIZE_OFF);
        tartolista.getColumnModel().getColumn(0).setPreferredWidth(50);
        tartolista.getColumnModel().getColumn(1).setPreferredWidth(90);
        tartolista.getColumnModel().getColumn(2).setPreferredWidth(90);
        tartolista.getColumnModel().getColumn(3).setPreferredWidth(90);
        tartolista.getColumnModel().getColumn(4).setPreferredWidth(90);
        tartolista.getColumnModel().getColumn(5).setPreferredWidth(150);
        tartolista.getColumnModel().getColumn(6).setPreferredWidth(90);
    }

    private void TartomodositasActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_TartomodositasActionPerformed
        // TODO add your handling code here:
        Session session = HibernateUtil.getSessionFactory().openSession();
        DefaultTableModel tableModel = (DefaultTableModel) tartolista.getModel();
        //int UpdateQuery;
        float teljeshossz;
        if (tableModel.getRowCount() > 0) {
            // A Jtable-ben lévő adatok módosítása
            session.beginTransaction();
            int j = tableModel.getRowCount();
            for (int k = 0; k < j; k++) {
                String[] data = new String[6];
                data[0] = tableModel.getValueAt(k, 0).toString();
                data[1] = tableModel.getValueAt(k, 1).toString();
                data[2] = tableModel.getValueAt(k, 2).toString();
                data[3] = tableModel.getValueAt(k, 3).toString();
                data[4] = tableModel.getValueAt(k, 4).toString();
                data[5] = tableModel.getValueAt(k, 5).toString();
                if (tableModel.getValueAt(k, 6) != null) {
                    // Adattörlés                    
                    // A tartó adatai
                    parancs = "delete from Tartok where id='" + data[0] + "'";
                    query = session.createQuery(parancs);
                    result = query.executeUpdate();
                    // A tartó erői
                    parancs = "delete from Tartoerok where projekt = '" + megnevezes + "' and tartonev='" + data[0] + "'";
                    query = session.createQuery(parancs);
                    result = query.executeUpdate();
                } else {
                    // Adatmódosítás
                    // A tartó adatai
                    parancs = "update Tartok set Tartonev='" + data[1] + "', ";
                    parancs = parancs + " hossz='" + data[2] + "', ";
                    parancs = parancs + " konzol1='" + data[3] + "', ";
                    parancs = parancs + " konzol2='" + data[4] + "', ";
                    parancs = parancs + " szelveny='" + data[5] + "' ";
                    parancs = parancs + " where id = '" + data[0] + "'";
                    query = session.createQuery(parancs);
                    //System.out.println(parancs);
                    result = query.executeUpdate();
                    teljeshossz = Float.parseFloat(String.valueOf(data[2])) + Float.parseFloat(String.valueOf(data[3])) + Float.parseFloat(String.valueOf(data[4]));
                    // Ha van hozzárendelve profil, akkor ennek a hosszmérete is megváltozik
                    parancs = "update Tartoerok set hossz='" + teljeshossz;
                    parancs = parancs + "' where szelveny <>'' and hely=0 and jelleg = 2 and tartonev = '";
                    parancs = parancs + data[1] + "' and projekt = '" + projekt + "'";
                    query = session.createQuery(parancs);
                    result = query.executeUpdate();
                    
                }
            }
            // Jtable frissítése
            session.getTransaction().commit();
                    session.close();
            tartolista_tablatolto();
        }
    }//GEN-LAST:event_TartomodositasActionPerformed

    private void TartobeiroActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_TartobeiroActionPerformed
        // TODO add your handling code here:
        Session session = HibernateUtil.getSessionFactory().openSession();
        Tartok ujtarto = new Tartok();
        Tartoerok ujtartoero = new Tartoerok();
        float ertek = 0f;   // Ez lesz a folyómétersúly
        int tipus = 3;
        if (tartonev.getText().length() > 0) {
            // Az új tartó beírása
            if (Integer.valueOf(konzol1.getText()) + Integer.valueOf(konzol2.getText()) == 0) {
                tipus = 1;
            }
            if (Integer.valueOf(hossz.getText()) == 0) {
                tipus = 2;
            }
            session.beginTransaction();
            ujtarto.setProjekt(megnevezes);
            ujtarto.setTartonev(tartonev.getText());
            ujtarto.setTipus(tipus);
            //System.out.println(":_"+hossz.getText()+"_:");
            //System.out.println(Float.parseFloat(hossz.toString()));
                       
            ujtarto.setKonzol1(Float.parseFloat(konzol1.getText()));
            ujtarto.setKonzol2(Float.parseFloat(konzol2.getText()));
            ujtarto.setHossz(Float.parseFloat(hossz.getText())); 
            ujtarto.setSzelveny(szelvenyek_listaja.getSelectedItem().toString());
            ujtarto.setNote(megjegyzes.getText());
            ujtarto.setFelvitel(now);
            session.save(ujtarto);
            
            /*parancs = "insert into Tartok (projekt, tartonev, tipus, hossz, konzol1, konzol2, szelveny, note) values ('";
            parancs = parancs + megnevezes + "','";
            parancs = parancs + tartonev.getText() + "','";
            parancs = parancs + tipus + "','";
            parancs = parancs + hossz.getText() + "','";
            parancs = parancs + konzol1.getText() + "','";
            parancs = parancs + konzol2.getText() + "','";
            parancs = parancs + szelveny.getSelectedItem().toString() + "','";
            parancs = parancs + megjegyzes.getText() + "');";
            //System.out.println("SQL parancs: " + parancs);
            query = session.createQuery(parancs);
            result = query.executeUpdate();*/
            //session.getTransaction().commit();
            //session.close();
            // A szelvény felvitele a tartóerők közé mint egy megoszló terhelést
            // Az új tartó folyómétersúlyának kikeresése
            //session.beginTransaction();
            parancs = "FROM Szelveny where nev ='" + szelvenyek_listaja.getSelectedItem().toString() + "'";
            //System.out.println(parancs);
            List<Szelveny> szelvenyek = session.createQuery(parancs).list();
            for (int i = 0; i < szelvenyek.size(); i++) {
                ertek = szelvenyek.get(i).getFmsuly() / 10000;
            }
            //session.getTransaction().commit();
            //session.close();
            // Az új tartóerő bevitele 
            float teljeshossz = Float.parseFloat(hossz.getText()) + Float.parseFloat(konzol1.getText()) + Float.parseFloat(konzol2.getText());
            //session.beginTransaction();
            ujtartoero.setProjekt(projekt.getText());
            ujtartoero.setTartonev(tartonev.getText());
            ujtartoero.setSzelveny(szelvenyek_listaja.getSelectedItem().toString());
            ujtartoero.setErtek(ertek);
            ujtartoero.setHely(0f);
            ujtartoero.setHossz(teljeshossz);
            ujtartoero.setJelleg(2);
            ujtartoero.setFelvitel(now);
            session.save(ujtartoero);            
            /*parancs = "insert into Tartoerok (projekt,tartonev,szelveny,ertek,hely,hossz,jelleg) values ('";
            parancs = parancs + projekt.getText() + "','";
            parancs = parancs + tartonev.getText() + "','";
            parancs = parancs + szelveny.getSelectedItem().toString() + "','";
            parancs = parancs + ertek + "','0','";
            parancs = parancs + teljeshossz + "','2');";
            //System.out.println("SQL parancs: " + parancs);
            query = session.createQuery(parancs);
            result = query.executeUpdate(); */
            session.getTransaction().commit();
            session.close();
            // Adatfrissítés
            hossz.setText("100");
            konzol1.setText("0");
            konzol2.setText("0");
            tartonev.setText("");
            // A Jtable frissítés
            tartolista_tablatolto();
        }
    }//GEN-LAST:event_TartobeiroActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        // TODO add your handling code here:
        System.exit(0);
    }//GEN-LAST:event_jButton1ActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton Tartobeiro;
    private javax.swing.JButton Tartomodositas;
    private javax.swing.JTextField hossz;
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JTable jTable1;
    private javax.swing.JTextField konzol1;
    private javax.swing.JTextField konzol2;
    private javax.swing.JTextField megjegyzes;
    private javax.swing.JTextField projekt;
    private javax.swing.JComboBox szelvenyek_listaja;
    private javax.swing.JTable tartolista;
    private javax.swing.JTextField tartonev;
    // End of variables declaration//GEN-END:variables
}
