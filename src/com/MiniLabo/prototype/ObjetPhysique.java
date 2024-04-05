package com.MiniLabo.prototype;

public interface ObjetPhysique {

    public Vecteur2f ÉvaluerForces(ObjetPhysique O, int TailleX, int TailleY, float Zoom);

    public void changerPosition(Vecteur2f pos);
    public void ajouterPosition(Vecteur2f pos);
    public Vecteur2f avoirPosition();

    public void changerPrevPosition(Vecteur2f pos);
    public void ajouterPrevPosition(Vecteur2f pos);
    public Vecteur2f avoirPrevPosition();
    public void prevPositionInit(double h);

    public void changerVitesse(Vecteur2f v);
    public void ajouterVitesse(Vecteur2f v);
    public Vecteur2f avoirVitesse();

    public void changerForce(Vecteur2f f);
    public void ajouterForce(Vecteur2f f);
    public Vecteur2f avoirForce();

    public double avoirMasse();

    public ObjetPhysique copy();
    public void copy(ObjetPhysique o);
}
