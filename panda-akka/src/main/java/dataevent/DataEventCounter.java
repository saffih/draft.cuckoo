package dataevent;

import helper.Counter;

/**
 * Created by saffi on 22/09/16.
 */
public class DataEventCounter {

    // rate is slow enough - we do not need long.
    Counter wordCount=new Counter();
    Counter eventCount=new Counter();

    public void add(DataEvent de){
        if (de==null){
            return;
        }
        eventCount.add(de.event_type);
        wordCount.add(de.data);
    }
}
