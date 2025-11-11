package ui;

import serverhandling.ServerFacade;

import static ui.EscapeSequences.*;

public class ClientPost {

    private final ServerFacade server;

    public ClientPost(ServerFacade server) {
        this.server = server;
    }
    public static String help() {
        return SET_TEXT_COLOR_BLUE +"create <NAME>" + RESET_TEXT_COLOR + "- a game\n" +
                 SET_TEXT_COLOR_BLUE +"list" + RESET_TEXT_COLOR +"- games\n" +
                SET_TEXT_COLOR_BLUE + "join <ID> [WHITE|BLACK]" + RESET_TEXT_COLOR + "- a game\n"
                +SET_TEXT_COLOR_BLUE + "observe <ID>"+ RESET_TEXT_COLOR + "- a game\n"
                + SET_TEXT_COLOR_BLUE+"logout" + RESET_TEXT_COLOR+ "When you are done\n"
                + SET_TEXT_COLOR_BLUE+"quit" + RESET_TEXT_COLOR+ " - playing chess\n" +
                SET_TEXT_COLOR_BLUE+"help" + RESET_TEXT_COLOR +"lists possible commands\n";
    }
}
