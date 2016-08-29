package game2048;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import javax.swing.plaf.basic.BasicTextFieldUI;
import javax.swing.text.JTextComponent;

/**
 * Creates a text field that has an initial 'hint', but then when the user
 * begins to type into it, that 'hint' disappears.
 * 
 * Give the object a string hint, whether to hide it or not, and a color,
 * if necessary.
 */
public class HintTextField extends BasicTextFieldUI implements FocusListener {

    private String hint;
    private boolean hideOnFocus;
    private Color color;

    /**
     * Creates a default text field with a string and no hide on focus 
     * (most common wanted feature).
     * 
     * @param hint string to display in text field
     */
    public HintTextField(String hint) {
        this(hint, false);
    }

    /**
     * Creates a text field with a hint and user specified preference of whether
     * or not to hide the text.
     * 
     * @param hint string to display in text field
     * @param hideOnFocus whether or not to hide the text on focus
     */
    public HintTextField(String hint, boolean hideOnFocus) {
        this(hint, hideOnFocus, null);
    }

    /**
     * Creates a text field with a hint, user specified preference of whether
     * or not to hide the text, and a user specified color.
     * 
     * @param hint string to display in text field
     * @param hideOnFocus whether or not to hide the text on focus
     * @param color color of the hint
     */
    public HintTextField(String hint, boolean hideOnFocus, Color color) {
        this.hint = hint;
        this.hideOnFocus = hideOnFocus;
        this.color = color;
    }

    /**
     * Gets the color of the hint.
     * 
     * @return color of text
     */
    public Color getColor() {
        return color;
    }

    /**
     * Sets a new color for the hint.
     * 
     * @param color new color for the text
     */
    public void setColor(Color color) {
        this.color = color;
        repaint();
    }

    /**
     * Repaints the text field.
     */
    private void repaint() {
        if (getComponent() != null) {
            getComponent().repaint();
        }
    }

    /**
     * Tells the user whether the text will hide or not upon focus.
     * 
     * @return hideOnFocus
     */
    public boolean isHideOnFocus() {
        return hideOnFocus;
    }

    /**
     * The user sets whether the text will hide or not upon focus.
     * 
     * @param hideOnFocus true or false
     */
    public void setHideOnFocus(boolean hideOnFocus) {
        this.hideOnFocus = hideOnFocus;
        repaint();
    }

    /**
     * Gets the current hint of the text field.
     * 
     * @return hint
     */
    public String getHint() {
        return hint;
    }

    /**
     * Sets a new hint for the text field.
     * 
     * @param hint new hint string
     */
    public void setHint(String hint) {
        this.hint = hint;
        repaint();
    }

    /**
     * Paints the text field components.
     * 
     * @param g graphics 
     */
    @Override
    protected void paintSafely(Graphics g) {
        super.paintSafely(g);
        JTextComponent comp = getComponent();
        if (hint != null && comp.getText().length() == 0 && (!(hideOnFocus && comp.hasFocus()))) {
            if (color != null) {
                g.setColor(color);
            } else {
                g.setColor(comp.getForeground().brighter().brighter().brighter());
            }
            int padding = (comp.getHeight() - comp.getFont().getSize()) / 2;
            g.drawString(hint, 2, comp.getHeight() - padding - 1);
        }
    }

    /**
     * The user has focused in on the text field.
     * 
     * @param e FocusEvent description
     */
    @Override
    public void focusGained(FocusEvent e) {
        if (hideOnFocus) {
            repaint();
        }
    }

    /**
     * The user has focused out of the text field.
     * 
     * @param e FocusEvent description
     */
    @Override
    public void focusLost(FocusEvent e) {
        if (hideOnFocus) {
            repaint();
        }
    }

    /**
     * Sets the FocusListener for this component.
     */
    @Override
    protected void installListeners() {
        super.installListeners();
        getComponent().addFocusListener(this);
    }

    /**
     * Un-sets the FocusListener for this component upon exit.
     */
    @Override
    protected void uninstallListeners() {
        super.uninstallListeners();
        getComponent().removeFocusListener(this);
    }
}
