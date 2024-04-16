package com.MiniLabo.prototype;

public class MoléculeRéf extends Molécule{
    public Vecteur3D AABB = new Vecteur3D(0);
    public double rayon = 0;

    public MoléculeRéf(){
        super();
        calculerAABB();
    }
    
    private void calculerAABB(){
        Vecteur3D max = new Vecteur3D(Double.MAX_VALUE);
        Vecteur3D min = new Vecteur3D(-Double.MAX_VALUE);
        for (int i = 0; i < Atomes.size(); i++) {
            if(Atomes.get(i).position.x + Atomes.get(i).rayonCovalent > max.x){
                max.x = Atomes.get(i).position.x + Atomes.get(i).rayonCovalent;
            }
            if(Atomes.get(i).position.y + Atomes.get(i).rayonCovalent > max.y){
                max.y = Atomes.get(i).position.y + Atomes.get(i).rayonCovalent;
            }
            if(Atomes.get(i).position.z + Atomes.get(i).rayonCovalent > max.z){
                max.z = Atomes.get(i).position.z + Atomes.get(i).rayonCovalent;
            }
            if(Atomes.get(i).position.x - Atomes.get(i).rayonCovalent < min.x){
                min.x = Atomes.get(i).position.x - Atomes.get(i).rayonCovalent;
            }
            if(Atomes.get(i).position.y - Atomes.get(i).rayonCovalent < min.y){
                min.y = Atomes.get(i).position.y - Atomes.get(i).rayonCovalent;
            }
            if(Atomes.get(i).position.z - Atomes.get(i).rayonCovalent < min.z){
                min.z = Atomes.get(i).position.z - Atomes.get(i).rayonCovalent;
            }
        }
        rayon = Vecteur3D.distance(min,max);
        AABB = new Vecteur3D(max.x-min.x, max.y-min.y, max.z-min.z);
    }

    public static MoléculeRéf avoirH2O(){
        MoléculeRéf H2O = new MoléculeRéf();

        Atome O = new Atome(8);
        Atome H1 = new Atome(1);
        H1.position = new Vecteur3D(1,0,0);
        Atome H2 = new Atome(1);
        H2.position = new Vecteur3D(-1,0,0);

        H2O.ajouterAtome(O);
        H2O.ajouterAtome(H1);
        H2O.ajouterAtome(H2);

        return H2O;
    }

    public static MoléculeRéf avoirNaCl(){
        MoléculeRéf NaCl = new MoléculeRéf();

        Atome Na = new Atome(11);
        Atome Cl = new Atome(17);

        Na.position = new Vecteur3D(0);
        Cl.position = new Vecteur3D(2.54);

        NaCl.ajouterAtome(Na);
        NaCl.ajouterAtome(Cl);

        return NaCl;
    }

    public static MoléculeRéf avoirCH2(){
        MoléculeRéf CH2 = new MoléculeRéf();

        Atome C = new Atome(6);
        Atome H1 = new Atome(1);
        H1.position = new Vecteur3D(1,0,0);
        Atome H2 = new Atome(1);
        H2.position = new Vecteur3D(-1,0,0);

        CH2.ajouterAtome(C);
        CH2.ajouterAtome(H1);
        CH2.ajouterAtome(H2);

        return CH2;
    }
}
