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
    public String formuleChimique = "";

    private ArrayList<int[]> systèmesConjugués = new ArrayList<>(); //Liste des systèmes conjugués dans la molécule fait partie.
    
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
        évaluerFormuleChimique(); //Mise à jour de la formule chimique
    }

    /**
     * Retire un atome à la molécule
     * @param a - Atome à retirer
     */
    public void retirerAtome(Atome a){
        int indexe = Atomes.indexOf(a);
        Atomes.remove(indexe);
        posAtomes.remove(indexe);
        évaluerFormuleChimique(); //Mise à jour de la forumle chimique
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
        if(this != m){
            for (int i = 0; i < m.Atomes.size(); i++) {
                //Pour tout les atomes de m
                Atomes.add(m.Atomes.get(i));      //Prendre l'atome de m.
                posAtomes.add(new Vecteur3D(0));  //Ajouter une place pour la position de l'atome.
                m.Atomes.get(i).molécule = this;  //Changer la référence à la molécule de l'atome.
            }
            MiseÀJourPos(); //Recalculer la position relative de chaque atome.
            évaluerFormuleChimique(); //Mettre à jour la formule chimique
            évaluerSystèmesConjugués();
        }else{
            System.out.println("Ne peut pas fusionner la même molécule.");
        }
    }

    /**
     * Sépare la molécule en deux. Coupe au lien entre l'atome a et l'atome b. Modifiera la référence à chaque molécule
     * respective automatiquement.
     * @param a - Atome autour du lien brisé qui serat contenu dans cette molécule
     * @param b - Atome autour du lien brisé qui serat contenu dans la molécule renoyée
     */
    public void séparerMolécule(Atome a, Atome b){
        ArrayList<Integer> vusA = new ArrayList<>();
        ArrayList<Atome> molA = ajouterAtomeÀMolécule(Environnement.indexOf(a), vusA);
        ArrayList<Integer> vusB = new ArrayList<>();
        ArrayList<Atome> molB = ajouterAtomeÀMolécule(Environnement.indexOf(b), vusB);

        if (!molA.contains(b)) {
            Atomes = molA;
            posAtomes = new ArrayList<>();
            for (int i = 0; i < molA.size(); i++) {
                posAtomes.add(new Vecteur3D(0));
            }
            MiseÀJourPos();
            évaluerFormuleChimique();
            évaluerSystèmesConjugués();

            Molécule moléculeB = new Molécule();
            moléculeB.Atomes = molB;
            for (int i = 0; i < molB.size(); i++) {
                moléculeB.posAtomes.add(new Vecteur3D(0));
            }
            moléculeB.MiseÀJourPos();
            moléculeB.évaluerFormuleChimique();
            moléculeB.évaluerSystèmesConjugués();

        }else{
            System.out.println("La molécule n'a pas été coupée en deux. Aucune action ne sera entreprise.");
        }
    }

    /**Vas chercher tout les atomes reliés à cet atome et renvoie ainsi les constituants de la molécule.
     * @param Atomes - Liste de tout les atomes de la simulation
     * @param indexe - Indexe de l'atome à regarder
     * @param vus - Liste de tout les atomes déjà traités
    */
    private static ArrayList<Atome> ajouterAtomeÀMolécule(int indexe, ArrayList<Integer> vus){

        ArrayList<Atome> retour = new ArrayList<>(); // Initialise la liste des atomes de retours.

        if(!vus.contains(indexe)){
            // Si l'atome n'a pas déjà été traité (A)
            vus.add(indexe);    //Indiquer qu'il aura été traité
            for (int i = 0; i < Environnement.get(indexe).liaisonIndexe.length; i++) {
                // Chercher dans tout les atomes liés (A')
                if(!vus.contains(Environnement.get(indexe).liaisonIndexe[i]) && Environnement.get(indexe).liaisonIndexe[i] != -1){
                    //Si cet atome lié n'a pas déjà été traité
                    //Aller chercher tout les atomes liés à A' (A'')
                    ArrayList<Atome> r = ajouterAtomeÀMolécule(Environnement.get(indexe).liaisonIndexe[i], vus);
                    //Ajouter ces atomes à la liste de retour
                    for (int j = 0; j < r.size(); j++) {
                        retour.add(r.get(j));
                    }
                }
            }
            retour.add(Environnement.get(indexe));    //Ajouter A au total
        }
        return retour;
    }

    /**Évalue la formule chimique de la molécule et change la variable formuleChimique */
    private void évaluerFormuleChimique(){
        int[] NP = new int[19];     //Nombre d'atome de chaque type, suivant leur nombre de protons
        for (int i = 0; i < Atomes.size(); i++) {
            NP[Atomes.get(i).NP]++;   //Ajouter cet atome au total
        }

        //Convertir la liste en lettres compréhensibles
        formuleChimique = "";
        for (int i = 0; i < NP.length; i++) {
            if(NP[i] > 0){
                switch (i) {
                    case 1:
                        formuleChimique += "H";
                        break;
                    case 2:
                        formuleChimique += "He";
                        break;
                    case 3:
                        formuleChimique += "Li";
                        break;
                    case 4:
                        formuleChimique += "Be";
                        break;
                    case 5:
                        formuleChimique += "B";
                        break;
                    case 6:
                        formuleChimique += "C";
                        break;
                    case 7:
                        formuleChimique += "N";
                        break;
                    case 8:
                        formuleChimique += "O";
                        break;
                    case 9:
                        formuleChimique += "F";
                        break;
                    case 10:
                        formuleChimique += "Ne";
                        break;
                    case 11:
                        formuleChimique += "Na";
                        break;
                    case 12:
                        formuleChimique += "Mg";
                        break;
                    case 13:
                        formuleChimique += "Al";
                        break;
                    case 14:
                        formuleChimique += "Si";
                        break;
                    case 15:
                        formuleChimique += "P";
                        break;
                    case 16:
                        formuleChimique += "S";
                        break;
                    case 17:
                        formuleChimique += "Cl";
                        break;
                    case 18:
                        formuleChimique += "Ar";
                        break;
                    default:
                }
                formuleChimique += ""+NP[i];
            }
        }
    }
    

    private void évaluerSystèmesConjugués(){
        /*boolean[] estDouble = new boolean[Atomes.size()]; //Liste des atomes qui possèdent un lien multiple
        boolean[] estSimple = new boolean[Atomes.size()]; //Liste des atomes qui possèdent un lien simple
        boolean[] estPlus = new boolean[Atomes.size()];   //Liste des atomes qui ont une charge positive
        boolean[] estÉN = new boolean[Atomes.size()];     //Liste des atomes qui sont fortement électronégatifs
        boolean[] estDoublet = new boolean[Atomes.size()];//Liste des atomes qui portent des doublets
        for (int i = 0; i < Atomes.size(); i++) {
            for (int j = 0; j < Atomes.get(i).liaisonIndexe.length; j++) {
                if(Atomes.get(i).liaisonType[j]){
                    estDouble[i] = true;
                    break;
                }
            }
            for (int j = 0; j < Atomes.get(i).liaisonIndexe.length; j++) {
                if(!Atomes.get(i).liaisonType[j]){
                    estSimple[i] = true;
                    break;
                }
            }

            if(Atomes.get(i).NP > Atomes.get(i).NE){
                estPlus[i] = true;
            }

            //TODO #22 fixer le minimum d'électronégativité
            
            if (Atomes.get(i).électronégativité > 2.0) {
                estÉN[i] = true;
            }

            if(Atomes.get(i).doublets > 0){
                estDoublet[i] = true;
            }
        }*/

        for (int i = 0; i < Atomes.size(); i++) {
            //=-=
            for (int j = 0; j < Atomes.get(i).liaisonIndexe.length; j++) {
                
            }
            //:-=
            //=-+
            //=X
            //=+
            //:-+
        }
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
