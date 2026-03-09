package cu.edu.unah.rest;

import com.fasterxml.jackson.core.type.TypeReference;
import cu.edu.unah.util.AreaCultivoResponse;
import cu.edu.unah.util.AreaCultivoResponsePK;
import cu.edu.unah.util.JSONUtils;
import cu.edu.unah.util.RendimientoResponse;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class RestAreaCultivo {

    private static final HttpClient client = HttpClient.newBuilder().version(HttpClient.Version.HTTP_2).build();
    private static final String serviceURL = "http://localhost:8081/areaCultivo";

    //sending request to retrieve all AreaCultivo available.
    public List<AreaCultivoResponse> findAllAreaCultivo() {
        HttpRequest req = HttpRequest.newBuilder(URI.create(serviceURL)).GET().build();
        CompletableFuture<HttpResponse<String>> response = client.sendAsync(req, HttpResponse.BodyHandlers.ofString());
        List<AreaCultivoResponse> list_AreaCultivo = null;
        try {
            list_AreaCultivo = JSONUtils.convertFromJsonToList(response.get().body(), new
                    TypeReference<List<AreaCultivoResponse>>() {});
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        response.join();
        return list_AreaCultivo;
    }

    //sending request retrieve the areaCultivo based on the areaCultivoname
    public AreaCultivoResponse findById(AreaCultivoResponsePK areaCultivoResponsePK) {
        AreaCultivoResponse areaCultivo = null;
        String inputJson = null;
        inputJson = JSONUtils.covertFromObjectToJson(areaCultivoResponsePK);
        HttpRequest req = HttpRequest.newBuilder(URI.create(serviceURL+"/findById"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(inputJson)).build();
        CompletableFuture<HttpResponse<String>> response = client.sendAsync(req, HttpResponse.BodyHandlers.ofString());
        try {
            if(response.get().statusCode() == 500){
                response.join();
                return null;
            }else {

                try {
                    areaCultivo = JSONUtils.covertFromJsonToObject(response.get().body(), AreaCultivoResponse.class);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
                response.join();
                return areaCultivo;
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return areaCultivo;
    }

    //send request to add the product details.
    public boolean create(AreaCultivoResponse areaCultivo){
        String inputJson = null;
        inputJson = JSONUtils.covertFromObjectToJson(areaCultivo);
        HttpRequest request = HttpRequest.newBuilder(URI.create(serviceURL+"/create"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(inputJson)).build();
        CompletableFuture<HttpResponse<String>> response = client.sendAsync(request,HttpResponse.BodyHandlers.ofString());
        try {
            if(response.get().statusCode() == 500){
                return false;
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
            return false;
        } catch (ExecutionException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    //send request to update a areaCultivo details.
    public boolean update(AreaCultivoResponse areaCultivo){
        String inputJson= null;
        inputJson = JSONUtils.covertFromObjectToJson(areaCultivo);
        HttpRequest request = HttpRequest.newBuilder(URI.create(serviceURL+"/edit"))
                .header("Content-Type", "application/json")
                .PUT(HttpRequest.BodyPublishers.ofString(inputJson)).build();
        CompletableFuture<HttpResponse<String>> response = client.sendAsync(request,HttpResponse.BodyHandlers.ofString());
        try {
            if(response.get().statusCode() == 500){
                response.join();
                return false;
            } else {
                areaCultivo = JSONUtils.covertFromJsonToObject(response.get().body(), AreaCultivoResponse.class);
                response.join();
                return true;
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return false;
    }

    //send request to delete the areaCultivo by its areaCultivoname
    public boolean delete(AreaCultivoResponsePK areaCultivoResponsePK) {
        String inputJson = null;
        inputJson = JSONUtils.covertFromObjectToJson(areaCultivoResponsePK);
        AreaCultivoResponse areaCultivoResponse = null;
        HttpRequest request = HttpRequest.newBuilder(URI.create(serviceURL+"/delete"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(inputJson)).build();
        CompletableFuture<HttpResponse<String>> response = client.sendAsync(request,HttpResponse.BodyHandlers.ofString());
        try {
            if(response.get().statusCode() == 500) {
                response.join();
                return false;
            } else {
                areaCultivoResponse = JSONUtils.covertFromJsonToObject(response.get().body(), AreaCultivoResponse.class);
                response.join();
                return true;
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return false;
    }

    public List<AreaCultivoResponse> findByActivo(boolean activo) {
        HttpRequest req = HttpRequest.newBuilder(URI.create(serviceURL+"/findByActivo/"+activo)).GET().build();
        CompletableFuture<HttpResponse<String>> response = client.sendAsync(req, HttpResponse.BodyHandlers.ofString());
        List<AreaCultivoResponse> list = null;
        try {
            list = JSONUtils.convertFromJsonToList(response.get().body(), new TypeReference<List<AreaCultivoResponse>>() {});
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        response.join();
        return list;
    }

    public List<RendimientoResponse> getRendimiento() {
        HttpRequest req = HttpRequest.newBuilder(URI.create(serviceURL+"/rendimiento")).GET().build();
        CompletableFuture<HttpResponse<String>> response = client.sendAsync(req, HttpResponse.BodyHandlers.ofString());
        List<RendimientoResponse> list = null;
        try {
            list = JSONUtils.convertFromJsonToList(response.get().body(), new TypeReference<List<RendimientoResponse>>() {});
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        response.join();
        return list;
    }

}
