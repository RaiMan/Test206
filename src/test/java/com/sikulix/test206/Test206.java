package com.sikulix.test206;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.MethodOrderer.DisplayName;
import org.sikuli.basics.Debug;
import org.sikuli.basics.Settings;
import org.sikuli.script.*;
import org.sikuli.script.Image;
import org.sikuli.script.support.Commons;
import org.sikuli.script.support.RunTime;
import org.sikuli.script.support.devices.MouseDevice;
import org.sikuli.script.support.devices.ScreenDevice;
import org.sikuli.script.support.gui.SXDialog;

import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
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
  static boolean mouseOK = true;
  static boolean screenOK = true;

  @BeforeAll
  static void beforeAll() {
    Commons.info("beforeAll");
    if (!MouseDevice.isUseable()) {
      Commons.error("FATAL: Mouse not useable - skipping screen related tests");
      mouseOK = false;
    }
    if (!ScreenDevice.isUseable()) {
      Commons.error("FATAL: Terminating: Screen capture blocked - skipping screen related tests");
      screenOK = false;
    }
    File workDir = Commons.getWorkDir();
    String sikulix_test_tag = System.getenv("SIKULIX_TEST_TAG");
    if (sikulix_test_tag != null && !sikulix_test_tag.isEmpty()) {
      tag = sikulix_test_tag;
    } else if (!mouseOK || !screenOK) {
      tag = "Image";
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

  void highlight(Element element) {
    if (!verbose) {
      return;
    }
    if (element instanceof Region) {
      ((Region) element).highlight(1);
    }
  }

  void highlight(Element element, List<Match> matches) {
    if (!verbose) {
      return;
    }
    if (element instanceof Region) {
      boolean actionLogs = Settings.ActionLogs;
      Settings.ActionLogs = false;
      ((Region) element).highlight(matches, 2);
      Settings.ActionLogs = actionLogs;
    }
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
  void test006_getAnyAll_Image() { //TODO minScore 0,94 (Region: 0,98) ???
    if (!trace()) return;
    //verbose = true;
    double minScore = 1;
    Image image = Image.create("SikulixTest001");
    List<Object> targets = Arrays.asList(new String[]{"img", "img100"});
    List<List<Match>> matchesList = image.getAnyAll(targets);
    List<Match> matches = matchesList.get(0);
    matches.addAll(matchesList.get(1));
    for (Match match : matches) {
      info("%d: %s %s", match.getIndex(), match, match.getImage().getName());
      minScore = Math.min(minScore, match.getScore());
    }
    boolean indexOK = matches.get(0).getIndex() == 0 && matches.get(matches.size() - 1).getIndex() == 1;
    assertTrue(matches.size() == 7 && indexOK);
  }

  @Test
  void test011_find_Region() {
    if (!trace()) return;
    SXDialog.onScreen(sxDialogImages);
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
    SXDialog.onScreen(sxDialogImages);
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
    SXDialog.onScreen(sxDialogImages, 2);
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
    SXDialog.onScreen(sxDialogImages, 0, 2);
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
  void test021_findAll_Region() {
    if (!trace()) return;
    SXDialog.onScreen(sxDialogImages);
    Match match;
    int expected = 6;
    int actual = 0;
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
    SXDialog.onScreen(sxDialogImages);
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
    SXDialog.onScreen(sxDialogImages, 2);
    int expected = 6;
    List<Match> matches = screen.getAll(3, "img");
    for (Match match : matches) {
      info("%s %s", match, match.getImage());
    }
    assertEquals(expected, matches.size());
  }

  @Test
  void test031_getAny_Region() {
    if (!trace()) return;
    //verbose = true;
    double minScore = 1;
    SXDialog.onScreen(sxDialogImages);
    List<Object> targets = Arrays.asList("sikulix-red", "img", "sikulix-red", "img100");
    List<Match> matches = screen.getAny(targets);
    highlight(screen, matches);
    for (Match match : matches) {
      info("%d: %s %s", match.getIndex(), match, match.getImage().getName());
      minScore = Math.min(minScore, match.getScore());
    }
    boolean indexOK = matches.get(0).getIndex() == 1 && matches.get(1).getIndex() == 3;
    assertTrue(matches.size() == 2 && indexOK && minScore > 0.99);
  }

  @Test
  void test032_getAnyAll_Region() {
    if (!trace()) return;
    //verbose = true;
    double minScore = 1;
    SXDialog.onScreen(sxDialogImages);
    List<Object> targets = Arrays.asList("img", "img100");
    List<List<Match>> matchesList = screen.getAnyAll(targets);
    List<Match> matches = matchesList.get(0);
    matches.addAll(matchesList.get(1));
    highlight(screen, matches);
    for (Match match : matches) {
      info("%d: %s %s", match.getIndex(), match, match.getImage().getName());
      minScore = Math.min(minScore, match.getScore());
    }
    boolean indexOK = matches.get(0).getIndex() == 0 && matches.get(matches.size() - 1).getIndex() == 1;
    assertTrue(matches.size() == 7 && indexOK && minScore > 0.98);
  }

  @Test
  void test033_getBest_Region() {
    if (!trace()) return;
    //verbose = true;
    SXDialog.onScreen(sxDialogImages);
    List<Object> targets = Arrays.asList("img200", "img", "img100");
    Match match = screen.getBest(targets);
    highlight(match);
    info("%d: %s %s", match.getIndex(), match, match.getImage().getName());
    assertEquals(1, match.getIndex());
  }

  @Test
  void test034_waitBest_Region() {
    if (!trace()) return;
    //verbose = true;
    SXDialog.onScreen(sxDialogImages, 2);
    List<Object> targets = Arrays.asList("img200", "img", "img100");
    Match match = screen.waitBest(3, targets);
    if (match != null) {
      highlight(match);
      info("%d: %s %s", match.getIndex(), match, match.getImage().getName());
      assertEquals(1, match.getIndex());
    } else {
      assertTrue(false);
    }
  }

  @Test
  void test035_waitAny_Region() {
    if (!trace()) return;
    //verbose = true;
    double minScore = 1;
    SXDialog.onScreen(sxDialogImages, 2);
    List<Object> targets = Arrays.asList("sikulix-red", "img", "sikulix-red", "img100");
    List<Match> matches = screen.waitAny(3, targets);
    highlight(screen, matches);
    for (Match match : matches) {
      info("%d: %s %s", match.getIndex(), match, match.getImage().getName());
      minScore = Math.min(minScore, match.getScore());
    }
    boolean indexOK = matches.get(0).getIndex() == 1 && matches.get(1).getIndex() == 3;
    assertTrue(matches.size() == 2 && indexOK && minScore > 0.99);
  }

}
