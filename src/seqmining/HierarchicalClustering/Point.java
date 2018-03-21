/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package seqmining.HierarchicalClustering;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import seqmining.DataClasse.Pattern;
import seqmining.DataClasse.Event;
/**
 *
 * @author Soumia Dermouche
 */
public class Point {
     private double x = 0;
    private double y = 0;
    private int cluster_number = 0;

    public Point(double x, double y)
    {
        this.setX(x);
        this.setY(y);
    }
    
    public void setX(double x) {
        this.x = x;
    }
    
    public double getX()  {
        return this.x;
    }
    
    public void setY(double y) {
        this.y = y;
    }
    
    public double getY() {
        return this.y;
    }
    
    public void setCluster(int n) {
        this.cluster_number = n;
    }
    
    public int getCluster() {
        return this.cluster_number;
    }
     //Calculates the City block distance between two points.
    protected static double distanceCityBlock(Point p, Point centroid) 
    {
        double res= Math.abs((centroid.getY()+centroid.getX()) - (p.getY()+p.getX())) + (Math.abs(centroid.getX() - p.getX()));
        //System.out.println(p.toString()+" "+centroid.toString()+ " dit: "+ res);
        return (res);
    }
    
    
    //Calculates the overlap distance between two points.
    protected static double distanceoverlap(Point p, Point centroid) 
    {
        if(Math.min(centroid.getY()+centroid.getX() ,p.getY()+p.getX())> Math.max(centroid.getX() , p.getX()))
         return ((Math.min(centroid.getX()+centroid.getY() , p.getX()+p.getY())-Math.max(centroid.getX() , p.getX())) / (Math.max(centroid.getX()+centroid.getY() ,p.getX()+p.getY())-Math.min (centroid.getX() , p.getX())));
        else
            return 0;
    }
    
     @Override
    public String toString() {
    	return "("+x+","+y+")";
    }
}
