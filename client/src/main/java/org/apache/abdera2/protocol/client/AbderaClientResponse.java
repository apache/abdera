package org.apache.abdera2.protocol.client;

import org.apache.abdera2.model.Document;
import org.apache.abdera2.model.Element;
import org.apache.abdera2.parser.ParseException;
import org.apache.abdera2.parser.Parser;
import org.apache.abdera2.parser.ParserOptions;

public interface AbderaClientResponse extends ClientResponse {

  public abstract <T extends Element> Document<T> getDocument()
      throws ParseException;

  public abstract <T extends Element> Document<T> getDocument(
      ParserOptions options) throws ParseException;

  public abstract <T extends Element> Document<T> getDocument(Parser parser)
      throws ParseException;

  public abstract <T extends Element> Document<T> getDocument(Parser parser,
      ParserOptions options) throws ParseException;

}