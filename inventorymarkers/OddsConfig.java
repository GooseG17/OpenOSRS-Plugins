package net.runelite.client.plugins.inventorymarkers;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup("odds")
public interface OddsConfig extends Config {
   @ConfigItem(
        position = 0,
      keyName = "trials",
      name = "Trial Count",
      description = "Number of trials to run during simulations"
   )
   default int getTrialCount() {
      return 100000;
   }

   @ConfigItem(
           position = 1,
      keyName = "tent",
      name = "Tentacle Odds",
      description = "Toggles calculation of tentacle odds"
   )
   default boolean calculateTentacle() {
      return true;
   }

   @ConfigItem(
           position = 2,
           keyName = "whip",
           name = "Abyssal whip Odds",
           description = "Toggles calculation of whip odds"
   )
   default boolean calculateWhip() {
      return false;
   }

   @ConfigItem(
           position = 3,
           keyName = "scim",
           name = "Scim Odds",
           description = "Toggles calculation of scim odds"
   )
   default boolean calculateScim() {
      return false;
   }

   @ConfigItem(
           position = 6,
      keyName = "dds",
      name = "DDS Odds",
      description = "Toggles calculation of DDS odds"
   )
   default boolean calculateDDS() {
      return false;
   }

   @ConfigItem(
           position = 4,
      keyName = "box",
      name = "Boxing Odds",
      description = "Toggles calculation of boxing odds"
   )
   default boolean calculateBoxing() {
      return false;
   }

   @ConfigItem(
           position = 5,
      keyName = "knives",
      name = "Knives Odds",
      description = "Toggles calculation of ranging odds"
   )
   default boolean calculateRanged() {
      return false;
   }

   @ConfigItem(
           position = 7,
      keyName = "challengeSound",
      name = "Sound on Challenge",
      description = "Plays a sound when a challenge is received"
   )
   default boolean challengeSound() {
      return false;
   }

   @ConfigItem(
           position = 8,
           keyName = "playerOption",
           name = "Player option",
           description = "Show Lookup option in right click menu"
   )
   default boolean playerOption()
   {
      return true;
   }

   @ConfigItem(
           position = 9,
           keyName = "duelRequest",
           name = "Duel Request",
           description = "Show Lookup option when challenged"
   )
   default boolean duelRequest()
   {
      return true;
   }
}
