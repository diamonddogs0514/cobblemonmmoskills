# NeoForge porting notes

This note records the fixes needed when splitting the original Fabric-only
Cobblemon MMO Skills project into common, Fabric, and NeoForge modules.

## Current module shape

- `common` contains shared gameplay logic, commands, data, Cobblemon event hooks,
  mixins, language assets, and the Fabric-only `SkillGuiFactory` source needed by
  the Fabric build.
- `fabric` contains the Fabric entrypoint and Fabric event bus registration.
- `neoforge` contains the NeoForge entrypoint and NeoForge event bus
  registration.

The release jars can be collected with:

```powershell
.\gradlew.bat collectReleaseJars
```

The output is:

```text
build/release/cobblemonmmoskills-<minecraft_version>-fabric-<mod_version>.jar
build/release/cobblemonmmoskills-<minecraft_version>-neoforge-<mod_version>.jar
```

## Fabric behavior

The Fabric entrypoint still registers the same behavior as the original
single-loader mod:

- block break XP
- attack block/entity hooks
- use block/item hooks
- living damage and after-damage hooks
- killed entity hooks
- server tick hook
- command registration
- Cobblemon training, capture, and breeding event registration

The GUI command path changed internally from a direct `SkillGuiFactory` reference
to reflection. On Fabric, `SkillGuiFactory` and `sgui` are present, so the GUI
still opens as before. The fallback is only used when the GUI class or one of its
dependencies is unavailable.

## NeoForge GUI state

NeoForge does not use `eu.pb4:sgui`. Instead, it registers a platform GUI opener
from the NeoForge entrypoint and opens a vanilla `ChestMenu`-based fake inventory
screen.

The common command code calls `SkillGuiBridge`.

- Fabric registers `SkillGuiFactory`, preserving the original `sgui` behavior.
- NeoForge registers `NeoForgeSkillGuiFactory`, which mirrors the same 9x6 item
  layout, item names, lore, and click navigation using a custom `ChestMenu`.

Keep `sgui` out of the NeoForge runtime. The NeoForge jar excludes only
`SkillGuiFactory*.class` from the common transformed jar, while keeping
`SkillGuiBridge`.

## Problems encountered and fixes

### NeoForge runClient launched Fabric/Knot

Symptom:

```text
ClassNotFoundException: net.fabricmc.loader.launch.knot.KnotClient
```

Cause:

Architectury Loom defaults to Fabric unless the platform is set before applying
Loom.

Fix:

Set `loom.platform` for each subproject before applying `dev.architectury.loom`.

```groovy
subprojects {
    if (project.name == 'neoforge') {
        project.extensions.extraProperties.set('loom.platform', 'neoforge')
    } else {
        project.extensions.extraProperties.set('loom.platform', 'fabric')
    }

    apply plugin: 'dev.architectury.loom'
}
```

Then use the NeoForge dependency configuration:

```groovy
dependencies {
    neoForge "net.neoforged:neoforge:${rootProject.neoforge_version}"
}
```

If `launch.cfg` or `.main_class` is stale, regenerate:

```powershell
.\gradlew.bat :neoforge:configureClientLaunch --rerun-tasks --console=plain --no-daemon
```

`net.fabricmc.devlaunchinjector.Main` may still appear as the dev launcher. That
alone is not a Fabric launch. Check for `--launchTarget forgeclientdev` and
`fml.neoForgeVersion` in the log.

### Cobblemon missing KotlinForForge

Symptom:

```text
needs language provider kotlinforforge:5.3 or above to load
```

Cause:

Cobblemon NeoForge requires KotlinForForge, but its dependency metadata did not
bring it into the dev runtime automatically.

Fix:

Add the KotlinForForge Maven repository and dependency:

```groovy
repositories {
    maven { url = 'https://thedarkcolour.github.io/KotlinForForge/' }
}

dependencies {
    modImplementation("thedarkcolour:kotlinforforge-neoforge:5.3.0") {
        exclude group: "net.neoforged.fancymodloader", module: "loader"
    }
}
```

The loader exclusion avoids duplicate FML loader module issues in Architectury
Loom dev runs.

### Split package in NeoForge module layer

Symptom:

```text
java.lang.module.ResolutionException:
Modules cobblemonmmoskills and generated_x export package
jp.foxhound.cobblemonmmoskills to module cobblemon
```

Cause:

The NeoForge dev runtime sees the common jar and the NeoForge module as separate
modules. Both exported the same package `jp.foxhound.cobblemonmmoskills`.

Fix:

Move the NeoForge platform entrypoint out of the common root package:

```text
jp.foxhound.cobblemonmmoskills.neoforge.CobblemonmmoskillsNeoForge
```

Avoid placing platform-specific classes in packages that are also present in
`common` when running under NeoForge.

### `kotlin.Unit` ClassNotFound during Cobblemon mixin transform

Symptom:

```text
ClassNotFoundException: kotlin.Unit
MixinPreProcessorException ... SnapshotWarningMixin
```

Cause:

KotlinForForge was discovered as a mod, but Cobblemon's mixins referenced
Kotlin classes early enough that Kotlin stdlib was not visible to the transform
classpath.

Fix:

Add the Kotlin runtime jars to Loom's Forge/NeoForge runtime library
configuration:

```groovy
dependencies {
    forgeRuntimeLibrary "org.jetbrains.kotlin:kotlin-stdlib:2.0.0"
    forgeRuntimeLibrary "org.jetbrains.kotlin:kotlin-stdlib-jdk7:2.0.0"
    forgeRuntimeLibrary "org.jetbrains.kotlin:kotlin-stdlib-jdk8:2.0.0"
    forgeRuntimeLibrary "org.jetbrains.kotlin:kotlin-reflect:2.0.0"
}
```

This lets mixin transformation see `kotlin.Unit` before normal mod loading
finishes.

### Gradle memory/file watcher issues

Symptom:

```text
java.lang.OutOfMemoryError: Java heap space
Error while receiving file changes
Some project locks have not been unlocked.
```

Fix:

Use a larger Gradle heap and disable the VFS file watcher:

```properties
org.gradle.jvmargs=-Xmx3G
org.gradle.vfs.watch=false
```

If stale Gradle processes hold Loom cache locks, stop project-specific Gradle
or Java processes and rerun the task. In IntelliJ, a background Gradle import can
also hold the `fabric-loom` cache lock for several minutes.

## Verification commands

Use these after future NeoForge migration work:

```powershell
.\gradlew.bat :neoforge:clean :neoforge:runClient --console=plain --no-daemon
.\gradlew.bat build --console=plain --no-daemon
.\gradlew.bat collectReleaseJars --console=plain --no-daemon
```

Expected NeoForge log markers:

```text
ModLauncher running: args [--launchTarget, forgeclientdev, ...]
Kotlin For Forge Enabled!
Launching Cobblemon 1.7.3
Reloading ResourceManager: ... mod/cobblemon, mod/cobblemonmmoskills
```
