package cj.mf.actors;

import akka.actor.UntypedActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import cj.mf.beans.Task;
import cj.mf.services.TaskDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * Created by cjm on 6/13/15.
 */
@Component
@Scope("prototype")
public class TaskActor extends UntypedActor {

    private final LoggingAdapter log = Logging.getLogger(getContext().system(), "TaskProcessor");

    @Autowired
    private TaskDAO taskDAO;

    @Override
    public void onReceive(Object message) throws Exception {

        long result = taskDAO.createTask((Task) message);
        log.debug("Created task {}", result);
    }
}
