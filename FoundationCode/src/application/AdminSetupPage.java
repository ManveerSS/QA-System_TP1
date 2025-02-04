package application;

import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.sql.SQLException;

import databasePart1.*;

/**
 * The SetupAdmin class handles the setup process for creating an administrator account.
 * This is intended to be used by the first user to initialize the system with admin credentials.
 */
public class AdminSetupPage {
	
    private final DatabaseHelper databaseHelper;

    public AdminSetupPage(DatabaseHelper databaseHelper) {
        this.databaseHelper = databaseHelper;
    }

    public void show(Stage primaryStage) {
    	// Input fields for userName and password
        TextField userNameField = new TextField();
        userNameField.setPromptText("Enter Admin userName");
        userNameField.setMaxWidth(250);
        
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Enter Password");
        passwordField.setMaxWidth(250);
        
        // Displays error messages in terms of username and password inputs
        Label userNameErrorLabel = new Label();
        userNameErrorLabel.setStyle("-fx-text-fill: red; -fx-font-size: 12px;");
        
        Label passwordErrorLabel = new Label();
        passwordErrorLabel.setStyle("-fx-text-fill: red; -fx-font-size: 12px;");
        
        Button setupButton = new Button("Setup");
        
        setupButton.setOnAction(a -> {
        	// Retrieve user input
            String userName = userNameField.getText();
            String password = passwordField.getText();
            
            // Set up user fields and error message string for validity check
            String validUserName = "";
            String validPassword = "";
            String errMsg = "";
            
            // Checks if username input satisfies requirements using the UserNameRecognizer class
            // If the error message is empty, then it passes
            errMsg = UserNameRecognizer.checkForValidUserName(userName);	
            if (errMsg != "") {
            	userNameErrorLabel.setText(errMsg);
            } else {
            	validUserName = userName;
            	userNameErrorLabel.setText(null);
            }
            
            // Checks if password satisfies requirements using the PasswordEvaluator class
            // If the error message is empty, then it passes
            errMsg = PasswordEvaluator.evaluatePassword(password);
            if (errMsg != "") {
            	passwordErrorLabel.setText(errMsg);
            } else {
            	validPassword = password;
            	passwordErrorLabel.setText(null);
            }
            
            // If the username and password is validated, it will bypass this check to create a new user
            if (validUserName != "" && validPassword != "") { 
            	try {
           		// Create a new User object with admin role and register in the database
                	User user = new User(validUserName, password, "admin");
                     databaseHelper.register(user);
                     System.out.println("Administrator setup completed.");
                      
                     // Navigate to the Welcome Login Page
                     new WelcomeLoginPage(databaseHelper).show(primaryStage,user);
                } catch (SQLException e) {
                     System.err.println("Database error: " + e.getMessage());
                     e.printStackTrace();
                }
            }         
        });

        VBox layout = new VBox(10, userNameField, userNameErrorLabel, passwordField, passwordErrorLabel, setupButton);
        layout.setStyle("-fx-padding: 20; -fx-alignment: center;");

        primaryStage.setScene(new Scene(layout, 800, 400));
        primaryStage.setTitle("Administrator Setup");
        primaryStage.show();
    }
}
