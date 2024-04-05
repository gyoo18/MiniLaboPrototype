package com.MiniLabo.prototype;

public interface ObjetPhysique {

    public Vecteur3f ÉvaluerForces(ObjetPhysique O, int TailleX, int TailleY, int TailleZ, float Zoom);

    public void changerPosition(Vecteur3f pos);
    public void ajouterPosition(Vecteur3f pos);
    public Vecteur3f avoirPosition();

    public void changerPrevPosition(Vecteur3f pos);
    public void ajouterPrevPosition(Vecteur3f pos);
    public Vecteur3f avoirPrevPosition();
    public void prevPositionInit(double h);

    public void changerVitesse(Vecteur3f v);
    public void ajouterVitesse(Vecteur3f v);
    public Vecteur3f avoirVitesse();

    public void changerForce(Vecteur3f f);
    public void ajouterForce(Vecteur3f f);
    public Vecteur3f avoirForce();

    public double avoirMasse();

    public ObjetPhysique copy();
}
