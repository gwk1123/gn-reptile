
package com.gn.reptile.dome.bili;

import java.awt.image.BufferedImage;

/**
 * @author
 */
public class Verify {
	public static void main(String[] args){
	}

	public static int getTX2(BufferedImage fristGif, BufferedImage secondGif) {
		// 转换图片为灰度数组
		int[][] fristArray = getImageGray(fristGif);
		int[][] secondArray = getImageGray(secondGif);
		// 获得差异数组
		int[][] reArray = getReArray(fristArray, secondArray);
		// 获得最右边方块最右边的x坐标
		int TX2 = getTwoX2(reArray);
		//这个47是实验得出
		return TX2-47;
	}

	private static int getTwoX2(int[][] reArray) {
		// 返回最右边方块最右边的x坐标
		int gifWidth = reArray[0].length;// 260
		int k=0;
		//从右往左扫描
		for (int i = gifWidth - 1; i >= 0; i--) {
			int sum = 0;
			for (int[] aReArray : reArray) {
				if (aReArray[i] == 0) {
					sum++;
				}
			}
			//如果连续三竖列的黑点数超过20，我就认为到达了缺口位置
			if (sum >= 20) {
				k++;
			}
			if(k==3) {
				return i;
			}
		}
		return 0;
	}

	private static int[][] getReArray(int[][] fristArray, int[][] secondArray) {
		// 获得差异数组
		int gifHeight = fristArray.length;// 116
		int gifWidth = fristArray[0].length;// 260
		int[][] reArray = new int[gifHeight][gifWidth];
		for (int i = 0; i < gifHeight; i++) {
			for (int j = 0; j < gifWidth; j++) {
				reArray[i][j] = Integer.MAX_VALUE;
				//差异过大，说明是缺口位置
				if (Math.abs(fristArray[i][j] - secondArray[i][j]) > 3500000) {
				    //将像素点变为黑色
					reArray[i][j] = 0;
				}
			}
		}
		return reArray;
	}
	private static int[][] getImageGray(BufferedImage bufferedImage) {
		// BufferedImage灰化到数组
		int result[][] = new int[bufferedImage.getHeight()][bufferedImage.getWidth()];
		for (int i = 0; i < bufferedImage.getWidth(); i++) {
			for (int j = 0; j < bufferedImage.getHeight(); j++) {
				final int color = bufferedImage.getRGB(i, j);
				final int r = (color >> 16) & 0xff;
				final int g = (color >> 8) & 0xff;
				final int b = color & 0xff;
				int gray = (int) (0.3 * r + 0.59 * g + 0.11 * b);
				int newPixel = colorToRGB(gray, gray, gray);
				result[j][i] = newPixel;
			}
		}
		return result;
	}
	private static int colorToRGB(int red, int green, int blue) {
		// RGB到灰化值
		int newPixel = 0;
		newPixel += 255;
		newPixel = newPixel << 8;
		newPixel += red;
		newPixel = newPixel << 8;
		newPixel += green;
		newPixel = newPixel << 8;
		newPixel += blue;
		return newPixel;
	}

}
