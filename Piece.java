import java.util.ArrayList;
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
    protected double material;
    protected ArrayList<int[]> validMoves = new ArrayList<>();
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

    public Piece(ImageIcon icon, String name, int xPos, int yPos, Board board) {
        this.icon = icon;
        this.name = name;
        this.xPos = xPos;
        this.yPos = yPos;
        this.board = board;
        visible = false;
        selected = false;
        label = new JLabel(icon);
        isWhite = true;
        enpassantable = false;
    }

    public void updateValidMoves() {
        validMoves = calculateValidMoves();
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
//        if (state[3] == 1) {
//            System.out.println("ran IMPORTANT");
//            parent.setVisible(true);
//            visible = false;
//            board.getPieces()[xPos][yPos] = null;
//            board.getPieces()[xPos][yPos] = parent;
//            parent.setxPos(xPos);
//            parent.setyPos(yPos);
//            yPos = -1;
//            xPos = -1;
//            parent.unSimMove();
//        }

        return true;
    }

    public boolean tryMove(int x, int y) {
        selected = false;
        unhighlightValidMoves();
        if (!canMove(x, y)) {
            return false;
        }
        board.noPassants();
        move(x, y);

        
        return true;
    }

    public boolean move(int x, int y) {
        Piece targetPiece = board.getPieces()[x][y];
        if (targetPiece != null && targetPiece.isWhite() != this.isWhite) {
            board.removePiece(targetPiece);
        }
        
        board.movePiece(this, x, y);
        board.toggleTurn();

        
        return true;
    }

    public boolean canMoveThroughCheck(int x, int y) {
        if (xPos == -1 && yPos == -1) {
            visible = false;
            return false;
        }
        Piece previousPiece = board.getPieces()[x][y];
        board.getPieces()[x][y] = this;
        
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

    public ArrayList<int[]> calculateValidMoves() {
        return new ArrayList<>();
    }
    public boolean inBoard(int x, int y) {
        if (x >= Globals.ROWS || y >= Globals.COLS || x < 0 || y < 0) {
            return false;
        }
        return true;
    }
    public double[] getEvaluation() {
        double mg_score = getMGValue();
        double eg_score = getEGValue();
        int index;
        if (isWhite) {
            index = (xPos) * (Globals.ROWS - 1) + yPos;
        } else {
            index = (Globals.ROWS - 1 - xPos) * (Globals.ROWS - 1) + yPos;
        }
        double[] extra = pieceSpecificEvaluation();
        mg_score += getMGPieceTable(index) + extra[0];
        eg_score += getEGPieceTable(index) + extra[1];

//        if (board.isAttackedUnsafely(xPos, yPos, this, isWhite)) {
//            mg_score -= getMGValue() / 2;
//            eg_score -= getEGValue() * 0.75;
//        } 
//        if (board.isAttacked(xPos, yPos, !isWhite, this)) {
//            mg_score += getMGValue() / 2;
//            eg_score += getEGValue() * 0.75;
//        }
        double gamePhase = getGamePhase();
        return new double[]{mg_score, eg_score, gamePhase};
    }

    
    public double[] pieceSpecificEvaluation() {
        return new double[]{0, 0};
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass()!= o.getClass()) {
            return false;
        }
        Piece piece = (Piece) o;
        if (xPos!= piece.xPos || yPos!= piece.yPos || isWhite!= piece.isWhite) {
            return false;
        }
        if (canCastle!= piece.canCastle) {
            return false;
        }
        if (enpassantable!= piece.enpassantable) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = getPieceNum();
        hash = 7 * hash + xPos;
        hash = 9 * hash + yPos;
        hash = 9 * hash + (isWhite? 1 : 0);
        hash = 3 * hash + (canCastle? 1 : 0);
        hash = 3 * hash + (enpassantable? 1 : 0);
        return hash;
    }

    public int getPieceNum() {
        return 0;
    }

    public double getMGValue() {
        return 0;
    }

    public double getEGValue() {
        return 0;
    }

    public double getGamePhase() {
        return 0;
    }

    public double getMGPieceTable(int index) {
        return Globals.MG_PAWN_TABLE[index];
    }

    public double getEGPieceTable(int index) {
        return Globals.EG_PAWN_TABLE[index];
    }

    public boolean isACheck(int x, int y) {
        int oldX = xPos;
        int oldY = yPos;
        xPos = x;
        yPos = y;
        if (isWhite && canAttack(board.getbKing().getxPos(), board.getbKing().getyPos())) {
            xPos = oldX;
            yPos = oldY;
            return true;
        } else if (!isWhite && canAttack(board.getwKing().getxPos(), board.getwKing().getyPos())) {
            xPos = oldX;
            yPos = oldY;
            return true;
        }
        xPos = oldX;
        yPos = oldY;
        return false;
    }

    public void highlightValidMoves() {
        ArrayList<int[]> validMoves;
        if (this.validMoves.size() == 0) {
            validMoves = calculateValidMoves();
        } else {
            validMoves = this.validMoves;
        }
        int[][] highlightSquares = new int[validMoves.size() + 1][];
        highlightSquares[0] = new int[]{xPos, yPos};
        for (int i = 0; i < validMoves.size(); i++) {
            highlightSquares[i + 1] = new int[]{validMoves.get(i)[0], validMoves.get(i)[1]};
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

    public double getMaterial() {
        return material;
    }

    public void setMaterial(double material) {
        this.material = material;
    }

    public ArrayList<int[]> getValidMoves() {
        return validMoves;
    }

    public void setValidMoves(ArrayList<int[]> validMoves) {
        this.validMoves = validMoves;
    }
}
