package com.MiniLabo.prototype;

public class Paramètres {

    public enum Mode{
        SIM,
        INIT,
    }
    public static Mode mode = Mode.INIT;

    public static String emplacementFichierAnalyse = "C:\\Users\\Poliv\\Documents\\Math";

    public static int TailleX = 1000; //Taille de simulation 
    public static int TailleY = 512;
    public static int TailleZ = 512;
    public static float Zoom = 30f;
    public static int FOV = 70;     //Champ de vision de la caméra

    public static long tempsAttenteAnalyse = 500;

    public static Intégrateur.Modèle modèleIntégrateur = Intégrateur.Modèle.VERLET_VB;

    public static boolean UtiliserFilsExécution = true;
    public static int NBFils = 10;

    public static int itérationsPlacementInitial = 1000;
    public static double deltaPlacement = 0.05;

    public static double ordreGrandeurMorse = Math.pow(10.0,23);

    /**Delta temps de simulation entre chaque mise à jour de la simulation en fs */
    public static double dt = 0.625*Math.pow(10.0,-18.0);

    public static int NbMolécules = 1450;
    public static int NBessais = 5;
    public static boolean BEAA = true;
    public static double tampon = 1.7;
    public static boolean voisin=true;

    public static MoléculeRéf PlacementMolécule(int i){
        if(i < 3){
            return MoléculeRéf.avoirNaCl();
        }else{
            return MoléculeRéf.avoirH2O();
        }
    }

    public static double TempératureInitiale = 10.0 -273.15;

    public static boolean ListeForce[] = {
        true, //Force Paulie
        true, //Force Vanderwal
        true, //Force électrique
        true, //Force de Morse
        true, //Force de Torsion
        false, //Force Diedre
        false, //Boite Magique
    };

    public static double distForceÉval = 100.0;
    public static double ForceFriction = -0.0000000000000;
    public static double ForceGravité = 0.0;

    public static boolean PotentielMorseDécalé = false;
}
