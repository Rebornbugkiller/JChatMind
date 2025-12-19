import React from "react";
import type { ChatMessageVO } from "../../../api/api.ts";
import { Bubble } from "@ant-design/x";
import XMarkdown from "@ant-design/x-markdown";
import type { SseMessageType } from "../../../types";

interface AgentChatHistoryProps {
  messages: ChatMessageVO[];
  displayAgentStatus?: boolean;
  agentStatusText?: string;
  agentStatusType?: SseMessageType;
}

const AgentChatHistory: React.FC<AgentChatHistoryProps> = ({
  messages,
  displayAgentStatus = false,
  agentStatusText = "",
  agentStatusType,
}) => {
  // 获取状态标签
  const getStatusLabel = () => {
    switch (agentStatusType) {
      case "AI_PLANNING":
        return "规划中";
      case "AI_THINKING":
        return "思考中";
      case "AI_EXECUTING":
        return "执行中";
      default:
        return "处理中";
    }
  };

  return (
    <div>
      {messages.map((message) => {
        return (
          <div className="mb-3" key={message.id}>
            {message.role === "assistant" && (
              <Bubble
                content={message.content}
                placement="start"
                contentRender={(content) => (
                  <XMarkdown
                    streaming={{ enableAnimation: false, hasNextChunk: true }}
                  >
                    {content}
                  </XMarkdown>
                )}
              />
            )}
            {message.role === "user" && (
              <Bubble content={message.content} placement="end" />
            )}
          </div>
        );
      })}
      {displayAgentStatus && (
        <div className="mb-3">
          <div
            className="animate-pulse"
            style={{
              animation: "pulse 0.8s cubic-bezier(0.4, 0, 0.6, 1) infinite",
              filter: "brightness(1.15)",
            }}
          >
            <Bubble
              content={
                <span className="flex items-center gap-2">
                  <span
                    className="font-semibold text-blue-600"
                    style={{
                      animation:
                        "pulse 0.7s cubic-bezier(0.4, 0, 0.6, 1) infinite",
                      textShadow:
                        "0 0 10px rgba(37, 99, 235, 1), 0 0 20px rgba(37, 99, 235, 0.8), 0 0 30px rgba(37, 99, 235, 0.5)",
                      filter: "brightness(1.3)",
                    }}
                  >
                    ✨ {getStatusLabel()}
                  </span>
                  <span className="text-gray-400">·</span>
                  <span className="text-gray-600">{agentStatusText}</span>
                </span>
              }
              placement="start"
            />
          </div>
        </div>
      )}
    </div>
  );
};

export default AgentChatHistory;
