package com.voidaspect.public24.controller;

import ai.api.util.IOUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.io.IOException;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class WebhookIntegrationTest {

    @Autowired
    private MockMvc mockMvc;


    @Test
    public void testExchangeHistory_USD() throws Exception {
        String request = bodyPath("/data/webhook/history-usd-request.json");
        String response = bodyPath("/data/webhook/history-usd-response.json");

        mockMvc.perform(post("/api-ai")
                .contentType(MediaType.APPLICATION_JSON)
                .content(request).with(httpBasic("dev-user", "p24_DEV")))
                .andExpect(status().isOk())
                .andExpect(content().json(response, true));

    }

    private String bodyPath(String path) throws IOException {
        return IOUtils.readAll(getClass().getResourceAsStream(path));
    }

}