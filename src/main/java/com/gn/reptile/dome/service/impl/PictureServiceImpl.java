package com.gn.reptile.dome.service.impl;

import com.gn.reptile.dome.service.PictureService;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.Collections;

/**
 * 截图（测试可行）
 */

@Service
public class PictureServiceImpl implements PictureService {


    /**
     * 向下滑动次数
     */
    private int scrollTimes = 10;

    /**
     * 上次高度
     */
    private int lastHeight = 0;

    /**
     * 重置窗口大小(调整至可以正常截图)
     *
     * @param driver 驱动对象
     */
    private void resetWindowSizeToScreenshot(WebDriver driver) {
        // 窗口最大化
        resetWindowSize(driver);
        // 向下滑动页面：到指定次数 || 高度不再变化，退出
        JavascriptExecutor javascriptExecutor = (JavascriptExecutor) driver;
        for (int i = 0; i < this.scrollTimes; i++) {
            // 获取当前高度
            Object thisHeightObject = javascriptExecutor.executeScript("return document.body.scrollHeight;");
            int thisHeight = Integer.parseInt(String.valueOf(thisHeightObject));
            // 判断高度
            if (this.lastHeight != thisHeight) {
                // 向下滑动
                javascriptExecutor.executeScript("window.scrollBy(0,10000)");
                // 滑动后赋值
                this.lastHeight = thisHeight;
            } else {
                // 高度相同，跳出
                break;
            }
        }
        // 设置窗口高度
        Dimension size = driver.manage().window().getSize();
        driver.manage().window().setSize(new Dimension(size.width, this.lastHeight));
    }

    /**
     * 重置窗口大小(最大化窗口)
     *
     * @param driver 驱动对象
     */
    private void resetWindowSize(WebDriver driver) {
        driver.manage().window().maximize();
    }

    /**
     * 全屏截图（当前可视范围）
     *
     * @param driver 驱动对象
     * @return 截图内容
     */
    public File fullScreenshot(WebDriver driver) {
        // 调整窗口
        resetWindowSize(driver);
        // 截图
        File file = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
        // 调整窗口
        resetWindowSize(driver);
        return file;
    }

    /**
     * 全屏截图（长图）
     *
     * @param driver 驱动对象
     * @return 截图内容
     */
    public File fullScreenshotLong(WebDriver driver) {
        // 调整窗口
        resetWindowSizeToScreenshot(driver);
        // 截图
        File file = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
        // 调整窗口
        resetWindowSize(driver);
        return file;
    }

    /**
     * 局域截图（单图）
     *
     * @param driver  驱动对象
     * @param element 要截图的文档节点对象
     * @return 截图内容
     */
    public File localScreenshot(WebDriver driver, WebElement element) {
        // 调整窗口
//        resetWindowSizeToScreenshot(driver);
        // 截图
        File file = element.getScreenshotAs(OutputType.FILE);
        // 调整窗口
//        resetWindowSize(driver);
        return file;
    }

    public int getScrollTimes() {
        return scrollTimes;
    }

    public void setScrollTimes(int scrollTimes) {
        this.scrollTimes = scrollTimes;
    }

    public int getLastHeight() {
        return lastHeight;
    }

    public void setLastHeight(int lastHeight) {
        this.lastHeight = lastHeight;
    }


    @Override
    public void compared() throws Exception {

        System.setProperty("webdriver.chrome.driver", "D:\\chromedriver\\chromedriver_win32\\chromedriver.exe");
        ChromeOptions options = new ChromeOptions();
        options.setPageLoadStrategy(PageLoadStrategy.EAGER);
        options.setExperimentalOption("excludeSwitches", Collections.singletonList("enable-automation"));
        options.setExperimentalOption("useAutomationExtension", false);
        WebDriver webDriver = new ChromeDriver(options);
//        //打开登陆页面
//        webDriver.get("https://www.baidu.com/");
//        WebElement oneElement = webDriver.findElement(By.xpath("//*[@id=\"su\"]"));
//
//        File file = localScreenshot(webDriver, oneElement);
//        FileUtils.copyFile(file, new File("D:\\filename", System.currentTimeMillis() + ".png"));


//打开登陆页面
        webDriver.get("https://www.17sucai.com/pins/demo-show?id=31475");
        //窗口最大化
//        webDriver.manage().window().maximize();
        Thread.sleep(1000);//等待验证码加载出来
        webDriver.switchTo().frame(webDriver.findElement(By.xpath("//*[@id=\"iframe\"]")));
        //密码框填写
        webDriver.findElement(By.xpath("/html/body/div/div[1]/div[3]/input")).sendKeys("535352535");
        //登录
        webDriver.findElement(By.xpath("/html/body/div/div[1]/div[4]/input")).click();
        Thread.sleep(2000);//等待验证码加载出来


        //截屏
        WebElement oneElement = webDriver.findElement(By.xpath("//*[@id=\"scream\"]"));
        File file = oneElement.getScreenshotAs(OutputType.FILE);

        //移动图片到指定位置
        FileUtils.copyFile(file, new File("D:\\filename", System.currentTimeMillis()+".png"));
        webDriver.switchTo().defaultContent(); //回到原来页面



    }


}
