import com.ordina.data.DataGetter;
import com.ordina.data.opensky.OpenSky;
import org.junit.jupiter.api.*;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayName("The DataGetter")
public class DataGetterTest {

    static OpenSky data;
    static URL resource;

    @BeforeAll
    static void initAll() throws IOException, URISyntaxException {
        resource = DataGetter.class.getClassLoader().getResource("opensky.json").toURI().toURL();
    }

    @Test
    @Order(1)
    @DisplayName("is instantiated")
    public void init() throws IOException {
        DataGetter<OpenSky> getter = new DataGetter<>(resource, OpenSky.class);
        data = getter.get();

        assertNotNull(data);
        assertInstanceOf(OpenSky.class, data);
    }

    @Test
    @Order(2)
    @DisplayName("and returns valid object")
    public void isObjectValid() {
        assertNotNull(data.getTime());
        assertNotNull(data.getStates());
        assertInstanceOf(OpenSky.State.class, data.getStates().get(0));
        assertFalse(data.getStates().isEmpty());
        assertNotNull(data.getStates().get(0).icao24());
        assertNotNull(data.getStates().get(0).lastContact());
    }

    @Test
    @Order(3)
    @DisplayName("with valid values")
    public void isValueValid() {
        assertEquals(1654817546, data.getTime());
        assertEquals(56, data.getStates().size());
        assertEquals("ab1644", data.getStates().get(0).icao24());
        assertEquals(1654817451, data.getStates().get(0).lastContact());
        assertEquals(false, data.getStates().get(0).onGround());
    }
}

