package com.konsol.beatstream.service.audioPlugins.youtube;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

public class YouTubeVideoInfoScraper {

    // Regex for validating a YouTube URL
    private static final String YOUTUBE_URL_REGEX = "^(https?://)?(www\\.)?(youtube\\.com|youtu\\.?be)/.+$";

    // Validate YouTube URL using regex
    public static boolean isValidYouTubeUrl(String url) {
        Pattern pattern = Pattern.compile(YOUTUBE_URL_REGEX);
        Matcher matcher = pattern.matcher(url);
        return matcher.matches();
    }

    // Extract video ID from YouTube URL
    public static String getVideoIdFromUrl(String url) {
        String videoId = null;
        String[] splitUrl = url.split("(v=|youtu.be/|embed/|watch\\?v=|watch\\?.+&v=)");
        if (splitUrl.length > 1) {
            videoId = splitUrl[1].split("&")[0];
        }
        return videoId;
    }

    // Fetch YouTube video information by scraping the page
    public static String fetchYouTubeVideoTitle(String videoUrl) throws IOException {
        if (!isValidYouTubeUrl(videoUrl)) {
            System.out.println("Invalid YouTube URL");
            throw new IOException("Invalid YouTube URL");
        }

        // Connect to the YouTube page
        Document doc = Jsoup.connect(videoUrl).get();

        // Extract video title
        Element titleElement = doc.selectFirst("meta[name=title]");
        String title = titleElement != null ? titleElement.attr("content") : "No title found";

        // Extract video description
        Element descriptionElement = doc.selectFirst("meta[name=description]");
        String description = descriptionElement != null ? descriptionElement.attr("content") : "No description found";

        // Extract video views (if available in the page content)
        String views = "No view count found";
        Element viewCountElement = doc.selectFirst("meta[itemprop=interactionCount]");
        if (viewCountElement != null) {
            views = viewCountElement.attr("content") + " views";
        }

        // Print the extracted information
        System.out.println("Title: " + title);
        System.out.println("Description: " + description);
        System.out.println("Views: " + views);
        return title;
    }

    public static void main(String[] args) {
        String videoUrl = "https://www.youtube.com/watch?v=dQw4w9WgXcQ"; // Replace with actual video URL

        try {
            fetchYouTubeVideoTitle(videoUrl);
        } catch (IOException e) {
            System.err.println("Error fetching video info: " + e.getMessage());
        }
    }
}
