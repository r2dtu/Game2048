package game2048;

/**
 * Sample Board
 * <p/>
 *   0 1 2 3
 * 0 - - - -
 * 1 - - - -
 * 2 - - - -
 * 3 - - - -
 * <p/>
 * 
 * This class defines the board for use in the 2048 game.
 * The board uses a 2D array to store its numbers. Numbers are manipulated
 * through moves using the arrow keys. The tiles move according to the arrow
 * keys, merging when the numbers match.
 */

import java.util.*;
import java.io.*;
import java.awt.Point;

/**
 *
 * @author David
 */
public class Board {

    // Grid instances
    public final int GRID_SIZE;
    private final int[][] grid;

    // Keep track of the game's score
    private int score;
    
    // Random number generator
    private final Random random;
    
    // Array of old Point values
    private String[][] newTiles;

    /**
     * Constructs a fresh board with random tiles.
     *
     * @param boardSize size of the board
     * @param random random number generator
     */
    public Board(int boardSize, Random random) {

        // Initialize board
        this.random = random;
        GRID_SIZE = boardSize;
        score = 0;
        grid = new int[GRID_SIZE][GRID_SIZE];
        newTiles = new String[GRID_SIZE][GRID_SIZE];

        // Add starting tiles
        int times = 0;
        while (times++ < GameConstants.NUM_START_TILES) {
            addRandomTile();
        }
    }

    /**
     * Construct a board based off of an input file.
     *
     * @param inputBoard input .board file name
     * @param random random number generator
     * @throws java.io.IOException
     */
    public Board(String inputBoard, Random random) throws IOException {
        this.random = random;

        // Read the whole board to memory
        Scanner input = new Scanner(new File(inputBoard));
        GRID_SIZE = input.nextInt();
        score = input.nextInt();
        grid = new int[GRID_SIZE][GRID_SIZE];
        newTiles = new String[GRID_SIZE][GRID_SIZE];
        for (int[] rows : grid) {
            for (int column = 0; column < GRID_SIZE; column++) {
                rows[column] = input.nextInt();
            }
        }
    }

    /**
     * Saves the current board to a file.
     *
     * @param outputBoard output file name (for saving current progress)
     * @throws java.io.IOException
     */
    public void saveBoard(String outputBoard) throws IOException {

        // Try to create the file and add in the information
        try (PrintWriter outputFile = new PrintWriter(new File(outputBoard))) {

            outputFile.println(GRID_SIZE);
            outputFile.println(score);
            for (int[] rows : grid) {
                for (int column = 0; column < GRID_SIZE; column++) {
                    outputFile.print(rows[column] + " ");
                }
                outputFile.println();
            }
        }
    }

    /**
     * Adds a random tile (of value 2 or 4) to a random empty space on the
     * board.
     */
    public final void addRandomTile() {

        // Count number of available tiles
        int count = 0;
        for (int[] rows : grid) {
            for (int column = 0; column < GRID_SIZE; column++) {
                if (rows[column] == 0) {
                    count++;
                }
            }
        }

        // If count is 0, just exit
        if (count > 0) {

            // Get a random int called location between 0 and count - 1
            int location = random.nextInt(count);
            // Get random int called value btwn 0 and 99
            int value = random.nextInt(100);

            // New tile value
            int tileVal;

            // New tile will be 2 or 4
            if (value < GameConstants.TWO_PROBABILITY) {
                tileVal = 2;
            } else {
                tileVal = 4;
            }

            // Walk the board row first, column second keeping count of the
            // empty spaces you find.  When you hit the i'th empty spot,
            // place your new tile
            int secondCount = 0;
            for (int row = 0; row < GRID_SIZE; row++) {
                for (int column = 0; column < GRID_SIZE; column++) {
                    if (grid[row][column] == 0) {
                        secondCount++;
                        if (secondCount == location + 1) {
                            newTiles[row][column] = "new";
                            grid[row][column] = tileVal;
                        }
                    }
                }
            }
        }
    }

    /**
     * Rotates the board by 90 degrees clockwise or 90 degrees
     * counter-clockwise.
     *
     * @param rotateClockwise rotate the board clockwise or counter
     */
    public void rotate(boolean rotateClockwise) {

        // Copy arr to second
        int[][] gridCopy = new int[GRID_SIZE][GRID_SIZE];
        for (int row = 0; row < GRID_SIZE; row++) {
            for (int column = 0; column < GRID_SIZE; column++) {
                gridCopy[row][column] = grid[row][column];
            }
        }

        // Rotate the actual grid
        for (int row = 0; row < gridCopy.length; row++) {
            for (int column = 0; column < gridCopy[row].length; column++) {
                if (rotateClockwise) {
                    grid[row][column] = gridCopy[GRID_SIZE - 1 - column][row];
                } else {
                    grid[row][column] = gridCopy[column][GRID_SIZE - 1 - row];
                }
            }
        }
    }

    /**
     * Makes sure that the input file is in the correct format.
     *
     * @param inputFile
     * @return file has correct format
     */
    public static boolean isInputFileCorrectFormat(String inputFile) {

        // Open the file: make sure it has a size, score, and a grid with
        // the specified size!
        try {
            
            Scanner input = new Scanner (new File(inputFile));            
            if (!input.hasNextInt()) {
                return false;
            }            
            int gridSize = input.nextInt();
            if (!input.hasNextInt()) {
                return false;
            }            
            input.nextInt();
            
            int numbers = 0;
            while (input.hasNextInt()) {
                input.next();
                numbers++;
            }
            
            if (numbers == gridSize * gridSize) return true;
            
        } catch (Exception e) {
            return false;
        }
        return false;
    }

    /**
     * Performs a move operation. Note that horizontal are flipped,
     * because of the way the double array grid is accessed.
     *
     * @param direction
     * @return successful move
     */
    public boolean move(Direction direction) {
        if (direction == Direction.UP || direction == Direction.DOWN) {
            if (canMoveHorizontally(direction == Direction.UP)) {
                return moveHorizontal(direction == Direction.UP);
            }
        }
        if (direction == Direction.LEFT || direction == Direction.RIGHT) {
            if (canMoveVertically(direction == Direction.LEFT)) {
                return moveVertical(direction == Direction.LEFT);
            }
        }
        return false;
    }

    /**
     * Moves all tiles up / down.
     *
     * @param up up / down
     * @return successful move
     */
    public boolean moveVertical(boolean up) {

        // We gotta move twice, but only merge once.
        int times = 0;
        while (times++ < 2) {
            
            // Move tiles up / down where possible
            for (int column = 0; column < GRID_SIZE; column++) {
                for (int row = up ? 0 : GRID_SIZE - 1; 
                        up ? row < GRID_SIZE : row > -1;
                        row += up ? 1 : -1) {

                    // Just move the tiles up / down, making sure zeroes
                    // are inserted after movement
                    if (grid[row][column] == 0 && 
                            row != (up ? GRID_SIZE - 1 : 0)) {

                        for (int incrow = 1; 
                                up ? row + incrow < GRID_SIZE :
                                     row - incrow > -1; incrow++) {

                            int newRow = up ? row + incrow : row - incrow;
                            if (grid[newRow][column] != 0) {
                                grid[row][column] = grid[newRow][column];
                                grid[newRow][column] = 0;
                                break;
                            }
                        }
                    }
                }
            }
            
            if (times == 2) break;

            // Then check for mergability
            for (int column = 0; column < GRID_SIZE; column++) {
                for (int row = up ? 0 : GRID_SIZE - 1; 
                        up ? row < GRID_SIZE - 1 : row > 0;
                        row += up ? 1 : -1) {

                    if (grid[row][column] != 0) {

                        // Check for adjacent merges
                        int nextRow = up ? row + 1 : row - 1;
                        if (grid[row][column] == grid[nextRow][column]) {
                            newTiles[row][column] = "merge";
                            grid[row][column] += grid[nextRow][column];
                            score += grid[row][column];
                            grid[nextRow][column] = 0;
                            continue;
                        }

                        // Check for non-adjacent merges
                        for (int incrow = 1; 
                                up ? row + incrow < GRID_SIZE :
                                     row - incrow > -1; incrow++) {

                            int newRow = up ? row + incrow : row - incrow;
                            
                            // If there's a zero in between, don't bother
                            if (grid[newRow][column] != 0) {
                                break;
                            }
                            
                            // Otherwise if they match, merge time
                            if (grid[row][column] == grid[newRow][column]) {
                                newTiles[row][column] = "merge";
                                grid[row][column] += grid[newRow][column];
                                score += grid[row][column];
                                grid[newRow][column] = 0;
                            }
                        }
                    }
                }
            }
        }
        return true;
    }

    /**
     * Moves all tiles left / right.
     *
     * @param left left / right
     * @return successful move
     */
    public boolean moveHorizontal(boolean left) {
        
        // We gotta move twice, but only merge once.
        int times = 0;
        while (times++ < 2) {
            
            // Move tiles left / right where possible
            for (int row = 0; row < GRID_SIZE; row++) {
                for (int column = left ? 0 : GRID_SIZE - 1; 
                        left ? column < GRID_SIZE : column > -1;
                        column += left ? 1 : -1) {

                    // Just move the tiles left / right, making sure zeroes
                    // are inserted after movement
                    if (grid[row][column] == 0 && 
                            column != (left ? GRID_SIZE - 1 : 0)) {

                        for (int inccol = 1; 
                                left ? column + inccol < GRID_SIZE :
                                     column - inccol > -1; inccol++) {

                            int newCol = left ? column + inccol : column - inccol;
                            if (grid[row][newCol] != 0) {
                                grid[row][column] = grid[row][newCol];
                                grid[row][newCol] = 0;
                                break;
                            }
                        }
                    }
                }
            }
            
            if (times == 2) break;

            // Then check for mergability
            for (int row = 0; row < GRID_SIZE; row++) {
                for (int column = left ? 0 : GRID_SIZE - 1; 
                        left ? column < GRID_SIZE - 1 : column > 0;
                        column += left ? 1 : -1) {

                    if (grid[row][column] != 0) {

                        // Check for adjacent merges
                        int nextCol = left ? column + 1 : column - 1;
                        if (grid[row][column] == grid[row][nextCol]) {
                            newTiles[row][column] = "merge";
                            grid[row][column] += grid[row][nextCol];
                            score += grid[row][column];
                            grid[row][nextCol] = 0;
                            continue;
                        }

                        // Check for non-adjacent merges
                        for (int inccol = 1; 
                                left ? column + inccol < GRID_SIZE :
                                     column - inccol > -1; inccol++) {

                            int newCol = left ? column + inccol : 
                                    column - inccol;
                            
                            // If there's a zero in between, don't bother
                            if (grid[row][newCol] != 0) {
                                break;
                            }
                            
                            // Otherwise if they match, merge time
                            if (grid[row][column] == grid[row][newCol]) {
                                newTiles[row][column] = "merge";
                                grid[row][column] += grid[row][newCol];
                                score += grid[row][column];
                                grid[row][newCol] = 0;
                            }
                        }
                    }
                }
            }
        }
        return true;
    }

    /**
     * Check to see if we have a game over.
     *
     * @return true if game over
     */
    public boolean isGameOver() {
        return !canMoveHorizontally(true) && !canMoveHorizontally(false)
                && !canMoveVertically(true) && !canMoveVertically(false);
    }

    /**
     * Determine if we can move in a given direction.
     *
     * @param direction direction specified by Direction class
     * @return we can move
     */
    public boolean canMove(Direction direction) {
        if (direction == Direction.UP) {
            return canMoveHorizontally(true);
        } else if (direction == Direction.DOWN) {
            return canMoveHorizontally(false);
        } else if (direction == Direction.LEFT) {
            return canMoveVertically(true);
        } else if (direction == Direction.RIGHT) {
            return canMoveVertically(false);
        }
        return false;
    }

    /**
     * Can we move horizontally in left or right direction?
     *
     * @param left left or right
     * @return yes we can
     */
    private boolean canMoveHorizontally(boolean left) {

        // Loop through each row
        for (int[] rows : grid) {
            boolean allZeroes = true;
            int prevVal = -1;

            // Check for any zeroes in the rows
            for (int column = 0; column < GRID_SIZE; column++) {
                if (rows[column] != 0) {
                    allZeroes = false;
                    break;
                }
            }

            // If it's all zeroes, we don't care
            if (!allZeroes) {

                // Loop through the columns, and see if we can in fact move left
                for (int column = left ? 0 : GRID_SIZE - 1;
                        left ? column < GRID_SIZE : column > -1;
                        column += left ? 1 : -1) {

                    // Compare the previous value with the current. If they're
                    // the same, and not zero, we're good. Otherwise, keep 
                    // comparing adjacent values
                    if (rows[column] != prevVal) {
                        // If there's a zero, we can move left / right
                        if (prevVal == 0) {
                            return true;
                        }
                        prevVal = rows[column];
                    } else {
                        if (rows[column] == 0) {
                            continue;
                        }
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * Can we move vertically?
     *
     * @param up up / down
     * @return yes we can!
     */
    private boolean canMoveVertically(boolean up) {

        // Loop through each column
        for (int column = 0; column < GRID_SIZE; column++) {
            boolean allZeroes = true;
            int prevVal = -1;

            // Keep column constant, loop through rows
            for (int row = 0; row < GRID_SIZE; row++) {
                if (grid[row][column] != 0) {
                    allZeroes = false;
                    break;
                }
            }

            // If it's all zeroes, we don't care
            if (!allZeroes) {

                // Loop through the rows, and see if we can in fact move left
                for (int row = up ? 0 : GRID_SIZE - 1;
                        up ? row < GRID_SIZE : row > -1;
                        row += up ? 1 : -1) {

                    // Compare the previous value with the current. If they're
                    // the same, and not zero, we're good. Otherwise, keep 
                    // comparing adjacent values
                    if (grid[row][column] != prevVal) {
                        // If there's a zero, we can move up / down
                        if (prevVal == 0) {
                            return true;
                        }
                        prevVal = grid[row][column];
                    } else {
                        if (grid[row][column] == 0) {
                            continue;
                        }
                        return true;
                    }
                }
            }
        }
        return false;
    }

    // Return the reference to the 2048 Grid
    public int[][] getGrid() {
        return grid;
    }

    // Return the reference to the new tiles grid
    public String[][] getNewTiles() {
        return newTiles;
    }

    // Clears all new tile strings
    public void clearNewTiles() {
        for (int i = 0; i < newTiles.length; i++) {
            for (int j = 0; j < newTiles[i].length; j++) {
                newTiles[i][j] = "";
            }
        }
    }
    
    // Return the score
    public int getScore() {
        return score;
    }

    @Override
    public String toString() {
        StringBuilder outputString = new StringBuilder();
        outputString.append(String.format("Score: %d\n", score));
        for (int row = 0; row < GRID_SIZE; row++) {
            for (int column = 0; column < GRID_SIZE; column++) {
                outputString.append(grid[row][column] == 0 ? "    -"
                        : String.format("%5d", grid[row][column]));
            }

            outputString.append("\n");
        }
        return outputString.toString();
    }
}
