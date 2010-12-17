package org.apache.abdera.i18n.text.io;

import static org.junit.Assert.assertEquals;

import java.util.Locale;

import org.apache.abdera.i18n.text.io.CompressionUtil.CompressionCodec;
import org.junit.Test;

public class CompressionUtilTest {
    
    @Test
    public void getCodecWithTurkishLocale (){
        Locale.setDefault(new Locale("tr", "", ""));
        CompressionCodec codec = CompressionUtil.getCodec("gzip");
        assertEquals("GZIP", codec.toString());
    }
    
    @Test
    public void compressionCodecWithTurkishLocale (){
        CompressionCodec codec = CompressionCodec.value("gzip");
        assertEquals("GZIP", codec.toString());
    }
}
