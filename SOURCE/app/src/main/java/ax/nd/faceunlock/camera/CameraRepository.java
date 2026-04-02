package ax.nd.faceunlock.camera;

import android.hardware.Camera;

public class CameraRepository {
    private static CameraRepository sInstance;
    private final CameraData mCameraData = new CameraData();

    public static synchronized CameraRepository getInstance() {
        if (sInstance == null) {
            sInstance = new CameraRepository();
        }
        return sInstance;
    }

    public CameraData getCameraData() {
        return mCameraData;
    }

    public static class CameraData {
        public Camera mCamera;
        public int mCameraId;
        public Camera.CameraInfo mCameraInfo;
        public Camera.Parameters mParameters;
    }
}