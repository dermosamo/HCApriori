
package seqmining.HierarchicalClustering;

/* 
 * KMeans.java ; Cluster.java ; Point.java
 *
 * Solution implemented by DataOnFocus
 * www.dataonfocus.com
 * 2015
 *
*/

import java.util.ArrayList;
import java.util.List;
import seqmining.HierarchicalClustering.Point;
import seqmining.HierarchicalClustering.Cluster;
import com.opencsv.CSVReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import seqmining.Parameters;
import seqmining.DataClasse.Event;
import seqmining.DataClasse.SequenceData;
import seqmining.DataClasse.Sequence;
import seqmining.util.Metrics;
public class HierarchicalClustering {
     //Number of Events
    static private List<Point> points;
    static private List<Cluster> clusters;
    public static HashMap<Integer, Double> Epsilon = new HashMap<Integer, Double>();
    public static SequenceData dataset;
    public static void HC()throws IOException 
    {
        int cpt=0;
        Epsilon = new HashMap<Integer, Double>();
        StringBuilder content = new StringBuilder();
        //load data from input file
        dataset = new SequenceData(Parameters.DATA_PATH);
        for (int i=0;i<=Parameters.NumberEventType;i++)
        {
                points = new ArrayList();
                clusters = new ArrayList();  
                //load all events of type i from the inputfile and compute their mean duration
                Double MeanDuration=DataEventType(i,dataset);
                //Hierarchical Clustering based on similariry, as input, we give the minimum similariry to respect between events 
                Double sim = MeanDuration*Parameters.epsilon;
                Epsilon.put(i,sim);
                HerachicalClusteringDistance(sim);
                //We keep only the clusters that contains at less nb elements
                clusters=Cluster.ClustersFiltrer(clusters,Parameters.minEventNumber);
                //The clusters are ordered in ascending order based on their starting time
                clusters=Cluster.OrdedClusters(clusters);
                //results display
                if(clusters.size()>0)
                {
                    content.append(i+"\n");
                    Cluster.plotClusters(clusters, content,Parameters.minEventNumber);
                    cpt++;
                }
        }
            //Save the results in out file  
            File f = new File(Parameters.CENTROID_PATH);   
            BufferedWriter writer = new BufferedWriter(new FileWriter(f)); //writers
            writer.write(cpt + "\n");
            writer.write(content.toString());
            System.out.println(content);
            writer.close();
            SaveSimilarity(Parameters.Epsilon_PATH);
    }
    //for each event, we load from the similarity file, the minimum similarity or maximum distance to use when merging events
    public static void SaveSimilarity(String outputfilename) throws FileNotFoundException, IOException 
    {
        File f = new File(outputfilename);   
        BufferedWriter writer = new BufferedWriter(new FileWriter(f)); //writers
        Iterator<Integer> keySetIterator = Epsilon.keySet().iterator(); 
        while(keySetIterator.hasNext())
        { 
            Integer key = keySetIterator.next(); 
            writer.write(key + " "+ Epsilon.get(key)+ "\n");
        } 
        writer.close();
    }
    
     //  Type StartTime and Duration of a given non-verbal signal (EventType) in the input file
   public static Double DataEventType(int EventType, SequenceData datasett)
   {
                        Double duration =0.0;
                        int cpt=0;
                        for (Sequence example : datasett.getExamples()) 
                        {
                            List<Event> events= example.getEvents();
                            for (Event e : events)
                            {
                                if(e.getEventType()==EventType)
                                {
                                  Point p= new Point (e.getStartTime(), e.getDuration());
                                  points.add(p);
                                  duration+=e.getDuration();
                                  cpt++;
                                }
                            }
                        }
                        if (cpt!=0)
                        return duration/cpt;
                        else
                            return 0.0;
   }
    
    // Hierarchical Clustering based on similariry, as input, we give the minimum similariry to respect between events 
    public static void HerachicalClusteringSimilarity(double dist) 
    {
        //Initialisation
        int cpt=0;
        for(Point p : points) 
        {
    		Cluster cluster = new Cluster(cpt);
    		cluster.setCentroid(p);
                cluster.addPoint(p);
    		clusters.add(cluster);
                cpt++;
        } 
       boolean end=false;
       while(!end)
       {
            double MaxDistance=0;
            int Cluster1=-1;
            int Cluster2=-1;
            for(Cluster c1 : clusters) 
            {
                for(Cluster c2 : clusters) 
                {
                    if(c1.getId()!=c2.getId()&&!c1.Merged&&!c2.Merged)
                    {
                        double distance = Point.distanceoverlap(c1.getCentroid(), c2.getCentroid());
                        if(distance > MaxDistance )
                        {
                            MaxDistance = distance;
                            Cluster1=c1.getId();
                            Cluster2=c2.getId();
                            //System.out.println("distance between  : " + c1.getId() +" and : " + c2.getId()+" is: "+distance);
                        }
                    }
                }
            }
            if(MaxDistance>dist)
            {
                //plotClusters();
                //System.out.println("Cluster1: " + Cluster1 +" x "+clusters.get(Cluster1).getCentroid().getX()+ " Y "+clusters.get(Cluster1).getCentroid().getY()+" Cluster2: " + Cluster2+" "+" x "+clusters.get(Cluster2).getCentroid().getX()+ " Y "+clusters.get(Cluster2).getCentroid().getY()+"MaxDistance "+ MaxDistance);
                 double distance = Point.distanceoverlap(clusters.get(Cluster1).getCentroid(), clusters.get(Cluster2).getCentroid());
                 //System.out.println("distance: "+ distance);
                MergeClusters(Cluster1,Cluster2);
            }
            else
            {
                if(MaxDistance<dist||clusters.size()==1)
                {
                      end=true;
                       //System.out.println("Finnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnns ");
                }
            }
       }

    }
    //Hierarchical Clustering based on distance, as input, we give the minimum similariry to respect between events 
    public static void HerachicalClusteringDistance(double dist) 
    {
        //Initialisation
        int cpt=0;
        for(Point p : points) 
        {
    		Cluster cluster = new Cluster(cpt);
    		cluster.setCentroid(p);
                cluster.addPoint(p);
    		clusters.add(cluster);
                cpt++;
        } 
       boolean end=false;
       while(!end)
       {
            double MinDistance=Double.POSITIVE_INFINITY;
            int Cluster1=-1;
            int Cluster2=-1;
            for(Cluster c1 : clusters) 
            {
                for(Cluster c2 : clusters) 
                {
                    if(c1.getId()!=c2.getId()&&!c1.Merged&&!c2.Merged)
                    {
                        double distance = Point.distanceCityBlock(c1.getCentroid(), c2.getCentroid());
                        if(distance < MinDistance )
                        {
                            MinDistance = distance;
                            Cluster1=c1.getId();
                            Cluster2=c2.getId();
                            //System.out.println("distance between  : " + c1.getId() +" and : " + c2.getId()+" is: "+distance);
                        }
                    }
                }
            }
            if(MinDistance<=dist)
            {
                   MergeClusters(Cluster1,Cluster2);
            }
            else
            {
                if(MinDistance>dist||clusters.size()==1)
                {
                      end=true;
                      // System.out.println("Finnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnns ");
                }
            }
       }

    }
    //Merge two clusters
    public static void MergeClusters(int Cluster1Id,int Cluster2Id) 
    {
        Cluster C1=clusters.get(Cluster1Id);
        Cluster C2=clusters.get(Cluster2Id);
        C1.addPoints(C2.getPoints());
        clusters.get(Cluster2Id).setMerged(true);
        C1.calculateCentroid();
    }
}
