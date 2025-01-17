import java.util.ArrayList;

import javax.swing.ImageIcon;

public class King extends Piece{
    public King(ImageIcon icon, String name, int xPos, int yPos) {
        super(icon, name, xPos, yPos);
        pieceType = "King";
        material = 1000;
        canCastle = true;
    }

    @Override
    public boolean canMove(int x, int y) {
        if (outOfBoard(x, y)) {
            return false;
        }
        if (x == xPos && y == yPos) {
            return false;
        }
        Piece potTarget = board.getPieces()[x][y];
        if (potTarget != null && potTarget.isWhite() == isWhite) {
            return false;
        }
        boolean castleLeftPreliminaryCheck = canCastle && x == xPos && y == 0 || y == 1 || y == 2;
        boolean castleRightPreliminaryCheck = canCastle && x == xPos && y == Globals.COLS - 1 || y == Globals.COLS - 2;
        boolean validMoveNoCastle = ((Math.abs(x - xPos) <= 1 && Math.abs(y - yPos) <= 1) && Math.abs(x - xPos) + Math.abs(y - yPos) != 0);
        if (!validMoveNoCastle && (!castleLeftPreliminaryCheck && !castleRightPreliminaryCheck)) {
            return false;
        }
        
        // try moving the king there
        Piece previousPiece = board.getPieces()[x][y];
        board.getPieces()[x][y] = this;
        board.getPieces()[getxPos()][getyPos()] = null;
        if (board.isAttacked(x, y, isWhite)) {
            board.getPieces()[x][y] = previousPiece;
            board.getPieces()[getxPos()][getyPos()] = this;
            return false;
        }
        board.getPieces()[x][y] = previousPiece;
        board.getPieces()[getxPos()][getyPos()] = this;
        
        if (validMoveNoCastle) {
            return true;
        }
        
        if (castleLeftPreliminaryCheck) {
            return validCastleLeft(false);
        } 
        if (castleRightPreliminaryCheck) {
            return validCastleRight(false);
        }
        return false;
    }

    @Override
    public boolean canAttack(int x, int y) {
        if (x == xPos && y == yPos) {
            return false;
        }
        if (outOfBoard(x, y)) {
            return false;
        }
        
        Piece target = board.getPieces()[x][y];
        if (target != null && target.isWhite() == isWhite) {
            return false;
        }
        if ((Math.abs(x - xPos) <= 1 && Math.abs(y - yPos) <= 1) && Math.abs(x - xPos) + Math.abs(y - yPos) != 0) {
            return true;
        }
        
        return false;
    }
    // TODO: add support for castling

    @Override
    public boolean simMove(int x, int y) {
        if ((xPos == -1 && yPos == -1) || !visible) {
            visible = false;
            return false;
        }
        
        int[] state = {xPos, yPos, 0, 0, 0, 0};
        if (lastState.isEmpty()) {
            state[3] = 1;
            state[5] = canCastle ? 1 : 0;
            canCastle = false;
        }
        Piece potPiece = board.getPieces()[x][y];
        if (potPiece != null && potPiece.isWhite() != isWhite) {
            state[2] = 1;
            capturedPiece.push(potPiece);
            potPiece.setVisible(false);
            
        }
        lastState.push(state);
        board.getPieces()[xPos][yPos] = null;
        board.getPieces()[x][y] = this;
        if (Math.abs(y - yPos) == 2) {
            state[4] = 1;
            if (yPos - y == 2) {
                Piece castleRook = board.getPieces()[xPos][0];
                board.getPieces()[xPos][0] = null;
                board.getPieces()[xPos][y + 1] = castleRook;
                castleRook.setyPos(y + 1);
                castleRook.setCanCastle(false);
            } else {
                Piece castleRook = board.getPieces()[xPos][Globals.COLS - 1];
                board.getPieces()[xPos][Globals.COLS - 1] = null;
                board.getPieces()[xPos][y - 1] = castleRook;
                castleRook.setyPos(y - 1);
                castleRook.setCanCastle(false);
            }
            
        }
//        this.setCanCastle(false);
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
        if (state[4] == 1) {
            if (state[1] - yPos == 2) {
                Piece castleRook = board.getPieces()[xPos][yPos + 1];
                
                board.getPieces()[xPos][yPos + 1] = null;
                board.getPieces()[xPos][0] = castleRook;
                castleRook.setCanCastle(true);
                castleRook.setyPos(0);
            } else {
                Piece castleRook = board.getPieces()[xPos][yPos - 1];
                board.getPieces()[xPos][yPos - 1] = null;
                board.getPieces()[xPos][Globals.COLS - 1] = castleRook;
                if (castleRook == null) {
                    System.out.println(board);
                    System.out.println(board.getMoveOrder());
                    System.out.println("HOW!!");
                }
                castleRook.setCanCastle(true);
                castleRook.setyPos(Globals.COLS - 1);
            }
            this.setCanCastle(true);
        }
        
        board.getPieces()[xPos][yPos] = null;
        if (state[2] == 1) {
            Piece captured = capturedPiece.pop();
            captured.setVisible(true);
            board.getPieces()[xPos][yPos] = captured;
        }
        board.getPieces()[state[0]][state[1]] = this;
        xPos = state[0];
        yPos = state[1];
        if (lastState.isEmpty()) {
            canCastle = state[5] == 1;
        }

        return true;
    }

    public boolean validCastleLeft(boolean inCheck) {
        if (!canCastle || inCheck) {
            return false;
        }
        Piece leftRook = board.getPieces()[xPos][0];
        if (leftRook == null || leftRook.isWhite()!= isWhite() || !leftRook.isCanCastle() || board.getPieces()[xPos][1]!= null || board.getPieces()[xPos][2]!= null || board.getPieces()[xPos][3] != null) {
            return false;
        }
        int x = xPos;
        int y = yPos - 1;
        Piece previousPiece = board.getPieces()[x][y];
        board.getPieces()[x][y] = this;
        board.getPieces()[getxPos()][getyPos()] = null;
        if (board.isAttacked(x, y, isWhite)) {
            board.getPieces()[x][y] = previousPiece;
            board.getPieces()[getxPos()][getyPos()] = this;
            return false;
        }
        board.getPieces()[x][y] = previousPiece;
        board.getPieces()[getxPos()][getyPos()] = this;
        if (board.isAttacked(xPos, yPos - 2, isWhite) || board.isAttacked(xPos, yPos - 1, isWhite)) {
            return false;
        }

        return true;
    }

    public boolean validCastleRight(boolean inCheck) {
        if (!canCastle || inCheck) {
            return false;
        }
        Piece rightRook = board.getPieces()[xPos][Globals.COLS - 1];
        if (rightRook == null || rightRook.isWhite()!= isWhite() || !rightRook.isCanCastle() || board.getPieces()[xPos][Globals.COLS - 2]!= null || board.getPieces()[xPos][Globals.COLS - 3] != null) {
            return false;
        }
        int x = xPos;
        int y = yPos + 1;
        Piece previousPiece = board.getPieces()[x][y];
        board.getPieces()[x][y] = this;
        board.getPieces()[getxPos()][getyPos()] = null;
        if (board.isAttacked(x, y, isWhite)) {
            board.getPieces()[x][y] = previousPiece;
            board.getPieces()[getxPos()][getyPos()] = this;
            return false;
        }
        board.getPieces()[x][y] = previousPiece;
        board.getPieces()[getxPos()][getyPos()] = this;
        if (board.isAttacked(xPos, yPos + 1, isWhite) || board.isAttacked(xPos, yPos + 2, isWhite)) {
            return false;
        }
        return true;

    }



    @Override
    public boolean move(int x, int y) {
        if (!((Math.abs(x - xPos) <= 1 && Math.abs(y - yPos) <= 1) && Math.abs(x - xPos) + Math.abs(y - yPos) != 0)) {
            if (y < yPos) {
                return castleLeft();
            } else {
                return castleRight();
            }
        }
        Piece targetPiece = board.getPieces()[x][y];
        if (targetPiece != null && targetPiece.isWhite() != this.isWhite) {
            board.removePiece(targetPiece);
        }
        board.movePiece(this, x, y);
        canCastle = false;
        board.toggleTurn();
        return true;
    }

    public boolean castleLeft() {
        Piece leftRook = board.getPieces()[xPos][0];
        board.movePiece(leftRook, xPos, yPos - 1);
        board.movePiece(this, xPos, yPos - 2);
        canCastle = false;
        board.toggleTurn();
        return true;
    }

    public boolean castleRight() {
        Piece rightRook = board.getPieces()[xPos][Globals.COLS - 1];
        board.movePiece(rightRook, xPos, yPos + 1);
        board.movePiece(this, xPos, yPos + 2);
        canCastle = false;
        board.toggleTurn();
        return true;
    }

    public boolean outOfBoard(int x, int y) {
        return x > Globals.COLS - 1 || y > Globals.ROWS - 1 || x < 0 || y < 0;
    }

    @Override
    public int[][] getValidMoves() {
        ArrayList<int[]> validMoves = new ArrayList<>();
        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                if ((i == 0 && j == 0) || outOfBoard(xPos, yPos)) {
                    continue;
                }
                if (canMove(xPos + i, yPos + j)) {
                    validMoves.add(new int[]{xPos + i, yPos + j});
                }
            }
        }
        if (canCastle) {
            if (validCastleLeft(board.inCheck(isWhite()))) {
                int x = xPos;
                int y = yPos - 2;
                boolean canMove = true;
                Piece previousPiece = board.getPieces()[x][y];
                board.getPieces()[x][y] = this;
                board.getPieces()[getxPos()][getyPos()] = null;
                if (board.isAttacked(x, y, isWhite)) {
                    canMove = false;
                }
                board.getPieces()[x][y] = previousPiece;
                board.getPieces()[getxPos()][getyPos()] = this;
                if (canMove)
                validMoves.add(new int[]{xPos, yPos - 2});
            }
            if (validCastleRight(board.inCheck(isWhite()))) {
                int x = xPos;
                int y = yPos + 2;
                boolean canMove = true;
                Piece previousPiece = board.getPieces()[x][y];
                board.getPieces()[x][y] = this;
                board.getPieces()[getxPos()][getyPos()] = null;
                if (board.isAttacked(x, y, isWhite)) {
                    canMove = false;
                }
                board.getPieces()[x][y] = previousPiece;
                board.getPieces()[getxPos()][getyPos()] = this;
                if (canMove)
                validMoves.add(new int[]{xPos, yPos + 2});
            }
        }
        return validMoves.toArray(new int[validMoves.size()][]);
    }

    @Override
    public double getMGValue() {
        return Globals.MG_VALUE[5];
    }

    @Override
    public double getEGValue() {
        return Globals.EG_VALUE[5];
    }

    @Override
    public double getGamePhase() {
        return 0;
    }

    @Override
    public double getMGPieceTable(int index) {
        return Globals.MG_KING_TABLE[index];
    }

    @Override
    public double getEGPieceTable(int index) {
        return Globals.EG_KING_TABLE[index];
    }
    
}
