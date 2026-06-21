package jp.foxhound.cobblemonmmoskills.state;

import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Map;
import java.util.UUID;
import java.util.Set;
import jp.foxhound.cobblemonmmoskills.Cobblemonmmoskills;
import jp.foxhound.cobblemonmmoskills.skill.PlayerSkillProfile;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.storage.DimensionDataStorage;

public final class SkillPersistentState extends SavedData {
    private static final String STATE_KEY = Cobblemonmmoskills.MOD_ID + "_skills";
    private final Map<UUID, PlayerSkillProfile> profiles = new HashMap<>();

    public static final Factory<SkillPersistentState> TYPE = new Factory<>(
        SkillPersistentState::new,
        SkillPersistentState::fromNbt,
        null
    );

    public PlayerSkillProfile getOrCreate(UUID playerId) {
        return profiles.computeIfAbsent(playerId, ignored -> new PlayerSkillProfile());
    }

    public void put(UUID playerId, PlayerSkillProfile profile) {
        profiles.put(playerId, profile);
        setDirty();
    }

    public Set<Entry<UUID, PlayerSkillProfile>> entries() {
        return Set.copyOf(profiles.entrySet());
    }

    public boolean reset(UUID playerId) {
        boolean removed = profiles.remove(playerId) != null;
        if (removed) {
            setDirty();
        }
        return removed;
    }

    public int resetAll() {
        int count = profiles.size();
        if (count > 0) {
            profiles.clear();
            setDirty();
        }
        return count;
    }

    public static SkillPersistentState get(MinecraftServer server) {
        DimensionDataStorage dataStorage = server.overworld().getDataStorage();
        return dataStorage.computeIfAbsent(TYPE, STATE_KEY);
    }

    @Override
    public CompoundTag save(CompoundTag nbt, HolderLookup.Provider registries) {
        ListTag players = new ListTag();

        for (Map.Entry<UUID, PlayerSkillProfile> entry : profiles.entrySet()) {
            CompoundTag playerNbt = new CompoundTag();
            playerNbt.putUUID("uuid", entry.getKey());
            playerNbt.put("profile", entry.getValue().toNbt());
            players.add(playerNbt);
        }

        nbt.put("players", players);
        return nbt;
    }

    private static SkillPersistentState fromNbt(CompoundTag nbt, HolderLookup.Provider registries) {
        SkillPersistentState state = new SkillPersistentState();
        ListTag players = nbt.getList("players", Tag.TAG_COMPOUND);

        for (int i = 0; i < players.size(); i++) {
            CompoundTag playerNbt = players.getCompound(i);
            state.profiles.put(
                playerNbt.getUUID("uuid"),
                PlayerSkillProfile.fromNbt(playerNbt.getCompound("profile"))
            );
        }

        return state;
    }
}
