package com.hikvision.energy.energis.zoomifycutter.image;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.imageio.ImageIO;
import javax.imageio.ImageReadParam;
import javax.imageio.ImageReader;

import com.hikvision.energy.energis.zoomifycutter.image.tile.TileInfo;
import com.hikvision.energy.energis.zoomifycutter.image.tile.TileInfo.TileLod;
import com.hikvision.energy.energis.zoomifycutter.image.ImageFile;

/**
 * 图片分块，大图片先经分块再做切片
 *
 */
public class ImageBlock implements Runnable {
	/** 图片信息 */
	protected ImageFile imageFile;
	/** 分块在图片中的起始坐标 */
	protected int blockX;
	protected int blockY;
	/** 分块尺寸 */
	protected int blockWidth;
	protected int blockHeight;
	/** 该分块的切片起始行列号 */
	protected int startRow;
	protected int startCol;

	/** 切片要求 */
	protected TileInfo tileInfo;
	/** 当前切片层级信息 */
	protected TileLod tileLod;

	/** 二次抽样系数 */
	private int sourceSubsampling;
	/** 当前比例尺下瓦片在分块中的尺寸 */
	private int scaleTileW;
	private int scaleTileH;

	public ImageBlock(ImageFile imageFile, int blockX, int blockY, int blockWidth, int blockHeight, int startRow,
			int startCol, TileInfo tileInfo, TileLod tileLod) {
		this.imageFile = imageFile;
		this.blockX = blockX;
		this.blockY = blockY;
		this.blockWidth = blockWidth;
		this.blockHeight = blockHeight;
		this.startRow = startRow;
		this.startCol = startCol;
		this.tileInfo = tileInfo;
		this.tileLod = tileLod;

		// 比例尺
		double scale = tileLod.getScale();
		this.sourceSubsampling = (int) Math.floor(scale);
		// 分块后瓦块的尺寸
		this.scaleTileW = (int) Math.ceil(tileInfo.getWidth() * scale);
		this.scaleTileH = (int) Math.ceil(tileInfo.getHeight() * scale);
		if (this.sourceSubsampling >= 2) {
			this.scaleTileW /= this.sourceSubsampling;
			this.scaleTileH /= this.sourceSubsampling;
		}
	}

	/**
	 * 执行分块
	 * 
	 * @throws IOException
	 */
	public BufferedImage block() throws IOException {
		ImageReader imageReader = imageFile.getImageReader();
		ImageReadParam imageReadParam = imageReader.getDefaultReadParam();
		// 设置分块范围
		Rectangle rectangle = new Rectangle(blockX, blockY, blockWidth, blockHeight);
		imageReadParam.setSourceRegion(rectangle);

		// 二次抽样
		if (sourceSubsampling >= 2) {
			imageReadParam.setSourceSubsampling(sourceSubsampling, sourceSubsampling, 0, 0);
		}

		System.out.println("Cut block —— x:" + blockX + " y:" + blockY + " width:" + blockWidth + " height:"
				+ blockHeight + " subsampling:" + sourceSubsampling);

		BufferedImage bufferedImage = imageReader.read(0, imageReadParam);
		imageReader.dispose();

		return bufferedImage;
	}

	/**
	 * 执行切片
	 * 
	 * @throws IOException
	 */
	public void tile() throws IOException {
		// 分块
		BufferedImage block = this.block();
		// 实际的分块尺寸
		int actualBlockW = block.getWidth(), actualBlockH = block.getHeight();
		// 分块可切出的切片行列数
		int tileRows = (int) Math.ceil((double) actualBlockH / scaleTileH);
		int tileCols = (int) Math.ceil((double) actualBlockW / scaleTileW);
		// 切片层级
		int level = tileLod.getLevel();

		// 最终切片尺寸
		int tileWidth = tileInfo.getWidth(), tileHeight = tileInfo.getHeight();
		// 切片图片类型
		String format = tileInfo.getFormat();



		// 保存切片的绝对路径
		int tileIndex = 0;
		String tileOutPath = tileInfo.getOutput() + File.separator + TileInfo.TILE_FOLDER + tileIndex;
		// 创建路径
		File outPathFile = new File(tileOutPath);
		if (!outPathFile.exists() || !outPathFile.isDirectory()) {
			outPathFile.mkdirs();
		}

		double[] tileCount = tileInfo.getTileCount();
		List<TileLod> tierSize = tileInfo.getTierSize();

		// 逐行逐列进行切片
		for (int tileRow = 0; tileRow < tileRows; tileRow++) {
			int currTileIndex = 0;
			for (int tileCol = 0; tileCol < tileCols; tileCol++) {
				// 切片在分块中的位置
				int tileX = tileCol * scaleTileW, tileY = tileRow * scaleTileH;
				// 计算切片实际尺寸
				int actualTileW = scaleTileW, actualTileH = scaleTileH;
				if (tileX + actualTileW > actualBlockW) {
					actualTileW = actualBlockW - tileX;
				}
				if (tileY + actualTileH > actualBlockH) {
					actualTileH = actualBlockH - tileY;
				}

				// 创建切片对象
				BufferedImage bufferedImage = new BufferedImage(tileWidth, tileHeight, BufferedImage.TYPE_INT_RGB);
				Graphics2D graphics2d = (Graphics2D) bufferedImage.getGraphics();
				// 底色设置为白色
				graphics2d.setColor(Color.WHITE);
				graphics2d.fillRect(0, 0, tileWidth, tileHeight);
				// 绘制切片
				graphics2d.drawImage(block, 0, 0, (int) (tileWidth * ((double) actualTileW / scaleTileW)),
						(int) (tileHeight * ((double) actualTileH / scaleTileH)), tileX, tileY, tileX + actualTileW,
						tileY + actualTileH, null);
				graphics2d.dispose();
				// 构造切片文件保存路径
				String tileName = level + "-" + (startCol + tileCol) + "-" + (startRow + tileRow) + "." + format;

				//计算group的id号
				currTileIndex = (tileCol + Double.valueOf(tileRow * tierSize.get(level).getTileX() + tileCount[level]).intValue()) / 256;
				if (currTileIndex != tileIndex) {
					tileOutPath = tileInfo.getOutput() + File.separator + TileInfo.TILE_FOLDER + currTileIndex;
					outPathFile = new File(tileOutPath);
					if (!outPathFile.exists() || !outPathFile.isDirectory()) {
						outPathFile.mkdirs();
					}
					tileIndex = currTileIndex;
				}
				String savePath = tileOutPath + File.separator + tileName;

				File imgFile = new File(savePath);
				if (imgFile.exists() && imgFile.isFile()) {
					imgFile.delete();
				}
				// 保存文件
				ImageIO.write(bufferedImage, format, imgFile);
				// 释放资源
				bufferedImage.flush();
			}
		}
		block.flush();

	}


	public void run() {
		try {
			tile();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
