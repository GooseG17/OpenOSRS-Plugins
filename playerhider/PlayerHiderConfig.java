package net.runelite.client.plugins.playerhider;

import net.runelite.api.Skill;
import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.ConfigSection;

@ConfigGroup("playerhider")
public interface PlayerHiderConfig extends Config
{
	@ConfigItem(
		name = "Player model",
		keyName = "playerModel",
		description = "Changes playerModel"
	)
	default int playerModel()
	{
		return -1;
	}

	@ConfigItem(
			name = "Player stats",
			keyName = "playerStats",
			description = "Changes playerStats"
	)
	default boolean playerStats()
	{
		return true;
	}

	@ConfigItem(
			name = "Thumbnail stats",
			keyName = "thumbnail",
			description = "Changes playerStats"
	)
	default boolean thumbnail()
	{
		return false;
	}

	@ConfigSection(
			name = "Stat change",
			description = "Stat change",
			position = 0,
			closedByDefault = false
	)
	String statChange = "statChange";


	@ConfigItem(
			name = "toggleStatChange",
			keyName = "toggleStatChange",
			description = "Changes playerStats",
			section = statChange
	)
	default boolean toggleStatChange()
	{
		return true;
	}


	@ConfigItem(
			name = "skillToChange",
			keyName = "skillToChange",
			description = "Changes skillToChange",
			section = statChange
	)
	default Skill skillToChange()
	{
		return Skill.HITPOINTS;
	}

	@ConfigItem(
			name = "statLevel",
			keyName = "statLevel",
			description = "Changes statLevel",
			section = statChange
	)
	default int statLevel()
	{
		return 1;
	}

}
