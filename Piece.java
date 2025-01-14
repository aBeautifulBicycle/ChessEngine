import java.util.Arrays;
import java.util.Stack;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class Piece {
    protected ImageIcon icon;
    protected String name;
    protected int xPos;
    protected int yPos;
    protected boolean visible;
    protected boolean selected;
    protected JLabel label;
    protected boolean isWhite;
    protected Board board;
    protected boolean enpassantable;
    protected boolean canCastle;
    protected int[][] highlightedSquares;
    protected Stack<int[]> lastState = new Stack<>();
    protected Stack<Piece> capturedPiece = new Stack<>();
    protected String pieceType;
    protected Piece parent;
    public Piece(ImageIcon icon, String name, int xPos, int yPos) {
        this.icon = icon;
        this.name = name;
        this.xPos = xPos;
        this.yPos = yPos;
        visible = false;
        selected = false;
        label = new JLabel(icon);
        isWhite = true;
        enpassantable = false;
    }

    public boolean simMove(int x, int y) {
        if ((xPos == -1 && yPos == -1) || !visible) {
            visible = false;
            return false;
        }
        
        int[] state = {xPos, yPos, 0, 0};
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

        return true;
    }

    public boolean tryMove(int x, int y) {
        selected = false;
        unhighlightValidMoves();
        if (!canMove(x, y)) {
            return false;
        }
        board.noPassants();
        Piece targetPiece = board.getPieces()[x][y];
        if (targetPiece != null && targetPiece.isWhite() != this.isWhite) {
            board.removePiece(targetPiece);
        }
        board.movePiece(this, x, y);
        board.toggleTurn();

        
        return true;
    }

    public boolean canMoveThroughCheck(int x, int y) {
        Piece previousPiece = board.getPieces()[x][y];
        board.getPieces()[x][y] = this;
        if (xPos == -1 && yPos == -1) {
            visible = false;
            return false;
        }
        board.getPieces()[getxPos()][getyPos()] = null;
        if (previousPiece != null) {
            previousPiece.setVisible(false);
        }
        
        if (board.inCheck(isWhite)) {
            if (previousPiece != null) {
                previousPiece.setVisible(true);
            }
            board.getPieces()[x][y] = previousPiece;
            board.getPieces()[getxPos()][getyPos()] = this;
            return false;
        }
        if (previousPiece != null) {
            previousPiece.setVisible(true);
        }
        board.getPieces()[x][y] = previousPiece;
        board.getPieces()[getxPos()][getyPos()] = this;
        return true;
    }

    public boolean canAttack(int x, int y) {
        return canMove(x, y);
    }

    public void removeFromBoard(JPanel[][] squares) {
        board.removePiece(this);
        visible = false;
    }

    public double calculateScore() {
        return 0;
    }

    public int[][] getValidMoves() {
        return new int[][]{{0, 0}};
    }
    public boolean inBoard(int x, int y) {
        if (x >= Globals.ROWS || y >= Globals.COLS || x < 0 || y < 0) {
            return false;
        }
        return true;
    }

    public void highlightValidMoves() {
        int[][] validMoves = getValidMoves();
        int[][] highlightSquares = new int[validMoves.length + 1][];
        highlightSquares[0] = new int[]{xPos, yPos};
        for (int i = 0; i < validMoves.length; i++) {
            highlightSquares[i + 1] = new int[]{validMoves[i][0], validMoves[i][1]};
        }
        highlightedSquares = highlightSquares;
        board.highlightSquares(highlightSquares);
    }

    public void unhighlightValidMoves() {
        board.unhighlightSquares(highlightedSquares);
    }

    @Override
    public String toString() {
        return name;
    }

    public boolean canMove(int x, int y) {
        return true;
    }
    public ImageIcon getIcon() {
        return icon;
    }
    public void setIcon(ImageIcon icon) {
        this.icon = icon;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public int getxPos() {
        return xPos;
    }
    public void setxPos(int xPos) {
        this.xPos = xPos;
    }
    public int getyPos() {
        return yPos;
    }
    public void setyPos(int yPos) {
        this.yPos = yPos;
    }
    public boolean isVisible() {
        return visible;
    }
    public void setVisible(boolean visible) {
        this.visible = visible;
    }
    public boolean isSelected() {
        return selected;
    }
    public void setSelected(boolean selected) {
        this.selected = selected;
    }
    public JLabel getLabel() {
        return label;
    }
    public void setLabel(JLabel label) {
        this.label = label;
    }

    public boolean isWhite() {
        return isWhite;
    }

    public void setWhite(boolean isWhite) {
        this.isWhite = isWhite;
    }

    public Board getBoard() {
        return board;
    }

    public void setBoard(Board board) {
        this.board = board;
    }

    public boolean isEnpassantable() {
        return enpassantable;
    }

    public void setEnpassantable(boolean enpassantable) {
        this.enpassantable = enpassantable;
    }

    public int[][] getHighlightedSquares() {
        return highlightedSquares;
    }

    public void setHighlightedSquares(int[][] highlightedSquares) {
        this.highlightedSquares = highlightedSquares;
    }

    public boolean isCanCastle() {
        return canCastle;
    }

    public void setCanCastle(boolean canCastle) {
        this.canCastle = canCastle;
    }

    public String getPieceType() {
        return pieceType;
    }

    public void setPieceType(String pieceType) {
        this.pieceType = pieceType;
    }

    public Stack<int[]> getLastState() {
        return lastState;
    }

    public void setLastState(Stack<int[]> lastState) {
        this.lastState = lastState;
    }

    public Stack<Piece> getCapturedPiece() {
        return capturedPiece;
    }

    public void setCapturedPiece(Stack<Piece> capturedPiece) {
        this.capturedPiece = capturedPiece;
    }

    public Piece getParent() {
        return parent;
    }

    public void setParent(Piece parent) {
        this.parent = parent;
    }
}
