import java.io.IOException;
import java.io.InputStream;

/**
 * A simple wrapper for InputStream allowing
 * read stream content (e.g., files) with a
 * given speed (i.e., bytes per second)
 *
 * Created by phuong.nguyen on 6/26/14.
 */
public class ReplayInputStream extends InputStream {
    // Raw input stream
    private final InputStream rawStream;
    // Limit speed in bytes per second
    private final long maxBytesPerSec;
    private final long startTime = System.currentTimeMillis();

    // Number of bytes have been read
    private long bytesRead = 0;
    // Sleep duration to slow down the reading if needed
    private static final long SLEEP_DURATION_MS = 50;

    public ReplayInputStream(InputStream rawStream, long maxBytesPerSec) {
        this.rawStream = rawStream;
        this.maxBytesPerSec = maxBytesPerSec;
    }

    /**
     * Close the raw stream after finishing
     *
     * @throws IOException
     */
    @Override
    public void close() throws IOException {
        rawStream.close();
    }

    /**
     * Overrided 1-byte reading method that includes delaying
     * for slowing down the speed if needed
     *
     * @return 1 if read successfully, -1 otherwise
     * @throws IOException
     */
    @Override
    public int read() throws IOException {
        delay();
        int data = rawStream.read();
        if (data != -1) {
            bytesRead++;
        }
        return data;
    }

    /**
     * Delay the reading process to satisfy speed
     * limit. Update sleeping time on-the-fly to adapt
     * with varying reading speed
     *
     * @throws IOException
     */
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

    /**
     * Get the current reading speed in bytes
     * per second
     *
     * @return current reading speed in bps
     */
    public long getBytesPerSec() {
        long elapsed = (System.currentTimeMillis() - startTime) / 1000;
        if (elapsed == 0) {
            return bytesRead;
        } else {
            return bytesRead / elapsed;
        }
    }
}