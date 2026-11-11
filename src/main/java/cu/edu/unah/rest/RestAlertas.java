package cu.edu.unah.rest;

import com.fasterxml.jackson.core.type.TypeReference;
import cu.edu.unah.util.AlertaResponse;
import cu.edu.unah.util.JSONUtils;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class RestAlertas {

    private static final HttpClient client = HttpClient.newBuilder().version(HttpClient.Version.HTTP_2).build();
    private static final String serviceURL = "http://localhost:8081/alertas";

    public List<AlertaResponse> findAll(boolean soloNoLeidas) {
        HttpRequest req = HttpRequest.newBuilder(URI.create(
                serviceURL + "?soloNoLeidas=" + soloNoLeidas)).GET().build();
        CompletableFuture<HttpResponse<String>> response = client.sendAsync(req, HttpResponse.BodyHandlers.ofString());
        List<AlertaResponse> list = null;
        try {
            list = JSONUtils.convertFromJsonToList(response.get().body(), new TypeReference<List<AlertaResponse>>() {});
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<AlertaResponse> findAll() {
        return findAll(false);
    }

    public boolean marcarLeida(Long id) {
        HttpRequest request = HttpRequest.newBuilder(URI.create(serviceURL + "/" + id + "/leida"))
                .header("Content-Type", "application/json")
                .PUT(HttpRequest.BodyPublishers.noBody()).build();
        CompletableFuture<HttpResponse<String>> response = client.sendAsync(request, HttpResponse.BodyHandlers.ofString());
        try {
            return response.get().statusCode() < 400;
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean marcarResuelta(Long id) {
        HttpRequest request = HttpRequest.newBuilder(URI.create(serviceURL + "/" + id + "/resuelta"))
                .header("Content-Type", "application/json")
                .PUT(HttpRequest.BodyPublishers.noBody()).build();
        CompletableFuture<HttpResponse<String>> response = client.sendAsync(request, HttpResponse.BodyHandlers.ofString());
        try {
            return response.get().statusCode() < 400;
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean generar() {
        HttpRequest request = HttpRequest.newBuilder(URI.create(serviceURL + "/generar"))
                .POST(HttpRequest.BodyPublishers.noBody()).build();
        CompletableFuture<HttpResponse<String>> response = client.sendAsync(request, HttpResponse.BodyHandlers.ofString());
        try {
            return response.get().statusCode() < 400;
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return false;
    }
}
