
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
import seqmining.DataClasse.Match;
import seqmining.DataClasse.Pattern;
import seqmining.DataClasse.SequenceData;
import seqmining.DataClasse.Sequence;
import seqmining.util.AprioriUtils;
import seqmining.util.Metrics;
public class HierarchicalClusteringND {
     //Number of Events
    static private List<Cluster2D> clusters;
    public static HashMap<Integer, Double> Epsilon = new HashMap<Integer, Double>();
    public static SequenceData dataset;
    
    public static void Epsilon()throws IOException 
    {
        Epsilon = new HashMap<Integer, Double>();
        dataset = new SequenceData(Parameters.DATA_PATH);
        for (int i=0;i<=Parameters.NumberEventType;i++)
        {
                Double MeanDuration=DataEventType(i,dataset);
                Double sim = MeanDuration*Parameters.epsilon;
                Epsilon.put(i,sim);
        }
        SaveSimilarity(Parameters.Epsilon_PATH);
        Loadsimilarity(Parameters.Epsilon_PATH);
    }
     //for each event, we load from the similarity file, the minimum similarity or maximum distance to use when merging events
    public static void Loadsimilarity(String inputfilename)throws FileNotFoundException, IOException 
    {
       Epsilon = new HashMap<Integer, Double>();
       CSVReader csvr = new CSVReader(new FileReader(inputfilename),' ');
	//handle column definitions
        String[] lineDef = csvr.readNext();       
        String[] CurrentLine;
	        while((CurrentLine=csvr.readNext()) != null)
	        {
                   Epsilon.put(Integer.valueOf(CurrentLine[0]),Double.valueOf(CurrentLine[1]));
                }
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
    
    public static List<Pattern> HCND(List<Pattern> Patterns)throws IOException 
    {
        Epsilon();
        //Initialisation
        clusters = new ArrayList();  
        int cpt=0;
        for(Pattern p : Patterns) 
        {
    		Cluster2D cluster = new Cluster2D(cpt);
    		cluster.setCentroid(p);
                cluster.addPattern(p);
    		clusters.add(cluster);
                cpt++;
        } 
                HerachicalClusteringDistance();
                //We keep only the clusters that contains at less nb elements
                clusters=Cluster2D.ClustersFiltrer(clusters,Parameters.minEventNumber);
                //The clusters are ordered in ascending order based on their starting time
                clusters=Cluster2D.OrdedClusters(clusters);
                //results display
                if(clusters.size()>0)
                {
                    Cluster2D.plotClusters(clusters);
                }
                List<Pattern> CentroidsPatterns= new ArrayList<Pattern>();
                for (Cluster2D c : clusters)
                {
                    CentroidsPatterns.add(c.getCentroid());
                }
                return CentroidsPatterns;
    }
   
    
    //Hierarchical Clustering based on distance, as input, we give the minimum similariry to respect between events 
    public static void HerachicalClusteringDistance() 
    { 
       boolean end=false;
       while(!end)
       {
            Match savematch= new Match(false,Double.POSITIVE_INFINITY);
            int Cluster1=-1;
            int Cluster2=-1;
            for(Cluster2D c1 : clusters) 
            {
                for(Cluster2D c2 : clusters) 
                {
                    if(c1.getId()!=c2.getId()&&!c1.Merged&&!c2.Merged)
                    {
                        Match res = AprioriUtils.CityBlockPatterns(c1.getCentroid(), c2.getCentroid());
                        //System.out.println("distance between  : " + c1.getPatterns() +" and : " + c2.getPatterns()+" is: "+res.getProb() );
                        if(res.getProb() < savematch.getProb() )
                        {
                            savematch=res;
                            Cluster1=c1.getId();
                            Cluster2=c2.getId();
                            //System.out.println("distance between  : " + c1.getId() +" and : " + c2.getId()+" is: "+res.getProb() );
                        }
                    }
                }
            }
            if(savematch.getMatch())
            {
                   MergeClusters(Cluster1,Cluster2);
                   //System.out.println("Cluster  : " + Cluster2 +" is merged in : " + Cluster1);
            }
            else
            {
                if(!savematch.getMatch()||clusters.size()==1)
                {
                      end=true;
                }
            }
       }

    }
    //Merge two clusters
    public static void MergeClusters(int Cluster1Id,int Cluster2Id) 
    {
        Cluster2D C1=clusters.get(Cluster1Id);
        Cluster2D C2=clusters.get(Cluster2Id);
        C1.addPatterns(C2.getPatterns());
        clusters.get(Cluster2Id).setMerged(true);
        //System.out.println(" the cluster "+ Cluster2Id + " is merged ");
       C1.calculateCentroid(C1.getCentroid(),C2.getCentroid());
    }
}
