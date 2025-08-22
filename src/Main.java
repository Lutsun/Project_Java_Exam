
import Database.DatabaseManager;
import Database.DatabaseOperations;
import UI.EmployeeManagementUI;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        // Informations de connexion à la base de données
        String url = "jdbc:sqlite:lib/Gestion_employes.db";

        // Initialisation de la base de données et de l'interface graphique
        DatabaseManager dbManager = new DatabaseManager(url);
        SwingUtilities.invokeLater(() -> new EmployeeManagementUI(dbManager).createAndShowGUI());
    }
}

