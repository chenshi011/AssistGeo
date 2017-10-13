package cn.potmart.geo.process.test.crop;

import cn.potmart.geo.process.crop.RasterCrop;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.Polygon;
import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.io.WKTReader;

import java.io.File;
import java.io.IOException;

/**
 * Created by GOT.hodor on 2017/10/11.
 */
public class Crop {

    public static void main(String[] args) {
        try{
            String inpath = "F:\\MapWorkspace\\gdem\\L12.tif";
            String outpath = "F:\\MapWorkspace\\gdem\\some.tif";
            File in = new File(inpath);
            File out = new File(outpath);


            String wkt = "POLYGON((119.5 31, 120.0 30, 119.5 29, 119 30, 119.5 31))";
            Geometry polygon = geometryFromWkt(wkt);

            RasterCrop crop = new RasterCrop(in, polygon, out);
            crop.readCoverage();
            crop.executeCrop();

        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Geometry geometryFromWkt(String wkt) {
        WKTReader reader = new WKTReader();
        try{
            return reader.read(wkt);
        }catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }
}
