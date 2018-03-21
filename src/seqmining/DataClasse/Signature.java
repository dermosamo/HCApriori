
package seqmining.DataClasse;

import java.util.List;

/**
 *
 * @author soumia
 */
public class Signature 
{
    private List<Integer> Sig;
    private int FatherRightId;
    private int FatherLeft;
    public Signature(List<Integer> Sig)
    {
        this.Sig=Sig;
    }

    public List<Integer> getSig() 
    {
        return Sig;
    }

    public int getFatherRightId()
    {
        return FatherRightId;
    }

    public int getFatherLeft() 
    {
        return FatherLeft;
    }

    public void setFatherRightId(int FatherRightId) 
    {
        this.FatherRightId = FatherRightId;
    }

    public void setFatherLeft(int PatterenId)
    {
        this.FatherLeft = PatterenId;
    }
    
    public String  getstring()
    {
        String res="";
        for (int i : Sig)
        {
            res=res.concat(Integer.toString(i));
        }
        return res;
    }
    
    
}
