package net.duohuo.dhroid.net.upload;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.apache.http.entity.mime.MultipartEntity;

public class ProgressMultipartEntity extends MultipartEntity {

	private ProgressListener listener;

	public ProgressMultipartEntity() {
		super();
	}

	public void setProgressListener(ProgressListener listener) {
		this.listener = listener;
	}

	@Override
	public void writeTo(final OutputStream outstream) throws IOException {
		super.writeTo(new CountingOutputStream(outstream, this.listener));
	}

	public static interface ProgressListener {
		void transferred(long num);
		boolean isCanceled();
	}

	public static class CountingOutputStream extends FilterOutputStream {

		private final ProgressListener listener;
		private long transferred;

		public CountingOutputStream(final OutputStream out,
				final ProgressListener listener) {
			super(out);
			this.listener = listener;
			this.transferred = 0;
		}

		public void write(byte[] b, int off, int len) throws IOException {
			if(!listener.isCanceled()){
				out.write(b, off, len);
				this.transferred += len;
				this.listener.transferred(this.transferred);
			}else{
				throw new CancelException("任务已被取消");
			}
		}

		public void write(int b) throws IOException {
			out.write(b);
			this.transferred++;
			this.listener.transferred(this.transferred);
		}
	}
	
	
}