package seqmining;
/**
 *
 * @author Soumia Dermouche
 */
public class Parameters 
{
    // number of sequences in the the data set
    public static int NumberSequences = 193;
    // number of event type in the the data set
    public static int NumberEventType = 40;
    // path of intput data
    public static String DATA_PATH = "data\\Data.txt";
    // path of initial patterns (cenrtroid)
    public static String CENTROID_PATH = "data\\seed_centroids";
    // path of outputs (results)
    public static String Results_PATH = "data\\Results.txt";
    // path of outputs (results)
    public static String Epsilon_PATH = "data\\epsilons.txt";
    // minimum support
    public static double fmin = 0.02;
    // Thrershold used to compute epsilon
    public static double epsilon = 0.9;
    // The minimum number of events in the cluster to be concedered
    public static int minEventNumber = 1;
}
