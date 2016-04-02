package com.fisherevans.chess;

import java.io.Serializable;

/**
 * Created by fisher.evans on 1/7/16.
 */
public class Position implements Serializable {
    private static final String ERR_FMT_BOUND = "Invalid value. Not met: ! %d<=%s:%d<=%d";
    private static final String ERR_FMT_ENC = "Invalid encoded format: %s";

    public static final int MIN_POSITION = 0;
    public static final int MAX_POSITION = 7;

    public static final char[] X_TRANSLATION = new char[] {'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h'};
    public static final char[] Y_TRANSLATION = new char[] {'1', '2', '3', '4', '5', '6', '7', '8'};

    private static final char ENCODE_SEPARATOR = ',';

    public final int x, y;

    public Position(int x, int y) {
        if(x < MIN_POSITION || x > MAX_POSITION)
            throw new RuntimeException(String.format(ERR_FMT_BOUND, MIN_POSITION, "x", x, MAX_POSITION));
        if(y < MIN_POSITION || y > MAX_POSITION)
            throw new RuntimeException(String.format(ERR_FMT_BOUND, MIN_POSITION, "y", y, MAX_POSITION));
        this.x = x;
        this.y = y;
    }

    public char getXChar() {
        return X_TRANSLATION[x];
    }

    public char getYChar() {
        return Y_TRANSLATION[y];
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(x) + 31*Integer.hashCode(y);
    }

    @Override
    public boolean equals(Object obj) {
        if(obj != null && obj instanceof Position) {
            Position other = (Position) obj;
            return x == other.x && y == other.y;
        } else {
            return false;
        }
    }

    @Override
    public String toString() {
        return new String(new char[] { getXChar(), ENCODE_SEPARATOR, getYChar()});
    }

    public static Position fromString(final String encoded) {
        final char[] chars = encoded.toCharArray();
        if(chars.length != 3 || chars[1] != ENCODE_SEPARATOR)
            throw new RuntimeException(String.format(ERR_FMT_ENC, encoded));
        try {
            final int tx = Integer.parseInt(String.valueOf(chars[0]));
            final int ty = Integer.parseInt(String.valueOf(chars[2]));
            return new Position(tx, ty);
        } catch (Exception e) {
            throw new RuntimeException(String.format(ERR_FMT_ENC, encoded), e);
        }
    }

    public static boolean inRange(int x, int y) {
        if(x < MIN_POSITION || x > MAX_POSITION)
            return false;
        if(y < MIN_POSITION || y > MAX_POSITION)
            return false;
        return true;
    }
}
