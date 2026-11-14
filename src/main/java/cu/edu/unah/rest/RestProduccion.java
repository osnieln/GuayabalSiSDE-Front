package cu.edu.unah.rest;

import com.fasterxml.jackson.core.type.TypeReference;
import cu.edu.unah.entity.Produccion;
import cu.edu.unah.util.JSONUtils;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class RestProduccion {

    private static final HttpClient client = HttpClient.newBuilder().version(HttpClient.Version.HTTP_2).build();
    private static final String serviceURL = "http://localhost:8081/produccion";

    //sending request to retrieve all Produccion available.
    public List<Produccion> findAllProduccion() {
        HttpRequest req = HttpRequest.newBuilder(URI.create(serviceURL)).GET().build();
        CompletableFuture<HttpResponse<String>> response = client.sendAsync(req, HttpResponse.BodyHandlers.ofString());
        List<Produccion> list_Produccion = null;
        try {
            list_Produccion = JSONUtils.convertFromJsonToList(response.get().body(), new
                    TypeReference<List<Produccion>>() {});
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        response.join();
        return list_Produccion;
    }

    //sending request retrieve the produccion based on the produccionname
    public Produccion findById(Long id) {
        Produccion produccion = null;
        HttpRequest req = HttpRequest.newBuilder(URI.create(serviceURL+"/findById/"+id)).GET().build();
        CompletableFuture<HttpResponse<String>> response = client.sendAsync(req, HttpResponse.BodyHandlers.ofString());
        try {
            if(response.get().statusCode() == 500){
                response.join();
                return null;
            }else {

                try {
                    produccion = JSONUtils.covertFromJsonToObject(response.get().body(), Produccion.class);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
                response.join();
                return produccion;
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return produccion;
    }

    //send request to add the product details.
    public boolean create(Produccion produccion){
        String inputJson = null;
        inputJson = JSONUtils.covertFromObjectToJson(produccion);
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

    //send request to update a produccion details.
    public boolean update(Produccion produccion){
        String inputJson= null;
        inputJson = JSONUtils.covertFromObjectToJson(produccion);
        HttpRequest request = HttpRequest.newBuilder(URI.create(serviceURL+"/edit"))
                .header("Content-Type", "application/json")
                .PUT(HttpRequest.BodyPublishers.ofString(inputJson)).build();
        CompletableFuture<HttpResponse<String>> response = client.sendAsync(request,HttpResponse.BodyHandlers.ofString());
        try {
            if(response.get().statusCode() >= 400){
                response.join();
                return false;
            } else {
                produccion = JSONUtils.covertFromJsonToObject(response.get().body(), Produccion.class);
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

    //send request to delete the produccion by its produccionname
    public boolean delete(long id) {
        HttpRequest request = HttpRequest.newBuilder(URI.create(serviceURL+"/delete/"+id)).DELETE().build();
        CompletableFuture<HttpResponse<String>> response = client.sendAsync(request,HttpResponse.BodyHandlers.ofString());
        try {
            if(response.get().statusCode() >= 400) {
                response.join();
                return false;
            } else {
                Produccion produccion = JSONUtils.covertFromJsonToObject(response.get().body(), Produccion.class);
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
