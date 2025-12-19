package com.kama.jchatmind;

import com.kama.jchatmind.agent.JChatMindTest;
import com.kama.jchatmind.agent.tools.test.CityTool;
import com.kama.jchatmind.agent.tools.test.DateTool;
import com.kama.jchatmind.agent.tools.test.WeatherTool;
import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.method.MethodToolCallbackProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class JChatMindTests {

    @Autowired
    private ChatClient deepSeekChatClient;

    @Autowired
    private CityTool cityTool;

    @Autowired
    private DateTool dateTool;

    @Autowired
    private WeatherTool weatherTool;

    @Test
    public void test() {
        ToolCallback[] toolCallbacks = MethodToolCallbackProvider.builder()
                .toolObjects(cityTool, dateTool, weatherTool)
                .build()
                .getToolCallbacks();
        JChatMindTest jChatMindTest = new JChatMindTest(deepSeekChatClient, toolCallbacks);
        jChatMindTest.run("今天的天气怎么样？");
    }
}
