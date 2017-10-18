package ichttt.gradle.forgelogin;

import org.apache.logging.log4j.core.Logger;

public class LoggerWrapper extends Logger {

    protected LoggerWrapper(Logger logger) {
        super(logger.getContext(), logger.getName(), logger.getMessageFactory());
    }

    @Override
    public void info(String message) { //Don't print that...
        if (message == null || !message.startsWith("accessToken") && !message.startsWith("password") && !message.startsWith("username"))
            super.info(message);
    }
}
