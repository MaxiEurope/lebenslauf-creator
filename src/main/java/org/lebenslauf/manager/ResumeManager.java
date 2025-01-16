package org.lebenslauf.manager;

import org.lebenslauf.model.Resume;
import org.lebenslauf.util.LogUtils;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ResumeManager extends BaseManager {
    public ResumeManager(DBConnection db) {
        super(db);
    }

    public int getMaxVersionNumberForUser(int userId) {
        String query = "SELECT MAX(version_number) AS max_version FROM resume_versions WHERE user_id = ?";
        try (PreparedStatement stmt = db.getConnection().prepareStatement(query)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("max_version");
            }
        } catch (SQLException e) {
            String errMsg = "Error getting max version number for user " + userId;
            LogUtils.logError(e, errMsg);
            throw new RuntimeException(errMsg);
        }
        return 0; // no v found
    }

    public void addResumeVersion(int userId, int versionNumber, Resume resume) {
        String query = "INSERT INTO resume_versions (user_id, version_number, first_name, last_name, gender, birth_place, birth_date, city, address, postal_code, nationality, phone_number, email, experience, education, image_base64) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

        try (PreparedStatement stmt = db.getConnection().prepareStatement(query)) {
            stmt.setInt(1, userId);
            stmt.setInt(2, versionNumber);
            stmt.setString(3, resume.getFirstName());
            stmt.setString(4, resume.getLastName());
            stmt.setString(5, resume.getGender());
            stmt.setString(6, resume.getBirthPlace());
            stmt.setDate(7, resume.getBirthDate().isEmpty() ? null : Date.valueOf(LocalDate.parse(resume.getBirthDate())));
            stmt.setString(8, resume.getCity());
            stmt.setString(9, resume.getAddress());
            stmt.setString(10, resume.getPostalCode());
            stmt.setString(11, resume.getNationality());
            stmt.setString(12, resume.getPhoneNumber());
            stmt.setString(13, resume.getEmail());
            stmt.setString(14, String.join("\n", resume.getExperience()));
            stmt.setString(15, String.join("\n", resume.getEducation()));
            stmt.setString(16, resume.getImageBase64());
            stmt.executeUpdate();
        } catch (SQLException e) {
            String errMsg = "Error adding resume version for user " + userId;
            LogUtils.logError(e, errMsg);
            throw new RuntimeException(errMsg);
        }
    }

    public List<Resume> getResumeVersionsForUser(int userId) {
        String query = "SELECT * FROM resume_versions WHERE user_id = ? ORDER BY version_number ASC";
        List<Resume> resumes = new ArrayList<>();
        try (PreparedStatement stmt = db.getConnection().prepareStatement(query)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Resume resume = new Resume();
                resume.setFirstName(rs.getString("first_name"));
                resume.setLastName(rs.getString("last_name"));
                resume.setGender(rs.getString("gender"));
                resume.setBirthPlace(rs.getString("birth_place"));
                Date bd = rs.getDate("birth_date");
                resume.setBirthDate(bd == null ? "" : bd.toString());
                resume.setCity(rs.getString("city"));
                resume.setAddress(rs.getString("address"));
                resume.setPostalCode(rs.getString("postal_code"));
                resume.setNationality(rs.getString("nationality"));
                resume.setPhoneNumber(rs.getString("phone_number"));
                resume.setEmail(rs.getString("email"));
                String exp = rs.getString("experience") == null ? "" : rs.getString("experience");
                String edu = rs.getString("education") == null ? "" : rs.getString("education");
                resume.setExperience(List.of(exp.split("\\n")));
                resume.setEducation(List.of(edu.split("\\n")));
                resume.setImageBase64(rs.getString("image_base64"));
                resumes.add(resume);
            }
        } catch (SQLException e) {
            String errMsg = "Error getting resume versions for user " + userId;
            LogUtils.logError(e, errMsg);
            throw new RuntimeException(errMsg);
        }
        return resumes;
    }
}
