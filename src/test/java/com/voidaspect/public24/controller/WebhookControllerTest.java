package com.voidaspect.public24.controller;

import ai.api.model.Fulfillment;
import com.google.gson.Gson;
import com.voidaspect.public24.Tests;
import com.voidaspect.public24.config.gson.GsonConfig;
import com.voidaspect.public24.service.agent.AgentWebhook;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.BDDMockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(value = WebhookController.class, includeFilters =
@ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = {GsonConfig.class}))
public class WebhookControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private Gson gson;

    @MockBean
    private AgentWebhook agentWebhook;

    @Test
    public void testExchangeHistory_USD() throws Exception {
        String request = Tests.loadTestResourceAsString("/data/webhook/history-usd-request.json");
        String response = Tests.loadTestResourceAsString("/data/webhook/history-usd-response.json");

        given(agentWebhook.fulfillAgentResponse(any()))
                .willReturn(gson.fromJson(response, Fulfillment.class));

        mockMvc.perform(post("/api-ai")
                .contentType(MediaType.APPLICATION_JSON)
                .content(request).with(httpBasic("dev-user", "p24_DEV")))
                .andExpect(status().isOk())
                .andExpect(content().json(response, true));

        verify(agentWebhook, only())
                .fulfillAgentResponse(any());
    }

}