package ai.kudi.dropwizardkafkastarter.process;

import ai.kudi.beowulf.MessageProcessor;
import ai.kudi.beowulf.TypedMessageBus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class description
 *
 * @author Abayomi Popoola
 * created on 18/01/2019
 */
public class ProcessManager {

    private static final Logger log = LoggerFactory.getLogger(ProcessManager.class);
    private final TypedMessageBus messageBus;

    public ProcessManager(final TypedMessageBus messageBus) {
        this.messageBus = messageBus;


        final MessageProcessor messageProcessor = new MessageProcessor(this.messageBus);

//        messageProcessor.registerMessageHandler();
    }
}
