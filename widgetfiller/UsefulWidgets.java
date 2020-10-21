package net.runelite.client.plugins.widgetfiller;

import net.runelite.api.widgets.WidgetInfo;

public enum UsefulWidgets {
    CRAFT_INTERFACE(270, 0),

//    CHATBOX_FULL_INPUT(WidgetInfo.CHATBOX_FULL_INPUT.getGroupId(),WidgetInfo.CHATBOX_FULL_INPUT.getChildId()),

    CHATBOX_FULL_INPUT(WidgetInfo.CHATBOX_FULL_INPUT.getGroupId(),WidgetInfo.CHATBOX_FULL_INPUT.getChildId()),
    BANK_DEPOSIT_INVEN(WidgetInfo.BANK_DEPOSIT_INVENTORY.getGroupId(),WidgetInfo.BANK_DEPOSIT_INVENTORY.getChildId()),
    DIALOG_NPC_CONT(WidgetInfo.DIALOG_NPC_CONTINUE.getGroupId(),WidgetInfo.DIALOG_NPC_CONTINUE.getChildId()),
    DIALOG_OPTION(WidgetInfo.DIALOG_OPTION.getGroupId(),WidgetInfo.DIALOG_OPTION.getChildId()),
    LEVEL_UP(WidgetInfo.LEVEL_UP.getGroupId(),WidgetInfo.LEVEL_UP.getChildId()),
    PVP_WILDERNESS_LEVEL(WidgetInfo.PVP_WILDERNESS_LEVEL.getGroupId(),WidgetInfo.PVP_WILDERNESS_LEVEL.getChildId()),
    BANK_CONTAINER(WidgetInfo.BANK_CONTAINER.getGroupId(),WidgetInfo.BANK_CONTAINER.getChildId()),
    BANK_DEPOSIT_EQUIP(WidgetInfo.BANK_DEPOSIT_EQUIPMENT.getGroupId(),WidgetInfo.BANK_DEPOSIT_EQUIPMENT.getChildId()),
    CHATBOX_PARENT(WidgetInfo.CHATBOX_PARENT.getGroupId(),WidgetInfo.CHATBOX_PARENT.getChildId()),
    CHATBOX(WidgetInfo.CHATBOX.getGroupId(),WidgetInfo.CHATBOX.getChildId()),
    CHATBOX_MESSAGES(WidgetInfo.CHATBOX_MESSAGES.getGroupId(),WidgetInfo.CHATBOX_MESSAGES.getChildId()),
    CHATBOX_BUTTONS(WidgetInfo.CHATBOX_BUTTONS.getGroupId(),WidgetInfo.CHATBOX_BUTTONS.getChildId()),
    CHATBOX_TITLE(WidgetInfo.CHATBOX_TITLE.getGroupId(),WidgetInfo.CHATBOX_TITLE.getChildId()),
    CHATBOX_CONTAINER(WidgetInfo.CHATBOX_CONTAINER.getGroupId(),WidgetInfo.CHATBOX_CONTAINER.getChildId()),
    CHATBOX_REPORT_TEXT(WidgetInfo.CHATBOX_REPORT_TEXT.getGroupId(),WidgetInfo.CHATBOX_REPORT_TEXT.getChildId()),
    CHATBOX_INPUT(WidgetInfo.CHATBOX_INPUT.getGroupId(),WidgetInfo.CHATBOX_INPUT.getChildId()),
    COMBAT_LEVEL(WidgetInfo.COMBAT_LEVEL.getGroupId(),WidgetInfo.COMBAT_LEVEL.getChildId()),
    COMBAT_STYLE_ONE(WidgetInfo.COMBAT_STYLE_ONE.getGroupId(),WidgetInfo.COMBAT_STYLE_ONE.getChildId()),
    COMBAT_STYLE_TWO(WidgetInfo.COMBAT_STYLE_TWO.getGroupId(),WidgetInfo.COMBAT_STYLE_TWO.getChildId()),
    COMBAT_STYLE_THREE(WidgetInfo.COMBAT_STYLE_THREE.getGroupId(),WidgetInfo.COMBAT_STYLE_THREE.getChildId()),
    COMBAT_STYLE_FOUR(WidgetInfo.COMBAT_STYLE_FOUR.getGroupId(),WidgetInfo.COMBAT_STYLE_FOUR.getChildId()),
    COMBAT_SPELLS(WidgetInfo.COMBAT_SPELLS.getGroupId(),WidgetInfo.COMBAT_SPELLS.getChildId()),
    COMBAT_SPELL_BOX(WidgetInfo.COMBAT_SPELL_BOX.getGroupId(),WidgetInfo.COMBAT_SPELL_BOX.getChildId()),
    COMBAT_SPELL_ICON(WidgetInfo.COMBAT_SPELL_ICON.getGroupId(),WidgetInfo.COMBAT_SPELL_ICON.getChildId()),
    COMBAT_SPELL_TEXT(WidgetInfo.COMBAT_SPELL_TEXT.getGroupId(),WidgetInfo.COMBAT_SPELL_TEXT.getChildId()),
    COMBAT_AUTO_RETAL(WidgetInfo.COMBAT_AUTO_RETALIATE.getGroupId(),WidgetInfo.COMBAT_AUTO_RETALIATE.getChildId()),
    DIALOG_SPRITE(WidgetInfo.DIALOG_SPRITE.getGroupId(),WidgetInfo.DIALOG_SPRITE.getChildId()),
    DIALOG_NPC(WidgetInfo.DIALOG_NPC.getGroupId(),WidgetInfo.DIALOG_NPC.getChildId()),
    DIALOG_NPC_NAME(WidgetInfo.DIALOG_NPC_NAME.getGroupId(),WidgetInfo.DIALOG_NPC_NAME.getChildId()),
    DIALOG_NPC_TEXT(WidgetInfo.DIALOG_NPC_TEXT.getGroupId(),WidgetInfo.DIALOG_NPC_TEXT.getChildId()),
    DIALOG_PLAYER(WidgetInfo.DIALOG_PLAYER.getGroupId(),WidgetInfo.DIALOG_PLAYER.getChildId()),
    LEVEL_UP_SKILL(WidgetInfo.LEVEL_UP_SKILL.getGroupId(),WidgetInfo.LEVEL_UP_SKILL.getChildId()),
    LEVEL_UP_LEVEL(WidgetInfo.LEVEL_UP_LEVEL.getGroupId(),WidgetInfo.LEVEL_UP_LEVEL.getChildId()),
    MINIMAP_ORBS(WidgetInfo.MINIMAP_ORBS.getGroupId(),WidgetInfo.MINIMAP_ORBS.getChildId()),
    XP_ORB(WidgetInfo.MINIMAP_XP_ORB.getGroupId(),WidgetInfo.MINIMAP_XP_ORB.getChildId()),
    PRAYER_ORB(WidgetInfo.MINIMAP_PRAYER_ORB.getGroupId(),WidgetInfo.MINIMAP_PRAYER_ORB.getChildId()),
    QUICK_PRAYER_ORB(WidgetInfo.MINIMAP_QUICK_PRAYER_ORB.getGroupId(),WidgetInfo.MINIMAP_QUICK_PRAYER_ORB.getChildId()),
    RUN_ORB(WidgetInfo.MINIMAP_RUN_ORB.getGroupId(),WidgetInfo.MINIMAP_RUN_ORB.getChildId()),
    TOGGLE_RUN_ORB(WidgetInfo.MINIMAP_TOGGLE_RUN_ORB.getGroupId(),WidgetInfo.MINIMAP_TOGGLE_RUN_ORB.getChildId()),
    RUN_ORB_TEXT(WidgetInfo.MINIMAP_RUN_ORB_TEXT.getGroupId(),WidgetInfo.MINIMAP_RUN_ORB_TEXT.getChildId()),
    HEALTH_ORB(WidgetInfo.MINIMAP_HEALTH_ORB.getGroupId(),WidgetInfo.MINIMAP_HEALTH_ORB.getChildId()),
    SPEC_ORB(WidgetInfo.MINIMAP_SPEC_ORB.getGroupId(),WidgetInfo.MINIMAP_SPEC_ORB.getChildId()),
    ;
    private final int groupId;
    private final int childId;

    private UsefulWidgets(int groupId, int childId) {
        this.groupId = groupId;
        this.childId = childId;
    }

    public int getId() {
        return this.groupId << 16 | this.childId;
    }

    public int getGroupId() {
        return this.groupId;
    }

    public int getChildId() {
        return this.childId;
    }

    public int getPackedId() {
        return this.groupId << 16 | this.childId;
    }

    public static int TO_GROUP(int id) {
        return id >>> 16;
    }

    public static int TO_CHILD(int id) {
        return id & '\uffff';
    }

    public static int PACK(int groupId, int childId) {
        return groupId << 16 | childId;
    }

    public WidgetInfo widgetInfo() {
        for (WidgetInfo value : WidgetInfo.values()) {
            if (this.getId() == value.getId() && this.getGroupId() == value.getGroupId()) return value;
        }
        return WidgetInfo.INVENTORY;
    }
}
