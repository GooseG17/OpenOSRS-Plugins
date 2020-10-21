package net.runelite.client.plugins.inventorymarkers.math;

import net.runelite.client.plugins.inventorymarkers.OddsConfig;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class DuelSimulator {
   private final RSPlayer self;
   private final RSPlayer opponent;
   private final int trials;
   private final Map<DuelType, Double> results;
   private final Map<DuelType, Double> ddsResults;
   private final List<RSPlayer> players = new ArrayList(2);

   private OddsConfig config;
   public DuelSimulator(OddsConfig config, RSPlayer self, RSPlayer opponent) {
      this.trials = config.getTrialCount();
      this.config = config;
      this.self = self;
      this.opponent = opponent;
      this.results = new HashMap(DuelType.values().length);
      this.ddsResults = new HashMap(DuelType.values().length);
      this.players.add(self);
      this.players.add(opponent);
      if (!config.calculateBoxing()) {
         results.put(DuelType.BOX, 0.0);
         ddsResults.put(DuelType.BOX, 0.0);
      }
      if (!config.calculateWhip()) {
         results.put(DuelType.WHIP, 0.0);
         ddsResults.put(DuelType.WHIP, 0.0);
      }
      if (!config.calculateTentacle()) {
         results.put(DuelType.TENT, 0.0);
         ddsResults.put(DuelType.TENT, 0.0);
      }
      if (!config.calculateRanged()) {
         results.put(DuelType.R_KNIFE, 0.0);
         ddsResults.put(DuelType.R_KNIFE, 0.0);
      }
      if (!config.calculateScim()) {
         results.put(DuelType.SCIM, 0.0);
         ddsResults.put(DuelType.SCIM, 0.0);
      }
      if (!config.calculateDDS()) {
         ddsResults.put(DuelType.BOX, 0.0);
         ddsResults.put(DuelType.R_KNIFE, 0.0);
         ddsResults.put(DuelType.TENT, 0.0);
         ddsResults.put(DuelType.WHIP, 0.0);
         ddsResults.put(DuelType.SCIM, 0.0);
      }
      for (DuelType value : DuelType.values()) {
         getOdds(value);
         getOdds(value,DuelType.DDS);
      }
   }

   private Double calculateOdds(final DuelType type, final DuelType spec) {
      double wins = 0.0D;
      for(int i = 0; i < this.trials; ++i) {
         Collections.shuffle(this.players);
         RSPlayer a = (RSPlayer)this.players.get(0);
         RSPlayer b = (RSPlayer)this.players.get(1);
         if (a == null || b == null) return 0.0;
         a.resetHP();
         b.resetHP();
         if (spec == DuelType.DDS) {
            for(int j = 0; j < 8; ++j) {
               if (getHitChance(DuelType.DDS, b, a) > ThreadLocalRandom.current().nextDouble()) {
                  a.applyDamage((double)ThreadLocalRandom.current().nextInt(0, (int)b.getMaxHit(DuelType.DDS) + 1));
                  if (a.isDead()) {
                     wins += b.equals(this.self) ? 1.0D : 0.0D;
                     break;
                  }
               }

               if (getHitChance(DuelType.DDS, a, b) > ThreadLocalRandom.current().nextDouble()) {
                  b.applyDamage((double)ThreadLocalRandom.current().nextInt(0, (int)a.getMaxHit(DuelType.DDS) + 1));
                  if (b.isDead()) {
                     wins += a.equals(this.self) ? 1.0D : 0.0D;
                     break;
                  }
               }
            }
         }

         while(!this.self.isDead() && !this.opponent.isDead()) {
            if (getHitChance(type, b, a) > ThreadLocalRandom.current().nextDouble()) {
               a.applyDamage((double)ThreadLocalRandom.current().nextInt(0, (int)b.getMaxHit(type) + 1));
               if (a.isDead()) {
                  wins += b.equals(this.self) ? 1.0D : 0.0D;
                  break;
               }
            }

            if (getHitChance(type, a, b) > ThreadLocalRandom.current().nextDouble()) {
               b.applyDamage((double)ThreadLocalRandom.current().nextInt(0, (int)a.getMaxHit(type) + 1));
               if (b.isDead()) {
                  wins += a.equals(this.self) ? 1.0D : 0.0D;
                  break;
               }
            }
         }
      }

      return 100.0D * wins / (double)this.trials;
   }

   public Double getOdds(DuelType type) {
      return getOdds(type,DuelType.BOX);
   }

   public Double getOdds(final DuelType type, final DuelType spec) {
      Map<DuelType, Double> temp = spec == DuelType.DDS ? ddsResults : results;
      if (!temp.containsKey(type)) {
         temp.put(type, this.calculateOdds(type, spec));
      }
      return temp.get(type);
   }

   private static double getHitChance(DuelType type, RSPlayer a, RSPlayer b) {
      double attack = a.getAccuracyRoll(type);
      double defense = b.getDefensiveRoll(type);
      return attack > defense ? 1.0D - (defense + 2.0D) / (2.0D * (attack + 1.0D)) : attack / (2.0D * (defense + 1.0D));
   }

   public RSPlayer getSelf() {
      return this.self;
   }

   public RSPlayer getOpponent() {
      return this.opponent;
   }

   public int getTrials() {
      return this.trials;
   }

   public Map<DuelType, Double> getResults() {
      return this.results;
   }

   public void base1Results() {
      ddsResults.put(DuelType.BOX, 1.0);
      ddsResults.put(DuelType.R_KNIFE, 1.0);
      ddsResults.put(DuelType.TENT, 1.0);
      ddsResults.put(DuelType.WHIP, 1.0);
      ddsResults.put(DuelType.SCIM, 1.0);
      results.put(DuelType.BOX, 1.0);
      results.put(DuelType.R_KNIFE, 1.0);
      results.put(DuelType.TENT, 1.0);
      results.put(DuelType.WHIP, 1.0);
      results.put(DuelType.SCIM, 1.0);
   }
}
