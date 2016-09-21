import akka.actor.*;
import akka.stream.ActorMaterializer;
import akka.stream.Materializer;
import scala.concurrent.duration.Duration;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class HelloAkkaJava {


    public static class GotEvent implements Serializable {
        DataEvent dataEvent;
    }


    public static class Collector extends UntypedActor {

        public void onReceive(Object message) {
            if (message instanceof GotEvent)
            // publish that;
            {
                ;
            } else {
                unhandled(message);
            }
        }
    }

    public class StreamPoller extends UntypedActor {
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
                for (String st; (st = sth.getString()) != null; ) {
                    DataEvent de = DataEventHelper.fromJsonSilentFail(st);
                    if (de != null) {
                        getContext().parent().tell(de, getSelf());
                    }
                }
            } // for testing
            else if (message instanceof String) {
                String st = (String) message;
                this.sth = new StreamHelper(new ByteArrayInputStream(st.getBytes(StandardCharsets.UTF_8)));
            } else {
                unhandled(message);
            }
        }
    }

    public static class Greet implements Serializable {
    }

    public static class WhoToGreet implements Serializable {
        public final String who;

        public WhoToGreet(String who) {
            this.who = who;
        }
    }

    public static class Greeting implements Serializable {
        public final String message;

        public Greeting(String message) {
            this.message = message;
        }
    }

    public static class Greeter extends UntypedActor {
        String greeting = "";

        public void onReceive(Object message) {
            if (message instanceof WhoToGreet)
                greeting = "hello, " + ((WhoToGreet) message).who;

            else if (message instanceof Greet)
                // Send the current greeting back to the sender
                getSender().tell(new Greeting(greeting), getSelf());

            else unhandled(message);
        }
    }

    public static void main(String[] args) {
        try {
            // Create the 'helloakka' actor system
            final ActorSystem system = ActorSystem.create("pandaaka");
            final Materializer mat = ActorMaterializer.create(system);

            // Create the 'greeter' actor
            final ActorRef greeter = system.actorOf(Props.create(Greeter.class), "greeter");

            // Create the "actor-in-a-box"
            final Inbox inbox = Inbox.create(system);

            // Tell the 'greeter' to change its 'greeting' message
            greeter.tell(new WhoToGreet("akka"), ActorRef.noSender());

            // Ask the 'greeter for the latest 'greeting'
            // Reply should go to the "actor-in-a-box"
            inbox.send(greeter, new Greet());

            // Wait 5 seconds for the reply with the 'greeting' message
            final Greeting greeting1 = (Greeting) inbox.receive(Duration.create(5, TimeUnit.SECONDS));
            System.out.println("Greeting: " + greeting1.message);

            // Change the greeting and ask for it again
            greeter.tell(new WhoToGreet("typesafe"), ActorRef.noSender());
            inbox.send(greeter, new Greet());
            final Greeting greeting2 = (Greeting) inbox.receive(Duration.create(5, TimeUnit.SECONDS));
            System.out.println("Greeting: " + greeting2.message);

            // after zero seconds, send a Greet message every second to the greeter with a sender of the GreetPrinter
            final ActorRef greetPrinter = system.actorOf(Props.create(GreetPrinter.class));
            system.scheduler().schedule(Duration.Zero(), Duration.create(1, TimeUnit.SECONDS), greeter, new Greet(), system.dispatcher(), greetPrinter);
        } catch (TimeoutException ex) {
            System.out.println("Got a timeout waiting for reply from an actor");
            ex.printStackTrace();
        }
    }

    public static class GreetPrinter extends UntypedActor {
        public void onReceive(Object message) {
            if (message instanceof Greeting)
                System.out.println(((Greeting) message).message);
        }
    }
}
