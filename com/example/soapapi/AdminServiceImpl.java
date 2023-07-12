package com.example.soapapi;

import javax.jws.WebService;
import java.security.SecureRandom;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

@WebService(endpointInterface = "com.example.soapapi.AdminService")
public class AdminServiceImpl implements AdminService {
    // Informations de connexions
    String url = "jdbc:mysql://localhost:3306/soap_db";
    String username = "abdallahi";
    String password = "passer123";

    // Variables pour exécuter les requêtes
    ResultSet resultSet = null;
    PreparedStatement statement;
    Connection connection = null;

    @Override
    public String authenticate(String username, String password) {
        // Le code correspondant à l'authentification ici
        // Retourne true si l'authentification réussit, sinon false
        // Exemple simple : retourne true si le nom d'utilisateur et le mot de passe
        // sont coorespond à un compte dans la base de données.

        try {
            // Connexion à la base données
            connection = DriverManager.getConnection(url, this.username, this.password);

            // Création de la requête SQL avec une clause WHERE
            String sqlQuery = "SELECT * FROM users where username = ? and password = ?";

            // Création de l'objet Statement
            statement = connection.prepareStatement(sqlQuery);
            statement.setString(1, username);
            statement.setString(2, password);

            // Exécution de la requête SQL
            resultSet = statement.executeQuery();

            // Vérification du résultat
            if (resultSet.next()) {
                // Délaration des variables locales
                String tokenGenerated = tokenGenerator();
                boolean tokenChanged = modifyUserToken(username, tokenGenerated);
                if (tokenChanged) {
                    System.out.println("Utilisateur connecté > "+ username);
                    return tokenGenerated;
                } else {
                    return null;
                }

            } else {
                return null;
            }

        } catch (SQLException e) {
            System.out.println("Erreur lors de l'authentification");
            e.printStackTrace();
            return null;

        }
    }

    @Override
    public List<User> getUsers(String token) {
        // Votre logique pour récupérer la liste des utilisateurs ici
        // Exemple simple : retourne une liste statique d'utilisateurs

        if (isAdministrator(token)) {
            try {
                List<User> users = new ArrayList<>();
                // Connexion à la base données
                connection = DriverManager.getConnection(url, this.username, this.password);

                // Création de la requête SQL avec une clause WHERE
                String sqlQuery = "SELECT * FROM users";

                // Création de l'objet Statement
                statement = connection.prepareStatement(sqlQuery);

                // Exécution de la requête SQL
                resultSet = statement.executeQuery();

                // Traitement du résultat
                while (resultSet.next()) {
                    users.add(new User(resultSet.getString("username"), resultSet.getString("password"),
                            resultSet.getInt("admin")));
                }
                return users;

            } catch (SQLException e) {
                System.out.println("Erreur lors de la récupération des utilisateurs");
                e.printStackTrace();
                return null;
            }
        } else {
            return null;
        }

    }

    @Override
    public boolean addUser(String username, String password, int admin, String token) {
        // Le code correspondant à l'ajout d'un utilisateur ici
        // Retourne true si l'ajout réussit, sinon false
        // Exemple simple : retourne true si le nom d'utilisateur n'existe pas encore
        // dans la base de données.

        if (isAdministrator(token)) {
            try {
                System.out.println("Création d'un utilisateur");
                // Connexion à la base données
                connection = DriverManager.getConnection(url, this.username, this.password);

                // Création de la requête SQL
                String sqlQuery = "INSERT INTO users(username, password, admin) values(?, ?, ?)";

                // Création de l'objet Statement
                statement = connection.prepareStatement(sqlQuery);
                statement.setString(1, username);
                statement.setString(2, password);
                statement.setInt(3, admin);

                // Exécution de la requête SQL
                int rowAffected = statement.executeUpdate();

                // Traitement du résultat
                if (rowAffected > 0) {
                    System.out.println("Utilisateur ajouté >"+ username);
                    return true;
                } else {
                    return false;
                }

            } catch (SQLException e) {
                System.out.println("Erreur d'ajout d'utilisateur");
                e.printStackTrace();
                return false;

            }
        } else {
            return false;
        }

    }

    @Override
    public boolean removeUser(String username, String token) {
        // Le code correspondant à la suppression d'un utilisateur ici
        // Retourne true si la suppression réussit, sinon false
        // Exemple simple : retourne true si le nom d'utilisateur existe déja dans la
        // base de données.
        if (isAdministrator(token)) {
            try {
                // Connexion à la base données
                connection = DriverManager.getConnection(url, this.username, this.password);

                // Création de la requête SQL avec une clause WHERE
                String sqlQuery = "DELETE FROM users where username = ?";

                // Création de l'objet Statement
                statement = connection.prepareStatement(sqlQuery);
                statement.setString(1, username);

                // Exécution de la requête SQL
                int rowAffected = statement.executeUpdate();

                // Traitement du résultat
                if (rowAffected > 0) {
                    System.out.println("Utilisateur supprimé > "+ username);
                    return true;
                } else {
                    return false;
                }

            } catch (SQLException e) {
                System.out.println("Erreur de suppression d'utilisateur");
                e.printStackTrace();
                return false;
            }
        } else {
            return false;
        }

    }

    @Override
    public User getUser(String username, String token) {
        if (isAdministrator(token)) {
            try {
                // Connexion à la base données
                connection = DriverManager.getConnection(url, this.username, this.password);

                // Création de la requête SQL avec une clause WHERE
                String sqlQuery = "SELECT * FROM users where username = ?";

                // Création de l'objet Statement
                statement = connection.prepareStatement(sqlQuery);
                statement.setString(1, username);

                // Exécution de la requête SQL
                resultSet = statement.executeQuery();

                // Vérification du résultat
                if (resultSet.next()) {
                    User user = new User(resultSet.getString("username"), resultSet.getString("password"),
                            resultSet.getInt("admin"));
                    return user;
                } else {
                    return null;
                }
            } catch (SQLException e) {
                System.out.println("Erreur de récupération d'un utilisateur");
                // e.printStackTrace();
                return null;
            }
        } else {
            return null;
        }
    }

    @Override
    public boolean modifyUser(String username, String newUsername, String newPassword, int newState, String token) {
        if (isAdministrator(token)) {
            try {
                // Connexion à la base données
                connection = DriverManager.getConnection(url, this.username, this.password);

                // Création de la requête SQL avec une clause WHERE
                String sqlQuery = "UPDATE users SET username=?, password=?, admin=? where username=?";

                // Création de l'objet Statement
                statement = connection.prepareStatement(sqlQuery);
                statement.setString(1, newUsername);
                statement.setString(2, newPassword);
                statement.setInt(3, newState);
                statement.setString(4, username);

                // Exécution de la requête SQL
                int rowAffected = statement.executeUpdate();

                // Traitement du résultat
                if (rowAffected > 0) {
                    return true;
                } else {
                    return false;
                }

            } catch (SQLException e) {
                System.out.println("Erreur de modification de la base de données");
                // e.printStackTrace();
                return false;
            }
        } else {
            return false;
        }

    }

    @Override
    public boolean deconnect(String username, String token) {
        try {
            if (isAdministrator(token)) {
                // Connexion à la base données
                connection = DriverManager.getConnection(url, this.username, this.password);

                // Création de la requête SQL avec une clause WHERE
                String sqlQuery = "UPDATE users SET token=NULL where username=?";

                statement = connection.prepareStatement(sqlQuery);
                statement.setString(1, username);

                // Exécution de la requête SQL
                int rowAffected = statement.executeUpdate();

                if (rowAffected > 0) {
                    return true;
                } else {
                    return false;
                }
            } else {
                return false;
            }

        } catch (SQLException e) {
            System.out.println("Erreur de déconnexion");
            e.printStackTrace();
            return false;

        }
    }

    // La fonction qui permet de générer des tokens
    private String tokenGenerator() {
        int tokenLength = 10; // Longueur du token

        // Création d'un objet SecureRandom
        SecureRandom secureRandom = new SecureRandom();

        // Génération d'un tableau de bytes aléatoires
        byte[] randomBytes = new byte[tokenLength];
        secureRandom.nextBytes(randomBytes);

        // Encodage des bytes en base64
        String token = Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes);
        return token;
    }

    // La fonction qui permet de tester si un utilisateur est un administerateur
    private boolean isAdministrator(String token) {
        try {
            // Connexion à la base données
            connection = DriverManager.getConnection(url, this.username, this.password);

            int administrator = 1;
            // Création de la requête SQL avec une clause WHERE
            String sqlQuery = "SELECT * FROM users where token = ? and admin = ?";

            statement = connection.prepareStatement(sqlQuery);
            statement.setString(1, token);
            statement.setInt(2, administrator);

            // Exécution de la requête SQL
            resultSet = statement.executeQuery();

            if (resultSet.next()) {
                return true;
            } else {
                return false;
            }

        } catch (SQLException e) {
            System.out.println("Erreur de la vérification d'administrateur");
            e.printStackTrace();
            return false;

        }
    }

    private boolean modifyUserToken(String username, String token) {

        try {
            // Connexion à la base données
            connection = DriverManager.getConnection(url, this.username, this.password);

            // Création de la requête SQL avec une clause WHERE
            String sqlQuery = "UPDATE users SET token=? where username=?";

            // Création de l'objet Statement
            statement = connection.prepareStatement(sqlQuery);
            statement.setString(1, token);
            statement.setString(2, username);
            // Exécution de la requête SQL
            int rowAffected = statement.executeUpdate();

            // Traitement du résultat
            if (rowAffected > 0) {
                return true;
            } else {
                return false;
            }

        } catch (SQLException e) {
            System.out.println("Erreur de modification du token de l'utilisateur");
            e.printStackTrace();
            return false;
        }

    }

}
