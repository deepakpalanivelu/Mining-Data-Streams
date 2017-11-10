import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.concurrent.Semaphore;
import java.net.InetAddress;
import java.util.regex.Pattern;

/**
 * Created by deepakrtp on 10/10/17.
 */
public class P2 {
    private BufferedReader reader;
    private int windowSize;
    private QueryProcessor queryProcessor;
    private StreamReader streamReader;
    private Semaphore lock;
    private static final Pattern PATTERN = Pattern.compile(
            "^(([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.){3}([01]?\\d\\d?|2[0-4]\\d|25[0-5])$");


    P2() {
        lock = new Semaphore(1);
    }

    private void process() {
        try {

            reader = new BufferedReader(new InputStreamReader(System.in));
            String input = reader.readLine().trim();
            while (input.startsWith("#")) {
                input = reader.readLine().trim();
            }
            windowSize = Integer.parseInt(input);
            if (windowSize <= 0) {
                System.out.println("Window size is invalid. Exiting.");
                System.exit(0);
            }
            else {
                System.out.println("Window size =" +windowSize);
            }
            input = reader.readLine().trim();
            while (input.startsWith("#")) {
                input = reader.readLine().trim();
            }
            String[] parts = input.split(":");
            if  ( (Integer.parseInt(parts[1]) <1024) || (Integer.parseInt(parts[1]) > 65535) ) {
                System.out.println("Please give a valid port in the range 1025 - 65535. Exiting now");
                System.exit(0);
            }
            connectionThread(parts[0].trim(), Integer.parseInt(parts[1].trim()));
            queryThread();

        } catch (Exception e) {
            System.out.println("  ");
            System.exit(0);
        }
    }

    private void connectionThread(String ip, int port) {
        streamReader = new StreamReader(ip, port, windowSize, lock);
        Thread t1 = new Thread(streamReader);
        t1.start();
    }

    private void queryThread() {
        queryProcessor = new QueryProcessor(windowSize, streamReader, lock);
        Thread queryThread = new Thread(queryProcessor);
        queryThread.start();
    }

    public static void main(String args[]) {
        P2 p = new P2();
        p.process();
    }
}
