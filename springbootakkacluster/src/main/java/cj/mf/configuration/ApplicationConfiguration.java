package cj.mf.configuration;

import akka.actor.ActorSystem;
import cj.mf.extension.SpringExtension;
import com.mchange.v2.c3p0.ComboPooledDataSource;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.Properties;

/**
 * Created by cjm on 6/13/15.
 */
@Configuration
@Lazy
@ComponentScan(basePackages = {"cj.mf.services",
        "cj.mf.actors", "cj.mf.extension"})
public class ApplicationConfiguration {

        @Autowired
        private ApplicationContext applicationContext;

        @Autowired
        private SpringExtension springExtension;

        @Bean
        public ActorSystem actorSystem() {
                ActorSystem system = ActorSystem
                        .create("AkkaTaskProcessing", akkaConfiguration());
                springExtension.initialize(applicationContext);
                return system;
        }

        @Bean
        public Config akkaConfiguration() {
                return ConfigFactory.load();
        }

        @Bean
        public JdbcTemplate jdbcTemplate() throws Exception {
                // c3p0
                final Properties properties = new Properties(System.getProperties());
                properties.put("com.mchange.v2.log.MLog",
                        "com.mchange.v2.log.FallbackMLog");
                properties.put("com.mchange.v2.log.FallbackMLog.DEFAULT_CUTOFF_LEVEL",
                        "OFF");
                System.setProperties(properties);

                final ComboPooledDataSource source = new ComboPooledDataSource();
                source.setMaxPoolSize(100);
                source.setDriverClass("org.h2.Driver");
                source.setJdbcUrl("jdbc:h2:mem:taskdb");
                source.setUser("sa");
                source.setPassword("");

                JdbcTemplate template = new JdbcTemplate(source);
                template.update("CREATE TABLE tasks (id INT(11) AUTO_INCREMENT, " + "payload VARCHAR(255), updated DATETIME)");
                return template;
        }
}
