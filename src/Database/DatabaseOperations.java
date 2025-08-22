package Database;

import java.util.Vector;

    public interface DatabaseOperations {
        void addEmployee(String name, String department, double salary);
        void deleteEmployee(int id);
        Vector<Vector<Object>> getAllEmployees();

        void updateEmployee(int id, String name, String department, double salary);

    }

