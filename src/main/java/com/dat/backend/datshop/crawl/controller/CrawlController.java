package com.dat.backend.datshop.crawl.controller;

import com.dat.backend.datshop.crawl.dto.CrawlRequest;
import com.dat.backend.datshop.crawl.dto.ProductResponse;
import com.dat.backend.datshop.crawl.service.CrawlService;
import com.dat.backend.datshop.template.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/crawl")
@PreAuthorize("hasRole('ADMIN')")
public class CrawlController {
    private final CrawlService crawlService;

    @PostMapping("/start")
    public ApiResponse<List<ProductResponse>> startCrawl(@RequestBody CrawlRequest crawlRequest) {
        return ApiResponse.success(crawlService.crawlProducts(crawlRequest));
    }
}
