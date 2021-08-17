package realestate;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

@AutoConfigureMockMvc
public class TestsBase {

    static {
        MDC.put("requestId", "TEST-" + UUID.randomUUID());
    }

    @Autowired
    protected ObjectMapper jsonObjectMapper;

    @Autowired
    private MockMvc mockMvc;

    protected MockMvc getMockMvc() {
        return this.mockMvc;
    }

}
