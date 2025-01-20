package org.lebenslauf.manager;

/**
 * Abstract base class for managers.
 */
public abstract class BaseManager {
    protected final DBConnection db;

    public BaseManager(DBConnection db) {
        this.db = db;
    }

    public abstract void logManagerInfo();
}
