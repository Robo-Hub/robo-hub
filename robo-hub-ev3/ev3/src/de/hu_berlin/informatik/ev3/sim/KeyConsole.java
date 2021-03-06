package de.hu_berlin.informatik.ev3.sim;

import java.io.IOException;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

/**
 * Created by robert on 17.05.16.
 */
public class KeyConsole {

  public static String ttyConfig;

  public static void test(String[] args) {

    try {
      setTerminalToCBreak();

      int i=0;
      while (true) {

        System.out.println( ""+ i++ );

        if ( System.in.available() != 0 ) {
          int c = System.in.read();
          System.out.println( c + "" );
          if ( c == 0x1B ) {
            break;
          }
        }
      } // end while
    } catch (IOException e) {
      System.err.println("IOException");
    } catch (InterruptedException e) {
      System.err.println("InterruptedException");
    } finally {
      try {
        stty( ttyConfig.trim() );
      } catch (Exception e) {
        System.err.println("Exception restoring tty config");
      }
    }
  }

  public static void setTerminalToCBreak() throws IOException, InterruptedException {
    ttyConfig = stty("-g");

    // set the console to be character-buffered instead of line-buffered
    stty("-icanon min 1");

    // disable character echoing
    stty("-echo");
  }

  /**
  *  Execute the stty command with the specified arguments
  *  against the current active terminal.
  */
  public static String stty(final String args) throws IOException, InterruptedException {
    String cmd = "stty " + args + " < /dev/tty";

    return exec(new String[] {"sh","-c", cmd});
  }

  /**
   *  Execute the specified command and return the output
   *  (both stdout and stderr).
   */
  public static String exec(final String[] cmd) throws IOException, InterruptedException {
    ByteArrayOutputStream bout = new ByteArrayOutputStream();

    Process p = Runtime.getRuntime().exec(cmd);
    int c;
    InputStream in = p.getInputStream();

    while ((c = in.read()) != -1) {
      bout.write(c);
    }

    in = p.getErrorStream();

    while ((c = in.read()) != -1) {
      bout.write(c);
    }

    p.waitFor();

    String result = new String(bout.toByteArray());
    return result;
  }
}
