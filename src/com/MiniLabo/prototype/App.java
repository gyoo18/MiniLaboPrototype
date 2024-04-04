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
    private static int TailleX = 1440; //Taille de simulation 
    private static int TailleY = 824;
    private static float Zoom = 30f;

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
        double expacement = 6.0;
        double Vinitial = (100f*Math.pow(10.0, -4.0)/(1f/16f*Math.pow(10.0, -16.0)));

        for(int x = 0; x < (TailleX/(Zoom*expacement)) - 0.3; x++){
            for(int y = 0; y < (TailleY/(Zoom*expacement)) - 0.3; y++){
                
                /*Atome Air = new Atome(Math.random()<0.78?7:8);
                Air.position = new Vecteur2f(4+x*expacement + 1.5*expacement/6 - (TailleX/(2*Zoom)),y*expacement + 1.24*expacement/6 - (TailleY/(2*Zoom))+2);
                //H1.vélocité = new Vecteur2f((Math.random() * 2.0 - 1.0) * 5.0 * Math.pow(10.0, 20.0), (Math.random() * 2.0 - 1.0) * 5.0 * Math.pow(10.0, 20.0));
                Air.vélocité = new Vecteur2f(Vinitial*((Math.random() - 0.5f)), Vinitial*((Math.random() - 0.5f)));
                Hs.add(Air);*/

                
                Atome H1 = new Atome(1);
                H1.position = new Vecteur2f(4+x*expacement + expacement*1.8/6 - (TailleX/(2*Zoom)),y*expacement + expacement*1/6 - (TailleY/(2*Zoom))+2);
                //H1.vélocité = new Vecteur2f((Math.random() * 2.0 - 1.0) * 5.0 * Math.pow(10.0, 20.0), (Math.random() * 2.0 - 1.0) * 5.0 * Math.pow(10.0, 20.0));
                H1.vélocité = new Vecteur2f(Vinitial*((Math.random() - 0.5f)), Vinitial*((Math.random() - 0.5f)));
                Hs.add(H1);

                Atome H2 = new Atome(1);
                H2.position = new Vecteur2f(4+x*expacement - 0/6*expacement - (TailleX/(2*Zoom)),y*expacement + expacement*1.8/6 - (TailleY/(2*Zoom))+2);
                H2.vélocité = new Vecteur2f(Vinitial*((Math.random() - 0.5f)), Vinitial*((Math.random() - 0.5f)));
                Hs.add(H2);

                if (Math.random() <0.7){
                Atome H3 = new Atome(1);
                H3.position = new Vecteur2f(4+x*expacement - expacement*1.8/6 - (TailleX/(2*Zoom)),y*expacement + expacement*1/6 - (TailleY/(2*Zoom))+2);
                H3.vélocité = new Vecteur2f(Vinitial*((Math.random() - 0.5f)), Vinitial*((Math.random() - 0.5f)));
                Hs.add(H3);
                    if (Math.random() <0.6){
                    Atome H4 = new Atome(1);
                    H4.position = new Vecteur2f(4+x*expacement - expacement*0/6 - (TailleX/(2*Zoom)),y*expacement - expacement*1.8/6 - (TailleY/(2*Zoom))+2);
                    H4.vélocité = new Vecteur2f(Vinitial*((Math.random() - 0.5f)), Vinitial*((Math.random() - 0.5f)));
                    Hs.add(H4);

                    }
            
                } 
                /*Atome CvO1 = new Atome(Math.random()<0.7?6:8);
                CvO1.position = new Vecteur2f(4+x*expacement -expacement*0/6 - (TailleX/(2*Zoom)),y*expacement -expacement*0/6 - (TailleY/(2*Zoom))+2);
                CvO1.vélocité = new Vecteur2f(Vinitial*((Math.random() - 0.5f)), Vinitial*((Math.random() - 0.5f)));
                Hs.add(CvO1);*/

                Atome C1 = new Atome(6);
                C1.position = new Vecteur2f(4+x*expacement -expacement*0/6 - (TailleX/(2*Zoom)),y*expacement -expacement*0/6 - (TailleY/(2*Zoom))+2);
                C1.vélocité = new Vecteur2f(Vinitial*((Math.random() - 0.5f)), Vinitial*((Math.random() - 0.5f)));
                Hs.add(C1);


                /*Atome C2 = new Atome(6);
                C2.position = new Vecteur2f(x*expacement +1 -(TailleX/(2*Zoom)),y*expacement +1 - (TailleY/(2*Zoom)));
                C2.vélocité = new Vecteur2f((Math.random() * 2.0 - 1.0) * 3.0 * Math.pow(10.0, 1.0), (Math.random() * 2.0 - 1.0) * 3.0 * Math.pow(10.0, 1.0));
                Hs.add(C2);
                Atome C3 = new Atome(6);
                C3.position = new Vecteur2f(x*expacement +3.6 -(TailleX/(2*Zoom)),y*expacement +3.6 - (TailleY/(2*Zoom)));
                C3.vélocité = new Vecteur2f((Math.random() * 2.0 - 1.0) * 3.0 * Math.pow(10.0, 1.0), (Math.random() * 2.0 - 1.0) * 3.0 * Math.pow(10.0, 1.0));
                Hs.add(C3);
                Atome C4 = new Atome(6);
                C4.position = new Vecteur2f(x*expacement +3.6 -(TailleX/(2*Zoom)),y*expacement +3.6 - (TailleY/(2*Zoom)));
                C4.vélocité = new Vecteur2f((Math.random() * 2.0 - 1.0) * 3.0 * Math.pow(10.0, 1.0), (Math.random() * 2.0 - 1.0) * 3.0 * Math.pow(10.0, 1.0));
                Hs.add(C4);*/

                 /*Atome H1 = new Atome(1);
                H1.position = new Vecteur2f(1,1);
                //H1.vélocité = new Vecteur2f((Math.random() * 2.0 - 1.0) * 5.0 * Math.pow(10.0, 20.0), (Math.random() * 2.0 - 1.0) * 5.0 * Math.pow(10.0, 20.0));
                Hs.add(H1);

                Atome H2 = new Atome(1);
                H2.position = new Vecteur2f(-1,1);
                //H2.vélocité = new Vecteur2f((Math.random() * 2.0 - 1.0) * 5.0 * Math.pow(10.0, 20.0), (Math.random() * 2.0 - 1.0) * 5.0 * Math.pow(10.0, 20.0));
                Hs.add(H2);*/

                /*Atome O = new Atome(8);
                O.position = new Vecteur2f(0,0);
                //O.vélocité = new Vecteur2f((Math.random() * 2.0 - 1.0) * 5.0 * Math.pow(10.0, 20.0), (Math.random() * 2.0 - 1.0) * 5.0 * Math.pow(10.0, 20.0));
                Hs.add(O);*/

               // H1.vélocité = new Vecteur2f(Vinitial*((Math.random() - 0.5f))*H1.m, Vinitial*((Math.random() - 0.5f))*H1.m);
                
            }
        }   while (true) {
            g.setColor(new Color(150, 150, 150, 100));
            g.fillRect(0, 0, TailleX, TailleY);

            double Ke = 0;
            for (int i = 0; i < Hs.size(); i++) {
                Ke += Hs.get(i).m*Math.pow( Hs.get(i).vélocité.length(), 2.0)*0.5f;
            }
            //System.out.println("Ke : " + Ke);

            /*int lié = 0;
            for (int i = 0; i < Hs.size(); i++) {
                if(Hs.get(i).liaison != -1){lié++;}
            }
            System.err.println("Liés : " + 100.0*(double)lié/(double)Hs.size());*/

            for (int N = 0; N < 10; N++) {                                                 //Sous-étapes. Répète N fois/image
                for (int i = 0; i < Hs.size(); i++) {
                    Hs.get(i).miseÀJourForces(Hs, i, TailleX, TailleY, Zoom); //Mise à jour des forces
                }
                for (int i = 0; i < Hs.size(); i++) {
                    Hs.get(i).miseÀJourPos(2f*1f/16f*Math.pow(10.0, -16.0)); //Mise à jour de la position. Change Delta t
                }
            }

            for (int i = 0; i < Hs.size(); i++) {
                DessinerAtome(Hs.get(i),Hs);
            }

            énoncerMolécules(Hs);
           //System.out.println("Timer : " + );

            SwingUtilities.updateComponentTreeUI(frame);
            //Thread.sleep(1000);
          
        }
    }

    public static void DessinerAtome(Atome A, ArrayList<Atome> B){
        double PR = A.rayonCovalent*Zoom;
        g.setStroke(new BasicStroke());
        if(A.charge > 0.0){
            g.setColor(new Color(255, (int)mix(0.0,255f,1.0-Math.min(A.charge/2.0,1.0)), (int)mix(0.0, 255f, 1.0-Math.min(A.charge/2.0,1.0))));
        }else if(A.charge == 0.0){
            g.setColor(Color.WHITE);
        }else if(A.charge < 0.0){
            g.setColor(new Color((int)mix(0f,255f,1.0-Math.min(-A.charge/2.0,1.0)), (int)mix(0f, 255f, 1.0-Math.min(-A.charge/2.0,1.0)), 255));
        }
        //g.fillOval((int)(A.position.x*Math.pow(10.0,0) - PR) + (TailleX/2), (TailleY/2) - (int)(A.position.y*Math.pow(10.0,0) + PR), (int)(PR)*2,(int)(PR)*2 );
        g.fillOval((int)(Zoom*A.position.x - PR) + (TailleX/2), (TailleY/2) - (int)(Zoom*A.position.y + PR), (int)(PR)*2,(int)(PR)*2);

        double ER = 0.1*Zoom;
        g.setColor(Color.YELLOW);
        for (int i = 0; i < A.anglesDoublets.length; i++) {
            Vecteur2f Epos = Vecteur2f.add(A.position,new Vecteur2f(A.anglesDoublets[i],A.rayonCovalent,0));
            g.fillOval((int)(Zoom*Epos.x - ER) + (TailleX/2), (TailleY/2) - (int)(Zoom*Epos.y + ER), (int)(ER)*2,(int)(ER)*2);
        }

        for (int i = 0; i < A.liaisonIndexe.length; i++) {
            if(A.liaisonIndexe[i] != -1 && !A.liaisonType[i]){
                g.setStroke(new BasicStroke());
                g.setColor(Color.BLACK);
                g.drawLine(  (TailleX/2) + (int)(A.position.x*Zoom ), (TailleY/2) - (int)(A.position.y*Zoom) , (TailleX/2) + (int)(B.get(A.liaisonIndexe[i]).position.x*Zoom) , (TailleY/2) - (int)(B.get(A.liaisonIndexe[i]).position.y*Zoom) );
            }else if(A.liaisonIndexe[i] != -1 && A.liaisonType[i]){
                g.setStroke(new BasicStroke());
                g.setColor(Color.BLUE);
                g.drawLine( (TailleX/2) + (int)(A.position.x*Zoom + 2), (TailleY/2) - (int)(A.position.y*Zoom) , (TailleX/2) + (int)(B.get(A.liaisonIndexe[i]).position.x*Zoom + 2 ), (TailleY/2) - (int)(B.get(A.liaisonIndexe[i]).position.y*Zoom) );
            }
        }
    }

    private static double mix(double a, double b, double m){
        return (1.0-m)*a + m*b;
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
