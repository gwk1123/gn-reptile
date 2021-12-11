package com.gn.reptile.dome.verifyCode;

import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

public class Tess4J {

    private static Logger logger = LoggerFactory.getLogger(Tess4J.class);
    private static String tess_data = "src/main/resources/tessdata";

    public static String executeTess4J(String imgUrl){
        String ocrResult = "";
        try{
            ITesseract instance = new Tesseract();
            instance.setDatapath(tess_data);
            //instance.setLanguage("chi_sim");
            File imgDir = new File(imgUrl);
            //long startTime = System.currentTimeMillis();
            ocrResult = instance.doOCR(imgDir);
        }catch (TesseractException e){
            logger.error("异常:{}",e);
            e.printStackTrace();
        }
        return ocrResult;
    }


    public static void main(String[] args) throws TesseractException {

        Tesseract tesseract = new Tesseract();
        tesseract.setDatapath("src/main/resources/tessdata");
        System.out.println(tesseract.doOCR(new File("E:\\images\\123.jpg_removeLine.jpg")));

    }

}
