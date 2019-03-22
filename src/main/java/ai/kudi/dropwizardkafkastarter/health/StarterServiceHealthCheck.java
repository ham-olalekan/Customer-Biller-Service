package ai.kudi.dropwizardkafkastarter.health;

import com.codahale.metrics.health.HealthCheck;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

/**
 * Health Check.
 *
 * @author Abayomi Popoola
 */
public class StarterServiceHealthCheck extends HealthCheck {

    /**
     * MongoDatabase.
     */
    private MongoDatabase mongoDatabase;

    /**
     * Constructor.
     *
     * @param mongoDatabase
     */
    public StarterServiceHealthCheck(final MongoDatabase mongoDatabase) {
        this.mongoDatabase = mongoDatabase;
    }

    @Override
    protected Result check() {
        try {
            final Document document = mongoDatabase.runCommand(new Document("buildInfo", 1));
            if (document == null) {
                return Result.unhealthy("Can not perform operation buildInfo in Database.");
            }
        } catch (final Exception e) {
            return Result.unhealthy("Can not get the information from database.");
        }
        return Result.healthy();
    }
}
