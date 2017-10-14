package cn.potmart.geo.process.test.interpolate;

import cn.potmart.geo.process.interpolate.IDWGridBuilder;
import cn.potmart.geo.process.interpolate.WarpGridBuilder;
import com.vividsolutions.jts.geom.*;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.impl.PackedCoordinateSequenceFactory;
import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.io.WKTReader;
import org.geotools.coverage.CoverageFactoryFinder;
import org.geotools.coverage.grid.GridCoverage2D;
import org.geotools.coverage.grid.GridCoverageFactory;
import org.geotools.coverage.grid.GridGeometry2D;
import org.geotools.coverage.grid.Interpolator2D;
import org.geotools.coverage.grid.io.imageio.GeoToolsWriteParams;
import org.geotools.data.DataUtilities;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.feature.DefaultFeatureCollection;
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.geotools.feature.simple.SimpleFeatureTypeBuilder;
import org.geotools.gce.geotiff.GeoTiffFormat;
import org.geotools.gce.geotiff.GeoTiffReader;
import org.geotools.gce.geotiff.GeoTiffWriteParams;
import org.geotools.gce.geotiff.GeoTiffWriter;
import org.geotools.gce.image.WorldImageWriter;
import org.geotools.geojson.feature.FeatureJSON;
import org.geotools.geometry.DirectPosition2D;
import org.geotools.geometry.Envelope2D;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.process.ProcessException;
import org.geotools.process.raster.ContourProcess;
import org.geotools.process.vector.BarnesSurfaceProcess;
import org.geotools.process.vector.HeatmapProcess;
import org.geotools.process.vector.VectorToRasterProcess;
import org.geotools.referencing.CRS;
import org.geotools.referencing.crs.DefaultEngineeringCRS;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.geotools.referencing.operation.builder.MappedPosition;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.geometry.Envelope;
import org.opengis.parameter.GeneralParameterValue;
import org.opengis.parameter.ParameterValue;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.util.ProgressListener;

import javax.media.jai.Interpolation;
import java.awt.Dimension;
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

    /**
     *
     */
    public static void vectorToRaster() {
        String path = "F:\\MapWorkspace\\gdem";
        try{
            SimpleFeatureCollection featureCollection = createFeatureCollection();

            //cn.potmart.geo.process.interpolate.Interpolate interpolate = new cn.potmart.geo.process.interpolate.Interpolate();

            simpleVectorToRaster(featureCollection, path);

            executeVectorToRaster(featureCollection, path);

            vectorToBarnesSurface(featureCollection, path);

            customSurface(path);

            heatmapSurface(featureCollection, path);

            executeContour(path);

        }catch (FactoryException e) {
            e.printStackTrace();
        }catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     *
     * @return
     * @throws FactoryException
     */
    private static SimpleFeatureCollection createFeatureCollection() throws FactoryException {
        SimpleFeatureTypeBuilder typeBuilder = new SimpleFeatureTypeBuilder();
        typeBuilder.setName("zvals");

        typeBuilder.add("name", String.class);
        typeBuilder.add("zval", Integer.class);
        typeBuilder.add("geom", Point.class, CRS.decode("EPSG:4326"));

        SimpleFeatureType featureType = typeBuilder.buildFeatureType();

        List<SimpleFeature> list = new ArrayList<>();

        SimpleFeatureBuilder featureBuilder = new SimpleFeatureBuilder(featureType);

        featureBuilder.set("name", "one");
        featureBuilder.set("zval", Integer.valueOf(50));
        featureBuilder.set("geom", geometryFromWkt("POINT(120 30)"));
        SimpleFeature feature1 = featureBuilder.buildFeature("1");
        list.add(feature1);

        featureBuilder.set("name", "two");
        featureBuilder.set("zval", Integer.valueOf(20));
        featureBuilder.set("geom", geometryFromWkt("POINT(119.5 29.5)"));
        SimpleFeature feature2 = featureBuilder.buildFeature("2");
        list.add(feature2);

        featureBuilder.set("name", "three");
        featureBuilder.set("zval", Integer.valueOf(50));
        featureBuilder.set("geom", geometryFromWkt("POINT(119 29)"));
        SimpleFeature feature3 = featureBuilder.buildFeature("3");
        list.add(feature3);

        featureBuilder.set("name", "four");
        featureBuilder.set("zval", Integer.valueOf(50));
        featureBuilder.set("geom", geometryFromWkt("POINT(119 30)"));
        SimpleFeature feature4 = featureBuilder.buildFeature("4");
        list.add(feature4);

        featureBuilder.set("name", "five");
        featureBuilder.set("zval", Integer.valueOf(50));
        featureBuilder.set("geom", geometryFromWkt("POINT(120 29)"));
        SimpleFeature feature5 = featureBuilder.buildFeature("5");
        list.add(feature5);

        return DataUtilities.collection(list);
    }

    /**
     *
     * @param featureCollection
     * @param path
     * @throws IOException
     */
    public static void simpleVectorToRaster(SimpleFeatureCollection featureCollection, String path) throws IOException {
        Envelope envelope = featureCollection.getBounds();

        //GridCoverage2D gridCoverage = interpolate.vectorToRaster(featureCollection, 32, 32, "value", "zval", envelope, null);

        GridCoverage2D sourceCoverage = VectorToRasterProcess.process(
                featureCollection,
                "zval",
                new Dimension(256,256),
                envelope,
                "geom",
                null
        );

        GridCoverage2D gridCoverage = Interpolator2D.create(sourceCoverage);
        File file = new File(path + "\\vec2ras.tiff");
        writeTiff(file, gridCoverage);
    }

    /**
     *
     * @param featureCollection
     * @param path
     * @throws IOException
     */
    public static void executeVectorToRaster(SimpleFeatureCollection featureCollection, String path) throws IOException {
        ReferencedEnvelope env = new ReferencedEnvelope(
                118, 121, 28, 31,
                DefaultGeographicCRS.WGS84
        );

        VectorToRasterProcess parseProcess = new VectorToRasterProcess();
        GridCoverage2D gridCoverage = parseProcess.execute(
                featureCollection,
                6,
                6,
                "some",
                "zval",
                env,
                null
        );

        File file = new File(path + "//exe_raster.tif");
        writeTiff(file, gridCoverage);

        //contourProcess(gridCoverage, path);

    }

    /**
     *
     * @param featureCollection
     * @param path
     * @throws FactoryException
     * @throws IOException
     */
    public static void vectorToBarnesSurface(SimpleFeatureCollection featureCollection, String path) throws FactoryException, IOException {
        ReferencedEnvelope env = new ReferencedEnvelope(
                118,121,28,31,
                CRS.decode("EPSG:4326")
        );

        BarnesSurfaceProcess barnesSurfaceProcess = new BarnesSurfaceProcess();
        GridCoverage2D barnesGrid = barnesSurfaceProcess.execute(
                featureCollection,
                "zval", // valueAttr
                1000, // dataLimit
                10.0, // scale
                (Double) null, // convergence
                (Integer) 2, // passes
                (Integer) null, // minObservations
                (Double) null, // maxObservationDistance
                -999.0, // noDataValue
                1, // pixelsPerCell
                0.0, // queryBuffer
                env, // outputEnv
                100, // outputWidth
                100, // outputHeight
                null // monitor
        );

        File file2 = new File(path + "\\barnes.tiff");
        writeTiff(file2, barnesGrid);
    }

    /**
     * Custom surface
     */
    public static void customSurface(String path) {
        ReferencedEnvelope bounds = new ReferencedEnvelope(0, 30, 0, 30, DefaultGeographicCRS.WGS84);
        Coordinate[] data = new Coordinate[] {
                new Coordinate(10, 10, 100),
                new Coordinate(10, 20, 20),
                new Coordinate(20, 10, 0),
                new Coordinate(20, 20, 80) };
        SimpleFeatureCollection fc = createPoints(data, bounds);

        ProgressListener monitor = null;

        BarnesSurfaceProcess process = new BarnesSurfaceProcess();
        GridCoverage2D cov = process.execute(fc, // data
                "value", // valueAttr
                1000, // dataLimit
                10.0, // scale
                (Double) null, // convergence
                (Integer) 2, // passes
                (Integer) null, // minObservations
                (Double) null, // maxObservationDistance
                -999.0, // noDataValue
                1, // pixelsPerCell
                0.0, // queryBuffer
                bounds, // outputEnv
                100, // outputWidth
                100, // outputHeight
                monitor // monitor)
        );

        File file = new File(path + "\\simple_surface.tif");
        try {
            writeTiff(file, cov);
        }catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * Heat map
     */
    private static void heatmapSurface(SimpleFeatureCollection featureCollection, String path) {
        HeatmapProcess heatmapProcess = new HeatmapProcess();
        ReferencedEnvelope bounds = new ReferencedEnvelope(
                118,121,28,31,
                DefaultGeographicCRS.WGS84
        );

        GridCoverage2D gridCoverage = heatmapProcess.execute(
                featureCollection,
                16,
                "zval",
                1,
                bounds,
                256,
                256,
                null
        );

        File file = new File(path + "\\heatmap.tiff");

        try{
            writeTiff(file, gridCoverage);
        }catch (IOException e) {
            e.printStackTrace();
        }



    }

    private static void executeContour(String path) throws ProcessException, IOException {
        GridCoverage2D gridCoverage = createVerticalGradient(
                10,
                10,
                null,
                0,
                10
        );
        contourProcess(gridCoverage, path);
    }

    private static void contourProcess(GridCoverage2D gridCoverage, String path) throws ProcessException, IOException {
        ContourProcess contourProcess = new ContourProcess();
        SimpleFeatureCollection featureCollection = contourProcess.execute(
                gridCoverage,
                0,
                new double[20],
                null,
                true,
                true,
                null,
                null
        );

        File file = new File(path + "\\contour.json");

        writeGeoJSONFile(file, featureCollection);
    }

    private static void writeGeoJSONFile(File file, SimpleFeatureCollection featureCollection) throws IOException {
        FeatureJSON featureJSON = new FeatureJSON();
        featureJSON.writeFeatureCollection(featureCollection, file);
    }

    /**
     * write tif file
     * @param file
     * @param gridCoverage
     * @throws IOException
     */
    private static void writeTiff(File file, GridCoverage2D gridCoverage) throws IOException {
        GeoTiffWriter tiffWriter = new GeoTiffWriter(file);
        GeoTiffWriteParams params = new GeoTiffWriteParams();
        params.setCompressionMode(GeoTiffWriteParams.MODE_EXPLICIT);
        params.setCompressionType("LZW");
        ParameterValue<GeoToolsWriteParams> value = GeoTiffFormat.GEOTOOLS_WRITE_PARAMS.createValue();
        value.setValue(params);
        tiffWriter.write(gridCoverage, new GeneralParameterValue[]{value});
        tiffWriter.dispose();
    }

    /**
     * create points
     * @param pts
     * @param bounds
     * @return
     */
    private static SimpleFeatureCollection createPoints(Coordinate[] pts, ReferencedEnvelope bounds) {

        SimpleFeatureTypeBuilder tb = new SimpleFeatureTypeBuilder();
        tb.setName("obsType");
        tb.setCRS(bounds.getCoordinateReferenceSystem());
        tb.add("shape", MultiPoint.class);
        tb.add("value", Double.class);

        SimpleFeatureType type = tb.buildFeatureType();
        SimpleFeatureBuilder fb = new SimpleFeatureBuilder(type);
        DefaultFeatureCollection fc = new DefaultFeatureCollection();

        GeometryFactory factory = new GeometryFactory(new PackedCoordinateSequenceFactory());

        for (Coordinate p : pts) {
            Geometry point = factory.createPoint(p);
            fb.add(point);
            fb.add(p.z);
            fc.add(fb.buildFeature(null));
        }

        return fc;
    }

    private static GridCoverage2D createVerticalGradient(
            final int dataRows, final int dataCols,
            ReferencedEnvelope worldEnv,
            final float startValue, final float endValue) {

        GridCoverageFactory covFactory = CoverageFactoryFinder.getGridCoverageFactory(null);

        if (dataRows < 2) {
            throw new IllegalArgumentException("dataRows must be >= 2");
        }
        if (dataCols < 1) {
            throw new IllegalArgumentException("dataCols must be positive");
        }

        if (worldEnv == null) {
            worldEnv = new ReferencedEnvelope(0, dataCols, 0, dataRows, null);
        }

        float[][] DATA = new float[dataRows][dataCols];
        float delta = (endValue - startValue) / (dataRows - 1);

        for (int iy = 0; iy < dataRows; iy++) {
            float value = startValue + iy * delta;
            for (int ix = 0; ix < dataCols; ix++) {
                DATA[iy][ix] = value;
            }
            value += delta;
        }

        return covFactory.create("coverage", DATA, worldEnv);
    }

    /**
     * IDW
     */
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


    /**
     * Positions
     * @param env
     * @param number
     * @param deltas
     * @param crs
     * @return
     */
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

    /**
     * parse wkt
     * @param wkt
     * @return
     */
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
