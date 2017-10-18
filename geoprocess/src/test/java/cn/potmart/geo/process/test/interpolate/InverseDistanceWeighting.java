package cn.potmart.geo.process.test.interpolate;

import cn.potmart.geo.feature.build.FeatureTypeBuilder;
import cn.potmart.geo.process.test.Utils;
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

import static java.lang.System.out;

/**
 * Created by GOT.hodor on 2017/10/16.
 */
public class InverseDistanceWeighting {

    public static void main(String[] args) {
        executeAction();
    }

    public static void executeAction() {
        String path = "F:\\MapWorkspace\\gdem";
        try{
            featureGrid(path);
            generateTiffGrid(path);
            rasterToPolygon(path);
        }catch (IOException e) {
            e.printStackTrace();
        }catch (FactoryException e) {
            e.printStackTrace();
        }

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

    private static void featureGrid(String path) throws IOException {
        ReferencedEnvelope env = new ReferencedEnvelope(
                118, 121, 28, 31,
                DefaultGeographicCRS.WGS84
        );
        SimpleFeatureSource featureSource = Grids.createSquareGrid(env, 0.1);
        SimpleFeatureCollection featureCollection = featureSource.getFeatures();

        FeatureJSON featureJSON = new FeatureJSON();
        File file = new File(path + "\\featureGrid.json");
        featureJSON.writeFeatureCollection(featureCollection, file);
    }

    private static void generateTiffGrid(String path) throws IOException, FactoryException {
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

        for (int i=0; i<w;i++){
            for (int j=0;j<h;j++) {
                matrix[i][j] = noDataVal;
            }
        }

        FeatureIterator<SimpleFeature> fi = featureCollection.features();
        String key = "zval";

        double xcellSize = (env.getMaximum(0) - env.getMinimum(0))/w;
        double ycellSize = (env.getMaximum(1) - env.getMinimum(1))/h;

        try{
            while (fi.hasNext()) {
                SimpleFeature feature = fi.next();

                Geometry geometry = (Geometry) feature.getDefaultGeometry();
                Coordinate[] coordinates = geometry.getCoordinates();

                Grids.createSquareGrid(env, 256);

                for (int n=0; n < coordinates.length; n++) {
                    double col = (coordinates[n].x - env.getMinimum(0)) / xcellSize;
                    double row = (coordinates[n].y - env.getMinimum(1)) / ycellSize;

                    int colIdx = Double.valueOf(Math.floor(col)).intValue();
                    int rowIdx = Double.valueOf(Math.floor(row)).intValue();

                    out.println("x :" + colIdx + " y : " + rowIdx);

                    matrix[rowIdx][colIdx] = (Float.class).cast(feature.getAttribute(key));
                }

            }
        }finally {
            fi.close();
        }

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

}
