package com.ningfeifei.cameraapplication;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.Size;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.OrientationEventListener;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.Toast;

/** 
 * <p><b>Description:</b> 自定义摄像头拍照Activity </p>
 * <p><b>ClassName:</b> TestCameraActivity</p> 
 * @author NingFeifei
 * <p><b>date</b> 2016-7-6 下午12:50:17 </p> 
 */
@SuppressLint("NewApi") public class TestCameraActivity extends Activity implements SensorEventListener {
	/** 图片名字时间戳格式*/ 
	private static String SDF = "yyyyMMddHHmmssSSS";
	
	Camera camera;

	SurfaceView surfaceView;
	int camera_id = 0;
	IOrientationEventListener iOriListener;

	final int SUCCESS = 233;
	final int CANCEL_SAVE = 234;
	SnapHandler handler = new SnapHandler();

	int camera_direction = CameraInfo.CAMERA_FACING_BACK; // 摄像头方向
	private SensorManager sm; 
	private GSensitiveView back,cancel,save,switchCamera,gsView;
	byte[] tempdata;
	
	private boolean needSave;
	private String path,picName;
	private Intent intent;
	private Bundle bundle;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		// 显示界面
		setContentView(MResource.getIdByName(this, "layout", "activity_incell_camera"));
		intent = this.getIntent();
		bundle = intent.getExtras()==null?new Bundle():intent.getExtras();
		needSave = bundle.getBoolean("needSave", true);
//		needSave = bundle.getBoolean("needSave", false);
		path = bundle.getString("picPath", Environment.getExternalStorageDirectory()+"myCameraPic/IMG/");
		picName = bundle.getString("picName",getCurrentPicName());
		
		surfaceView = (SurfaceView) this.findViewById(MResource.getIdByName(this, "id", "surfaceView"));
		switchCamera = (GSensitiveView) findViewById(MResource.getIdByName(this, "id", "take_switch"));
		//设置图标默认为后置摄像头
		switchCamera.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				switchCamera();
			}

		});
		sm = (SensorManager) getSystemService(SENSOR_SERVICE);  
	    sm.registerListener(this, sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);  
	    
	    back = (GSensitiveView) findViewById(MResource.getIdByName(this, "id", "take_back"));
		back.setImage(MResource.getIdByName(this, "drawable", "take_back"));
		back.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		
		cancel = (GSensitiveView) findViewById(MResource.getIdByName(this, "id", "take_cancel"));
		cancel.setImage(MResource.getIdByName(this, "drawable", "take_cancel"));
		cancel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				handler.sendEmptyMessage(CANCEL_SAVE);
				gsView.setVisibility(View.VISIBLE);
				cancel.setVisibility(View.GONE);
				save.setVisibility(View.GONE);
			}
		});
		
		save = (GSensitiveView) findViewById(MResource.getIdByName(this, "id", "take_ok"));
		save.setImage(MResource.getIdByName(this, "drawable", "take_ok"));
		save.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(needSave){
					savePic();
				}else{
					handler.sendEmptyMessage(SUCCESS);
				}
			}
		});
	    
	    gsView = (GSensitiveView) findViewById(MResource.getIdByName(this, "id", "snap"));
	    gsView.setImage(MResource.getIdByName(this, "drawable", "take"));
		gsView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				camera.takePicture(null, null, new PictureCallback() {
					@Override
					public void onPictureTaken(byte[] data, Camera camera) {
						cancel.setVisibility(View.VISIBLE);
						save.setVisibility(View.VISIBLE);
						gsView.setVisibility(View.GONE);
						tempdata = data;
						bundle.putByteArray("dataByte", data);
					}

				});
			}

		});

		surfaceView.getHolder().setKeepScreenOn(true);// 屏幕常亮
		surfaceView.getHolder().addCallback(new Callback() {

			@Override
			public void surfaceCreated(SurfaceHolder holder) {
				// TODO Auto-generated method stub
				int mNumberOfCameras = Camera.getNumberOfCameras();

				// Find the ID of the default camera
				CameraInfo cameraInfo = new CameraInfo();
				for (int i = 0; i < mNumberOfCameras; i++) {
					Camera.getCameraInfo(i, cameraInfo);
					if (cameraInfo.facing == CameraInfo.CAMERA_FACING_BACK) {
						camera_id = i;
					}
				}
				camera = Camera.open(camera_id);
				try {
					camera.setPreviewDisplay(holder);
					camera.startPreview(); // 开始预览

					iOriListener.enable();

				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}

			@Override
			public void surfaceChanged(SurfaceHolder holder, int format,
					int width, int height) {
				// TODO Auto-generated method stub
				setCameraAndDisplay(width, height);

			}

			@Override
			public void surfaceDestroyed(SurfaceHolder holder) {
				// TODO Auto-generated method stub
				if (null != camera) {
					camera.release();
					camera = null;
				}

			}

		});// 为SurfaceView的句柄添加一个回调函数

		iOriListener = new IOrientationEventListener(this);
	}
	
	private void savePic() {
		Thread thread = new Thread(new Runnable() {
			@Override
			public void run() {
				File dir = new File(path);
				if (!dir.exists()) {
					dir.mkdirs();// 创建文件夹
				}
				File f = new File(path+picName);
				if (!f.exists()) {
					try {
						f.createNewFile();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				FileOutputStream outputStream;
				try {
					outputStream = new FileOutputStream(f);
					outputStream.write(tempdata); // 写入sd卡中
					outputStream.close(); // 关闭输出流
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} // 文件输出流
				catch (IOException e) {
					e.printStackTrace();
				}
				Log.v("TestCameraActivityTag", "store success");
				handler.sendEmptyMessage(SUCCESS);
			}

		});
		// 启动存储照片的线程
		thread.start();
	}
	
	public void onSensorChanged(SensorEvent event) {  
        if (Sensor.TYPE_ACCELEROMETER != event.sensor.getType()) {  
            return;  
        }  
     
        float[] values = event.values;  
        float ax = values[0];  
        float ay = values[1];  
     
        double g = Math.sqrt(ax * ax + ay * ay);  
        double cos = ay / g;  
        if (cos > 1) {  
            cos = 1;  
        } else if (cos < -1) {  
            cos = -1;  
        }  
        double rad = Math.acos(cos);  
        if (ax < 0) {  
            rad = 2 * Math.PI - rad;  
        }  
     
        int uiRot = getWindowManager().getDefaultDisplay().getRotation();  
        double uiRad = Math.PI / 2 * uiRot;  
        rad -= uiRad;  
     
        gsView.setRotation(rad);  
        back.setRotation(rad);  
        cancel.setRotation(rad);  
        save.setRotation(rad);  
        switchCamera.setRotation(rad);  
    }
	
	private String getCurrentPicName(){
		DateFormat dateTimeFormat = new SimpleDateFormat(SDF);
		String currDateTime = dateTimeFormat.format(new Date());
		return "IMG_" + currDateTime + ".jpg";
	}

	@Override
	protected void onDestroy() {
		sm.unregisterListener(this);
		super.onDestroy();
//		this.iOriListener.disable();
	}

	public class IOrientationEventListener extends OrientationEventListener {

		public IOrientationEventListener(Context context) {
			super(context);
			// TODO Auto-generated constructor stub
		}

		@Override
		public void onOrientationChanged(int orientation) {
			// TODO Auto-generated method stub
			if (ORIENTATION_UNKNOWN == orientation) {
				return;
			}
			CameraInfo info = new CameraInfo();
			Camera.getCameraInfo(camera_id, info);
			orientation = (orientation + 45) / 90 * 90;
			int rotation = 0;
			if (info.facing == CameraInfo.CAMERA_FACING_FRONT) {
				rotation = (info.orientation - orientation + 360) % 360;
			} else {
				rotation = (info.orientation + orientation) % 360;
			}
			if (null != camera) {
				Camera.Parameters parameters = camera.getParameters();
				parameters.setRotation(rotation);
				camera.setParameters(parameters);
			}

		}

	}
	
	public void switchCamera() {
		if (camera_direction == CameraInfo.CAMERA_FACING_BACK) {
			camera_direction = CameraInfo.CAMERA_FACING_FRONT;
			//改变图标
		} else {
			camera_direction = CameraInfo.CAMERA_FACING_BACK;
			//改变图标
		}
		int mNumberOfCameras = Camera.getNumberOfCameras();
		CameraInfo cameraInfo = new CameraInfo();
		for (int i = 0; i < mNumberOfCameras; i++) {
			Camera.getCameraInfo(i, cameraInfo);
			if (cameraInfo.facing == camera_direction) {
				camera_id = i;
			}
		}
		if (null != camera) {
			camera.stopPreview();
			camera.release();
		}
		camera = Camera.open(camera_id);
		try {
			camera.setPreviewDisplay(surfaceView.getHolder());
			camera.startPreview();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		setCameraAndDisplay(surfaceView.getWidth(), surfaceView.getHeight());
	}

	@SuppressLint("InlinedApi") 
	public void setCameraAndDisplay(int width, int height) {
		Camera.Parameters parameters = camera.getParameters();
		/* 获取摄像头支持的PictureSize列表 */
		List<Camera.Size> pictureSizeList = parameters
				.getSupportedPictureSizes();
		/* 从列表中选取合适的分辨率 */
		Size picSize = CameraUtils.getProperSize(pictureSizeList,
				((float) width) / height);
		if (null != picSize) {
			parameters.setPictureSize(picSize.width, picSize.height);
		} else {
			picSize = parameters.getPictureSize();
		}
		/* 获取摄像头支持的PreviewSize列表 */
		List<Camera.Size> previewSizeList = parameters
				.getSupportedPreviewSizes();
		Size preSize = CameraUtils.getProperSize(previewSizeList,
				((float) width) / height);
		if (null != preSize) {
			Log.v("TestCameraActivityTag", preSize.width + "," + preSize.height);
			parameters.setPreviewSize(preSize.width, preSize.height);
		}

		/* 根据选出的PictureSize重新设置SurfaceView大小 */
		float w = picSize.width;
		float h = picSize.height;
		surfaceView.setLayoutParams(new RelativeLayout.LayoutParams(
				(int) (height * (w / h)), height));

		parameters.setJpegQuality(100); // 设置照片质量

		// 先判断是否支持，否则会报错
		if (parameters.getSupportedFocusModes().contains(
				Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE)) {
			parameters
					.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
		}
		camera.cancelAutoFocus();// 只有加上了这一句，才会自动对焦。
		camera.setDisplayOrientation(0);
		camera.setParameters(parameters);
	}

	@SuppressLint("HandlerLeak") 
	class SnapHandler extends Handler {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case SUCCESS:
				Toast.makeText(TestCameraActivity.this, "照片存储至"+path+"文件夹",
						Toast.LENGTH_SHORT).show();
				if(needSave){
					intent.setData(CameraUtils.filePath2Uri(TestCameraActivity.this, path, picName));
				}
				TestCameraActivity.this.setResult(RESULT_OK, intent); 
				TestCameraActivity.this.finish();
				break;
			case CANCEL_SAVE:
				try {
					camera.setPreviewDisplay(surfaceView.getHolder());
					camera.startPreview();
				} catch (IOException e) {
					e.printStackTrace();
				}
				break;

			default:
				break;
			}
		}

	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
	}
}
