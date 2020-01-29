package com.blocities.customerbiller.db;

import com.mongodb.ConnectionString;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Lukman Olalekan
 */
public class MongoDBFactoryConnection {

    /**
     * Logger class.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(MongoDBFactoryConnection.class);

    /**
     * The configuration for connect to MongoDB Server.
     */
    private String MONGODB_URI;

    /**
     * Constructor.
     *
     * @param MONGODB_URI the mongoDB connection data.
     */
    public MongoDBFactoryConnection(final String MONGODB_URI) {
        this.MONGODB_URI = MONGODB_URI;
    }

    /**
     * Gets the connection to MongoDB.
     *
     * @return the mongo Client.
     */
    public MongoClient getClient() {
        LOGGER.info("Creating mongoDB client.");
        final MongoClient client = MongoClients.create(MONGODB_URI);
        return client;
    }

    /**
     * Connection URL objects {@link ConnectionString} that contain the information of servers.
     *
     * @return connectionString
     */
    public ConnectionString getServers() {
        ConnectionString connectionString = new ConnectionString(MONGODB_URI);
        return connectionString;
    }
}

