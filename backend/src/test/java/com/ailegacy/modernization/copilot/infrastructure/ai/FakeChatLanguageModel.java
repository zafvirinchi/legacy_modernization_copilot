package com.ailegacy.modernization.copilot.infrastructure.ai;

import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.output.Response;

import java.util.List;

/**
 * Test double standing in for a real OpenAI-backed ChatLanguageModel, since no
 * API key or network access is available in this sandbox.
 */
class FakeChatLanguageModel implements ChatLanguageModel {

    private final String cannedResponse;
    private final RuntimeException failure;

    private FakeChatLanguageModel(String cannedResponse, RuntimeException failure) {
        this.cannedResponse = cannedResponse;
        this.failure = failure;
    }

    static FakeChatLanguageModel returning(String cannedResponse) {
        return new FakeChatLanguageModel(cannedResponse, null);
    }

    static FakeChatLanguageModel throwing(RuntimeException failure) {
        return new FakeChatLanguageModel(null, failure);
    }

    @Override
    public Response<AiMessage> generate(List<ChatMessage> messages) {
        if (failure != null) {
            throw failure;
        }
        return Response.from(AiMessage.from(cannedResponse));
    }

}
