package com.example.demo.controllers;

import co.elastic.apm.api.ElasticApm;
import co.elastic.apm.api.Span;
import com.example.demo.services.TestService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class TestController {
    private final TestService testService;

    @GetMapping("/super-fast")
    public String getSuperFastApi(HttpServletRequest request) {
        Span span = ElasticApm.currentSpan()
                .startSpan("incoming", "http", null)
                .setName(request.getMethod() + "-super-fast");

        span.setLabel("level", "top");
        span.end();
        return "I'm super fast.";
    }

    @GetMapping("/self-referring")
    public String getSelfReferringApi(HttpServletRequest request,
                                      @RequestParam(name="port", required = false, defaultValue = "8081") String port
    ) throws IOException {
        Span span = ElasticApm.currentSpan()
                .startSpan("external", "http", null)
                .setName(request.getMethod() + "-self-referring");

        URL url = new URL("http://localhost:" + port + "/fast");
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");

        span.injectTraceHeaders(con::setRequestProperty);

        InputStream responseStream = con.getInputStream();
        String result = new BufferedReader(new InputStreamReader(responseStream))
                .lines().collect(Collectors.joining("\n"));

        span.end();
        return "{\"external hello message\": "+ result +" }";
    }

    @GetMapping("/fast")
    public String getFastApi(HttpServletRequest request) throws InterruptedException {
        Span span = ElasticApm.currentSpan()
                .startSpan("incoming", "http", null)
                .setName(request.getMethod() + "-super-fast");

        Thread.sleep(20); // sleep for 20 milliseconds
        span.setLabel("level", "internal");
        span.end();
        return "I'm fast!";
    }

    @GetMapping("/slow")
    public String getSlowApi(HttpServletRequest request) throws InterruptedException {
        Span span = ElasticApm.currentSpan()
                .startSpan("incoming", "http", null)
                .setName(request.getMethod() + "-slow");

        span.setLabel("level", "top");
        String respMessage = testService.getMessage("I'm slow :(");
        Thread.sleep(3000); // sleep for 3 seconds
        span.end();
        return respMessage;
    }

    @GetMapping("/super-slow")
    @Profile({"predev", "dev", "staging"})
    public String getSuperSlowApi() throws InterruptedException {
        Thread.sleep(60000); // sleep for 1 minute
        return "I'm super slow. Refactor me before moving to production!! :)";
    }
}
