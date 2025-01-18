import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Random;

public class Window {
    private JFrame window;
    private Board board;
    private boolean gameOver = false;
    public Window(int width, int height, int gameType) {
        initWindow(width, height, gameType);

    }

    public void initWindow(int width, int height, int gameType) {
        JPanel[][] squares;
        ArrayList<Piece> pieces = new ArrayList<>();
        Piece[][] pieceGrid;
        pieceGrid = new Piece[Globals.ROWS][Globals.COLS];
        squares = new JPanel[Globals.ROWS][Globals.COLS];
        
        
        window = new JFrame("Chessboard");
        window.setLayout(new GridLayout(8, 8));
        window.setResizable(false);
        


        for (int row = 0; row < Globals.ROWS; row++) {
            for (int col = 0; col < Globals.COLS; col++) {
                JPanel square = new JPanel();
                
                if ((row + col) % 2 == 0) {
                    square.setBackground(Globals.BACKGROUND1);
                } else {
                    square.setBackground(Globals.BACKGROUND2);
                }


                window.add(square);
                squares[row][col] = square;
            }
        }

        window.setSize(width, height);
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        window.setVisible(true);
        board = new Board(pieceGrid, squares, pieces);
        fenToPosition(Globals.TEST_FEN, gameType);
        if (gameType == 2) {
            engineMakeMove(Globals.MAX_SEARCH_DEPTH);
            addNewPiecesToBoard();
        }
        if (gameType == 3) {
            long start = System.nanoTime();
            int maxDepth = Globals.MAX_SEARCH_DEPTH;
            while (!gameOver) {
                engineMakeMove(maxDepth);
                addNewPiecesToBoard();
                
            }
            long end = System.nanoTime();
            System.out.println("Time taken: " + (end - start) / 1_000_000 + "ms");
            
            
        }


    }

    public void fenToPosition(String fen, int gameType) {
        // Reset the board
        for (int row = 0; row < Globals.ROWS; row++) {
            for (int col = 0; col < Globals.COLS; col++) {
                Piece potPiece = board.getPieces()[row][col];
                if (potPiece != null) {
                    board.removePiece(potPiece);
                }
            }
        }
        String[] fenSplit = fen.split(" ");
        String castlingRights = fenSplit[2];
        String enpassantSquare = fenSplit[3];
        
        board.unhighlightCheck();
        board.setPieceList(new ArrayList<>());
        if (fenSplit[1].equals("w"))
        board.setWhiteTurn(true);
        if (fenSplit[1].equals("b"))
        board.setWhiteTurn(false);
        
        String[] rows = fenSplit[0].split("/");
        for (int col = 0; col < rows.length; col++) {
            int rowsSkipped = 0;
            for (int row = 0; row < Globals.COLS; row++) {
                char c = rows[col].charAt(row - rowsSkipped);
                if (Character.isDigit(c)) {
                    row += Integer.parseInt(Character.toString(c)) - 1;
                    rowsSkipped += Integer.parseInt(Character.toString(c)) - 1;
                    continue;
                }
                Piece currentPiece;
                switch (c) {
                    case 'r':
                        currentPiece = new Rook(Globals.BLACK_ROOK_PNG, "r", col, row);
                        if (row == 0 && castlingRights.contains("q") && col == 0) {
                            currentPiece.setCanCastle(true);
                        } else if (row == Globals.COLS - 1 && castlingRights.contains("k") && col == 0) {
                            currentPiece.setCanCastle(true);
                        } else {
                            currentPiece.setCanCastle(false);
                        }
                        break;
                    case 'n':
                        currentPiece = new Knight(Globals.BLACK_KNIGHT_PNG, "n", col, row);
                        break;
                    case 'b':
                        currentPiece = new Bishop(Globals.BLACK_BISHOP_PNG, "b", col, row);
                        break;
                    case 'q':
                        currentPiece = new Queen(Globals.BLACK_QUEEN_PNG, "q", col, row);
                        break;
                    case 'k':
                        currentPiece = new King(Globals.BLACK_KING_PNG, "k", col, row);
                        board.setbKing(currentPiece);
                        break;
                    case 'p':
                        currentPiece = new Pawn(Globals.BLACK_PAWN_PNG, "p", col, row);
                        break;
                    case 'R':
                        currentPiece = new Rook(Globals.WHITE_ROOK_PNG, "R", col, row);
                        if (row == 0 && castlingRights.contains("Q") && col == Globals.COLS - 1) {
                            currentPiece.setCanCastle(true);
                        } else if (row == Globals.COLS - 1 && castlingRights.contains("K") && col == Globals.COLS - 1) {
                            currentPiece.setCanCastle(true);
                        } else {
                            currentPiece.setCanCastle(false);
                        }
                        break;
                    case 'N':
                        currentPiece = new Knight(Globals.WHITE_KNIGHT_PNG, "N", col, row);
                        break;
                    case 'B':
                        currentPiece = new Bishop(Globals.WHITE_BISHOP_PNG, "B", col, row);
                        break;
                    case 'Q':
                        currentPiece = new Queen(Globals.WHITE_QUEEN_PNG, "Q", col, row);
                        break;
                    case 'K':
                        currentPiece = new King(Globals.WHITE_KING_PNG, "K", col, row);
                        board.setwKing(currentPiece);
                        break;
                    case 'P':
                        currentPiece = new Pawn(Globals.WHITE_PAWN_PNG, "P", col, row);
                        break;
                    default:
                        throw new IllegalArgumentException("FEN entered incorrectly!");

                }
                board.getPieces()[col][row] = currentPiece;
                currentPiece.setVisible(true);
                if (Character.isLowerCase(c)) {
                    currentPiece.setWhite(false);
                } else {
                    currentPiece.setWhite(true);
                }
                
                currentPiece.setBoard(board);
                board.getPieceList().add(currentPiece);
                
                
                
            }
        }
        gameListener(gameType);
        if (enpassantSquare != null && !enpassantSquare.equals("-")) {
            int height = Globals.COLS - Integer.parseInt(enpassantSquare.substring(1, 2));
            if (height == 2) {
                height = 3;
            } else if (height == Globals.COLS - 3) {
                height = Globals.COLS - 4;
            }
            board.getPieces()[height][enpassantSquare.charAt(0) - 'a'].setEnpassantable(true);
        }
        board.setHalfmoveClock(Integer.parseInt(fenSplit[4]));
        board.setFullmoveClock(Integer.parseInt(fenSplit[5]));

        window.repaint();


    }

    public void gameListener(int gameType) {
        for (int col = 0; col < Globals.COLS; col++) {
            for (int row = 0; row < Globals.ROWS; row++) {

                board.getSquares()[col][row].removeAll();
                if (board.getPieces()[col][row] != null)
                board.getSquares()[col][row].add(board.getPieces()[col][row].getLabel());


                int finalRow = col;
                int finalCol = row;
                

                board.getSquares()[col][row].addMouseListener(new MouseAdapter() {
                    @Override
                    public void mousePressed(MouseEvent e) {
                    if (gameType == 0) {
                        processInput(finalRow, finalCol, e, gameType);
                        addNewPiecesToBoard();
                    } else if (gameType == 1 && board.isWhiteTurn()) {
                        if (processInput(finalRow, finalCol, e, gameType)) {
                            window.repaint();

                            // Run the computer's move on a separate thread
                            new Thread(() -> {
                                engineMakeMove(Globals.MAX_SEARCH_DEPTH);
                                addNewPiecesToBoard();
                                SwingUtilities.invokeLater(window::repaint);
                            }).start();
                        }
                    } else if (gameType == 2 && !board.isWhiteTurn()) {
                        if (processInput(finalRow, finalCol, e, gameType)) {
                            window.repaint();

                            // Run the computer's move on a separate thread
                            new Thread(() -> {
                                engineMakeMove(Globals.MAX_SEARCH_DEPTH);
                                addNewPiecesToBoard();
                                SwingUtilities.invokeLater(window::repaint);
                            }).start();
                        }
                    }
                }
                });
            }
        }
    }

    public void engineMakeMove(int maxDepth) {
        window.repaint();
        Piece[][] cleanBoard = new Piece[board.getPieces().length][];
        for (int i = 0; i < cleanBoard.length; i++) {
            cleanBoard[i] = board.getPieces()[i].clone();
        }
        Move move = board.evaluatePosition(board.isWhiteTurn(), maxDepth, 0, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY);
        
        
        System.out.println("took " + board.getNumMoves() + " moves to get result");
        board.setNumMoves(0);
        board.setVisitedPositions(new HashSet<>());
        
        board.setPieces(cleanBoard);
        if (move == null) {
            return;
        }
        System.out.println("Engine move: " + move);
        if (!moveAttempt(move)) {
            System.out.println("Engine attempted to make an invalid move.");
            Random rand = new Random();
            while (true) {
                ArrayList<Move> moves = board.getAllValidMoves(board.isWhiteTurn());
                if (moves.isEmpty()) {
                    break;
                }
                Move randomMove = moves.get(rand.nextInt(moves.size()));
                if (moveAttempt(randomMove)) {
                    System.out.println("Doing random move " + randomMove + " instead");
                    break;
                }
            }
        }
    }

    public boolean processInput(int finalRow, int finalCol, MouseEvent e, int gameType) {
        boolean moved = false;
        if (e.getButton() == MouseEvent.BUTTON1) {
            Iterator<Piece> iterator = board.getPieceList().iterator();
            while (iterator.hasNext()) {
                Piece piece = iterator.next(); 
                if (!((board.isWhiteTurn() && piece.isWhite()) || (!piece.isWhite() && !board.isWhiteTurn()))) {
                    continue;
                }
                if (piece.getxPos() == finalRow && piece.getyPos() == finalCol && piece.isVisible()) {
                    System.out.println(piece.getName() + " selected at: (" + finalRow + ", " + finalCol + ")");
                    piece.setSelected(true);
                } else if (piece.isSelected()) { // Check if the object is selected
                    // Move the object to the clicked square
                    moved = moveAttempt(piece, finalRow, finalCol);
                    
                }
            }
           processRemoval();
            
        } else {
            printBoardState();
            if (gameOver) {
                gameOver = false;
                fenToPosition(Globals.STARTING_BOARD_FEN, gameType);
            }
            deselect();
            
        }
        return moved;
    }

    public boolean moveAttempt(Piece piece, int finalRow, int finalCol) {
        boolean moved = false;
        if (piece.tryMove(finalRow, finalCol)) {
            moved = true;
            System.out.println(piece.getName() + " moved to: (" + finalRow + ", " + finalCol + ")");
            
            
            updateMoveClocks(piece.isWhite());
            int gameState = board.getGameState();
            if (gameState != 0) {
                gameOver = true;
                printGameState(gameState);
                
            }
            printBoardState();
            board.getFenList().add(board.getFen());

            
        }
        return moved;
    }

    public boolean moveAttempt(Move move) {
        boolean moved = false;
        Piece piece = move.getMovePiece();
        int finalRow = move.getMoveLocation()[0];
        int finalCol = move.getMoveLocation()[1];
        if (piece.tryMove(finalRow, finalCol)) {
            moved = true;
            System.out.println(piece.getName() + " moved to: (" + finalRow + ", " + finalCol + ")");
            
            
            updateMoveClocks(piece.isWhite());
            int gameState = board.getGameState();
            if (gameState != 0) {
                gameOver = true;
                printGameState(gameState);
            }
            printBoardState();
            board.getFenList().add(board.getFen());

            
        }
        return moved;
    }

    public void updateMoveClocks(boolean isWhite) {
        if (!isWhite) {
            board.setFullmoveClock(board.getFullmoveClock() + 1);
    }
    board.setHalfmoveClock(board.getHalfmoveClock() + 1);
    }

    public void printGameState(int gameState) {
        if (gameState == 1) {
            System.out.println("Checkmate! Game is over.");
            System.out.println("White wins!");
            
        } else if (gameState == 2) {
            System.out.println("Checkmate! Game is over.");
            System.out.println("Black wins!");
        } else if (gameState == 3) {
            System.out.println("Stalemate! Game is over.");
        } else if (gameState == 4) {
                System.out.println("Threefold repetition! Game is over.");
        } else if (gameState == 5) {
                System.out.println("Fifty-move rule! Game is over.");
        } else if (gameState == 6) {
                System.out.println("Insufficient material! Game is over.");
        } else if (gameState == 0) {
            System.out.println("Game is still in progress.");
        }
        System.out.println(board.getNumMoves());
    }

    public void processRemoval() {
        ArrayList<Piece> removePieces = new ArrayList<>();
        for (Piece p : board.getPieceList()) {
            if (!p.isVisible()) {
                removePieces.add(p);
            }
        }
        board.getPieceList().removeAll(removePieces);
        for (Piece p : board.getPieceList()) {
            if (p.isSelected() && p.isVisible()) {
                p.highlightValidMoves();
            }
        }
    }

    public void deselect() {
        for (Piece p : board.getPieceList()) {
            if (p.isSelected()) {
                p.unhighlightValidMoves();
            }
            p.setSelected(false);
        }
    }

    public void addNewPiecesToBoard() {
        if (!board.getNewPieces().isEmpty()) {
            board.getPieceList().addAll(board.getNewPieces());
            board.setNewPieces(new ArrayList<>());
        }
    }

    public void printBoardState() {
        System.out.println(getFullFen());
        System.out.println(board);
    }

    public String getFullFen() {
        String currentFen = board.getFen();
        String fullFen = currentFen;
        if (board.isWhiteTurn()) {
            fullFen += " w";
        } else {
            fullFen += " b";
        }
        fullFen += " - -";
        fullFen += " " + board.getHalfmoveClock() + " " + board.getFullmoveClock();
        return fullFen;
    }
}
