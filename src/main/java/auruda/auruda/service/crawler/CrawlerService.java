package auruda.auruda.service.crawler;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Consumer;

/**
 * 웹 크롤러 서비스 (행사, 축제, 콘서트)
 */
@Service
public class CrawlerService {

    // 기본값 제거: 비어있으면 Selenium Manager 사용
    @Value("${selenium.chrome.driver-path:}")
    private String chromeDriverPath;

    /**
     * [서비스 로직]
     * 축제 데이터 크롤링
     * @param region 지역
     * @param page   페이지번호(1부터)
     */
    public List<Map<String, String>> fetchLatestFestivalData(String region, int page) {
        PageConfig cfg = PageConfig.builder()
                .searchUrl("https://search.naver.com/search.naver?where=nexearch&sm=top_sly.hst&fbm=0&acr=1&ie=utf8&query=%EC%B6%95%EC%A0%9C")
                .firstPaintSelector(".list_type.switch")                     // 첫 로딩 완료 기준
                .dropdownTriggerSelector("li.tab._select_trigger > a[onclick*='goOtherTCR']")  // 지역 드롭다운 트리거
                .filterPanelSelector("div.cm_filter_area._select_panel")     // 드롭다운 패널
                .regionOptionsSelector("li[data-text]")                      // 지역 항목
                .paginationNextSelector("a.pg_next:not([aria-disabled='true'])")
                .pageReadyAfterPaginateSelector(".card_area")                // 페이지 전환 후 로딩 완료 기준
                .pageParser(this::parseFestivalPage)                         // 페이지 파서
                .build();

        return fetchListGeneric(cfg, page, region, null);
    }

    /**
     * [서비스 로직]
     * 콘서트 데이터 크롤링
     * @param page   페이지번호(1부터)
     * @param filter 정렬 필터(popular/date)
     * @param region 지역
     */
    public List<Map<String, String>> fetchConcertData(int page, String filter, String region) {
        PageConfig cfg = PageConfig.builder()
                .searchUrl("https://search.naver.com/search.naver?where=nexearch&sm=top_hty&fbm=0&ie=utf8&query=%EC%BD%98%EC%84%9C%ED%8A%B8")
                .firstPaintSelector(".list_image_info.type_pure._panel_wrapper")
                .dropdownTriggerSelector("li.tab._custom_select_trigger[data-key='u1']")
                .filterPanelSelector("div.cm_filter_area._custom_select_panel")
                .regionOptionsSelector("li.item._item[data-text]")
                .paginationNextSelector("a.pg_next:not([aria-disabled='true'])")
                .pageReadyAfterPaginateSelector(".list_image_info.type_pure._panel_wrapper")
                .pageParser(this::parseConcertPage)
                .preParseAction(driver -> applyConcertFilter(driver, filter)) // 정렬 적용
                .build();

        return fetchListGeneric(cfg, page, region, null);
    }

    /* =========================
     *  Core Generic Flow
     * ========================= */
    private List<Map<String, String>> fetchListGeneric(
            PageConfig cfg,
            int pageCount,
            String region,
            Consumer<WebDriver> beforePaging
    ) {
        Objects.requireNonNull(cfg, "PageConfig must not be null");
        List<Map<String, String>> result = new ArrayList<>();

        // 0) 구버전 경로 개입 차단 및 선택적 경로 주입
        System.clearProperty("webdriver.chrome.driver");
        if (chromeDriverPath != null && !chromeDriverPath.isBlank()) {
            System.setProperty("webdriver.chrome.driver", chromeDriverPath);
        }

        // 1) 매 요청마다 새 드라이버
        WebDriver driver = new ChromeDriver(buildChromeOptions());
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));

        try {
            // 페이지 오픈 및 첫 페인트 대기
            driver.get(cfg.searchUrl);
            waitUntil(wait, ExpectedConditions.presenceOfElementLocated(By.cssSelector(cfg.firstPaintSelector)));

            // 지역 필터(옵션)
            if (region != null && !region.isEmpty()) {
                applyRegionFilter(driver, wait, cfg.dropdownTriggerSelector, cfg.filterPanelSelector, cfg.regionOptionsSelector, region);
            }

            // 사전 액션(정렬 등)
            if (cfg.preParseAction != null) {
                cfg.preParseAction.accept(driver);
                sleepSilently(400);
            }
            if (beforePaging != null) beforePaging.accept(driver);

            // 페이지네이션
            goToPage(driver, wait, cfg.paginationNextSelector, cfg.pageReadyAfterPaginateSelector, pageCount);

            // 파싱
            Document doc = Jsoup.parse(driver.getPageSource());
            result.addAll(cfg.pageParser.apply(doc, pageCount));

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            driver.quit();
        }

        if (result.isEmpty()) {
            Map<String, String> noData = new HashMap<>();
            noData.put("message", "데이터가 없습니다.");
            result.add(noData);
        }
        return result;
    }

    /* =========================
     *  Parsers
     * ========================= */

    //==축제 파서==//
    private List<Map<String, String>> parseFestivalPage(Document doc, int pageCount) {
        List<Map<String, String>> list = new ArrayList<>();
        Elements cardAreas = doc.select(".card_area");
        if (cardAreas.size() < pageCount) return list;

        Element currentCardArea = cardAreas.get(pageCount - 1);
        Elements cardItems = currentCardArea.select(".card_item");

        for (Element cardItem : cardItems) {
            Map<String, String> festival = new HashMap<>();

            Element img = cardItem.selectFirst(".img_box img");
            if (img != null) festival.put("image", img.attr("src"));

            Element title = cardItem.selectFirst(".title .this_text a");
            if (title != null) {
                festival.put("title", title.text());
                festival.put("link", title.attr("href"));
            } else {
                festival.put("title", "제목 없음");
            }

            Element status = cardItem.selectFirst(".this_text");
            if (status != null) festival.put("status", status.text());

            Element date = cardItem.select(".rel_info dt:contains(기간) + dd.no_ellip").first();
            if (date != null) festival.put("date", date.text());

            Element location = cardItem.select(".rel_info dt:contains(장소) + dd").first();
            if (location != null) festival.put("location", location.text());

            list.add(festival);
        }
        return list;
    }

    //==콘서트 파서==//
    private List<Map<String, String>> parseConcertPage(Document doc, int pageCount) {
        List<Map<String, String>> list = new ArrayList<>();
        Elements panels = doc.select(".list_image_info.type_pure._panel_wrapper ul");
        if (panels.size() < pageCount) return list;

        Element currentPanel = panels.get(pageCount - 1);
        Elements items = currentPanel.select("li");

        for (Element li : items) {
            Map<String, String> concert = new HashMap<>();

            Element img = li.selectFirst(".thumb img");
            if (img != null) concert.put("image", img.attr("src"));

            Element title = li.selectFirst(".title_box .name");
            if (title != null) concert.put("title", title.text());

            Element location = li.selectFirst(".sub_text.line_1");
            if (location != null) concert.put("location", location.text());

            Element link = li.selectFirst("a");
            if (link != null) concert.put("link", link.attr("href"));

            Element status = li.selectFirst(".this_text");
            if (status != null) concert.put("status", status.text());

            list.add(concert);
        }
        return list;
    }

    /* =========================
     *  Selenium Helpers
     * ========================= */

    //==ChromeOptions 기본 세팅==//
    private ChromeOptions buildChromeOptions() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless=new"); // 최신 헤드리스
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        // 필요시 UA/언어 등 추가
        return options;
    }

    //==WebDriverWait 래핑==//
    private void waitUntil(WebDriverWait wait, ExpectedCondition<?> condition) {
        wait.until(condition);
    }

    //==지역 필터 적용==//
    private void applyRegionFilter(
            WebDriver driver,
            WebDriverWait wait,
            String dropdownTriggerSelector,
            String filterPanelSelector,
            String regionOptionsSelector,
            String region
    ) {
        WebElement trigger = driver.findElement(By.cssSelector(dropdownTriggerSelector));
        String expanded = trigger.getAttribute("aria-expanded");
        if (expanded == null || "false".equals(expanded)) {
            trigger.click();
            waitUntil(wait, ExpectedConditions.attributeToBe(trigger, "aria-expanded", "true"));
            WebElement panel = driver.findElement(By.cssSelector(filterPanelSelector));
            ((JavascriptExecutor) driver).executeScript("arguments[0].setAttribute('aria-hidden', 'false')", panel);
        }

        List<WebElement> regions = driver.findElements(By.cssSelector(regionOptionsSelector));
        boolean found = false;
        for (WebElement opt : regions) {
            if (region.equals(opt.getAttribute("data-text"))) {
                opt.click();
                found = true;
                sleepSilently(400);
                break;
            }
        }
        if (!found) {
            System.out.println("해당 지역을 찾을 수 없습니다: " + region);
        }
    }

    //==원하는 페이지 번호까지 "다음 페이지" 버튼 클릭==//
    private void goToPage(
            WebDriver driver,
            WebDriverWait wait,
            String nextSelector,
            String afterSelector,
            int pageCount
    ) {
        if (pageCount <= 1) return;
        for (int i = 1; i < pageCount; i++) {
            try {
                WebElement next = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector(nextSelector)));
                next.click();
                waitUntil(wait, ExpectedConditions.presenceOfElementLocated(By.cssSelector(afterSelector)));
            } catch (Exception ex) {
                System.out.println("페이지가 더 이상 존재하지 않습니다.");
                break;
            }
        }
    }

    //==콘서트 필터 적용==//
    private void applyConcertFilter(WebDriver driver, String filter) {
        try {
            By by = "popular".equalsIgnoreCase(filter)
                    ? By.cssSelector("li.tab[data-value='popular'] a")
                    : By.cssSelector("li.tab[data-value='date'] a");
            WebElement btn = driver.findElement(by);
            btn.click();
        } catch (org.openqa.selenium.NoSuchElementException ignored) {
        }
    }

    //==Thread.sleep() 안전하게 실행==//
    private void sleepSilently(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException ignored) {
            Thread.currentThread().interrupt();
        }
    }

    /* =========================
     *  Config
     * ========================= */

    //== 페이지 설정 ==//
    private static class PageConfig {
        final String searchUrl;
        final String firstPaintSelector;
        final String dropdownTriggerSelector;
        final String filterPanelSelector;
        final String regionOptionsSelector;
        final String paginationNextSelector;
        final String pageReadyAfterPaginateSelector;
        final Consumer<WebDriver> preParseAction; // 정렬/필터 등
        final BiFunction<Document, Integer, List<Map<String, String>>> pageParser;

        private PageConfig(Builder b) {
            this.searchUrl = b.searchUrl;
            this.firstPaintSelector = b.firstPaintSelector;
            this.dropdownTriggerSelector = b.dropdownTriggerSelector;
            this.filterPanelSelector = b.filterPanelSelector;
            this.regionOptionsSelector = b.regionOptionsSelector;
            this.paginationNextSelector = b.paginationNextSelector;
            this.pageReadyAfterPaginateSelector = b.pageReadyAfterPaginateSelector;
            this.preParseAction = b.preParseAction;
            this.pageParser = b.pageParser;
        }

        static Builder builder() { return new Builder(); }

        static class Builder {
            private String searchUrl;
            private String firstPaintSelector;
            private String dropdownTriggerSelector;
            private String filterPanelSelector;
            private String regionOptionsSelector;
            private String paginationNextSelector;
            private String pageReadyAfterPaginateSelector;
            private Consumer<WebDriver> preParseAction;
            private BiFunction<Document, Integer, List<Map<String, String>>> pageParser;

            Builder searchUrl(String v) { this.searchUrl = v; return this; }
            Builder firstPaintSelector(String v) { this.firstPaintSelector = v; return this; }
            Builder dropdownTriggerSelector(String v) { this.dropdownTriggerSelector = v; return this; }
            Builder filterPanelSelector(String v) { this.filterPanelSelector = v; return this; }
            Builder regionOptionsSelector(String v) { this.regionOptionsSelector = v; return this; }
            Builder paginationNextSelector(String v) { this.paginationNextSelector = v; return this; }
            Builder pageReadyAfterPaginateSelector(String v) { this.pageReadyAfterPaginateSelector = v; return this; }
            Builder preParseAction(Consumer<WebDriver> v) { this.preParseAction = v; return this; }
            Builder pageParser(BiFunction<Document, Integer, List<Map<String, String>>> v) { this.pageParser = v; return this; }

            PageConfig build() {
                Objects.requireNonNull(searchUrl);
                Objects.requireNonNull(firstPaintSelector);
                Objects.requireNonNull(paginationNextSelector);
                Objects.requireNonNull(pageReadyAfterPaginateSelector);
                Objects.requireNonNull(pageParser);
                return new PageConfig(this);
            }
        }
    }
}
