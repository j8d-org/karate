package driver;

import io.karatelabs.common.Resource;
import io.karatelabs.common.ResourceType;
import io.karatelabs.http.*;
import io.karatelabs.markup.ResourceResolver;
import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;
import java.util.function.Function;

/**
 * HTTP server for serving test HTML pages with Karate templating support.
 * Uses ServerRequestHandler for ka:scope, th:text, session support.
 * Custom routing to serve static files (.css, .js, .ico) from root path.
 */
public class ServerStarter {

    // File extensions to serve as static (not templated)
    private static final Set<String> STATIC_EXTENSIONS = Set.of(
        ".css", ".js", ".ico", ".gif", ".png", ".jpg", ".jpeg", ".svg"
    );

    private static final Path HTML_ROOT = Path.of("src/test/java/driver/html");

    @Test
    void testServer() {
        HttpServer server = start(8080);
        server.waitSync();
    }

    public static HttpServer start(int port) {
        // Resource resolver that loads files from the html directory
        ResourceResolver resolver = (resourcePath, caller) -> {
            String path = resourcePath;
            if (path.isEmpty() || path.equals("/")) {
                path = "00.html";
            }
            if (!path.contains(".")) {
                path = path + ".html";
            }
            if (path.startsWith("/")) {
                path = path.substring(1);
            }

            Path filePath = HTML_ROOT.resolve(path);
            if (Files.exists(filePath) && Files.isRegularFile(filePath)) {
                return Resource.from(filePath);
            }
            return null;
        };

        // Configure server with session support
        ServerConfig config = new ServerConfig()
                .sessionStore(new InMemorySessionStore())
                .sessionExpirySeconds(600)
                .devMode(true)
                .csrfEnabled(false)
                .apiPrefixEnabled(false)
                .staticPrefixEnabled(false)
                .xFrameOptions("SAMEORIGIN"); // Allow iframes from same origin for e2e tests

        ServerRequestHandler templateHandler = new ServerRequestHandler(config, resolver);

        // Custom handler that routes static files vs templates
        Function<HttpRequest, HttpResponse> handler = request -> {
            String path = request.getPath();

            // Serve static files directly (bypass templating)
            if (isStaticFile(path)) {
                return serveStatic(path);
            }

            // For HTML files, use the template handler
            return templateHandler.apply(request);
        };

        return HttpServer.start(port, handler);
    }

    private static boolean isStaticFile(String path) {
        String lower = path.toLowerCase();
        for (String ext : STATIC_EXTENSIONS) {
            if (lower.endsWith(ext)) {
                return true;
            }
        }
        return false;
    }

    private static HttpResponse serveStatic(String path) {
        HttpResponse response = new HttpResponse();
        try {
            String resourcePath = path.startsWith("/") ? path.substring(1) : path;
            Path filePath = HTML_ROOT.resolve(resourcePath);

            if (Files.exists(filePath) && Files.isRegularFile(filePath)) {
                byte[] content = Files.readAllBytes(filePath);
                ResourceType rt = ResourceType.fromFileExtension(path);
                if (rt != null) {
                    response.setBody(content, rt);
                } else {
                    response.setBody(content, ResourceType.BINARY);
                }
            } else {
                response.setStatus(404);
                response.setBody("Not found: " + path);
            }
        } catch (Exception e) {
            response.setStatus(500);
            response.setBody("Error: " + e.getMessage());
        }
        return response;
    }
}
