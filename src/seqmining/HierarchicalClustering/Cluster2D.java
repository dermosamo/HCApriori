
package seqmining.HierarchicalClustering;
import java.util.ArrayList;
import java.util.List;
import seqmining.HierarchicalClustering.Point;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;
import seqmining.DataClasse.Event;
import seqmining.DataClasse.Pattern;
import seqmining.util.AprioriUtils;
/**
 *
 * @author Soumia Dermouche
 */
public class Cluster2D {
        public List<Pattern> Patterns;
	public Pattern centroid;
	public int id;
        public boolean Merged;
        public boolean Tried;
	
	public Cluster2D(int id) {
		this.id = id;
		this.Patterns = new ArrayList();
		this.centroid = null;
                this.Merged = false;
                this.Tried=false;
	}
	public Cluster2D(int id, List<Pattern>  Patterns, Pattern centroid) {
		this.id = id;
		this.Patterns = Patterns;
		this.centroid = centroid;
                this.Merged = false;
                this.Tried=false;
	}
	public List getPatterns() {
		return Patterns;
	}
	public void addPatterns(List<Pattern> Listpatterns) 
        {
            for(Pattern p : Listpatterns)
		Patterns.add(p);
	}
        public void addPattern(Pattern P) {
		Patterns.add(P);
	}
	public void setPattterns(List<Pattern> Listpatterns) {
		this.Patterns = Listpatterns;
	}

	public Pattern getCentroid() {
		return centroid;
	}

	public void setCentroid(Pattern centroid) {
		this.centroid = centroid;
	}
        
        public boolean getMerged() {
		return Merged;
	}
        public void setMerged(boolean Merged) {
		this.Merged = Merged;
	}
        public boolean getTried() {
		return Tried;
	}

	public void setTried(boolean Tried) {
		this.Tried = Tried;
	}

	public int getId() {
		return id;
	}
	
	public void clear() {
		Patterns.clear();
	}
        
        public  void calculateCentroid(Pattern P1, Pattern P2) {
            List<Event> Patterns1 = P1.getPattern(true);
            List<Event> Patterns2 = P2.getPattern(true);
            assert (Patterns1.size() == Patterns2.size());
            float starttime=0;
            float duration=0;
            Pattern C = new Pattern();
            for(int i=0;i<Patterns1.size();i++)
            {
                Event e1=Patterns1.get(i);
                Event e2=Patterns2.get(i);
                assert(e1.getEventType()== e2.getEventType());
                {
                    starttime=(e1.getStartTime()+e2.getStartTime())/2;
                    duration=(e1.getDuration()+e2.getDuration())/2;
                    C.addEvent(new Event(e1.getEventType(),starttime, duration));
                }
            }
            this.setCentroid(C);
    }
        
        public static  List<Cluster2D> ClustersFiltrer(List<Cluster2D> clusters, int PatternNumber) 
        {
            List<Cluster2D> SaveClusters= new ArrayList();
            int cpt=-1;
            for (Cluster2D  c : clusters) 
            {
                    if(!c.Merged && c.Patterns.size()>PatternNumber)
                    {
                            cpt++;
                            Cluster2D newCluster= new Cluster2D (cpt,c.getPatterns(),c.getCentroid());
                            //System.out.println(" ffffffffff "+c.getPatterns()+ " "+c.getCentroid());
                            SaveClusters.add((newCluster));
                    }
            }
            return SaveClusters;
        }
        
         
        public static void  plotClusters(List<Cluster2D> clusters) 
        {
            for (Cluster2D  c : clusters) 
            {
                    if(!c.Merged)
                    {
                        //System.out.println(c.toString());
                    }
            }
        }
       //Orded Clusters 
        public static List<Cluster2D> OrdedClusters(List<Cluster2D> clusters) 
        {
            List<Cluster2D> SaveClusters= new ArrayList();
            int cpt=-1;
            boolean end=false;
            while(!end)
            {
                   int min = MinCluster(clusters);
                   if(min!=-1)
                   {
                        cpt++;
                        clusters.get(min).setTried(true);
                        Cluster2D newCluster= new Cluster2D (cpt,clusters.get(min).getPatterns(),clusters.get(min).getCentroid());
                        SaveClusters.add((newCluster));
                   }
                   else
                       end=true;
                       
            }
            return SaveClusters;
        }
        //Cluter with the minimum start time  
        public static int MinCluster(List<Cluster2D> clusters) 
        {
           double minClus=Double.POSITIVE_INFINITY;
           int SaveCluster=-1;
           for (Cluster2D  C : clusters) 
            {
                    if(!C.getTried() && C.getCentroid().getPattern(true).get(0).getStartTime()<minClus)
                    {
                       minClus=C.getCentroid().getPattern(true).get(0).getStartTime();
                       SaveCluster=C.getId();
                    }
            }
          return SaveCluster;
        }
        
     public static double DoubleFormat(double f1)
     {
      DecimalFormat decim = new DecimalFormat("#.##", DecimalFormatSymbols.getInstance(Locale.ENGLISH));
      return (Double.parseDouble(decim.format(f1)));
     }
        
}
