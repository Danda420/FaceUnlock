package ax.nd.faceunlock.camera.callables;

import android.hardware.Camera;
import ax.nd.faceunlock.camera.listeners.CameraListener;
import ax.nd.faceunlock.camera.listeners.FocusResultListener;

public class AutoFocusCallable extends CameraCallable {
    private final boolean mAutoFocus;
    private final FocusResultListener mFocusListener;

    public AutoFocusCallable(boolean autoFocus, FocusResultListener focusResultListener, CameraListener cameraListener) {
        super(cameraListener);
        mAutoFocus = autoFocus;
        mFocusListener = focusResultListener;
    }

    @Override
    public void run() {
        try {
            Camera camera = getCameraData().mCamera;
            if (camera != null) {
                if (mAutoFocus) {
                    camera.autoFocus((success, camera1) -> {
                        if (mFocusListener != null) {
                            mFocusListener.onEventCallback(0, success);
                        }
                    });
                } else {
                    camera.cancelAutoFocus();
                }
                
                CameraCallable.runOnUiThread(() -> {
                    if (getCameraListener() != null) {
                        getCameraListener().onComplete(null);
                    }
                });
            }
        } catch (Exception e) {
            if (getCameraListener() != null) {
                CameraCallable.runOnUiThread(() -> {
                    if (getCameraListener() != null)
                        getCameraListener().onError(e);
                });
            }
        }
    }
}