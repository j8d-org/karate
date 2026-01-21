package driver;

import com.intuit.karate.Results;
import com.intuit.karate.Runner;
import io.karatelabs.http.HttpServer;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

/**
 *
 * @author pthomas3
 */
class LocalSingleRunner {
    
    static HttpServer server;
    
    @BeforeAll
    static void beforeAll() {
        server = ServerStarter.start(0);        
    }
    
    void run(String id) {
        Results results = Runner.path("src/test/java/driver/" + id + ".feature")
                .karateEnv("single")
                .systemProperty("server.port", server.getPort() + "")
                .systemProperty("driver.type", "chrome")
                .configDir("src/test/java/driver").parallel(1);
        assertEquals(0, results.getFailCount(), results.getErrorMessages());        
    }
    
    @Test
    void testSingle() {
        run("00");
    }

    // Tests for features that are commented out or conditional in 00.feature
    // Run these individually to verify v2 support

    @Test
    void test01_UrlTitleNavigation() {
        // Tests: driver.url, title, waitForUrl, refresh, back, forward, dimensions
        // Currently COMMENTED OUT in 00.feature
        run("01");
    }

    @Test
    void test12_DriverSend() {
        // Tests: driver.send() for raw CDP commands
        // Currently COMMENTED OUT in 00.feature
        run("12");
    }

    @Test
    void test13_SwitchFrameExternal() {
        // Tests: switchFrame with external URLs (Wikipedia)
        // Currently only runs with playwright
        run("13");
    }

    @Test
    void test18_SubmitRetry() {
        // Tests: submit() and retry()
        // Currently only runs with playwright
        run("18");
    }

    @Test
    void test99_Bootstrap() {
        // Tests: Bootstrap dropdown with mouse interactions
        // Currently NOT CALLED in 00.feature
        run("99_bootstrap");
    }

}
