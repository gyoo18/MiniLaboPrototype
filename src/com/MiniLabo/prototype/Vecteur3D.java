package com.MiniLabo.prototype;
/**
 * Vecteur de 3 dimension à coordonées cartésiennes (x,y,z)
 */
public class Vecteur3D {
    public double x;
    public double y;
    public double z;

    /**
     * Créé un nouveau vecteur
     * @param x - La composante x
     * @param y - La composante y
     * @param z - La composante z
     */
    public Vecteur3D(double x, double y, double z){
        this.x = x;
        this.y = y;
        this.z = z;
    }

    /**
     * Créé un nouveau vecteur en clonant le vecteur clone
     * @param clone
     */
    public Vecteur3D(Vecteur3D clone){
        this.x = clone.x;
        this.y = clone.y;
        this.z = clone.z;
    }

    /**
     * Créé un nouveau vecteur
     * @param xyz - Composantes x,y,z
     */
    public Vecteur3D(double xyz){
        this.x = xyz;
        this.y = xyz;
        this.z = x;
    }

    /**
     * Additionne b à ce vecteur (a+b)
     * @param b - Vecteur à additionner
     */
    public void addi(Vecteur3D b){
        x += b.x;
        y += b.y;
        z += b.z;
    }

    /**
     * Soustrait b à ce vecteur (a-b)
     * @param b - Vecteur à soustraire
     */
    public void sous(Vecteur3D b){
        x -= b.x;
        y -= b.y;
        z -= b.z;
    }

    /**
     * Multiplie ce vecteur par le scalaire s (a*s)
     * @param s - Facteur multiplicateur
     */
    public void mult(double s){
        x *= s;
        y *= s;
        z *= s;
    }

    /**
     * Multiplie les composantes de ce vecteur par les composantes de m (a.x*m.x; a.y*m.y)
     * @param m - Vecteur multiplicateur
     */
    public void mult(Vecteur3D m){
        x *= m.x;
        y *= m.y;
        z *= m.z;
    }

    /**
     * Divise les composantes de ce vecteur par les composantes de d (a.x/d.x; a.y/d.y)
     * @param d
     */
    public void div(Vecteur3D d){
        x = x/d.x;
        y = y/d.y;
        z = z/d.z;
    }

    /**
     * Normalise ce vecteur
     */
    public void norm(){
        if(longueur() > 0){
            x = x/ longueur();
            y = y/ longueur();
            z = z/ longueur();
        }else{
            System.err.println("Normalisation de vecteur nul. Les composantes resteront 0");
        }
    }


    /**
     * Retourne la longueur de ce vecteur
     * @return ||a||
     */
    public double longueur(){
        return (double) Math.sqrt(x*x+y*y+z*z);
    }

    /**
     * Retourne le produit vectoriel entre ce vecteur et c (a^c)
     * @param c
     * @return a^c
     */
    public Vecteur3D croix(Vecteur3D c){
        Vecteur3D r = new Vecteur3D(0,0,0);
        r.x = (y*c.z)-(z*c.y);
        r.y = (z*c.x)-(x*c.z);
        r.z = (x*c.y)-(y*c.x);
        return r;
    }

    /**
     * Retourne la distance entre les vecteurs a et b
     * @param a
     * @param b
     * @return ||a-b||
     */
    public static double distance(Vecteur3D a, Vecteur3D b){
        return (double) Math.sqrt((a.x-b.x) * (a.x-b.x) + (a.y-b.y) * (a.y-b.y) + (a.z-b.z) * (a.z-b.z));
    }

    /**
     * Retourne la somme des vecteurs a et b
     * @param a
     * @param b
     * @return a+b
     */
    public static Vecteur3D addi(Vecteur3D a, Vecteur3D b){
        return new Vecteur3D(a.x+b.x, a.y + b.y, a.z+b.z);
    }

    /**
     * Retourne la différence des vecteurs a et b
     * @param a
     * @param b
     * @return a-b
     */
    public static Vecteur3D sous(Vecteur3D a, Vecteur3D b){
        return new Vecteur3D(a.x-b.x, a.y - b.y, a.z-b.z);
    }

    /**
     * Retourne le vecteur a multiplié par le scalaire s
     * @param a
     * @param s
     * @return a*s
     */
    public static Vecteur3D mult(Vecteur3D a, double s){
        return new Vecteur3D(a.x*s, a.y*s, a.z*s);
    }

    /**
     * Retourne un vecteur dont les composantes sont le produit des composantes de a et b
     * @param a
     * @param b
     * @return (a.x*b.x; a.y*b.y)
     */
    public static Vecteur3D mult(Vecteur3D a, Vecteur3D b){
        return new Vecteur3D(a.x*b.x,a.y*b.y, a.z*b.z);
    }

    /**
     * Retourne un vectuer dont les compsantes sont le quotient des composantes de a et b
     * @param a
     * @param b
     * @return (a.x/b.x; a.y/b.y)
     */
    public static Vecteur3D div(Vecteur3D a, Vecteur3D b){
        return new Vecteur3D(a.x/b.x,a.y/b.y, a.y/b.z);
    }

    /**
     * Retourne le produit scalaire entre les vecteurs a et b
     * @param a
     * @param b
     * @return a•
     */
    public static double scal(Vecteur3D a, Vecteur3D b){
        return (a.x*b.x)+(a.y*b.y)+(a.z*b.z);
    }

    /**
     * Retourne le produit vectoriel entre les vecteurs a et b
     * @param a
     * @param b
     * @return a^b
     */
    /* |i  j  k |
     * |ax ay az|    |ay az|    |ax az|    |ax ay|
     * |bx by bz| = i|by bz| + j|bx bz| + k|bx by| = i(ay*bz-az*by) + j(ax*bz-az*bx) + k(ax*by-ay*bx)
     * */
    public static Vecteur3D croix(Vecteur3D a, Vecteur3D b){
        return new Vecteur3D(a.y*b.z - a.z*b.y, a.z*b.x - a.x*b.z, a.x*b.y - a.y*b.x);
    }
    /**
     * Renvoi le produit mixte entre les vecteur a, b et c
     * @param a  
     * @param b
     * @param c
     * @return a•(b^c)
     */
    /* |ax ay az|
     * |bx by bz|     |by bz|     |bx bz|     |bx by|
     * |cx cy cz| = ax|cy cz| + ay|cx cz| + az|cx cy| = ax(by*cz-bz*cy) + ay(bx*cz-bz*cx) + az(bx*cy-by*cx)
     * */
    public static double mixte(Vecteur3D a, Vecteur3D b, Vecteur3D c){
        return  a.x*(b.y*c.z-b.z*c.y) + a.y*(b.z*c.x-b.x*c.z) + a.z*(b.x*c.y-b.y*c.x);
    }

    /**
     * Retourne le vecteur a normalisé
     * @param a
     * @return (1/||a||)*a
     */
    public static Vecteur3D norm(Vecteur3D a){
        if(a.longueur() > 0){
            return new Vecteur3D(a.x/a.longueur(),a.y/a.longueur(),a.z/a.longueur());
        }else{
            //System.err.println("Normalisation d'un vecteur nul. Les composantes resteront 0.");
            return a;
        }
    }

    /**
     * Retourne un vecteur dont les composantes représentes la rotation x,y,z nécessaire pour pointer vers le point voulus
     * @param positionInitiale
     * @param destination
     * @param assiette
     * @return (angleX, angleY, angleZ) qui pointe vers 'positionInitiale' à partir de 'destination' avec une assiette de 'assiette'
     */
    public static Vecteur3D dirigerVers(Vecteur3D positionInitiale, Vecteur3D destination, double assiette){
        Vecteur3D a = Vecteur3D.sous(positionInitiale,destination);
        Vecteur3D b = new Vecteur3D(0,0,0);
        Vecteur3D c = Vecteur3D.norm(a);
        b.x = (double)Math.toDegrees(Math.asin(-c.y));
        b.y = (double)Math.toDegrees(Math.atan2(a.x,a.z));
        b.z = assiette;
        return b;
    }

    /**
     * Retourne une copie de ce vecteur
     * @return copie de a
     */
    public Vecteur3D copier(){
        return new Vecteur3D(x,y,z);
    }
    
    /**
     * Retourne le vecteur opposé à ce vecteur
     * @return -a
     */
    public Vecteur3D opposé(){
        return new Vecteur3D(-x,-y,-z);
    }

}
