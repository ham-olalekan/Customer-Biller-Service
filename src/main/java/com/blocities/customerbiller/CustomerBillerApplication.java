package com.blocities.customerbiller;

import com.blocities.customerbiller.db.MongoDBFactoryConnection;
import com.blocities.customerbiller.db.MongoDBManaged;
import com.blocities.customerbiller.health.StarterServiceHealthCheck;
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

/**
 * This class start application.
 *
 * @author Lukman Olalekan
 */
public class CustomerBillerApplication extends Application<CustomerBillerApplicationConfiguration> {

    /**
     * Logger class.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(CustomerBillerApplication.class);

    /**
     * Entry point for start Application.
     *
     * @param args the args.
     * @throws Exception when the app can not start.
     */
    public static void main(final String[] args) throws Exception {
        LOGGER.info("Start application.");
        new CustomerBillerApplication().run(args);
    }

    @Override
    public String getName() {
        return "CustomerBillerApplication";
    }

    @Override
    public void initialize(final Bootstrap<CustomerBillerApplicationConfiguration> bootstrap) {
        bootstrap.setConfigurationSourceProvider(new SubstitutingSourceProvider(
                bootstrap.getConfigurationSourceProvider(),
                new EnvironmentVariableSubstitutor(false)));
        bootstrap.addBundle(new SwaggerBundle<CustomerBillerApplicationConfiguration>() {
            @Override
            protected SwaggerBundleConfiguration getSwaggerBundleConfiguration(
                    final CustomerBillerApplicationConfiguration dropwizardMongoDBMicroserviceConfiguration) {
                return dropwizardMongoDBMicroserviceConfiguration.getSwaggerBundleConfiguration();
            }
        });

        bootstrap.getObjectMapper().registerModule(new ParameterNamesModule());
        bootstrap.getObjectMapper().registerModule(new JavaTimeModule());
        bootstrap.getObjectMapper().registerModule(new Jdk8Module());
        bootstrap.getObjectMapper().setDateFormat(new StdDateFormat().withColonInTimeZone(true));
    }

    @Override
    public void run(final CustomerBillerApplicationConfiguration configuration,
                    final Environment environment) {
        final MongoDBFactoryConnection mongoDBManagerConn = new MongoDBFactoryConnection(
                configuration.getMongodbUri());

        final MongoDBManaged mongoDBManaged = new MongoDBManaged(mongoDBManagerConn.getClient());


        environment.lifecycle().manage(mongoDBManaged);
        environment.healthChecks().register(
                "StarterServiceHealthCheck",
                new StarterServiceHealthCheck(mongoDBManagerConn.getClient()
                        .getDatabase(mongoDBManagerConn.getServers().getDatabase())));
    }

}
