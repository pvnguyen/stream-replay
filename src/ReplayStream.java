import java.io.*;

/**
 * Created by phuong.nguyen on 6/26/14.
 */

public class ReplayStream {

    public static void main(String[] args) throws IOException {
        if (args.length != 2) {
            System.out.println("Wrong arguments.");
            System.out.println("java ReplayStream <input_file> <bytes_per_second>");
            System.exit(-1);
        }

        InputStream replayIS = new ReplayInputStream(
                new FileInputStream(args[0]),
                Integer.parseInt(args[1]));
        int c;
        int total = 0;
        long startTime = System.currentTimeMillis();
        while (replayIS.read() != -1) {
            total++;
        }
        long readingTime = (System.currentTimeMillis() - startTime) / 1000;
        System.out.println("Finish reading " + total + " bytes in " +
                readingTime + " seconds." );
        System.out.println("Replayed speed: " + args[1] + " bps");
        System.out.println("Real speed: " + (total / readingTime) + " bps");
    }
}
