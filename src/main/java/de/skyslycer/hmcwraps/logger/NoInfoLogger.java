package de.skyslycer.hmcwraps.logger;

import java.util.logging.Level;
import java.util.logging.Logger;

public class NoInfoLogger extends Logger {

    public NoInfoLogger(String name, String resourceBundleName) {
        super(name, resourceBundleName);
    }

    @Override
    public void info(String msg) { }

    @Override
    public void log(Level level, String msg) {
        if (level == Level.INFO) {
            return;
        }
        super.log(level, msg);
    }

}
