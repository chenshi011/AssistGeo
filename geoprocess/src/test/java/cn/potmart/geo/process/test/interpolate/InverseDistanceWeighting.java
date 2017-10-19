package cn.potmart.geo.process.test.interpolate;

import cn.potmart.geo.feature.build.FeatureTypeBuilder;
import cn.potmart.geo.process.test.Utils;
import com.sun.corba.se.impl.logging.UtilSystemException;
import com.sun.corba.se.impl.orbutil.graph.NodeData;
import com.vividsolutions.jts.geom.*;
import it.geosolutions.jaiext.interpolators.InterpolationNearest;
import it.geosolutions.jaiext.range.NoDataContainer;
import org.apache.commons.collections.map.HashedMap;
import org.geotools.coverage.GridSampleDimension;
import org.geotools.coverage.grid.*;
import org.geotools.coverage.processing.operation.*;
import org.geotools.coverage.processing.operation.Interpolate;
import org.geotools.data.collection.ListFeatureCollection;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.feature.FeatureCollection;
import org.geotools.feature.FeatureIterator;
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.geotools.feature.simple.SimpleFeatureTypeBuilder;
import org.geotools.feature.simple.SimpleFeatureTypeImpl;
import org.geotools.gce.geotiff.GeoTiffReader;
import org.geotools.gce.geotiff.GeoTiffWriter;
import org.geotools.geojson.feature.FeatureJSON;
import org.geotools.geometry.DirectPosition2D;
import org.geotools.geometry.jts.JTS;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.grid.Grids;
import org.geotools.process.raster.ContourProcess;
import org.geotools.process.raster.PolygonExtractionProcess;
import org.geotools.process.vector.BarnesSurfaceInterpolator;
import org.geotools.process.vector.BarnesSurfaceProcess;
import org.geotools.process.vector.BilinearInterpolator;
import org.geotools.process.vector.SimplifyProcess;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.geotools.util.Converter;
import org.geotools.util.GeometryTypeConverterFactory;
import org.jaitools.numeric.Range;
import org.opengis.coverage.InterpolationMethod;
import org.opengis.coverage.grid.GridCoverage;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.operation.TransformException;
import wContour.*;

import javax.media.jai.Interpolation;
import java.awt.*;
import java.awt.Polygon;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.List;

import static java.lang.System.gc;
import static java.lang.System.out;

/**
 * Created by GOT.hodor on 2017/10/16.
 */
public class InverseDistanceWeighting {

    public static void main(String[] args) {
        executeAction();
    }

    public static void executeAction() {
        String path = "F:/MapWorkspace/gdem";
        //featureGrid(path);
        //generateTiffGrid(path);
        //rasterToPolygon(path);

        guideIDW(path);

    }

    /*
    private static GridCoverage2D getMatrix(GridCoverage2D gridCoverage) {
        RenderedImage img = gridCoverage.getRenderedImage();
        Raster raster = img.getData();
    }

    private static double[][] idw(double[][] grid) {
        wContour.Interpolate.interpolation_IDW_Neighbor(grid);
    }
    */

    private static GridCoverage2D gtIDW(GridCoverage2D grid) {
        return Interpolator2D.create(grid);
    }

    private static void featureGrid(String path) {
        try{
            ReferencedEnvelope env = new ReferencedEnvelope(
                    118, 121, 28, 31,
                    DefaultGeographicCRS.WGS84
            );
            SimpleFeatureSource featureSource = Grids.createSquareGrid(env, 0.1);
            SimpleFeatureCollection featureCollection = featureSource.getFeatures();

            FeatureJSON featureJSON = new FeatureJSON();
            File file = new File(path + "\\featureGrid.json");
            featureJSON.writeFeatureCollection(featureCollection, file);
        }catch (IOException e){
            e.printStackTrace();
        }

    }

    /**
     *
     * @param path
     * @throws IOException
     * @throws FactoryException
     */
    private static void generateTiffGrid(String path) {

        try{
            GridCoverageFactory gcf = new GridCoverageFactory();

            ReferencedEnvelope env = new ReferencedEnvelope(
                    118, 121, 28, 31,
                    DefaultGeographicCRS.WGS84
            );

            int w = 256;
            int h = 256;

            float[][] matrix = new float[256][256];

            float noDataVal = -999;

            SimpleFeatureCollection featureCollection = Utils.createPointFeatureCollection();

            vectorToMatrix(featureCollection, "zval", env, w, h, noDataVal);

        /*
        BilinearInterpolator interpolator = new BilinearInterpolator(matrix, noDataVal);
        float[][] interMatrix = interpolator.interpolate(w, h, false);
        */

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
                    256, // outputWidth
                    256, // outputHeight
                    null // monitor
            );

            GridCoverage2D gridCoverage = gcf.create("name", matrix, env);

            //灰度源
            GridCoverage[] sources = new GridCoverage[1];
            sources[0] = barnesGrid;

            //无值
            Map<String, Object> props = new HashedMap();
            props.put(NoDataContainer.GC_NODATA, new NoDataContainer(-999));

            GridCoverage2D gridCoverage2D = gcf.create(
                    "a",
                    gridCoverage.getRenderedImage(),
                    env,
                    gridCoverage.getSampleDimensions(),
                    sources,
                    props);



            File file = new File(path + "/idw.tiff");
            Utils.writeTiff(file, gridCoverage2D);

            File file1 = new File(path + "/arcii_idw.ascii");
            Utils.writeArcGrid(file1, gridCoverage2D);

            gridCoverage.dispose(true);
            gridCoverage2D.dispose(true);


            GeoTiffReader tiffReader = new GeoTiffReader(file);
            GridCoverage2D readGrid = tiffReader.read(null);
            int[] arr = new int[1];
            RenderedImage renderedImage = readGrid.getRenderedImage();
            Raster raster = renderedImage.getData();
            raster.getPixel(170,170,arr);
            out.print(arr[0]);
        }catch (IOException e) {
            e.printStackTrace();
        }catch (FactoryException e) {
            e.printStackTrace();
        }

    }

    /**
     *
     */
    private static void rasterToPolygon(String path) {
        File file = new File(path + "/barnes.tiff");

        try{
            GeoTiffReader tiffReader = new GeoTiffReader(file);
            GridCoverage2D gridCoverage = tiffReader.read(null);
            double noData = tiffReader.getMetadata().getNoData();

            List<Number> noDataVals = new ArrayList<>();
            noDataVals.add(noData);

            List<Range> ranges = new ArrayList<>();

            Range range = new Range(0.0,true,43.92,false);
            ranges.add(range);

            Range range1 = new Range(43.92, true, 43.93, false);
            ranges.add(range1);

            Range range2 = new Range(43.93, true, 100.0, false);
            ranges.add(range2);


            PolygonExtractionProcess extractionProcess = new PolygonExtractionProcess();
            SimpleFeatureCollection featureCollection = extractionProcess.execute(
                    gridCoverage,
                    0,
                    true,
                    null,
                    noDataVals, //Collection<Number> noDataValues
                    ranges, //List<Range> ranges
                    null
            );

            /*
            SimplifyProcess simplifyProcess = new SimplifyProcess();
            SimpleFeatureCollection featureCollection1 = simplifyProcess.execute(featureCollection, 1, true);
            */

            FeatureIterator<SimpleFeature> fi2 = featureCollection.features();

            SimpleFeatureTypeBuilder typeBuilder = new SimpleFeatureTypeBuilder();
            typeBuilder.setName("line");
            typeBuilder.add("geom", LineString.class, featureCollection.getSchema().getCoordinateReferenceSystem());
            typeBuilder.setDefaultGeometry("geom");
            SimpleFeatureType featureType = typeBuilder.buildFeatureType();

            SimpleFeatureBuilder featureBuilder = new SimpleFeatureBuilder(featureType);

            ListFeatureCollection featureCollection1 = new ListFeatureCollection(featureType);


            try{
                while (fi2.hasNext()) {
                    SimpleFeature feature = fi2.next();
                    com.vividsolutions.jts.geom.Polygon polygon = (com.vividsolutions.jts.geom.Polygon)feature.getDefaultGeometry();
                    LineString lineString = polygon.getExteriorRing();

                    featureBuilder.set("geom", lineString);

                    SimpleFeature f = featureBuilder.buildFeature(null);
                    featureCollection1.add(f);

                }
            }catch (Exception e) {
                e.printStackTrace();
            }
            finally {
                fi2.close();
            }

            FeatureIterator<SimpleFeature> fi = featureCollection.features();
            try{
                while (fi.hasNext()) {
                    SimpleFeature feature = fi.next();
                    feature.setDefaultGeometry(JTS.smooth((Geometry) feature.getDefaultGeometry(), 0.0));
                }
            }finally {
                fi.close();
            }

            File file1 = new File(path + "/extract.json");
            Utils.writeGeoJSONFile(file1, featureCollection);


            File file2 = new File(path + "/extract2.json");
            Utils.writeGeoJSONFile(file2, featureCollection1);

            double[] levels = new double[]{0.0, 43.91, 43.92, 43.93, 100};


            ContourProcess contourProcess = new ContourProcess();
            SimpleFeatureCollection featureCollection2 = contourProcess.execute(
                    gridCoverage,
                    0,
                    levels,
                    0.1,
                    true,
                    true,
                    null,
                    null
            );

            File file3 = new File(path + "/contour.json");
            Utils.writeGeoJSONFile(file3, featureCollection2);

            gridCoverage.dispose(true);
            tiffReader.dispose();

        }catch (IOException e) {
            e.printStackTrace();
        }



    }

    private static void guideIDW(String path) {
        try{
            actIDW(path);
        }catch (FactoryException e) {
            e.printStackTrace();
        }catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static float[][] vectorToMatrix(SimpleFeatureCollection featureCollection, String attr, ReferencedEnvelope env, int w, int h, float nv) {
        float[][] matrix = new float[w][h];
        for (int i=0;i<w;i++) {
            for (int j=0;j<h;j++){
                matrix[i][j] = nv;
            }
        }

        double xcellSize = (env.getMaximum(0) - env.getMinimum(0))/w;
        double ycellSize = (env.getMaximum(1) - env.getMinimum(1))/h;

        FeatureIterator<SimpleFeature> fi = featureCollection.features();
        try{
            while (fi.hasNext()) {
                SimpleFeature feature = fi.next();

                Geometry geometry = (Geometry) feature.getDefaultGeometry();
                Coordinate[] coordinates = geometry.getCoordinates();

                for (int n=0; n < coordinates.length; n++) {
                    double col = (coordinates[n].x - env.getMinimum(0)) / xcellSize;
                    double row = (coordinates[n].y - env.getMinimum(1)) / ycellSize;

                    int colIdx = Double.valueOf(Math.floor(col)).intValue();
                    int rowIdx = Double.valueOf(Math.floor(row)).intValue();

                    out.println("x :" + colIdx + " y : " + rowIdx);

                    matrix[colIdx][rowIdx] = (Float.class).cast(feature.getAttribute(attr));
                }

            }
        }finally {
            fi.close();
        }
        return matrix;
    }

    private static void actIDW(String path) throws FactoryException, IOException {
        GridCoverageFactory gcf = new GridCoverageFactory();

        ReferencedEnvelope env = new ReferencedEnvelope(
                118, 121, 28, 31,
                DefaultGeographicCRS.WGS84
        );

        int w = 256;
        int h = 256;

        float noDataVal = -999;

        SimpleFeatureCollection featureCollection = Utils.createPointFeatureCollection();

        float[][] matrix = vectorToMatrix(featureCollection, "zval", env, w, h, noDataVal);

        float[][] idwMatrix = idw(matrix, 4, 2, noDataVal);

        GridCoverage2D gridCoverage2D = gcf.create("idw", idwMatrix, env);

        File file = new File(path + "/idw_grid.tiff");
        Utils.writeTiff(file, gridCoverage2D);

    }

    /**
     * Inverse Distance Weighting
     * only support float[][]
     * @param grid matrix
     * @param gr search grid range
     * @param pow power, suggest 2
     * @param nv grid no data value
     * @return
     */
    public static float[][] idw(float[][] grid, int gr, int pow, float nv) {
        /**
         * <p>
         *     Inverse Distance Weighting
         *     Zi' = Σ(Zi/Di^pow) * ([Σ(1/Di^pow)]^(-1))
         * </p>
         */

        if (gr < 1) gr = 1; //default grid radius = 1;

        int w = grid.length;    // grid width
        int h = grid[0].length; // grid height

        float[][] matrix = new float[w][h];

        //region do loop
        for (int x=0; x < w; x++) {     //from grid start x to grid end x
            for (int y=0; y < h; y++) {     //from grid end y to grid end y

                //region if grid cell value is a observable value, continue the loop
                if (grid[x][y] != nv) {
                    matrix[x][y] = grid[x][y];
                    continue;
                }
                //endregion

                //region block by grid range
                int sx = (x - gr) < 0 ? 0 : (x - gr);   // start x
                int sy = (y - gr) < 0 ? 0 : (y - gr);   // start y
                int ex = (x + gr) > (w-1) ? (w-1) : (x + gr);   //end x
                int ey = (y + gr) > (h-1) ? (h-1) : (y + gr);   //end y
                //endregion

                //region if all nearest is noDataValue, grid cell is noDataValue
                int sx1 = sx;
                boolean isNV = true;

                while (sx1 <= ex) {     //form start x to end x
                    int sy1 = sy;   //from start y to end y
                    while (sy1 <= ey) {
                        isNV = grid[sx1][sy1] == nv;
                        if (!isNV){
                            break;
                        }
                        sy1++;
                    }
                    if (!isNV) {
                        break;
                    }
                    sx1++;
                }

                if (isNV) {
                    matrix[x][y] = nv;
                    continue;
                }
                //endregion

                //region grid cell value from block, use IDW
                float sz = 0.0f;      // z val summary
                float sw = 0.0f;      // weight summary
                //region loop block
                while (sx <= ex) {
                    int sy1 = sy;   //from start y to end y;
                    while (sy1 <= ey) {
                        int dx = sx - x;    // delta x
                        int dy = sy1 - y;    // delta y
                        double dis = Math.sqrt(dx * dx + dy * dy);  // distance
                        float pdis = (float) Math.pow(dis, pow);    // distance power
                        float val = (grid[sx][sy1] == nv)?0.0f:grid[sx][sy1];   //grid cell value
                        if (dis != 0) {     // distance=0 is cell its self
                            sz = sz +  val/pdis;    // summary of z value
                            sw = sw + 1/pdis;       // summary of weighted
                        }
                        sy1++;
                    }
                    sx++;
                }
                //endregion
                matrix[x][y] = sz/sw;   // interpolated cell value
                //endregion

            }
        }
        //endregion
        return matrix;
    }



}
