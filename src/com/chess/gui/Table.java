package com.chess.gui;

import com.chess.engine.board.Board;
import com.chess.engine.board.BoardUtils;
import com.chess.engine.board.Move;
import com.chess.engine.board.Tile;
import com.chess.engine.pieces.Piece;
import com.chess.engine.player.MoveTransition;
import com.google.common.collect.Lists;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.Dimension;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;


public class Table {

    public final BoardPanel boardPanel;
    public  Board chessBoard;

    public Tile sourceTile;
    public Tile destinationTile;
    public Piece humanMovedPiece;
    public BoardDirection boardDirection;
    private boolean highlightLegalMoves;

    private final Color darkTileColor = Color.decode("#4d2600");
    private final Color lightTileColor = Color.decode("#ffffe6");

    public final Dimension OUTER_FRAME_DIMENSION = new Dimension(650, 650);
    public static final Dimension BOARD_PANEL_DIMENSION = new Dimension(260, 260);
    public static final Dimension TILE_PANEL_DIMENSION = new Dimension(30, 30);

    public String defaultPieceImagesPath = "art/holywarriors/";


    public Table() {    // this is the constructor

        // create the board
        this.chessBoard = Board.createStandardBoard();

        // create the main game frame
        JFrame gameFrame = new JFrame("JChess");
        gameFrame.setLayout(new BorderLayout());
        gameFrame.setSize(OUTER_FRAME_DIMENSION);

        // create a menu bar
        final JMenuBar tableMenuBar = createTableMenuBar();
        gameFrame.setJMenuBar(tableMenuBar);

        // create the board panel
        this.boardPanel = new BoardPanel();                       
        gameFrame.add(this.boardPanel, BorderLayout.CENTER);
        gameFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        gameFrame.setVisible(true);

        // set the default board direction
        this.boardDirection = BoardDirection.NORMAL;

        // default highlight legal moves
        this.highlightLegalMoves = false;


    }
//:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::.::::::::::::::::::::::::::::::\\    

    public JMenuBar createTableMenuBar() {
        final JMenuBar tableMenuBar = new JMenuBar();
        tableMenuBar.add(createFileMenu());
        tableMenuBar.add(createPreferencesMenu());
        return tableMenuBar;
    }

    public JMenu createFileMenu() {
        final JMenu fileMenu = new JMenu("File");
        final JMenuItem openPGN = new JMenuItem("Load PGN File");
        openPGN.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("Open up that pgn file!");
            }
        });
        fileMenu.add(openPGN);

        final JMenuItem exitMenuItem = new JMenuItem("Exit");
        exitMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
        fileMenu.add(exitMenuItem);
        return fileMenu;
    }

    private JMenu createPreferencesMenu() {

        final JMenu preferencesMenu = new JMenu("Preferences");
        final JMenuItem flipBoardMenuItem = new JMenuItem("Flip Board");
        flipBoardMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                boardDirection = boardDirection.opposite();
                boardPanel.drawBoard(chessBoard);
            }
        });
        preferencesMenu.add(flipBoardMenuItem);
        preferencesMenu.addSeparator();

        final JCheckBoxMenuItem legalMoveHighlighterCheckbox = new JCheckBoxMenuItem("Highlight Legal Moves", false);
            legalMoveHighlighterCheckbox.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    highlightLegalMoves = legalMoveHighlighterCheckbox.isSelected();
                }
            });
            preferencesMenu.add(legalMoveHighlighterCheckbox);

            return preferencesMenu;
        }

    public enum BoardDirection {
        NORMAL {
            @Override
            List<TilePanel> traverse(final List<TilePanel> boardTiles) {
                return boardTiles;
            }
            @Override
            BoardDirection opposite() { return FLIPPED; }

        },
        FLIPPED {
            @Override
            List<TilePanel> traverse(final List<TilePanel> boardTiles) {
                return Lists.reverse(boardTiles);
            }
            @Override
            BoardDirection opposite() { return NORMAL; }
        };

        abstract List<TilePanel> traverse(final List<TilePanel> boardTiles);
        abstract BoardDirection opposite();
    }


    // this is visual component representing the board
    private class BoardPanel extends JPanel{
        
        final List<TilePanel> boardTiles;
        // constructor
        BoardPanel() {
            super(new GridLayout(8, 8));
            this.boardTiles = new ArrayList<>();
            for(int i = 0; i < BoardUtils.NUM_TILES; i++) {
                final TilePanel tilePanel = new TilePanel(this, i); // this is the BoardPanel object, i = tileId
                this.boardTiles.add(tilePanel);
                add(tilePanel);
            }
            setPreferredSize(BOARD_PANEL_DIMENSION);
            validate();
        }
        
        void drawBoard(final Board board) {
            removeAll();
            for(final TilePanel tilePanel : boardDirection.traverse(boardTiles)) {
                tilePanel.drawTile(board);
                add(tilePanel);
            }
            validate();
            repaint();
        }
        // TODO ??
    }

    // this is visual component representing the tile
    private class TilePanel extends JPanel{
        private final int tileId;
        
        TilePanel(final BoardPanel boardPanel, final int tileId) {
            super(new GridBagLayout());
            this.tileId = tileId;
            setPreferredSize(TILE_PANEL_DIMENSION); 
            assignTileColor();
            assignTilePieceIcon(chessBoard);

            addMouseListener(new MouseListener() {
                @Override
                public void mouseClicked(final MouseEvent e) {

                    if(SwingUtilities.isRightMouseButton(e)) {
                    
                        sourceTile = null;
                        humanMovedPiece = null;

                    } else if(SwingUtilities.isLeftMouseButton(e)) {
                        if (sourceTile == null) {
                            sourceTile = chessBoard.getTile(tileId);
                            humanMovedPiece = sourceTile.getPiece();
                            if(humanMovedPiece == null) {
                                sourceTile = null;
                            }
                            System.out.println("sourceTile is " + sourceTile);

                        } else {
                            destinationTile = chessBoard.getTile(tileId);
                            final Move move = Move.MoveFactory.createMove(chessBoard, 
                                                                          sourceTile.getTileCoordinate(),
                                                                          destinationTile.getTileCoordinate());
                            final MoveTransition transition = chessBoard.currentPlayer().makeMove(move);
                            if(transition.getMoveStatus().isDone()) {
                                chessBoard = transition.getTransitionBoard();
                                // TODO add a move that was made to the move log 
                            }

                            sourceTile = null;
                            humanMovedPiece = null;
                        }

                        SwingUtilities.invokeLater(() -> {
                            boardPanel.drawBoard(chessBoard);
                            System.out.println("Your mouse clicked on tile " + tileId);
                            System.out.println("selected piece is" + humanMovedPiece);
                        });
                    }
                }

                @Override
                public void mouseEntered(MouseEvent e) {}

                 @Override
                public void mouseExited(MouseEvent e) {}

                @Override
                public void mousePressed(MouseEvent e) {}

                @Override
                public void mouseReleased(MouseEvent e) {}
                
            });
            validate();
        }

        void drawTile(final Board board) {
            assignTileColor();
            assignTilePieceIcon(board);
            highlightLegals(board);
            validate();
            repaint();
        }

        private void highlightLegals(final Board board) {
            if(highlightLegalMoves) {
                for(final Move move : pieceLegalMoves(board)) {
                    if(move.getDestinationCoordinate() == this.tileId) {
                        try {
                            add(new JLabel(new ImageIcon(ImageIO.read(new File("art/misc/green_dot.png")))));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }

        private Collection<Move> pieceLegalMoves(final Board board) {
            if(humanMovedPiece != null && humanMovedPiece.getPieceAlliance() == board.currentPlayer().getAlliance()) {
                return humanMovedPiece.calculateLegalMoves(board);
            }
            return Collections.emptyList();
        }

        private void assignTilePieceIcon(final Board board) {
            this.removeAll();
            if(board.getTile(this.tileId).isTileOccupied()) {
                try {
                    final BufferedImage image =
                            ImageIO.read(new File(defaultPieceImagesPath +
                                    board.getTile(this.tileId).getPiece().getPieceAlliance().toString().substring(0, 1) +
                                    board.getTile(this.tileId).getPiece().toString() + ".gif"));

                    add(new JLabel(new ImageIcon(image)));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    
        private void assignTileColor() {
            if(BoardUtils.EIGHTH_RANK[this.tileId] ||
               BoardUtils.SIXTH_RANK[this.tileId] ||
               BoardUtils.FOURTH_RANK[this.tileId] ||
               BoardUtils.SECOND_RANK[this.tileId]) {
                setBackground(this.tileId % 2 == 0 ? lightTileColor : darkTileColor);
                // lightTileColor and darkTileColor are static final fields
                // you need to define them in the Table class
                // like so: private static final Color lightTileColor = Color.decode("#FFFACD");
            } else if(BoardUtils.SEVENTH_RANK[this.tileId] ||
                      BoardUtils.FIFTH_RANK[this.tileId] ||
                      BoardUtils.THIRD_RANK[this.tileId] ||
                      BoardUtils.FIRST_RANK[this.tileId]) {
                setBackground(this.tileId % 2 != 0 ? lightTileColor : darkTileColor);
            }
        }
    }    
}
 
