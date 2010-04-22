package tests;
/*
 * © Copyright 2010 Martin Yrjölä. All Rights Reserved.
 * See COPYING for information on licensing.
 */

import controllers.FileController;
import exceptions.CorruptLevelFileException;
import org.junit.Assert;
import org.junit.Test;

import java.io.StringReader;

public class FileControllerTest {

    @Test
    public void testCorruptLevelFiles() {
        String corrupt1 =
                        "3 4\n" +
                        "111\n" +
                        "1v1\n" +
                        "111";
        String corrupt2 =
                        "3 3\n" +
                        "111\n" +
                        "1v11\n" +
                        "111";
        try {
            FileController.loadWorld(new StringReader(corrupt1));
            Assert.fail("FileController was able to read file with corrupt header.");
        } catch (CorruptLevelFileException ignored) {
            // Success if caught.
        }
        try {
            FileController.loadWorld(new StringReader(corrupt2));
            Assert.fail("FileController was able to read line with incorrect width.");
        } catch (CorruptLevelFileException ignored) {
            // Success if caught.
        }
    }

    @Test
    public void testCorrectFile() throws CorruptLevelFileException {
        String correct =
                        "3 3\n" +
                        "111\n" +
                        "1v1\n" +
                        "111";
        char[][] levelMatrix = FileController.loadWorld(new StringReader(correct));
        Assert.assertArrayEquals(levelMatrix[0], new char[] {'1', '1', '1'});
        Assert.assertArrayEquals(levelMatrix[1], new char[] {'1', 'v', '1'});
        Assert.assertArrayEquals(levelMatrix[2], new char[] {'1', '1', '1'});
    }
}
