/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package statika_awt;

import java.io.BufferedWriter;
import java.io.FileWriter;

/**
 *
 * @author SD-LEAP
 */
public class svg_out {    
    public void svg_kimenet(int metszek,float[] nyiroero,float[] nyomatek, int nyil_db, int[] nyil_koord,float[] nyil_ero,
                            int megoszlo_db,int[][] megoszlo_koord,float[] megoszlo_teher,float fa,float fb,int hossz,int konzol1,int konzol2) {
        String szoveg;
        int maxnyomatek_hely,maxnyiroero_hely;
        int x,y,x1,y1,x2,y2;
        float f;
        int bazis;
        try{  // Create file 
          FileWriter fstream = new FileWriter("1.svg");
          BufferedWriter out = new BufferedWriter(fstream);
                    
          out.write("<?xml version='1.0' standalone='no'?>");
          out.write("<!DOCTYPE svg PUBLIC '-//W3C//DTD SVG 1.1//EN' 'http://www.w3.org/Graphics/SVG/1.1/DTD/svg11.dtd'>");
          out.write("<svg width='100%' height='100%' version='1.1' xmlns='http://www.w3.org/2000/svg'>");
          // A tartó
          szoveg = "<line x1='70' y1='42' x2='470' y2='42' style='stroke:black;stroke-width:2'/> ";out.write(szoveg);
          szoveg = "<line x1='70' y1='52' x2='70' y2='480' style='stroke:grey;stroke-width:0.5'/> ";out.write(szoveg);
          szoveg = "<line x1='470' y1='52' x2='470' y2='480' style='stroke:grey;stroke-width:0.5'/> ";out.write(szoveg);
          
          // A nyíróerőábra
          szoveg = "<text x='50' y='169' style='font:16px verdana bold'>T</text>"; out.write(szoveg);          
          szoveg = "<text x='60' y='162' style='font:16px verdana bold'>+</text>"; out.write(szoveg);
          szoveg = "<text x='60' y='177' style='font:16px verdana bold'>-</text>"; out.write(szoveg);
          szoveg = "<line x1='65' y1='167' x2='475' y2='167' style='stroke:blue;stroke-width:0.7'/> ";out.write(szoveg);
          bazis = 167;
          f =0;
          x1 = 0;  // Ez jelzi majd a negatív nyomatékot
          for (int i=0; i<metszek; i++) { 
              if (nyiroero[i]>f) { f= nyiroero[i];maxnyiroero_hely=i;x1 =1;}}
          for (int i=0; i<metszek; i++) { 
              if (-nyiroero[i]>f) { f=-nyiroero[i];maxnyiroero_hely=i;x1 =0;}} 
          out.write("<polygon fill='red' stroke='black' stroke-width='1' points='");  
          szoveg = String.valueOf(70)+","+String.valueOf(bazis)+" ";
          out.write(szoveg);           
          for (int i=0; i<metszek;i++){               
              szoveg = String.valueOf(i+70)+","+String.valueOf(nyiroero[i]*(90/f)+bazis)+" ";
              out.write(szoveg);                      
          }
          szoveg = String.valueOf(470)+","+String.valueOf(bazis)+" ";
          out.write(szoveg); 
          out.write("'/>");
          
          // A nyomatéki ábra          
          szoveg = "<text x='50' y='369' style='font:16px verdana bold'>M</text>"; out.write(szoveg);
          szoveg = "<text x='60' y='362' style='font:16px verdana bold'>+</text>"; out.write(szoveg);
          szoveg = "<text x='60' y='377' style='font:16px verdana bold'>-</text>"; out.write(szoveg);
          szoveg = "<line x1='65' y1='367' x2='475' y2='367' style='stroke:blue;stroke-width:0.7'/> ";out.write(szoveg);
          // A Maximális nyomaték meghatározása
          f =0;
          bazis = 367;
          x1 = 0;  // Ez jelzi majd a negatív nyomatékot
          maxnyomatek_hely = 0;
          for (int i=0; i<metszek; i++) {
              if (nyomatek[i]>f) { f= nyomatek[i]; maxnyomatek_hely = i; x1 =1;}
          }
          for (int i=0; i<metszek; i++) {
              if (-nyomatek[i]>f) { f=-nyomatek[i]; maxnyomatek_hely = i; x1=0;}
          }          
          out.write("<polygon fill='pink' stroke='black' stroke-width='1' points='");          
          szoveg = String.valueOf(70)+","+String.valueOf(bazis)+" ";
          out.write(szoveg); 
          for (int i=0; i<metszek;i++){               
              szoveg = String.valueOf(i+70)+","+String.valueOf(nyomatek[i]*(90/f)+bazis)+" ";
              out.write(szoveg);
          }
          szoveg = String.valueOf(470)+","+String.valueOf(bazis)+" ";
          out.write(szoveg);
          out.write("'/>");
          //out.write("<line x1='70' y1='42' x2='470' y2='42' style='stroke:black;stroke-width:2'/>");
          
          out.write("</svg>");
          //Close the output stream
          
          
          out.close();
          }catch (Exception e){//Catch exception if any
          System.err.println("Error: " + e.getMessage());
          }
    }
}                            
