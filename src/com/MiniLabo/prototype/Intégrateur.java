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

        ArrayList<Vecteur2f> K1v = new ArrayList<>();
        ArrayList<Vecteur2f> K1a = new ArrayList<>();
        ArrayList<Vecteur2f> K2v = new ArrayList<>();
        ArrayList<Vecteur2f> K2a = new ArrayList<>();
        ArrayList<Vecteur2f> K3v = new ArrayList<>();
        ArrayList<Vecteur2f> K3a = new ArrayList<>();
        ArrayList<Vecteur2f> K4v = new ArrayList<>();
        ArrayList<Vecteur2f> K4a = new ArrayList<>();


        ArrayList<ObjetPhysique> oTmp = new ArrayList<>();
        for (int i = 0; i < O.size(); i++) {
            oTmp.add(O.get(i).copy());
        }

        /* k1v = v(x)
         * k1a = f(v)
         * k2v = v(x+k1vh/2)
         * k2a = f(v+k1ah/2)
         * k3v = v(x+k2vh/2)
         * k3a = f(v+k2ah/2)
         * k4v = v(x+k3vh)
         * k4a = f(v+k3ah)
         * x = x+(h/6)(k1v+2k2v+2k3v+k4v)
         * v = v+(h/6)(k1a+2k2a+2k3a+k4a)
         */

        for (ObjetPhysique o : O) {
            //float K1x = s.v;
            Vecteur2f k1v = o.avoirVitesse().copy();
            //float K1v = F(s);
            Vecteur2f k1a = Vecteur2f.scale(o.ÉvaluerForces(o, TailleX, TailleY, Zoom),1.0/o.avoirMasse());

            K1v.add(k1v);
            K1a.add(k1a);
        }

        for(int i = 0; i < O.size(); i++){
            //s2.x = s.x + (s.h/2f)*K1x;
            O.get(i).ajouterPosition(Vecteur2f.scale(K1v.get(i),h/2.0));
            //s2.v = s.v + (s.h/2f)*K1v;
            Vecteur2f k2v = Vecteur2f.add(O.get(i).avoirVitesse(), Vecteur2f.scale(K1a.get(i),h/2.0));
            //float K2x = s2.v;
            O.get(i).changerVitesse(k2v);
            K2v.add(k2v);
        }
        for (int i = 0; i < O.size(); i++) {
            //float K2v = F(s2);
            Vecteur2f k2a = Vecteur2f.scale(O.get(i).ÉvaluerForces(O.get(i), TailleX, TailleY, Zoom),1.0/O.get(i).avoirMasse());
            K2a.add(k2a);
        }

        for (int i = 0; i < O.size(); i++) {
            //Struct s3 = new Struct(s);
            O.get(i).copy(oTmp.get(i));
            //s3.x = s.x + (s.h/2f)*K2x;
            O.get(i).ajouterPosition(Vecteur2f.scale(K2v.get(i),h/2.0));
            //s3.v = s.v + (s.h/2f)*K2v;
            Vecteur2f k3v = Vecteur2f.add(O.get(i).avoirVitesse(), Vecteur2f.scale(K2a.get(i),h/2.0));
            //float K3x = s3.v;
            O.get(i).changerVitesse(k3v);
            K3v.add(k3v);
        }
        for (int i = 0; i < O.size(); i++) {
            //float K3v = F(s3);
            Vecteur2f k3a = Vecteur2f.scale(O.get(i).ÉvaluerForces(O.get(i), TailleX, TailleY, Zoom),1.0/O.get(i).avoirMasse());
            K3a.add(k3a);
        }

        for (int i = 0; i < O.size(); i++) {
            //Struct s4 = new Struct(s);
            O.get(i).copy(oTmp.get(i));
            //s4.x = s.x + s.h*K3x;
            O.get(i).ajouterPosition(Vecteur2f.scale(K3v.get(i),h));
            //s4.v = s.v + s.h*K3v;
            Vecteur2f k4v = Vecteur2f.add(O.get(i).avoirVitesse(), Vecteur2f.scale(K3a.get(i),h));
            //float K4x = s4.v;
            O.get(i).changerVitesse(k4v);
            K4v.add(k4v);
        }
        for (int i = 0; i < O.size(); i++) {
            //float K4v= F(s4);
            Vecteur2f k4a = Vecteur2f.scale(O.get(i).ÉvaluerForces(O.get(i), TailleX, TailleY, Zoom),1.0/O.get(i).avoirMasse());
            K4a.add(k4a);
        }

        for (int i = 0; i < O.size(); i++) {
            K2v.get(i).scale(2.0);
            K2a.get(i).scale(2.0);
            K3v.get(i).scale(2.0);
            K3a.get(i).scale(2.0);
            O.get(i).copy(oTmp.get(i));
            //s.x = s.x + (s.h/6f)*(K1x + 2f*K2x + 2f*K3x + K4x);
            O.get(i).ajouterPosition(Vecteur2f.scale(Vecteur2f.add(K1v.get(i), Vecteur2f.add(K2v.get(i), Vecteur2f.add(K3v.get(i), K4v.get(i)))), h/6.0));
            //s.v = (s.h/6f)*(K1v + 2f*K2v + 2f*K3v + K4v);
            O.get(i).ajouterVitesse(Vecteur2f.scale(Vecteur2f.add(K1a.get(i), Vecteur2f.add(K2a.get(i), Vecteur2f.add(K3a.get(i), K4a.get(i)))), h/6.0));
            O.get(i).ÉvaluerForces(O.get(i), TailleX, TailleY, Zoom);
        }
    }
}
