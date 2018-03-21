
package seqmining.HierarchicalClustering;
import java.util.ArrayList;
import java.util.List;
import seqmining.HierarchicalClustering.Point;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;
import static seqmining.HierarchicalClustering.Cluster2D.DoubleFormat;
/**
 *
 * @author Soumia Dermouche
 */
public class Cluster {
        public List<Point> points;
	public Point centroid;
	public int id;
        public boolean Merged;
        public boolean Tried;
	
	public Cluster(int id) {
		this.id = id;
		this.points = new ArrayList();
		this.centroid = null;
                this.Merged = false;
                this.Tried=false;
	}
	public Cluster(int id, List<Point>  points, Point centroid) {
		this.id = id;
		this.points = points;
		this.centroid = centroid;
                this.Merged = false;
                 this.Tried=false;
	}
	public List getPoints() {
		return points;
	}
	
	public void addPoints(List<Point> Listpoint) {
            for(Point p : Listpoint)
		points.add(p);
	}
        public void addPoint(Point point) {
		points.add(point);
	}
	public void setPoints(List points) {
		this.points = points;
	}

	public Point getCentroid() {
		return centroid;
	}

	public void setCentroid(Point centroid) {
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
		points.clear();
	}
        
        public  void calculateCentroid() {
            double sumX = 0;
            double sumY = 0;
              
            for(Point point : points)
            {
            	sumX += point.getX();
                sumY += point.getY();
            }
               if(points.size() > 0) 
               {
                    double newX = sumX / points.size();
                    double newY = sumY / points.size();
                    this.setCentroid(new Point (newX, newY));
                }
    }
        
        public static  List<Cluster> ClustersFiltrer(List<Cluster> clusters, int PointsNumber) 
        {
            List<Cluster> SaveClusters= new ArrayList();
            int cpt=-1;
            for (Cluster  c : clusters) 
            {
                    if(!c.Merged && c.points.size()>PointsNumber)
                    {
                            cpt++;
                            Cluster newCluster= new Cluster (cpt,c.getPoints(),c.getCentroid());
                            System.out.println(" ffffffffff "+c.getPoints()+ " "+c.getCentroid());
                            SaveClusters.add((newCluster));
                    }
            }
            return SaveClusters;
        }
        public static StringBuilder  plotClusters(List<Cluster> clusters, StringBuilder content, int ClusterNumber) 
        {
            StringBuilder StartTime = new StringBuilder();
            StringBuilder Duration = new StringBuilder();
            for (Cluster  c : clusters) 
            {
                    if(!c.Merged)
                    {
                        if(c.points.size()> ClusterNumber)
                        {
                            System.out.println("[ Cluster Centroid: " + DoubleFormat(c.getCentroid().getX())+" " +DoubleFormat(c.getCentroid().getY())+ "]");
                            StartTime.append(DoubleFormat(c.getCentroid().getX())).append(" ");
                            Duration.append(DoubleFormat(c.getCentroid().getY())).append(" ");
                            //System.out.println("[Points: \n");
                            for(Point p : c.points) 
                            {
                                //System.out.println(p);
                            }
                            //System.out.println("]");
                       }
                    }
            }
            // System.out.println(StartTime);
            //System.out.println(Duration);
            content.append(StartTime).append("\n");
            content.append(Duration).append("\n");
            return content;
        }
       
       //Orded Clusters 
        public static List<Cluster> OrdedClusters(List<Cluster> clusters) 
        {
           List<Cluster> SaveClusters= new ArrayList();
             int cpt=-1;
           boolean end=false;
            while(!end)
            {
                   int min = MinCluster(clusters);
                   if(min!=-1)
                   {
                        cpt++;
                        clusters.get(min).setTried(true);
                        Cluster newCluster= new Cluster (cpt,clusters.get(min).getPoints(),clusters.get(min).getCentroid());
                        SaveClusters.add((newCluster));
                   }
                   else
                       end=true;
                       
            }
            return SaveClusters;
        }
        //Cluter with the minimum start time  
        public static int MinCluster(List<Cluster> clusters) 
        {
           double minClus=Double.POSITIVE_INFINITY;
           int SaveCluster=-1;
           for (Cluster  C : clusters) 
            {
                    if(!C.getTried() && C.getCentroid().getX()<minClus)
                    {
                       minClus=C.getCentroid().getX();
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
