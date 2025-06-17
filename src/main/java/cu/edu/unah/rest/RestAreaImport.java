package cu.edu.unah.rest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import cu.edu.unah.util.AreaImportDTO;
import cu.edu.unah.util.GeometryUtil;
import cu.edu.unah.util.JSONUtils;
import org.springframework.web.util.UriUtils;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class RestAreaImport {

    private static final HttpClient client = HttpClient.newBuilder().version(HttpClient.Version.HTTP_2).build();
    private static final String serviceURL = "http://localhost:8050/api/geoDataImporter";

    public List<String> listAreaImport() {
        HttpRequest req = HttpRequest.newBuilder(URI.create(serviceURL+"/tablesName")).GET().build();
        CompletableFuture<HttpResponse<String>> response = client.sendAsync(req, HttpResponse.BodyHandlers.ofString());
        List<String> list_Area = null;
        try {
            list_Area = JSONUtils.convertFromJsonToList(response.get().body(), new
                    TypeReference<List<String>>() {});
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        response.join();
        return list_Area;
    }

    public List<AreaImportDTO> getAreaImport(String area) {
        String encodedArea = UriUtils.encodePath(area, "UTF-8");
        HttpRequest req = HttpRequest.newBuilder(URI.create(serviceURL+"/"+encodedArea)).GET().build();
        CompletableFuture<HttpResponse<String>> response = client.sendAsync(req, HttpResponse.BodyHandlers.ofString());
        List<HashMap<String, String>> mapList = new ArrayList<>();
        List<AreaImportDTO> areaImportDTOList = new ArrayList<>();
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            mapList = objectMapper.readValue(response.get().body(), new TypeReference<List<HashMap<String, String>>>() {});
            mapList.forEach(map -> {
                AreaImportDTO areaImportDTO = AreaImportDTO.builder()
                        .description(map.get("descriptio") == null ? "Area "+ UUID.randomUUID(): map.get("descriptio"))
                        .geometry(GeometryUtil.convertMultiPolygonToPolygon(map.get("geom")))
                        .build();
                areaImportDTOList.add(areaImportDTO);
            });

        } catch (InterruptedException | ExecutionException | JsonProcessingException e) {
            e.printStackTrace();
        }
        response.join();
        return areaImportDTOList;
    }

}
