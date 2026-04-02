# AutoPatcher

The AutoPatcher is a utility designed to inject the necessary FaceUnlock service hooks into the Android framework by patching your system's `services.jar`.

## How It Works
The `patchFU` shell script decompiles the target `services.jar` using `APKEditor.jar`. It searches for `FaceService.smali` and `FaceProvider.smali` to automatically inject the `startService` execution method. It also attempts to optionally patch the `scheduleEnroll` method to properly register the preview surface. Finally, it merges the custom `faceunlock/classes.dex` into the jar and recompiles it.

## Guide To Use It

**Prerequisites:**
* Java installed (required for `APKEditor.jar`).
* A Linux/Bash environment.

**Usage:**
Run the `patchFU` script, providing the path to the `services.jar` you wish to modify as the first argument:
```bash
./patchFU <path_to_service.jar>
```

**Debugging:**
If the patch fails or you want to see detailed output from the decompilation and recompilation process, you can enable debug mode. Simply create an empty file named .debug in the same directory as the script before running it.
touch .debug
```bash
./patchFU <path_to_service.jar>
```