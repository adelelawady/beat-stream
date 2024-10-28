package com.konsol.beatstream.service.audioPlugins.youtube;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.stereotype.Component;

@Component
public class YouTubePlaylistInfo {

    public PlaylistDetails getPlaylistDetails(String url) throws IOException {
        List<String> videoIdList = new ArrayList<>();
        // Path to the ChromeDriver executable
        System.setProperty(
            "webdriver.chrome.driver",
            "C:\\Users\\adel\\Downloads\\Compressed\\chromedriver-win64_2\\chromedriver-win64\\chromedriver.exe"
        ); // Replace with your chromedriver path

        String tagName = "ytd-playlist-panel-video-renderer"; // Replace 'div' with any other tag you want to extract

        // Setup Chrome options for headless mode (optional)
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless"); // Run in headless mode (without a GUI)
        options.addArguments("--disable-gpu");
        options.addArguments("--no-sandbox");
        options.addArguments("--incognito"); // Optional: use incognito mode
        options.addArguments(
            "user-agent=Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/114.0.0.0 Safari/537.36"
        );
        options.addArguments("--remote-allow-origins=*");
        options.addArguments("–-charset=UTF-8"); // Set the charset to UTF-8

        // Create WebDriver instance
        WebDriver driver = new ChromeDriver(options);

        try {
            // Load the page using Selenium
            //driver.manage().timeouts().pageLoadTimeout(30, TimeUnit.SECONDS);
            driver.get(url);

            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));
            WebElement playlistElement = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(tagName)));

            // Retrieve the HTML source after scripts are executed
            String pageSource = driver.getPageSource();

            // Use WebDriverWait to wait until the playlist element is visible

            // Parse the HTML using Jsoup
            Document document = Jsoup.parse(pageSource, StandardCharsets.UTF_8.name());

            // Extract elements based on the tagToWaitFor (e.g., div.playlist)
            Elements elements = document.select("ytd-playlist-panel-video-renderer"); // Adjust this if needed

            // Loop through each element and extract <a> tags with their href attributes
            for (Element element : elements) {
                // Find all <a> tags inside the current element
                Elements links = element.getElementsByTag("a");

                /*
                try{
                     Element title = element.getElementById("video-title");
                    String titlePrint =  title.attr("title");
                    System.out.println(titlePrint);
                }catch(Exception e){

                }
                */

                for (Element link : links) {
                    String href = link.attr("href");

                    String videoId = extractVideoId(href);
                    if (!videoIdList.contains(videoId)) {
                        videoIdList.add(videoId);
                        // System.out.println("Link found: " +extractVideoId(href));
                    }
                }
            }
            driver.quit();
            videoIdList.forEach(System.out::println);
            return new PlaylistDetails(YouTubeVideoInfoScraper.fetchYouTubeVideoTitle(url), videoIdList);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return new PlaylistDetails(url, videoIdList);
        }
    }

    private static String extractVideoId(String url) {
        String regex = "[?&]v=([^&]+)";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(url);

        if (matcher.find()) {
            return matcher.group(1); // Return the captured group
        }
        return null; // Return null if no match is found
    }

    public static class PlaylistDetails {

        private final String title;
        private final List<String> trackUrls;

        public PlaylistDetails(String title, List<String> trackUrls) {
            this.title = title;
            this.trackUrls = trackUrls;
        }

        public String getTitle() {
            return title;
        }

        public List<String> getTrackUrls() {
            return trackUrls;
        }

        @Override
        public String toString() {
            return "PlaylistDetails{" + "title='" + title + '\'' + ", trackUrls=" + trackUrls + '}';
        }
    }
}
