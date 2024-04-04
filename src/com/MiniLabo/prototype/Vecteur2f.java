package MiniLabo.prototype;
public class Vecteur2f {
    public double x;
    public double y;

    public Vecteur2f(double x,double y){
        this.x = x;
        this.y = y;
    }

    public Vecteur2f(double xy){
        this.x = xy;
        this.y = xy;
    }

    public Vecteur2f(double angle, double module, int o){
        this.x = module*Math.cos(angle);
        this.y = module*Math.sin(angle);
    }

    public void add(Vecteur2f b){
        x += b.x;
        y += b.y;
    }

    public void sub(Vecteur2f b){
        x -= b.x;
        y -= b.y;
    }

    public void scale(double s){
        x *= s;
        y *= s;
    }

    public void multiply(Vecteur2f m){
        x *= m.x;
        y *= m.y;
    }

    public void divide(Vecteur2f d){
        x = x/d.x;
        y = y/d.y;
    }

    public void normalize(){
        double l = length();
        x = x/l;
        y = y/l;
    }

    public static Vecteur2f add(Vecteur2f a, Vecteur2f b){
        return new Vecteur2f(a.x+b.x, a.y + b.y);
    }

    public static Vecteur2f sub(Vecteur2f a, Vecteur2f b){
        return new Vecteur2f(a.x-b.x, a.y - b.y);
    }

    public static Vecteur2f scale(Vecteur2f a, double s){
        return new Vecteur2f(a.x * s,a.y*s);
    }

    public static Vecteur2f multiply(Vecteur2f a, Vecteur2f b){
        return new Vecteur2f(a.x*b.x,a.y*b.y);
    }

    public static Vecteur2f divide(Vecteur2f a, Vecteur2f b){
        return new Vecteur2f(a.x/b.x,a.y/b.y);
    }

    public double length(){
        return (double) Math.sqrt(Math.pow(x,2.0)+Math.pow(y,2.0));
    }

    public static double distance(Vecteur2f a, Vecteur2f b){
        return (double) Math.sqrt(Math.pow(a.x-b.x,2.0)+Math.pow(a.y-b.y,2.0));
    }

    public static double dot(Vecteur2f a,Vecteur2f b){
        return (a.x*b.x)+(a.y*b.y);
    }

    public static Vecteur2f normalize(Vecteur2f a){
        if(a.length() != 0 ){
            return new Vecteur2f(a.x/a.length(),a.y/a.length());
        }else{
            return new Vecteur2f(0);
        }
    }

    public Vecteur2f copy(){
        return new Vecteur2f(x,y);
    }

    public Vecteur2f negative(){
        return new Vecteur2f(-x,-y);
    }
}
