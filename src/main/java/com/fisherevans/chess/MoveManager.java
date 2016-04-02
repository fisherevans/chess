package com.fisherevans.chess;

import com.fisherevans.chess.Piece.BoardMap;
import com.fisherevans.chess.Piece.Color;
import com.fisherevans.chess.Piece.Type;
import javafx.geometry.Pos;

import java.util.HashSet;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Created by fisher.evans on 1/11/16.
 */
public class MoveManager {

    public static MoveFunction function(Piece piece) {
        switch (piece.type) {
            case PAWN: { return pawn(piece.color); }
            case ROOK: { return rook(piece.color); }
            case KNIGHT: { return knight(piece.color); }
            case BISHOP: { return bishop(piece.color); }
            case QUEEN: { return queen(piece.color); }
            case KING: { return king(piece.color); }
            default: { return dummy(); }
        }
    }

    private static MoveFunction dummy() {
        return (position, boardMap) -> {
            Set<Position> positions = new HashSet();
            return positions;
        };
    }

    private static MoveFunction pawn(final Color color) {
        return (position, boardMap) -> {
            Set<Position> positions = new HashSet();
            if(position.y == (color == Color.WHITE ? Position.MAX_POSITION : Position.MIN_POSITION))
                return positions;
            int sign = color == Color.WHITE ? +1 : -1;
            boolean initial = color == Color.WHITE
                    ? position.y == Position.MIN_POSITION+1
                    : position.y == Position.MAX_POSITION-1;
            Position oneUp = new Position(position.x, position.y + 1 * sign);
            if(boardMap.getPiece(oneUp) == null) {
                positions.add(oneUp);
                if(initial) {
                    Position twoUp = new Position(position.x, position.y + 2 * sign);
                    if(boardMap.getPiece(twoUp) == null)
                        positions.add(twoUp);
                }
            }
            Consumer<Position> diagF = diagP -> {
                Piece captured = boardMap.getPiece(diagP);
                if(captured != null && captured.color != color)
                    positions.add(diagP);
            };
            if(position.x != Position.MIN_POSITION) diagF.accept(new Position(position.x-1, oneUp.y));
            if(position.x != Position.MAX_POSITION) diagF.accept(new Position(position.x+1, oneUp.y));
            return positions;
        };
    }

    private static MoveFunction rook(final Color color) {
        return (position, boardMap) -> {
            Set<Position> positions = new HashSet();
            for(int delta = 1;delta >= -1;delta -= 2) {
                for(int x = position.x+delta;x >= Position.MIN_POSITION && x <= Position.MAX_POSITION;x += delta)
                    if(stepInterpMove(color, new Position(x, position.y), positions, boardMap)) break;
                for(int y = position.y+delta;y >= Position.MIN_POSITION && y <= Position.MAX_POSITION;y += delta)
                    if(stepInterpMove(color, new Position(position.x, y), positions, boardMap)) break;
            }
            return positions;
        };
    }

    private static MoveFunction bishop(final Color color) {
        return (position, boardMap) -> {
            Set<Position> positions = new HashSet();
            for(int signX = 1;signX >= -1;signX -= 2) {
                for(int signY = 1;signY >= -1;signY -= 2) {
                    for(int d = 1;d < Position.MAX_POSITION;d++) {
                        int newX = position.x + d*signX;
                        int newY = position.y + d*signY;
                        if(Position.inRange(newX, newY) == false || stepInterpMove(color, new Position(newX, newY), positions, boardMap))
                            break;
                    }
                }
            }
            return positions;
        };
    }

    private static MoveFunction queen(final Color color) {
        return (position, boardMap) -> {
            Set<Position> positions = new HashSet();
            positions.addAll(rook(color).calc(position, boardMap));
            positions.addAll(bishop(color).calc(position, boardMap));
            return positions;
        };
    }

    private static MoveFunction king(final Color color) {
        return (position, boardMap) -> {
            Set<Position> positions = new HashSet();
            for(int dx = -1;dx <= 1;dx++) {
                for(int dy = -1;dy <= 1;dy++) {
                    if(dy == 0 && dx == 0)
                        continue;
                    int newX = position.x + dx, newY = position.y + dy;
                    if(Position.inRange(newX, newY)) {
                        Position newPosition = new Position(newX, newY);
                        stepInterpMove(color, newPosition, positions, boardMap);
                    }
                }
            }
            return positions;
        };
    }

    private static MoveFunction knight(final Color color) {
        return (position, boardMap) -> {
            Set<Position> positions = new HashSet();
            for(int sign = 1;sign >= -1;sign -= 2) {
                for(int smallDelta = 1;smallDelta >= -1;smallDelta -= 2) {
                    int largeDelta = 2*sign;
                    int newX1 = position.x + smallDelta, newY1 = position.y + largeDelta;
                    if(Position.inRange(newX1, newY1))
                        stepInterpMove(color, new Position(newX1, newY1), positions, boardMap);
                    int newX2 = position.x + largeDelta, newY2 = position.y + smallDelta;
                    if(Position.inRange(newX2, newY2))
                        stepInterpMove(color, new Position(newX2, newY2), positions, boardMap);
                }
            }
            return positions;
        };
    }

    private static boolean stepInterpMove(Color thisColor, Position newPosition, Set<Position> positions, BoardMap boardMap) {
        Piece captured = boardMap.getPiece(newPosition);
        if(captured == null)
            positions.add(newPosition);
        else {
            if(captured.color != thisColor)
                positions.add(newPosition);
            return true;
        }
        return false;
    }

    @FunctionalInterface
    public interface MoveFunction {
        public Set<Position> calc(Position position, BoardMap boardMap);
    }
}
