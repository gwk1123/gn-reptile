package com.gn.reptile.dome.verifyCode;


import com.gn.reptile.utils.Constants;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import org.bytedeco.javacpp.opencv_core;
import org.bytedeco.javacpp.opencv_highgui;
import org.bytedeco.javacpp.opencv_core.Mat;
import org.bytedeco.javacpp.opencv_core.Size;

import java.io.File;

import static org.bytedeco.javacpp.opencv_highgui.imshow;
import static org.bytedeco.javacpp.opencv_highgui.waitKey;
import static org.bytedeco.javacpp.opencv_imgcodecs.*;
import static org.bytedeco.javacpp.opencv_imgproc.*;

public class Dome {


    public static void main(String[] args) throws TesseractException {
        test2();
    }



    public static void test2() throws TesseractException {
        String imagesPath = System.getProperty(Constants.images_path)+"\\images";
        Mat element = getStructuringElement(MORPH_RECT, new Size(7, 7));
        Mat a1 = imread(imagesPath+"\\12.jpg");

        Mat orgMat = new opencv_core.Mat();
        resize(a1,orgMat,new Size(0,0),5,5,1);

        opencv_core.Mat grayImage = new opencv_core.Mat();
        opencv_core.Mat result = new opencv_core.Mat();

        //灰度化
        cvtColor(orgMat, grayImage,CV_BGR2GRAY);
        //二值化
        threshold(grayImage, result, 180, 255, CV_THRESH_BINARY_INV);
        imshow("ez",result);
        waitKey(0);

        //腐蚀
        Mat desMat = new Mat();
        erode(result,desMat,element);
        imshow("fs",desMat);
        waitKey(0);


        Mat element1 = getStructuringElement(MORPH_RECT, new Size(5, 5));
        Mat desMat1 = new Mat();
        erode(desMat,desMat1,element1);
        imshow("fs1",desMat1);
        waitKey(0);


        //膨胀
        Mat pzMat = new Mat();
        dilate(desMat1,pzMat,element);
        imshow("pz",pzMat);
        waitKey(0);

        Mat blurMat = new Mat();
        medianBlur(pzMat,blurMat,7); //中值过滤
        imshow("result",blurMat);
        waitKey(0);


        String blurFileName = imagesPath+"\\12_blur.jpg";
        imwrite(blurFileName, pzMat);
        //ocr 识别
        Tesseract tesseract = new Tesseract();
        tesseract.setDatapath("src/main/resources/tessdata");
        tesseract.setLanguage("eng");
        String code=  tesseract.doOCR(new File(blurFileName));
        System.out.println("code:"+code);
    }


    public static void test1(){

        String imagesPath = System.getProperty(Constants.images_path);

        //读入图片
        opencv_core.IplImage orgImg = cvLoadImage("E:\\images\\123.jpg");
//        opencv_core.IplImage orgImg = cvLoadImage(imagesPath+"\\images\\test_dg.jpg");
        opencv_core.IplImage desImg = opencv_core.IplImage.create(orgImg.width(), orgImg.height(), orgImg.depth(), orgImg.nChannels());

        opencv_core.IplConvKernel kernel = cvCreateStructuringElementEx(5, 5, 1, 1, CV_SHAPE_RECT);
        cvErode(orgImg, desImg, kernel, 1); //腐蚀
        //显示图片
        opencv_highgui.cvShowImage("fushi", desImg);

//        opencv_core.IplImage pzImg = opencv_core.IplImage.create(orgImg.width(), orgImg.height(), orgImg.depth(), orgImg.nChannels());
//        cvDilate(desImg,pzImg,kernel,1);
//        opencv_highgui.cvShowImage("pengzhang", pzImg);


        opencv_highgui.cvWaitKey(0);
    }


}

