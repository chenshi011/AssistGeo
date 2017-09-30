package com.hikvision.energy.energis.zoomifycutter.image;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.hikvision.energy.energis.zoomifycutter.image.tile.TileInfo;
import com.hikvision.energy.energis.zoomifycutter.image.tile.TileInfo.TileLod;

/**
 * 切图入口类
 *
 */
public class ImageCutter {

	/**
	 * 图片默认缩放比例
	 * <p>
	 * 即做多级切图时，需对图片做逐级缩小，每级相对于上一级的宽高比为 1/DEFAULT_SCALING
	 */
	protected static final double DEFAULT_SCALING = 2;

	/**
	 * 对图片被缩小的默认最小尺寸
	 * <p>
	 * 对图片做缩小操作，当缩小至小于一定尺寸时为止
	 */
	protected static final int DEFAULT_MIN_SIZE = 256;

	/**
	 * 按照默认参数确定切图层级后切图
	 * 
	 * @param file
	 *            图片文件
	 * @param output
	 *            瓦片输出路径
	 * @throws IOException
	 */
	public static TileInfo cutImage(File file, String output) throws IOException {
		return cutImage(file, output, DEFAULT_SCALING);
	}

	/**
	 * 指定缩放比例，确定切图层级后进行切图
	 * 
	 * @param file
	 *            图片文件
	 * @param output
	 *            瓦片输出路径
	 * @param scaling
	 *            多层级切片，图片缩放比例
	 * @throws IOException
	 */
	public static TileInfo cutImage(File file, String output, double scaling) throws IOException {
		ImageFile imageFile = new ImageFile(file);
		//计算切图级别及切图比例尺
		List<TileLod> tileLods = computeDefaultTileLods(imageFile.width, imageFile.height, scaling);
		return cutImage(imageFile, output, tileLods);
	}

	/**
	 * 指定缩放比例和缩放次数，确定切图层级后进行切图
	 * 
	 * @param file
	 *            图片文件
	 * @param output
	 *            瓦片输出路径
	 * @param scaling
	 *            图片缩放比例
	 * @param enlargeTimes
	 *            图片被放大的次数，为0则表示不做放大
	 * @param narrowTimes
	 *            图片被缩小的次数，为0则表示不做缩小
	 * @throws IOException
	 */
	public static TileInfo cutImage(File file, String output, double scaling, int enlargeTimes, int narrowTimes)
			throws IOException {

		List<TileLod> tileLods = new ArrayList<TileLod>();
		for (int i = 0; i < narrowTimes; i++) {
			tileLods.add(new TileLod(i, DoubleUtil.pow(scaling, narrowTimes - i)));
		}
		// 原图片比例尺
		tileLods.add(new TileLod(narrowTimes, TileLod.ORIGINAL_SCALE));
		for (int i = 1; i <= enlargeTimes; i++) {
			tileLods.add(new TileLod(i + narrowTimes, DoubleUtil.pow(scaling, -i)));
		}

		return cutImage(file, output, tileLods);
	}

	/**
	 * 指定缩放比例和放大次数，缩小次数按照图片尺寸计算，确定切图层级后进行切图
	 * 
	 * @param file
	 *            图片文件
	 * @param output
	 *            瓦片输出路径
	 * @param scaling
	 *            图片缩放比例
	 * @param enlargeTimes
	 *            图片被放大的次数，为0则表示不做放大
	 * @throws IOException
	 */
	public static TileInfo cutImage(File file, String output, double scaling, int enlargeTimes) throws IOException {
		if (enlargeTimes <= 0) {
			return cutImage(file, output, scaling);
		}
		ImageFile imageFile = new ImageFile(file);

		List<TileInfo.TileLod> tileLods = computeDefaultTileLods(imageFile.width, imageFile.height, scaling);
		for (int i = 1; i <= enlargeTimes; i++) {
			tileLods.add(new TileInfo.TileLod(tileLods.size(), DoubleUtil.pow(scaling, -i)));
		}

		return cutImage(imageFile, output, tileLods);
	}

	/**
	 * 指定切图层级信息进行切图
	 * 
	 * @param file
	 *            图片文件
	 * @param output
	 *            瓦片输出路径
	 * @param tileLods
	 *            切图层级信息
	 * @throws IOException
	 */
	public static TileInfo cutImage(File file, String output, List<TileLod> tileLods) throws IOException {
		ImageFile imageFile = new ImageFile(file);
		return cutImage(imageFile, output, tileLods);
	}

	/**
	 * 执行分块并切图
	 * 
	 * @param imageFile
	 * @param output
	 * @param tileLods
	 * @throws IOException
	 */
	protected static TileInfo cutImage(ImageFile imageFile, String output, List<TileLod> tileLods) throws IOException {
		File outputFile = new File(output);
		if (!outputFile.exists() || !outputFile.isDirectory()) {
			outputFile.mkdirs();
		}

		TileInfo tileInfo = new TileInfo();
		tileInfo.setOutput(output);

		tileInfo.setLods(tileLods);

		//设置静态地图初始显示范围
		int imgW = imageFile.width, imgH = imageFile.height;
		tileInfo.setInitialExtent(DoubleUtil.mul(imgW, TileLod.ORIGINAL_RESOLUTION),
				-DoubleUtil.mul(imgH, TileLod.ORIGINAL_RESOLUTION));
		//设置静态地图边界范围
		tileInfo.setFullExtent(DoubleUtil.mul(imgW, TileLod.ORIGINAL_RESOLUTION),
				-DoubleUtil.mul(imgH, TileLod.ORIGINAL_RESOLUTION));

		//--------计算group id的数组--------
		List<TileLod> tierSizeInTiles = new ArrayList<>();
		int tileSize = 256;
		while (imgW > tileSize || imgH > tileSize) {
			TileLod tileLod = new TileLod();
			tileLod.setTileX(Math.ceil(imgW / tileSize +1));
			tileLod.setTileY(Math.ceil(imgH / tileSize +1));
			tierSizeInTiles.add(tileLod);

			tileSize += tileSize;
		}

		TileLod lod = new TileLod();
		lod.setTileX(1D);
		lod.setTileY(1D);
		tierSizeInTiles.add(lod);
		Collections.reverse(tierSizeInTiles);

		double[] tileCountUpToTiler = new double[tierSizeInTiles.size()];
		tileCountUpToTiler[0] = 0;


		for (int i =1; i < tierSizeInTiles.size(); i++) {
			double count = tierSizeInTiles.get(i-1).getTileX().doubleValue() * tierSizeInTiles.get(i-1).getTileY().doubleValue()
					+ tileCountUpToTiler[i-1];
			tileCountUpToTiler[i] = count;
		}

		tileInfo.setTierSize(tierSizeInTiles);
		tileInfo.setTileCount(tileCountUpToTiler);

		//----------------------------------


		// 执行切片
		ImageBlocker.cutBlocks(imageFile, tileInfo, true);
		System.out.println("Cut Image end!");
		// 生成切图数据xml
		tileInfo.saveXML();
		// 生成缩略图
		//Thumbnails.generate(imageFile, output, null);
		//Thumbnails.generate(imageFile, output, null, 350, 210, true);
		int height = (1242*imageFile.height)/imageFile.width.intValue();//高宽比例，手机APP要求宽安装1242来计算
		Thumbnails.generate(imageFile, output, null, 1242, height, true);
		return tileInfo;
	}

	/**
	 * 根据原图片尺寸按照默认配置计算切片的层级信息
	 * 
	 * @param imageWidth
	 *            原图片的宽度
	 * @param imageHeight
	 *            原图片的高度
	 * @param scaling
	 *            多层级切片，图片缩放比例
	 * @return
	 */
	protected static List<TileLod> computeDefaultTileLods(int imageWidth, int imageHeight, double scaling) {
		int levels = 0;
		while (true) {
			if (imageWidth <= DEFAULT_MIN_SIZE && imageHeight <= DEFAULT_MIN_SIZE) {
				break;
			}
			imageWidth /= scaling;
			imageHeight /= scaling;
			levels++;
		}
		System.out.println("Cut levels : " + (levels + 1));

		List<TileLod> tileLods = new ArrayList<TileLod>();
		for (int i = 0; i <= levels; i++) {
			tileLods.add(new TileLod(i, DoubleUtil.pow(scaling, (levels - i))));
		}
		return tileLods;
	}

	public static void main(String[] args) throws IOException {

		cutImage(new File("D:/test/map/heren.png"), "D:/test/out");
	}

}
