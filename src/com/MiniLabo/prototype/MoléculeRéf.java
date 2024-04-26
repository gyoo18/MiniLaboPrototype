package com.MiniLabo.prototype;

import java.util.ArrayList;

public class MoléculeRéf extends Molécule{
    public Vecteur3D BEAA = new Vecteur3D(0); //Boîte Englobante Alignée sur les Axes.
    public double rayon = 0; //Rayon de la plus petite sphère contenant la molécule.

    public MoléculeRéf(){
        super();
        calculerBEAA();
    }
    
    private void calculerBEAA(){
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

    /**
     * Modèle de construction de molécule. Copier, coller et modifier pour créer un nouveau modèle.
     * Ne pas oublier de changer de <prev> {@code private} </prev> à <prev> {@code public} </prev>.
     * @return - Molécule voulus
     */
    private static MoléculeRéf avoirMolécule(){
        //Changer avoirMolécule() pour avoir[insérer nom de la molécule]()
        MoléculeRéf mol = new MoléculeRéf(); //Création de la molécule

        //Créer chacun des atomes
        Atome A = new Atome(1);    //Créer et donner un nom à chaque atome.
        A.position = new Vecteur3D(1,0,0);//Donner une position à chaque atome. Elle serat relative au centre de la molécule.

        //Ajouter chaque atome à la molécule
        mol.ajouterAtome(A);

        //Ne pas changer
        for (int i = 0; i < mol.Atomes.size(); i++) {
            mol.Atomes.get(i).indexe = i; //Donne un indexe à chaque atome
        }

        //Ne pas changer
        ArrayList<Atome> Environnement = Atome.Environnement; //Modifie temporairement la référence à
        Atome.MettreÀJourEnvironnement(mol.Atomes);           //l'environnement pour détecter la résonance
        MiseÀJourEnvironnement(mol.Atomes);

        //Créer les liens entre chaque atome
        //indexeAtomes serat le numéro auquel l'atome a été ajouté dans la molécule. Ex.: H3 a été ajouté en 4em, son indexe serat 3.
        //LiaisonIndexe réfère au numéro de case dans lequel on fait la liaison. Ex.: O veut faire un premier lien : il serat dans la case 0, pour en faire un deuxième il devrat être dans la case 1.
        //LiaisonOrdre fait référence à l'ordre de liaison. Chaque lien de la liaison doit être ajouté individuellement. Ex.: Pour faire un lien double entre O1 et O2, il faut ajouter un lien sigma d'ordre 2, puis un lien pi d'ordre 2.
        //LiaisonType indique si c'est un lien sigma, ou pi. Sigma = false, Pi = true
        A.créerLien(0, 0, 0, 1, false);

        //Ne pas changer
        mol.évaluerSystèmesConjugués(); //Détecte la résonance
        mol.initialiserDoublets();      //Initialise la position des doublets à l'équilibre
        //Ne pas changer
        Atome.MettreÀJourEnvironnement(Environnement);//Remet la référence à l'environnement
        MiseÀJourEnvironnement(Environnement);

        //Ne pas changer
        mol.calculerBEAA(); //Calcule la Boîte Englobante Alignée sur les Axes
        mol.MiseÀJourPos(); //Calcule le centre de la molécule et déplace les atomes.

        return mol; //Renvoie la molécule
    }

    public static MoléculeRéf avoirH2O(){
        MoléculeRéf H2O = new MoléculeRéf();

        Atome O = new Atome(8);
        O.position = new Vecteur3D(0);
        Atome H1 = new Atome(1);
        H1.position = new Vecteur3D(0.70,-0.70,0);
        Atome H2 = new Atome(1);
        H2.position = new Vecteur3D(-0.70,-0.70,0);
       

        H2O.ajouterAtome(O);
        H2O.ajouterAtome(H1);
        H2O.ajouterAtome(H2);

        for (int i = 0; i < H2O.Atomes.size(); i++) {
            H2O.Atomes.get(i).indexe = i;
        }

        ArrayList<Atome> Environnement = Atome.Environnement;
        Atome.MettreÀJourEnvironnement(H2O.Atomes);
        MiseÀJourEnvironnement(H2O.Atomes);
        O.créerLien(1, 0, 0, 1, false);
        O.créerLien(2, 1, 0, 1, false);

        H2O.évaluerSystèmesConjugués();
        H2O.initialiserDoublets();

        Atome.MettreÀJourEnvironnement(Environnement);
        MiseÀJourEnvironnement(Environnement);
        Molécule.MiseÀJourEnvironnement(Environnement);

        H2O.calculerBEAA();
        H2O.MiseÀJourPos();
      


        return H2O;
    }

    public static MoléculeRéf avoirOHm(){
        MoléculeRéf OHm = new MoléculeRéf();

        Atome O = new Atome(8);
        O.ajouterÉlectron();
        O.évaluerValence();
        Atome H1 = new Atome(1);
        H1.position = new Vecteur3D(1,0,0);

        OHm.ajouterAtome(O);
        OHm.ajouterAtome(H1);

        for (int i = 0; i < OHm.Atomes.size(); i++) {
            OHm.Atomes.get(i).indexe = i;
        }

        ArrayList<Atome> Environnement = Atome.Environnement;
        Atome.MettreÀJourEnvironnement(OHm.Atomes);
        Molécule.MiseÀJourEnvironnement(OHm.Atomes);
        O.créerLien(1, 0, 0, 1, false);
       
        OHm.évaluerSystèmesConjugués();
        OHm.initialiserDoublets();

        Atome.MettreÀJourEnvironnement(Environnement);
        MiseÀJourEnvironnement(Environnement);
        Molécule.MiseÀJourEnvironnement(Environnement);

        OHm.calculerBEAA();
        OHm.MiseÀJourPos();

        return OHm;
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
        H3.position = new Vecteur3D(0.707,0.707,0);
        H3.évaluerValence();

        H3Op.ajouterAtome(O);
        H3Op.ajouterAtome(H1);
        H3Op.ajouterAtome(H2);
        H3Op.ajouterAtome(H3);

        for (int i = 0; i < H3Op.Atomes.size(); i++) {
            H3Op.Atomes.get(i).indexe = i;
        }

        ArrayList<Atome> Environnement = Atome.Environnement;
        Atome.MettreÀJourEnvironnement(H3Op.Atomes);
        Molécule.MiseÀJourEnvironnement(H3Op.Atomes);
        O.créerLien(1, 0, 0, 1, false);
        O.créerLien(2, 1, 0, 1, false);
        O.créerLien(3, 2, 0, 1, false);

        H3Op.évaluerSystèmesConjugués();
        H3Op.initialiserDoublets();

        Atome.MettreÀJourEnvironnement(Environnement);
        MiseÀJourEnvironnement(Environnement);
        Molécule.MiseÀJourEnvironnement(Environnement);

        H3Op.calculerBEAA();
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
        MiseÀJourEnvironnement(NaCl.Atomes);
        Na.créerLien(1, 0, 0, 1, false);

        NaCl.évaluerSystèmesConjugués();
        NaCl.initialiserDoublets();

        Atome.MettreÀJourEnvironnement(Environnement);
        MiseÀJourEnvironnement(Environnement);

        NaCl.calculerBEAA();
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
        MiseÀJourEnvironnement(CH2.Atomes);
        C.créerLien(1, 0, 0, 1, false);
        C.créerLien(2, 1, 0, 1, false);

        CH2.évaluerSystèmesConjugués();
        CH2.initialiserDoublets();

        Atome.MettreÀJourEnvironnement(Environnement);
        MiseÀJourEnvironnement(Environnement);

        CH2.calculerBEAA();
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
        MiseÀJourEnvironnement(CH4.Atomes);
        C.créerLien(1, 0, 0, 1, false);
        C.créerLien(2, 1, 0, 1, false);
        C.créerLien(3, 2, 0, 1, false);
        C.créerLien(4, 3, 0, 1, false);

        CH4.évaluerSystèmesConjugués();
        CH4.initialiserDoublets();

        Atome.MettreÀJourEnvironnement(Environnement);
        MiseÀJourEnvironnement(Environnement);

        CH4.calculerBEAA();
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
        MiseÀJourEnvironnement(C4H6.Atomes);
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

        C4H6.évaluerSystèmesConjugués();
        C4H6.initialiserDoublets();

        Atome.MettreÀJourEnvironnement(Environnement);
        MiseÀJourEnvironnement(Environnement);
        Molécule.MiseÀJourEnvironnement(Environnement);


        C4H6.calculerBEAA();
        C4H6.MiseÀJourPos();

        return C4H6;
    }

    public static MoléculeRéf avoirAcétate(){
        MoléculeRéf C2H3O2 = new MoléculeRéf();

        Atome C1 = new Atome(6);
        C1.position = new Vecteur3D(0);
        Atome C2 = new Atome(6);
        C2.position = new Vecteur3D(-1.05,-1.05,0);
        Atome O1 = new Atome(8);
        O1.position = new Vecteur3D(0,1.24,0);
        Atome O2 = new Atome(8);
        O2.position = new Vecteur3D(0.98,-0.98,0);
        O2.ajouterÉlectron();
        O2.évaluerValence();
        Atome H1 = new Atome(1);
        H1.position = new Vecteur3D(-1.63,-0.47,0.58);
        Atome H2 = new Atome(1);
        H2.position = new Vecteur3D(-1.63,-0.47,-0.58);
        Atome H3 = new Atome(1);
        H3.position = new Vecteur3D(-1.05,-2.12,0);

        C2H3O2.ajouterAtome(C1);
        C2H3O2.ajouterAtome(C2);
        C2H3O2.ajouterAtome(O1);
        C2H3O2.ajouterAtome(O2);
        C2H3O2.ajouterAtome(H1);
        C2H3O2.ajouterAtome(H2);
        C2H3O2.ajouterAtome(H3);

        for (int i = 0; i < C2H3O2.Atomes.size(); i++) {
            C2H3O2.Atomes.get(i).indexe = i;
        }
        
        ArrayList<Atome> Environnement = new ArrayList<>();
        Atome.MettreÀJourEnvironnement(C2H3O2.Atomes);
        MiseÀJourEnvironnement(C2H3O2.Atomes);
        C1.créerLien( C2.indexe, 0, 0, 1, false);
        C1.créerLien( O1.indexe, 1, 0, 2, false);
        C1.créerLien( O1.indexe, 2, 1, 2, true);
        C1.créerLien( O2.indexe, 3, 0, 1, false);
        C2.créerLien( H1.indexe, 1, 0, 1, false);
        C2.créerLien( H2.indexe, 2, 0, 1, false);
        C2.créerLien( H3.indexe, 3, 0, 1, false);

        C2H3O2.évaluerSystèmesConjugués();
        C2H3O2.initialiserDoublets();

        Atome.MettreÀJourEnvironnement(Environnement);
        MiseÀJourEnvironnement(Environnement);

        C2H3O2.calculerBEAA();

        return C2H3O2;
    }

    public static MoléculeRéf avoirC2H6(){
        MoléculeRéf C2H6 = new MoléculeRéf();

        Atome C1 = new Atome(6);
        C1.évaluerValence();
        Atome H1 = new Atome(1);
        //H1.position = new Vecteur3D(0.49,-0.7,0.49);
        H1.position = new Vecteur3D(0.49,0.49,-0.7);
        //H1.position = new Vecteur3D(-0.7,0.49,0.49);
        H1.évaluerValence();
        Atome H2 = new Atome(1);
        //H2.position = new Vecteur3D(0,-0.7,-0.7);
        H2.position = new Vecteur3D(0,-0.7,-0.7);
        //H2.position = new Vecteur3D(-0.7,-0.7,0);
        H2.évaluerValence();
        Atome H3 = new Atome(1);
        //H3.position = new Vecteur3D(-0.49,-0.7,0.49);
        H3.position = new Vecteur3D(-0.49,0.49,-0.7);
        //H3.position = new Vecteur3D(-0.7,0.49,-0.49);
        H3.évaluerValence();

        Atome C2 = new Atome(6);
        //C2.position = new Vecteur3D(0,1.7,0);
        C2.position = new Vecteur3D(0,0,1.7);
        //C2.position = new Vecteur3D(1.7,0,0);
        C2.évaluerValence();
        Atome H4 = new Atome(1);
       // H4.position= Vecteur3D.addi(V3.addi(Vecteur3D.mult(H1.position, 1),C2.position), new Vecteur3D(0) );
        H4.position = Vecteur3D.addi(new V3(0.48,0.49,0.7),C2.position);
        H4.évaluerValence();
        Atome H5 = new Atome(1);
        //H5.position= Vecteur3D.addi(V3.addi(Vecteur3D.mult(H2.position, 1),C2.position), new Vecteur3D(0)  );
        H5.position = Vecteur3D.addi(new V3(0.1,-0.7,0.7),C2.position);
        H5.évaluerValence();
        Atome H6 = new Atome(1);
       // H6.position= Vecteur3D.addi(V3.addi(Vecteur3D.mult(H3.position, 1),C2.position),  new Vecteur3D(0) );
        H6.position = Vecteur3D.addi(new V3(-0.49,0.48,0.7),C2.position);
        H6.évaluerValence();
        

        C2H6.ajouterAtome(C1);
        C2H6.ajouterAtome(H1);
        C2H6.ajouterAtome(H2);
        C2H6.ajouterAtome(H3);
        C2H6.ajouterAtome(C2);
        C2H6.ajouterAtome(H4);
        C2H6.ajouterAtome(H5);
        C2H6.ajouterAtome(H6);

        for (int i = 0; i < C2H6.Atomes.size(); i++) {
            C2H6.Atomes.get(i).indexe = i;
        }

        ArrayList<Atome> Environnement = Atome.Environnement;
        Atome.MettreÀJourEnvironnement(C2H6.Atomes);
        MiseÀJourEnvironnement(C2H6.Atomes);
        C1.créerLien(H1.indexe, 0, 0, 1, false);
        C1.créerLien(H2.indexe, 1, 0, 1, false);
        C1.créerLien(H3.indexe, 2, 0, 1, false);
        C1.créerLien(C2.indexe, 3, 0, 1, false);
        C2.créerLien(H4.indexe, 1, 0, 1, false);
        C2.créerLien(H5.indexe, 2, 0, 1, false);
        C2.créerLien(H6.indexe, 3, 0, 1, false);

        C2H6.évaluerSystèmesConjugués();
        C2H6.initialiserDoublets();

        Atome.MettreÀJourEnvironnement(Environnement);
        MiseÀJourEnvironnement(Environnement);

        C2H6.calculerBEAA();
        C2H6.MiseÀJourPos();

        return C2H6;
    }

    public static MoléculeRéf avoirH2(){
        MoléculeRéf mol = new MoléculeRéf(); //Création de la molécule

        //Créer chacun des atomes
        Atome H1 = new Atome(1);    //Créer et donner un nom à chaque atome.
        H1.position = new Vecteur3D(-0.32,0,0);//Donner une position à chaque atome. Elle serat relative au centre de la molécule.
        Atome H2 = new Atome(1);    //Créer et donner un nom à chaque atome.
        H2.position = new Vecteur3D(0.32,0,0);//Donner une position à chaque atome. Elle serat relative au centre de la molécule.

        //Ajouter chaque atome à la molécule
        mol.ajouterAtome(H1);
        mol.ajouterAtome(H2);

        //Ne pas changer
        for (int i = 0; i < mol.Atomes.size(); i++) {
            mol.Atomes.get(i).indexe = i; //Donne un indexe à chaque atome
        }

        //Ne pas changer
        ArrayList<Atome> Environnement = Atome.Environnement; //Modifie temporairement la référence à
        Atome.MettreÀJourEnvironnement(mol.Atomes);           //l'environnement pour détecter la résonance
        MiseÀJourEnvironnement(mol.Atomes);

        //Créer les liens entre chaque atome
        H1.créerLien(1, 0, 0, 1, false);

        //Ne pas changer
        mol.évaluerSystèmesConjugués(); //Détecte la résonance
        mol.initialiserDoublets();      //Initialise la position des doublets à l'équilibre
        //Ne pas changer
        Atome.MettreÀJourEnvironnement(Environnement);//Remet la référence à l'environnement
        MiseÀJourEnvironnement(Environnement);

        //Ne pas changer
        mol.calculerBEAA(); //Calcule la Boîte Englobante Alignée sur les Axes
        mol.MiseÀJourPos(); //Calcule le centre de la molécule et déplace les atomes.

        return mol; //Renvoie la molécule
    }

    public static MoléculeRéf avoirHCl(){
        //Changer avoirMolécule() pour avoir[insérer nom de la molécule]()
        MoléculeRéf HCl = new MoléculeRéf(); //Création de la molécule

        //Créer chacun des atomes
        Atome H = new Atome(1);    
        H.position = new Vecteur3D(0,0,0);
        Atome Cl = new Atome(17);    
        Cl.position = new Vecteur3D(1,0,0);

        //Ajouter chaque atome à la molécule
        HCl.ajouterAtome(H);
        HCl.ajouterAtome(Cl);

        //Ne pas changer
        for (int i = 0; i < HCl.Atomes.size(); i++) {
            HCl.Atomes.get(i).indexe = i;
        }

        //Ne pas changer
        ArrayList<Atome> Environnement = Atome.Environnement;
        Atome.MettreÀJourEnvironnement(HCl.Atomes);           
        MiseÀJourEnvironnement(HCl.Atomes);

        //Créer les liens entre chaque atome
        H.créerLien(Cl.indexe, 0, 0, 1, false);

        //Ne pas changer
        HCl.évaluerSystèmesConjugués(); //Détecte la résonance
        HCl.initialiserDoublets();      //Initialise la position des doublets à l'équilibre
        //Ne pas changer
        Atome.MettreÀJourEnvironnement(Environnement);//Remet la référence à l'environnement
        MiseÀJourEnvironnement(Environnement);

        //Ne pas changer
        HCl.calculerBEAA(); //Calcule la Boîte Englobante Alignée sur les Axes
        HCl.MiseÀJourPos(); //Calcule le centre de la molécule et déplace les atomes.

        return HCl; //Renvoie la molécule
    }

    public static MoléculeRéf avoirCl2(){
        //Changer avoirMolécule() pour avoir[insérer nom de la molécule]()
        MoléculeRéf Cl2 = new MoléculeRéf(); //Création de la molécule

        //Créer chacun des atomes
        Atome ClA = new Atome(17);    
        ClA.position = new Vecteur3D(0,0,0);
        Atome ClB = new Atome(17);    
        ClB.position = new Vecteur3D(1,0,0);

        //Ajouter chaque atome à la molécule
        Cl2.ajouterAtome(ClA);
        Cl2.ajouterAtome(ClB);

        //Ne pas changer
        for (int i = 0; i < Cl2.Atomes.size(); i++) {
            Cl2.Atomes.get(i).indexe = i;
        }

        //Ne pas changer
        ArrayList<Atome> Environnement = Atome.Environnement;
        Atome.MettreÀJourEnvironnement(Cl2.Atomes);           
        MiseÀJourEnvironnement(Cl2.Atomes);

        //Créer les liens entre chaque atome
        ClA.créerLien(ClB.indexe, 0, 0, 1, false);

        //Ne pas changer
        Cl2.évaluerSystèmesConjugués(); //Détecte la résonance
        Cl2.initialiserDoublets();      //Initialise la position des doublets à l'équilibre
        //Ne pas changer
        Atome.MettreÀJourEnvironnement(Environnement);//Remet la référence à l'environnement
        MiseÀJourEnvironnement(Environnement);

        //Ne pas changer
        Cl2.calculerBEAA(); //Calcule la Boîte Englobante Alignée sur les Axes
        Cl2.MiseÀJourPos(); //Calcule le centre de la molécule et déplace les atomes.

        return Cl2; //Renvoie la molécule
    }

    public static MoléculeRéf avoirCl2(){
        //Changer avoirMolécule() pour avoir[insérer nom de la molécule]()
        MoléculeRéf Cl2 = new MoléculeRéf(); //Création de la molécule

        //Créer chacun des atomes
        Atome ClA = new Atome(17);    
        ClA.position = new Vecteur3D(0,0,0);
        Atome ClB = new Atome(17);    
        ClB.position = new Vecteur3D(1,0,0);

        //Ajouter chaque atome à la molécule
        Cl2.ajouterAtome(ClA);
        Cl2.ajouterAtome(ClB);

        //Ne pas changer
        for (int i = 0; i < Cl2.Atomes.size(); i++) {
            Cl2.Atomes.get(i).indexe = i;
        }

        //Ne pas changer
        ArrayList<Atome> Environnement = Atome.Environnement;
        Atome.MettreÀJourEnvironnement(Cl2.Atomes);           
        MiseÀJourEnvironnement(Cl2.Atomes);

        //Créer les liens entre chaque atome
        ClA.créerLien(ClB.indexe, 0, 0, 1, false);

        //Ne pas changer
        Cl2.évaluerSystèmesConjugués(); //Détecte la résonance
        Cl2.initialiserDoublets();      //Initialise la position des doublets à l'équilibre
        //Ne pas changer
        Atome.MettreÀJourEnvironnement(Environnement);//Remet la référence à l'environnement
        MiseÀJourEnvironnement(Environnement);

        //Ne pas changer
        Cl2.calculerBEAA(); //Calcule la Boîte Englobante Alignée sur les Axes
        Cl2.MiseÀJourPos(); //Calcule le centre de la molécule et déplace les atomes.

        return Cl2; //Renvoie la molécule
    }
}
