package game2048;

/**
 * This file defines the graphics window for the 2048 game.
 * 
 * This program uses a mixture of JavaFX and Java SwingX. The main program
 * uses JavaFX, while the user interactive input boar and output saving board
 * use Java SwingX. It uses Thread.sleep(), which is not the best solution,
 * but waits for the file handling to be finished before the program runs or
 * exits.
 * 
 */
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionListener;
import java.awt.event.KeyListener;
import java.io.IOException;
import javafx.application.*;
import javafx.scene.*;
import javafx.scene.paint.*;
import javafx.scene.shape.*;
import javafx.scene.layout.*;
import javafx.stage.*;
import javafx.event.*;
import javafx.scene.input.*;
import javafx.scene.text.*;
import javafx.geometry.*;
import javafx.scene.effect.BlendMode;
import javax.swing.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.animation.Animation;
import javafx.animation.ScaleTransition;
import javafx.util.Duration;

/**
 *
 * @author David
 *
 * @TODO ANIMATION!
 * @TODO SOUND!
 * @TODO Resizable Game Window
 * @TODO Support game boards other than 4x4
 *
 */
public class Game2048 extends Application {

    // The filename for where to save the Board
    private String outputBoard;

    // The 2048 Game Board
    private Board board;

    // GUI objects for game window
    private StackPane layout;
    private GridPane pane;
    private Text titleText, scoreText;

    // InputFileHandler must finish before the pane is created
    private InputFileHandler ifh;

    // To hold all tiles and their corresponding values as text objects
    private final ArrayList<Tile> tiles = new ArrayList<>();
    private final ArrayList<TileText> tileTexts = new ArrayList<>();
    
    private ScaleTransition transition;

    /**
     * Opens up the game window.
     *
     * @param primaryStage main, top level container
     * @throws java.lang.InterruptedException for thread's sleep
     */
    @Override
    public void start(Stage primaryStage) throws InterruptedException {
        // Process Arguments and Initialize the Game Board
        processArgs(getParameters().getRaw().toArray(new String[0]));

        // Wait until the file handler window is done ... not the best way
        while (!ifh.isFinished()) {
            Thread.sleep(1);
        }
        
        // Create the pane that will hold all of the visual objects
        pane = new GridPane();
        pane.setAlignment(Pos.CENTER);
        pane.setPadding(new Insets(11.5, 12.5, 13.5, 14.5));
        pane.setStyle("-fx-background-color: rgb(187, 173, 160)");
        // Set the spacing between the Tiles
        pane.setHgap(15);
        pane.setVgap(15);

        // 2048 title (of the game)
        titleText = new Text();
        titleText.setFont(Font.font("Comic Sans MS",
                FontWeight.BOLD, GameConstants.TEXT_SIZE_LOW));
        titleText.setText("2048");

        // Score board of the game
        scoreText = new Text();
        scoreText.setFont(Font.font("Comic Sans MS",
                FontWeight.BOLD, GameConstants.TEXT_SIZE_HIGH));
        scoreText.setText("Score: " + board.getScore());

        // center align each text object
        GridPane.setHalignment(titleText, HPos.CENTER);
        GridPane.setValignment(titleText, VPos.CENTER);
        GridPane.setHalignment(scoreText, HPos.CENTER);
        GridPane.setValignment(scoreText, VPos.CENTER);

        // Adds text objects to GridPane - span two columns
        pane.add(titleText, 0, 0, 2, 1);
        pane.add(scoreText, 2, 0, 2, 1);

        // Create a Stack Pane (will hold GridPane and Game Over pane)
        layout = new StackPane();
        layout.getChildren().add(pane);

        // JavaFX Applet Window
        Scene scene = new Scene(layout);
        primaryStage.setTitle("Game2048");
        primaryStage.setScene(scene);
        primaryStage.setWidth(
                (GameConstants.TILE_WIDTH + 20) * board.GRID_SIZE);
        primaryStage.setHeight(
                (GameConstants.TILE_WIDTH + 20) * (board.GRID_SIZE + 1));
        primaryStage.show();
        primaryStage.requestFocus();

        // JavaFX's version of onSetDefaultOperation
        primaryStage.setOnCloseRequest((WindowEvent t) -> {
            if (t.getEventType() == WindowEvent.WINDOW_CLOSE_REQUEST) {
                
                // If the game is over, it saves by itself anyway
                if (board.isGameOver()) {
                    System.exit(0);
                }
                
                handleInteractiveSaveBoard();
            }
        });
        
        // Listen for keyboard input
        scene.setOnKeyPressed(new MyKeyHandler());

        // Create the game's grid
        for (int index = 0; 
                index < (board.GRID_SIZE * board.GRID_SIZE); index++) {

            // Creating a new tile at given index stored in an ArrayList
            tiles.add(new Tile());

            // Creating Text objects that correspond to their tile
            tileTexts.add(new TileText());
            
            // Get the corresponding value from the board grid & update the tile
            // text that corresponds to that value
            // i.e. index 0 = [0, 0], index 4 = [1, 0]
            int tileVal = board.getGrid()
                    [index % board.GRID_SIZE][index / board.GRID_SIZE];

            // Update the text displayed on the tile
            if (tileVal > 0) {
                tileTexts.get(index).setText(tileVal + "");
                tiles.get(index).setFill(tileVal == 2 ? 
                        GameConstants.COLOR_2 : GameConstants.COLOR_4);
            }

            // Empty rectangle, basically the layout of the board (no animation)
            pane.add(new Tile(),
                    index % board.GRID_SIZE, index / board.GRID_SIZE + 1);

            // Add the tiles / text objects to the GridPane at its location
            pane.add(tiles.get(index),
                    index % board.GRID_SIZE, index / board.GRID_SIZE + 1);
            pane.add(tileTexts.get(index),
                    index % board.GRID_SIZE, index / board.GRID_SIZE + 1);
            GridPane.setHalignment(tileTexts.get(index), HPos.CENTER);
        }

        // Update all tiles and colors of the game board
        updateGUIBoard();
    }

    /**
     * The method used to process the command line arguments.
     *
     * @param args
     */
    private void processArgs(String[] args) {

        // The filename for where to load the Board
        String inputBoard = null;

        // The size of the Board
        int boardSize = 0;

        // Set the default output file if none specified
        if (outputBoard == null) {
            outputBoard = "2048.board";
        }
        // Set the default Board size if none specified or less than 2
        if (boardSize < 2) {
            boardSize = 4;
        }

        // Initialize the Game Board
        try {
            ifh = new InputFileHandler(boardSize);
        } catch (Exception e) {
            System.out.println(e.getClass().getName()
                    + " was thrown while creating a "
                    + "Board from file " + inputBoard);
            System.out.println("Either your Board(String, Random) "
                    + "Constructor is broken or the file isn't "
                    + "formated correctly");
            System.exit(-1);
        }
    }

    /**
     * Updates the tiles of the board.
     */
    private void updateGUIBoard() {

        // Loop through the new updated grid and update the whole game
        for (int index = 0;
                index < Math.pow(board.GRID_SIZE, 2); index++) {

            // Remove all children from the pane (one at a time)
            pane.getChildren().remove(tiles.get(index));
            pane.getChildren().remove(tileTexts.get(index));

            // Get the value of the tile from the grid array
            int tileVal = board.getGrid()
                    [index % board.GRID_SIZE][index / board.GRID_SIZE];

            // If the tile has a corresponding grid value, change the
            // tile's text and color depending on the value
            if (tileVal > 0) {
                tileTexts.get(index).setText(tileVal + "");

                // Update tile's color
                Color colorToFill = GameConstants.getNewTileColor(tileVal);
                tiles.get(index).setFill(colorToFill);

                // Update the color and font size of the new tile texts
                if (tileVal < 8) {
                    tileTexts.get(index).setFill(
                            GameConstants.COLOR_VALUE_DARK);
                    tileTexts.get(index).setFont(
                            GameConstants.LOW_VALUE_FONT);
                } else {
                    tileTexts.get(index).setFill(
                            GameConstants.COLOR_VALUE_LIGHT);
                    if (tileVal < 1024) {
                        tileTexts.get(index).setFont(
                                GameConstants.MID_VALUE_FONT);
                    } else {
                        tileTexts.get(index).setFont(
                                GameConstants.HIGH_VALUE_FONT);
                    }
                }
            } // Otherwise, just give it the default color
            else {
                tiles.get(index).setFill(GameConstants.COLOR_EMPTY);
                tileTexts.get(index).setText("");
            }

            // Add the newly updated tiles and its correponding value
            pane.add(tiles.get(index),
                    index % board.GRID_SIZE, index / board.GRID_SIZE + 1);
            pane.add(tileTexts.get(index),
                    index % board.GRID_SIZE, index / board.GRID_SIZE + 1);
            
            // String info for tile
            String info = board.getNewTiles()[index % board.GRID_SIZE][index / board.GRID_SIZE];
            if (info != null) {
                switch (info) {
                    case "new":
                        controlZoomInOutAnimation(tiles.get(index), false);
                        controlZoomInOutAnimation(tileTexts.get(index), false);
                        break;
                    case "merge":
                        controlZoomInOutAnimation(tiles.get(index), true);
                        controlZoomInOutAnimation(tileTexts.get(index), true);
                        break;
                }
            }
            
            GridPane.setHalignment(tileTexts.get(index), HPos.CENTER);
            scoreText.setText("Score: " + board.getScore());
        }        
    }

    /**
     * Handles zooming animation done by merging or appearance of a new tile.
     * 
     * @param r object to animate
     * @param merge merge or new
     */
    private void controlZoomInOutAnimation(Shape s, boolean merge) {
        transition = new ScaleTransition(
                Duration.millis(merge ? GameConstants.MERGE_DURATION_TIME
                        : GameConstants.NEW_TILE_DURATION_TIME), s);
        transition.setFromX(merge ? GameConstants.MERGE_TILE_START_SCALAR
                : GameConstants.NEW_TILE_START_SCALAR);
        transition.setFromY(merge ? GameConstants.MERGE_TILE_START_SCALAR
                : GameConstants.NEW_TILE_START_SCALAR);
        transition.setToX(merge ? GameConstants.MERGE_TILE_END_SCALAR
                : GameConstants.NEW_TILE_END_SCALAR);
        transition.setToY(merge ? GameConstants.MERGE_TILE_END_SCALAR
                : GameConstants.NEW_TILE_END_SCALAR);
        transition.setCycleCount(merge ? 2 : 1);
        transition.setAutoReverse(true);
        transition.play();
    }
    
    /**
     * Gives the user the option to type in a new name for the saved file.
     */
    private void handleInteractiveSaveBoard() {
        
        // Let the user save, or not save!
        JFrame endGameFrame = new JFrame();
        endGameFrame.setTitle("Play 2048!");
        endGameFrame.setSize(400, 300);
        endGameFrame.setLocationRelativeTo(null);

        // Two user buttons
        JButton yes = new JButton("Save!");
        JButton no = new JButton("No thanks.");

        // Text field to type in file
        JTextField outputFile = new JTextField();
        outputFile.setPreferredSize(new Dimension(350, 30));
        outputFile.setUI(new HintTextField(
                "If you wish to save to .board file, type it here or click Save",
                false));
        outputFile.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(java.awt.event.KeyEvent e) {}
            
            @Override
            public void keyPressed(java.awt.event.KeyEvent e) {
                if (e.getKeyCode() == GameConstants.ENTER_BUTTON) yes.doClick();
            }

            @Override
            public void keyReleased(java.awt.event.KeyEvent e) {}
            
        });
//            inputFile.setHorizontalAlignment(JTextField.CENTER);

        // If user clicks yes button, save the file to what they want
        yes.addActionListener((java.awt.event.ActionEvent e) -> {
            try {
                if (outputFile.getText().isEmpty()) {
                    outputFile.setText(outputBoard);
                }
                board.saveBoard(outputFile.getText());
            } catch (IOException ex) {
                Logger.getLogger(Game2048.class.getName()).log(
                        Level.SEVERE, null, ex);
            }
            System.exit(0);
        });

        // Otherwise, don't save and just exit..
        no.addActionListener((java.awt.event.ActionEvent e) -> {
            System.exit(0);
        });
        
        endGameFrame.setDefaultCloseOperation(
                WindowConstants.EXIT_ON_CLOSE);

        // Show the user their choices for saving
        JPanel userChoices = new JPanel();
        userChoices.add(outputFile, BorderLayout.NORTH);
        userChoices.add(yes, BorderLayout.CENTER);
        userChoices.add(no, BorderLayout.SOUTH);
        endGameFrame.add(userChoices);
        endGameFrame.setVisible(true);
    }
    
    /**
     * Handles the game over GUI.
     */
    private void handleGameOver() {

        // Create the game over text object
        Text gameOverText = new Text();
        gameOverText.setFont(GameConstants.GAME_OVER_FONT);
        gameOverText.setFill(GameConstants.COLOR_VALUE_DARK);
        gameOverText.setText("Game Over!");

        // Create the overlay
        Rectangle gameOverRect = new Rectangle();
        gameOverRect.setWidth(
                (GameConstants.TILE_WIDTH + 20) * board.GRID_SIZE);
        gameOverRect.setHeight(
                (GameConstants.TILE_WIDTH + 20) * (board.GRID_SIZE + 1));
        gameOverRect.setFill(GameConstants.COLOR_GAME_OVER);
        gameOverRect.setBlendMode(BlendMode.OVERLAY);

        // Add the two objects to the window
        layout.getChildren().addAll(gameOverRect, gameOverText);

        // Save the board to outputBoard
        try {
            board.saveBoard(outputBoard);
        } catch (IOException ex) {
            Logger.getLogger(Game2048.class.getName()).log(
                            Level.SEVERE, null, ex);
        }

    }
    
    /**
     * Handles all keyboard events.
     */
    private class MyKeyHandler implements EventHandler<KeyEvent> {

        boolean turnOffHandling = false;
        /**
         * Handles a key that has been pressed.
         *
         * @param e KeyEvent to handle
         */
        @Override
        public void handle(KeyEvent e) {

            // We only want to deal with keys if the game is in session
            if (!board.isGameOver()) {

                if (transition != null &&
                        transition.getStatus() == Animation.Status.RUNNING) {
                    
                    transition.setDelay(Duration.ZERO);
                }
                
                // Tracking successful moves for continuation of game
                boolean successfulMove = false;
                
                // Save the old point for transition
//                for (int index = 0; index < tiles.size(); index++) {
//                    tiles.get(index).setOldPt(
//                            new Point(index % tiles.size(), 
//                                    index / tiles.size() + 1));
//                }

                board.clearNewTiles();

                // Handle key pressed events
                switch (e.getCode().getName()) {
                    case "Left":
                        successfulMove = board.move(Direction.LEFT);
                        break;
                    case "Right":
                        successfulMove = board.move(Direction.RIGHT);
                        break;
                    case "Up":
                        successfulMove = board.move(Direction.UP);
                        break;
                    case "Down":
                        successfulMove = board.move(Direction.DOWN);
                        break;
                    case "S":
                        handleInteractiveSaveBoard();
                        break;
                    default:
                        break;
                }

                // Add a random tile if we've moved successfully
                if (successfulMove) {
                    board.addRandomTile();

                    // Update the tile colors and texts and score of the game
                    updateGUIBoard();
                }

                // If the game is over, overlay the window with Game Over!
                if (board.isGameOver()) {

                    handleGameOver();
                    turnOffHandling = true;
                }
                
            } // End !isGameOver() check
            
            else {
                
                // Only will happen when a game over board is written in
                if (!turnOffHandling) {

                    handleGameOver();
                    turnOffHandling = true;
                }
            }

        } // End KeyHandler handle()

    } // End KeyHandler private class

    /**
     * Handles the user input of the initial window.
     */
    private class InputFileHandler extends JFrame {

        private final JButton yes, no;
        private final JTextField inputFile;
        private final int boardSize;
        private boolean finished = false;

        /**
         * Creates a JFrame window for user to input a file.
         * 
         * @param boardSize 
         */
        InputFileHandler(int boardSize) {
            this.boardSize = boardSize;

            // Draw the window
            setTitle("Play 2048!");
            setSize(400, 300);

            // Add two buttons for user
            yes = new JButton("I'm ready to play!");
            no = new JButton("Cancel");
            yes.addActionListener(new ButtonListener(this));
            no.addActionListener(new ButtonListener(this));            

            // Add text field to input existing .board file
            inputFile = new JTextField();
            inputFile.setPreferredSize(new Dimension(350, 30));
            inputFile.setUI(new HintTextField(
                    "If you wish to use a saved .board file, type it here.",
                    false));
            
            // If they press enter, it's the same as clicking 'I'm ready'
            inputFile.addKeyListener(new KeyListener() {
                @Override
                public void keyTyped(java.awt.event.KeyEvent e) {}

                @Override
                public void keyPressed(java.awt.event.KeyEvent e) {
                    if (e.getKeyCode() == GameConstants.ENTER_BUTTON) yes.doClick();
                }

                @Override
                public void keyReleased(java.awt.event.KeyEvent e) {}
            });
//            inputFile.setHorizontalAlignment(JTextField.CENTER);

            // Create the panel and add the buttons & text field to it
            JPanel userChoices = new JPanel();
            userChoices.add(inputFile, BorderLayout.NORTH);
            userChoices.add(yes, BorderLayout.CENTER);
            userChoices.add(no, BorderLayout.SOUTH);
            
            // Add the panel to the JFrame, and show it to the user
            add(userChoices);
            setLocationRelativeTo(null);
            setVisible(true);
            
            // If the X on the window is clicked, the program will stop
            setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        }

        /**
         * Is the handler done running?
         * 
         * @return true if done
         */
        public boolean isFinished() {
            return finished;
        }
        
        /**
         * Handles the buttons on the initial user window.
         */
        private class ButtonListener implements ActionListener {

            // Reference to the InputFileHandler JFrame
            private final JFrame frame;

            /**
             * Creates a listener for the passed in JFrame.
             * 
             * @param frame 
             */
            ButtonListener(JFrame frame) {
                this.frame = frame;
            }

            /**
             * Listens for an action performed, and executes when called on
             * by the event handler.
             * 
             * @param e ActionEvent reference
             */
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                
                // The user is ready to play
                if (e.getActionCommand().equals("I'm ready to play!")) {
                    
                    // If the file doesn't end in .board, don't bother
                    if (inputFile.getText().endsWith(".board")) {

                        // Check if the file is in the right format!
                        if (!Board.isInputFileCorrectFormat(
                                inputFile.getText())) {

                            // If not, try again! Display the error message.
                            inputFile.setText("");
                            HintTextField newHTF = new HintTextField(
                                    "SEVERE: Be sure the format of the .board "
                                    + "file is correct.",
                                    false);
                            inputFile.setUI(newHTF);
                            newHTF.setColor(java.awt.Color.RED);
                            return;
                        } 

                        try {
                            // Create a new board with the given file
                            board = new Board(inputFile.getText(),
                                    new Random());
                            
                        } // Some file exception
                        catch (IOException ex) {
                            Logger.getLogger(Game2048.class.getName()).log(
                                    Level.SEVERE, null, ex);
                        }
                    } 

                    // If there's no .board file specified, create a new one
                    else {
                        
                        // New default board
                        board = new Board(boardSize, new Random());
                    }
                    
                    // Lets the program know that InputFileHandler is done
                    finished = true;

                    // Hide the option window
                    frame.setVisible(false);
                }

                // The user clicked something other than 'I'm Ready to Play'
                else {

                    // Exit the program
                    System.exit(0);
                }
            }
            
        } // End of ButtonListener class
        
    } // End of InputFileHandler class

} // End of Gui2048 class