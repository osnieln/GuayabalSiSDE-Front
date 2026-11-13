package cu.edu.unah.rest;

import com.fasterxml.jackson.core.type.TypeReference;
import cu.edu.unah.util.AreaCultivoResponsePK;
import cu.edu.unah.util.CompletarCosechaRequest;
import cu.edu.unah.util.JSONUtils;
import cu.edu.unah.util.PlanificacionCosechaResponse;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class RestPlanificacionCosecha {

    private static final Duration REQUEST_TIMEOUT = Duration.ofSeconds(10);

    private static final HttpClient client = HttpClient.newBuilder()
            .version(HttpClient.Version.HTTP_2)
            .connectTimeout(Duration.ofSeconds(5))
            .build();
    private static final String serviceURL = "http://localhost:8081/planificacionCosecha";

    public List<PlanificacionCosechaResponse> findCalendario(String desde, String hasta) {
        HttpRequest req = HttpRequest.newBuilder(URI.create(serviceURL + "/calendario/" + desde + "/" + hasta))
                .timeout(REQUEST_TIMEOUT).GET().build();
        List<PlanificacionCosechaResponse> list = null;
        try {
            HttpResponse<String> response = client.sendAsync(req, HttpResponse.BodyHandlers.ofString())
                    .get(REQUEST_TIMEOUT.plusSeconds(5).getSeconds(), TimeUnit.SECONDS);
            list = JSONUtils.convertFromJsonToList(response.body(), new TypeReference<List<PlanificacionCosechaResponse>>() {});
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            e.printStackTrace();
        }
        return list;
    }

    public boolean completar(AreaCultivoResponsePK pk, Double produccionReal) {
        CompletarCosechaRequest request = CompletarCosechaRequest.builder()
                .areaCultivoResponsePk(pk)
                .produccionReal(produccionReal)
                .build();
        String inputJson = JSONUtils.covertFromObjectToJson(request);
        HttpRequest httpRequest = HttpRequest.newBuilder(URI.create(serviceURL + "/completar"))
                .timeout(REQUEST_TIMEOUT)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(inputJson)).build();
        try {
            HttpResponse<String> response = client.sendAsync(httpRequest, HttpResponse.BodyHandlers.ofString())
                    .get(REQUEST_TIMEOUT.plusSeconds(5).getSeconds(), TimeUnit.SECONDS);
            return response.statusCode() < 400;
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean cancelar(AreaCultivoResponsePK pk) {
        String inputJson = JSONUtils.covertFromObjectToJson(pk);
        HttpRequest httpRequest = HttpRequest.newBuilder(URI.create(serviceURL + "/cancelar"))
                .timeout(REQUEST_TIMEOUT)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(inputJson)).build();
        try {
            HttpResponse<String> response = client.sendAsync(httpRequest, HttpResponse.BodyHandlers.ofString())
                    .get(REQUEST_TIMEOUT.plusSeconds(5).getSeconds(), TimeUnit.SECONDS);
            return response.statusCode() < 400;
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            e.printStackTrace();
        }
        return false;
    }
}
