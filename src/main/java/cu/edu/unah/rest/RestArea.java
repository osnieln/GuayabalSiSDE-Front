package cu.edu.unah.rest;

import com.fasterxml.jackson.core.type.TypeReference;
import cu.edu.unah.entity.Area;
import cu.edu.unah.util.AreaResponse;
import cu.edu.unah.util.JSONUtils;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class RestArea {

    private static final HttpClient client = HttpClient.newBuilder().version(HttpClient.Version.HTTP_2).build();
    private static final String serviceURL = "http://localhost:8081/area";

    //sending request to retrieve all Area available.
    public List<AreaResponse> findAllArea() {
        HttpRequest req = HttpRequest.newBuilder(URI.create(serviceURL)).GET().build();
        CompletableFuture<HttpResponse<String>> response = client.sendAsync(req, HttpResponse.BodyHandlers.ofString());
        List<AreaResponse> list_Area = null;
        try {
            list_Area = JSONUtils.convertFromJsonToList(response.get().body(), new
                    TypeReference<List<AreaResponse>>() {});
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        response.join();
        return list_Area;
    }

    public List<String> findDistinctCapa() {
        HttpRequest req = HttpRequest.newBuilder(URI.create(serviceURL+"/findDistinctCapa")).GET().build();
        CompletableFuture<HttpResponse<String>> response = client.sendAsync(req, HttpResponse.BodyHandlers.ofString());
        List<String> capaList = null;
        try {
            capaList = JSONUtils.convertFromJsonToList(response.get().body(), new
                    TypeReference<List<String>>() {});
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        response.join();
        return capaList;
    }

    //sending request retrieve the area based on the areaname
    public AreaResponse findById(Long id) {
        AreaResponse area = null;
        HttpRequest req = HttpRequest.newBuilder(URI.create(serviceURL+"/findById/"+id)).GET().build();
        CompletableFuture<HttpResponse<String>> response = client.sendAsync(req, HttpResponse.BodyHandlers.ofString());
        try {
            if(response.get().statusCode() == 500){
                response.join();
                return null;
            }else {

                try {
                    area = JSONUtils.covertFromJsonToObject(response.get().body(), AreaResponse.class);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
                response.join();
                return area;
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return area;
    }

    //send request to add the product details.
    public boolean create(AreaResponse area){
        String inputJson = null;
        inputJson = JSONUtils.covertFromObjectToJson(area);
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

    //send request to update a area details.
    public boolean update(AreaResponse area){
        String inputJson= null;
        inputJson = JSONUtils.covertFromObjectToJson(area);
        HttpRequest request = HttpRequest.newBuilder(URI.create(serviceURL+"/edit"))
                .header("Content-Type", "application/json")
                .PUT(HttpRequest.BodyPublishers.ofString(inputJson)).build();
        CompletableFuture<HttpResponse<String>> response = client.sendAsync(request,HttpResponse.BodyHandlers.ofString());
        try {
            if(response.get().statusCode() == 500){
                response.join();
                return false;
            } else {
                area = JSONUtils.covertFromJsonToObject(response.get().body(), AreaResponse.class);
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

    //send request to delete the area by its areaname
    public boolean delete(long id) {
        HttpRequest request = HttpRequest.newBuilder(URI.create(serviceURL+"/delete/"+id)).DELETE().build();
        CompletableFuture<HttpResponse<String>> response = client.sendAsync(request,HttpResponse.BodyHandlers.ofString());
        try {
            if(response.get().statusCode() == 500) {
                response.join();
                return false;
            } else {
                AreaResponse area = JSONUtils.covertFromJsonToObject(response.get().body(), AreaResponse.class);
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

    public List<AreaResponse> findByCapa(String capa) {
        HttpRequest req = HttpRequest.newBuilder(URI.create(serviceURL+"/findByCapa/"+capa)).GET().build();
        CompletableFuture<HttpResponse<String>> response = client.sendAsync(req, HttpResponse.BodyHandlers.ofString());
        List<AreaResponse> list = null;
        try {
            list = JSONUtils.convertFromJsonToList(response.get().body(), new TypeReference<List<AreaResponse>>() {});
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        response.join();
        return list;
    }

}
