package com.MiniLabo.prototype;

/**Cette classe est une abréviation de Vecteur3D
 * <p>Vecteur de 3 dimension à coordonées cartésiennes (x,y,z)</p>
 */
public class V3 extends Vecteur3D{
    /**
     * Créé un nouveau vecteur
     * @param x - La composante x
     * @param y - La composante y
     * @param z - La composante z
     */
    public V3(double x, double y, double z){
        super(x, y, z);
    }

    /**
     * Créé un nouveau vecteur en clonant le vecteur clone
     * @param clone
     */
    public V3(Vecteur3D clone){
        super(clone);
    }

    /**
     * Créé un nouveau vecteur
     * @param xyz - Composantes x,y,z
     */
    public V3(double xyz){
        super(xyz);
    }
}
