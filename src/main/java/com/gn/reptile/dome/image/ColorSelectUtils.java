package com.gn.reptile.dome.image;

import com.gn.reptile.utils.Constants;
import org.bytedeco.javacpp.Loader;
import org.bytedeco.javacpp.opencv_core;
import static org.bytedeco.javacpp.opencv_core.*;
import static org.bytedeco.javacpp.opencv_highgui.cvShowImage;
import static org.bytedeco.javacpp.opencv_highgui.cvWaitKey;
import static org.bytedeco.javacpp.opencv_imgcodecs.cvLoadImage;
import static org.bytedeco.javacpp.opencv_imgproc.*;


/**
 * https://blog.csdn.net/nannan7777/article/details/121305779?spm=1001.2101.3001.6650.18&utm_medium=distribute.pc_relevant.none-task-blog-2%7Edefault%7EOPENSEARCH%7Edefault-18.nonecase&depth_1-utm_source=distribute.pc_relevant.none-task-blog-2%7Edefault%7EOPENSEARCH%7Edefault-18.nonecase
 * 颜色分割变换
 * rina 2021.11.14
 */
public class ColorSelectUtils {

    //hsv绿色范围
    public static opencv_core.CvScalar g_min = cvScalar(35, 43, 46, 0);  //HSV色域
    public static opencv_core.CvScalar g_max= cvScalar(77, 255, 220, 0); //HSV色域

    public static void main(String[] args) {
        String imagesPath = System.getProperty(Constants.images_path);

        //读入图片
        IplImage orgImg = cvLoadImage(imagesPath+"\\images\\test.png");
        //rgb->hsv
        IplImage hsv = IplImage.create( orgImg.width(), orgImg.height(), orgImg.depth(), orgImg.nChannels() );
        cvCvtColor( orgImg, hsv, CV_BGR2HSV );
        IplImage imgThreshold = cvCreateImage(cvGetSize(orgImg), 8, 1);
        //阈值化
        cvInRangeS(hsv, g_min, g_max, imgThreshold);
        //形态学闭处理
        IplImage Morphology_result  = IplImage.create(orgImg.width(),orgImg.height(), IPL_DEPTH_8U, 1);
        IplConvKernel kernelCross = cvCreateStructuringElementEx(21, 21,7,7, CV_SHAPE_RECT);
        cvMorphologyEx(imgThreshold, Morphology_result, Morphology_result, kernelCross, MORPH_CLOSE, 1);
        //膨胀腐蚀
        IplImage erosion_dst  = IplImage.create(orgImg.width(),orgImg.height(), IPL_DEPTH_8U, 1);
        IplImage dilate_dst  = IplImage.create(orgImg.width(),orgImg.height(), IPL_DEPTH_8U, 1);
        IplConvKernel kernel=cvCreateStructuringElementEx(3,3,1,1,CV_SHAPE_RECT);
        cvErode( Morphology_result, erosion_dst, kernel,3);   //腐蚀
        cvDilate( erosion_dst, dilate_dst, kernel,4);   //膨胀

        //查找轮廓并生成轮廓数组, 画出轮廓矩形
        CvMemStorage mem = CvMemStorage.create();
        CvSeq contours = new CvSeq();
        CvSeq ptr = new CvSeq();
        cvFindContours(dilate_dst, mem, contours, Loader.sizeof(CvContour.class) , CV_RETR_CCOMP, CV_CHAIN_APPROX_SIMPLE, cvPoint(0,0));
        CvRect boundingBox;
        int index = 1;
        for (ptr = contours; ptr != null; ptr = ptr.h_next()) {
            boundingBox = cvBoundingRect(ptr, 0);
            cvRectangle( orgImg , cvPoint( boundingBox.x(), boundingBox.y() ),
                    cvPoint( boundingBox.x() + boundingBox.width(), boundingBox.y() + boundingBox.height()),
                    cvScalar( 0, 255, 255, 255 ), 2, 0, 0 );
            System.out.println("boundingBox_index" + index + ".x     :     " + boundingBox.x());
            System.out.println("boundingBox_index" + index + ".y     :     " + boundingBox.y());
            System.out.println("boundingBox_index" + index + ".width     :     " + boundingBox.width());
            System.out.println("boundingBox_index" + index + ".height     :     " + boundingBox.height());
            index++;
        }
        cvShowImage( "Contours", orgImg );
        cvWaitKey(0);
    }


}
