package com.MiniLabo.prototype;

import java.util.ArrayList;

public class Intégrateur {
    public static void IterEuler(ArrayList<ObjetPhysique> O, double h, int TailleX, int TailleY, float Zoom){
        for (ObjetPhysique o : O) {
            o.changerForce(o.ÉvaluerForces(o,TailleX, TailleY, Zoom));
        }

        for (ObjetPhysique o : O) {
            o.ajouterVitesse(Vecteur2f.scale(o.avoirForce(),h/o.avoirMasse()));
            o.ajouterPosition(Vecteur2f.scale(o.avoirVitesse(), h));
        }
    }

    public static void IterVerlet(ArrayList<ObjetPhysique> O, double h, int TailleX, int TailleY, float Zoom){
        for (ObjetPhysique o : O) {
            o.prevPositionInit(h);
            o.changerForce(o.ÉvaluerForces(o,TailleX, TailleY, Zoom));
        }

        for (ObjetPhysique o : O) {
            Vecteur2f prevPos = o.avoirPosition();
            o.changerPosition(Vecteur2f.add(Vecteur2f.scale(o.avoirPosition(), 2.0), Vecteur2f.add(Vecteur2f.scale(o.avoirPrevPosition(), -1.0), Vecteur2f.scale(o.avoirForce(), h*h/o.avoirMasse()))));
            o.changerPrevPosition(prevPos);
        }
    }

    public static void IterVerletV(ArrayList<ObjetPhysique> O, double h, int TailleX, int TailleY, float Zoom){

        for (ObjetPhysique o : O) {
            o.ajouterPosition(Vecteur2f.add(Vecteur2f.scale(o.avoirVitesse(),h), Vecteur2f.scale(o.avoirForce(), h*h/(2.0*o.avoirMasse()))));
        }

        for (ObjetPhysique o : O) {
            Vecteur2f force = o.avoirForce();
            o.changerForce(o.ÉvaluerForces(o,TailleX, TailleY, Zoom));
            o.ajouterVitesse(Vecteur2f.scale(Vecteur2f.add(force, o.avoirForce()), h/(2.0*o.avoirMasse())));
        }
    }

    public static void IterVerletVB(ArrayList<ObjetPhysique> O, double h, int TailleX, int TailleY, float Zoom){

        for (ObjetPhysique o : O) {
            o.ajouterVitesse(Vecteur2f.scale(o.avoirForce(), h/(2.0*o.avoirMasse())));
        }

        for (ObjetPhysique o : O) {
            o.ajouterPosition(Vecteur2f.add(Vecteur2f.scale(o.avoirVitesse(),h), Vecteur2f.scale(o.avoirForce(), h*h/(2.0*o.avoirMasse()))));
        }

        for (ObjetPhysique o : O) {
            o.changerForce(o.ÉvaluerForces(o,TailleX, TailleY, Zoom));
            o.ajouterVitesse(Vecteur2f.scale( o.avoirForce(), h/(2.0*o.avoirMasse())));
        }
    }

    public static void IterRK4(ArrayList<ObjetPhysique> O, double h, int TailleX, int TailleY, float Zoom){
        ArrayList<ObjetPhysique> K1o = new ArrayList<>();
        ArrayList<ObjetPhysique> K2o = new ArrayList<>();
        ArrayList<ObjetPhysique> K3o = new ArrayList<>();
        ArrayList<ObjetPhysique> K4o = new ArrayList<>();

        for (ObjetPhysique o : O) {
            ObjetPhysique k1 = o.copy();
            k1.changerVitesse(o.avoirVitesse());
            k1.changerForce(k1.ÉvaluerForces(o, TailleX, TailleY, Zoom));
            K1o.add(k1);
        }

        for (int i = 0; i < O.size(); i++) {
            //Struct s2 = new Struct(s);
            ObjetPhysique k2 = O.get(i).copy();
            //s2.x = s.x + (s.h/2f)*K1x;
            k2.changerPosition(Vecteur2f.add( O.get(i).avoirPosition(), Vecteur2f.scale(K1o.get(i).avoirVitesse(), h/2.0)));
            //s2.v = s.v + (s.h/2f)*K1v;
            //float K2x = s2.v;
            k2.changerVitesse(Vecteur2f.add( O.get(i).avoirVitesse(), Vecteur2f.scale(K1o.get(i).avoirForce(), h/(2.0*k2.avoirMasse()))));
            //float K2v = F(s2);
            //k2.changerForce(k2.ÉvaluerForces(k2, TailleX, TailleY, Zoom));
            K2o.add(k2);
        }
        for (ObjetPhysique k2 : K2o) {
            k2.changerForce(k2.ÉvaluerForces(k2, TailleX, TailleY, Zoom));
        }

        for (int i = 0; i < O.size(); i++) {
            //Struct s3 = new Struct(s);
            ObjetPhysique k3 = O.get(i).copy();
            //s3.x = s.x + (s.h/2f)*K2x;
            k3.changerPosition(Vecteur2f.add( O.get(i).avoirPosition(), Vecteur2f.scale(K2o.get(i).avoirVitesse(), h/2.0)));
            //s3.v = s.v + (s.h/2f)*K2v;
            //float K3x = s3.v;
            k3.changerVitesse(Vecteur2f.add( O.get(i).avoirVitesse(), Vecteur2f.scale(K2o.get(i).avoirForce(), h/(2.0*k3.avoirMasse()))));
            //float K3v = F(s3);
            //k3.changerForce(O.get(i).ÉvaluerForces(k3, TailleX, TailleY, Zoom));
            K3o.add(k3);
        }
        for (ObjetPhysique k3 : K3o) {
            k3.changerForce(k3.ÉvaluerForces(k3, TailleX, TailleY, Zoom));
        }


        for (int i = 0; i < O.size(); i++) {
            //Struct s4 = new Struct(s);
            ObjetPhysique k4 = O.get(i).copy();
            //s4.x = s.x + s.h*K3x;
            k4.changerPosition(Vecteur2f.add( O.get(i).avoirPosition(), Vecteur2f.scale(K3o.get(i).avoirVitesse(), h)));
            //s4.v = s.v + s.h*K3v;
            //float K4x = s4.v;
            k4.changerVitesse(Vecteur2f.add( O.get(i).avoirVitesse(), Vecteur2f.scale(K3o.get(i).avoirForce(), h/(k4.avoirMasse()))));
            //float K4v= F(s4);
            k4.changerForce(O.get(i).ÉvaluerForces(k4, TailleX, TailleY, Zoom));
            K4o.add(k4);
        }

        for (int i = 0; i < O.size(); i++) {
            //s.x = s.x + (s.h/6f)*(K1x + 2f*K2x + 2f*K3x + K4x);
            //O.get(i).ajouterPosition(Vecteur2f.scale(Vecteur2f.add(K1o.get(i).avoirVitesse(), Vecteur2f.add(K2o.get(i).avoirVitesse(), Vecteur2f.add(K2o.get(i).avoirVitesse(), K2o.get(i).avoirVitesse()))), h/6.0));
            O.get(i).ajouterPosition(Vecteur2f.scale(K3o.get(i).avoirVitesse(), h));
            //s.v = s.v + (s.h/6f)*(K1v + 2f*K2v + 2f*K3v + K4v);
            //O.get(i).ajouterVitesse(Vecteur2f.scale(Vecteur2f.add(K1o.get(i).avoirForce(), Vecteur2f.add(K2o.get(i).avoirForce(), Vecteur2f.add(K2o.get(i).avoirForce(), K2o.get(i).avoirForce()))), h/(6.0*O.get(i).avoirMasse())));
            O.get(i).changerVitesse(K3o.get(i).avoirVitesse());//(Vecteur2f.scale(K2o.get(i).avoirForce(),h/(O.get(i).avoirMasse())));
        }

        //s.E = E(s);

        //return s;
    }
}
