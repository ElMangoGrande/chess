package ui;

import java.util.Locale;
import java.util.Scanner;

import static ui.ClientPost.postHelp;
import static ui.ClientPre.preHelp;
import static ui.EscapeSequences.*;

import serverhandling.ResponseException;
import serverhandling.ServerFacade;
import ui.*;

public class REPL {

    private ClientGame game;
    private ClientPost post;
    private ClientPre pre;
    private final ServerFacade server;

    private enum State { PRELOGIN, POSTLOGIN, INGAME }
    private State state = State.PRELOGIN;
    private String authToken;

    public REPL(String serverUrl){
        this.server = new ServerFacade(serverUrl);
        game = new ClientGame(server);
        post = new ClientPost(server);
        pre = new ClientPre(server);

    }

    private void printPrompt() {
        System.out.print("\n" + RESET_TEXT_COLOR + ">>> " + SET_TEXT_COLOR_BLUE);
    }

    public void run(){
        System.out.println("Welcome to Chess. Type help or else");
        System.out.print(preHelp());

        Scanner scanner = new Scanner(System.in);
        var result = "";

        while(!result.equals("quit")){
            printPrompt();
            String line = scanner.nextLine();

            try {
                result = eval(line);
                System.out.print(SET_TEXT_COLOR_WHITE + result);
            } catch (ResponseException e) {
                System.out.print(SET_TEXT_COLOR_RED + e.getMessage());
            } catch (Throwable e) {
                System.out.print(SET_TEXT_COLOR_RED + "Error: " + e.getMessage());
            }

        }
    }

    public String eval(String input) throws ResponseException {
        if (input.equalsIgnoreCase("quit")) {
            return "quit";
        }
        input = input.trim();
        if(input.isEmpty()){
            return "";
        }

        String result;

        //switch state
        switch(state){
            case PRELOGIN -> {
                result = pre.eval(input);
                if(result.equals("registered") || result.equals("login successful")){
                    authToken = pre.getAuthToken();
                }
            }
            case POSTLOGIN -> result = post.eval(input,authToken);
            case INGAME -> result = game.eval(input);
            default -> result = "Invalid state.";
        }

        //switch for result
        switch(result.toLowerCase()){
            case "login successful" ->{
                state = State.POSTLOGIN;
                return "login successful.\n" + postHelp();
            }
            case "registered" -> {
                state = State.POSTLOGIN;
                return "Registration successful.\n" + postHelp();
            }
            case "logout"->{
                state = State.PRELOGIN;
                return "logged out. \n" + preHelp();
            }
            case "observe", "joined game"->{
                state = State.INGAME;
                return "entering game...";
            }
            default ->{
                return result;
            }

        }
    }
}
