package cn.potmart.geo.process.interpolate;

import it.geosolutions.jaiext.interpolators.InterpolationNearest;
import org.geotools.coverage.grid.GridCoverage2D;
import org.geotools.coverage.grid.Interpolator2D;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.feature.FeatureCollection;
import org.geotools.filter.function.InterpolateFunction;
import org.geotools.gce.image.WorldImageWriter;
import org.geotools.process.vector.VectorToRasterProcess;
import org.geotools.referencing.operation.builder.MappedPosition;
import org.opengis.geometry.Envelope;
import org.opengis.util.ProgressListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Created by GOT.hodor on 2017/10/12.
 */
public class Interpolate {

    private final Logger log = LoggerFactory.getLogger(Interpolate.class);

    private List<MappedPosition> mpList = null;

    private File output;

    public Interpolate() {

    }

    public Interpolate(List<MappedPosition> mappedPositionList, File output) {
        this.output = output;
        mpList = mappedPositionList;
    }

    public void executeInterpolate(GridCoverage2D source) {
        Interpolator2D.create(source);

        WorldImageWriter imageWriter = new WorldImageWriter(output);
        try{
            imageWriter.write(source, null);
        }catch (IOException e) {
            log.error("interpolate error", e);
        }

    }

    /**
     *
     * @param features
     * @param rw
     * @param rh
     * @param title
     * @param attr
     * @param bound
     * @param listener
     * @return
     */
    public GridCoverage2D vectorToRaster(SimpleFeatureCollection features, Integer rw, Integer rh, String title,
                                         String attr, Envelope bound, ProgressListener listener) {
        VectorToRasterProcess process = new VectorToRasterProcess();
        return process.execute(features, rw, rh, title, attr, bound, listener);
    }

    /**
     *
     */
    public void oldInterpolate() {

    }

    public File getOutput() {
        return output;
    }

    public void setOutput(File output) {
        this.output = output;
    }
}
