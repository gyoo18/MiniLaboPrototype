package com.MiniLabo.prototype;

import java.util.ArrayList;

public class Intégrateur {

    public static void IterEuler(ArrayList<Atome> O, double h){
        for (Atome o : O) {
            o.Force = new Vecteur3f(0);
            for (int j = 0; j < o.forceDoublet.length; j++) {
                o.forceDoublet[j] = new Vecteur3f(0);
            }
            o.ÉvaluerContraintes();
        }

        for (Atome o : O) {
            Atome.ÉvaluerForces(o);
            o.ÉvaluerContraintes();
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

        for (Atome o : O) {
            o.Force = new Vecteur3f(0);
            for (int j = 0; j < o.forceDoublet.length; j++) {
                o.forceDoublet[j] = new Vecteur3f(0);
            }
            
        }

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
            o.Force = new Vecteur3f(0);
            for (int j = 0; j < o.forceDoublet.length; j++) {
                o.forceDoublet[j] = new Vecteur3f(0);
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
            o.Force = new Vecteur3f(0);
            for (int j = 0; j < o.forceDoublet.length; j++) {
                o.forceDoublet[j] = new Vecteur3f(0);
            }
        }

        for (Atome o : O) {
            o.vélocité.add(Vecteur3f.scale(o.Force, h/(2.0*o.m)));

            if(o.forceDoublet != null){
                for (int i = 0; i < o.forceDoublet.length; i++) {
                    o.vélDoublet[i].add(Vecteur3f.scale(o.forceDoublet[i], h/(4.0*Atome.mE)));
                }
            }
            o.ÉvaluerContraintes();
        }

        for (Atome o : O) {
            o.position.add(Vecteur3f.add(Vecteur3f.scale(o.vélocité,h), Vecteur3f.scale(o.Force, h*h/(2.0*o.m))));

            if(o.forceDoublet != null){
                for (int i = 0; i < o.forceDoublet.length; i++) {
                    o.positionDoublet[i].add(Vecteur3f.add(Vecteur3f.scale(o.vélDoublet[i],h), Vecteur3f.scale(o.forceDoublet[i], h*h/(4.0*Atome.mE))));
                }
            }
            o.ÉvaluerContraintes();
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
    public static void IterVerletVBCD(ArrayList<Atome> O, double h){

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

        for (Atome o : O) {
            o.Force = new Vecteur3f(0);
            for (int j = 0; j < o.forceDoublet.length; j++) {
                o.forceDoublet[j] = new Vecteur3f(0);
            }
        }

        //TODO Vincent intégrer les doublets dans RK4 
        
        Vecteur3f[] K1v = new Vecteur3f[O.size()];
        Vecteur3f[] K1a = new Vecteur3f[O.size()];
        Vecteur3f[] K2v = new Vecteur3f[O.size()];
        Vecteur3f[] K2a = new Vecteur3f[O.size()];
        Vecteur3f[] K3v = new Vecteur3f[O.size()];
        Vecteur3f[] K3a = new Vecteur3f[O.size()];
        Vecteur3f[] K4v = new Vecteur3f[O.size()];
        Vecteur3f[] K4a = new Vecteur3f[O.size()];

        Vecteur3f[][] K1vd = new Vecteur3f[O.size()][];
        Vecteur3f[][] K1ad = new Vecteur3f[O.size()][];
        Vecteur3f[][] K2vd = new Vecteur3f[O.size()][];
        Vecteur3f[][] K2ad = new Vecteur3f[O.size()][];
        Vecteur3f[][] K3vd = new Vecteur3f[O.size()][];
        Vecteur3f[][] K3ad = new Vecteur3f[O.size()][];
        Vecteur3f[][] K4vd = new Vecteur3f[O.size()][];
        Vecteur3f[][] K4ad = new Vecteur3f[O.size()][];


        Atome[] oTmp = new Atome[O.size()];
        for (int i = 0; i < O.size(); i++) {
            oTmp[i] = O.get(i).copy();
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
            K1v[i] = O.get(i).vélocité.copy();
            //float K1v = F(s);
            Atome.ÉvaluerForces(O.get(i));
            K1a[i] = Vecteur3f.scale(O.get(i).Force,1.0/O.get(i).m);

            K1vd[i] = O.get(i).vélDoublet.clone();
            K1ad[i] = O.get(i).forceDoublet.clone();

            for (int j = 0; j < K1ad[i].length; j++) {
                //float K1x = s.v;
                //K1vd[i][j] = O.get(i).vélDoublet[j].copy();
                //float K1v = F(s);
                K1ad[i][j] = Vecteur3f.scale(K1ad[i][j],1.0/(2.0*Atome.mE));
            }
        }
        
        for(int i = 0; i < O.size(); i++){
            //s2.x = s.x + (s.h/2f)*K1x;
            O.get(i).position.add(Vecteur3f.scale(K1v[i],h/2.0));
            //s2.v = s.v + (s.h/2f)*K1v;
            Vecteur3f k2v = Vecteur3f.add(O.get(i).vélocité, Vecteur3f.scale(K1a[i],h/2.0));
            //float K2x = s2.v;
            O.get(i).vélocité = k2v;
            K2v[i] = k2v;

            K2vd[i] = new Vecteur3f[oTmp[i].positionDoublet.length];

            for (int j = 0; j < oTmp[i].positionDoublet.length; j++) {
                //s2.x = s.x + (s.h/2f)*K1x;
                O.get(i).positionDoublet[j].add(Vecteur3f.scale(K1vd[i][j],h/2.0));
                //float K1x = s.v;
                K2vd[i][j] = O.get(i).vélDoublet[j].copy();
            }
        }
        for (int i = 0; i < O.size(); i++) {
             //float K2v = F(s2);
             Atome.ÉvaluerForces(O.get(i));
             Vecteur3f k2a = Vecteur3f.scale(O.get(i).Force,1.0/O.get(i).m);
             K2a[i] = k2a;

             K2ad[i] = new Vecteur3f[oTmp[i].positionDoublet.length];

             for (int j = 0; j < oTmp[i].positionDoublet.length; j++) {
                K2ad[i][j] = Vecteur3f.scale( O.get(i).forceDoublet[j].copy(), 0.5/Atome.mE );
             }
        }

        for (int i = 0; i < O.size(); i++) {
            //Struct s3 = new Struct(s);
            O.get(i).copy(oTmp[i]);
            //s3.x = s.x + (s.h/2f)*K2x;
            O.get(i).position.add(Vecteur3f.scale(K2v[i],h/2.0));
            //s3.v = s.v + (s.h/2f)*K2v;
            Vecteur3f k3v = Vecteur3f.add(O.get(i).vélocité, Vecteur3f.scale(K2a[i],h/2.0));
            //float K3x = s3.v;
            O.get(i).vélocité = k3v;
            K3v[i] = k3v;

            K3vd[i] = new Vecteur3f[oTmp[i].positionDoublet.length];

            for (int j = 0; j < oTmp[i].positionDoublet.length; j++) {
                //s2.x = s.x + (s.h/2f)*K1x;
                O.get(i).positionDoublet[j].add(Vecteur3f.scale(K2vd[i][j],h/2.0));
                //float K1x = s.v;
                K3vd[i][j] = O.get(i).vélDoublet[j].copy();
            }
        }
        for (int i = 0; i < O.size(); i++) {
            //float K3v = F(s3);
            Atome.ÉvaluerForces(O.get(i));
            Vecteur3f k3a = Vecteur3f.scale(O.get(i).Force,1.0/O.get(i).m);
            K3a[i] = k3a;

            K3ad[i] = new Vecteur3f[oTmp[i].positionDoublet.length];

            for (int j = 0; j < oTmp[i].positionDoublet.length; j++) {
               K3ad[i][j] = Vecteur3f.scale( O.get(i).forceDoublet[j].copy(), 0.5/Atome.mE );
            }
        }

        for (int i = 0; i < O.size(); i++) {
            //Struct s4 = new Struct(s);
            O.get(i).copy(oTmp[i]);
            //s4.x = s.x + s.h*K3x;
            O.get(i).position.add(Vecteur3f.scale(K3v[i],h));
            //s4.v = s.v + s.h*K3v;
            Vecteur3f k4v = Vecteur3f.add(O.get(i).vélocité, Vecteur3f.scale(K3a[i],h));
            //float K4x = s4.v;
            O.get(i).vélocité = k4v;
            K4v[i] = k4v;

            K4vd[i] = new Vecteur3f[oTmp[i].positionDoublet.length];

            for (int j = 0; j < oTmp[i].positionDoublet.length; j++) {
                //s2.x = s.x + (s.h/2f)*K1x;
                O.get(i).positionDoublet[j].add(Vecteur3f.scale(K2vd[i][j],h/2.0));
                //float K1x = s.v;
                K4vd[i][j] = O.get(i).vélDoublet[j].copy();
            }
        }
        for (int i = 0; i < O.size(); i++) {
            //float K4v= F(s4);
            Atome.ÉvaluerForces(O.get(i));
            Vecteur3f k4a = Vecteur3f.scale(O.get(i).Force,1.0/O.get(i).m);
            K4a[i] = k4a;

            K4ad[i] = new Vecteur3f[oTmp[i].positionDoublet.length];

            for (int j = 0; j < oTmp[i].positionDoublet.length; j++) {
               K4ad[i][j] = Vecteur3f.scale( O.get(i).forceDoublet[j].copy(), 0.5/Atome.mE );
            }
        }

        for (int i = 0; i < O.size(); i++) {
            K2v[i].scale(2.0);
            K2a[i].scale(2.0);
            K3v[i].scale(2.0);
            K3a[i].scale(2.0);
            O.get(i).copy(oTmp[i]);
            //s.x = s.x + (s.h/6f)*(K1x + 2f*K2x + 2f*K3x + K4x);
            O.get(i).position.add(Vecteur3f.scale(Vecteur3f.add(K1v[i], Vecteur3f.add(K2v[i], Vecteur3f.add(K3v[i], K4v[i]))), h/6.0));
            //s.v = (s.h/6f)*(K1v + 2f*K2v + 2f*K3v + K4v);
            O.get(i).vélocité.add(Vecteur3f.scale(Vecteur3f.add(K1a[i], Vecteur3f.add(K2a[i], Vecteur3f.add(K3a[i], K4a[i]))), h/6.0));
            
            for (int j = 0; j < O.get(i).positionDoublet.length; j++) {
                K2vd[i][j].scale(2.0);
                K2ad[i][j].scale(2.0);
                K3vd[i][j].scale(2.0);
                K3ad[i][j].scale(2.0);
                //s.x = s.x + (s.h/6f)*(K1x + 2f*K2x + 2f*K3x + K4x);
                O.get(i).positionDoublet[j].add(Vecteur3f.scale(Vecteur3f.add(K1vd[i][j], Vecteur3f.add(K2vd[i][j], Vecteur3f.add(K3vd[i][j], K4vd[i][j]))), h/6.0));
                //O.get(i).positionDoublet[j].add(Vecteur3f.scale(K1vd[i][j], h));
                //s.v = (s.h/6f)*(K1v + 2f*K2v + 2f*K3v + K4v);
                O.get(i).vélDoublet[j].add(Vecteur3f.scale(Vecteur3f.add(K1ad[i][j], Vecteur3f.add(K2ad[i][j], Vecteur3f.add(K3ad[i][j], K4ad[i][j]))), h/6.0));
                //O.get(i).vélDoublet[j].add(Vecteur3f.scale(K1ad[i][j], h));
            }
            
            O.get(i).ÉvaluerContraintes();
        }
    }
}
