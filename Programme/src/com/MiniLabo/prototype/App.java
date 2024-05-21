package com.MiniLabo.prototype;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Frame;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.util.ArrayList;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

import java.awt.RenderingHints;

public class App {
    public static Paramètres p; // = Paramètres.avoirParamètres();

    public static float FOVet; // = p.FOV;
    private static float FOVBoite; // = p.FOV;
    private static float FOVetBoite; // = p.FOV;

    public static ArrayList<Atome> Hs = new ArrayList<>();       //Liste des atomes
    //public static volatile ArrayList<Integer> indexe = new ArrayList<>(); //Ordre de dessin des atomes.

    /**Temps réel de départ de la simulation en ms */
    public static long départ = System.currentTimeMillis();
    /**Temps réel écoulé depuis le début de la simulation en ms*/
    public static long chrono = 0; 
    /**Temps de simlation écoulé depuis le début de la simulation en fs */
    public static double temps = 0;
    /**Delta temps en temps réel entre chaque mise à jour de la simulation en ms */
    public static long DeltaT = 0;

    private static String[] AnalyseTexte = new String[12];
    private static double[] AnalyseValeurs = new double[AnalyseTexte.length];
    private static File fichierAnalyse; // = new File(p.emplacementFichierAnalyse + "Analyse.csv");
    private static FileWriter fileWriter;

    private static BoucleDessin boucleDessin;
    private static Thread thread;

    private static ArrayList<ArrayList<String>> fichierAnalyseContenu = new ArrayList<>();
    private static int simI = 0;

    public static void main(String[] args) throws Exception {
        System.out.println("Bienvenue dans MiniLabo!");

        int essais = 0;
        for (int i = 0; i < 23; i++) {
            simI = i;
            fichierAnalyseContenu.add(new ArrayList<String>());
            boolean commencer = true;
            while (commencer || p.répéter) {
                //int i = 1;
                p = Paramètres.chargerDepuisFichier();

                File dossier = new File(p.dossierAnalyse);
                if(!dossier.exists()){
                    dossier.mkdirs();
                }
                fichierAnalyse = new File(p.dossierAnalyse + "Sim_" + i + ".csv");

                p.mode = Paramètres.Mode.ENTRE_DEUX;
                FOVet = p.FOV;
                FOVBoite = p.FOV;
                FOVetBoite = p.FOV;
                Hs.clear();
                if(boucleDessin != null){
                    boucleDessin.indexe.clear();
                }
                chrono = 0;
                if(p.répéter){
                    essais ++;
                }else{
                    essais = 0;
                }
                commencer = false;
                p.répéter = false;
                Initialisation();
                simulation();
                //Analyse se fait à partir de simulation();
                if(essais > 5 && p.répéter){
                    p.répéter = false;
                    essais = 0;
                }
            }
        }
    }

    public static void Initialisation(){
        System.out.println("Initialisation");
        p.mode = Paramètres.Mode.INIT;

        temps = 0;
        chrono = 0;

        if(boucleDessin == null){
            boucleDessin = new BoucleDessin();
            thread = new Thread(boucleDessin);
            p.mode = Paramètres.Mode.INIT;
            thread.start();
        }
        boucleDessin.init = true;
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
        
        p.mode = Paramètres.Mode.AJOUT_MOL;
        //Initialiser les atomes selon l'algorithme de poisson
        int NbMolécules = p.NbMolécules;  //Nombre de molécules voulus
        int totalMolécules = 0;//Nombre de molécules ajoutés
        int essais = 0;
        int NBessais = p.NBessais;           //Nombre d'essais à placer la molécule
        boolean BEAA = p.BEAA;   //Mode de calcul d'intersection. Faux = sphère, Vrai = BEAA
        double tampon = p.tampon;  //Zone tampon entre les atomes

        System.out.println("Placement des "+NbMolécules+" molécules.");
        long timer = System.currentTimeMillis();
        //Placer une molécule dans la simulation tant qu'on n'aura pas atteint le total voulus.
        //Si on essais de placer la molécule trops de fois, la simulation est déjà pleine et il faut arrêter.
        while (totalMolécules < NbMolécules && essais < NBessais) {
            essais++;
            MoléculeRéf mol = p.PlacementMolécule(totalMolécules);

            //position aléatoire dans le domaine.
            Vecteur3D position = new Vecteur3D(2.0*(Math.random()-0.5) * (p.TailleX/(2.0*p.Zoom) - mol.BEAA.x),2.0*(Math.random()-0.5) * (p.TailleY/(2.0*p.Zoom) - mol.BEAA.y),2.0*(Math.random()-0.5) * (p.TailleZ/(2.0*p.Zoom) - mol.BEAA.z));
            //position = new Vecteur3D(0);
            boolean intersecte = false;
            for (int i = 0; i < Hs.size(); i++) {
                //Réessayer si cet emplacement intersecte un atome dans la simulation
                if(p.BEAA){
                    //Intersection avec la BEAA
                    Vecteur3D posRel = Vecteur3D.sous(Hs.get(i).position,position); //Position relative de l'atome par rapport à la nouvelle molécule
                    if(Math.max(Math.abs(posRel.x) - Hs.get(i).rayonCovalent - tampon,0) < mol.BEAA.x/2.0 && Math.max(Math.abs(posRel.y) - Hs.get(i).rayonCovalent - tampon,0) < mol.BEAA.y/2.0 && Math.max(Math.abs(posRel.z) - Hs.get(i).rayonCovalent - tampon,0) < mol.BEAA.z/2.0){
                        //S'il y a intersection
                        intersecte = true;
                        break; //Sortir de la boucle en n'ajoutant pas la molécule
                    }
                }else{
                    //Intesection avec la sphère
                    if(Vecteur3D.distance(position,Hs.get(i).position) + Hs.get(i).rayonCovalent + tampon < mol.rayon){
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

        boucleDessin.Hs = (ArrayList<Atome>) Hs.clone();

        p.mode = Paramètres.Mode.INIT;

        Atome.MettreÀJourEnvironnement(Hs);
        Molécule.MiseÀJourEnvironnement(Hs);
        Intégrateur.initialisation(Hs,p.NBFils);
        Intégrateur.FilsExécution = p.UtiliserFilsExécution;
        Intégrateur.modèle = p.modèleIntégrateur;

        System.out.println("Initialisation de la température.");
        for (int i = 0; i < Hs.size(); i++) {
            double module = Atome.TempératureEnVitesse(p.TempératureInitiale+273.15, Hs.get(i).m);
            double Angle1=Math.random()*2.0*Math.PI- 1.0*Math.PI;
            double Angle2=Math.random()*1.0*Math.PI - 0.5*Math.PI;
            
            Hs.get(i).vélocité = new Vecteur3D(module*Math.cos(Angle1)*Math.sin(Angle2),module*Math.sin(Angle1)*Math.sin(Angle2),module*Math.cos(Angle2) );
        }

        System.out.println("Initialisation des positions d'équilibre.");
        timer = System.currentTimeMillis();

        for (int i = 0; i < p.itérationsPlacementInitial; i++) {
                        
            Intégrateur.calculerForces(Hs);
            for (int j = 0; j < Hs.size(); j++) {
                Hs.get(j).position.addi(V3.mult(V3.norm(Hs.get(j).Force),p.deltaPlacement));
                for (int k = 0; k < Hs.get(j).forceDoublet.size(); k++) {
                    Hs.get(j).positionDoublet.get(k).addi(V3.mult(V3.norm(Hs.get(j).forceDoublet.get(k)), p.deltaPlacement));
                }
                Hs.get(j).ÉvaluerContraintes();
            }

            boucleDessin.progressionPlacement = 100.0*(double)i/(double)p.itérationsPlacementInitial;
            try {Thread.sleep(1);} catch (Exception e) {}
        }

        try{
            fileWriter = new FileWriter(fichierAnalyse, Charset.forName("UTF-8"));
            fileWriter.write("Molécules: ;" + totalMolécules+ "; Atomes: ;" + Hs.size()+"; Température Initiale (°C): ;" + p.TempératureInitiale + "; Solution : ;" + énoncerMolécules(Hs) + "; Intégrateur : ;" + p.modèleIntégrateur.name() + ";\n");
            fileWriter.write("chrono (s); MPS; temps (fs); Température (°C); Volume (m^3); Pression (kPa); Énergie Potentielle (JÅ); Énergie Cinétique (JÅ); Énergie Mécanique (JÅ);\n");
        }catch(Exception e){
            e.printStackTrace();
        }

        System.out.println("Initialisation complète.");
    }

    public static void simulation(){
        System.out.println("Début de la simulation.");
        p.mode = Paramètres.Mode.SIM;

        départ = System.currentTimeMillis();
        chrono = System.currentTimeMillis()-départ;
        //try{
            while (chrono < p.simDurée && !p.répéter && p.mode != Paramètres.Mode.FIN_SIM && p.mode != Paramètres.Mode.FIN_PROGRAME) {

                if(!thread.isAlive()){
                    //boucleDessin = new BoucleDessin();
                    thread = new Thread(boucleDessin);
                    p.mode = Paramètres.Mode.INIT;
                    boucleDessin.Hs = (ArrayList<Atome>)Hs.clone();
                    boucleDessin.init = true;
                    boucleDessin.indexe.clear();
                    thread.start();
                }
                
                double T = 0.0; //Température moyenne
                /* double mailmanresonant =0; */
                boucleDessin.MisesÀJours++;
                
                for (int i = 0; i < Hs.size(); i++) {
                    Hs.get(i).miseÀJourLiens();    //Créer/Détruire les liens.
                }
                
                Intégrateur.Iter(Hs, p.dt);
                temps += p.dt;
                
                chrono = System.currentTimeMillis()-départ;
                //try {Thread.sleep(10);} catch (Exception e) {}
            }
        //}catch(Exception e){
        //    e.printStackTrace();
        //}
        p.mode = Paramètres.Mode.FIN_SIM;
        Intégrateur.tuerFils();
        //try {Thread.sleep(1000);} catch (Exception e) {}
    }

    public static void analyse(int MisesÀJours, long analyseChrono){
        AnalyseTexte[0] = "====== Analyse ====== " + Intégrateur.modèle.name();
        double DeltaTD=0;
        if (MisesÀJours==0){
            DeltaT=Long.MAX_VALUE;
            DeltaTD=Double.POSITIVE_INFINITY;
        } else {
            DeltaT = (System.currentTimeMillis()-départ-analyseChrono)/MisesÀJours;
            DeltaTD = (double)(System.currentTimeMillis()-départ-analyseChrono)/(double)MisesÀJours;
        }

        AnalyseTexte[1] = "chrono: " + analyseChrono/1000 + "s";
        AnalyseTexte[2] = "MPS: " + String.format("%.03f",1.0/(DeltaTD/1000.0));

        AnalyseTexte[3] = "temps : " + String.format("%.03f", temps*Math.pow(10.0,15.0)) + " fs, rapidité : " + String.format("%.03f", (p.dt*Math.pow(10.0,15.0))/(DeltaTD/1000.0)) + " fs/s";

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
        //double pression = Hs.size()*Atome.R*température/volume;
        

        double Ek = 0;
        double Ep = 0;
        for (int i = 0; i < Hs.size(); i++) {
            Hs.get(i).potentiel = 0;
        }
        for (int i = 0; i < Hs.size(); i++) {
            Ek += Math.pow(Hs.get(i).vélocité.longueur(),2.0)*Hs.get(i).m*0.5;
            Atome.évaluerÉnergiePotentielle(Hs.get(i),p.PotentielMorseDécalé);
        }
        for (int i = 0; i < Hs.size(); i++) {
            Ep += Hs.get(i).potentiel;
        }
        double dist = Vecteur3D.distance(Hs.get(0).position, Hs.get(1).position);
        Ek *= 2.0; //TODO #40 Figurer pourquoi Ek doit être multiplié par 2.

        double pression = Ek/(volume);

        AnalyseTexte[6] = "Pression: " + String.format("%.3E",pression) + " kPa";
        AnalyseTexte[7] = "Énergie potentielle: " + String.format("%.5E",Ep) + " JÅ " + (AnalyseValeurs[7]-Ep<0.0?"▲":"▼");
        AnalyseValeurs[7] = Ep;
        AnalyseTexte[8] = "Énergie cinétique: " + String.format("%.5E",Ek) + " JÅ " + (AnalyseValeurs[8]-Ek<0.0?"▲":"▼");
        AnalyseValeurs[8] = Ek;
        AnalyseTexte[9] = "Énergie mécanique: " + String.format("%.5E",Ek+Ep) + " JÅ " + (AnalyseValeurs[9]-(Ep+Ek)<0.0?"▲":"▼");
        AnalyseValeurs[9] = Ek+Ep;

        AnalyseTexte[10] = énoncerMolécules(Hs);                         //Lister les pourcentages de présence de chaques molécules dans la simulation

        try {
            fileWriter.write(analyseChrono + ";" + String.format("%.03f",1/(DeltaTD/1000.0)) + ";" + String.format("%.03f", temps*Math.pow(10.0,15.0)) + ";" + température + ";" + volume + ";" + pression + ";" + Ep + ";" + Ek + ";" + (Ep+Ek) + ";\n");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private static class BoucleDessin implements Runnable{
        private static Graphics2D g;
        private static JFrame frame;

        public volatile int MisesÀJours = 0;
        public volatile double progressionPlacement = 0.0;

        public volatile boolean init = false;

        public volatile ArrayList<Atome> Hs = new ArrayList<>();
        public volatile ArrayList<Integer> indexe = new ArrayList<>();

        private boolean frameActivé = false;

        private long analyseChrono;

        @Override
        public void run(){
            System.out.println("Thread de dessin : " + Thread.currentThread().getName());
            BufferedImage b = new BufferedImage(p.TailleX, p.TailleY,BufferedImage.TYPE_4BYTE_ABGR);    //Initialiser l'image de dessin des atomes
            g = (Graphics2D) b.getGraphics();   //Initialiser le contexte graphique
            JLabel image = new JLabel(new ImageIcon(b)); //Créer un objet Image pour l'écran
            frame = new JFrame();                //Initialiser l'écran
            frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
            frame.setSize(p.TailleX + 100,p.TailleY + 100); //Taille de la fenêtre
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
            init = false;

            long mailman = System.currentTimeMillis(); //utilisé pour projeter dans terminal
            while (true) {
                if(p.mode == Paramètres.Mode.ENTRE_DEUX || p.mode == Paramètres.Mode.AJOUT_MOL || p.mode == Paramètres.Mode.FIN_SIM) {
                    continue;
                }
                if(init){
                    b = new BufferedImage(p.TailleX, p.TailleY,BufferedImage.TYPE_4BYTE_ABGR);    //Initialiser l'image de dessin des atomes
                    g = (Graphics2D) b.getGraphics();   //Initialiser le contexte graphique
                    
                    if(image != null){
                        frame.remove(image);
                    }
                    image = new JLabel(new ImageIcon(b)); //Créer un objet Image pour l'écran
                    //frame = new JFrame();                //Initialiser l'écran
                    frame.setSize(p.TailleX + 100,p.TailleY + 100); //Taille de la fenêtre
                    frame.add(image);                           //Ajouter l'objet Image à l'écran
                    frame.setVisible(true);                   //Afficher la fenêtre
                    frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

                    g.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
                    g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                    g.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
                    g.setRenderingHint(RenderingHints.KEY_DITHERING, RenderingHints.VALUE_DITHER_ENABLE);
                    g.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
                    g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
                    g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                    g.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
                    
                    init = false;
                }

                g.setColor(new Color(00, 100, 100, 100));   //Couleur de l'arrière-plan
                g.fillRect(0, 0, p.TailleX, p.TailleY);             //Rafraîchir l'écran en effaçant tout

                //Affichage de la simulation
                DessinerBoite(g);  //Dessiner le domaine

                //Ajouter les atomes dans l'ordre de dessin
                while(Hs.size() > indexe.size()) {
                    indexe.add(indexe.size());
                }

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

                if (System.currentTimeMillis()-mailman > p.analyseÉchantillonsIntervalles && p.mode == Paramètres.Mode.SIM){
                    mailman = System.currentTimeMillis();
                    analyse(MisesÀJours,analyseChrono);
                    analyseChrono = System.currentTimeMillis()-départ;
                    MisesÀJours = 0;
                }

                g.setColor(new Color(50,50,50,200));
                g.fillRect(0, 0, 220, AnalyseTexte.length*15+10);
                g.setColor(Color.WHITE);
                if(p.mode == Paramètres.Mode.SIM){
                    for (int i = 0; i < AnalyseTexte.length; i++) {
                        if(AnalyseTexte[i] != null){
                            g.drawString(AnalyseTexte[i], 5, (i+1)*15);
                        }
                    }
                }else if(p.mode == Paramètres.Mode.INIT){
                    
                    g.setColor(new Color(50,50,50,200));
                    g.fillRect(0, 0, 220, AnalyseTexte.length*15+10);
                    g.setColor(Color.WHITE);
                    g.drawString("Initialisation de la position d'équilibre: ",5,15);
                    g.drawString(String.format("%.0f",progressionPlacement) + "% complété.",5,30);
                }

                SwingUtilities.updateComponentTreeUI(frame);    //Mise à jour de l'affichage
                try {Thread.sleep(30);} catch (Exception e) {}
            }
        }
    }
    
    /**Dessine une boite représentant le domaine de simulation à  l'écran */
    public static void DessinerBoite(Graphics2D g){
        double multPersZBoiteLoin=(FOVBoite/(p.TailleZ/(2*p.Zoom)+p.TailleZ/(2.0*p.Zoom) + FOVetBoite));    //Multiplicateur de profondeur de la face arrière (Forme la perspective)
        double multPersZBoiteProche=(FOVBoite/(-p.TailleZ/(2*p.Zoom)+p.TailleZ/(2.0*p.Zoom) + FOVetBoite)); //Multiplicateur de profondeur de la face avant
        g.setStroke(new BasicStroke());
        g.setColor(Color.MAGENTA);  //Couleur de la boîte
        int TailleX = p.TailleX;
        int TailleY = p.TailleY;
        int TailleZ = p.TailleZ;
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

        float FOV = p.FOV;
        float Zoom = p.Zoom;
        int TailleX = p.TailleX;
        int TailleY = p.TailleY;
        int TailleZ = p.TailleZ;

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
