package com.MiniLabo.prototype;

public class Paramètres {

    public enum Mode{
        SIM,
        INIT,
        FIN,
        ENTRE_DEUX,
        AJOUT_MOL,
    }

    private static Paramètres paramètres;

    public volatile Mode mode = Mode.INIT;

    public String emplacementFichierAnalyse = "C:\\Users\\Poliv\\Documents\\Math\\New folder\\";

    public int TailleX = 1000; //Taille de simulation 
    public int TailleY = 512;
    public int TailleZ = 512;
    public float Zoom = 30f;
    public int FOV = 70;     //Champ de vision de la caméra

    public boolean répéter = false;

    public long tempsAttenteAnalyse = 500;

    public Intégrateur.Modèle modèleIntégrateur = Intégrateur.Modèle.VERLET_VB;

    public boolean UtiliserFilsExécution = false;
    public int NBFils = 10;

    public static int itérationsPlacementInitial = 500;
    public static double deltaPlacement = 0.01;
    
    //Ces parametre sont utile pour debuger, et verifier.
    
    public static double ordreGrandeurMorse =Math.pow(10,23);
    public static double Morsep = 10000.0; //10000.0;
    public static double ModulePaulie = 1.0; 
    public static double DistancePaulie= 2.0;// 2.0;
    public static double ModuleTorsionDoublet= 10000.0; //10000.0;
    public static double ModuleTorsion= 1000.0; //1000.0;

    /**Delta temps de simulation entre chaque mise à jour de la simulation en fs */
    public double dt = 0.625*Math.pow(10.0,-18.0);

    public int NbMolécules = 150;
    public int NBessais = 100;
    public boolean BEAA = false;
    public double tampon = 0.1;
    public boolean voisin=true;

    private int SimMolécules = 0;
    private int type = -1;

    public MoléculeRéf PlacementMolécule(int i){
        switch (SimMolécules) {
            case 1:
                BEAA = false;
                return MoléculeRéf.avoirH2O();
            case 2:
                if (i < type) {
                    BEAA = true;
                    return MoléculeRéf.avoirNaCl();
                }else{
                    BEAA = false;
                    return MoléculeRéf.avoirH2O();
                }
            case 3:
                if (i < type) {
                    BEAA = true;
                    return MoléculeRéf.avoirHCl();
                }else{
                    BEAA = false;
                    return MoléculeRéf.avoirH2O();
                }
            case 4:
                if (i < type) {
                    BEAA = true;
                    return MoléculeRéf.avoirNaOH();
                }else{
                    BEAA = false;
                    return MoléculeRéf.avoirH2O();
                }
            case 5:
                if (i < type) {
                    BEAA = true;
                    return MoléculeRéf.avoirNaOH();
                }else if(type >= i && i < 2.0*type){
                    BEAA = false;
                    return MoléculeRéf.avoirHCl();
                }else{
                    BEAA = false;
                    return MoléculeRéf.avoirH2O();
                }
            case 6:
                if (0.5 < Math.random()) {
                    BEAA = true;
                    return MoléculeRéf.avoirNaOH();
                }else{
                    BEAA = false;
                    return MoléculeRéf.avoirHCl();
                }
            case 7:
                if (0.5 < Math.random()) {
                    BEAA = true;
                    return MoléculeRéf.avoirCl2();
                }else{
                    BEAA = true;
                    return MoléculeRéf.avoirF2();
                }
            case 8:
                BEAA = false;
                return MoléculeRéf.avoirCH4();
            case 9:
                BEAA = true;
                return MoléculeRéf.avoirC2H6();
            case 10:
                BEAA = true;
                return MoléculeRéf.avoirCH3();
            case 11:
                double ran = Math.random();
                if (0.3 < ran) {
                    BEAA = true;
                    return MoléculeRéf.avoirNH2m();
                }else if(0.6 < ran){
                    BEAA = false;
                    return MoléculeRéf.avoirNH3();
                }else{
                    BEAA = true;
                    return MoléculeRéf.avoirNH4p();
                }
            case 12:
                BEAA = true;
                return MoléculeRéf.avoirCO2();
            case 13:
                MoléculeRéf CMol = new MoléculeRéf();

                Atome C = new Atome(6);

                CMol.ajouterAtome(C);
                C.indexe = 0;

                CMol.calculerBEAA();
                CMol.MiseÀJourPos();
                BEAA = false;
                return CMol;
            default:
                BEAA = true;
                return MoléculeRéf.avoirH2();
        }
    }

    public double TempératureInitiale = 0.0;

    public boolean ListeForce[] = {
        true, //Force Paulie
        true, //Force Vanderwal
        true, //Force électrique
        true, //Force de Morse
       true, //Force de Torsion
        false, //Force Diedre
        false, //Boite Magique
    };

    public double distForceÉval = 1.0;
    public double ForceFriction = -0.00000000000000;
    public double ForceGravité = 0.0;

    public boolean PotentielMorseDécalé = false;

    public static Paramètres avoirParamètres(int i){
        if(paramètres == null){
            paramètres = new Paramètres();
        }
        paramètres.type = 0;
        paramètres.NbMolécules = 0;
        switch (i) {
            case 1://1
                paramètres.type = 0;
                paramètres.SimMolécules = 1;
                break;
            case 2://A
                paramètres.type = 1;
                paramètres.SimMolécules = 1;
                break;
            case 3://C
                paramètres.type = 3;
                paramètres.SimMolécules = 1;
                break;
            case 4://A
                paramètres.type = 1;
                paramètres.SimMolécules = 2;
                break;
            case 5://C
                paramètres.type = 3;
                paramètres.SimMolécules = 2;
                break;
            case 6://B
                paramètres.type = 2;
                paramètres.SimMolécules = 3;
                break;
            case 7://C
                paramètres.type = 3;
                paramètres.SimMolécules = 3;
                break;
            case 8://B
                paramètres.type = 2;
                paramètres.SimMolécules = 4;
                break;
            case 9://C
                paramètres.type = 3;
                paramètres.SimMolécules = 4;
                break;
            case 10://C
                paramètres.type = 3;
                paramètres.SimMolécules = 5;
                break;
            case 11://A
                paramètres.type = 1;
                paramètres.SimMolécules = 6;
                break;
            case 12://B
                paramètres.type = 2;
                paramètres.SimMolécules = 6;
                break;
            case 13://B(100)
                paramètres.type = 2;
                paramètres.NbMolécules = 100;
                paramètres.SimMolécules = 7;
                break;
            case 14://1
                paramètres.type = 0;
                paramètres.SimMolécules = 8;
                break;
            case 15://B(100)
                paramètres.type = 2;
                paramètres.NbMolécules = 100;
                paramètres.SimMolécules = 8;
                break;
            case 16://1
                paramètres.type = 0;
                paramètres.SimMolécules = 9;
                break;
            case 17://B(100)
                paramètres.type = 2;
                paramètres.NbMolécules = 100;
                paramètres.SimMolécules = 9;
                break;
            case 18://B(150)
                paramètres.type = 2;
                paramètres.NbMolécules = 150;
                paramètres.SimMolécules = 10;
                break;
            case 19://B
                paramètres.type = 2;
                paramètres.SimMolécules = 11;
                break;
            case 20://C
                paramètres.type = 3;
                paramètres.SimMolécules = 11;
                break;
            case 21://A
                paramètres.type = 1;
                paramètres.SimMolécules = 12;
                break;
            case 22://B(100)
                paramètres.type = 2;
                paramètres.NbMolécules = 100;
                paramètres.SimMolécules = 12;
                break;
            case 23://B
                paramètres.type = 2;
                paramètres.SimMolécules = 13;
                break;
            default:
                break;
        }
        
        switch (paramètres.type) {
            case 0:
                paramètres.Zoom = 150f;
                paramètres.TailleX = 800;
                paramètres.TailleY = 800;
                paramètres.TailleZ = 800;
                paramètres.UtiliserFilsExécution = false;
                paramètres.NbMolécules = 1;
                paramètres.distForceÉval = 0.0;
                paramètres.TempératureInitiale = 25.0;
                paramètres.dt = 0.625*Math.pow(10.0,-19.0);
                paramètres.tempsAttenteAnalyse = 500;
                break;
            case 1:
                paramètres.Zoom = 60f;
                paramètres.TailleX = 1080;
                paramètres.TailleY = 512;
                paramètres.TailleZ = 512;
                paramètres.UtiliserFilsExécution = false;
                paramètres.NbMolécules = 20;
                paramètres.distForceÉval = 0.0;
                paramètres.TempératureInitiale = 25.0;
                paramètres.dt = 0.625*Math.pow(10.0,-17.0);
                paramètres.tempsAttenteAnalyse = 1000;
                break;
            case 2:
                paramètres.Zoom = 40f;
                paramètres.TailleX = 1420;
                paramètres.TailleY = 512;
                paramètres.TailleZ = 512;
                paramètres.UtiliserFilsExécution = true;
                paramètres.NbMolécules = 150;
                paramètres.distForceÉval = 0.0;
                paramètres.TempératureInitiale = 25.0;
                paramètres.dt = 0.625*Math.pow(10.0,-17.0);
                paramètres.tempsAttenteAnalyse = 5000;
                break;
            case 3:
                paramètres.Zoom = 30f;
                paramètres.TailleX = 1420;
                paramètres.TailleY = 512;
                paramètres.TailleZ = 512;
                paramètres.UtiliserFilsExécution = true;
                paramètres.NbMolécules = 2000;
                paramètres.distForceÉval = 5.0;
                paramètres.TempératureInitiale = 25.0;
                paramètres.dt = 0.625*Math.pow(10.0,-17.0);
                paramètres.tempsAttenteAnalyse = 5000;
                break;
            default:
                break;
        }

        return paramètres;
    }
}
