package cn.potmart.geo.process.test.expression;

import cn.potmart.geo.process.test.Utils;
import org.geotools.data.DataStore;
import org.geotools.data.DataStoreFinder;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.data.transform.Definition;
import org.geotools.data.transform.TransformFactory;
import org.geotools.factory.CommonFactoryFinder;
import org.geotools.filter.text.cql2.CQL;
import org.geotools.filter.text.cql2.CQLException;
import org.geotools.filter.text.ecql.ECQL;
import org.geotools.geojson.feature.FeatureJSON;
import org.opengis.filter.expression.Expression;
import org.opengis.referencing.FactoryException;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by GOT.hodor on 2017/10/28.
 */
public class Exp {

    public static void main(String[] args) {
        try{
            Map<String, Object> params = Utils.connParams();

            DataStore dataStore = DataStoreFinder.getDataStore(params);

            String layer = "camera";
            SimpleFeatureSource featureSource = dataStore.getFeatureSource(layer);

            //String ecqlExpression = "if_then_else(greaterThan(zval, 50), 1, 0)";
            String ecqlExpression = "\"zval\" + 10";
            Expression expression = CQL.toExpression(ecqlExpression);

            List<Definition> definitionList = new ArrayList<>();
            definitionList.add(new Definition("z_val", expression));

            SimpleFeatureSource transSource = TransformFactory.transform(featureSource, "comparision", definitionList);

            SimpleFeatureCollection featureCollection = transSource.getFeatures();

            FeatureJSON featureJSON = new FeatureJSON();

            File file = new File("E:/comparision.json");
            featureJSON.writeFeatureCollection(featureCollection, file);

            dataStore.dispose();

        }catch (CQLException e) {

        }catch (IOException e) {

        }

    }
}
