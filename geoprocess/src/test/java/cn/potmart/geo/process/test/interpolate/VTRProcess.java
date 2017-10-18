package cn.potmart.geo.process.test.interpolate;

import cn.potmart.geo.process.test.Utils;
import com.vividsolutions.jts.geom.Geometry;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.feature.FeatureIterator;
import org.geotools.geometry.jts.Geometries;
import org.geotools.process.vector.VectorToRasterProcess;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.referencing.FactoryException;

import java.awt.*;
import java.io.IOException;

import static java.lang.System.out;

/**
 * Created by GOT.hodor on 2017/10/16.
 */
public class VTRProcess {

    public static void main(String[] args) {
        Color color = valueToColor(44.0, 1);
        out.println(color.toString());

        geometryType();
    }

    private static Color valueToColor(Number value, int type) {
        int intBits;
        if(type == 1) {
            intBits = Float.floatToIntBits(value.floatValue());
        } else {
            intBits = value.intValue();
        }

        out.printf("val : " + value + " bits : " + intBits);
        return new Color(intBits, true);
    }

    private static void geometryType() {
        try{
            SimpleFeatureCollection featureCollection = Utils.createPointFeatureCollection();
            FeatureIterator fi = featureCollection.features();

            try{
                while (fi.hasNext()) {
                    SimpleFeature feature = (SimpleFeature) fi.next();
                    Geometry geometry = (Geometry) feature.getDefaultGeometry();
                    Geometries type = Geometries.get(geometry);
                    out.println("featureType : " + type.ordinal() + " class : " + type.getBinding().getName());
                }
            }finally {
                fi.close();
            }

            String line = "LineString(118 30, 119 30, 120 29)";
            Geometry geometry = Utils.geometryFromWkt(line);

            Geometries type = Geometries.get(geometry);
            out.println("featureType : " + type.ordinal() + " class : " + type.getBinding().getName());

        }catch (FactoryException e) {
            e.printStackTrace();
        }

    }
}
