package com.MiniLabo.prototype;

public class Paramètres {
    public static String emplacementFichierAnalyse = "C:\\Users\\Poliv\\Documents\\Math";

    public static int TailleX = 1000; //Taille de simulation 
    public static int TailleY = 512;
    public static int TailleZ = 512;
    public static float Zoom = 65f;
    public static int FOV = 70;     //Champ de vision de la caméra

    public static long tempsAttenteAnalyse = 500;

    public static Intégrateur.Modèle modèleIntégrateur = Intégrateur.Modèle.VERLET_VB;

    public static boolean UtiliserFilsExécution = false;
    public static int NBFils = 6;

    public static int itérationsPlacementInitial = 0;
    public static double deltaPlacement = 0.01;

    /**Delta temps de simulation entre chaque mise à jour de la simulation en fs */
    public static double dt = 0.625*Math.pow(10.0,-19.0);

    public static int NbMolécules = 1;
    public static int NBessais = 40;
    public static boolean BEAA = false;
    public static double tampon = 0.7;
    public static boolean voisin=true;

    public static MoléculeRéf PlacementMolécule(int i){
        if(i < 0){
            return MoléculeRéf.avoirNaCl();
        }else{
            return MoléculeRéf.avoirH2O();
        }
    }

    public static double TempératureInitiale = 25.0;

    public static boolean ListeForce[] = {
        true, //Force Paulie
        false, //Force Vanderwal
        true, //Force électrique
        true, //Force de Morse
        true, //Force de Torsion
        false, //Force Diedre
        false, //Boite Magique
    };

    public static double distForceÉval = 0.0;
    public static double ForceFriction = -0.0;
    public static double ForceGravité = 0.0;

    public static boolean PotentielMorseDécalé = false;
}
