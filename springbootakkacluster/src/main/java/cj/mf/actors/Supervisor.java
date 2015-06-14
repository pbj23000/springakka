package cj.mf.actors;

import akka.actor.ActorRef;
import akka.actor.Terminated;
import akka.actor.UntypedActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.routing.ActorRefRoutee;
import akka.routing.Routee;
import akka.routing.Router;
import akka.routing.SmallestMailboxRoutingLogic;
import cj.mf.beans.Task;
import cj.mf.extension.SpringExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by cjm on 6/13/15.
 */
@Component
@Scope("prototype")
public class Supervisor extends UntypedActor {

    private final LoggingAdapter log = Logging
            .getLogger(getContext().system(), "Supervisor");

    @Autowired
    private SpringExtension springExtension;

    private Router router;

    @Override
    public void preStart() throws Exception {
        log.info("Starting up");

        List<Routee> routees = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            ActorRef actor = getContext().actorOf(springExtension.props("taskActor"));
            getContext().watch(actor);
            routees.add(new ActorRefRoutee(actor));
        }
        router = new Router(new SmallestMailboxRoutingLogic(), routees);
        super.preStart();
    }

    @Override
    public void onReceive(Object message) throws Exception {

        if (message instanceof Task) {
            router.route(message, getSender());
        } else if (message instanceof Terminated) {
            // Readd task actors if one failed
            router = router.removeRoutee(((Terminated) message).actor());
            ActorRef actor = getContext().actorOf(springExtension.props("taskActor"));
            getContext().watch(actor);
            router = router.addRoutee(new ActorRefRoutee(actor));
        } else {
            log.error("Unable to handle this message {}", message);
        }
    }

    @Override
    public void postStop() throws Exception {
        log.info("Shutting down");
        super.postStop();
    }
}
