/**
 *
 * @author Soumia Dermouche
 */
package seqmining.DataClasse;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import seqmining.util.AprioriUtils;

/**
 * class which represents a collection of examples
 * 
 * @author Guangchen
 * 
 */
public class SequenceData {
	private List<Sequence> sequences;

	public SequenceData(String dsFilePath) throws IOException {
		sequences = new ArrayList<Sequence>();
		loadDataset(dsFilePath);
	}
	private void loadDataset(String dsFilePath) throws IOException {

		BufferedReader reader = null;

		try {
			reader = new BufferedReader(new InputStreamReader(new FileInputStream(dsFilePath)));

			long numExamples = Long.parseLong(reader.readLine().trim());

			for (long i = 1; i <= numExamples; i++) 
                        {
                                String[] tokens = reader.readLine().trim().split("\\s+");

				assert tokens.length == 2;

				// need deal with scientific notation
				// long exampleId = Long.parseLong(tokens[0]);
				long exampleId = Double.valueOf(tokens[0]).longValue();
				String[] eventTypes = reader.readLine().trim().split("\\s+");
				String[] startTimes = reader.readLine().trim().split("\\s+");
				String[] durations = reader.readLine().trim().split("\\s+");

				/* lengths should all be equal */
				assert (eventTypes.length == startTimes.length)
						&& (eventTypes.length == durations.length);

				Sequence ex = new Sequence(exampleId);
                                for (int j = 0; j < eventTypes.length; j++) {
					ex.addEvent(new Event(Integer.parseInt(eventTypes[j]),
							Float.parseFloat(startTimes[j]), Float
									.parseFloat(durations[j])));
				}
                                //System.out.println("Sequence "+ ex.toString());
                                Collections.sort(ex.getEvents());
                                ex.setEvents(ex.getEvents());
				sequences.add(ex);
			}
		} finally {
			if (reader != null)
				reader.close();
		}
	}
        
        public static void SaveDataset(String dsFilePath,  List<Sequence> sequences) throws IOException 
        {
                        StringBuilder content = new StringBuilder();
                        int cpt=1;
			for (Sequence seq : sequences) 
                        {
                                List<Event> events= seq.getEvents();
                                String type="";                           
                                String begintime="";
                                String duration="";
                                for (Event s: events)
                                {
                                                   type=type.concat(s.getEventType()+" ");
                                                   begintime=begintime.concat(Double.toString(AprioriUtils.DoubleFormat(s.getStartTime()))+" ");
                                                   duration=duration.concat(Double.toString(AprioriUtils.DoubleFormat(s.getDuration()))+" ");
                                }
                                            content.append(cpt+" 0\n");
                                            cpt=cpt+1;
                                            content.append(type.substring(0,type.length()-1)+"\n");
                                            content.append(begintime.substring(0,begintime.length()-1)+"\n");
                                            content.append(duration.substring(0,duration.length()-1)+"\n");
                        }
                        File f = new File(dsFilePath);   
                        BufferedWriter writer = new BufferedWriter(new FileWriter(f)); //writers
                        writer.write(cpt-1 + "\n");
                        writer.write(content.toString());
                        writer.close();
        }
        public void addsequence(Sequence S)
        {
            sequences.add(S);
        }
        public List<Sequence> getExamples() {
		return sequences;
	}
        public static List<Float> GetStatTimebyType(SequenceData D,int EventType)
        {
            List<Float> StartTimeList= new ArrayList<Float>();
                        for (Sequence S : D.sequences) 
                        {
                            List<Event> events= S.getEvents();
                            for (Event e : events)
                            {
                                 if(e.getEventType()==EventType) 
                                 {
                                     StartTimeList.add(e.getStartTime());
                                 }
                            }
                        }
                        Collections.sort(StartTimeList);
            return StartTimeList;
        }
        public static List<Float> GetDurationbyType(SequenceData D,int EventType)
        {
                        List<Float> DurationList= new ArrayList<Float>();
                        for (Sequence S : D.sequences) 
                        {
                            List<Event> events= S.getEvents();
                            for (Event e : events)
                            {
                                 if(e.getEventType()==EventType) 
                                 {
                                     DurationList.add(e.getDuration());
                                 }
                            }
                        }
                        Collections.sort(DurationList);
                        return DurationList;
        }
}
