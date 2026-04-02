package ax.nd.faceunlock.camera.callables;

import android.hardware.Camera;
import ax.nd.faceunlock.camera.listeners.ByteBufferCallbackListener;
import ax.nd.faceunlock.camera.listeners.CameraListener;

public class SetPreviewCallbackCallable extends CameraCallable {
    private final ByteBufferCallbackListener mCallback;
    private final boolean mWithBuffer;

    public SetPreviewCallbackCallable(ByteBufferCallbackListener callback, boolean withBuffer, CameraListener cameraListener) {
        super(cameraListener);
        mCallback = callback;
        mWithBuffer = withBuffer;
    }

    @Override
    public void run() {
        try {
            Camera camera = getCameraData().mCamera;
            if (camera != null) {
                if (mWithBuffer) {
                    camera.setPreviewCallbackWithBuffer((data, cam) -> {
                        if (mCallback != null) mCallback.onEventCallback(0, data);
                    });
                } else {
                    camera.setPreviewCallback((data, cam) -> {
                        if (mCallback != null) mCallback.onEventCallback(0, data);
                    });
                }
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