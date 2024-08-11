package cu.edu.unah.rest;

import com.fasterxml.jackson.core.type.TypeReference;
import cu.edu.unah.entity.Authorities;
import cu.edu.unah.entity.AuthoritiesPK;
import cu.edu.unah.util.JSONUtils;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class RestAuthorities {
    private static final HttpClient client = HttpClient.newBuilder().version(HttpClient.Version.HTTP_2).build();
    private static final String serviceURL = "http://localhost:8081/Authorities/";

    //sending request to retrieve all Authorities available.
    public List<Authorities> findAllAuthorities() {
        HttpRequest req = HttpRequest.newBuilder(URI.create(serviceURL+"findAll")).GET().build();
        CompletableFuture<HttpResponse<String>> response = client.sendAsync(req, HttpResponse.BodyHandlers.ofString());
        List<Authorities> list_Authorities = null;
        try {
            list_Authorities = JSONUtils.convertFromJsonToList(response.get().body(), new
                    TypeReference<List<Authorities>>() {});
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        response.join();
        return list_Authorities;
    }

    //sending request retrieve the authorities by id
    public List<Authorities> findAuthorityByUsername(String username)
    {
        HttpRequest req = HttpRequest.newBuilder(URI.create(serviceURL+"findByUsername/"+username)).GET().build();
        CompletableFuture<HttpResponse<String>> response = client.sendAsync(req, HttpResponse.BodyHandlers.ofString());
        try {
            if(response.get().statusCode() == 500){
                response.join();
                return null;
            }else {
                List<Authorities> list_Authorities = null;
                try {
                    list_Authorities = JSONUtils.convertFromJsonToList(response.get().body(), new
                            TypeReference<List<Authorities>>() {});
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
                response.join();
                return list_Authorities;
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return null;
    }

    //send request to add the product details.
    public boolean createAuthority(Authorities authority){
        String inputJson = null;
        inputJson = JSONUtils.covertFromObjectToJson(authority);
        HttpRequest request = HttpRequest.newBuilder(URI.create(serviceURL+"create"))
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

    //send request to update a Authority details.
    public boolean updateAuthority(Authorities authority){
        String inputJson= null;
        inputJson = JSONUtils.covertFromObjectToJson(authority);
        HttpRequest request = HttpRequest.newBuilder(URI.create(serviceURL+"update"))
                .header("Content-Type", "application/json")
                .PUT(HttpRequest.BodyPublishers.ofString(inputJson)).build();
        CompletableFuture<HttpResponse<String>> response = client.sendAsync(request,HttpResponse.BodyHandlers.ofString());
        try {
            if(response.get().statusCode() == 500){
                response.join();
                return false;
            } else {
                authority = JSONUtils.covertFromJsonToObject(response.get().body(), Authorities.class);
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

    //send request to delete the Authority by its Authorityname
    public boolean deleteAuthority(AuthoritiesPK authoritiesPK)
    {
        String inputJson= null;
        inputJson = JSONUtils.covertFromObjectToJson(authoritiesPK);
        HttpRequest request = HttpRequest.newBuilder(URI.create(serviceURL+"delete"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(inputJson)).build();
        CompletableFuture<HttpResponse<String>> response = client.sendAsync(request,HttpResponse.BodyHandlers.ofString());
        try {
            if(response.get().statusCode() == 500) {
                response.join();
                return false;
            } else {
                Authorities Authority = JSONUtils.covertFromJsonToObject(response.get().body(), Authorities.class);
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
