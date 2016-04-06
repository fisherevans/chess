package com.fisherevans.chess;

import com.fisherevans.chess.Piece.Color;
import com.fisherevans.chess.Piece.Type;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

public class ChessGame {
    public static void main(String[] args) {
        Map<String, Integer> wins = playMany(100000);
        println("\n\n");
        for(String color:wins.keySet()) {
            System.out.println(color + " won " + wins.get(color) + " times.");
        }
    }

    public static Map<String, Integer> playMany(int count) {
        Map<String, Integer> wins = new HashMap<>();
        wins.put("Dummy", 0);
        wins.put("Greedy", 0);
        wins.put("Draw", 0);
        for(int id = 0;id < count;id++) {
            GameIntelligence white = id % 2 == 0 ? GameIntelligence.dummy() : GameIntelligence.greedy();
            GameIntelligence black = id % 2 == 0 ? GameIntelligence.greedy() : GameIntelligence.dummy();
            Color winner = playOne(white, black);
            String winnerName = "";
            if(winner == null) {
                winnerName = "Draw";
            } else {
                if(winner == Color.WHITE) {
                    winnerName = id % 2 == 0 ? "Dummy" : "Greedy";
                } else { // BLACK
                    winnerName = id % 2 == 0 ? "Greedy" : "Dummy";
                }
            }
            wins.put(winnerName, wins.get(winnerName) + 1);
        }
        return wins;
    }

    public static Color playOne(GameIntelligence white, GameIntelligence black) {
        ChessGame game = new ChessGame(white, black);
        game.play();
        return game.getWinner();
    }

    private Board board;
    private Map<Color, GameIntelligence> ai = new HashMap<>();
    private boolean played = false;
    private Color winner = null;
    private final List<Move> moves;
    private Color currentTurn;

    public ChessGame(GameIntelligence white, GameIntelligence black) {
        this.board = Board.createDefaultBoard();
        white.initialize(board.copy(), Color.WHITE);
        ai.put(Color.WHITE, white);
        black.initialize(board.copy(), Color.BLACK);
        ai.put(Color.BLACK, black);
        moves = new ArrayList<>();
        currentTurn = Color.WHITE;
    }

    public boolean isPlayed() {
        return played;
    }

    public Color getWinner() {
        return winner;
    }

    public List<Move> getMoves() {
        return moves;
    }

    public Color getCurrentTurn() {
        return currentTurn;
    }

    public void play() {
        if(played)
            throw new RuntimeException("This game has already been played!");

        Scanner scanner = new Scanner(System.in);

        boolean wait = false;
        int turn = 1;
        int lastCapture = 0;
        int lastPawn = 0;
        Move lastMove = null;
        while(wait == false || scanner.hasNextLine()) {
            if(wait) {
                scanner.nextLine();
            }
            println("");
            if(doPrint) {
                board.print(System.out);
            }
            println("");
            Set<Move> availableMoves = board.getAvailableMoves(currentTurn, true);
            boolean inCheck = board.isChecked(currentTurn);
            println(currentTurn + " has " + availableMoves.size() + " available moves.");
            if(inCheck) {
                println(currentTurn + " is in CHECK!");
            }
            if(availableMoves.size() == 0) {
                if(inCheck) {
                    endGame(currentTurn.opposite(), "Check mate!", turn);
                } else {
                    endGame(null, currentTurn + " is not in check, but cannot move. It's a draw!", turn);
                }
                return;
            }
            lastMove = ai.get(currentTurn).calculateMove(lastMove, availableMoves);
            if(lastMove == null) {
                endGame(currentTurn.opposite(), currentTurn + " has forfeited!", turn);
                return;
            }
            try {
                board.applyMove(lastMove);
                ai.get(Color.WHITE).applyMove(lastMove);
                ai.get(Color.BLACK).applyMove(lastMove);
            } catch (Exception e) {
                e.printStackTrace();
                System.exit(1);
            }
            moves.add(lastMove);
            if(lastMove.captured != null) {
                lastCapture = 0;
            }
            if(lastMove.piece.type == Type.PAWN) {
                lastPawn = 0;
            }
            lastCapture++;
            lastPawn++;
            if(isEndCondition(lastPawn, lastCapture, turn)) {
                return;
            }
            currentTurn = currentTurn.opposite();
            println("Turn " + (turn++) + " - " + lastMove.toLongString());
        }
    }

    private void endGame(Color winner, String reason, int turns) {
        this.winner = winner;
        played = true;
        println("--------------------\n");
        println((winner == null ? "No one" : winner.name()) + " won the game!");
        println(reason);
        println("\nThe game took " + turns + " turns.");
    }

    private boolean isEndCondition(int lastPawn, int lastCapture, int turn) {
        if(lastCapture > 50) {
            endGame(null, "The last piece was captured 50 turns ago. It's a draw!", turn);
            return true;
        }
        if(lastPawn > 50) {
            endGame(null, "The last pawn moved was 50 turns ago. It's a draw!", turn);
            return true;
        }
        return false;
    }

    private static final boolean doPrint = false;

    public static void println(String message) {
        if(doPrint) {
            System.out.println(message);
        }
    }

    public static void print(String message) {
        if(doPrint) {
            System.out.print(message);
        }
    }
}
