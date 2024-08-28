package DestinationHashGenerator;

import org.json.JSONObject;
import org.json.JSONArray;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.util.Random;

public class DestinationHashGenerator {
    public static void main(String[] args) {
        if (args.length != 2) {
            System.out.println("Usage: java -jar DestinationHashGenerator.jar 240345920035 test.json");
            return;
        }

        String prn = args[0].toLowerCase();
        String jsonFilePath = args[1];

        try {
            String jsonContent = new String(Files.readAllBytes(Paths.get(jsonFilePath)));
            JSONObject jsonObject = new JSONObject(jsonContent);

            String destination = findDestination(jsonObject);
            if (destination == null) {
                System.out.println("The key 'destination' was not found in the JSON file.");
                return;
            }

            String randomString = generateRandomString(8);
            String input = prn + destination + randomString;
            String hash = generateMD5Hash(input);

            System.out.println(hash + ";" + randomString);
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static String findDestination(JSONObject jsonObject) {
        for (String key : jsonObject.keySet()) {
            Object value = jsonObject.get(key);
            if (key.equals("destination")) {
                return value.toString();
            } else if (value instanceof JSONObject) {
                String result = findDestination((JSONObject) value);
                if (result != null) return result;
            }
        }
        return null;
    }

    private static String generateRandomString(int length) {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        Random random = new Random();
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(characters.charAt(random.nextInt(characters.length())));
        }
        return sb.toString();
    }

    private static String generateMD5Hash(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] digest = md.digest(input.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : digest) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
