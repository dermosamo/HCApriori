/**
 *
 * @author Soumia Dermouche
 */
package seqmining.DataClasse;


public class Event implements Comparable<Event> {
	private int eventType;
        private int Modality;
	private float startTime;
	private float duration;
	private float endTime;
	private float centroid;

	public Event(int eventType, float startTime, float duration) {
		super();
		this.eventType = eventType;
                this.Modality=Modality2(eventType);
                this.startTime = startTime;
		this.duration = duration;
		this.endTime = this.startTime + this.duration;
		this.centroid = this.startTime + this.duration / 2.0f;
	}
        
        public int Modality(int eventType) 
        {
            int m=-1;
            if(eventType==1||eventType==2||eventType==3||eventType==4||eventType==5||eventType==6||eventType==7||eventType==8)
                m=1;
            else
            { 
                if(eventType==9||eventType==10||eventType==11||eventType==12||eventType==13||eventType==14||eventType==15)
                    m=2;
                else
                { 
                    if(eventType==16||eventType==17)
                        m=3;
                    else
                    { 
                        if(eventType==18)
                            m=4;
                        else
                        { 
                            if(eventType==19||eventType==20||eventType==21)
                                m=5;
                            else
                            { 
                                if(eventType==22||eventType==23||eventType==24)
                                    m=6;
                                else
                                { 
                                    if(eventType==25||eventType==26||eventType==27||eventType==29)
                                        m=7;
                                }
                            }
                        }
                    }
                }
            }
            return m;
	}
        public int Modality2(int eventType) 
        {
            int m=-1;
            if(eventType==1||eventType==2||eventType==3)
                m=1;
            else
            { 
                if(eventType==4||eventType==5||eventType==6)
                    m=2;
                else
                { 
                    if(eventType==7)
                        m=3;
                    else
                    { 
                        if(eventType==8||eventType==9||eventType==10||eventType==12||eventType==13)
                            m=4;
                        else
                        { 
                            if(eventType==14||eventType==15)
                                m=5;
                        }
                    }
                }
            }
            return m;
	}
        public int getModality() 
        {
		return Modality;
	}
	public int getEventType() 
        {
		return eventType;
	}

	public void setStartTime(float st) 
        {
		 this.startTime=st;
	}
        public float getStartTime() 
        {
		return startTime;
	}

	public float getDuration() 
        {
		return duration;
	}

	public float getEndTime() 
        {
            endTime=startTime+duration;
		return endTime;
	}

	public float getCentroid() {
		return centroid;
	}

	@Override
	public int compareTo(Event e) {
		// natural order by starting time then by ending time then by
		// lexicographical order of event type

		int res = Float.valueOf(startTime).compareTo(e.getStartTime());

		if (res != 0)
			return res;

		res = Float.valueOf(endTime).compareTo(e.getEndTime());

		if (res != 0)
			return res;

		return Integer.valueOf(eventType).compareTo(e.getEventType());
	}
        //Calculates the City block distance between two points.
    public static float distanceCityBlock(Event E1, Event E2) 
    {
        return (Math.abs(E1.getStartTime() - E2.getStartTime())+Math.abs(E1.getEndTime() - E2.getEndTime()));
    }

    @Override
    public String toString() {
        return "Event{" + "eventType=" + eventType + ", startTime=" + startTime + ", duration=" + duration + '}';
    }
        

}
