package luka;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import java.io.IOException;

public class WeatherApp extends Application {
    private TextField cityInput;
    private Label weatherLabel;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Weather App");

        cityInput = new TextField();
        cityInput.setPromptText("Enter city name");

        Button getWeatherButton = new Button("Get Weather");
        getWeatherButton.setOnAction(e -> fetchWeather());

        weatherLabel = new Label();

        VBox layout = new VBox(10);
        layout.setPadding(new Insets(20));
        layout.getChildren().addAll(cityInput, getWeatherButton, weatherLabel);

        Scene scene = new Scene(layout, 300, 200);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void fetchWeather() {
        String city = cityInput.getText();
        if (!city.isEmpty()) {
            String apiKey = "ee9f77ed5ee9649d057dddc9d486c0e4";
            String url = String.format("http://api.openweathermap.org/data/2.5/weather?q=%s&appid=%s", city, apiKey);

            OkHttpClient client = new OkHttpClient();

            Request request = new Request.Builder()
                .url(url)
                .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    e.printStackTrace();
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (response.isSuccessful()) {
                        String responseData = response.body().string();
                        Platform.runLater(() -> updateWeatherLabel(responseData));
                    } else {
                        Platform.runLater(() -> weatherLabel.setText("Error: " + response.code()));
                    }
                }
            });
        } else {
            weatherLabel.setText("Please enter a city name");
        }
    }

    private void updateWeatherLabel(String json) {
        Gson gson = new Gson();
        JsonObject jsonObject = gson.fromJson(json, JsonObject.class);
        String weather = jsonObject.getAsJsonArray("weather")
                                .get(0).getAsJsonObject()
                                .get("description").getAsString();
        double temp = jsonObject.getAsJsonObject("main")
                                .get("temp").getAsDouble() - 273.15; // Convert from Kelvin to Celsius

        weatherLabel.setText(String.format("Weather: %s\nTemperature: %.2f°C", weather, temp));
    }
}
