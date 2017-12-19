package cn.potmart.geo.process.test;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.io.WKTReader;
import org.geotools.coverage.grid.GridCoverage2D;
import org.geotools.coverage.grid.io.imageio.GeoToolsWriteParams;
import org.geotools.data.DataUtilities;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.geotools.feature.simple.SimpleFeatureTypeBuilder;
import org.geotools.gce.arcgrid.ArcGridFormat;
import org.geotools.gce.arcgrid.ArcGridWriteParams;
import org.geotools.gce.arcgrid.ArcGridWriter;
import org.geotools.gce.geotiff.GeoTiffFormat;
import org.geotools.gce.geotiff.GeoTiffWriteParams;
import org.geotools.gce.geotiff.GeoTiffWriter;
import org.geotools.geojson.feature.FeatureJSON;
import org.geotools.geometry.DirectPosition2D;
import org.geotools.process.raster.CoverageUtilities;
import org.geotools.referencing.CRS;
import org.geotools.referencing.operation.builder.MappedPosition;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.geometry.Envelope;
import org.opengis.parameter.GeneralParameterValue;
import org.opengis.parameter.ParameterValue;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * Created by GOT.hodor on 2017/10/16.
 */
public class Utils {

    public static Map<String, Object> connParams() {
        Map<String,Object> params = new HashMap<>();
        params.put( "dbtype", "postgis");
        params.put( "host", "127.0.0.1");
        params.put( "port", 6432);
        params.put( "schema", "public");
        params.put( "database", "energis_db");
        params.put( "user", "postgres");
        params.put( "passwd", "postgres");
        return params;
    }

    /**
     * parse wkt
     * @param wkt
     * @return
     */
    public static Geometry geometryFromWkt(String wkt) {
        try{
            WKTReader wktReader = new WKTReader();
            return wktReader.read(wkt);
        }catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Positions
     * @param env
     * @param number
     * @param deltas
     * @param crs
     * @return
     */
    public static List<MappedPosition> generateMappedPositions(Envelope env, int number, double deltas, CoordinateReferenceSystem crs) {
        List<MappedPosition> vectors = new ArrayList<MappedPosition>();
        double minx = env.getLowerCorner().getCoordinate()[0];
        double miny = env.getLowerCorner().getCoordinate()[1];

        double maxx = env.getUpperCorner().getCoordinate()[0];
        double maxy = env.getUpperCorner().getCoordinate()[1];

        final Random random = new Random(8578348921369L);

        for (int i = 0; i < number; i++) {
            double x = minx + (random.nextDouble() * (maxx - minx));
            double y = miny + (random.nextDouble() * (maxy - miny));
            vectors.add(new MappedPosition(new DirectPosition2D(crs, x, y),
                    new DirectPosition2D(crs,
                            (x + (random.nextDouble() * deltas)) - (random.nextDouble() * deltas),
                            (y + (random.nextDouble() * deltas)) - (random.nextDouble() * deltas))));
        }

        return vectors;
    }

    /**
     * 写文件
     * @param file 文件
     * @param gridCoverage GridCoverage2D
     * @throws IOException
     */
    public static void writeTiff(File file, GridCoverage2D gridCoverage) throws IOException {
        GeoTiffWriter tiffWriter = new GeoTiffWriter(file);
        GeoTiffWriteParams params = new GeoTiffWriteParams();
        params.setCompressionMode(GeoTiffWriteParams.MODE_EXPLICIT);
        params.setCompressionType("LZW");
        ParameterValue<GeoToolsWriteParams> value = GeoTiffFormat.GEOTOOLS_WRITE_PARAMS.createValue();
        value.setValue(params);
        tiffWriter.write(gridCoverage, new GeneralParameterValue[]{value});
        tiffWriter.dispose();
    }

    public static void writeGeoJSONFile(File file, SimpleFeatureCollection featureCollection) throws IOException {
        FeatureJSON featureJSON = new FeatureJSON();
        featureJSON.writeFeatureCollection(featureCollection, file);
    }

    public static void writeArcGrid(File file, GridCoverage2D gridCoverage) throws IOException {
        ArcGridWriter arcGridWriter = new ArcGridWriter(file);
        ArcGridWriteParams params = new ArcGridWriteParams();
        ParameterValue<GeoToolsWriteParams> value = ArcGridFormat.GEOTOOLS_WRITE_PARAMS.createValue();
        value.setValue(params);
        arcGridWriter.write(gridCoverage, new GeneralParameterValue[]{value});
        arcGridWriter.dispose();
    }

    /**
     *
     * @return
     * @throws FactoryException
     */
    public static SimpleFeatureCollection createPointFeatureCollection() throws FactoryException {
        SimpleFeatureTypeBuilder typeBuilder = new SimpleFeatureTypeBuilder();
        typeBuilder.setName("zvals");

        typeBuilder.add("name", String.class);
        typeBuilder.add("zval", Float.class);
        typeBuilder.add("geom", Point.class, CRS.decode("EPSG:4326"));

        SimpleFeatureType featureType = typeBuilder.buildFeatureType();

        List<SimpleFeature> list = new ArrayList<>();

        SimpleFeatureBuilder featureBuilder = new SimpleFeatureBuilder(featureType);

        featureBuilder.set("name", "one");
        featureBuilder.set("zval", Float.valueOf(50));
        featureBuilder.set("geom", geometryFromWkt("POINT(120 30)"));
        SimpleFeature feature1 = featureBuilder.buildFeature("1");
        list.add(feature1);

        featureBuilder.set("name", "two");
        featureBuilder.set("zval", Float.valueOf(20));
        featureBuilder.set("geom", geometryFromWkt("POINT(119.5 29.5)"));
        SimpleFeature feature2 = featureBuilder.buildFeature("2");
        list.add(feature2);

        featureBuilder.set("name", "three");
        featureBuilder.set("zval", Float.valueOf(50));
        featureBuilder.set("geom", geometryFromWkt("POINT(119 29)"));
        SimpleFeature feature3 = featureBuilder.buildFeature("3");
        list.add(feature3);

        featureBuilder.set("name", "four");
        featureBuilder.set("zval", Float.valueOf(50));
        featureBuilder.set("geom", geometryFromWkt("POINT(119 30)"));
        SimpleFeature feature4 = featureBuilder.buildFeature("4");
        list.add(feature4);

        featureBuilder.set("name", "five");
        featureBuilder.set("zval", Float.valueOf(50));
        featureBuilder.set("geom", geometryFromWkt("POINT(120 29)"));
        SimpleFeature feature5 = featureBuilder.buildFeature("5");
        list.add(feature5);

        return DataUtilities.collection(list);
    }

}
