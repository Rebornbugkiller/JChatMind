package com.kama.jchatmind.agent;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.chat.messages.*;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.model.tool.DefaultToolCallingChatOptions;
import org.springframework.ai.model.tool.ToolCallingManager;
import org.springframework.ai.model.tool.ToolExecutionResult;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.util.Assert;

@Slf4j
public class JChatMindTest {

    private final ChatClient chatClient;
    private AgentState agentState;
    private final ChatMemory chatMemory;
    private final ChatOptions chatOptions;
    private final ToolCallingManager toolCallingManager;
    private final ToolCallback[] tools;

    private ChatResponse lastChatResponse;

    private final String chatSessionId = "chat-session-1";

    public JChatMindTest(ChatClient chatClient, ToolCallback[] tools) {
        this.chatClient = chatClient;
        this.chatMemory = MessageWindowChatMemory.builder()
                .build();
        this.chatOptions = DefaultToolCallingChatOptions.builder()
                .internalToolExecutionEnabled(false)
                .build();
        this.toolCallingManager = ToolCallingManager.builder().build();
        this.agentState = AgentState.IDLE;
        this.tools = tools;
    }

    private boolean think() {
        String thinkPrompt = """
                现在你是一个智能的的具体「决策模块」。
                请根据当前对话上下文，决定下一步的动作。
                """;
        Prompt prompt = Prompt.builder()
                .chatOptions(chatOptions)
                .messages(chatMemory.get(chatSessionId))
                .build();
        this.lastChatResponse = chatClient.prompt(prompt)
                .system(thinkPrompt)
                .toolCallbacks(tools)
                .call()
                .chatResponse();
        Assert.notNull(this.lastChatResponse, "Last chat response cannot be null");

        boolean hasToolCalls = this.lastChatResponse.hasToolCalls();

        log.info("INSERT(role = {})：{}",
                this.lastChatResponse.getResult().getOutput().getMessageType(),
                this.lastChatResponse.getResult().getOutput().getText());

        return hasToolCalls;
    }


    private void execute() {
        Assert.notNull(this.lastChatResponse, "Last chat response cannot be null");
        if (!this.lastChatResponse.hasToolCalls()) {
            return;
        }

        Prompt prompt = Prompt.builder()
                .messages(chatMemory.get(chatSessionId))
                .chatOptions(chatOptions)
                .build();

        ToolExecutionResult toolExecutionResult = toolCallingManager.executeToolCalls(prompt, this.lastChatResponse);
        chatMemory.clear(chatSessionId);
        chatMemory.add(chatSessionId, toolExecutionResult.conversationHistory());

        int conversationHistorySize = toolExecutionResult.conversationHistory().size();

        ToolResponseMessage message = (ToolResponseMessage) toolExecutionResult.conversationHistory()
                .get(conversationHistorySize - 1);

        int toolCallSize = message.getResponses().size();

        for (int i = 0; i < toolCallSize; i++) {
            ToolResponseMessage.ToolResponse toolResponse = message.getResponses().get(i);
            log.info("ToolCall: name = {}, result = {}", toolResponse.name(), toolResponse.responseData());
        }
    }

    private void step() {
        if (think()) {
            execute();
        } else {
            agentState = AgentState.FINISHED;
        }
    }

    public void run(String userInput) {
        Assert.notNull(userInput, "User input cannot be null");
        UserMessage userMessage = new UserMessage(userInput);
        chatMemory.add(chatSessionId, userMessage);
        while (agentState != AgentState.FINISHED) {
            step();
        }
        System.out.println("结束");
    }
}
