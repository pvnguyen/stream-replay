/**
 * Created by phuong.nguyen
 */
import java.io.InputStream;
import java.io.IOException;

public class ReplayInputStream extends InputStream {
    private final InputStream rawStream;
    private long totalBytesRead;
    private long startTimeMillis;

    private static final int BYTES_PER_KILOBYTE = 1024;
    private static final int MILLIS_PER_SECOND = 1000;
    private final int ratePerMillis;

    public ReplayInputStream(InputStream rawStream, int kBytesPersecond) {
        this.rawStream = rawStream;
        ratePerMillis = kBytesPersecond * BYTES_PER_KILOBYTE / MILLIS_PER_SECOND;
    }

    @Override
    public int read() throws IOException {
        if (startTimeMillis == 0) {
            startTimeMillis = System.currentTimeMillis();
        }
        long now = System.currentTimeMillis();
        long interval = now - startTimeMillis;

        if (interval * ratePerMillis < totalBytesRead + 1) {
            try {
                final long sleepTime = ratePerMillis / (totalBytesRead + 1) - interval;
                Thread.sleep(Math.max(1, sleepTime));
            } catch (InterruptedException e) {
            }
        }
        totalBytesRead += 1;

        return rawStream.read();
    }
}
