package cn.potmart.geo.process.test.buffer;

import cn.potmart.geo.process.test.Utils;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.geojson.feature.FeatureJSON;
import org.geotools.process.vector.BufferFeatureCollection;
import org.opengis.referencing.FactoryException;

import java.io.IOException;

public class BufferProcess {

    public static void main(String[] args) {
        buffer();
    }

    private static void buffer() {
        try{
            SimpleFeatureCollection simpleFeatureCollection = Utils.createPointFeatureCollection();
            BufferFeatureCollection bufferFeatureCollection = new BufferFeatureCollection();
            SimpleFeatureCollection result = bufferFeatureCollection.execute(simpleFeatureCollection, 100.0, "geom");
            FeatureJSON featureJSON = new FeatureJSON();
            String json = featureJSON.toString(result);
            System.out.println(json);
            System.out.println(featureJSON.toString(simpleFeatureCollection));
        }catch (FactoryException e) {

        }catch (IOException e) {

        }

    }
}
