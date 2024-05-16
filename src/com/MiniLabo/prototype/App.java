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
import java.awt.RenderingHints;

public class App {

    public static int TailleX = 1000; //Taille de simulation 
    public static int TailleY = 512;
    public static int TailleZ = 512;
    public static float Zoom = 30f;
    public static int FOV = 70;     //Champ de vision de la caméra
    public static int FOVet = FOV;
    private static int FOVBoite = FOV;
    private static int FOVetBoite = FOV;

    public static ArrayList<Atome> Hs = new ArrayList<>();       //Liste des atomes
    public static ArrayList<Integer> indexe = new ArrayList<>(); //Ordre de dessin des atomes.

    /**Temps réel de départ de la simulation en ms */
    public static long départ = System.currentTimeMillis();
    /**Temps réel écoulé depuis le début de la simulation en ms*/
    public static long chrono = 0; 
    /**Temps de simlation écoulé depuis le début de la simulation en fs */
    public static double temps = 0;
    /**Delta temps de simulation entre chaque mise à jour de la simulation en fs */
    public static double dt = 0;
    /**Delta temps en temps réel entre chaque mise à jour de la simulation en ms */
    public static long DeltaT = 0;

    private static String[] AnalyseTexte = new String[12];
    private static double[] AnalyseValeurs = new double[AnalyseTexte.length];
    private static String Valeurs = "";
    public static Vecteur3D ForceSytème = new Vecteur3D(0);
    private static ArrayList<Vecteur2D> GraphiqueVal = new ArrayList<>();
    private static ArrayList<Vecteur2D> GraphiqueVal2 = new ArrayList<>();

    private static BoucleDessin boucleDessin = new BoucleDessin();

    public static void main(String[] args) throws Exception {
        System.out.println("Bienvenue dans MiniLabo!");

        Initialisation();
        simulation();
        //Analyse se fait à partir de simulation();
        
    }

    public static void Initialisation(){
        System.out.println("Initialisation");

        Thread thread = new Thread(boucleDessin);
        boucleDessin.mode = com.MiniLabo.prototype.App.BoucleDessin.MODE.INIT;
        thread.start();
        //Molécule de base

        /*//Initialiser les atomes en grille
        float [] espacement = {3f,2f,2f};        //Espacement entre les atomes en x,y,z
        for(int x = 1; Math.abs(x) < (TailleX/(Zoom*espacement[0])) - 1; x++){
            for(int y = 1; Math.abs(y) < (TailleY/(Zoom*espacement[1])) - 1; y++){
                for(int z = 1; Math.abs(z) < (TailleZ/(Zoom*espacement[2])) -1 ; z++){

                    //int x = 1;int y = 1; int z = 1;
                    H2O.position = new Vecteur3D(-(TailleX/(2.0*Zoom)) + x*espacement[0], -(TailleY/(2.0*Zoom)) + y*espacement[1], -(TailleZ/(2.0*Zoom)) + z*espacement[2]);
                    MoléculeRéf.intégrerÀSimulation(Hs, H2O);
                }
            }
        }*/
        
        //Initialiser les atomes selon l'algorithme de poisson
        int NbMolécules = 1000;  //Nombre de molécules voulus
        int totalMolécules = 0;//Nombre de molécules ajoutés
        int essais = 0;
        int NBessais = 40;           //Nombre d'essais à placer la molécule
        boolean BEAA = true;   //Mode de calcul d'intersection. Faux = sphère, Vrai = BEAA
        double tampon =0.1;  //Zone tampon entre les atomes

        System.out.println("Placement des "+NbMolécules+" molécules.");
        long timer = System.currentTimeMillis();
        //Placer une molécule dans la simulation tant qu'on n'aura pas atteint le total voulus.
        //Si on essais de placer la molécule trops de fois, la simulation est déjà pleine et il faut arrêter.
        while (totalMolécules < NbMolécules && essais < NBessais) {
            essais++;
            MoléculeRéf mol = MoléculeRéf.avoirH2O();
            if(totalMolécules < 3){
                mol = MoléculeRéf.avoirNaCl();
            }if(totalMolécules >= 3 && totalMolécules < 6){
                //mol = MoléculeRéf.avoirOHm();
            }

            //position aléatoire dans le domaine.
            Vecteur3D position = new Vecteur3D(2.0*(Math.random()-0.5) * (TailleX/(2.0*Zoom) - mol.BEAA.x),2.0*(Math.random()-0.5) * (TailleY/(2.0*Zoom) - mol.BEAA.y),2.0*(Math.random()-0.5) * (TailleZ/(2.0*Zoom) - mol.BEAA.z));
            //position = new Vecteur3D(0);
            boolean intersecte = false;
            for (int i = 0; i < Hs.size(); i++) {
                //Réessayer si cet emplacement intersecte un atome dans la simulation
                if(BEAA){
                    //Intersection avec la BEAA
                    Vecteur3D posRel = Vecteur3D.sous(Hs.get(i).position,position); //Position relative de l'atome par rapport à la nouvelle molécule
                    if(Math.max(Math.abs(posRel.x) - Hs.get(i).rayonCovalent - tampon,0) < mol.BEAA.x/2.0 && Math.max(Math.abs(posRel.y) - Hs.get(i).rayonCovalent - tampon,0) < mol.BEAA.y/2.0 && Math.max(Math.abs(posRel.z) - Hs.get(i).rayonCovalent - tampon,0) < mol.BEAA.z/2.0){
                        //S'il y a intersection
                        intersecte = true;
                        break; //Sortir de la boucle en n'ajoutant pas la molécule
                    }
                }else{
                    //Intesection avec la sphère
                    if(Vecteur3D.distance(position,Hs.get(i).position) + Hs.get(i).rayonCovalent < mol.rayon){
                        //S'il y a intersection
                        intersecte = true;
                        break; //Sortir de la boucle en n'ajoutant pas la molécule
                    }
                }
            }

            //S'il n'y a pas d'intersection
            if(!intersecte){
                mol.position = position;
                MoléculeRéf.intégrerÀSimulation(Hs, mol); //Ajouter molécule à simulation
                essais = 0;
                totalMolécules++;
            }

            if(System.currentTimeMillis()-timer > 1000){
                timer = System.currentTimeMillis();
                System.out.println("Placement des molécules " + String.format("%.0f",100.0*(double)totalMolécules/(double)NbMolécules) + "%");
            }
        }

        System.out.println("Molécules placées. " + totalMolécules + " molécules sont rentrées dans la zone de simulation.");
        System.out.println("Total Atomes : " + Hs.size());
        System.out.println("Initialisation des systèmes.");

        Atome.MettreÀJourEnvironnement(Hs);
        Molécule.MiseÀJourEnvironnement(Hs);
        Intégrateur.initialisation(Hs,8);
        Intégrateur.FilsExécution = true;

        System.out.println("Initialisation de la température.");
        for (int i = 0; i < Hs.size(); i++) {
            double module = Atome.TempératureEnVitesse(25.0+273.15, Hs.get(i).m);
            //double module=Math.pow(10, 15);
            double Angle1=Math.random()*2.0*Math.PI;
            double Angle2=Math.random()*2.0*Math.PI;
            //Hs.get(i).vélocité = new Vecteur3D(2.0*(Math.random()-0.5)*module,2.0*(Math.random()-0.5)*module,2.0*(Math.random()-0.5)*module);
            
            Hs.get(i).vélocité = new Vecteur3D(module*Math.cos(Angle1)*Math.cos(Angle2),module*Math.sin(Angle1)*Math.cos(Angle2),module*Math.sin(Angle2) );
        }

        System.out.println("Initialisation de l'ordre de dessin.");
        //Ajouter les atomes dans l'ordre de dessin
        for (int i = 0; i < Hs.size(); i++) {
            indexe.add(i);
        }

        System.out.println("Initialisation des positions d'équilibre.");
        timer = System.currentTimeMillis();

        int itérations = 100;
        for (int i = 0; i < itérations; i++) {
            
            Atome.MettreÀJourEnvironnement(Hs);                 //Mettre à jour l'environnement du point de vue des atomes.
            Molécule.MiseÀJourEnvironnement(Hs);                //Mettre à jour l'environnement du point de vue des molécules.

            ForceSytème = new Vecteur3D(0);
            for (int j = 0; j < Hs.size(); j++) {
                Hs.get(j).miseÀJourLiens();    //Créer/Détruire les liens.
            }
            
            Intégrateur.calculerForces(Hs);
            for (int j = 0; j < Hs.size(); j++) {
                Hs.get(j).position.addi(V3.mult(V3.norm(Hs.get(j).Force),0.01));
                for (int k = 0; k < Hs.get(j).forceDoublet.size(); k++) {
                    Hs.get(j).positionDoublet.get(k).addi(V3.mult(V3.norm(Hs.get(j).forceDoublet.get(k)), 0.01));
                }
                Hs.get(j).ÉvaluerContraintes();
            }

            boucleDessin.progressionPlacement = 100.0*(double)i/(double)itérations;
            //try {Thread.sleep(10);} catch (Exception e) {}
        }

        System.out.println("Initialisation complète.");
    }

    public static void simulation(){
        System.out.println("Début de la simulation.");
        boucleDessin.mode = com.MiniLabo.prototype.App.BoucleDessin.MODE.SIM;

        départ = System.currentTimeMillis();
        dt =0.625*Math.pow(10.0,-16);              //Delta temps de la simulation
        while (true) {
            
            Atome.MettreÀJourEnvironnement(Hs);                 //Mettre à jour l'environnement du point de vue des atomes.
            Molécule.MiseÀJourEnvironnement(Hs);                //Mettre à jour l'environnement du point de vue des molécules.

            double T = 0.0; //Température moyenne
            /* double mailmanresonant =0; */
            boucleDessin.MisesÀJours++;
            
            ForceSytème = new Vecteur3D(0);
            for (int i = 0; i < Hs.size(); i++) {
                Hs.get(i).miseÀJourLiens();    //Créer/Détruire les liens.
            }
            
            Intégrateur.IterVerletVB(Hs, dt);
            temps += dt;
            
            //try {Thread.sleep(10);} catch (Exception e) {}
        }
    }

    public static void analyse(int MisesÀJours){
        //g.setColor(Color.WHITE);
        AnalyseTexte[0] = "====== Analyse ======";
        DeltaT = (System.currentTimeMillis()-départ-chrono)/MisesÀJours;
        double DeltaTD = (double)(System.currentTimeMillis()-départ-chrono)/(double)MisesÀJours;
        chrono = System.currentTimeMillis()-départ;
        temps += dt;
        AnalyseTexte[1] = "chrono: " + chrono/1000 + "s";
        AnalyseTexte[2] = "MPS: " + String.format("%.03f",1/((double)DeltaT/1000.0));

        //Statistiques sur la vitesse de la simulation
        AnalyseTexte[3] = "temps : " + String.format("%.03f", temps*Math.pow(10.0,15.0)) + " fs, rapidité : " + String.format("%.03f", (dt*Math.pow(10.0,15.0))/(DeltaTD/1000.0)) + " fs/s";
        //résultatTest += String.format("%.03f", (temps*Math.pow(10.0,15.0))/((double)(System.currentTimeMillis()-chorono)/1000.0)) + ";";
        //longueurTest ++;

        double température = Atome.Température(Hs);
        AnalyseTexte[4] = "Température: " + String.format("%.0f",( température-273.15)) + "°C";

        Vecteur3D max = new Vecteur3D(-Double.MAX_VALUE);
        Vecteur3D min = new Vecteur3D(Double.MAX_VALUE);
        for (int i = 0; i < Hs.size(); i++) {
            max.x = Math.max(Hs.get(i).position.x + Hs.get(i).rayonCovalent, max.x);
            max.y = Math.max(Hs.get(i).position.y + Hs.get(i).rayonCovalent, max.y);
            max.z = Math.max(Hs.get(i).position.z + Hs.get(i).rayonCovalent, max.z);

            min.x = Math.min(Hs.get(i).position.x - Hs.get(i).rayonCovalent, min.x);
            min.y = Math.min(Hs.get(i).position.y - Hs.get(i).rayonCovalent, min.y);
            min.z = Math.min(Hs.get(i).position.z - Hs.get(i).rayonCovalent, min.z);
        }

        double volume = (max.x-min.x)*(max.y-min.y)*(max.z-min.z);
        AnalyseTexte[5] = "Volume: " + String.format("%.3E",volume*Math.pow(10.0,-30.0)) + " m^3";
        double pression = Hs.size()*Atome.R*température/volume;
        AnalyseTexte[6] = "Pression: " + String.format("%.3E",pression) + " kPa";

        double Ek = 0;
        double Ep = 0;
        for (int i = 0; i < Hs.size(); i++) {
            Hs.get(i).potentiel = 0;
        }
        for (int i = 0; i < Hs.size(); i++) {
            //if(Hs.get(i).position.longueur()>0){
                Ek += Math.pow(Hs.get(i).vélocité.longueur(),2.0)*Hs.get(i).m*0.5;
            //}
            Atome.évaluerÉnergiePotentielle(Hs.get(i),false);
        }
        for (int i = 0; i < Hs.size(); i++) {
            Ep += Hs.get(i).potentiel;
        }
        double dist = Vecteur3D.distance(Hs.get(0).position, Hs.get(1).position);
        Ek *= 2.0; //TODO #40 Figurer pourquoi Ek doit être multiplié par 2.

        AnalyseTexte[7] = "Énergie potentielle: " + String.format("%.5E",Ep) + " JÅ " + (AnalyseValeurs[7]-Ep<0.0?"▲":"▼");
        AnalyseValeurs[7] = Ep;
        AnalyseTexte[8] = "Énergie cinétique: " + String.format("%.5E",Ek) + " JÅ " + (AnalyseValeurs[8]-Ek<0.0?"▲":"▼");
        AnalyseValeurs[8] = Ek;
        AnalyseTexte[9] = "Énergie mécanique: " + String.format("%.5E",Ek+Ep) + " JÅ " + (AnalyseValeurs[9]-(Ep+Ek)<0.0?"▲":"▼");
        AnalyseValeurs[9] = Ek+Ep;
        //GraphiqueVal.add(new Vecteur2D(dist,Ep));
        //if(GraphiqueVal.size() > 200){
        //    GraphiqueVal.remove(0);
        //}
        //GraphiqueVal2.add(new Vecteur2D(dist,Ek));
        //if(GraphiqueVal2.size() > 200){
        //    GraphiqueVal2.remove(0);
        //}

        AnalyseTexte[10] = énoncerMolécules(Hs);                         //Lister les pourcentages de présence de chaques molécules dans la simulation

        
        //AnalyseTexte[11] = dist + " Å de distance";
        //double angle = Math.acos(V3.scal(V3.norm(Hs.get(1).position), V3.norm(Hs.get(2).position)));
        //double EkP = -1000.0*((Math.PI)*(Math.PI/2.0)-0.5*(Math.PI/2.0)*(Math.PI/2.0))+1000.0*((Math.PI)*angle-0.5*angle*angle);
        //EkP *= 2.0*1.07;
        //double EpP = 1000.0*((Math.PI)*angle-0.5*angle*angle)
        //AnalyseTexte[11] = "Énergie cinétique prédite: " + String.format("%.3E", EkP) + " JÅ. " + String.format("%.10f", 100.0*Math.abs(EkP-Ek)/Math.abs(EkP)) + "% d'écart.";
        //double EpP = (2.0/(12.0*Math.pow(dist,12.0)));
        //AnalyseTexte[13] = "Énergie potentielle prédite: " + String.format("%.3E", EpP) + " JÅ. " + String.format("%.0f", 100.0*Math.abs(EpP-Ep)/Math.abs(EpP)) + "% d'écart.";
    }
    
    private static class BoucleDessin implements Runnable{
        private static Graphics2D g;
        private static JFrame frame;

        public static enum MODE{INIT,SIM}

        public volatile MODE mode = MODE.INIT;

        public volatile int MisesÀJours = 0;
        public volatile double progressionPlacement = 0.0;

        @Override
        public void run(){

            BufferedImage b = new BufferedImage(TailleX, TailleY,BufferedImage.TYPE_4BYTE_ABGR);    //Initialiser l'image de dessin des atomes
            g = (Graphics2D) b.getGraphics();   //Initialiser le contexte graphique

            JLabel image = new JLabel(new ImageIcon(b)); //Créer un objet Image pour l'écran
            frame = new JFrame();                //Initialiser l'écran
            frame.setSize(TailleX + 100,TailleY + 100); //Taille de la fenêtre
            frame.add(image);                           //Ajouter l'objet Image à l'écran
            frame.setVisible(true);                   //Afficher la fenêtre

            g.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            g.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
            g.setRenderingHint(RenderingHints.KEY_DITHERING, RenderingHints.VALUE_DITHER_ENABLE);
            g.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
            g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            g.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);

            long mailman = System.currentTimeMillis(); //utilisé pour projeter dans terminal
            while (true) {
                g.setColor(new Color(00, 100, 100, 100));   //Couleur de l'arrière-plan
                g.fillRect(0, 0, TailleX, TailleY);             //Rafraîchir l'écran en effaçant tout

                //Affichage de la simulation
                DessinerBoite(g);  //Dessiner le domaine

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
                    DessinerAtome(g,Hs.get(indexe.get(i)), Hs);
                }

                if (System.currentTimeMillis()-mailman > 5000 && mode == MODE.SIM){
                    mailman = System.currentTimeMillis();
                    analyse(MisesÀJours);
                    MisesÀJours = 0;
                }

                g.setColor(new Color(50,50,50,200));
                g.fillRect(0, 0, 220, AnalyseTexte.length*15+10);
                g.setColor(Color.WHITE);
                if(mode == MODE.SIM){
                    for (int i = 0; i < AnalyseTexte.length; i++) {
                        if(AnalyseTexte[i] != null){
                            g.drawString(AnalyseTexte[i], 5, (i+1)*15);
                        }
                    }
                }else if(mode == MODE.INIT){
                    
                    g.setColor(new Color(50,50,50,200));
                    g.fillRect(0, 0, 220, AnalyseTexte.length*15+10);
                    g.setColor(Color.WHITE);
                    g.drawString("Initialisation de la position d'équilibre: ",5,15);
                    g.drawString(String.format("%.0f",progressionPlacement) + "% complété.",5,30);
                }

                //g.setColor(Color.BLUE);
                //for (int i = 0; i < GraphiqueVal.size(); i++) {
                //    g.fillOval((int)(300.0*GraphiqueVal.get(i).x), 2*AnalyseTexte.length*15-(int)(1.0*GraphiqueVal.get(i).y), 4, 4);
                //}
                //g.setColor(Color.RED);
                //for (int i = 0; i < GraphiqueVal2.size(); i++) {
                //    g.fillOval((int)(300.0*GraphiqueVal2.get(i).x), 2*AnalyseTexte.length*15-(int)(1.0*GraphiqueVal2.get(i).y), 4, 4);
                //}

                //Vecteur3D posI = new Vecteur3D(0);
                //Vecteur3D directionF = Vecteur3D.addi(Vecteur3D.mult(Vecteur3D.norm(ForceSytème),0.03*Math.max(Math.log(Zoom*ForceSytème.longueur()+1.0),0.0)),posI);
                //double multPersZF = (FOV*Zoom/((directionF.z+TailleZ/(2.0*Zoom)) + FOVet));
                //g.setColor(Color.RED);       //Couleur de la force
                //g.drawLine((TailleX/2) + (int)((posI.x)*multPersZF), (TailleY/2) - (int)((posI.y)*multPersZF), (TailleX/2) + (int)((+directionF.x)*multPersZF) , (TailleY/2) - (int)((directionF.y)*multPersZF));

                SwingUtilities.updateComponentTreeUI(frame);    //Mise à jour de l'affichage
                try {Thread.sleep(30);} catch (Exception e) {}
            }
        }
    }
    
    /**Dessine une boite représentant le domaine de simulation à  l'écran */
    public static void DessinerBoite(Graphics2D g){
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
    
    /**
     * Dessine l'atome A à l'écran.
     * @param A - Atome à dessiner
     * @param B - Liste des atomes de la simulation. Est utilisé pour dessiner les liens.
     */
    public static void DessinerAtome(Graphics2D g, Atome A, ArrayList<Atome> B){

        double multPersZ=(FOV*Zoom/(A.position.z+TailleZ/(2.0*Zoom) + FOVet)); //Multiplicateur de profondeur (forme la perspective)

        //Dessin des doublets en avant de l'atome
        double ER = 0.1*multPersZ; //Rayon 2D du doublet
        g.setColor(Color.YELLOW);   //Couleur de l'électron
        for (int i = 0; i < A.positionDoublet.size(); i++) {
            if(A.positionDoublet.get(i).z> 0.0){
                Vecteur3D Epos = Vecteur3D.addi(A.position, A.positionDoublet.get(i)); //Position 3D du doublet
                //Dessiner le doublet
                g.fillOval((int)(Epos.x*multPersZ - ER) + (TailleX/2), (TailleY/2) - (int)(Epos.y*multPersZ + ER), (int)(ER)*2,(int)(ER)*2);
            }
        }

        g.setStroke(new BasicStroke());
        double col = 1.0-((A.position.z*2.0*Zoom/TailleZ) + 0.5)*0.5; //Obscurissement avec la profondeur
        col = clamp(col, 0.0, 1.0);

        double charge = A.charge-(2.0*A.doublets);
        //Rouge = charge+, Blanc = neutre, Bleu = charge-
        if(charge > 0.0){
            //Rouge
            g.setColor(new Color((int)(col*255f), (int)mix(0.0,col*255f,1.0-Math.min(charge/2.0,1.0)), (int)mix(0.0, col*255f, 1.0-Math.min(charge/2.0,1.0)), 200));
        }else if(charge == 0.0){
            //Blanc
            g.setColor(new Color((int)(255*col),(int)(255*col),(int)(255*col),200));
        }else if(charge < 0.0){
            //Bleu
            g.setColor(new Color((int)mix(0f,col*255f,1.0-Math.min(-charge/2.0,1.0)), (int)mix(0f, col*255f, 1.0-Math.min(-charge/2.0,1.0)), (int)(col*255f), 200));
        }

        double PR = A.rayonCovalent*multPersZ;  //Rayon 2D de l'atome
        //Dessiner l'atome
        g.fillOval((int)(((A.position.x)*multPersZ - PR) + (TailleX/2)), (int)((TailleY/2) - (int)((A.position.y)*multPersZ + PR)),(int)((PR))*2,(int)(PR)*2);
       
       /*  g.setColor(new Color(0,0,0,200));
        g.fillOval((int)(((A.position.x)*multPersZ - PR) + (TailleX/2)), (int)((TailleY/2) - (int)((A.position.y)*multPersZ + PR/2)),(int)((PR))*3/4,(int)(PR)*3/4);
        g.fillOval((int)(((A.position.x)*multPersZ - 0*PR) + (TailleX/2)), (int)((TailleY/2) - (int)((A.position.y)*multPersZ + PR/2)),(int)((PR))*3/4,(int)(PR)*3/4);
        g.drawLine((int) ((A.position.x)*multPersZ - PR) + (TailleX/2),(int)((TailleY/2) - (int)((A.position.y)*multPersZ + PR)), (int)((A.position.x)*multPersZ - 0*PR) + (TailleX/2),(int)((TailleY/2) - (int)((A.position.y)*multPersZ + PR)));
         *///Dessin des doublets en arrières de l'atome
        g.setColor(Color.YELLOW); //Couleur de l'électron
        for (int i = 0; i < A.positionDoublet.size(); i++) {
            if(A.positionDoublet.get(i) .z <= 0.0){
                Vecteur3D Epos = Vecteur3D.addi(A.position, A.positionDoublet.get(i));//Position 3D du doublet
                //Dessiner le doublet
                g.fillOval((int)(Epos.x*multPersZ - ER) + (TailleX/2), (TailleY/2) - (int)(Epos.y*multPersZ + ER), (int)(ER)*2,(int)(ER)*2);
            }
        }

        //Dessiner les liens
        for (int i = 0; i < A.liaisonIndexe.size(); i++) {
            
            if(A.liaisonIndexe.get(i) != -1 && !A.liaisonType.get(i)){
                // Si c'est une liaison sigma
                double multPersZB = (FOV*Zoom/(B.get(A.liaisonIndexe.get(i)).position.z+TailleZ/(2.0*Zoom) + FOVet)); //Profondeur du dexième atome
                g.setStroke(new BasicStroke());
                g.setColor(Color.BLACK);        //Couleur de la liaison
                //Dessiner la liaison
                g.drawLine(  (TailleX/2) + (int)((A.position.x)*multPersZ), (TailleY/2) - (int)((A.position.y)*multPersZ) , (TailleX/2) + (int)((B.get(A.liaisonIndexe.get(i)).position.x)*multPersZB) , (TailleY/2) - (int)((B.get(A.liaisonIndexe.get(i)).position.y)*multPersZB));
            
            }else if(A.liaisonIndexe.get(i) != -1 && A.liaisonType.get(i)){
                //Si c'est une liaison pi
                double multPersZB = (FOV*Zoom/(B.get(A.liaisonIndexe.get(i)).position.z+TailleZ/(2.0*Zoom) + FOVet));   // Profondeur du deuxième atome
                g.setStroke(new BasicStroke());
                g.setColor(Color.BLUE);         //Couleur de la liaison
                //Dessiner la liaison
                g.drawLine(  (TailleX/2) + (int)((A.position.x + 0.3f)*multPersZ), (TailleY/2) - (int)((A.position.y)*multPersZ) , (TailleX/2) + (int)((B.get(A.liaisonIndexe.get(i)).position.x+0.3f)*multPersZB) , (TailleY/2) - (int)((B.get(A.liaisonIndexe.get(i)).position.y)*multPersZB));
            }
        }
         ////Dessiner force resultante
         //Vecteur3D directionF = Vecteur3D.addi(Vecteur3D.mult(Vecteur3D.norm(A.Force),0.05*Math.max(Math.log(Zoom*A.Force.longueur()+1.0),0.0)),A.position);
         //double multPersZF = (FOV*Zoom/((directionF.z+TailleZ/(2.0*Zoom)) + FOVet));
         //g.setColor(Color.RED);       //Couleur de la force
         //g.drawLine((TailleX/2) + (int)((A.position.x)*multPersZ), (TailleY/2) - (int)((A.position.y)*multPersZ), (TailleX/2) + (int)((+directionF.x)*multPersZF) , (TailleY/2) - (int)((directionF.y)*multPersZF));
         //for (int i = 0; i < A.forceDoublet.size(); i++) {
         //   Vecteur3D posI = Vecteur3D.addi(A.position, A.positionDoublet.get(i));
         //   directionF = Vecteur3D.addi(Vecteur3D.mult(Vecteur3D.norm(A.forceDoublet.get(i)),0.03*Math.max(Math.log(Zoom*A.forceDoublet.get(i).longueur()+1.0),0.0)),posI);
         //   multPersZF = (FOV*Zoom/((directionF.z+TailleZ/(2.0*Zoom)) + FOVet));
         //   g.setColor(Color.RED);       //Couleur de la force
         //   g.drawLine((TailleX/2) + (int)((posI.x)*multPersZ), (TailleY/2) - (int)((posI.y)*multPersZ), (TailleX/2) + (int)((+directionF.x)*multPersZF) , (TailleY/2) - (int)((directionF.y)*multPersZF));
         //}
         ////Vecteur vitesse
         //Vecteur3D directionV = Vecteur3D.addi(Vecteur3D.mult(Vecteur3D.norm(A.vélocité),0.03*Math.log(Zoom*A.vélocité.longueur()+1)),A.position);
         //double multPersZV = (FOV*Zoom/((directionV.z+TailleZ/(2.0*Zoom)) + FOVet));
         //g.setColor(Color.WHITE);       //Couleur de la force
         ////g.drawLine((TailleX/2) + (int)((A.position.x)*multPersZ), (TailleY/2) - (int)((A.position.y)*multPersZ), (TailleX/2) + (int)((+directionF.x)*multPersZV) , (TailleY/2) - (int)((directionF.y)*multPersZV));
   }

    /**
     * Fonction d'interpolation linéaire ente a et b.
     * @param a - Valeur a
     * @param b - Valeur b
     * @param m - Facteur d'interpolation
     * @return Interpolation linéaire entre a et b
     */
    private static double mix(double a, double b, double m){
        return (1.0-m)*a + m*b;
    }

    /**
     * Fixe a entre les valeurs de b et c.
     * @param a - Valeur à fixer
     * @param b - Minimum de a
     * @param c - Maximum de b
     * @return Max( Min( a, c ), b )
     */
    private static double clamp(double a, double b, double c){
        if(a < b){
            return b;
        }else if(a > c){
            return c;
        }else{
            return a;
        }
    }

    /**Liste le pourcentage de présence de chaque molécule dans la simulation
     * @param Atomes - Liste des atomes de la simulation
    */
    public static String énoncerMolécules(ArrayList<Atome> Atomes){

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
        return outB;
    }

    /**Vas chercher tout les atomes reliés à cet atome et renvoie ainsi les constituants de la molécule.
     * @param Atomes - Liste de tout les atomes de la simulation
     * @param indexe - Indexe de l'atome à regarder
     * @param vus - Liste de tout les atomes déjà traités
    */
    public static int[] ajouterAtomeÀMolécule(ArrayList<Atome> Atomes, int indexe, ArrayList<Integer> vus){

        int[] retour = new int[19]; // Initialise la liste des atomes de retours.
        // retour est une liste de la quantité de chaque type d'atomes liés chaque case est liée au numéro
        //   atomique et son contenu indique la quantité. Ex.: r[2] = 5 indique qu'il y a 5 Héliums
        //   retour[0] indique la charge de la molécule.

        if(!vus.contains(indexe)){
            // Si l'atome n'a pas déjà été traité (A)
            vus.add(indexe);    //Indiquer qu'il aura été traité
            for (int i = 0; i < Atomes.get(indexe).liaisonIndexe.size(); i++) {
                // Chercher dans tout les atomes liés (A')
                if(!vus.contains(Atomes.get(indexe).liaisonIndexe.get(i)) && Atomes.get(indexe).liaisonIndexe.get(i) != -1){
                    //Si cet atome lié n'a pas déjà été traité
                    //Aller chercher tout les atomes liés à A' (A'')
                    int[] r = ajouterAtomeÀMolécule(Atomes, Atomes.get(indexe).liaisonIndexe.get(i), vus);
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
