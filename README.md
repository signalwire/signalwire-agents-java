# SignalWire AI Agents Java SDK

A Java framework for building, deploying, and managing AI agents as microservices that interact with the [SignalWire](https://signalwire.com) platform.

## Features

- **Agent Framework** — Build AI agents with structured prompts, tools, and skills
- **SWML Generation** — Automatic SWML document creation for the SignalWire AI platform
- **SWAIG Functions** — Define tools the AI can call during conversations
- **DataMap Tools** — Server-side API integrations without webhook infrastructure
- **Contexts & Steps** — Structured multi-step conversation workflows
- **Skills System** — Modular, reusable capabilities (datetime, math, web search, etc.)
- **Prefab Agents** — Ready-to-use agent patterns (surveys, reception, FAQ, etc.)
- **Multi-Agent Hosting** — Run multiple agents on a single server
- **RELAY Client** — Real-time WebSocket-based call control and messaging
- **REST Client** — Full SignalWire REST API access with typed resources
- **Embeddable** — Run standalone or embed in Spring Boot, Servlet containers, etc.

## Quick Start

```java
import com.signalwire.sdk.agent.AgentBase;
import com.signalwire.sdk.swaig.FunctionResult;

var agent = AgentBase.builder()
    .name("my-agent")
    .build();

agent.setPromptText("You are a helpful assistant.");

agent.defineTool("get_time", "Get the current time", Map.of(),
    (args, rawData) -> new FunctionResult("The time is " + java.time.LocalTime.now()));

agent.run();
```

## Requirements

- Java 21+
- Gradle 8+ (or Maven)

## Build

```bash
./gradlew build
./gradlew test
```

## Environment Variables

| Variable | Description | Default |
|----------|-------------|---------|
| `PORT` | HTTP server port | `3000` |
| `SWML_BASIC_AUTH_USER` | Basic auth username | auto-generated |
| `SWML_BASIC_AUTH_PASSWORD` | Basic auth password | auto-generated |
| `SWML_PROXY_URL_BASE` | Proxy/tunnel base URL | auto-detected |
| `SIGNALWIRE_PROJECT_ID` | Project ID for RELAY/REST | — |
| `SIGNALWIRE_API_TOKEN` | API token for RELAY/REST | — |
| `SIGNALWIRE_SPACE` | Space hostname | — |

## License

Copyright (c) SignalWire. All rights reserved.
