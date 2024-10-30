package com.konsol.beatstream.service.audioPlugins.Spotify;

import static com.konsol.beatstream.service.audioPlugins.Spotify.SpotifyDownloader.getSpotifyLinkType;

import com.konsol.beatstream.config.AppSettingsConfiguration;
import java.io.IOException;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.nio.file.Paths;
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
public class SpotifyPlaylistInfo {

    String startupPath = Paths.get("").toAbsolutePath().toString();
    String chromedriver;
    public String PlayListTitle = "";
    private static final String TRACK_REGEX = "^https://open\\.spotify\\.com/track/([a-zA-Z0-9]+)$";
    private static final String PLAYLIST_REGEX = "^https://open\\.spotify\\.com/playlist/([a-zA-Z0-9]+)$";

    public SpotifyPlaylistInfo() {
        try {
            chromedriver = AppSettingsConfiguration.getSettings()
                .getProperty("beatstream.settings.plugins.chromedriver_path", startupPath + "\\plugins\\chromedriver.exe");
        } catch (Exception e) {
            chromedriver = startupPath + "\\plugins\\chromedriver.exe";
        }
    }

    public PlaylistDetails getPlaylistDetails(String url) throws Exception {
        List<String> linksFound = new ArrayList<>();
        System.setProperty("webdriver.chrome.driver", chromedriver); // Replace with your chromedriver path

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

        if ("Playlist".equals(getSpotifyLinkType(url))) {
            System.setOut(new PrintStream(System.out, true, "UTF-8"));
            driver.manage().timeouts().pageLoadTimeout(30, TimeUnit.SECONDS);
            driver.get(url);

            Thread.sleep(10000);
            List<WebElement> items = new ArrayList<>();

            JavascriptExecutor js = (JavascriptExecutor) driver;

            WebElement targetElement = driver.findElement(By.xpath("//div[@data-testid='top-sentinel' and @role='presentation']"));
            WebElement PlayListName = driver.findElement(By.cssSelector("span[data-testid=\"entityTitle\"]"));
            String className = targetElement.getAttribute("class");
            List<WebElement> targetElements = driver.findElements(By.cssSelector("div[data-testid=\"tracklist-row\"]"));
            int currentSize = targetElements.size();
            int prevSize = 0;
            String className1 = targetElements.get(0).getAttribute("class").split(" ")[1];
            while (prevSize != currentSize) {
                prevSize = currentSize;
                ((JavascriptExecutor) driver).executeScript(
                        " var container = document.getElementsByClassName('" +
                        className +
                        "');\n" +
                        "        \n" +
                        "        var items = document.getElementsByClassName('" +
                        className1 +
                        "');\n" +
                        "        \n" +
                        "        var targetItem = items[" +
                        currentSize +
                        "]; // Index is zero-based\n" +
                        "        \n" +
                        "        targetItem.scrollIntoView({ behavior: 'smooth', block: 'center' });"
                    ); // Shows an alert box

                Thread.sleep(2000); // Wait for the page to load
                List<WebElement> targetElementsx = driver.findElements(By.cssSelector("div[data-testid=\"tracklist-row\"]"));
                currentSize = targetElementsx.size();
            }
            PlayListTitle = PlayListName.getText();
            List<WebElement> targetElementsss = driver.findElements(By.cssSelector("div[data-testid=\"tracklist-row\"]"));

            for (WebElement ele : targetElementsss) {
                WebElement eleFound = ele.findElement(By.cssSelector("a[data-testid=\"internal-track-link\"]"));
                System.out.println(eleFound.getAttribute("href"));
                String spotLink = eleFound.getAttribute("href");
                if (isSpotifyLink(spotLink)) {
                    linksFound.add(eleFound.getAttribute("href"));
                }
            }

            Thread.sleep(15000);
        } else {
            throw new Exception(url + " Is Not Spotify list");
        }

        // Close the WebDriver
        driver.quit();

        return new PlaylistDetails(this.PlayListTitle, linksFound);
    }

    public static boolean isSpotifyLink(String url) {
        return url.matches(TRACK_REGEX) || url.matches(PLAYLIST_REGEX);
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
