package cu.edu.unah.rest;

import com.fasterxml.jackson.core.type.TypeReference;
import cu.edu.unah.util.AlertaResponse;
import cu.edu.unah.util.JSONUtils;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class RestAlertas {

    private static final Duration REQUEST_TIMEOUT = Duration.ofSeconds(10);

    private static final HttpClient client = HttpClient.newBuilder()
            .version(HttpClient.Version.HTTP_2)
            .connectTimeout(Duration.ofSeconds(5))
            .build();
    private static final String serviceURL = "http://localhost:8081/alertas";

    public List<AlertaResponse> findAll(boolean soloNoLeidas) {
        HttpRequest req = HttpRequest.newBuilder(URI.create(
                serviceURL + "?soloNoLeidas=" + soloNoLeidas)).timeout(REQUEST_TIMEOUT).GET().build();
        List<AlertaResponse> list = null;
        try {
            HttpResponse<String> response = client.sendAsync(req, HttpResponse.BodyHandlers.ofString())
                    .get(REQUEST_TIMEOUT.plusSeconds(5).getSeconds(), TimeUnit.SECONDS);
            list = JSONUtils.convertFromJsonToList(response.body(), new TypeReference<List<AlertaResponse>>() {});
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<AlertaResponse> findAll() {
        return findAll(false);
    }

    public boolean marcarLeida(Long id) {
        HttpRequest request = HttpRequest.newBuilder(URI.create(serviceURL + "/" + id + "/leida"))
                .timeout(REQUEST_TIMEOUT)
                .header("Content-Type", "application/json")
                .PUT(HttpRequest.BodyPublishers.noBody()).build();
        try {
            HttpResponse<String> response = client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                    .get(REQUEST_TIMEOUT.plusSeconds(5).getSeconds(), TimeUnit.SECONDS);
            return response.statusCode() < 400;
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean marcarResuelta(Long id) {
        HttpRequest request = HttpRequest.newBuilder(URI.create(serviceURL + "/" + id + "/resuelta"))
                .timeout(REQUEST_TIMEOUT)
                .header("Content-Type", "application/json")
                .PUT(HttpRequest.BodyPublishers.noBody()).build();
        try {
            HttpResponse<String> response = client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                    .get(REQUEST_TIMEOUT.plusSeconds(5).getSeconds(), TimeUnit.SECONDS);
            return response.statusCode() < 400;
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean generar() {
        HttpRequest request = HttpRequest.newBuilder(URI.create(serviceURL + "/generar"))
                .timeout(REQUEST_TIMEOUT)
                .POST(HttpRequest.BodyPublishers.noBody()).build();
        try {
            HttpResponse<String> response = client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                    .get(REQUEST_TIMEOUT.plusSeconds(5).getSeconds(), TimeUnit.SECONDS);
            return response.statusCode() < 400;
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            e.printStackTrace();
        }
        return false;
    }
}
