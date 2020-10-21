package net.runelite.client.plugins.zulrah;

import lombok.Setter;
import net.runelite.client.ui.overlay.RenderableEntity;

import java.awt.*;

public class TextComponent implements RenderableEntity
{
    @Setter
    private String text;

    @Setter
    private Point position = new Point();

    @Setter
    private Color color = Color.WHITE;

    @Override
    public Dimension render(Graphics2D graphics)
    {
        // Draw shadow
        graphics.setColor(Color.BLACK);
        graphics.drawString(text, position.x + 1, position.y + 1);

        // Draw actual text
        graphics.setColor(color);
        graphics.drawString(text, position.x, position.y);

        final FontMetrics fontMetrics = graphics.getFontMetrics();
        return new Dimension(fontMetrics.stringWidth(text), fontMetrics.getHeight());
    }
}
