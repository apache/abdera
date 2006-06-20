package org.apache.abdera.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.Reader;

import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;

import org.apache.abdera.model.Base;

/**
 * Provides a simple (and likely somewhat inefficient) implementation of 
 * javax.xml.transform.Source that allows Abdera objects to be used with
 * the javax.xml.transform API's
 */
public class AbderaSource 
  extends StreamSource 
  implements Source {

  private Base base = null;
  
  public AbderaSource(Base base) {
    this.base = base;
  }
  
  @Override
  public InputStream getInputStream() {
    try {
      PipedOutputStream pipeout = new PipedOutputStream();
      PipedInputStream pipein = new PipedInputStream(pipeout);
      base.writeTo(pipeout);
      pipeout.flush();
      pipeout.close();
      return pipein;
    } catch (IOException e) {}
    return null;
  }

  @Override
  public Reader getReader() {
    return null;
  }

  @Override
  public void setInputStream(InputStream in) {
    throw new UnsupportedOperationException();
  }

  @Override
  public void setReader(Reader reader) {
    throw new UnsupportedOperationException();
  }

}
