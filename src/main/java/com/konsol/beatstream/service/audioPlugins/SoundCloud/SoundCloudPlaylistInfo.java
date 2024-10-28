package com.konsol.beatstream.service.audioPlugins.SoundCloud;

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
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.stereotype.Component;

@Component
public class SoundCloudPlaylistInfo {

    public PlaylistDetails getPlaylistDetails(String url) throws IOException {
        List<String> videoIdList = new ArrayList<>();
        // String playlistUrl = "https://soundcloud.com/chargetogame8/sets/2024-mp3-mp3"; // Replace with your playlist URL
        // Path to the ChromeDriver executable
        System.setProperty(
            "webdriver.chrome.driver",
            "C:\\Users\\adel\\Downloads\\Compressed\\chromedriver-win64_2\\chromedriver-win64\\chromedriver.exe"
        ); // Replace with your chromedriver path

        // URL of the webpage to download
        // Tag name to search for
        // String tagName = "ytd-playlist-panel-video-renderer";  // Replace 'div' with any other tag you want to extract

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
        driver.get(url);

        // Wait for the cookie consent button to be clickable and click it
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        try {
            WebElement acceptCookiesButton = wait.until(ExpectedConditions.elementToBeClickable(By.id("onetrust-accept-btn-handler")));
            acceptCookiesButton.click();
            System.out.println("Cookies accepted.");
        } catch (Exception e) {
            System.out.println("Cookie consent button not found or clickable: " + e.getMessage());
        }

        // Scroll down to the end of the page to load all items
        long lastHeight = (long) ((JavascriptExecutor) driver).executeScript("return document.body.scrollHeight");

        while (true) {
            // Scroll down to the bottom
            ((JavascriptExecutor) driver).executeScript("window.scrollTo(0, document.body.scrollHeight);");

            // Wait for new items to load
            try {
                Thread.sleep(2000); // Adjust this time as necessary
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            // Calculate new scroll height and compare it with the last scroll height
            long newHeight = (long) ((JavascriptExecutor) driver).executeScript("return document.body.scrollHeight");
            if (newHeight == lastHeight) {
                break; // Break the loop if no new content is loaded
            }
            lastHeight = newHeight;
        }

        // Wait a moment for the last items to load
        try {
            Thread.sleep(2000); // Adjust this time as necessary
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Grab all track items
        List<WebElement> trackItems = driver.findElements(By.cssSelector(".trackList__item .trackItem__content"));

        // Print out inner HTML and href of each track item
        for (WebElement item : trackItems) {
            // Print inner HTML
            // System.out.println(item.getAttribute("innerHTML"));

            // Find the <a> tag inside trackItem__content
            WebElement link = item.findElements(By.tagName("a")).get(1);

            // Get href attribute and print it
            String href = link.getAttribute("href");

            videoIdList.add(href);
            //System.out.println("Link: " + href);
        }

        // Close the WebDriver
        driver.quit();

        return new PlaylistDetails(getSongNameFromUrl(url), videoIdList);
    }

    public static String getSongNameFromUrl(String url) throws IOException {
        // Fetch the document from the URL
        Document document = Jsoup.connect(url).get();

        // Use a CSS selector to find the song title in the HTML
        Elements titleElements = document.select("meta[property='og:title']");

        if (!titleElements.isEmpty()) {
            Element titleElement = titleElements.first();
            return titleElement.attr("content"); // Extract the content attribute
        }

        return "Song title not found.";
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
