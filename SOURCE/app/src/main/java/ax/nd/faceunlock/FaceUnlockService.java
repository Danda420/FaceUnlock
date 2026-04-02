package ax.nd.faceunlock;

import static ax.nd.faceunlock.util.Util.getSystemContext;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.SurfaceTexture;
import android.util.Log;
import android.view.Surface;

import java.util.concurrent.CountDownLatch;

import ax.nd.faceunlock.camera.CameraFaceAuthController;
import ax.nd.faceunlock.camera.CameraFaceEnrollController;
import ax.nd.faceunlock.util.Util;
import ax.nd.faceunlock.vendor.FacePPImpl;

public class FaceUnlockService {
    private static String TAG = "FaceUnlockService";
    public static final Boolean DEBUG = Util.getBooleanSystemProperty("persist.sys.facehal.verbose", false);
    private static FacePPImpl sFacePP;
    private static Surface sEnrollSurface = null;

    public static void setEnrollSurface(Surface surface) {
        if (DEBUG) Log.i(TAG, "Successfully intercepted Enrollment Surface from FaceProvider!");
        sEnrollSurface = surface;
    }

    @SuppressLint("NewApi")
    public static void startService() {
        final Context context = getSystemContext();
        new Thread(() -> {
            try {
                sFacePP = new FacePPImpl(context);
                sFacePP.init();

                SurfaceTexture dummyTexture = new SurfaceTexture(10);
                Surface dummySurface = new Surface(dummyTexture);

                Util.setSystemProperty("debug.face.command", "0");
                Util.setSystemProperty("debug.face.result", "0");

                if (DEBUG) Log.i(TAG, "Property Polling Bridge started successfully!");

                while (true) {
                    int command = Util.getIntSystemProperty("debug.face.command", 0);

                    switch (command) {
                        case 0:
                            break;
                        case 1: {
                            if (DEBUG) Log.i(TAG, "Received Property Command: " + command);
                            Util.setSystemProperty("debug.face.command", "0");

                            final CountDownLatch latch = new CountDownLatch(1);
                            final int[] result = { -1 };

                            Surface targetSurface = (sEnrollSurface != null && sEnrollSurface.isValid()) ? sEnrollSurface : dummySurface;

                            sFacePP.saveFeatureStart();
                            CameraFaceEnrollController.getInstance(context).start(new CameraFaceEnrollController.CameraCallback() {
                                byte[] mFeature = new byte[10000];
                                byte[] mFaceData = new byte[40000];
                                int[] mOutId = new int[1];

                                @Override
                                public int handleSaveFeature(byte[] data, int width, int height, int angle) {
                                    return sFacePP.saveFeature(data, width, height, angle, true, mFeature, mFaceData, mOutId);
                                }

                                @Override
                                public void handleSaveFeatureResult(int res) {
                                    if (res == 0) {
                                        result[0] = 1;
                                        latch.countDown();
                                    }
                                }

                                @Override public void onFaceDetected() {}
                                @Override public void onTimeout() { latch.countDown(); }
                                @Override public void onCameraError() { latch.countDown(); }
                                @Override public void setDetectArea(android.hardware.Camera.Size size) {
                                    sFacePP.setDetectArea(0, 0, size.height, size.width);
                                }
                            }, 1, targetSurface);

                            int status = waitForFace(latch, 15000);

                            CameraFaceEnrollController.getInstance(context).stop(null);
                            sFacePP.saveFeatureStop();
                            sEnrollSurface = null;

                            if (status == 1 && result[0] == 1) {
                                Util.setSystemProperty("debug.face.result", "1");
                            }
                            break;
                        }
                        case 2: {
                            if (DEBUG) Log.i(TAG, "Received Property Command: " + command);
                            Util.setSystemProperty("debug.face.command", "0");

                            final CountDownLatch latch = new CountDownLatch(1);
                            final int[] result = { -1 };

                            sFacePP.compareStart();
                            CameraFaceAuthController authController = new CameraFaceAuthController(context, new CameraFaceAuthController.ServiceCallback() {
                                @Override
                                public int handlePreviewData(byte[] data, int width, int height) {
                                    int[] scores = new int[20];
                                    int res = sFacePP.compare(data, width, height, 0, true, true, scores);
                                    if (res == 0) {
                                        result[0] = 1;
                                        latch.countDown();
                                    }
                                    return res;
                                }

                                @Override public void setDetectArea(android.hardware.Camera.Size size) {
                                    sFacePP.setDetectArea(0, 0, size.height, size.width);
                                }
                                @Override public void onTimeout(boolean b) { latch.countDown(); }
                                @Override public void onCameraError() { latch.countDown(); }
                            });

                            authController.start(1, dummyTexture);

                            int status = waitForFace(latch, 4000);

                            authController.stop();
                            sFacePP.compareStop();

                            if (status == 1 && result[0] == 1) {
                                Util.setSystemProperty("debug.face.result", "1");
                            }
                            break;
                        }
                        case 3:
                            if (DEBUG) Log.i(TAG, "Received Property Command: " + command);
                            Util.setSystemProperty("debug.face.command", "0");

                            sFacePP.deleteFeature(1);
                            Util.setSystemProperty("debug.face.result", "1");
                            break;
                        default:
                            Log.w(TAG, "Unknown property command received: " + command);
                            Util.setSystemProperty("debug.face.command", "0");
                            break;
                    }
                    Thread.sleep(100);
                }
            } catch (Exception e) {
                Log.e(TAG, "Property bridge error", e);
            }
        }).start();
    }

    private static int waitForFace(CountDownLatch latch, int maxTimeMs) {
        int timeMs = 0;
        while (timeMs < maxTimeMs) {
            if (latch.getCount() == 0) return 1;

            if (Util.getIntSystemProperty("debug.face.command", 0) == 4) {
                if (DEBUG) Log.i(TAG, "Received CANCEL command! Force-aborting camera...");
                Util.setSystemProperty("debug.face.command", "0");
                sEnrollSurface = null;
                return -1;
            }

            try { Thread.sleep(100); } catch (Exception e) {}
            timeMs += 100;
        }
        return 0;
    }
}