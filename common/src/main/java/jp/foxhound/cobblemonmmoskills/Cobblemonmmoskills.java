package jp.foxhound.cobblemonmmoskills;

import jp.foxhound.cobblemonmmoskills.integration.CobblemonBreedingEvents;
import jp.foxhound.cobblemonmmoskills.integration.CobblemonCaptureEvents;
import jp.foxhound.cobblemonmmoskills.integration.CobblemonTrainingEvents;

public final class Cobblemonmmoskills {
    public static final String MOD_ID = "cobblemonmmoskills";

    private Cobblemonmmoskills() {
    }

    public static void initializeCommon() {
        CobblemonTrainingEvents.register();
        CobblemonCaptureEvents.register();
        CobblemonBreedingEvents.register();
    }
}
