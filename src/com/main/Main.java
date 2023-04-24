package com.main;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
//import java.io.FileInputStream;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
//import java.util.Properties;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import org.json.JSONException;
import org.json.JSONObject;

public class Main {
	
	public static void getDataFromDatabase() {
	    try {
	        Class.forName("com.mysql.cj.jdbc.Driver");
	        Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/mydb", "root", "");
	        String sql = "SELECT statement, statementresult FROM statements"; 
	        PreparedStatement statement = connection.prepareStatement(sql);
	        ResultSet resultSet = statement.executeQuery();
	        if (!resultSet.isBeforeFirst()) {
	            System.out.println("There is no data in the database.");
	        } else {
	        	 JFrame frame = new JFrame("List of all statements");
	                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	                DefaultTableModel model = new DefaultTableModel();
	                model.addColumn("Statement");
	                model.addColumn("Sesult");
	            while (resultSet.next()) {
	                String statementdb = resultSet.getString("statement"); 
	                String result = resultSet.getString("statementresult"); 
	               
	                model.addRow(new Object[]{statementdb , result});
	                
	            }
	            JTable table = new JTable(model);
                JScrollPane scrollPane = new JScrollPane(table);
                frame.add(scrollPane);
                JButton addStatementButton = new JButton("Add Statement");
                addStatementButton.addActionListener((ActionListener) new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                    	 String apiUrl = "https://api.openai.com/v1/chat/completions";
                         String statement = JOptionPane.showInputDialog(null, "Please ask your AI assistant:");
                         if (statement != null && !statement.isEmpty()) {
                         	String requestBody = "{"
                                     + "\"model\": \"gpt-3.5-turbo\","
                                     + "\"messages\": [{\"role\": \"user\", \"content\": \"" + statement + "\"}],"
                                     + "\"temperature\": 0.7"
                                     + "}";
                            
//                             String apiKey = props.getProperty("api.key");
                             HttpClient client = HttpClient.newHttpClient();
                             HttpRequest request = HttpRequest.newBuilder()
                                     .uri(URI.create(apiUrl))
                                     .header("Content-Type", "application/json")
                                     .header("Authorization", "")
                                     .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                                     .build();

                             client.sendAsync(request, BodyHandlers.ofString())
                                     .thenApply(HttpResponse::body)
                                     .thenAccept(responseBody -> {
                                         System.out.println("Response:");
                                         try {
                                         	 JSONObject json = new JSONObject(responseBody);
                                              String id = json.getString("id"); 
                                              JSONObject firstChoice = json.getJSONArray("choices").getJSONObject(0); 
                                              System.out.println("Id: " + id);
                                              
                                              JSONObject message = firstChoice.getJSONObject("message");
                                              String content = message.getString("content");
                                              System.out.println(content);
                                              try {
                                      			Class.forName("com.mysql.cj.jdbc.Driver");
                                      			Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/mydb", "root", "");
                                      			String sql = "INSERT INTO statements (statement, statementresult) VALUES (?, ?)";
                                      			PreparedStatement st = connection.prepareStatement(sql);
                                      			st.setString(1, statement);
                                      			st.setString(2, content);
                                      			st.executeUpdate();
                                      			st.close();
                                      			connection.close();
                                      			System.out.println("The result saved to the database successfully!");
                                      		} catch (ClassNotFoundException | SQLException e1) {
                                      			e1.printStackTrace();
                                      		}
                                         } catch (JSONException e1) {
                                             e1.printStackTrace();
                                         }
                                     })
                                     .join();
                         } else {
                             System.out.println("Error: Statement cannot be empty or null.");
                         }
                    }
                });
                frame.add(addStatementButton, BorderLayout.PAGE_END); // Add add statement button to the bottom of the frame

                frame.setSize(400, 300);
                frame.setVisible(true);
	        }
	        resultSet.close();
	        statement.close();
	        connection.close();
	    } catch (ClassNotFoundException | SQLException e) {
	        e.printStackTrace();
	    }
	}

	
	
	
	
	

    public static void main(String[] args) throws IOException, InterruptedException {
//    	 Properties props = new Properties();
//         try (FileInputStream fis = new FileInputStream("config.properties")) {
//             props.load(fis);
//         }
    	
    	
    	
    	getDataFromDatabase();

    
    }
}
