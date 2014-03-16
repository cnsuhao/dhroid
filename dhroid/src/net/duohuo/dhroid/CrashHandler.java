package net.duohuo.dhroid;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.Thread.UncaughtExceptionHandler;
import java.util.Date;

import net.duohuo.dhroid.activity.ActivityTack;
import net.duohuo.dhroid.dialog.DialogCallBack;
import net.duohuo.dhroid.dialog.IDialog;
import net.duohuo.dhroid.ioc.IocContainer;
import net.duohuo.dhroid.net.DhNet;
import net.duohuo.dhroid.net.NetTask;
import net.duohuo.dhroid.net.Response;
import net.duohuo.dhroid.thread.Task;
import net.duohuo.dhroid.thread.ThreadWorker;
import net.duohuo.dhroid.util.DhUtil;
import net.duohuo.dhroid.util.FileUtil;

import android.app.Activity;
import android.content.Context;
import android.os.Debug;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

/**
 * 
 * @author 
 * 
 */
public class CrashHandler implements UncaughtExceptionHandler {

	public static final String TAG = "CrashHandler";
	private static final String OOM = "java.lang.OutOfMemoryError";
	private static final String HPROF_FILE_PATH = Environment
			.getExternalStorageDirectory().getPath() + "/data.hprof";

	private static CrashHandler sCrashHandler;

	private CrashHandler() {
	}

	public synchronized static CrashHandler getInstance() {
		if (sCrashHandler == null) {
			sCrashHandler = new CrashHandler();
		}
		return sCrashHandler;
	}

	public CrashHandler init() {
		Thread.setDefaultUncaughtExceptionHandler(this);
		return this;
	}

	public void uploadLast(Context context,String url, String name) {
		File cachefile=FileUtil.getCacheDir();
		final File file=new File(cachefile,"error.log");
		if (file.exists()) {
			new DhNet(url).upload(name, file, new NetTask(context) {
				@Override
				public void doInUI(Response response, Integer transfer) {
					if (transfer == NetTask.TRANSFER_DOUI) {
						file.delete();
					}
				}
			});
		}
	}

	public static boolean isOOM(Throwable throwable) {
		Log.d(TAG, "getName:" + throwable.getClass().getName());
		if (OOM.equals(throwable.getClass().getName())) {
			return true;
		} else {
			Throwable cause = throwable.getCause();
			if (cause != null) {
				return isOOM(cause);
			}
			return false;
		}
	}

	public void uncaughtException(Thread thread, Throwable throwable) {
		if (isOOM(throwable)) {
			try {
				Debug.dumpHprofData(HPROF_FILE_PATH);
			} catch (Exception e) {
				Log.e(TAG, "couldn’t dump hprof", e);
			}
		}
			
			File logdir=FileUtil.getCacheDir();
			try {
				FileWriter fw = new FileWriter(new File(logdir,"error.log"), true);
				fw.write(new Date() + "\n");
				StackTraceElement[] stackTrace = throwable.getStackTrace();
				fw.write(throwable.getMessage() + "\n");
				for (int i = 0; i < stackTrace.length; i++) {
					fw.write("file:" + stackTrace[i].getFileName() + " class:"
							+ stackTrace[i].getClassName() + " method:"
							+ stackTrace[i].getMethodName() + " line:"
							+ stackTrace[i].getLineNumber() + "\n");
				}
				fw.write("\n");
				fw.flush();
				fw.close();
			} catch (IOException e) {
				Log.e("crash handler", "load file failed...", e.getCause());
			}
			ActivityTack.getInstanse().exit(IocContainer.getShare().getApplicationContext());
			android.os.Process.killProcess(android.os.Process.myPid());
			System.exit(1);
	}
}
