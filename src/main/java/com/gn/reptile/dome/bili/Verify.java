
package com.gn.reptile.dome.bili;

import java.awt.image.BufferedImage;

/**
 * @author
 */
public class Verify {
	public static void main(String[] args){
	}

	public static int getTX2(BufferedImage fristGif, BufferedImage secondGif) {
		// ת��ͼƬΪ�Ҷ�����
		int[][] fristArray = getImageGray(fristGif);
		int[][] secondArray = getImageGray(secondGif);
		// ��ò�������
		int[][] reArray = getReArray(fristArray, secondArray);
		// ������ұ߷������ұߵ�x����
		int TX2 = getTwoX2(reArray);
		//���47��ʵ��ó�
		return TX2-47;
	}

	private static int getTwoX2(int[][] reArray) {
		// �������ұ߷������ұߵ�x����
		int gifWidth = reArray[0].length;// 260
		int k=0;
		//��������ɨ��
		for (int i = gifWidth - 1; i >= 0; i--) {
			int sum = 0;
			for (int[] aReArray : reArray) {
				if (aReArray[i] == 0) {
					sum++;
				}
			}
			//������������еĺڵ�������20���Ҿ���Ϊ������ȱ��λ��
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
		// ��ò�������
		int gifHeight = fristArray.length;// 116
		int gifWidth = fristArray[0].length;// 260
		int[][] reArray = new int[gifHeight][gifWidth];
		for (int i = 0; i < gifHeight; i++) {
			for (int j = 0; j < gifWidth; j++) {
				reArray[i][j] = Integer.MAX_VALUE;
				//�������˵����ȱ��λ��
				if (Math.abs(fristArray[i][j] - secondArray[i][j]) > 3500000) {
				    //�����ص��Ϊ��ɫ
					reArray[i][j] = 0;
				}
			}
		}
		return reArray;
	}
	private static int[][] getImageGray(BufferedImage bufferedImage) {
		// BufferedImage�һ�������
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
		// RGB���һ�ֵ
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
