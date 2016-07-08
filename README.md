CameraApplication
===================================================================
- 1、调用此功能时使用action进行显示启动
- 2、调用本功能的Activity需要要传入是否需要本功能提供保存图片(needSave:true保存，false不保存)，保存图片的路径(path)和图片的名字(picName) 如果needSave为true，将按照传入的图片名字和路径保存，没有传入path，默认保存在Environment.getExternalStorageDirectory()+"myCameraPic/IMG/"， 如果没有传入picName，默认名字为IMG_yyyyMMddHHmmssSSS.jpg,yyyyMMddHHmmssSSS为当前时间戳，返回的Intent里面会存入dataByte(照片的二进制流，从Bunble中获取)和uri(使用data.getData())获取,若needSave为false，本功能不为用户保存照片,只返回图片的二进制数据dataByte。
  


----------
**启动示例**
```
	Intent intent = new Intent();
	Bundle bundle = new Bundle();
	bundle.putBoolean("needSave", true);
	bundle.putString("picName", localTempImgFileName);
	bundle.putString("picPath", getTakePhotoPicPathDir());
	intent.putExtras(bundle);
	intent.setClass(getActivity(), com.ningfeifei.cameraapplication.TestCameraActivity.class);
	startActivityForResult(intent, INIT_MY_CAMERA);
	
	//接收返回拍照数据
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(resultCode == Activity.RESULT_OK){
			switch (requestCode) {
			case INIT_MY_CAMERA:
				//TODO coding here...
				break;
			default:
				break;
			}
		}

	}
```
	
- 3、CameraUtils类中提供了图片路径和Uri互转的方法
   CameraUtils.uri2filePath(Uri uri, Activity activity)
   CameraUtils.filePath2Uri(Activity context, String dir, String name)
   
###截图
--------------------------------------------------------------------------------------------
 ![takePic](/assets/takePic.png)
 ![catchPic](/assets/catchPic.png)
