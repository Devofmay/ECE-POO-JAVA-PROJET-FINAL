package fr.ece.javaprojetfinal.basics;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ProjetDAO {
    private static final String SQL_BY_USERID =
            "SELECT p.ID, p.Nom, p.Description, p.Date_creation, p.Date_echeance, p.Responsable, p.Statut " +
                    "FROM projet p " +
                    "JOIN utilisateurs_projet up ON p.ID = up.Projet_id " +
                    "WHERE up.User_id = ?";

    private static final String SQL_BY_RESPONSABLE =
            "SELECT ID, Nom, Description, Date_creation, Date_echeance, Responsable, Statut " +
                    "FROM projet WHERE Responsable = ?";

    public List<Projet> findByUserId(int userId) throws SQLException {
        try (Connection conn = DBconnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_BY_USERID)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                return mapResultSetToList(rs);
            }
        }
    }

    public List<Projet> findByResponsableId(int responsableId) throws SQLException {
        try (Connection conn = DBconnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_BY_RESPONSABLE)) {
            ps.setInt(1, responsableId);
            try (ResultSet rs = ps.executeQuery()) {
                return mapResultSetToList(rs);
            }
        }
    }

    private List<Projet> mapResultSetToList(ResultSet rs) throws SQLException {
        List<Projet> projets = new ArrayList<>();
        while (rs.next()) {
            int id = rs.getInt("ID");
            String nom = rs.getString("Nom");
            String desc = rs.getString("Description");
            Date dCreate = rs.getDate("Date_creation");
            Date dEch = rs.getDate("Date_echeance");
            Integer resp = rs.getObject("Responsable") != null ? rs.getInt("Responsable") : null;
            String statut = rs.getString("Statut");
            java.util.Date dateCreation = dCreate != null ? new java.util.Date(dCreate.getTime()) : null;
            java.util.Date dateEcheance = dEch != null ? new java.util.Date(dEch.getTime()) : null;
            projets.add(new Projet(id, nom, desc, dateCreation, dateEcheance, resp, statut));
        }
        return projets;
    }
    public void deleteById(int id) throws SQLException {
        // Adjust table/column names if your DB uses different names
        String sql = "DELETE FROM projets WHERE id = ?";
        try (Connection conn = DBconnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            int affected = ps.executeUpdate();
            if (affected == 0) {
                throw new SQLException("No project deleted, id not found: " + id);
            }
        }
    }

}
