import java.util.HashMap;
import java.util.Random;

public class ShortenURL {
    private HashMap<String, String> keyMap; //map longUrl to shortUrl
    private HashMap<String, String> valueMap; //map shortUrl to longUrl
    private char[] charts;  //charts used for hashing
    private Random random;  //RNG that creates key
    private int keyLength; //the length of short url, defaults to 8
    private String domain; //the default domain for the shortUrl

    public ShortenURL() {
        keyMap = new HashMap<>();
        valueMap = new HashMap<>();
        charts = new char[62];
        generateCharts(charts);
        random = new Random();
        keyLength = 8;
        domain = "www.tinyurl.com/";
    }

    public ShortenURL(int length, String domain) {
        this();
        keyLength = length;
        if (!domain.isEmpty()) this.domain = sanitizeUrl(domain);
    }

    public String shortenUrl(String longUrl) {
        String shortUrl = "";
        if (validateUrl(longUrl)) {
            longUrl = sanitizeUrl(longUrl);
            if (valueMap.containsKey(longUrl)) shortUrl = domain + "/" + valueMap.get(longUrl);
            else shortUrl = domain + "/" + getKey(longUrl);
        }
        return shortUrl;
    }

    public String expandUrl(String shortUrl) {
        String key = shortUrl.substring(this.domain.length() + 1);
        return keyMap.get(key);
    }

    private String getKey(String longUrl) {
        String key = "";
        boolean flag = true;
        while (flag) {
            for (int i = 0; i < keyLength; i++) {
                key += charts[random.nextInt(62)];
            }
            if (!keyMap.containsKey(key)) flag = false;
            else key = "";
        }
        keyMap.put(key, longUrl);
        valueMap.put(longUrl, key);

        return key;
    }

    private boolean validateUrl(String longUrl) {
        return !longUrl.isEmpty(); //more validation needed in production
    }

    private String sanitizeUrl(String longUrl) {
        if (longUrl.substring(0, 7).equals("http://")) longUrl = longUrl.substring(7);
        if (longUrl.substring(0, 8).equals("https://")) longUrl = longUrl.substring(8);
        if (longUrl.charAt(longUrl.length() - 1) == '/') longUrl = longUrl.substring(0, longUrl.length() - 1);
        return longUrl;
    }

    private void generateCharts(char[] charts) {
        for (int i = 0; i < 62; i++) {
            int curr;
            if (i < 10) curr = i + 48;
            else if (i >= 10 && i <= 35) curr = i + 55;
            else curr = i + 61;
            charts[i] = (char) curr;
        }
    }

    public static void main(String[] args) {
        ShortenURL u = new ShortenURL(5, "http://www.tinyurl.com/");
        String urls[] = { "www.google.com/", "www.google.com",
                "http://www.yahoo.com", "www.yahoo.com/", "www.amazon.com",
                "www.amazon.com/page1.php", "www.amazon.com/page2.php",
                "www.flipkart.in", "www.rediff.com", "www.techmeme.com",
                "www.techcrunch.com", "www.lifehacker.com", "www.icicibank.com" };
        for (String url : urls) {
            System.out.println("URL: " + url + "\tTiny: "
                    + u.shortenUrl(url) + "\tExpanded: "
                    + u.expandUrl(u.shortenUrl(url)));
        }
    }
}
