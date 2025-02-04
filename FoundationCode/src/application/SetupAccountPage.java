package application;

import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.sql.SQLException;

import databasePart1.*;

/**
 * SetupAccountPage class handles the account setup process for new users.
 * Users provide their userName, password, and a valid invitation code to register.
 */
public class SetupAccountPage {
	
    private final DatabaseHelper databaseHelper;
    // DatabaseHelper to handle database operations.
    public SetupAccountPage(DatabaseHelper databaseHelper) {
        this.databaseHelper = databaseHelper;
    }

    /**
     * Displays the Setup Account page in the provided stage.
     * @param primaryStage The primary stage where the scene will be displayed.
     */
    public void show(Stage primaryStage) {
    	// Input fields for userName, password, and invitation code
        TextField userNameField = new TextField();
        userNameField.setPromptText("Enter userName");
        userNameField.setMaxWidth(250);

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Enter Password");
        passwordField.setMaxWidth(250);
        
        TextField inviteCodeField = new TextField();
        inviteCodeField.setPromptText("Enter InvitationCode");
        inviteCodeField.setMaxWidth(250);
        
        // Label to display username, password, or other miscellaneous errors
        Label userNameErrorLabel = new Label();
        userNameErrorLabel.setStyle("-fx-text-fill: red; -fx-font-size: 12px;");

        Label passwordErrorLabel = new Label();
        passwordErrorLabel.setStyle("-fx-text-fill: red; -fx-font-size: 12px;");
        
        Label errorLabel = new Label();
        errorLabel.setStyle("-fx-text-fill: red; -fx-font-size: 12px;");
        

        Button setupButton = new Button("Setup");
        
        setupButton.setOnAction(a -> {
        	// Retrieve user input
            String userName = userNameField.getText();
            String password = passwordField.getText();
            String code = inviteCodeField.getText();
            
            // Set up user fields and error message string for validity check
            String validUserName = "";
            String validPassword = "";
            String errMsg = "";
            
            // Checks if username satisfies requirements using the UserNAmeRecognizer class
            // If the error message is empty, then it is valid
            errMsg = UserNameRecognizer.checkForValidUserName(userName);	
            if (errMsg != "") {
            	userNameErrorLabel.setText(errMsg);
            } else {
            	validUserName = userName;
            	userNameErrorLabel.setText(null);
            }
            
            // Checks if password satisfies requirements 
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
	            	// Check if the user already exists
	            	if(!databaseHelper.doesUserExist(userName)) {
	            		
	            		// Validate the invitation code
	            		if(databaseHelper.validateInvitationCode(code)) {
	            			
	            			// Create a new user and register them in the database
			            	User user=new User(userName, password, "user");
			                databaseHelper.register(user);
			                
			             // Navigate to the Welcome Login Page
			                new WelcomeLoginPage(databaseHelper).show(primaryStage,user);
	            		}
	            		else {
	            			errorLabel.setText("Please enter a valid invitation code");
	            		}
	            	}
	            	else {
	            		errorLabel.setText("This useruserName is taken!!.. Please use another to setup an account");
	            	}
	            	
	            } catch (SQLException e) {
	                System.err.println("Database error: " + e.getMessage());
	                e.printStackTrace();
	            }
            }
        });

        VBox layout = new VBox(10);
        layout.setStyle("-fx-padding: 20; -fx-alignment: center;");
        layout.getChildren().addAll(userNameField, userNameErrorLabel, passwordField, passwordErrorLabel, inviteCodeField, setupButton, errorLabel);

        primaryStage.setScene(new Scene(layout, 800, 400));
        primaryStage.setTitle("Account Setup");
        primaryStage.show();
    }
}
