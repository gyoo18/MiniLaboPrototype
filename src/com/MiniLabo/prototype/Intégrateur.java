package com.MiniLabo.prototype;

import java.util.ArrayList;

public class Intégrateur {
    public static void IterEuler(ArrayList<ObjetPhysique> O, double h, int TailleX, int TailleY, int TailleZ, float Zoom){
        for (ObjetPhysique o : O) {
            o.changerForce(o.ÉvaluerForces(o,TailleX, TailleY,TailleZ, Zoom));
        }

        for (ObjetPhysique o : O) {
            o.ajouterVitesse(Vecteur3f.scale(o.avoirForce(),h/o.avoirMasse()));
            o.ajouterPosition(Vecteur3f.scale(o.avoirVitesse(), h));
        }
    }

    public static void IterVerlet(ArrayList<ObjetPhysique> O, double h, int TailleX, int TailleY, int TailleZ, float Zoom){
        for (ObjetPhysique o : O) {
            o.prevPositionInit(h);
            o.changerForce(o.ÉvaluerForces(o,TailleX, TailleY,TailleZ, Zoom));
        }

        for (ObjetPhysique o : O) {
            Vecteur3f prevPos = o.avoirPosition();
            o.changerPosition(Vecteur3f.add(Vecteur3f.scale(o.avoirPosition(), 2.0), Vecteur3f.add(Vecteur3f.scale(o.avoirPrevPosition(), -1.0), Vecteur3f.scale(o.avoirForce(), h*h/o.avoirMasse()))));
            o.changerPrevPosition(prevPos);
        }
    }

    public static void IterVerletV(ArrayList<ObjetPhysique> O, double h, int TailleX, int TailleY, int TailleZ, float Zoom){

        for (ObjetPhysique o : O) {
            o.ajouterPosition(Vecteur3f.add(Vecteur3f.scale(o.avoirVitesse(),h), Vecteur3f.scale(o.avoirForce(), h*h/(2.0*o.avoirMasse()))));
        }

        for (ObjetPhysique o : O) {
            Vecteur3f force = o.avoirForce();
            o.changerForce(o.ÉvaluerForces(o,TailleX, TailleY,TailleZ, Zoom));
            o.ajouterVitesse(Vecteur3f.scale(Vecteur3f.add(force, o.avoirForce()), h/(2.0*o.avoirMasse())));
        }
    }

    public static void IterVerletVB(ArrayList<ObjetPhysique> O, double h, int TailleX, int TailleY, int TailleZ, float Zoom){

        for (ObjetPhysique o : O) {
            o.ajouterVitesse(Vecteur3f.scale(o.avoirForce(), h/(2.0*o.avoirMasse())));
        }

        for (ObjetPhysique o : O) {
            o.ajouterPosition(Vecteur3f.add(Vecteur3f.scale(o.avoirVitesse(),h), Vecteur3f.scale(o.avoirForce(), h*h/(2.0*o.avoirMasse()))));
        }

        for (ObjetPhysique o : O) {
            o.changerForce(o.ÉvaluerForces(o,TailleX, TailleY,TailleZ, Zoom));
            o.ajouterVitesse(Vecteur3f.scale( o.avoirForce(), h/(2.0*o.avoirMasse())));
        }
    }

    public static void IterRK4(ArrayList<ObjetPhysique> O, double h, int TailleX, int TailleY, int TailleZ,  float Zoom){
        ArrayList<ObjetPhysique> K1o = new ArrayList<>();
        ArrayList<ObjetPhysique> K2o = new ArrayList<>();
        ArrayList<ObjetPhysique> K3o = new ArrayList<>();
        ArrayList<ObjetPhysique> K4o = new ArrayList<>();

        for (ObjetPhysique o : O) {
            ObjetPhysique k1 = o.copy();
            k1.changerVitesse(o.avoirVitesse());
            k1.changerForce(k1.ÉvaluerForces(o, TailleX, TailleY,TailleZ, Zoom));
            K1o.add(k1);
        }

        for (int i = 0; i < O.size(); i++) {
            //Struct s2 = new Struct(s);
            ObjetPhysique k2 = O.get(i).copy();
            //s2.x = s.x + (s.h/2f)*K1x;
            k2.changerPosition(Vecteur3f.add( O.get(i).avoirPosition(), Vecteur3f.scale(K1o.get(i).avoirVitesse(), h/2.0)));
            //s2.v = s.v + (s.h/2f)*K1v;
            //float K2x = s2.v;
            k2.changerVitesse(Vecteur3f.add( O.get(i).avoirVitesse(), Vecteur3f.scale(K1o.get(i).avoirForce(), h/(2.0*k2.avoirMasse()))));
            //float K2v = F(s2);
            //k2.changerForce(k2.ÉvaluerForces(k2, TailleX, TailleY, Zoom));
            K2o.add(k2);
        }
        for (ObjetPhysique k2 : K2o) {
            k2.changerForce(k2.ÉvaluerForces(k2, TailleX, TailleY,TailleZ, Zoom));
        }

        for (int i = 0; i < O.size(); i++) {
            //Struct s3 = new Struct(s);
            ObjetPhysique k3 = O.get(i).copy();
            //s3.x = s.x + (s.h/2f)*K2x;
            k3.changerPosition(Vecteur3f.add( O.get(i).avoirPosition(), Vecteur3f.scale(K2o.get(i).avoirVitesse(), h/2.0)));
            //s3.v = s.v + (s.h/2f)*K2v;
            //float K3x = s3.v;
            k3.changerVitesse(Vecteur3f.add( O.get(i).avoirVitesse(), Vecteur3f.scale(K2o.get(i).avoirForce(), h/(2.0*k3.avoirMasse()))));
            //float K3v = F(s3);
            //k3.changerForce(O.get(i).ÉvaluerForces(k3, TailleX, TailleY, Zoom));
            K3o.add(k3);
        }
        for (ObjetPhysique k3 : K3o) {
            k3.changerForce(k3.ÉvaluerForces(k3, TailleX, TailleY,TailleZ, Zoom));
        }


        for (int i = 0; i < O.size(); i++) {
            //Struct s4 = new Struct(s);
            ObjetPhysique k4 = O.get(i).copy();
            //s4.x = s.x + s.h*K3x;
            k4.changerPosition(Vecteur3f.add( O.get(i).avoirPosition(), Vecteur3f.scale(K3o.get(i).avoirVitesse(), h)));
            //s4.v = s.v + s.h*K3v;
            //float K4x = s4.v;
            k4.changerVitesse(Vecteur3f.add( O.get(i).avoirVitesse(), Vecteur3f.scale(K3o.get(i).avoirForce(), h/(k4.avoirMasse()))));
            //float K4v= F(s4);
            k4.changerForce(O.get(i).ÉvaluerForces(k4, TailleX, TailleY,TailleZ, Zoom));
            K4o.add(k4);
        }

        for (int i = 0; i < O.size(); i++) {
            //s.x = s.x + (s.h/6f)*(K1x + 2f*K2x + 2f*K3x + K4x);
            //O.get(i).ajouterPosition(Vecteur3f.scale(Vecteur3f.add(K1o.get(i).avoirVitesse(), Vecteur3f.add(K2o.get(i).avoirVitesse(), Vecteur3f.add(K2o.get(i).avoirVitesse(), K2o.get(i).avoirVitesse()))), h/6.0));
            O.get(i).ajouterPosition(Vecteur3f.scale(K3o.get(i).avoirVitesse(), h));
            //s.v = s.v + (s.h/6f)*(K1v + 2f*K2v + 2f*K3v + K4v);
            //O.get(i).ajouterVitesse(Vecteur3f.scale(Vecteur3f.add(K1o.get(i).avoirForce(), Vecteur3f.add(K2o.get(i).avoirForce(), Vecteur3f.add(K2o.get(i).avoirForce(), K2o.get(i).avoirForce()))), h/(6.0*O.get(i).avoirMasse())));
            O.get(i).changerVitesse(K3o.get(i).avoirVitesse());//(Vecteur3f.scale(K2o.get(i).avoirForce(),h/(O.get(i).avoirMasse())));
        }

        //s.E = E(s);

        //return s;
    }
}
