package com.main;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import javax.swing.JOptionPane;

import org.json.JSONException;
import org.json.JSONObject;

public class Main {

    public static void main(String[] args) throws IOException, InterruptedException {

        String apiUrl = "https://api.openai.com/v1/chat/completions";
        String statement = JOptionPane.showInputDialog(null, "Please ask your AI assistant:");
        String requestBody = "{"
                + "\"model\": \"gpt-3.5-turbo\","
                + "\"messages\": [{\"role\": \"user\", \"content\": \"" + statement + "\"}],"
                + "\"temperature\": 0.7"
                + "}";

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(apiUrl))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer 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")
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
