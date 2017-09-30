package com.hikvision.energy.energis.zoomifycutter.image;

import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;

/**
 * 要做切图的原始图片信息
 *
 */
public class ImageFile {

	/** 图片宽度 */
	public Integer width;
	/** 图片高度 */
	public Integer height;
	/** 图片类型 */
	public String formatName;
	/** 输入的图片流，在使用完后需要调用close()关闭 */
	private File imageFile = null;
	private ImageInputStream inputStream = null;

	public ImageFile(File imageFile) throws IOException {
		this.imageFile = imageFile;
		ImageReader imageReader = getImageReader();
		// 读取图片基本信息
		this.width = imageReader.getWidth(0);
		this.height = imageReader.getHeight(0);
		this.formatName = imageReader.getFormatName();
		// 释放资源
		imageReader.dispose();
	}

	/**
	 * 图片操作完成后需关闭流
	 * 
	 * @throws IOException
	 */
	public void dispose() throws IOException {
		if (null != this.inputStream) {
			this.inputStream.close();
		}
	}

	/**
	 * 获得解析和解码该图像的超类
	 * <p>
	 * 使用完毕后需做<code>dispose()</code>释放资源
	 * 
	 * @return <code>ImageReader</code>的一个实例，用于读取解析和解码传入的图片
	 * @throws IOException
	 */
	public synchronized ImageReader getImageReader() throws IOException {
		inputStream = ImageIO.createImageInputStream(imageFile);
		ImageReader imageReader = ImageIO.getImageReaders(this.inputStream).next();
		imageReader.setInput(this.inputStream, false);
		return imageReader;
	}

}
