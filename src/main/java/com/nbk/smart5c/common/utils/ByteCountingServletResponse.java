package com.nbk.smart5c.common.utils;

import javax.servlet.ServletOutputStream;
import javax.servlet.WriteListener;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 包装响应以统计实际写出的字节数。导出内容直接写入响应流，磁盘上没有文件可读取大小。
 */
public class ByteCountingServletResponse extends HttpServletResponseWrapper {

    private final AtomicLong byteCount = new AtomicLong();
    private ServletOutputStream countingStream;

    public ByteCountingServletResponse(HttpServletResponse response) {
        super(response);
    }

    public long getByteCount() {
        return byteCount.get();
    }

    /**
     * 已写出字节数的可读形式，如 1MB
     */
    public String getReadableSize() {
        return FileUtil.numToStringSpace(byteCount.get());
    }

    @Override
    public ServletOutputStream getOutputStream() throws IOException {
        if (countingStream == null) {
            ServletOutputStream delegate = super.getOutputStream();
            countingStream = new ServletOutputStream() {
                @Override
                public boolean isReady() {
                    return delegate.isReady();
                }

                @Override
                public void setWriteListener(WriteListener writeListener) {
                    delegate.setWriteListener(writeListener);
                }

                @Override
                public void write(int b) throws IOException {
                    delegate.write(b);
                    byteCount.incrementAndGet();
                }

                @Override
                public void write(byte[] b, int off, int len) throws IOException {
                    delegate.write(b, off, len);
                    byteCount.addAndGet(len);
                }

                @Override
                public void flush() throws IOException {
                    delegate.flush();
                }

                @Override
                public void close() throws IOException {
                    delegate.close();
                }
            };
        }
        return countingStream;
    }
}
