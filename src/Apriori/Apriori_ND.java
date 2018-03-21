
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
import seqmining.DataClasse.Signature;
import seqmining.DataClasse.SequenceData;
import seqmining.DataClasse.Pattern;
import seqmining.HierarchicalClustering.HierarchicalClusteringND;
import static seqmining.HierarchicalClustering.HierarchicalClusteringND.Loadsimilarity;
import seqmining.util.AprioriUtils;

/**
 *
 * @author Soumia Dermouche
 */


public class Apriori_ND {
    private  SequenceData dataset;
    private  int  KeyMap=0;
    private  Map<Integer, Pattern> MapAllFrequentPatterns = new HashMap<Integer, Pattern>();
    private  List<Pattern> CurrentFrequentPatterns= new ArrayList<Pattern>();
    private  List<Pattern> AllCurrentPatterns= new ArrayList<Pattern>();
    private  List<Pattern> FinalPatterns= new ArrayList<Pattern>();
    private  List<Pattern> CandidatPatterns= new ArrayList<Pattern>();
    private  List<Signature> CurentSignature= new ArrayList<Signature>();
    private  List<Signature> ExploredSignature= new ArrayList<Signature>();
    private  List<Integer> InitialSignature= new ArrayList<Integer>();
    public  void AprioriMethode()throws IOException
    {
                HierarchicalClusteringND.Epsilon();
                AprioriUtils.LoadEpsilon(Parameters.Epsilon_PATH);
                dataset = new SequenceData(Parameters.DATA_PATH);
                System.out.println("dataset size "+ dataset.getExamples().size());
                for (Sequence S : dataset.getExamples())
                    System.out.println("Sequence "+ S.toString() );
                //Add initial signature of lenght 1
                InitialSignatureGeneration();
                for (Signature S : CurentSignature)
                { 
                    List<Pattern> candidates= Projection(S.getSig());
                    CandidatPatterns=HierarchicalClusteringND.HCND(candidates);
                    CurrentFrequentPatterns=PurnningStep(CandidatPatterns);
                    AllCurrentPatterns.addAll(CurrentFrequentPatterns);
                    ExploredSignature.add(S);
                }
               
                if (AllCurrentPatterns.isEmpty()) 
                {
                    System.out.println("No patterns of length 1");
		} 
                else 
                {
                    AddPtternsMap(AllCurrentPatterns);
                    //System.out.println(" AllCurrentPatterns "+ AllCurrentPatterns.toString());
                    InitialFrequentSignatureGeneration(AllCurrentPatterns);
                    boolean terminated = false;
                    int maximumPatternLen = 10;
                    int currentPatternLength = 2;
                    while (!(terminated)) 
                    {

                            System.out.println(" Pattern generation of length: " + currentPatternLength);
                            CurentSignature=SignaturesFromPatterns(AllCurrentPatterns);
                            AllCurrentPatterns= new ArrayList<Pattern>();
                            for (Signature S : CurentSignature)
                            { 
                                if(!ExploredSignature.contains(S))
                                {
                                    ExploredSignature.add(S);
                                    List<Pattern> candidates= Projection(S.getSig());
                                    CandidatPatterns=HierarchicalClusteringND.HCND(candidates);
                                    CurrentFrequentPatterns=PurnningStep(CandidatPatterns);
                                    if (!CurrentFrequentPatterns.isEmpty()) 
                                    {
                                        MapAllFrequentPatterns.remove(S.getFatherLeft());
                                    }
                                    AllCurrentPatterns.addAll(CurrentFrequentPatterns);
                                }
                            }
                            if (AllCurrentPatterns.isEmpty()) 
                            {
                                    System.out.println("No patterns of length " + currentPatternLength);
                                    terminated = true;
                            } 
                            else
                            {  
                                  AddPtternsMap(AllCurrentPatterns);
                            }

                            if (currentPatternLength >= maximumPatternLen) 
                            {
                                    System.out.println("Maximum pattern length is is reached" + maximumPatternLen);
                                    terminated = true;
                            }

                            currentPatternLength++;
                    }
                }
		savePatternsToTextFile();
}
public void AddPtternsMap(List<Pattern> candidates) throws IOException 
{
    for (Pattern candidatePattern : candidates) 
    {
        KeyMap++;
        candidatePattern.setPatterenId(KeyMap);
        MapAllFrequentPatterns.put(KeyMap,candidatePattern);  
    }
}
   public void InitialSignatureGeneration()
   {
      for (int i=1;i<=Parameters.NumberEventType;i++)
      {
        List<Integer> Sig=new ArrayList();
        Sig.add(i);
        CurentSignature.add (new Signature(Sig));  
        InitialSignature.add(i);
      }
   }
   public void InitialFrequentSignatureGeneration(List<Pattern> input)
   {
       InitialSignature=  new ArrayList<Integer>();
      for (Pattern P : input) 
      { 
            List<Integer> EventSeq= P.getSignature(); 
            for (int i : EventSeq) 
            {
                if(!InitialSignature.contains(i))
                {InitialSignature.add(i);}
            }
      }
   }
  public List<Signature> SignaturesFromPatterns (List<Pattern> input)
  {
        CurentSignature= new ArrayList<Signature>();
        List<Signature> candidates  = new ArrayList<Signature> ();
        for (Pattern P : input) 
        {
			for (Integer InitialPattern : InitialSignature) 
                        {  
                                List<Integer> sig= P.getSignature();
                                sig.add(InitialPattern);
			   	Signature S = new Signature(sig);
                                if(!ExistList(candidates,S))
                                {
                                    S.setFatherLeft(P.getPatterenId());
                                    candidates.add(S);
                                }
                        }
        }
        return candidates;
  }
  public static boolean ExistList(List<Signature> candidates,Signature sig)
  {
        boolean exist=false;
        for (Signature S : candidates) 
        {
            if(S.getstring().equals(sig.getstring()))
            {
                return true;
            }
        }
      return exist;
  }
public void savePatternsToTextFile() throws IOException 
{
                 System.out.println("total patterns :"+ MapAllFrequentPatterns.size());
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
public List<Pattern> Projection (List<Integer> signature)
{
                                List<Pattern> candidates  = new ArrayList<Pattern> ();
                                //System.out.println("dataset ssssssssssssssssssssssssssssss "+ dataset.getExamples().size());
                                for (Sequence example : dataset.getExamples())
                                {
					Pattern P = AprioriUtils.PatternFromSignature(example,signature);
                                        System.out.println(" Sequengggggggggggggggggggg "+ example.toString() );
					if (P!=null) 
                                        {
                                            System.out.println(" Sequencessssssssssssssssss "+ example.toString() );
                                            candidates.add(P);
                                            System.out.println(" pattern "+ P.toString()+ " From la Sequence "+ example.toString() );
                                        }
                                }
                                return candidates;
}
 /*public void main(String[] args) throws FileNotFoundException, IOException  
 { 
     dataset = new SequenceData(Constants.DATA_PATH);
     List<Integer> signature= new ArrayList<Integer>();
     signature.add(1);
     List<Pattern> candidates= Projection(signature);
     CandidatPatterns=HierarchicalClusteringND.HCND(candidates);  
     PurnningStep(CandidatPatterns);
 }*/
 public List<Pattern> PurnningStep (List<Pattern> candidates) throws FileNotFoundException, IOException
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
                                        //candidatePattern.setSupport(cpt);
                                        //candidatePattern.setconfidence((cpt / (float) Constants.NumberSequences));
				}
	                    }
                            return frequentPatterns;
  }
}
