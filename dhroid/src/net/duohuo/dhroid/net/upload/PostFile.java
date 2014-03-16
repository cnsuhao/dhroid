package net.duohuo.dhroid.net.upload;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;
import java.util.Map.Entry;

import android.util.Log;


/**
 * POST上传文件
 * @author aokunsang
 * @Date 2011-12-6
 */
public class PostFile {

	private static PostFile postFile = new PostFile();
	
	private final static String LINEND = "\r\n";
	private final static String BOUNDARY = "---------------------------7da2137580612"; //数据分隔线
	private final static String PREFIX = "--";
	private final static String MUTIPART_FORMDATA = "multipart/form-data";
	private final static String CHARSET = "utf-8";
	private final static String CONTENTTYPE = "application/octet-stream";
	
	private PostFile(){}
	public static PostFile getInstance(){
		return postFile;
	}
	
	/**
	 * HTTP上传文件
	 * @param actionUrl  请求服务器的路径
	 * @param params     传递的表单内容
	 * @param files      多个文件信息
	 * @return
	 * @throws IOException 
	 */
	public String post(String actionUrl,Map<String,Object> params,FileInfo[] files) throws IOException{
	
			URL url = new URL(actionUrl);
			HttpURLConnection urlConn = (HttpURLConnection)url.openConnection();
			urlConn.setDoOutput(true);   //允许输出
			urlConn.setDoInput(true);   //允许输入
			urlConn.setUseCaches(false);
			urlConn.setRequestMethod("POST");
			urlConn.setRequestProperty("connection", "Keep-Alive");
			urlConn.setRequestProperty("Charset", CHARSET);
			urlConn.setRequestProperty("Content-Type", MUTIPART_FORMDATA+";boundary="+BOUNDARY);
			
			DataOutputStream dos = new DataOutputStream(urlConn.getOutputStream());
			//构建表单数据
			String entryText = bulidFormText(params);
			Log.i("-------描述信息---------------", entryText);
			dos.write(entryText.getBytes());
			
			StringBuffer sb = new StringBuffer("");
	
			for(FileInfo file : files){
				sb.append(PREFIX).append(BOUNDARY).append(LINEND);
				sb.append("Content-Disposition: form-data; name=\""+file.getFileTextName()+"\"; filename=\""+file.getFile().getAbsolutePath()+"\""+LINEND);
				sb.append("Content-Type:"+CONTENTTYPE+";charset="+CHARSET+LINEND);
				sb.append(LINEND);
				dos.write(sb.toString().getBytes());
				
				InputStream is = new FileInputStream(file.getFile());
				byte[] buffer = new byte[1024];
                int len = 0;
                while ((len = is.read(buffer)) != -1) {
                	dos.write(buffer, 0, len);
                }
                is.close();
                dos.write(LINEND.getBytes());
			}
			//请求的结束标志
			byte[] end_data = (PREFIX + BOUNDARY + PREFIX + LINEND).getBytes();
			dos.write(end_data);
			dos.flush();
	//-----------------------------------  发送请求数据结束  ----------------------------
			
    //----------------------------------   接收返回信息 ------------------------
			
			int code = urlConn.getResponseCode();
			if(code!=200){
				urlConn.disconnect();
				return "";
			}else{
				BufferedReader br = new BufferedReader(new InputStreamReader(urlConn.getInputStream()));
				String result = "";
				String line = null;
				while((line = br.readLine())!=null){
					result += line;
				}
				br.close();
				urlConn.disconnect();
				return result;
			}
		
	}
	
	/**
	 * HTTP上传单个文件
	 * @param actionUrl  请求服务器的路径
	 * @param params     传递的表单内容
	 * @param files      单个文件信息
	 * @return
	 * @throws IOException 
	 */
	public String post(String actionUrl,Map<String,Object> params,FileInfo fileInfo) throws IOException{
		return post(actionUrl, params, new FileInfo[]{fileInfo});
	}
	
	/**
	 * 封装表单文本数据
	 * @param paramText
	 * @return
	 */
	private String bulidFormText(Map<String,Object> paramText){
		if(paramText==null || paramText.isEmpty()) return "";
		StringBuffer sb = new StringBuffer("");
		for(Entry<String, Object> entry : paramText.entrySet()){ 
			sb.append(PREFIX).append(BOUNDARY).append(LINEND);
			sb.append("Content-Disposition:form-data;name=\""
                    + entry.getKey() + "\"" + LINEND);
//			sb.append("Content-Type:text/plain;charset=" + CHARSET + LINEND);
			sb.append(LINEND);
			sb.append(entry.getValue());
			sb.append(LINEND);
		}
		return sb.toString();
	}
	/**
	 * 封装文件文本数据
	 * @param files
	 * @return
	 */
	private String buildFromFile(FileInfo[] files){
		StringBuffer sb = new StringBuffer();
		for(FileInfo file : files){
			sb.append(PREFIX).append(BOUNDARY).append(LINEND);
			sb.append("Content-Disposition: form-data; name=\""+file.getFileTextName()+"\"; filename=\""+file.getFile().getAbsolutePath()+"\""+LINEND);
			sb.append("Content-Type:"+CONTENTTYPE+";charset="+CHARSET+LINEND);
			sb.append(LINEND);
		}
		return sb.toString();
	}

	
}
