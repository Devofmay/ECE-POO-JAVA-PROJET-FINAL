package fr.ece.javaprojetfinal.basics;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import fr.ece.javaprojetfinal.basics.DBconnect;
import java.sql.Date;
import java.time.LocalDate;

public class TacheDAO {

    public List<Tache> findByUserId(int userId) throws SQLException {
        String sql = "SELECT t.*, p.Nom AS projet_nom " +
                "FROM taches t " +
                "JOIN projet p ON t.Id_projet = p.ID " +
                "JOIN utilisateurs_projet up ON up.Projet_id = p.ID " +
                "WHERE up.User_id = ? " +
                "ORDER BY t.Date_echeances ASC";

        List<Tache> list = new ArrayList<>();
        try (Connection conn = DBconnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Tache t = new Tache();
                    t.setId(rs.getInt("ID"));
                    t.setNom(rs.getString("Nom"));
                    t.setDescription(rs.getString("Description"));
                    Date dc = rs.getDate("Date_creation");
                    if (dc != null) t.setDateCreation(dc.toLocalDate());
                    Date de = rs.getDate("Date_echeances");
                    if (de != null) t.setDateEcheance(de.toLocalDate());
                    t.setProjetId(rs.getInt("Id_projet"));
                    t.setProjetNom(rs.getString("projet_nom"));
                    t.setStatut(rs.getString("Statut"));
                    t.setPriorite(rs.getString("Priorite"));
                    list.add(t);
                }
            }
        }
        return list;
    }
}
