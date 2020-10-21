package net.runelite.client.plugins.inventorymarkers.math;

import com.google.common.base.Strings;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.Skill;
import net.runelite.http.api.hiscore.HiscoreClient;
import net.runelite.http.api.hiscore.HiscoreEndpoint;
import net.runelite.http.api.hiscore.HiscoreResult;

import javax.annotation.Nullable;
import javax.inject.Inject;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class RSPlayer {
   private static final double[] tentCutoffs;
   private static final double[] ddsCutoffs;
   private static final double[] boxCutoffs;
   private static final double[] scimCutoff;
   private final String userName;
   private final double attackLevel;
   private final double strengthLevel;
   private final double defenseLevel;
   private final double hitpointsLevel;
   private final double rangedLevel;
   private final Map<DuelType, AttackStance> stanceMap;
   private final AttackStance tentStance;
   private final AttackStance ddsStance;
   private final AttackStance boxStance;
   private final AttackStance rangeStance;
   private final AttackStance scimStance;
   private double currentHealth;

   public RSPlayer(final String userName, final double attackLevel, final double strengthLevel, final double defenseLevel, final double hitpointsLevel, final double rangedLevel) {
      this.userName = userName;
      this.attackLevel = attackLevel;
      this.strengthLevel = strengthLevel;
      this.defenseLevel = defenseLevel;
      this.hitpointsLevel = hitpointsLevel;
      this.rangedLevel = rangedLevel;
      this.currentHealth = hitpointsLevel;
      this.tentStance = Arrays.stream(tentCutoffs).anyMatch(d -> d == strengthLevel) ? AttackStance.CONTROLLED : AttackStance.MELEE_ACCURATE;
      this.ddsStance = Arrays.stream(ddsCutoffs).anyMatch(d -> d == strengthLevel)
              ? AttackStance.AGGRESSIVE : AttackStance.MELEE_ACCURATE;
      this.scimStance = Arrays.stream(scimCutoff).anyMatch(d -> d == strengthLevel) ? AttackStance.AGGRESSIVE : AttackStance.MELEE_ACCURATE;
      this.boxStance = Arrays.stream(boxCutoffs).anyMatch(d -> d == strengthLevel) ? AttackStance.AGGRESSIVE : AttackStance.MELEE_ACCURATE;
      this.rangeStance = AttackStance.RAPID;
      (this.stanceMap = new HashMap<DuelType, AttackStance>()).put(DuelType.DDS, this.ddsStance);
      this.stanceMap.put(DuelType.BOX, this.boxStance);
      this.stanceMap.put(DuelType.SCIM, this.scimStance);
      this.stanceMap.put(DuelType.TENT, this.tentStance);
      this.stanceMap.put(DuelType.R_KNIFE, this.rangeStance);
      this.stanceMap.put(DuelType.WHIP, this.tentStance);
   }

   @Nullable
   public static RSPlayer fromName(HiscoreClient hiscoreClient, final String name) {
      final String user = name.replace(' ', ' ');
      if (Strings.isNullOrEmpty(user)) {
         return null;
      }
      HiscoreResult result;
      try {
         result = hiscoreClient.lookup(user, HiscoreEndpoint.NORMAL);
      } catch (IOException ex) {
         RSPlayer.log.warn("Error fetching Hiscore data " + ex.getMessage());
         return null;
      }
      if (result == null) {
         return null;
      }
      RSPlayer player = null;
      try {
         final double att = result.getAttack().getLevel();
         final double str = result.getStrength().getLevel();
         final double def = result.getDefence().getLevel();
         final double hit = result.getHitpoints().getLevel();
         final double rng = result.getRanged().getLevel();
         player = new RSPlayer(name, att, str, def, hit, rng);
      } catch (NullPointerException e) {
         RSPlayer.log.warn("Unable to convert hiscore result to RSPlayer");
      }
      return player;
   }

   public static RSPlayer fromSelf(final Client client) {
      final String userName = client.getLocalPlayer().getName();
      final double att = client.getRealSkillLevel(Skill.ATTACK);
      final double str = client.getRealSkillLevel(Skill.STRENGTH);
      final double def = client.getRealSkillLevel(Skill.DEFENCE);
      final double hit = client.getRealSkillLevel(Skill.HITPOINTS);
      final double rng = client.getRealSkillLevel(Skill.RANGED);
      return new RSPlayer(userName, att, str, def, hit, rng);
   }

   protected void resetHP() {
      this.currentHealth = this.hitpointsLevel;
   }

   protected void applyDamage(final double dmg) {
      this.currentHealth -= dmg;
   }

   protected boolean isDead() {
      return this.currentHealth <= 0.0;
   }

   public double getMaxHit(final DuelType type) {
      final Equipment e = type.getEquipment();
      final double a = (e.getStyle() == CombatStyle.MELEE) ? (this.strengthLevel + this.stanceMap.get(type).getStrengthBonus()) : (this.rangedLevel + this.stanceMap.get(type).getRangedBonus());
      final double b = (e.getStyle() == CombatStyle.MELEE) ? e.getEquipmentBonus().getMeleeStrengthBonus() : e.getEquipmentBonus().getRangedStrengthBonus();
      return Math.floor(Math.floor(0.5 + (a + 8.0) * (b + 64.0) / 640.0) * e.getMaxHitFactor());
   }

   public double getAccuracyRoll(final DuelType type) {
      final Equipment e = type.getEquipment();
      final double a = (e.getStyle() == CombatStyle.MELEE) ? (this.attackLevel + this.stanceMap.get(type).getAttackBonus()) : (this.rangedLevel + this.stanceMap.get(type).getRangedBonus());
      final double b = e.getEquipmentBonus().getBonusForDuelType(type);
      return Math.floor(Math.floor((a + 8.0) * (b + 64.0)) * e.getAccuracyFactor());
   }

   public double getDefensiveRoll(final DuelType type) {
      final double a = this.defenseLevel + this.stanceMap.get(type).getDefenceBonus();
      final double b = 0.0;
      return Math.floor((a + 8.0) * 64.0);
   }

   @Override
   public boolean equals(final Object o) {
      if (o == this) {
         return true;
      }
      if (!(o instanceof RSPlayer)) {
         return false;
      }
      final RSPlayer other = (RSPlayer) o;
      if (!other.canEqual(this)) {
         return false;
      }
      final Object this$userName = this.getUserName();
      final Object other$userName = other.getUserName();
      if (this$userName == null) {
         if (other$userName == null) {
            return true;
         }
      } else if (this$userName.equals(other$userName)) {
         return true;
      }
      return false;
   }

   public boolean canEqual(final Object other) {
      return other instanceof RSPlayer;
   }

   @Override
   public int hashCode() {
      final int PRIME = 59;
      int result = 1;
      final Object $userName = this.getUserName();
      result = result * 59 + (($userName == null) ? 43 : $userName.hashCode());
      return result;
   }

   public String getUserName() {
      return this.userName;
   }

   public int getAttackLevel() {
      return (int)this.attackLevel;
   }

   public int getStrengthLevel() {
      return (int)this.strengthLevel;
   }

   public int getDefenseLevel() {
      return (int)this.defenseLevel;
   }

   public int getHitpointsLevel() {
      return (int)this.hitpointsLevel;
   }

   public int getRangedLevel() {
      return (int)this.rangedLevel;
   }

   public Map<DuelType, AttackStance> getStanceMap() {
      return this.stanceMap;
   }

   public double getCurrentHealth() {
      return this.currentHealth;
   }

   static {
      tentCutoffs = new double[]{70.0, 75.0, 79.0, 83.0, 87.0, 92.0, 96.0};
      ddsCutoffs = new double[]{60.0, 61.0, 62.0, 66.0, 67.0, 68.0, 73.0, 74.0, 75.0, 79.0, 80.0, 81.0, 85.0, 86.0, 87.0, 91.0, 92.0, 93.0, 97.0, 98.0, 99.0};
      boxCutoffs = new double[]{4.0, 5.0, 6.0, 14.0, 15.0, 16.0, 24.0, 25.0, 26.0, 34.0, 35.0, 36.0, 44.0, 45.0, 46.0, 54.0, 55.0, 56.0, 64.0, 65.0, 66.0, 74.0, 75.0, 76.0, 84.0, 85.0, 86.0, 94.0, 95.0, 96.0};
      scimCutoff = new double[]{61.0, 62.0, 63.0, 66.0, 67.0, 68.0, 71.0, 72.0, 73.0, 76.0, 77.0, 78.0, 81.0, 82.0, 83.0, 85.0, 86.0, 87.0, 90.0, 91.0, 92.0, 95.0, 96.0, 97.0};
   }
}


