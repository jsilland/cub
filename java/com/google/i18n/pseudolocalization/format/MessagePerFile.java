package com.google.i18n.pseudolocalization.format;

import com.google.i18n.pseudolocalization.message.DefaultVisitor;
import com.google.i18n.pseudolocalization.message.Message;
import com.google.i18n.pseudolocalization.message.NonlocalizableTextFragment;
import com.google.i18n.pseudolocalization.message.Placeholder;
import com.google.i18n.pseudolocalization.message.SimpleMessage;
import com.google.i18n.pseudolocalization.message.TextFragment;
import com.google.i18n.pseudolocalization.message.VisitorContext;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A MessageCatalog that treats the file contents as a single message encoded in
 * UTF8.
 */
class MessagePerFile implements MessageCatalog {

  private static final Charset UTF8 = Charset.forName("UTF-8");

  private List<Message> messages = new ArrayList<Message>();

  public ReadableMessageCatalog readFrom(InputStream stream) throws IOException {
    StringBuilder buf = new StringBuilder();
    BufferedReader reader = new BufferedReader(new InputStreamReader(stream, UTF8));
    int ch;
    while ((ch = reader.read()) != -1) {
      buf.append((char) ch);
    }
    reader.close();
    List<Message> list = new ArrayList<Message>();
    list.add(new SimpleMessage(buf.toString()));
    messages = Collections.unmodifiableList(list);
    return new ReadableMessageCatalog() {
      public Iterable<Message> readMessages() {
        return messages;
      }
      
      public void close() {
        // do nothing
      }
    };
  }

  public WritableMessageCatalog writeTo(final OutputStream out) {
    return new WritableMessageCatalog() {        
      public void writeMessage(Message msg) throws IOException {
        // TODO(jat): extract this to a common place
        final StringBuilder buf = new StringBuilder();
        msg.accept(new DefaultVisitor() {
          @Override
          public void visitNonlocalizableTextFragment(VisitorContext ctx,
              NonlocalizableTextFragment fragment) {
            buf.append(fragment.getText());
          }

          @Override
          public void visitPlaceholder(VisitorContext ctx, Placeholder placeholder) {
            buf.append(placeholder.getTextRepresentation());
          }

          @Override
          public void visitTextFragment(VisitorContext ctx, TextFragment fragment) {
            buf.append(fragment.getText());
          }
        });
        out.write(buf.toString().getBytes(UTF8));
      }
      
      public void close() throws IOException {
        out.close();
      }
    };
  }
}