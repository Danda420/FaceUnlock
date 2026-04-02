package ax.nd.faceunlock.camera.callables;

import android.hardware.Camera;
import ax.nd.faceunlock.camera.listeners.CameraListener;

public class AddCallbackBufferCallable extends CameraCallable {
    private final byte[] mBuffer;

    public AddCallbackBufferCallable(byte[] buffer, CameraListener cameraListener) {
        super(cameraListener);
        mBuffer = buffer;
    }

    @Override
    public void run() {
        try {
            Camera camera = getCameraData().mCamera;
            if (camera != null) {
                camera.addCallbackBuffer(mBuffer);
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