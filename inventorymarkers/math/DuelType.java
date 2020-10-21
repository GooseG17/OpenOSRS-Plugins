package net.runelite.client.plugins.inventorymarkers.math;

public enum DuelType {
   DDS(Equipment.DDS),
   TENT(Equipment.TENTACLE),
   WHIP(Equipment.WHIP),
   SCIM(Equipment.D_SCIM),
   R_KNIFE(Equipment.RUNE_KNIFE),
   BOX(Equipment.BOX)
   ;

   private final Equipment equipment;

   private DuelType(Equipment equipment) {
      this.equipment = equipment;
   }

   public Equipment getEquipment() {
      return this.equipment;
   }
}
