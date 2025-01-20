package org.lebenslauf.service;

import org.lebenslauf.model.Resume;
import org.lebenslauf.manager.DBConnection;
import org.lebenslauf.manager.ResumeManager;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

/**
 * Service class for resume operations.
 */
public class ResumeService {
    private final ResumeManager resumeManager;

    // Base folder where all user resume files will be saved
    private static final String BASE_FOLDER = "user_resumes/";
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

    public void quickSaveResume(int userId, Resume resume) {
        // Ensure the user folder exists
        Path userFolder = Paths.get(BASE_FOLDER + userId);
        if (!Files.exists(userFolder)) {
            try {
                Files.createDirectories(userFolder);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // Save the resume file (using .ser for serialization)
        String fileName = "resume_quick_save.ser";
        Path filePath = userFolder.resolve(fileName);
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(filePath.toFile()))) {
            out.writeObject(resume);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Resume loadQuickSavedResume(int userId) {
        Path userFolder = Paths.get(BASE_FOLDER + userId);
        Path filePath = userFolder.resolve("resume_quick_save.ser");

        if (Files.exists(filePath)) {
            try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(filePath.toFile()))) {
                return (Resume) in.readObject();
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
        return null; // Return null if no saved resume exists
    }
    
}
