package com.gn.reptile.dome.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.gn.reptile.dome.manage.entity.Quarantine;
import com.gn.reptile.dome.manage.service.QuarantineManageService;
import com.gn.reptile.dome.service.QuarantineService;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.logging.LogType;
import org.openqa.selenium.logging.LoggingPreferences;
import org.openqa.selenium.support.ui.Select;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;

/**
 * 参考文档 https://blog.csdn.net/qq_22003641/article/details/79137327
 */

@Service
public class QuarantineServiceImpl implements QuarantineService {

    private Logger logger = LoggerFactory.getLogger(QuarantineServiceImpl.class);

    @Value("${google.driver.path}")
    private String path;
    private String windowHandles;
    private ChromeDriver chromeDriver = null;
    private List<QuarantineDriver> quarantineDriverDriverList = new ArrayList<>();
    private Lock lock = new ReentrantLock();

    @Autowired
    private QuarantineManageService quarantineManageService;

    public ChromeDriver getChromeDriver() {
        return chromeDriver;
    }




//    public QuarantineDriver initDriver(){
//        System.setProperty("webdriver.chrome.driver", path);
//        ChromeOptions options = new ChromeOptions();
//        options.setPageLoadStrategy(PageLoadStrategy.EAGER);
//        LoggingPreferences logPrefs = new LoggingPreferences();
//        logPrefs.enable(LogType.PERFORMANCE, Level.ALL);
//        options.setExperimentalOption("excludeSwitches", Collections.singletonList("enable-automation"));
//        options.setExperimentalOption("useAutomationExtension", false);
//            chromeDriver = new ChromeDriver(options);
//            chromeDriver.get("https://pbqc.quotabooking.gov.hk/booking/index_hk_tc.jsp");
//            windowHandles = chromeDriver.getWindowHandle();
//            QuarantineDriver qunarDriver = new QuarantineDriver();
//            qunarDriver.chromeDriver = chromeDriver;
//            qunarDriver.setWindowHandles(windowHandles);
//            return qunarDriver;
//    }



    public void asynProcessing(){
        ExecutorService executorService = Executors.newFixedThreadPool(5);
        QueryWrapper<Quarantine> wrapper =new QueryWrapper<>();
        List<Integer> flays = new ArrayList<>();
        flays.add(0);
        flays.add(1);
        wrapper.lambda().in(Quarantine::getFlay,flays).eq(Quarantine::getLockFlag,0);
        List<Quarantine> quarantines = quarantineManageService.list(wrapper);
        quarantines.stream().forEach(quarantine -> {
            //加分布式锁
            CompletableFuture.runAsync(() -> {
                UpdateWrapper<Quarantine> updateWrapper =new UpdateWrapper();
                updateWrapper.lambda().set(Quarantine::getLockFlag,1)
                .set(Quarantine::getUpdateTime, LocalDateTime.now())
                .eq(Quarantine::getId,quarantine.getId());
                index_hk_tc_1(quarantine);
            },executorService);
        });
    }


    public void index_hk_tc_1(Quarantine quarantine) {
        QuarantineDriver qunarDriver = getQuarantineDriver();
        ChromeDriver chromeDriver = qunarDriver.getChromeDriver();
        String windowHandles = qunarDriver.getWindowHandles();
        if (chromeDriver == null) {
            throw new RuntimeException(" The browser is not initialized! ");
        }
        ChromeDriver driver = null;
        try {
            logger.info(path);
            String index_hk_tc = "https://pbqc.quotabooking.gov.hk/booking/index_hk_tc.jsp";
            logger.info("file.encoding:" + System.getProperty("file.encoding"));
            logger.info(windowHandles);
            driver = chromeDriver;
            driver.navigate().to(index_hk_tc);
            Thread.sleep(2*1000);

            //放开开始预约
            String appointment = "gd2_reg";
            JavascriptExecutor jse = (JavascriptExecutor)driver;
            jse.executeScript("document.getElementById('"+appointment+"').className = 'sp1';");
            logger.info("开始预约点击.....");
            driver.findElementById(appointment).click();
            this.index_tc_2( driver,jse, quarantine);

            UpdateWrapper<Quarantine> updateWrapper =new UpdateWrapper();
            updateWrapper.lambda().set(Quarantine::getFlay,2)
                    .eq(Quarantine::getId,quarantine.getId());
        } catch (Exception e) {
            UpdateWrapper<Quarantine> updateWrapper =new UpdateWrapper();
            updateWrapper.lambda().set(Quarantine::getFlay,1)
                    .eq(Quarantine::getId,quarantine.getId());
            e.printStackTrace();
        }finally{
            UpdateWrapper<Quarantine> updateWrapper =new UpdateWrapper();
            updateWrapper.lambda().set(Quarantine::getLockFlag,0)
                    .set(Quarantine::getUpdateTime, LocalDateTime.now())
                    .eq(Quarantine::getId,quarantine.getId());

            qunarDriver.setNum(0);
//            if (driver != null){
//                if(driver.getWindowHandles().size() > 1){
//                    driver.close();
//                    try {
//                        Thread.sleep(100);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//                    driver.switchTo().window(windowHandles);
//                    //driver.quit();
//                }
//            }

        }
    }


    public void index_tc_2(ChromeDriver driver, JavascriptExecutor jse,Quarantine quarantine) throws InterruptedException {
//        String index_tc_2 = "https://pbqc.quotabooking.gov.hk/booking/hk/index_tc.jsp";
//        driver.navigate().to(index_tc_2);
        Thread.sleep(2*1000);
        jse.executeScript("document.getElementsByClassName('input-submit-div disable')[0].className = 'input-submit-div';");

        //下拉框的处理
        String xlk_id = "step_1_ATGC_NATCARDTYPE";
//        Select sel = new Select(driver.findElementById(xlk_id));
//        List<WebElement> options = sel.getOptions();
//        for (WebElement option : options) {
//            System.out.println(option.getText()); //Prints "Option", followed by "Not Option"
//        }

//        //使用name值选中
//        WebElement nameSelectElm = driver.findElementById(xlk_id);
//        Select mySelect= new Select(nameSelectElm);
//        mySelect.selectByVisibleText("菲律賓護照");
        //使用value值选中
        WebElement mySelectElm = driver.findElementById(xlk_id);
        Select mySelect= new Select(mySelectElm);
//        String philippinePassport = "Philippine Passport"; //菲律賓護照
        String philippinePassport =  quarantine.getIdType();
        mySelect.selectByValue(philippinePassport);

        //input框处理
//        String number = "430322199611228712";
        String number = quarantine.getIdNumber();
        String shz_input_id = "step_1_other_documentId";
        driver.findElementById(shz_input_id).clear();
        driver.findElementById(shz_input_id).sendKeys(number);

        //iframe的处理
        driver.switchTo().frame(driver.findElement(By.xpath("//*[@id=\"step_1_captchaCode\"]/div/div/iframe")));
        jse.executeScript("document.getElementById('recaptcha-token').type = 'text';");
        driver.findElementById("recaptcha-token").click();
        driver.switchTo().defaultContent(); //回到原来页面

        Thread.sleep(20*1000);
        //下一步
//        jse.executeScript("document.getElementsByClassName('input-submit-div disable')[0].className = 'input-submit-div';");

        jse.executeScript("document.querySelector('#step_1_booking_form > div.align-right > div:nth-child(1) > input[type=submit]').id = 'abc123456';");
        driver.findElementById("abc123456").click();
        index_tc_3(driver,jse, quarantine);
    }


    public void index_tc_3(ChromeDriver driver, JavascriptExecutor jse,Quarantine quarantine) throws InterruptedException {
//        QuarantineDriver qunarDriver = getQuarantineDriver();
//        ChromeDriver driver = qunarDriver.getChromeDriver();
//        String index_tc_3 = "https://pbqc.quotabooking.gov.hk/booking/hk/index_tc.jsp";
//        driver.navigate().to(index_tc_3);

        //勾选框处理
        Thread.sleep(2*1000);
        String pics_consent = "pics_consent";
        WebElement pics_consent_web = driver.findElementById(pics_consent);
        jse.executeScript("document.getElementById('"+pics_consent+"').click()",pics_consent_web);

        String nr_consent = "nr_consent";
        WebElement nr_consent_web = driver.findElementById(nr_consent);
        jse.executeScript("document.getElementById('"+nr_consent+"').click()",nr_consent_web);

        String gr_consent = "gr_consent";
        WebElement gr_consent_web = driver.findElementById(gr_consent);
        jse.executeScript("document.getElementById('"+gr_consent+"').click()",gr_consent_web);

        String note_2_confirm ="note_2_confirm";
        WebElement note_2_confirm_web = driver.findElementById(note_2_confirm);
        jse.executeScript("document.getElementById('"+note_2_confirm+"').click()",note_2_confirm_web);
        index_tc_4( driver, jse, quarantine);
    }

    public void index_tc_4(ChromeDriver driver, JavascriptExecutor jse,Quarantine quarantine) throws InterruptedException {

        Thread.sleep(2*1000);
        //外傭姓名 input框处理
//        String name = "xaio tong";
        String name = quarantine.getName();
        String step_2_fdh_name_id = "step_2_fdh_name";
        driver.findElementById(step_2_fdh_name_id).clear();
        driver.findElementById(step_2_fdh_name_id).sendKeys(name);

        //No special preference
        //外傭飲食偏好 下拉框处理
        String meal_id = "step_2_fdh_meal_preference";
        WebElement meal_selectElm = driver.findElementById(meal_id);
        Select meal_select= new Select(meal_selectElm);
//        String meal_value = "No special preference";
        String meal_value = quarantine.getDietPreference();
        meal_select.selectByValue(meal_value); //外傭飲食偏好

        //外傭能以英語溝通 单选框处理
//        String englishCommunication_id = "step_2_communicate_in_english_yes";
        String englishCommunication_id = quarantine.getEnglishCommunication();
        driver.findElementById(englishCommunication_id).click();

        //外傭能以廣東話溝通
//        String cantoneseCommunication_id = "step_2_communicate_in_cantonese_yes";
        String cantoneseCommunication_id = quarantine.getCantoneseCommunication();
        driver.findElementById(cantoneseCommunication_id).click();

        //外傭能以普通話溝通
//        String communicateInMandarin_id = "step_2_communicate_in_putonghua_yes";
        String communicateInMandarin_id = quarantine.getMandarinCommunicate();
        driver.findElementById(communicateInMandarin_id).click();

        //聯絡人姓名
//        String contactName = "xaio yu";
        String contactName = quarantine.getContactName();
        String step_2_name_of_contact_person_id = "step_2_name_of_contact_person";
        driver.findElementById(step_2_name_of_contact_person_id).clear();
        driver.findElementById(step_2_name_of_contact_person_id).sendKeys(contactName);

        //聯絡人電郵地址
//        String contactEmail = "6326597447@qq.com";
        String contactEmail = quarantine.getContactEmail();
        String step_2_email_of_contact_person_id = "step_2_email_of_contact_person";
        driver.findElementById(step_2_email_of_contact_person_id).clear();
        driver.findElementById(step_2_email_of_contact_person_id).sendKeys(contactEmail);

        //請選擇指定檢疫設施
        String quarantineFacility_id = "step_2_entrance_port_2";
        driver.findElementById(quarantineFacility_id).click();

        //強制檢疫開始日期
        String jy_date_id = "step_2_CBP_ID";
        jse.executeScript("document.querySelectorAll('input[name="+jy_date_id+"]')[0].disabled=false");
        jse.executeScript("document.querySelectorAll('input[name="+jy_date_id+"]')[0].id = 'jqzl123456';");
        driver.findElementById("jqzl123456").click();

        //聯絡人手提電話號碼
//        String contactPhoneNumber = "14253269";
        String contactPhoneNumber = quarantine.getContactPhone();
        String  step_2_tel_for_sms_notif_id = "step_2_tel_for_sms_notif";
        driver.findElementById(step_2_tel_for_sms_notif_id).clear();
        driver.findElementById(step_2_tel_for_sms_notif_id).sendKeys(contactPhoneNumber);

        //請再次輸入聯絡人手提電話號碼
        String contactPhoneNumberConfirm = contactPhoneNumber;
        String  step_2_tel_for_sms_notif_confirm_id = "step_2_tel_for_sms_notif_confirm";
        driver.findElementById(step_2_tel_for_sms_notif_confirm_id).clear();
        driver.findElementById(step_2_tel_for_sms_notif_confirm_id).sendKeys(contactPhoneNumberConfirm);

        //接收短訊通知的語言
        String smsLanguage_id = "step_2_language_preference_english";
        driver.findElementById(smsLanguage_id).click();

        //下一步
        String nextControlConfir_id = "step_2_form_control_confirm";
        driver.findElementById(nextControlConfir_id).click();

        Thread.sleep(2*1000);
        //人机识别
        driver.switchTo().frame(driver.findElement(By.xpath("//*[@id=\"step_2_captchaCode\"]/div/div/iframe")));
        jse.executeScript("document.getElementById('recaptcha-token').type = 'text';");
        driver.findElementById("recaptcha-token").click();
        driver.switchTo().defaultContent(); //回到原来页面

        Thread.sleep(30*1000);
        //确认
        logger.info("点击确认.....");
        driver.findElement(By.xpath("//*[@id=\"step_2_form_control\"]/div[1]/input")).click();


    }



    @Override
    public void initChromeDriver(String dataDir, Integer num) {
        init(dataDir, num);
    }

    public void init(String dataDir, Integer num) {
        System.setProperty("webdriver.chrome.driver", path);
        ChromeOptions options = new ChromeOptions();
        options.setPageLoadStrategy(PageLoadStrategy.EAGER);
        LoggingPreferences logPrefs = new LoggingPreferences();
        logPrefs.enable(LogType.PERFORMANCE, Level.ALL);
        options.setExperimentalOption("excludeSwitches", Collections.singletonList("enable-automation"));
        options.setExperimentalOption("useAutomationExtension", false);
        quarantineDriverDriverList.clear();
        for (int i = 0; i < num; i++) {
            chromeDriver = new ChromeDriver(options);
            chromeDriver.get("https://pbqc.quotabooking.gov.hk/booking/index_hk_tc.jsp");
            windowHandles = chromeDriver.getWindowHandle();
            QuarantineDriver qunarDriver = new QuarantineDriver();
            qunarDriver.chromeDriver = chromeDriver;
            qunarDriver.setWindowHandles(windowHandles);
            quarantineDriverDriverList.add(qunarDriver);
        }
    }


    private QuarantineDriver getQuarantineDriver() {
        try {
            lock.lock();
            Collections.sort(quarantineDriverDriverList);
            QuarantineDriver qunarDriver = quarantineDriverDriverList.get(0);
            if (qunarDriver.getNum() != 0) {

                for (int i = 0; i < 10; i++) {
                    if (qunarDriver.getNum() == 0) {
                        qunarDriver.setNum(1);
                        break;
                    }
                    Thread.sleep(1000);
                }
                if (qunarDriver.getNum() != 1) {
                    throw new RuntimeException("return error errCode ");
                }
            } else {
                qunarDriver.setNum(1);
            }
            return qunarDriver;
        } catch (Exception e) {
            return null;
        } finally {
            lock.unlock();
        }

    }


    class QuarantineDriver implements Comparable<QuarantineDriver> {
        private ChromeDriver chromeDriver;
        private String windowHandles;
        private Integer num = 0;

        public ChromeDriver getChromeDriver() {
            return chromeDriver;
        }

        public void setChromeDriver(ChromeDriver chromeDriver) {
            this.chromeDriver = chromeDriver;
        }

        public String getWindowHandles() {
            return windowHandles;
        }

        public void setWindowHandles(String windowHandles) {
            this.windowHandles = windowHandles;
        }

        public Integer getNum() {
            return num;
        }

        public void setNum(Integer num) {
            this.num = num;
        }

        @Override
        public int compareTo(QuarantineDriver o) {
            return this.num - o.num;
        }
    }

}

