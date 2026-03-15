/**
 * AWS Lambda Deployment Example.
 *
 * Demonstrates an agent designed for serverless deployment. In a real
 * Lambda setup you would use a Java Lambda handler framework to translate
 * API Gateway events to HTTP requests.
 *
 * For local testing, this runs as a normal HTTP server.
 */

import com.signalwire.agents.agent.AgentBase;
import com.signalwire.agents.swaig.FunctionResult;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

public class LambdaAgent {

    public static void main(String[] args) throws Exception {
        var agent = AgentBase.builder()
                .name("lambda-agent")
                .route("/")
                .port(3000)
                .build();

        agent.addLanguage("English", "en-US", "en-US-Standard-C");

        agent.promptAddSection("Role",
                "You are a helpful AI assistant running in AWS Lambda.");
        agent.promptAddSection("Instructions", "", List.of(
                "Greet users warmly and offer help",
                "Use the greet_user function when asked to greet someone",
                "Use the get_time function when asked about the current time"
        ));

        agent.defineTool("greet_user", "Greet a user by name",
                Map.of("type", "object",
                        "properties", Map.of(
                                "name", Map.of("type", "string",
                                        "description", "User name")
                        )),
                (toolArgs, raw) -> {
                    String name = (String) toolArgs.getOrDefault("name", "friend");
                    return new FunctionResult("Hello " + name + "! I'm running in AWS Lambda!");
                });

        agent.defineTool("get_time", "Get the current time",
                Map.of("type", "object", "properties", Map.of()),
                (toolArgs, raw) -> new FunctionResult(
                        "Current time: " + LocalDateTime.now()
                                .format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)));

        System.out.println("Starting Lambda agent (local testing) on port 3000...");
        agent.run();
    }
}
