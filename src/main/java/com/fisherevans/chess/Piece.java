package com.fisherevans.chess;

/**
 * Created by fisher.evans on 1/7/16.
 */
public class Piece {
    private static final String ERR_FMT_NULL = "Invalid value. %s, %s";
    private static final String ERR_FMT_ENC = "Invalid encoded format: %s";

    private static final char ENCODE_SEPARATOR = ',';

    public final Type type;
    public final Color color;

    public Piece(Color color, Type type) {
        if(type == null || color == null)
            throw new RuntimeException(String.format(ERR_FMT_NULL, String.valueOf(type), String.valueOf(color)));
        this.type = type;
        this.color = color;
    }

    @Override
    public int hashCode() {
        return type.hashCode() + 31*color.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if(obj != null && obj instanceof Piece) {
            Piece other = (Piece) obj;
            return type == other.type && color == other.color;
        } else {
            return false;
        }
    }

    @Override
    public String toString() {
        return new String(new char[] { color.code, ENCODE_SEPARATOR, type.code });
    }

    public String toLongString() {
        return color.toString() + " " + type.toString();
    }

    public static Piece fromString(final String encode) {
        final String[] split = encode.split(String.valueOf(ENCODE_SEPARATOR));
        if(split.length != 2)
            throw new RuntimeException(String.format(ERR_FMT_ENC, encode));
        try {
            final Type type = Type.valueOf(split[0]);
            final Color color = Color.valueOf(split[1]);
            return new Piece(color, type);
        } catch (Exception e) {
            throw new RuntimeException(String.format(ERR_FMT_ENC, encode), e);
        }
    }

    enum Type {
        PAWN('P', 1),
        ROOK('R', 2),
        KNIGHT('N', 2),
        BISHOP('B', 2),
        QUEEN('Q', 3),
        KING('K', 4);
        public final char code;
        public final int value;
        Type(char code, int value) {
            this.code = code;
            this.value = value;
        }
    }

    enum Color {
        BLACK('B'), WHITE('W');
        public final char code;
        Color(char code) {
            this.code = code;
        }
        Color opposite() {
            return this == BLACK ? WHITE : BLACK;
        }
    }

    @FunctionalInterface
    public interface BoardMap {
        public Piece getPiece(Position position);
    }
}
