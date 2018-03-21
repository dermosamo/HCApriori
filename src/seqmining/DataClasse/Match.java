
package seqmining.DataClasse;

/**
 *
 * @author soumia
 */
public class Match {
		private boolean match;
		private double prob;

		public Match(boolean match, double prob) 
                {
			super();
			this.match = match;
			this.prob = prob;
		}

		public boolean getMatch() 
                {
			return match;
		}

		public double getProb() 
                {
			return prob;
		}
    
}
