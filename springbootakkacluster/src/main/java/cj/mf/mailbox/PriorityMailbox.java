package cj.mf.mailbox;

import akka.actor.ActorSystem;
import akka.dispatch.PriorityGenerator;
import akka.dispatch.UnboundedPriorityMailbox;
import cj.mf.beans.Task;
import com.typesafe.config.Config;

/**
 * Created by cjm on 6/13/15.
 */
public class PriorityMailbox extends UnboundedPriorityMailbox {

    public PriorityMailbox(ActorSystem.Settings settings, Config config) {

        super(new PriorityGenerator() {
            @Override
            public int gen(Object message) {
                if (message instanceof Task) {
                    return ((Task) message).getPriority();
                } else {
                    // default
                    return 100;
                }
            }
        });
    }
}

