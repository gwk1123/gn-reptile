package com.gn.reptile.dome.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.gn.reptile.dome.manage.entity.Quarantine;
import com.gn.reptile.dome.manage.service.QuarantineManageService;
import com.gn.reptile.dome.service.QuarantineService;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
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
import org.springframework.util.CollectionUtils;

import java.time.LocalDateTime;
import java.util.*;
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

    private static Logger logger = LoggerFactory.getLogger(QuarantineServiceImpl.class);

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


    @Override
    public void asynProcessing(){
        ExecutorService executorService = Executors.newFixedThreadPool(3);
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
            this.quarantine_2( driver,jse, quarantine);


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


    public void quarantine_2(ChromeDriver driver, JavascriptExecutor jse,Quarantine quarantine){
        boolean flag = true;
        while (flag){
            try {
                logger.info("执行2......");
                index_tc_2( driver,  jse, quarantine);
                logger.info("执行2完成......");
                this.index_tc_3( driver,jse, quarantine);
                this.index_tc_4( driver,jse, quarantine);
                flag = false;
            }catch (Exception e){
                //刷新当前页面
                driver.navigate().refresh();
                logger.info("重新执行2......异常:{}",e.getMessage());
                flag = true;
            }
        }
    }


    public void index_tc_2(ChromeDriver driver, JavascriptExecutor jse,Quarantine quarantine) throws InterruptedException {
//        String index_tc_2 = "https://pbqc.quotabooking.gov.hk/booking/hk/index_tc.jsp";
//        driver.navigate().to(index_tc_2_url);
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

        Thread.sleep(10*1000);
        //下一步
//        jse.executeScript("document.getElementsByClassName('input-submit-div disable')[0].className = 'input-submit-div';");

        jse.executeScript("document.querySelector('#step_1_booking_form > div.align-right > div:nth-child(1) > input[type=submit]').id = 'abc123456';");
        driver.findElementById("abc123456").click();
//        index_tc_3(driver,jse, quarantine);
    }


    public void quarantine_3(ChromeDriver driver, JavascriptExecutor jse,Quarantine quarantine){
        boolean flag = true;
        while (flag){
            try {
                logger.info("执行3......");
                index_tc_3( driver,  jse, quarantine);
                logger.info("执行3完成......");
                flag = false;
            }catch (Exception e){
                logger.info("重新执行3......异常:{}",e);
                flag = true;
            }
        }
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
//        index_tc_4( driver, jse, quarantine);
    }


    public void quarantine_4(ChromeDriver driver, JavascriptExecutor jse,Quarantine quarantine){
        boolean flag = true;
        while (flag){
            try {
                logger.info("执行4......");
                index_tc_4( driver,  jse, quarantine);
                logger.info("执行4完成......");
                flag = false;
            }catch (Exception e){
                logger.info("重新执行4......异常:{}",e);
                flag = true;
            }
        }
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



    public static void main(String[] args) throws InterruptedException {
        getWebsite1();
    }



    public static void getWebsite1() throws InterruptedException {

        //调用chrome driver
        System.setProperty("webdriver.chrome.driver", "D:/chromedriver/chromedriver_win32/chromedriver.exe");
        ChromeOptions chromeOptions = new ChromeOptions();
        chromeOptions.addArguments("headless"); //不弹出浏览器
        //调用chrome
        ChromeDriver driver = new ChromeDriver(chromeOptions);
        //调整高度
        driver.executeScript("window.scrollTo(0, document.body.scrollHeight);");
        //获取网址
        driver.get("https://www.news.gov.hk/chi/categories/covid19/index.html");

        //下拉框的处理
        String year_id = "record-year-filter";
        Select year_sel = new Select(driver.findElementById(year_id));
        List<WebElement> year_options = year_sel.getOptions();
        for (WebElement option : year_options) {
            System.out.println(option.getText());
        }
        WebElement yearSelectElm = driver.findElementById(year_id);
        yearSelectElm.sendKeys("2021");

        //下拉框的处理
        String month_id = "record-month-filter";
        Select month_sel = new Select(driver.findElementById(month_id));
        List<WebElement> month_options = month_sel.getOptions();
        for (WebElement option : month_options) {
            System.out.println(option.getText());
        }

        WebElement monthSelectElm = driver.findElementById(month_id);
        monthSelectElm.sendKeys("12月");

        String search_id = "mobileTimeLineSearch";
        System.out.println("点击搜索.....");
        driver.findElementById(search_id).click();
        Thread.sleep(4000);
        List<String> titleHtmls = getPage( driver);
        if(!CollectionUtils.isEmpty(titleHtmls)){
            System.out.println("titleHtmls:"+JSON.toJSONString(titleHtmls));
            titleHtmls.stream().filter(Objects::nonNull).forEach(htmlUrl ->{
                extractContent( driver, htmlUrl);
            });
        }
        //关闭driver
        driver.close();
    }

    /**
     * 分页
     */
    public static List<String> getPage(ChromeDriver driver) throws InterruptedException {

        String next_class = "card-pager-next"; //下一页

        String total_xp= "/html/body/main/section/div[2]/div[2]/span[2]"; //获取总页数
        String totalStr = driver.findElement(By.xpath(total_xp)).getText();
        Integer total = Integer.valueOf(totalStr.replaceAll("/","").replaceAll(" ",""));
        System.out.println("total:"+total);

        JavascriptExecutor jse = (JavascriptExecutor)driver;
        String currentStr= (String) jse.executeScript("return document.querySelector(\"input[name='page']\").value;");
        Integer current = Integer.valueOf(currentStr);
        System.out.println("current:"+current);

        List<String> titleHtmls =new ArrayList<>();
        for(int i=current;i<=total;i++ ){
            logger.info("当前页:{},总页数:{}",i,total);
            List<String> html =getHtml( driver);
            titleHtmls.addAll(html);
//            driver.findElement(By.className(next_class)).click();
            WebElement element= driver.findElement(By.className(next_class));
            driver.executeScript("arguments[0].click();", element);
            Thread.sleep(3000);
        }
        return titleHtmls;
    }


    /**
     * 获取每页中有多少资讯
     * @param driver
     * @return
     * @throws InterruptedException
     */
    public static List<String> getHtml(ChromeDriver driver){
        List<String> hrefs = new ArrayList<>();
        String item_class = "news-item";
        List<WebElement> elements = driver.findElements(By.className(item_class));
        for (WebElement element : elements){
            String href = element.findElement(By.linkText("全文")).getAttribute("href");
            hrefs.add(href);
        }
        return hrefs;
    }

    /**
     * 提取内容
     */
    public static void extractContent(ChromeDriver driver, String url) {
        try {
            driver.navigate().to(url);
            Thread.sleep(4000);
            String html = driver.getPageSource();
//            System.out.println("html:" + html);
            Document document = Jsoup.parse(html);

            //标题
            Element h1Element = document.select("div[class=row]").select("h1[class=news-title h4 font-weight-bold col-9 col-sm-10 col-xl-12 mb-0]").get(0);
            String h1Title = h1Element.text();
            System.out.println("h1Title:"+h1Title);

            //发布日期
            Element timeElement = document.select("div[class=d-flex flex-wrap mb-3]").select("span[class=align-middle news-date text-nowrap mr-3]").get(0);
            String release_time = timeElement.text();
            System.out.println("release_time:"+release_time);

            List<Element> contentElements = document.select("div[class=newsdetail-content mt-3]");
            contentElements.stream().filter(Objects::nonNull).forEach(element -> {
                String contentStr = element.text();
                System.out.println("contentStr:"+contentStr);
            });
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


}

