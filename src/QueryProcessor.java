import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.concurrent.Semaphore;

/**
 * Created by deepakrtp on 11/10/17.
 */
public class QueryProcessor implements Runnable {
    private BufferedReader reader ;
    private StreamReader streamReader;
    private int windowSize;
    private Semaphore lock;

    QueryProcessor(int windowSize , StreamReader streamReader, Semaphore lock) {
        reader = new BufferedReader(new InputStreamReader(System.in));
        this.streamReader = streamReader;
        this.windowSize = windowSize;
        this.lock = lock;
    }

    @Override
    public void run() {
        while( true ) {
            try {
                if ( (reader.read()) != '\0') {
                    lock.acquire();
                    String input = reader.readLine().trim();
                    lock.release();
                    /*
                    have to check the format here

                    This is the format for query
                    What is the number of ones for last 1000 data?
                    ““What is the number of ones for last <k> data?
                    */
                    /*System.out.println("input = " +input);*/
                    if( input.startsWith("#") ) {
                        continue;
                    }
                    if(!input.startsWith("W")) {
                        input = "W"+ input;
                    }
                    String inputParts[] = input.split(" ");


                    if(inputParts.length != 10) {
                        print("The format for the query is wrong. Please enter in the correct format");
                        continue;
                    } else if(!inputParts[8].matches("[0-9]+")){
                        print("The k value is invalid in the query. Please enter in the correct format");
                        continue;
                    }
                    else if(Integer.parseInt(inputParts[8]) <= 0) {
                        print("The k value is invalid in the query.");
                        continue;
                    }
                    String format = "What is the number of ones for last "+ inputParts[8] + " data?";
                    if( !input.equals(format)  ) {
                        print("The format for the query is wrong. Please enter in the correct format");
                        continue;
                    }
                    lock.acquire();
                    engine (Integer.parseInt(inputParts[8]));
                    lock.release();

                }
            } catch (Exception e) {
                System.out.println("The format of the query is wrong.");
                e.printStackTrace();
            }
        }
    }

    private void print(String message) {
        try{
            lock.acquire();
            System.out.println();
            System.out.println(message);
            lock.release();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    /*
        Function to find the number of ones in the last K bits
        if query in window size then check the latest streams which we have stored in memory to get the exact number of ones
        else check the buckets to get a approximate of ones.
    */
    private void engine (int size) {

        if(size <= windowSize) {
            int oneCount =  streamReader.getExactCount(size);
            System.out.println();
            System.out.println("The number of ones of last " +size + " data is exactly " +oneCount);
        }
        else {
            int oneCount =  streamReader.getEstimateCount(size);
            System.out.println();
            System.out.println("The number of ones of last " +size + " data is estimated " +oneCount);
        }
    }
}
