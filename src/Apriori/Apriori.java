
package Apriori;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import seqmining.Parameters;
import seqmining.DataClasse.Sequence;
import seqmining.DataClasse.Event;
import seqmining.DataClasse.SequenceData;
import seqmining.DataClasse.Pattern;
import seqmining.util.AprioriUtils;

/**
 *
 * @author Soumia Dermouche
 */


public class Apriori {
    private static SequenceData dataset;
    private static int  KeyMap=0;
    private static Map<Integer, Pattern> MapAllFrequentPatterns = new HashMap<Integer, Pattern>();
    private static List<Pattern> CurrentFrequentPatterns= new ArrayList<Pattern>();
    private static List<Pattern> CandidatPatterns= new ArrayList<Pattern>();
    private static List<Pattern> InitialPatterns= new ArrayList<Pattern>();
    public static void AprioriMethode()throws IOException
    {
                AprioriUtils.LoadEpsilon(Parameters.Epsilon_PATH);
                dataset = new SequenceData(Parameters.DATA_PATH);
                List<Pattern> CentroidPAtterns=loadPatternSetFromCentroids();
                InitialPatterns=PurnningStep(CentroidPAtterns);
                CurrentFrequentPatterns=InitialPatterns;
                if (InitialPatterns.isEmpty()) 
                {
		 System.out.println("No patterns of length 1");
		} 
                else 
                {
                    AddPtternsMap(CurrentFrequentPatterns);
                    boolean terminated = false;
                    int maximumPatternLen = 10;
                    int currentPatternLength = 2;

                    while (!(terminated)) 
                    {

                            System.out.println(" Pattern generation of length: " + currentPatternLength);
                            CandidatPatterns = CandidateGeneration(CurrentFrequentPatterns);	
                            CurrentFrequentPatterns=PurnningStep(CandidatPatterns);
                            if (CurrentFrequentPatterns.isEmpty()) 
                            {
                                    System.out.println("No patterns of length " + currentPatternLength);
                                    terminated = true;
                            } 
                            else
                            {
                                 AddPtternsMap(CurrentFrequentPatterns);
                            }

                            if (currentPatternLength >= maximumPatternLen) {
                                    System.out.println("Maximum pattern length is is reached" + maximumPatternLen);
                                    terminated = true;
                            }

                            currentPatternLength++;
                    }
                }
		savePatternsToTextFile();

}
public static void AddPtternsMap(List<Pattern> candidates) throws IOException 
{
    for (Pattern candidatePattern : candidates) 
    {
        KeyMap++;
        candidatePattern.setPatterenId(KeyMap);
        MapAllFrequentPatterns.put(KeyMap,candidatePattern);  
        MapAllFrequentPatterns.remove(candidatePattern.getFatherLeftId());
        MapAllFrequentPatterns.remove(candidatePattern.getFatherRightId());
        //System.out.println("KeyMap "+KeyMap +" candidatePattern.getFatherRightId() "+candidatePattern.getFatherRightId()+" candidatePattern.getFatherRightId() "+ candidatePattern.getFatherRightId());
    }
}
public static void savePatternsToTextFile() throws IOException 
{
		BufferedWriter writer = null;
		try {
			int count = 1;
			StringBuilder content = new StringBuilder();
                        Iterator<Integer> keySetIterator = MapAllFrequentPatterns.keySet().iterator();
			while(keySetIterator.hasNext())    
                        {
				Integer key = keySetIterator.next();
                                Pattern pattern = MapAllFrequentPatterns.get(key);
				content.append(count++).append(" \n");
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
                      writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(Parameters.Results_PATH)));

			writer.write(count-1 + "\n");
			writer.write(content.toString());
			writer.flush();
		} 
                finally 
                {
			if (writer != null)
				writer.close();
		}
}
  public static List<Pattern> CandidateGeneration (List<Pattern> input)
  {
        List<Pattern> candidates  = new ArrayList<Pattern> ();
        for (Pattern P : input) 
        {
                  	List<Event> events = P.getPattern(true);
			Event lastEvent = events.get(events.size() - 1);
			for (Pattern InitialPattern : InitialPatterns) 
                        {
				Event singleEvent = InitialPattern.getPattern(false).get(0);
				int Find=P.FinfModality(singleEvent.getModality());
                                if(Find == -1)
                                {   
                                        if ((singleEvent.getStartTime() > lastEvent.getStartTime()&& (lastEvent.getEventType() != singleEvent.getEventType()))) 
                                        {
                                                Pattern pattern = new Pattern();
                                                pattern.getPattern(false).addAll(events);
                                                pattern.getPattern(false).add(singleEvent);
                                                pattern.setFatherLeftId(P.getPatterenId());
                                                pattern.setFatherRightId(InitialPattern.getPatterenId());
                                                candidates.add(pattern);
                                                }
                                }
                                else
                                {  
                                            Event lastEvent2= P.getPattern(false).get(Find);
                                            if ((singleEvent.getStartTime() > lastEvent.getStartTime()&& singleEvent.getStartTime() > lastEvent2.getStartTime()+lastEvent2.getDuration()&&(lastEvent.getEventType() != singleEvent.getEventType()))) 
                                            {
                                                    Pattern pattern = new Pattern();
                                                    pattern.getPattern(false).addAll(events);
                                                    pattern.getPattern(false).add(singleEvent);
                                                    pattern.setFatherLeftId(P.getPatterenId());
                                                    pattern.setFatherRightId(InitialPattern.getPatterenId());
                                                    candidates.add(pattern);
                                            }
                                }
                        }
        }
        return candidates;
  }
  public static List<Pattern> PurnningStep (List<Pattern> candidates)
  {
                            List<Pattern> frequentPatterns = new ArrayList<Pattern>();
                            for (Pattern candidatePattern : candidates) 
                            {
                                int cpt=0;
				for (Sequence example : dataset.getExamples())
                                {
					AprioriUtils.MatchResult res = AprioriUtils.match(example,candidatePattern);
					if (res.isMatch()) 
                                        {
                                               cpt++;
					}
				}
                               
                                if ((cpt / (float) Parameters.NumberSequences) > Parameters.fmin)
                                {
					frequentPatterns.add(candidatePattern);
                                }
	                    }
                            return frequentPatterns;
  }
  public static List<Pattern> loadPatternSetFromCentroids() throws IOException 
  {
            	List<Pattern> singleEventPatterns = new ArrayList<Pattern>();
                BufferedReader reader = null;
		try {
			reader = new BufferedReader(new InputStreamReader(
					new FileInputStream(Parameters.CENTROID_PATH)));
                            
                       long numEventTypes = Long.parseLong(reader.readLine().trim());

			for (long i = 0; i < numEventTypes; i++) {

				int eventType = Integer.parseInt(reader.readLine().trim());

				String[] startTimes = reader.readLine().trim().split("\\s+");
				String[] durations = reader.readLine().trim().split("\\s+");

				/* lengths should be equal */
				assert startTimes.length == durations.length;
				for (int j = 0; j < startTimes.length; j++) 
                                {
                                    Event E = new Event(eventType, Float.parseFloat(startTimes[j]), Float.parseFloat(durations[j]));
                                    Pattern p = new Pattern();
                                    p.addEvent(E);
                                    singleEventPatterns.add(p);
				}
			}
		} finally {
			if (reader != null)
				reader.close();
		}
		return singleEventPatterns;
	}
    
}
