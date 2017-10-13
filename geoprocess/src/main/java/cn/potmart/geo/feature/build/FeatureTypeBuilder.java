package cn.potmart.geo.feature.build;

import com.vividsolutions.jts.geom.*;
import org.geotools.factory.Hints;
import org.geotools.feature.AttributeTypeBuilder;
import org.geotools.feature.NameImpl;
import org.geotools.feature.simple.SimpleFeatureTypeBuilder;
import org.geotools.feature.simple.SimpleFeatureTypeImpl;
import org.geotools.referencing.ReferencingFactoryFinder;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.*;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.crs.CRSAuthorityFactory;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * <p>
 *     用于根据要素的字段描述来构造要素的类型
 * </p>
 *
 * Created by GOT.hodor on 2017/9/11.
 */
public class FeatureTypeBuilder {
    /**
     * <p>
     *     要素类型构造器
     *     1.调用buildSchema()方法，入参:
     *          schemaName 要素类型名称 -> String
     *          attrInfoList 属性字段信息 -> List
     *          geometryInfo 几何字段信息 -> FieldInfo
     *       返回 schema -> SimpleFeatureType
     *
     * </p>
     */

    /**
     * log
     */
    private final Logger log = LoggerFactory.getLogger(FeatureTypeBuilder.class);

    //region 字段类型
    /**
     * 字符型 java.lang.String
     */
    public static final String S_BINDING_STRING = "string";

    /**
     * 长整形 java.lang.Long
     */
    public final static String S_BINDING_LONG = "long";

    /**
     * 整形 java.lang.Integer
     */
    public final static String S_BINDING_INTEGER = "integer";

    /**
     * 双精度 java.lang.Double
     */
    public final static String S_BINDING_DOUBLE = "double";

    /**
     * 浮点 java.lang.Float
     */
    public final static String S_BINDING_FLOAT = "float";

    /**
     * 日期 java.util.Date
     */
    public final static String S_BINDING_DATE = "date";

    /**
     * 点 com.vividsolutions.jts.geom.Point
     */
    public final static String S_BINDING_POINT = "point";

    /**
     * 多点 com.vividsolutions.jts.geom.Multipoint
     */
    public final static String S_BINDING_MULTIPOINT = "multipoint";

    /**
     * 线 com.vividsolutions.jts.geom.LineString
     */
    public final static String S_BINDING_LINESTRING = "linestring";

    /**
     *  多线 com.vividsolutions.jts.geom.MultiLineString
     */
    public final static String S_BINDING_MULTILINESTRING = "multilinestring";

    /**
     * 面 com.vividsolutions.jts.geom.Polygon
     */
    public final static String S_BINDING_POLYGON = "polygon";

    /**
     * 多面 com.vividsolutions.jts.geom.MultiPolygon
     */
    public final static String S_BINDING_MULTIPOLYGON = "multipolygon";

    /**
     * 几何 com.vividsolutions.jts.geom.Geometry
     */
    public final static String S_BINDING_GEOMETRY = "geometry";

    /**
     * 包络范围 com.vividsolutions.jts.geom.Envelope
     */
    public final static String S_BINDING_ENVELOPE = "envelope";

    /**
     * 字段类型汇总
     */
    private final String[] types = {
            "string", "long", "integer", "float", "double", "date",
            "point", "multipoint",
            "linestring", "multilinestring",
            "polygon", "multipolygon"
    };

    /**
     * 几何图形类型汇总
     */
    private final String[] geometryType = {
            "point", "multipoint", "linestring", "multilinestring", "polygon", "multipolygon"
    };
    //endregion

    //region 坐标系
    /**
     * 像素坐标
     */
    public static final String S_CRS_PIXELS = "pixels";

    /**
     * 3857坐标
     */
    public static final String S_CRS_EPSG_3857 = "EPSG:3857";
    //endregion

    /**
     * schema builder
     */
    private SimpleFeatureTypeBuilder schemaBuilder;

    /**
     * attribute builder
     */
    private AttributeTypeBuilder attrBuilder;


    /**
     * constructor
     */
    public FeatureTypeBuilder() {
        //要素类型构造器
        schemaBuilder = new SimpleFeatureTypeBuilder();

        //字段构造器
        attrBuilder = new AttributeTypeBuilder();

    }


    //region ----------------------------------------------------------public methods----------------------------------------------------

    /**
     * 构建FeatureType
     * @param schemaName
     * @param attrInfoList
     * @param geometryInfo
     * @return
     */
    public SimpleFeatureType buildSchema(String schemaName, List<FieldInfo> attrInfoList, FieldInfo geometryInfo) {
        return buildSchema(schemaName, attrBuilder, attrInfoList, geometryInfo);
    }

    //region Schema

    /**
     * 构建FeatureType
     * @param schemaName
     * @param attrBuilder
     * @param attrInfoList
     * @param geometryInfo
     * @return
     */
    public SimpleFeatureType buildSchema(String schemaName, AttributeTypeBuilder attrBuilder, List<FieldInfo> attrInfoList, FieldInfo geometryInfo) {
        if (schemaName == null) {
            return null;
        }
        if (attrBuilder == null) {
            return null;
        }
        if (attrInfoList == null) {
            return null;
        }

        if(geometryInfo == null) {
            return null;
        }

        List<AttributeDescriptor> attributeDescriptors = buildAttributes(attrBuilder, attrInfoList, geometryInfo);
        if (attributeDescriptors == null) {
            return null;
        }

        return buildSchema(schemaName, attributeDescriptors, (GeometryDescriptor) attributeDescriptors.get(attributeDescriptors.size()-1));

    }

    /**
     * <p>
     *     创建schema
     * </p>
     * @param schemaName 名称
     * @param descriptorList 字段描述
     * @param defaultGeometry 几何字段
     * @return org.opengis.feature.simple.SimpleFeatureType
     */
    public SimpleFeatureType buildSchema(String schemaName, List<AttributeDescriptor> descriptorList, GeometryDescriptor defaultGeometry) {
        /**
         * <p>
         *     创建 Schema
         *     1.验证参数
         *     2.创建Schema 返回
         * </p>
         */

        //region 验证参数
        //名称验证
        if (schemaName == null) {
            return null;
        }

        //字段描述验证
        if (descriptorList == null || descriptorList.size() == 0) {
            return null;
        }

        //几何字段验证
        if (defaultGeometry == null) {
            return null;
        }

        if (!descriptorList.contains(defaultGeometry)) {
            return null;
        }
        //endregion

        //开始创建
        Name name = new NameImpl(schemaName);
        SimpleFeatureType schema = new SimpleFeatureTypeImpl(name, descriptorList, defaultGeometry,
                false, null, null, null);
        return schema;
    }
    //endregion

    //region AttributeDescriptor

    /**
     * 创建字段属性列表
     *
     * @param attrBuilder 属性构造器
     * @param attributeInfo 属性字段信息
     * @param geometryInfo 几何字段信息
     * @return java.util.List<org.opengis.feature.type.AttributeDescriptor>
     */
    public List<AttributeDescriptor> buildAttributes(AttributeTypeBuilder attrBuilder, List<FieldInfo> attributeInfo, FieldInfo geometryInfo) {
        /**
         * <p>
         *     构建属性
         *     1.验证输入参数
         *     2.创建属性字段
         * </p>
         */

        //region 验证参数
        if (attrBuilder == null) {
            log.error("未指定属性构造器");
        }

        if (attributeInfo == null || attributeInfo.size() == 0) {
            log.error("未指定属性信息");
            return null;
        }
        //endregion

        List<AttributeDescriptor> attributeDescriptors = new ArrayList<>();

        //region 属性字段
        //根据字段信息创建属性字段
        for (int i=0; i<attributeInfo.size(); i++) {
            FieldInfo fieldInfo = attributeInfo.get(i);
            AttributeDescriptor attributeDescriptor = this.buildAttributeDescriptor(attrBuilder, fieldInfo);
            if (attributeDescriptor != null) {
                attributeDescriptors.add(attributeDescriptor);
            }
        }


        //部分字段属性创建失败
        if (attributeDescriptors.size() != attributeInfo.size()) {
            log.error("指定的FeatureType构造属性字段失败");
            return null;
        }
        //endregion

        //region 几何字段
        GeometryDescriptor geometryDescriptor = this.buildGeometryDescriptor(attrBuilder, geometryInfo);
        if (geometryDescriptor == null) {
            log.error("指定的FeatureType构造Geometry Descriptor失败");
            return null;
        }

        attributeDescriptors.add(geometryDescriptor);
        //endregion

        return attributeDescriptors;

    }


    /**
     *
     * @param attrBuilder
     * @param fieldInfo
     * @return
     */
    public AttributeType buildAttributeType(AttributeTypeBuilder attrBuilder, FieldInfo fieldInfo) {
        /**
         * <p>
         *      创建字段
         *      1.字段信息参数验证
         *      2.设置参数
         * </p>
         */

        if (attrBuilder == null) {
            return null;
        }

        //region 字段信息参数验证
        //验证字段名称
        if (fieldInfo.getName() == null) {
            return null;
        }

        //验证字段类型
        if (!Arrays.asList(types).contains(fieldInfo.getType())) {
            return null;
        }
        //endregion


        //region 设置参数
        //字段名称
        attrBuilder.setName(fieldInfo.getName());

        //字段类型
        Class clazz = fieldTypeToBinding(fieldInfo.getType());
        attrBuilder.setBinding(clazz);

        //字段值长度
        if (clazz.equals(String.class)) {
            attrBuilder.setLength(fieldInfo.getLength());
        }

        //是否可为空
        attrBuilder.setNillable(fieldInfo.getNillable());

        //值上限
        if (fieldInfo.getMax() != null) {
            attrBuilder.setMaxOccurs(fieldInfo.getMax());
        }

        //值下限
        if (fieldInfo.getMin() != null) {
            attrBuilder.setMinOccurs(fieldInfo.getMin());
        }

        //默认值
        if (fieldInfo.getDefaultValue() != null) {
            attrBuilder.setDefaultValue(fieldInfo.getDefaultValue());
        }

        //描述
        if (fieldInfo.getDescription() != null) {
            attrBuilder.setDescription(fieldInfo.getDescription());
        }
        //endregion

        return attrBuilder.buildType();
    }

    /**
     * 创建几何字段类型
     * @param attrBuilder 属性字段类型构造器
     * @param fieldInfo 字段信息
     * @return org.opengis.feature.type.GeometryType
     */
    public GeometryType buildGeometryAttributeType(AttributeTypeBuilder attrBuilder, FieldInfo fieldInfo) {
        /**
         * <p>
         *     创建几何字段类型
         *     1.验证字段信息参数
         *     2.验证坐标系
         *     3.设置参数
         * </p>
         */

        //region 验证字段信息参数
        //判断builder
        if (attrBuilder == null) {
            return null;
        }

        //判断几何类型
        if (!Arrays.asList(geometryType).contains(fieldInfo.getType())) {
            return null;
        }

        //验证字段名称
        if (fieldInfo.getName() == null) {
            return null;
        }
        //endregion

        //region 验证坐标系
        if (fieldInfo.getCrsCode() == null || fieldInfo.getCrsCode().trim().length() == 0) {
            return null;
        }

        //pixels默认使用3857
        if (fieldInfo.getCrsCode().equals(S_CRS_PIXELS)) {
            fieldInfo.setCrsCode(S_CRS_EPSG_3857);
        }

        CoordinateReferenceSystem crs = readCRSFromCode(fieldInfo.getCrsCode());
        if (crs == null) {
            return null;
        }

        //endregion

        //region 设置构造参数
        attrBuilder.setName(fieldInfo.getName());
        Class clazz = fieldTypeToBinding(fieldInfo.getType());
        attrBuilder.setBinding(clazz);

        attrBuilder.setNillable(fieldInfo.getNillable());

        attrBuilder.setCRS(crs);

        if (fieldInfo.getDefaultValue() != null) {
            attrBuilder.setDefaultValue(fieldInfo.getDefaultValue());
        }

        if (fieldInfo.getDescription() != null) {
            attrBuilder.setDescription(fieldInfo.getDescription());
        }
        //endregion

        return attrBuilder.buildGeometryType();
    }


    /**
     *
     * @param attrBuilder
     * @param fieldInfo
     * @return
     */
    public AttributeDescriptor buildAttributeDescriptor(AttributeTypeBuilder attrBuilder, FieldInfo fieldInfo) {
        if (attrBuilder == null) {
            return null;
        }
        AttributeType attributeType = buildAttributeType(attrBuilder, fieldInfo);
        if (attributeType == null) {
            return null;
        }
        return attrBuilder.buildDescriptor(fieldInfo.getName(), attributeType);

    }

    /**
     * 构建几何字段描述
     * @param attrBuilder 属性构造器
     * @param fieldInfo 字段信息
     * @return org.opengis.feature.type.GeometryDescriptor
     */
    public GeometryDescriptor buildGeometryDescriptor(AttributeTypeBuilder attrBuilder, FieldInfo fieldInfo) {
        if (attrBuilder == null) {
            return null;
        }

        GeometryType geometryType = buildGeometryAttributeType(attrBuilder, fieldInfo);
        if (geometryType == null) {
            return null;
        }

        return attrBuilder.buildDescriptor(fieldInfo.getName(), geometryType);
    }

    /**
     * 根据EPSG编码获取CRS
     * @param code epsg编码
     * @return org.opengis.referencing.crs.CoordinateReferenceSystem
     */
    public CoordinateReferenceSystem readCRSFromCode(String code) {
        try{

            // 设置经度为first axis
            Hints hints = new Hints(Hints.FORCE_LONGITUDE_FIRST_AXIS_ORDER, Boolean.TRUE);

            //获取CRS
            CRSAuthorityFactory factory = ReferencingFactoryFinder.getCRSAuthorityFactory("EPSG", hints);
            CoordinateReferenceSystem crs = factory.createCoordinateReferenceSystem(code);
            return crs;
        }catch (FactoryException fe) {
            log.error("read CRS error [{}]", code, fe);
        }

        return null;

    }

    //endregion

    //region Assist methods

    /**
     * 字段类型 binding class
     * @param type 字段类型
     * @return java.lang.Class
     */
    public static Class fieldTypeToBinding(String type) {
        switch (type.toLowerCase()) {
            case S_BINDING_STRING:
                return String.class;
            case S_BINDING_LONG :
                return Long.class;
            case S_BINDING_INTEGER :
                return Integer.class;
            case S_BINDING_DOUBLE :
                return Double.class;
            case S_BINDING_FLOAT:
                return Float.class;
            case S_BINDING_DATE:
                return Date.class;
            case S_BINDING_POINT:
                return Point.class;
            case S_BINDING_MULTIPOINT:
                return MultiPoint.class;
            case S_BINDING_LINESTRING :
                return LineString.class;
            case S_BINDING_MULTILINESTRING:
                return MultiLineString.class;
            case S_BINDING_POLYGON:
                return Polygon.class;
            case S_BINDING_MULTIPOLYGON:
                return MultiPolygon.class;
            case S_BINDING_GEOMETRY:
                return Geometry.class;
            case S_BINDING_ENVELOPE:
                return Envelope.class;
            default:
                return String.class;
        }
    }

    //endregion
    //endregion

}
