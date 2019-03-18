package ai.kudi.dropwizardkafkastarter.process;

import ai.kudi.beowulf.kafka.KafkaMessageBus;
import io.dropwizard.lifecycle.Managed;

/**
 * This is used for manage the kafka process.
 *
 * @author Abayomi Popoola
 */
public class KafkaManaged implements Managed {

    /**
     * The messageBus client.
     */
    private final KafkaMessageBus messageBus;
    private final ProcessManager processManager;

    /**
     * Constructor.
     * @param messageBus the mongoDB client.
     */
    public KafkaManaged(final KafkaMessageBus messageBus, final ProcessManager processManager) {
        this.messageBus = messageBus;
        this.processManager = processManager;
    }

    @Override
    public void start() throws Exception {
        messageBus.startConsuming();
    }

    @Override
    public void stop() throws Exception {
        messageBus.shutdown();
    }
}
