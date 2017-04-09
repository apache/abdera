package org.apache.abdera.test.ext.serializer;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import org.apache.abdera.Abdera;
import org.apache.abdera.model.Document;
import org.apache.abdera.model.Element;

public final class AbderaTestHelper {

    public static final <T extends Element> T deserialize(final ByteArrayOutputStream serialized) {
        final ByteArrayInputStream in = new ByteArrayInputStream(serialized.toByteArray());
        final Document<T> doc = Abdera.getInstance().getParser().parse(in);
        return doc.getRoot();
    }

}
