package dataevent;

import java.io.Serializable;

/**
 * Created by saffi on 21/09/16.
 */
public class DataEvent implements Serializable {

    public String getEvent_type() {
        return event_type;
    }

    public String getData() {
        return data;
    }

    public long getTimestamp() {
        return timestamp;
    }

    String event_type;
    String data;
    long timestamp;

    public DataEvent(String event_type, String data, long timestamp) {
        this.event_type = event_type;
        this.data = data;
        this.timestamp = timestamp;
    }

}
