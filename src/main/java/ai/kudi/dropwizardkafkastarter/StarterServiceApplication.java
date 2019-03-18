package ai.kudi.dropwizardkafkastarter;

import ai.kudi.beowulf.GenericSExpressionMessageBus;
import ai.kudi.beowulf.GenericTypedMessageBus;
import ai.kudi.beowulf.TypedMessageBus;
import ai.kudi.beowulf.kafka.KafkaMessageBus;
import ai.kudi.dropwizardkafkastarter.db.MongoDBFactoryConnection;
import ai.kudi.dropwizardkafkastarter.db.MongoDBManaged;
import ai.kudi.dropwizardkafkastarter.health.StarterServiceHealthCheck;
import ai.kudi.dropwizardkafkastarter.process.KafkaManaged;
import ai.kudi.dropwizardkafkastarter.process.ProcessManager;
import ai.kudi.sexp.SExpParser;
import com.fasterxml.jackson.databind.util.StdDateFormat;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;
import io.dropwizard.Application;
import io.dropwizard.configuration.EnvironmentVariableSubstitutor;
import io.dropwizard.configuration.SubstitutingSourceProvider;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import io.federecio.dropwizard.swagger.SwaggerBundle;
import io.federecio.dropwizard.swagger.SwaggerBundleConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.Jedis;

/**
 * This class start application.
 *
 * @author Abayomi Popoola
 */
public class StarterServiceApplication extends Application<StarterServiceConfiguration> {

    /**
     * Logger class.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(StarterServiceApplication.class);

    /**
     * Entry point for start Application.
     *
     * @param args the args.
     * @throws Exception when the app can not start.
     */
    public static void main(final String[] args) throws Exception {
        LOGGER.info("Start application.");
        new StarterServiceApplication().run(args);
    }

    @Override
    public String getName() {
        return "StarterServiceApplication";
    }

    @Override
    public void initialize(final Bootstrap<StarterServiceConfiguration> bootstrap) {
        bootstrap.setConfigurationSourceProvider(new SubstitutingSourceProvider(
            bootstrap.getConfigurationSourceProvider(),
            new EnvironmentVariableSubstitutor(false)));
        bootstrap.addBundle(new SwaggerBundle<StarterServiceConfiguration>() {
            @Override
            protected SwaggerBundleConfiguration getSwaggerBundleConfiguration(
                final StarterServiceConfiguration dropwizardMongoDBMicroserviceConfiguration) {
                return dropwizardMongoDBMicroserviceConfiguration.getSwaggerBundleConfiguration();
            }
        });

        bootstrap.getObjectMapper().registerModule(new ParameterNamesModule());
        bootstrap.getObjectMapper().registerModule(new JavaTimeModule());
        bootstrap.getObjectMapper().registerModule(new Jdk8Module());
        bootstrap.getObjectMapper().setDateFormat(new StdDateFormat().withColonInTimeZone(true));
    }

    @Override
    public void run(final StarterServiceConfiguration configuration,
                    final Environment environment) {
        final MongoDBFactoryConnection mongoDBManagerConn = new MongoDBFactoryConnection(
            configuration.getMongodbUri());

        final MongoDBManaged mongoDBManaged = new MongoDBManaged(mongoDBManagerConn.getClient());

        final Jedis jedis = new Jedis(new HostAndPort(
                configuration.getRedis().split(":")[0],
                Integer.parseInt(configuration.getRedis().split(":")[1]))
        );

        final KafkaMessageBus messageBus = new KafkaMessageBus(configuration.getKafkaConfiguration());
        final GenericSExpressionMessageBus sExpMessageBus = new GenericSExpressionMessageBus<>(
            messageBus,
            new SExpParser());
        final TypedMessageBus genericTypedMessageBus = new GenericTypedMessageBus<>(sExpMessageBus);
        final ProcessManager messageProcessor = new ProcessManager(
            genericTypedMessageBus);

        final KafkaManaged kafkaManaged = new KafkaManaged(messageBus, messageProcessor);

        environment.lifecycle().manage(mongoDBManaged);
        environment.lifecycle().manage(kafkaManaged);
        environment.healthChecks().register(
            "StarterServiceHealthCheck",
            new StarterServiceHealthCheck(mongoDBManagerConn.getClient()));
    }

}
