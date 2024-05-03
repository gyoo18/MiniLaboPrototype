package com.MiniLabo.prototype;
import java.lang.invoke.MethodHandles.Lookup.ClassOption;
import java.util.ArrayList;
import java.util.Comparator;

import javax.swing.text.Position;

public class Atome{
    //État de l'atome
    public Vecteur3D prevPosition = null;                       //Position de l'atome à temps t-1
    public Vecteur3D position = new Vecteur3D(0,0,0);   //Position présente de l'atome
    public Vecteur3D vélocité = new Vecteur3D(0,0,0);     //Vélocité présente
    private Vecteur3D vélocitéMoyenne = new Vecteur3D(0);   //Vélocité moyenne sur le temps de l'atome.
    private double facteurMixVélMoyen = 0.5;                  //Facteur de mélange entre les vieilles vélocités et la nouvelle
    public Vecteur3D Force = new Vecteur3D(0);              //Force appliquée présentement

    public int NP;                      //Nombre de protons. Définis le type d'atomes.
    public int NE;                      //Nombre d'électrons.
    public double m;                    //Masse de l'atome
    public double charge = 0;           //Charge de l'atome
    public float électronégativité = 0; //Électronégativité de l'atome peut varier avec le nombre d'électrons

    public int indexe = -1; //Indexe de cet atome  dans la liste de la simulation

    //État des doublets
    public ArrayList<Vecteur3D> positionDoublet = new ArrayList<>(); //Position des doublets relatif au noyau
    public ArrayList<Vecteur3D> prevPosDoublet  = new ArrayList<>();  //Position des doublets à temps t-1
    public ArrayList<Vecteur3D> vélDoublet  = new ArrayList<>();     //Vélocité des doublets
    public ArrayList<Vecteur3D> forceDoublet  = new ArrayList<>();    //Force appliqué sur les doublets

    //État des liaisons
    /**  Indexe des atomes liés à cet atome. Chaque case représente une possibilité de liaison. 
    * Une liaison double ou triple utilisera deux et trois cases respectivement. 
    * Est -1 s'il n'y a aucune liaison.*/
    public ArrayList<Integer> liaisonIndexe  = new ArrayList<>();
    public ArrayList<Boolean> liaisonType  = new ArrayList<>();   // Types de liaisons. Sigma = faux, Pi = vrai
    public ArrayList<Integer> liaisonOrdre = new ArrayList<>();   // Ordres de liaisons. 1 = liaison simple, 2 = liaison double, 3 = liaison triple
    public int doublets;            // Nombre de doublets électroniques
    public double rayonCovalent;    // Rayon covalent d'ordre 1 sur cet atome.




    /**Liste des systèmes conjugués dont l'atome fait partis.
     * <p>Ordonnés comme suit : </p>
     * système[0] = Indexe du type de système.
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
     */
    public ArrayList<int[]> systèmesConjugués = new ArrayList<>(); //Liste des systèmes conjugués dont l'atome fait partis.
    
    /**Molécule dont fait partis cet atome */
    public Molécule molécule;

    /**Valeure maximale du nombre quantique princial n. Indique le nombre le lignes du tableau périodique permises. */
    private final int MAX_N = 3;    //Nombre principal maximal. Indique le nombre de ligne du tableau prériodique utilisé.
    /**Nombre maximal de cases quantiques. Dépendend de <b><code>MAX_N</code></b>. */
    private final int MAX_CASE = (MAX_N*(MAX_N+1)*(2*MAX_N+1))/6 - 1;   //Nombre maximal de cases quantiques

    /**Représentation des cases quantiques. Chaque case peut contenir un ou deux électrons. <b>0</b> = 0 électrons, <b>1</b> = 1 électron, <b>2</b> = 2 électrons. */
    private int[] cases;


    /**
     * List boolean des force en jeu dans la simulation
     * Paulie, Vanderwal, Electrique, Morse, Torsion, Diedre
     */
    public static boolean[] ListForce = {
        true, //Force Paulie
        true, //Force Vanderwal
        true, //Force électrique
        true, //Force de Morse
        true, //Force de Torsion
        true, //Force Diedre
        

    };

    /**
     * Créé un nouvel atome.
     * @param nombreProton - Nombre de proton de cet atome. Définit l'élément qu'il représente.
     * @param indexe - Indexe de cet atome dans liste de la simulation
     */
    public Atome(int nombreProton){
        if(!initialiséListes){
            initialiserListes();
        }
        NP = nombreProton;
        m = (double)NP*2.0*mP; //Calculer la masse de l'atome. La masse des électrons est négligeable.
        rayonCovalent = rayonsCovalents[NP-1]/100f; //Rayon covalent de l'atome. Les données sont en pm et nous travaillons en Å

        cases = new int[MAX_CASE+1]; //Initialiser les cases quantiques
        for (int i = 0; i < NP; i++) {
            ajouterÉlectron(); // Ajouter les électrons dans les cases
        }
        charge = 0;
        évaluerValence(); //Extraire certaines propriétés de l'atome à partir de la couche de valence

        //calculerÉlectronégativitée();

        molécule = new Molécule();  //Initialise la molécule
        molécule.ajouterAtome(this);//Ajoute cet atome à la molécule
    }

    /**Initialisation de l'atome uniquement utilisée lorsqu'on le copie*/
    private Atome(){}

    /**Initialise les listes de données qui en ont besoins */
    private void initialiserListes(){
        initialiséListes = true;
        //Initialiser fréquenceTorsion
        for (int i = 0; i < fréquenceTorsionDonnées.length; i++) {
            //Placer les valeurs de fréquenceTorsion aux bons endroits
            //Les indexes des deux atomes d'extrémités peuvent être interchangé et doivent donc être initialiser dans les deux sens
            fréquenceTorsion[(int)fréquenceTorsionDonnées[i][1]-1][(int)fréquenceTorsionDonnées[i][2]-1][(int)fréquenceTorsionDonnées[i][3]-1] = fréquenceTorsionDonnées[i][0];
            fréquenceTorsion[(int)fréquenceTorsionDonnées[i][1]-1][(int)fréquenceTorsionDonnées[i][3]-1][(int)fréquenceTorsionDonnées[i][2]-1] = fréquenceTorsionDonnées[i][0];
        }
    }

    /**Mise à jour de la référence à l'environnement
     * @param E - Liste des atomes de la simulation
    */
    public static void MettreÀJourEnvironnement(ArrayList<Atome> E){
        Environnement = E;
    }

    /**Retourne les forces appliqués sur l'atome A
     * @param a - Atome sur lequel appliquer les forces 
    */
    public static void ÉvaluerForces(Atome A){

        //Forces découlant des interractions avec les atomes non-liés
        for (int i = 0; i < Environnement.size(); i++) {
            //Pour tout les atomes
            if(Environnement.get(i) == A){
                //Si l'autre atome (A') est cet atome (A), sauter au prochain atome
                continue;
            }

            Atome APrime = Environnement.get(i);
            Vecteur3D dir = V3.norm( V3.sous(A.position,APrime.position) ); //Vecteur direction vers l'autre atome (A')
            double dist = V3.distance(APrime.position, A.position); //Distance entre A et A'

            if( true ){ // dist <2.0*(A.rayonCovalent+APrime.rayonCovalent)
                //Si A' se situe à moins de N rayons covalents de A
                if (ListForce[0]){
                    A.Force.addi( ForcePaulie(A.rayonCovalent,APrime.rayonCovalent, dist, dir)); //Appliquer la force de Pauli   
                }
                if (ListForce[1]){
                    A.Force.addi( ForceVanDerWall(A.NP, A.indexe, APrime.NP, APrime.indexe, dist, dir)); //Appliquer les forces de Van der Walls
                }
                if (ListForce[2]){
                    A.Force.addi( ForceÉlectrique(A.charge, APrime.charge,dist,dir)); //Appliquer la force électrique
                }

                //Forces des doublets d'A' sur A
                for (int j = 0; j < APrime.forceDoublet.size(); j++) {
                    dir = V3.norm(V3.sous( A.position,V3.addi(APrime.positionDoublet.get(j), APrime.position))); //Vecteur de direction vers l'autre atome (A')
                    dist = V3.distance(A.position, V3.addi(APrime.positionDoublet.get(j), APrime.position)); //Distance entre le doublet et A'
                    if (ListForce[0]){
                        A.Force.addi( ForcePaulie(A.rayonCovalent,APrime.rayonCovalent/4.0, dist, dir)); //Appliquer la force de Pauli
                    }
                    
                    if (ListForce[2]){
                        boolean voisin=false;
                         for (int i1=0; i1<A.liaisonIndexe.size(); i1++){
                            if(A.liaisonIndexe.get(i1)==-1){
                                continue;
                            }
                            if (APrime == Environnement.get(A.liaisonIndexe.get(i1))){
                             voisin=true;   
                                
                            }
                        }
                        if (!voisin){
                            A.Force.addi( ForceÉlectrique(A.charge, -2.0,dist,dir)); //Appliquer la force électrique
                        }
                        
                    }
                    
                }

                //Forces de A' sur les doublets
                for (int j = 0; j < A.forceDoublet.size(); j++) {

                    Vecteur3D eDir = V3.norm(V3.sous(V3.addi(A.positionDoublet.get(j), A.position),APrime.position)); //Vecteur de direction vers l'autre atome (A')
                    double eDist = V3.distance(V3.addi(A.position,A.positionDoublet.get(j)), APrime.position); //Distance entre le doublet et A'
                    if (ListForce[0]){
                        A.forceDoublet.get(j).addi( ForcePaulie(A.rayonCovalent/4.0,APrime.rayonCovalent, eDist, eDir)); //Appliquer la force de Pauli 
                    }
                    
                    if (ListForce[2]){
                        boolean voisin=false;
                         for (int i1=0; i1<A.liaisonIndexe.size(); i1++){
                            if(A.liaisonIndexe.get(i1)==-1){
                                continue;
                            }
                            if (APrime == Environnement.get(A.liaisonIndexe.get(i1))){
                             voisin=true;   
                            
                            }
                        } 
                        if (!voisin){
                            A.forceDoublet.get(j).addi( ForceÉlectrique(-2.0, APrime.charge,eDist,eDir) ); //Appliquer la force électrique
                        }
                    }
                  

                    //Forces des doublets de A' sur les doublets de A
                    for (int k = 0; k < APrime.forceDoublet.size(); k++) {
                        eDir = Vecteur3D.norm(Vecteur3D.sous(Vecteur3D.addi(A.positionDoublet.get(j), A.position),Vecteur3D.addi(APrime.positionDoublet.get(k), APrime.position))); //Vecteur de direction vers l'autre atome (A')
                        eDist = Vecteur3D.distance(Vecteur3D.addi(A.position,A.positionDoublet.get(j)), Vecteur3D.addi(APrime.positionDoublet.get(k), APrime.position)); //Distance entre le doublet et A'
                        if (ListForce[0]){
                            A.forceDoublet.get(j).addi( ForcePaulie(A.rayonCovalent/4.0,APrime.rayonCovalent/4.0, dist, dir)); //Appliquer la force de Pauli
                        }
                       
                        if (ListForce[2]){
                           
                            A.forceDoublet.get(j).addi(  ForceÉlectrique(-2, -2,eDist,eDir) ); //Appliquer la force électrique
                            
                        }
                        
                    }
                }
            }
        }

        for (int j = 0; j < A.forceDoublet.size(); j++) {
            for (int k = 0; k < A.forceDoublet.size(); k++) {
                //Si On regarde le même doublet, passer au prochain
                if(k==j){continue;}

                Vecteur3D eDir = Vecteur3D.norm(Vecteur3D.sous(A.positionDoublet.get(j),A.positionDoublet.get(k))); //Vecteur de direction vers l'autre doublet
                double eDist = Vecteur3D.distance( A.positionDoublet.get(j), A.positionDoublet.get(k)); //Distance entre le doublet et lautre doublet
                if (ListForce[2]){
                    A.forceDoublet.get(j).addi( ForceÉlectrique(-2, -2, eDist, eDir)); //Appliquer la force électrique entre les deux doublet   
                }
                
            }
        }

        //Compter le nombre de liens formés par A
        int nLiens = 0;
        boolean[] traité = new boolean[A.liaisonIndexe.size()];
        for (int j = 0; j < A.liaisonIndexe.size(); j++) {
            if(A.liaisonIndexe.get(j) != -1 && !traité[j] && !A.liaisonType.get(j)){
                //Si la liaison existe,
                //On ne prend que la liaison sigma pour éviter de la compter plus d'une fois.
                nLiens++;
                traité[j] = true;
            }
        }

        //Forces de liaisons
        for(int i = 0; i < A.liaisonIndexe.size(); i++){
            //Pour toutes les liaisons

            //S'il n'y a pas de liaison, sauter à la prochaine
            if(A.liaisonIndexe.get(i) == -1){
                continue;
            }
            //Si le type de liaison est pi, passer à la prochaine. Cela assure que les liaisons multiples ne sont traités qu'une fois.
            if(A.liaisonType.get(i)){
                continue;
            }
            
            //Évaluer le nombre de liaison existantes entre A et A'
            int liaisonOrdre = A.liaisonOrdre.get(i);
            Atome Ai=Environnement.get(A.liaisonIndexe.get(i));

            Vecteur3D dir = V3.norm( V3.sous(A.position, Ai.position) ); //Vecteur de direction qui pointe vers l'autre atome (A')
            double dist = V3.distance(Ai.position, A.position); //Distance entre A et A'
            if (ListForce[3]){
                A.Force.addi( ForceDeMorse(dist, dir, liaisonOrdre, A.NP, Ai.NP) ); //Appliquer la force de Morse
            }
            
            //si force torsion
            if (ListForce[4]){
                //Appliquer la force de torsion avec tout les autres liens
                for(int j = 0; j < A.liaisonIndexe.size(); j++){
                    //Pour toutes les liaisons de A
                    //Si la liaison n'existe pas, qu'elle est cette liaison ou qu'elle est pi (pour éviter de la compter plus d'une fois), passer à la prochaine
                    if(A.liaisonIndexe.get(j) == -1 || A.liaisonIndexe.get(i) == A.liaisonIndexe.get(j) || A.liaisonType.get(j)){
                        //Si la liaison n'existe pas ou qu'elle est celle que nous évaluons en ce moment, passer à la prochaine
                        continue;
                    }
                    Atome Aj=Environnement.get(A.liaisonIndexe.get(j));

                    Vecteur3D Force = ForceTorsion(Ai, A, Aj);
                    
                    
                    Ai.Force.addi(Force); //Appliquer force de torsion à IA
                    A.Force.addi(Force.opposé());
                    //Aj.Force.addi(Force.opposé());
                    //A.forceDoublet.get(j).addi(Force.opposé()); //Appliquer force au doublet
                    
                }

                //Torsion Atome-Doublet
                for(int j = 0; j < A.positionDoublet.size(); j++){
                    Vecteur3D force = ForceTorsion(Ai,A,j); //Calculer force de torsion appliquer sur Ai
                    Vecteur3D forceDoublet = ForceTorsion(j,A,Ai); //Calculer force de torsion appliquer sur le doublet
                    Ai.Force.addi(force); //Appliquer force de torsion à A'
                    A.Force.addi(force.opposé());
                    A.forceDoublet.get(j).addi(forceDoublet); //Appliquer force au doublet
                    A.Force.addi(forceDoublet.opposé());
                }
            }
        }
        
        if (ListForce[4]){
            //Force de torsion entre les doublets
            for (int i = 0; i < A.positionDoublet.size(); i++) {
                for(int j = 0; j < A.positionDoublet.size(); j++){
                    //Si on regarde le même doublet, passer au prochain
                    if(i==j){continue;} 
                    Vecteur3D forceDoublet= ForceTorsion(i,A,j);               
                    A.forceDoublet.get(i).addi(forceDoublet); //Appliquer force au doublet i
                    A.Force.addi(forceDoublet.opposé());
                }
            }

        }

        //Force Dièdre
        for(int i = 0; i < A.liaisonIndexe.size(); i++){
         if (ListForce[5]){
            //Pour toutes les liaisons

            //S'il n'y a pas de liaison, sauter à la prochaine
            if(A.liaisonIndexe.get(i) == -1){
                continue;
            }
            //Si le type de liaison est pi, passer à la prochaine. Cela assure que les liaisons multiples ne sont traités qu'une fois.
            if(A.liaisonType.get(i)){
                //System.out.println(1450491);
            }
            Atome Ai =Environnement.get(A.liaisonIndexe.get(i));  
            
            //int liaisonOrdre = A.liaisonOrdre.get(i);  //Évaluer le nombre de liaison existantes entre A et A1, pas important pour Ai, mais pour Aj

            for ( int j=0; j < A.liaisonIndexe.size(); j++ ){
                
                if(A.liaisonIndexe.get(j) == -1){
                    continue;
                }
                if(A.liaisonIndexe.get(j) == A.liaisonIndexe.get(i)){
                    continue;
                }
                Atome Aj =Environnement.get(A.liaisonIndexe.get(j));
                for ( int k=0; k < Aj.liaisonIndexe.size(); k++){
                    if(Aj.liaisonIndexe.get(k) == -1){
                        continue;
                    }
                    if(Environnement.get(Aj.liaisonIndexe.get(k)) == A){
                        continue;
                    }
                    Atome Ak =Environnement.get(Aj.liaisonIndexe.get(k));

                    Ai.Force.addi(ForceDiedre(Ai, A, Aj, Ak));
                    A.Force.addi(ForceDiedre(Ai,A,Aj,Ak).opposé());
                    //System.out.println(V3.distance(ForceDiedre(Ai, A, Aj, Ak),new V3(000)));
                }
            }
         }
        }

        double ModuleFriction = -0.00000000000001;
        A.Force.addi( V3.mult(A.vélocité,ModuleFriction)); //Appliquer une force de friction
        //A.Force.addi(new Vecteur3D(0,-1,0.0)); //Appliquer une force de gravité
        for (int i = 0; i < A.positionDoublet.size(); i++) {
            A.forceDoublet.get(i).addi(V3.mult(A.vélDoublet.get(i),ModuleFriction));
        }

        //Appliquer les forces des doublets sur l'atome.
        for (int i = 0; i < A.positionDoublet.size(); i++) {
            //Vecteur3D force = A.forceDoublet.get(i).opposé();
            double Sin0;
            Vecteur3D aT;
            if(A.forceDoublet.get(i).longueur() > 0){
                Sin0 = V3.croix(A.positionDoublet.get(i),A.forceDoublet.get(i)).longueur()/(A.positionDoublet.get(i).longueur()*A.forceDoublet.get(i).longueur());
                aT = V3.norm(V3.sous(A.forceDoublet.get(i),V3.mult(A.positionDoublet.get(i),V3.scal(A.positionDoublet.get(i), A.forceDoublet.get(i))/(A.positionDoublet.get(i).longueur()*A.positionDoublet.get(i).longueur()))));
            }else{
                Sin0 = 0.0;
                aT = new Vecteur3D(0);
            }
            A.Force.addi(V3.mult(V3.addi(A.forceDoublet.get(i), V3.mult(aT.opposé(),A.forceDoublet.get(i).longueur()*Sin0)),(A.m-2.0*mE)/(A.m)));
            A.forceDoublet.set(i,V3.mult(V3.addi(A.forceDoublet.get(i), V3.mult(aT,((A.m-2.0*mE)*A.forceDoublet.get(i).longueur()*Sin0/(2.0*mE)))),(2.0*mE)/(A.m)));
        }

        //Mettre à jour la vélocité moyenne
        if(A.vélocitéMoyenne.longueur()!=0.0){
            A.vélocitéMoyenne = Vecteur3D.addi(Vecteur3D.mult(A.vélocité,(1.0-A.facteurMixVélMoyen)), Vecteur3D.mult(A.vélocitéMoyenne, A.facteurMixVélMoyen));
        }else{
            A.vélocitéMoyenne = A.vélocité.copier();
        }
        
    }

    /**
     * Renvoie un vecteur qui représente la force électrique entre deux particules
     * @param q1 - Charge de la première particule en nombre de charges élémentaires. Sera multiplié par la charge élémentaire e.
     * @param q2 - Charge de la deuxième particule.
     * @param r - Distance entre les deux particule en Angströms
     * @param dir - Vecteur unitaire de direction qui pointe de la deuxième charge vers la première.
     * @return - Vecteur de force en Newtons Angströmiens
     * @see <a href = "https://www.erpi.com/fr/bundle-physique-1-m-can-ens-e-man-6m-d5-9782761354998.html">Source : <i>H. Benson, M. Lachance</i> (2015) Physique I, Mécanique, 5e Édition</a>
     */
    private static Vecteur3D ForceÉlectrique(double q1, double q2, double r, Vecteur3D dir){
        return ( V3.mult(dir,(K*q1*e*q2*e/Math.pow(r,2.0)) ));
    }
    
    /**
     * L'interaction de répulsion de Pauli est un phénomène quantique qui n'a pas d'équivalent physique. 
     * Cette force ne représente pas une conversion directe,seulement une approximation raisonnable.
     * Ainsi, cette fonction renvoie une force qui immite la répulsion de Pauli avec un terme de Lennard-Jones 6-12.
     * @param RayonCovalent1 - Rayon Covalent de la première particule. Utilisé pour calculer la longueur d'une liaison potentielle entre les deux particule. Cette force devrait équilibrer les forces de Van der Walls et la force électrique autour de 2 fois la longueur de liaison.
     * @param RayonCovalent2 - Rayon Covlaent de la deuxième particule.
     * @param dist - Distance entre les deux particules en Angströms.
     * @param dir - Vecteur unitaire de direction qui pointe de la deuxième particule vers la première.
     * @return Vecteur de force en Newtons Angströmiens
     * @see <h3>Sources :</h3> 
     *      <p>•<a href="https://cloudflare-ipfs.com/ipfs/QmXoypizjW3WknFiJnKLwHCnL72vedxjQkDDP1mXWo6uco/wiki/Pauli_exclusive_principle.html"><i>W. Pauli</i> (1924) Pauli Exclusion Principle</a>;</p>
     *      <p>•<a href="https://iopscience.iop.org/article/10.1088/0959-5309/43/5/301"><i>J. E. Lennard-Jones</i> (1931) Cohesion</a>;</p>
     */
    private static Vecteur3D ForcePaulie(double RayonCovalent1, double RayonCovalent2, double dist, Vecteur3D dir){
        return ( V3.mult(dir, (2.0*Math.pow(2.0*(RayonCovalent1+RayonCovalent2),13.0)/Math.pow(dist,13.0)) ));
    }
    
    /**
     * Renvoie une approximation des forces de Van der Walls.
     * @param NP - Nombre de protons du premier atome.
     * @param NPA - Nombres de protons du deuxième atome.
     * @param dist - Distance entre les deux particules en Angströms.
     * @param dir - Vecteur unitaire de direction qui pointe de la deuxième particule vers la première.
     * @return Vecteur de force en Newtons Angströmiens
     * @see <a href="https://staff.ulsu.ru/moliver/ref/corr/lond36.pdf">Source : <i>F. London</i> (1937) The General Theory of Molecular Forces</a>
     */
    private static Vecteur3D ForceVanDerWall(int NP, int indexeA, int NPA, int indexeB, double dist, Vecteur3D dir){
        //TODO #11 Implémenter moments dipolaires
        //TODO #12 Implémenter fréquence d'ionisation
        //TODO #26 Décider quelle température prendre pour Van der Walls
        double mu1 = Environnement.get(indexeA).évaluerMomentDipolaire().longueur(); //Moment dipolaire de la particule 1
        double mu2 = Environnement.get(indexeB).évaluerMomentDipolaire().longueur(); //Moment dipolaire de la particule 2
        double m = Environnement.get(indexeA).m*Environnement.get(indexeB).m/(Environnement.get(indexeA).m+Environnement.get(indexeB).m);
        double a1 = Polarisabilité[NP-1]*convPolar;  //Polarisabilité électronique de la particule 1
        double a2 = Polarisabilité[NPA-1]*convPolar;  //Polarisabilité électronique de la particule 2
        double nu1 = e/Math.sqrt(m*a1); //Fréquence d'ionisation de la particule 1
        double nu2 = e/Math.sqrt(m*a2);; //Fréquence d'ionisation de la particule 2

        ArrayList<Atome> paire = new ArrayList<>();
        paire.add(Environnement.get(indexeA));
        paire.add(Environnement.get(indexeB));

        double T = Math.max(Température(paire),1);   //Température du système en °K    //paire
        if (Double.isNaN(T)){
            System.out.println("TempératurenNan");
        }
        double Keesom = (2.0*mu1*mu1*mu2*mu2)/(3.0/*Math.pow(4.0*Math.PI*ep0*ep0,2.0)*/*kB*T);             //Forces de Keesom
        double Debye = (a1*mu2*mu2 + a2*mu1*mu1)/*Math.pow(4.0*Math.PI*ep0*ep0,2.0)*/;                   //Forces de Debye
        double London = ((3.0*h)/2.0)*((a1*a2))/*Math.pow(4.0*Math.PI*ep0*ep0,2.0))*/*((nu1*nu2)/(nu1+nu2));//Forces de London
        double module = -(Keesom + Debye + London);                                                   //Module des forces de Van der Walls. Nécessite d'implémenter les variables ci-dessus d'abords.
        return ( V3.mult(dir, 6.0*module/Math.pow(dist,7.0))); //(-(1.0*Math.pow(2.0*(rayonsCovalents[NP-1]/100.0+rayonsCovalents[NPA-1]/100.0),7.0)/Math.pow(dist,7.0)) )
    }

    /**
     * Calcule le moment dipolaire d'une molécule en évaluant les liens qu'il fait.
     * @return - Moment dipolaire de l'atome
     */
    private Vecteur3D évaluerMomentDipolaire(){
        //TODO #27 réviser évaluerMomentDipolaire()
        double chargeTotale = charge;
        chargeTotale += -2.0*doublets;
        for (int i = 0; i < liaisonIndexe.size(); i++) {
            if(liaisonIndexe.get(i) == -1){
                continue;
            }
            Atome Ap = Environnement.get(liaisonIndexe.get(i));
            chargeTotale += Ap.charge;
            chargeTotale += -2.0*Ap.doublets;
        }

        double équilibre = -chargeTotale/(liaisonIndexe.size()+1.0);

        Vecteur3D momentDipolaire = new Vecteur3D(0);
        for (int i = 0; i < liaisonIndexe.size(); i++) {
            if(liaisonIndexe.get(i) == -1){
                continue;
            }
            Atome Ap = Environnement.get(liaisonIndexe.get(i));
            Vecteur3D dir = V3.norm(V3.sous(Ap.position,position));
            double dist = V3.distance(Ap.position, position);

            momentDipolaire.addi(V3.mult(dir, dist*(Ap.charge-équilibre)));
        }

        return momentDipolaire;
    }

    /**
     * Le mécanisme de liaison de deux atome est très complexe, mais nous pouvons l'approximer avec un potentiel oscillatoire.
     * Cette fonction renvoie une force qui imite le comportement d'une particule dans un lien en utilisant le potentiel de Morse.
     * @param dist - Distance entre les deux atomes
     * @param dir - Vecteur unitaire de direction qui pointe du deuxième atome vers le premier
     * @param nLiaisons - Nombre de liaison présentes entre les deux atomes. (Liaison simple, double ou triple).
     * @param NP - Nombre de protons du premier atome.
     * @param NPA - Nombre de protons du deuxième atome.
     * @return Vecteur de force en Newtons Angströmiens.
     * @see <a href="https://journals.aps.org/pr/abstract/10.1103/PhysRev.34.57">Source : <i>P. M. Morse</i> (1929) Diatomic Molecules According to the Wave Mechanics. II. Vibrational Levels</a> Repéré sur <a href="https://en.wikipedia.org/wiki/Morse_potential">Wikipedia (2024)</a>
     *
     */
    private static Vecteur3D ForceDeMorse(double dist, Vecteur3D dir, int nLiaisons, int NP, int NPA){
        double l = 0; //Longueur de liaison
        if(nLiaisons == 1){
            l = (rayonsCovalents[NP-1] + rayonsCovalents[NPA-1] /*-9*Math.abs(AffinitéÉlectronique[NP]-AffinitéÉlectronique[NPA])*/); //Longueur d'ordre 1
        }else if(nLiaisons == 2){
            l = (rayonsCovalents2[NP-1] + rayonsCovalents2[NPA-1] /*-9*Math.abs(AffinitéÉlectronique[NP]-AffinitéÉlectronique[NPA])*/); //Longueur d'ordre 2
        }else if(nLiaisons == 3){
            l = (rayonsCovalents3[NP-1] + rayonsCovalents3[NPA-1] /*-9*Math.abs(AffinitéÉlectronique[NP]-AffinitéÉlectronique[NPA])*/);  //Longueur d'ordre 3;
        }


        l = l/100.0;    //La longueur est en pm et on travaille en Å.
        double D = ÉnergieDeDissociation[NP-1][NPA-1]*0.166053906717*Math.pow(10.0,23.0);     //Énergie de dissociation du lien. Conversion de kJ/mol en J_Å
        double p = ConstanteDeForce[NP-1][NPA-1]*10000;//10000.0;
        //Constante de force de la liaison. Est ajustée de façon ce que la force vale 1% (.99) du maximum 
        // à 2 fois la longueur de liaison, de façons à ce que quand le lien se brise, le potentiel soit 
        // quasiment identique à s'il n'était pas lié.
        double a = Math.sqrt(p/(2.0*D));
        double module = -D*(-2.0*a*Math.exp(-2.0*a*(dist-l)) + 2.0*a*Math.exp(-a*(dist-l))); //Appliquer la force de morse

        return ( Vecteur3D.mult(dir,module) );
    }
    
    /**
     * Renvoie la force de torsion à appliquer sur I dans le système I-K-J, où K est l'atome central.
     * @param IAxe - Vecteur directeur de K vers I
     * @param JAxe - Vecteur directeur de K vers J
     * @param mI - Masse de I
     * @param mJ - Masse de J
     * @param NBLiens - Nombre de liens que K forme
     * @param NBDoublets - Nombre de doublets que K possède
     * @param K - Nombre de protons de K
     * @param I - Nombre de protons de I. Mettre -1 si c'est un doublet.
     * @param J - Nombre de protons de J. Mettre -1 si c'est un doublet.
     * @return - Vecteur de force en newtons angstromiens
     */
    private static Vecteur3D ForceTorsion(Vecteur3D IAxe, Vecteur3D JAxe, double mI, double mJ, int NBLiens, int NBDoublets, int K, int I, int J){
        //Compter le nombre de liens formés par A
        Vecteur3D PlanAjAi= new Vecteur3D( V3.croix(JAxe,IAxe));
        
        Vecteur3D potdirection = new Vecteur3D(  V3.norm(V3.croix(IAxe,PlanAjAi)));

        double angle = Math.acos(Math.min(Math.max(V3.scal(V3.norm(IAxe), V3.norm(JAxe)),-1),1));
        double angle0; //Angle à l'équilibre entre I et J
        //Chercher l'angler à l'équilibre entre I et J //sela prend til en compte les doublets?
        switch(NBLiens+NBDoublets){
            case 2:
                angle0 = Math.PI;
                break;
            case 3:
                angle0 = 2.0*Math.PI/3.0;
                break;
            case 4:
                angle0 = 73.0*Math.PI/120.0;
                break;
            default:
                angle0 = angle;
            //  System.err.println("Force de torsion : le nombre de liens n'est pas 2,3 ou 4");
                break;
        }
        double Kij;
        if(I == -1 || J == -1){
            Kij = 10000.0;
        }else{
            double nbOndeFondamental = fréquenceTorsion[K-1][I-1][J-1]*Math.pow(10.0,-8.0); //nombre d'onde fondamental en Å^-1
            if(nbOndeFondamental == 0.0){
                nbOndeFondamental = 0.0;
            }
            double fréquenceFondamentale = c*nbOndeFondamental; //Fréquence fondamentale en Hz
            double masse = 1.0/((1.0/mI)+(1.0/mJ));
            Kij = Math.pow(fréquenceFondamentale,2.0)*masse*1000.0; //Force du ressort angulaire
        }

        double D0 = angle0-angle; //Delta theta      
        //TODO doublet angle different doublet doublet, doublet atome
        return ( Vecteur3D.mult(potdirection, -D0*Kij ));
    }
    
    private static Vecteur3D ForceTorsion(Atome Ai, Atome A, Atome Aj){
        Vecteur3D IAxe = Vecteur3D.sous(Ai.position,A.position);
        Vecteur3D JAxe = Vecteur3D.sous(Aj.position,A.position);
        return ForceTorsion(IAxe, JAxe, Ai.m, Aj.m, A.liaisonIndexe.size(), A.doublets, A.NP, Ai.NP, Aj.NP);
    }
   
    private static Vecteur3D ForceTorsion(Atome Ai, Atome A,  int Doubletj){
       Vecteur3D IAxe = Vecteur3D.sous(Ai.position,A.position);
       return ForceTorsion(IAxe, A.positionDoublet.get(Doubletj), Ai.m, 2.0*mE, A.liaisonIndexe.size(), A.doublets, A.NP, Ai.NP, -1);
    }

    private static Vecteur3D ForceTorsion(int Doubleti, Atome A,  Atome Aj){
        Vecteur3D JAxe = Vecteur3D.sous(Aj.position,A.position);
        return ForceTorsion(A.positionDoublet.get(Doubleti), JAxe, 2.0*mE, Aj.m, A.liaisonIndexe.size(), A.doublets, A.NP, -1, Aj.NP);
     }
   
    private static Vecteur3D ForceTorsion(int Doubleti, Atome A,  int Doubletj){
       return ForceTorsion(A.positionDoublet.get(Doubleti), A.positionDoublet.get(Doubletj), 2.0*mE, 2.0*mE, A.liaisonIndexe.size(), A.doublets, A.NP, -1, -1);
    }
  

    private static Vecteur3D ForceDiedre(Atome Ai, Atome A, Atome Aj, Atome Ak){
        double ConstanteDeForce=1000*Math.pow(10,20)*Math.pow(6.022,-1)*Math.pow(10 ,-23 );
        double sens = 1.0;
        Vecteur3D PlaniAj= new Vecteur3D( V3.croix(V3.sous(Aj.position ,A.position), V3.sous(Ai.position,A.position)));
        Vecteur3D PlanAjk= new Vecteur3D( V3.croix(V3.sous(Aj.position ,A.position ), V3.sous(Ak.position ,Aj.position)));
        if(Vecteur3D.mixte(PlaniAj, V3.sous(Aj.position ,A.position ), PlanAjk)>0 ){
            sens=1.0;
        }
        if(Vecteur3D.mixte(PlaniAj, Vecteur3D.sous(Aj.position ,A.position ), PlanAjk)<0 ){
            sens=-1.0;
           
        }
        if(Vecteur3D.mixte(PlaniAj, Vecteur3D.sous(Aj.position ,A.position ), PlanAjk)==0 ){
            sens=0.0;
        }
        
        double AngleO=1.0*Math.PI;
        Vecteur3D direction = new Vecteur3D(Vecteur3D.norm(PlaniAj));
        double iAjxAjk = V3.scal(V3.norm(PlaniAj),V3.norm(PlanAjk));
        double Angle= Math.acos(Math.min(Math.max(iAjxAjk,-1.0),1.0));
        double FDiedre=0.0;
        Boolean Liasondouble = false;
        for (int j=0; j < A.liaisonIndexe.size();j++){
            if (A.liaisonIndexe.get(j)!=-1){
                if (Environnement.get(A.liaisonIndexe.get(j))==Aj){
                    if (A.liaisonOrdre.get(j)==2){
                      Liasondouble=true;
                        if (Angle>0.5*Math.PI){
                            //A.briserLien(j);
                           // System.out.println(33);
                           
                        }
                    } 
                }
            }
           
        }
        if (Liasondouble){
            AngleO=0.5*Math.PI; //1/2 = 0.5 sa faisait bugger :()
            ConstanteDeForce=10*Math.pow(10,20)*Math.pow(6.022,-1)*Math.pow(10 ,-23 );
            double AngleP=1.0/AngleO;
            double AngleX=Angle*(2/Math.PI);
            if (AngleO-Angle==0){
               FDiedre=0;
            } else{

                //FDiedre = -sens*ConstanteDeForce*Math.pow((AngleO-Angle),-1);
                FDiedre = -sens*ConstanteDeForce*Math.pow(Math.pow(1-AngleX,-1)-AngleX+1,1);
            }
            if (FDiedre>10000){
                System.out.println(33);
            }
        
        
        } else {
            FDiedre = sens*ConstanteDeForce*(Math.pow((( Math.pow(AngleO,2) -  Math.pow(Angle,2) ) / ( 4.0*Math.pow(Math.PI,2) ) ),2));
        }


       
        
        
        
        //double FDiedre = sens*ConstanteDeForce*(Math.pow((1- ( Math.pow(Angle,2) ) / ( 4*Math.pow(Math.PI,2) ) ),-2));
        //double FDiedre = sens*ConstanteDeForce*((Math.PI-Angle));
       // double FDiedre = sens*ConstanteDeForce*2*((Angle)) / ( 4*Math.pow(Math.PI,2) )*(1- ( Math.pow(Angle,2) ) / ( 4*Math.pow(Math.PI,2) ) );
        /* if (Double.isNaN(FDiedre)){
            FDiedre=0;
        } */
       return ( Vecteur3D.mult(direction, FDiedre ));
       
    }

    /**Applique des contraintes de mouvement, comme des bords de domaines.*/
    public void ÉvaluerContraintes(){
        //TODO #10 Le rebond de Verlet perd toujours de l'énergie
        //Appliquer des bords de domaine
        //Rebondir en Y
        if(Math.abs(position.y) > (double)App.TailleY/(2.0*App.Zoom)){
            position.y = Math.signum(position.y)*(double)App.TailleY/(2.0*App.Zoom); //Contraindre la position
            vélocité.y = -vélocité.y; //Inverser la vitesse
            if(prevPosition != null){
                //prevPosition= Vecteur3D.addi(prevPosition, new Vecteur3D(0,2*(position.y-prevPosition.y),0) ); //Inverser la vitesse de Verlet
            }
        }
        //Rebondir en X
        if(Math.abs(position.x) > (double)App.TailleX/(2.0*App.Zoom)){
            position.x = Math.signum(position.x)*(double)App.TailleX/(2.0*App.Zoom); //Contraindre la position
            vélocité.x = -vélocité.x; //Inverser la vitesse
            if(prevPosition != null){
                //prevPosition= Vecteur3D.addi(prevPosition, new Vecteur3D(2*(position.x-prevPosition.x),0,0)); //Inverser la vitesse de Verlet
            }  
        }
        //Rebondir en Z
        if(Math.abs(position.z) > (double)App.TailleZ/(2.0*App.Zoom)){
            position.z = Math.signum(position.z)*(double)App.TailleZ/(2.0*App.Zoom); //Contraindre la position
            vélocité.z = -vélocité.z; //Inverser la vitesse
            if(prevPosition != null){
                //prevPosition= Vecteur3D.addi(prevPosition, new Vecteur3D(0,0,2*(position.z-prevPosition.z)) ); //Inverser la vitesse de Verlet
            }
        }

        //Conserver la même distance entre les doublets et l'atome
        for (int i = 0; i < forceDoublet.size(); i++) {
            positionDoublet.set(i, V3.mult(V3.norm(positionDoublet.get(i)), rayonCovalent));//Contraindre la position et la position précédente
            prevPosDoublet.set(i, V3.mult(V3.norm(prevPosDoublet.get(i)), rayonCovalent)); 
            //Retirer la vitesse centripède
            if(vélDoublet.get(i).longueur() > 0){
                vélDoublet.set(i, V3.sous(vélDoublet.get(i), V3.mult( positionDoublet.get(i), V3.scal(vélDoublet.get(i), positionDoublet.get(i))/(positionDoublet.get(i).longueur()*positionDoublet.get(i).longueur()) ) ));
                //vélDoublet[i] = V3.mult(V3.norm(vélDoublet[i]), Math.min(vélDoublet[i].longueur(), 10000000000000.0));
            }
        }
    }

    /**Ajouter un électron aux cases quantiques (en mode hybridé)*/
    public void ajouterÉlectron(){
        int Qn = 1; //Nombre quantique principal n
        int Ql = 0; //Nombre quantique azimutal l
        int Qm = 0; //Nombre quantique magnétique m
        boolean Qs = false; //Spin
        int casesIndexe = 0; //Curseur indexe de la case quantique
        NE++;   //Ajoute un électron
        m += mE; //Ajoute à la masse
        charge --; //Modifie la charge

        //Traverse la liste des cases
        for (int i = 0; i < MAX_N; i++) {
            //Traverser n de 1 à MAX_N
            Qs = false;
            int cItmp = casesIndexe;
            for(int j3 = 0; j3 < 2; j3++){
                //Passer deux fois, la première en ajoutant les spin up, puis les spins down
                Ql = 0;
                casesIndexe = cItmp;// Si on est au deuxième tour, reprendre au début de la couche
                for (int j = 0; j < Qn; j++) {
                    //Traverser les l de 0 à n-1
                    Qm = -Ql;
                    for (int j2 = 0; j2 < 2*Ql+1; j2++) {
                        //Traverser les m de -l à l
                        if(cases[casesIndexe] == j3 && Ql != 2){
                            //Si la case n'est pas remplie et qu'elle est égale à 0 si on est au premier tours, ou à 1 au deuxième tour,
                            cases[casesIndexe]++; //Ajouter l'électron dans la case
                            //System.out.println("n: " + Qn + ", l: " + Ql + ", m: " + Qm + ", s: " + Qs); //Debug
                            //Sortir de la boucle
                            i = 4;
                            j = 2*Qn;
                            j2 = 2*Ql;
                            j3 = 3;
                            break;
                        }
                        //Sinon,
                        Qm++; //Prochain m
                        casesIndexe++; //Prochaine case
                    }
                    Ql++; //Prochain l
                }
                Qs = true; //Deuxième tour, spin down
            }
            Qn++; //Prochain n
        }

        //Rafraîchir l'électronégativité
        calculerÉlectronégativitée();
    }

    /**Retirer un électron aux cases quantiques (en mode hybridé)*/
    public void retirerÉlectron(){
        int Qn = MAX_N; //Nombre quantique principal n. On doit traverser les cases à l'envers, donc on commence au MAX_N
        int Ql = Qn-1;  //Nombre quantique azimutal l.
        int Qm = Ql;    //Nombre quantique magnétique m.
        int casesIndexe = MAX_CASE; //Curseur indexe des cases quantique. On doit traverser les cases à l'envers, donc on commence à MAX_CASE
        int cas = 0;    //Indique dans quel situation nous nous trouvons.
        NE--;           //Retire un électron
        m -= mE;        //Retire la masse
        charge ++;      //Modifie la charge

        for (int i = 0; i < MAX_N; i++) {
            //Traverse tout les n de MAX_N à 1
            Ql = Qn-1;
            int indexeDébut = casesIndexe;  //Conserver l'indexe de la première case de la couche
            cas = cases[casesIndexe];       //Évaluer dans quel cas nous nous trouvons
            if(cas == 2){
                //Si la case est pleine, c'est la première case pleine et c'est celle à laquelle il faut retirer un électron
                cases[casesIndexe]--;
                //System.out.println("n: " + Qn + ", l: " + Ql + ", m: " + Qm + ", s: -1!"); //Debug
                //Sortir de la boucle
                i = 4; break;
            }else{
                //Si la case n'est pas pleine, il faut évaluer toutes les autres cases de la couche
                //pour évaluer si nous avons A) une couche vide B) une couche contenant des spin up
                // C) une couche pleine de spin up D) une couche contenant des spin down
                for (int j = 0; j < Qn; j++) {
                    //Traverser les l de n-1 à 0
                    Qm = Ql;
                    for (int j2 = 0; j2 < 2*Ql+1; j2++) {
                        //Traverser les m de l à -l
                        if(cases[casesIndexe] == cas+1){
                            //Si la première case était vide et que celle-ci a un électron
                            //ou que la première avait un électron et que celle-ci en a deux
                            cases[casesIndexe]--;   //Retirer un électron de cette case
                            //System.out.println("n: " + Qn + ", l: " + Ql + ", m: " + Qm + ", s: -1!");    //Debug
                            //Sortir de la boucle
                            cas = 0;
                            i = 4; j = 2*Qn; break;
                        }
                        //Sinon,
                        casesIndexe--;  //Prochaine case
                        Qm--;           //Rpochain m
                    }
                    Ql--;   //Prochain l
                }
                if(cas == 1){
                    //Si on a passé à travers tous les l sans retirer d'électrons et que la première case n'avait qu'un électron
                    cases[indexeDébut]--; //Retirer un électron à la première case
                    //System.out.println("n: " + Qn + ", l: " + Ql + ", m: " + Qm + ", s: -1!");
                }
            }
            Qn--; //Prochain n
        }

        calculerÉlectronégativitée();
    }

    /**Extrait certaines informations de la couche de valence*/
    public void évaluerValence(){
        //Évalue le nombre liaisons possibles
        int n = 0;
        int essais = 0;
        for (int i = 0; i < cases.length; i++) {
            //Pour toutes les cases
            if(cases[i] == 1){
                n++; //S'il n'y a qu'un électron dans la case, elle peut former un lien
            }
        }
        while (liaisonIndexe.size()<n) {
            liaisonIndexe.add(-1);
            liaisonType.add(false);
            liaisonOrdre.add(-1);
        }
        while (liaisonIndexe.size()>n) {
            for (int n1 = 0; n1 < liaisonIndexe.size(); n1++) {
                if ( liaisonIndexe.get(n1) == -1){
                    liaisonIndexe.remove(n1);
                    liaisonType.remove(n1);
                    liaisonOrdre.remove(n1);
                    break;
                }
            }
        }

        //Évaluer le nombre de doublets
        charge -= doublets*2.0;
        doublets =0;
        int Qn = MAX_N;     //Nombre quantique principal n. On doit traverser les cases à l'envers, donc on commence à MAX_N
        int Ql = 0;         //Nombre quantique azimutal l.
        int Qm = 0;         //Nombre quantique magnétique m.
        boolean trouvéNiveau = false;   //Indique si on a trouvé le niveau de la couche de valence
        int casesIndexe = MAX_CASE;     //Curseur indexe des cases quantiques
        for (int i = 0; i < MAX_N; i++) {
            //Traverser n de MAX_N à 1
            Ql = Qn-1;
            for (int j = 0; j < Qn; j++) {
                //Traverser l de n-1 à 0
                Qm = Ql;
                for(int j2 = 0; j2 < 2*Ql+1; j2++){
                    //Traverser m de l à -l
                    if(cases[casesIndexe] != 0){
                        //Si la case n'est pas vide, c'est la première case  non-vide et donc elle appartient à la couche de valence
                        trouvéNiveau = true;
                    }
                    if (cases[casesIndexe] == 2 && trouvéNiveau) {
                        //Si la case possède 2 électrons et qu'elle fait partie de la couche de valence
                        doublets++; //Ajouter un doublet
                        
                    }
                    casesIndexe--; //Prochaine case
                    Qm--;          //Prochain m
                }
                Ql--; //Prochain l
            }
            if(trouvéNiveau){
                //On a traversé toute la couche et si elle était la couche de valence, on sort de la boucle
                i = 4;
                break;
            }
            //Sinon,
            Qn--; //Prochain n
        }

        charge += (double)doublets*2.0; //Ajuste la charge de l'atome en fonction des doublets. Ils seront traités séparéments.
        //Remplir les listes
        while (positionDoublet.size()<doublets) {
            positionDoublet.add(new Vecteur3D( 2.0*(Math.random()-0.5),2.0*(Math.random()-0.5) ,2.0*(Math.random()-0.5) )); //  entre deux repasser position de liaison
            prevPosDoublet.add(positionDoublet.get(positionDoublet.size()-1));
            vélDoublet.add( new Vecteur3D(0, 0, 0));
            forceDoublet.add( new Vecteur3D(   0, 0, 0));
        }
        while (positionDoublet.size()>doublets) {
            positionDoublet.remove(0);
            prevPosDoublet.remove(0);
            vélDoublet.remove(0);
            forceDoublet.remove(0);
        }
        //System.out.println(doublets + " doublets et " + n + " liaisons possibles.");
    }
    
    /**Déplace les doublets de l'atome dans la direction de leur force appliquée. À utiliser pour initialiser leur position à l'équilibre */
    public void déplacerVersÉquilibre(){
        for (int i = 0; i < 1; i++) {
            for (int j = 0; j < doublets; j++) {
                forceDoublet.get(j).norm();
                forceDoublet.get(j).mult(0.03);
                positionDoublet.get(j).addi(forceDoublet.get(j));
            }
            Force.norm();
            Force.mult(0.03);
            position.addi(Force);
        }
    }

    /**Calcule l'électronégativité de cet atome en utilisant l'électronégativité d'Allred-Rochow et la règle de Slater. L'électronégativité est affectée par le nombre d'électrons. */
    private void calculerÉlectronégativitée(){
        //Implémente l'électronégativité d'Allred-Rochow, avec la règle de Slater.
        double sigma;
        if(NE > 0 ){
            sigma = ConstanteÉcran[NE-1];
        }else{
            sigma = 0.0;
        }
        //TODO #3 Régler problème d'NP négatif

        double Zeff = (double)NP-sigma;
        électronégativité = (float)(0.359*Zeff/(Radii[NP-1]*Radii[NP-1]))+0.744f;
    }
    
    /**Créé et brise les liens lorsque nécessaire.*/
    public void miseÀJourLiens(){

        //Aller chercher l'indexe de cet atome dans Environnement
        if(indexe == -1){
            indexe = Atome.Environnement.indexOf(this);
        }

        //Briser les liens
        for (int i = 0; i < liaisonIndexe.size(); i++) {
            //Pour toutes les possibilités de liaisons
            if(liaisonIndexe.get(i) == -1){
                //S'il n'y a pas de liaison, passer à la prochaine
                continue;
            }
            
            double dist = Vecteur3D.distance(position, Environnement.get(liaisonIndexe.get(i)).position); //Évaluer la distance entre les deux atomes
            if(dist > 2.0*(rayonCovalent + Environnement.get(liaisonIndexe.get(i)).rayonCovalent)){
                //Si la distance est 2 fois la longueur de liaison, briser le lien
                briserLien(i);
            }
        }

        //Créer les liens
        for (int i = 0; i < liaisonIndexe.size(); i++) {
            //Pour toutes les possibilités de liens
            double min_dist = Double.MAX_VALUE; //distance avec l'atome le plus proche
            if (liaisonIndexe.get(i) != -1) {
                //S'il y a une liaison, passer à la prochaine
                continue;
            }
            
            int indexePot = -1; //Indexe du candidat potentiel pour créer un lien
            int nLiaisons = 0;  //Nombre de liaisons déjà créées avec ce candidat
            int placeLibre = -1;//Nombre de liaisons que A' peut encore créer
            
            for (int j = 0; j < Atome.Environnement.size(); j++) {
                //Pour tout les atomes
                if(indexe == j){
                    //Si A' est A, passer au prochain
                    continue;
                }
                
                Atome APrime = Atome.Environnement.get(j); //Référence à A'
                double dist = Vecteur3D.distance(position, APrime.position); //Calculer la distance entre A et A'
                if(dist < min_dist && dist < 2.0*(rayonCovalent + APrime.rayonCovalent)){
                    //Si la distance est de moins de 2 longueurs de liaisons et qu'il est l'atome le plus proche
                    //Chercher une case qui peut acceuillir une liaison chez A'
                    int pL = -1;
                    
                    for (int k = 0; k < APrime.liaisonIndexe.size(); k++) {
                        if(APrime.liaisonIndexe.get(k) == -1){
                            pL = k;
                            break;
                        }
                    }
                    if(pL != -1){
                        //Si on a trouvé une place libre chez A'
                        //Évaluer le nombre de liaisons déjà créés avec A'
                        nLiaisons = 0;
                        for (int k = 0; k < liaisonIndexe.size(); k++) {
                            if(j == liaisonIndexe.get(k)){
                                nLiaisons++;
                            }
                        }
                        /*or (int k5 = 0; k5 < APrime.doublets; k5++) {
                            if(j == liaisonIndexe[k5]){
                                nLiaisons++;
                                APrime.doublets--;
                            }
                        }*/
                        if(nLiaisons < 3){
                            //S'il y a moins de 3 liaisons avec A'
                            indexePot = j;  //Garder A' comme candidat potentiel
                            min_dist = dist;//Garder A' comme atome le plus proche
                            placeLibre = pL;
                        }
                        
                    } 

                }
            }
            if(placeLibre != -1 && indexePot != -1 && nLiaisons < 3){
                //Si on a trouvé un A', qu'il a de la place libre et qu'on a moins de 3 liaisons déjà en cours avec lui,
                créerLien(indexePot, i, placeLibre, nLiaisons+1, nLiaisons!=0);
            }
        }
        //Créer liens avec doublets
        for (int i=doublets-1; i >= 0; i--){
            //Pour toutes les doublets
            double min_dist = Double.MAX_VALUE; //distance avec l'atome le plus proche
            
            for (int j = 0; j < Atome.Environnement.size(); j++) {
                //Pour tout les atomes
                if(indexe == j){
                    //Si A' est A, passer au prochain
                    continue;
                }
                
                Atome APrime = Atome.Environnement.get(j); //Référence à A' , A est l'atome ayant le doublets, A' n'importe qu'elle atome
                double dist = Vecteur3D.distance(position, APrime.position); //Calculer la distance entre A et A'
                if(dist < min_dist && dist < 2.0*(rayonCovalent + APrime.rayonCovalent)){
                        int Qn = MAX_N;     //Nombre quantique principal n. On doit traverser les cases à l'envers, donc on commence à MAX_N
                        int Ql = 0;         //Nombre quantique azimutal l.
                        int Qm = 0;         //Nombre quantique magnétique m.
                        int CaseVide = 0; //Trouver le nombre de caseVide
                        boolean trouvéNiveau = false;   //Indique si on a trouvé le niveau de la couche de valence
                        int casesIndexe = MAX_CASE;     //Curseur indexe des cases quantiques
                        for (int i1 = 0; i1 < MAX_N; i1++) {
                            //Traverser n de MAX_N à 1
                            Ql = Qn-1;
                            for (int j1 = 0; j1 < Qn; j1++) {
                                //Traverser l de n-1 à 0
                                Qm = Ql;
                                for(int j2 = 0; j2 < 2*Ql+1; j2++){
                                    //Traverser m de l à -l
                                    if(APrime.cases[casesIndexe] != 0){
                                        //Si la case n'est pas vide, c'est la première case  non-vide et donc elle appartient à la couche de valence
                                        trouvéNiveau = true;
                                    }
                                    if (APrime.cases[casesIndexe] == 2 && trouvéNiveau) {
                                        //Si la case possède 2 électrons et qu'elle fait partie de la couche de valence
                                    }
                                    if (APrime.cases[casesIndexe] != 0){
                                        int CasePotVide=0;
                                        if (casesIndexe <= 0){
                                            CasePotVide = 1;
                                        }
                                        if (0 <casesIndexe && casesIndexe <= 1){
                                            CasePotVide = 2;
                                        }
                                        if (1 <casesIndexe && casesIndexe <= 4){
                                            CasePotVide = 5;
                                        }
                                        if (4 < casesIndexe &&casesIndexe <= 5){
                                            CasePotVide = 6;
                                        }

                                        if (5 < casesIndexe &&casesIndexe <= 8){
                                            CasePotVide = 9;
                                        }
                                        if (8 < casesIndexe &&casesIndexe <= 9){
                                            CasePotVide = 10;
                                        }
                                        if (9 <casesIndexe &&casesIndexe <= 13){
                                            CasePotVide = 14;
                                        }
                                        CaseVide = CasePotVide-casesIndexe-1;
                                        break; //Cation, charge positive...
                                    }
                                    casesIndexe--; //Prochaine case
                                    Qm--;          //Prochain m
                                }
                                Ql--; //Prochain l
                            }
                            
                            if(trouvéNiveau){
                                //On a traversé toute la couche et si elle était la couche de valence, on sort de la boucle
                                i1 = 4;
                                break;
                            }
                            //Sinon,
                            Qn--; //Prochain n

                            if (Qn==0 && !trouvéNiveau){
                                CaseVide++;
                                 //Si aucun électron, CaseVide = 0 , 1S0
                            }
                        }

                     

                        if ( positionDoublet.size() > 0 && CaseVide > 0){

                        APrime.ajouterÉlectron(); 
                        retirerÉlectron();   //Doublet se transforme en 2 electron , qui
                        évaluerValence();
                        APrime.évaluerValence();    //ÉvaluerValence pour reprend / perde ses doubletts
                        //System.out.println(APrime.NP);
                        ForceSimoideDoublets += 0;
                        
                        }
                    
                }
        
            }
        }
        
        systèmesConjugués = molécule.obtenirSystèmesConjugués(indexe);
        évaluerRésonance();
        

    }
    
    private void évaluerRésonance(){
        //TODO #28 implémenter évaluerRésonance
        int n = 0; //Nombre de formes limites de résonances de cet atome
        for (int i = 0; i < systèmesConjugués.size(); i++) {
            //TODO compter le nombre de formes limites de résonnances
            switch (systèmesConjugués.get(i)[0]) {
                case 0:
                    n += 3;
                    break;
                case 1:
                    n += 0;
                    break;
                case 2:
                    n += 0;
                    break;
                case 3:
                    n += 0;
                    break;
                case 4:
                    n += 0;
                    break;
                case 5:
                    n += 0;
                    break;
                default:
                    break;
            }
        }
        for (int i = 0; i < systèmesConjugués.size(); i++) {
            for (int j = 0; j < systèmesConjugués.get(i).length; j++) {
                if (systèmesConjugués.get(i)[j] == indexe) {
                    switch (systèmesConjugués.get(i)[0]) {
                        case 0:
                            //=-=
                            //Formes limites : =-=, +-=-:(-) et (-):-=-+
                            //Hybride : ( |:)(=|-)(-|=)(=|-)(:| )
                            switch(j){
                                case 1:
                                    //( |:)A(=|-)
                                    //Ajouter 1/n :
                                    //Retirer 1/n -
                                case 2:
                                    //(=|-)B(-|=)
                                    //Retirer 1/n -
                                    //Ajouter 1/n -
                                case 3:
                                    //(-|=)C(=|-)
                                    //Ajouter 1/n -
                                    //Retirer 1/n -
                                case 4:
                                    //(=|-)D(:| )
                                    //Retirer 1/n -
                                    //Ajouter 1/n :
                                default:
                                    break;
                            }
                            break;
                        case 1: 
                            //:-=
                            //Formes limites : :-= et +=-:(-)
                            //Hybrides : (:|+)(-|=)(=|-:(-))
                            switch (j) {
                                case 1:
                                    //(:|+)A(-|=)
                                    //Retirer 1/n :
                                    //Ajouter 1/n +
                                    //Ajouter 1/n -
                                    break;
                                case 2:
                                    //(-|=)B(=|)
                                    break;
                                default:
                                    break;
                            }
                            break;
                        case 2:
                        case 3:
                        case 4:
                        case 5:

                        default:
                            break;
                    }
                }
            }
        }
    
    }
    private double forceSigmoide = 5.0;

    /**
     * <p>Créé un lien avec l'atome spécifié par indexeAtome.</p>
     * @param indexeAtome - Indexe de l'atome avec lequel faire un lien
     * @param liaisonIndexeA - Indexe de la case de liaisonIndexe[] qui contiendra le lien, dans l'atome A
     * @param liaisonIndexeB - Indexe de la case de liaisonIndexe[] qui contiendra le lien, dans l'atome B
     * @param liaisonOrdre - Ordre de liaison. 1 = lien simple, 2 = lien double, 3 = lien triple
     * @param liaisonType - Type de liaison. faux = sigma, vrai = pi
     */
    public void créerLien(int indexeAtome, int liaisonIndexeA, int liaisonIndexeB, int liaisonOrdre, boolean liaisonType ){
        Atome APrime = Atome.Environnement.get(indexeAtome);    //Référence à A'
        liaisonIndexe.set(liaisonIndexeA,indexeAtome);          //Ajouter une référence de A' à A
        APrime.liaisonIndexe.set(liaisonIndexeB,indexe);        //Donner une référence de A à A'
        this.liaisonOrdre.set(liaisonIndexeA,liaisonOrdre);     //Donner l'ordre de liaison à A
        APrime.liaisonOrdre.set(liaisonIndexeB,liaisonOrdre);   //Donner l'ordre de liaison à A'
        this.liaisonType.set(liaisonIndexeA,liaisonType);        //Donner le type de liaison créé
        APrime.liaisonType.set(liaisonIndexeB,liaisonType); //Donner le type de liaison créé

        //Mettre à jour l'ordre des autres liaisons
        for (int i = 0; i < this.liaisonOrdre.size(); i++) {
            if(liaisonIndexe.get(i) == indexeAtome){
                this.liaisonOrdre.set(i,liaisonOrdre);
            }
        }
        for (int i = 0; i < APrime.liaisonOrdre.size(); i++) {
            if(APrime.liaisonIndexe.get(i) == indexe){
                APrime.liaisonOrdre.set(i,liaisonOrdre);
            }
        }

        //Calculer la proportion d'électronégativité que chaque atome aporte à la liaison
        float proportion = (float)sigmoide( électronégativité/(électronégativité+APrime.électronégativité),forceSigmoide+ForceSimoideDoublets);
        charge += 1.0-2.0*proportion;               //Ajouter une charge partielle. Dans une liaison, deux électrons seront impliqués. 
        APrime.charge += 1.0-2.0*(1.0-proportion);  //Ces électrons seront plus ou moins attirés par l'un ou l'autre des atomes, d'où la charge partielle
        molécule.fusionnerMolécule(APrime.molécule);//Fusionner les deux molécules
    }

    /**
     * Détruit un lien.
     * @param indexeLiaison - Indexe de la case de liaisonIndexe[] qui contient le lien.
     */
    public void briserLien(int indexeLiaison){
        Atome APrime = Atome.Environnement.get(liaisonIndexe.get(indexeLiaison)); //Référence à A'
        //Distribuer les électrons entre les deux atomes
        //Calculer la proportion d'électronégativité apportée par l'atome dans le lien. Si les deux on la même, le résultat serat .5, le maximum serat 1 et le minimum serat 0
        float proportion = (float)sigmoide(électronégativité/(électronégativité+APrime.électronégativité),forceSigmoide+ForceSimoideDoublets); //Passer à travers une sigmoide pour mieux séparer les deux atomes.
        charge -= 1.0-2.0*proportion;                                   //Retirer la charge partielle de cet atome (A)
        APrime.charge -= 1.0-2.0*(1.0-proportion);//Retirer la charge partielle de l'autre atome (A')
        retirerÉlectron();                              //Retirer un électron à A
        APrime.retirerÉlectron(); //Retirer un électron à A'

        //Donner aléatoirement un électron à un atome. Plus l'atome est électronégatif, plus il a de chances d'obtenir l'électron
        if(Math.random() < proportion){
            ajouterÉlectron();
        }else{
            APrime.ajouterÉlectron();
        }
        
        //Recalculer les proportions, car l'électronégativité est affectée par la charge.
        proportion = (float)sigmoide(électronégativité/(électronégativité+APrime.électronégativité),forceSigmoide);
        //Donner aléatoirment un électron à un atome.
        if(Math.random() < proportion){
            ajouterÉlectron();
        }else{
            APrime.ajouterÉlectron();
        }
        
        //Mettre à jour l'ordre de liaison des autres liaisons
        for (int i = 0; i < liaisonIndexe.size(); i++) {
            if(liaisonIndexe.get(i) == APrime.indexe){
                liaisonOrdre.set(i,0);
            }
        }
        for (int i = 0; i < APrime.liaisonIndexe.size(); i++) {
            if(APrime.liaisonIndexe.get(i) == indexe){
                APrime.liaisonOrdre.set(i,0);
            }
        }

        //Retirer les références à A de A'
        for (int j = 0; j < APrime.liaisonIndexe.size(); j++) {
            if(APrime.liaisonIndexe.get(j) == indexe){
                APrime.liaisonIndexe.set(j,-1);
                APrime.liaisonType.set(j,false);
            }
        }
        //Retirer les références à A' de A
        for (int j = 0; j < liaisonIndexe.size(); j++) {
            if(liaisonIndexe.get(j) == APrime.indexe){
                liaisonIndexe.set(j,-1);
                liaisonType.set(j,false);
            }
        }

        //Séparer la molécule
        molécule.séparerMolécule(this, APrime);
        APrime.évaluerValence(); //ÉvaluerValence pour reprend / perde ses doubletts
        évaluerValence();
        ForceSimoideDoublets -= 0;
    }

    /**
     * Augmente le contraste d'une valeur entre 0 et 1, en l'éloignant de 0.5 et en la poussant vers les extrêmes.
     * @param x - Valeur à contraster. Doit être entre 0 et 1.
     * @param facteur - Facteur de contraste
     * @return Une version contrasté de x. La valeur serat comprise entre 0 et 1 et sigmoide(0) = 0; sigmoide(1) = 1
     * */
    private double sigmoide(double x, double facteur){
        double fNorm = 0.5/ ( Math.exp(facteur*(0.5))/(1+Math.exp(facteur*(0.5))) - 0.5);
        return fNorm*( Math.exp(facteur*(x-0.5))/(1+Math.exp(facteur*(x-0.5))) - 0.5 ) + 0.5;
    }

    public double Température(){
        return Math.pow(vélocité.longueur(),2.0)*m/(3.0*kB);
    }

    public static double Température(Atome a){
        return a.Température();
    }

    public static double Température(ArrayList<Atome> A){
        double v1 = 0.0;
        double Ek = 0.0;

        double Kstruges=1.0 + (10.0/3.0)*Math.log10(A.size());
        //double Yule = 
        double KsA= Math.ceil(Kstruges);
        double Delta =0.0 ;
        double MinEk=Double.MAX_VALUE;
        double MaxEk=Double.MIN_VALUE;
        double[] EkMod= new double[(int) KsA];
        double EkMode=0.0;
        for (int i = 0; i < A.size(); i++) {
        MinEk=Math.min(MinEk,Math.pow(A.get(i).vélocitéMoyenne.longueur(),2)*A.get(i).m*0.5);
        MaxEk=Math.max(MaxEk,Math.pow(A.get(i).vélocitéMoyenne.longueur(),2)*A.get(i).m*0.5);
        }
        Delta=(MaxEk-MinEk)/KsA;
        for (int i = 0; i < A.size(); i++) {
            for (int j = 0; j<KsA; j++){
                if (MinEk+j*Delta<= Math.pow(A.get(i).vélocitéMoyenne.longueur(),2)*A.get(i).m*0.5 && Math.pow(A.get(i).vélocitéMoyenne.longueur(),2)*A.get(i).m*0.5 <= MinEk+(j+1)*Delta){
                    EkMod[j]++;          
                }
            }
        }
            /** Donne la popularite de la/les cases les plus remplis*/
        double Population=0.0;

        for (int i = 0; i <KsA  ; i++) {
            Population=Math.max(Population,EkMod[i]);
        }

        //double n=0.0;
        for (int i = 0; i <KsA  ; i++) {
            if ( EkMod[i]==Population){
                EkMode=0.5*(MinEk+i*Delta+MinEk+(i+1)*Delta);
                break;
                //n++;
            }
        }
        //EkMode=EkMode/n;

         /* for (int i = 0; i < A.size(); i++) {
            v1 += A.get(i).vélocitéMoyenne.longueur();
            Ek += Math.pow(VitesseMode,2)*A.get(i).m*0.5;
            
        }  */
        
      //  Ek = Math.pow(   ,2.0)*A.get(i).m*0.5;
        //v1 = v1/(double)A.size();
        //Ek = Ek/A.size();
 

        //System.out.println("v1 : " + String.format("%.03G",v1) + " m/s"); Ek*2.0/(3.0*kB)
        if (Double.isNaN(EkMode*2.0/(3.0*kB))){
            System.out.println("EnenergiCinetiqueNAN");
            return 30.0;
        }else{
            return ((EkMode*2.0/(3.0*kB)));
        }

        /* for (int j=0; j < A.size()*0.5; j++){
        for (int i = 0; i < EK1.size()-1; i++) {
            if(EK1.get(i) < EK1.get((i+1))){
                
                
                double a = EK1.get(i);
                EK1.set( i, EK1.get(i+1));
                EK1.set( i+1 , a );
            
            }
        }
        } */

        /* v1 = v1/(double)A.size();
        Ek = Ek/A.size();
        //System.out.println("v1 : " + String.format("%.03G",v1) + " m/s");
        return Ek*2.0/(3.0*kB); */
    }

    public static double TempératureEnVitesse(double T, double m){
        return Math.sqrt(3.0*kB*T/m);
    }

    /**
     * Renvoie une copie de l'atome
     * @param copierMolécule - Si vrai, copie la molécule, sinon la molécule restera la même référence
     * @return Un atome copié
     * */
    public Atome copier(boolean copierMolécule){
        Atome a = new Atome();
        a.copier(this,copierMolécule);
        return a;
    }

    /**
     * Copie l'atome.
     * @param a - Atome à copier
     * @param copierMolécule - Si vrai, copie la molécule, sinon la molécule restera la même référence.
     * */
    public void copier(Atome a, boolean copierMolécule){
        Atome b = a;
        if(this.prevPosition != null){
            this.prevPosition = b.prevPosition.copier();
        }
        this.position = b.position.copier();
        this.vélocité = b.vélocité.copier();
        this.Force = b.Force.copier();
        this.positionDoublet = (ArrayList<Vecteur3D>) b.positionDoublet.clone();
        this.prevPosDoublet = (ArrayList<Vecteur3D>) b.prevPosDoublet.clone();
        this.vélDoublet = (ArrayList<Vecteur3D>) b.vélDoublet.clone();
        this.forceDoublet = (ArrayList<Vecteur3D>) b.forceDoublet.clone();
        this.NP = b.NP;
        this.NE = b.NE;
        this.m = b.m;
        this.charge = b.charge;
        this.électronégativité = b.électronégativité;
        this.indexe = b.indexe;

        this.liaisonIndexe = (ArrayList<Integer>) b.liaisonIndexe.clone();
        this.liaisonType = (ArrayList<Boolean>) b.liaisonType.clone(); // sigma = faux, pi = vrai
        this.liaisonOrdre = (ArrayList<Integer>) b.liaisonOrdre.clone();
        this.doublets = b.doublets;
        this.rayonCovalent = b.rayonCovalent;

        this.cases = b.cases.clone();

        if(copierMolécule){
            this.molécule = b.molécule.copier();
            this.molécule.retirerAtome(b);
            this.molécule.ajouterAtome(this);
        }else{
            this.molécule = b.molécule;
        }
    }

    /**Initialise la position précédente initiale avec une certaine vitesse. Est utilisé pour Verlet.
     * @param h - delta temps
     * */
    public void prevPositionInit(double h){
        if(prevPosition == null){
            prevPosition = Vecteur3D.addi(position, Vecteur3D.mult(vélocité, -h)).copier();
        }
    }

   
    private int ForceSimoideDoublets;
        /**Charge élémentaire : <b>1.602*10^-19</b> C */
    public static final double e = 1.602*Math.pow(10.0, -19.0);    //Charge élémentaire
        /**Masse du proton : <b>1.672*10^-27</b> kg */
    public static final double mP = 1.0*1.672*Math.pow(10.0,-27.0);//Masse du proton
        /**Masse de l'électron : <b>9.109*10^-31</b> kg */
    public static final double mE = 1.0*9.109*Math.pow(10.0,-31.0);//Masse de l'électron
        /**Facteur de conversion en Angströms : <b>10^-10</b> (1m = 10^10 Å) */
    public static final double Ag = Math.pow(10,-10);              //Facteur de conversion en Angströms
        /**Constante de Coulomb : <b>8.987*10^39</b> kg * Å^4 * s^-2 * C^-2 */
    public static final double K = 8.987*Math.pow(10.0,39.0);    //Constante de Coulomb
       /**Permittivité du vide, epsilon 0 : <b>8.854 * 10^-42</b> C^2 * s^2 * kg^-2 */
    public static final double ep0 = 8.854*Math.pow(10.0,-42);     //Permittivité du vide
       /**Constante de Planck : <b>6.626*10^-14</b> kg * m^2 * s^-1*/
    public static final double h = 6.626*Math.pow(10.0,-14);       //Constante de Planck
       /**Constante de Boltzman : <b>1.380649*10^-3</b> kg * m^2 * s^-2 * °K^-1*/
    public static final double kB = 1.380649*Math.pow(10.0,-3);       //Constante de Boltzman en Jarmstrong
   
    public static final double kBJ = 1.380*Math.pow(10.0,-23);       //Constante de Boltzman en J
       /**Vitesse de la lumière :<b>2.99792458*10^18</b> Å/s*/
    public static final double c = 2.99792458*Math.pow(10.0,18.0);//Vitesse de la lumière
       /**Nombre d'Avogadro : <b>6.02214076</b> mol^-1 */
    public static final double Navodagro = 6.02214076*Math.pow(10,23); //nombre d'avogadro en Mol-1
   
    public static final double R=Navodagro*kB;
    /**Référence à la liste des autres atomes de la simulation. */
    public static ArrayList<Atome> Environnement = new ArrayList<>(); //Référence à la liste des autres atomes de la simulation


    /**Électronégativité de Pauling de chaque élément.<
     *<p><a href="https://archive.org/details/CRCHandbookOfChemistryAndPhysics97thEdition2016/page/n1595/mode/2up"> Source : <i>W. M. Haynes</i> (2016), CRC Handbook of Chemistry and Physics 97th Edition</a>.</p>
     * */
    private static final float[] ÉlectronégativitéPauling = {
        2.20f,                                                                                                0.00f,
        0.98f,1.57f,                                                            2.04f,2.55f,3.04f,3.50f,3.98f,0.00f,
        0.93f,2.31f,                                                            1.61f,1.90f,2.19f,2.58f,3.16f,0.00f,
        0.82f,1.00f,1.36f,1.54f,1.63f,1.66f,1.55f,1.83f,1.88f,1.91f,1.90f,1.65f,1.81f,2.01f,2.18f,2.55f,2.96f,3.00f,
        0.82f,0.95f,1.22f,1.33f,1.60f,2.16f,1.90f,2.20f,2.28f,2.20f,1.93f,1.69f,1.78f,1.96f,2.05f,2.10f,2.66f,2.60f,
        0.79f,0.89f,      1.10f,1.12f,1.13f,1.14f,0.00f,1.20f,0.00f,1.22f,1.23f,1.24f,1.25f,0.00f,1.27f,1.30f,1.50f,
                    2.36f,1.90f,2.20f,2.20f,2.28f,2.54f,2.00f,1.62f,2.33f,2.02f,2.00f,2.20f,2.20f,0.70f,0.90f,      
        1.10f,1.30f,      1.50f,1.38f,1.36f,1.28f,1.30f,1.30f,1.30f,1.30f,1.30f,1.30f,1.30f,1.30f
    };

    /**Rayon covalent de chaque élément en pm. Doit être divisé par 100 pour travailler en Å.
     * <p><a href="https://pubmed.ncbi.nlm.nih.gov/19058281/"> Source : <i>P. Pyykkö & M. Atsumi</i> (2009) Molecular single-bond covalent radii for elements 1-118</a>,
     * repéré sur <a href="https://fr.wikipedia.org/wiki/Rayon_de_covalence">Wikipedia (2024)</a></p>
     * */
    private static final float[] rayonsCovalents = {
         32f,                                                                                 46f,
        133f,102f,                                                   85f, 75f, 71f, 63f, 64f, 67f,
        155f,139f,                                                  126f,116f,111f,103f, 99f, 96f,
        196f,171f,148f,136f,134f,122f,119f,116f,111f,110f,112f,118f,124f,121f,121f,116f,114f,117f,
        210f,185f,163f,154f,147f,138f,128f,125f,125f,120f,128f,136f,142f,140f,140f,136f,133f,131f,
        232f,196f,     180f,163f,176f,174f,173f,172f,168f,169f,168f,167f,166f,165f,164f,170f,162f,
                  152f,146f,137f,131f,129f,122f,123f,124f,133f,144f,144f,151f,145f,147f,142f,     
        223f,201f,     186f,175f,169f,170f,171f,172f,166f,166f,168f,168f,165f,167f,173f,176f,161f,
                  157f,149f,143f,141f,134f,129f,128f,121f,122f,136f,143f,162f,175f,165f,157f
    };

    /**Rayon covalent de liens doubles.
    * <p><a href="https://pubmed.ncbi.nlm.nih.gov/19058281/"> Source : <i>P. Pyykkö & M. Atsumi</i> (2009) Molecular single-bond covalent radii for elements 1-118</a>,
    * repéré sur <a href="https://fr.wikipedia.org/wiki/Rayon_de_covalence">Wikipedia (2024)</a></p>
    * */
    private static final float[] rayonsCovalents2 = {
         0f,                                                                                   0f,
        124f, 90f,                                                   78f, 67f, 60f, 57f, 59f, 96f,
        160f,132f,                                                  113f,107f,102f, 94f, 95f,107f,
        193f,147f,116f,117f,112f,111f,105f,109f,103f,101f,115f,120f,117f,111f,114f,107f,109f,121f,
        202f,157f,130f,127f,125f,121f,120f,114f,110f,117f,139f,144f,136f,130f,133f,128f,129f,135f,
        209f,161f,     139f,137f,138f,137f,135f,134f,134f,135f,135f,133f,133f,133f,131f,129f,131f,
                  128f,126f,120f,119f,116f,115f,112f,121f,142f,142f,135f,141f,135f,138f,145f,
        218f,173f,     153f,143f,138f,134f,136f,135f,135f,136f,139f,140f,140f,  0f,139f,159f,141f,
                  140f,136f,128f,128f,125f,125f,116f,116f,137f,  0f,  0f,  0f,  0f,  0f,  0f
    };

    /**Rayon covalent de liens triples.
     * <p><a href="https://pubmed.ncbi.nlm.nih.gov/19058281/"> Source : <i>P. Pyykkö & M. Atsumi</i> (2009) Molecular single-bond covalent radii for elements 1-118</a>,
     * repéré sur <a href="https://fr.wikipedia.org/wiki/Rayon_de_covalence">Wikipedia (2024)</a></p>
     * */
    private static final float[] rayonsCovalents3 = {
          0f,                                                                                  0f,
          0f, 85f,                                                   73f, 60f, 54f, 53f, 53f,  0f,
          0f,127f,                                                  111f,102f, 94f, 95f, 93f, 96f,
          0f,133f,114f,108f,106f,103f,103f,102f, 96f,101f,120f,  0f,121f,114f,106f,107f,110f,108f,
          0f,139f,124f,121f,116f,113f,110f,103f,106f,112f,137f,  0f,146f,132f,127f,121f,125f,122f,
          0f,149f,     139f,131f,128f,  0f,  0f,  0f,  0f,132f,  0f,  0f,  0f,  0f,  0f,  0f,131f,
                  122f,119f,115f,110f,109f,107f,110f,123f,  0f,150f,137f,135f,129f,138f,133f,
          0f,159f,     140f,136f,129f,118f,116f,  0f,  0f,  0f,  0f,  0f,  0f,  0f,  0f,  0f,  0f,
                  131f,126f,121f,119f,118f,113f,112f,118f,130f,  0f,  0f,  0f,  0f,  0f,  0f
    };

     /**Constante d'écran utilisé dans le calcul de la charge effective de l'atome.
     * <p><a href="https://link.springer.com/article/10.1007/s00214-009-0610-4"> Source : <i>D. C. Ghosh et al.</i> (2009) The Electronegativity Scale of Allred and Rochow : Revisited</a></p>.
     * */
    private final double ConstanteÉcran[] = {
        0.000,                                                                                                0.300,
        1.700,2.050,                                                            2.400,2.750,3.100,3.450,3.800,4.2515,
        8.800,9.150,                                                            9.500,9.850,10.20,10.55,10.90,11.25,
        16.80,17.15,18.00,18.85,19.70,20.55,21.40,22.25,23.10,23.95,24.80,25.65,26.00,26.35,26.70,27.05,27.40,27.75,
        34.80,35.15,36.00,36.85,37.70,38.55,39.40,40.25,41.10,41.95,42.80,43.65,44.00,44.35,44.70,45.05,45.40,45.75,
        52.80,53.15,      53.50,53.85,54.20,54.55,54.90,55.25,55.60,55.95,56.30,56.65,57.00,57.35,57.70,58.05,58.74,
                    58.75,59.10,59.45,59.80,60.15,60.50,60.85,61.20,61.55,76.00,76.35,76.70,77.05,77.4,77.75,
        84.80,83.15,      84.00,84.85,84.70,85.05,85.40,85.25,85.60,86.45,86.30,86.65,87.00,87.35,87.70,88.05,88.90
    };
    
    /**Rayon atomique absolut en Å. Utilisé dans le calcul de l'électronégativité.
     * <p><a href="https://link.springer.com/article/10.1007/s00214-009-0610-4"> Source : <i>D. C. Ghosh et al.</i> (2009) The Electronegativity Scale of Allred and Rochow : Revisited</a></p>.
     * */
    private static final double Radii [] = {
        0.5292,                                                                                                                0.3113,
        1.6283,1.08550,                                                                     0.8141,0.6513,0.5428,0.4652,0.4071,0.3676,
        2.1650,1.67110,                                                                     1.3608,1.1477,0.9922,0.8739,0.7808,0.7056,
        3.2930,2.5419,2.4149,2.2998,2.1953,2.1000,2.0124,1.9319,1.8575,1.7888,1.7250,1.6654,1.4489,1.2823,1.1450,1.0424,0.9532,0.8782,
        3.8487,2.9709,2.8224,2.6880,2.5658,2.4543,2.3520,2.2579,2.1711,2.0907,2.0160,1.9465,1.6934,1.4986,1.3440,1.2183,1.1141,1.0263,
        4.2433,3.2753,       2.6673,2.2494,1.9447,1.7129,1.5303,1.3830,1.2615,1.1596,1.0730,0.9984,0.9335,0.8765,0.8261,0.7812,0.7409,
                      0.7056,0.6716,0.6416,0.6141,0.5890,0.5657,0.5443,0.5244,0.5060,1.8670,1.6523,1.4818,1.3431,1.2283,1.1315,
        4.4479,3.4332,       3.2615,3.1061,2.2756,1.9767,1.7473,1.4496,1.2915,1.2960,1.1247,1.0465,0.9785,0.9188,0.8659,0.8188,0.8086,
    };

    /**Polarisabilité électronique des éléments en unités atomiques (e^2 * a0^2 * Eh^-1). Multiplier par <b><code>convPolar</code></b> pour convertir en (C^2 * s^2 * kg^-1) ou (C*m^2*V^-1)
     * <p><a href="https://www.tandfonline.com/doi/full/10.1080/00268976.2018.1535143">Source : <i>P. Schwerdtfeger & J. K. Nagle</i> (2018) Table of static dipole polarizabilities of the neutral elements in the periodic table</a></p>
     * */
    private static final double Polarisabilité[] = {
        4.50,                                                                               1.38,
        164,37.7,                                                  20.5,11.3, 7.4, 5.3,3.74,2.66,
        163,71.2,                                                  57.8,37.3,  25,19.4,14.6,11.1,
        290, 161,  97, 100,  87,  83,  68,  62,  55,  49,  47,38.7,  50,  40,  30,  29,  21,16.8,
        320, 197, 162, 112,  98,  87,  79,  72,  66,26.1,  55,  46,  65,  53,  43,  38,32.9,27.3,
        401, 272,      215, 205, 216, 208, 200, 192, 184, 158, 170, 165, 156, 150, 144, 139,
                  137, 103,  74,  68,  62,  57,  54,  48,  36,33.9,  50,  47,  48,  44,  42,  35,
        318, 246,      203, 217, 154, 129, 151, 132, 131, 144, 125, 122, 118, 113, 109, 110,
                  320, 112,  42,  40,  38,  36,  34,  32,  32,  28,  29,  31,  71,   0,  76,  58
    };
    /**Facteur multiplicateur pour convertir la polarisabilité d'unités atomiques vers des unitées agnstromiennes */
    private static final double convPolar = 1.64986832*Math.pow(10.0,-41.0);

    /**Constante de force de Morse exprimée en N/cm. Doit être convertis en multipliant par 100 pour travailler en Å.
     * <p><a href="https://archive.org/details/CRCHandbookOfChemistryAndPhysics97thEdition2016/page/n1597/mode/2up"> Source : <i>W. M. Haynes</i> (2016), CRC Handbook of Chemistry and Physics 97th Edition</a>.</p>
     * */
    private static final double[][] ConstanteDeForce = {
      //    H,  He,  Li,  Be,   B,   C,   N,   O,   F,  Ne,  Na,             Mg,  Al,  Si,   P,   S,  Cl,  Ar
        {5.75,   0,1.03,2.27,3.05,5.27,5.97,8.13,9.66,   0,0.78,              0,   0,   0,3.22,4.26,5.16,   0},//H
        {   0,   0,   0,   0,   0,   0,   0,   0,   0,   0,   0,              0,   0,   0,   0,   0,   0,   0},//He
        {1.03,   0,0.26,   0,   0,   0,   0,   0,2.50,   0,0.21,              0,   0,   0,   0,   0,1.43,   0},//Li
        {2.27,   0,   0,   0,   0,   0,   0,7.51,   0,   0,   0,              0,   0,   0,   0,   0,   0,   0},//Be
        {3.05,   0,   0,   0,   0,   0,   0,   0,   0,   0,   0,              0,   0,   0,   0,   0,   0,   0},//B
        {5.27,   0,   0,   0,   0,8.36,14.6,14.2,6.57,   0,   0,              0,   0,   0,7.83,7.94,3.80,   0},//C
        {5.97,   0,   0,   0,   0,14.6,20.8,27.7,   0,   0,   0,              0,   0,   0,5.56,   0,   0,   0},//N
        {8.13,   0,   0,7.51,   0,14.2,27.7,8.76,   0,   0,1.500,          3.48,   0,9.24,9.45,9.32,  0,   0},//O
        {9.66,   0,2.50,   0,   0,6.57,   0,   0,4.70,   0,1.76,              0,   0,4.90,   0,   0,4.48,   0},//F
        {   0,   0,   0,   0,   0,   0,   0,   0,   0,   0,   0,              0,   0,   0,   0,   0,   0,   0},//Ne
        {0.78,   0,0.21,   0,   0,   0,   0,1.50,1.76,   0,0.17,              0,   0,   0,   0,   0,1.09,   0},//Na
        {   0,   0,   0,   0,   0,   0,   0,3.48,   0,   0,   0,              0,   0,   0,   0,   0,   0,   0},//Mg
        {   0,   0,   0,   0,   0,   0,   0,   0,   0,   0,   0,              0,   0,   0,   0,   0,   0,   0},//Al
        {   0,   0,   0,   0,   0,   0,   0,9.24,4.90,   0,   0,              0,   0,2.15,   0,   0,2.63,   0},//Si
        {3.22,   0,   0,   0,   0,7.83,5.56,9.45,   0,   0,   0,              0,   0,   0,   0,   0,   0,   0},//P
        {4.26,   0,   0,   0,   0,7.94,   0,9.32,   0,   0,   0,              0,   0,   0,   0,4.96,   0,   0},//S
        {5.16,   0,1.43,   0,   0,3.80,   0,   0,4.48,   0,1.09,              0,   0,2.63,   0,   0,3.23,   0},//Cl
        {   0,   0,   0,   0,   0,   0,   0,   0,   0,   0,   0,              0,   0,   0,   0,   0,   0,   0},//Ar
    };
    /** Indique si les listes de données à initialiser ont été initialiées. */
    private static boolean initialiséListes = false; //Indique si les listes de données ci-dessous ont été initialisées.
    /**Fréquence fondamentale de vibration rotative d'un trio de 3 liens en cm^-1. 
     * Pour <b>{@code fréquenceTorsion[a][b][c]}</b>, <b>a</b> représente le nombre de proton de l'atome central et <b>b,c</b>, 
     * le nombre de protons des atomes d'extrémités; l'ordre est sans importance. La case 18 est réservées pour les doublets électroniques.
     * <p><a href="https://archive.org/details/CRCHandbookOfChemistryAndPhysics97thEdition2016/page/n1597/mode/2up"> Source : <i>W. M. Haynes</i> (2016), CRC Handbook of Chemistry and Physics 97th Edition</a>.</p>
     * */
   
   
     private static final double[][][] fréquenceTorsion = new double[19][19][19];
    /**Données de fréquenceTorsion. Utilisé pour compacter l'espace de stockage nécessaire. 
     * Organisé ainsi : pour <b>{@code fréquenceTorsion[a][b][c]}</b>, <b><code> fréquenceTorsionDonnées[d][] = {f,a,b,c}</code></b>, où <b>f</b> est 
     * la valeur de fréquenceTorsion à <b>[a][b][c]</b> et où <b>a</b> représente le nombre de protons de l'atome central
     * et <b>b,c</b>, le nombre de protons des atomes d'extrémités; l'ordre est sans importance.
     * <p><a href="https://archive.org/details/CRCHandbookOfChemistryAndPhysics97thEdition2016/page/n1597/mode/2up"> Source : <i>W. M. Haynes</i> (2016), CRC Handbook of Chemistry and Physics 97th Edition</a>.</p>
     * */
   
     private static final double[][] fréquenceTorsionDonnées = {
        //TODO #30 vérifier que les valeurs dans fréquenceTorsionDonnées
        { 667, 6, 8, 8},/*CO2 */          { 962, 6, 1, 1},/*CH2 */          { 520, 7, 9, 8},//FNO
        { 397, 6,16,16},/*CS2 GO*/        { 667, 6, 9, 9},/*CF2 */          { 332, 7,17, 8},//ClNO
        {  63, 6, 6, 6},/*C3  */          { 333, 6,17,17},/*CCl2*/          //{ 266, 7},//BrNO
        { 321, 7, 6, 6},/*CNC Lavalin*/   { 990,14, 1, 1},/*SiH2*/          {1419, 7, 1, 9},//HNF 
        { 423, 6, 7, 7},/*NCN */          { 345,14, 9, 9},/*SiF2*/          {1501, 7, 1, 8},//HNO 
        { 447, 5, 8, 8},/*BO2mages*/      /*{??}SiCl2*/                     { 886, 8, 1, 9},//HOF 
        { 120, 5,16,16},/*BS2 */          { 712, 6, 1, 7},/*HCN HNL*/       {1242, 8, 1,17},//HOCl
        {1595, 8, 1, 1},/*H2O */          { 451, 6, 9, 7},/*FCN */          {1392, 8, 1, 8},//HOO HAA
        { 461, 8, 9, 9},/*F2O */          { 378, 6,17, 7},/*ClCN*/          { 376, 8, 9, 8},//FOO BAR TUTU & WIBBLE
        { 296, 8,17,17},/*Cl2O*/          { 230, 6, 6, 7},/*CCN NCC*/       //{??},//ClOO
        { 701, 8, 8, 8},/*O3  */          { 379, 6, 6, 8},/*CCO CAA*/       {1063,16, 1, 8},//HSO
        {1183,16, 1, 1},/*H2S */          {1081, 6, 1, 8},/*HCO */          { 366,16, 7, 9},//NSF NHL
        { 357,16, 9, 9},/*SF2 */          /*{??},/*HCC_         */          { 273,16, 7,17},//NSCl
        { 208,16,17,17},/*SCl2*/          { 520, 6, 8,16},/*OCS */          {1407, 6, 1, 9},//HCF_
        { 518,16, 8, 8},/*SO2 secours*/   { 535, 6, 7, 8},/*NCO */          {1201, 6, 1,17},//HCCl
        /*{1034,34,1,1},H2Se*/            { 589, 7, 7, 8},/*NNO */          {1201, 6, 1,17},//HCCl
        {1497, 7, 1, 1},/*NH2 */          /*{??},/*HNB_*/                   { 860,14, 1, 9},//HSiF
        { 750, 7, 8, 8},/*NO2 */          /*{??},/*HNC_*/                   { 808,14, 1,17},//HSiCl
        { 573, 7, 9, 9},/*NF2 */          { 523, 7, 1,14},/*HNSi*/          { 992,15, 1, 1},//PH2 (!)
        { 445,17, 8, 8},/*ClO2 et martin*/{ 754, 5, 1, 8},/*HBO Max*/       { 487,15, 9, 9},//PF2 (!)
        { 404, 5,17, 8},/*ClBO*/          { 500, 5, 9, 8},/*FBO min*/       { 252,15,17,17},//PCl2 (!)
        { 498, 5, 9, 9},/*BF2 (!)*/       {1500, 6, 6, 1},/*HCC (!)*/       { 584, 6, 9, 8},//FCO (!)
        { 285, 6,17, 8},/*ClCO (!)*/      { 568, 7, 8, 9},/*|ONF| (!)*/     { 370, 7, 8,17},//ONCl (!)
    };
    /**Énergie de dissociation des liens en kJ/mol. Ces valeurs sont celles des molécules diatomiques correspondantes et sont par conséquent imprécises. 
     * <p><a href="https://archive.org/details/CRCHandbookOfChemistryAndPhysics97thEdition2016/page/n1565/mode/2up"> Source : <i>W. M. Haynes</i> (2016), CRC Handbook of Chemistry and Physics 97th Edition</a>.</p>
     */
    public static final double[][] ÉnergieDeDissociation = {
        //        H,   He,     Li,   Be,    B,     C,     N,      O,     F,   Ne,    Na,   Mg,   Al,   Si,    P,    S,     Cl,   Ar
    /*H */{435.7799,    0,238.039,    0,345.2, 338.72, 358.8, 429.74,569.68,    0,192.71,    0,  288,293.3,  297,    0,431.361,    0},
    /*He*/{       0,3.809,      0,  221,    0,      0,     0,      0,     0,    0,     0,    0,    0,    0,    0,    0,      0, 3.96},
    /*Li*/{ 238.039,    0,    105,    0,    0,      0,     0,  340.5,     0,    0,87.181, 67.4, 76.1,  149,    0,312.5,    469, 7.82},
    /*Be*/{       0,  221,      0,   59,    0,      0,     0,    437,   573,    0,     0,    0,    0,    0,    0,  372,    384,    0},
    /*B */{   345.2,    0,      0,    0,  290,    448, 377.9,    809,   732, 3.97,     0,    0,    0,  317,  347,  577,    427, 4.62},
    /*C */{  338.72,    0,      0,    0,  448, 605.03,749.31,1076.63, 513.8,    0,     0,    0,267.7,  447,507.5,713.3,  394.9,5.158},
    /*N */{   358.8,    0,      0,    0,377.9, 749.31,944.87, 630.57,     0,    0,     0,    0,  368,437.1,617.1,  467,  333.9,    0},
    /*O */{  429.74,    0,  340.5,  437,  809,1076.63,630.57,498.458,   220,    0,   270,358.2,501.9,799.6,  589,517.9, 267.47,    0},
    /*F */{  569.68,    0,      0,  573,  732,  513.8,     0,    220,158.67,    0, 477.3,445.6,  675,  405,    0,    0, 260.83,    0},
    /*Ne*/{       0,    0,      0,    0, 3.97,      0,     0,      0,     0,    0,   3.8,  4.1,  3.9,    0,    0,    0,      0, 4.27},
    /*Na*/{  192.71,    0, 87.181,    0,    0,      0,     0,    270, 477.3,  3.8,74.805,    0,    0,    0,    0,    0,  412.1,  4.2},
    /*Mg*/{       0,    0,   67.4,    0,    0,      0,     0,  358.2, 445.6,  4.1,     0, 11.3,    0,    0,    0,    0,    312,  3.7},
    /*Al*/{     288,    0,   76.1,    0,    0,  267.7,   368,  501.9,   675,  3.9,     0,    0,264.3,    0,216.7,  332,    502, 5.69},
    /*Si*/{   293.3,    0,    149,    0,  317,    447, 437.1,  799.6,   405,    0,     0,    0,    0,    0,363.6,    0,  416.7, 5.86},
    /*P */{     297,    0,      0,    0,  347,  507.5, 617.1,    589,     0,    0,     0,    0,216.7,363.6,489.1,    0,    376,    0},
    /*S */{       0,    0,  312.5,  372,  577,  713.3,   467,  517.9,     0,    0,     0,    0,  332,    0,    0,    0,  241.8,    0},
    /*Cl*/{ 431.361,    0,    469,  384,  427,  394.9, 333.9, 267.47,260.83,    0, 412.1,  312,  502,416.7,  376,241.8,242.851,    0},
    /*Ar*/{       0, 3.96,   7.82,    0, 4.62,  5.158,     0,      0,     0, 4.27,   4.2,  3.7, 5.69, 5.86,    0,    0,      0, 5.50},
};






}
