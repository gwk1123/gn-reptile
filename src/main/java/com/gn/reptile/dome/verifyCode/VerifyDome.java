package com.gn.reptile.dome.verifyCode;

import com.gn.reptile.utils.Constants;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import org.bytedeco.javacpp.opencv_core;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static org.bytedeco.javacpp.opencv_highgui.imshow;
import static org.bytedeco.javacpp.opencv_highgui.waitKey;
import static org.bytedeco.javacpp.opencv_imgcodecs.imread;
import static org.bytedeco.javacpp.opencv_imgcodecs.imwrite;
import static org.bytedeco.javacpp.opencv_imgproc.*;
import static org.bytedeco.javacpp.opencv_imgproc.CV_THRESH_BINARY;

/**
 * 破解度不高(去除干扰线倒是可以)
 */
public class VerifyDome {



    public static String handleImage(String imageFile) throws Exception{
        opencv_core.Mat image = imread(imageFile);
        File fileObj = new File(imageFile);
        String orignialFile= imageFile.substring(0,imageFile.indexOf("."));
        opencv_core.Mat grayImage = new opencv_core.Mat();
        opencv_core.Mat result = new opencv_core.Mat();

        //灰度化
        cvtColor(image, grayImage,CV_BGR2GRAY);
        //二值化
        threshold(grayImage, result, 180, 255, CV_THRESH_BINARY);

        String binaryFileName = fileObj.getParentFile().getAbsolutePath()+File.separator+fileObj.getName()+"_temp.png";
        System.out.println(binaryFileName);
        imwrite(binaryFileName, result);
//        //去噪
        BufferedImage images = ImageIO.read(new File(binaryFileName));
        BufferedImage changedImages = removeLine(images,2);
        File removeLineImage = new File(fileObj.getParentFile().getAbsolutePath()+File.separator+fileObj.getName()+"_removeLine.png");
        System.out.println(removeLineImage);
        ImageIO.write(changedImages,"png", removeLineImage);

        opencv_core.Mat imageMat = imread(removeLineImage.getAbsolutePath());
        opencv_core.Mat blurMat = new opencv_core.Mat();
        medianBlur(imageMat,blurMat,3); //中值过滤

        opencv_core.Mat blurMat1 = new opencv_core.Mat();
        medianBlur(blurMat,blurMat1,3); //中值过滤

        String blurFileName = fileObj.getParentFile().getAbsolutePath()+File.separator+fileObj.getName()+"_blur.png";
        imwrite(blurFileName, blurMat1);

        //ocr 识别
//        Thread.sleep(2000);
        Tesseract tesseract = new Tesseract();
        tesseract.setDatapath("src/main/resources/tessdata");
        tesseract.setLanguage("eng");
        String code=  tesseract.doOCR(new File(blurFileName));
        return code;
    }

    private static BufferedImage removeLine(BufferedImage img, int px) {
        if (img != null) {
            int width = img.getWidth();
            int height = img.getHeight();

            for (int x = 0; x < width; x++) {
                List<Integer> list = new ArrayList<Integer>();
                for (int y = 0; y < height; y++) {
                    int count = 0;
                    while (y < height - 1 && isBlack(img.getRGB(x, y))) {
                        count++;
                        y++;
                    }
                    if (count <= px && count > 0) {
                        for (int i = 0; i <= count; i++) {
                            list.add(y - i);
                        }
                    }
                }
                if (list.size() != 0) {
                    for (int i = 0; i < list.size(); i++) {
                        img.setRGB(x, list.get(i), Color.white.getRGB());
                    }
                }
            }
        }
        return img;

    }

    public static boolean isBlack(int rgb){
        Color c = new Color(rgb);
        int b = c.getBlue();
        int r = c.getRed();
        int g = c.getGreen();
        int sum = r+g+b;
        if(sum<10){
            return true;
        }
        return false;
        //sum的值越小（最小为零，黑色）颜色越重，
        //sum的值越大（最大值是225*3）颜色越浅，
        //sum的值小于10就算是黑色了.
    }




    public static void main(String[] args) throws Exception {
        try {
            String imagesPath = System.getProperty(Constants.images_path);
            File directory = new File(imagesPath+"\\images\\14.jpg");
            String code =   handleImage(directory.getAbsolutePath());
            System.out.println("编码:"+code);
        } catch (TesseractException | InterruptedException e) {
            e.printStackTrace();
        }
    }




}
