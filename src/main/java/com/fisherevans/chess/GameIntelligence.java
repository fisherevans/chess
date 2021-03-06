package com.fisherevans.chess;

import com.fisherevans.chess.Piece.Color;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

import static com.fisherevans.chess.ChessGame.print;
import static com.fisherevans.chess.ChessGame.println;

public interface GameIntelligence {
    void initialize(Board board, Color color);

    void applyMove(Move move) throws Exception;

    Move calculateMove(Move lastMove, Set<Move> availableMoves);

    static GameIntelligence dummy() {
        return new GameIntelligence() {
            private Board board;
            private Color color;
            @Override
            public void initialize(Board board, Color color) {
                this.board = board;
                this.color = color;
            }
            @Override
            public void applyMove(Move move) throws Exception {
                board.applyMove(move);
            }
            @Override
            public Move calculateMove(Move lastMove, Set<Move> availableMoves) {
                List<Move> moves = new ArrayList<>(availableMoves);
                Collections.shuffle(moves);
                return moves.size() > 0 ? moves.get(0) : null;
            }
        };
    }

    static GameIntelligence greedy() {
        return new GameIntelligence() {
            private Board board;
            private Color color;
            @Override
            public void initialize(Board board, Color color) {
                this.board = board;
                this.color = color;
            }
            @Override
            public void applyMove(Move move) throws Exception {
                board.applyMove(move);
            }
            @Override
            public Move calculateMove(Move lastMove, Set<Move> availableMoves) {
                List<Move> moves = new ArrayList<>(availableMoves);
                Collections.shuffle(moves);
                Move myMove = null;
                for(Move move:moves) {
                    if(move.captured != null) {
                        if(myMove == null || move.captured.type.value > myMove.captured.type.value){
                            myMove = move;
                        }
                    }
                }
                if(myMove == null) {
                    return moves.isEmpty() ? null : moves.get(0);
                } else {
                    return myMove;
                }
            }
        };
    }

    static GameIntelligence human() {
        return new GameIntelligence() {
            private Board board;
            private Color color;
            @Override
            public void initialize(Board board, Color color) {
                this.board = board;
                this.color = color;
            }
            @Override
            public void applyMove(Move move) throws Exception {
                board.applyMove(move);
            }
            @Override
            public Move calculateMove(Move lastMove, Set<Move> availableMoves) {
                Scanner in = new Scanner(System.in);
                Move move = null;
                while (move == null) {
                    println("Please make a move for " + color);
                    Position from, to;
                    try {
                        print("From: ");
                        from = Position.fromString(in.nextLine().trim());
                        print("To:   ");
                        to = Position.fromString(in.nextLine().trim());
                    } catch (Exception e) {
                        println("Invalid input, try again. (" + e.getLocalizedMessage() + ")\n");
                        continue;
                    }
                    for(Move potMove:availableMoves) {
                        if(potMove.from.equals(from) && potMove.to.equals(to)) {
                            move = potMove;
                            continue;
                        }
                    }
                    println("Invalid move selection!\n");
                }
                return move;
            }
        };
    }
}
