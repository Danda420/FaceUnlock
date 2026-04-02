package ax.nd.faceunlock.camera.callables;

import android.hardware.Camera;
import ax.nd.faceunlock.camera.listeners.CameraListener;

public class SetDisplayOrientationCallback extends CameraCallable {
    private final int mOrientation;

    public SetDisplayOrientationCallback(int orientation, CameraListener cameraListener) {
        super(cameraListener);
        mOrientation = orientation;
    }

    @Override
    public void run() {
        try {
            Camera camera = getCameraData().mCamera;
            if (camera != null) {
                camera.setDisplayOrientation(mOrientation);
                if (getCameraListener() != null) {
                    getCameraListener().onComplete(null);
                }
            }
        } catch (Exception e) {
            if (getCameraListener() != null) {
                getCameraListener().onError(e);
            }
        }
    }
}