package com.MiniLabo.prototype;

import java.util.ArrayList;

public class Intégrateur {
    public static void IterEuler(ArrayList<ObjetPhysique> O, double h, int TailleX, int TailleY, float Zoom){
        for (int i = 0; i < O.size(); i ++) {
            O.get(i).changerForce(O.get(i).ÉvaluerForces(i, TailleX, TailleY, Zoom));
        }

        for (ObjetPhysique o : O) {
            o.ajouterVitesse(Vecteur2f.scale(o.avoirForce(),h/o.avoirMasse()));
            o.ajouterPosition(Vecteur2f.scale(o.avoirVitesse(), h));
        }
    }

    public static void IterVerlet(ArrayList<ObjetPhysique> O, double h, int TailleX, int TailleY, float Zoom){
        for (int i = 0; i < O.size(); i ++) {
            O.get(i).prevPositionInit(h);
            O.get(i).changerForce(O.get(i).ÉvaluerForces(i, TailleX, TailleY, Zoom));
        }

        for (ObjetPhysique o : O) {
            Vecteur2f prevPos = o.avoirPosition();
            o.changerPosition(Vecteur2f.add(Vecteur2f.scale(o.avoirPosition(), 1.0), Vecteur2f.add(Vecteur2f.scale(o.avoirPrevPosition(), 0.0), Vecteur2f.scale(o.avoirForce(), h*h/o.avoirMasse()))));
            o.changerPrevPosition(prevPos);
        }
    }
}
