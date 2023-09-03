package mayton.json;

import org.junit.jupiter.api.Test;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

import static org.junit.jupiter.api.Assertions.*;

class JsonCheckTest {

    @Test
    void test1() throws FileNotFoundException {
        assertEquals(0, JsonCheck.check(new FileInputStream("testdata/01.json"), new FileInputStream("testdata/01-schema.json")));
    }

}