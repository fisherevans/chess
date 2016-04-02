package com.fisherevans.chess;

/**
 * Created by fisher.evans on 1/8/16.
 */
public class Move {
    public final Position from, to;
    public final Piece piece, captured;

    public Move(Position from, Position to, Piece piece, Piece captured) {
        if(from == null || to == null || piece == null)
            throw new RuntimeException("From, to, or piece CANNOT be null");
        this.from = from;
        this.to = to;
        this.piece = piece;
        this.captured = captured;
    }

    @Override
    public int hashCode() {
        return from.hashCode() + to.hashCode()*31 + piece.hashCode()*111 + (captured == null ? 0 : captured.hashCode()*317);
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof Move) {
            Move other = (Move) obj;
            return from.equals(other.from)
                    && to.equals(other.to)
                    && piece.equals(other.piece);
        } else
            return false;
    }

    @Override
    public String toString() {
        return String.format("%s:%s>%s[%]",
                piece.toString(), from.toString(), to.toString(), captured == null ? "" : captured);
    }

    public String toLongString() {
        return String.format("%s moved from %s to %s%s",
                piece.toLongString(), from.toString(), to.toString(),
                captured == null ? "" : ", capturing a " + captured.toLongString());
    }
}
