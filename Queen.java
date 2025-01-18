import java.util.ArrayList;
import java.util.Arrays;

import javax.swing.ImageIcon;

public class Queen extends Piece {
    public Queen(ImageIcon icon, String name, int xPos, int yPos) {
        super(icon, name, xPos, yPos);
        pieceType = "Queen";
        material = 9;
    }

    public boolean canMoveIgnoreSide(int x, int y) {
        if (x == xPos && y == yPos) {
            return false;
        }
        if (outOfBoard(x, y)) {
            return false;
        }
        int maxDif = Math.max(Math.abs(xPos - x), Math.abs(yPos - y));
        if (!(x == xPos || y == yPos || Math.abs(x - xPos) == Math.abs(y - yPos) || (Math.pow(Math.abs(xPos - x), 2) + Math.pow(Math.abs(yPos - y), 2) == 5 && maxDif == 2))) {
            return false;
        }

        if (inWay(x, y)) {
            return false;
        }
        if (Math.abs(x - xPos) == Math.abs(y - yPos)) {
            return true;
        }
        if (x == xPos && noPieceInWayY(y)) {
            return true;
        }
        if (y == yPos && noPieceInWayX(x)) {
            return true;
        }
        if (Math.pow(Math.abs(xPos - x), 2) + Math.pow(Math.abs(yPos - y), 2) == 5 && maxDif == 2) {
            return true;
        }
        return false;
    }

    @Override
    public boolean canMove(int x, int y) {
        if (x == xPos && y == yPos) {
            return false;
        }
        if (outOfBoard(x, y)) {
            return false;
        }
        if (!(x == xPos || y == yPos || Math.abs(x - xPos) == Math.abs(y - yPos))) {
            return false;
        }
        Piece potentialTarget = board.getPieces()[x][y];
        if (potentialTarget != null && potentialTarget.isWhite() == isWhite) {
            return false;
        }
        if (!canMoveThroughCheck(x, y)) {
            return false;
        }

        if (inWay(x, y)) {
            return false;
        }
        if (Math.abs(x - xPos) == Math.abs(y - yPos)) {
            return true;
        }
        if (x == xPos && noPieceInWayY(y)) {
            return true;
        }
        if (y == yPos && noPieceInWayX(x)) {
            return true;
        }
        return false;
    }
    @Override
    public boolean canAttack(int x, int y) {
        if (x == xPos && y == yPos) {
            return false;
        }
        if (!(x == xPos || y == yPos || Math.abs(x - xPos) == Math.abs(y - yPos))) {
            return false;
        }
        Piece target = board.getPieces()[x][y];
        if (target != null && target.isWhite() == isWhite) {
            return false;
        }
        
        if (inWay(x, y)) {
            return false;
        }
        if (Math.abs(x - xPos) == Math.abs(y - yPos)) {
            return true;
        }
        if (x == xPos && noPieceInWayY(y)) {
            return true;
        }
        if (y == yPos && noPieceInWayX(x)) {
            return true;
        }
        return false;
    }

    public boolean outOfBoard(int x, int y) {
        return x > Globals.COLS - 1 || y > Globals.ROWS - 1 || x < 0 || y < 0;
    }

    public boolean inWay(int x, int y) {
        if (outOfBoard(x, y)) {
            return false;
        }
        int dx = Math.abs(x - xPos);
        int dy = Math.abs(y - yPos);
        if (dx!=dy) {
            return false;
        }
        int stepX = (x > xPos)? 1 : -1;
        int stepY = (y > yPos)? 1 : -1;
        for (int i = 1; i < dx; i++) {
            if (board.getPieces()[xPos + stepX*i][yPos + stepY*i]!=null) {
                return true;
            }
        }
        return false;
    }
    public boolean noPieceInWayX(int x) {
        if (outOfBoard(x, yPos)) {
            return false;
        }
        if (x < xPos) {
            for (int i = xPos - 1; i > x; i--) {
                if (board.getPieces()[i][yPos] != null) {
                    return false;
                }
            }
        } else {
            for (int i = xPos + 1; i < x; i++) {
                if (board.getPieces()[i][yPos] != null) {
                    return false;
                }
            }
        }
        return true;
        
    }

    public boolean noPieceInWayY(int y) {
        if (outOfBoard(xPos, y)) {
            return false;
        }
        if (y < yPos) {
            for (int i = yPos - 1; i > y; i--) {
                if (board.getPieces()[xPos][i] != null) {
                    return false;
                }
            }
        } else {
            for (int i = yPos + 1; i < y; i++) {
                if (board.getPieces()[xPos][i] != null) {
                    return false;
                }
            }
        }
        return true;
    }

     @Override
    public ArrayList<int[]> calculateValidMoves() {
        ArrayList<int[]> validMoves = new ArrayList<>();
        boolean inWay1 = false;
        boolean inWay2 = false;
        boolean inWay3 = false;
        boolean inWay4 = false;
        for (int i = 0; i < Globals.COLS; i++) {
            inWay1 = inWay1 || inWay(xPos + i, yPos + i);
            if (!inWay1 && canMove(xPos + i, yPos + i)) {
                validMoves.add(new int[]{xPos + i, yPos + i});
            }
            inWay2 = inWay2 || inWay(xPos - i, yPos + i);
            if (!inWay2 && canMove(xPos - i, yPos + i)) {
                validMoves.add(new int[]{xPos - i, yPos + i});
            }
            inWay3 = inWay3 || inWay(xPos + i, yPos - i);
            if (!inWay3 && canMove(xPos + i, yPos - i)) {
                validMoves.add(new int[]{xPos + i, yPos - i});
            }
            inWay4 = inWay4 || inWay(xPos - i, yPos - i);
            if (!inWay4 && canMove(xPos - i, yPos - i)) {
                validMoves.add(new int[]{xPos - i, yPos - i});
            }
        }
        for (int i = xPos + 1; i < Globals.COLS; i++) {
            if (canMove(i, yPos)) {
                validMoves.add(new int[]{i, yPos});
            } else if (!noPieceInWayX(i)) {
                break;
            }
        }
        for (int i = xPos - 1; i >= 0; i--) {
            if (canMove(i, yPos)) {
                validMoves.add(new int[]{i, yPos});
            } else if (!noPieceInWayX(i)) {
                break;
            }
        }
        for (int i = yPos + 1; i < Globals.ROWS; i++) {
            if (canMove(xPos, i)) {
                validMoves.add(new int[]{xPos, i});
            } else if (!noPieceInWayY(i)) {
                break;
            }
        }

        for (int i = yPos - 1; i >= 0; i--) {
            if (canMove(xPos, i)) {
                validMoves.add(new int[]{xPos, i});
            } else if (!noPieceInWayY(i)) {
                break;
            }
        }
        return validMoves;
    }

    public ArrayList<int[]> getValidMovesIncludeSameSide() {
        ArrayList<int[]> validMoves = new ArrayList<>();
        boolean inWay1 = false;
        boolean inWay2 = false;
        boolean inWay3 = false;
        boolean inWay4 = false;
        for (int i = 0; i < Globals.COLS; i++) {
            inWay1 = inWay1 || inWay(xPos + i, yPos + i);
            if (!inWay1 && canMoveIgnoreSide(xPos + i, yPos + i)) {
                validMoves.add(new int[]{xPos + i, yPos + i});
            }
            inWay2 = inWay2 || inWay(xPos - i, yPos + i);
            if (!inWay2 && canMoveIgnoreSide(xPos - i, yPos + i)) {
                validMoves.add(new int[]{xPos - i, yPos + i});
            }
            inWay3 = inWay3 || inWay(xPos + i, yPos - i);
            if (!inWay3 && canMoveIgnoreSide(xPos + i, yPos - i)) {
                validMoves.add(new int[]{xPos + i, yPos - i});
            }
            inWay4 = inWay4 || inWay(xPos - i, yPos - i);
            if (!inWay4 && canMoveIgnoreSide(xPos - i, yPos - i)) {
                validMoves.add(new int[]{xPos - i, yPos - i});
            }
        }
        for (int i = xPos + 1; i < Globals.COLS; i++) {
            if (canMoveIgnoreSide(i, yPos)) {
                validMoves.add(new int[]{i, yPos});
            } else if (!noPieceInWayX(i)) {
                break;
            }
        }
        for (int i = xPos - 1; i >= 0; i--) {
            if (canMoveIgnoreSide(i, yPos)) {
                validMoves.add(new int[]{i, yPos});
            } else if (!noPieceInWayX(i)) {
                break;
            }
        }
        for (int i = yPos + 1; i < Globals.ROWS; i++) {
            if (canMoveIgnoreSide(xPos, i)) {
                validMoves.add(new int[]{xPos, i});
            } else if (!noPieceInWayY(i)) {
                break;
            }
        }

        for (int i = yPos - 1; i >= 0; i--) {
            if (canMoveIgnoreSide(xPos, i)) {
                validMoves.add(new int[]{xPos, i});
            } else if (!noPieceInWayY(i)) {
                break;
            }
        }

        if (canMoveIgnoreSide(xPos + 1, yPos + 2)) {
            validMoves.add(new int[]{xPos + 1, yPos + 2});
        }
        if (canMoveIgnoreSide(xPos + 1, yPos - 2)) {
            validMoves.add(new int[]{xPos + 1, yPos - 2});
        }
        if (canMoveIgnoreSide(xPos - 1, yPos + 2)) {
            validMoves.add(new int[]{xPos - 1, yPos + 2});
        }
        if (canMoveIgnoreSide(xPos - 1, yPos - 2)) {
            validMoves.add(new int[]{xPos - 1, yPos - 2});
        }
        if (canMoveIgnoreSide(xPos + 2, yPos + 1)) {
            validMoves.add(new int[]{xPos + 2, yPos + 1});
        }
        if (canMoveIgnoreSide(xPos + 2, yPos - 1)) {
            validMoves.add(new int[]{xPos + 2, yPos - 1});
        }
        if (canMoveIgnoreSide(xPos - 2, yPos + 1)) {
            validMoves.add(new int[]{xPos - 2, yPos + 1});
        }
        if (canMoveIgnoreSide(xPos - 2, yPos - 1)) {
            validMoves.add(new int[]{xPos - 2, yPos - 1});
        }
        return validMoves;
    }

    @Override
    public double getMGValue() {
        return Globals.MG_VALUE[4];
    }

    @Override
    public double getEGValue() {
        return Globals.EG_VALUE[4];
    }

    @Override
    public double getGamePhase() {
        return 4;
    }

    @Override
    public double getMGPieceTable(int index) {
        return Globals.MG_QUEEN_TABLE[index];
    }

    @Override
    public double getEGPieceTable(int index) {
        return Globals.EG_QUEEN_TABLE[index];
    }
    
}
