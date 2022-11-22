import java.util.Arrays;
import java.util.Random;
import java.util.Scanner;

public class Main {

    /*
    *   Simple game written in Java.
    *   Features a small "prediction" (?) system.
    *   Gives a second chance to the player if he is about to lose.
    *   Chance of it happening can be changed.
    *   First one to reach position 30+ wins.
    */

    /* commonVars */
    public static final String ANSI_RESET = "\u001B[0m";
    public static final String[] COLORS = new String[6];

    /* gameVars */
    public static int[] posMines = new int[4]; // Number of mines to generate
    public static int[] posPlayer = new int[2]; // Number of players (should always be 2)
    public static int[] p_posPlayer = new int[2]; // Variable to store our "prediction"
    public static int turn = 0; // Stores who is currently playing
    public static int partyMode = 0; // Party mode ON / OFF ( 1 / 0 )
    public static int status = 0; // Stores current game status
    public static int fix = 0; // Small fix that I don't like but needs to be here :c
    public static int dice = 0; // Our main gameplay feature :rofl:
    public static int secondChance = 4; // Second-chance chance - 0 will make it trigger 100%, max is 10 (0%)

    // Util functions
    public static void println(String text){ // Text animation + random colors (if party mode = 1)
        for (int i = 0; i < text.length(); i++){
            if (partyMode == 1){
                Random rnd = new Random();
                System.out.print(COLORS[rnd.nextInt(6)] + text.charAt(i) + ANSI_RESET);
            }
            else
                System.out.print(text.charAt(i));
            sleep(20);
        }
        System.out.println();
    }
    public static boolean contains(final int[] arr, final int key) { // Used to check if array contains key.
        return Arrays.stream(arr).anyMatch(i -> i == key);
    }
    public static void sleep(final int time){ // Used to sleep lol
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public static void main(String[] args) {
        // Declaring the color
        COLORS[0] = "\u001B[33m";
        COLORS[1] = "\u001B[32m";
        COLORS[2] = "\u001B[36m";
        COLORS[3] = "\u001B[35m";
        COLORS[4] = "\u001B[36m";
        COLORS[5] = "\u001B[37m";

        // Utils
        Random rnd = new Random();
        Scanner U_Scan = new Scanner(System.in);

        // Generate mines - only on startup / 1 time.
        for (int i = 0; i < posMines.length; i++) {
            posMines[i] = rnd.nextInt(29) + 1;
        }

        // Game loop - becomes inactive when a player wins or when status != 0
        while (posPlayer[0] < 30 && posPlayer[1] < 30 && status == 0){
            // Debug way of checking positions, I just don't like it but can be added to the gameplay.
            // System.out.println("| 1 | 2 | 3 | 4 | 5 | 6 | 7 | 8 | 9 | 0 | 1 | 2 | 3 | 4 | 5 | 6 | 7 | 8 | 9 | 0 | 1 | 2 | 3 | 4 | 5 | 6 | 7 | 8 | 9 | 0 | 1 | 2 | 3 | 4 | 5 |");

            // Render loop - runs until it generates 30 rows.
            for (int i = 0; i <= 30; i++){
                // Make sure game didn't alr finish on last render loop.
                if (status == 0){
                    // Check if any player is about to lose.
                    // Ye, u can prob just check current turn but why not just check both to prevent cheaters XD
                    if (contains(posMines, posPlayer[0])){
                        // Game over!
                        println("GAME OVER! Player 1 exploded!");
                        status = 1;
                        break;
                    }
                    else if (contains(posMines, posPlayer[1])){
                        // Game over!
                        println("GAME OVER! Player 2 exploded!");
                        status = 1;
                        break;
                    }
                    // Simple way of not drawing both players int the same position
                    else if (posPlayer[1] == posPlayer[0] && posPlayer[0] == i && fix == 0){
                        System.out.print("| "+ (turn + 1) +" ");
                        fix = 1;
                    }
                    // Draw player 2
                    else if (posPlayer[1] == i && posPlayer[1] != posPlayer[0]){
                        System.out.print("| 2 ");
                    }
                    // Draw player 1
                    else if (posPlayer[0] == i && posPlayer[1] != posPlayer[0]){
                        System.out.print("| 1 ");
                    }
                    // Draw mines
                    else if (contains(posMines, i)) {
                        System.out.print("| x ");
                    }
                    else if (i == 30){
                        System.out.println("|");
                    }
                    else {
                        System.out.print("|   ");
                    }
                }
            }

            // Game turn logic - runs when status == 0
            if (status == 0){
                // Resets "fix", prevents player from drawing multiple times on prev loop
                // TODO: This should be looked into it since there has to be a cleaner way of fixing it!
                fix = 0;

                // Ask for player turn
                println("Player " + (turn + 1) + " turn!");
                U_Scan.next();

                // "Roll the dice"
                dice = rnd.nextInt(5) + 1;

                // [debug] print our first gen
                System.err.println("[debug] first_gen " + dice);

                // "Predict" our next movement
                p_posPlayer[turn] += dice;

                // Check if number will land on mina and give a chance of that not happening
                if (contains(posMines, p_posPlayer[turn]) && rnd.nextInt(11) > secondChance){
                    // This is not going to be called if our secondChance isn't true!
                    System.err.println("[debug] second chance...");

                    // Reset our last prediction.
                    p_posPlayer[turn] -= dice;

                    // If it lands on mine, re-roll and reassign once - nerf -> smaller number
                    dice = rnd.nextInt(3) + 1;
                }

                // Print our new position addition
                println("Dice rolled: " + dice);

                // Move the player
                posPlayer[turn] += dice;

                // Update our prediction position
                p_posPlayer[turn] = posPlayer[turn];

                // Sleep 1s
                sleep(1000);

                // Switch turn
                turn = 1 - turn;
            }
        }

        // check score and print winner - end game
        if (posPlayer[0] >= 30){
            System.out.println("Player 1 Wins!");
        }
        else if (posPlayer[1] >= 30){
            System.out.println("Player 2 Wins!");
        }

    }
}