package jp.foxhound.cobblemonmmoskills.skill;

import net.minecraft.nbt.CompoundTag;

public final class SkillProgress {
    public static final int MAX_LEVEL = 1000;
    private int level;
    private double xpIntoLevel;

    public int level() {
        return level;
    }

    public double xpIntoLevel() {
        return xpIntoLevel;
    }

    public void setLevel(int level) {
        this.level = Math.min(MAX_LEVEL, Math.max(0, level));
        this.xpIntoLevel = 0.0;
    }

    public int addXp(double amount) {
        if (level >= MAX_LEVEL) {
            level = MAX_LEVEL;
            xpIntoLevel = 0.0;
            return 0;
        }

        xpIntoLevel += amount;
        int gainedLevels = 0;

        while (level < MAX_LEVEL && xpIntoLevel >= xpRequiredForNextLevel(level)) {
            xpIntoLevel -= xpRequiredForNextLevel(level);
            level++;
            gainedLevels++;
        }

        if (level >= MAX_LEVEL) {
            level = MAX_LEVEL;
            xpIntoLevel = 0.0;
        }

        return gainedLevels;
    }

    public static int xpRequiredForNextLevel(int currentLevel) {
        if (currentLevel >= MAX_LEVEL) {
            return 0;
        }
        return 1020 + currentLevel * 20;
    }

    public double totalXp() {
        return (10.0 * level * level) + (1010.0 * level) + xpIntoLevel;
    }

    public CompoundTag toNbt() {
        CompoundTag nbt = new CompoundTag();
        nbt.putInt("level", level);
        nbt.putDouble("xpIntoLevel", xpIntoLevel);
        return nbt;
    }

    public static SkillProgress fromNbt(CompoundTag nbt) {
        SkillProgress progress = new SkillProgress();
        progress.level = Math.min(MAX_LEVEL, Math.max(0, nbt.getInt("level")));
        progress.xpIntoLevel = progress.level >= MAX_LEVEL ? 0.0 : Math.max(0.0, nbt.getDouble("xpIntoLevel"));
        return progress;
    }
}
