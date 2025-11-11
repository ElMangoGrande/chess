package ui;

import serverhandling.ServerFacade;

public class ClientGame {

    private final ServerFacade server;

    public ClientGame(ServerFacade server) {
        this.server = server;
    }
}
