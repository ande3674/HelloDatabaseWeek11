import java.sql.*;

import static input.InputUtils.*;

/**
 * Created by ce6915gp on 4/19/2018.
 */
public class Vet {

    private static final String url = "jdbc:sqlite:vet.sqlite";

    public static void main(String[] args) {

        int choice;

        do {
            choice = intInput("Enter 1 to add a dog, " +
                    "2 to search for a dog, " +
                    "3 to update vax status, " +
                    "4 to delete a dog, " +
                    "5 to quit.");

            if (choice == 1){
                addDog();
            }
            else if (choice == 2){
                searchDog();
            }
            else if (choice == 3){
                updateVaxStatus();
            }
            else if (choice == 4){
                deleteDog();
            }
            else if (choice == 5){
                System.out.println("goodbye!");
            }
            else {
                System.out.println("Please enter 1, 2, or 3.");
            }
        } while (choice != 3);
    }

    private static void deleteDog() {

        final String deleteSql = "DELETE FROM dogs WHERE name LIKE ?";

        try (Connection connection = DriverManager.getConnection(url);
             PreparedStatement ps = connection.prepareStatement(deleteSql)) {

            String name = stringInput("Enter the dog's name to delete: ");

            ps.setString(1, name);

            int rowsUpdated = ps.executeUpdate();

            if (rowsUpdated == 0){
                System.out.println("Sorry, dog not found.");
            } else {
                System.out.println("Dog deleted.");
            }
        } catch (SQLException e){
            System.out.println("Error updating dog.");
            System.out.println(e);
        }
    }

    private static void updateVaxStatus() {

        final String updateVaxSql = "UPDATE dogs SET vax = ? WHERE name LIKE ?";

        try (Connection connection = DriverManager.getConnection(url);
             PreparedStatement ps = connection.prepareStatement(updateVaxSql)){

            String name = stringInput("Enter dog's name: ");
            boolean vax = yesNoInput("Is this dog vaccinated? ");

            ps.setBoolean(1, vax);
            ps.setString(2, name);

            int rowsUpdated = ps.executeUpdate();

            if (rowsUpdated == 0){
                System.out.println("Sorry, dog not found.");
            } else {
                System.out.println("Database updated.");
            }
        } catch (SQLException e){
            System.out.println("Error updating dog.");
            System.out.println(e);
        }
    }

    private static void searchDog() {

        final String searchSql = "SELECT * FROM dogs WHERE upper(name) LIKE upper(?)";

        try (Connection connection = DriverManager.getConnection(url);
             PreparedStatement searchStatement = connection.prepareStatement(searchSql)) {

            String searchName = stringInput("Enter a dog name to search for: ");

            searchStatement.setString(1, "%" + searchName + "%");
            ResultSet dogRs = searchStatement.executeQuery();

            if (!dogRs.isBeforeFirst()) { // this returns false if there are no results
                System.out.println("Sorry, no dogs found with that name.");
            }

            else {
                while (dogRs.next()){
                    String name = dogRs.getString("name");
                    int age = dogRs.getInt("age");
                    double weight = dogRs.getDouble("weight");
                    boolean vax = dogRs.getBoolean("vax");
                    System.out.printf("Name %s, age %d, weight %f, vaccinated %s\n", name, age, weight, vax);
                }
            }
        } catch (SQLException e){
            System.out.println("Error searching for dog");
            System.out.println(e);
        }
    }

    private static void addDog() {

        final String addSql = "INSERT INTO dogs VALUES (?, ?, ?, ?)";

        try (Connection connection = DriverManager.getConnection(url);
             PreparedStatement ps = connection.prepareStatement(addSql)) {
            String name = stringInput("Enter dog name: ");
            int age = intInput("Enter dog's age: ");
            double weight = doubleInput("Enter dog's weight: ");
            boolean vax = yesNoInput("Is dog vaccinated?");

            ps.setString(1, name);
            ps.setInt(2, age);
            ps.setDouble(3, weight);
            ps.setBoolean(4, vax);

            ps.execute();

        } catch (SQLException e){
            System.out.println("Error adding new dog.");
            System.out.println(e);
        }
    }
}
