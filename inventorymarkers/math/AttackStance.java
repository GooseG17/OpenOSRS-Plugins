package net.runelite.client.plugins.inventorymarkers.math;

public enum AttackStance {
   MELEE_ACCURATE(3.0D, 0.0D, 0.0D, 0.0D),
   CONTROLLED(1.0D, 1.0D, 1.0D, 0.0D),
   DEFENSIVE(0.0D, 0.0D, 3.0D, 0.0D),
   AGGRESSIVE(0.0D, 3.0D, 0.0D, 0.0D),
   RANGED_ACCURATE(0.0D, 0.0D, 0.0D, 3.0D),
   RAPID(0.0D, 0.0D, 0.0D, 0.0D),
   LONGRANGE(0.0D, 0.0D, 3.0D, 0.0D);

   private final double attackBonus;
   private final double strengthBonus;
   private final double defenceBonus;
   private final double rangedBonus;

   private AttackStance(double attackBonus, double strengthBonus, double defenceBonus, double rangedBonus) {
      this.attackBonus = attackBonus;
      this.strengthBonus = strengthBonus;
      this.defenceBonus = defenceBonus;
      this.rangedBonus = rangedBonus;
   }

   public double getAttackBonus() {
      return this.attackBonus;
   }

   public double getStrengthBonus() {
      return this.strengthBonus;
   }

   public double getDefenceBonus() {
      return this.defenceBonus;
   }

   public double getRangedBonus() {
      return this.rangedBonus;
   }
}
