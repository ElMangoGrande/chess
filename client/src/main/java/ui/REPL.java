package ui;

import java.util.Scanner;

import static ui.EscapeSequences.*;

public class REPL {

    private ClientGame game;
    private ClientPost post;
    private ClientPre pre;


    REPL(){
        game = new ClientGame();
        post = new ClientPost();
        pre = new ClientPre();
    }

    private void printPrompt() {
        System.out.print("\n" + RESET_TEXT_COLOR + ">>> " + SET_TEXT_COLOR_BLUE);
    }

    public void run(){
        System.out.println("Welcome to Chess. Type help or else");
        System.out.print(ClientPre.help());

        Scanner scanner = new Scanner(System.in);
        var result = "";

        while(!result.equals("quit")){
            printPrompt();
            String line = scanner.nextLine();

            //call eval for pre
            //analyze return on eval from pre
            //move onto next loop if you should, else stay in loop

        }

    }
}
