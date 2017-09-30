package com.hikvision.energy.energis.zoomifycutter.image.tile;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.XMLWriter;

import java.beans.Transient;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.hikvision.energy.energis.zoomifycutter.image.DoubleUtil;

public class TileInfo implements Serializable {
    public static final String TILE_FOLDER = "TileGroup";
    private static final long serialVersionUID = -5320711371378608850L;
    private String pkid;
    private Integer width = Integer.valueOf(256);

    private Integer height = Integer.valueOf(256);

    private String format = "png";

    private Integer srid = Integer.valueOf(3857);

    private List<TileLod> lods = new ArrayList<TileLod>();

    private Double[] fullExtent = new Double[4];

    private Double[] initialExtent = new Double[4];
    private String output;
    private String tiledServer;

    private double[] tileCount;

    private List<TileLod> tierSize;

    public JSONObject toJSON() {
        JSONObject json = new JSONObject();
        json.put("pkid", this.pkid);
        json.put("rows", this.width);
        json.put("cols", this.height);
        json.put("format", this.format);
        json.put("srid", this.srid);
        json.put("lods", this.lods);

        JSONObject fullExtentJSON = new JSONObject();
        fullExtentJSON.put("xmin", this.fullExtent[0]);
        fullExtentJSON.put("ymin", this.fullExtent[1]);
        fullExtentJSON.put("xmax", this.fullExtent[2]);
        fullExtentJSON.put("ymax", this.fullExtent[3]);
        json.put("fullExtent", fullExtentJSON);

        JSONObject initialExtentJSON = new JSONObject();
        initialExtentJSON.put("xmin", this.initialExtent[0]);
        initialExtentJSON.put("ymin", this.initialExtent[1]);
        initialExtentJSON.put("xmax", this.initialExtent[2]);
        initialExtentJSON.put("ymax", this.initialExtent[3]);
        json.put("initialExtent", initialExtentJSON);

        json.put("output", this.output);
        json.put("tiledServer", this.tiledServer);
        return json;
    }

    public TileInfo() {
    }

    public TileInfo(String pkid, Integer width, Integer height, String format, Integer srid, List<TileLod> lods,
            Double[] fullExtent, Double[] initialExtent, String output, String tiledServer) {
        this.pkid = pkid;
        this.width = width;
        this.height = height;
        this.format = format;
        this.srid = srid;
        this.lods = lods;
        this.fullExtent = fullExtent;
        this.initialExtent = initialExtent;
        this.output = output;
        this.tiledServer = tiledServer;
    }

    public String getPkid() {
        return this.pkid;
    }

    public void setPkid(String pkid) {
        this.pkid = pkid;
    }

    public Integer getWidth() {
        return this.width;
    }

    public void setWidth(Integer width) {
        this.width = width;
    }

    public Integer getHeight() {
        return this.height;
    }

    public void setHeight(Integer height) {
        this.height = height;
    }

    public String getFormat() {
        return this.format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public Integer getSrid() {
        return this.srid;
    }

    public void setSrid(Integer srid) {
        this.srid = srid;
    }

    public String getLods() {
        return JSONObject.toJSONString(this.lods);
    }

    
    @SuppressWarnings("unchecked")
    public void setLods(String lods) {
        this.lods = ((List<TileLod>) JSONArray.parseArray(lods, TileLod.class));
    }

    public List<TileLod> getLodsList() {
        return this.lods;
    }

    @Transient
    public void setLods(List<TileLod> lods) {
        this.lods = lods;
    }

    public String getFullExtent() {
        return JSONArray.toJSONString(this.fullExtent);
    }

    @Transient
    public Double[] getFullExtentArray() {
        return this.fullExtent;
    }

    public void setFullExtent(String fullExtent) {
        JSONArray array = JSONArray.parseArray(fullExtent);
        int arraySize = array.size();
        this.fullExtent = new Double[arraySize];
        for (int i = 0; i < arraySize; i++)
            this.fullExtent[i] = Double.valueOf(array.getDouble(i));
    }

    public void setFullExtent(Double[] fullExtent) {
        this.fullExtent = fullExtent;
    }

    public void setFullExtent(Double xmax, Double ymin) {
        this.fullExtent = new Double[] { Double.valueOf(0.0D), ymin, xmax, Double.valueOf(0.0D) };
    }

    public String getInitialExtent() {
        return JSONArray.toJSONString(this.initialExtent);
    }

    @Transient
    public Double[] getInitialExtentArray() {
        return this.initialExtent;
    }

    public void setInitialExtent(String initialExtent) {
        JSONArray array = JSONArray.parseArray(initialExtent);
        int arraySize = array.size();
        this.initialExtent = new Double[arraySize];
        for (int i = 0; i < arraySize; i++)
            this.initialExtent[i] = Double.valueOf(array.getDouble(i));
    }

    public void setInitialExtent(Double[] initialExtent) {
        this.initialExtent = initialExtent;
    }

    public void setInitialExtent(Double xmax, Double ymin) {
        this.initialExtent = new Double[] { Double.valueOf(0.0D), ymin, xmax, Double.valueOf(0.0D) };
    }

    public String getOutput() {
        return this.output;
    }

    public void setOutput(String output) {
        this.output = output;
    }

    public String getTiledServer() {
        return this.tiledServer;
    }

    public void setTiledServer(String tiledServer) {
        this.tiledServer = tiledServer;
    }

    public double[] getTileCount() {
        return tileCount.clone();
    }

    public void setTileCount(double[] tileCount) {
        this.tileCount = tileCount.clone();
    }

    public List<TileLod> getTierSize() {
        return tierSize;
    }

    public void setTierSize(List<TileLod> tierSize) {
        this.tierSize = tierSize;
    }

    public String toString() {
        return "TileInfo [pkid=" + this.pkid + ", width=" + this.width + ", height=" + this.height + ", format="
                + this.format + ", srid=" + this.srid + ", lods=" + this.lods + ", fullExtent="
                + Arrays.toString(this.fullExtent) + ", initialExtent=" + Arrays.toString(this.initialExtent)
                + ", output=" + this.output + ", tiledServer=" + this.tiledServer + "]";
    }

    public int hashCode() {
        @SuppressWarnings("unused")
        int prime = 31;
        int result = 1;
        result = 31 * result + (this.pkid == null ? 0 : this.pkid.hashCode());
        return result;
    }

    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        TileInfo other = (TileInfo) obj;
        if (this.pkid == null) {
            if (other.pkid != null)
                return false;
        } else if (!this.pkid.equals(other.pkid))
            return false;
        return true;
    }

    public void saveXML() throws IOException {
        Document document = DocumentHelper.createDocument();
        Element tileInfoElement = document.addElement("tileInfo");

        if (this.tiledServer != null) {
            Element serverElement = tileInfoElement.addElement("tiledServer");
            serverElement.setText(this.tiledServer);
        }

        if (this.output != null) {
            Element outputElement = tileInfoElement.addElement("output");
            outputElement.setText(this.output);
        }

        Element rowsElement = tileInfoElement.addElement("rows");
        rowsElement.setText(this.width.toString());

        Element colsElement = tileInfoElement.addElement("cols");
        colsElement.setText(this.height.toString());

        Element formatElement = tileInfoElement.addElement("format");
        formatElement.setText(this.format);

        Element srElement = tileInfoElement.addElement("spatialReference");
        Element wkidElement = srElement.addElement("wkid");
        wkidElement.setText(this.srid.toString());

        Element lodsElement = tileInfoElement.addElement("lods");
        for (TileLod tileLod : this.lods) {
            Element tileLodElement = lodsElement.addElement("lod");
            Element lodElement = tileLodElement.addElement("level");
            lodElement.setText(tileLod.getLevel().toString());

            Element resolutionElement = tileLodElement.addElement("resolution");
            resolutionElement.setText(tileLod.getResolution().toString());

            Element scaleElement = tileLodElement.addElement("scale");
            scaleElement.setText(tileLod.getScale().toString());
        }

        Element extentElement = tileInfoElement.addElement("initialExtent");
        Element xminElement = extentElement.addElement("xmin");
        xminElement.setText(this.initialExtent[0].toString());
        Element yminElement = extentElement.addElement("ymin");
        yminElement.setText(this.initialExtent[1].toString());
        Element xmaxElement = extentElement.addElement("xmax");
        xmaxElement.setText(this.initialExtent[2].toString());
        Element ymaxElement = extentElement.addElement("ymax");
        ymaxElement.setText(this.initialExtent[3].toString());

        Element extentElement1 = tileInfoElement.addElement("fullExtent");
        Element xminElement1 = extentElement1.addElement("xmin");
        xminElement1.setText(this.fullExtent[0].toString());
        Element yminElement1 = extentElement1.addElement("ymin");
        yminElement1.setText(this.fullExtent[1].toString());
        Element xmaxElement1 = extentElement1.addElement("xmax");
        xmaxElement1.setText(this.fullExtent[2].toString());
        Element ymaxElement1 = extentElement1.addElement("ymax");
        ymaxElement1.setText(this.fullExtent[3].toString());

        XMLWriter xmlWriter = new XMLWriter(new FileWriter(this.output + File.separator+"tileInfo.xml"));
        xmlWriter.write(document);
        xmlWriter.close();
    }

    public static class TileLod implements Serializable{
        
        private static final long serialVersionUID = 4652644573260040308L;

        public static final Double ORIGINAL_SCALE = Double.valueOf(1.0D);

        public static final Double ORIGINAL_RESOLUTION = Double.valueOf(1.0D);
        private Integer level;
        private Double resolution;
        private Double scale;

        private Double tileX;
        private Double tileY;

        public TileLod() {
        }

        public TileLod(Integer level, Double scale) {
            this.level = level;
            this.scale = scale;
            this.resolution = Double
                    .valueOf(DoubleUtil.round(DoubleUtil.mul(ORIGINAL_RESOLUTION, scale).doubleValue()));
        }

        public TileLod(Integer level, Double resolution, Double scale) {
            this.level = level;
            this.resolution = resolution;
            this.scale = scale;
        }

        public String toString() {
            return "TileLod [level=" + this.level + ", resolution=" + this.resolution + ", scale=" + this.scale + "]";
        }

        public int hashCode() {
            @SuppressWarnings("unused")
            int prime = 31;
            int result = 1;
            result = 31 * result + (this.level == null ? 0 : this.level.hashCode());
            return result;
        }

        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            TileLod other = (TileLod) obj;
            if (this.level == null) {
                if (other.level != null)
                    return false;
            } else if (!this.level.equals(other.level))
                return false;
            return true;
        }

        public Integer getLevel() {
            return this.level;
        }

        public void setLevel(Integer level) {
            this.level = level;
        }

        public Double getResolution() {
            return this.resolution;
        }

        public void setResolution(Double resolution) {
            this.resolution = resolution;
        }

        public Double getScale() {
            return this.scale;
        }

        public void setScale(Double scale) {
            this.scale = scale;
        }

        public Double getTileX() {
            return tileX;
        }

        public void setTileX(Double tileX) {
            this.tileX = tileX;
        }

        public Double getTileY() {
            return tileY;
        }

        public void setTileY(Double tileY) {
            this.tileY = tileY;
        }


    }
}