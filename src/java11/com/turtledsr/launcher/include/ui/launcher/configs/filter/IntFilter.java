/*
Only allows integers as input in a textfield
*/

package com.turtledsr.launcher.include.ui.launcher.configs.filter;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.DocumentFilter;

public final class IntFilter extends DocumentFilter {
  @Override
  public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr) throws BadLocationException {
    Document doc = fb.getDocument();
    StringBuilder builder = new StringBuilder();
    builder.append(doc.getText(0, doc.getLength()));
    builder.insert(offset, string);

    if(test(builder.toString())) {
      super.insertString(fb, offset, string, attr);
    }
  }

  @Override
  public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
    Document doc = fb.getDocument();
    StringBuilder builder = new StringBuilder();
    builder.append(doc.getText(0, doc.getLength()));
    builder.replace(offset, offset + length, text);

    if(test(builder.toString())) {
      super.replace(fb, offset, length, text, attrs);
    }
  }

  @Override
  public void remove(FilterBypass fb, int offset, int length) throws BadLocationException {
    Document doc = fb.getDocument();
    StringBuilder builder = new StringBuilder();
    builder.append(doc.getText(0, doc.getLength()));
    builder.delete(offset, offset + length);

    if(test(builder.toString())) {
      super.remove(fb, offset, length);
    }
  }

  private boolean test(String text) {
    if(text.contains(".")) return false;

    try {Integer.parseInt(text);
      return true;
    } catch(Exception e) {
      return false;
    }
  }
}
