package cu.edu.unah.rest;

import com.fasterxml.jackson.core.type.TypeReference;
import cu.edu.unah.entity.Cultivo;
import cu.edu.unah.entity.Produccion;
import cu.edu.unah.util.JSONUtils;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class RestCultivo {

    private static final HttpClient client = HttpClient.newBuilder().version(HttpClient.Version.HTTP_2).build();
    private static final String serviceURL = "http://localhost:8081/cultivo";

    //sending request to retrieve all Cultivo available.
    public List<Cultivo> findAllCultivo() {
        HttpRequest req = HttpRequest.newBuilder(URI.create(serviceURL)).GET().build();
        CompletableFuture<HttpResponse<String>> response = client.sendAsync(req, HttpResponse.BodyHandlers.ofString());
        List<Cultivo> list_Cultivo = null;
        try {
            list_Cultivo = JSONUtils.convertFromJsonToList(response.get().body(), new
                    TypeReference<List<Cultivo>>() {});
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        response.join();
        return list_Cultivo;
    }

    //sending request retrieve the cultivo based on the cultivoname
    public Cultivo findById(Long id) {
        Cultivo cultivo = null;
        HttpRequest req = HttpRequest.newBuilder(URI.create(serviceURL+"/findById/"+id)).GET().build();
        CompletableFuture<HttpResponse<String>> response = client.sendAsync(req, HttpResponse.BodyHandlers.ofString());
        try {
            if(response.get().statusCode() == 500){
                response.join();
                return null;
            }else {

                try {
                    cultivo = JSONUtils.covertFromJsonToObject(response.get().body(), Cultivo.class);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
                response.join();
                return cultivo;
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return cultivo;
    }

    //sending request retrieve the cultivo based on the cultivoname
    public Cultivo findByProduccion(Long id) {
        Cultivo cultivo = null;
        HttpRequest req = HttpRequest.newBuilder(URI.create(serviceURL+"/findByProduccion/"+id)).GET().build();
        CompletableFuture<HttpResponse<String>> response = client.sendAsync(req, HttpResponse.BodyHandlers.ofString());
        try {
            if(response.get().statusCode() == 500){
                response.join();
                return null;
            }else {

                try {
                    cultivo = JSONUtils.covertFromJsonToObject(response.get().body(), Cultivo.class);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
                response.join();
                return cultivo;
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return cultivo;
    }

    //send request to add the product details.
    public boolean create(Cultivo cultivo){
        String inputJson = null;
        inputJson = JSONUtils.covertFromObjectToJson(cultivo);
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

    //send request to update a cultivo details.
    public boolean update(Cultivo cultivo){
        String inputJson= null;
        inputJson = JSONUtils.covertFromObjectToJson(cultivo);
        HttpRequest request = HttpRequest.newBuilder(URI.create(serviceURL+"/edit"))
                .header("Content-Type", "application/json")
                .PUT(HttpRequest.BodyPublishers.ofString(inputJson)).build();
        CompletableFuture<HttpResponse<String>> response = client.sendAsync(request,HttpResponse.BodyHandlers.ofString());
        try {
            if(response.get().statusCode() == 500){
                response.join();
                return false;
            } else {
                cultivo = JSONUtils.covertFromJsonToObject(response.get().body(), Cultivo.class);
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

    //send request to delete the cultivo by its cultivoname
    public boolean delete(long id) {
        HttpRequest request = HttpRequest.newBuilder(URI.create(serviceURL+"/delete/"+id)).DELETE().build();
        CompletableFuture<HttpResponse<String>> response = client.sendAsync(request,HttpResponse.BodyHandlers.ofString());
        try {
            if(response.get().statusCode() == 500) {
                response.join();
                return false;
            } else {
                Cultivo cultivo = JSONUtils.covertFromJsonToObject(response.get().body(), Cultivo.class);
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

    public List<Cultivo> searchByDescripcion(String texto) {
        HttpRequest req = HttpRequest.newBuilder(URI.create(serviceURL+"/search/"+texto)).GET().build();
        CompletableFuture<HttpResponse<String>> response = client.sendAsync(req, HttpResponse.BodyHandlers.ofString());
        List<Cultivo> list = null;
        try {
            list = JSONUtils.convertFromJsonToList(response.get().body(), new TypeReference<List<Cultivo>>() {});
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        response.join();
        return list;
    }

}
