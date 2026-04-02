package ax.nd.faceunlock.camera.callables;

import android.hardware.Camera;
import ax.nd.faceunlock.camera.listeners.CameraListener;
import ax.nd.faceunlock.camera.listeners.ReadParametersListener;

public class ReadParamsCallable extends CameraCallable {
    private final ReadParametersListener mReadListener;

    public ReadParamsCallable(ReadParametersListener listener, CameraListener cameraListener) {
        super(cameraListener);
        mReadListener = listener;
    }

    @Override
    public void run() {
        try {
            Camera camera = getCameraData().mCamera;
            if (camera != null) {
                getCameraData().mParameters = camera.getParameters();
                if (mReadListener != null) {
                    mReadListener.onEventCallback(0, getCameraData().mParameters);
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