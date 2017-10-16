package cn.potmart.geo.process.test.interpolate;

import cn.potmart.geo.process.test.Utils;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import org.geotools.coverage.grid.*;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.feature.FeatureCollection;
import org.geotools.feature.FeatureIterator;
import org.geotools.geojson.feature.FeatureJSON;
import org.geotools.geometry.DirectPosition2D;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.grid.Grids;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.operation.TransformException;
import wContour.*;

import javax.media.jai.Interpolation;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;

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

        float noDataVal = -999.0f;

        SimpleFeatureCollection featureCollection = Utils.createFeatureCollection();

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

                    matrix[rowIdx][colIdx] = (Integer.class).cast(feature.getAttribute(key));
                }

            }
        }finally {
            fi.close();
        }

        GridCoverage2D gridCoverage = gcf.create("name", matrix, env);



        File file = new File(path + "/idw.tiff");
        Utils.writeTiff(file, gridCoverage);
    }


    private static void writeGeoTiff(File file, GridCoverage2D gridCoverage) {

    }
}
