package com.MiniLabo.prototype;
import java.util.ArrayList;

public class Atome {
    public Vecteur2f position = new Vecteur2f(100,0);
    public Vecteur2f vélocité = new Vecteur2f(0,0);
    public Vecteur2f m_force = new Vecteur2f(0);
    public double[] anglesDoublets;
    public double[] vélAngleDoublets;
    public double[] ForceAngleDoublets;
    public double e = 1.602*Math.pow(10.0, -19.0);
    public double mP = 1.0*1.672*Math.pow(10.0,-27.0);
    public double mE = 1.0*9.109*Math.pow(10.0,-31.0);
    public double Ag = Math.pow(10,-15);
    public int NP;
    public int NE;
    public double m;
    public double charge = 0;

    public int[] liaisonIndexe;
    public boolean[] liaisonType; // sigma = faux, pi = vrai
    public double[] distLiaison;
    public int doublets;
    public double rayonCovalent;

    public double K = 8.987*Math.pow(10.0,39.0);

    private int[] cases;

    private int MAX_N = 4;
    private int MAX_CASE = (MAX_N*(MAX_N+1)*(2*MAX_N+1))/6 - 1;

    private float[] électronégativité = {
        2.20f,                                                                                                0.00f,
        0.98f,1.57f,                                                            2.04f,2.55f,3.04f,3.50f,3.98f,0.00f,
        0.93f,2.31f,                                                            1.61f,1.90f,2.19f,2.58f,3.16f,0.00f,
        0.82f,1.00f,1.36f,1.54f,1.63f,1.66f,1.55f,1.83f,1.88f,1.91f,1.90f,1.65f,1.81f,2.01f,2.18f,2.55f,2.96f,3.00f,
        0.82f,0.95f,1.22f,1.33f,1.60f,2.16f,1.90f,2.20f,2.28f,2.20f,1.93f,1.69f,1.78f,1.96f,2.05f,2.10f,2.66f,2.60f,
        0.79f,0.89f,      1.10f,1.12f,1.13f,1.14f,0.00f,1.20f,0.00f,1.22f,1.23f,1.24f,1.25f,0.00f,
                    1.27f,1.30f,1.50f,2.36f,1.90f,2.20f,2.20f,2.28f,2.54f,2.00f,1.62f,2.33f,2.02f,2.00f,2.20f,2.20f,
        0.70f,0.90f,      1.10f,1.30f,1.50f,1.38f,1.36f,1.28f,1.30f,1.30f,1.30f,1.30f,1.30f,1.30f,
                    1.30f,1.30f
    };

    private float[] rayonsCovalents = {
         32f,                                                                                 46f,
        133f,102f,                                                   85f, 75f, 71f, 63f, 64f, 67f,
        155f,139f,                                                  126f,116f,111f,103f, 99f, 96f,
        196f,171f,148f,136f,134f,122f,119f,116f,111f,110f,112f,118f,124f,121f,121f,116f,114f,117f,
        210f,185f,163f,154f,147f,138f,128f,125f,125f,120f,128f,136f,142f,140f,140f,136f,133f,131f,
        232f,196f,     180f,163f,176f,174f,173f,172f,168f,169f,168f,167f,166f,165f,164f,170f,
                  162f,152f,146f,137f,131f,129f,122f,123f,124f,133f,144f,144f,151f,145f,147f,142f,     
        223f,201f,     186f,175f,169f,170f,171f,172f,166f,166f,168f,168f,165f,167f,173f,176f,
                  161f,157f,149f,143f,141f,134f,129f,128f,121f,122f,136f,143f,162f,175f,165f,157f
    };

    private float[] rayonsCovalents2 = {
         0f,                                                                                   0f,
        124f, 90f,                                                   78f, 67f, 60f, 57f, 59f, 96f,
        160f,132f,                                                  113f,107f,102f, 94f, 95f,107f,
        193f,147f,116f,117f,112f,111f,105f,109f,103f,101f,115f,120f,117f,111f,114f,107f,109f,121f,
        202f,157f,130f,127f,125f,121f,120f,114f,110f,117f,139f,144f,136f,130f,133f,128f,129f,135f,
        209f,161f,     139f,137f,138f,137f,135f,134f,134f,135f,135f,133f,133f,133f,131f,129f,
                  131f,128f,126f,120f,119f,116f,115f,112f,121f,142f,142f,135f,141f,135f,138f,145f,
        218f,173f,     153f,143f,138f,134f,136f,135f,135f,136f,139f,140f,140f,  0f,139f,159f,
                  141f,140f,136f,128f,128f,125f,125f,116f,116f,137f,  0f,  0f,  0f,  0f,  0f,  0f
    };

    private float[] rayonsCovalents3 = {
          0f,                                                                                  0f,
          0f, 85f,                                                   73f, 60f, 54f, 53f, 53f,  0f,
          0f,127f,                                                  111f,102f, 94f, 95f, 93f, 96f,
          0f,133f,114f,108f,106f,103f,103f,102f, 96f,101f,120f,  0f,121f,114f,106f,107f,110f,108f,
          0f,139f,124f,121f,116f,113f,110f,103f,106f,112f,137f,  0f,146f,132f,127f,121f,125f,122f,
          0f,149f,     139f,131f,128f,  0f,  0f,  0f,  0f,132f,  0f,  0f,  0f,  0f,  0f,  0f,
                  131f,122f,119f,115f,110f,109f,107f,110f,123f,  0f,150f,137f,135f,129f,138f,133f,
          0f,159f,     140f,136f,129f,118f,116f,  0f,  0f,  0f,  0f,  0f,  0f,  0f,  0f,  0f,
                    0f,131f,126f,121f,119f,118f,113f,112f,118f,130f,  0f,  0f,  0f,  0f,  0f,  0f
    };

    /*private float[] ÉnergieDeDissociation = {
        432f,  0f,  0f, 
    };*/

    public Atome(int nombreProton){
        NP = nombreProton;
        m = (double)NP*2.0*mP;
        rayonCovalent = rayonsCovalents[NP-1]/100f;

        cases = new int[MAX_CASE+1];
        for (int i = 0; i < NP; i++) {
            ajouterÉlectron();
        }
        évaluerValence();
    }

    public void miseÀJourForces(ArrayList<Atome> Atomes, int indexe, int TailleX, int TailleY, float Zoom){

        miseÀJourLiens(Atomes, indexe);

        Vecteur2f force = new Vecteur2f(0);
        for (int i = 0; i < Atomes.size(); i++) {
            if(Atomes.get(i) != this){
                Vecteur2f dir = Vecteur2f.normalize( Vecteur2f.sub(position,Atomes.get(i).position) );
                double dist = Vecteur2f.distance(Atomes.get(i).position, position);

                if(dist < 10.0*rayonCovalent){

                    int nLiaisons = 0;
                    for (int j = 0; j < liaisonIndexe.length; j++) {
                        if(liaisonIndexe[j] == i){
                            nLiaisons++;
                        }
                    }
                    if(nLiaisons > 0){
                        double l = rayonCovalent + Atomes.get(i).rayonCovalent;
                        if(nLiaisons == 1){
                            l = rayonsCovalents[NP-1] + rayonsCovalents[Atomes.get(i).NP-1];
                        }else if(nLiaisons == 2){
                            l = rayonsCovalents2[NP-1] + rayonsCovalents2[Atomes.get(i).NP-1];
                        }else if(nLiaisons == 3){
                            l = rayonsCovalents3[NP-1] + rayonsCovalents3[Atomes.get(i).NP-1];
                        }
                        l = l/100.0;
                        double D = 40000.0; //*Math.pow(10.0,12.0);
                        double p = 2*D*Math.pow(Math.log(1-Math.sqrt(0.99))/l,2.0);
                        double a = Math.sqrt(p/(2.0*D));
                        double module = -D*(-2.0*a*Math.exp(-2.0*a*(dist-l)) + 2.0*a*Math.exp(-a*(dist-l)));    //force du lien, potentiel de morse
                        //force.add( Vecteur2f.scale(dir, module) );
                    }
                    force.add( Vecteur2f.scale(dir,(80.0*Math.pow(1.0*(rayonCovalent+Atomes.get(i).rayonCovalent),11.0)/Math.pow(dist,13.0)) )); //force paulie
                    force.add( Vecteur2f.scale(dir,-(80.0*Math.pow(1.0*(rayonCovalent+Atomes.get(i).rayonCovalent),5.0)/Math.pow(dist,7.0)) ));

                    force.add( Vecteur2f.scale(dir,(K*charge*e*Atomes.get(i).charge*e/Math.pow(dist,2.0)) )); //Force electrique, les forces se repousse quand il son positive hydrogen est .37 ag

                    /*for (int j = 0; j < Atomes.get(i).anglesDoublets.length; j++) {
                        Vecteur2f aPos = new Vecteur2f(Atomes.get(i).anglesDoublets[j],Atomes.get(i).rayonCovalent,0);
                        Vecteur2f pos = Vecteur2f.add(Atomes.get(i).position,aPos);
                        dir = Vecteur2f.normalize( Vecteur2f.sub(pos,position) );
                        dist = Vecteur2f.distance(position, pos);
        
                        //force.add( Vecteur2f.scale(dir,(Math.pow(10.0,13.0)*Math.pow((rayonCovalent+3.0),12.0)/Math.pow(dist,12.0)) )); //force paulie
                        //force.add( Vecteur2f.scale(dir,-(Math.pow(10.0,13.0)*Math.pow((rayonCovalent+3.0),6.0)/Math.pow(dist,6.0)) ));
        
                        //force.add( Vecteur2f.scale(dir,(K*-2.0*e*charge*e/Math.pow(dist,2.0)) )); //Force electrique, les forces se repousse quand il son positive hydrogen est .37 ag
                    }

                    for (int j = 0; j < anglesDoublets.length; j++) {
                        Vecteur2f aPos = new Vecteur2f(anglesDoublets[j],rayonCovalent,0);
                        Vecteur2f pos = Vecteur2f.add(position,aPos);
                        dir = Vecteur2f.normalize( Vecteur2f.sub(pos,Atomes.get(i).position) );
                        dist = Vecteur2f.distance(Atomes.get(i).position, pos);

                        Vecteur2f forceA = new Vecteur2f(0);
        
                        forceA.add( Vecteur2f.scale(dir,(Math.pow(10.0,13.0)*Math.pow((3.0+Atomes.get(i).rayonCovalent),12.0)/Math.pow(dist,12.0)) )); //force paulie
                        forceA.add( Vecteur2f.scale(dir,-(Math.pow(10.0,13.0)*Math.pow((3.0+Atomes.get(i).rayonCovalent),6.0)/Math.pow(dist,6.0)) ));
        
                        forceA.add( Vecteur2f.scale(dir,(K*-2.0*e*Atomes.get(i).charge*e/Math.pow(dist,2.0)) )); //Force electrique, les forces se repousse quand il son positive hydrogen est .37 ag

                        Vecteur3f croix = Vecteur3f.croix(new Vecteur3f(forceA.x,forceA.y,0.0), new Vecteur3f(aPos.x,aPos.y,0));
                        ForceAngleDoublets[j] -= croix.z;
                    }*/
                }
            }
        }

        force.add( Vecteur2f.scale(vélocité,-0.0000000000001));
        //force.add(new Vecteur2f(0,-9.8));

       if(Math.abs(position.y) > (double)TailleY/(2.0*Zoom)){
            position.y = Math.signum(position.y)*(double)TailleY/(2.0*Zoom);
            vélocité.y = -vélocité.y;
        }
        if(Math.abs(position.x) > (double)TailleX/(2.0*Zoom)){
            position.x = Math.signum(position.x)*(double)TailleX/(2.0*Zoom);
            vélocité.x = -vélocité.x;
        }

        m_force = force;
    }

    public void miseÀJourPos(double deltaTemp){
        position.add(Vecteur2f.scale(vélocité, deltaTemp));
        position.add(Vecteur2f.scale(m_force,(deltaTemp*deltaTemp)/(2.0*m)));
        //position.add(new Vecteur2f(0.001f*2f*(Math.random()-0.5f),0.001f*2f*(Math.random()-0.5f)));
        vélocité.add(Vecteur2f.scale(m_force, deltaTemp/m));
       // vélocité = Vecteur2f.scale(Vecteur2f.normalize(vélocité), Math.min(vélocité.length(), Math.pow(10.0,22.0))); //Atome charger pas impacter par la force de pauli action reaction

        for(int i = 0; i < anglesDoublets.length; i++){
            vélAngleDoublets[i] += ForceAngleDoublets[i] * (deltaTemp/(2.0*mE*rayonCovalent*rayonCovalent));
            //vélAngleDoublets[i] = Math.min(Math.abs(vélAngleDoublets[i]), Math.pow(10.0,23.0))*Math.signum(vélAngleDoublets[i]);
            anglesDoublets[i] += vélAngleDoublets[i] * deltaTemp + ForceAngleDoublets[i] * ((deltaTemp*deltaTemp)/(2.0*mE*rayonCovalent*rayonCovalent));
            ForceAngleDoublets[i] = 0;
        }
    }

    private void ajouterÉlectron(){
        int Qn = 1;
        int Ql = 0;
        int Qm = 0;
        boolean Qs = false;
        int casesIndexe = 0;
        NE++;
        m += mE;

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
    }

    private void retirerÉlectron(){
        int Qn = MAX_N;
        int Ql = Qn-1;
        int Qm = Ql;
        int casesIndexe = MAX_CASE;
        int cas = 0;
        NE--;
        m -= mE;

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
        System.out.println(doublets + " doublets et " + n + " liaisons possibles.");
    }

    private void miseÀJourLiens(ArrayList<Atome> Atomes, int indexe){
        //briser les liens

        for (int i = 0; i < liaisonIndexe.length; i++) {
            if(liaisonIndexe[i] != -1){
                double dist = Vecteur2f.distance(position, Atomes.get(liaisonIndexe[i]).position);
                if(dist > 2.0*(rayonCovalent + Atomes.get(liaisonIndexe[i]).rayonCovalent)){
                    //distribution des électrons
                    float proportion = (float)électronégativité[NP-1]/(float)(électronégativité[NP-1]+électronégativité[Atomes.get(liaisonIndexe[i]).NP-1]);
                    charge -= 1.0-2.0*proportion;
                    Atomes.get(liaisonIndexe[i]).charge -= 1.0-2.0*(1.0-proportion);            //revision de charge
                    retirerÉlectron();
                    charge++;
                    Atomes.get(liaisonIndexe[i]).retirerÉlectron();
                    Atomes.get(liaisonIndexe[i]).charge++;

                    if(Math.random() < proportion){
                        ajouterÉlectron();
                        charge--;
                    }else{
                        Atomes.get(liaisonIndexe[i]).ajouterÉlectron();
                        Atomes.get(liaisonIndexe[i]).charge--;
                    }

                    if(Math.random() < proportion){
                        ajouterÉlectron();
                        charge--;
                    }else{
                        Atomes.get(liaisonIndexe[i]).ajouterÉlectron();
                        Atomes.get(liaisonIndexe[i]).charge--;
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
                        double dist = Vecteur2f.distance(position, Atomes.get(j).position);
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

                    float proportion = (float)électronégativité[NP-1]/(float)(électronégativité[NP-1]+électronégativité[Atomes.get(indexePot).NP-1]);
                    charge += 1.0-2.0*proportion;
                    Atomes.get(indexePot).charge += 1.0-2.0*(1.0-proportion);
                }
            }
        }
    }
}
