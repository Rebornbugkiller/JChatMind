package com.kama.jchatmind.agent.tools.test;

import com.kama.jchatmind.agent.tools.Tool;
import com.kama.jchatmind.agent.tools.ToolType;
import org.springframework.stereotype.Component;

@Component
public class DateTool implements Tool {
    @Override
    public String getName() {
        return "dateTool";
    }

    @Override
    public String getDescription() {
        return "获取当前的日期";
    }

    @Override
    public ToolType getType() {
        return ToolType.FIXED;
    }

    @org.springframework.ai.tool.annotation.Tool(name = "getDate", description = "获取当前的日期")
    public String getDate() {
        return "2023-05-05";
    }
}
