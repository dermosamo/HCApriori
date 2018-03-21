package seqmining.DataClasse;
/**
 *
 * @author Soumia Dermouche
 */
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import seqmining.Parameters;
import seqmining.util.AprioriUtils;
import seqmining.DataClasse.Match;


public class Pattern {
        private int FatherLeftId;
        private int FatherRightId;
        private int PatterenId;
        private int Sup;
        private double Support;
        private double confidence;
        private double SupportOverlap;
        private double confidenceOverlap;
	private List<Event> pattern;
        private String Type;
        private String Signature;
	public Pattern() 
        {
		pattern = new ArrayList<Event>();
	}

	public int getNumEvents() 
        {
		return pattern.size();
	}

	public void addEvent(Event e) 
        {
		pattern.add(e);
	}

	public List<Event> getPattern(boolean sort) 
        {
		if (!sort)
			return pattern;

		Collections.sort(pattern);
		return pattern;
	}
        public Pattern RemovelastEvent() 
        {
            Collections.sort(pattern);
            Pattern P= new Pattern();
            for(int i=0;i<pattern.size()-1;i++)
            {
                P.addEvent(pattern.get(i));
            }
            return P;
	}
        public static Match CityBlockPAtterns(Pattern p1, Pattern p2) 
        {
            Collections.sort(p1.pattern);
            Collections.sort(p1.pattern);
            assert (p1.pattern.size() == p2.pattern.size());
            double res=0;
            for(int i=0;i<p1.pattern.size();i++)
            {
                Event e1=p1.pattern.get(i);
                Event e2=p2.pattern.get(i);
                assert(e1.getEventType()== e2.getEventType());  
                res += Math.abs(e1.getEndTime() - e2.getEndTime()) + Math.abs(e1.getStartTime()- e2.getStartTime());
                //System.out.println(p.toString()+" "+centroid.toString()+ " dit: "+ res);
            }
            return new Match(true, res);
        }
        public List<Integer> getSignature() 
        {
            Collections.sort(pattern);
            List<Integer> ListEvents= new ArrayList<Integer>();
            for(int i=0;i<pattern.size();i++)
            {
                int k= pattern.get(i).getEventType();
                ListEvents.add(k);
            }
            return ListEvents;
	}

        public String getType()
        {
		return Type;
	}

	public void setType(String Type) 
        {
		this.Type = Type;
	}
        public double ComputeSupport(List<Pattern> sequences, Pattern Pat) 
        {
            double supp=0;
            int support=0;
            for (Pattern Sequence: sequences)
            {
                if(AprioriUtils.Signaturescomparaison(Pat.getSignature(),Sequence.getSignature()))
                {
                    supp++;
                    support++;
                    //System.out.println(" Pattern "+ Pat.toString()+" Sequence "+ Sequence.toString()+" sup "+supp);
                    //System.out.println(" Pat"+Pat.getSignature()+" Sequence "+Sequence.getSignature()+" sup "+supp);
                }
               
            }
            Pat.setSupport( AprioriUtils.DoubleFormat(supp/sequences.size()));
            Pat.setSup(support);
            return  AprioriUtils.DoubleFormat(supp/sequences.size());
	}
        public int getSup() 
        {
		return this.Sup ;
	}

	public void setSup(int Sup) 
        {
		this.Sup = Sup;
	}
        public double getSupport() 
        {
		return this.Support ;
	}

	public void setSupport(double Support) 
        {
		this.Support = Support;
	}
         public double getSupportOverlap() 
        {
		return this.SupportOverlap ;
	}

	public void setSupportOverlap(double SupportOverlap) 
        {
		this.SupportOverlap = SupportOverlap;
	}
         public double getconfidenceOverlap() 
        {
		return this.confidenceOverlap ;
	}
        public void setconfidenceOverlap(double confidenceOverlap) 
        {
		this.confidenceOverlap = confidenceOverlap;
	}
        public double getconfidence() 
        {
		return this.confidence ;
	}
        public void setconfidence(double confidence) 
        {
		this.confidence = confidence;
	}
        public double Computeconfidence(List<Pattern> Sequences, Pattern Pat, String Type) 
        {
            double conf=0;
            for (Pattern sequence : Sequences)
            {
                if(sequence.getType().equalsIgnoreCase(Type) && AprioriUtils.Signaturescomparaison(Pat.getSignature(),sequence.getSignature()))
                {
                    conf++;
                    //System.out.println(" sig " +Pat.getSignature()+" sig "+sequence.getSignature());
                    //System.out.println(" Pat"+ Pat.toString()+" sig " +Pat.getSignature()+" P "+P.toString()+" sig "+P.getSignature());
                }
               
            }
            if(Pat.getSupport()!=0)
            {
                Pat.setconfidence(AprioriUtils.DoubleFormat(conf/Pat.getSup()));
                return AprioriUtils.DoubleFormat(AprioriUtils.DoubleFormat(conf/Pat.getSup()));
            }
            else
            {
              Pat.setconfidence(0);  
              return 0;
            }
           
	}

	public double ComputeconfidenceOverlap(List<Pattern> Sequences, Pattern Pat, String Type) 
        {
            double conf=0;
            for (Pattern sequence : Sequences)
            {
                if(sequence.getType().equalsIgnoreCase(Type) && AprioriUtils.Signaturescomparaison(Pat.getSignature(),sequence.getSignature()))
                {
                    conf+=AprioriUtils.SignaturescomparaisonOverlap(Pat.getSignature(),Pat,sequence.getSignature(),sequence);
                }
            }
            if(Pat.getSupport()!=0)
            {
                Pat.setconfidenceOverlap(AprioriUtils.DoubleFormat(conf/Pat.getSup()));
                return AprioriUtils.DoubleFormat(AprioriUtils.DoubleFormat(conf/Pat.getSup()));
            }
            else
            {
              Pat.setconfidenceOverlap(0);  
              return 0;
            }
	}
        
        public double ComputeSupportOverlap(List<Pattern> sequences, Pattern Pat) 
        {
            double supp=0;
            for (Pattern Sequence: sequences)
            {
                supp+=AprioriUtils.SignaturescomparaisonOverlap(Pat.getSignature(),Pat,Sequence.getSignature(),Sequence);
                //System.out.println(" Patern  "+ Pat+ " sequence "+ Sequence+" supp overlap "+supp );
            }
           
            Pat.setSupportOverlap( AprioriUtils.DoubleFormat(supp/sequences.size()));
	    return AprioriUtils.DoubleFormat(supp/sequences.size());
	}
        public int getFatherLeftId() {
		return FatherLeftId;
	}

	public void setFatherLeftId(int FatherLeftId) 
        {
		this.FatherLeftId = FatherLeftId;
	}
        
         public int getFatherRightId() {
		return FatherRightId;
	}

	public void setFatherRightId(int FatherRightId) 
        {
		this.FatherRightId = FatherRightId;
	}
        
        public int getPatterenId() {
		return PatterenId;
	}

	public void setPatterenId(int PatterenId) {
		this.PatterenId = PatterenId;
	}

	public Event getLastEvent() {
		Collections.sort(pattern);
		return pattern.get(pattern.size() - 1);
	}
        
        public int  FinfModality(int EventModality) 
        {
                Collections.sort(pattern);
                int find=-1;
		for(int i=0;i<pattern.size();i++)
                {
                    if(pattern.get(i).getModality()== EventModality)
                    {
                         find=i;
                    }
                }
		return find;
	}
        
        public Event getLastEventModality(int EventModality) {
		Collections.sort(pattern);
                Event E =null;
                for(int i=0;i<pattern.size();i++)
                {
                    if(pattern.get(i).getModality()== EventModality)
                    {
                         E=pattern.get(i);
                    }
                }
                return E;	
	}
        
        public int  EventTypeNumber(int EventType) 
        {
               Collections.sort(pattern);
               
                int find=0;
		for(int i=0;i<pattern.size();i++)
                {
                    if(pattern.get(i).getEventType()== EventType)
                    {
                         find++;
                    }
                }
		return find;
	}

	public Set<Integer> getEventTypeSet() {
		Set<Integer> eventTypeSet = new HashSet<Integer>();
                Collections.sort(pattern);
 		for (Event e : pattern) {
			eventTypeSet.add(e.getEventType());
		}

		return eventTypeSet;
	}


	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		StringBuilder eventTypes = new StringBuilder();
		StringBuilder startTimes = new StringBuilder();
		StringBuilder durations = new StringBuilder();

		for (Event ev : pattern) {
			eventTypes.append(ev.getEventType() + " ");
			startTimes.append(ev.getStartTime() + " ");
			durations.append(ev.getDuration() + " ");
		}

		sb.append(eventTypes.toString() + "\n");
		sb.append(startTimes.toString() + "\n");
		sb.append(durations.toString() + "\n");

		return sb.toString();
	}
        public static List<Pattern> loadPatterns(String dsFilePath, String type) throws IOException {
		BufferedReader reader = null;
                List<Pattern> patterns = new ArrayList<Pattern>();
		try {
			reader = new BufferedReader(new InputStreamReader(new FileInputStream(dsFilePath)));

			long NbPatterns = Long.parseLong(reader.readLine().trim());

			for (long i = 1; i <= NbPatterns; i++) 
                        {
				String[] PatternsRate =reader.readLine().trim().split("\\s+");
				String[] eventTypes = reader.readLine().trim().split("\\s+");
				String[] startTimes = reader.readLine().trim().split("\\s+");
				String[] durations = reader.readLine().trim().split("\\s+");

				/* lengths should all be equal */
				assert (eventTypes.length == startTimes.length)
						&& (eventTypes.length == durations.length);

				Pattern P = new Pattern();
                                for (int j = 0; j < eventTypes.length; j++) {
					P.addEvent(new Event(Integer.parseInt(eventTypes[j]),
							Float.parseFloat(startTimes[j]), Float
									.parseFloat(durations[j])));
				}
                                P.setType(type);
                                P.setSupport(Float.parseFloat(PatternsRate[1]));
                                P.setconfidence(Float.parseFloat(PatternsRate[2]));
                                P.setSupportOverlap(Float.parseFloat(PatternsRate[3]));
                                P.setconfidenceOverlap(Float.parseFloat(PatternsRate[4]));
				patterns.add(P);
			}
		} finally {
			if (reader != null)
				reader.close();
		}
                return patterns;
	}
        
        public static List<Pattern> loadPatterns2(String dsFilePath, String type) throws IOException {
		BufferedReader reader = null;
                List<Pattern> patterns = new ArrayList<Pattern>();
		try {
			reader = new BufferedReader(new InputStreamReader(new FileInputStream(dsFilePath)));

			long NbPatterns = Long.parseLong(reader.readLine().trim());

			for (long i = 1; i <= NbPatterns; i++) 
                        {
				String[] PatternsRate =reader.readLine().trim().split("\\s+");
				String[] eventTypes = reader.readLine().trim().split("\\s+");
				String[] startTimes = reader.readLine().trim().split("\\s+");
				String[] durations = reader.readLine().trim().split("\\s+");

				/* lengths should all be equal */
				assert (eventTypes.length == startTimes.length)
						&& (eventTypes.length == durations.length);

				Pattern P = new Pattern();
                                for (int j = 0; j < eventTypes.length; j++) {
					P.addEvent(new Event(Integer.parseInt(eventTypes[j]),
							Float.parseFloat(startTimes[j]), Float
									.parseFloat(durations[j])));
				}
                                P.setType(type);
                                /*P.setSupport(Float.parseFloat(PatternsRate[1]));
                                P.setconfidence(Float.parseFloat(PatternsRate[2]));
                                P.setSupportOverlap(Float.parseFloat(PatternsRate[3]));
                                P.setconfidenceOverlap(Float.parseFloat(PatternsRate[4]));*/
				patterns.add(P);
			}
		} finally {
			if (reader != null)
				reader.close();
		}
                return patterns;
	}
public static void savePatternsToTextFile(List<Pattern> Patterns, String outputfile) throws IOException 
{
		BufferedWriter writer = null;
		try {

			int count = 1;
			StringBuilder content = new StringBuilder();
                        for (Pattern pattern : Patterns)
                        {
				content.append(count++).append(" ").append(pattern.getSupport()).append(" ").append(pattern.getSupportOverlap()).append(" ").append(pattern.getconfidence()).append(" ").append(pattern.getconfidenceOverlap()).append(" \n");
				StringBuilder eventTypes = new StringBuilder();
				StringBuilder startTimes = new StringBuilder();
				StringBuilder durations = new StringBuilder();

				for (Event ev : pattern.getPattern(true)) 
                                {
					eventTypes.append(ev.getEventType()).append(" ");
					startTimes.append(ev.getStartTime()).append(" ");
					durations.append(ev.getDuration()).append(" ");
				}

				eventTypes.append("\n");
				startTimes.append("\n");
				durations.append("\n");

				content.append(eventTypes.toString());
				content.append(startTimes.toString());
				content.append(durations.toString());
			}

			writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outputfile)));

			writer.write(count-1 + "\n");
			writer.write(content.toString());
			writer.flush();
		} finally {
			if (writer != null)
				writer.close();
		}
}

public static void savePatternsToTextFile2(List<Pattern> Patterns, String outputfile) throws IOException 
{
		BufferedWriter writer = null;
		try {

			int count = 1;
			StringBuilder content = new StringBuilder();
                        for (Pattern pattern : Patterns)
                        {
				content.append(count++).append("\n");
				StringBuilder eventTypes = new StringBuilder();
				StringBuilder startTimes = new StringBuilder();
				StringBuilder durations = new StringBuilder();

				for (Event ev : pattern.getPattern(true)) 
                                {
					eventTypes.append(ev.getEventType()).append(" ");
					startTimes.append(ev.getStartTime()).append(" ");
					durations.append(ev.getDuration()).append(" ");
				}

				eventTypes.append("\n");
				startTimes.append("\n");
				durations.append("\n");

				content.append(eventTypes.toString());
				content.append(startTimes.toString());
				content.append(durations.toString());
			}

			writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outputfile)));

			writer.write(count-1 + "\n");
			writer.write(content.toString());
			writer.flush();
		} finally {
			if (writer != null)
				writer.close();
		}
}

}
