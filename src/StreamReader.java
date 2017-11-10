import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.Socket;
import java.util.LinkedList;
import java.util.concurrent.Semaphore;
import java.util.regex.Pattern;

/**
 * Created by deepakrtp on 10/10/17.
 */
public class StreamReader implements Runnable {
    private static int R_VALUE = 4;
    private Socket socket;
    private int port;
    private String ip;
    private int windowSize;
    private Semaphore lock;
    private int timeStamp = 1;
    private LinkedList<Bucket> buckets;
    private LinkedList<Integer> bitsInWindow;
    private static final Pattern PATTERN = Pattern.compile(
            "^(([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.){3}([01]?\\d\\d?|2[0-4]\\d|25[0-5])$");

    StreamReader(String ip, int port, int windowSize, Semaphore lock) {
        this.ip = ip;
        this.port = port;
        this.windowSize = windowSize;
        this.lock = lock;
        bitsInWindow = new LinkedList<Integer>();
        buckets = new LinkedList<Bucket>();
    }

    private String hosttoIP(String ip) {
        boolean check;
        try{
            /*boolean check1 = validate(ip); */
            if (ip.matches(".*[a-z].*")) {
                InetAddress IP_Address = InetAddress.getByName(ip);
                ip = IP_Address.toString();
                ip = ip.split("/")[1];
                /* System.out.println(ip); */
            }
            check = validate(ip);
            if (check == false) {
                System.out.println("Wrong host name. Please enter a valid one");
                System.exit(0);
            }
        } catch(Exception e) {
            System.out.println("Please enter a valid host name");
            System.exit(0);
        }
        return ip;
    }

    private  boolean validate(final String ip) {
        return PATTERN.matcher(ip).matches();
    }

    @Override
    public void run() {
        try {
            socket = new Socket(hosttoIP(ip), port);
            BufferedReader reader = new BufferedReader(new
                InputStreamReader(socket.getInputStream()));
            String inputLine;
            while ((inputLine = reader.readLine()) != null) {
                lock.acquire();
                System.out.print(inputLine + " ");
                /* if input is one */
                if(Integer.parseInt(inputLine) == 1) {
                    Bucket bucket = new Bucket(timeStamp);
                    maintainBuckets(bucket);
                }
                timeStamp++;

                if( bitsInWindow.size() + 1 > windowSize ) {
                    bitsInWindow.poll();
                }
                bitsInWindow.add(Integer.parseInt(inputLine));
                lock.release();
            }
        }
        catch(Exception e) {
            System.out.println("Error in connecting to the server. Server might not be running");
            System.exit(0);
        }
    }

    /*
        1.Insert bucket to the list of buckets
        2.Check if there are four buckets of size 1, if yes merge the last two.
        3.If there is a merge check the buckets if the next size and see if there are buckets of four.
          If yes repeat the same and it goes on till there are bucket counts of same size less than 4.
     */
    private void maintainBuckets(Bucket bucket) {
        buckets.addFirst(bucket);
        boolean flag = true;
        int tempSize = 1;
        int bucketCount = 0;
        int tempbucketId = 0;
        while (flag) {
            for(int bucketId = tempbucketId; bucketId < buckets.size();bucketId++) {
                if (buckets.get(bucketId).getSize() == tempSize ) {
                    bucketCount++;
                    if(bucketCount == R_VALUE) {
                        mergeBuckets(bucketId, 2 * tempSize);
                        tempSize = 2 * tempSize;
                        bucketCount = 0;
                        tempbucketId = bucketId - 1;
                        if(bucketId == buckets.size() - 1) {
                            flag = false;
                        }
                        break;
                    }
                    if(bucketId == buckets.size() - 1) {
                        flag = false;
                    }
                }
                else {
                    flag = false;
                    break;
                }

            }
        }
        /*print()*/;
    }
    public void print() {
        System.out.println("Updated bucket starts here");
        for(int bucketId = 0; bucketId < buckets.size();bucketId++) {
            System.out.println("end Time stamp = " +buckets.get(bucketId).getEndTimeStamp() + "bucket size = "+buckets.get(bucketId).getSize() );
        }
        System.out.println("Updated bucket ends here");
    }


    private void mergeBuckets(int bucketId, int bucketNewSize) {
        buckets.get(bucketId - 1).setSize( bucketNewSize );
        buckets.remove(bucketId);
    }

    public int getExactCount(int size) {
        int oneCount = 0;
        for (int i = Math.max(bitsInWindow.size() - size,0) ; i < bitsInWindow.size(); i++) {
            if(bitsInWindow.get(i) == 1) {
                oneCount++;
            }
        }
        return oneCount;
    }

    public int getEstimateCount(int size) {
        int oneCount = 0;
        int fromTimeStamp = (timeStamp - 1) - size;
        for(int bucketId = 0; bucketId < buckets.size();bucketId++) {
            if(buckets.get(bucketId).getEndTimeStamp() > fromTimeStamp) {
                oneCount += buckets.get(bucketId).getSize();
            }
            else if(buckets.get(bucketId).getEndTimeStamp() == fromTimeStamp){
                oneCount++;
                break;
            }
            else if(buckets.get(bucketId).getEndTimeStamp() < fromTimeStamp) {
                oneCount = oneCount - (buckets.get(bucketId - 1).getSize() / 2);
                break;
            }
        }
        return oneCount;
    }
}
