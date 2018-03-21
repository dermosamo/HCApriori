
package seqmining.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import seqmining.DataClasse.Event;
import seqmining.DataClasse.Pattern;
import seqmining.DataClasse.Sequence;
import seqmining.DataClasse.SequenceData;
/**
 *
 * @author Soumia Dermouche
 */
public class Metrics {
         public static class MatchResult 
         {
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

      
        public static MatchResult CityblockEvent(Sequence example, Event event) 
        {

		List<Event> events = example.getEventsByType(event.getEventType(),true, null);

		if ((null == events) || (events.size() == 0)) {
			return new MatchResult(false, 0f);
		}

		List<Event> overlapped = new ArrayList<Event>();

		for (Event ev : events) {
			float maxStartTime = Math.max(ev.getStartTime(),
					event.getStartTime());
			float minEndTime = Math.min(ev.getEndTime(), event.getEndTime());

			if (!(minEndTime > maxStartTime)) {
				// no overlap
				continue;
			}

			overlapped.add(ev);
		}

		if (overlapped.size() == 0) {
			return new MatchResult(false, 0f);
		}

		// XOR style matching
		float xorRange = Math.abs(overlapped.get(0).getStartTime()
				- event.getStartTime());
		float diff = 0f;
		for (int i = 1; i < overlapped.size(); i++) {
			diff = overlapped.get(i).getStartTime()
					- overlapped.get(i - 1).getEndTime();
			xorRange += diff;

			
		}
		diff = Math.abs(overlapped.get(overlapped.size() - 1).getEndTime() - event.getEndTime());
		xorRange += diff;
                //System.out.println("event: "+event.toString()+ " example "+example.toString()+"prop "+xorRange);
                return new MatchResult(true, xorRange);
	}
    //Overlap between two events 
      public static MatchResult Overlape(Sequence example, Event event) 
      {
		List<Event> events = example.getEventsByType(event.getEventType(),
				true, null);
		if ((null == events) || (events.size() == 0)) 
                {
			return new MatchResult(false, 0f);
		}
		float overlap=0;
                float Maxoverlap=0;
                float maxduration=0;
		for (Event ev : events) {
			float maxStartTime = Math.max(ev.getStartTime(),
					event.getStartTime());
			float minEndTime = Math.min(ev.getEndTime(), event.getEndTime());

			if (!(minEndTime > maxStartTime)) 
                        {
				// no overlap
				continue;
			}
                        else
                        {
                            overlap=minEndTime-maxStartTime;
                            if(Maxoverlap<overlap)
                            {
                                Maxoverlap=overlap;
                                maxduration=ev.getDuration();
                            }
                        }
		}
                // normalize the score by event length
                //System.out.println("event: "+event.toString()+ " example "+example.toString()+"Maxover "+Maxoverlap/Math.max(event.getDuration(), maxduration));
                return new MatchResult(true, Maxoverlap/Math.max(event.getDuration(), maxduration));
                
	}
       
        //Calcul accuracy of patterns 
        public static double Accuracy(SequenceData dataset, SequenceData dataset2) throws IOException 
        {
                //Loadsimilarity();
                List<Pattern> ListPattern=new ArrayList<Pattern>();
                for (Sequence example : dataset2.getExamples()) 
                {     
                   List<Event> events= example.getEvents();
                   Pattern pattern=new Pattern();
                  // pattern.setId(String.valueOf(example.getId()));
                   pattern.getPattern(false).addAll(events);
                   ListPattern.add(pattern);
                }
                double cpt=0;
                for (Sequence example : dataset.getExamples()) 
                {      
                    boolean match=false;
                    for (Pattern pat : ListPattern)
                    {
                                            AprioriUtils.MatchResult res= AprioriUtils.match(example, pat);
                                            if(res.isMatch())
                                            {
                                                    match=true;
                                            }
                    }
                    if (match)
                    {
                        cpt++;
                    }
                }
                double acc= cpt/dataset.getExamples().size();
                System.out.println(" Accuracy: " + acc );
                return acc;
        }  
        
        /* public static double RecalAlpha(SequenceData dataset, SequenceData dataset2,int alpha) throws IOException 
        {
                if (alpha ==1)
                Loadsimilarity1();
                if (alpha ==2)
                Loadsimilarity2();
                 if (alpha ==3)
                Loadsimilarity3();
                List<Pattern> ListPattern=new ArrayList<Pattern>();
                for (Sequence example : dataset2.getExamples()) 
                {     
                   List<Event> events= example.getEvents();
                   Pattern pattern=new Pattern();
                   pattern.setId(String.valueOf(example.getId()));
                   pattern.getPattern(false).addAll(events);
                   ListPattern.add(pattern);
                }
                double cpt=0;
                for (Sequence example : dataset.getExamples()) 
                {      
                    boolean match=false;
                    for (Pattern pat : ListPattern)
                    {
                                            AprioriUtils.MatchResult res= AprioriUtils.matchF(example, pat,false,0,0,0);
                                            if(res.isMatch())
                                            {
                                                    match=true;
                                                    //System.out.println("Sequence "+ example.toString()+ " Pat "+ pat.toString());
                                            }
                    }
                    if (match)
                    {
                        cpt++;
                    }
                }
                double acc= cpt/dataset.getExamples().size();
                System.out.println(" Recall " + acc );
                return acc;
        } 
         
          public static double PrecisionAlpha(SequenceData dataset, SequenceData dataset2, int alpha) throws IOException 
        {
                if (alpha ==1)
                Loadsimilarity1();
                if (alpha ==2)
                Loadsimilarity2();
                 if (alpha ==3)
                Loadsimilarity3();
                List<Pattern> ListPattern=new ArrayList<Pattern>();
                for (Sequence example : dataset2.getExamples()) 
                {     
                   List<Event> events= example.getEvents();
                   Pattern pattern=new Pattern();
                   pattern.setId(String.valueOf(example.getId()));
                   pattern.getPattern(false).addAll(events);
                   ListPattern.add(pattern);
                }
                double cpt=0;
               
                    for (Pattern pat : ListPattern)
                    {
                         boolean match=false;
                         for (Sequence example : dataset.getExamples()) 
                         {      
                                            AprioriUtils.MatchResult res= AprioriUtils.match(example, pat,false,0,0,0);
                                            if(res.isMatch())
                                            {
                                                    match=true;
                                                     //System.out.println( " Pat "+ pat.toString()+"Sequence "+ example.toString());
                                
                                            }
                        }
                        if (match)
                        {
                            cpt++;
                        }
                    }
                double acc= cpt/dataset2.getExamples().size();
                System.out.println(" Precision " + acc );
                return acc;
        }
        
        public static double Pres(SequenceData dataset, SequenceData dataset2) throws IOException 
        {
                Loadsimilarity();
                List<Pattern> ListPattern=new ArrayList<Pattern>();
                for (Sequence example : dataset2.getExamples()) 
                {     
                   List<Event> events= example.getEvents();
                   Pattern pattern=new Pattern();
                   pattern.setId(String.valueOf(example.getId()));
                   pattern.getPattern(false).addAll(events);
                   ListPattern.add(pattern);
                }
                double cpt=0;
                    for (Pattern pat : ListPattern)
                    {
                        boolean match=false;
                        for (Sequence example : dataset.getExamples()) 
                        {  
                                            AprioriUtils.MatchResult res= AprioriUtils.match(example, pat,false,0,0,0);
                                            if(res.isMatch())
                                            {
                                                    match=true;
                                            }
                        }
                        if (match)
                        {
                            cpt++;
                        }
                    }
                double acc= cpt/dataset2.getExamples().size();
                System.out.println(" pres " + acc );
                return acc;
        }  
        
        
      
        public static void CalculPrecision(SequenceData dataset, SequenceData dataset2 ) throws IOException 
        {
            Loadsimilarity2();
            //LoadDuration();
            //load patterns from file
            List<Pattern> ListPattern=new ArrayList<Pattern>();
            for (Sequence example : dataset2.getExamples()) 
            {     
               List<Event> events= example.getEvents();
               Pattern pattern=new Pattern();
               pattern.setId(String.valueOf(example.getId()));
               pattern.getPattern(false).addAll(events);
               ListPattern.add(pattern);
            }
               
                int cpt=0;
                for (Pattern pat : ListPattern)  
                {      
                    boolean match=false;
                    for (Sequence example : dataset.getExamples())
                    {
                                            AprioriUtils.MatchResult res= AprioriUtils.match(example, pat,false,0,0,0);
                                            if(res.isMatch())
                                            {
                                                    match=true;
                                                    //System.out.println(" SequentialPattern "+ pat.getId()+" nb match :  "+ cpt);
                                            }
                    }
                   
                    if (match)
                    {
                       cpt=cpt+1;
                    }
                }
                System.out.println(" Presision " +cpt/dataset2.getExamples().size()+ " Cpt "+ cpt);
        }   
        
        public static void CalculRecall(SequenceData dataset, SequenceData dataset2 ) throws IOException 
        {
            Loadsimilarity();
            //LoadDuration();
            //load patterns from file
            List<Pattern> ListPattern=new ArrayList<Pattern>();
            for (Sequence example : dataset2.getExamples()) 
            {     
               List<Event> events= example.getEvents();
               Pattern pattern=new Pattern();
               pattern.setId(String.valueOf(example.getId()));
               pattern.getPattern(false).addAll(events);
               ListPattern.add(pattern);
            }
               
                double recall=0;
                for (Sequence example : dataset.getExamples())
                {
                        int cpt=0;
                        for (Pattern pat : ListPattern)  
                        {      
                    
                                            AprioriUtils.MatchResult res= AprioriUtils.match(example, pat,false,0,0,0);
                                            //System.out.println("S "+example.toString()+" p "+ pat.toString() +" match :  "+ res.isMatch()+"\n");
                                            if(res.isMatch())
                                            {
                                                cpt++;
                                            }
                        }
                    //System.out.println(" SequentialPattern "+ pat.getId()+" nb match :  "+ cpt);
                    recall+=cpt;   
                }
                System.out.println(" Recall " +recall/(dataset2.getExamples().size())/(dataset.getExamples().size()));
        }        
       */
    
    
}
