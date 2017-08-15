package com.voidaspect.public24.service.agent;

import com.voidaspect.public24.controller.BadWebhookRequestException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

@RequiredArgsConstructor
@Getter
enum Intent {

    CURRENT_EXCHANGE_RATE("Current Exchange Rate"),

    EXCHANGE_RATE_HISTORY("Exchange Rate History");

    private final String name;

    static Intent getByName(String name) {
        return Arrays.stream(values())
                .filter(intent -> compareIgnoreCaseAndSpaces(intent.name, name))
                .findAny()
                .orElseThrow(() -> new BadWebhookRequestException(
                        "Unable to resolve intent name: " + name));
    }

    static boolean compareIgnoreCaseAndSpaces(String s1, String s2) {
        return s1.replace(" ", "")
                .equalsIgnoreCase(s2.replace(" ", ""));
    }
}
