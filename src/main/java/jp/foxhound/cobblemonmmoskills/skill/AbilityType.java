package jp.foxhound.cobblemonmmoskills.skill;

public enum AbilityType {
    SUPER_BREAKER(SkillType.MINING, "Super Breaker", 5, 240),
    TREE_FELLER(SkillType.WOODCUTTING, "Tree Feller", 5, 240),
    GIGA_DRILL_BREAKER(SkillType.EXCAVATION, "Giga Drill Breaker", 5, 240),
    SERRATED_STRIKES(SkillType.SWORDS, "Serrated Strikes", 5, 240),
    SKULL_SPLITTER(SkillType.AXES, "Skull Splitter", 5, 240),
    BERSERK(SkillType.UNARMED, "Berserk", 5, 240),
    GREEN_TERRA(SkillType.HERBALISM, "Green Terra", 5, 240);

    private final SkillType skillType;
    private final String displayName;
    private final int unlockLevel;
    private final int cooldownSeconds;

    AbilityType(SkillType skillType, String displayName, int unlockLevel, int cooldownSeconds) {
        this.skillType = skillType;
        this.displayName = displayName;
        this.unlockLevel = unlockLevel;
        this.cooldownSeconds = cooldownSeconds;
    }

    public SkillType skillType() {
        return skillType;
    }

    public String displayName() {
        return displayName;
    }

    public int unlockLevel() {
        return unlockLevel;
    }

    public int cooldownSeconds() {
        return cooldownSeconds;
    }

    public int durationSeconds(int skillLevel) {
        return 2 + (Math.min(50, skillLevel) / 5);
    }
}
