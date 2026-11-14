package cu.edu.unah.rest;

import cu.edu.unah.util.DashboardResponse;
import cu.edu.unah.util.JSONUtils;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class RestDashboard {

    private static final HttpClient client = HttpClient.newBuilder().version(HttpClient.Version.HTTP_2).build();
    private static final String serviceURL = "http://localhost:8081/dashboard";

    public DashboardResponse getDashboard() {
        HttpRequest req = HttpRequest.newBuilder(URI.create(serviceURL)).GET().build();
        CompletableFuture<HttpResponse<String>> response = client.sendAsync(req, HttpResponse.BodyHandlers.ofString());
        DashboardResponse dashboard = null;
        try {
            dashboard = JSONUtils.covertFromJsonToObject(response.get().body(), DashboardResponse.class);
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        response.join();
        return dashboard;
    }
}
