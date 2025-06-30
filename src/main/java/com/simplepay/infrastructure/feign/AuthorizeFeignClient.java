package com.simplepay.infrastructure.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@FeignClient(name = "authorizeClient", url = "https://util.devi.tools/api/v2")
public interface AuthorizeFeignClient {
    @GetMapping("/authorize")
    @ResponseBody
    AuthorizeResponse authorize();

    record AuthorizeResponse(String status, Data data) {
        public record Data(boolean authorization) {}
    }
}

