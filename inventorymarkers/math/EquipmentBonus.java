package net.runelite.client.plugins.inventorymarkers.math;

public class EquipmentBonus {
   private final double stabBonus;
   private final double slashBonus;
   private final double crushBonus;
   private final double magicBonus;
   private final double rangedBonus;
   private final double meleeStrengthBonus;
   private final double rangedStrengthBonus;
   private final double magicStrengthBonus;

   public double getBonusForDuelType(DuelType type) {
      switch(type) {
      case DDS:
         return this.stabBonus;
      case TENT:
      case SCIM:
         return this.slashBonus;
      case R_KNIFE:
         return this.rangedBonus;
      case BOX:
         return this.crushBonus;
      default:
         return 0.0D;
      }
   }

   public EquipmentBonus(double stabBonus, double slashBonus, double crushBonus, double magicBonus, double rangedBonus, double meleeStrengthBonus, double rangedStrengthBonus, double magicStrengthBonus) {
      this.stabBonus = stabBonus;
      this.slashBonus = slashBonus;
      this.crushBonus = crushBonus;
      this.magicBonus = magicBonus;
      this.rangedBonus = rangedBonus;
      this.meleeStrengthBonus = meleeStrengthBonus;
      this.rangedStrengthBonus = rangedStrengthBonus;
      this.magicStrengthBonus = magicStrengthBonus;
   }

   public double getStabBonus() {
      return this.stabBonus;
   }

   public double getSlashBonus() {
      return this.slashBonus;
   }

   public double getCrushBonus() {
      return this.crushBonus;
   }

   public double getMagicBonus() {
      return this.magicBonus;
   }

   public double getRangedBonus() {
      return this.rangedBonus;
   }

   public double getMeleeStrengthBonus() {
      return this.meleeStrengthBonus;
   }

   public double getRangedStrengthBonus() {
      return this.rangedStrengthBonus;
   }

   public double getMagicStrengthBonus() {
      return this.magicStrengthBonus;
   }
}
