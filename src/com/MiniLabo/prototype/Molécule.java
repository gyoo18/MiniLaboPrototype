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
        a.molécule = this;
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
        }else{
    //        System.out.println("Ne peut pas fusionner la même molécule.");
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
            //évaluerSystèmesConjugués();

            Molécule moléculeB = new Molécule();
            moléculeB.Atomes = molB;
            for (int i = 0; i < molB.size(); i++) {
                moléculeB.posAtomes.add(new Vecteur3D(0));
            }
            moléculeB.MiseÀJourPos();
            moléculeB.évaluerFormuleChimique();
            //moléculeB.évaluerSystèmesConjugués();

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
            for (int i = 0; i < Environnement.get(indexe).liaisonIndexe.size(); i++) {
                // Chercher dans tout les atomes liés (A')
                if(!vus.contains(Environnement.get(indexe).liaisonIndexe.get(i)) && Environnement.get(indexe).liaisonIndexe.get(i) != -1){
                    //Si cet atome lié n'a pas déjà été traité
                    //Aller chercher tout les atomes liés à A' (A'')
                    ArrayList<Atome> r = ajouterAtomeÀMolécule(Environnement.get(indexe).liaisonIndexe.get(i), vus);
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
    
    /**Détecte les systèmes conjugués de résonances présents dans la molécules */
    public void évaluerSystèmesConjugués(){
        //TODO #24 Régler bug de (A)-(B)=(A) résonance
        //TODO #25 Tester détection résonance
        for (int i = 0; i < Atomes.size(); i++) {
            Atome A = Atomes.get(i);
            //=-= | type = 0
            boolean estDouble = false; //Indique si A possède au moins un lien double
            boolean estSimple = false; //Indique si A possède au mois un lien simple
            boolean estDouble2 = false; //Indique si au moins un B dans =(A)-(B) possède au moins une liaison double
            ArrayList<Atome> A1 = new ArrayList<>(); //Liste des atomes liés par liaison double à A (A1)=(A)
            ArrayList<Atome> A3 = new ArrayList<>(); //Liste des atomes liés par liaison simple à A (A)-(A3)
            ArrayList<ArrayList<Atome>> A4 = new ArrayList<>(); //Liste des atomes liés par liaison simple aux atomes de A3 (A3)=(A4)
            //Chercher tout les atomes liés par liaison double à A
            for (int j = 0; j < A.liaisonIndexe.size(); j++) {
                //Pour toutes les liaisons de A
                if(A.liaisonOrdre.get(j) > 1 && !A.liaisonType.get(j) && A.liaisonIndexe.get(j) != -1){
                    //Si l'ordre de cette liaison est >1, et que cette liaison existe.
                    //On prend la liaison sigma pour éviter de compter cette liaison plus d'une fois.
                    estDouble = true; //Indiquer qu'on a au moins une liaison double
                    A1.add(Environnement.get(A.liaisonIndexe.get(j))); //Ajouter l'autre atome à A1
                }
            }
            //Chercher tout les atomes liés par une liaison simple à A
            for (int j = 0; j < A.liaisonIndexe.size(); j++) {
                //Pour toutes les liaisons de A
                if(A.liaisonOrdre.get(j) == 1 && A.liaisonIndexe.get(j) != -1){
                    //Si cette liaison existe et est simple,
                    estSimple = true; //Indiquer qu'on a au moins une liaison simple

                    Atome Ap = Environnement.get(A.liaisonIndexe.get(j)); //Référence à l'autre atome de cette liaison (A3)
                    boolean ajouté = false; //Indique si on a déjà ajouté A3 à la liste des A3
                    //Chercher tout les atomes liés par une liaison double à A3
                    for (int j2 = 0; j2 < Ap.liaisonIndexe.size(); j2++) {
                        //Pour toutes les liaisons de A3
                        if(Ap.liaisonOrdre.get(j2) > 1 && !Ap.liaisonType.get(j) && Ap.liaisonIndexe.get(j2) != -1){
                            //Si cette liaison existe et qu'elle est d'ordre >1,
                            //On prend la liaison sigma pour éviter de compter cette liaison plus d'une fois.
                            estDouble2 = true; //Indique que A3 possède au moins une liaison double
                            if(!ajouté){
                                A3.add(Environnement.get(A.liaisonIndexe.get(j))); //Ajouter A3 à la liste des A3. N'ajoute pas si A3 n'a pas de liaison double, car il ne formera pas de système conjugué.
                                A4.add(new ArrayList<Atome>()); //Ajouter une liste de A4 à A3
                                ajouté = true; //Indiquer qu'on a ajouté A3 à la liste
                            }
                            A4.get(A4.size()-1).add(Environnement.get(Ap.liaisonIndexe.get(j2))); //Ajouter A4 à la liste de A4
                        }
                    }
                }
            }
            //Ajouter le système conjugué
            if(estDouble&&estSimple&&estDouble2){
                //Si on a un =-=,
                //Ajouter toutes les combinaisons de systèmes qu'il peut y avoir.
                for (int j = 0; j < A1.size(); j++) {
                    for (int j2 = 0; j2 < A3.size(); j2++) {
                        for (int k = 0; k < A4.get(j2).size(); k++) {
                            //Pour =-=, le systèmes est organisé ainsi :
                            //(A1)=(A2)-(A3)=(A4). A2 = cet atome, A.
                            //[ type = 0, indexe de A1, indexe de A2, indexe de A3, indexe de A4]
                            int[] système = {0, A1.get(j).indexe, i, A3.get(j2).indexe, A4.get(j2).get(k).indexe}; //Créer le système
                            ajouterSystèmeConjugé(système); //Stocker le système
                        }
                    }
                }
            }

            //:-= | type = 1
            if(A.doublets > 0){
                //Si on a un doublet,
                //Chercher tout les atomes liés par une liaison simple à A
                for (int j = 0; j < A.liaisonIndexe.size(); j++) {
                    //Pour toutes les liaisons de A
                    //Si la liaison n'existe pas, passer au prochain.
                    if(A.liaisonIndexe.get(j) == -1){
                        continue;
                    }
                    
                    Atome B = Environnement.get(A.liaisonIndexe.get(j)); //Référence à l'atome à l'autre bout de cette liaison (B)
                    //Chercher tout les atomes liés par une liaison double à B
                    for (int j2 = 0; j2 < B.liaisonIndexe.size(); j2++) {
                        //Pour toutes les liaisons de B
                        if(B.liaisonIndexe.get(j2) != -1 && B.liaisonOrdre.get(j2) > 1 && !B.liaisonType.get(j)){
                            //Si cette liaison existe, qu'elle est d'ordre >1
                            //On prend la liaison sigma pour éviter de compter cette liaison plus d'une fois.
                            //Pour :-=, le système est organisé ainsi:
                            //   :(A)-(B)=(C)
                            //   [type = 1, indexe de A, indexe de B, indexe de C]
                            int[] système = {1, i, B.indexe, B.liaisonIndexe.get(j2)}; //Créer système conjugué
                            ajouterSystèmeConjugé(système); //Ajouter système conjugué dans la liste
                        }
                    }
                }
            }

            //=-+ | type = 2
            if(A.NP > A.NE){
                //Si A a un atome de plus que d'électrons (est considéré positif dans la nomnclature. Ne pas utiliser la charge à causes des charges partielles.)
                for (int j = 0; j < A.liaisonIndexe.size(); j++) {
                    //Pour toutes les liaisons de A,
                    //Si la liaison n'existe pas, passer au prochain
                    if(A.liaisonIndexe.get(j) == -1){
                        continue;
                    }
                    
                    Atome B = Environnement.get(A.liaisonIndexe.get(j)); //Référence à l'atome à l'autre bout de la liaison (B)
                    //Chercher tout les atomes liés par une liaison double à B
                    for (int j2 = 0; j2 < B.liaisonIndexe.size(); j2++) {
                        //Pour toutes les liaisons de B
                        if(B.liaisonOrdre.get(j2) > 1 && B.liaisonType.get(j2) && !B.liaisonType.get(j2)){
                            //Si cette liaison existe et qu'elle est d'ordre >1
                            //On prend la liaison sigmae pour éviter de compter cette liaison plus qu'une fois.
                            //Pour =-+, le système est organisé ainsi:
                            //  +(A)-(B)=(C)
                            //  [type = 2, indexe de A, indexe de B, indexe de C]
                            int[] système = {2, i, B.indexe, B.liaisonIndexe.get(j2)}; //Création du système.
                            ajouterSystèmeConjugé(système); //Ajouter le système dans la liste.
                        }
                    }
                }
            }
            //TODO #22 fixer le minimum d'électronégativité
            //=X | type = 3
            if(A.électronégativité > 4.0){
                //Inclut N(5.5), O(8.3), F(12) et Cl(4.3).
                //Chercher tout les atomes qui forment une liaison double avec A
                for (int j = 0; j < A.liaisonIndexe.size(); j++) {
                    //Pour toutes les liaisons de A
                    if(A.liaisonOrdre.get(j) > 1 && !A.liaisonType.get(j) && A.liaisonIndexe.get(j) != -1){
                        //Si la liaison existe et est d'ordre 2,
                        //On prend la liaison sigma pour éviter de la compter plus d'une fois.
                        //Pour =X, le système est organisé ainsi:
                        //  X(A)=(B)
                        //  [type = 3, indexe de A, indexe de B]
                        int[] système = {3, i,A.liaisonIndexe.get(j)}; //Créer le système
                        ajouterSystèmeConjugé(système); //Ajouter le système à la liste
                    }
                }
            }

            //=+ | type = 4
            if (A.NP > A.NE) {
                //Si A a un atome de plus que d'électrons (est considéré positif dans la nomenclature. Ne pas utiliser la charge à causes des charges partielles.)
                //Chercher tout les atomes liés par un lien double à A.
                for (int j = 0; j < A.liaisonIndexe.size(); j++) {
                    //Pour toutes les liaisons de A.
                    if(A.liaisonOrdre.get(i) > 1 && !A.liaisonType.get(j) && A.liaisonIndexe.get(i) != -1){
                        //Si la liaison est d'ordre >1 et qu'elle existe,
                        //On prend la liaison sigma pour éviter de la compter plus d'une fois
                        //Pour =+, le système est organisé ainsi:
                        //  +(A)=(B)
                        //  [type = 4, indexe de A, indexe de B]
                        int[] système = {4, i, A.liaisonIndexe.get(j)}; //Créer le système.
                        ajouterSystèmeConjugé(système); //Ajouter le système à la liste.
                    }
                }
            }

            //:-+ | type = 5
            if (A.doublets > 0) {
                //Si A possède un doublet
                //Chercher tout les atomes lié par un lien simple à A
                for (int j = 0; j < A.liaisonIndexe.size(); j++) {
                    //Pour toutes les liaisons de A, 
                    //Si la liaison n'existe pas, passer à la prochaine.
                    if(A.liaisonIndexe.get(j) == -1){
                        continue;
                    }
                    
                    Atome B = Atomes.get(A.liaisonIndexe.get(j)); //Référence à l'atome à l'autre bout de la liaison (B)
                    if(A.liaisonOrdre.get(j) == 1 && B.NP > B.NE){
                        //Si la liaison est d'ordre 1, que B a plus de protons que d'électrons (qu'il est considéré positif dans la nomenclature. Ne pas utiliser la charge à causes des charges partielles.)
                        //Pour :-+, le système est organisé ainsi:
                        //  :(A)-(B)+
                        //  [type = 5, indexe de A, indexe de B]
                        int[] système = {5, i, A.liaisonIndexe.get(j)}; //Créer système.
                        ajouterSystèmeConjugé(système); //Ajouter système à la liste.
                    }
                }
            }
        }
    }

    /**
     * <p>Ajoute un système conjugué de résonance à la liste des systèmes. Si ce système est déjà comptabilisé, il ne serat
     * pas ajouté une seconde fois. Prend en argument une liste de int sous la forme suivante:</p>
     *  système[0] = Indexe du type de système.
     *  <ul><li>0 = [ <b>=-=</b> ],</li>
     *      <li>1 = [ <b>:-=</b> ],</li>
     *      <li>2 = [ <b>=-+</b> ],</li>
     *      <li>3 = [ <b>=X </b> ],</li>
     *      <li>4 = [ <b>=+ </b> ],</li>
     *      <li>5 = [ <b>:-+</b> ] </li> </ul>
     *  <p>système[1 à n] = Indexe des atomes impliqués dans le système dans l'ordre suivant: </p>
     *  <ul><li>[ <b>=-=</b> ] -> (A)<b>=</b>(B)<b>-</b>(C)<b>=</b>(D). <b>[ type = 0, indexe (A)=, indexe =(B)-, indexe -(C)=, indexe =(D) ]</b> <i>Dans un sens, comme dans l'autre, s'il existe déjà, il ne serat pas comptabilisé. </i></li>
     *      <li>[ <b>:-=</b> ] . -> . <b>:</b>(A)<b>-</b>(B)<b>=</b>(C). <b>[ type = 1, indexe :(A)-, indexe -(B)=, indexe =(C) ]</b> <i>L'autre sens n'est pas accepté.</i></li>
     *      <li>[ <b>=-+</b> ] -> <b>+</b>(A)<b>-</b>(B)<b>=</b>(C). <b>[ type = 2, indexe +(A)-, indexe -(B)=, indexe =(C) ]</b> <i>L'autre sens n'est pas accepté.</i></li>
     *      <li>[ <b>=X </b> ] . -> <b>X</b>(A)<b>=</b>(B) ........ <b>[ type = 3, indexe X(A)=, indexe =(B) ]</b> <i>L'autre sens n'est pas accepté.</i></li>
     *      <li>[ <b>=+ </b> ] . -> <b>+</b>(A)<b>=</b>(B) ........ <b>[ type = 4, indexe +(A)=, indexe =(B) ]</b> <i>L'autre sens n'est pas accepté.</i></li>
     *      <li>[ <b>:-+</b> ] . -> . <b>:</b>(A)<b>-</b>(B)<b>+</b> ...... <b>[ type = 5, indexe :(A)-, indexe -(B)+ ]</b> <i>L'autre sens n'est pas accepté.</i></li>
     *  </ul>
     * @param système - Description du système.
     */
    private void ajouterSystèmeConjugé(int[] système){
        boolean déjàPrésent = false;
        for (int i = 0; i < systèmesConjugués.size(); i++) {
            if(système[0] == systèmesConjugués.get(i)[0]){
                boolean égale = true;
                for (int j = 1; j < système.length; j++) {
                    égale = égale && système[j] == systèmesConjugués.get(i)[j];
                }
                boolean égaleSC = true;
                for (int j = 1; j < système.length; j++) {
                    égaleSC = égaleSC && système[j] == systèmesConjugués.get(i)[système.length-j];
                }
                if (égale || égaleSC) {
                    déjàPrésent = true;
                    break;
                }
            }
        }

        if (!déjàPrésent) {
            systèmesConjugués.add(système);
        }
    }

    public void initialiserDoublets(){
        for (int i = 0; i < 100; i++) {
            for (int j = 0; j < Atomes.size(); j++) {
                Atomes.get(j).déplacerDoublet();
            }
        }
    }

    /**
     * Renvois tout les systèmes conjugués dont l'atome fait partis.
     * @param indexe - Indexe de l'atome.
     * @return - ArrayList<int[]> de systèmes conjugués.
     * 
     * <p>Les sytèmes conjugués sont décrits selon la forme suivante:</p>
     * <p>système[0] = Indexe du type de système.</p>
     *  <ul><li>0 = [ <b>=-=</b> ],</li>
     *      <li>1 = [ <b>:-=</b> ],</li>
     *      <li>2 = [ <b>=-+</b> ],</li>
     *      <li>3 = [ <b>=X </b> ],</li>
     *      <li>4 = [ <b>=+ </b> ],</li>
     *      <li>5 = [ <b>:-+</b> ] </li> </ul>
     *  <p>système[1 à n] = Indexe des atomes impliqués dans le système dans l'ordre suivant: </p>
     *  <ul><li>[ <b>=-=</b> ] -> (A)<b>=</b>(B)<b>-</b>(C)<b>=</b>(D). <b>[ type = 0, indexe (A)=, indexe =(B)-, indexe -(C)=, indexe =(D) ]</b> <i>Dans un sens, ou dans l'autre, mais un seul des deux serat retourné. </i></li>
     *      <li>[ <b>:-=</b> ] . -> . <b>:</b>(A)<b>-</b>(B)<b>=</b>(C). <b>[ type = 1, indexe :(A)-, indexe -(B)=, indexe =(C) ]</b> </li>
     *      <li>[ <b>=-+</b> ] -> <b>+</b>(A)<b>-</b>(B)<b>=</b>(C). <b>[ type = 2, indexe +(A)-, indexe -(B)=, indexe =(C) ]</b> </li>
     *      <li>[ <b>=X </b> ] . -> <b>X</b>(A)<b>=</b>(B) ........ <b>[ type = 3, indexe X(A)=, indexe =(B) ]</b> </li>
     *      <li>[ <b>=+ </b> ] . -> <b>+</b>(A)<b>=</b>(B) ........ <b>[ type = 4, indexe +(A)=, indexe =(B) ]</b> </li>
     *      <li>[ <b>:-+</b> ] . -> . <b>:</b>(A)<b>-</b>(B)<b>+</b> ...... <b>[ type = 5, indexe :(A)-, indexe -(B)+ ]</b> </li>
     *  </ul>
     */
    public ArrayList<int[]> obtenirSystèmesConjugués(int indexe){
        ArrayList<int[]> retour = new ArrayList<>();
        for (int i = 0; i < systèmesConjugués.size(); i++) {
            for (int j = 1; j < systèmesConjugués.get(i).length; j++) {
                if(systèmesConjugués.get(i)[j]==indexe){
                    retour.add(systèmesConjugués.get(i));
                    break;
                }
            }
        }

        return retour;
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
