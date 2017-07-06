package com.aircast.photobag.utils;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;

import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;

public class PBCustomMultiPartEntity extends MultipartEntity {

    private final ProgressListener listener;
    private CountingOutputStream mCountingOutputStream;

    public PBCustomMultiPartEntity(final ProgressListener listener) {
        super();
        this.listener = listener;
    }

    public PBCustomMultiPartEntity(final HttpMultipartMode mode,
            final ProgressListener listener) {
        super(mode);
        this.listener = listener;
    }

    public PBCustomMultiPartEntity(HttpMultipartMode mode,
            final String boundary, final Charset charset,
            final ProgressListener listener) {
        super(mode, boundary, charset);
        this.listener = listener;
    }

    @Override
    public void writeTo(final OutputStream outstream) throws IOException {
        if (this.mCountingOutputStream == null) {
            this.mCountingOutputStream = new CountingOutputStream(outstream, listener);
        }
        super.writeTo(this.mCountingOutputStream);
    }

    public /*static*/ interface ProgressListener {
        void transferred(long num);
        boolean cancelUpload();
    }
    
    private static class CountingOutputStream extends FilterOutputStream {
        private long bytesBransferred;
        private final ProgressListener mProgresslistener;
        private int mCount = 0;

        public CountingOutputStream(OutputStream paramOutputStream,
                ProgressListener progressListener) {
            super(paramOutputStream);
            this.mProgresslistener = progressListener;
            this.bytesBransferred = 0L;
            this.mCount = 0;
        }

        public void write(byte[] paramArrayOfByte, int off,
                int len) throws IOException {
            if ((off | len | (paramArrayOfByte.length - (len + off)) | (off + len)) < 0) {
                throw new IndexOutOfBoundsException();
            }

            this.out.write(paramArrayOfByte, off, len);
            this.bytesBransferred = this.bytesBransferred + len;
            if (this.mProgresslistener != null) {
                if (this.mProgresslistener.cancelUpload()) {
                    return;
                }
                mCount = mCount + 1;
                if (mCount == 5) {
                    this.mProgresslistener.transferred(this.bytesBransferred);
                    mCount = 0;
                }
            }
            if (!Thread.currentThread().isInterrupted())
                return;
            throw new IOException("upload_cancelled");
        }
    }
    
}