package mybase.controllers;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.java.Log;
import me.postaddict.instagram.scraper.model.Account;
import me.postaddict.instagram.scraper.model.Media;
import me.postaddict.instagram.scraper.model.PageObject;
import mybase.domain.InstProfile;
import mybase.repo.InstRepo;
import mybase.services.ProfileService;
import org.apache.commons.lang3.StringUtils;
import org.jinstagram.auth.InstagramAuthService;
import org.jinstagram.auth.model.Token;
import org.jinstagram.auth.model.Verifier;
import org.jinstagram.auth.oauth.InstagramService;
import org.openqa.selenium.*;
import org.openqa.selenium.Point;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.interactions.Coordinates;
import org.openqa.selenium.interactions.Locatable;
import org.openqa.selenium.logging.LogEntries;
import org.openqa.selenium.logging.LogType;
import org.openqa.selenium.logging.LoggingPreferences;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;
import java.awt.*;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.io.File;
import java.util.*;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

@Log
@Data
@RestController
@AllArgsConstructor
@RequestMapping("/api/profile")
public class ProfileController {
    private final ProfileService profileService;
    private final InstRepo instRepo;

    //private static final Token EMPTY_TOKEN = null;

    @PostMapping("/instagram/loadFollowersList")
    private InstProfile/*Map<String, String>*/ loadFollowersList(@RequestBody String instUsername) throws AWTException {
        log.info("CHROME loadFollowersList");

        /*ChromeOptions options = new ChromeOptions();
        DesiredCapabilities cap = DesiredCapabilities.chrome();
        cap.setCapability(ChromeOptions.CAPABILITY, options);

        LoggingPreferences logPrefs = new LoggingPreferences();
        logPrefs.enable(LogType.PERFORMANCE, Level.ALL);
        //logPrefs.enable(LogType.N, Level.ALL);
        cap.setCapability(CapabilityType.SUPPORTS_NETWORK_CONNECTION, logPrefs);*/

        /*PROXY NEW EACH REQUEST!!!*/

        WebDriver driver = new ChromeDriver(/*cap*/);
        //driver.manage().window().maximize();
        driver.manage().window().setPosition(new Point(-2000, 0));

        ((JavascriptExecutor) driver).executeScript("window.open('https://google.com');");

        String url = "https://www.instagram.com/accounts/login/";
        driver.get(url);

        WebElement login = (new WebDriverWait(driver, 10))
                .until(ExpectedConditions.presenceOfElementLocated(By.name("username")));
        login.sendKeys("creep_waves");

        WebElement password = (new WebDriverWait(driver, 10))
                .until(ExpectedConditions.presenceOfElementLocated(By.name("password")));
        password.sendKeys("QwertyRoute01");
        password.sendKeys(Keys.ENTER);

        new WebDriverWait(driver, 10).until(ExpectedConditions.presenceOfElementLocated(By.className("piCib")));
        String url1 = "https://www.instagram.com/" + instUsername;
        driver.get(url1);

        WebElement followersButton = (new WebDriverWait(driver, 10))
                .until(ExpectedConditions.presenceOfElementLocated(By.xpath("//a[contains(@href, '/romarioagrow/followers/')]")));
        followersButton.click();

        driver.switchTo().activeElement();

        WebElement followersDIV = (new WebDriverWait(driver, 10))
                .until(ExpectedConditions.presenceOfElementLocated(By.className("PZuss")));

        /*Get User Data*/
        Map<String, String> followersData = new HashMap<>();
        List<WebElement> li = followersDIV.findElements(By.tagName("li"));
        li.forEach(webElement -> {
            List<String> userData = Arrays.asList(webElement.getText().split("\n"));
            String userName = userData.get(0);
            String fullName = userData.get(1);

            System.out.println();
            log.info("strings: " + userData.toString());
            log.info("userName: " + userName);
            log.info("fullName: " + fullName);
            followersData.putIfAbsent(userName, fullName);
            {
                //webElement.getCssValue()
                /*WebElement div1 = webElement.findElement(By.tagName("div"));
                log.info(div1.toString());
                List<WebElement> webElements = webElement.findElements(By.tagName("div"));
                for (WebElement element : webElements) {
                }*/
                /*WebElement div2 = div1.findElement(By.tagName("div"));
                log.info(div2.toString());
                WebElement div3 = div2.findElement(By.tagName("div"));
                log.info(div2.toString());
                WebElement span = div3.findElement(By.tagName("span"));
                log.info(span.toString());*/
            }
        });

        //log.info(driver.getPageSource());
        String followersMeta = StringUtils.substringBetween(driver.getPageSource(),"<meta content=\""," подписчиков,");
        String followingMeta = StringUtils.substringBetween(driver.getPageSource(),"подписчиков, "," подписок,");

        int userFollowersCount;
        int followersAmount = Integer.parseInt(followersMeta);

        log.info("followersMeta: " + followersMeta);
        log.info("followingMeta: " + followingMeta);

        Coordinates cor= ((Locatable) followersDIV).getCoordinates();
        cor.inViewPort();

        while (followersData.size() < followersAmount) {
            try
            {
                cor.inViewPort();
                //Thread.sleep(100);

                ///refreshFollowersDIV();
                followersDIV = (new WebDriverWait(driver, 10)).until(ExpectedConditions.presenceOfElementLocated(By.className("PZuss")));
                li = followersDIV.findElements(By.tagName("li"));

                li.forEach(webElement -> {
                    List<String> userData = Arrays.asList(webElement.getText().split("\n"));
                    String userName = userData.get(0);///.replaceAll("\"","");
                    String fullName = userData.get(1);

                    System.out.println();
                    log.info("strings: " + userData.toString());
                    log.info("userName: " + userName);
                    log.info("fullName: " + fullName);

                    if (userData.size() == 3) {
                        followersData.putIfAbsent(userName, fullName);
                    }
                    else followersData.putIfAbsent(userName, "");
                    //followersData.putIfAbsent(userName, fullName);
                });
                log.info("size: " + followersData.size());
            }
            catch (Exception ex) {
                ex.printStackTrace();
            }
        }


        InstProfile instProfile = instRepo.findById(instUsername).orElseGet(() -> {
            return new InstProfile(instUsername);
        });

        instProfile.setFollowers(followersData);
        instRepo.save(instProfile);
        log.info(instProfile.toString());

        return instProfile;
    }


    @PostMapping("/instagram/loadInstFollows")
    private LinkedList<Collection> loadInstFollows(@RequestBody String instUsername) {
        log.info("loadInstFollows");
        return profileService.loadInstFollows(instUsername);
    }

    @PostMapping("/instagram/loadInstPosts")
    private PageObject<Media> loadInstPosts(@RequestBody String instUsername) {
        log.info("loadInstPosts");
        return profileService.loadInstPosts(instUsername);
    }

    @PostMapping("/instagram/loadInstProfile")
    private Account loadInstProfile(@RequestBody String instUsername) {
        log.info("loadInstProfile");
        return profileService.instApiAccount(instUsername);
    }
}
