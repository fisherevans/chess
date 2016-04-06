package com.fisherevans.chess;

import com.fisherevans.chess.Piece.Color;
import com.fisherevans.chess.Piece.Type;

import java.io.PrintStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

/**
 * Created by fisher.evans on 1/7/16.
 */
public class Board implements Serializable {
    private final Map<Position, Piece> pieces;

    public Board(Map<Position, Piece> pieces) {
        this.pieces = pieces;
    }

    public Board() {
        this(new HashMap<>());
    }

    public void applyMove(final Move move) throws Exception {
        Piece fromPiece = pieces.get(move.from);
        if(fromPiece == null)
            throw new Exception("Must move a piece!");
        if(false == MoveManager.function(fromPiece).calc(move.from, p -> pieces.get(p)).contains(move.to))
            throw new Exception("Invalid move, to destination is not valid!");
        Piece toPiece = pieces.get(move.to);
        if(toPiece != null && toPiece.color == fromPiece.color)
            throw new Exception("You cannot take your own piece!");
        pieces.remove(move.from);
        pieces.put(move.to, fromPiece);
    }

    public boolean isChecked(Color color) {
        final Set<Move> availableMoves = getAvailableMoves(color.opposite(), false);
        for(Move move:availableMoves)
            if(move.captured != null && move.captured.color == color && move.captured.type == Type.KING)
                return true;
        return false;
    }

    public Set<Move> getAvailableMoves(Color color, boolean checkForCheck) {
        Set<Move> availableMoves = new HashSet();
        for(Map.Entry<Position, Piece> entry:pieces.entrySet()) {
            Position from = entry.getKey();
            Piece piece = entry.getValue();
            if(piece.color == color) {
                for (Position to : MoveManager.function(piece).calc(from, p -> pieces.get(p))) {
                    Move move = new Move(from, to, piece, pieces.get(to));
                    if(checkForCheck) {
                        Board copy = copy();
                        try {
                            copy.applyMove(move);
                        } catch (Exception e) {
                            throw new RuntimeException("This shouldn't happen", e);
                        }
                        if(false == copy.isChecked(color)) {
                            availableMoves.add(move);
                        }
                    } else {
                        availableMoves.add(move);
                    }
                }
            }
        }
        return availableMoves;
    }

    public void print(PrintStream out) {
        String horz = " +--+--+--+--+--+--+--+--+";
        for(int y = Position.MAX_POSITION;y >= Position.MIN_POSITION;y--) {
            out.println(horz);
            out.print(Position.Y_TRANSLATION[y]);
            for(int x = Position.MIN_POSITION;x <= Position.MAX_POSITION;x++) {
                Position position = new Position(x, y);
                Piece piece = pieces.get(position);
                if(piece != null)
                    out.print(new String(new char[] { '|', piece.color.code, piece.type.code }));
                else
                    out.print("|  ");
            }
            out.println("|");
        }
        out.println(horz);
        out.print("  ");
        for(int x = Position.MIN_POSITION;x <= Position.MAX_POSITION;x++)
            out.print(new String(new char[] { Position.X_TRANSLATION[x], ' ', ' '}));
        out.println();
    }

    public Board copy() {
        Map<Position, Piece> newPieces = new HashMap<>();
        newPieces.putAll(pieces);
        return new Board(newPieces);
    }

    public static Board createDefaultBoard() {
        Board board = new Board();
        for(byte x = Position.MIN_POSITION; x <= Position.MAX_POSITION; x++)
            board.placeSingleDefaultPiece(Type.PAWN, true, x);
        board.placeSingleDefaultPiece(Type.ROOK, false, Position.MIN_POSITION, Position.MAX_POSITION);
        board.placeSingleDefaultPiece(Type.KNIGHT, false, Position.MIN_POSITION+1, Position.MAX_POSITION-1);
        board.placeSingleDefaultPiece(Type.BISHOP, false, Position.MIN_POSITION+2, Position.MAX_POSITION-2);

        board.pieces.put(new Position(Position.MIN_POSITION+3, Position.MIN_POSITION), new Piece(Color.WHITE, Type.QUEEN));
        board.pieces.put(new Position(Position.MIN_POSITION+3, Position.MAX_POSITION), new Piece(Color.BLACK, Type.QUEEN));
        board.pieces.put(new Position(Position.MAX_POSITION-3, Position.MIN_POSITION), new Piece(Color.WHITE, Type.KING));
        board.pieces.put(new Position(Position.MAX_POSITION-3, Position.MAX_POSITION), new Piece(Color.BLACK, Type.KING));
        return board;
    }

    public static Board createTestBoard() {
        Board board = new Board();
        board.pieces.put(new Position(Position.MIN_POSITION+3, Position.MIN_POSITION+3), new Piece(Color.WHITE, Type.KING));
        board.pieces.put(new Position(Position.MAX_POSITION-3, Position.MAX_POSITION-3), new Piece(Color.BLACK, Type.KING));
        return board;
    }

    public static Board createCMBoard() {
        Board board = new Board();
        board.pieces.put(new Position(Position.MIN_POSITION, Position.MIN_POSITION), new Piece(Color.WHITE, Type.KING));
        board.pieces.put(new Position(Position.MAX_POSITION, Position.MAX_POSITION), new Piece(Color.BLACK, Type.KING));
        board.pieces.put(new Position(Position.MAX_POSITION, Position.MIN_POSITION+2), new Piece(Color.BLACK, Type.ROOK));
        board.pieces.put(new Position(Position.MAX_POSITION, Position.MIN_POSITION+4), new Piece(Color.BLACK, Type.ROOK));
        board.pieces.put(new Position(Position.MAX_POSITION, Position.MIN_POSITION+6), new Piece(Color.BLACK, Type.ROOK));
        board.pieces.put(new Position(Position.MAX_POSITION, Position.MIN_POSITION+5), new Piece(Color.BLACK, Type.ROOK));
        return board;
    }

    private void placeSingleDefaultPiece(final Type type, boolean secondRow, final int ... dxs) {
        for(int dx:dxs) {
            pieces.put(new Position(dx, Position.MIN_POSITION+(secondRow?1:0)), new Piece(Color.WHITE, type));
            pieces.put(new Position(Position.MAX_POSITION-dx, Position.MAX_POSITION-(secondRow?1:0)), new Piece(Color.BLACK, type));
        }
    }
}
