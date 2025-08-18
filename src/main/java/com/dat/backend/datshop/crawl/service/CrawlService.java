package com.dat.backend.datshop.crawl.service;

import com.dat.backend.datshop.crawl.dto.CrawlRequest;
import com.dat.backend.datshop.crawl.dto.ProductResponse;
import com.google.gson.stream.JsonWriter;
import io.github.bonigarcia.wdm.WebDriverManager;
import lombok.RequiredArgsConstructor;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.stereotype.Service;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CrawlService {
    public List<ProductResponse> crawlProducts(CrawlRequest crawlRequest) {
        String url = crawlRequest.getUrl();

        WebDriverManager.chromedriver().setup();

        // Cấu hình headless để chạy không cần mở cửa sổ Chrome
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless");


        // Khởi tạo WebDriver với ChromeDriver
        WebDriver driver = new ChromeDriver(options);
        List<ProductResponse> productResponses = new ArrayList<>();

        try {
            driver.get(url);
            // Cuộn trang xuống 3 lần để tải hết dữ liệu
            scrollDown(driver, 3, 2000);
            // Thực hiện crawl dữ liệu từ trang web
            // Lấy ra list sản phẩm
            List<WebElement> productElements = driver.findElements(By.cssSelector(".col-md-3.col-6"));
            for (WebElement productElement : productElements) {
                try {
                    // Lấy id sản phẩm
                    String id = UUID.randomUUID().toString();
                    // Lấy ảnh sản phẩm
                    String imgUrl = productElement.findElement(By.cssSelector(".img-fluid")).getDomAttribute("src");
                    // Lấy tên sản phẩm
                    String name = productElement.findElement(By.cssSelector(".mt-2 p")).getText();
                    // Lấy giá sản phẩm
                    List<WebElement> allPrice = productElement.findElements(By.cssSelector(".price span[style*='color:#ff0000']"));
                    String price;

                    if (allPrice.isEmpty()) {
                        price = productElement.findElement(By.cssSelector(".price")).getText();
                    }
                    else {
                        price = allPrice.getFirst().getText();
                    }

                    // Tạo đối tượng ProductResponse và thêm vào danh sách
                    ProductResponse productResponse = ProductResponse.builder()
                            .id(id)
                            .imgUrl(imgUrl)
                            .name(name)
                            .price(price)
                            .build();
                    productResponses.add(productResponse);
                } catch (NoSuchElementException e) {
                    // Bỏ qua sản phẩm nếu không tìm thấy thông tin cần thiết
                }
            }

        }
        catch (Exception e) {
            System.out.println(e.getMessage());
            throw new RuntimeException("Error while crawling products: " + e.getMessage());
        }
        finally {
            // Đóng WebDriver
            driver.quit();
        }

        // Lưu danh sách sản phẩm vào file JSON
        try {
            saveToJsonFile(productResponses);
        }
        catch (IOException e) {
            System.out.println("Error while saving to JSON file: " + e.getMessage());
            throw new RuntimeException("Error while saving to JSON file: " + e.getMessage());
        }

        // Trả về danh sách sản phẩm đã crawl
        return productResponses;
    }

    private void scrollDown(WebDriver driver, int howMany, int delayMills) {
        JavascriptExecutor js = (JavascriptExecutor) driver;
        for (int i = 0; i < howMany; i++) {
            try {
                js.executeScript("window.scrollTo(0, document.body.scrollHeight);");
                Thread.sleep(delayMills);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            } catch (JavascriptException e) {
                // Nếu có lỗi xảy ra, có thể là do trang đã tải hết dữ liệu
                break;
            }
        }
    }

    private void saveToJsonFile(List<ProductResponse> productResponses) throws IOException {
        JsonWriter writer = new JsonWriter(new FileWriter("products.json"));
        writer.beginArray();
        for (ProductResponse product : productResponses) {
            writer.beginObject();
            writer.name("id").value(product.getId());
            writer.name("imgUrl").value(product.getImgUrl());
            writer.name("name").value(product.getName());
            writer.name("price").value(product.getPrice());
            writer.endObject();
        }
        writer.endArray();
        writer.close();
    }
}
