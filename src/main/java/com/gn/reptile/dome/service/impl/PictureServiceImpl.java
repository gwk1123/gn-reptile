package com.gn.reptile.dome.service.impl;

import com.gn.reptile.dome.service.PictureService;
import org.springframework.stereotype.Service;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;


@Service
public class PictureServiceImpl implements PictureService {

    public void compared(){}


    /**
     * @param args
     */
    public static void main(String[] args) {
        List<String> hashCodes = new ArrayList<String>();

        String filename = ImageHelper.path + "\\images\\";
        String hashCode = null;

        for (int i = 0; i < 6; i++)
        {
            hashCode = produceFingerPrint(filename + "example" + (i + 1) + ".jpg");
            hashCodes.add(hashCode);
        }
        System.out.println("Resources: ");
        System.out.println(hashCodes);
        System.out.println();

        String sourceHashCode = produceFingerPrint(filename + "source.jpg");
        System.out.println("Source: ");
        System.out.println(sourceHashCode);
        System.out.println();

        for (int i = 0; i < hashCodes.size(); i++)
        {
            int difference = hammingDistance(sourceHashCode, hashCodes.get(i));
            System.out.print("��������:"+difference+"     ");
            if(difference==0){
                System.out.println("source.jpgͼƬ��example"+(i+1)+".jpgһ��");
            }else if(difference<=5){
                System.out.println("source.jpgͼƬ��example"+(i+1)+".jpg�ǳ�����");
            }else if(difference<=10){
                System.out.println("source.jpgͼƬ��example"+(i+1)+".jpg�е�����");
            }else if(difference>10){
                System.out.println("source.jpgͼƬ��example"+(i+1)+".jpg��ȫ��һ��");
            }
        }

    }

    /**
     * ����"��������"��Hamming distance����
     * �������ͬ������λ������5����˵������ͼƬ�����ƣ��������10����˵���������Ų�ͬ��ͼƬ��
     * @param sourceHashCode ԴhashCode
     * @param hashCode ��֮�Ƚϵ�hashCode
     */
    public static int hammingDistance(String sourceHashCode, String hashCode) {
        int difference = 0;
        int len = sourceHashCode.length();

        for (int i = 0; i < len; i++) {
            if (sourceHashCode.charAt(i) != hashCode.charAt(i)) {
                difference ++;
            }
        }

        return difference;
    }

    /**
     * ����ͼƬָ��
     * @param filename �ļ���
     * @return ͼƬָ��
     */
    public static String produceFingerPrint(String filename) {
        BufferedImage source = ImageHelper.readPNGImage(filename);// ��ȡ�ļ�

        int width = 8;
        int height = 8;

        // ��һ������С�ߴ硣
        // ��ͼƬ��С��8x8�ĳߴ磬�ܹ�64�����ء���һ����������ȥ��ͼƬ��ϸ�ڣ�ֻ�����ṹ�������Ȼ�����Ϣ��������ͬ�ߴ硢����������ͼƬ���졣
        BufferedImage thumb = ImageHelper.thumb(source, width, height, false);

        // �ڶ�������ɫ�ʡ�
        // ����С���ͼƬ��תΪ64���Ҷȡ�Ҳ����˵���������ص��ܹ�ֻ��64����ɫ��
        int[] pixels = new int[width * height];
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                pixels[i * height + j] = ImageHelper.rgbToGray(thumb.getRGB(i, j));
            }
        }

        // ������������ƽ��ֵ��
        // ��������64�����صĻҶ�ƽ��ֵ��
        int avgPixel = ImageHelper.average(pixels);

        // ���Ĳ����Ƚ����صĻҶȡ�
        // ��ÿ�����صĻҶȣ���ƽ��ֵ���бȽϡ����ڻ����ƽ��ֵ����Ϊ1��С��ƽ��ֵ����Ϊ0��
        int[] comps = new int[width * height];
        for (int i = 0; i < comps.length; i++) {
            if (pixels[i] >= avgPixel) {
                comps[i] = 1;
            } else {
                comps[i] = 0;
            }
        }

        // ���岽�������ϣֵ��
        // ����һ���ıȽϽ���������һ�𣬾͹�����һ��64λ�����������������ͼƬ��ָ�ơ���ϵĴ��򲢲���Ҫ��ֻҪ��֤����ͼƬ������ͬ����������ˡ�
        StringBuffer hashCode = new StringBuffer();
        for (int i = 0; i < comps.length; i+= 4) {
            int result = comps[i] * (int) Math.pow(2, 3) + comps[i + 1] * (int) Math.pow(2, 2) + comps[i + 2] * (int) Math.pow(2, 1) + comps[i + 2];
            hashCode.append(binaryToHex(result));
        }

        // �õ�ָ���Ժ󣬾Ϳ��ԶԱȲ�ͬ��ͼƬ������64λ���ж���λ�ǲ�һ���ġ�
        return hashCode.toString();
    }

    /**
     * ������תΪʮ������
     * @param int binary
     * @return char hex
     */
    private static char binaryToHex(int binary) {
        char ch = ' ';
        switch (binary)
        {
            case 0:
                ch = '0';
                break;
            case 1:
                ch = '1';
                break;
            case 2:
                ch = '2';
                break;
            case 3:
                ch = '3';
                break;
            case 4:
                ch = '4';
                break;
            case 5:
                ch = '5';
                break;
            case 6:
                ch = '6';
                break;
            case 7:
                ch = '7';
                break;
            case 8:
                ch = '8';
                break;
            case 9:
                ch = '9';
                break;
            case 10:
                ch = 'a';
                break;
            case 11:
                ch = 'b';
                break;
            case 12:
                ch = 'c';
                break;
            case 13:
                ch = 'd';
                break;
            case 14:
                ch = 'e';
                break;
            case 15:
                ch = 'f';
                break;
            default:
                ch = ' ';
        }
        return ch;
    }




}
