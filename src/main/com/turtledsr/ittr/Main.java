package com.turtledsr.ittr;

/*
Entry point for unsupported java versions 
*/

import javax.swing.JOptionPane;

public final class Main {
  public static void main(String[] args) throws Exception {
    JOptionPane.showMessageDialog(null, "[ERROR]: Invalid java version, please use Java 11+");
    return;
  }
}
