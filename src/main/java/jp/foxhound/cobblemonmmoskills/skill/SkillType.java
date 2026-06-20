package jp.foxhound.cobblemonmmoskills.skill;

public enum SkillType {
    MINING("Mining"),
    WOODCUTTING("Woodcutting"),
    EXCAVATION("Excavation"),
    ACROBATICS("Acrobatics"),
    FISHING("Fishing"),
    SWORDS("Swords"),
    AXES("Axes"),
    UNARMED("Unarmed"),
    ARCHERY("Archery"),
    HERBALISM("Herbalism"),
    ALCHEMY("Alchemy"),
    REPAIR("Repair"),
    SMELTING("Smelting"),
    TRAINING("Training"),
    CAPTURE("Capture"),
    BREEDING("Breeding");

    private final String displayName;

    SkillType(String displayName) {
        this.displayName = displayName;
    }

    public String displayName() {
        return displayName;
    }

    public String translationKey() {
        return "skill.cobblemonmmoskills." + name().toLowerCase();
    }
}
