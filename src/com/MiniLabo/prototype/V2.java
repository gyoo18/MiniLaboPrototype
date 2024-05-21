package com.MiniLabo.prototype;

/**Cette classe est une abréviation de Vecteur2D
 * <p>Vecteur de 2 dimension à coordonées cartésiennes (x,y)</p>
 */
public class V2 extends Vecteur2D{
      /**
     * Créé un nouveau vecteur
     * @param x - La composante x
     * @param y - La composante y
     */
    public V2(double x, double y){
        super(x, y);
    }

    /**
     * Créé un nouveau vecteur en clonant le vecteur clone
     * @param clone
     */
    public V2(Vecteur2D clone){
        super(clone);
    }

    /**
     * Créé un nouveau vecteur
     * @param xy - Composantes x,y
     */
    public V2(double xy){
        super(xy);
    }
}
