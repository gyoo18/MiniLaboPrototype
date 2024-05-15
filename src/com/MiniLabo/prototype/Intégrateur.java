package com.MiniLabo.prototype;

import java.util.ArrayList;

/**
 * Classe d'intégrateurs numériques qui intègrent des atomes.
 */

public class Intégrateur {

    /**Classe qui serat exécutée sur les fils d'exécutions pour le calcul des forces */
    private static class FilsDistributeur implements Runnable{

        /**Sous-ensemble de la liste d'atome que ce fil traitera en parallèle. */
        private volatile ArrayList<Atome> ensemble;
        /**Indique si ce fil a terminé sa tâche de traitement. */
        private volatile boolean terminé = false;

        public FilsDistributeur(){}

        /**
         * Modifie le sous-ensemble que ce fil d'exécution traitera
         * @param sousEnsemble
         */
        public void changerEnsemble(ArrayList<Atome> sousEnsemble){
            ensemble = sousEnsemble;
        }

        @Override
        public void run() {
            System.out.println(Thread.currentThread().getName() + " est activé pour le calcul des forces.");
            while (true) {
                terminé = false;
                for (int i = 0; i < ensemble.size(); i++) {
                    Atome.ÉvaluerForces(ensemble.get(i));

                    
                }
                terminé = true;
                synchronized (this){
                    try {
                        wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    /**Indique si l'intégrateur fait usage des fils d'exécutions pour un calcul en paralèlle des forces. <code>true</code> par défaut.*/
    public static boolean FilsExécution = true;
    /**Indique le nombre de fils d'exécutions utilisés dans le calcul des forces. 
     * À titre de référence uniquement, <b>NE PAS MODIFIER DIRECTEMENT</b>. */
    public static int nbFils = 0;

    /**Liste des fils d'exécutions pour le calcul des forces. */
    private static Thread[] bouc;
    /**Liste des classes qui s'exécuteront sur les fils d'exécutions pour le calcul des forces. */
    private static FilsDistributeur[] fils;

    /**
     * Initialise la classe Intégrateur. À appeler avant d'utiliser les fils d'exécutions.
     * @param O - Liste des atomes contenus dans la simulation
     * @param nbFils - Nombre de fils d'exécutions à utiliser. 10 est la valeur par défaut reccomandée, mais faites vos propres tests.
     */
    public static void initialisation(ArrayList<Atome> O, int nbFils){
        bouc = new Thread[nbFils];
        fils = new FilsDistributeur[nbFils];

        for (int i = 0; i < bouc.length; i++) {
            if(bouc[i] != null){
                bouc[i].interrupt();
            }
        }
        ArrayList<ArrayList<Atome>> Op = new ArrayList<>();
        int L = O.size()/bouc.length;
        for (int i = 0; i < bouc.length; i++) {
            Op.add(new ArrayList<>());
            if(L*(i+2) < O.size()){
                for (int j = L*i; j < L*(i+1); j++) {
                    Op.get(i).add(O.get(j));
                }
            }else{
                for (int j = L*i; j < O.size(); j++) {
                    Op.get(i).add(O.get(j));
                }
            }
        }
        for (int i = 0; i < bouc.length; i++) {
            fils[i] = new FilsDistributeur();
            fils[i].changerEnsemble(Op.get(i));
            bouc[i] = new Thread(fils[i]);
            bouc[i].start();
        }
    }

    /**
     * Calcule les forces sur la liste d'atome donnée. Chaque atome conservera une référence à 
     * son résultat et à celui de ses doublets. Si <code>FilsExécution = true</code>, utilisera le nombre
     * de fils d'exécutions spécifié par <code>nbFils</code>.
     * @param O - Liste des atomes sur lesquels évaluer la force.
     */
    public static void calculerForces(ArrayList<Atome> O){
        if(FilsExécution){
            for (Atome o : O) {
                o.Force = new Vecteur3D(0);
                for (int j = 0; j < o.forceDoublet.size(); j++) {
                    o.forceDoublet.set(j,new Vecteur3D(0));
                }
            }

            for (int i = 0; i < bouc.length; i++) {
                synchronized (fils[i]){
                    fils[i].terminé = false;
                    fils[i].notify();
                }
               /*  synchronized (fils[i]){
                    fils[i].notify();
                    if(fils[i].terminé){
                        fils[i].terminé = false;
                    }
                } */
            }

            boolean terminé = false;
            long timer = System.currentTimeMillis();
            while (!terminé) {
                terminé = true;
                for (int i = 0; i < bouc.length; i++) {
                    synchronized (fils[i]){
                        terminé = terminé && fils[i].terminé;
                    }
                }
                if(!terminé && System.currentTimeMillis()-timer > 1000*O.size()/bouc.length){
                    terminé = true;
                    System.err.println("Les fils d'exécutions ont pris trop de temps. Sortie de l'attente.");
                }
            }
            for (Atome o : O) {
                Atome.ÉquilibrerDoublets(o);
            }
        }else{
            for (Atome o : O) {
                o.Force = new Vecteur3D(0);
                for (int j = 0; j < o.forceDoublet.size(); j++) {
                    o.forceDoublet.set(j,new Vecteur3D(0));
                }
            }
            for (Atome o : O) {
                Atome.ÉvaluerForces(o);
            }
            for (Atome o : O) {
                Atome.ÉquilibrerDoublets(o);
            }
            //for (Atome o : O) {
            //    App.ForceSytème.addi(o.Force);
            //    for (int i = 0; i < o.forceDoublet.size(); i++) {
            //        App.ForceSytème.addi(o.forceDoublet.get(i));
            //    }
            //}
        }
    }

    /**
     * Intègre selon la méthode d'Euler
     * @param O - la liste des Atomes à intégre
     * @param h - le delta temps
     */
    public static void IterEuler(ArrayList<Atome> O, double h){

        calculerForces(O);

        for (Atome o : O) {
            o.vélocité.addi( Vecteur3D.mult(o.Force,h/o.m) );
            o.position.addi( Vecteur3D.mult(o.vélocité, h) );

            if(o.forceDoublet != null){
                for (int i = 0; i < o.forceDoublet.size(); i++) {
                    o.vélDoublet.get(i).addi(Vecteur3D.mult(o.forceDoublet.get(i),h/Atome.mE));
                    o.positionDoublet.get(i).addi(Vecteur3D.mult(o.vélDoublet.get(i),h));
                }
            }

            o.ÉvaluerContraintes();
        }
    }

    /**
     * Intègre selon la méthode de Verlet à deux pas (Störmer-Verlet)
     * @param O - La liste des Atomes à intégrer
     * @param h - Le delta temps
     */
    public static void IterVerlet(ArrayList<Atome> O, double h){

        //TODO #5 Vincent Les doublets sont trop rapides

        for (Atome o : O) {
            o.prevPositionInit(h);
        }

        calculerForces(O);

        for (Atome o : O) {
            
            Vecteur3D pPos = o.position.copier();

            /* if(Math.abs(o.position.y) > (double)App.TailleY/(2.0*App.Zoom)){
                o.prevPosition= Vecteur3D.addi(o.prevPosition, new Vecteur3D(0,2*(o.position.y-o.prevPosition.y),0) );
            }
            if(Math.abs(o.position.x) > (double)App.TailleX/(2.0*App.Zoom)){
                o.prevPosition= Vecteur3D.addi(o.prevPosition, new Vecteur3D(2*(o.position.x-o.prevPosition.x),0,0) );
            }
            if(Math.abs(o.position.z) > (double)App.TailleZ/(2.0*App.Zoom)){
                o.prevPosition= Vecteur3D.addi(o.prevPosition, new Vecteur3D(0,0,2*(o.position.z-o.prevPosition.z)) );
            } */
            o.position = (Vecteur3D.addi(Vecteur3D.mult(o.position, 2.0), Vecteur3D.addi(Vecteur3D.mult(o.prevPosition, -1.0), Vecteur3D.mult(o.Force, h*h/o.m))));
            o.prevPosition = pPos.copier();

            o.vélocité = Vecteur3D.mult(Vecteur3D.sous(o.position, pPos),1.0/(2.0*h));
            
            if(o.forceDoublet != null){
                for (int i = 0; i < o.forceDoublet.size(); i++) {
                    pPos = o.positionDoublet.get(i).copier();
                    o.positionDoublet.set(i,(Vecteur3D.addi(Vecteur3D.mult(o.positionDoublet.get(i), 2.0), Vecteur3D.addi(Vecteur3D.mult(o.prevPosDoublet.get(i), -1.0), Vecteur3D.mult(o.forceDoublet.get(i), h*h/Atome.mE)))));
                    o.prevPosDoublet.set(i,pPos);

                    
                    o.vélDoublet.set(i,Vecteur3D.mult(Vecteur3D.sous(o.positionDoublet.get(i), pPos),1.0/(2.0*h)));
                }
               /*  for (int i = 0; i < o.forceDoublet.size(); i++) {
                    o.positionDoublet.get(i) = Vecteur3D.scale(Vecteur3D.normalize(o.positionDoublet.get(i)), o.rayonCovalent);
                    o.prevPosDoublet[i] = Vecteur3D.scale(Vecteur3D.normalize(o.prevPosDoublet[i]), o.rayonCovalent);
                    if(o.vélDoublet.get(i).longueur() > 0){
                        o.vélDoublet.get(i) = new Vecteur3D.sub(o.vélDoublet.get(i), Vecteur3D.scale( o.positionDoublet.get(i), Vecteur3D.scal(o.vélDoublet.get(i), o.positionDoublet.get(i))/(o.positionDoublet.get(i).longueur()*o.positionDoublet.get(i).longueur()) ) );
                        o.vélDoublet.get(i) = new Vecteur3D.mult(Vecteur3D.norm(o.vélDoublet.get(i)), Math.min(o.vélDoublet.get(i).length(), 10000000000000.0));
                    }
                } */
                
            }

            o.ÉvaluerContraintes();
            
        }
    }

    /**
     * Intègre selon la méthode de Verlet à un pas (Velocity Verlet)
     * @param O - La liste des atomes à intégrer
     * @param h - Le delta temps
     */
    public static void IterVerletV(ArrayList<Atome> O, double h){

        for (Atome o : O) {
            o.position.addi(Vecteur3D.addi(Vecteur3D.mult(o.vélocité,h), Vecteur3D.mult(o.Force, h*h/(2.0*o.m))));

            if(o.forceDoublet != null){
                for (int i = 0; i < o.forceDoublet.size(); i++) {
                    o.positionDoublet.get(i).addi(Vecteur3D.addi(Vecteur3D.mult(o.vélDoublet.get(i),h), Vecteur3D.mult(o.forceDoublet.get(i), h*h/(4.0*Atome.mE))));
                }
            }
            
        }

        Vecteur3D[] Force = new Vecteur3D[O.size()];
        Vecteur3D[][] eForce = new Vecteur3D[O.size()][];
        for (int i = 0; i < O.size(); i++) {
            eForce[i] = new Vecteur3D[O.get(i).forceDoublet.size()];
            for (int j = 0; j < O.get(i).forceDoublet.size(); j++) {
                eForce[i][j] = O.get(i).forceDoublet.get(j).copier();
            }

            Force[i] = O.get(i).Force;
        }

        calculerForces(O);

        for (int i = 0; i < O.size(); i++) {
            O.get(i).vélocité.addi(Vecteur3D.mult(Vecteur3D.addi(Force[i], O.get(i).Force), h/(2.0*O.get(i).m)));

            for (int j = 0; j < O.get(i).forceDoublet.size(); j++) {
                O.get(i).vélDoublet.get(j).addi(Vecteur3D.mult(Vecteur3D.addi(eForce[i][j], O.get(i).forceDoublet.get(j)), h/(4.0*Atome.mE)));
            }

            O.get(i).ÉvaluerContraintes();
        }
    }

    /**
     * Intègre selon la méthode de Verlet à un pas avec une évaluation de la vitesse à t+1/2 en premier (Velocity Verlet variant)
     * @param O - La liste des atomes à intégrer
     * @param h - Le delta temps
     */
    public static void IterVerletVB(ArrayList<Atome> O, double h){

        for (Atome o : O) {
            o.vélocité.addi(Vecteur3D.mult(o.Force, h/(2.0*o.m)));

            if(o.forceDoublet != null){
                for (int i = 0; i < o.forceDoublet.size(); i++) {
                    o.vélDoublet.get(i).addi(Vecteur3D.mult(o.forceDoublet.get(i), h/(4.0*Atome.mE)));
                }
            }
            o.ÉvaluerContraintes();
        }

        for (Atome o : O) {
            o.position.addi(Vecteur3D.addi(Vecteur3D.mult(o.vélocité,h), Vecteur3D.mult(o.Force, h*h/(2.0*o.m))));

            if(o.forceDoublet != null){
                for (int i = 0; i < o.forceDoublet.size(); i++) {
                    o.positionDoublet.get(i).addi(Vecteur3D.addi(Vecteur3D.mult(o.vélDoublet.get(i),h), Vecteur3D.mult(o.forceDoublet.get(i), h*h/(4.0*Atome.mE))));
                }
            }
            o.ÉvaluerContraintes();
        }

        calculerForces(O);

        for (Atome o : O) {
            o.vélocité.addi(Vecteur3D.mult( o.Force, h/(2.0*o.m)));

            if(o.forceDoublet != null){
                for (int i = 0; i < o.forceDoublet.size(); i++) {
                    o.vélDoublet.get(i).addi(Vecteur3D.mult( o.forceDoublet.get(i), h/(4.0*Atome.mE)));
                }
            }
        }
        
    }

    public static void IterVerletVBCD(ArrayList<Atome> O, double h){

        for (Atome o : O) {
            o.vélocité.addi(Vecteur3D.mult(o.Force, h/(2.0*o.m)));

            if(o.forceDoublet != null){
                for (int i = 0; i < o.forceDoublet.size(); i++) {
                    o.vélDoublet.get(i).addi(Vecteur3D.mult(o.forceDoublet.get(i), h/(4.0*Atome.mE)));
                }
            }
        }

        for (Atome o : O) {
            o.position.addi(Vecteur3D.addi(Vecteur3D.mult(o.vélocité,h), Vecteur3D.mult(o.Force, h*h/(2.0*o.m))));

            if(o.forceDoublet != null){
                for (int i = 0; i < o.forceDoublet.size(); i++) {
                    o.positionDoublet.get(i).addi(Vecteur3D.addi(Vecteur3D.mult(o.vélDoublet.get(i),h), Vecteur3D.mult(o.forceDoublet.get(i), h*h/(4.0*Atome.mE))));
                }
            }
        }

        calculerForces(O);

        for (Atome o : O) {
            o.vélocité.addi(Vecteur3D.mult( o.Force, h/(2.0*o.m)));

            if(o.forceDoublet != null){
                for (int i = 0; i < o.forceDoublet.size(); i++) {
                    o.vélDoublet.get(i).addi(Vecteur3D.mult( o.forceDoublet.get(i), h/(4.0*Atome.mE)));
                }
            }

            o.ÉvaluerContraintes();
        }
    }
    
    /**
     * Intègre selon la méthode Runge-Kutta d'ordre 4 (RK4). 
     * </p>Ne supporte pas les fils d'exécutions</p>
     * @param O - La liste des atomes à intégrer
     * @param h - Le delta temps
     */
    public static void IterRK4(ArrayList<Atome> O, double h){

        //TODO Vincent intégrer les doublets dans RK4 
        
        Vecteur3D[] K1v = new Vecteur3D[O.size()];
        Vecteur3D[] K1a = new Vecteur3D[O.size()];
        Vecteur3D[] K2v = new Vecteur3D[O.size()];
        Vecteur3D[] K2a = new Vecteur3D[O.size()];
        Vecteur3D[] K3v = new Vecteur3D[O.size()];
        Vecteur3D[] K3a = new Vecteur3D[O.size()];
        Vecteur3D[] K4v = new Vecteur3D[O.size()];
        Vecteur3D[] K4a = new Vecteur3D[O.size()];

        Vecteur3D[][] K1vd = new Vecteur3D[O.size()][];
        Vecteur3D[][] K1ad = new Vecteur3D[O.size()][];
        Vecteur3D[][] K2vd = new Vecteur3D[O.size()][];
        Vecteur3D[][] K2ad = new Vecteur3D[O.size()][];
        Vecteur3D[][] K3vd = new Vecteur3D[O.size()][];
        Vecteur3D[][] K3ad = new Vecteur3D[O.size()][];
        Vecteur3D[][] K4vd = new Vecteur3D[O.size()][];
        Vecteur3D[][] K4ad = new Vecteur3D[O.size()][];


        Atome[] oTmp = new Atome[O.size()];
        for (int i = 0; i < O.size(); i++) {
            oTmp[i] = O.get(i).copier(false);
        }

        /* k1v = v(x)
         * k1a = f(v)
         * k2v = v(x+k1vh/2)
         * k2a = f(v+k1ah/2)
         * k3v = v(x+k2vh/2)
         * k3a = f(v+k2ah/2)
         * k4v = v(x+k3vh)
         * k4a = f(v+k3ah)+
         * x = x+(h/6)(k1v+2k2v+2k3v+k4v)
         * v = v+(h/6)(k1a+2k2a+2k3a+k4a)
         */

         for (int i = 0; i < oTmp.length; i++) {
            //float K1x = s.v;
            K1v[i] = O.get(i).vélocité.copier();
            //float K1v = F(s);
            Atome.ÉvaluerForces(O.get(i));
            K1a[i] = Vecteur3D.mult(O.get(i).Force,1.0/O.get(i).m);

            K1vd[i] = (Vecteur3D[]) O.get(i).vélDoublet.clone();
            K1ad[i] = (Vecteur3D[]) O.get(i).forceDoublet.clone();

            for (int j = 0; j < K1ad[i].length; j++) {
                //float K1x = s.v;
                //K1vd[i][j] = O.get(i).vélDoublet.get(j).copy();
                //float K1v = F(s);
                K1ad[i][j] = Vecteur3D.mult(K1ad[i][j],1.0/(2.0*Atome.mE));
            }
        }
        
        for(int i = 0; i < O.size(); i++){
            //s2.x = s.x + (s.h/2f)*K1x;
            O.get(i).position.addi(Vecteur3D.mult(K1v[i],h/2.0));
            //s2.v = s.v + (s.h/2f)*K1v;
            Vecteur3D k2v = Vecteur3D.addi(O.get(i).vélocité, Vecteur3D.mult(K1a[i],h/2.0));
            //float K2x = s2.v;
            O.get(i).vélocité = k2v;
            K2v[i] = k2v;

            K2vd[i] = new Vecteur3D[oTmp[i].positionDoublet.size()];

            for (int j = 0; j < oTmp[i].positionDoublet.size(); j++) {
                //s2.x = s.x + (s.h/2f)*K1x;
                O.get(i).positionDoublet.get(j).addi(Vecteur3D.mult(K1vd[i][j],h/2.0));
                //float K1x = s.v;
                K2vd[i][j] = O.get(i).vélDoublet.get(j).copier();
            }
        }
        for (int i = 0; i < O.size(); i++) {
             //float K2v = F(s2);
             Atome.ÉvaluerForces(O.get(i));
             Vecteur3D k2a = Vecteur3D.mult(O.get(i).Force,1.0/O.get(i).m);
             K2a[i] = k2a;

             K2ad[i] = new Vecteur3D[oTmp[i].positionDoublet.size()];

             for (int j = 0; j < oTmp[i].positionDoublet.size(); j++) {
                K2ad[i][j] = Vecteur3D.mult( O.get(i).forceDoublet.get(j).copier(), 0.5/Atome.mE );
             }
        }

        for (int i = 0; i < O.size(); i++) {
            //Struct s3 = new Struct(s);
            O.get(i).copier(oTmp[i],false);
            //s3.x = s.x + (s.h/2f)*K2x;
            O.get(i).position.addi(Vecteur3D.mult(K2v[i],h/2.0));
            //s3.v = s.v + (s.h/2f)*K2v;
            Vecteur3D k3v = Vecteur3D.addi(O.get(i).vélocité, Vecteur3D.mult(K2a[i],h/2.0));
            //float K3x = s3.v;
            O.get(i).vélocité = k3v;
            K3v[i] = k3v;

            K3vd[i] = new Vecteur3D[oTmp[i].positionDoublet.size()];

            for (int j = 0; j < oTmp[i].positionDoublet.size(); j++) {
                //s2.x = s.x + (s.h/2f)*K1x;
                O.get(i).positionDoublet.get(j).addi(Vecteur3D.mult(K2vd[i][j],h/2.0));
                //float K1x = s.v;
                K3vd[i][j] = O.get(i).vélDoublet.get(j).copier();
            }
        }
        for (int i = 0; i < O.size(); i++) {
            //float K3v = F(s3);
            Atome.ÉvaluerForces(O.get(i));
            Vecteur3D k3a = Vecteur3D.mult(O.get(i).Force,1.0/O.get(i).m);
            K3a[i] = k3a;

            K3ad[i] = new Vecteur3D[oTmp[i].positionDoublet.size()];

            for (int j = 0; j < oTmp[i].positionDoublet.size(); j++) {
               K3ad[i][j] = Vecteur3D.mult( O.get(i).forceDoublet.get(j).copier(), 0.5/Atome.mE );
            }
        }

        for (int i = 0; i < O.size(); i++) {
            //Struct s4 = new Struct(s);
            O.get(i).copier(oTmp[i],false);
            //s4.x = s.x + s.h*K3x;
            O.get(i).position.addi(Vecteur3D.mult(K3v[i],h));
            //s4.v = s.v + s.h*K3v;
            Vecteur3D k4v = Vecteur3D.addi(O.get(i).vélocité, Vecteur3D.mult(K3a[i],h));
            //float K4x = s4.v;
            O.get(i).vélocité = k4v;
            K4v[i] = k4v;

            K4vd[i] = new Vecteur3D[oTmp[i].positionDoublet.size()];

            for (int j = 0; j < oTmp[i].positionDoublet.size(); j++) {
                //s2.x = s.x + (s.h/2f)*K1x;
                O.get(i).positionDoublet.get(j).addi(Vecteur3D.mult(K2vd[i][j],h/2.0));
                //float K1x = s.v;
                K4vd[i][j] = O.get(i).vélDoublet.get(j).copier();
            }
        }
        for (int i = 0; i < O.size(); i++) {
            //float K4v= F(s4);
            Atome.ÉvaluerForces(O.get(i));
            Vecteur3D k4a = Vecteur3D.mult(O.get(i).Force,1.0/O.get(i).m);
            K4a[i] = k4a;

            K4ad[i] = new Vecteur3D[oTmp[i].positionDoublet.size()];

            for (int j = 0; j < oTmp[i].positionDoublet.size(); j++) {
               K4ad[i][j] = Vecteur3D.mult( O.get(i).forceDoublet.get(j).copier(), 0.5/Atome.mE );
            }
        }

        for (int i = 0; i < O.size(); i++) {
            K2v[i].mult(2.0);
            K2a[i].mult(2.0);
            K3v[i].mult(2.0);
            K3a[i].mult(2.0);
            O.get(i).copier(oTmp[i],false);
            //s.x = s.x + (s.h/6f)*(K1x + 2f*K2x + 2f*K3x + K4x);
            O.get(i).position.addi(Vecteur3D.mult(Vecteur3D.addi(K1v[i], Vecteur3D.addi(K2v[i], Vecteur3D.addi(K3v[i], K4v[i]))), h/6.0));
            //s.v = (s.h/6f)*(K1v + 2f*K2v + 2f*K3v + K4v);
            O.get(i).vélocité.addi(Vecteur3D.mult(Vecteur3D.addi(K1a[i], Vecteur3D.addi(K2a[i], Vecteur3D.addi(K3a[i], K4a[i]))), h/6.0));
            
            for (int j = 0; j < O.get(i).positionDoublet.size(); j++) {
                K2vd[i][j].mult(2.0);
                K2ad[i][j].mult(2.0);
                K3vd[i][j].mult(2.0);
                K3ad[i][j].mult(2.0);
                //s.x = s.x + (s.h/6f)*(K1x + 2f*K2x + 2f*K3x + K4x);
                O.get(i).positionDoublet.get(j).addi(Vecteur3D.mult(Vecteur3D.addi(K1vd[i][j], Vecteur3D.addi(K2vd[i][j], Vecteur3D.addi(K3vd[i][j], K4vd[i][j]))), h/6.0));
                //O.get(i).positionDoublet.get(j).add(Vecteur3f.scale(K1vd[i][j], h));
                //s.v = (s.h/6f)*(K1v + 2f*K2v + 2f*K3v + K4v);
                O.get(i).vélDoublet.get(j).addi(Vecteur3D.mult(Vecteur3D.addi(K1ad[i][j], Vecteur3D.addi(K2ad[i][j], Vecteur3D.addi(K3ad[i][j], K4ad[i][j]))), h/6.0));
                //O.get(i).vélDoublet.get(j).add(Vecteur3f.scale(K1ad[i][j], h));
            }
            
            O.get(i).ÉvaluerContraintes();
        }
    }
}
