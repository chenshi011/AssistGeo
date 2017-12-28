package cn.potmart.geo.process.test.expression;

import org.geotools.filter.text.cql2.CQLException;
import org.geotools.filter.text.ecql.ECQL;
import org.opengis.filter.expression.Expression;

public class Concatenate {

    public static void main(String[] args) {
        try {
            String ecql = "concatenate(code)";
            Expression expression = ECQL.toExpression(ecql);
        }catch (CQLException e) {
            e.printStackTrace();
        }

    }
}
