package cn.potmart.geo.process.interpolate;

import org.geotools.referencing.operation.transform.IdentityTransform;
import org.opengis.geometry.Envelope;
import org.opengis.referencing.NoSuchIdentifierException;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.TransformException;

import java.util.List;

/**
 * Created by GOT.hodor on 2017/10/13.
 */


public class IDWGridBuilder extends WarpGridBuilder {
    /**
     * Constructs IDWGridBuilder from set of parameters.
     *
     * @param vectors known shift vectors
     * @param dx width of gird cell
     * @param dy height of grid cells
     * @param env Envelope of generated grid
     * @throws org.opengis.referencing.operation.TransformException
     */
    public IDWGridBuilder(List vectors, double dx, double dy, Envelope env)
            throws TransformException, NoSuchIdentifierException {
        super(vectors, dx, dy, env, IdentityTransform.create(2));
    }

    /**
     * Constructs IDWGridBuilder from set of parameters. The Warp Grid values are
     * calculated in transformed coordinate system.
     * @param vectors known shift vectors
     * @param dx width of gird cell
     * @param dy height of grid cells
     * @param envelope Envelope of generated grid
     * @param realToGrid Transformation from real to grid coordinates (when working with images)
     * @throws TransformException
     */
    public IDWGridBuilder(List vectors, double dx, double dy, Envelope envelope,
                          MathTransform realToGrid) throws TransformException, NoSuchIdentifierException {
        super(vectors, dx, dy, envelope, realToGrid);
    }

    protected float[] computeWarpGrid(GridParameters gridParams)
            throws TransformException {
        IDWInterpolation dxInterpolation = new IDWInterpolation(buildPositionsMap(0));
        IDWInterpolation dyInterpolation = new IDWInterpolation(buildPositionsMap(1));

        return interpolateWarpGrid(gridParams, dxInterpolation, dyInterpolation);
    }
}
