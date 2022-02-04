package com.sikulix.test206;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.MethodOrderer.DisplayName;
import org.sikuli.basics.Debug;
import org.sikuli.script.*;
import org.sikuli.script.Image;
import org.sikuli.script.support.Commons;
import org.sikuli.script.support.RunTime;
import org.sikuli.script.support.gui.SXDialog;

import java.awt.*;
import java.io.File;
import java.util.Iterator;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@TestMethodOrder(DisplayName.class)
public class Test206 {

  static Region screen = new Screen();

  static String tag = "";
  static boolean verbose = false;
  static String imagePath;
  static SXDialog sxDialogImages;
  static Dimension sxDialogImagesSize;

  @BeforeAll
  static void beforeAll() {
    Commons.info("beforeAll");
    File workDir = Commons.getWorkDir();
    String sikulix_test_tag = System.getenv("SIKULIX_TEST_TAG");
    if (sikulix_test_tag != null && !sikulix_test_tag.isEmpty()) {
      tag = sikulix_test_tag;
    }
    String sikulix_test_verbose = System.getenv("SIKULIX_TEST_VERBOSE");
    if (sikulix_test_verbose != null) {
      verbose = true;
    }
    String images = "src/main/resources/images";
    ImagePath.setBundleFolder(new File(workDir, images));
    imagePath = ImagePath.getBundlePath();
    RunTime.loadOpenCV();
    //Commons.info("");
  }

  @AfterAll
  static void afterAll() {
    Commons.info("afterAll");
    App.focus(appRun);
  }

  @BeforeEach
  void beforeEach() {
    sxDialogImages = new SXDialog("#image; file:" + imagePath + "/SikulixTest001.png;", SXDialog.POSITION.TOPLEFT);
    sxDialogImagesSize = sxDialogImages.getFinalSize();
    Commons.stopTrace();
    //Debug.on(3);
  }

  @AfterEach
  void afterEach() {
    sxDialogImages.dispose();
    Debug.off();
    App.focus(appRun);
  }

  static String appRun = "idea";

  static void show(SXDialog dialog) {
    show(dialog,0,0);
  }

  static void show(SXDialog dialog, long when) {
    show(dialog, when,0);
  }

  static void show(SXDialog dialog, long when, long time) {
    screen = new Region(0, 0, sxDialogImagesSize.width, sxDialogImagesSize.height);
    new Thread(new Runnable() {
      @Override
      public void run() {
        if (when > 0) {
          Commons.pause(when);
        }
        dialog.setAlwaysOnTop(true);
        dialog.run();
        if (time > 0) {
          Commons.pause(time);
          dialog.dispose();
        }
      }
    }).start();
    Commons.pause(1);
  }

  boolean trace() {
    StackTraceElement stackTrace = Thread.currentThread().getStackTrace()[2];
    String className = stackTrace.getFileName().replace(".java", "");
    String methodName = stackTrace.getMethodName();
    System.out.printf("[%s::%s] ", className, methodName);
    if (tag.isEmpty() || methodName.contains(tag)) {
      System.out.println("");
      return true;
    }
    System.out.println("skipped");
    return false;
  }

  void info(String msg, Object... args) {
    if (!verbose) {
      return;
    }
    Commons.info(msg, args);
  }

  @Test
  void test001_find_Image() {
    if (!trace()) return;
    Image image = Image.create("SikulixTest001");
    try {
      Match match = image.find("img");
      info("%s %s", match, match.getImage());
      assertTrue(true);
    } catch (FindFailed e) {
      assertTrue(false);
    }
  }
  @Test
  void test002_findResized_Image() {
    if (!trace()) return;
    Image image = Image.create("SikulixTest001");
    try {
      Match match = image.find("img100");
      info("100: %s %s", match, match.getImage());
      match = image.find(new Pattern("img100").resize(1.25f));
      info("125: %s %s", match, match.getImage());
      match = image.find(new Pattern("img100").resize(1.5f));
      info("150: %s %s", match, match.getImage());
      match = image.find(new Pattern("img100").resize(2f));
      info("200: %s %s", match, match.getImage());
      assertTrue(true);
    } catch (FindFailed e) {
      assertTrue(false);
    }
  }
  @Test
  void test005_getAll_Image() {
    if (!trace()) return;
    Image image = Image.create("SikulixTest001");
    int expected = 6;
    List<Match> matches = image.getAll("img");
    for (Match match : matches) {
      info("%s %s", match, match.getImage());
    }
    assertEquals(expected, matches.size());
  }
  @Test
  void test011_find_Region() {
    if (!trace()) return;
    show(sxDialogImages);
    try {
      Match match = screen.find("img");
      info("%s %s", match, match.getImage());
      assertTrue(true);
    } catch (FindFailed e) {
      assertTrue(false);
    }
  }
  @Test
  void test012_findAgain_Region() {
    if (!trace()) return;
    Image.create("img").setLastSeen(null);
    show(sxDialogImages);
    try {
      Match match = screen.find("img");
      info("%s %s", match, match.getImage());
      match = screen.find("img");
      info("%s %s", match, match.getImage());
      assertTrue(true);
    } catch (FindFailed e) {
      assertTrue(false);
    }
  }
  @Test
  void test014_wait_Region() {
    if (!trace()) return;
    show(sxDialogImages, 2);
    try {
      Match match = screen.wait("img");
      info("%s %s", match, match.getImage());
      assertTrue(true);
    } catch (FindFailed e) {
      assertTrue(false);
    }
  }
  @Test
  void test018_waitVanish_Region() {
    if (!trace()) return;
    show(sxDialogImages, 0, 2);
    boolean success = screen.waitVanish("img");
    assertTrue(success);
  }
  @Test
  void test019_waitVanishNotThere_Region() {
    if (!trace()) return;
    boolean success = screen.waitVanish("img");
    assertTrue(!success);
  }
  @Test
  void test031_findAll_Region() {
    if (!trace()) return;
    show(sxDialogImages);
    Match match;
    int expected = 6;
    int actual = 0;
    //Commons.startTrace();
    try {
      Iterator<Match> finderIterator = screen.findAll("img");
      while (finderIterator.hasNext()) {
        actual++;
        match = finderIterator.next();
        info("%s %s", match, match.getImage());
      }
      assertEquals(expected, actual);
    } catch (FindFailed e) {
      assertTrue(false);
    }
  }
  @Test
  void test022_getAll_Region() {
    if (!trace()) return;
    show(sxDialogImages);
    int expected = 6;
    List<Match> matches = screen.getAll("img");
    for (Match match : matches) {
      info("%s %s", match, match.getImage());
    }
    assertEquals(expected, matches.size());
  }
  @Test
  void test023_getAllWait_Region() {
    if (!trace()) return;
    show(sxDialogImages, 2);
    int expected = 6;
    List<Match> matches = screen.getAll(3,"img");
    for (Match match : matches) {
      info("%s %s", match, match.getImage());
    }
    assertEquals(expected, matches.size());
  }
}
