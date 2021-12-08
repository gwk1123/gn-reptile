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
	    //�û���
		final String USERID = "66665555444@qq.com";
		//����
		final String PASSWORD = "123456789";
		WebDriver webDriver = new ChromeDriver();
		//�򿪵�½ҳ��
		webDriver.get("https://passport.bilibili.com/login");
		//�������
		webDriver.manage().window().maximize();
		while(true)
		{
		    //�û�������д
			webDriver.findElement(By.xpath("//*[@id=\'login-username\']")).sendKeys(Keys.chord(Keys.CONTROL, "a"),USERID);
			//�������д
			webDriver.findElement(By.xpath("//*[@id=\'login-passwd\']")).sendKeys(Keys.chord(Keys.CONTROL, "a"),PASSWORD);
			//������ť
			WebElement key = webDriver.findElement(By.xpath("//*[@id=\'gc-box\']/div/div[3]/div[2]"));
			Actions action = new Actions(webDriver);
			//����ƶ���������ť��
			action.moveToElement(key).perform();
			Thread.sleep(300);//�ȴ���֤����س���
			BufferedImage image1;
			//����
            image1 = ImageIO.read(((TakesScreenshot) webDriver).getScreenshotAs(OutputType.FILE));
            //��֤�����Ͻ�ͼƬ
            WebElement kw = webDriver.findElement(By.xpath("//*[@id=\'gc-box\']/div/div[1]/div[2]/div[1]/a[2]/div[1]/div[1]"));
            //��֤�����½�ͼƬ
			WebElement ka = webDriver.findElement(By.xpath("//*[@id=\'gc-box\']/div/div[1]/div[2]/div[1]/a[2]/div[1]/div[52]"));
			//�ӽ�ͼ�зָ������ԭʼ��֤��
			image1 = image1.getSubimage(kw.getLocation().x, kw.getLocation().y, Math.abs(ka.getLocation().x-kw.getLocation().x)+ka.getSize().getWidth(),Math.abs(ka.getLocation().y-kw.getLocation().y)+ka.getSize().getHeight());
			//��갴ס������ť����ʾͼƬȱ��
			action.clickAndHold(key).perform();
			Thread.sleep(50);//�ȴ���ȱ�ڵ���֤����س���
            //�ٴν���
			BufferedImage image2 = ImageIO.read(((TakesScreenshot) webDriver).getScreenshotAs(OutputType.FILE));
			//�ָ����ȱ�ڵ���֤��
			image2 = image2.getSubimage(kw.getLocation().x, kw.getLocation().y, Math.abs(ka.getLocation().x-kw.getLocation().x)+ka.getSize().getWidth(),Math.abs(ka.getLocation().y-kw.getLocation().y)+ka.getSize().getHeight());
			 //��������ƶ�һ�ξ��룬��ξ���ļ���ο�Verify.getTX2()����
			action.moveByOffset(Verify.getTX2(image1,image2), 0).perform();
			//�ɿ�����������֤
			action.release().perform();
			//�ȴ���֤���
			Thread.sleep(500);
			//���û�г��֡���֤ʧ�ܡ���˵����֤�ɹ����˳�ѭ��
			if(!webDriver.getPageSource().contains("��֤ʧ��"))break;
			//��ִ�е����˵����֤ʧ�ܣ�ˢ����������֤
			webDriver.navigate().to("https://passport.bilibili.com/login");
		}
		//����chromedriver.exe���ͷ���Դ
		Runtime.getRuntime().exec("taskkill /F /im " + "chromedriver.exe");
	}

}
