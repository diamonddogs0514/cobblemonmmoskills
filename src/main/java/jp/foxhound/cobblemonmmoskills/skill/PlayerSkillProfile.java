package jp.foxhound.cobblemonmmoskills.skill;

import java.util.EnumMap;
import net.minecraft.nbt.CompoundTag;

public final class PlayerSkillProfile {
    private final EnumMap<SkillType, SkillProgress> progressBySkill = new EnumMap<>(SkillType.class);

    public PlayerSkillProfile() {
        for (SkillType skillType : SkillType.values()) {
            progressBySkill.put(skillType, new SkillProgress());
        }
    }

    public SkillProgress getProgress(SkillType skillType) {
        return progressBySkill.get(skillType);
    }

    public CompoundTag toNbt() {
        CompoundTag root = new CompoundTag();

        for (SkillType skillType : SkillType.values()) {
            root.put(skillType.name(), progressBySkill.get(skillType).toNbt());
        }

        return root;
    }

    public static PlayerSkillProfile fromNbt(CompoundTag root) {
        PlayerSkillProfile profile = new PlayerSkillProfile();

        for (SkillType skillType : SkillType.values()) {
            if (root.contains(skillType.name())) {
                profile.progressBySkill.put(skillType, SkillProgress.fromNbt(root.getCompound(skillType.name())));
            }
        }

        return profile;
    }
}
