/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game2048;

import java.awt.Point;
import javafx.scene.shape.Rectangle;

/**
 *
 * @author David
 */
public class Tile extends Rectangle {
    
    private Point oldPt;
    private Point newPt;
    
    public Tile() {
        setWidth(GameConstants.TILE_WIDTH);
        setHeight(GameConstants.TILE_WIDTH);
        setFill(GameConstants.COLOR_EMPTY);
        
        oldPt = new Point();
        newPt = new Point();
    }
    
    public Point getOldPt() {
        return oldPt;
    }
    
    public Point getNewPt() {
        return newPt;
    }
    
    public void setOldPt(Point point) {
        oldPt.setLocation(point.getX(), point.getY());
    }

    public void setNewPt(Point point) {
        newPt.setLocation(point.getX(), point.getY());
    }
    
}
