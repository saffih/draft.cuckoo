package actors;

import akka.actor.UntypedActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import dataevent.DataEventCounter;

/**
 * Created by saffi on 22/09/16.
 */
public class EventCounter extends UntypedActor {
    LoggingAdapter log = Logging.getLogger(getContext().system(), this);
    DataEventCounter dataCounter = new DataEventCounter();

    public void onReceive(Object message) {
        if (message instanceof GotEvent) {
            GotEvent ge = (GotEvent) message;
            dataCounter.add(ge.dataEvent);
        } else {
            unhandled(message);
        }
    }
}
