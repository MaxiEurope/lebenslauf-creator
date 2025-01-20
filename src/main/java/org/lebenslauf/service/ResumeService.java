package org.lebenslauf.service;

import org.lebenslauf.model.Resume;
import org.lebenslauf.manager.DBConnection;
import org.lebenslauf.manager.ResumeManager;

import java.util.List;

/**
 * Service class for resume operations.
 */
public class ResumeService {
    private final ResumeManager resumeManager;

    /**
     * Constructor.
     *
     * @param db the database connection
     */
    public ResumeService(DBConnection db) {
        this.resumeManager = new ResumeManager(db);
        this.resumeManager.logManagerInfo();
    }

    /**
     * Get the next version number for a user.
     *
     * @param userId the user ID
     * @return the next version number
     */
    public int getNextVersionNumber(int userId) {
        int currentMax = resumeManager.getMaxVersionNumberForUser(userId);
        return currentMax + 1;
    }

    /**
     * Save a new resume version for a user.
     *
     * @param userId the user ID
     * @param versionNumber the version number
     * @param resume the resume object
     */
    public void saveResumeVersion(int userId, int versionNumber, Resume resume) {
        resumeManager.addResumeVersion(userId, versionNumber, resume);
    }

    /**
     * Get all resume versions for a user.
     *
     * @param userId the user ID
     * @return a list of resume objects
     */
    public List<Resume> getUserResumeVersions(int userId) {
        return resumeManager.getResumeVersionsForUser(userId);
    }
}
