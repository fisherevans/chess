package com.fisherevans.chess;

import com.fisherevans.chess.Piece.Color;
import com.fisherevans.chess.Piece.Type;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

public class ChessGame {
    public static void main(String[] args) {
        Map<String, Integer> wins = playMany(100);
        for(String color:wins.keySet()) {
            System.out.println(color + " won " + wins.get(color) + " times.");
        }
    }

    public static Map<String, Integer> playMany(int count) {
        Map<String, Integer> wins = new HashMap<>();
        wins.put(winnerName(Color.BLACK), 0);
        wins.put(winnerName(Color.WHITE), 0);
        wins.put(winnerName(null), 0);
        for(int id = 0;id < count;id++) {
            String winner = playOne();
            wins.put(winner, wins.get(winner) + 1);
        }
        return wins;
    }

    public static String playOne() {
        ChessGame game = new ChessGame(GameIntelligence.dummy(), GameIntelligence.greedy());
        game.play();
        return winnerName(game.getWinner());
    }

    public static String winnerName(Color color) {
        return color == null ? "Draw" : color.name();
    }

    private Board board;
    private Map<Color, GameIntelligence> ai = new HashMap<>();
    private boolean played = false;
    private Color winner = null;

    public ChessGame(GameIntelligence white, GameIntelligence black) {
        this.board = Board.createDefaultBoard();
        white.initialize(board.copy(), Color.WHITE);
        ai.put(Color.WHITE, white);
        black.initialize(board.copy(), Color.BLACK);
        ai.put(Color.BLACK, black);
    }

    public boolean isPlayed() {
        return played;
    }

    public Color getWinner() {
        return winner;
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
            System.out.println();
            board.print(System.out);
            System.out.println();
            Set<Move> availableMoves = board.getAvailableMoves(true);
            boolean inCheck = board.isChecked(board.getCurrentTurn());
            System.out.println(board.getCurrentTurn() + " has " + availableMoves.size() + " available moves.");
            if(inCheck) {
                System.out.println(board.getCurrentTurn() + " is in CHECK!");
            }
            if(availableMoves.size() == 0) {
                if(inCheck) {
                    endGame(board.getCurrentTurn().opposite(), "Check mate!", turn);
                } else {
                    endGame(null, board.getCurrentTurn() + " is not in check, but cannot move. It's a draw!", turn);
                }
                return;
            }
            lastMove = ai.get(board.getCurrentTurn()).calculateMove(lastMove, availableMoves);
            if(lastMove == null) {
                endGame(board.getCurrentTurn().opposite(), board.getCurrentTurn() + " has forfeited!", turn);
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
            System.out.println("Turn " + (turn++) + " - " + lastMove.toLongString());
        }
    }

    private void endGame(Color winner, String reason, int turns) {
        this.winner = winner;
        played = true;
        System.out.println("--------------------\n");
        System.out.println((winner == null ? "No one" : winner.name()) + " won the game!");
        System.out.println(reason);
        System.out.println("\nThe game took " + turns + " turns.");
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
}
