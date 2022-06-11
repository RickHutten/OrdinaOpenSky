import com.ordina.*;
import com.ordina.controller.ApiController;
import com.ordina.data.DataGetter;
import com.ordina.data.SharedStorage;
import com.ordina.data.opensky.OpenSky;
import com.ordina.data.opensky.OpenSkyDataProcessor;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = {App.class})
@DisplayName("The Controller")
public class ControllerTest {
    @Autowired
    private ApiController controller;

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @BeforeAll
    static void initAll() throws IOException, URISyntaxException {
        URL resource = DataGetter.class.getClassLoader().getResource("opensky.json").toURI().toURL();
        DataGetter<OpenSky> getter = new DataGetter<>(resource, OpenSky.class);
        SharedStorage.getInstance().setOpenSky(getter);
        OpenSky data = getter.get();
        OpenSkyDataProcessor.process(data);
    }

    @Test
    @DisplayName("homepage returns html")
    void index() {
        assertNotNull(controller);
        assertTrue(restTemplate.getForObject("http://localhost:" + port + "/", String.class).contains("<!DOCTYPE html>"));
    }

    @Test
    @DisplayName("counts planes over the netherlands")
    void netherlands() {
        assertEquals(1, (int) restTemplate.getForObject("http://localhost:" + port + "/nl", Integer.class));
    }

    @Test
    @DisplayName("warns for planes that are about to change altitude slice")
    void warning() {
        assertIterableEquals(Arrays.asList("7c6b41", "a3f2c6"), restTemplate.getForObject("http://localhost:" + port + "/warnings", List.class));
    }

    @Test
    @DisplayName("groups the planes by altitude")
    void altitude() {
        Map<String, List<String>> data = restTemplate.getForObject("http://localhost:" + port + "/altitude", HashMap.class);
        assertTrue(data.containsKey("1000"));
        assertEquals(2, data.get("7000").size());
    }

    @Test
    @DisplayName("returns top 3 countries of origin")
    void count() {
        Map<String, Integer> data = restTemplate.getForObject("http://localhost:" + port + "/count", HashMap.class);
        assertEquals(40, data.get("United States"));
        assertEquals(6, data.get("Australia"));
        assertEquals(4, data.get("Canada"));
        assertEquals(3, data.size());
    }
}
