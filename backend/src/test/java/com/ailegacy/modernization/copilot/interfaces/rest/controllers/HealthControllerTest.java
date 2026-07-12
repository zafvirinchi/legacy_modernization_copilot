package com.ailegacy.modernization.copilot.interfaces.rest.controllers;

import com.ailegacy.modernization.copilot.interfaces.rest.exception.GlobalExceptionHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.env.MockEnvironment;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class HealthControllerTest {

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        MockEnvironment environment = new MockEnvironment();
        environment.setActiveProfiles("test");

        HealthController healthController = new HealthController(environment);
        ReflectionTestUtils.setField(healthController, "appName", "AI Legacy Modernization Copilot");
        ReflectionTestUtils.setField(healthController, "appVersion", "1.0.0");

        mockMvc = MockMvcBuilders.standaloneSetup(healthController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void healthEndpointShouldReturnServiceStatus() throws Exception {
        mockMvc.perform(get("/health"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith("application/json"))
                .andExpect(jsonPath("$.status").value("UP"))
                .andExpect(jsonPath("$.service").value("AI Legacy Modernization Copilot"))
                .andExpect(jsonPath("$.environment").value("test"))
                .andExpect(content().string(containsString("timestamp")));
    }
}
