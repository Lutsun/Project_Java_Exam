package UI;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.Vector;
import Database.DatabaseOperations;
import Database.DatabaseManager;

public class EmployeeManagementUI {
    private static DatabaseOperations dbManager;

    public EmployeeManagementUI(DatabaseOperations dbManager) {
        this.dbManager = dbManager;
    }

    public static void main(String[] args) {
        // URL de connexion à la base SQLite
        String url = "jdbc:sqlite:lib/employes.db";

        // Initialisation du DatabaseManager
        DatabaseOperations dbManager = new DatabaseManager(url);

        // Lancer l'interface graphique
        SwingUtilities.invokeLater(() -> new EmployeeManagementUI(dbManager).createAndShowGUI());
    }

    public void createAndShowGUI() {
        JFrame frame = new JFrame("Gestion des Employés");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        // Utiliser une palette de couleurs moderne
        Color backgroundColor = new Color(240, 240, 240);
        Color buttonColor = new Color(51, 153, 255);
        Color buttonTextColor = Color.WHITE;
        Color addButtonColor = new Color(76, 175, 80); // Vert
        Color updateButtonColor = new Color(255, 152, 0); // Orange
        Color deleteButtonColor = new Color(244, 67, 54); // Rouge
        Color panelBorderColor = new Color(200, 200, 200);

        // Police moderne
        Font modernFont = new Font("Segoe UI", Font.PLAIN, 14);
        Font modernBoldFont = new Font("Segoe UI", Font.BOLD, 14);

        // Panel principal
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(backgroundColor);

        // Panel de gauche (formulaire modernisé)
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(panelBorderColor),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)));
        formPanel.setBackground(Color.WHITE);

        // Champs du formulaire
        JLabel nameLabel = new JLabel("Nom:");
        nameLabel.setFont(modernBoldFont);
        JTextField nameField = new JTextField(15);
        nameField.setFont(modernFont);
        nameField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(panelBorderColor),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)));

        JLabel departmentLabel = new JLabel("Département:");
        departmentLabel.setFont(modernBoldFont);
        JComboBox<String> departmentComboBox = new JComboBox<>(new String[]{"RH", "IT", "Marketing", "Finance"});
        departmentComboBox.setFont(modernFont);
        departmentComboBox.setBackground(Color.WHITE);
        departmentComboBox.setBorder(BorderFactory.createLineBorder(panelBorderColor));

        JLabel salaryLabel = new JLabel("Salaire:");
        salaryLabel.setFont(modernBoldFont);
        JTextField salaryField = new JTextField(15);
        salaryField.setFont(modernFont);
        salaryField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(panelBorderColor),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)));

        formPanel.add(nameLabel);
        formPanel.add(nameField);
        formPanel.add(Box.createVerticalStrut(15)); // Espacement
        formPanel.add(departmentLabel);
        formPanel.add(departmentComboBox);
        formPanel.add(Box.createVerticalStrut(15)); // Espacement
        formPanel.add(salaryLabel);
        formPanel.add(salaryField);

        mainPanel.add(formPanel, BorderLayout.WEST);

        // Panel de droite (tableau)
        JPanel tablePanel = new JPanel();
        tablePanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(panelBorderColor),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)));
        tablePanel.setBackground(backgroundColor);
        String[] columnNames = {"ID", "Nom", "Département", "Salaire"};
        JTable table = new JTable(new DefaultTableModel(new Object[0][0], columnNames));
        table.setRowHeight(25);
        table.setFont(modernFont);
        JScrollPane tableScrollPane = new JScrollPane(table);
        tablePanel.add(tableScrollPane);

        mainPanel.add(tableScrollPane, BorderLayout.CENTER);

        // Panel inférieur (boutons)
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 15));
        buttonPanel.setBackground(backgroundColor);

        JButton addButton = createStyledButton("Ajouter", addButtonColor, buttonTextColor);
        JButton clearButton = createStyledButton("Effacer", buttonColor, buttonTextColor);
        JButton updateButton = createStyledButton("Modifier", updateButtonColor, buttonTextColor);
        JButton deleteButton = createStyledButton("Supprimer", deleteButtonColor, buttonTextColor);

        buttonPanel.add(addButton);
        buttonPanel.add(clearButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(deleteButton);

        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        // Ajouter des listeners aux boutons
        addButton.addActionListener(e -> {
            String name = nameField.getText();
            String department = (String) departmentComboBox.getSelectedItem();
            try {
                double salary = Double.parseDouble(salaryField.getText());
                dbManager.addEmployee(name, department, salary);
                updateTable(table);
                nameField.setText("");
                salaryField.setText("");
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(null, "Veuillez entrer un salaire valide.", "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        });

        clearButton.addActionListener(e -> {
            nameField.setText("");
            salaryField.setText("");
            departmentComboBox.setSelectedIndex(0);
        });

        deleteButton.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow != -1) {
                int id = (int) table.getValueAt(selectedRow, 0);
                dbManager.deleteEmployee(id);
                updateTable(table);
            } else {
                JOptionPane.showMessageDialog(null, "Veuillez sélectionner une ligne à supprimer.", "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        });

        updateButton.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow != -1) {
                try {
                    int id = (int) table.getValueAt(selectedRow, 0);
                    String name = nameField.getText();
                    String department = (String) departmentComboBox.getSelectedItem();

                    // Vérifier si le champ salaire est valide
                    String salaryText = salaryField.getText().trim(); // Supprimer les espaces avant ou après le texte
                    if (salaryText.isEmpty() || !salaryText.matches("\\d+(\\.\\d+)?")) {
                        throw new NumberFormatException(); // Lancer une exception si le format est incorrect
                    }

                    double salary = Double.parseDouble(salaryText);
                    dbManager.updateEmployee(id, name, department, salary);
                    updateTable(table);
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(null, "Veuillez entrer un salaire valide.", "Erreur", JOptionPane.ERROR_MESSAGE);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(null, "Une erreur s'est produite : " + ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(null, "Veuillez sélectionner une ligne à modifier.", "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        });


        // Ajouter le panel principal au frame
        frame.add(mainPanel);
        frame.setSize(800, 600);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private JButton createStyledButton(String text, Color backgroundColor, Color textColor) {
        JButton button = new JButton(text);
        button.setBackground(backgroundColor);
        button.setForeground(textColor);
        button.setFocusPainted(false);
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(backgroundColor.darker()),
                BorderFactory.createEmptyBorder(10, 15, 10, 15)));
        return button;
    }

    private void updateTable(JTable table) {
        Vector<Vector<Object>> employes = dbManager.getAllEmployees();
        Vector<String> columnNames = new Vector<>();
        columnNames.add("ID");
        columnNames.add("Nom");
        columnNames.add("Departement");
        columnNames.add("Salaire");

        table.setModel(new DefaultTableModel(employes, columnNames));
        table.revalidate();
        table.repaint();
    }
}
