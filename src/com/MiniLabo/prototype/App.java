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
    private static int TailleX = 512; //Taille de simulation 
    private static int TailleY = 512;
    private static int TailleZ = 200;
    private static float Zoom = 10f;

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

        ArrayList<Atome> Hs = new ArrayList<>();
        double espacement = 6.0;
        for(int x = 0; x < (TailleX/(Zoom*espacement)) - 1; x++){
            for(int y = 0; y < (TailleY/(Zoom*espacement)) - 1; y++){
                for(int z = 0; z < (TailleZ/(Zoom*espacement)) - 1; z++){

                    Atome H1 = new Atome(1);
                    H1.position = new Vecteur3f(x*espacement + 1 - (TailleX/(2*Zoom)),y*espacement + 1 - (TailleY/(2*Zoom)),(TailleZ/(2*Zoom))); //z*espacement + 1 - (TailleZ/(2*Zoom)
                    //H1.vélocité = new Vecteur3f((Math.random() * 2.0 - 1.0) * 5.0 * Math.pow(10.0, 20.0), (Math.random() * 2.0 - 1.0) * 5.0 * Math.pow(10.0, 20.0));
                    //Hs.add(H1);

                    Atome H2 = new Atome(11);
                    H2.position = new Vecteur3f(x*espacement - 0 - (TailleX/(2*Zoom)),y*espacement + 2.54 - (TailleY/(2*Zoom)), (-z*espacement + (TailleZ/(2*Zoom))));
                    //H2.vélocité = new Vecteur3f((Math.random() * 2.0 - 1.0) * 3.0 * Math.pow(10.0, 13.0), (Math.random() * 2.0 - 1.0) * 3.0 * Math.pow(10.0, 13.0),(Math.random() * 2.0 - 1.0) * 3.0 * Math.pow(10.0, 13.0));
                    Hs.add(H2);

                    Atome O = new Atome(17);
                    O.position = new Vecteur3f(x*espacement - (TailleX/(2*Zoom)),y*espacement - (TailleY/(2*Zoom)), (-z*espacement + (TailleZ/(2*Zoom))));
                    //O.vélocité = new Vecteur3f((Math.random() * 2.0 - 1.0) * 3.0 * Math.pow(10.0, 13.0), (Math.random() * 2.0 - 1.0) * 3.0 * Math.pow(10.0, 13.0),(Math.random() * 2.0 - 1.0) * 3.0 * Math.pow(10.0, 13.0));
                    Hs.add(O);
                }
            }
        }
        
        double temps = 0.0;
        long chorono = System.currentTimeMillis();
        double dt = 7.0*Math.pow(10.0,-16.0); //Delta t
        while (true) {
            g.setColor(new Color(150, 150, 150, 100));
            g.fillRect(0, 0, TailleX, TailleY);

            Atome.MettreÀJourEnvironnement(Hs);

            for (int N = 0; N < 10; N++) {          //Sous-étapes. Répète N fois/image
                for (int i = 0; i < Hs.size(); i++) {
                    Hs.get(i).miseÀJourLiens(Hs, i); //Mise à jour des liens
                }
                Intégrateur.IterRK4((ArrayList<ObjetPhysique>)(ArrayList<?>)Hs, dt, TailleX, TailleY, TailleZ, Zoom); //Mise à jour de la position.
                temps += dt;
            }

            for (int i = 0; i < Hs.size(); i++) {
                DessinerAtome(Hs.get(i),Hs);
            }

            System.out.println("temps : " + String.format("%.03f", temps*Math.pow(10.0,15.0)) + " fs, rapidité : " + String.format("%.03f", (temps*Math.pow(10.0,15.0))/((double)(System.currentTimeMillis()-chorono)/1000.0)) + " fs/s");

            //énoncerMolécules(Hs);

            SwingUtilities.updateComponentTreeUI(frame);
            //Thread.sleep(1000);
        }
    }

    public static void DessinerAtome(Atome A, ArrayList<Atome> B){
        double PR = 70*A.rayonCovalent*Zoom/(A.position.z+TailleZ/(2.0*Zoom) + 70.0);
        g.setStroke(new BasicStroke());
        double col = 1.0-((A.position.z*2.0*Zoom/TailleZ) + 0.5)*0.5;
        col = clamp(col, 0.0, 1.0);
        if(A.charge > 0.0){
            g.setColor(new Color((int)(col*255f), (int)mix(0.0,col*255f,1.0-Math.min(A.charge/2.0,1.0)), (int)mix(0.0, col*255f, 1.0-Math.min(A.charge/2.0,1.0)), 150));
        }else if(A.charge == 0.0){
            g.setColor(new Color(255,255,255,150));
        }else if(A.charge < 0.0){
            g.setColor(new Color((int)mix(0f,col*255f,1.0-Math.min(-A.charge/2.0,1.0)), (int)mix(0f, col*255f, 1.0-Math.min(-A.charge/2.0,1.0)), (int)(col*255f), 150));
        }
        //g.fillOval((int)(A.position.x*Math.pow(10.0,0) - PR) + (TailleX/2), (TailleY/2) - (int)(A.position.y*Math.pow(10.0,0) + PR), (int)(PR)*2,(int)(PR)*2 );
        g.fillOval((int)(((70*Zoom*A.position.x)/(A.position.z+TailleZ/(2.0*Zoom) + 70.0) - PR) + (TailleX/2)), (int)((TailleY/2) - (int)((70.0*Zoom*A.position.y)/(A.position.z+TailleZ/(2.0*Zoom) + 70.0) + PR)),(int)((PR))*2,(int)(PR)*2);

        double ER = 270*0.1*Zoom/(A.position.z+TailleZ/2);
        g.setColor(Color.YELLOW);
        for (int i = 0; i < A.anglesDoublets.length; i++) {
            Vecteur3f Epos = Vecteur3f.add(A.position,new Vecteur3f(A.anglesDoublets[i],A.rayonCovalent,0));
            //g.fillOval((int)(Zoom*Epos.x - ER) + (TailleX/2), (TailleY/2) - (int)(Zoom*Epos.y + ER), (int)(ER)*2,(int)(ER)*2);
        }

        for (int i = 0; i < A.liaisonIndexe.length; i++) {
            if(A.liaisonIndexe[i] != -1 && !A.liaisonType[i]){
                g.setStroke(new BasicStroke());
                g.setColor(Color.BLACK);
                //g.drawLine(  (TailleX/2) + (int)(160*(A.position.x*Zoom)/(A.position.z+TailleZ/2)), (TailleY/2) - (int)(160*(A.position.y*Zoom)/(A.position.z+TailleZ/2)) , (TailleX/2) + (int)(160*(B.get(A.liaisonIndexe[i]).position.x*Zoom)/(A.position.z+TailleZ/2)) , (TailleY/2) - (int)(160*(B.get(A.liaisonIndexe[i]).position.y*Zoom)) );
            }else if(A.liaisonIndexe[i] != -1 && A.liaisonType[i]){
                g.setStroke(new BasicStroke());
                g.setColor(Color.BLUE);
                //g.drawLine( (TailleX/2) + (int)(160*(A.position.x*Zoom + 2)/(A.position.z+TailleZ/2)), (TailleY/2) - (int)(160*(A.position.y*Zoom)/(A.position.z+TailleZ/2)) , (TailleX/2) + (int)(160*(B.get(A.liaisonIndexe[i]).position.x*Zoom+2)/(A.position.z+TailleZ/2)), (TailleY/2) - (int)(160*(B.get(A.liaisonIndexe[i]).position.y*Zoom)/(A.position.z+TailleZ/2)) );
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
