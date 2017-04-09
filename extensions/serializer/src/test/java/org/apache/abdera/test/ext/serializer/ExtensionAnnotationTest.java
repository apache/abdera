package org.apache.abdera.test.ext.serializer;

import java.io.ByteArrayOutputStream;
import javax.xml.namespace.QName;

import org.apache.abdera.Abdera;
import org.apache.abdera.ext.serializer.ConventionSerializationContext;
import org.apache.abdera.ext.serializer.annotation.Entry;
import org.apache.abdera.ext.serializer.annotation.Extension;
import org.apache.abdera.model.Element;
import org.apache.abdera.model.ExtensibleElement;
import org.apache.abdera.writer.StreamWriter;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public final class ExtensionAnnotationTest {

    private static final String PREFIX = "foo";

    private static final String NS = "http://example.org/foo";

    private static final String EXT_NAME = "ext";

    private static final QName EXT_QNAME = new QName(NS, EXT_NAME, PREFIX);

    /**
     * Expected serialized result:
     * <?xml version='1.0'?>
     * <entry xmlns="http://www.w3.org/2005/Atom">
     *   <foo:ext xmlns:s="http://example.org/foo">
     *     foo
     *   </foo:ext>
     * </entry>
     */
    @Entry
    public static final class EntryWithSimpleExtension {

        private final String value;

        public EntryWithSimpleExtension(final String value) {
            this.value = value;
        }

        @Extension(prefix = PREFIX, ns = NS, name = EXT_NAME, simple = true)
        public String getSimpleExtension() {
            return value;
        }

    }

    /**
     * Expected serialized result:
     * <?xml version='1.0'?>
     * <entry xmlns="http://www.w3.org/2005/Atom">
     *   <foo:ext xmlns:s="http://example.org/foo">
     *     <foo:ext xmlns:s="http://example.org/foo">
     *       foo
     *     </foo:ext>
     *   </foo:ext>
     * </entry>
     */
    @Entry
    public static final class EntryWithExtensionNestingSimpleExtension {

        @Extension(prefix = PREFIX, ns = NS, name = EXT_NAME)
        public ExtensionNestingSimpleExtension getExtension() {
            return new ExtensionNestingSimpleExtension("foo");
        }

    }

    public static final class ExtensionNestingSimpleExtension {

        private final String value;

        public ExtensionNestingSimpleExtension(final String value) {
            this.value = value;
        }

        @Extension(prefix = PREFIX, ns = NS, name = EXT_NAME, simple = true)
        public String getNestedExtension() {
            return value;
        }

    }

    /**
     * Expected serialized result:
     * <?xml version='1.0'?>
     * <entry xmlns="http://www.w3.org/2005/Atom">
     *   <foo:ext xmlns:s="http://example.org/foo">
     *     <foo:ext xmlns:s="http://example.org/foo">
     *       <foo:ext xmlns:s="http://example.org/foo">
     *         foo
     *       </foo:ext>
     *     </foo:ext>
     *   </foo:ext>
     * </entry>
     */
    @Entry
    public static final class EntryWithExtensionNestingExtensionNestingSimpleExtension {

        @Extension(prefix = PREFIX, ns = NS, name = EXT_NAME)
        public ExtensionNestingExtensionNestingSimpleExtension getExtension() {
            return new ExtensionNestingExtensionNestingSimpleExtension("foo");
        }

    }

    public static final class ExtensionNestingExtensionNestingSimpleExtension {

        private final String value;

        public ExtensionNestingExtensionNestingSimpleExtension(final String value) {
            this.value = value;
        }

        @Extension(prefix = PREFIX, ns = NS, name = EXT_NAME)
        public ExtensionNestingSimpleExtension getExtensionNestingSimpleExtension() {
            return new ExtensionNestingSimpleExtension(value);
        }

    }

    private final Abdera abdera = Abdera.getInstance();

    @Test
    public void shouldGenerateSimpleExtension() {
        // given
        final EntryWithSimpleExtension source = new EntryWithSimpleExtension("foo");

        // when
        final StreamWriter streamWriter = abdera.newStreamWriter();
        final ByteArrayOutputStream serialized = new ByteArrayOutputStream();
        streamWriter.setOutputStream(serialized).setAutoIndent(true);
        final ConventionSerializationContext context = new ConventionSerializationContext(streamWriter);
        streamWriter.startDocument();
        context.serialize(source);
        streamWriter.endDocument();

        // then
        final org.apache.abdera.model.Entry entry = AbderaTestHelper.deserialize(serialized);
        final ExtensibleElement extension = entry.getExtension(EXT_QNAME);
        assertNotNull(extension);
        assertEquals("foo", extension.getText().trim());
    }

    @Test
    public void shouldGenerateExtensionNestingSimpleExtension() {
        // given
        final EntryWithExtensionNestingSimpleExtension source = new EntryWithExtensionNestingSimpleExtension();

        // when
        final StreamWriter streamWriter = abdera.newStreamWriter();
        final ByteArrayOutputStream serialized = new ByteArrayOutputStream();
        streamWriter.setOutputStream(serialized).setAutoIndent(true);
        final ConventionSerializationContext context = new ConventionSerializationContext(streamWriter);
        streamWriter.startDocument();
        context.serialize(source);
        streamWriter.endDocument();

        // then
        final org.apache.abdera.model.Entry entry = AbderaTestHelper.deserialize(serialized);
        final ExtensibleElement extension = entry.getExtension(EXT_QNAME);
        assertNotNull(extension);
        final Element simple = extension.getExtension(EXT_QNAME);
        assertNotNull(simple);
        assertEquals("foo", simple.getText().trim());
    }

    @Test
    public void shouldGenerateExtensionNestingExtensionNestingSimpleExtension() {
        // given
        final EntryWithExtensionNestingExtensionNestingSimpleExtension source = 
                new EntryWithExtensionNestingExtensionNestingSimpleExtension();

        // when
        final StreamWriter streamWriter = abdera.newStreamWriter();
        final ByteArrayOutputStream serialized = new ByteArrayOutputStream();
        streamWriter.setOutputStream(serialized).setAutoIndent(true);
        final ConventionSerializationContext context = new ConventionSerializationContext(streamWriter);
        streamWriter.startDocument();
        context.serialize(source);
        streamWriter.endDocument();

        // then
        final org.apache.abdera.model.Entry entry = AbderaTestHelper.deserialize(serialized);
        final ExtensibleElement extension = entry.getExtension(EXT_QNAME);
        assertNotNull(extension);
        final ExtensibleElement nested = extension.getExtension(EXT_QNAME);
        assertNotNull(nested);
        final Element simple = nested.getExtension(EXT_QNAME);
        assertNotNull(simple);
        assertEquals("foo", simple.getText().trim());
    }
}
