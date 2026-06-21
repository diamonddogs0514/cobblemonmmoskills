package jp.foxhound.cobblemonmmoskills.gui;

import net.minecraft.server.level.ServerPlayer;

public final class SkillGuiBridge {
    @FunctionalInterface
    public interface MainOpener {
        void open(ServerPlayer player);
    }

    @FunctionalInterface
    public interface LeaderboardOpener {
        void open(ServerPlayer player, int tabIndex);
    }

    private static MainOpener mainOpener;
    private static LeaderboardOpener leaderboardOpener;

    private SkillGuiBridge() {
    }

    public static void register(MainOpener main, LeaderboardOpener leaderboard) {
        mainOpener = main;
        leaderboardOpener = leaderboard;
    }

    public static boolean openMain(ServerPlayer player) {
        if (mainOpener == null) {
            return false;
        }
        mainOpener.open(player);
        return true;
    }

    public static boolean openLeaderboard(ServerPlayer player, int tabIndex) {
        if (leaderboardOpener == null) {
            return false;
        }
        leaderboardOpener.open(player, tabIndex);
        return true;
    }
}
