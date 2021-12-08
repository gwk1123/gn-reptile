package com.gn.reptile.dome.service.impl;

import com.sun.image.codec.jpeg.ImageFormatException;
import com.sun.image.codec.jpeg.JPEGCodec;
import com.sun.image.codec.jpeg.JPEGImageDecoder;
import com.sun.image.codec.jpeg.JPEGImageEncoder;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;
import java.io.*;

/**
 * ͼƬ�����࣬��Ҫ���ͼƬˮӡ����
 * 
 * @author  025079
 * @version  [�汾��, 2011-11-28]
 * @see  [�����/����]
 * @since  [��Ʒ/ģ��汾]
 */
public class ImageHelper {

	// ��Ŀ��Ŀ¼·��
	public static final String path = System.getProperty("user.dir");
	
	/**
	 * ��������ͼ <br/>
	 * ����:ImageIO.write(BufferedImage, imgType[jpg/png/...], File);
	 * 
	 * @param source
	 *            ԭͼƬ
	 * @param width
	 *            ����ͼ��
	 * @param height
	 *            ����ͼ��
	 * @param b
	 *            �Ƿ�ȱ�����
	 * */
	public static BufferedImage thumb(BufferedImage source, int width,
			int height, boolean b) {
		// targetW��targetH�ֱ��ʾĿ�곤�Ϳ�
		int type = source.getType();
		BufferedImage target = null;
		double sx = (double) width / source.getWidth();
		double sy = (double) height / source.getHeight();

		if (b) {
			if (sx > sy) {
				sx = sy;
				width = (int) (sx * source.getWidth());
			} else {
				sy = sx;
				height = (int) (sy * source.getHeight());
			}
		}

		if (type == BufferedImage.TYPE_CUSTOM) { // handmade
			ColorModel cm = source.getColorModel();
			WritableRaster raster = cm.createCompatibleWritableRaster(width,
					height);
			boolean alphaPremultiplied = cm.isAlphaPremultiplied();
			target = new BufferedImage(cm, raster, alphaPremultiplied, null);
		} else
			target = new BufferedImage(width, height, type);
		Graphics2D g = target.createGraphics();
		// smoother than exlax:
		g.setRenderingHint(RenderingHints.KEY_RENDERING,
				RenderingHints.VALUE_RENDER_QUALITY);
		g.drawRenderedImage(source, AffineTransform.getScaleInstance(sx, sy));
		g.dispose();
		return target;
	}

	/**
	 * ͼƬˮӡ
	 * 
	 * @param imgPath
	 *            ������ͼƬ
	 * @param markPath
	 *            ˮӡͼƬ
	 * @param x
	 *            ˮӡλ��ͼƬ���Ͻǵ� x ����ֵ
	 * @param y
	 *            ˮӡλ��ͼƬ���Ͻǵ� y ����ֵ
	 * @param alpha
	 *            ˮӡ͸���� 0.1f ~ 1.0f
	 * */
	public static void waterMark(String imgPath, String markPath, int x, int y,
			float alpha) {
		try {
			// ���ش�����ͼƬ�ļ�
			Image img = ImageIO.read(new File(imgPath));

			BufferedImage image = new BufferedImage(img.getWidth(null),
					img.getHeight(null), BufferedImage.TYPE_INT_RGB);
			Graphics2D g = image.createGraphics();
			g.drawImage(img, 0, 0, null);

			// ����ˮӡͼƬ�ļ�
			Image src_biao = ImageIO.read(new File(markPath));
			g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_ATOP,
					alpha));
			g.drawImage(src_biao, x, y, null);
			g.dispose();

			// ���洦�����ļ�
			FileOutputStream out = new FileOutputStream(imgPath);
			JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(out);
			encoder.encode(image);
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * ����ˮӡ
	 * 
	 * @param imgPath
	 *            ������ͼƬ
	 * @param text
	 *            ˮӡ����
	 * @param font
	 *            ˮӡ������Ϣ
	 * @param color
	 *            ˮӡ������ɫ
	 * @param x
	 *            ˮӡλ��ͼƬ���Ͻǵ� x ����ֵ
	 * @param y
	 *            ˮӡλ��ͼƬ���Ͻǵ� y ����ֵ
	 * @param alpha
	 *            ˮӡ͸���� 0.1f ~ 1.0f
	 */

	public static void textMark(String imgPath, String text, Font font,
			Color color, int x, int y, float alpha) {
		try {
			Font Dfont = (font == null) ? new Font("����", 20, 13) : font;

			Image img = ImageIO.read(new File(imgPath));

			BufferedImage image = new BufferedImage(img.getWidth(null),
					img.getHeight(null), BufferedImage.TYPE_INT_RGB);
			Graphics2D g = image.createGraphics();

			g.drawImage(img, 0, 0, null);
			g.setColor(color);
			g.setFont(Dfont);
			g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_ATOP,
					alpha));
			g.drawString(text, x, y);
			g.dispose();
			FileOutputStream out = new FileOutputStream(imgPath);
			JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(out);
			encoder.encode(image);
			out.close();
		} catch (Exception e) {
			System.out.println(e);
		}
	}
	
	/**
	 * ��ȡJPEGͼƬ
	 * @param filename �ļ���
	 * @return BufferedImage ͼƬ����
	 */
	public static BufferedImage readJPEGImage(String filename)
	{
		try {
			InputStream imageIn = new FileInputStream(new File(filename));
			// �õ�����ı����������ļ�������jpg��ʽ����
			JPEGImageDecoder decoder = JPEGCodec.createJPEGDecoder(imageIn);
			// �õ�������ͼƬ����
			BufferedImage sourceImage = decoder.decodeAsBufferedImage();
			
			return sourceImage;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (ImageFormatException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	/**
	 * ��ȡJPEGͼƬ
	 * @param filename �ļ���
	 * @return BufferedImage ͼƬ����
	 */
	public static BufferedImage readPNGImage(String filename)
	{
		try {
			File inputFile = new File(filename);  
	        BufferedImage sourceImage = ImageIO.read(inputFile);
			return sourceImage;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (ImageFormatException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	/**
	 * �Ҷ�ֵ����
	 * @param pixels ����
	 * @return int �Ҷ�ֵ
	 */
	public static int rgbToGray(int pixels) {
		// int _alpha = (pixels >> 24) & 0xFF;
		int _red = (pixels >> 16) & 0xFF;
		int _green = (pixels >> 8) & 0xFF;
		int _blue = (pixels) & 0xFF;
		return (int) (0.3 * _red + 0.59 * _green + 0.11 * _blue);
	}
	
	/**
	 * ���������ƽ��ֵ
	 * @param pixels ����
	 * @return int ƽ��ֵ
	 */
	public static int average(int[] pixels) {
		float m = 0;
		for (int i = 0; i < pixels.length; ++i) {
			m += pixels[i];
		}
		m = m / pixels.length;
		return (int) m;
	}
}
