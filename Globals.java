import java.awt.Color;
import java.awt.Image;

import javax.swing.ImageIcon;

public class Globals {
    public static final int COLS = 8;
    public static final int ROWS = 8;
    public static final int WIDTH = 800;
    public static final int HEIGHT = 800;
    public static final int OFFSET_AMOUNT = 10;
    public static final String STARTING_BOARD_FEN = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";
    public static final String TEST_FEN           = "r3k2r/p1ppqpb1/bn2pnp1/3PN3/1p2P3/2N2Q1p/PPPBBPPP/R3K2R w KQkq - 0 1";
    public static final int MAX_SEARCH_DEPTH = 3;

    public static final Color BACKGROUND1 = new Color(0, 150, 150);
    public static final Color BACKGROUND2 = new Color(64, 64, 64);
    public static final Color BACKGROUND1_HIGHLIGHTED = new Color(0, 150, 200);
    public static final Color BACKGROUND2_HIGHLIGHTED = new Color(64, 64, 114);
    public static final Color BACKGROUND1_SELECTED = new Color(255, 255, 0);
    public static final Color BACKGROUND2_SELECTED = new Color(255, 255, 1);
    public static final Color BACKGROUND1_CHECKED = new Color(125, 0, 0);
    public static final Color BACKGROUND2_CHECKED = new Color(126, 0, 0);
    public static final ImageIcon BLACK_PAWN_PNG   = new ImageIcon(new ImageIcon("resources/png/pieces/bp.png").getImage().getScaledInstance(WIDTH / COLS - OFFSET_AMOUNT, HEIGHT / ROWS - OFFSET_AMOUNT, Image.SCALE_SMOOTH));
    public static final ImageIcon WHITE_PAWN_PNG   = new ImageIcon(new ImageIcon("resources/png/pieces/wp.png").getImage().getScaledInstance(WIDTH / COLS - OFFSET_AMOUNT, HEIGHT / ROWS - OFFSET_AMOUNT, Image.SCALE_SMOOTH));
    public static final ImageIcon BLACK_KNIGHT_PNG = new ImageIcon(new ImageIcon("resources/png/pieces/bn.png").getImage().getScaledInstance(WIDTH / COLS - OFFSET_AMOUNT, HEIGHT / ROWS - OFFSET_AMOUNT, Image.SCALE_SMOOTH));
    public static final ImageIcon WHITE_KNIGHT_PNG = new ImageIcon(new ImageIcon("resources/png/pieces/wn.png").getImage().getScaledInstance(WIDTH / COLS - OFFSET_AMOUNT, HEIGHT / ROWS - OFFSET_AMOUNT, Image.SCALE_SMOOTH));
    public static final ImageIcon BLACK_BISHOP_PNG = new ImageIcon(new ImageIcon("resources/png/pieces/bb.png").getImage().getScaledInstance(WIDTH / COLS - OFFSET_AMOUNT, HEIGHT / ROWS - OFFSET_AMOUNT, Image.SCALE_SMOOTH));
    public static final ImageIcon WHITE_BISHOP_PNG = new ImageIcon(new ImageIcon("resources/png/pieces/wb.png").getImage().getScaledInstance(WIDTH / COLS - OFFSET_AMOUNT, HEIGHT / ROWS - OFFSET_AMOUNT, Image.SCALE_SMOOTH));
    public static final ImageIcon BLACK_ROOK_PNG   = new ImageIcon(new ImageIcon("resources/png/pieces/br.png").getImage().getScaledInstance(WIDTH / COLS - OFFSET_AMOUNT, HEIGHT / ROWS - OFFSET_AMOUNT, Image.SCALE_SMOOTH));
    public static final ImageIcon WHITE_ROOK_PNG   = new ImageIcon(new ImageIcon("resources/png/pieces/wr.png").getImage().getScaledInstance(WIDTH / COLS - OFFSET_AMOUNT, HEIGHT / ROWS - OFFSET_AMOUNT, Image.SCALE_SMOOTH));
    public static final ImageIcon BLACK_QUEEN_PNG  = new ImageIcon(new ImageIcon("resources/png/pieces/bq.png").getImage().getScaledInstance(WIDTH / COLS - OFFSET_AMOUNT, HEIGHT / ROWS - OFFSET_AMOUNT, Image.SCALE_SMOOTH));
    public static final ImageIcon WHITE_QUEEN_PNG  = new ImageIcon(new ImageIcon("resources/png/pieces/wq.png").getImage().getScaledInstance(WIDTH / COLS - OFFSET_AMOUNT, HEIGHT / ROWS - OFFSET_AMOUNT, Image.SCALE_SMOOTH));
    public static final ImageIcon BLACK_KING_PNG   = new ImageIcon(new ImageIcon("resources/png/pieces/bk.png").getImage().getScaledInstance(WIDTH / COLS - OFFSET_AMOUNT, HEIGHT / ROWS - OFFSET_AMOUNT, Image.SCALE_SMOOTH));
    public static final ImageIcon WHITE_KING_PNG   = new ImageIcon(new ImageIcon("resources/png/pieces/wk.png").getImage().getScaledInstance(WIDTH / COLS - OFFSET_AMOUNT, HEIGHT / ROWS - OFFSET_AMOUNT, Image.SCALE_SMOOTH));
    
    public static final int NUM_PIECES = 10;
}
