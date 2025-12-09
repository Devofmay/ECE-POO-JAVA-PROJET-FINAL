package fr.ece.javaprojetfinal.basics;

import fr.ece.javaprojetfinal.basics.Utilisateur;
import fr.ece.javaprojetfinal.basics.DBconnect;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UtilisateurDAO {

    public List<Utilisateur> findAll() throws SQLException {
        List<Utilisateur> list = new ArrayList<>();
        String sql = "SELECT ID, Name, Address, Role, MDP FROM utilisateur";
        try (Connection c = DBconnect.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                int id = rs.getInt("ID");
                String name = rs.getString("Name");
                String address = rs.getString("Address");
                int roleInt = rs.getInt("Role");
                String role = roleInt == 1 ? "Admin" : "User";
                String mdp = rs.getString("MDP");
                // adapt to your Utilisateur constructor (nom, adresse, role, motDePasse)
                list.add(new Utilisateur(id, name, address, role, mdp));
            }
        }
        return list;
    }

    public void deleteById(int id) throws SQLException {
        String sql = "DELETE FROM utilisateur WHERE ID = ?";
        try (Connection c = DBconnect.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    public void update(Utilisateur u) throws SQLException {
        String sql = "UPDATE utilisateur SET Name = ?, Address = ?, Role = ?, MDP = ? WHERE ID = ?";
        try (Connection c = DBconnect.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, u.getNom()); // or getName() if your model uses English names
            ps.setString(2, u.getAdresse());
            // store role as numeric in DB: Admin -> 1, otherwise 0
            int roleInt = "Admin".equalsIgnoreCase(u.getRole()) ? 1 : 0;
            ps.setInt(3, roleInt);
            ps.setString(4, u.getMotDePasse());
            ps.setInt(5, u.getId());
            ps.executeUpdate();
        }
    }
    public void insert(Utilisateur u) throws SQLException {
        String sql = "INSERT INTO utilisateur (Name, Address, Role, MDP) VALUES (?, ?, ?, ?)";
        try (Connection c = DBconnect.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, u.getNom());
            ps.setString(2, u.getAdresse());
            int roleInt = "Admin".equalsIgnoreCase(u.getRole()) ? 1 : 0;
            ps.setInt(3, roleInt);
            ps.setString(4, u.getMotDePasse());
            ps.executeUpdate();
        }
    }

    // New helper: find a user's name by their ID
    public String findNameById(int id) throws SQLException {
        String sql = "SELECT Name FROM utilisateur WHERE ID = ?";
        try (Connection c = DBconnect.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("Name");
                }
            }
        }
        return null;
    }

}
