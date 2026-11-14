package cu.edu.unah.rest;

import com.fasterxml.jackson.core.type.TypeReference;
import cu.edu.unah.util.JSONUtils;
import cu.edu.unah.util.TrabajadorResponse;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class RestTrabajador {

    private static final HttpClient client = HttpClient.newBuilder().version(HttpClient.Version.HTTP_2).build();
    private static final String serviceURL = "http://localhost:8081/trabajador";

    public List<TrabajadorResponse> findAllTrabajador() {
        HttpRequest req = HttpRequest.newBuilder(URI.create(serviceURL)).GET().build();
        CompletableFuture<HttpResponse<String>> response = client.sendAsync(req, HttpResponse.BodyHandlers.ofString());
        List<TrabajadorResponse> list = null;
        try {
            list = JSONUtils.convertFromJsonToList(response.get().body(), new
                    TypeReference<List<TrabajadorResponse>>() {});
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        response.join();
        return list;
    }

    public TrabajadorResponse findById(Long id) {
        TrabajadorResponse trabajador = null;
        HttpRequest req = HttpRequest.newBuilder(URI.create(serviceURL+"/findById/"+id)).GET().build();
        CompletableFuture<HttpResponse<String>> response = client.sendAsync(req, HttpResponse.BodyHandlers.ofString());
        try {
            if(response.get().statusCode() == 500){
                response.join();
                return null;
            }else {
                try {
                    trabajador = JSONUtils.covertFromJsonToObject(response.get().body(), TrabajadorResponse.class);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
                response.join();
                return trabajador;
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return trabajador;
    }

    public boolean create(TrabajadorResponse trabajadorResponse){
        String inputJson = JSONUtils.covertFromObjectToJson(trabajadorResponse);
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

    public boolean update(TrabajadorResponse trabajadorResponse){
        String inputJson = JSONUtils.covertFromObjectToJson(trabajadorResponse);
        HttpRequest request = HttpRequest.newBuilder(URI.create(serviceURL+"/edit"))
                .header("Content-Type", "application/json")
                .PUT(HttpRequest.BodyPublishers.ofString(inputJson)).build();
        CompletableFuture<HttpResponse<String>> response = client.sendAsync(request,HttpResponse.BodyHandlers.ofString());
        try {
            if(response.get().statusCode() >= 400){
                response.join();
                return false;
            } else {
                trabajadorResponse = JSONUtils.covertFromJsonToObject(response.get().body(), TrabajadorResponse.class);
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
                TrabajadorResponse trabajadorResponse = JSONUtils.covertFromJsonToObject(response.get().body(), TrabajadorResponse.class);
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
