/**
 * Created by Ludo van den Boom <ludo@cubicphuse.nl> on 06/04/2017.
 */

        package com.cubicphuse.RCTTorch;

        import android.content.Context;
        import android.hardware.Camera;
        import android.hardware.camera2.CameraAccessException;
        import android.hardware.camera2.CameraManager;
        import android.os.Build;
        import android.os.Handler;
        import android.os.Looper;

        import com.facebook.react.bridge.Callback;
        import com.facebook.react.bridge.ReactApplicationContext;
        import com.facebook.react.bridge.ReactContextBaseJavaModule;
        import com.facebook.react.bridge.ReactMethod;

public class RCTTorchModule extends ReactContextBaseJavaModule {
    private final ReactApplicationContext myReactContext;
    private Boolean isTorchOn = false;
    private Camera camera;
    private CameraManager cameraManager;

    public RCTTorchModule(ReactApplicationContext reactContext) {
        super(reactContext);

        // Need access to reactContext to check for camera
        this.myReactContext = reactContext;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            cameraManager =
                    (CameraManager) this.myReactContext.getSystemService(Context.CAMERA_SERVICE);
        }
    }

    CameraManager.TorchCallback torchCallback = new CameraManager.TorchCallback() {
        @Override
        public void onTorchModeUnavailable(String cameraId) {
            super.onTorchModeUnavailable(cameraId);
        }

        @Override
        public void onTorchModeChanged(String cameraId, boolean enabled) {
            super.onTorchModeChanged(cameraId, enabled);
            isTorchOn = enabled;
        }
    };

    @Override
    public String getName() {
        return "RCTTorch";
    }

    @ReactMethod
    public void getTorchStatus(Callback successCallback, Callback failureCallback) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            cameraManager.registerTorchCallback(torchCallback, null);
            try {
                successCallback.invoke(isTorchOn);
            } catch (Exception e) {
                String errorMessage = e.getMessage();
                failureCallback.invoke("Error: " + errorMessage);
            }
        }
    }

    @ReactMethod
    public void switchState(Boolean newState, Callback successCallback, Callback failureCallback) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            try {
                String cameraId = cameraManager.getCameraIdList()[0];
                cameraManager.setTorchMode(cameraId, newState);
                successCallback.invoke(true);
            } catch (Exception e) {
                String errorMessage = e.getMessage();
                failureCallback.invoke("Error: " + errorMessage);
            }
        } else {
            Camera.Parameters params;

            if (newState && !isTorchOn) {
                camera = Camera.open();
                params = camera.getParameters();
                params.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
                camera.setParameters(params);
                camera.startPreview();
                isTorchOn = true;
            } else if (isTorchOn) {
                params = camera.getParameters();
                params.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);

                camera.setParameters(params);
                camera.stopPreview();
                camera.release();
                isTorchOn = false;
            }
        }
    }
}
