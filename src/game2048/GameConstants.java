package game2048;

/**
 * All of the final constants for Game 2048.
 */

import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

/**
 *
 * @author David
 */
public final class GameConstants {
    
    // To be run on any platform with just the .jar file
    public static final boolean STAND_ALONE = false;

    // Board constants
    public static final int NUM_START_TILES = 2;
    public static final int TWO_PROBABILITY = 90;

    // Default tile size
    public static final int TILE_WIDTH = 106;

    // Low value tiles (2, 4, 8, 16, 32, 64)
    public static final int TEXT_SIZE_LOW = 55;
    // Mid value tiles (128, 256, 512)
    public static final int TEXT_SIZE_MID = 45;
    // High value tiles (>= 1024)
    public static final int TEXT_SIZE_HIGH = 35;

    // Fonts for text objects on the game window
    public static final Font LOW_VALUE_FONT
            = Font.font("Times New Roman", FontWeight.BOLD, TEXT_SIZE_LOW);
    public static final Font MID_VALUE_FONT
            = Font.font("Times New Roman", FontWeight.BOLD, TEXT_SIZE_MID);
    public static final Font HIGH_VALUE_FONT
            = Font.font("Times New Roman", FontWeight.BOLD, TEXT_SIZE_HIGH);
    public static final Font GAME_OVER_FONT
            = Font.font("Times New Roman", FontWeight.BOLD, 80);

    // Fill colors for each of the Tile values
    public static final Color COLOR_EMPTY = Color.rgb(238, 228, 218, 0.35);
    public static final Color COLOR_2 = Color.rgb(238, 228, 218);
    public static final Color COLOR_4 = Color.rgb(237, 224, 200);
    public static final Color COLOR_8 = Color.rgb(242, 177, 121);
    public static final Color COLOR_16 = Color.rgb(245, 149, 99);
    public static final Color COLOR_32 = Color.rgb(246, 124, 95);
    public static final Color COLOR_64 = Color.rgb(246, 94, 59);
    public static final Color COLOR_128 = Color.rgb(237, 207, 114);
    public static final Color COLOR_256 = Color.rgb(237, 204, 97);
    public static final Color COLOR_512 = Color.rgb(237, 200, 80);
    public static final Color COLOR_1024 = Color.rgb(237, 197, 63);
    public static final Color COLOR_2048 = Color.rgb(237, 194, 46);
    public static final Color COLOR_OTHER = Color.BLACK;
    public static final Color COLOR_GAME_OVER = Color.rgb(238, 228, 218, 0.73);

    // Color for tiles >= 8
    public static final Color COLOR_VALUE_LIGHT = Color.rgb(249, 246, 242);
    // Color for tiles < 8
    public static final Color COLOR_VALUE_DARK = Color.rgb(119, 110, 101);
    
    // Key constants (from KeyCode)
    public static final int ENTER_BUTTON = 10;
    
    // Animation constants
    public static final int MERGE_DURATION_TIME = 150;
    public static final float MERGE_TILE_START_SCALAR = 1f;
    public static final float MERGE_TILE_END_SCALAR = 1.25f;
    public static final int NEW_TILE_DURATION_TIME = 200;
    public static final float NEW_TILE_START_SCALAR = .25f;
    public static final float NEW_TILE_END_SCALAR = 1f;
    
    /**
     * Returns a specific tile's color.
     *
     * @param tileVal
     * @return new color of the tile
     */
    public static final Color getNewTileColor(int tileVal) {
        switch (tileVal) {
            case 2:
                return GameConstants.COLOR_2;
            case 4:
                return GameConstants.COLOR_4;
            case 8:
                return GameConstants.COLOR_8;
            case 16:
                return GameConstants.COLOR_16;
            case 32:
                return GameConstants.COLOR_32;
            case 64:
                return GameConstants.COLOR_64;
            case 128:
                return GameConstants.COLOR_128;
            case 256:
                return GameConstants.COLOR_256;
            case 512:
                return GameConstants.COLOR_512;
            case 1024:
                return GameConstants.COLOR_1024;
            case 2048:
                return GameConstants.COLOR_2048;
            default:
                return GameConstants.COLOR_OTHER;
        }
    }

}
