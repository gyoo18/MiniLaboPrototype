package com.MiniLabo.prototype;
import java.util.ArrayList;

public class Atome implements ObjetPhysique{
    public Vecteur3f prevPosition = null;
    public Vecteur3f position = new Vecteur3f(100,0,0);
    public Vecteur3f vélocité = new Vecteur3f(0,0,0);
    public Vecteur3f Force = new Vecteur3f(0);
    public double[] anglesDoublets;
    public double[] vélAngleDoublets;
    public double[] ForceAngleDoublets;
    public static double e = 1.602*Math.pow(10.0, -19.0);
    public static double mP = 1.0*1.672*Math.pow(10.0,-27.0);
    public static double mE = 1.0*9.109*Math.pow(10.0,-31.0);
    public static double Ag = Math.pow(10,-15);
    public int NP;
    public int NE;
    public double m;
    public double charge = 0;
    public float électronégativité = 0;

    public int[] liaisonIndexe;
    public boolean[] liaisonType; // sigma = faux, pi = vrai
    public double[] distLiaison;
    public int doublets;
    public double rayonCovalent;

    public static double K = 8.987*Math.pow(10.0,39.0);

    private int[] cases;

    private int MAX_N = 4;
    private int MAX_CASE = (MAX_N*(MAX_N+1)*(2*MAX_N+1))/6 - 1;

    private static ArrayList<Atome> Environnement = new ArrayList<>();

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
<<<<<<< HEAD
    private double Radii [] = {
        0.5292,
        0.3113,
        1.6283,
        1.0855,
        0.8141,
        0.6513,
        0.5428,
        0.4652,
        0.4071,
        0.3676,
        2.165,
        1.6711,
        1.3608,
        1.1477,
        0.9922,
        0.8739,
        0.7808,
        0.7056,
        3.293,
        2.5419,
        2.4149,
        2.2998,
        2.1953,
        2.1,
        2.0124,
        1.9319,
        1.8575,
        1.7888,
        1.725,
        1.6654,
        1.4489,
        1.2823,
        1.145,
        1.0424,
        0.9532,
        0.8782,
        3.8487,
        2.9709,
        2.8224,
        2.688,
        2.5658,
        2.4543,
        2.352,
        2.2579,
        2.1711,
        2.0907,
        2.016,
        1.9465,
        1.6934,
        1.4986,
        1.344,
        1.2183,
        1.1141,
        1.0263,
        4.2433,
        3.2753,
        2.6673,
        2.2494,
        1.9447,
        1.7129,
        1.5303,
        1.383,
        1.2615,
        1.1596,
        1.073,
        0.9984,
        0.9335,
        0.8765,
        0.8261,
        0.7812,
        0.7409,
        0.7056,
        0.6716,
        0.6416,
        0.6141,
        0.589,
        0.5657,
        0.5443,
        0.5244,
        0.506,
        1.867,
        1.6523,
        1.4818,
        1.3431,
        1.2283,
        1.1315,
        4.4479,
        3.4332,
        3.2615,
        3.1061,
        2.2756,
        1.9767,
        1.7473,
        1.4496,
        1.2915,
        1.296,
        1.1247,
        1.0465,
        0.9785,
        0.9188,
        0.8659,
        0.8188,
        0.8086,





    };

=======
>>>>>>> fff148ca276dd6c5fd9b953ace157aff3b50029f
    public Atome(int nombreProton){
        NP = nombreProton;
        m = (double)NP*2.0*mP;
        rayonCovalent = rayonsCovalents[NP-1]/100f;

        cases = new int[MAX_CASE+1];
        for (int i = 0; i < NP; i++) {
            ajouterÉlectron();
        }
        charge = 0;
        évaluerValence();

        //calculerÉlectronégativitée();
    }

    private Atome(){}

    public static void MettreÀJourEnvironnement(ArrayList<Atome> E){
        Environnement = E;
    }

    public Vecteur3f ÉvaluerForces(ObjetPhysique O,int TailleX, int TailleY, int TailleZ, float Zoom){
        Atome atome = (Atome) O;

        Vecteur3f force = new Vecteur3f(0);
        for (int i = 0; i < Environnement.size(); i++) {
            if(Environnement.get(i) != atome){
                Vecteur3f dir = Vecteur3f.normalize( Vecteur3f.sub(atome.position,Environnement.get(i).position) );
                double dist = Vecteur3f.distance(Environnement.get(i).position, atome.position);

                if(dist < 20*atome.rayonCovalent){

                    force.add( Vecteur3f.scale(dir,(1*80.0*Math.pow(1.0*(atome.rayonCovalent+Environnement.get(i).rayonCovalent),11.0)/Math.pow(dist,13.0)) )); //force paulie
                    force.add( Vecteur3f.scale(dir,-(80.0*Math.pow(1.0*(atome.rayonCovalent+Environnement.get(i).rayonCovalent),5.0)/Math.pow(dist,7.0)) ));

                    force.add( Vecteur3f.scale(dir,(1*K*atome.charge*e*Environnement.get(i).charge*e/Math.pow(dist,2.0)) )); //Force electrique, les forces se repousse quand il son positive hydrogen est .37 ag

                    /*for (int j = 0; j < Atomes.get(i).anglesDoublets.length; j++) {
                        Vecteur3f aPos = new Vecteur3f(Atomes.get(i).anglesDoublets[j],Atomes.get(i).rayonCovalent,0);
                        Vecteur3f pos = Vecteur3f.add(Atomes.get(i).position,aPos);
                        dir = Vecteur3f.normalize( Vecteur3f.sub(pos,position) );
                        dist = Vecteur3f.distance(position, pos);
        
                        //force.add( Vecteur3f.scale(dir,(Math.pow(10.0,13.0)*Math.pow((rayonCovalent+3.0),12.0)/Math.pow(dist,12.0)) )); //force paulie
                        //force.add( Vecteur3f.scale(dir,-(Math.pow(10.0,13.0)*Math.pow((rayonCovalent+3.0),6.0)/Math.pow(dist,6.0)) ));
        
                        //force.add( Vecteur3f.scale(dir,(K*-2.0*e*charge*e/Math.pow(dist,2.0)) )); //Force electrique, les forces se repousse quand il son positive hydrogen est .37 ag
                    }

                    for (int j = 0; j < anglesDoublets.length; j++) {
                        Vecteur3f aPos = new Vecteur3f(anglesDoublets[j],rayonCovalent,0);
                        Vecteur3f pos = Vecteur3f.add(position,aPos);
                        dir = Vecteur3f.normalize( Vecteur3f.sub(pos,Atomes.get(i).position) );
                        dist = Vecteur3f.distance(Atomes.get(i).position, pos);

                        Vecteur3f forceA = new Vecteur3f(0);
        
                        forceA.add( Vecteur3f.scale(dir,(Math.pow(10.0,13.0)*Math.pow((3.0+Atomes.get(i).rayonCovalent),12.0)/Math.pow(dist,12.0)) )); //force paulie
                        forceA.add( Vecteur3f.scale(dir,-(Math.pow(10.0,13.0)*Math.pow((3.0+Atomes.get(i).rayonCovalent),6.0)/Math.pow(dist,6.0)) ));
        
                        forceA.add( Vecteur3f.scale(dir,(K*-2.0*e*Atomes.get(i).charge*e/Math.pow(dist,2.0)) )); //Force electrique, les forces se repousse quand il son positive hydrogen est .37 ag

                        Vecteur3f croix = Vecteur3f.croix(new Vecteur3f(forceA.x,forceA.y,0.0), new Vecteur3f(aPos.x,aPos.y,0));
                        ForceAngleDoublets[j] -= croix.z;
                    }*/
                }
            }
        }

        boolean[] liaisonTraitée = new boolean[atome.liaisonIndexe.length];
        for(int i = 0; i < atome.liaisonIndexe.length; i++){
            if(liaisonIndexe[i] != -1){
                Vecteur3f dir = Vecteur3f.normalize( Vecteur3f.sub(position,Environnement.get(liaisonIndexe[i]).position) );
                double dist = Vecteur3f.distance(Environnement.get(liaisonIndexe[i]).position, position);

                int nLiaisons = 0;
                for (int j = 0; j < liaisonIndexe.length; j++) {
                    if(liaisonIndexe[j] == liaisonIndexe[i]){
                        nLiaisons++;
                        if(i != j){
                            liaisonTraitée[j] = true;
                        }
                    }
                }
                if(nLiaisons > 0 && !liaisonTraitée[i]){
                    double l = rayonCovalent + Environnement.get(liaisonIndexe[i]).rayonCovalent;
                    if(nLiaisons == 1){
                        l = rayonsCovalents[NP-1] + rayonsCovalents[Environnement.get(liaisonIndexe[i]).NP-1];
                    }else if(nLiaisons == 2){
                        l = rayonsCovalents2[NP-1] + rayonsCovalents2[Environnement.get(liaisonIndexe[i]).NP-1];
                    }else if(nLiaisons == 3){
                        l = rayonsCovalents3[NP-1] + rayonsCovalents3[Environnement.get(liaisonIndexe[i]).NP-1];
                    }
                    l = l/100.0;
                    double D = 40000.0; //*Math.pow(10.0,12.0);
                    double p = 2*D*Math.pow(Math.log(1-Math.sqrt(0.99))/l,2.0);
                    double a = Math.sqrt(p/(2.0*D));
                    double module = -D*(-2.0*a*Math.exp(-2.0*a*(dist-l)) + 2.0*a*Math.exp(-a*(dist-l)));    //force du lien, potentiel de morse
                    force.add( Vecteur3f.scale(dir, module) );

                    liaisonTraitée[i] = true;
                }
            }
        }

        //force.add( Vecteur3f.scale(atome.vélocité,-0.0000000000001));
        //force.add(new Vecteur3f(0,-0.1,0.0));

       if(Math.abs(atome.position.y) > (double)TailleY/(2.0*Zoom)){
            atome.position.y = Math.signum(atome.position.y)*(double)TailleY/(2.0*Zoom);
            atome.vélocité.y = -atome.vélocité.y;
        }
        if(Math.abs(atome.position.x) > (double)TailleX/(2.0*Zoom)){
            atome.position.x = Math.signum(atome.position.x)*(double)TailleX/(2.0*Zoom);
            atome.vélocité.x = -atome.vélocité.x;
        }
        if(Math.abs(atome.position.z) > (double)TailleZ/(2.0*Zoom)){
            atome.position.z = Math.signum(atome.position.z)*(double)TailleZ/(2.0*Zoom);
            atome.vélocité.z = -atome.vélocité.z;
        }

        return force;
    }

    /*public void miseÀJourPos(double deltaTemp){
        position.add(Vecteur3f.scale(vélocité, deltaTemp));
        position.add(Vecteur3f.scale(Force,(deltaTemp*deltaTemp)/(2.0*m)));
        //position.add(new Vecteur3f(0.001f*2f*(Math.random()-0.5f),0.001f*2f*(Math.random()-0.5f)));
        vélocité.add(Vecteur3f.scale(Force, deltaTemp/m));
       // vélocité = Vecteur3f.scale(Vecteur3f.normalize(vélocité), Math.min(vélocité.length(), Math.pow(10.0,22.0))); //Atome charger pas impacter par la force de pauli action reaction

        for(int i = 0; i < anglesDoublets.length; i++){
            vélAngleDoublets[i] += ForceAngleDoublets[i] * (deltaTemp/(2.0*mE*rayonCovalent*rayonCovalent));
            //vélAngleDoublets[i] = Math.min(Math.abs(vélAngleDoublets[i]), Math.pow(10.0,23.0))*Math.signum(vélAngleDoublets[i]);
            anglesDoublets[i] += vélAngleDoublets[i] * deltaTemp + ForceAngleDoublets[i] * ((deltaTemp*deltaTemp)/(2.0*mE*rayonCovalent*rayonCovalent));
            ForceAngleDoublets[i] = 0;
        }
    }*/

    private void ajouterÉlectron(){
        int Qn = 1;
        int Ql = 0;
        int Qm = 0;
        boolean Qs = false;
        int casesIndexe = 0;
        NE++;
        m += mE;
        charge --;

        for (int i = 0; i < MAX_N; i++) {
            Qs = false;
            int cItmp = casesIndexe;
            for(int j3 = 0; j3 < 2; j3++){
                Ql = 0;
                casesIndexe = cItmp;
                for (int j = 0; j < Qn; j++) {
                    Qm = -Ql;
                    for (int j2 = 0; j2 < 2*Ql+1; j2++) {
                        if(cases[casesIndexe] == j3 && Ql != 2){
                            cases[casesIndexe]++;
                            //System.out.println("n: " + Qn + ", l: " + Ql + ", m: " + Qm + ", s: " + Qs);
                            i = 4;
                            j = 2*Qn;
                            j2 = 2*Ql;
                            j3 = 3;
                            break;
                        }
                        Qm++;
                        casesIndexe++;
                    }
                    Ql++;
                }
                Qs = true;
            }
            Qn++;
        }

        calculerÉlectronégativitée();
    }

    private void retirerÉlectron(){
        int Qn = MAX_N;
        int Ql = Qn-1;
        int Qm = Ql;
        int casesIndexe = MAX_CASE;
        int cas = 0;
        NE--;
        m -= mE;
        charge ++;

        for (int i = 0; i < MAX_N; i++) {
            Ql = Qn-1;
            int indexeDébut = casesIndexe;
            cas = cases[casesIndexe];
            if(cas == 2){
                cases[casesIndexe]--;
                //System.out.println("n: " + Qn + ", l: " + Ql + ", m: " + Qm + ", s: -1!");
                i = 4; break;
            }else{
                for (int j = 0; j < Qn; j++) {
                    Qm = Ql;
                    for (int j2 = 0; j2 < 2*Ql+1; j2++) {
                        if(cases[casesIndexe] == cas+1){
                            cases[casesIndexe]--;
                            cas = 0;
                            //System.out.println("n: " + Qn + ", l: " + Ql + ", m: " + Qm + ", s: -1!");
                            i = 4; j = 2*Qn; break;
                        }
                        casesIndexe--;
                        Qm--;
                    }
                    Ql--;
                }
                if(cas == 1){
                    cases[indexeDébut]--;
                    //System.out.println("n: " + Qn + ", l: " + Ql + ", m: " + Qm + ", s: -1!");
                }
            }
            Qn--;
        }

        calculerÉlectronégativitée();
    }

    private void évaluerValence(){
        int n = 0;
        for (int i = 0; i < cases.length; i++) {
            if(cases[i] == 1){
                n++;
            }
        }
        liaisonIndexe = new int[n];
        liaisonType = new boolean[n];
        distLiaison = new double[n];
        for (int i = 0; i < liaisonIndexe.length; i++) {
            liaisonIndexe[i] = -1;
            liaisonType[i] = false;
            distLiaison[i] = Double.MAX_VALUE;
        }

        int Qn = MAX_N;
        int Ql = 0;
        int Qm = 0;
        boolean trouvéNiveau = false;
        int casesIndexe = MAX_CASE;
        for (int i = 0; i < MAX_N; i++) {
            Ql = Qn-1;
            for (int j = 0; j < Qn; j++) {
                Qm = Ql;
                for(int j2 = 0; j2 < 2*Ql+1; j2++){
                    if(cases[casesIndexe] != 0){
                        trouvéNiveau = true;
                    }
                    if (cases[casesIndexe] == 2 && trouvéNiveau) {
                        doublets++;
                    }
                    casesIndexe--;
                    Qm--;
                }
                Ql--;
                if(trouvéNiveau){
                    i = 4;
                    break;
                }
            }
            Qn--;
        }
        //charge += (double)doublets*2.0;
        anglesDoublets = new double[doublets];
        vélAngleDoublets = new double[doublets];
        ForceAngleDoublets = new double[doublets];
        for (int i = 0; i < anglesDoublets.length; i++) {
            anglesDoublets[i] = Math.random();
        }
        //System.out.println(doublets + " doublets et " + n + " liaisons possibles.");
    }

    private void calculerÉlectronégativitée(){

        double sigma;
        if(NE > 0 ){
            sigma = ConstanteÉcran[NE-1];
        }else{
            sigma = 0.0; //TODO #3 Régler problème d'NP négatif
        }

        double Zeff = (double)NP-sigma;
        électronégativité = (float)(0.359*Zeff/(Radii[NP-1]*Radii[NP-1]))+0.744f;
    }
    
    public void miseÀJourLiens(ArrayList<Atome> Atomes, int indexe){
        //briser les liens

        for (int i = 0; i < liaisonIndexe.length; i++) {
            if(liaisonIndexe[i] != -1){
                double dist = Vecteur3f.distance(position, Atomes.get(liaisonIndexe[i]).position);
                if(dist > 2.0*(rayonCovalent + Atomes.get(liaisonIndexe[i]).rayonCovalent)){
                    //distribution des électrons
                    float proportion = (float)sigmoid(électronégativité/(électronégativité+Atomes.get(liaisonIndexe[i]).électronégativité),15.0);
                    charge -= 1.0-2.0*proportion;
                    Atomes.get(liaisonIndexe[i]).charge -= 1.0-2.0*(1.0-proportion);            //révision de charge
                    retirerÉlectron();
                    //charge++;
                    Atomes.get(liaisonIndexe[i]).retirerÉlectron();
                    //Atomes.get(liaisonIndexe[i]).charge++;

                    if(Math.random() < proportion){
                        ajouterÉlectron();
                    }else{
                        Atomes.get(liaisonIndexe[i]).ajouterÉlectron();
                    }
                    
                    proportion = (float)sigmoid(électronégativité/(électronégativité+Atomes.get(liaisonIndexe[i]).électronégativité),15.0);

                    if(Math.random() < proportion){
                        ajouterÉlectron();
                    }else{
                        Atomes.get(liaisonIndexe[i]).ajouterÉlectron();
                    }

                    for (int j = 0; j < Atomes.get(liaisonIndexe[i]).liaisonIndexe.length; j++) {
                        if(Atomes.get(liaisonIndexe[i]).liaisonIndexe[j] == indexe){
                            Atomes.get(liaisonIndexe[i]).liaisonIndexe[j] = -1;
                            Atomes.get(liaisonIndexe[i]).liaisonType[j] = false;
                            Atomes.get(liaisonIndexe[i]).distLiaison[j] = Double.MAX_VALUE;
                        }
                    }
                    liaisonIndexe[i] = -1;
                    liaisonType[i] = false;
                    distLiaison[i] = Double.MAX_VALUE;
                }
            }
        }

        //créer les liens

        for (int i = 0; i < liaisonIndexe.length; i++) {
            double min_dist = Double.MAX_VALUE;
            if (liaisonIndexe[i] == -1) {
                int indexePot = -1;
                int nLiaisons = 0;
                int placeLibre = -1;
                for (int j = 0; j < Atomes.size(); j++) {
                    if(indexe != j){
                        double dist = Vecteur3f.distance(position, Atomes.get(j).position);
                        if(dist < min_dist && dist < 2.0*(rayonCovalent + Atomes.get(j).rayonCovalent)){
                            placeLibre = -1;
                            for (int k = 0; k < Atomes.get(j).liaisonIndexe.length; k++) {
                                if(Atomes.get(j).liaisonIndexe[k] == -1){
                                    placeLibre = k;
                                    break;
                                }
                            }
                            if(placeLibre != -1){
                                nLiaisons = 0;
                                for (int k = 0; k < liaisonIndexe.length; k++) {
                                    if(j == liaisonIndexe[k]){
                                        nLiaisons++;
                                    }
                                }
                                indexePot = j;
                                min_dist = dist;
                            }
                        }
                    }
                }
                if(placeLibre != -1 && indexePot != -1 && nLiaisons < 3){
                    liaisonIndexe[i] = indexePot;
                    Atomes.get(indexePot).liaisonIndexe[placeLibre] = indexe;
                    if (nLiaisons == 0) {
                        liaisonType[i] = false;
                    }else {
                        liaisonType[i] = true;   
                    }

                    float proportion = (float)sigmoid(électronégativité/(électronégativité+Atomes.get(indexePot).électronégativité),15.0);
                    charge += 1.0-2.0*proportion;
                    Atomes.get(indexePot).charge += 1.0-2.0*(1.0-proportion);
                }
            }
        }
    }

    private double sigmoid(double x, double facteur){
        return Math.exp(facteur*(x-0.5))/(1+Math.exp(facteur*(x-0.5)));
    }

    @Override
    public void changerPosition(Vecteur3f pos) {
        position = pos.copy();
    }

    @Override
    public void ajouterPosition(Vecteur3f pos) {
        position.add(pos);
    }

    public Vecteur3f avoirPosition(){
        return position;
    }

    @Override
    public void changerVitesse(Vecteur3f v) {
        vélocité = v.copy();
    }

    @Override
    public void ajouterVitesse(Vecteur3f v) {
        vélocité.add(v);
    }

    @Override
    public Vecteur3f avoirVitesse(){
        return vélocité;
    }

    public double avoirMasse(){
        return m;
    }

    @Override
    public void changerForce(Vecteur3f f) {
        Force = f.copy();
    }

    @Override
    public void ajouterForce(Vecteur3f f) {
        Force.add(f);
    }

    @Override
    public Vecteur3f avoirForce() {
        return Force;
    }

    @Override
    public void changerPrevPosition(Vecteur3f pos) {
        prevPosition = pos.copy();
    }

    @Override
    public void ajouterPrevPosition(Vecteur3f pos) {
        prevPosition.add(pos);
    }

    @Override
    public Vecteur3f avoirPrevPosition() {
        return prevPosition;
    }

    public void prevPositionInit(double h){
        if(prevPosition == null){
            prevPosition = Vecteur3f.add(position, Vecteur3f.scale(vélocité, -h/m)).copy();
        }
    }

    public Atome copy(){
        Atome a = new Atome();
        a.prevPosition = this.prevPosition==null?null:this.prevPosition.copy();
        a.position = this.position.copy();
        a.vélocité = this.vélocité.copy();
        a.Force = this.Force.copy();
        a.anglesDoublets = this.anglesDoublets.clone();
        a.vélAngleDoublets = this.vélAngleDoublets.clone();
        a.ForceAngleDoublets = this.ForceAngleDoublets.clone();
        a.NP = this.NP;
        a.NE = this.NE;
        a.m = this.m;
        a.charge = this.charge;

        a.liaisonIndexe = this.liaisonIndexe.clone();
        a.liaisonType = this.liaisonType.clone(); // sigma = faux, pi = vrai
        a.distLiaison = this.distLiaison.clone();
        a.doublets = this.doublets;
        a.rayonCovalent = this.rayonCovalent;

        a.cases = this.cases;

        return a;
    }

    public void copy(ObjetPhysique a){
        Atome b = (Atome) a;
        this.prevPosition = b.prevPosition;
        this.position = b.position.copy();
        this.vélocité = b.vélocité.copy();
        this.Force = b.Force.copy();
        this.anglesDoublets = b.anglesDoublets.clone();
        this.vélAngleDoublets = b.vélAngleDoublets.clone();
        this.ForceAngleDoublets = b.ForceAngleDoublets.clone();
        this.NP = b.NP;
        this.NE = b.NE;
        this.m = b.m;
        this.charge = b.charge;

        this.liaisonIndexe = b.liaisonIndexe.clone();
        this.liaisonType = b.liaisonType.clone(); // sigma = faux, pi = vrai
        this.distLiaison = b.distLiaison.clone();
        this.doublets = b.doublets;
        this.rayonCovalent = b.rayonCovalent;

        this.cases = b.cases;
    }
}
