package org.lebenslauf.manager;

public abstract class BaseManager {
    protected final DBConnection db;

    public BaseManager(DBConnection db) {
        this.db = db;
    }
}
