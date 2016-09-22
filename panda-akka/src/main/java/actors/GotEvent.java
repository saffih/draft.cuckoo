package actors;

import dataevent.DataEvent;

import java.io.Serializable;

/**
 * Created by saffi on 22/09/16.
 */
public class GotEvent implements Serializable {
    DataEvent dataEvent;

    public GotEvent(DataEvent dataEvent) {
        this.dataEvent = dataEvent;
    }
}
