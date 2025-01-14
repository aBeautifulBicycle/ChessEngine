import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Stack;

import javax.swing.JPanel;

public class Board {
    public class Move {
        private int[] moveLocation;
        private Piece movePiece;
        public Move(int[] moveLocation, Piece movePiece) {
            this.moveLocation = moveLocation;
            this.movePiece = movePiece;
        }
        public int[] getMoveLocation() {
            return moveLocation;
        }
        public void setMoveLocation(int[] moveLocation) {
            this.moveLocation = moveLocation;
        }
        public Piece getMovePiece() {
            return movePiece;
        }
        public void setMovePiece(Piece movePiece) {
            this.movePiece = movePiece;
        }
        @Override
        public String toString() {
            return movePiece + " " + Arrays.toString(moveLocation);
        }
    }
    private Piece[][] pieces;
    private ArrayList<Piece> newPieces;
    private JPanel[][] squares;
    private ArrayList<Piece> pieceList;
    private Piece wKing, bKing;
    private boolean whiteTurn = true;
    private int[] checkSquarePos;
    private int halfmoveClock, fullmoveClock;
    private ArrayList<String> fenList = new ArrayList<>();
    private Stack<Move> moveOrder = new Stack<>();
    public Board(Piece[][] pieces, JPanel[][] squares, ArrayList<Piece> pieceList) {
        this.pieces = pieces;
        this.squares = squares;
        this.pieceList = pieceList;
        whiteTurn = true;
        newPieces = new ArrayList<>();
    }

    public String getFen() {
        StringBuilder fen = new StringBuilder();
        for (int i = 0; i < 8; i++) {
            int emptyCount = 0;
            for (int j = 0; j < 8; j++) {
                if (pieces[i][j] == null || !pieces[i][j].isVisible()) {
                    emptyCount++;
                } else {
                    if (emptyCount > 0) {
                        fen.append(emptyCount);
                        emptyCount = 0;
                    }
                    fen.append(pieces[i][j].getName());
                }
            }
            if (emptyCount > 0) {
                fen.append(emptyCount);
            }
            if (i < 7) {
                fen.append("/");
            }
        }
        return fen.toString();
    }

    public void toggleTurn() {
        whiteTurn = !whiteTurn;
    }

    public int getGameState() {
        if (isCheckMate()) {
            if (whiteTurn) {
                return 2;
            } else {
                return 1;
            }
        }
        if (noMoves()) {
            return 3;
        }

        if (threefoldRepition()) {
            return 4;
        }
        if (halfmoveClock >= 100) {
            return 5;
        }
        if (noSufficientMaterial()) {
            return 6;
        }
        
        
        return 0;
    }

    public boolean threefoldRepition() {
        int count = 0;
        String fen = getFen();
        for (String f : fenList) {
            if (f.equals(fen)) {
                count++;
            }
            if (count == 3) {
                return true;
            }
        }
        return false;
    }

    public boolean noSufficientMaterial() {
        int whiteCount = 0;
        int blackCount = 0;
        for (Piece p : pieceList) {
            if (!p.isVisible()) {
                continue;
            }
            if (p.getPieceType().equals("Pawn") || p.getPieceType().equals("Rook") || p.getPieceType().equals("Queen")) {
                return false;
            }
            if (p.isWhite()) {
                whiteCount++;
            } else {
                blackCount++;
            }
        }
        for (Piece p : newPieces) {
            if (!p.isVisible()) {
                continue;
            }
            if (p.getPieceType().equals("Pawn") || p.getPieceType().equals("Rook") || p.getPieceType().equals("Queen")) {
                return false;
            }
            if (p.isWhite()) {
                whiteCount++;
            } else {
                blackCount++;
            }
        }
        if (whiteCount <= 2 && blackCount <= 2) {
            return true;
        }
        return false;
    }

    public void noPassants() {
        for (Piece p : pieceList) {
            if (p.isEnpassantable()) {
                p.setEnpassantable(false);
            }
        }
        for (Piece p : newPieces) {
            if (p.isEnpassantable()) {
                p.setEnpassantable(false);
            }
        }
    }

    public int exploreAllPositions(boolean isWhite, int depth, boolean checkValid) {
        int numMoves = 0;
        int maxDepth = Globals.MAX_SEARCH_DEPTH;
        Piece[][] initPieces = new Piece[pieces.length][];
        ArrayList<Move> moves = getAllValidMoves(isWhite);
        if (depth == 0) {
            moveOrder = new Stack<>();
        }
        depth++;
        
        
        if (checkValid) {
            for (int i = 0; i < pieces.length; i++) {
                initPieces[i] = Arrays.copyOf(pieces[i], pieces[i].length);
            }
        }
        for (Move move : moves) {
            Piece p = move.getMovePiece();
            numMoves++;
            if (p.simMove(move.getMoveLocation()[0], move.getMoveLocation()[1])) {
                
                moveOrder.push(move);
                if (depth < maxDepth) {
                    numMoves += exploreAllPositions(!isWhite, depth, checkValid);
                }
                
                p.unSimMove();
                if (checkValid) {
                    checkValid(initPieces);
                }
                moveOrder.pop();
            }
        }


        return numMoves;
    }

    public ArrayList<Move> getAllValidMoves(boolean isWhite) {
        ArrayList<Piece> removePieces = new ArrayList<>();
        ArrayList<Move> moves = new ArrayList<>();
        for (Piece p : pieceList) {
            if (p.isVisible() && p.isWhite() == isWhite) {
                int[][] valid = p.getValidMoves();
                for (int[] movePos : valid) {
                    Move move = new Move(movePos, p);
                    moves.add(move);
                }
                
            } 
        }

        for (Piece p : newPieces) {
            if (p.isVisible() && p.isWhite() == isWhite) {
                int[][] valid = p.getValidMoves();
                for (int[] movePos : valid) {
                    Move move = new Move(movePos, p);
                    moves.add(move);
                }
            } else {
                removePieces.add(p);
            }
        }

        for (Piece p : removePieces) {
            newPieces.remove(p);
        }
        return moves;
    }

    public int getAllValidMoves(boolean isWhite, int depth, boolean checkValid) {
        int numMoves = 0;
        ArrayList<Piece> removePieces = new ArrayList<>();
        if (depth == 0) {
            moveOrder = new Stack<>();
        }
        depth++;
        int maxDepth = 4;
        Piece[][] initPieces = new Piece[pieces.length][];
        if (checkValid) {
            for (int i = 0; i < pieces.length; i++) {
                initPieces[i] = Arrays.copyOf(pieces[i], pieces[i].length);
            }
        }
        
        int count = 0;
        for (Piece p : pieceList) {
            if (p.isVisible() && p.isWhite() == isWhite) {
                int[][] valid = p.getValidMoves();
                for (int[] move : valid) {
                    count++;
                    int initX = p.getxPos();
                    int initY = p.getyPos();
                    if (p.simMove(move[0], move[1])) {
                        moveOrder.push(new Move(move, p));
                        if (depth < maxDepth) {
                            numMoves += getAllValidMoves(!isWhite, depth, checkValid);
                        }
                        
                        p.unSimMove();
                        if (checkValid) {
                            checkValid(initPieces);
                        }
                        moveOrder.pop();
                    }
                }
            } 
        }
        for (Piece p : newPieces) {
            if (p.isVisible() && p.isWhite() == isWhite) {
                int[][] valid = p.getValidMoves();
                for (int[] move : valid) {
                    count++;
                    
                    if (p.simMove(move[0], move[1])) {
                        moveOrder.push(new Move(move, p));
                        if (depth < maxDepth) {
                            numMoves += getAllValidMoves(!isWhite, depth, checkValid);
                        }
                        moveOrder.pop();
                        p.unSimMove();
                        if (checkValid) {
                            checkValid(initPieces);
                        }
                    }
                }
            } else {
                removePieces.add(p);
            }
        }
        for (Piece p : removePieces) {
            newPieces.remove(p);
        }
        if (checkValid) {
        checkValid(initPieces);
    }
        return numMoves + count;
    }

    public boolean checkValid(Piece[][] initPieces) {
        boolean same = true;
        for (int i = 0; i < initPieces.length; i++) {
            for (int j = 0; j < initPieces.length; j++) {
                if (initPieces[i][j] == null && pieces[i][j] == null) {
                    continue;
                }
                if (initPieces[i][j] == null) {
                    System.out.println("Wrong for " + i + " " + j);
                    System.out.println("init is null");
                    same = false;
                    break;
                }
                if (pieces[i][j] == null) {
                    System.out.println("Wrong for " + i + " " + j);
                    System.out.println("pieces is null");
                    System.out.println("should be " + initPieces[i][j]);
                    System.out.println(Arrays.deepToString(moveOrder.toArray()));
                    same = false;
                    break;
                }
                if (!initPieces[i][j].equals(pieces[i][j])) {
                    System.out.println("Wrong for " + i + " " + j);
                    System.out.println("supposed to be " + initPieces[i][j] + " but was " + pieces[i][j]);
                    System.out.println(initPieces[i][j].getxPos() + " " + initPieces[i][j].getyPos());
                    System.out.println(pieces[i][j].getxPos() + " " + pieces[i][j].getyPos());
                    same = false;
                    break;
                }
            }
        }
        return same;
    }

    public void highlightSquares(int[][] positions) {
        for (int[] position : positions) {
            int x = position[0];
            int y = position[1];
            Color currentColor = squares[x][y].getBackground();
            if (position == positions[0]) {
                if (currentColor.equals(Globals.BACKGROUND1)) {
                    squares[x][y].setBackground(Globals.BACKGROUND1_SELECTED);
                } else if (currentColor.equals(Globals.BACKGROUND2)) {
                    squares[x][y].setBackground(Globals.BACKGROUND2_SELECTED);
                }
                squares[x][y].revalidate();
                squares[x][y].repaint();
                continue;
            }
            
            if (currentColor.equals(Globals.BACKGROUND1)) {
                squares[x][y].setBackground(Globals.BACKGROUND1_HIGHLIGHTED);
            } else if (currentColor.equals(Globals.BACKGROUND2)) {
                squares[x][y].setBackground(Globals.BACKGROUND2_HIGHLIGHTED);
            }
            squares[x][y].revalidate();
            squares[x][y].repaint();
        }
        
    }

    public void unhighlightCheck() {
        if (checkSquarePos == null) {
            return;
        }
        int x = checkSquarePos[0];
        int y = checkSquarePos[1];
        Color currentColor = squares[x][y].getBackground();
        if (currentColor.equals(Globals.BACKGROUND1_CHECKED)) {
            squares[x][y].setBackground(Globals.BACKGROUND1);
        } else if (currentColor.equals(Globals.BACKGROUND2_CHECKED)) {
            squares[x][y].setBackground(Globals.BACKGROUND2);
        }
        checkSquarePos = null;
        squares[x][y].revalidate();
        squares[x][y].repaint();
    }

    public void highlightCheck() {
        if (whiteTurn && inCheck(false)) {
            int x = bKing.getxPos();
            int y = bKing.getyPos();
            Color currentColor = squares[x][y].getBackground();
            if (currentColor.equals(Globals.BACKGROUND1)) {
                squares[x][y].setBackground(Globals.BACKGROUND1_CHECKED);
            } else if (currentColor.equals(Globals.BACKGROUND2)) {
                squares[x][y].setBackground(Globals.BACKGROUND2_CHECKED);
            }
            checkSquarePos = new int[]{x, y};
            
        }
        
        if (!whiteTurn && inCheck(true)) {
            int x = wKing.getxPos();
            int y = wKing.getyPos();
            Color currentColor = squares[x][y].getBackground();
            if (currentColor.equals(Globals.BACKGROUND1)) {
                squares[x][y].setBackground(Globals.BACKGROUND1_CHECKED);
            } else if (currentColor.equals(Globals.BACKGROUND2)) {
                squares[x][y].setBackground(Globals.BACKGROUND2_CHECKED);
            }
            checkSquarePos = new int[]{x, y};
        }
    }

    public boolean isCheckMate() {
        if (!noMoves()) {
            return false;
        } 
        
        return inCheck(whiteTurn);
    }

    public boolean noMoves() {
        ArrayList<int[]> allValidMoves = new ArrayList<int[]>();
        for (Piece piece : pieceList) {
            if (piece.isWhite() == whiteTurn && piece.isVisible()) {
                
                int[][] validMoves = piece.getValidMoves();
                for (int[] validMove : validMoves) {
                    allValidMoves.add(validMove);
                }
                if (!allValidMoves.isEmpty()) {
                    return false;
                }
                
                
            }
        }
        for (Piece piece : newPieces) {
            if (piece.isWhite() == whiteTurn && piece.isVisible()) {
                int[][] validMoves = piece.getValidMoves();
                for (int[] validMove : validMoves) {
                    allValidMoves.add(validMove);
                }
                if (!allValidMoves.isEmpty()) {
                    return false;
                }
            }
        }
        return true;
    }

    public void unhighlightSquares(int[][] positions) {
        if (positions == null) {
            return;
        }
        for (int[] position : positions) {
            int x = position[0];
            int y = position[1];
            Color currentColor = squares[x][y].getBackground();
            if (position == positions[0]) {
                if (currentColor.equals(Globals.BACKGROUND1_SELECTED)) {
                    squares[x][y].setBackground(Globals.BACKGROUND1);
                } else if (currentColor.equals(Globals.BACKGROUND2_SELECTED)) {
                    squares[x][y].setBackground(Globals.BACKGROUND2);
                }
                squares[x][y].revalidate();
                squares[x][y].repaint();
                continue;
            }
            if (currentColor.equals(Globals.BACKGROUND1_HIGHLIGHTED)) {
                squares[x][y].setBackground(Globals.BACKGROUND1);
            } else if (currentColor.equals(Globals.BACKGROUND2_HIGHLIGHTED)) {
                squares[x][y].setBackground(Globals.BACKGROUND2);
            }
            squares[x][y].revalidate();
            squares[x][y].repaint();
        }

    }

    public void removePiece(Piece piece) {
//        pieceList.remove(piece);
        removePieceFromSquare(piece, piece.getxPos(), piece.getyPos());
        pieces[piece.getxPos()][piece.getyPos()] = null;
        piece.setVisible(false);
        piece.setSelected(false);
        piece.setxPos(-1);
        piece.setyPos(-1);
        halfmoveClock = 0;
        
    }
    public void removePieceFromSquare(Piece piece, int x, int y) {
        if (squares[x][y] == null)
        return;
        squares[x][y].remove(piece.getLabel());
        squares[x][y].revalidate();
        squares[x][y].repaint();
    }

    public void addPieceToSquare(Piece piece, int x, int y) {
        squares[x][y].add(piece.getLabel());
        squares[x][y].revalidate();
        squares[x][y].repaint();
    }

    public void movePiece(Piece piece, int x, int y) {
        removePieceFromSquare(piece, piece.getxPos(), piece.getyPos());
        addPieceToSquare(piece, x, y);
        pieces[x][y] = piece;
        pieces[piece.getxPos()][piece.getyPos()] = null;
        
        piece.setxPos(x);
        piece.setyPos(y);
        unhighlightCheck();
        highlightCheck();
    }

    public boolean inCheck(boolean isWhite) {
        
        if (!isWhite && wKing == null) {
            return false;
        } else if (isWhite && bKing == null) {
            return false;
        }
        for (Piece p : pieceList) {
            if (isWhite && p.isWhite()!= isWhite && p.isVisible() && p.canAttack(wKing.getxPos(), wKing.getyPos())) {
                return true;
            } else if (!isWhite && p.isWhite() != isWhite && p.isVisible() && p.canAttack(bKing.getxPos(), bKing.getyPos())) {
                return true;
            }

        }
        for (Piece p : newPieces) {
            if (isWhite && p.isWhite()!= isWhite && p.isVisible() && p.canAttack(wKing.getxPos(), wKing.getyPos())) {
                return true;
            } else if (!isWhite && p.isWhite() != isWhite && p.isVisible() && p.canAttack(bKing.getxPos(), bKing.getyPos())) {
                return true;
            }
        }
        return false;
    }

    public boolean isAttacked(int x, int y, boolean byWhite) {
        for (Piece p : pieceList) {
            if (p.isWhite() != byWhite && p.canAttack(x, y)) {
                return true;
            }
        }
        for (Piece p : newPieces) {
            if (p.isWhite() != byWhite && p.canAttack(x, y)) {
                return true;
            }
        }
        return false;
    }

    public Piece[][] getPieces() {
        return pieces;
    }

    public void setPieces(Piece[][] pieces) {
        this.pieces = pieces;
    }

    public JPanel[][] getSquares() {
        return squares;
    }

    public void setSquares(JPanel[][] squares) {
        this.squares = squares;
    }

    public ArrayList<Piece> getPieceList() {
        return pieceList;
    }

    public void setPieceList(ArrayList<Piece> pieceList) {
        this.pieceList = pieceList;
    }

    public boolean isWhiteTurn() {
        return whiteTurn;
    }

    public void setWhiteTurn(boolean whiteTurn) {
        this.whiteTurn = whiteTurn;
    }

    public Piece getwKing() {
        return wKing;
    }

    public void setwKing(Piece wKing) {
        this.wKing = wKing;
    }

    public Piece getbKing() {
        return bKing;
    }

    public void setbKing(Piece bKing) {
        this.bKing = bKing;
    }

    public ArrayList<Piece> getNewPieces() {
        return newPieces;
    }

    public void setNewPieces(ArrayList<Piece> newPieces) {
        this.newPieces = newPieces;
    }

    public int[] getCheckSquarePos() {
        return checkSquarePos;
    }

    public void setCheckSquarePos(int[] checkSquarePos) {
        this.checkSquarePos = checkSquarePos;
    }

    public int getHalfmoveClock() {
        return halfmoveClock;
    }

    public void setHalfmoveClock(int halfmoveClock) {
        this.halfmoveClock = halfmoveClock;
    }

    public int getFullmoveClock() {
        return fullmoveClock;
    }

    public void setFullmoveClock(int fullmoveClock) {
        this.fullmoveClock = fullmoveClock;
    }

    public ArrayList<String> getFenList() {
        return fenList;
    }

    public void setFenList(ArrayList<String> fenList) {
        this.fenList = fenList;
    }

    public Stack<Move> getMoveOrder() {
        return moveOrder;
    }

    public void setMoveOrder(Stack<Move> moveOrder) {
        this.moveOrder = moveOrder;
    }
}
