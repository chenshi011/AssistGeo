import static java.lang.System.out;

/**
 * Created by GOT.hodor on 2017/12/19.
 */
public class GemetryDescriptor {

    public static void main(String[] args) {
        int imgW = 14080;
        int imgH = 9472;
        int tileSize = 256;

        while (imgW > tileSize || imgH > tileSize) {
            double tileX = Math.ceil(imgW / tileSize);
            double tileY = Math.ceil(imgH / tileSize);

            out.println("x ->" + tileX + " y ->" + tileY + " tile size ->" + tileSize);
            tileSize += tileSize;
        }

        out.println(Math.ceil(14080.0/512.0));
    }
}
