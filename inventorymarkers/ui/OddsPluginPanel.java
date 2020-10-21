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
package net.runelite.client.plugins.inventorymarkers.ui;

import lombok.Getter;
import net.runelite.client.plugins.UI;
import net.runelite.client.plugins.inventorymarkers.OddsConfig;
import net.runelite.client.plugins.inventorymarkers.OddsPlugin;
import net.runelite.client.plugins.inventorymarkers.math.DuelSimulator;
import net.runelite.client.plugins.inventorymarkers.math.RSPlayer;
import net.runelite.client.ui.ColorScheme;
import net.runelite.client.ui.PluginPanel;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.concurrent.atomic.AtomicReference;

public class OddsPluginPanel extends PluginPanel
{
	private static final Color DEFAULT_BORDER_COLOR = Color.GREEN;
	private static final Color DEFAULT_FILL_COLOR = new Color(0, 255, 0, 0);

	private static final int DEFAULT_BORDER_THICKNESS = 3;

	private final JLabel title = new JLabel();
	private final JPanel baseCalc = new JPanel();
	private final JPanel markerView = new JPanel(new GridBagLayout());

	@Getter
	private Color selectedColor = DEFAULT_BORDER_COLOR;

	@Getter
	private Color selectedFillColor = DEFAULT_FILL_COLOR;

	@Getter
	private int selectedBorderThickness = DEFAULT_BORDER_THICKNESS;

	private OddsPlugin plugin;
	private OddsConfig config;
	public OddsPluginPanel(OddsPlugin plugin, OddsConfig config)
	{
		this.plugin = plugin;
		this.config = config;

		setLayout(new BorderLayout());
		setBorder(new EmptyBorder(10, 10, 10, 10));

		JPanel northPanel = new JPanel(new BorderLayout());
		northPanel.setBorder(new EmptyBorder(1, 0, 10, 0));

		title.setText("Odds calc");
		title.setForeground(Color.WHITE);

		northPanel.add(title, BorderLayout.WEST);

		JPanel centerPanel = new JPanel(new BorderLayout());
		centerPanel.setBackground(ColorScheme.DARK_GRAY_COLOR);

		markerView.setBackground(ColorScheme.DARK_GRAY_COLOR);

		GridBagConstraints constraints = new GridBagConstraints();
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.weightx = 1;
		constraints.gridx = 0;
		constraints.gridy = 0;


		setCalc();
		markerView.add(baseCalc, constraints);
		constraints.gridy++;

		centerPanel.add(markerView, BorderLayout.CENTER);

		add(northPanel, BorderLayout.NORTH);
		add(centerPanel, BorderLayout.CENTER);
	}

	private JPanel oddsPanel = new JPanel();
	private PlayerOddsPanel lastPanel = null;
	private AtomicReference<PlayerOddsPanel> panelAtomicReference = new AtomicReference<>(null);
	public void rebuild()
	{
		if (plugin.getPanels().size() != (markerView.getComponentCount() - 1)/2) {
			rebuildd();
		}
		PlayerOddsPanel panel = panelAtomicReference.get();
		if (oddsPanel.getComponentCount() == 0 && panel != null) {
			oddsPanel.add(panel);
			rebuildd();
		}

		repaint();
		revalidate();
	}

	private void rebuildd() {
		GridBagConstraints constraints = new GridBagConstraints();
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.weightx = 1;
		constraints.gridx = 0;
		constraints.gridy = 0;

		markerView.removeAll();
		markerView.add(baseCalc);
		constraints.gridy++;

		for (final JPanel marker : plugin.getPanels()) {
			markerView.add(marker, constraints);
			constraints.gridy++;

			markerView.add(Box.createRigidArea(new Dimension(0, 10)), constraints);
			constraints.gridy++;
		}
	}

	private void setCalc() {
		JSpinner s1 = new JSpinner(new SpinnerNumberModel(99.0,1.0,99.0,1.0));
		JSpinner s2 = new JSpinner(new SpinnerNumberModel(99.0,1.0,99.0,1.0));
		JSpinner s3 = new JSpinner(new SpinnerNumberModel(99.0,1.0,99.0,1.0));
		JSpinner s4 = new JSpinner(new SpinnerNumberModel(99.0,1.0,99.0,1.0));
		JSpinner s5 = new JSpinner(new SpinnerNumberModel(99.0,1.0,99.0,1.0));
		JSpinner o1 = new JSpinner(new SpinnerNumberModel(99.0,1.0,99.0,1.0));
		JSpinner o2 = new JSpinner(new SpinnerNumberModel(99.0,1.0,99.0,1.0));
		JSpinner o3 = new JSpinner(new SpinnerNumberModel(99.0,1.0,99.0,1.0));
		JSpinner o4 = new JSpinner(new SpinnerNumberModel(99.0,1.0,99.0,1.0));
		JSpinner o5 = new JSpinner(new SpinnerNumberModel(99.0,1.0,99.0,1.0));

		DuelSimulator simulator = new DuelSimulator(config,new RSPlayer("a",1,1,1,1,1), new RSPlayer("OpponentName",1,1,1,1,1));
		simulator.base1Results();
		PlayerOddsPanel panel = new PlayerOddsPanel(plugin,config,simulator);
		oddsPanel.add(panel);
		panelAtomicReference.set(panel);
		JButton calc = new JButton("Calculate");
		calc.addActionListener(l-> {
			RSPlayer self = new RSPlayer("Self",(Double)s1.getValue(),(Double)s2.getValue(),(Double)s3.getValue(),(Double)s4.getValue(),(Double)s5.getValue());

			RSPlayer opp = new RSPlayer("Opponent",(Double)o1.getValue(),(Double)o2.getValue(),(Double)o3.getValue(),(Double)o4.getValue(),(Double)o5.getValue());

			plugin.getExecutorService().execute(()-> {
				panelAtomicReference.set(null);
				oddsPanel.removeAll();
				DuelSimulator ds = new DuelSimulator(config, self, opp);
				SwingUtilities.invokeLater(()->panelAtomicReference.set(new PlayerOddsPanel(plugin, config, ds)));
			});
		});

		JButton removeAll = new JButton("Remove all");
		removeAll.addActionListener(l-> {
			plugin.getPanels().clear();
		});
		baseCalc.setLayout(new BoxLayout(baseCalc,BoxLayout.Y_AXIS));
		baseCalc.add(UI.createJPanel(p -> {
			p.setLayout(new GridLayout(0,3));
			p.add(new JLabel("Attack"));
			p.add(s1);
			p.add(o1);
			p.add(new JLabel("Strength"));
			p.add(s2);
			p.add(o2);
			p.add(new JLabel("Defence"));
			p.add(s3);
			p.add(o3);
			p.add(new JLabel("Health"));
			p.add(s4);
			p.add(o4);
			p.add(new JLabel("Ranged"));
			p.add(s5);
			p.add(o5);
		}));
		baseCalc.add(oddsPanel);
		baseCalc.add(calc);
		baseCalc.add(removeAll);
		UI.align(baseCalc);
		baseCalc.setPreferredSize(baseCalc.getPreferredSize());
	}
}
