package cj.mf;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.PoisonPill;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import cj.mf.beans.Task;
import cj.mf.extension.SpringExtension;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.Random;

/**
 * Hello world!
 *
 */
@Configuration
@EnableAutoConfiguration
@ComponentScan("cj.mf.configuration")
public class AkkaApplication {

    public static void main( String[] args ) throws Exception {

        ApplicationContext context = SpringApplication.run(AkkaApplication.class, args);

        ActorSystem system = context.getBean(ActorSystem.class);

        final LoggingAdapter log = Logging.getLogger(system, "Application");

        log.info("Starting up");

        SpringExtension extension = context.getBean(SpringExtension.class);

        // Use the SpringExtension to create props for a named actor bean
        ActorRef supervisor = system.actorOf(
                extension.props("supervisor").withMailbox("akka.priority-mailbox")
        );

        for (int i = 0; i < 5000000; i++) {
            Task task = new Task("payload " + i, new Random().nextInt(99));
            supervisor.tell(task, null);
        }

        // Poison pill will be queued with a priority of 100 as the last message
        supervisor.tell(PoisonPill.getInstance(), null);

        while (!supervisor.isTerminated()) {
            Thread.sleep(100);
        }

        log.info("Created {} tasks", context.getBean(JdbcTemplate.class)
                .queryForObject("SELECT COUNT(*) FROM tasks", Integer.class));

        log.info("Shutting down");

        system.shutdown();
        system.awaitTermination();
    }
}
