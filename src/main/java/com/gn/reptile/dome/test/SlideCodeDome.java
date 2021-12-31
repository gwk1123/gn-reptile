package com.gn.reptile.dome.test;

import org.apache.commons.io.FileUtils;
import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.openqa.selenium.By;
import org.openqa.selenium.PageLoadStrategy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.interactions.Actions;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import static org.opencv.highgui.HighGui.imshow;
import static org.opencv.highgui.HighGui.waitKey;

/**
 * 测试可行 滑动验证码
 */
public class SlideCodeDome {


    public static void main(String[] args) {
        seleniumTest();
    }


    private static final String INDEX_URL = "https://007.qq.com/online.html?ADTAG=index.head";
    private static void seleniumTest() {

        System.setProperty("webdriver.chrome.driver", "D:\\chromedriver\\chromedriver_win32\\chromedriver.exe");
        ChromeOptions options = new ChromeOptions();
        options.setPageLoadStrategy(PageLoadStrategy.EAGER);
        options.setExperimentalOption("excludeSwitches", Collections.singletonList("enable-automation"));
        options.setExperimentalOption("useAutomationExtension", false);
        WebDriver driver = new ChromeDriver(options);
        try {

            driver.get(INDEX_URL);
            driver.manage().window().maximize(); // 设置浏览器窗口最大化
            Thread.sleep(3000);
            driver.findElement(By.className("wp-onb-tit")).findElements(By.tagName("a")).get(1).click();
            Thread.sleep(500);
            // 点击出现滑动图
            waitWebElement(driver, By.id("code"), 500).click();
            Thread.sleep(100);
            // 获取到验证区域
            driver.switchTo().frame(waitWebElement(driver, By.id("tcaptcha_iframe"), 500));
            Thread.sleep(100);
            // 获取滑动按钮
            WebElement moveElemet = waitWebElement(driver, By.id("tcaptcha_drag_button"), 500);
            Thread.sleep(100);
            // 获取带阴影的背景图
            String bgUrl = waitWebElement(driver, By.id("slideBg"), 500).getAttribute("src");
            Thread.sleep(100);
            // 获取带阴影的小图
            String sUrl = waitWebElement(driver, By.id("slideBlock"), 500).getAttribute("src");
            Thread.sleep(100);
            // 获取高度
            String topStr = waitWebElement(driver, By.id("slideBlock"), 500).getAttribute("style").substring(32, 36);
            int top = Integer.parseInt(topStr.substring(0, topStr.indexOf("p"))) * 2;
            Thread.sleep(100);
            // 计算移动距离
            int distance = (int) Double.parseDouble(getTencentDistance(bgUrl, sUrl, top));
            // 滑动
            move(driver, moveElemet, distance);
            Thread.sleep(5000);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            driver.close();
        }
    }

    /**
     * 获取腾讯验证滑动距离
     *
     * @return
     */
//    public static String dllPath = "C://chrome//opencv_java440.dll";

    public static String getTencentDistance(String bUrl, String sUrl, int top) {
//        System.load(dllPath);
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        File bFile = new File("C:/qq_b.jpg");
        File sFile = new File("C:/qq_s.jpg");
        try {
            FileUtils.copyURLToFile(new URL(bUrl), bFile);
            FileUtils.copyURLToFile(new URL(sUrl), sFile);
            BufferedImage bgBI = ImageIO.read(bFile);
            BufferedImage sBI = ImageIO.read(sFile);
            // 裁剪
            bgBI = bgBI.getSubimage(360, top, bgBI.getWidth() - 370, sBI.getHeight());
            ImageIO.write(bgBI, "png", bFile);
            Mat s_mat = Imgcodecs.imread(sFile.getPath());
            Mat b_mat = Imgcodecs.imread(bFile.getPath());
            // 转灰度图像
            Mat s_newMat = new Mat();
            Imgproc.cvtColor(s_mat, s_newMat, Imgproc.COLOR_BGR2GRAY);
            imshow("hudu",s_newMat);
            waitKey(0);
            // 二值化图像
            binaryzation(s_newMat);
            Imgcodecs.imwrite(sFile.getPath(), s_newMat);
            imshow("er",s_newMat);
            waitKey(0);

            int result_rows = b_mat.rows() - s_mat.rows() + 1;
            int result_cols = b_mat.cols() - s_mat.cols() + 1;
            Mat g_result = new Mat(result_rows, result_cols, CvType.CV_32FC1);
            imshow("b_mat",b_mat);
            waitKey(0);
            imshow("s_mat",s_mat);
            waitKey(0);
            imshow("g_result",g_result);
            waitKey(0);
            Imgproc.matchTemplate(b_mat, s_mat, g_result, Imgproc.TM_SQDIFF); // 归一化平方差匹配法
            // 归一化相关匹配法
            Core.normalize(g_result, g_result, 0, 1, Core.NORM_MINMAX, -1, new Mat());
            Point matchLocation = new Point();
            Core.MinMaxLocResult mmlr = Core.minMaxLoc(g_result);
            matchLocation = mmlr.maxLoc; // 此处使用maxLoc还是minLoc取决于使用的匹配算法
            Imgproc.rectangle(b_mat, matchLocation,
                    new Point(matchLocation.x + s_mat.cols(), matchLocation.y + s_mat.rows()), new Scalar(0, 0, 0, 0));
            return "" + ((matchLocation.x + s_mat.cols() + 360 - sBI.getWidth() - 46) / 2);
        } catch (Throwable e) {
            e.printStackTrace();
            return null;
        } finally {
            bFile.delete();
            sFile.delete();
        }
    }
    /**
     *
     * @param mat
     *            二值化图像
     */
    public static void binaryzation(Mat mat) {
        int BLACK = 0;
        int WHITE = 255;
        int ucThre = 0, ucThre_new = 127;
        int nBack_count, nData_count;
        int nBack_sum, nData_sum;
        int nValue;
        int i, j;
        int width = mat.width(), height = mat.height();
        // 寻找最佳的阙值
        while (ucThre != ucThre_new) {
            nBack_sum = nData_sum = 0;
            nBack_count = nData_count = 0;

            for (j = 0; j < height; ++j) {
                for (i = 0; i < width; i++) {
                    nValue = (int) mat.get(j, i)[0];

                    if (nValue > ucThre_new) {
                        nBack_sum += nValue;
                        nBack_count++;
                    } else {
                        nData_sum += nValue;
                        nData_count++;
                    }
                }
            }
            nBack_sum = nBack_sum / nBack_count;
            nData_sum = nData_sum / nData_count;
            ucThre = ucThre_new;
            ucThre_new = (nBack_sum + nData_sum) / 2;
        }
        // 二值化处理
        int nBlack = 0;
        int nWhite = 0;
        for (j = 0; j < height; ++j) {
            for (i = 0; i < width; ++i) {
                nValue = (int) mat.get(j, i)[0];
                if (nValue > ucThre_new) {
                    mat.put(j, i, WHITE);
                    nWhite++;
                } else {
                    mat.put(j, i, BLACK);
                    nBlack++;
                }
            }
        }
        // 确保白底黑字
        if (nBlack > nWhite) {
            for (j = 0; j < height; ++j) {
                for (i = 0; i < width; ++i) {
                    nValue = (int) (mat.get(j, i)[0]);
                    if (nValue == 0) {
                        mat.put(j, i, WHITE);
                    } else {
                        mat.put(j, i, BLACK);
                    }
                }
            }
        }
    }
    // 延时加载
    private static WebElement waitWebElement(WebDriver driver, By by, int count) throws Exception {
        WebElement webElement = null;
        boolean isWait = false;
        for (int k = 0; k < count; k++) {
            try {
                webElement = driver.findElement(by);
                if (isWait)
                    System.out.println(" ok!");
                return webElement;
            } catch (org.openqa.selenium.NoSuchElementException ex) {
                isWait = true;
                if (k == 0)
                    System.out.print("waitWebElement(" + by.toString() + ")");
                else
                    System.out.print(".");
                Thread.sleep(50);
            }
        }
        if (isWait)
            System.out.println(" outTime!");
        return null;
    }





    /**
     * 模拟人工移动
     * @param driver
     * @param element 页面滑块
     * @param distance 需要移动距离
     */
    public static void move(WebDriver driver, WebElement element, int distance) throws InterruptedException {
        int randomTime = 0;
        if (distance > 90) {
            randomTime = 250;
        } else if (distance > 80 && distance <= 90) {
            randomTime = 150;
        }
        List<Integer> track = getMoveTrack(distance - 2);
        int moveY = 1;
        try {
            Actions actions = new Actions(driver);
            actions.clickAndHold(element).perform();
            Thread.sleep(200);
            for (int i = 0; i < track.size(); i++) {
                actions.moveByOffset(track.get(i), moveY).perform();
                Thread.sleep(new Random().nextInt(300) + randomTime);
            }
            Thread.sleep(200);
            actions.release(element).perform();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /**
     * 根据距离获取滑动轨迹
     * @param distance 需要移动的距离
     * @return
     */
    public static List<Integer> getMoveTrack(int distance) {
        List<Integer> track = new ArrayList<>();// 移动轨迹
        Random random = new Random();
        int current = 0;// 已经移动的距离
        int mid = (int) distance * 4 / 5;// 减速阈值
        int a = 0;
        int move = 0;// 每次循环移动的距离
        while (true) {
            a = random.nextInt(10);
            if (current <= mid) {
                move += a;// 不断加速
            } else {
                move -= a;
            }
            if ((current + move) < distance) {
                track.add(move);
            } else {
                track.add(distance - current);
                break;
            }
            current += move;
        }
        return track;
    }


}
