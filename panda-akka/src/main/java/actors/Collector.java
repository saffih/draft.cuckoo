package actors;

import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;

/**
 * Created by saffi on 22/09/16.
 */
public class Collector extends UntypedActor {
    LoggingAdapter log = Logging.getLogger(getContext().system(), this);


    ActorRef poller = getContext().watch(getContext().actorOf(
            Props.create(StreamPoller.class), "eventCreator"));

    ActorRef counter = getContext().watch(getContext().actorOf(
            Props.create(EventCounter.class), "eventCounter"));


    public void onReceive(Object message) {
        if (message instanceof GotEvent)
        // publish that;
        {
            counter.tell(message, getSelf());
        } else {
            unhandled(message);
        }
    }
}
