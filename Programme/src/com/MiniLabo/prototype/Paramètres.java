package com.MiniLabo.prototype;

import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;

import com.MiniLabo.prototype.Intégrateur.Modèle;

public class Paramètres {

    public enum Mode{
        SIM,
        INIT,
        FIN_SIM,
        ENTRE_DEUX,
        AJOUT_MOL,
        FIN_PROGRAME
    }

    private static Paramètres paramètres;

    public volatile Mode mode = Mode.INIT;

    public String dossierAnalyse = System.getProperty("user.dir") + "\\";

    public int TailleX = 1000; //Taille de simulation 
    public int TailleY = 512;
    public int TailleZ = 512;
    public float Zoom = 30f;
    public float FOV = 70;     //Champ de vision de la caméra

    public boolean répéter = false;

    public long analyseÉchantillonsIntervalles = 500;

    public Intégrateur.Modèle modèleIntégrateur = Intégrateur.Modèle.VERLET_VB;

    public boolean UtiliserFilsExécution = false;
    public int NBFils = 10;

    public static int itérationsPlacementInitial =200;
    public static double deltaPlacement = 0.1;

    public static long simDurée = 10000;
    
    //Ces parametre sont utile pour debuger, et verifier.
    
    public static double ordreGrandeurMorse = Math.pow(10.0,23.0);
    public static double Morsep = 10000.0;
    public static double ModulePaulie = 1.0; 
    public static double DistancePaulie= 2.0;
    public static double ModuleTorsionDoublet= 10000.0;
    public static double ModuleTorsion= 10000.0;

    /**Delta temps de simulation entre chaque mise à jour de la simulation en fs */
    public double dt = 0.625*Math.pow(10.0,-18.0);

    public int NbMolécules = 150;
    public int NBessais = 100;
    public boolean BEAA = false;
    public double tampon = 0.5;
    public boolean voisin=true;

    private int SimMolécules = 2;
    private int type = -1;

    public MoléculeRéf PlacementMolécule(int i){
        switch (SimMolécules) {
            case 1:
                BEAA = false;
                return MoléculeRéf.avoirH2O();
            case 2:
                if (i < 1) {
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
        true, //Force Diedre
        false, //Boite Magique
    };

    public double distForceÉval = 1.0;
    public double ForceFriction = 0.0;
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

        /* switch (i) {
            case 1:
                paramètres.type = 2;
                paramètres.SimMolécules = 2;
                paramètres.modèleIntégrateur = Modèle.EULER;
                break;
            case 2:
                paramètres.type = 2;
                paramètres.SimMolécules = 2;
                paramètres.modèleIntégrateur = Modèle.VERLET;
                break;
            case 3:
                paramètres.type = 2;
                paramètres.SimMolécules = 2;
                paramètres.modèleIntégrateur = Modèle.VERLET_V;
                break;
            case 4:
                paramètres.type = 2;
                paramètres.SimMolécules = 2;
                paramètres.modèleIntégrateur = Modèle.VERLET_VB;
                break;
            case 5:
                paramètres.type = 2;
                paramètres.SimMolécules = 2;
                paramètres.modèleIntégrateur = Modèle.VERLET_VBCD;
                break;
            case 6:
                paramètres.type = 2;
                paramètres.SimMolécules = 2;
                paramètres.modèleIntégrateur = Modèle.RK4;
                break;
            default:
                break;
        } */
        
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
                paramètres.dt = 0.625*Math.pow(10.0,-17.0);
                paramètres.analyseÉchantillonsIntervalles = 500;
                paramètres.ListeForce[1] = false;
                break;
            case 1:
                paramètres.Zoom = 60f;
                paramètres.TailleX = 1080;
                paramètres.TailleY = 512;
                paramètres.TailleZ = 512;
                paramètres.UtiliserFilsExécution = false;
                paramètres.NbMolécules = 20;
                paramètres.distForceÉval = 5.0;
                paramètres.TempératureInitiale = 25.0;
                paramètres.dt = 0.625*Math.pow(10.0,-17.0);
                paramètres.analyseÉchantillonsIntervalles = 1000;
                paramètres.ListeForce[1] = true;
                break;
            case 2:
                paramètres.Zoom = 40f;
                paramètres.TailleX = 1420;
                paramètres.TailleY = 512;
                paramètres.TailleZ = 512;
                paramètres.UtiliserFilsExécution = true;
                paramètres.NbMolécules = 150;
                paramètres.distForceÉval = 5.0;
                paramètres.TempératureInitiale = 25.0;
                paramètres.dt = 0.625*Math.pow(10.0,-17.0);
                paramètres.analyseÉchantillonsIntervalles = 1000;
                paramètres.ListeForce[1] = true;
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
                paramètres.analyseÉchantillonsIntervalles = 1000;
                paramètres.ListeForce[1] = true;
                break;
            default:
                break;
        }

        return paramètres;
    }

    public static Paramètres chargerDepuisFichier(){
        paramètres = new Paramètres();
        try {
            File fichierParam = new File(System.getProperty("user.dir") + "\\MiniLaboPrototype\\param.txt");
            FileReader fileReader = new FileReader(fichierParam);

            ArrayList<String> listeParamNom = new ArrayList<>();
            ArrayList<String> listeParamVal = new ArrayList<>();
            String paramNom = "";
            String paramVal = "";
            boolean commentaire = false;
            boolean cursParamNom = true;
            boolean cursParamVal = false;
            int i = 0;
            boolean lire = true;
            while (lire) {

                int val = fileReader.read();
                char c = (char)val;
                if(c == '\r'){
                    continue;
                }
                if (commentaire && !(c == '\n')) {
                    continue;
                }
                if (commentaire && (c == '\n')) {
                    commentaire = false;
                    cursParamNom = true;
                    cursParamVal = false;
                    continue;
                }
                if(!commentaire && (c == '\n' || val == -1)){
                    cursParamNom = true;
                    cursParamVal = false;
                    if(paramNom.length() != 0){
                        paramVal = paramVal.replace(" ", "");
                        listeParamVal.add(paramVal);
                        paramVal = "";
                        paramNom = "";
                    }
                    if(val == -1){
                        lire = false;
                    }
                    continue;
                }
                if(c == '#'){
                    commentaire = true;
                    continue;
                }
                if(c == '='){
                    cursParamNom = false;
                    paramNom = paramNom.replace(" ", "");
                    paramNom = paramNom.toUpperCase();
                    listeParamNom.add(paramNom);
                    cursParamVal = true;
                    continue;
                }
                if(cursParamNom){
                    paramNom += c;
                }
                if(cursParamVal){
                    paramVal += c;
                }
            }
            //System.out.println("Hi");

            for (int j = 0; j < listeParamNom.size(); j++) {
                switch (listeParamNom.get(j)) {
                    case "FOV":
                        paramètres.FOV = Float.parseFloat(listeParamVal.get(j));
                        break;
                    case "INI_TAILLE_X":
                        paramètres.TailleX = Integer.parseInt(listeParamVal.get(j));
                        break;
                    case "INI_TAILLE_Y":
                        paramètres.TailleY = Integer.parseInt(listeParamVal.get(j));
                        break;
                    case "INI_TAILLE_Z":
                        paramètres.TailleZ = Integer.parseInt(listeParamVal.get(j));
                        break;
                    case "INI_ZOOM":
                        paramètres.Zoom = Float.parseFloat(listeParamVal.get(j));
                        break;
                    case "INI_NB_MOLÉCULES":
                        paramètres.NbMolécules = Integer.parseInt(listeParamVal.get(j));
                        break;
                    case "INI_NB_ESSAIS":
                        paramètres.NBessais = 100;
                        break;
                    case "INI_COLLISION":
                        switch (listeParamVal.get(j).toUpperCase()) {
                            case "BEAA":
                                paramètres.BEAA = true;
                                break;
                            case "SPHÈRE":
                                paramètres.BEAA = false;
                                break;
                            default:
                                System.err.println(Txt.ROUGE + "ERREUR" + Txt.ROUGE + "Paramètres dans " + fichierParam.getAbsolutePath() + fichierParam.getName() 
                                    + " :\nINI_COLLISION n'accepte pas " + listeParamNom.get(j) + " comme valeure."
                                    + " Valeure acceptées :\nBEAA \nSPHÈRE");
                                break;
                        }
                        break;
                    case "INI_TAMPON":
                        paramètres.tampon = Double.parseDouble(listeParamVal.get(j));
                        break;
                    case "INI_NB_ITÉRATIONS":
                        paramètres.itérationsPlacementInitial = Integer.parseInt(listeParamVal.get(j));
                        break;
                    case "INI_DELTA_S":
                        paramètres.deltaPlacement = Double.parseDouble(listeParamVal.get(j));
                        break;
                    case "INI_TEMPÉRATURE":
                        paramètres.TempératureInitiale = Double.parseDouble(listeParamVal.get(j));
                        break;
                    case "SIM_DURÉE":
                        paramètres.simDurée = (long)(Double.parseDouble(listeParamVal.get(j))*1000.0);
                        break;
                    case "SIM_DELTA_TEMPS":
                        paramètres.dt = Double.parseDouble(listeParamVal.get(j))*Math.pow(10.0,-15.0);
                        break;
                    case "SIM_FILS_EXÉCUTIONS":
                        paramètres.UtiliserFilsExécution = Boolean.parseBoolean(listeParamVal.get(j));
                        break;
                    case "SIM_NB_FILS":
                        paramètres.NBFils = Integer.parseInt(listeParamVal.get(j));
                        break;
                    case "SIM_MODÈLE_INTÉGRATEUR":
                        paramètres.modèleIntégrateur = Intégrateur.Modèle.valueOf(listeParamVal.get(j));
                        break;
                    case "SIM_F_PAULIE":
                        paramètres.ListeForce[0] = Boolean.parseBoolean(listeParamVal.get(j));
                        break;
                    case "SIM_F_VAN_DER_WALLS":
                        paramètres.ListeForce[1] = Boolean.parseBoolean(listeParamVal.get(j));
                        break;
                    case "SIM_F_ÉLECTRIQUE":
                        paramètres.ListeForce[2] = Boolean.parseBoolean(listeParamVal.get(j));
                        break;
                    case "SIM_F_MORSE":
                        paramètres.ListeForce[3] = Boolean.parseBoolean(listeParamVal.get(j));
                        break;
                    case "SIM_F_TORSION":
                        paramètres.ListeForce[4] = Boolean.parseBoolean(listeParamVal.get(j));
                        break;
                    case "SIM_F_DIÈDRE":
                        paramètres.ListeForce[5] = Boolean.parseBoolean(listeParamVal.get(j));
                        break;
                    case "SIM_F_FRICTION":
                        paramètres.ForceFriction = -Double.parseDouble(listeParamVal.get(j));
                        break;
                    case "SIM_F_GRAVITÉ":
                        if(Boolean.parseBoolean(listeParamVal.get(j))){
                            paramètres.ForceGravité = 9.8*Math.pow(10.0, 10.0);
                        }else{
                            paramètres.ForceGravité = 0.0;
                        }
                        break;
                    case "SIM_CONDITIONS_FRONTIÈRES":
                        switch (listeParamVal.get(j)) {
                            case "COLLISION":
                                paramètres.ListeForce[6] = false;
                                break;
                            case "RÉPÉTÉ":
                                paramètres.ListeForce[6] = true;
                                break;
                            default:
                                System.err.println(Txt.ROUGE + "ERREUR" + Txt.ROUGE + "Paramètres dans " + fichierParam.getAbsolutePath() + fichierParam.getName() 
                                    + " :\nSIM_CONDITIONS_FRONTIÈRES n'accepte pas " + listeParamNom.get(j) + " comme valeure."
                                    + " Valeure acceptées :\nCOLLISION \nRÉPÉTÉ");
                                break;
                        }
                        break;
                    case "SIM_FORCES_INTRA_LIENS":
                        paramètres.voisin = !Boolean.parseBoolean(listeParamVal.get(j));
                        break;
                    case "SIM_RAYON_INFLUENCE":
                        paramètres.distForceÉval = Double.parseDouble(listeParamVal.get(j));
                        break;
                    case "ANALYSE_DOSSIER":
                        String dossier = listeParamVal.get(j);
                        if(dossier.contains("$$/")){
                            dossier = dossier.replace("$$/", "/");
                            paramètres.dossierAnalyse = System.getProperty("user.dir") + dossier;
                        }else{
                            paramètres.dossierAnalyse = dossier;
                        }
                        break;
                    case "AN_ÉCHANTILLONS_INTERVALLES":
                        paramètres.analyseÉchantillonsIntervalles = (int)(Double.parseDouble(listeParamVal.get(j))*1000.0);
                        break;
                    case "AN_POTENTIEL_MORSE_DÉCALÉ":
                        paramètres.PotentielMorseDécalé = Boolean.parseBoolean(listeParamVal.get(j));
                        break;
                    default:
                        System.err.println(Txt.ROUGE + "ERREUR" + Txt.EFF + " Paramètres dans " + fichierParam.getAbsolutePath() + fichierParam.getName() + " :\n" + listeParamNom.get(j) + " n'est pas un paramètres valide");
                        break;
                }
            }
            fileReader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return paramètres;
    }
    
}
