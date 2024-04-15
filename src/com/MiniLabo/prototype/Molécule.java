package com.MiniLabo.prototype;

import java.util.ArrayList;

import org.ejml.simple.SimpleMatrix;

public class Molécule {
    public ArrayList<Atome> Atomes = new ArrayList<>();
    public Vecteur3D position;
    public Vecteur3D rotation;
    public Matrice4f transformée;

    public Molécule(){
        position = new Vecteur3D(0);
        rotation = new Vecteur3D(0);
        transformée = null; //Implémenter Matrice4f avant.
    } 
}
