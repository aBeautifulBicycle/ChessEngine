import javax.swing.JFrame;
import javax.swing.JPanel;

import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

public class Window {
    private JFrame window;
    private Board board;
    private boolean gameOver = false;
    public Window(int width, int height) {
        initWindow(width, height);

    }

    public void initWindow(int width, int height) {
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
        fenToPosition(Globals.TEST_FEN);


    }

    public void fenToPosition(String fen) {
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
        gameListener();
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

    public void gameListener() {
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
                                    if (piece.tryMove(finalRow, finalCol)) {
                                        moved = true;
                                        System.out.println(piece.getName() + " moved to: (" + finalRow + ", " + finalCol + ")");
                                        
                                        
                                        if (!piece.isWhite()) {
                                             board.setFullmoveClock(board.getFullmoveClock() + 1);
                                        }
                                        board.setHalfmoveClock(board.getHalfmoveClock() + 1);
                                        int gameState = board.getGameState();
                                        if (gameState != 0) {
                                            gameOver = true;
                                        }
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
                                        }

                                        
                                    }
                                    
                                }
                            }
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
                            
                        } else {
                            if (gameOver) {
                                gameOver = false;
                                fenToPosition(Globals.STARTING_BOARD_FEN);
                            }
                            for (Piece p : board.getPieceList()) {
                                if (p.isSelected()) {
                                    p.unhighlightValidMoves();
                                }
                                p.setSelected(false);
                            }
                            
                        }
                        if (!board.getNewPieces().isEmpty()) {
                            board.getPieceList().addAll(board.getNewPieces());
                            board.setNewPieces(new ArrayList<>());
                        }
                        if (moved) {
                            String currentFen = board.getFen();
                            String fullFen = currentFen;
                            if (board.isWhiteTurn()) {
                                fullFen += " w";
                            } else {
                                fullFen += " b";
                            }
                            fullFen += " - -";
                            fullFen += " " + board.getHalfmoveClock() + " " + board.getFullmoveClock();
                            System.out.println(fullFen);
                            board.getFenList().add(currentFen);
                            
                            long start = System.nanoTime();
                            System.out.println(board.exploreAllPositions(board.isWhiteTurn(), 0, true));
                            board.evaluatePosition(board.isWhiteTurn(), Globals.MAX_SEARCH_DEPTH);
                            System.out.println(board.getNumMoves());
                            board.setNumMoves(0);
                            long end = System.nanoTime();
                            System.out.println("Time taken (with check): " + (end - start) / 1_000_000 + "ms");
                            
                            start = System.nanoTime();
                            System.out.println(board.exploreAllPositions(board.isWhiteTurn(), 0, false));
                            board.evaluatePosition(board.isWhiteTurn(), Globals.MAX_SEARCH_DEPTH);
                            System.out.println(board.getNumMoves());
                            board.setNumMoves(0);
                            end = System.nanoTime();
                            System.out.println("Time taken (no check): " + (end - start) / 1_000_000 + "ms");
                        }
                    }
                });
            }
        }
    }
}
