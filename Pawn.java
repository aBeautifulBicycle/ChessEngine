import java.util.ArrayList;

import javax.swing.ImageIcon;

public class Pawn extends Piece{
    public Pawn(ImageIcon icon, String name, int xPos, int yPos) {
        super(icon, name, xPos, yPos);
        pieceType = "Pawn";
        material = 1;
    }

    @Override
    public boolean canMove(int x, int y) {
        if (x == xPos && y == yPos || !inBoard(x, y)) {
            return false;
        }

        int forward = isWhite() ? -1 : 1;
        int startRow = isWhite() ? Globals.ROWS - 2 : 1;
        
        Piece targetPiece = board.getPieces()[x][y];
        
        // Forward move
        if (y == yPos) {
            if (x == xPos + forward && targetPiece == null) {
                return canMoveThroughCheck(x, y);
            }
            if (x == xPos + 2 * forward && xPos == startRow && 
                board.getPieces()[xPos + forward][y] == null && targetPiece == null) {
                return canMoveThroughCheck(x, y);
            }
            return false;
        }
        
        // Diagonal capture or en passant
        if (x == xPos + forward && Math.abs(y - yPos) == 1) {
            if (targetPiece != null && targetPiece.isWhite() != isWhite()) {
                return canMoveThroughCheck(x, y);
            }
            Piece enPassantTarget = board.getPieces()[xPos][y];
            if (enPassantTarget != null && enPassantTarget.isWhite() != isWhite() && 
                enPassantTarget.isEnpassantable()) {
                return canMoveThroughCheck(x, y);
            }
        }
        
        return false;
    }

    @Override
    public boolean canAttack(int x, int y) {
        if (x == xPos && y == yPos) {
            return false;
        }
        Piece targetPiece = board.getPieces()[x][y];
        if (y == yPos &&  targetPiece != null) {
            return false;
        }
        
        if (isWhite()) {
            if (x == xPos - 1 && Math.abs(yPos - y) == 1) {
                if (targetPiece != null && targetPiece.isWhite() != this.isWhite())
                    return true;
                if (board.getPieces()[xPos][y] != null && board.getPieces()[xPos][y].isWhite() != isWhite && board.getPieces()[xPos][y].isEnpassantable()) {
                    return true;
                } 
                return false;
            } else {
                return false;
            }

        } else {
            if (x == xPos + 1 && Math.abs(yPos - y) == 1) {
                if (targetPiece != null && targetPiece.isWhite() != this.isWhite())
                    return true;
                if (board.getPieces()[xPos][y] != null && board.getPieces()[xPos][y].isWhite() != isWhite && board.getPieces()[xPos][y].isEnpassantable()) {
                    return true;
                } 
                return false;
            } else {
                return false;
            }
        }
    }

    @Override
    public ArrayList<int[]> calculateValidMoves() {
        ArrayList<int[]> moves = new ArrayList<int[]>();
        int x = xPos;
        int y = yPos;
        if (isWhite()) {
            if (canMove(x - 1, y)) {
                moves.add(new int[]{x - 1, y});
            }
            if (canMove(x - 2, y)) {
                moves.add(new int[]{x - 2, y});
            }
            if (canMove(x - 1, y - 1)) {
                moves.add(new int[]{x - 1, y - 1});
            }
            if (canMove(x - 1, y + 1)) {
                moves.add(new int[]{x - 1, y + 1});
            }
        } else {
            if (canMove(x + 1, y)) {
                moves.add(new int[]{x + 1, y});
            }
            if (canMove(x + 2, y)) {
                moves.add(new int[]{x + 2, y});
            }
            if (canMove(x + 1, y - 1)) {
                moves.add(new int[]{x + 1, y - 1});
            }
            if (canMove(x + 1, y + 1)) {
                moves.add(new int[]{x + 1, y + 1});
            }
        }
        
        return moves;
    }

    @Override
    public boolean move(int x, int y) {
        if (Math.abs(x - xPos) == 2) {
            enpassantable = true;
        }
        Piece targetPiece = board.getPieces()[x][y];
        if (targetPiece != null && targetPiece.isWhite() != this.isWhite()) {
            board.removePiece(targetPiece);
        } else if (y != yPos) {
            
            board.removePiece(board.getPieces()[xPos][y]);
            
        }
        board.movePiece(this, x, y);
        if (xPos == 0 || xPos == Globals.ROWS - 1) {
            promote();
        }
        board.toggleTurn();
        board.setHalfmoveClock(0);
        
        return true;
    }

    // TODO: add support for en passant and promotions
    @Override
    public boolean simMove(int x, int y) {
        if ((xPos == -1 && yPos == -1) || !visible) {
            visible = false;
            return false;
        }
        
        
        int[] state = {xPos, yPos, 0, 0, 0, 0, 0};
        if (enpassantable) {
            state[6] = 2;
            enpassantable = false;
        }
        if (lastState.isEmpty()) {
            state[3] = 1;
        }
        Piece potPiece = board.getPieces()[x][y];
        if (potPiece == null && y != yPos) {
            
            potPiece = board.getPieces()[xPos][y];
            if (board.getMoveOrder().isEmpty() || !board.getMoveOrder().peek().getMovePiece().equals(potPiece)) {
                return false;
            }
            state[2] = 2;
            state[4] = xPos;
            state[5] = y;
        }
        if (Math.abs(xPos - x) == 2) {
            if (((y - 1) >= 0 && board.getPieces()[x][y - 1] != null && board.getPieces()[x][y - 1].isWhite() != isWhite) || ((y + 1) < Globals.ROWS && board.getPieces()[x][y + 1] != null && board.getPieces()[x][y + 1].isWhite() != isWhite)) {
                enpassantable = true;
                state[6] = 1;
            }
            
        }
        if (potPiece != null && potPiece.isWhite() != isWhite) {

            state[2] = Math.max(1, state[2]);
            capturedPiece.push(potPiece);
            potPiece.setVisible(false);
            
        }
        lastState.push(state);
        board.getPieces()[xPos][yPos] = null;
        board.getPieces()[x][y] = this;
        xPos = x;
        yPos = y;

        if (xPos == 0 || xPos == Globals.ROWS - 1) {
            Piece promotedPiece;
            if (isWhite) {
                promotedPiece = new Queen(Globals.WHITE_QUEEN_PNG, "Q", xPos, yPos);
            } else {
                promotedPiece = new Queen(Globals.BLACK_QUEEN_PNG, "q", xPos, yPos);
            }
            promotedPiece.setVisible(true);
            visible = false;
            board.getPieces()[xPos][yPos] = promotedPiece;
            promotedPiece.setParent(this);
            board.getNewPieces().add(promotedPiece);
            promotedPiece.setBoard(board);
            promotedPiece.setWhite(isWhite);
        }
        return true;
    }

    @Override
    public boolean unSimMove() {
        if (lastState.isEmpty()) {
            return false;
        }
        int[] state = lastState.pop();
        if (board.getPieces()[xPos][yPos] != null && !board.getPieces()[xPos][yPos].equals(this)) {
            board.getPieces()[xPos][yPos].setVisible(false);
            board.getPieces()[xPos][yPos] = null;
            visible = true;
            if (xPos == 0) {
                xPos = 1;
            } else {
                xPos = Globals.COLS - 2;
            }
        }
        
        board.getPieces()[xPos][yPos] = null;
        if (state[6] == 1) {
            enpassantable = false;
        } 
        else if (state[6] == 2) {
            enpassantable = true;
            if (lastState.isEmpty()) {
                enpassantable = false;
            } else {
                int[] nextState = lastState.pop();
                nextState[6] = 1;
                lastState.push(nextState);
            }
            
        }
        if (state[2] == 1) {
            Piece captured = capturedPiece.pop();
            captured.setVisible(true);
            board.getPieces()[xPos][yPos] = captured;
        } else if (state[2] == 2) {
            Piece captured;
            captured = capturedPiece.pop();
            captured.setVisible(true);
            board.getPieces()[state[4]][state[5]] = captured;
        }
        board.getPieces()[state[0]][state[1]] = this;
        xPos = state[0];
        yPos = state[1];

        return true;
    }

    public void promote() {
        int x = xPos;
        int y = yPos;
        board.removePiece(this);
        String name = "";
        if (isWhite()) {
            name = "Q";
        } else {
            name = "q";
        }
        ImageIcon icon;
        if (isWhite()) {
            icon = Globals.WHITE_QUEEN_PNG;
        } else {
            icon = Globals.BLACK_QUEEN_PNG;
        }
        Piece newPiece = new Queen(icon, name, x, y);
        
        newPiece.setWhite(isWhite);
        newPiece.setBoard(board);
        newPiece.setVisible(true);
        board.getPieces()[x][y] = newPiece;
        board.getNewPieces().add(newPiece);
        board.getSquares()[x][y].add(newPiece.getLabel());
        if (board.inCheck(!isWhite)) {
            board.highlightCheck();
        }
        
        
        
    }
}
