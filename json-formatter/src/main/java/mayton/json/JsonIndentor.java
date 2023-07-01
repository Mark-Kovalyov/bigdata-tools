package mayton.json;

import java.io.Reader;
import java.io.Writer;

public interface JsonIndentor {

    void process(Reader r, Writer w);

}
