package ax.nd.faceunlock.camera.callables;

import android.hardware.Camera;
import ax.nd.faceunlock.camera.listeners.CameraListener;

public class SetFaceDetectionCallback extends CameraCallable {
    private final Camera.FaceDetectionListener mListener;

    public SetFaceDetectionCallback(Camera.FaceDetectionListener listener, CameraListener cameraListener) {
        super(cameraListener);
        mListener = listener;
    }

    @Override
    public void run() {
        try {
            Camera camera = getCameraData().mCamera;
            if (camera != null) {
                camera.setFaceDetectionListener(mListener);
                if (mListener != null) {
                    camera.startFaceDetection();
                } else {
                    camera.stopFaceDetection();
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