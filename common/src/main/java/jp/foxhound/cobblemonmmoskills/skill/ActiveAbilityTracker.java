package jp.foxhound.cobblemonmmoskills.skill;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;
import net.minecraft.server.MinecraftServer;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

public final class ActiveAbilityTracker {
    private final Map<UUID, EnumMap<AbilityType, AbilityState>> statesByPlayer = new HashMap<>();

    public boolean activate(ServerPlayer player, AbilityType ability, int skillLevel) {
        long tick = currentTick(player.getServer());
        AbilityState state = stateFor(player.getUUID(), ability);

        if (state.lastActivationTick == tick) {
            return false;
        }

        if (state.activeUntilTick > tick) {
            player.sendSystemMessage(Component.literal(ability.displayName() + " is already active."));
            return false;
        }

        if (state.cooldownUntilTick > tick) {
            long remainingSeconds = (state.cooldownUntilTick - tick + 19) / 20;
            player.sendSystemMessage(Component.literal(
                ability.displayName() + " cooldown: " + remainingSeconds + "s"
            ));
            return false;
        }

        int durationSeconds = ability.durationSeconds(skillLevel);
        state.activeUntilTick = tick + durationSeconds * 20L;
        state.cooldownUntilTick = tick + ability.cooldownSeconds() * 20L;
        state.lastActivationTick = tick;

        player.sendSystemMessage(Component.literal(
            ability.displayName() + " activated for " + durationSeconds + "s"
        ));
        return true;
    }

    public boolean isActive(ServerPlayer player, AbilityType ability) {
        AbilityState state = stateFor(player.getUUID(), ability);
        return state.activeUntilTick > currentTick(player.getServer());
    }

    public long remainingActiveSeconds(ServerPlayer player, AbilityType ability) {
        AbilityState state = stateFor(player.getUUID(), ability);
        long remainingTicks = state.activeUntilTick - currentTick(player.getServer());
        return remainingTicks <= 0 ? 0L : (remainingTicks + 19L) / 20L;
    }

    public long remainingCooldownSeconds(ServerPlayer player, AbilityType ability) {
        AbilityState state = stateFor(player.getUUID(), ability);
        long remainingTicks = state.cooldownUntilTick - currentTick(player.getServer());
        return remainingTicks <= 0 ? 0L : (remainingTicks + 19L) / 20L;
    }

    public void tick(MinecraftServer server) {
        long tick = currentTick(server);
        Iterator<Map.Entry<UUID, EnumMap<AbilityType, AbilityState>>> iterator = statesByPlayer.entrySet().iterator();

        while (iterator.hasNext()) {
            Map.Entry<UUID, EnumMap<AbilityType, AbilityState>> entry = iterator.next();
            entry.getValue().values().removeIf(state -> state.activeUntilTick <= tick && state.cooldownUntilTick <= tick);

            if (entry.getValue().isEmpty()) {
                iterator.remove();
            }
        }
    }

    private AbilityState stateFor(UUID playerId, AbilityType ability) {
        return statesByPlayer
            .computeIfAbsent(playerId, ignored -> new EnumMap<>(AbilityType.class))
            .computeIfAbsent(ability, ignored -> new AbilityState());
    }

    private long currentTick(MinecraftServer server) {
        return server == null ? 0L : server.getTickCount();
    }

    private static final class AbilityState {
        private long activeUntilTick;
        private long cooldownUntilTick;
        private long lastActivationTick;
    }
}
