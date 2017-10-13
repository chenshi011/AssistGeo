package cn.potmart.geo.process.crop;

import com.vividsolutions.jts.geom.Geometry;
import org.geotools.coverage.grid.GridCoverage2D;
import org.geotools.coverage.grid.GridEnvelope2D;
import org.geotools.coverage.grid.GridGeometry2D;
import org.geotools.coverage.grid.io.AbstractGridFormat;
import org.geotools.coverage.grid.io.GridCoverage2DReader;
import org.geotools.coverage.grid.io.GridFormatFinder;
import org.geotools.factory.Hints;
import org.geotools.gce.geotiff.GeoTiffFormat;
import org.geotools.gce.geotiff.GeoTiffWriteParams;
import org.geotools.gce.geotiff.GeoTiffWriter;
import org.geotools.process.raster.CropCoverage;
import org.geotools.referencing.operation.matrix.AffineTransform2D;
import org.opengis.coverage.grid.GridCoverageWriter;
import org.opengis.parameter.GeneralParameterValue;
import org.opengis.parameter.ParameterValueGroup;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.util.ProgressListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.geom.AffineTransform;
import java.io.File;
import java.io.IOException;

/**
 * Created by GOT.hodor on 2017/10/11.
 */
public class RasterCrop {
    private final Logger log = LoggerFactory.getLogger(RasterCrop.class);

    private File inputFile;

    private File outputFile;

    private Geometry shape;

    private GridCoverage2D gridCoverage;

    private ProgressListener progressListener;

    private AbstractGridFormat gridFormat;

    /**
     * construct
     */
    public RasterCrop() {

    }

    /**
     *
     * @param inputFile
     * @param shape
     * @param outputFile
     */
    public RasterCrop(File inputFile, Geometry shape, File outputFile) {
        this.inputFile = inputFile;
        this.shape = shape;
        this.outputFile = outputFile;
    }

    /**
     * read coverage
     */
    public void readCoverage() {
        if (inputFile == null) {
            return;
        }

        gridFormat = GridFormatFinder.findFormat(inputFile);

        Hints hints = null;
        if (gridFormat instanceof GeoTiffFormat) {
            hints = new Hints(Hints.FORCE_LONGITUDE_FIRST_AXIS_ORDER,Boolean.TRUE);
        }

        GridCoverage2DReader gridReader = gridFormat.getReader(
                inputFile,
                hints);

        try{
            gridCoverage = gridReader.read(null);
        }catch (IOException e) {
            log.error("grid reader occurred io exception", e);
        }


    }

    /**
     * execute
     */
    public void executeCrop() {
        if (!detectParams()) {
            return;
        }

        CropCoverage cropCoverage = new CropCoverage();

        try{
            GridCoverage2D cropGridCoverage = cropCoverage.execute(gridCoverage, shape, progressListener);

            GeoTiffWriteParams tiffWriteParams = new GeoTiffWriteParams();
            tiffWriteParams.setCompressionMode(GeoTiffWriteParams.MODE_EXPLICIT);
            ParameterValueGroup params = new GeoTiffFormat().getWriteParameters();
            params.parameter(AbstractGridFormat.GEOTOOLS_WRITE_PARAMS.getName().toString()).setValue(tiffWriteParams);

            GridCoverageWriter gridWriter = gridFormat.getWriter(outputFile);
            gridWriter.write(cropGridCoverage, params.values().toArray(new GeneralParameterValue[1]));
            gridWriter.dispose();

        }catch (IOException e) {
            log.error("crop grid coverage occured exception", e);
        }

    }

    /**
     * detect some params
     * @return
     */
    private boolean detectParams() {
        if (gridCoverage == null) {
            return false;
        }

        if (shape == null) {
            return false;
        }

        if (outputFile == null) {
            return false;
        }

        if (gridFormat == null) {
            return false;
        }

        return true;
    }

    public File getInputFile() {
        return inputFile;
    }

    public void setInputFile(File inputFile) {
        this.inputFile = inputFile;
    }

    public Geometry getShape() {
        return shape;
    }

    public void setShape(Geometry shape) {
        this.shape = shape;
    }

    public ProgressListener getProgressListener() {
        return progressListener;
    }

    public void setProgressListener(ProgressListener progressListener) {
        this.progressListener = progressListener;
    }

    public File getOutputFile() {
        return outputFile;
    }

    public void setOutputFile(File outputFile) {
        this.outputFile = outputFile;
    }
}
