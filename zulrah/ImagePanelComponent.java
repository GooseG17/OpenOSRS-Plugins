package net.runelite.client.plugins.zulrah;

import com.google.common.base.Strings;
import lombok.Setter;
import net.runelite.client.ui.overlay.RenderableEntity;
import net.runelite.client.ui.overlay.components.BackgroundComponent;

import javax.annotation.Nullable;
import java.awt.*;
import java.awt.image.BufferedImage;

public class ImagePanelComponent implements RenderableEntity
{
	private static final int TOP_BORDER = 3;
	private static final int SIDE_BORDER = 6;
	private static final int BOTTOM_BORDER = 6;
	private static final int SEPARATOR = 4;

	@Setter
	@Nullable
	private String title;

	@Setter
	private Color titleColor = Color.WHITE;

	@Setter
	private Color backgroundColor = new Color(70, 61, 50, 156);

	@Setter
	private BufferedImage image;

	@Setter
	private Point position = new Point();

	@Override
	public Dimension render(Graphics2D graphics)
	{
		final Dimension dimension = new Dimension();
		final FontMetrics metrics = graphics.getFontMetrics();
		int height = TOP_BORDER + (Strings.isNullOrEmpty(title) ? 0 : metrics.getHeight())
				+ SEPARATOR + image.getHeight() + BOTTOM_BORDER;
		int width = Math.max(Strings.isNullOrEmpty(title) ? 0 : metrics.stringWidth(title), image.getWidth()) + SIDE_BORDER * 2;
		dimension.setSize(width, height);

		if (dimension.height == 0)
		{
			return null;
		}

		// Calculate panel dimensions
		int y = position.y + TOP_BORDER + metrics.getHeight();

		// Render background
		final BackgroundComponent backgroundComponent = new BackgroundComponent();
		backgroundComponent.setBackgroundColor(backgroundColor);
		backgroundComponent.setRectangle(new Rectangle(position.x, position.y, dimension.width, dimension.height));
		backgroundComponent.render(graphics);

		// Render title
		if (!Strings.isNullOrEmpty(title))
		{
			final TextComponent titleComponent = new TextComponent();
			titleComponent.setText(title);
			titleComponent.setColor(titleColor);
			titleComponent.setPosition(new Point(position.x + (width - metrics.stringWidth(title)) / 2, y));
			titleComponent.render(graphics);
			y += SEPARATOR;
		}

		// Render image
		graphics.drawImage(image, position.x + (width - image.getWidth()) / 2, y, null);

		return dimension;
	}
}