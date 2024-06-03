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
    /**Liste de paramètre */
    public static Paramètres p;
    /**Liste d'atomes */
    public static ArrayList<Atome> Atomes = new ArrayList<>();       //Liste des atomes

    /**Temps réel de départ de la simulation en ms */
    public static long départ = System.currentTimeMillis();
    /**Temps réel écoulé depuis le début de la simulation en ms*/
    public static long chrono = 0; 
    /**Temps de simlation écoulé depuis le début de la simulation en fs */
    public static double temps = 0;
    /**Delta temps en temps réel entre chaque mise à jour de la simulation en ms */
    public static long DeltaT = 0;

    /**Texte de l'analyse qui sera affiché à l'écran. Chaque élément de la liste est une ligne de texte */
    private static String[] AnalyseTexte = new String[12];
    /**Valeures des éléments d'analyses. Utilisé pour certains calculs dans le temps. */
    private static double[] AnalyseValeurs = new double[AnalyseTexte.length];
    /**Fichier dans lequel sera écrite l'analyse */
    private static File fichierAnalyse;
    private static FileWriter fileWriter;

    /**Fil d'exécution pour dessiner l'écran en parallèle de la simulation */
    private static BoucleDessin boucleDessin;
    private static Thread thread;

    public static void main(String[] args) throws Exception {
        System.out.println("Bienvenue dans MiniLabo!");

        //boucle pour reccomencer la simulation si elle plante
        int essais = 0; //Nombre de fois que la simulation a reccomancé
        boolean commencer = true;
        //Reccomencer tant que p.répéter est vrai
        while (commencer || p.répéter) {
            p = Paramètres.chargerDepuisFichier(); //Aller chercher les paramètres dans le fichier param.txt

            //Créer le fichier d'analyse, s'il n'existe pas déjà
            File dossier = new File(p.dossierAnalyse);
            if(!dossier.exists()){
                dossier.mkdirs();
            }
            fichierAnalyse = new File(p.dossierAnalyse + "Sim.csv");

            //Si la simulation a planté et qu'on a dû reccomencer, ajouter un essais,
            //sinon, passer à la prochaine et réinitialiser le compteur
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

            //Si on a fait plus de 5 essais, annuler la simulation et passer à la prochaine.
            if(essais > 5 && p.répéter){
                p.répéter = false;
                essais = 0;
            }
        }
    }

    public static void Initialisation(){
        System.out.println("Initialisation");
        p.mode = Paramètres.Mode.INI; //Mettre la simulation en mode initialisation

        temps = 0; //Réinitialiser le temps de simulation
        chrono = 0;//Réinitialiser le temps réel

        //Si la boucle de dessin n'est pas initialisée, la partire
        if(boucleDessin == null){
            boucleDessin = new BoucleDessin();
            thread = new Thread(boucleDessin);
            p.mode = Paramètres.Mode.INI;
            thread.start();
        }
        boucleDessin.ini = true;

        p.mode = Paramètres.Mode.AJOUT_MOL; //Mettre la simulation en mode ajout de molécules
        //Retirer tout les atomes de la liste de simulation, pour commencer la prochaine.
        Atomes.clear();
        if(boucleDessin != null){
            boucleDessin.indexe.clear();
        }

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
            for (int i = 0; i < Atomes.size(); i++) {
                //Réessayer si cet emplacement intersecte un atome dans la simulation
                if(p.BEAA){
                    //Intersection avec la BEAA
                    Vecteur3D posRel = Vecteur3D.sous(Atomes.get(i).position,position); //Position relative de l'atome par rapport à la nouvelle molécule
                    if(Math.max(Math.abs(posRel.x) - Atomes.get(i).rayonCovalent - tampon,0) < mol.BEAA.x/2.0 && Math.max(Math.abs(posRel.y) - Atomes.get(i).rayonCovalent - tampon,0) < mol.BEAA.y/2.0 && Math.max(Math.abs(posRel.z) - Atomes.get(i).rayonCovalent - tampon,0) < mol.BEAA.z/2.0){
                        //S'il y a intersection
                        intersecte = true;
                        break; //Sortir de la boucle en n'ajoutant pas la molécule
                    }
                }else{
                    //Intesection avec la sphère
                    if(Vecteur3D.distance(position,Atomes.get(i).position) + Atomes.get(i).rayonCovalent + tampon < mol.rayon){
                        //S'il y a intersection
                        intersecte = true;
                        break; //Sortir de la boucle en n'ajoutant pas la molécule
                    }
                }
            }

            //S'il n'y a pas d'intersection
            if(!intersecte){
                mol.position = position;
                MoléculeRéf.intégrerÀSimulation(Atomes, mol); //Ajouter molécule à simulation
                essais = 0;
                totalMolécules++;
            }

            //Mettre à jour la barre de progression de l'initialisation
            if(System.currentTimeMillis()-timer > 1000){
                timer = System.currentTimeMillis();
                System.out.println("Placement des molécules " + String.format("%.0f",100.0*(double)totalMolécules/(double)NbMolécules) + "%");
            }
        }

        System.out.println("Molécules placées. " + totalMolécules + " molécules sont rentrées dans la zone de simulation.");
        System.out.println("Total Atomes : " + Atomes.size());
        System.out.println("Initialisation des systèmes.");

        boucleDessin.Hs = (ArrayList<Atome>) Atomes.clone(); //Donner les atomes à la boucle de dessin

        p.mode = Paramètres.Mode.INI; //Passer la simulation en mode initialisation

        Atome.MettreÀJourEnvironnement(Atomes); //Donner une référence de tout les atomes à la classe atome
        Molécule.MiseÀJourEnvironnement(Atomes); //Donner une référence de tout les atomes à la classe Molécule
        Intégrateur.initialisation(Atomes,p.NBFils); //Initialiser l'intégrateur
        Intégrateur.FilsExécution = p.UtiliserFilsExécution; //Indiquer si on utilise les fils d'exécutions
        Intégrateur.modèle = p.modèleIntégrateur; //Indiquer le modèle d'intégrateur utilisé

        //Initialiser la température initiale
        System.out.println("Initialisation de la température.");
        for (int i = 0; i < Atomes.size(); i++) {
            //Pour tout les atomes
            double module = Atome.TempératureEnVitesse(p.TempératureInitiale+273.15, Atomes.get(i).m); //Aller chercher le module de la vitesse
            double Angle1=Math.random()*2.0*Math.PI- 1.0*Math.PI; //Prendre un angle horizontal aléatoire
            double Angle2=Math.random()*1.0*Math.PI - 0.5*Math.PI;//Prendre un angle azimutal aléatoire
            //Donner une vélocité selon le module de la vitesse dans une direction aléatoire
            Atomes.get(i).vélocité = new Vecteur3D(module*Math.cos(Angle1)*Math.sin(Angle2),module*Math.sin(Angle1)*Math.sin(Angle2),module*Math.cos(Angle2) );
        }

        //Les atomes sont positionnés de façons aléatoires dans l'espace, ce qui veut dire que beaucoups
        //d'entre eux sont dans des situations très instables, ce qui leur donne énormément d'énergie
        //potentielle, qui se convertis rapidement en énergie cintétique et donc en température. Pour
        //conserver la bonne température initiale, nous les positionnons dans un minimum local d'énergie
        //potentielle (qui ne peut donc pas se transformer en énergie cinétique : ils sont au repos) et
        //nous leur donnons une vitesse initiale. Afin de trouver ce minimum local, nous utilisond le fait
        //que F = -∇U, et utilisons la méthode de la déscente du gradient.
        System.out.println("Initialisation des positions d'équilibre.");
        timer = System.currentTimeMillis();

        for (int i = 0; i < p.itérationsPlacementInitial; i++) {
                        
            Intégrateur.calculerForces(Atomes); //Aller chercher la force
            //Déplacer tout les atomes et les doublets d'un petit bond en direction de F
            for (int j = 0; j < Atomes.size(); j++) {
                Atomes.get(j).position.addi(V3.mult(V3.norm(Atomes.get(j).Force),p.deltaPlacement));
                for (int k = 0; k < Atomes.get(j).forceDoublet.size(); k++) {
                    Atomes.get(j).positionDoublet.get(k).addi(V3.mult(V3.norm(Atomes.get(j).forceDoublet.get(k)), p.deltaPlacement));
                }
                Atomes.get(j).ÉvaluerContraintes(); //S'assurer d'évaluer les contraintes
            }
            //Mettre à jour la barre de progression
            boucleDessin.progressionPlacement = 100.0*(double)i/(double)p.itérationsPlacementInitial;
        }

        //Initialiser le fichier d'analyse
        try{
            fileWriter = new FileWriter(fichierAnalyse, Charset.forName("UTF-8")); //Créeravec un encodage UTF-8 (nécessaire pour dessiner les accents)
            //Un .csv est comme un excel. Chaque ligne correspond à la ligne et les colones sont séparées par des « ; ». Par la suite, Excel peut importer un .csv et faire un tableau plus complexe.
            //Ajouter de l'information d'en-tête.
            fileWriter.write("Molécules: ;" + totalMolécules+ "; Atomes: ;" + Atomes.size()+"; Température Initiale (°C): ;" + p.TempératureInitiale + "; Solution : ;" + énoncerMolécules(Atomes) + "; Intégrateur : ;" + p.modèleIntégrateur.name() + ";\n");
            //Ajouter les noms des colonnes de données.
            fileWriter.write("chrono (s); MPS; temps (fs); Température (°C); Volume (m^3); Pression (kPa); Énergie Potentielle (JÅ); Énergie Cinétique (JÅ); Énergie Mécanique (JÅ);\n");
        }catch(Exception e){
            e.printStackTrace();
        }

        System.out.println("Initialisation complète.");
    }

    public static void simulation(){
        System.out.println("Début de la simulation.");
        p.mode = Paramètres.Mode.SIM; //Mettre le programme en mode simulation

        départ = System.currentTimeMillis(); //stocker le temps de départ de la simulation
        //try{
            //Boucle de simulation
            //Continuer tant qu'on n'a pas dépassé le temps alloué à la simulation et que le programme n'est pas en mode FIN_SIM ou FIN_PROGRAME
            while (chrono < p.simDurée && !p.répéter && p.mode != Paramètres.Mode.FIN_SIM && p.mode != Paramètres.Mode.FIN_PROGRAME) {

                //Si la fil de dessin a planté ou qu'il n'est pas encore créé, l'initialiser
                if(!thread.isAlive()){
                    thread = new Thread(boucleDessin);
                    boucleDessin.Hs = (ArrayList<Atome>)Atomes.clone();
                    boucleDessin.ini = true;
                    boucleDessin.indexe.clear();
                    thread.start();
                }
                
                boucleDessin.MisesÀJours++; //Mettre à jour le nombre de mises à jours faites par échantillonages
                
                for (int i = 0; i < Atomes.size(); i++) {
                    Atomes.get(i).miseÀJourLiens();    // Créer/Détruire les liens.
                }
                
                Intégrateur.Iter(Atomes, p.dt); //Déplacer les atomes d'un pas de simulation
                temps += p.dt; //Mettre à jour le temps de simulation
                
                chrono = System.currentTimeMillis()-départ; //Mettre à jour le chrono
                //try {Thread.sleep(10);} catch (Exception e) {}
            }
        //}catch(Exception e){
        //    e.printStackTrace(); //Les try_catch ralentissent beaucoup la simulation
        //}
        p.mode = Paramètres.Mode.FIN_SIM; //Si on sort de la boucle, mettre le mode du programme à FIN_SIM
        Intégrateur.tuerFils(); //Arrêter les fils d'exécutions
        //try {Thread.sleep(1000);} catch (Exception e) {}
    }

    public static void analyse(int MisesÀJours, long analyseChrono){
        AnalyseTexte[0] = "====== Analyse | " + Intégrateur.modèle.name() + " ======="; //En-tête de l'analyse + nom de l'intégrateur
        double DeltaTD; //Temps en seconde entre chaque mise à jour.
        if (MisesÀJours==0){
            DeltaT=Long.MAX_VALUE;
            DeltaTD=Double.POSITIVE_INFINITY;
        } else {
            DeltaT = (System.currentTimeMillis()-départ-analyseChrono)/MisesÀJours;
            DeltaTD = (double)(System.currentTimeMillis()-départ-analyseChrono)/(double)MisesÀJours;
        }

        AnalyseTexte[1] = "chrono: " + analyseChrono/1000 + "s"; //temps réel de la simulation
        AnalyseTexte[2] = "MPS: " + String.format("%.03f",1.0/(DeltaTD/1000.0)); //Mises à jours de la simulation par secondes
        //Temps de la simulation en fs + rapidité de la simulation en fs/s
        AnalyseTexte[3] = "temps : " + String.format("%.03f", temps*Math.pow(10.0,15.0)) + " fs, rapidité : " + String.format("%.03f", (p.dt*Math.pow(10.0,15.0))/(DeltaTD/1000.0)) + " fs/s";

        double température = Atome.Température(Atomes); //Obtenir la température du système
        AnalyseTexte[4] = "Température: " + String.format("%.0f",( température-273.15)) + "°C"; //Afficher la température du système

        //Calculer le volume approximatif de la solution en calculant une BEAA
        Vecteur3D max = new Vecteur3D(-Double.MAX_VALUE);
        Vecteur3D min = new Vecteur3D(Double.MAX_VALUE);
        for (int i = 0; i < Atomes.size(); i++) {
            max.x = Math.max(Atomes.get(i).position.x + Atomes.get(i).rayonCovalent, max.x);
            max.y = Math.max(Atomes.get(i).position.y + Atomes.get(i).rayonCovalent, max.y);
            max.z = Math.max(Atomes.get(i).position.z + Atomes.get(i).rayonCovalent, max.z);

            min.x = Math.min(Atomes.get(i).position.x - Atomes.get(i).rayonCovalent, min.x);
            min.y = Math.min(Atomes.get(i).position.y - Atomes.get(i).rayonCovalent, min.y);
            min.z = Math.min(Atomes.get(i).position.z - Atomes.get(i).rayonCovalent, min.z);
        }

        double volume = (max.x-min.x)*(max.y-min.y)*(max.z-min.z);
        AnalyseTexte[5] = "Volume: " + String.format("%.3E",volume*Math.pow(10.0,-30.0)) + " m^3"; //Afficher le volume de la solution
        //double pression = Hs.size()*Atome.R*température/volume;
        
        //Calculer l'énergie du système
        double Ek = 0; //Énergie cinétique
        double Ep = 0; //Énergie mécanique
        //Réinitialiser l'énergie potentielle de chaque atome
        for (int i = 0; i < Atomes.size(); i++) {
            Atomes.get(i).potentiel = 0;
        }
        //Additionner l'énergie cinétique et calculer l'énergie potentielle. Ep est modifié par d'autres atomes, alors on ne peut pas simplement calculer et additionner en même temps, car il se pourrait qu'un atome modife Ep d'un atome qu'on a déjà passé. On ne calculerais alors pas cette contribution.
        for (int i = 0; i < Atomes.size(); i++) {
            Ek += Math.pow(Atomes.get(i).vélocité.longueur(),2.0)*Atomes.get(i).m*0.5;
            Atome.évaluerÉnergiePotentielle(Atomes.get(i),p.PotentielMorseDécalé);
        }
        //Additionner l'énergie potentielle de chaque atome.
        for (int i = 0; i < Atomes.size(); i++) {
            Ep += Atomes.get(i).potentiel;
        }
        Ek *= 2.0; //TODO #40 Figurer pourquoi Ek doit être multiplié par 2.

        //Calculer la pression à partir de Ek
        double pression = Ek/(volume);
        AnalyseTexte[6] = "Pression: " + String.format("%.3E",pression) + " kPa";

        //Imprimer les énergies et leur variations
        AnalyseTexte[7] = "Énergie potentielle: " + String.format("%.5E",Ep) + " JÅ " + (AnalyseValeurs[7]-Ep<0.0?"▲":"▼");
        AnalyseValeurs[7] = Ep;
        AnalyseTexte[8] = "Énergie cinétique: " + String.format("%.5E",Ek) + " JÅ " + (AnalyseValeurs[8]-Ek<0.0?"▲":"▼");
        AnalyseValeurs[8] = Ek;
        AnalyseTexte[9] = "Énergie mécanique: " + String.format("%.5E",Ek+Ep) + " JÅ " + (AnalyseValeurs[9]-(Ep+Ek)<0.0?"▲":"▼");
        AnalyseValeurs[9] = Ek+Ep;

        AnalyseTexte[10] = énoncerMolécules(Atomes); //Lister les pourcentages de présence de chaques molécules dans la simulation

        //Écrire l'analyse dans le fichier sortant.
        try {
            fileWriter.write(analyseChrono + ";" + String.format("%.03f",1/(DeltaTD/1000.0)) + ";" + String.format("%.03f", temps*Math.pow(10.0,15.0)) + ";" + température + ";" + volume + ";" + pression + ";" + Ep + ";" + Ek + ";" + (Ep+Ek) + ";\n");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    //Code exécuté en parallèle de la simulation pour la dessiner et l'analyser
    private static class BoucleDessin implements Runnable{
        private static Graphics2D g;
        private static JFrame frame;

        public volatile int MisesÀJours = 0; //Nombre de mises à jours depuis le dernier échantillons d'analyse
        public volatile double progressionPlacement = 0.0; //Pourcentage de progression du placement des atomes dans leur minimum d'énergie potentielle

        public volatile boolean ini = false; //Indique si la boucle de dessin a besoin d'être initalisée
 
        public volatile ArrayList<Atome> Hs = new ArrayList<>(); //Liste des Atomes de la simulation
        public volatile ArrayList<Integer> indexe = new ArrayList<>(); //Liste des Atomes ordonnés selon la profondeure.
        
        private long analyseChrono; //Temps de la simulation en temps rééel, en s.

        @Override
        public void run(){
            System.out.println("Thread de dessin : " + Thread.currentThread().getName());
            BufferedImage b = new BufferedImage(p.TailleX, p.TailleY,BufferedImage.TYPE_4BYTE_ABGR);    //Initialiser l'image de dessin des atomes
            g = (Graphics2D) b.getGraphics();   //Initialiser le contexte graphique
            JLabel image = null; //Créer un objet Image pour l'écran
            frame = new JFrame();                //Initialiser l'écran
            frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE); //Indique que le programme s'arrêtera lorsque la fenêtre se fermera

            ini = true;

            long postier = System.currentTimeMillis(); //Indique le temps depuis la dernière analyse.
            while (true) {
                //Si le mode du programme est en ajout de molécules ou en terminaison, mettre le dessin sur pause.
                if(p.mode == Paramètres.Mode.AJOUT_MOL || p.mode == Paramètres.Mode.FIN_SIM) {
                    continue;
                }
                //Si la simulation a besoin d'être réinitialisée.
                if(ini){
                    b = new BufferedImage(p.TailleX, p.TailleY,BufferedImage.TYPE_4BYTE_ABGR);    //Initialiser l'image de dessin des atomes
                    g = (Graphics2D) b.getGraphics();   //Initialiser le contexte graphique
                    
                    //Si l'image existe déjà, la retirer de l'écran
                    if(image != null){
                        frame.remove(image);
                    }
                    image = new JLabel(new ImageIcon(b)); //Créer un objet Image pour l'écran
                    //frame = new JFrame();                //Initialiser l'écran
                    frame.setSize(p.TailleX + 100,p.TailleY + 100); //Taille de la fenêtre
                    frame.add(image);                           //Ajouter l'objet Image à l'écran
                    frame.setVisible(true);                   //Afficher la fenêtre
                    frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

                    //Indiquer des paramètres de dessins de haute qualité
                    g.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
                    g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                    g.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
                    g.setRenderingHint(RenderingHints.KEY_DITHERING, RenderingHints.VALUE_DITHER_ENABLE);
                    g.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
                    g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
                    g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                    g.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
                    
                    ini = false;
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

                //Exécuter l'analyse toutes les p.analyseÉchantillonsIntervalles millisecondes, uniquement si le programme est en mode simulation
                if (System.currentTimeMillis()-postier > p.analyseÉchantillonsIntervalles && p.mode == Paramètres.Mode.SIM){
                    analyse(MisesÀJours,analyseChrono); //Exécuter l'analyse
                    postier = System.currentTimeMillis(); //réinitialiser le postier
                    analyseChrono = System.currentTimeMillis()-départ; //Mettre à jour le temps réel en secondes.
                    MisesÀJours = 0; //Réinitialiser le nombre de mises à jours.
                }

                //Dessiner le texte d'analyse
                //Dessiner l'arrière-plan
                g.setColor(new Color(50,50,50,200));
                g.fillRect(0, 0, 220, AnalyseTexte.length*15+10);
                //Dessiner le texte
                g.setColor(Color.WHITE);
                if(p.mode == Paramètres.Mode.SIM){
                    //Si on est en mode simulation, dessiner le texte d'analyse
                    for (int i = 0; i < AnalyseTexte.length; i++) {
                        if(AnalyseTexte[i] != null){
                            g.drawString(AnalyseTexte[i], 5, (i+1)*15);
                        }
                    }
                }else if(p.mode == Paramètres.Mode.INI){
                    //Si on est en mode initialisation, dessiner la barre de progression de l'initialisation
                    g.setColor(new Color(50,50,50,200));
                    g.fillRect(0, 0, 220, AnalyseTexte.length*15+10);
                    g.setColor(Color.WHITE);
                    g.drawString("Initialisation de la position d'équilibre: ",5,15);
                    g.drawString(String.format("%.0f",progressionPlacement) + "% complété.",5,30);
                }

                SwingUtilities.updateComponentTreeUI(frame);    //Mise à jour de l'affichage
                try {Thread.sleep(30);} catch (Exception e) {} //Ajouter une petite attente pour garder le dessin à 30 FPS et ne pas prendre trop de ressources.
            }
        }
    }
    
    /**Dessine une boite représentant le domaine de simulation à  l'écran */
    public static void DessinerBoite(Graphics2D g){
        double multPersZBoiteLoin=(p.FOV/(p.TailleZ/(2*p.Zoom)+p.TailleZ/(2.0*p.Zoom) + p.FOV));    //Multiplicateur de profondeur de la face arrière (Forme la perspective)
        double multPersZBoiteProche=(p.FOV/(-p.TailleZ/(2*p.Zoom)+p.TailleZ/(2.0*p.Zoom) + p.FOV)); //Multiplicateur de profondeur de la face avant
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

        double multPersZ=(FOV*Zoom/(A.position.z+TailleZ/(2.0*Zoom) + p.FOV)); //Multiplicateur de profondeur (forme la perspective)

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
                double multPersZB = (FOV*Zoom/(B.get(A.liaisonIndexe.get(i)).position.z+TailleZ/(2.0*Zoom) + p.FOV)); //Profondeur du dexième atome
                g.setStroke(new BasicStroke());
                g.setColor(Color.BLACK);        //Couleur de la liaison
                //Dessiner la liaison
                g.drawLine(  (TailleX/2) + (int)((A.position.x)*multPersZ), (TailleY/2) - (int)((A.position.y)*multPersZ) , (TailleX/2) + (int)((B.get(A.liaisonIndexe.get(i)).position.x)*multPersZB) , (TailleY/2) - (int)((B.get(A.liaisonIndexe.get(i)).position.y)*multPersZB));
            
            }else if(A.liaisonIndexe.get(i) != -1 && A.liaisonType.get(i)){
                //Si c'est une liaison pi
                double multPersZB = (FOV*Zoom/(B.get(A.liaisonIndexe.get(i)).position.z+TailleZ/(2.0*Zoom) + p.FOV));   // Profondeur du deuxième atome
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
