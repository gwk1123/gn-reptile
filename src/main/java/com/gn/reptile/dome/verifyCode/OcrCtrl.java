package com.gn.reptile.dome.verifyCode;

public class OcrCtrl {

    public static void main(String[] args){

        //原始验证码地址
        String OriginalImg = "E:\\images\\12.jpg";
        //识别样本输出地址
        String ocrResult = "E:\\images\\34.jpg";
        //去噪点
        ImgUtils.removeBackground(OriginalImg, ocrResult);
        //裁剪边角
        ImgUtils.cuttingImg(ocrResult);
        //OCR识别
        String code = Tess4J.executeTess4J(ocrResult);
        //输出识别结果
        System.out.println("Ocr识别结果: \n" + code);

    }


}
