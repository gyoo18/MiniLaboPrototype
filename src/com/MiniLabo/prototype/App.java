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
    public static int TailleZ = 200;
    public static float Zoom = 40f;
    public static int FOV = 30;     //Champ de vision de la caméra
    public static int FOVet = FOV;
    private static int FOVBoite = FOV;
    private static int FOVetBoite = FOV;


    public static void main(String[] args) throws Exception {
        System.out.println("Hello, World!");

        BufferedImage b = new BufferedImage(TailleX, TailleY,BufferedImage.TYPE_4BYTE_ABGR);    //Initialiser l'image de dessin des atomes
        g = (Graphics2D) b.getGraphics();   //Initialiser le contexte graphique

        JLabel image = new JLabel(new ImageIcon(b)); //Créer un objet Image pour l'écran
        JFrame frame = new JFrame();                //Initialiser l'écran
        frame.setSize(TailleX + 100,TailleY + 100); //Taille de la fenêtre
        frame.add(image);                           //Ajouter l'objet Image à l'écran
        frame.setVisible(true);                   //Afficher la fenêtre

        try{
            //Thread.sleep(3000);
        }catch(Exception e){
            e.printStackTrace();
        }

        //Initialiser les atomes
        ArrayList<Atome> Hs = new ArrayList<>();       //Liste des atomes
        ArrayList<Integer> indexe = new ArrayList<>(); //Ordre de dessin des atomes.
        double espacement = 3;                        //Espacement entre les atomes
        for(int x = 0; x < (TailleX/(Zoom*espacement)) - 1; x++){
            for(int y = 0; y < (TailleY/(Zoom*espacement)) - 1; y++){
                for(int z = 0; z < (TailleZ/(Zoom*espacement)) - 1/espacement; z++){

                    Atome C = new Atome(8);
                    C.position = new Vecteur3f(x*espacement + 1 - (TailleX/(2*Zoom)),y*espacement - (TailleY/(2*Zoom)), ((-z)*espacement + (TailleZ/(2*Zoom))));
                    C.vélocité = new Vecteur3f((Math.random() * 2.0 - 1.0) * 3.0 * Math.pow(10.0, 14.0), (Math.random() * 2.0 - 1.0) * 3.0 * Math.pow(10.0, 14.0),(Math.random() * 2.0 - 1.0) * 3.0 * Math.pow(10.0, 14.0));
                    Hs.add(C);

                    /*Atome H2 = new Atome(17);
                    H2.position = new Vecteur3f(x*espacement + 2.54 - (TailleX/(2*Zoom)),y*espacement - (TailleY/(2*Zoom)), ((-z)*espacement + (TailleZ/(2*Zoom))));
                    H2.vélocité = new Vecteur3f((Math.random() * 2.0 - 1.0) * 3.0 * Math.pow(10.0, 14.0), (Math.random() * 2.0 - 1.0) * 3.0 * Math.pow(10.0, 14.0),(Math.random() * 2.0 - 1.0) * 3.0 * Math.pow(10.0, 14.0));
                    //Hs.add(H2);*/

                    /*Atome H3 = new Atome(1);
                    H3.position = new Vecteur3f(x*espacement - (TailleX/(2*Zoom)),y*espacement - (TailleY/(2*Zoom)), ((-z)*espacement + (TailleZ/(2*Zoom))));
                    H2.vélocité = new Vecteur3f((Math.random() * 2.0 - 1.0) * 3.0 * Math.pow(10.0, 14.0), (Math.random() * 2.0 - 1.0) * 3.0 * Math.pow(10.0, 14.0),(Math.random() * 2.0 - 1.0) * 3.0 * Math.pow(10.0, 14.0));
                    Hs.add(H3);*/
                }
            }
        }

        for (int i = 0; i < Hs.size(); i++) {
            indexe.add(i);  //Ajouter les atomes dans l'ordre de dessin
        }
        
        double temps = 0.0;                         //Temps de simulation écoulé
        long chorono = System.currentTimeMillis();  //Temps au début de la simulation
        double dt = 1*Math.pow(10.0,-17.0);     //Delta temps de la simulation
        while (true) {
            g.setColor(new Color(100, 00, 100, 100));   //Couleur de l'arrière-plan
            g.fillRect(0, 0, TailleX, TailleY);             //Rafraîchir l'écran en effaçant tout

            Atome.MettreÀJourEnvironnement(Hs);                 //Mettre à jour l'environnement du point de vue des atomes.

            for (int N = 0; N < 20; N++) {              //Sous-étapes. Répète N fois/image
                for (int i = 0; i < Hs.size(); i++) {
                    Hs.get(i).miseÀJourLiens(Hs, i);    //Créer/Détruire les liens.
                }
                Intégrateur.IterVerlet(Hs, dt); //Mise à jour de la position.
                temps += dt;
            }

            //Affichage de la simulation
            DessinerBoite();                            //Dessiner le domaine

            //Ordonner les atomes pour résoudre le problème de visibilité
            for (int i = 0; i < Hs.size()-1; i++) {
                if(Hs.get(indexe.get(i)).position.z < Hs.get(indexe.get(i+1)).position.z){
                    int a = indexe.get(i);
                    indexe.set( i, indexe.get(i+1));
                    indexe.set(i+1, a);
                }
            }

            //Dessiner les atomes dans l'ordre
            for (int i = 0; i < indexe.size(); i++) {
                DessinerAtome(Hs.get(indexe.get(i)), Hs);
            }
            
            //Statistiques sur la vitesse de la simulation
            System.out.println("temps : " + String.format("%.03f", temps*Math.pow(10.0,15.0)) + " fs, rapidité : " + String.format("%.03f", (temps*Math.pow(10.0,15.0))/((double)(System.currentTimeMillis()-chorono)/1000.0)) + " fs/s");

            //énoncerMolécules(Hs);     //Lister les pourcentages de présence de chaques molécules dans la simulation

            SwingUtilities.updateComponentTreeUI(frame);    //Mise à jour de l'affichage
        }
    }

    public static void DessinerBoite(){
        double multPersZBoiteLoin=(FOVBoite/(TailleZ/(2*Zoom)+TailleZ/(2.0*Zoom) + FOVetBoite));    //Multiplicateur de profondeur de la face arrière (Forme la perspective)
        double multPersZBoiteProche=(FOVBoite/(-TailleZ/(2*Zoom)+TailleZ/(2.0*Zoom) + FOVetBoite)); //Multiplicateur de profondeur de la face avant
        g.setStroke(new BasicStroke());
        g.setColor(Color.MAGENTA);  //Couleur de la boîte

        //Face arrière
        g.drawLine( 
            (int)( (TailleX/2) + ( TailleX/2)*multPersZBoiteLoin ),  // Point +++
            (int)( (TailleY/2) - ( TailleY/2)*multPersZBoiteLoin ),
            (int)( (TailleX/2) + (-TailleX/2)*multPersZBoiteLoin ),  // Point -++
            (int)( (TailleY/2) - ( TailleY/2)*multPersZBoiteLoin )
        );

        g.drawLine(
            (int)( (TailleX/2) + ( TailleX/2)*multPersZBoiteLoin ), // Point +-+
            (int)( (TailleY/2) - (-TailleY/2)*multPersZBoiteLoin ),
            (int)( (TailleX/2) + (-TailleX/2)*multPersZBoiteLoin ), // Point --+
            (int)( (TailleY/2) - (-TailleY/2)*multPersZBoiteLoin )
        );

        g.drawLine( 
            (int)( (TailleX/2) + ( TailleX/2)*multPersZBoiteLoin ), // Point +-+
            (int)( (TailleY/2) - (-TailleY/2)*multPersZBoiteLoin ),
            (int)( (TailleX/2) + ( TailleX/2)*multPersZBoiteLoin ), // Point +++ 
            (int)( (TailleY/2) - ( TailleY/2)*multPersZBoiteLoin )       
        );

        g.drawLine( 
            (int)( (TailleX/2) + (-TailleX/2)*multPersZBoiteLoin ), // Point --+
            (int)( (TailleY/2) - (-TailleY/2)*multPersZBoiteLoin ),
            (int)( (TailleX/2) + (-TailleX/2)*multPersZBoiteLoin ), // Point -++
            (int)( (TailleY/2) - ( TailleY/2)*multPersZBoiteLoin )
        );

        //Arrêtes de côtés 
        g.drawLine( 
            (int)( (TailleX/2) + ( TailleX/2)*multPersZBoiteProche ), // Point +-+
            (int)( (TailleY/2) - (-TailleY/2)*multPersZBoiteProche ),
            (int)( (TailleX/2) + ( TailleX/2)*multPersZBoiteLoin   ), // Point +--
            (int)( (TailleY/2) - (-TailleY/2)*multPersZBoiteLoin   )
        );

        g.drawLine(
            (int)( (TailleX/2) + ( TailleX/2)*multPersZBoiteProche ), // Point +++
            (int)( (TailleY/2) - ( TailleY/2)*multPersZBoiteProche ),
            (int)( (TailleX/2) + ( TailleX/2)*multPersZBoiteLoin   ), // Point ++-
            (int)( (TailleY/2) - ( TailleY/2)*multPersZBoiteLoin   )       
        );

        g.drawLine(
            (int)( (TailleX/2) + (-TailleX/2)*multPersZBoiteProche ), // Point --+
            (int)( (TailleY/2) - (-TailleY/2)*multPersZBoiteProche ),
            (int)( (TailleX/2) + (-TailleX/2)*multPersZBoiteLoin   ), // Point ---
            (int)( (TailleY/2) - (-TailleY/2)*multPersZBoiteLoin   )       
        );

        g.drawLine( 
            (int)( (TailleX/2) + (-TailleX/2)*multPersZBoiteProche ), // Point -++
            (int)( (TailleY/2) - ( TailleY/2)*multPersZBoiteProche ),
            (int)( (TailleX/2) + (-TailleX/2)*multPersZBoiteLoin   ), // Point -+-
            (int)( (TailleY/2) - ( TailleY/2)*multPersZBoiteLoin   )
        );

        //Face Avant
        g.drawLine( 
            (int)( (TailleX/2) + ( TailleX/2)*multPersZBoiteProche ), // Point ++-
            (int)( (TailleY/2) - ( TailleY/2)*multPersZBoiteProche ),
            (int)( (TailleX/2) + (-TailleX/2)*multPersZBoiteProche ), // Point -+-
            (int)( (TailleY/2) - ( TailleY/2)*multPersZBoiteProche )       
        );

        g.drawLine( 
            (int)( (TailleX/2) + ( TailleX/2)*multPersZBoiteProche ), // Point +--
            (int)( (TailleY/2) - (-TailleY/2)*multPersZBoiteProche ),
            (int)( (TailleX/2) + (-TailleX/2)*multPersZBoiteProche ), // Point ---
            (int)( (TailleY/2) - (-TailleY/2)*multPersZBoiteProche )       
        );

        g.drawLine( 
            (int)( (TailleX/2) + ( TailleX/2)*multPersZBoiteProche ), // Point +--
            (int)( (TailleY/2) - (-TailleY/2)*multPersZBoiteProche ),
            (int)( (TailleX/2) + ( TailleX/2)*multPersZBoiteProche ), // Point ++-
            (int)( (TailleY/2) - ( TailleY/2)*multPersZBoiteProche )
        );

        g.drawLine( 
            (int)( (TailleX/2) + (-TailleX/2)*multPersZBoiteProche ), // Point ---
            (int)( (TailleY/2) - (-TailleY/2)*multPersZBoiteProche ),
            (int)( (TailleX/2) + (-TailleX/2)*multPersZBoiteProche ), // Point -+-
            (int)( (TailleY/2) - ( TailleY/2)*multPersZBoiteProche )       
        );
    } 
    
    public static void DessinerAtome(Atome A, ArrayList<Atome> B){

        double multPersZ=(FOV*Zoom/(A.position.z+TailleZ/(2.0*Zoom) + FOVet)); //Multiplicateur de profondeur (forme la perspective)

        //Dessin des doublets en avant de l'atome
        double ER = 0.15*multPersZ; //Rayon 2D du doublet
        g.setColor(Color.YELLOW);   //Couleur de l'électron
        for (int i = 0; i < A.positionDoublet.length; i++) {
            if(A.positionDoublet[i] .z> 0.0){
                Vecteur3f Epos = Vecteur3f.add(A.position, A.positionDoublet[i]); //Position 3D du doublet
                //Dessiner le doublet
                g.fillOval((int)(Epos.x*multPersZ - ER) + (TailleX/2), (TailleY/2) - (int)(Epos.y*multPersZ + ER), (int)(ER)*2,(int)(ER)*2);
            }
        }

        g.setStroke(new BasicStroke());
        double col = 1.0-((A.position.z*2.0*Zoom/TailleZ) + 0.5)*0.5; //Obscurissement avec la profondeur
        col = clamp(col, 0.0, 1.0);
        //Rouge = charge+, Blanc = neutre, Bleu = charge-
        if(A.charge > 0.0){
            //Rouge
            g.setColor(new Color((int)(col*255f), (int)mix(0.0,col*255f,1.0-Math.min(A.charge/2.0,1.0)), (int)mix(0.0, col*255f, 1.0-Math.min(A.charge/2.0,1.0)), 200));
        }else if(A.charge == 0.0){
            //Blanc
            g.setColor(new Color((int)(255*col),(int)(255*col),(int)(255*col),200));
        }else if(A.charge < 0.0){
            //Bleu
            g.setColor(new Color((int)mix(0f,col*255f,1.0-Math.min(-A.charge/2.0,1.0)), (int)mix(0f, col*255f, 1.0-Math.min(-A.charge/2.0,1.0)), (int)(col*255f), 200));
        }

        double PR = A.rayonCovalent*multPersZ;  //Rayon 2D de l'atome
        //Dessiner l'atome
        g.fillOval((int)(((A.position.x)*multPersZ - PR) + (TailleX/2)), (int)((TailleY/2) - (int)((A.position.y)*multPersZ + PR)),(int)((PR))*2,(int)(PR)*2);

        //Dessin des doublets en arrières de l'atome
        g.setColor(Color.YELLOW); //Couleur de l'électron
        for (int i = 0; i < A.positionDoublet.length; i++) {
            if(A.positionDoublet[i] .z < 0.0){
                Vecteur3f Epos = Vecteur3f.add(A.position, A.positionDoublet[i]);//Position 3D du doublet
                //Dessiner le doublet
                g.fillOval((int)(Epos.x*multPersZ - ER) + (TailleX/2), (TailleY/2) - (int)(Epos.y*multPersZ + ER), (int)(ER)*2,(int)(ER)*2);
            }
        }

        //Dessiner les liens
        for (int i = 0; i < A.liaisonIndexe.length; i++) {
            
            if(A.liaisonIndexe[i] != -1 && !A.liaisonType[i]){
                // Si c'est une liaison sigma
                double multPersZB = (FOV*Zoom/(B.get(A.liaisonIndexe[i]).position.z+TailleZ/(2.0*Zoom) + FOVet)); //Profondeur du dexième atome
                g.setStroke(new BasicStroke());
                g.setColor(Color.BLACK);        //Couleur de la liaison
                //Dessiner la liaison
                g.drawLine(  (TailleX/2) + (int)((A.position.x)*multPersZ), (TailleY/2) - (int)((A.position.y)*multPersZ) , (TailleX/2) + (int)((B.get(A.liaisonIndexe[i]).position.x)*multPersZB) , (TailleY/2) - (int)((B.get(A.liaisonIndexe[i]).position.y)*multPersZB));
            
            }else if(A.liaisonIndexe[i] != -1 && A.liaisonType[i]){
                //Si c'est une liaison pi
                double multPersZB = (FOV*Zoom/(B.get(A.liaisonIndexe[i]).position.z+TailleZ/(2.0*Zoom) + FOVet));   // Profondeur du deuxième atome
                g.setStroke(new BasicStroke());
                g.setColor(Color.BLUE);         //Couleur de la liaison
                //Dessiner la liaison
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

    //Liste le pourcentage de présence de chaque molécule dans la simulation
    public static void énoncerMolécules(ArrayList<Atome> Atomes){

        ArrayList<Integer> vus = new ArrayList<>();         //Tout les atomes déjà évalués
        ArrayList<String> Molécules = new ArrayList<>();    //Toutes les molécules présentes
        ArrayList<Integer> molNombre = new ArrayList<>();   //Quantité de chaque molécule

        for (int i = 0; i < Atomes.size(); i++) {

            int[] r = ajouterAtomeÀMolécule(Atomes, i, vus);    //retourne la liste des atomes attachés à cet atome
            // r est une liste de la quantité de chaque type d'atomes liés chaque case est liée au numéro
            //   atomique et son contenu indique la quantité. Ex.: r[2] = 5 indique qu'il y a 5 Héliums
            //   r[0] indique la charge de la molécule

            //Traduit la liste d'atome en formule chimique
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

            //Si on as déjà cet atome, ajoute au total, sinon ajoute à la liste
            if(Molécules.contains(out)){
                molNombre.set(Molécules.indexOf(out),molNombre.get(Molécules.indexOf(out))+1);
            }else if(out != ""){
                Molécules.add(out);
                molNombre.add(1);
            }
        }

        //Imprimer la liste des molécules
        String outB = "";
        int total = 0;  //Le nombre de molécules présentes dans la simulation
        for (int i = 0; i < molNombre.size(); i++) {
            total += molNombre.get(i);
        }
        //Imprime en pourcentage de présence.
        for (int k = 0; k < Molécules.size(); k++) {
            outB +=  String.format( "%.2f", 100.0*(double)molNombre.get(k)/(double)total ) + " " + Molécules.get(k)+", ";
        }
        System.out.println(outB);
    }

    //Vas chercher tout les atomes reliés à cet atome et renvoie ainsi les constituants de la molécule.
    public static int[] ajouterAtomeÀMolécule(ArrayList<Atome> Atomes, int indexe, ArrayList<Integer> vus){

        int[] retour = new int[19]; // Initialise la liste des atomes de retours.
        // retour est une liste de la quantité de chaque type d'atomes liés chaque case est liée au numéro
        //   atomique et son contenu indique la quantité. Ex.: r[2] = 5 indique qu'il y a 5 Héliums
        //   retour[0] indique la charge de la molécule.

        if(!vus.contains(indexe)){
            // Si l'atome n'a pas déjà été traité (A)
            vus.add(indexe);    //Indiquer qu'il aura été traité
            for (int i = 0; i < Atomes.get(indexe).liaisonIndexe.length; i++) {
                // Chercher dans tout les atomes liés (A')
                if(!vus.contains(Atomes.get(indexe).liaisonIndexe[i]) && Atomes.get(indexe).liaisonIndexe[i] != -1){
                    //Si cet atome lié n'a pas déjà été traité
                    //Aller chercher tout les atomes liés à A' (A'')
                    int[] r = ajouterAtomeÀMolécule(Atomes, Atomes.get(indexe).liaisonIndexe[i], vus);
                    //Ajouter ces atomes à la liste de retour
                    for (int j = 0; j < r.length; j++) {
                        retour[j] += r[j];
                    }
                }
            }
            retour[Atomes.get(indexe).NP]++;    //Ajouter A au total
            retour[0] += Atomes.get(indexe).charge;     //Ajouter sa charge au total de la charge.
        }
        return retour;
    }
}
