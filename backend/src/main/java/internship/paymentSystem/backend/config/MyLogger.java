package internship.paymentSystem.backend.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MyLogger {
    private static final Logger LOGGER = LoggerFactory.getLogger(MyLogger.class);

    private static MyLogger instance = null;

    private MyLogger() {}

    public static synchronized MyLogger getInstance() {
        if (instance == null)
            instance = new MyLogger();

        return instance;
    }

    public void logInfo(String message) {
        LOGGER.info(message);
    }

    public void logError(String message) {
        LOGGER.error(message);
    }
}
