/**
 * 
 */
package net.duohuo.dhroiddemos.other;

import java.io.File;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import net.duohuo.dhroid.activity.BaseActivity;
import net.duohuo.dhroid.ioc.annotation.InjectView;
import net.duohuo.dhroid.net.DhNet;
import net.duohuo.dhroid.net.NetTask;
import net.duohuo.dhroid.net.Response;
import net.duohuo.dhroid.util.FileUtil;
import net.duohuo.dhroid.util.ImageUtil;
import net.duohuo.dhroid.util.PhotoUtil;
import net.duohuo.dhroiddemos.R;

/**
 *	
 * @author duohuo-jinghao
 * @date 2014-3-18
 */
public class PicUploadActivity extends BaseActivity{
	@InjectView(id=R.id.upload,click="toUpload")
	View toUploadV;
	@InjectView(id=R.id.imageView1)
	ImageView iamgeV;
	
	public static final int code_photo=100;
	public static final int code_came=101;
	public static final int code_zoom=102;
	String path;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.other_photo);
	}
	
	/**
	 * 
	 */
	public void toUpload() {
		File file=new File(FileUtil.getCacheDir(),System.currentTimeMillis()+".jpg");
		path=file.getAbsolutePath();
		PhotoUtil.getPhoto(this,code_came, code_photo,file);
	}
	
	/* (non-Javadoc)
	 * @see android.app.Activity#onActivityResult(int, int, android.content.Intent)
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(resultCode==Activity.RESULT_OK){
			switch (requestCode) {
			case code_came:
				PhotoUtil.onPhotoFromCamera(this, code_zoom, path	, 100, 100, 1);
				break;
			case code_photo:
				PhotoUtil.onPhotoFromPick(this, code_zoom,path, data, 100, 100, 1);
				break;
			case code_zoom:
				File file=PhotoUtil.onPhotoZoom(path, 300, 300, 10);
				Bitmap bitmap=PhotoUtil.getLocalImage(file, 100, 100);
				bitmap=ImageUtil.toRoundCorner(bitmap, bitmap.getWidth()/2);
				iamgeV.setImageBitmap(bitmap);
				uploadFile(file);
			default:
				break;
			}
		}
	}
	
	public void uploadFile(File file){
//		DhNet net=new DhNet("http://www.duohuo.net");
//		net.addParam("key1", "参数1")
//		.addParam("key2", "参数1").upload("fileName", file, new NetTask(this) {
//			@Override
//			public void doInUI(Response response, Integer transfer) {
//				if (response.isSuccess()) {
//					Boolean uploading = response.getBundle("uploading");
//					if (!uploading) {
//						//上传完成
//					}else{
//						//已上传大小
//						long length= response.getBundle("length");
//						//文件总大小
//						long total=  response.getBundle("total");
//					}
//				}
//			}
//		});
	}
	
	
	
}
