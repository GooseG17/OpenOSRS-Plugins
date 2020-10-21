package net.runelite.client.plugins.inventorymarkers.math;

public enum Equipment {
   TENTACLE(CombatStyle.MELEE, 1.0D, 1.0D, new EquipmentBonus(0.0D, 90.0D, 0.0D, 0.0D, 0.0D, 86.0D, 0.0D, 0.0D)),
   WHIP(CombatStyle.MELEE, 1.0D, 1.0D, new EquipmentBonus(0.0D, 82.0D, 0.0D, 0.0D, 0.0D, 82.0D, 0.0D, 0.0D)),
   DDS(CombatStyle.MELEE, 1.25D, 1.15D, new EquipmentBonus(40.0D, 25.0D, -4.0D, 1.0D, 0.0D, 40.0D, 0.0D, 0.0D)),
   D_SCIM(CombatStyle.MELEE, 1.0D, 1.0D, new EquipmentBonus(8.0D, 67.0D, -2.0D, 1.0D, 0.0D, 66.0D, 0.0D, 0.0D)),
   BOX(CombatStyle.MELEE, 1.0D, 1.0D, new EquipmentBonus(0.0D, 0.0D, 0.0D, 0.0D, 0.0D, 0.0D, 0.0D, 0.0D)),
   RUNE_KNIFE(CombatStyle.RANGED, 1.0D, 1.0D, new EquipmentBonus(0.0D, 0.0D, 0.0D, 0.0D, 25.0D, 0.0D, 24.0D, 0.0D));

   private final CombatStyle style;
   private final double accuracyFactor;
   private final double maxHitFactor;
   private final EquipmentBonus equipmentBonus;

   private Equipment(CombatStyle style, double accuracyFactor, double maxHitFactor, EquipmentBonus equipmentBonus) {
      this.style = style;
      this.accuracyFactor = accuracyFactor;
      this.maxHitFactor = maxHitFactor;
      this.equipmentBonus = equipmentBonus;
   }

   public CombatStyle getStyle() {
      return this.style;
   }

   public double getAccuracyFactor() {
      return this.accuracyFactor;
   }

   public double getMaxHitFactor() {
      return this.maxHitFactor;
   }

   public EquipmentBonus getEquipmentBonus() {
      return this.equipmentBonus;
   }
}
