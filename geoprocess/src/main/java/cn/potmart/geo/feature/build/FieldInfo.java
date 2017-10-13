package cn.potmart.geo.feature.build;

/**
 * <p>
 *     描述要素字段详细信息
 * </p>
 *
 * Created by GOT.hodor on 2017/9/11.
 */
public class FieldInfo {

    /**
     * 字段名
     */
    private String name;

    /**
     * 字段类型
     */
    private String type;

    /**
     * 字段值可否为空
     */
    private Boolean nillable = true;

    /**
     * 字段值长度
     */
    private Integer length = 1;

    /**
     * 字段值范围上限
     */
    private Integer max;

    /**
     * 字段值范围下限
     */
    private Integer min;

    /**
     * 字段描述
     */
    private String description;

    /**
     * 字段默认值
     */
    private Object defaultValue;

    /**
     * 坐标系编码
     */
    private String crsCode = "EPSG:3857";

    /**
     * 字段名
     * @return
     */
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * 字段类型
     * @return
     */
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    /**
     * 是否可为空
     * @return
     */
    public Boolean getNillable() {
        return nillable;
    }

    public void setNillable(Boolean nillable) {
        this.nillable = nillable;
    }

    /**
     * 值长度 (String)
     * @return
     */
    public Integer getLength() {
        return length;
    }

    public void setLength(Integer length) {
        this.length = length;
    }

    /**
     * 值上限
     * @return
     */
    public Integer getMax() {
        return max;
    }

    public void setMax(Integer max) {
        this.max = max;
    }

    /**
     * 值下限
     * @return
     */
    public Integer getMin() {
        return min;
    }

    public void setMin(Integer min) {
        this.min = min;
    }

    /**
     * 描述
     * @return
     */
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * 默认值
     * @return
     */
    public Object getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(Object defaultValue) {
        this.defaultValue = defaultValue;
    }

    /**
     * 坐标系
     * @return
     */
    public String getCrsCode() {
        return crsCode;
    }

    public void setCrsCode(String crsCode) {
        this.crsCode = crsCode;
    }
}
