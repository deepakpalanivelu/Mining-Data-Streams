import java.util.*;
/**
 * Created by deepakrtp on 11/10/17.
 */

public class Bucket {

    private int size;
    private int endTimeStamp;

    Bucket(int timeStamp) {

        this.size = 1;
        this.endTimeStamp = timeStamp;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public int getSize() {
        return size;
    }

    public int getEndTimeStamp() {
        return endTimeStamp;
    }

}
