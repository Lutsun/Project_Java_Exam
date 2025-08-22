package Database;

import java.sql.*;
import java.util.Vector;

public class DatabaseManager implements DatabaseOperations {

    private Connection connection;

    public DatabaseManager(String url) {
        try {
            connection = DriverManager.getConnection(url);
            System.out.println("Connexion réussie !");
            initializeDatabase(); // Vérifie et crée la table si nécessaire
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Erreur de connexion à la base de données : " + e.getMessage());
        }
    }

    // Méthode privée pour initialiser la table si elle n'existe pas
    private void initializeDatabase() {
        String createTableQuery = """
            CREATE TABLE IF NOT EXISTS employes (
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            nom TEXT NOT NULL,
            departement TEXT,
            salaire REAL
            );
        """;

        try (Statement stmt = connection.createStatement()) {
            stmt.execute(createTableQuery);
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de l'initialisation de la base de données : " + e.getMessage());
        }
    }

    @Override
    public void addEmployee(String name, String department, double salary) {
        String query = "INSERT INTO employes (nom, departement, salaire) VALUES (?, ?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setString(1, name);
            ps.setString(2, department);
            ps.setDouble(3, salary);

            int rowsAffected = ps.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Employé ajouté avec succès !");
            } else {
                System.out.println("Aucun employé ajouté.");
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de l'ajout de l'employé : " + e.getMessage());
        }
    }

    @Override
    public void deleteEmployee(int id) {
        String query = "DELETE FROM employes WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, id);
            ps.executeUpdate();
            System.out.println("Employé supprimé avec succès !");

            // Réinitialiser l'auto-incrémentation
            resetAutoIncrement();

        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la suppression de l'employé : " + e.getMessage());
        }
    }

    // Méthode pour réinitialiser l'auto-incrémentation en recuperant le plus petit id manquant et empecher l'id de s'auto incrementé
    private void resetAutoIncrement() {
        String query = "SELECT MIN(id) FROM employes WHERE id NOT IN (SELECT id FROM employes)";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            if (rs.next()) {
                int nextId = rs.getInt(1);
                if (nextId > 0) {
                    String resetQuery = "UPDATE sqlite_sequence SET seq = ? WHERE name = 'employes'";
                    try (PreparedStatement ps = connection.prepareStatement(resetQuery)) {
                        ps.setInt(1, nextId - 1);  // Réinitialise la séquence à l'ID manquant le plus petit
                        ps.executeUpdate();
                        System.out.println("Auto-incrémentation réinitialisée.");
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la réinitialisation de l'auto-incrémentation : " + e.getMessage());
        }
    }

    @Override
    public Vector<Vector<Object>> getAllEmployees() {
        String query = "SELECT * FROM employes";
        Vector<Vector<Object>> data = new Vector<>();
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                Vector<Object> row = new Vector<>();
                row.add(rs.getInt("id"));
                row.add(rs.getString("nom"));
                row.add(rs.getString("departement"));
                row.add(rs.getDouble("salaire"));
                data.add(row);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors du chargement des employés : " + e.getMessage());
        }
        return data;
    }

    @Override
    public void updateEmployee(int id, String name, String department, double salary) {
        String query = "UPDATE employes SET Nom = ?, Departement = ?, Salaire = ? WHERE ID = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, name);
            stmt.setString(2, department);
            stmt.setDouble(3, salary);
            stmt.setInt(4, id);  // Ici, l'ID de l'employé à modifier
            stmt.executeUpdate();
            System.out.println("Employe mis à jour avec succès !");
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Erreur lors de la mise à jour de l'employé : " + e.getMessage());
        }
    }

    // Fermer la connexion à la base de données
    public void closeConnection() {
        try {
            if (connection != null) {
                connection.close();
                System.out.println("Connexion fermée.");
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de la fermeture de la connexion : " + e.getMessage());
        }
    }
}
