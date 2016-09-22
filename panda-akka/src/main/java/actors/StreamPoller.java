package actors;

import akka.actor.Cancellable;
import akka.actor.UntypedActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import dataevent.DataEvent;
import dataevent.DataEventHelper;
import helper.StreamHelper;
import scala.concurrent.duration.Duration;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

/**
 * Created by saffi on 22/09/16.
 */
public class StreamPoller extends UntypedActor {
    LoggingAdapter log = Logging.getLogger(getContext().system(), this);
    StreamHelper sth = new StreamHelper(System.in);

    private final Cancellable tick = getContext().system().scheduler().schedule(
            Duration.create(500, TimeUnit.MILLISECONDS),
            Duration.create(1, TimeUnit.SECONDS),
            getSelf(), "tick", getContext().dispatcher(), null);

    public StreamPoller() {

    }

    @Override
    public void postStop() {
        tick.cancel();
    }


    @Override
    public void onReceive(Object message) throws Exception {
        // for testing
        if (message.equals("tick")) {
            // do something useful here
            reportDataEvents();
        } // for testing
        else if (message instanceof String) {
            String st = (String) message;
            this.sth = new StreamHelper(new ByteArrayInputStream(st.getBytes(StandardCharsets.UTF_8)));
        } else {
            unhandled(message);
        }
    }

    private void reportDataEvents() throws IOException {
        for (String st; (st = sth.getString()) != null; ) {
            DataEvent de = DataEventHelper.fromJsonSilentFail(st);
            if (de != null) {
                getContext().parent().tell(new GotEvent(de), getSelf());
            }
        }
    }
}
