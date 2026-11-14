package cu.edu.unah.rest;

import com.fasterxml.jackson.core.type.TypeReference;
import cu.edu.unah.entity.Agroquimico;
import cu.edu.unah.util.AgroquimicoResponse;
import cu.edu.unah.util.JSONUtils;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class RestAgroquimico {

    private static final HttpClient client = HttpClient.newBuilder().version(HttpClient.Version.HTTP_2).build();
    private static final String serviceURL = "http://localhost:8081/agroquimico";

    public List<AgroquimicoResponse> findAllAgroquimico() {
        HttpRequest req = HttpRequest.newBuilder(URI.create(serviceURL)).GET().build();
        CompletableFuture<HttpResponse<String>> response = client.sendAsync(req, HttpResponse.BodyHandlers.ofString());
        List<AgroquimicoResponse> list_Area = null;
        try {
            list_Area = JSONUtils.convertFromJsonToList(response.get().body(), new
                    TypeReference<List<AgroquimicoResponse>>() {});
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        response.join();
        return list_Area;
    }

    public AgroquimicoResponse findById(Long id) {
        AgroquimicoResponse agroquimico = null;
        HttpRequest req = HttpRequest.newBuilder(URI.create(serviceURL+"/findById/"+id)).GET().build();
        CompletableFuture<HttpResponse<String>> response = client.sendAsync(req, HttpResponse.BodyHandlers.ofString());
        try {
            if(response.get().statusCode() == 500){
                response.join();
                return null;
            }else {

                try {
                    agroquimico = JSONUtils.covertFromJsonToObject(response.get().body(), AgroquimicoResponse.class);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
                response.join();
                return agroquimico;
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return agroquimico;
    }

    public boolean create(AgroquimicoResponse agroquimicoResponse){
        String inputJson = null;
        inputJson = JSONUtils.covertFromObjectToJson(agroquimicoResponse);
        HttpRequest request = HttpRequest.newBuilder(URI.create(serviceURL+"/create"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(inputJson)).build();
        CompletableFuture<HttpResponse<String>> response = client.sendAsync(request,HttpResponse.BodyHandlers.ofString());
        try {
            if(response.get().statusCode() >= 400){
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

    public boolean update(AgroquimicoResponse agroquimicoResponse){
        String inputJson= null;
        inputJson = JSONUtils.covertFromObjectToJson(agroquimicoResponse);
        HttpRequest request = HttpRequest.newBuilder(URI.create(serviceURL+"/edit"))
                .header("Content-Type", "application/json")
                .PUT(HttpRequest.BodyPublishers.ofString(inputJson)).build();
        CompletableFuture<HttpResponse<String>> response = client.sendAsync(request,HttpResponse.BodyHandlers.ofString());
        try {
            if(response.get().statusCode() >= 400){
                response.join();
                return false;
            } else {
                agroquimicoResponse = JSONUtils.covertFromJsonToObject(response.get().body(), AgroquimicoResponse.class);
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

    public boolean delete(long id) {
        HttpRequest request = HttpRequest.newBuilder(URI.create(serviceURL+"/delete/"+id)).DELETE().build();
        CompletableFuture<HttpResponse<String>> response = client.sendAsync(request,HttpResponse.BodyHandlers.ofString());
        try {
            if(response.get().statusCode() >= 400) {
                response.join();
                return false;
            } else {
                AgroquimicoResponse agroquimicoResponse = JSONUtils.covertFromJsonToObject(response.get().body(), AgroquimicoResponse.class);
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

}
