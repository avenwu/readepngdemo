package net.avenwu.png;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by aven on 6/15/17.
 */
public class CheckPngFile {
  public static void main(String... args) {
    System.out.println("Start..");

    String[] paths = {"resources/fake-image.png", "resources/lenna_original.png"};
    for (String path : paths) {
      System.out.println("Check image=" + path);
      if (matchPngSignature(path)) {
        if (hasWidth(path)) {
          System.out.println("Png check succeeded");
        } else {
          System.out.println("Png check content failed");
        }
      } else {
        System.out.println("Png check header failed");
      }
    }
  }

  /**
   * The first eight bytes of a PNG file always contain the following values:
   * <p/>
   * (decimal)              137  80  78  71  13  10  26  10
   * (hexadecimal)           89  50  4e  47  0d  0a  1a  0a
   * (ASCII C notation)    \211   P   N   G  \r  \n \032 \n
   */
  static int[] PNG_HEADER_ASCII = {0x89, 0x50, 0x4e, 0x47, 0x0d, 0x0a, 0x1a, 0x0a};

  public static boolean matchPngSignature(String path) {
    File file = new File(path);
    if (!file.exists()) {
      System.out.println("File not exist: " + file.getAbsolutePath());
      return false;
    }
    if (!file.isFile()) {
      System.out.println("Not file: " + file.getAbsolutePath());
    }
    if (file.exists() && file.isFile()) {
      InputStream inputStream = null;
      try {
        inputStream = new FileInputStream(file);
        byte[] header = new byte[PNG_HEADER_ASCII.length];
        int read = inputStream.read(header);
        if (read != PNG_HEADER_ASCII.length) {
          System.out.println("Failed to get image header: read " + read + "bytes");
          return false;
        }
        for (int i = 0; i < PNG_HEADER_ASCII.length; i++) {
          //必须的，去高位，只保留两位十六进制结果
          int c = 0xFF & header[i];
          if (c != PNG_HEADER_ASCII[i]) {
            System.out.println("Miss match byte[" + i + "]=" + header[i]);
            return false;
          }
        }
        // check IHDR
        // length : 4bytes
        byte[] lengthBytesArray = new byte[4];
        read = inputStream.read(lengthBytesArray);
        int l = bytes2int(lengthBytesArray);
        System.out.println("lengthBytesArray=" + l);

        // chunk type : 4bytes
        // 73 72 68 82
        byte[] chunkType = new byte[4];
        read = inputStream.read(chunkType);

        // length data
        byte[] widthBytesArray = new byte[4];
        // widthBytesArray
        read = inputStream.read(widthBytesArray);
        int width = bytes2int(widthBytesArray);
        System.out.println("width:" + width);
        //height
        read = inputStream.read(widthBytesArray);
        int height = bytes2int(widthBytesArray);
        System.out.println("height:" + height);
      } catch (IOException e) {
        e.printStackTrace();
        return false;
      } finally {
        if (inputStream != null) {
          try {
            inputStream.close();
          } catch (IOException e) {
            e.printStackTrace();
          }
        }
      }
    }
    return true;
  }

  // ((lengthBytesArray[0] & 0xFF) << 24) + ((lengthBytesArray[1] & 0xFF) << 16) + ((lengthBytesArray[2] & 0xFF) << 8) + (lengthBytesArray[3] & 0xFF);
  public static int bytes2int(byte[] bytes) {
    int length = bytes.length;
    int sum = 0;
    for (int i = 0; i < length; i++) {
      sum += ((bytes[i] & 0xFF) << (8 * (length - i - 1)));
    }
    return sum;
  }

  public static boolean hasWidth(String path) {
    File file = new File(path);
    if (!file.exists()) {
      System.out.println("File not exist: " + file.getAbsolutePath());
      return false;
    }
    if (!file.isFile()) {
      System.out.println("Not file: " + file.getAbsolutePath());
    }
    try {
      BufferedImage bufferedImage = ImageIO.read(file);
      if (bufferedImage == null) {
        System.out.println("Load file as image failed");
        return false;
      }
      if (bufferedImage.getWidth() <= 0 || bufferedImage.getWidth() <= 0) {
        System.out.println("Invalid image, as et width & height failed");
        return false;
      }
    } catch (IOException e) {
      e.printStackTrace();
    } finally {
    }

    return true;
  }
}
