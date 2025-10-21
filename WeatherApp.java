// File: WeatherApp.java
// A simple Java weather app using Open-Meteo API (no API key needed)

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import org.json.*; // you'll need org.json library (explained below)
import java.util.Scanner;

public class WeatherApp {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        System.out.print("Enter city name: ");
        String city = sc.nextLine();

        try {
            // Step 1: Get latitude & longitude from geocoding API
            String geoUrl = "https://geocoding-api.open-meteo.com/v1/search?name=" + city.replace(" ", "%20");
            JSONObject geoData = readJsonFromUrl(geoUrl);

            JSONArray results = geoData.getJSONArray("results");
            if (results.length() == 0) {
                System.out.println("City not found!");
                return;
            }

            JSONObject location = results.getJSONObject(0);
            double lat = location.getDouble("latitude");
            double lon = location.getDouble("longitude");
            String name = location.getString("name");

            // Step 2: Fetch weather data
            String weatherUrl = "https://api.open-meteo.com/v1/forecast?latitude=" + lat + "&longitude=" + lon + "&current_weather=true";
            JSONObject weatherData = readJsonFromUrl(weatherUrl);
            JSONObject currentWeather = weatherData.getJSONObject("current_weather");

            double temperature = currentWeather.getDouble("temperature");
            double windspeed = currentWeather.getDouble("windspeed");

            // Step 3: Print results
            System.out.println("\n🌤 Weather in " + name + ":");
            System.out.println("Temperature: " + temperature + "°C");
            System.out.println("Wind Speed: " + windspeed + " km/h");

        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static JSONObject readJsonFromUrl(String urlString) throws Exception {
        URL url = new URL(urlString);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.connect();

        BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = br.readLine()) != null) {
            sb.append(line);
        }
        br.close();
        return new JSONObject(sb.toString());
    }
}
