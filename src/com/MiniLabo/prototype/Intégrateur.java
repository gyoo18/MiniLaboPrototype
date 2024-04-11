package com.MiniLabo.prototype;

import java.util.ArrayList;

public class Intégrateur {

    public static void IterEuler(ArrayList<Atome> O, double h){
        for (Atome o : O) {
            Atome.ÉvaluerForces(o);
        }

        for (Atome o : O) {
            o.vélocité.add( Vecteur3f.scale(o.Force,h/o.m) );
            o.position.add( Vecteur3f.scale(o.vélocité, h) );

            if(o.forceDoublet != null){
                for (int i = 0; i < o.forceDoublet.length; i++) {
                    o.vélDoublet[i].add(Vecteur3f.scale(o.forceDoublet[i],h/Atome.mE));
                    o.positionDoublet[i].add(Vecteur3f.scale(o.vélDoublet[i],h));
                }
            }

            o.ÉvaluerContraintes();
        }
    }

    public static void IterVerlet(ArrayList<Atome> O, double h){

        //TODO #5 Vincent Les doublets sont trop rapides

        for (Atome o : O) {
            o.prevPositionInit(h);
            Atome.ÉvaluerForces(o);
        }

        for (Atome o : O) {
            Vecteur3f pPos = o.position.copier();
            o.position = (Vecteur3f.add(Vecteur3f.scale(o.position, 2.0), Vecteur3f.add(Vecteur3f.scale(o.prevPosition, -1.0), Vecteur3f.scale(o.Force, h*h/o.m))));
            o.prevPosition = pPos.copier();

            if(o.forceDoublet != null){
                for (int i = 0; i < o.forceDoublet.length; i++) {
                    pPos = o.positionDoublet[i].copier();
                    o.positionDoublet[i] = (Vecteur3f.add(Vecteur3f.scale(o.positionDoublet[i], 2.0), Vecteur3f.add(Vecteur3f.scale(o.prevPosDoublet[i], -1.0), Vecteur3f.scale(o.forceDoublet[i], h*h/Atome.mE))));
                    o.prevPosDoublet[i] = pPos;
                }
            }

            o.ÉvaluerContraintes();
        }
    }

    public static void IterVerletV(ArrayList<Atome> O, double h){

        for (Atome o : O) {
            o.position.add(Vecteur3f.add(Vecteur3f.scale(o.vélocité,h), Vecteur3f.scale(o.Force, h*h/(2.0*o.m))));

            if(o.forceDoublet != null){
                for (int i = 0; i < o.forceDoublet.length; i++) {
                    o.positionDoublet[i].add(Vecteur3f.add(Vecteur3f.scale(o.vélDoublet[i],h), Vecteur3f.scale(o.forceDoublet[i], h*h/(4.0*Atome.mE))));
                }
            }
        }

        for (Atome o : O) {

            Vecteur3f[] eForce = new Vecteur3f[o.forceDoublet.length];
            if(o.forceDoublet != null){
                for (int i = 0; i < o.forceDoublet.length; i++) {
                    eForce[i] = o.forceDoublet[i].copier();
                }
            }

            Vecteur3f force = o.Force;
            Atome.ÉvaluerForces(o);
            o.vélocité.add(Vecteur3f.scale(Vecteur3f.add(force, o.Force), h/(2.0*o.m)));

            if(o.forceDoublet != null){
                for (int i = 0; i < o.forceDoublet.length; i++) {
                    o.vélDoublet[i].add(Vecteur3f.scale(Vecteur3f.add(eForce[i], o.forceDoublet[i]), h/(4.0*Atome.mE)));
                }
            }

            o.ÉvaluerContraintes();
        }
    }

    public static void IterVerletVB(ArrayList<Atome> O, double h){

        for (Atome o : O) {
            o.vélocité.add(Vecteur3f.scale(o.Force, h/(2.0*o.m)));

            if(o.forceDoublet != null){
                for (int i = 0; i < o.forceDoublet.length; i++) {
                    o.vélDoublet[i].add(Vecteur3f.scale(o.forceDoublet[i], h/(4.0*Atome.mE)));
                }
            }
        }

        for (Atome o : O) {
            o.position.add(Vecteur3f.add(Vecteur3f.scale(o.vélocité,h), Vecteur3f.scale(o.Force, h*h/(2.0*o.m))));

            if(o.forceDoublet != null){
                for (int i = 0; i < o.forceDoublet.length; i++) {
                    o.positionDoublet[i].add(Vecteur3f.add(Vecteur3f.scale(o.vélDoublet[i],h), Vecteur3f.scale(o.forceDoublet[i], h*h/(4.0*Atome.mE))));
                }
            }
        }

        for (Atome o : O) {
            Atome.ÉvaluerForces(o);
            o.vélocité.add(Vecteur3f.scale( o.Force, h/(2.0*o.m)));

            if(o.forceDoublet != null){
                for (int i = 0; i < o.forceDoublet.length; i++) {
                    o.vélDoublet[i].add(Vecteur3f.scale( o.forceDoublet[i], h/(4.0*Atome.mE)));
                }
            }

            o.ÉvaluerContraintes();
        }
    }

    public static void IterRK4(ArrayList<Atome> O, double h){

        //TODO Vincent intégrer les doublets dans RK4 
        
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
            Vecteur3f k1v = o.vélocité.copy();
            //float K1v = F(s);
            Atome.ÉvaluerForces(o);
            Vecteur3f k1a = Vecteur3f.scale(o.Force,1.0/o.m);

            K1v.add(k1v);
            K1a.add(k1a);
        }
        
        for(int i = 0; i < O.size(); i++){
            //s2.x = s.x + (s.h/2f)*K1x;
            O.get(i).position.add(Vecteur3f.scale(K1v.get(i),h/2.0));
            //s2.v = s.v + (s.h/2f)*K1v;
            Vecteur3f k2v = Vecteur3f.add(O.get(i).vélocité, Vecteur3f.scale(K1a.get(i),h/2.0));
            //float K2x = s2.v;
            O.get(i).vélocité = k2v;
            K2v.add(k2v);
        }
        for (int i = 0; i < O.size(); i++) {
             //float K2v = F(s2);
             Vecteur3f k2a = Vecteur3f.scale(Atome.ÉvaluerForces(O.get(i)),1.0/O.get(i).m);
             K2a.add(k2a);
        }


        for (int i = 0; i < O.size(); i++) {
            //Struct s3 = new Struct(s);
            O.get(i).copy(oTmp.get(i));
            //s3.x = s.x + (s.h/2f)*K2x;
            O.get(i).position.add(Vecteur3f.scale(K2v.get(i),h/2.0));
            //s3.v = s.v + (s.h/2f)*K2v;
            Vecteur3f k3v = Vecteur3f.add(O.get(i).vélocité, Vecteur3f.scale(K2a.get(i),h/2.0));
            //float K3x = s3.v;
            O.get(i).vélocité = k3v;
            K3v.add(k3v);
        }
        for (int i = 0; i < O.size(); i++) {
            //float K3v = F(s3);
            Vecteur3f k3a = Vecteur3f.scale(Atome.ÉvaluerForces(O.get(i)),1.0/O.get(i).m);
            K3a.add(k3a);
        }

        for (int i = 0; i < O.size(); i++) {
            //Struct s4 = new Struct(s);
            O.get(i).copy(oTmp.get(i));
            //s4.x = s.x + s.h*K3x;
            O.get(i).position.add(Vecteur3f.scale(K3v.get(i),h));
            //s4.v = s.v + s.h*K3v;
            Vecteur3f k4v = Vecteur3f.add(O.get(i).vélocité, Vecteur3f.scale(K3a.get(i),h));
            //float K4x = s4.v;
            O.get(i).vélocité = k4v;
            K4v.add(k4v);
        }
        for (int i = 0; i < O.size(); i++) {
            //float K4v= F(s4);
            Vecteur3f k4a = Vecteur3f.scale(Atome.ÉvaluerForces(O.get(i)),1.0/O.get(i).m);
            K4a.add(k4a);
        }

        for (int i = 0; i < O.size(); i++) {
            K2v.get(i).scale(2.0);
            K2a.get(i).scale(2.0);
            K3v.get(i).scale(2.0);
            K3a.get(i).scale(2.0);
            O.get(i).copy(oTmp.get(i));
            //s.x = s.x + (s.h/6f)*(K1x + 2f*K2x + 2f*K3x + K4x);
            O.get(i).position.add(Vecteur3f.scale(Vecteur3f.add(K1v.get(i), Vecteur3f.add(K2v.get(i), Vecteur3f.add(K3v.get(i), K4v.get(i)))), h/6.0));
            //O.get(i).ajouterPosition(Vecteur3f.scale(K4v.get(i), h));
            //s.v = (s.h/6f)*(K1v + 2f*K2v + 2f*K3v + K4v);
            O.get(i).vélocité.add(Vecteur3f.scale(Vecteur3f.add(K1a.get(i), Vecteur3f.add(K2a.get(i), Vecteur3f.add(K3a.get(i), K4a.get(i)))), h/6.0));
            //O.get(i).ajouterVitesse(Vecteur3f.scale(K4a.get(i), h));
            //Atome.ÉvaluerForces(O.get(i));
        }
    }
    
    public static void IterVerletVBC(ArrayList<Atome> O, double h){
         
        for (Atome o : O) {
            o.vélocité.add(Vecteur3f.scale(o.Force, h/(2.0*o.m)));

            if(o.forceDoublet != null){
                for (int i = 0; i < o.forceDoublet.length; i++) {
                    o.vélDoublet[i].add(Vecteur3f.scale(o.forceDoublet[i], h/(4.0*Atome.mE)));
                }
            }
        }

        for (Atome o : O) {
            o.position.add(Vecteur3f.add(Vecteur3f.scale(o.vélocité,h), Vecteur3f.scale(o.Force, h*h/(2.0*o.m))));

            if(o.forceDoublet != null){
                for (int i = 0; i < o.forceDoublet.length; i++) {
                    o.positionDoublet[i].add(Vecteur3f.add(Vecteur3f.scale(o.vélDoublet[i],h), Vecteur3f.scale(o.forceDoublet[i], h*h/(4.0*Atome.mE))));
                }
            }
        }

        for (Atome o : O) {
            Atome.ÉvaluerForces(o);
            o.vélocité.add(Vecteur3f.scale( o.Force, h/(2.0*o.m)));

            if(o.forceDoublet != null){
                for (int i = 0; i < o.forceDoublet.length; i++) {
                    o.vélDoublet[i].add(Vecteur3f.scale( o.forceDoublet[i], h/(4.0*Atome.mE)));
                }
            }

            o.ÉvaluerContraintes();
        }
    }
}
