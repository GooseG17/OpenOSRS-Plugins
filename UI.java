package net.runelite.client.plugins;

import javax.swing.*;
import java.awt.*;
import java.util.function.Consumer;

public class UI {
    /**
     * Hollow factory/decorator for JLabels
     * @param actions
     * @return
     */
    @SafeVarargs
    public static JPanel createJPanel(Consumer<JPanel>... actions) {
        JPanel panel = new JPanel();
        for(Consumer<JPanel> action: actions) {
            action.accept(panel);
        }
        return panel;
    }

    /**
     * Hollow factory/decorator for JLabels
     * @param actions
     * @return
     */
    @SafeVarargs
    public static JLabel createJLabel(Consumer<JLabel>... actions) {
        JLabel label = new JLabel();
        for(Consumer<JLabel> action: actions) {
            action.accept(label);
        }
        return label;
    }

    public static void align(JPanel panel) {
        for (Component component : panel.getComponents()) {
            if (component instanceof JPanel) {
                align((JPanel)component);
            } else if (component instanceof JComponent) {
                ((JComponent)component).setAlignmentX(0.5f);
            }
        }
    }

    public static JPanel boxPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        return panel;
    }
}
