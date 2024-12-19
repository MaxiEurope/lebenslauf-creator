package org.lebenslauf.service;

import org.lebenslauf.model.Resume;
import org.lebenslauf.manager.DBConnection;
import org.lebenslauf.manager.ResumeManager;

import java.util.List;

public class ResumeService {
    private final ResumeManager resumeManager;

    public ResumeService(DBConnection db) {
        this.resumeManager = new ResumeManager(db);
    }

    public int getNextVersionNumber(int userId) {
        int currentMax = resumeManager.getMaxVersionNumberForUser(userId);
        return currentMax + 1;
    }

    public void saveResumeVersion(int userId, int versionNumber, Resume resume) {
        resumeManager.addResumeVersion(userId, versionNumber, resume);
    }

    public List<Resume> getUserResumeVersions(int userId) {
        return resumeManager.getResumeVersionsForUser(userId);
    }
}
