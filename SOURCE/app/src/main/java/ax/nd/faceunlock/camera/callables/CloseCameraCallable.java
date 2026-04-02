package ax.nd.faceunlock.camera.callables;

import ax.nd.faceunlock.camera.CameraRepository;
import ax.nd.faceunlock.camera.listeners.CameraListener;
import android.hardware.Camera;

public class CloseCameraCallable extends CameraCallable {

    public CloseCameraCallable(CameraListener cameraListener) {
        super(cameraListener);
    }

    @Override
    public void run() {
        CameraRepository.CameraData cameraData = getCameraData();
        if (cameraData.mCamera != null) {
            try {
                cameraData.mCamera.setPreviewCallback(null);
                cameraData.mCamera.stopPreview();
                cameraData.mCamera.release();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                cameraData.mCamera = null;
            }
        }
        
        if (getCameraListener() != null) {
            CameraCallable.runOnUiThread(() -> getCameraListener().onComplete(null));
        }
    }
}