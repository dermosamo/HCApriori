package seqmining.util;
/**
 *
 * @author Soumia Dermouche
 */
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.opencsv.CSVReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Set;
import seqmining.Parameters;
import seqmining.HierarchicalClustering.HierarchicalClustering;

import seqmining.DataClasse.Event;
import seqmining.DataClasse.Sequence;
import seqmining.DataClasse.Pattern;
import static seqmining.HierarchicalClustering.HierarchicalClustering.HC;
import seqmining.DataClasse.Match;
import seqmining.DataClasse.SequenceData;
import seqmining.HierarchicalClustering.HierarchicalClusteringND;

public class AprioriUtils {
	private static float THRESHOLD = 1.0E-5f;

	public static class MatchResult {
		private boolean match;
		private float prob;

		public MatchResult(boolean match, float prob) 
                {
			super();
			this.match = match;
			this.prob = prob;
		}

		public boolean isMatch() 
                {
			return match;
		}

		public float getProb() 
                {
			return prob;
		}

	}
        public static HashMap<Integer, Double> Epsilon = new HashMap<Integer, Double>();
	/**
	 * Match between an example and an event
	 * 
	 * @param example
	 * @param event
	 * @param usePenalty
	 * @param penaltyThreshold
	 * @param penaltyFactor
	 * @return
	 */
	public static MatchResult match(Sequence example, Pattern pattern) 
        {
                float prob = 0f;

		if (pattern.getNumEvents() > example.getNumEvents()) 
                {
			return new MatchResult(false, -1f);
		}
                boolean res=EqualEventSize(example, pattern);
                if (!res) 
                {
			return new MatchResult(false, 0f);
		}
		for (Event event : pattern.getPattern(false)) 
                {
			MatchResult matchRes = match(example, event);
			if (!matchRes.isMatch()) 
                        {
				return new MatchResult(false, -1f);
			}
			prob += matchRes.getProb();
		}
                return new MatchResult(true, prob);
	}
        public static Match CityBlockPatterns(Pattern p1, Pattern p2) 
        {
            List<Event> Patterns1 = p1.getPattern(true);
            List<Event> Patterns2 = p2.getPattern(true);
            assert (Patterns1.size() == Patterns2.size());
            float resT=0;
            for(int i=0;i<Patterns1.size();i++)
            {
                Event e1=Patterns1.get(i);
                Event e2=Patterns2.get(i);
                assert(e1.getEventType()== e2.getEventType());
                {
                    float res = Math.abs(e1.getEndTime() - e2.getEndTime()) + Math.abs(e1.getStartTime()- e2.getStartTime());
                    resT+=res;
                    Double sim = Epsilon.get(e1.getEventType());
                    if(res>sim)
                    {
                        return new Match(false, Double.POSITIVE_INFINITY);    
                    }
                }
            }
            //System.out.println("Pattern "+ p1.toString()+" match "+ p2.toString()+ " res "+ resT);
            return new Match(true, resT);
        }
        public  static boolean EqualEventSize(Sequence example, Pattern pattern)
        {
                List<Integer> Eventspat=pattern.getSignature();
                List<Integer> EventSeq= example.getSignature2();
                //System.out.println(" example "+example.toString()+" seqseg "+ example.getSignature2().toString()+" pattern "+pattern.toString()+" patsig "+ pattern.getSignature2().toString());
                int j=0;
                int indiceStart=0;
                while (j<Eventspat.size())
                {
                              int results = findEventInList(EventSeq, indiceStart,Eventspat.get(j));
                              if(results==-1)
                                   return false;
                              else
                              {
                                indiceStart =results;
                                j++;
                              }
                }
                return true;
        }
        
        public  static boolean Signaturescomparaison(List<Integer> Eventspat,  List<Integer> EventSeq)
        {
                //System.out.println(" example "+example.toString()+" seqseg "+ example.getSignature2().toString()+" pattern "+pattern.toString()+" patsig "+ pattern.getSignature2().toString());
                if (Eventspat.size()>EventSeq.size())
                    return false;
                int j=0;
                int indiceStart=0;
                while (j<Eventspat.size())
                {
                              int results = findEventInList(EventSeq, indiceStart,Eventspat.get(j));
                              if(results==-1)
                              {
                                  //System.out.println("nOOOOOO Pattern "+ Eventspat.toString()+" Sequence "+ EventSeq.toString());
                                  return false;
                              }
                              else
                              {
                                //System.out.println(" yESSSS Pattern "+ Eventspat.toString()+" Sequence "+ EventSeq.toString());
                                indiceStart =results+1;
                                j++;
                              }
                }
                return true;
        }
        
        public static double SignaturescomparaisonOverlap(List<Integer> Eventspat, Pattern pat,  List<Integer> EventSeq, Pattern Sequence)
        {
                //System.out.println(" example "+example.toString()+" seqseg "+ example.getSignature2().toString()+" pattern "+pattern.toString()+" patsig "+ pattern.getSignature2().toString());
                if (Eventspat.size()>EventSeq.size()||!Signaturescomparaison(Eventspat,EventSeq))
                    return 0;
                int j=0;
                int indiceStart=0;
                double overlap=0;
                while (j<Eventspat.size())
                {
                              int results = findEventInList(EventSeq, indiceStart,Eventspat.get(j));
                              if(results==-1)
                              {
                                  //System.out.println("nOOOOOO Pattern "+ Eventspat.toString()+" Sequence "+ EventSeq.toString());
                                  return 0;
                              }
                              else
                              {
                                
                                overlap+= Overlap(pat.getPattern(true).get(j), Sequence.getPattern(true).get(results));
                                //System.out.println(" yESSSS Pattern "+ Eventspat.toString()+" Sequence "+ EventSeq.toString()+ " Overlap"+ overlap);
                                indiceStart =results+1;
                                j++;
                              }
                }
                return overlap/Eventspat.size();
        }
        
        public static Pattern PatternFromSignature(Sequence example, List<Integer> signature)
        {
            
                List<Integer> EventSeq= example.getSignature2();
                //System.out.println(" example "+example.toString()+" seqseg "+ example.getSignature2().toString()+" pattern "+pattern.toString()+" patsig "+ pattern.getSignature2().toString());
                int j=0;
                int indiceStart=0;
                Pattern P= new Pattern();
                while (j<signature.size())
                {
                              int results = findEventInList(EventSeq, indiceStart,signature.get(j));
                              if(results==-1)
                                   return null;
                              else
                              {
                                  indiceStart = results+1;
                                  j++;
                                  P.addEvent(example.getEvents().get(results));
                              }
                }
                //System.out.println(" example "+example.toString()+" signature "+ signature.toString()+" pattern "+P.toString());
                return P;
        }
        public static int findEventInList(List<Integer> ListEvents, int indiceStart, int EventType )
        {
                int res=-1;
                int j=indiceStart;
                while (j<ListEvents.size())
                {
                    if(ListEvents.get(j)== EventType)
                      return j;
                    else
                       j++;
                }
            return res;
        } 
        public static MatchResult match(Sequence example, Event event) 
        {
		List<Event> events = example.getEventsByType(event.getEventType(),true, null);
		if ((null == events) || (events.size() == 0)) 
                {
			return new MatchResult(false, 1f);
		}
		float savediff = Float.POSITIVE_INFINITY;
                Event saveEvent=null;
		for (Event ev : events) 
                {
			float dist=Event.distanceCityBlock(ev,event);
                        if(dist<savediff)
                        {
                            savediff=dist;
                            saveEvent=ev;
                        }
		}
		if (saveEvent==null) 
                {
			return new MatchResult(false, 1f);
		}
		float diff = Math.abs(saveEvent.getStartTime() - event.getStartTime())+Math.abs(saveEvent.getEndTime() - event.getEndTime());
                //System.out.println(" Event "+ event.getEventType()+" Epsilon "+Epsilon.toString());
                Double sim = Epsilon.get(event.getEventType());
                if(diff<sim)
                {
                     return new MatchResult(true, diff);    
                }
                else
                    return new MatchResult(false, 1f);
	}
        public static double Overlap(Event event1, Event event2) 
        {
		if ((event1.getEventType() != event2.getEventType())) 
                {
			return 0;
		}
			float maxStartTime = Math.max(event1.getStartTime(),event2.getStartTime());
			float minEndTime = Math.min(event1.getEndTime(), event2.getEndTime());

			if (!(minEndTime > maxStartTime))
                        {
				return 0;
			}
                        else
                        {
                            double diffstart= Math.abs(event1.getStartTime() - event2.getStartTime());
                            double diffend= Math.abs(event1.getEndTime() - event2.getEndTime());
                            double interval= Math.max(event1.getEndTime(), event2.getEndTime())-Math.min(event1.getStartTime(),event2.getStartTime());
                            double o=(interval-diffend-diffstart)/interval;
                            //System.out.println(" diffstart "+diffstart+" diffend "+diffend+" interval "+ interval+" overlap "+o);
                            return o;
                        }
	}
    public static void LoadEpsilon(String inputfilename)throws FileNotFoundException, IOException 
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
    public static void patternsCombinaison() throws IOException
    {
        List<Pattern> sequences2 = Pattern.loadPatterns("C:\\Users\\soumi\\Documents\\NetBeansProjects\\HCApriori\\data\\variation\\S\\R2\\NDPatternsFDeval.txt", "DI");
        System.out.println("sequences2 size "+ sequences2.size());
        //List<Sequence> sequences2=dataset.getExamples();
        List<Pattern> patterns=  new ArrayList<Pattern>();
        //int nbexample=dataset.getExamples().size();
        int cpt=0;
        for (Pattern S1: sequences2)
        {
            for (Pattern S2: sequences2)
            {
                if(S1!=S2)
                {
                    cpt++;
                    Pattern p= new Pattern();
                    for(Event e : S1.getPattern(true))
                    {
                         p.addEvent(e);
                    }
                    float starttime=p.getLastEvent().getStartTime()+p.getLastEvent().getDuration();
                    //System.out.println(" starttime "+ starttime+ " e start time "+ S3.getEvents().get(S3.getNumEvents()-1).getStartTime()+ " duration "+ S3.getEvents().get(S3.getNumEvents()-1).getDuration()+ " Event "+ S3.getEvents().get(S3.getNumEvents()-1).toString());

                    for(Event e : S2.getPattern(true))
                    {
                         p.addEvent(new Event(e.getEventType(),(e.getStartTime()+starttime),e.getDuration()));
                    }
                    
                    System.out.println(" cpt " + cpt + " p " + p);
                    p.setSupport((S1.getSupport()+S2.getSupport())/2);
                    p.setSupportOverlap((S1.getSupportOverlap()+S2.getSupportOverlap())/2);
                    p.setconfidence((S1.getconfidence()+S2.getconfidence())/2);
                    p.setconfidenceOverlap((S1.getconfidenceOverlap()+S2.getconfidenceOverlap())/2);
                    patterns.add(p);
                }
            }
        }
        Pattern.savePatternsToTextFile(patterns,"C:\\Users\\soumi\\Documents\\NetBeansProjects\\HCApriori\\data\\variation\\S\\R2\\NDPatternsFD2.txt");
    }
        public static void PatternsEvaluation(String turn,int i, String Variation)throws IOException
        {
            List<Pattern> pattterns = new ArrayList<Pattern>();
            List<Pattern> sequences = new ArrayList<Pattern>();
            sequences=Pattern.loadPatterns2("data\\variation\\"+turn+"\\R"+i+"\\DataDI.txt","DI");
            sequences.addAll(Pattern.loadPatterns2("data\\variation\\"+turn+"\\R"+i+"\\DataDD.txt","DD"));
            sequences.addAll(Pattern.loadPatterns2("data\\variation\\"+turn+"\\R"+i+"\\DataFI.txt","FI"));
            sequences.addAll(Pattern.loadPatterns2("data\\variation\\"+turn+"\\R"+i+"\\DataFD.txt","FD"));
            pattterns= Pattern.loadPatterns2("data\\variation\\"+turn+"\\R"+i+"\\NDpatternsFD2.txt","");
            System.out.println("Patterns size "+ pattterns.size());
            System.out.println("sequences size "+ sequences.size());
            double MeanSupport=0;
            double MeanSupportOverlap=0;
            double MeanConfidence=0;
            double MeanConfidenceOverlap=0;
            for (Pattern P : pattterns)
            {
                MeanSupport+=P.ComputeSupport(sequences, P);
                MeanSupportOverlap+=P.ComputeSupportOverlap(sequences, P);
                
                //System.out.println("P "+ P.getSignature()+ "  sup  " + s);
            }
            for (Pattern P : pattterns)
            {
                MeanConfidence+=P.Computeconfidence(sequences, P, Variation);
                MeanConfidenceOverlap+=P.ComputeconfidenceOverlap(sequences, P,Variation);
            }
            System.out.println("MeanSupport "+ MeanSupport/pattterns.size()+ "  MeanSupportOverlap  " + MeanSupportOverlap/pattterns.size()+ " MeanConfidence "+ MeanConfidence/pattterns.size()+ " MeanConfidenceOverlap "+ MeanConfidenceOverlap/pattterns.size());
            Pattern.savePatternsToTextFile(pattterns, "data\\variation\\"+turn+"\\R"+i+"\\NDPatternsFDeval.txt");
        }
        public static double DoubleFormat(double f1)
        {
            DecimalFormat decim = new DecimalFormat("#.##", DecimalFormatSymbols.getInstance(Locale.ENGLISH));
            return (Double.parseDouble(decim.format(f1)));
        }
        public static void main(String[] args) throws FileNotFoundException, IOException  
        {
           patternsCombinaison();
          //PatternsEvaluation("S",2,"FI");
        }
}
