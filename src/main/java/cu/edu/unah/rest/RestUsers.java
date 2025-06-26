package cu.edu.unah.rest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import cu.edu.unah.entity.Users;
import cu.edu.unah.util.JSONUtils;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class RestUsers {

    private static final HttpClient client = HttpClient.newBuilder().version(HttpClient.Version.HTTP_2).build();
    private static final String serviceURL = "http://localhost:8081/Users/";

    //sending request to retrieve all users available.
    public List<Users> findAllUsers() {
        HttpRequest req = HttpRequest.newBuilder(URI.create(serviceURL+"findAll")).GET().build();
        CompletableFuture<HttpResponse<String>> response = client.sendAsync(req, HttpResponse.BodyHandlers.ofString());
        List<Users> list_users = null;
        try {
            list_users = JSONUtils.convertFromJsonToList(response.get().body(), new
                    TypeReference<List<Users>>() {});
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        response.join();
        return list_users;
    }

    public List<String> findAuthoritiesByUsername(String username) {
        HttpRequest req = HttpRequest.newBuilder(URI.create(serviceURL+"findAuthoritiesByUsername/"+username)).GET().build();
        CompletableFuture<HttpResponse<String>> response = client.sendAsync(req, HttpResponse.BodyHandlers.ofString());
        List<String> listAuthorities = null;
        try {
            listAuthorities = JSONUtils.convertFromJsonToList(response.get().body(), new
                    TypeReference<List<String>>() {});
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        response.join();
        return listAuthorities;
    }

    //sending request retrieve the user based on the username
    public Users findUserByUsername(String username) {
        Users user = null;
        HttpRequest req = HttpRequest.newBuilder(URI.create(serviceURL+"find/"+username)).GET().build();
        CompletableFuture<HttpResponse<String>> response = client.sendAsync(req, HttpResponse.BodyHandlers.ofString());
        try {
            if(response.get().statusCode() == 500){
                response.join();
                return null;
            }else {

                try {
                    user = JSONUtils.covertFromJsonToObject(response.get().body(), Users.class);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
                response.join();
                return user;
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return user;
    }

    //send request to add the product details.
    public boolean createUser(Users user){
        String inputJson = null;
        inputJson = JSONUtils.covertFromObjectToJson(user);
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

    //send request to update a User details.
    public boolean updateUser(Users user){
        String inputJson= null;
        inputJson = JSONUtils.covertFromObjectToJson(user);
        HttpRequest request = HttpRequest.newBuilder(URI.create(serviceURL+"update"))
                .header("Content-Type", "application/json")
                .PUT(HttpRequest.BodyPublishers.ofString(inputJson)).build();
        CompletableFuture<HttpResponse<String>> response = client.sendAsync(request,HttpResponse.BodyHandlers.ofString());
        try {
            if(response.get().statusCode() == 500){
                response.join();
                return false;
            } else {
                user = JSONUtils.covertFromJsonToObject(response.get().body(), Users.class);
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

    //send request to delete the user by its username
    public boolean deleteUser(String username) {
        HttpRequest request = HttpRequest.newBuilder(URI.create(serviceURL+"delete/"+username)).DELETE().build();
        CompletableFuture<HttpResponse<String>> response = client.sendAsync(request,HttpResponse.BodyHandlers.ofString());
        try {
            if(response.get().statusCode() == 500) {
                response.join();
                return false;
            } else {
                Users user = JSONUtils.covertFromJsonToObject(response.get().body(), Users.class);
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
