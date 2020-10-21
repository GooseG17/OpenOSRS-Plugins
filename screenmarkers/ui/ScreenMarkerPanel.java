/*
 * Copyright (c) 2018, Kamiel, <https://github.com/Kamielvf>
 * Copyright (c) 2018, Psikoi <https://github.com/psikoi>
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package net.runelite.client.plugins.screenmarkers.ui;

import net.runelite.client.plugins.UI;
import net.runelite.client.plugins.screenmarkers.ScreenMarkerOverlay;
import net.runelite.client.plugins.screenmarkers.ScreenMarkerPlugin;
import net.runelite.client.ui.ColorScheme;
import net.runelite.client.ui.FontManager;
import net.runelite.client.ui.components.FlatTextField;
import net.runelite.client.ui.components.colorpicker.RuneliteColorPicker;
import net.runelite.client.util.ImageUtil;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.function.Consumer;
import java.util.function.Supplier;

class ScreenMarkerPanel extends JPanel
{
	private static final int DEFAULT_FILL_OPACITY = 75;

	private static final Border NAME_BOTTOM_BORDER = new CompoundBorder(
		BorderFactory.createMatteBorder(0, 0, 1, 0, ColorScheme.DARK_GRAY_COLOR),
		BorderFactory.createLineBorder(ColorScheme.DARKER_GRAY_COLOR));

	private static final ImageIcon BORDER_COLOR_ICON;
	private static final ImageIcon BORDER_COLOR_HOVER_ICON;
	private static final ImageIcon NO_BORDER_COLOR_ICON;
	private static final ImageIcon NO_BORDER_COLOR_HOVER_ICON;

	private static final ImageIcon FILL_COLOR_ICON;
	private static final ImageIcon FILL_COLOR_HOVER_ICON;
	private static final ImageIcon NO_FILL_COLOR_ICON;
	private static final ImageIcon NO_FILL_COLOR_HOVER_ICON;

	private static final ImageIcon FULL_OPACITY_ICON;
	private static final ImageIcon FULL_OPACITY_HOVER_ICON;
	private static final ImageIcon NO_OPACITY_ICON;
	private static final ImageIcon NO_OPACITY_HOVER_ICON;

	private static final ImageIcon VISIBLE_ICON;
	private static final ImageIcon VISIBLE_HOVER_ICON;
	private static final ImageIcon INVISIBLE_ICON;
	private static final ImageIcon INVISIBLE_HOVER_ICON;

	private static final ImageIcon DELETE_ICON;
	private static final ImageIcon DELETE_HOVER_ICON;

	private final ScreenMarkerPlugin plugin;
	private final ScreenMarkerOverlay marker;

	private final JLabel borderColorIndicator = new JLabel();
	private final JLabel fillColorIndicator = new JLabel();
	private final JLabel opacityIndicator = new JLabel();
	private final JLabel visibilityLabel = new JLabel();
	private final JLabel deleteLabel = new JLabel();

	private final FlatTextField nameInput = new FlatTextField();
	private final JLabel save = new JLabel("Save");
	private final JLabel cancel = new JLabel("Cancel");
	private final JLabel rename = new JLabel("Rename");

	private final SpinnerModel spinnerModel = new SpinnerNumberModel(5, 0, Integer.MAX_VALUE, 1);
	private final JSpinner thicknessSpinner = new JSpinner(spinnerModel);

	private boolean visible;

	static
	{
		final BufferedImage borderImg = ImageUtil.getResourceStreamFromClass(ScreenMarkerPlugin.class, "border_color_icon.png");
		final BufferedImage borderImgHover = ImageUtil.luminanceOffset(borderImg, -150);
		BORDER_COLOR_ICON = new ImageIcon(borderImg);
		BORDER_COLOR_HOVER_ICON = new ImageIcon(borderImgHover);

		NO_BORDER_COLOR_ICON = new ImageIcon(borderImgHover);
		NO_BORDER_COLOR_HOVER_ICON = new ImageIcon(ImageUtil.alphaOffset(borderImgHover, -100));

		final BufferedImage fillImg = ImageUtil.getResourceStreamFromClass(ScreenMarkerPlugin.class, "fill_color_icon.png");
		final BufferedImage fillImgHover = ImageUtil.luminanceOffset(fillImg, -150);
		FILL_COLOR_ICON = new ImageIcon(fillImg);
		FILL_COLOR_HOVER_ICON = new ImageIcon(fillImgHover);

		NO_FILL_COLOR_ICON = new ImageIcon(fillImgHover);
		NO_FILL_COLOR_HOVER_ICON = new ImageIcon(ImageUtil.alphaOffset(fillImgHover, -100));

		final BufferedImage opacityImg = ImageUtil.getResourceStreamFromClass(ScreenMarkerPlugin.class, "opacity_icon.png");
		final BufferedImage opacityImgHover = ImageUtil.luminanceOffset(opacityImg, -150);
		FULL_OPACITY_ICON = new ImageIcon(opacityImg);
		FULL_OPACITY_HOVER_ICON = new ImageIcon(opacityImgHover);

		NO_OPACITY_ICON = new ImageIcon(opacityImgHover);
		NO_OPACITY_HOVER_ICON = new ImageIcon(ImageUtil.alphaOffset(opacityImgHover, -100));

		final BufferedImage visibleImg = ImageUtil.getResourceStreamFromClass(ScreenMarkerPlugin.class, "visible_icon.png");
		VISIBLE_ICON = new ImageIcon(visibleImg);
		VISIBLE_HOVER_ICON = new ImageIcon(ImageUtil.alphaOffset(visibleImg, -100));

		final BufferedImage invisibleImg = ImageUtil.getResourceStreamFromClass(ScreenMarkerPlugin.class, "invisible_icon.png");
		INVISIBLE_ICON = new ImageIcon(invisibleImg);
		INVISIBLE_HOVER_ICON = new ImageIcon(ImageUtil.alphaOffset(invisibleImg, -100));

		final BufferedImage deleteImg = ImageUtil.getResourceStreamFromClass(ScreenMarkerPlugin.class, "delete_icon.png");
		DELETE_ICON = new ImageIcon(deleteImg);
		DELETE_HOVER_ICON = new ImageIcon(ImageUtil.alphaOffset(deleteImg, -100));
	}

	ScreenMarkerPanel(ScreenMarkerPlugin plugin, ScreenMarkerOverlay marker)
	{
		this.plugin = plugin;
		this.marker = marker;
		this.visible = marker.getMarker().isVisible();

//		setLayout(new BorderLayout());
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		setBackground(ColorScheme.DARKER_GRAY_COLOR);

		JPanel nameWrapper = new JPanel(new BorderLayout());
		nameWrapper.setBackground(ColorScheme.DARKER_GRAY_COLOR);
		nameWrapper.setBorder(NAME_BOTTOM_BORDER);

		JPanel nameActions = new JPanel(new BorderLayout(3, 0));
		nameActions.setBorder(new EmptyBorder(0, 0, 0, 8));
		nameActions.setBackground(ColorScheme.DARKER_GRAY_COLOR);

		save.setVisible(false);
		save.setFont(FontManager.getRunescapeSmallFont());
		save.setForeground(ColorScheme.PROGRESS_COMPLETE_COLOR);
		save.addMouseListener(new MouseAdapter()
		{
			@Override
			public void mousePressed(MouseEvent mouseEvent)
			{
				save();
			}

			@Override
			public void mouseEntered(MouseEvent mouseEvent)
			{
				save.setForeground(ColorScheme.PROGRESS_COMPLETE_COLOR.darker());
			}

			@Override
			public void mouseExited(MouseEvent mouseEvent)
			{
				save.setForeground(ColorScheme.PROGRESS_COMPLETE_COLOR);
			}
		});

		cancel.setVisible(false);
		cancel.setFont(FontManager.getRunescapeSmallFont());
		cancel.setForeground(ColorScheme.PROGRESS_ERROR_COLOR);
		cancel.addMouseListener(new MouseAdapter()
		{
			@Override
			public void mousePressed(MouseEvent mouseEvent)
			{
				cancel();
			}

			@Override
			public void mouseEntered(MouseEvent mouseEvent)
			{
				cancel.setForeground(ColorScheme.PROGRESS_ERROR_COLOR.darker());
			}

			@Override
			public void mouseExited(MouseEvent mouseEvent)
			{
				cancel.setForeground(ColorScheme.PROGRESS_ERROR_COLOR);
			}
		});

		rename.setFont(FontManager.getRunescapeSmallFont());
		rename.setForeground(ColorScheme.LIGHT_GRAY_COLOR.darker());
		rename.addMouseListener(new MouseAdapter()
		{
			@Override
			public void mousePressed(MouseEvent mouseEvent)
			{
				nameInput.setEditable(true);
				updateNameActions(true);
			}

			@Override
			public void mouseEntered(MouseEvent mouseEvent)
			{
				rename.setForeground(ColorScheme.LIGHT_GRAY_COLOR.darker().darker());
			}

			@Override
			public void mouseExited(MouseEvent mouseEvent)
			{
				rename.setForeground(ColorScheme.LIGHT_GRAY_COLOR.darker());
			}
		});

		nameActions.add(save, BorderLayout.EAST);
		nameActions.add(cancel, BorderLayout.WEST);
		nameActions.add(rename, BorderLayout.CENTER);

		nameInput.setText(marker.getMarker().getName());
		nameInput.setBorder(null);
		nameInput.setEditable(false);
		nameInput.setBackground(ColorScheme.DARKER_GRAY_COLOR);
		nameInput.setPreferredSize(new Dimension(0, 24));
		nameInput.getTextField().setForeground(Color.WHITE);
		nameInput.getTextField().setBorder(new EmptyBorder(0, 8, 0, 0));
		nameInput.addKeyListener(new KeyAdapter()
		{
			@Override
			public void keyPressed(KeyEvent e)
			{
				if (e.getKeyCode() == KeyEvent.VK_ENTER)
				{
					save();
				}
				else if (e.getKeyCode() == KeyEvent.VK_ESCAPE)
				{
					cancel();
				}
			}
		});
		nameInput.getTextField().addMouseListener(new MouseAdapter()
		{
			@Override
			public void mouseEntered(MouseEvent mouseEvent)
			{
				preview(true);
			}

			@Override
			public void mouseExited(MouseEvent mouseEvent)
			{
				preview(false);
			}
		});

		nameWrapper.add(nameInput, BorderLayout.CENTER);
		nameWrapper.add(nameActions, BorderLayout.EAST);

		JPanel bottomContainer = new JPanel(new BorderLayout());
		bottomContainer.setBorder(new EmptyBorder(8, 0, 8, 0));
		bottomContainer.setBackground(ColorScheme.DARKER_GRAY_COLOR);

		JPanel leftActions = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
		leftActions.setBackground(ColorScheme.DARKER_GRAY_COLOR);

		borderColorIndicator.setToolTipText("Edit border color");
		borderColorIndicator.addMouseListener(new MouseAdapter()
		{
			@Override
			public void mousePressed(MouseEvent mouseEvent)
			{
				openBorderColorPicker();
			}

			@Override
			public void mouseEntered(MouseEvent mouseEvent)
			{
				borderColorIndicator.setIcon(marker.getMarker().getBorderThickness() == 0 ? NO_BORDER_COLOR_HOVER_ICON : BORDER_COLOR_HOVER_ICON);
			}

			@Override
			public void mouseExited(MouseEvent mouseEvent)
			{
				borderColorIndicator.setIcon(marker.getMarker().getBorderThickness() == 0 ? NO_BORDER_COLOR_ICON : BORDER_COLOR_ICON);
			}
		});

		fillColorIndicator.setToolTipText("Edit fill color");
		fillColorIndicator.addMouseListener(new MouseAdapter()
		{
			@Override
			public void mousePressed(MouseEvent mouseEvent)
			{
				openFillColorPicker();
			}

			@Override
			public void mouseEntered(MouseEvent mouseEvent)
			{
				fillColorIndicator.setIcon(marker.getMarker().getFill().getAlpha() == 0 ? NO_FILL_COLOR_HOVER_ICON : FILL_COLOR_HOVER_ICON);
			}

			@Override
			public void mouseExited(MouseEvent mouseEvent)
			{
				fillColorIndicator.setIcon(marker.getMarker().getFill().getAlpha() == 0 ? NO_FILL_COLOR_ICON : FILL_COLOR_ICON);
			}
		});

		thicknessSpinner.setValue(marker.getMarker().getBorderThickness());
		thicknessSpinner.setPreferredSize(new Dimension(50, 20));
		thicknessSpinner.addChangeListener(ce -> updateThickness(true));

		opacityIndicator.setToolTipText("Toggle background transparency");
		opacityIndicator.addMouseListener(new MouseAdapter()
		{
			@Override
			public void mousePressed(MouseEvent mouseEvent)
			{
				final Color fill = marker.getMarker().getFill();

				if (fill.getAlpha() == 0)
				{
					marker.getMarker().setFill(new Color(fill.getRed(), fill.getGreen(), fill.getBlue(), DEFAULT_FILL_OPACITY));
				}
				else
				{
					marker.getMarker().setFill(new Color(fill.getRed(), fill.getGreen(), fill.getBlue(), 0));
				}

				updateFill();
				plugin.updateConfig();
			}

			@Override
			public void mouseEntered(MouseEvent mouseEvent)
			{
				opacityIndicator.setIcon(marker.getMarker().getFill().getAlpha() == 0 ? NO_OPACITY_HOVER_ICON : FULL_OPACITY_HOVER_ICON);
			}

			@Override
			public void mouseExited(MouseEvent mouseEvent)
			{
				opacityIndicator.setIcon(marker.getMarker().getFill().getAlpha() == 0 ? NO_OPACITY_ICON : FULL_OPACITY_ICON);
			}
		});

		leftActions.add(borderColorIndicator);
		leftActions.add(fillColorIndicator);
		leftActions.add(opacityIndicator);
		leftActions.add(thicknessSpinner);

		JPanel rightActions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
		rightActions.setBackground(ColorScheme.DARKER_GRAY_COLOR);

		visibilityLabel.setToolTipText(visible ? "Hide screen marker" : "Show screen marker");
		visibilityLabel.addMouseListener(new MouseAdapter()
		{
			@Override
			public void mousePressed(MouseEvent mouseEvent)
			{
				toggle(!visible);
			}

			@Override
			public void mouseEntered(MouseEvent mouseEvent)
			{
				visibilityLabel.setIcon(visible ? VISIBLE_HOVER_ICON : INVISIBLE_HOVER_ICON);
			}

			@Override
			public void mouseExited(MouseEvent mouseEvent)
			{
				updateVisibility();
			}
		});

		deleteLabel.setIcon(DELETE_ICON);
		deleteLabel.setToolTipText("Delete screen marker");
		deleteLabel.addMouseListener(new MouseAdapter()
		{
			@Override
			public void mousePressed(MouseEvent mouseEvent)
			{
				int confirm = JOptionPane.showConfirmDialog(ScreenMarkerPanel.this,
					"Are you sure you want to permanently delete this screen marker?",
					"Warning", JOptionPane.OK_CANCEL_OPTION);

				if (confirm == 0)
				{
					plugin.deleteMarker(marker);
				}
			}

			@Override
			public void mouseEntered(MouseEvent mouseEvent)
			{
				deleteLabel.setIcon(DELETE_HOVER_ICON);
			}

			@Override
			public void mouseExited(MouseEvent mouseEvent)
			{
				deleteLabel.setIcon(DELETE_ICON);
			}
		});

		JPanel ahkActions = new JPanel();

		ahkActions.setLayout(new BoxLayout(ahkActions, BoxLayout.Y_AXIS));
		{
			ahkActions.add(UI.createJPanel(p-> {
				p.setLayout(new GridLayout(0,4));
				p.add(toggleLabel("animation", marker.getMarker()::setCheckAnimation, marker.getMarker()::isCheckAnimation));
				p.add(toggleLabel("interaction", marker.getMarker()::setCheckInteraction, marker.getMarker()::isCheckInteraction));
				p.add(toggleLabel("movement", marker.getMarker()::setCheckMovement, marker.getMarker()::isCheckMovement));
				p.add(toggleLabel("bank", marker.getMarker()::setCheckBank, marker.getMarker()::isCheckBank));
				p.add(toggleLabel("craft", marker.getMarker()::setCheckCraft, marker.getMarker()::isCheckCraft));
				p.add(toggleLabel("lvlUp", marker.getMarker()::setCheckLevel, marker.getMarker()::isCheckLevel));
				p.add(toggleLabel("chatopt", marker.getMarker()::setCheckChatOptions, marker.getMarker()::isCheckChatOptions));
				p.add(toggleLabel("continue", marker.getMarker()::setCheckContinue, marker.getMarker()::isCheckContinue));
				p.add(toggleLabel("stamina", marker.getMarker()::setCheckStam, marker.getMarker()::isCheckStam));
			}));
			ahkActions.add(UI.createJPanel(p-> {
				p.setLayout(new GridLayout(0,2));
				p.add(spinner("HP", marker.getMarker()::setMinHealth, marker.getMarker()::getMinHealth));
				p.add(spinner("min max", marker.getMarker()::setMaxHealth, marker.getMarker()::getMaxHealth));
				p.add(spinner("Pray", marker.getMarker()::setMinPrayer, marker.getMarker()::getMinPrayer));
				p.add(spinner("min max", marker.getMarker()::setMaxPrayer, marker.getMarker()::getMaxPrayer));
				p.add(spinner("Energy", marker.getMarker()::setMinEnergy, marker.getMarker()::getMinEnergy));
				p.add(spinner("min max", marker.getMarker()::setMaxEnergy, marker.getMarker()::getMaxEnergy));
				p.add(spinner("Inven", marker.getMarker()::setMinInventory, marker.getMarker()::getMinInventory));
				p.add(spinner("min max", marker.getMarker()::setMaxInventory, marker.getMarker()::getMaxInventory));
			}));
			ahkActions.setPreferredSize(ahkActions.getPreferredSize());
			UI.align(ahkActions);
			ahkActions.setBackground(ColorScheme.DARKER_GRAY_COLOR);
		}
		rightActions.add(visibilityLabel);
		rightActions.add(deleteLabel);

		bottomContainer.add(leftActions, BorderLayout.WEST);
		bottomContainer.add(rightActions, BorderLayout.EAST);
//		add(ahkActions, BorderLayout.SOUTH);
//		add(nameWrapper, BorderLayout.NORTH);
//		add(bottomContainer, BorderLayout.CENTER);
		add(nameWrapper);
		add(bottomContainer);
		add(ahkActions);


		updateVisibility();
		updateFill();
		updateBorder();
		updateBorder();

	}

	private void preview(boolean on)
	{
		if (visible)
		{
			return;
		}

		marker.getMarker().setVisible(on);
	}

	private void toggle(boolean on)
	{
		visible = on;
		marker.getMarker().setVisible(visible);
		plugin.updateConfig();
		updateVisibility();
	}

	private void save()
	{
		marker.getMarker().setName(nameInput.getText());
		plugin.updateConfig();

		nameInput.setEditable(false);
		updateNameActions(false);
		requestFocusInWindow();
	}

	private void cancel()
	{
		nameInput.setEditable(false);
		nameInput.setText(marker.getMarker().getName());
		updateNameActions(false);
		requestFocusInWindow();
	}

	private void updateNameActions(boolean saveAndCancel)
	{
		save.setVisible(saveAndCancel);
		cancel.setVisible(saveAndCancel);
		rename.setVisible(!saveAndCancel);

		if (saveAndCancel)
		{
			nameInput.getTextField().requestFocusInWindow();
			nameInput.getTextField().selectAll();
		}
	}

	/* Updates the thickness without saving on config */
	private void updateThickness(boolean save)
	{
		marker.getMarker().setBorderThickness((Integer) thicknessSpinner.getValue());
		updateBorder();
		if (save)
		{
			plugin.updateConfig();
		}
	}

	private void updateVisibility()
	{
		visibilityLabel.setIcon(visible ? VISIBLE_ICON : INVISIBLE_ICON);
	}

	private void updateFill()
	{
		final boolean isFullyTransparent = marker.getMarker().getFill().getAlpha() == 0;

		if (isFullyTransparent)
		{
			fillColorIndicator.setBorder(null);
		}
		else
		{
			Color color = marker.getMarker().getFill();
			Color fullColor = new Color(color.getRed(), color.getGreen(), color.getBlue());
			fillColorIndicator.setBorder(new MatteBorder(0, 0, 3, 0, fullColor));
		}

		fillColorIndicator.setIcon(isFullyTransparent ? NO_FILL_COLOR_ICON : FILL_COLOR_ICON);
		opacityIndicator.setIcon(isFullyTransparent ? NO_OPACITY_ICON : FULL_OPACITY_ICON);
	}

	private void updateBorder()
	{
		if (marker.getMarker().getBorderThickness() == 0)
		{
			borderColorIndicator.setBorder(null);
		}
		else
		{
			Color color = marker.getMarker().getColor();
			borderColorIndicator.setBorder(new MatteBorder(0, 0, 3, 0, color));
		}

		borderColorIndicator.setIcon(marker.getMarker().getBorderThickness() == 0 ? NO_BORDER_COLOR_ICON : BORDER_COLOR_ICON);
	}

	private void openFillColorPicker()
	{
		RuneliteColorPicker colorPicker = plugin.getColorPickerManager().create(
			SwingUtilities.windowForComponent(this),
			marker.getMarker().getFill(),
			marker.getMarker().getName() + " Fill",
			false);
		colorPicker.setLocation(getLocationOnScreen());
		colorPicker.setOnColorChange(c ->
		{
			marker.getMarker().setFill(c);
			updateFill();
		});
		colorPicker.setOnClose(c -> plugin.updateConfig());
		colorPicker.setVisible(true);
	}

	private void openBorderColorPicker()
	{
		RuneliteColorPicker colorPicker = plugin.getColorPickerManager().create(
			SwingUtilities.windowForComponent(this),
			marker.getMarker().getColor(),
			marker.getMarker().getName() + " Border",
			false);
		colorPicker.setLocation(getLocationOnScreen());
		colorPicker.setOnColorChange(c ->
		{
			marker.getMarker().setColor(c);
			updateBorder();
		});
		colorPicker.setOnClose(c -> plugin.updateConfig());
		colorPicker.setVisible(true);
	}

//	LOW(new Color(204,50,50)),
//	MED(new Color(231,180,22)),
//	HIGH(new Color(45,201,55));
	private Color enabled = new Color(45,201,55);
	private Color disabled = new Color(204,50,50);
	public JLabel toggleLabel(String name, Consumer<Boolean> consumer, Supplier<Boolean> supplier)
	{
		JLabel ahk1 = new JLabel(name.substring(0,Math.min(7,name.length())));
		ahk1.setBackground(supplier.get() ? enabled : disabled);
		ahk1.setBorder(supplier.get() ? BorderFactory.createLoweredBevelBorder() : BorderFactory.createRaisedBevelBorder());
		ahk1.setToolTipText(supplier.get() ? "Don't check "+name : "Check "+name);
		ahk1.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent mouseEvent) {
				if (supplier.get()) {
					consumer.accept(false);
				} else {
					consumer.accept(true);
				}
				ahk1.setBackground(supplier.get() ? enabled : disabled);
				ahk1.setBorder(supplier.get() ? BorderFactory.createLoweredBevelBorder() : BorderFactory.createRaisedBevelBorder());
				ahk1.setToolTipText(supplier.get() ? "Don't check "+name : "Check "+name);
				plugin.updateConfig();
			}

			@Override
			public void mouseEntered(MouseEvent mouseEvent) {
//				ahk1.setIcon(supplier.get() ? VISIBLE_HOVER_ICON : INVISIBLE_HOVER_ICON);
			}

			@Override
			public void mouseExited(MouseEvent mouseEvent) {
//				ahk1.setIcon(supplier.get() ? VISIBLE_ICON : INVISIBLE_ICON);
			}
		});
		return ahk1;
	}
	public JPanel spinner(String name, Consumer<Integer> consumer, Supplier<Integer> supplier)
	{

		SpinnerModel spinnerModel = new SpinnerNumberModel(supplier.get().intValue(), 0, Integer.MAX_VALUE, 1);
		JSpinner spinner = new JSpinner(spinnerModel);
		spinner.setPreferredSize(new Dimension(50, 20));

		JLabel label = new JLabel(name);
		spinner.addChangeListener(l -> {
			consumer.accept((Integer)spinner.getValue());
		});
		JPanel p = new JPanel(new GridLayout(0,2));
		p.add(label);
		p.add(spinner);
		return p;
	}
}
