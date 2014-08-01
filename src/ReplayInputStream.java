import java.io.IOException;
import java.io.InputStream;

/**
 * Created by phuong.nguyen on 6/26/14.
 */

public class ReplayInputStream extends InputStream {
    private final InputStream rawStream;
    private final long maxBytesPerSec;
    private final long startTime = System.currentTimeMillis();

    private long bytesRead = 0;
    private static final long SLEEP_DURATION_MS = 50;

    public ReplayInputStream(InputStream rawStream, long maxBytesPerSec) {
        this.rawStream = rawStream;
        this.maxBytesPerSec = maxBytesPerSec;
    }

    @Override
    public void close() throws IOException {
        rawStream.close();
    }

    @Override
    public int read() throws IOException {
        delay();
        int data = rawStream.read();
        if (data != -1) {
            bytesRead++;
        }
        return data;
    }

    private void delay() throws IOException {
        long curSpeed = getBytesPerSec();
        if (curSpeed > maxBytesPerSec) {
            try {
                long sleepTime = (curSpeed - maxBytesPerSec) * 1000;
                Thread.sleep(Math.max(sleepTime, SLEEP_DURATION_MS));
            } catch (InterruptedException e) {
            }
        }
    }

    public long getBytesPerSec() {
        long elapsed = (System.currentTimeMillis() - startTime) / 1000;
        if (elapsed == 0) {
            return bytesRead;
        } else {
            return bytesRead / elapsed;
        }
    }
}