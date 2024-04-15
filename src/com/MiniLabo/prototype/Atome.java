package com.MiniLabo.prototype;
import java.util.ArrayList;

public class Atome{

    //État de l'atome
    public Vecteur3D prevPosition = null;                       //Position de l'atome à temps t-1
    public Vecteur3D position = new Vecteur3D(100,0,0);   //Position présente de l'atome
    public Vecteur3D vélocité = new Vecteur3D(0,0,0);     //Vélocité présente
    public Vecteur3D Force = new Vecteur3D(0);              //Force appliquée présentement

    public int NP;                      //Nombre de protons. Définis le type d'atomes.
    public int NE;                      //Nombre d'électrons.
    public double m;                    //Masse de l'atome
    public double charge = 0;           //Charge de l'atome
    public float électronégativité = 0; //Électronégativité de l'atome peut varier avec le nombre d'électrons

    public int indexe = -1; //Indexe de cet atome dans la liste de la simulation

    //État des doublets
    public Vecteur3D[] positionDoublet; //Position des doublets relatif au noyau
    public Vecteur3D[] prevPosDoublet;  //Position des doublets à temps t-1
    public Vecteur3D[] vélDoublet;      //Vélocité des doublets
    public Vecteur3D[] forceDoublet;    //Force appliqué sur les doublets

    public int[] liaisonIndexe;
    // Indexe des atomes liés à cet atome. Chaque case représente une possibilité de liaison. 
    // Une liaison double ou triple utilisera deux et trois cases respectivement. 
    // Est -1 s'il n'y a aucune liaison.

    public boolean[] liaisonType;   // Types de liaisons. Sigma = faux, Pi = vrai
    public int doublets;            // Nombre de doublets électroniques
    public double rayonCovalent;    // Rayon covalent d'ordre 1 sur cet atome.

    public Molécule molécule;       //Molécule de l'atome.

    private final int MAX_N = 4;    //Nombre principal maximal. Indique le nombre de ligne du tableau prériodique utilisé.
    private final int MAX_CASE = (MAX_N*(MAX_N+1)*(2*MAX_N+1))/6 - 1;   //Nombre maximal de cases quantiques

    private int[] cases;    //Cases quantiques

    public static final double e = 1.602*Math.pow(10.0, -19.0);    //Charge élémentaire
    public static final double mP = 1.0*1.672*Math.pow(10.0,-27.0);//Masse du proton
    public static final double mE = 1.0*9.109*Math.pow(10.0,-31.0);//Masse de l'électron
    public static final double Ag = Math.pow(10,-15);              //Facteur de conversion en Angströms
    public static final double K = 8.987*Math.pow(10.0,39.0);    //Constante de Coulomb
    public static final double ep0 = 8.854*Math.pow(10.0,-42);     //Permittivité du vide
    public static final double h = 6.626*Math.pow(10.0,-14);       //Constante de Planck
    public static final double kB = 1.380*Math.pow(10.0,-3);       //Constante de Boltzman

    private static ArrayList<Atome> Environnement = new ArrayList<>(); //Référence à la liste des autres atomes de la simulation

    private static float[] AffinitéÉlectronique = {
        2.20f,                                                                                                0.00f,
        0.98f,1.57f,                                                            2.04f,2.55f,3.04f,3.50f,3.98f,0.00f,
        0.93f,2.31f,                                                            1.61f,1.90f,2.19f,2.58f,3.16f,0.00f,
        0.82f,1.00f,1.36f,1.54f,1.63f,1.66f,1.55f,1.83f,1.88f,1.91f,1.90f,1.65f,1.81f,2.01f,2.18f,2.55f,2.96f,3.00f,
        0.82f,0.95f,1.22f,1.33f,1.60f,2.16f,1.90f,2.20f,2.28f,2.20f,1.93f,1.69f,1.78f,1.96f,2.05f,2.10f,2.66f,2.60f,
        0.79f,0.89f,      1.10f,1.12f,1.13f,1.14f,0.00f,1.20f,0.00f,1.22f,1.23f,1.24f,1.25f,0.00f,1.27f,1.30f,1.50f,
                    2.36f,1.90f,2.20f,2.20f,2.28f,2.54f,2.00f,1.62f,2.33f,2.02f,2.00f,2.20f,2.20f,0.70f,0.90f,      
        1.10f,1.30f,      1.50f,1.38f,1.36f,1.28f,1.30f,1.30f,1.30f,1.30f,1.30f,1.30f,1.30f,1.30f
    };

    private static float[] rayonsCovalents = {
         32f,                                                                                 46f,
        133f,102f,                                                   85f, 75f, 71f, 63f, 64f, 67f,
        155f,139f,                                                  126f,116f,111f,103f, 99f, 96f,
        196f,171f,148f,136f,134f,122f,119f,116f,111f,110f,112f,118f,124f,121f,121f,116f,114f,117f,
        210f,185f,163f,154f,147f,138f,128f,125f,125f,120f,128f,136f,142f,140f,140f,136f,133f,131f,
        232f,196f,     180f,163f,176f,174f,173f,172f,168f,169f,168f,167f,166f,165f,164f,170f,162f,
                  152f,146f,137f,131f,129f,122f,123f,124f,133f,144f,144f,151f,145f,147f,142f,     
        223f,201f,     186f,175f,169f,170f,171f,172f,166f,166f,168f,168f,165f,167f,173f,176f,161f,
                  157f,149f,143f,141f,134f,129f,128f,121f,122f,136f,143f,162f,175f,165f,157f
    };

    private static float[] rayonsCovalents2 = {
         0f,                                                                                   0f,
        124f, 90f,                                                   78f, 67f, 60f, 57f, 59f, 96f,
        160f,132f,                                                  113f,107f,102f, 94f, 95f,107f,
        193f,147f,116f,117f,112f,111f,105f,109f,103f,101f,115f,120f,117f,111f,114f,107f,109f,121f,
        202f,157f,130f,127f,125f,121f,120f,114f,110f,117f,139f,144f,136f,130f,133f,128f,129f,135f,
        209f,161f,     139f,137f,138f,137f,135f,134f,134f,135f,135f,133f,133f,133f,131f,129f,131f,
                  128f,126f,120f,119f,116f,115f,112f,121f,142f,142f,135f,141f,135f,138f,145f,
        218f,173f,     153f,143f,138f,134f,136f,135f,135f,136f,139f,140f,140f,  0f,139f,159f,141f,
                  140f,136f,128f,128f,125f,125f,116f,116f,137f,  0f,  0f,  0f,  0f,  0f,  0f
    };

    private static float[] rayonsCovalents3 = {
          0f,                                                                                  0f,
          0f, 85f,                                                   73f, 60f, 54f, 53f, 53f,  0f,
          0f,127f,                                                  111f,102f, 94f, 95f, 93f, 96f,
          0f,133f,114f,108f,106f,103f,103f,102f, 96f,101f,120f,  0f,121f,114f,106f,107f,110f,108f,
          0f,139f,124f,121f,116f,113f,110f,103f,106f,112f,137f,  0f,146f,132f,127f,121f,125f,122f,
          0f,149f,     139f,131f,128f,  0f,  0f,  0f,  0f,132f,  0f,  0f,  0f,  0f,  0f,  0f,131f,
                  122f,119f,115f,110f,109f,107f,110f,123f,  0f,150f,137f,135f,129f,138f,133f,
          0f,159f,     140f,136f,129f,118f,116f,  0f,  0f,  0f,  0f,  0f,  0f,  0f,  0f,  0f,  0f,
                  131f,126f,121f,119f,118f,113f,112f,118f,130f,  0f,  0f,  0f,  0f,  0f,  0f
    };

    //Constante d'écran utilisé dans le calcul de la charge effective de l'atome
    private double ConstanteÉcran[] = {
        0.000,                                                                                                0.300,
        1.700,2.050,                                                            2.400,2.750,3.100,3.450,3.800,4.2515,
        8.800,9.150,                                                            9.500,9.850,10.20,10.55,10.90,11.25,
        16.80,17.15,18.00,18.85,19.70,20.55,21.40,22.25,23.10,23.95,24.80,25.65,26.00,26.35,26.70,27.05,27.40,27.75,
        34.80,35.15,36.00,36.85,37.70,38.55,39.40,40.25,41.10,41.95,42.80,43.65,44.00,44.35,44.70,45.05,45.40,45.75,
        52.80,53.15,      53.50,53.85,54.20,54.55,54.90,55.25,55.60,55.95,56.30,56.65,57.00,57.35,57.70,58.05,58.74,
                    58.75,59.10,59.45,59.80,60.15,60.50,60.85,61.20,61.55,76.00,76.35,76.70,77.05,77.4,77.75,
        84.80,83.15,      84.00,84.85,84.70,85.05,85.40,85.25,85.60,86.45,86.30,86.65,87.00,87.35,87.70,88.05,88.90
    };
    
    //Rayon atomique absolut Utilisé dans le calcul de l'électronégativité
    private double Radii [] = {
        0.5292,                                                                                                                0.3113,
        1.6283,1.08550,                                                                     0.8141,0.6513,0.5428,0.4652,0.4071,0.3676,
        2.1650,1.67110,                                                                     1.3608,1.1477,0.9922,0.8739,0.7808,0.7056,
        3.2930,2.5419,2.4149,2.2998,2.1953,2.1000,2.0124,1.9319,1.8575,1.7888,1.7250,1.6654,1.4489,1.2823,1.1450,1.0424,0.9532,0.8782,
        3.8487,2.9709,2.8224,2.6880,2.5658,2.4543,2.3520,2.2579,2.1711,2.0907,2.0160,1.9465,1.6934,1.4986,1.3440,1.2183,1.1141,1.0263,
        4.2433,3.2753,       2.6673,2.2494,1.9447,1.7129,1.5303,1.3830,1.2615,1.1596,1.0730,0.9984,0.9335,0.8765,0.8261,0.7812,0.7409,
                      0.7056,0.6716,0.6416,0.6141,0.5890,0.5657,0.5443,0.5244,0.5060,1.8670,1.6523,1.4818,1.3431,1.2283,1.1315,
        4.4479,3.4332,       3.2615,3.1061,2.2756,1.9767,1.7473,1.4496,1.2915,1.2960,1.1247,1.0465,0.9785,0.9188,0.8659,0.8188,0.8086,
    };

    private int Table2 [][] = { 
        { 0 } , // tableau [0] de int
        { 0 } , // tableau [1] de int
        { 0 } , // tableau [2] de int
        { 0 } , // tableau [3] de int
    };
    
    /**
     * Créé un nouvel atome.
     * @param nombreProton - Nombre de proton de cet atome. Définit l'élément qu'il représente.
     * @param indexe - Indexe de cet atome dans liste de la simulation
     */
    public Atome(int nombreProton){
        NP = nombreProton;
        m = (double)NP*2.0*mP; //Calculer la masse de l'atome. La masse des électrons est négligeable.
        rayonCovalent = rayonsCovalents[NP-1]/100f; //Rayon covalent de l'atome. Les données sont en pm et nous travaillons en Å

        cases = new int[MAX_CASE+1]; //Initialiser les cases quantiques
        for (int i = 0; i < NP; i++) {
            ajouterÉlectron(); // Ajouter les électrons dans les cases
        }
        charge = 0;
        évaluerValence(); //Extraire certaines propriétés de l'atome à partir de la couche de valence

        //calculerÉlectronégativitée();

        molécule = new Molécule();  //Initialise la molécule
        molécule.ajouterAtome(this);//Ajoute cet atome à la molécule
    }


    /**Initialisation de l'atome uniquement utilisée lorsqu'on le copie*/
    private Atome(){}

    /**Mise à jour de la référence à l'environnement
     * @param E - Liste des atomes de la simulation
    */
    public static void MettreÀJourEnvironnement(ArrayList<Atome> E){
        Environnement = E;
    }

    /**Retourne les forces appliqués sur l'atome A
     * @param a - Atome sur lequel appliquer les forces 
    */
    public static void ÉvaluerForces(Atome A){

        //Forces découlant des interractions avec les atomes non-liés
        for (int i = 0; i < Environnement.size(); i++) {
            //Pour tout les atomes
            if(Environnement.get(i) == A){
                //Si l'autre atome (A') est cet atome (A), sauter au prochain atome
                continue;
            }

            Atome APrime = Environnement.get(i);
            Vecteur3D dir = Vecteur3D.norm( Vecteur3D.sous(A.position,APrime.position) ); //Vecteur direction vers l'autre atome (A')
            double dist = Vecteur3D.distance(APrime.position, A.position); //Distance entre A et A'

            if(true){ ///dist < 10*A.rayonCovalent){
                //Si A' se situe à moins de N rayons covalents de A
                
                A.Force.addi( ForcePaulie(A.rayonCovalent,APrime.rayonCovalent, dist, dir)); //Appliquer la force de Pauli
                A.Force.addi( ForceVanDerWall(A.rayonCovalent,APrime.rayonCovalent, dist, dir)); //Appliquer les forces de Van der Walls
                A.Force.addi( ForceÉlectrique(A.charge, APrime.charge,dist,dir)); //Appliquer la force électrique

                for (int j = 0; j < APrime.forceDoublet.length; j++) {
                    dir = Vecteur3D.norm(Vecteur3D.sous( A.position,Vecteur3D.addi(APrime.positionDoublet[j], APrime.position))); //Vecteur de direction vers l'autre atome (A')
                    dist = Vecteur3D.distance(A.position, Vecteur3D.addi(APrime.positionDoublet[j], APrime.position)); //Distance entre le doublet et A'

                    //A.Force.addi( ForcePaulie(A.rayonCovalent,APrime.rayonCovalent/4.0, dist, dir)); //Appliquer la force de Pauli
                    //A.Force.addi( ForceÉlectrique(A.charge, -2,dist,dir)); //Appliquer la force électrique
                }

                //Forces des autres atomes sur les doublets
                for (int j = 0; j < A.forceDoublet.length; j++) {

                    Vecteur3D eDir = Vecteur3D.norm(Vecteur3D.sous(Vecteur3D.addi(A.positionDoublet[j], A.position),APrime.position)); //Vecteur de direction vers l'autre atome (A')
                    double eDist = Vecteur3D.distance(Vecteur3D.addi(A.position,A.positionDoublet[j]), APrime.position); //Distance entre le doublet et A'

                    //A.forceDoublet[j].addi( ForcePaulie(A.rayonCovalent,APrime.rayonCovalent/4.0, eDist, eDir)); //Appliquer la force de Pauli
                    //A.forceDoublet[j].addi( ForceÉlectrique(-2, APrime.charge,eDist,eDir) ); //Appliquer la force électrique

                    for (int k = 0; k < APrime.forceDoublet.length; k++) {
                        eDir = Vecteur3D.norm(Vecteur3D.sous(Vecteur3D.addi(A.positionDoublet[j], A.position),Vecteur3D.addi(APrime.positionDoublet[k], APrime.position))); //Vecteur de direction vers l'autre atome (A')
                        eDist = Vecteur3D.distance(Vecteur3D.addi(A.position,A.positionDoublet[j]), Vecteur3D.addi(APrime.positionDoublet[k], APrime.position)); //Distance entre le doublet et A'

                        //A.forceDoublet[j].addi(  ForceÉlectrique(-2, -2,eDist,eDir) ); //Appliquer la force électrique
                    }


                }
            }
        }

        for (int j = 0; j < A.forceDoublet.length; j++) {
            for (int k = 0; k < A.forceDoublet.length; k++) {
                //Si On regarde le même doublet, passer au prochain
                if(k==j){continue;}

                Vecteur3D eDir = Vecteur3D.norm(Vecteur3D.sous(A.positionDoublet[j],A.positionDoublet[k])); //Vecteur de direction vers l'autre atome (A')
                double eDist = Vecteur3D.distance( A.positionDoublet[j], A.positionDoublet[k]); //Distance entre le doublet et A'

                //A.Force.addi(new Vecteur3D(30.0,0.0,0.0));
                A.forceDoublet[j].addi( ForceÉlectrique(-2, -2, eDist, eDir)); //Appliquer la force électrique
            }
        }

        //Forces de liaisons
        boolean[] liaisonTraitée = new boolean[A.liaisonIndexe.length]; //Liste des liaison déjà traités
        //S'il y a plus d'une liaison par atome, A' sera représenté plus d'une fois dans la liste.
        //Puisque nous ne voulons pas appliquer la force plus d'une fois, il faut indiquer
        //qu'il a déjà été traité.
        for(int i = 0; i < A.liaisonIndexe.length; i++){
            //Pour toutes les liaisons

            //S'il n'y a pas de liaison, sauter à la prochaine
            if(A.liaisonIndexe[i] == -1){
                continue;
            }
            //Si l'atome a déjà été traité, passer au prochain
            if(liaisonTraitée[i]){
                continue;
            }

            //Évaluer le nombre de liaison existantes entre A et A'
            int nLiaisons = 0;
            for (int j = 0; j < A.liaisonIndexe.length; j++) {
                if(A.liaisonIndexe[j] == A.liaisonIndexe[i]){
                    nLiaisons++;
                    if(i != j){
                        liaisonTraitée[j] = true; //Indiquer que A' a déjà été traité
                    }
                }
            }

            Vecteur3D dir = Vecteur3D.norm( Vecteur3D.sous(A.position, Environnement.get(A.liaisonIndexe[i]).position) ); //Vecteur de direction qui pointe vers l'autre atome (A')
            double dist = Vecteur3D.distance(Environnement.get(A.liaisonIndexe[i]).position, A.position); //Distance entre A et A'

            A.Force.addi( ForceDeMorse(dist, dir, nLiaisons, A.NP, Environnement.get(A.liaisonIndexe[i]).NP) ); //Appliquer la force de Morse

            //Appliquer la force de torsion avec tout les autres liens
            //Trop instable pour le moment
            int nLiens = 0;
            boolean[] traité = new boolean[A.liaisonIndexe.length];
            for (int j = 0; j < A.liaisonIndexe.length; j++) {
                if(A.liaisonIndexe[j] != -1 && !traité[j]){
                    nLiens++;
                    traité[j] = true;
                }
            }

            //TODO #7 améliorer la stabilité de la force de torsion

            /*for(int j = 0; j < A.liaisonIndexe.length; j++){
                
                if(A.liaisonIndexe[j] == -1 || A.liaisonIndexe[i] == A.liaisonIndexe[j]){
                    //Si la liaison n'existe pas ou qu'elle est celle que nous évaluons en ce moment, passer à la prochaine
                    continue;
                }
                
                Vecteur3D lDir = Vecteur3D.sous( Environnement.get(A.liaisonIndexe[i]).position, Environnement.get(A.liaisonIndexe[j]).position);

                Vecteur3D IAxe = Vecteur3D.sous( Environnement.get(A.liaisonIndexe[i]).position, A.position );
                //Vecteur3D IDir = Vecteur3D.sous( lDir, Vecteur3D.mult( IAxe, Vecteur3D.scal(lDir, IAxe)/(IAxe.longueur()*IAxe.longueur()) ) );
                //IDir.norm();

                Vecteur3D JAxe = Vecteur3D.sous( Environnement.get(A.liaisonIndexe[j]).position, A.position );
                //Vecteur3D JDir = Vecteur3D.sous( lDir.opposé(), Vecteur3D.mult( JAxe, Vecteur3D.scal(lDir, JAxe)/(JAxe.longueur()*JAxe.longueur()) ) );
                //JDir.norm();

                double angle = Math.acos(Vecteur3D.scal(IAxe, JAxe)/(IAxe.longueur()*JAxe.longueur()));
                double angle0;

                //TODO #6 Vincent faire distinction s'il y a des doublets électroniques
                switch(nLiens+A.positionDoublet.length){
                    case 2:
                        angle0 = Math.PI;
                        break;
                    case 3:
                        angle0 = 2.0*Math.PI/3.0;
                        break;
                    case 4:
                        angle0 = 73.0*Math.PI/120.0;
                        break;
                    default:
                        angle0 = angle;
                        // System.err.println("Force de torsion : le nombre de liens n'est pas 2,3 ou 4");
                        break;
                }

                double Kij = 1000.0;
                double D0 = angle0-angle;
                
                Atome.Environnement.get(A.liaisonIndexe[i]).Force.addi(Vecteur3D.mult(lDir, D0*Kij));
                //Atome.Environnement.get(A.liaisonIndexe[j]).Force.addi(Vecteur3D.mult(JDir, D0*Kij));
            }

            for(int j = 0; j < A.positionDoublet.length; j++){
                Vecteur3D lDir = Vecteur3D.sous( Environnement.get(A.liaisonIndexe[i]).position, Vecteur3D.addi(A.positionDoublet[j],A.position));

                Vecteur3D IAxe = Vecteur3D.sous( Environnement.get(A.liaisonIndexe[i]).position, A.position );
                //Vecteur3D IDir = Vecteur3D.sous( lDir, Vecteur3D.mult( IAxe, Vecteur3D.scal(lDir, IAxe)/(IAxe.longueur()*IAxe.longueur()) ) );
                //IDir.norm();

                Vecteur3D JAxe = A.positionDoublet[j];
                //Vecteur3D JDir = Vecteur3D.sous( lDir.opposé(), Vecteur3D.mult( JAxe, Vecteur3D.scal(lDir, JAxe)/(JAxe.longueur()*JAxe.longueur()) ) );
                //JDir.norm();

                double angle = Math.acos(Vecteur3D.scal(IAxe, JAxe)/(IAxe.longueur()*JAxe.longueur()));
                double angle0;

                //TODO #6 Vincent faire distinction s'il y a des doublets électroniques
                switch(nLiens+A.positionDoublet.length){
                    case 2:
                        angle0 = Math.PI;
                        break;
                    case 3:
                        angle0 = 2.0*Math.PI/3.0;
                        break;
                    case 4:
                        angle0 = 73.0*Math.PI/120.0;
                        break;
                    default:
                        angle0 = angle;
                        //  System.err.println("Force de torsion : le nombre de liens n'est pas 2,3 ou 4");
                        break;
                }

                double Kij = 10000.0;
                double D0 = angle0-angle;
                
                Atome.Environnement.get(A.liaisonIndexe[i]).Force.addi(Vecteur3D.mult(lDir, D0*Kij));
                A.forceDoublet[j].addi(Vecteur3D.mult(lDir.opposé(), D0*Kij));
            }
            */
            liaisonTraitée[i] = true; //Indiquer que la liaison a été traité
        }

        //Force de torsion entre les doublets. Stable à cause de la contrainte de distance.
        /*int nLiens = 0;
        boolean[] traité = new boolean[A.liaisonIndexe.length];
        for (int j = 0; j < A.liaisonIndexe.length; j++) {
            if(A.liaisonIndexe[j] != -1 && !traité[j]){
                nLiens++;
                traité[j] = true;
            }
        }
        for (int i = 0; i < A.positionDoublet.length; i++) {
            for(int j = 0; j < A.positionDoublet.length; j++){
                if(i==j){continue;}
                Vecteur3D lDir = Vecteur3D.sous( Vecteur3D.addi(A.positionDoublet[i],A.position), Vecteur3D.addi(A.positionDoublet[j],A.position));

                Vecteur3D IAxe = A.positionDoublet[i];
                //Vecteur3D IDir = Vecteur3D.sous( lDir, Vecteur3D.mult( IAxe, Vecteur3D.scal(lDir, IAxe)/(IAxe.longueur()*IAxe.longueur()) ) );
                //IDir.norm();

                Vecteur3D JAxe = A.positionDoublet[j];
                //Vecteur3D JDir = Vecteur3D.sous( lDir.opposé(), Vecteur3D.mult( JAxe, Vecteur3D.scal(lDir, JAxe)/(JAxe.longueur()*JAxe.longueur()) ) );
                //JDir.norm();

                double angle = Math.acos(Vecteur3D.scal(IAxe, JAxe)/(IAxe.longueur()*JAxe.longueur()));
                double angle0;

                //TODO #6 Vincent faire distinction s'il y a des doublets électroniques
                switch(nLiens+A.positionDoublet.length){
                    case 2:
                        angle0 = Math.PI;
                        break;
                    case 3:
                        angle0 = 2.0*Math.PI/3.0;
                        break;
                    case 4:
                        angle0 = 73.0*Math.PI/120.0;
                        break;
                    default:
                        angle0 = angle;
                    //  System.err.println("Force de torsion : le nombre de liens n'est pas 2,3 ou 4");
                        break;
                }

                double Kij = 1000.0;
                double D0 = angle0-angle;
                
                A.forceDoublet[i].addi(Vecteur3D.mult(lDir, D0*Kij));
                //A.forceDoublet[j].addi(Vecteur3D.mult(JDir, D0*Kij));
            }
        }*/

        double ModuleFriction = -0.0000000000001;
        A.Force.addi( Vecteur3D.mult(A.vélocité,ModuleFriction)); //Appliquer une force de friction
        //A.Force.addi(new Vecteur3D(0,-1,0.0)); //Appliquer une force de gravité
        for (int i = 0; i < A.positionDoublet.length; i++) {
            //A.forceDoublet[i].addi(Vecteur3D.mult(A.vélDoublet[i],ModuleFriction));
            Vecteur3D vélTangeante = Vecteur3D.sous(A.vélDoublet[i], Vecteur3D.mult(A.positionDoublet[i], Vecteur3D.scal(A.vélDoublet[i], A.positionDoublet[i])/(A.positionDoublet[i].longueur()*A.positionDoublet[i].longueur())) );
            A.forceDoublet[i].addi(Vecteur3D.mult(vélTangeante, -0.000000000001));
        }

        //Appliquer les forces des doublets sur l'atome.
        for (int i = 0; i < A.positionDoublet.length; i++) {
            //A.forceDoublet[i].sous(Vecteur3D.mult(A.positionDoublet[i],Vecteur3D.scal(A.positionDoublet[i],A.forceDoublet[i])/(A.positionDoublet[i].longueur()*A.positionDoublet[i].longueur())));
            //A.Force.addi( A.forceDoublet[i] ); // Vecteur3D.mult(A.positionDoublet[i], Vecteur3D.scal(A.positionDoublet[i],A.forceDoublet[i])/(A.positionDoublet[i].longueur()*A.positionDoublet[i].longueur())));// Vecteur3D.mult(A.positionDoublet[i], Vecteur3D.scal(A.forceDoublet[i], A.positionDoublet[i])/(A.positionDoublet[i].longueur()*A.positionDoublet[i].longueur())) );
            //A.forceDoublet[i].sous(Vecteur3D.mult(A.positionDoublet[i],Vecteur3D.scal(A.positionDoublet[i],A.forceDoublet[i])/(A.positionDoublet[i].longueur()*A.positionDoublet[i].longueur())));
            A.forceDoublet[i] = new Vecteur3D(0);
        }

        //A.ÉvaluerContraintes();
    }

    /**
     * Renvoie un vecteur qui représente la force électrique entre deux particules
     * @param q1 - Charge de la première particule en nombre de charges élémentaires. Sera multiplié par la charge élémentaire e.
     * @param q2 - Charge de la deuxième particule.
     * @param r - Distance entre les deux particule en Angströms
     * @param dir - Vecteur unitaire de direction qui pointe de la deuxième charge vers la première.
     * @return - Vecteur de force en Newtons Angströmiens
     */
    private static Vecteur3D ForceÉlectrique(double q1, double q2, double r, Vecteur3D dir){
        return ( Vecteur3D.mult(dir,(K*q1*e*q2*e/Math.pow(r,2.0)) ));
    }
    
    /**
     * L'interaction de répulsion de Pauli est un phénomène quantique qui n'a pas d'équivalent physique. 
     * Cette force ne représente pas une conversion directe,seulement une approximation raisonnable.
     * Ainsi, cette fonction renvoie une force qui immite la répulsion de Pauli avec un terme de Lennard-Jones 6-12.
     * @param RayonCovalent1 - Rayon Covalent de la première particule. Utilisé pour calculer la longueur d'une liaison potentielle entre les deux particule. Cette force devrait équilibrer les forces de Van der Walls et la force électrique autour de 2 fois la longueur de liaison.
     * @param RayonCovalent2 - Rayon Covlaent de la deuxième particule.
     * @param dist - Distance entre les deux particules en Angströms.
     * @param dir - Vecteur unitaire de direction qui pointe de la deuxième particule vers la première.
     * @return Vecteur de force en Newtons Angströmiens
     */
    private static Vecteur3D ForcePaulie(double RayonCovalent1, double RayonCovalent2, double dist, Vecteur3D dir){
        return ( Vecteur3D.mult(dir, (80.0*Math.pow(1.0*(RayonCovalent1+RayonCovalent2),13.0)/Math.pow(dist,13.0)) ));
    }
    
    /**
     * Renvoie une approximation des forces de Van der Walls.
     * @param RayonCovalent1 - Rayon covalent de la première particule.
     * @param RayonCovalent2 - Rayon covalent de la deuxième particule.
     * @param dist - Distance entre les deux particules en Angströms.
     * @param dir - Vecteur unitaire de direction qui pointe de la deuxième particule vers la première.
     * @return Vecteur de force en Newtons Angströmiens
     */
    private static Vecteur3D ForceVanDerWall(double RayonCovalent1, double RayonCovalent2, double dist, Vecteur3D dir){
        //TODO #11 Implémenter moments dipolaires
        //TODO #12 Implémenter fréquence d'ionisation
        //TODO #13 Implémenter polarisabilité électronique
        //TODO #14 Implémenter température
        double mu1 = 1.0; //Moment dipolaire de la particule 1
        double mu2 = 1.0; //Moment dipolaire de la particule 2
        double nu1 = 1.0; //Fréquence d'ionisation de la particule 1
        double nu2 = 1.0; //Fréquence d'ionisation de la particule 2
        double a1 = 1.0;  //Polarisabilité électronique de la particule 1
        double a2 = 1.0;  //Polarisabilité électronique de la particule 2
        double T = 1.0;   //Température du système en °K
        double Keesom = (2.0*mu1*mu1*mu2*mu2)/(3*Math.pow(4*Math.PI*ep0*ep0,2.0)*kB*T);             //Forces de Keesom
        double Debye = (a1*mu2*mu2 + a2*mu1*mu1)/Math.pow(4*Math.PI*ep0*ep0,2.0);                   //Forces de Debye
        double London = ((3*h)/2.0)*((a1*a2)/Math.pow(4*Math.PI*ep0*ep0,2.0))*((nu1*nu2)/(nu1+nu2));//Forces de London
        double module = -(Keesom + Debye + London);                                                   //Module des forces de Van der Walls. Nécessite d'implémenter les variables ci-dessus d'abords.
        return ( Vecteur3D.mult(dir, (-(80.0*Math.pow(1.0*(RayonCovalent1+RayonCovalent2),7.0)/Math.pow(dist,7.0)) )));
    }

    /**
     * Le mécanisme de liaison de deux atome est très complexe, mais nous pouvons l'approximer avec un potentiel oscillatoire.
     * Cette fonction renvoie une force qui imite le comportement d'une particule dans un lien en utilisant le potentiel de Morse.
     * @param dist - Distance entre les deux atomes
     * @param dir - Vecteur unitaire de direction qui pointe du deuxième atome vers le premier
     * @param nLiaisons - Nombre de liaison présentes entre les deux atomes. (Liaison simple, double ou triple).
     * @param NP - Nombre de protons du premier atome.
     * @param NPA - Nombre de protons du deuxième atome.
     * @return Vecteur de force en Newtons Angströmiens.
     */
    private static Vecteur3D ForceDeMorse(double dist, Vecteur3D dir, int nLiaisons, int NP, int NPA){
        double l = 0; //Longueur de liaison
        if(nLiaisons == 1){
            l = rayonsCovalents[NP-1] + rayonsCovalents[NPA-1]; //Longueur d'ordre 1
        }else if(nLiaisons == 2){
            l = rayonsCovalents2[NP-1] + rayonsCovalents2[NPA-1]; //Longueur d'ordre 2
        }else if(nLiaisons == 3){
            l = rayonsCovalents3[NP-1] + rayonsCovalents3[NPA-1];  //Longueur d'ordre 3;
        }

        l = l/100.0;    //La longueur est en pm et on travaille en Å.
        double D = 40000.0; //*Math.pow(10.0,12.0);     //Énergie de dissociation du lien.
        double p = 2*D*Math.pow(Math.log(1-Math.sqrt(0.99))/l,2.0);
        //Constante de force de la liaison. Est ajustée de façon ce que la force vale 1% (.99) du maximum 
        // à 2 fois la longueur de liaison, de façons à ce que quand le lien se brise, le potentiel soit 
        // quasiment identique à s'il n'était pas lié.
        double a = Math.sqrt(p/(2.0*D));
        double module = -D*(-2.0*a*Math.exp(-2.0*a*(dist-l)) + 2.0*a*Math.exp(-a*(dist-l))); //Appliquer la force de morse

        return ( Vecteur3D.mult(dir,module) );
    }
    
    /**Applique des contraintes de mouvement, comme des bords de domaines.*/
    public void ÉvaluerContraintes(){
        //TODO #10 Le rebond de Verlet perd toujours de l'énergie
        //Appliquer des bords de domaine
        //Rebondir en Y
        if(Math.abs(position.y) > (double)App.TailleY/(2.0*App.Zoom)){
            position.y = Math.signum(position.y)*(double)App.TailleY/(2.0*App.Zoom); //Contraindre la position
            vélocité.y = -vélocité.y; //Inverser la vitesse
            if(prevPosition != null){
                //prevPosition= Vecteur3D.addi(prevPosition, new Vecteur3D(0,2*(position.y-prevPosition.y),0) ); //Inverser la vitesse de Verlet
            }
        }
        //Rebondir en X
        if(Math.abs(position.x) > (double)App.TailleX/(2.0*App.Zoom)){
            position.x = Math.signum(position.x)*(double)App.TailleX/(2.0*App.Zoom); //Contraindre la position
            vélocité.x = -vélocité.x; //Inverser la vitesse
            if(prevPosition != null){
                //prevPosition= Vecteur3D.addi(prevPosition, new Vecteur3D(2*(position.x-prevPosition.x),0,0)); //Inverser la vitesse de Verlet
            }  
        }
        //Rebondir en Z
        if(Math.abs(position.z) > (double)App.TailleZ/(2.0*App.Zoom)){
            position.z = Math.signum(position.z)*(double)App.TailleZ/(2.0*App.Zoom); //Contraindre la position
            vélocité.z = -vélocité.z; //Inverser la vitesse
            if(prevPosition != null){
                //prevPosition= Vecteur3D.addi(prevPosition, new Vecteur3D(0,0,2*(position.z-prevPosition.z)) ); //Inverser la vitesse de Verlet
            }
        }

        //Conserver la même distance entre les doublets et l'atome
        for (int i = 0; i < forceDoublet.length; i++) {
            positionDoublet[i] = Vecteur3D.mult(Vecteur3D.norm(positionDoublet[i]), rayonCovalent); //Contraindre la position et la position précédente
            prevPosDoublet[i] = Vecteur3D.mult(Vecteur3D.norm(prevPosDoublet[i]), rayonCovalent);
            //Retirer la vitesse centripède
            if(vélDoublet[i].longueur() > 0){
                vélDoublet[i] = Vecteur3D.sous(vélDoublet[i], Vecteur3D.mult( positionDoublet[i], Vecteur3D.scal(vélDoublet[i], positionDoublet[i])/(positionDoublet[i].longueur()*positionDoublet[i].longueur()) ) );
                //vélDoublet[i] = Vecteur3D.mult(Vecteur3D.norm(vélDoublet[i]), Math.min(vélDoublet[i].longueur(), 10000000000000.0));
            }
        }
    }

    /**Ajouter un électron aux cases quantiques (en mode hybridé)*/
    private void ajouterÉlectron(){
        int Qn = 1; //Nombre quantique principal n
        int Ql = 0; //Nombre quantique azimutal l
        int Qm = 0; //Nombre quantique magnétique m
        boolean Qs = false; //Spin
        int casesIndexe = 0; //Curseur indexe de la case quantique
        NE++;   //Ajoute un électron
        m += mE; //Ajoute à la masse
        charge --; //Modifie la charge

        //Traverse la liste des cases
        for (int i = 0; i < MAX_N; i++) {
            //Traverser n de 1 à MAX_N
            Qs = false;
            int cItmp = casesIndexe;
            for(int j3 = 0; j3 < 2; j3++){
                //Passer deux fois, la première en ajoutant les spin up, puis les spins down
                Ql = 0;
                casesIndexe = cItmp;// Si on est au deuxième tour, reprendre au début de la couche
                for (int j = 0; j < Qn; j++) {
                    //Traverser les l de 0 à n-1
                    Qm = -Ql;
                    for (int j2 = 0; j2 < 2*Ql+1; j2++) {
                        //Traverser les m de -l à l
                        if(cases[casesIndexe] == j3 && Ql != 2){
                            //Si la case n'est pas remplie et qu'elle est égale à 0 si on est au premier tours, ou à 1 au deuxième tour,
                            cases[casesIndexe]++; //Ajouter l'électron dans la case
                            //System.out.println("n: " + Qn + ", l: " + Ql + ", m: " + Qm + ", s: " + Qs); //Debug
                            //Sortir de la boucle
                            i = 4;
                            j = 2*Qn;
                            j2 = 2*Ql;
                            j3 = 3;
                            break;
                        }
                        //Sinon,
                        Qm++; //Prochain m
                        casesIndexe++; //Prochaine case
                    }
                    Ql++; //Prochain l
                }
                Qs = true; //Deuxième tour, spin down
            }
            Qn++; //Prochain n
        }

        //Rafraîchir l'électronégativité
        calculerÉlectronégativitée();
    }

    /**Retirer un électron aux cases quantiques (en mode hybridé)*/
    private void retirerÉlectron(){
        int Qn = MAX_N; //Nombre quantique principal n. On doit traverser les cases à l'envers, donc on commence au MAX_N
        int Ql = Qn-1;  //Nombre quantique azimutal l.
        int Qm = Ql;    //Nombre quantique magnétique m.
        int casesIndexe = MAX_CASE; //Curseur indexe des cases quantique. On doit traverser les cases à l'envers, donc on commence à MAX_CASE
        int cas = 0;    //Indique dans quel situation nous nous trouvons.
        NE--;           //Retire un électron
        m -= mE;        //Retire la masse
        charge ++;      //Modifie la charge

        for (int i = 0; i < MAX_N; i++) {
            //Traverse tout les n de MAX_N à 1
            Ql = Qn-1;
            int indexeDébut = casesIndexe;  //Conserver l'indexe de la première case de la couche
            cas = cases[casesIndexe];       //Évaluer dans quel cas nous nous trouvons
            if(cas == 2){
                //Si la case est pleine, c'est la première case pleine et c'est celle à laquelle il faut retirer un électron
                cases[casesIndexe]--;
                //System.out.println("n: " + Qn + ", l: " + Ql + ", m: " + Qm + ", s: -1!"); //Debug
                //Sortir de la boucle
                i = 4; break;
            }else{
                //Si la case n'est pas pleine, il faut évaluer toutes les autres cases de la couche
                //pour évaluer si nous avons A) une couche vide B) une couche contenant des spin up
                // C) une couche pleine de spin up D) une couche contenant des spin down
                for (int j = 0; j < Qn; j++) {
                    //Traverser les l de n-1 à 0
                    Qm = Ql;
                    for (int j2 = 0; j2 < 2*Ql+1; j2++) {
                        //Traverser les m de l à -l
                        if(cases[casesIndexe] == cas+1){
                            //Si la première case était vide et que celle-ci a un électron
                            //ou que la première avait un électron et que celle-ci en a deux
                            cases[casesIndexe]--;   //Retirer un électron de cette case
                            //System.out.println("n: " + Qn + ", l: " + Ql + ", m: " + Qm + ", s: -1!");    //Debug
                            //Sortir de la boucle
                            cas = 0;
                            i = 4; j = 2*Qn; break;
                        }
                        //Sinon,
                        casesIndexe--;  //Prochaine case
                        Qm--;           //Rpochain m
                    }
                    Ql--;   //Prochain l
                }
                if(cas == 1){
                    //Si on a passé à travers tous les l sans retirer d'électrons et que la première case n'avait qu'un électron
                    cases[indexeDébut]--; //Retirer un électron à la première case
                    //System.out.println("n: " + Qn + ", l: " + Ql + ", m: " + Qm + ", s: -1!");
                }
            }
            Qn--; //Prochain n
        }

        calculerÉlectronégativitée();
    }

    /**Extrait certaines informations de la couche de valence*/
    private void évaluerValence(){
        //Évalue le nombre liaisons possibles
        int n = 0;
        for (int i = 0; i < cases.length; i++) {
            //Pour toutes les cases
            if(cases[i] == 1){
                n++; //S'il n'y a qu'un électron dans la case, elle peut former un lien
            }
        }
        liaisonIndexe = new int[n];     //Initialiser la liste des indexes de liaisons
        liaisonType = new boolean[n];   //Initialiser la liste des types de liaisons
        //Remplir les listes
        for (int i = 0; i < liaisonIndexe.length; i++) {
            liaisonIndexe[i] = -1;
            liaisonType[i] = false;
        }

        //Évaluer le nombre de doublets
        int Qn = MAX_N;     //Nombre quantique principal n. On doit traverser les cases à l'envers, donc on commence à MAX_N
        int Ql = 0;         //Nombre quantique azimutal l.
        int Qm = 0;         //Nombre quantique magnétique m.
        boolean trouvéNiveau = false;   //Indique si on a trouvé le niveau de la couche de valence
        int casesIndexe = MAX_CASE;     //Curseur indexe des cases quantiques
        for (int i = 0; i < MAX_N; i++) {
            //Traverser n de MAX_N à 1
            Ql = Qn-1;
            for (int j = 0; j < Qn; j++) {
                //Traverser l de n-1 à 0
                Qm = Ql;
                for(int j2 = 0; j2 < 2*Ql+1; j2++){
                    //Traverser m de l à -l
                    if(cases[casesIndexe] != 0){
                        //Si la case n'est pas vide, c'est la première case  non-vide et donc elle appartient à la couche de valence
                        trouvéNiveau = true;
                    }
                    if (cases[casesIndexe] == 2 && trouvéNiveau) {
                        //Si la case possède 2 électrons et qu'elle fait partie de la couche de valence
                        doublets++; //Ajouter un doublet
                    }
                    casesIndexe--; //Prochaine case
                    Qm--;          //Prochain m
                }
                Ql--; //Prochain l
            }
            if(trouvéNiveau){
                //On a traversé toute la couche et si elle était la couche de valence, on sort de la boucle
                i = 4;
                break;
            }
            //Sinon,
            Qn--; //Prochain n
        }

        //charge += (double)doublets*2.0; //Ajuste la charge de l'atome en fonction des doublets. Ils seront traités séparéments.
        positionDoublet = new Vecteur3D[doublets]; //Initialiser la liste des positions des doublets
        prevPosDoublet = new Vecteur3D[doublets];  //Initialiser la liste des positions précédentes des doublets
        vélDoublet = new Vecteur3D[doublets];      //Initialiser la liste des vélocités des doublets
        forceDoublet = new Vecteur3D[doublets];    //Initialiser la liste des forces des doublets
        //Remplir les listes
        for (int i = 0; i < positionDoublet.length; i++) {
            forceDoublet[i] = new Vecteur3D(0);
            vélDoublet[i] = new Vecteur3D(0);
            positionDoublet[i] = new Vecteur3D((Math.random()-0.5)*2.0,(Math.random()-0.5)*2.0,(Math.random()-0.5)*2.0); //Donner une position de départ aléatoire entre (0,0,0) et (1,1,1). Elle serat ramenée au rayon de l'atome plus tard.
            prevPosDoublet[i] = positionDoublet[i].copier(); //Donner la position initiale comme position précédente initiale.
        }
        System.out.println(doublets + " doublets et " + n + " liaisons possibles.");
    }

    /**Calcule l'électronégativité de cet atome en utilisant l'électronégativité d'Allred-Rochow et la règle de Slater. L'électronégativité est affectée par le nombre d'électrons. */
    private void calculerÉlectronégativitée(){
        //Implémente l'électronégativité d'Allred-Rochow, avec la règle de Slater.
        double sigma;
        if(NE > 0 ){
            sigma = ConstanteÉcran[NE-1];
        }else{
            sigma = 0.0;
        }
        //TODO #3 Régler problème d'NP négatif

        double Zeff = (double)NP-sigma;
        électronégativité = (float)(0.359*Zeff/(Radii[NP-1]*Radii[NP-1]))+0.744f;
    }
    
    /**Créé et brise les liens lorsque nécessaire.*/
    public void miseÀJourLiens(){

        //Aller chercher l'indexe de cet atome dans Environnement
        if(indexe == -1){
            indexe = Atome.Environnement.indexOf(this);
        }

        double forceSigmoide = 5.0;

        //Briser les liens
        for (int i = 0; i < liaisonIndexe.length; i++) {
            //Pour toutes les possibilités de liaisons
            if(liaisonIndexe[i] == -1){
                //S'il n'y a pas de liaison, passer à la prochaine
                continue;
            }
            
            Atome APrime = Atome.Environnement.get(liaisonIndexe[i]); //Référence à A'
            double dist = Vecteur3D.distance(position, APrime.position); //Évaluer la distance entre les deux atomes
            if(dist > 2.0*(rayonCovalent + APrime.rayonCovalent)){
                //Si la distance est 2 fois la longueur de liaison,
                //Distribuer les électrons entre les deux atomes
                //Calculer la proportion d'électronégativité apportée par l'atome dans le lien. Si les deux on la même, le résultat serat .5, le maximum serat 1 et le minimum serat 0
                float proportion = (float)sigmoide(électronégativité/(électronégativité+APrime.électronégativité),forceSigmoide); //Passer à travers une sigmoide pour mieux séparer les deux atomes.
                charge -= 1.0-2.0*proportion;                                   //Retirer la charge partielle de cet atome (A)
                APrime.charge -= 1.0-2.0*(1.0-proportion);//Retirer la charge partielle de l'autre atome (A')
                retirerÉlectron();                              //Retirer un électron à A
                APrime.retirerÉlectron(); //Retirer un électron à A'

                //Donner aléatoirement un électron à un atome. Plus l'atome est électronégatif, plus il a de chances d'obtenir l'électron
                if(Math.random() < proportion){
                    ajouterÉlectron();
                }else{
                    APrime.ajouterÉlectron();
                }
                
                //Recalculer les proportions, car l'électronégativité est affectée par la charge.
                proportion = (float)sigmoide(électronégativité/(électronégativité+APrime.électronégativité),forceSigmoide);
                //Donner aléatoirment un électron à un atome.
                if(Math.random() < proportion){
                    ajouterÉlectron();
                }else{
                    APrime.ajouterÉlectron();
                }

                //Retirer les références à A'
                for (int j = 0; j < APrime.liaisonIndexe.length; j++) {
                    if(APrime.liaisonIndexe[j] == indexe){
                        APrime.liaisonIndexe[j] = -1;
                        APrime.liaisonType[j] = false;
                    }
                }
                liaisonIndexe[i] = -1;
                liaisonType[i] = false;

                //Séparer la molécule
                molécule.séparerMolécule(this, APrime);
            }
        }

        //Créer les liens
        for (int i = 0; i < liaisonIndexe.length; i++) {
            //Pour toutes les possibilités de liens
            double min_dist = Double.MAX_VALUE; //distance avec l'atome le plus proche
            if (liaisonIndexe[i] != -1) {
                //S'il y a une liaison, passer à la prochaine
                continue;
            }
            
            int indexePot = -1; //Indexe du candidat potentiel pour créer un lien
            int nLiaisons = 0;  //Nombre de liaisons déjà créées avec ce candidat
            int placeLibre = -1;//Nombre de liaisons que A' peut encore créer
            for (int j = 0; j < Atome.Environnement.size(); j++) {
                //Pour tout les atomes
                if(indexe == j){
                    //Si A' est A, passer au prochain
                    continue;
                }
                
                Atome APrime = Atome.Environnement.get(j); //Référence à A'
                double dist = Vecteur3D.distance(position, APrime.position); //Calculer la distance entre A et A'
                if(dist < min_dist && dist < 2.0*(rayonCovalent + APrime.rayonCovalent)){
                    //Si la distance est de moins de 2 longueurs de liaisons et qu'il est l'atome le plus proche
                    //Chercher une case qui peut acceuillir une liaison chez A'
                    placeLibre = -1;
                    for (int k = 0; k < APrime.liaisonIndexe.length; k++) {
                        if(APrime.liaisonIndexe[k] == -1){
                            placeLibre = k;
                            break;
                        }
                    }
                    if(placeLibre != -1){
                        //Si on a trouvé une place libre chez A'
                        //Évaluer le nombre de liaisons déjà créés avec A'
                        nLiaisons = 0;
                        for (int k = 0; k < liaisonIndexe.length; k++) {
                            if(j == liaisonIndexe[k]){
                                nLiaisons++;
                            }
                        }
                        if(nLiaisons < 3){
                            //S'il y a moins de 3 liaisons avec A'
                            indexePot = j;  //Garder A' comme candidat potentiel
                            min_dist = dist;//Garder A' comme atome le plus proche
                        }
                    }
                }
            }
            if(placeLibre != -1 && indexePot != -1 && nLiaisons < 3){
                //Si on a trouvé un A', qu'il a de la place libre et qu'on a moins de 3 liaisons déjà en cours avec lui,
                Atome APrime = Atome.Environnement.get(indexePot); //Référence à A'
                liaisonIndexe[i] = indexePot;   //Ajouter une référence de A' à A
                APrime.liaisonIndexe[placeLibre] = indexe;   //Donner une référence de A à A'
                if (nLiaisons == 0) {
                    //Si on n'a aucune liaison avec A', la nouvelle serat de type sigma
                    liaisonType[i] = false;
                    APrime.liaisonType[placeLibre] = false;
                }else {
                    //Sinon, la nouvelle sera de type pi
                    liaisonType[i] = true;
                    APrime.liaisonType[placeLibre] = true;
                }

                //Calculer la proportion d'électronégativité que chaque atome aporte à la liaison
                float proportion = (float)sigmoide( électronégativité/(électronégativité+APrime.électronégativité),forceSigmoide );
                charge += 1.0-2.0*proportion;               //Ajouter une charge partielle. Dans une liaison, deux électrons seront impliqués. 
                APrime.charge += 1.0-2.0*(1.0-proportion);  //Ces électrons seront plus ou moins attirés par l'un ou l'autre des atomes, d'où la charge partielle
                molécule.fusionnerMolécule(APrime.molécule);//Fusionner les deux molécules
            }
        }
    }

    /**
     * Augmente le contraste d'une valeur entre 0 et 1, en l'éloignant de 0.5 et en la poussant vers les extrêmes.
     * @param x - Valeur à contraster. Doit être entre 0 et 1.
     * @param facteur - Facteur de contraste
     * @return Une version contrasté de x. La valeur serat comprise entre 0 et 1 et sigmoide(0) = 0; sigmoide(1) = 1
     */
    private double sigmoide(double x, double facteur){
        double fNorm = 0.5/ ( Math.exp(facteur*(0.5))/(1+Math.exp(facteur*(0.5))) - 0.5);
        return fNorm*( Math.exp(facteur*(x-0.5))/(1+Math.exp(facteur*(x-0.5))) - 0.5 ) + 0.5;
    }

    /**
     * Renvoie une copie de l'atome
     * @param copierMolécule - Si vrai, copie la molécule, sinon la molécule restera la même référence
     * @return Un atome copié
     */
    public Atome copier(boolean copierMolécule){
        Atome a = new Atome();
        a.prevPosition = this.prevPosition==null?null:this.prevPosition.copier();
        a.position = this.position.copier();
        a.vélocité = this.vélocité.copier();
        a.Force = this.Force.copier();
        a.positionDoublet = this.positionDoublet.clone();
        a.prevPosDoublet = this.prevPosDoublet.clone();
        a.vélDoublet = this.vélDoublet.clone();
        a.forceDoublet = this.forceDoublet.clone();
        a.NP = this.NP;
        a.NE = this.NE;
        a.m = this.m;
        a.charge = this.charge;

        a.liaisonIndexe = this.liaisonIndexe.clone();
        a.liaisonType = this.liaisonType.clone(); // sigma = faux, pi = vrai
        a.doublets = this.doublets;
        a.rayonCovalent = this.rayonCovalent;

        a.cases = this.cases;

        if(copierMolécule){
            a.molécule = this.molécule.copier();
            a.molécule.retirerAtome(this);
            a.molécule.ajouterAtome(a);
        }else{
            a.molécule = this.molécule;
        }

        return a;
    }

    /**
     * Copie l'atome.
     * @param a - Atome à copier
     * @param copierMolécule - Si vrai, copie la molécule, sinon la molécule restera la même référence.
     */
    public void copier(Atome a, boolean copierMolécule){
        Atome b = (Atome) a;
        this.prevPosition = b.prevPosition;
        this.position = b.position.copier();
        this.vélocité = b.vélocité.copier();
        this.Force = b.Force.copier();
        this.positionDoublet = b.positionDoublet.clone();
        this.prevPosDoublet = b.prevPosDoublet.clone();
        this.vélDoublet = b.vélDoublet.clone();
        this.forceDoublet = b.forceDoublet.clone();
        this.NP = b.NP;
        this.NE = b.NE;
        this.m = b.m;
        this.charge = b.charge;

        this.liaisonIndexe = b.liaisonIndexe.clone();
        this.liaisonType = b.liaisonType.clone(); // sigma = faux, pi = vrai
        this.doublets = b.doublets;
        this.rayonCovalent = b.rayonCovalent;

        this.cases = b.cases;

        if(copierMolécule){
            this.molécule = b.molécule.copier();
            this.molécule.retirerAtome(b);
            this.molécule.ajouterAtome(this);
        }else{
            this.molécule = b.molécule;
        }
    }

    /**Initialise la position précédente initiale avec une certaine vitesse. Est utilisé pour Verlet.
     * @param h - delta temps
    */
    public void prevPositionInit(double h){
        if(prevPosition == null){
            prevPosition = Vecteur3D.addi(position, Vecteur3D.mult(vélocité, -h)).copier();
        }
    }
}
