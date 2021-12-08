package com.gn.reptile.dome.bili;

import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.interactions.Actions;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 * @author
 */
public class LoginBili {

	public static void main(String[] args) throws InterruptedException, WebDriverException, IOException {
	    //用户名
		final String USERID = "66665555444@qq.com";
		//密码
		final String PASSWORD = "123456789";
		WebDriver webDriver = new ChromeDriver();
		//打开登陆页面
		webDriver.get("https://passport.bilibili.com/login");
		//窗口最大化
		webDriver.manage().window().maximize();
		while(true)
		{
		    //用户名框填写
			webDriver.findElement(By.xpath("//*[@id=\'login-username\']")).sendKeys(Keys.chord(Keys.CONTROL, "a"),USERID);
			//密码框填写
			webDriver.findElement(By.xpath("//*[@id=\'login-passwd\']")).sendKeys(Keys.chord(Keys.CONTROL, "a"),PASSWORD);
			//滑动按钮
			WebElement key = webDriver.findElement(By.xpath("//*[@id=\'gc-box\']/div/div[3]/div[2]"));
			Actions action = new Actions(webDriver);
			//鼠标移动到滑动按钮上
			action.moveToElement(key).perform();
			Thread.sleep(300);//等待验证码加载出来
			BufferedImage image1;
			//截屏
            image1 = ImageIO.read(((TakesScreenshot) webDriver).getScreenshotAs(OutputType.FILE));
            //验证码左上角图片
            WebElement kw = webDriver.findElement(By.xpath("//*[@id=\'gc-box\']/div/div[1]/div[2]/div[1]/a[2]/div[1]/div[1]"));
            //验证码右下角图片
			WebElement ka = webDriver.findElement(By.xpath("//*[@id=\'gc-box\']/div/div[1]/div[2]/div[1]/a[2]/div[1]/div[52]"));
			//从截图中分割出整张原始验证码
			image1 = image1.getSubimage(kw.getLocation().x, kw.getLocation().y, Math.abs(ka.getLocation().x-kw.getLocation().x)+ka.getSize().getWidth(),Math.abs(ka.getLocation().y-kw.getLocation().y)+ka.getSize().getHeight());
			//鼠标按住滑动按钮以显示图片缺口
			action.clickAndHold(key).perform();
			Thread.sleep(50);//等待有缺口的验证码加载出来
            //再次截屏
			BufferedImage image2 = ImageIO.read(((TakesScreenshot) webDriver).getScreenshotAs(OutputType.FILE));
			//分割出有缺口的验证码
			image2 = image2.getSubimage(kw.getLocation().x, kw.getLocation().y, Math.abs(ka.getLocation().x-kw.getLocation().x)+ka.getSize().getWidth(),Math.abs(ka.getLocation().y-kw.getLocation().y)+ka.getSize().getHeight());
			 //鼠标向右移动一段距离，这段距离的计算参考Verify.getTX2()函数
			action.moveByOffset(Verify.getTX2(image1,image2), 0).perform();
			//松开鼠标以完成验证
			action.release().perform();
			//等待验证结果
			Thread.sleep(500);
			//如果没有出现“验证失败”，说明验证成功，退出循环
			if(!webDriver.getPageSource().contains("验证失败"))break;
			//能执行到这里，说明验证失败，刷新以重新验证
			webDriver.navigate().to("https://passport.bilibili.com/login");
		}
		//结束chromedriver.exe以释放资源
		Runtime.getRuntime().exec("taskkill /F /im " + "chromedriver.exe");
	}

}
