/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game2048;

import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

/**
 *
 * @author David
 */
public class TileText extends Text {
    
    public TileText() {
        setFont(Font.font("Times New Roman", 
                FontWeight.BOLD, GameConstants.TEXT_SIZE_LOW));
        setFill(GameConstants.COLOR_VALUE_DARK);
    }
    
}
