package com.main;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.Properties;

import javax.swing.JOptionPane;

import org.json.JSONException;
import org.json.JSONObject;

public class Main {

    public static void main(String[] args) throws IOException, InterruptedException {
    	 Properties props = new Properties();
         try (FileInputStream fis = new FileInputStream("config.properties")) {
             props.load(fis);
         }
        String apiUrl = "https://api.openai.com/v1/chat/completions";
        String statement = JOptionPane.showInputDialog(null, "Please ask your AI assistant:");
        String requestBody = "{"
                + "\"model\": \"gpt-3.5-turbo\","
                + "\"messages\": [{\"role\": \"user\", \"content\": \"" + statement + "\"}],"
                + "\"temperature\": 0.7"
                + "}";
       
        String apiKey = props.getProperty("api.key");
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(apiUrl))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + apiKey)
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
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                })
                .join();
    }

}
