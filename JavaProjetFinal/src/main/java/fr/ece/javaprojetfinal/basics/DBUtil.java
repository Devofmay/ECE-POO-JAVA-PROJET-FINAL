// java
package fr.ece.javaprojetfinal.basics;

import java.sql.*;
import java.util.Optional;

/**
 * helper BDD mini
 */
public class DBUtil {

    private static final String URL = "jdbc:mysql://localhost:3306/gestionnaire_jdbc?useSSL=false&serverTimezone=UTC";
    private static final String USER = "root";
    private static final String PASS = "";

    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASS);
    }

    public static Optional<User> getUserById(int id) {
        String sql = "SELECT ID, Name, Address, MDP FROM utilisateur WHERE ID = ?";
        try (Connection c = getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    User u = new User(
                            rs.getInt("ID"),
                            rs.getString("Name"),
                            rs.getString("Address"),
                            rs.getString("MDP")
                    );
                    return Optional.of(u);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    public static boolean updateUser(int id, String name, String address, String mdp) {
        String sql = "UPDATE utilisateur SET Name = ?, Address = ?, MDP = ? WHERE ID = ?";
        try (Connection c = getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, name);
            ps.setString(2, address);
            ps.setString(3, mdp);
            ps.setInt(4, id);
            return ps.executeUpdate() == 1;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static class User {
        public final int id;
        public final String name;
        public final String address;
        public final String mdp;

        public User(int id, String name, String address, String mdp) {
            this.id = id;
            this.name = name;
            this.address = address;
            this.mdp = mdp;
        }
    }
}
