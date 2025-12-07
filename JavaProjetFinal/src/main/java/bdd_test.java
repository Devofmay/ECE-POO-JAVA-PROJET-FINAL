import java.sql.DriverManager;
import java.sql.SQLException;

void main() {
    String url = "jdbc:mysql://localhost:3306/Gestionnaire_JDBC?createDatabaseIfNotExist=true";
    String username = "root",password = "";
    try{
        var conn= DriverManager.getConnection(url,username,password);
        System.out.println("Connected to database successfully");
    }

    catch (
        SQLException e){
        e.printStackTrace();
    }
}