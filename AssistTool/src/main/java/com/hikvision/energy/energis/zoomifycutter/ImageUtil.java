package com.hikvision.energy.energis.zoomifycutter;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.imageio.ImageReadParam;
import javax.imageio.ImageReader;

import com.hikvision.energy.energis.zoomifycutter.image.DoubleUtil;
import com.hikvision.energy.energis.zoomifycutter.image.ImageFile;



public class ImageUtil {
    
    /** 默认的缩略图尺寸 */
    protected final static int DEFAULT_THUMBNAILS_SIZE = 200;

    protected final static boolean DEFAULT_KEEY_ASPECT_RATIO = true;
    /** 保存的缩略图名称 */
    public final static String THUMBNAILS_NAME = "thumbnails.jpg";
    /** 保存的缩略图的类型 */
    public final static String THUMBNAILS_TYPE = "jpg";
    
    /**
     * 生成指定宽高的缩略图
     * 
     * @param imageFile
     *            图片文件
     * @param outPut
     *            输出路径
     * @param fileName
     *            保存的文件名
     * @param width
     *            缩略图宽度
     * @param height
     *            缩略图高度
     * @param keepAspectRatio
     *            是否需要保持宽高比
     *            <p>
     *            为<code>false</code>的话缩略图需充满指定宽高的图片
     *            <p>
     *            反之则以原图片比例居中在指定宽高的图片中
     * @throws IOException 异常
     */
    public static String generate(ImageFile imageFile, String outPut, String fileName, int width, int height,
            boolean keepAspectRatio) throws IOException {
        // 原图片尺寸
        int imgW = imageFile.width, imgH = imageFile.height;
        String imgFormat = imageFile.formatName;
        System.out.println("imgW:" + imgW);
        System.out.println("imgH:" + imgH);

        // 处理文件名
        if (null == fileName) {
            fileName = THUMBNAILS_NAME;
        }
        String fullPath = outPut + File.separator + fileName;
        File thumbnailsFile = new File(fullPath);
        // 删除已存在文件
        if (thumbnailsFile.exists() && thumbnailsFile.isFile()) {
            thumbnailsFile.delete();
        }

        // 图片尺寸小于缩略图尺寸
        if (imgW <= width && imgH <= height) {
            ImageReader imageReader = imageFile.getImageReader();
            BufferedImage bufferedImage = imageReader.read(0);
            ImageIO.write(bufferedImage, "jpg", thumbnailsFile);
            bufferedImage.flush();
            imageReader.dispose();
        } else {
            // 要绘制的图像尺寸
            int graphicWidth = width, graphicHeight = height;
            // 图像起始绘制坐标
            int graphicX = 0, graphicY = 0;
            // 横向缩小比例
            double horScale = DoubleUtil.div(graphicWidth, imgW);
            // 纵向缩小比例
            double verScale = DoubleUtil.div(graphicHeight, imgH);

            // 需要保持图像宽高比，需依据比例计算宽高和图像起始坐标
            if (keepAspectRatio) {
                if (horScale > verScale) {
                    graphicWidth = (int) (imgW * verScale);
                    graphicX = (width - graphicWidth) / 2;
                    horScale = verScale;
                } else {
                    graphicHeight = (int) (imgH * horScale);
                    graphicY = (height - graphicHeight) / 2;
                    verScale = horScale;
                }
            }

            System.out.println("graphicWidth:" + graphicWidth);
            System.out.println("graphicHeight:" + graphicHeight);
            System.out.println("graphicX:" + graphicX);
            System.out.println("graphicY:" + graphicY);

            // 二次抽样系数
            int sourceXSubsampling = (int) Math.floor(1 / horScale);
            int sourceYSubsampling = (int) Math.floor(1 / verScale);
            if (sourceXSubsampling < 2) {
                sourceXSubsampling = 1;
            }
            if (sourceYSubsampling < 2) {
                sourceYSubsampling = 1;
            }

            System.out.println("sourceXSubsampling:" + sourceXSubsampling);
            System.out.println("sourceYSubsampling:" + sourceYSubsampling);

            ImageReader imageReader = imageFile.getImageReader();
            ImageReadParam imageReadParam = imageReader.getDefaultReadParam();
            imageReadParam.setSourceSubsampling(sourceXSubsampling, sourceYSubsampling, 0, 0);

            // 读取源图片
            BufferedImage source = imageReader.read(0, imageReadParam);
            // 创建目标图片
            BufferedImage destination = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            Graphics2D graphics2d = (Graphics2D) destination.getGraphics();
            // 底色设置为白色
            graphics2d.setColor(Color.WHITE);
            graphics2d.fillRect(0, 0, width, height);
            // 绘制图片
            graphics2d.drawImage(source, graphicX, graphicY, graphicX + graphicWidth, graphicY + graphicHeight, 0, 0,
                    source.getWidth(), source.getHeight(), null);
            // 释放资源
            graphics2d.dispose();
            // 保存文件
            ImageIO.write(destination, imgFormat, thumbnailsFile);
            // 释放资源
            destination.flush();
            source.flush();
            imageReader.dispose();
        }

        return fullPath;
    }
    /**
     * @Description:将缩放过的像素坐标转换为原图片大小的像素坐标
     * @author zhangguoping5
     * @date 创建时间：2015年12月26日 下午12:59:52
     * @version 1.0
     * @since
     * @param sourceParam
     * @param zoomLevel
     * @param maxZoomLevel
     * @return
     */
    public static int transformGisToPix(int sourceParam,int zoomLevel,int maxZoomLevel){
        int pixValue = (int) (sourceParam*Math.pow(1.1, (maxZoomLevel-zoomLevel-1)));
        return pixValue;
    }
}
