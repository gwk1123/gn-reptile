package com.gn.reptile.dome.service.impl;

import com.gn.reptile.dome.bili.Verify;
import com.gn.reptile.dome.service.PictureService;
import org.openqa.selenium.*;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.logging.LogType;
import org.openqa.selenium.logging.LoggingPreferences;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.*;
import org.openqa.selenium.Point;
import org.openqa.selenium.chrome.ChromeDriver;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.awt.image.RasterFormatException;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

@Service
public class PictureServiceImpl implements PictureService {

    public void compared() {

    }

    public static void main(String[] args) throws Exception{
         compared_1();
    }

    public static void compared_1() throws Exception {

        //用户名
        final String USERID = "66665555444@qq.com";
        //密码
        final String PASSWORD = "123456789";

        System.setProperty("webdriver.chrome.driver", "D:\\chromedriver\\chromedriver_win32\\chromedriver.exe");
        ChromeOptions options = new ChromeOptions();
        options.setPageLoadStrategy(PageLoadStrategy.EAGER);
        options.setExperimentalOption("excludeSwitches", Collections.singletonList("enable-automation"));
        options.setExperimentalOption("useAutomationExtension", false);
        WebDriver webDriver = new ChromeDriver(options);
        //打开登陆页面
        webDriver.get("https://www.17sucai.com/pins/demo-show?id=31475");
        //窗口最大化
        webDriver.manage().window().maximize();
        Thread.sleep(10000);//等待验证码加载出来
        webDriver.switchTo().frame(webDriver.findElement(By.xpath("//*[@id=\"iframe\"]")));
//        while(true)
//        {
            //用户名框填写
//            webDriver.findElement(By.xpath("//*[@id=\'login-username\']")).sendKeys(Keys.chord(Keys.CONTROL, "a"),USERID);
            //密码框填写
            webDriver.findElement(By.xpath("/html/body/div/div[1]/div[3]/input")).sendKeys("535352535");
            //登录
            webDriver.findElement(By.xpath("/html/body/div/div[1]/div[4]/input")).click();
            Thread.sleep(2000);//等待验证码加载出来



            //滑动按钮
            WebElement key = webDriver.findElement(By.xpath("//*[@id=\"imgVer\"]/div[2]/div[2]"));
            Actions action = new Actions(webDriver);
            //鼠标移动到滑动按钮上
            action.moveToElement(key).perform();
            Thread.sleep(300);//等待验证码加载出来
            BufferedImage image1;
            //截屏
            WebElement oneElement = webDriver.findElement(By.xpath("//*[@id=\"scream\"]"));

        //移动图片到指定位置
        FileUtils.copyFile(createElementImage(webDriver,oneElement), new File("D:\\filename", System.currentTimeMillis()+".png"));


//            image1 = ImageIO.read(((TakesScreenshot) webDriver).getScreenshotAs(OutputType.FILE));
            //验证码左上角图片
//            WebElement kw = webDriver.findElement(By.xpath("//*[@id=\'gc-box\']/div/div[1]/div[2]/div[1]/a[2]/div[1]/div[1]"));
//            //验证码右下角图片
//            WebElement ka = webDriver.findElement(By.xpath("//*[@id=\'gc-box\']/div/div[1]/div[2]/div[1]/a[2]/div[1]/div[52]"));
//            //从截图中分割出整张原始验证码
//            image1 = image1.getSubimage(kw.getLocation().x, kw.getLocation().y, Math.abs(ka.getLocation().x-kw.getLocation().x)+ka.getSize().getWidth(),Math.abs(ka.getLocation().y-kw.getLocation().y)+ka.getSize().getHeight());
//            //鼠标按住滑动按钮以显示图片缺口
//            action.clickAndHold(key).perform();
//            Thread.sleep(50);//等待有缺口的验证码加载出来
//            //再次截屏
//            BufferedImage image2 = ImageIO.read(((TakesScreenshot) webDriver).getScreenshotAs(OutputType.FILE));
            webDriver.switchTo().defaultContent(); //回到原来页面
//            //分割出有缺口的验证码
//            image2 = image2.getSubimage(kw.getLocation().x, kw.getLocation().y, Math.abs(ka.getLocation().x-kw.getLocation().x)+ka.getSize().getWidth(),Math.abs(ka.getLocation().y-kw.getLocation().y)+ka.getSize().getHeight());
//            //鼠标向右移动一段距离，这段距离的计算参考Verify.getTX2()函数
//            action.moveByOffset(Verify.getTX2(image1,image2), 0).perform();
//            //松开鼠标以完成验证
//            action.release().perform();
//            //等待验证结果
//            Thread.sleep(500);
//            //如果没有出现“验证失败”，说明验证成功，退出循环
//            if(!webDriver.getPageSource().contains("验证失败")) {
//                break;
//            }
//            //能执行到这里，说明验证失败，刷新以重新验证
//            webDriver.navigate().to("https://www.17sucai.com/pins/demo-show?id=31475");
//        }
//        //结束chromedriver.exe以释放资源
//        Runtime.getRuntime().exec("taskkill /F /im " + "chromedriver.exe");


    }



    /**
     *
     * https://www.cnblogs.com/mingyue5826/p/12806682.html
     * 根据Element截图指定区域方法
     *
     * @param driver
     * @param element  截图区域
     * @throws Exception
     */
    public static File elementSnapshot(WebDriver driver, WebElement element) throws Exception {
        //创建全屏截图
        File screen = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
        BufferedImage image = ImageIO.read(screen);

        //获取元素的高度、宽度
        int width = element.getSize().getWidth();
        int height = element.getSize().getHeight();

        //创建一个矩形使用上面的高度，和宽度
        Rectangle rect = new Rectangle(width, height);
        //元素坐标
        Point p = element.getLocation();


        //对前面的矩形进行操作
        //TODO 使用可以截全图的方法（滚动条），暂未找到方式
        int w = rect.width; //指定矩形区域的宽度
        int h = rect.height;//指定矩形区域的高度
        int x = p.getX(); //指定矩形区域左上角的X坐标
        int y = p.getY(); //指定矩形区域左上角的Y坐标

        //driver的分辨率，这里设置1920*1080
        int w_driver = 1920;
        int h_driver = 1080;

        System.out.println("width:" + w);
        System.out.println("height:"+ h);
        System.out.println("x:"+ x);
        System.out.println("y:"+ y);

        System.out.println("y+height:"+(y + h));
        System.out.println("x+width:"+ (x + w));

        /**
         * 如果Element的Y坐标值加上高度超过driver的高度
         * 就会报错(y + height) is outside or not
         * 退而求其次，调整图片的宽度和高度, 调整到适合driver的分辨率
         * 此时会截图driver可见的元素区域快照
         * TODO 如果能找到跨滚动条截图的方式，可以不用裁剪
         */
        try{
            if ( y + h > h_driver){
                h = h- (y + h - h_driver); //

                System.out.println("修改后的height:" + h);
                System.out.println("修改后的y+height："+ (y+h));
            }
            //(x + width) is outside or not
            if (x + w > w_driver){
                w = x - (x + w - w_driver);

                System.out.println("修改后的width："+ w);
                System.out.println("修改后的x+width:"+ (x+w));
            }

            BufferedImage img = image.getSubimage(x, y, w, h);
            ImageIO.write(img, "png", screen);
            System.out.println("Screenshot By element success");

        }catch (IOException e){
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return screen;
    }


    public static File createElementImage(WebDriver driver, WebElement webElement) throws IOException {
// 获得webElement的位置和大小。
        File screen = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
        Point location = webElement.getLocation();

        Dimension size = webElement.getSize();

// 创建全屏截图。

        BufferedImage originalImage = ImageIO.read(screen);

// 截取webElement所在位置的子图。

        BufferedImage croppedImage = originalImage.getSubimage(

                location.getX(),

                location.getY(),

                size.getWidth(),

                size.getHeight());

        ImageIO.write(croppedImage, "png", screen);
        return screen;

    }




}
