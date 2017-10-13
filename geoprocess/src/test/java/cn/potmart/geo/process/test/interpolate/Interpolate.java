package cn.potmart.geo.process.test.interpolate;

import cn.potmart.geo.feature.build.FieldInfo;
import cn.potmart.geo.process.interpolate.IDWGridBuilder;
import cn.potmart.geo.process.interpolate.WarpGridBuilder;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.io.WKTReader;
import org.geotools.coverage.grid.GridCoverage2D;
import org.geotools.coverage.grid.GridCoverageFactory;
import org.geotools.coverage.grid.Interpolator2D;
import org.geotools.coverage.grid.io.imageio.GeoToolsWriteParams;
import org.geotools.data.DataUtilities;
import org.geotools.data.collection.ListFeatureCollection;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.geotools.feature.simple.SimpleFeatureTypeBuilder;
import org.geotools.gce.geotiff.GeoTiffFormat;
import org.geotools.gce.geotiff.GeoTiffReader;
import org.geotools.gce.geotiff.GeoTiffWriteParams;
import org.geotools.gce.geotiff.GeoTiffWriter;
import org.geotools.gce.image.WorldImageWriter;
import org.geotools.geometry.DirectPosition2D;
import org.geotools.geometry.Envelope2D;
import org.geotools.process.raster.CoverageUtilities;
import org.geotools.process.vector.VectorToRasterProcess;
import org.geotools.referencing.CRS;
import org.geotools.referencing.crs.DefaultEngineeringCRS;
import org.geotools.referencing.operation.builder.MappedPosition;
import org.opengis.coverage.InterpolationMethod;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.geometry.Envelope;
import org.opengis.parameter.GeneralParameterValue;
import org.opengis.parameter.ParameterValue;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import javax.media.jai.Interpolation;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by GOT.hodor on 2017/10/13.
 */
public class Interpolate {

    public static void main(String[] args) {
        System.setProperty("org.geotools.referencing.forceXY", "true");
        //idwWarpGridBuilder();
        vectorToRaster();
        //rasterInterpolate();
    }

    public static void rasterInterpolate() {
        String in = "F:\\MapWorkspace\\gdem\\some_1.tif";
        String path = "F:\\MapWorkspace\\gdem\\ras_interpolate.tif";

        try{
            File input = new File(in);
            GeoTiffReader tiffReader = new GeoTiffReader(input);
            GridCoverage2D sourceGrid = tiffReader.read(null);

            GridCoverage2D gridCoverage = Interpolator2D.create(sourceGrid, Interpolation.getInstance(Interpolation.INTERP_BICUBIC));

            File output = new File(path);
            GeoTiffWriter tiffWriter = new GeoTiffWriter(output);

            GeoTiffWriteParams params = new GeoTiffWriteParams();
            ParameterValue<GeoToolsWriteParams> value = GeoTiffFormat.GEOTOOLS_WRITE_PARAMS.createValue();
            value.setValue(params);

            tiffWriter.write(gridCoverage, new GeneralParameterValue[]{value});

            tiffWriter.dispose();

        }catch (IOException e) {
            e.printStackTrace();
        }


    }


    public static void vectorToRaster() {
        String path = "F:\\MapWorkspace\\gdem";
        try{
            SimpleFeatureTypeBuilder typeBuilder = new SimpleFeatureTypeBuilder();
            typeBuilder.setName("zvals");

            typeBuilder.add("name", String.class);
            typeBuilder.add("zval", Integer.class);
            typeBuilder.add("geom", Point.class, CRS.decode("EPSG:4326"));

            SimpleFeatureType featureType = typeBuilder.buildFeatureType();

            List<SimpleFeature> list = new ArrayList<>();

            SimpleFeatureBuilder featureBuilder = new SimpleFeatureBuilder(featureType);

            featureBuilder.set("name", "one");
            featureBuilder.set("zval", Integer.valueOf(1000));
            featureBuilder.set("geom", geometryFromWkt("POINT(120 30)"));
            SimpleFeature feature1 = featureBuilder.buildFeature("1");
            list.add(feature1);

            featureBuilder.set("name", "two");
            featureBuilder.set("zval", Integer.valueOf(2000));
            featureBuilder.set("geom", geometryFromWkt("POINT(119.5 29.5)"));
            SimpleFeature feature2 = featureBuilder.buildFeature("2");
            list.add(feature2);

            featureBuilder.set("name", "three");
            featureBuilder.set("zval", Integer.valueOf(3000));
            featureBuilder.set("geom", geometryFromWkt("POINT(119 29)"));
            SimpleFeature feature3 = featureBuilder.buildFeature("3");
            list.add(feature3);

            SimpleFeatureCollection featureCollection = DataUtilities.collection(list);

            cn.potmart.geo.process.interpolate.Interpolate interpolate = new cn.potmart.geo.process.interpolate.Interpolate();

            Envelope envelope = featureCollection.getBounds();

            //GridCoverage2D gridCoverage = interpolate.vectorToRaster(featureCollection, 32, 32, "value", "zval", envelope, null);

            GridCoverage2D sourceCoverage = VectorToRasterProcess.process(featureCollection, "zval",
                    new Dimension(256,256), envelope,
                    "geom", null);

            GridCoverage2D gridCoverage = Interpolator2D.create(sourceCoverage);

            File file = new File(path + "\\vec2ras.tiff");
            GeoTiffWriter tiffWriter = new GeoTiffWriter(file);
            GeoTiffWriteParams params = new GeoTiffWriteParams();
            params.setCompressionMode(GeoTiffWriteParams.MODE_EXPLICIT);
            params.setCompressionType("LZW");
            ParameterValue<GeoToolsWriteParams> value = GeoTiffFormat.GEOTOOLS_WRITE_PARAMS.createValue();
            value.setValue(params);
            tiffWriter.write(gridCoverage, new GeneralParameterValue[]{value});
            tiffWriter.dispose();

        }catch (FactoryException e) {
            e.printStackTrace();
        }catch (IOException e) {
            e.printStackTrace();
        }

    }


    public static void idwWarpGridBuilder() {
        String path = "F:\\MapWorkspace\\gdem";
        CoordinateReferenceSystem crs = DefaultEngineeringCRS.GENERIC_2D;
        boolean show = false;
        boolean write = true;
        try {
            // Envelope 20*20 km 00
            Envelope env = new Envelope2D(crs, -50, -50, 400000, 200000);

            // Generates 15 MappedPositions of approximately 2 m differences
            List<MappedPosition> mp = generateMappedPositions(env, 6, 2, crs);

            WarpGridBuilder builder = new IDWGridBuilder(mp, 5000, 5000, env);

            //gridTest(mp, builder.getMathTransform());
            GridCoverage2D dx  =  (new GridCoverageFactory()).create("idw - dx", builder.getDxGrid(), env);
            GridCoverage2D dy =  (new GridCoverageFactory()).create("idw - dy", builder.getDyGrid(), env);

            if (show == true) {
                dx.show();
                dy.show();
            }

            if (write == true) {
                WorldImageWriter writerx = new WorldImageWriter((Object) (new File(
                        path+"idwdx.png")));

                writerx.write(dx, null);
                WorldImageWriter writery = new WorldImageWriter((Object) (new File(
                        path+"idwdy.png")));

                writery.write(dy, null);

                writerx.dispose();
                writery.dispose();
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private static List<MappedPosition> generateMappedPositions(Envelope env, int number, double deltas, CoordinateReferenceSystem crs) {
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

    private static Geometry geometryFromWkt(String wkt) {
        try{
            WKTReader wktReader = new WKTReader();
            return wktReader.read(wkt);
        }catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }
}
