package testUtils;


import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class RequestsSender {

    private static HttpClient client = HttpClient.newHttpClient();

    public static HttpResponse<String> sendGetRequest(String path) throws IOException, InterruptedException {
        URI url = URI.create(path);
        HttpRequest request = HttpRequest
                .newBuilder()
                .GET()
                .uri(url)
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        return response;
    }

    public static HttpResponse<String> sendPostRequest(String path, String jsonToPost) throws IOException, InterruptedException {
        URI url = URI.create(path);
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(jsonToPost)).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        return response;

    }

    public static HttpResponse<String> sendDeleteRequest(String path) throws IOException, InterruptedException {
        URI url = URI.create(path);
        HttpRequest request = HttpRequest
                .newBuilder()
                .DELETE()
                .uri(url)
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        return response;
    }
}
