package com.MiniLabo.prototype;

import java.util.ArrayList;

public class MoléculeRéf extends Molécule{
    public Vecteur3D BEAA = new Vecteur3D(0); //Boîte Englobante Alignée sur les Axes.
    public double rayon = 0; //Rayon de la plus petite sphère contenant la molécule.

    public MoléculeRéf(){
        super();
        calculerAABB();
    }
    
    private void calculerAABB(){
        Vecteur3D max = new Vecteur3D(-Double.MAX_VALUE);
        Vecteur3D min = new Vecteur3D(Double.MAX_VALUE);
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
        rayon = Math.max( Math.max( (max.x-min.x), (max.y-min.y) ), (max.z-min.z) ); //Rayon de la plus petite sphère contenant la molécule
        BEAA = new Vecteur3D(max.x-min.x, max.y-min.y, max.z-min.z); //BEAA de la molécule
    }

    public void copier(MoléculeRéf m){
        super.copier(m);
        this.BEAA = m.BEAA.copier();
        this.rayon = m.rayon;
    }

    public MoléculeRéf copier(){
        MoléculeRéf m = new MoléculeRéf();
        m.copier(this);
        return m;
    }

    public static void intégrerÀSimulation(ArrayList<Atome> Environnement, MoléculeRéf molécule){
        int décalage = Environnement.size();
        MoléculeRéf mol = molécule.copier();
        for (int i = 0; i < molécule.Atomes.size(); i++) {
            Atome A = mol.Atomes.get(i).copier(false);
            A.indexe += décalage;
            for (int j = 0; j < A.liaisonIndexe.size(); j++) {
                if(A.liaisonIndexe.get(j) != -1){
                    A.liaisonIndexe.set(j, A.liaisonIndexe.get(j)+décalage);
                }
            }
            A.position = Vecteur3D.addi(mol.position, mol.posAtomes.get(i));
            Environnement.add(A);
        }
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

        for (int i = 0; i < H2O.Atomes.size(); i++) {
            H2O.Atomes.get(i).indexe = i;
        }

        ArrayList<Atome> Environnement = Atome.Environnement;
        Atome.MettreÀJourEnvironnement(H2O.Atomes);
        O.créerLien(1, 0, 0, 1, false);
        O.créerLien(2, 1, 0, 1, false);
        Atome.MettreÀJourEnvironnement(Environnement);

        H2O.calculerAABB();
        H2O.MiseÀJourPos();

        return H2O;
    }

    public static MoléculeRéf avoirH3Op(){
        MoléculeRéf H3Op = new MoléculeRéf();

        Atome O = new Atome(8);
        O.retirerÉlectron();
        O.évaluerValence();
        Atome H1 = new Atome(1);
        H1.position = new Vecteur3D(1,0,0);
        Atome H2 = new Atome(1);
        H2.position = new Vecteur3D(-1,0,0);
        Atome H3 = new Atome(1);
        H3.position = new Vecteur3D(0,1,0);
        


        H3Op.ajouterAtome(O);
        H3Op.ajouterAtome(H1);
        H3Op.ajouterAtome(H2);
        H3Op.ajouterAtome(H3);

        for (int i = 0; i < H3Op.Atomes.size(); i++) {
            H3Op.Atomes.get(i).indexe = i;
        }

        ArrayList<Atome> Environnement = Atome.Environnement;
        Atome.MettreÀJourEnvironnement(H3Op.Atomes);
        O.créerLien(1, 0, 0, 1, false);
        O.créerLien(2, 1, 0, 1, false);
        O.créerLien(3, 2, 0, 1, false);
        Atome.MettreÀJourEnvironnement(Environnement);

        H3Op.calculerAABB();
        H3Op.MiseÀJourPos();

        return H3Op;
    }


    public static MoléculeRéf avoirNaCl(){
        MoléculeRéf NaCl = new MoléculeRéf();

        Atome Na = new Atome(11);
        Atome Cl = new Atome(17);

        Na.position = new Vecteur3D(0);
        Cl.position = new Vecteur3D(2.54);

        NaCl.ajouterAtome(Na);
        NaCl.ajouterAtome(Cl);

        for (int i = 0; i < NaCl.Atomes.size(); i++) {
            NaCl.Atomes.get(i).indexe = i;
        }

        ArrayList<Atome> Environnement = Atome.Environnement;
        Atome.MettreÀJourEnvironnement(NaCl.Atomes);
        Na.créerLien(1, 0, 0, 1, false);
        Atome.MettreÀJourEnvironnement(Environnement);

        NaCl.calculerAABB();
        NaCl.MiseÀJourPos();

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

        for (int i = 0; i < CH2.Atomes.size(); i++) {
            CH2.Atomes.get(i).indexe = i;
        }

        ArrayList<Atome> Environnement = Atome.Environnement;
        Atome.MettreÀJourEnvironnement(CH2.Atomes);
        C.créerLien(1, 0, 0, 1, false);
        C.créerLien(2, 1, 0, 1, false);
        Atome.MettreÀJourEnvironnement(Environnement);

        CH2.calculerAABB();
        CH2.MiseÀJourPos();

        return CH2;
    }

    public static MoléculeRéf avoirCH4(){
        MoléculeRéf CH4 = new MoléculeRéf();

        Atome C = new Atome(6);
        Atome H1 = new Atome(1);
        H1.position = new Vecteur3D(1,0,0);
        Atome H2 = new Atome(1);
        H2.position = new Vecteur3D(-1,0,0);
        Atome H3 = new Atome(1);
        H3.position = new Vecteur3D(0,-1,0);
        Atome H4 = new Atome(1);
        H4.position = new Vecteur3D(0,1,0);

        CH4.ajouterAtome(C);
        CH4.ajouterAtome(H1);
        CH4.ajouterAtome(H2);
        CH4.ajouterAtome(H3);
        CH4.ajouterAtome(H4);

        for (int i = 0; i < CH4.Atomes.size(); i++) {
            CH4.Atomes.get(i).indexe = i;
        }

        ArrayList<Atome> Environnement = Atome.Environnement;
        Atome.MettreÀJourEnvironnement(CH4.Atomes);
        C.créerLien(1, 0, 0, 1, false);
        C.créerLien(2, 1, 0, 1, false);
        C.créerLien(3, 2, 0, 1, false);
        C.créerLien(4, 3, 0, 1, false);
        Atome.MettreÀJourEnvironnement(Environnement);

        CH4.calculerAABB();
        CH4.MiseÀJourPos();

        return CH4;
    }

    public static MoléculeRéf avoir1_3_Dibutyle(){
        MoléculeRéf C4H6 = new MoléculeRéf();

        Atome C1 = new Atome(6);
        C1.position = new Vecteur3D(-2.25,0,0);
        Atome C2 = new Atome(6);
        C2.position = new Vecteur3D(-0.75,0,0);
        Atome C3 = new Atome(6);
        C3.position = new Vecteur3D(0.75,0,0);
        Atome C4 = new Atome(6);
        C4.position = new Vecteur3D(2.25,0,0);
        Atome H1 = new Atome(1);
        H1.position = new Vecteur3D(-2.25,1,0);
        Atome H2 = new Atome(1);
        H2.position = new Vecteur3D(-2.25,-1,0);
        Atome H3 = new Atome(1);
        H3.position = new Vecteur3D(-0.75,-1,0);
        Atome H4 = new Atome(1);
        H4.position = new Vecteur3D(0.75,1,0);
        Atome H5 = new Atome(1);
        H5.position = new Vecteur3D(2.25,1,0);
        Atome H6 = new Atome(1);
        H6.position = new Vecteur3D(2.25,-1,0);

        C4H6.ajouterAtome(C1);
        C4H6.ajouterAtome(C2);
        C4H6.ajouterAtome(C3);
        C4H6.ajouterAtome(C4);
        C4H6.ajouterAtome(H1);
        C4H6.ajouterAtome(H2);
        C4H6.ajouterAtome(H3);
        C4H6.ajouterAtome(H4);
        C4H6.ajouterAtome(H5);
        C4H6.ajouterAtome(H6);

        for (int i = 0; i < C4H6.Atomes.size(); i++) {
            C4H6.Atomes.get(i).indexe = i;
        }

        ArrayList<Atome> Environnement = Atome.Environnement;
        Atome.MettreÀJourEnvironnement(C4H6.Atomes);
        C1.créerLien(4, 0, 0, 1, false); //Liaison avec H1
        C1.créerLien(5, 1, 0, 1, false); //Liaison avec H2
        C1.créerLien(1, 2, 0, 2, false); //Liaison 1 avec C2
        C1.créerLien(1, 3, 1, 2, true ); //Liaison 2 avec C2
        C2.créerLien(6, 2, 0, 1, false); //Liaison avec H3
        C2.créerLien(2, 3, 0, 1, false); //Liaison avec C3
        C3.créerLien(7, 1, 0, 1, false); //Liaison avec H4
        C3.créerLien(3, 2, 0, 2, false); //Liaison 1 avec C4
        C3.créerLien(3, 3, 1, 2, true ); //Liaison 2 avec C4
        C4.créerLien(8, 2, 0, 1, false); //Liaison avec H5
        C4.créerLien(9, 3, 0, 1, false); //Liaison avec H6
        Atome.MettreÀJourEnvironnement(Environnement);

        C4H6.évaluerSystèmesConjugués();

        C4H6.calculerAABB();

        return C4H6;
    }
}
