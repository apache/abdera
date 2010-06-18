package org.apache.abdera.i18n.test.templates;

import static org.junit.Assert.assertEquals;

import java.util.HashMap;
import java.util.Map;

import org.apache.abdera.i18n.templates.Template;

import org.junit.Test;

public class TestTemplate {
  
    @Test
    public void testTemplateNeg() {
        Template t = new Template("*http://cnn.com/{-neg|all|foo,bar}");
        Map m = new HashMap();
        m.put("foo", "value");
        String out = t.expand(m);

        assertEquals("*http://cnn.com/", out);
    }
}
