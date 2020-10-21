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

import net.runelite.client.plugins.UI;
import net.runelite.client.plugins.inventorymarkers.OddsConfig;
import net.runelite.client.plugins.inventorymarkers.OddsPlugin;
import net.runelite.client.plugins.inventorymarkers.math.DuelSimulator;
import net.runelite.client.plugins.inventorymarkers.math.DuelType;
import org.apache.commons.lang3.StringUtils;

import javax.swing.*;
import java.awt.*;
import java.text.DecimalFormat;

public class PlayerOddsPanel extends JPanel
{
	private OddsPlugin plugin;
	private OddsConfig config;
	private DuelSimulator simulator;
	private String opponentName = "";
	public PlayerOddsPanel(OddsPlugin plugin, OddsConfig config, DuelSimulator simulator) {
		this.plugin = plugin;
		this.config = config;
		this.simulator = simulator;
		this.setLayout(new BoxLayout(this,BoxLayout.Y_AXIS));

		this.add(UI.createJPanel(p-> {
			p.setLayout(new GridLayout(0,1));
			p.add(new JLabel(simulator.getOpponent().getUserName()));
			p.setSize(p.getPreferredSize());
		}));
		opponentName = simulator.getOpponent().getUserName();
		this.add(createLabelStats());

		GridLayout gl = new GridLayout(0,2);
		JPanel grid = new JPanel();
		grid.setLayout(gl);
		grid.add(UI.createJPanel(p -> {
			p.setLayout(new GridLayout(0,1));
			p.add(new JLabel("With DDS"));
			createLabel(p,DuelType.TENT, DuelType.DDS);
			createLabel(p,DuelType.WHIP, DuelType.DDS);
			createLabel(p,DuelType.SCIM, DuelType.DDS);
			createLabel(p,DuelType.BOX, DuelType.DDS);
			createLabel(p,DuelType.R_KNIFE, DuelType.DDS);
			UI.align(p);
		}));

		grid.add(UI.createJPanel(p -> {
			p.setLayout(new GridLayout(0,1));
			p.add(new JLabel("No DDS"));
			createLabel(p,DuelType.TENT, DuelType.BOX);
			createLabel(p,DuelType.WHIP, DuelType.BOX);
			createLabel(p,DuelType.SCIM, DuelType.BOX);
			createLabel(p,DuelType.BOX, DuelType.BOX);
			createLabel(p,DuelType.R_KNIFE, DuelType.BOX);
			UI.align(p);
		}));

		this.add(grid);

		JButton remove = new JButton("Remove");
		remove.addActionListener(l -> {
			plugin.getPanels().remove(this);
		});
		this.add(remove);
		UI.align(this);
	}

	private Color getColor(double a, double b) {
		if (a > b) {
			return Color.GREEN;
		} else {
			return b > a ? Color.RED : Color.YELLOW;
		}
	}

	public String getOpponentName() {
		return opponentName;
	}
	DecimalFormat df = new DecimalFormat("#.#");
	private void createLabel(JPanel main, DuelType dt, DuelType spec) {
		JPanel p = new JPanel(new GridLayout(0,2));
		Double odds = simulator.getOdds(dt, spec);
		if (odds <= 0) return;
		p.add(UI.createJLabel(l -> l.setText(StringUtils.capitalize(dt.toString().toLowerCase()))));
		p.add(UI.createJLabel(l -> {
			l.setText(df.format(odds));
			l.setForeground(odds < 49.7 ? Color.RED : (odds > 50 ? Color.GREEN : Color.YELLOW));
		}));
		main.add(p);
	}
	private JPanel createLabelStats() {
		JPanel p = new JPanel();
		p.setLayout(new GridLayout(0,4));
		p.add(UI.createJLabel(l -> l.setText("Attack")));
		p.add(UI.createJLabel(l ->{
			l.setText(simulator.getSelf().getAttackLevel()+ "/"+simulator.getOpponent().getAttackLevel()+"");
			l.setForeground(this.getColor(simulator.getSelf().getAttackLevel(), simulator.getOpponent().getAttackLevel()));
		}));

		p.add(UI.createJLabel(l -> l.setText("Strength")));
		p.add(UI.createJLabel(l ->{
			l.setText(simulator.getSelf().getStrengthLevel()+ "/"+simulator.getOpponent().getStrengthLevel()+"");
			l.setForeground(this.getColor(simulator.getSelf().getStrengthLevel(), simulator.getOpponent().getStrengthLevel()));
		}));

		p.add(UI.createJLabel(l -> l.setText("Defence")));
		p.add(UI.createJLabel(l ->{
			l.setText(simulator.getSelf().getDefenseLevel()+ "/"+simulator.getOpponent().getDefenseLevel()+"");
			l.setForeground(this.getColor(simulator.getSelf().getDefenseLevel(), simulator.getOpponent().getDefenseLevel()));
		}));

		p.add(UI.createJLabel(l -> l.setText("Hitpoints")));
		p.add(UI.createJLabel(l ->{
			l.setText(simulator.getSelf().getHitpointsLevel()+ "/"+simulator.getOpponent().getHitpointsLevel()+"");
			l.setForeground(this.getColor(simulator.getSelf().getHitpointsLevel(), simulator.getOpponent().getHitpointsLevel()));
		}));
		p.add(UI.createJLabel(l -> l.setText("Ranged")));
		p.add(UI.createJLabel(l ->{
			l.setText(simulator.getSelf().getRangedLevel()+ "/"+simulator.getOpponent().getRangedLevel()+"");
			l.setForeground(this.getColor(simulator.getSelf().getRangedLevel(), simulator.getOpponent().getRangedLevel()));
		}));
		UI.align(p);
		return p;
	}
}
