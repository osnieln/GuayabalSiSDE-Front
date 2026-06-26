package cu.edu.unah.rest;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import cu.edu.unah.util.ConfiguracionCorreoDTO;
import cu.edu.unah.util.JSONUtils;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public class RestConfiguracionCorreo {

    private static final HttpClient client = HttpClient.newBuilder()
            .version(HttpClient.Version.HTTP_2).build();
    private static final String BASE_URL = "http://localhost:8081/api/config/correo";
    private static final ObjectMapper mapper = new ObjectMapper();

    public ConfiguracionCorreoDTO getConfig() {
        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL))
                .timeout(Duration.ofSeconds(10))
                .GET().build();
        CompletableFuture<HttpResponse<String>> future = client.sendAsync(req, HttpResponse.BodyHandlers.ofString());
        try {
            HttpResponse<String> resp = future.get(10, TimeUnit.SECONDS);
            if (resp.statusCode() == 200) {
                Map<String, Object> map = mapper.readValue(resp.body(),
                        new TypeReference<Map<String, Object>>() {});
                if (map == null) return null;
                ConfiguracionCorreoDTO dto = new ConfiguracionCorreoDTO();
                dto.setHost((String) map.getOrDefault("host", "smtp.gmail.com"));
                Object puerto = map.get("puerto");
                dto.setPuerto(puerto instanceof Number ? ((Number) puerto).intValue() : 587);
                dto.setUsuario((String) map.getOrDefault("usuario", ""));
                dto.setContrasena((String) map.getOrDefault("contrasena", ""));
                dto.setRemitente((String) map.getOrDefault("remitente", ""));
                return dto;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public Map<String, String> saveConfig(ConfiguracionCorreoDTO dto) {
        String body = JSONUtils.covertFromObjectToJson(dto);
        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL))
                .timeout(Duration.ofSeconds(10))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(body)).build();
        CompletableFuture<HttpResponse<String>> future = client.sendAsync(req, HttpResponse.BodyHandlers.ofString());
        try {
            HttpResponse<String> resp = future.get(10, TimeUnit.SECONDS);
            return mapper.readValue(resp.body(), new TypeReference<Map<String, String>>() {});
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public Map<String, String> testConexion(ConfiguracionCorreoDTO dto) {
        String body = JSONUtils.covertFromObjectToJson(dto);
        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/test"))
                .timeout(Duration.ofSeconds(20))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(body)).build();
        CompletableFuture<HttpResponse<String>> future = client.sendAsync(req, HttpResponse.BodyHandlers.ofString());
        try {
            HttpResponse<String> resp = future.get(20, TimeUnit.SECONDS);
            return mapper.readValue(resp.body(), new TypeReference<Map<String, String>>() {});
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
