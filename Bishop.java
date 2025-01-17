import java.util.ArrayList;

import javax.swing.ImageIcon;

public class Bishop extends Piece{
    public Bishop(ImageIcon icon, String name, int xPos, int yPos) {
        super(icon, name, xPos, yPos);
        pieceType = "Bishop";
        material = 3.25;  
    }

    @Override
    public boolean canMove(int x, int y) {
        if (x == xPos && y == yPos) {
            return false;
        }
        
        if (outOfBoard(x, y)) {
            return false;
        }
        if (!(Math.abs(x - xPos) == Math.abs(y - yPos))) {
            return false;
        }
        Piece potentialTarget = board.getPieces()[x][y];
        if (potentialTarget != null && potentialTarget.isWhite() == isWhite()) {
            return false;
        }
        if (!canMoveThroughCheck(x, y)) {
            return false;
        }

        if (inWay(x, y)) {
            return false;
        }
        return true;
    }
    @Override
    public boolean canAttack(int x, int y) {
        if (x == xPos && y == yPos) {
            return false;
        }
        Piece target = board.getPieces()[x][y];
        if (target != null && target.isWhite() == isWhite) {
            return false;
        }

        if (!(Math.abs(x - xPos) == Math.abs(y - yPos))) {
            return false;
        }
        
        if (inWay(x, y)) {
            return false;
        }
        return true;
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

    @Override
    public int[][] getValidMoves() {
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
        return validMoves.toArray(new int[validMoves.size()][]);
    }
    
}
