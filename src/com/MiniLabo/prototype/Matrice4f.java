package com.MiniLabo.prototype;

/* index:
 * 0 ; 1 ; 2 ; 3
 * 4 ; 5 ; 6 ; 7
 * 8 ; 9 ; 10; 11
 * 12; 13; 14; 15
 *
 * use : x + y*4
 * */

public class Matrice4f {
    public double[] mat;

    public void changer(double[] mat){
        if(mat.length == 16) {
            this.mat = mat;
        }else{
            System.err.println("Matrice4f.changer(), mat[] doit avoir une longueur de 16");
        }
    }

    public Matrice4f(){
        mat = new double[16];
        faireIdentité();
    }

    public void faireIdentité(){
        mat = new double[]
                {1.0, 0.0, 0.0, 0.0,
                 0.0, 1.0, 0.0, 0.0,
                 0.0, 0.0, 1.0, 0.0,
                 0.0, 0.0, 0.0, 1.0};
    }

    public Matrice4f avoirIdentité(){
        mat = new double[]
                {1.0, 0.0, 0.0, 0.0,
                 0.0, 1.0, 0.0, 0.0,
                 0.0, 0.0, 1.0, 0.0,
                 0.0, 0.0, 0.0, 1.0};
        return this;
    }

    //TODO #15 Implémenter les fonctionnalités de Matrice4f
    public static Vecteur3D MultiplierMV(Matrice4f m, Vecteur3D v){
        double[] vec = new double[]{v.x,v.y,v.z,1.0f};
        double[] res = new double[4];
        Matrix.multiplyMV(res,0,m.mat,0,vec,0);
        return new Vecteur3D(res[0]/res[3],res[1]/res[3],res[2]/res[3]);
    }

    public static Matrice4f MultiplierMM(Matrice4f m, Matrice4f mb){
        Matrice4f res = new Matrice4f();
        Matrix.multiplyMM(res.mat,0,m.mat,0,mb.mat,0);
        return res;
    }

    public void inverse(){
        Matrix.invertM(mat,0,mat,0);
    }

    public static Matrice4f inverse(Matrice4f mat){
        Matrice4f resMat = new Matrice4f();
        Matrix.invertM(resMat.mat,0,mat.mat,0);
        return resMat;
    }

    public void rotation(Vecteur3D rotation){
        Matrice4f rot = new Matrice4f();
        Matrix.rotateM(rot.mat,0,rotation.x,1.0f,0.0f,0.0f);
        Matrix.rotateM(rot.mat,0,rotation.y,0.0f,1.0f,0.0f);
        Matrix.rotateM(rot.mat,0,rotation.z,0.0f,0.0f,1.0f);
        this.mat = Matrice4f.MultiplierMM(rot,this).mat;
    }

    public void translation(Vecteur3D translation){
        Matrix.translateM(mat, 0, mat, 0, translation.x,translation.y,translation.z);
    }

    public void échelle(Vecteur3D scale){
        Matrix.scaleM(mat, 0, mat, 0, scale.x,scale.y,scale.z);
    }

    public Matrice4f copier(){
        Matrice4f r = new Matrice4f();
        for (int i = 0; i < mat.length; i++) {
            r.mat[i] = mat[i];
        }
        return r;
    }
}
