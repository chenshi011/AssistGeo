package com.hikvision.energy.energis.zoomifycutter.image;

import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.hikvision.energy.energis.zoomifycutter.image.tile.TileInfo;
import com.hikvision.energy.energis.zoomifycutter.image.tile.TileInfo.TileLod;

/**
 * 多线程执行图片分块
 * <p>
 * 分块时间占用较长，采用多线程，提高效率
 *
 */
public class ImageBlocker {

	/** 能承受的最大分块的分辨率为两千万像素 */
	protected static final Integer MAX_BLOCK_RESOLUTION = 20000000;
	/** 获得计算机核心数，设置切片线程数 */
	protected static int processors = Runtime.getRuntime().availableProcessors();
	/** 创建执行分块的线程池 */
	protected static ThreadPoolExecutor cutThreadPool = null;
	static {
		// 为避免对磁盘造成过大压力，限定切片线程最大为4
		if (processors > 4) {
			processors = 4;
		}
		cutThreadPool = new ThreadPoolExecutor(processors, processors, 0, TimeUnit.SECONDS,
				new LinkedBlockingQueue<Runnable>());
	}

	/**
	 * 将传入的图片文件按照设定的多个层级依次进行分块
	 * 
	 * @param imageFile
	 *            图片文件
	 * @param tileInfo
	 *            切片配置信息
	 * @param sync
	 *            是否同步，即等待全部切图完成后结束方法
	 */
	public synchronized static void cutBlocks(ImageFile imageFile, TileInfo tileInfo, Boolean sync) {
		// 图片尺寸
		int imgW = imageFile.width, imgH = imageFile.height;
		// 配置的瓦片尺寸
		int tileW = tileInfo.getWidth(), tileH = tileInfo.getHeight();
		// 切片层级信息
		List<TileLod> tileLods = tileInfo.getLodsList();
		int lodSize = tileLods.size();
		// 逐个比例尺进行分块
		//从原始图片开始，逐级缩小切图
		for (int i = 0; i < lodSize; i++) {
			TileLod tileLod = tileLods.get(lodSize-1 - i);
			double scale = tileLod.getScale();
			// 当前比例尺下一个瓦片所代表的原始图片的尺寸大小
			int scaleTileW = (int) Math.ceil(tileW * scale);
			int scaleTileH = (int) Math.ceil(tileH * scale);
			// 最佳分块尺寸
			int[] blockSize = computeBlockSize(imgW, imgH, scaleTileW, scaleTileH, scale);
			int blockW = blockSize[0], blockH = blockSize[1];
			// 原图片可被分块的行列数
			int blockRows = (int) Math.ceil((double) imgH / blockH);
			int blockCols = (int) Math.ceil((double) imgW / blockW);
			// 每个分块下最多可切片的行列数
			int blockTileRows = (int) Math.ceil((double) blockH / scaleTileH);
			int blockTileCols = (int) Math.ceil((double) blockW / scaleTileW);
			// 逐行逐列进行分块切片
			for (int blockRow = 0; blockRow < blockRows; blockRow++) {
				for (int blockCol = 0; blockCol < blockCols; blockCol++) {
					// 各分块在原图片中的起始坐标位置
					int blockX = blockCol * blockW, blockY = blockRow * blockH;
					// 确定实际分块尺寸，图片边缘处可能不足一个分块
					int blockWidth = blockW, blockHeight = blockH;
					if (blockX + blockWidth > imgW) {
						blockWidth = imgW - blockX;
					}
					if (blockY + blockHeight > imgH) {
						blockHeight = imgH - blockY;
					}
					// 分块中的切片起始行列号
					int startRow = blockRow * blockTileRows, startCol = blockCol * blockTileCols;
					// 执行分块切片
					ImageBlock imageBlock = new ImageBlock(imageFile, blockX, blockY, blockWidth, blockHeight,
							startRow, startCol, tileInfo, tileLod);
					cutThreadPool.execute(imageBlock);
				}
			}
		}

		if (sync) {
			// 等待所有分块切图切完
			while (true) {
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				} finally {
					if (cutThreadPool.getActiveCount() <= 0) {
						break;
					}
				}
			}
		}

	}

	/**
	 * 计算一定比例尺下的图片的最佳分块尺寸
	 * <p>
	 * 在允许的最大分块分辨率之内，尽量减少分块数，同时避免过多列数可提高效率
	 * <p>
	 * 计算所得分块的高度应尽量为<code>tileH</code>的整数倍，可避免边缘处需要做拼接
	 * <p>
	 * 当<code>scale</code>值超过2时，可在分块的同时做二次抽样计算，减小图片尺寸
	 * 
	 * @param imgW
	 *            原图片宽度
	 * @param imgH
	 *            原图片高度
	 * @param scaleTileW
	 *            当前比例尺下瓦片在原图片中的宽度
	 * @param scaleTileH
	 *            当前比例尺下瓦片在原图片中的高度
	 * @param scale
	 *            要缩放的比例尺
	 * @return 计算出来的分块的宽高组成的数组
	 */
	protected static int[] computeBlockSize(int imgW, int imgH, int scaleTileW, int scaleTileH, double scale) {
		// 二次抽样系数
		int sourceSubsampling = (int) Math.floor(scale);

		long subImgW = imgW, subImgH = imgH;
		int subTileW = scaleTileW, subTileH = scaleTileH;
		// 大于2时先做抽样将图片缩小，切片尺寸做相同比例缩小
		if (sourceSubsampling >= 2) {
			subImgW /= sourceSubsampling;
			subImgH /= sourceSubsampling;
			subTileW /= sourceSubsampling;
			subTileH /= sourceSubsampling;
		}

		// 图片尺寸在范围以内，直接以全图为分块
		if (subImgW * subImgH <= MAX_BLOCK_RESOLUTION) {
			return new int[] { imgW, imgH };
		}

		// 取图片宽度为分块宽度，瓦片高度的最大整数倍作为分块高度
		if (subImgW * subTileH <= MAX_BLOCK_RESOLUTION) {
			// MAX_BLOCK_RESOLUTION / subImgW 得到最大高度，再 /subTileH计算得最大倍数
			return new int[] { imgW, (int) (scaleTileH * (MAX_BLOCK_RESOLUTION / subImgW / subTileH)) };
		}

		// 依次增加行列数直至分辨率达到最大
		int times = 2;
		while (subTileW * subTileH * DoubleUtil.pow(times, 2) <= MAX_BLOCK_RESOLUTION) {
			times++;
		}
		return new int[] { scaleTileW * times, scaleTileH * --times };
	}
}
