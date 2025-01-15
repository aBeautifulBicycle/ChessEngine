import java.util.ArrayList;

import javax.swing.ImageIcon;

public class Rook extends Piece{
    public Rook(ImageIcon icon, String name, int xPos, int yPos) {
        super(icon, name, xPos, yPos);
        pieceType = "Rook";
    }

    @Override
    public boolean canMove(int x, int y) {
        if (x == xPos && y == yPos) {
            return false;
        }
        if (outOfBoard(x, y)) {
            return false;
        }
        Piece potentialTarget = board.getPieces()[x][y];
        if (potentialTarget != null && potentialTarget.isWhite() == isWhite()) {
            return false;
        }
        // check checker
        if (!canMoveThroughCheck(x, y)) {
            return false;
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

    @Override
    public boolean canAttack(int x, int y) {
        if (x == xPos && y == yPos) {
            return false;
        }
        
        Piece target = board.getPieces()[x][y];
        if (target != null && target.isWhite() == isWhite) {
            return false;
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
    public boolean simMove(int x, int y) {
        if ((xPos == -1 && yPos == -1) || !visible) {
            visible = false;
            return false;
        }
        
        int[] state = {xPos, yPos, 0, 0, 0};
        if (lastState.isEmpty()) {
            state[4] = canCastle ? 1 : 0;
            canCastle = false;
        }
        Piece potPiece = board.getPieces()[x][y];
        if (potPiece != null && potPiece.isWhite() != isWhite) {
            state[2] = 1;
            capturedPiece.push(potPiece);
            potPiece.setVisible(false);
            
        }
        if (lastState.isEmpty() && parent != null) {
            state[3] = 1;
        }
        lastState.push(state);
        board.getPieces()[xPos][yPos] = null;
        board.getPieces()[x][y] = this;
        xPos = x;
        yPos = y;
        return true;
    }

    @Override
    public boolean unSimMove() {
        if (lastState.isEmpty()) {
            return false;
        }
        int[] state = lastState.pop();
        
        
        board.getPieces()[xPos][yPos] = null;
        if (state[2] == 1) {
            Piece captured = capturedPiece.pop();
            captured.setVisible(true);
            board.getPieces()[xPos][yPos] = captured;
        }
        board.getPieces()[state[0]][state[1]] = this;
        xPos = state[0];
        yPos = state[1];
        if (state[3] == 1) {
            parent.setVisible(true);
            visible = false;
            board.getPieces()[xPos][yPos] = parent;
            parent.unSimMove();
        }
        if (lastState.isEmpty()) {
            canCastle = state[4] == 1;
        }

        return true;
    }

    @Override
    public int[][] getValidMoves() {
        ArrayList<int[]> moves = new ArrayList<int[]>();
        int x = xPos;
        int y = yPos;
        for (int i = x + 1; i < Globals.COLS; i++) {
            if (canMove(i, y)) {
                moves.add(new int[]{i, y});
            } else if (!noPieceInWayX(i)) {
                break;
            }
        }
        for (int i = x - 1; i >= 0; i--) {
            if (canMove(i, y)) {
                moves.add(new int[]{i, y});
            } else if (!noPieceInWayX(i)) {
                break;
            }
        }
        for (int i = y + 1; i < Globals.ROWS; i++) {
            if (canMove(x, i)) {
                moves.add(new int[]{x, i});
            } else if (!noPieceInWayY(i)) {
                break;
            }
        }

        for (int i = y - 1; i >= 0; i--) {
            if (canMove(x, i)) {
                moves.add(new int[]{x, i});
            } else if (!noPieceInWayY(i)) {
                break;
            }
        }
        int[][] validMoves = new int[moves.size()][];
        for (int i = 0; i < moves.size(); i++) {
            validMoves[i] = moves.get(i);
        }
        
        return validMoves;
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
    public boolean move(int x, int y) {
        Piece targetPiece = board.getPieces()[x][y];
        if (targetPiece != null && targetPiece.isWhite() != this.isWhite) {
            board.removePiece(targetPiece);
        }
        canCastle = false;
        board.movePiece(this, x, y);
        board.toggleTurn();
        
        return true;
    }
}
