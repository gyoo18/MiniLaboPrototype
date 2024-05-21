package com.MiniLabo.prototype;
/**
 * Vecteur à deux dimensions en coordonées cartésiennes (x,y)
 */
public class Vecteur2D {
    public double x;
    public double y;

    /**
     * Créé un nouveau vecteur
     * @param x - Composante x du vecteur
     * @param y - Composante y du vecteur
     */
    public Vecteur2D(double x,double y){
        this.x = x;
        this.y = y;
    }

    /**
     * Créé un nouveau vecteur
     * @param xy - Composante x et y du vecteur
     */
    public Vecteur2D(double xy){
        this.x = xy;
        this.y = xy;
    }

     /**
     * Créé un nouveau vecteur en clonant le vecteur clone
     * @param clone
     */
    public Vecteur2D(Vecteur2D clone){
        this.x = clone.x;
        this.y = clone.y;
    }

    /**
     * Créé un nouveau vecteur à partir de composantes polaires
     * @param angle - Angle selon l'angle conventionel
     * @param module - module
     * @param o - n'est pas un paramètre
     */
    public Vecteur2D(double angle, double module, int o){
        this.x = module*Math.cos(angle);
        this.y = module*Math.sin(angle);
    }

    /**
     * Additionne b à ce vecteur (a+b)
     * @param b - Vecteur à additionner
     */
    public void addi(Vecteur2D b){
        x += b.x;
        y += b.y;
    }

    /**
     * Soustrait b de ce vecteur (a-b)
     * @param b - Vecteur à soustraire
     */
    public void sous(Vecteur2D b){
        x -= b.x;
        y -= b.y;
    }

    /**
     * Multiplie ce vecteur par un scalaire (a*s)
     * @param s - Facteur multiplicateur
     */
    public void mult(double s){
        x *= s;
        y *= s;
    }

    /**
     * Multiplie les composantes de ce vecteur par 
     * les composantes de m (a = (a.x*b.x; a.y*b.y))
     * @param m - Vecteur multiplicateur
     */
    public void mult(Vecteur2D m){
        x *= m.x;
        y *= m.y;
    }

    /**
     * Divise les composantes de ce vecteur par
     * les composantes de d (a = (a.x/d.x; a.y/d.y))
     * @param d
     */
    public void div(Vecteur2D d){
        x = x/d.x;
        y = y/d.y;
    }

    /**
     * Normalise ce vecteur
     */
    public void norm(){
        double l = longueur();
        x = x/l;
        y = y/l;
    }

    /**
     * Retourne l'addition des vecteurs a et b
     * @param a
     * @param b
     * @return a+b
     */
    public static Vecteur2D addi(Vecteur2D a, Vecteur2D b){
        return new Vecteur2D(a.x+b.x, a.y + b.y);
    }

    /**
     * Retourne la soustraction des vecteurs a et b
     * @param a
     * @param b
     * @return a-b
     */
    public static Vecteur2D sous(Vecteur2D a, Vecteur2D b){
        return new Vecteur2D(a.x-b.x, a.y - b.y);
    }

    /**
     * Retourne le vecteur a multiplié par le scalaire s
     * @param a
     * @param s
     * @return a*s
     */
    public static Vecteur2D mult(Vecteur2D a, double s){
        return new Vecteur2D(a.x * s,a.y*s);
    }

    /**
     * Retourne un vecteur dont les composantes sont les composantes de a multiplié par les composantes de b
     * @param a
     * @param b
     * @return (a.x*b.x; a.y*b.y)
     */
    public static Vecteur2D mult(Vecteur2D a, Vecteur2D b){
        return new Vecteur2D(a.x*b.x,a.y*b.y);
    }

    /**
     * Retourne un vecteur dont les composantes sont les composantes de a divisé par les composantes de b
     * @param a
     * @param b
     * @return (a.x/b.x; a.y/b.y)
     */
    public static Vecteur2D div(Vecteur2D a, Vecteur2D b){
        return new Vecteur2D(a.x/b.x,a.y/b.y);
    }

    /**
     * Retourne la longueur de ce vecteur
     * @return ||a||
     */
    public double longueur(){
        return (double) Math.sqrt(Math.pow(x,2.0)+Math.pow(y,2.0));
    }

    /**
     * Retourne la distance en a et b
     * @param a
     * @param b
     * @return ||a-b||
     */
    public static double distance(Vecteur2D a, Vecteur2D b){
        return (double) Math.sqrt(Math.pow(a.x-b.x,2.0)+Math.pow(a.y-b.y,2.0));
    }

    /**
     * Retourne le produit scalaire entre les vecteurs a et b
     * @param a
     * @param b
     * @return a•b
     */
    public static double scal(Vecteur2D a,Vecteur2D b){
        return (a.x*b.x)+(a.y*b.y);
    }

    /**
     * Retourne le vecteur a normalisé
     * @param a
     * @return (1/||a||)*a
     */
    public static Vecteur2D norm(Vecteur2D a){
        if(a.longueur() != 0 ){
            return new Vecteur2D(a.x/a.longueur(),a.y/a.longueur());
        }else{
            return new Vecteur2D(0);
        }
    }

    /**
     * Retourne une copie de ce vecteur
     * @return new Vecteur(x,y)
     */
    public Vecteur2D copier(){
        return new Vecteur2D(x,y);
    }

    /**
     * Retourne l'opposé de ce vecteur
     * @return -a
     */
    public Vecteur2D opposé(){
        return new Vecteur2D(-x,-y);
    }
}
