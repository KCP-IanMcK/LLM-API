package org.example;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Hello! Enter your Prompt: ");
        String prompt = scanner.nextLine();

        System.out.println(fetchResponse(prompt));
    }

    private static String fetchResponse(String prompt) {

        String systemPrompt = ""; //Here you can add your systemPrompt that is sent with every request

        try {
            String urlString = "http://localhost:11434/api/generate"; //The LLM runs on localhost:11434, the endpoint is under /api/generate
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Accept", "application/json");
            connection.setDoOutput(true);

            String inputString = "{\"model\": \"gemma3:4b\", \"prompt\": \"" + prompt + "\", \"stream\": false}"; //Here you have to add the systemPrompt, the prompt and the

            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = inputString.getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            if (connection.getResponseCode() != 200) {
                throw new RuntimeException("Model answered with status code: " + connection.getResponseCode());
            }

            // Read Response
            BufferedReader in = new BufferedReader(new InputStreamReader(
                    connection.getInputStream()));
            String inputLine;
            StringBuilder response = new StringBuilder();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            return extractResponse(String.valueOf(response));

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private static String extractResponse(String answer) {
        String[] list = answer.split("response");
        list = list[1].split("done");
        return list[0].substring(3, list[0].length()-3);
    }
}