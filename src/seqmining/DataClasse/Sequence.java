/**
 *
 * @author Soumia Dermouche
 */
package seqmining.DataClasse;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class Sequence {
	private long id;
	private List<Event> events;
	private Map<Integer, List<Event>> eventsByType;

	public Sequence(long id) 
        {
		this.id = id;
		events = new ArrayList<Event>();
		eventsByType = new HashMap<Integer, List<Event>>();
	}
        public List<Event> getSequence(boolean sort) 
        {
		if (!sort)
			return events;

		
		return events;
	}
        public Sequence  Concatenate( Sequence S2)
        {
            for(Event e : S2.events)
            {
                S2.events.add(e);
            }
            return S2;
        }
	public void addEvent(Event e) 
        {
		events.add(e);
		List<Event> lst = eventsByType.get(e.getEventType());
		if (lst == null) 
                {
			lst = new ArrayList<Event>();
			eventsByType.put(e.getEventType(), lst);
		}
		lst.add(e);
	}
        
        public int EventByTypeSize(int EventType) 
        {
		List<Event> lst = eventsByType.get(EventType);
                if (lst==null)
                    return 0;
                return lst.size();
	}

	
	public int getNumEvents() {
		return events.size();
	}

	public String getSignature() 
        {
            Collections.sort(events);
            String sig="";
            for(int i=0;i<events.size();i++)
            {
                int k= events.get(i).getEventType();
                if(i==0)
                    sig="-"+sig+String.valueOf(k)+"-";
                else
                    sig=sig+String.valueOf(k)+"-";
            }
            return sig;
	}
        
        public List<Integer>  getSignature2() 
        {
            Collections.sort(events);
            List<Integer> ListEvents= new ArrayList<Integer>();
            for(int i=0;i<events.size();i++)
            {
                int k= events.get(i).getEventType();
                ListEvents.add(k);
            }
            return ListEvents;
	}
	public List<Event> getEventsByType(int eventType, boolean sort,
			Comparator<Event> comparator) {
		List<Event> events = eventsByType.get(eventType);
		if (!sort) {
			return events;
		}

		if ((events != null) && (events.size() > 1)) {

			if (comparator != null) {
				// sort by user specified comparator
				Collections.sort(events, comparator);
			} else {
				// sort by natural order
				Collections.sort(events);
			}
		}

		return events;
	}

	public long getId() {
		return id;
	}

	public List<Event> getEvents() {
		return events;
	}
        public  void setEvents(List<Event> l) {
		 events=l;
	}

	public Map<Integer, List<Event>> getEventsByType() {
		return eventsByType;
	}
        
        public static int  EventTypeNumber(int EventType, Sequence E) 
        {
                int find=0;
		for(int i=0;i<E.events.size();i++)
                {
                    if(E.events.get(i).getEventType()== EventType)
                    {
                         find++;
                    }
                }
		return find;
	}
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		StringBuilder eventTypes = new StringBuilder();
		StringBuilder startTimes = new StringBuilder();
		StringBuilder durations = new StringBuilder();

		for (Event ev : events) {
			eventTypes.append(ev.getEventType() + " ");
			startTimes.append(ev.getStartTime() + " ");
			durations.append(ev.getDuration() + " ");
		}

		sb.append(eventTypes.toString() + "\n");
		sb.append(startTimes.toString() + "\n");
		sb.append(durations.toString() + "\n");

		return sb.toString();
	}

}

