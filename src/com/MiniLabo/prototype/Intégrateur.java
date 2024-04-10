package com.MiniLabo.prototype;

import java.util.ArrayList;

public class Intégrateur {
    public static void IterEuler(ArrayList<Atome> O, double h, int TailleX, int TailleY, int TailleZ, float Zoom){
        for (Atome o : O) {
            o.changerForce(o.ÉvaluerForces(o,TailleX, TailleY,TailleZ, Zoom));
        }

        for (Atome o : O) {
            o.ajouterVitesse(Vecteur3f.scale(o.avoirForce(),h/o.avoirMasse()));
            o.ajouterPosition(Vecteur3f.scale(o.avoirVitesse(), h));

            /*Vecteur3f[] doubletsForces = o.avoirDoubletForce();
            if(doubletsForces != null){
                for (int i = 0; i < doubletsForces.length; i++) {
                    o.avoirDoubletVél()[i].add(Vecteur3f.scale(doubletsForces[i],h/Atome.mE));
                    o.avoirDoubletPos()[i].add(Vecteur3f.scale(o.avoirDoubletVél()[i],h/Atome.mE));
                }
            }*/
        }
    }

    public static void IterVerlet(ArrayList<Atome> O, double h, int TailleX, int TailleY, int TailleZ, float Zoom){
        for (Atome o : O) {
            o.prevPositionInit(h);
            o.changerForce(o.ÉvaluerForces(o,TailleX, TailleY,TailleZ, Zoom));
        }

        for (Atome o : O) {
            Vecteur3f prevPos = o.avoirPosition();
            o.changerPosition(Vecteur3f.add(Vecteur3f.scale(o.avoirPosition(), 2.0), Vecteur3f.add(Vecteur3f.scale(o.avoirPrevPosition(), -1.0), Vecteur3f.scale(o.avoirForce(), h*h/o.avoirMasse()))));
            o.changerPrevPosition(prevPos);
        }
    }


    public static void IterVerletV(ArrayList<Atome> O, double h, int TailleX, int TailleY, int TailleZ, float Zoom){

        for (Atome o : O) {
            o.ajouterPosition(Vecteur3f.add(Vecteur3f.scale(o.avoirVitesse(),h), Vecteur3f.scale(o.avoirForce(), h*h/(2.0*o.avoirMasse()))));
        }

        for (Atome o : O) {
            Vecteur3f force = o.avoirForce();
            o.changerForce(o.ÉvaluerForces(o,TailleX, TailleY,TailleZ, Zoom));
            o.ajouterVitesse(Vecteur3f.scale(Vecteur3f.add(force, o.avoirForce()), h/(2.0*o.avoirMasse())));
        }
    }

    public static void IterVerletVB(ArrayList<Atome> O, double h, int TailleX, int TailleY, int TailleZ, float Zoom){

        for (Atome o : O) {
            o.ajouterVitesse(Vecteur3f.scale(o.avoirForce(), h/(2.0*o.avoirMasse())));
        }

        for (Atome o : O) {
            o.ajouterPosition(Vecteur3f.add(Vecteur3f.scale(o.avoirVitesse(),h), Vecteur3f.scale(o.avoirForce(), h*h/(2.0*o.avoirMasse()))));
        }

        for (Atome o : O) {
            o.changerForce(o.ÉvaluerForces(o,TailleX, TailleY,TailleZ, Zoom));
            o.ajouterVitesse(Vecteur3f.scale( o.avoirForce(), h/(2.0*o.avoirMasse())));
        }
    }

    public static void IterRK4(ArrayList<Atome> O, double h, int TailleX, int TailleY, int TailleZ,  float Zoom){
        
        ArrayList<Vecteur3f> K1v = new ArrayList<>();
        ArrayList<Vecteur3f> K1a = new ArrayList<>();
        ArrayList<Vecteur3f> K2v = new ArrayList<>();
        ArrayList<Vecteur3f> K2a = new ArrayList<>();
        ArrayList<Vecteur3f> K3v = new ArrayList<>();
        ArrayList<Vecteur3f> K3a = new ArrayList<>();
        ArrayList<Vecteur3f> K4v = new ArrayList<>();
        ArrayList<Vecteur3f> K4a = new ArrayList<>();


        ArrayList<Atome> oTmp = new ArrayList<>();
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

         for (Atome o : O) {
            //float K1x = s.v;
            Vecteur3f k1v = o.avoirVitesse().copy();
            //float K1v = F(s);
            Vecteur3f k1a = Vecteur3f.scale(o.ÉvaluerForces(o, TailleX, TailleY, TailleZ, Zoom),1.0/o.avoirMasse());

            K1v.add(k1v);
            K1a.add(k1a);
        }
        
        for(int i = 0; i < O.size(); i++){
            //s2.x = s.x + (s.h/2f)*K1x;
            O.get(i).ajouterPosition(Vecteur3f.scale(K1v.get(i),h/2.0));
            //s2.v = s.v + (s.h/2f)*K1v;
            Vecteur3f k2v = Vecteur3f.add(O.get(i).avoirVitesse(), Vecteur3f.scale(K1a.get(i),h/2.0));
            //float K2x = s2.v;
            O.get(i).changerVitesse(k2v);
            K2v.add(k2v);
        }
        for (int i = 0; i < O.size(); i++) {
             //float K2v = F(s2);
             Vecteur3f k2a = Vecteur3f.scale(O.get(i).ÉvaluerForces(O.get(i), TailleX, TailleY, TailleZ, Zoom),1.0/O.get(i).avoirMasse());
             K2a.add(k2a);
        }


        for (int i = 0; i < O.size(); i++) {
            //Struct s3 = new Struct(s);
            O.get(i).copy(oTmp.get(i));
            //s3.x = s.x + (s.h/2f)*K2x;
            O.get(i).ajouterPosition(Vecteur3f.scale(K2v.get(i),h/2.0));
            //s3.v = s.v + (s.h/2f)*K2v;
            Vecteur3f k3v = Vecteur3f.add(O.get(i).avoirVitesse(), Vecteur3f.scale(K2a.get(i),h/2.0));
            //float K3x = s3.v;
            O.get(i).changerVitesse(k3v);
            K3v.add(k3v);
        }
        for (int i = 0; i < O.size(); i++) {
            //float K3v = F(s3);
            Vecteur3f k3a = Vecteur3f.scale(O.get(i).ÉvaluerForces(O.get(i), TailleX, TailleY, TailleZ, Zoom),1.0/O.get(i).avoirMasse());
            K3a.add(k3a);
        }

        for (int i = 0; i < O.size(); i++) {
            //Struct s4 = new Struct(s);
            O.get(i).copy(oTmp.get(i));
            //s4.x = s.x + s.h*K3x;
            O.get(i).ajouterPosition(Vecteur3f.scale(K3v.get(i),h));
            //s4.v = s.v + s.h*K3v;
            Vecteur3f k4v = Vecteur3f.add(O.get(i).avoirVitesse(), Vecteur3f.scale(K3a.get(i),h));
            //float K4x = s4.v;
            O.get(i).changerVitesse(k4v);
            K4v.add(k4v);
        }
        for (int i = 0; i < O.size(); i++) {
            //float K4v= F(s4);
            Vecteur3f k4a = Vecteur3f.scale(O.get(i).ÉvaluerForces(O.get(i), TailleX, TailleY, TailleZ, Zoom),1.0/O.get(i).avoirMasse());
            K4a.add(k4a);
        }

        for (int i = 0; i < O.size(); i++) {
            K2v.get(i).scale(2.0);
            K2a.get(i).scale(2.0);
            K3v.get(i).scale(2.0);
            K3a.get(i).scale(2.0);
            O.get(i).copy(oTmp.get(i));
            //s.x = s.x + (s.h/6f)*(K1x + 2f*K2x + 2f*K3x + K4x);
            O.get(i).ajouterPosition(Vecteur3f.scale(Vecteur3f.add(K1v.get(i), Vecteur3f.add(K2v.get(i), Vecteur3f.add(K3v.get(i), K4v.get(i)))), h/6.0));
            //O.get(i).ajouterPosition(Vecteur3f.scale(K4v.get(i), h));
            //s.v = (s.h/6f)*(K1v + 2f*K2v + 2f*K3v + K4v);
            O.get(i).ajouterVitesse(Vecteur3f.scale(Vecteur3f.add(K1a.get(i), Vecteur3f.add(K2a.get(i), Vecteur3f.add(K3a.get(i), K4a.get(i)))), h/6.0));
            //O.get(i).ajouterVitesse(Vecteur3f.scale(K4a.get(i), h));
            O.get(i).ÉvaluerForces(O.get(i), TailleX, TailleY, TailleZ, Zoom);
        }
    }
    
    public static void IterVerletVBC(ArrayList<Atome> O, double h, int TailleX, int TailleY, int TailleZ, float Zoom){
         for (Atome o : O) {
            o.ajouterVitesse(Vecteur3f.scale(o.avoirForce(), h/(2.0*o.avoirMasse())));
        }

        for (Atome o : O) {
            o.ajouterPosition(Vecteur3f.add(Vecteur3f.scale(o.avoirVitesse(),h), Vecteur3f.scale(o.avoirForce(), h*h/(2.0*o.avoirMasse()))));
        }

        for (Atome o : O) {
            o.changerForce(o.ÉvaluerForces(o,TailleX, TailleY,TailleZ, Zoom));
            o.ajouterVitesse(Vecteur3f.scale( o.avoirForce(), h/(2.0*o.avoirMasse())));
        }

    }
}
