package com.MiniLabo.prototype;

import java.util.ArrayList;

/**
 * Classe représentant une molécule. Elle contient une liste d'atomes, ainsi que leurs positions relatives
 * au centre de la molécule.
 * Pourras avoir une rotation dans le futur.
 */
public class Molécule {
    public ArrayList<Atome> Atomes = new ArrayList<>();
    public ArrayList<Vecteur3D> posAtomes = new ArrayList<>();
    public Vecteur3D position;
    public Vecteur3D rotation;   //Non implémenté
    public Matrice4f transformée;//Non implémenté
    
    private static ArrayList<Atome> Environnement = new ArrayList<>();

    /**Créé une molécule centrée à (0,0,0) */
    public Molécule(){
        position = new Vecteur3D(0);
        rotation = new Vecteur3D(0);
        transformée = null; //Implémenter Matrice4f avant.
    }

    /**
     * Met à jour la référence à la liste des atomes de la simulation
     * @param E - Liste des atomes de la simulation
     */
    public static void MiseÀJourEnvironnement(ArrayList<Atome> E){
        Environnement = E;
    }
    
    /**
     * Ajoute un atome à la molécule.
     * @param a - Atome à ajouter
     */
    public void ajouterAtome(Atome a){
        Atomes.add(a);
        posAtomes.add(Vecteur3D.sous(a.position,position));
    }

    /**
     * Retire un atome à la molécule
     * @param a - Atome à retirer
     */
    public void retirerAtome(Atome a){
        int indexe = Atomes.indexOf(a);
        Atomes.remove(indexe);
        posAtomes.remove(indexe);
    }

    /**Met à jour la position relative de chaque atome et la position de la molécule */
    public void MiseÀJourPos(){
        position = new Vecteur3D(0);
        for (int i = 0; i < Atomes.size(); i++) {
            position.addi(Atomes.get(i).position);
        }
        position.mult(1.0/Atomes.size());

        for (int i = 0; i < Atomes.size(); i++) {
            posAtomes.set(i,Vecteur3D.sous(Atomes.get(i).position,position));
        }
    }

    /**
     * Fusionne une molécule à cette molécule. Modifiera la référence de chaque atome à la molécule automatiquement
     * @param m - Molécule à fusionner.
     */
    public void fusionnerMolécule(Molécule m){
        //TODO #16 Implémenter fusionner molécule
    }

    /**
     * Sépare la molécule en deux. Coupe au lien entre l'atome a et l'atome b. Modifiera la référence à chaque molécule
     * respective automatiquement.
     * @param a - Atome autour du lien brisé qui serat contenu dans cette molécule
     * @param b - Atome autour du lien brisé qui serat contenu dans la molécule renoyée
     */
    public void séparerMolécule(Atome a, Atome b){
        //TODO #17 implémenter séparer molécule
    }

    /**
     * Copie la molécule m
     * @param m - Molécule à copier
     */
    public void copier(Molécule m){
        this.Atomes = (ArrayList<Atome>) m.Atomes.clone();
        this.posAtomes = (ArrayList<Vecteur3D>) m.posAtomes.clone();
        this.position = m.position.copier();
        this.rotation = m.rotation.copier();
        //this.transformée = m.transformée.copier();
    }

    /**
     * Renvoie une copie de cette molécule.
     * @return Copie de cette molécule
     */
    public Molécule copier(){
        Molécule m = new Molécule();
        m.copier(this);
        return m;
    }
}
