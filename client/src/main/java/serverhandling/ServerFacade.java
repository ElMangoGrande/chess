package serverhandling;

import com.google.gson.Gson;
import model.*;
import java.net.*;
import java.net.http.*;
import java.net.http.HttpRequest.BodyPublisher;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse.BodyHandlers;


import java.net.http.HttpClient;


public class ServerFacade {

    private final HttpClient client = HttpClient.newHttpClient();
    private final String ServerUrl;

    public ServerFacade(String url){ServerUrl=url;}

    public RegistrationResult register(RegistrationRequest registrationrequest) throws ResponseException{
        var request = buildRequest("POST", "/user", registrationrequest, null);
        var response = sendRequest(request);
        return handleResponse(response, RegistrationResult.class);
    }

    public LoginResult login(LoginRequest requestLogin) throws ResponseException{
        var request = buildRequest("POST", "/session", requestLogin, null);
        var response = sendRequest(request);
        return handleResponse(response, LoginResult.class);
    }

    public void logout(LogoutRequest requestLogout) throws ResponseException{
        var request = buildRequest("DELETE", "/session", requestLogout, requestLogout.authToken());
        var response = sendRequest(request);
        handleResponse(response, null);
    }

    public void joinGame(JoinGameRequest requestJoin) throws ResponseException{
        var request = buildRequest("PUT", "/game", requestJoin, requestJoin.authToken());
        var response = sendRequest(request);
        handleResponse(response,null);
    }

    public CreateGameResult createGame(CreateGameRequest requestGame) throws ResponseException{
        var request = buildRequest("POST", "/game", requestGame, requestGame.authToken());
        var response = sendRequest(request);
        return handleResponse(response, CreateGameResult.class);
    }

    public ListGamesResult listGames(ListGamesRequest requestList) throws ResponseException{
        var request = buildRequest("GET", "/game", requestList, requestList.authToken());
        var response = sendRequest(request);
        return handleResponse(response, ListGamesResult.class);
    }

    public void clear() throws ResponseException{
        var request = buildRequest("DELETE","/db",null, null);
        var response = sendRequest(request);
        handleResponse(response,null);
    }


    private HttpRequest buildRequest(String method, String path, Object body, String tokenValue) {
        var request = HttpRequest.newBuilder()
                .uri(URI.create(ServerUrl + path))
                .method(method, makeRequestBody(body));
        if(tokenValue != null){
            request.setHeader("authentication", tokenValue);
        }
        else if (body != null) {
            request.setHeader("Content-Type", "application/json");
        }
        return request.build();
    }

    private BodyPublisher makeRequestBody(Object request) {
        if (request != null) {
            return BodyPublishers.ofString(new Gson().toJson(request));
        } else {
            return BodyPublishers.noBody();
        }
    }

    private HttpResponse<String> sendRequest(HttpRequest request) throws ResponseException {
        try {
            return client.send(request, BodyHandlers.ofString());
        } catch (Exception ex) {
            throw new ResponseException(ex.getMessage());
        }
    }

    private <T> T handleResponse(HttpResponse<String> response, Class<T> responseClass) throws ResponseException {
        var status = response.statusCode();
        if (!isSuccessful(status)) {
            throw new ResponseException(ResponseException.statusCode(status));
        }

        if (responseClass != null) {
            return new Gson().fromJson(response.body(), responseClass);
        }

        return null;
    }
    private boolean isSuccessful(int status) {
        return status / 100 == 2;
    }

}
