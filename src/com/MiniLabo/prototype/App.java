package com.MiniLabo.prototype;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;

public class App {
    private static Graphics2D g;
    public static int TailleX = 512; //Taille de simulation 
    public static int TailleY = 512;
    public static int TailleZ = 112 ;
    public static float Zoom = 10f;
    public static int FOV = 180;
    public static int FOVet = FOV;
    private static int FOVBoite = FOV;
    private static int FOVetBoite = FOV;


    public static void main(String[] args) throws Exception {
        System.out.println("Hello, World!");

        BufferedImage b = new BufferedImage(TailleX, TailleY,BufferedImage.TYPE_4BYTE_ABGR);
        g = (Graphics2D) b.getGraphics();
        //g.setStroke(new BasicStroke(2));
        //g.setColor(Color.BLACK);
        //g.drawOval(0, 0, 50, 50);

        JLabel image = new JLabel(new ImageIcon(b));
        JFrame frame = new JFrame();
        frame.setSize(TailleX + 100,TailleY + 100);
        frame.add(image);
        frame.setVisible(true);

        try{
            //Thread.sleep(3000);
        }catch(Exception e){
            e.printStackTrace();
        }

        /*for (int i = 1; i < 16; i++) {
            Atome H = new Atome(i);
            System.out.println(i + " " + H.électronégativité);
        }*/
//Avec Verlet, dt 1/16*10^-16, beaucoup de h2 comparement au rest, et est stable, VB bcp de Ch2
        ArrayList<Atome> Hs = new ArrayList<>();
        ArrayList<Integer> indexe = new ArrayList<>();
        double espacement =    12.0;
        for(int x = 0; x < (TailleX/(Zoom*espacement)) - 1; x++){
            for(int y = 0; y < (TailleY/(Zoom*espacement)) - 1; y++){
                for(int z = 0; z < (TailleZ/(Zoom*espacement)) - 1/espacement; z++){

                    Atome C = new Atome(6);
                    C.position = new Vecteur3f(x*espacement + 1.5 - (TailleX/(2*Zoom)),y*espacement - (TailleY/(2*Zoom)), ((-z)*espacement + (TailleZ/(2*Zoom))));
                    C.vélocité = new Vecteur3f((Math.random() * 2.0 - 1.0) * 3.0 * Math.pow(10.0, 14.0), (Math.random() * 2.0 - 1.0) * 3.0 * Math.pow(10.0, 14.0),(Math.random() * 2.0 - 1.0) * 3.0 * Math.pow(10.0, 14.0));
                    Hs.add(C);

                    Atome H2 = new Atome(1);
                    H2.position = new Vecteur3f(x*espacement + 3.0 - (TailleX/(2*Zoom)),y*espacement - (TailleY/(2*Zoom)), ((-z)*espacement + (TailleZ/(2*Zoom))));
                    H2.vélocité = new Vecteur3f((Math.random() * 2.0 - 1.0) * 3.0 * Math.pow(10.0, 14.0), (Math.random() * 2.0 - 1.0) * 3.0 * Math.pow(10.0, 14.0),(Math.random() * 2.0 - 1.0) * 3.0 * Math.pow(10.0, 14.0));
                    Hs.add(H2);

                    Atome H3 = new Atome(1);
                    H3.position = new Vecteur3f(x*espacement - (TailleX/(2*Zoom)),y*espacement - (TailleY/(2*Zoom)), ((-z)*espacement + (TailleZ/(2*Zoom))));
                    H2.vélocité = new Vecteur3f((Math.random() * 2.0 - 1.0) * 3.0 * Math.pow(10.0, 14.0), (Math.random() * 2.0 - 1.0) * 3.0 * Math.pow(10.0, 14.0),(Math.random() * 2.0 - 1.0) * 3.0 * Math.pow(10.0, 14.0));
                    Hs.add(H3);
                }
            }
        }

        for (int i = 0; i < Hs.size(); i++) {
            indexe.add(i);
        }
        
        double temps = 0.0;
        long chorono = System.currentTimeMillis();
        double dt = 0.25*Math.pow(10.0,-16.0); //Delta t
        while (true) {
            g.setColor(new Color(100, 00, 100, 100));
            g.fillRect(0, 0, TailleX, TailleY);

            Atome.MettreÀJourEnvironnement(Hs);

            for (int N = 0; N < 20; N++) {          //Sous-étapes. Répète N fois/image
                for (int i = 0; i < Hs.size(); i++) {
                    Hs.get(i).miseÀJourLiens(Hs, i); //Mise à jour des liens
                }
                Intégrateur.IterVerletVB(Hs, dt); //Mise à jour de la position.
                temps += dt;


            }

            DessinerBoite();

            for (int i = 0; i < Hs.size()-1; i++) {
                if(Hs.get(indexe.get(i)).position.z < Hs.get(indexe.get(i+1)).position.z){
                    int a = indexe.get(i);
                    indexe.set( i, indexe.get(i+1));
                    indexe.set(i+1, a);
                }
            }

            for (int i = 0; i < indexe.size(); i++) {
                DessinerAtome(Hs.get(indexe.get(i)), Hs);
            }
            

            System.out.println("temps : " + String.format("%.03f", temps*Math.pow(10.0,15.0)) + " fs, rapidité : " + String.format("%.03f", (temps*Math.pow(10.0,15.0))/((double)(System.currentTimeMillis()-chorono)/1000.0)) + " fs/s" );

            //énoncerMolécules(Hs); 

            SwingUtilities.updateComponentTreeUI(frame);
            //Thread.sleep(1000);
        }
    }
    public static void DessinerBoite(){
        double multPersZBoiteLoin=(FOVBoite/(TailleZ/(2*Zoom)+TailleZ/(2.0*Zoom) + FOVetBoite));
        double multPersZBoiteProche=(FOVBoite/(-TailleZ/(2*Zoom)+TailleZ/(2.0*Zoom) + FOVetBoite));
        g.setStroke(new BasicStroke());
        g.setColor(Color.MAGENTA);
            //Face de moue

                g.drawLine( 
            
                    (int)( (TailleX/2)*multPersZBoiteLoin  +(TailleX/2)    ) ,

                    (int)( (-TailleY/2)*multPersZBoiteLoin   + (TailleY/2)    ) ,

                    (int)( -(TailleX/2)*multPersZBoiteLoin + (TailleX/2)    ) ,

                    (int)( (-TailleY/2)*multPersZBoiteLoin + (TailleY/2)   )       
            
                );
                g.drawLine( 
            
                    (int)( (TailleX/2)*multPersZBoiteLoin  +(TailleX/2)    ) ,

                    (int)( (TailleY/2)*multPersZBoiteLoin   + (TailleY/2)    ) ,

                    (int)( -(TailleX/2)*multPersZBoiteLoin + (TailleX/2)    ) ,

                    (int)( (TailleY/2)*multPersZBoiteLoin + (TailleY/2)   )       
            
                );

                g.drawLine( 
            
                    (int)( (TailleX/2)*multPersZBoiteLoin  +(TailleX/2)    ) ,

                    (int)( (TailleY/2)*multPersZBoiteLoin   + (TailleY/2)    ) ,

                    (int)( (TailleX/2)*multPersZBoiteLoin + (TailleX/2)    ) ,

                    (int)( -(TailleY/2)*multPersZBoiteLoin + (TailleY/2)   )       
            
                );

                g.drawLine( 
            
                (int)( (-TailleX/2)*multPersZBoiteLoin  +(TailleX/2)    ) ,

                (int)( (TailleY/2)*multPersZBoiteLoin   + (TailleY/2)    ) ,

                (int)( (-TailleX/2)*multPersZBoiteLoin + (TailleX/2)    ) ,

                (int)( -(TailleY/2)*multPersZBoiteLoin + (TailleY/2)   )       
        
                 );

             //Arrete 

                g.drawLine( 
            
                    (int)( (TailleX/2)*multPersZBoiteProche  +(TailleX/2)    ) ,

                    (int)( (TailleY/2)*multPersZBoiteProche   + (TailleY/2)    ) ,

                    (int)( (TailleX/2)*multPersZBoiteLoin + (TailleX/2)    ) ,

                    (int)( (TailleY/2)*multPersZBoiteLoin + (TailleY/2)   )       
            
                );
                g.drawLine( 
            
                    (int)( (TailleX/2)*multPersZBoiteProche  +(TailleX/2)    ) ,

                    (int)( (-TailleY/2)*multPersZBoiteProche   + (TailleY/2)    ) ,

                    (int)( (TailleX/2)*multPersZBoiteLoin + (TailleX/2)    ) ,

                    (int)( (-TailleY/2)*multPersZBoiteLoin + (TailleY/2)   )       
            
                );
                g.drawLine( 
            
                    (int)( (-TailleX/2)*multPersZBoiteProche  +(TailleX/2)    ) ,

                    (int)( (TailleY/2)*multPersZBoiteProche   + (TailleY/2)    ) ,

                    (int)( (-TailleX/2)*multPersZBoiteLoin + (TailleX/2)    ) ,

                    (int)( (TailleY/2)*multPersZBoiteLoin + (TailleY/2)   )       
            
                );

                g.drawLine( 
            
                    (int)( (-TailleX/2)*multPersZBoiteProche  +(TailleX/2)    ) ,

                    (int)( (-TailleY/2)*multPersZBoiteProche   + (TailleY/2)    ) ,

                    (int)( (-TailleX/2)*multPersZBoiteLoin + (TailleX/2)    ) ,

                    (int)( (-TailleY/2)*multPersZBoiteLoin + (TailleY/2)   )       
            
                );



            //Face de beue
                g.drawLine( 
            
                (int)( (TailleX/2)*multPersZBoiteProche  +(TailleX/2)    ) ,

                (int)( (-TailleY/2)*multPersZBoiteProche   + (TailleY/2)    ) ,

                (int)( (-TailleX/2)*multPersZBoiteProche + (TailleX/2)    ) ,

                (int)( (-TailleY/2)*multPersZBoiteProche + (TailleY/2)   )       
        
                );

                g.drawLine( 
            
                (int)( (TailleX/2)*multPersZBoiteProche  +(TailleX/2)    ) ,

                (int)( (TailleY/2)*multPersZBoiteProche   + (TailleY/2)    ) ,

                (int)( (-TailleX/2)*multPersZBoiteProche + (TailleX/2)    ) ,

                (int)( (TailleY/2)*multPersZBoiteProche + (TailleY/2)   )       
        
                );

                g.drawLine( 
            
                (int)( (TailleX/2)*multPersZBoiteProche  +(TailleX/2)    ) ,

                (int)( (TailleY/2)*multPersZBoiteProche   + (TailleY/2)    ) ,

                (int)( (TailleX/2)*multPersZBoiteProche + (TailleX/2)    ) ,

                (int)( (-TailleY/2)*multPersZBoiteProche + (TailleY/2)   )       
        
                );

                g.drawLine( 
            
                (int)( (-TailleX/2)*multPersZBoiteProche  +(TailleX/2)    ) ,

                (int)( (TailleY/2)*multPersZBoiteProche   + (TailleY/2)    ) ,

                (int)( (-TailleX/2)*multPersZBoiteProche + (TailleX/2)    ) ,

                (int)( (-TailleY/2)*multPersZBoiteProche + (TailleY/2)   )       
        
                );

            







    } 
    

    public static void DessinerAtome(Atome A, ArrayList<Atome> B){

        double multPersZ=(FOV*Zoom/(A.position.z+TailleZ/(2.0*Zoom) + FOVet));
        double PR = A.rayonCovalent*multPersZ;
        g.setStroke(new BasicStroke());
        double col = 1.0-((A.position.z*2.0*Zoom/TailleZ) + 0.5)*0.5;
        col = clamp(col, 0.0, 1.0);
        if(A.charge > 0.0){
            g.setColor(new Color((int)(col*255f), (int)mix(0.0,col*255f,1.0-Math.min(A.charge/2.0,1.0)), (int)mix(0.0, col*255f, 1.0-Math.min(A.charge/2.0,1.0)), 200));
        }else if(A.charge == 0.0){
            g.setColor(new Color(255,255,255,200));
        }else if(A.charge < 0.0){
            g.setColor(new Color((int)mix(0f,col*255f,1.0-Math.min(-A.charge/2.0,1.0)), (int)mix(0f, col*255f, 1.0-Math.min(-A.charge/2.0,1.0)), (int)(col*255f), 200));
        }
        //g.fillOval((int)(A.position.x*Math.pow(10.0,0) - PR) + (TailleX/2), (TailleY/2) - (int)(A.position.y*Math.pow(10.0,0) + PR), (int)(PR)*2,(int)(PR)*2 );
        g.fillOval((int)(((A.position.x)*multPersZ - PR) + (TailleX/2)), (int)((TailleY/2) - (int)((A.position.y)*multPersZ + PR)),(int)((PR))*2,(int)(PR)*2);

        double ER = 0.2*multPersZ;
        g.setColor(Color.YELLOW);
        for (int i = 0; i < A.positionDoublet.length; i++) {
            Vecteur3f Epos = Vecteur3f.add(A.position, A.positionDoublet[i]);
            g.fillOval((int)(Epos.x*multPersZ - ER) + (TailleX/2), (TailleY/2) - (int)(Epos.y*multPersZ + ER), (int)(ER)*2,(int)(ER)*2);
        }

        for (int i = 0; i < A.liaisonIndexe.length; i++) {
            
            if(A.liaisonIndexe[i] != -1 && !A.liaisonType[i]){

                double multPersZB = (FOV*Zoom/(B.get(A.liaisonIndexe[i]).position.z+TailleZ/(2.0*Zoom) + FOVet));
                g.setStroke(new BasicStroke());
                g.setColor(Color.BLACK);
                g.drawLine(  (TailleX/2) + (int)((A.position.x)*multPersZ), (TailleY/2) - (int)((A.position.y)*multPersZ) , (TailleX/2) + (int)((B.get(A.liaisonIndexe[i]).position.x)*multPersZB) , (TailleY/2) - (int)((B.get(A.liaisonIndexe[i]).position.y)*multPersZB));
            
            }else if(A.liaisonIndexe[i] != -1 && A.liaisonType[i]){

                double multPersZB = (FOV*Zoom/(B.get(A.liaisonIndexe[i]).position.z+TailleZ/(2.0*Zoom) + FOVet));
                g.setStroke(new BasicStroke());
                g.setColor(Color.BLUE);
                g.drawLine(  (TailleX/2) + (int)((A.position.x + 0.3f)*multPersZ), (TailleY/2) - (int)((A.position.y)*multPersZ) , (TailleX/2) + (int)((B.get(A.liaisonIndexe[i]).position.x+0.3f)*multPersZB) , (TailleY/2) - (int)((B.get(A.liaisonIndexe[i]).position.y)*multPersZB));
            }
        }
    }

    private static double mix(double a, double b, double m){
        return (1.0-m)*a + m*b;
    }

    private static double clamp(double a, double b, double c){
        if(a < b){
            return b;
        }else if(a > c){
            return c;
        }else{
            return a;
        }
    }

    public static void énoncerMolécules(ArrayList<Atome> Atomes){
        ArrayList<Integer> vus = new ArrayList<>();
        ArrayList<String> Molécules = new ArrayList<>();
        ArrayList<Integer> molNombre = new ArrayList<>();
        for (int i = 0; i < Atomes.size(); i++) {
            int[] r = ajouterAtomeÀMolécule(Atomes, i, vus);

            String out = "";
            for (int j = 1; j < r.length; j++) {
                if(r[j] > 0){
                    switch (j) {
                        case 1:
                            out += "H"+r[j];
                            break;
                        case 2:
                            out += "He"+r[j];
                            break;
                        case 3:
                            out += "Li"+r[j];
                            break;
                        case 4:
                            out += "Be"+r[j];
                            break;
                        case 5:
                            out += "B"+r[j];
                            break;
                        case 6:
                            out += "C"+r[j];
                            break;
                        case 7:
                            out += "N"+r[j];
                            break;
                        case 8:
                            out += "O"+r[j];
                            break;
                        case 9:
                            out += "F"+r[j];
                            break;
                        case 10:
                            out += "Ne"+r[j];
                            break;
                        case 11:
                            out += "Na"+r[j];
                            break;
                        case 12:
                            out += "Mg"+r[j];
                            break;
                        case 13:
                            out += "Al"+r[j];
                            break;
                        case 14:
                            out += "Si"+r[j];
                            break;
                        case 15:
                            out += "P"+r[j];
                            break;
                        case 16:
                            out += "S"+r[j];
                            break;
                        case 17:
                            out += "Cl"+r[j];
                            break;
                        case 18:
                            out += "Ar"+r[j];
                            break;
                        default:
                            break;
                    }
                }
            }
            if(out != ""){
                //out += " " + r[0];
            }
            if(Molécules.contains(out)){
                molNombre.set(Molécules.indexOf(out),molNombre.get(Molécules.indexOf(out))+1);
            }else if(out != ""){
                Molécules.add(out);
                molNombre.add(1);
            }
        }
        String outB = "";
        int total = 0;
        for (int i = 0; i < molNombre.size(); i++) {
            total += molNombre.get(i);
        }
        for (int k = 0; k < Molécules.size(); k++) {
            outB +=  String.format( "%.2f", 100.0*(double)molNombre.get(k)/(double)total ) + " " + Molécules.get(k)+", ";
        }
        System.out.println(outB);
    }

    public static int[] ajouterAtomeÀMolécule(ArrayList<Atome> Atomes, int indexe, ArrayList<Integer> vus){
        int[] retour = new int[19];
        if(!vus.contains(indexe)){
            vus.add(indexe);
            for (int i = 0; i < Atomes.get(indexe).liaisonIndexe.length; i++) {
                if(!vus.contains(Atomes.get(indexe).liaisonIndexe[i]) && Atomes.get(indexe).liaisonIndexe[i] != -1){
                    int[] r = ajouterAtomeÀMolécule(Atomes, Atomes.get(indexe).liaisonIndexe[i], vus);
                    for (int j = 0; j < r.length; j++) {
                        retour[j] += r[j];
                    }
                }
            }
            retour[Atomes.get(indexe).NP]++;
            retour[0] += Atomes.get(indexe).charge;
        }
        return retour;
    }
}
