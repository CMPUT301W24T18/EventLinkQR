package com.example.eventlinkqr;


import static org.junit.Assert.assertEquals;

import android.graphics.Bitmap;

import com.google.zxing.BinaryBitmap;
import com.google.zxing.RGBLuminanceSource;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.QRCodeReader;

import org.junit.Test;

public class QRCodeInstrumentedTest {
    private static final String TEST_CODE_TEXT = "https://test.com";
    private static final int WIDTH = 64;
    private static final int HEIGHT = 64;

    @Test
    public void testImageGeneration() throws Exception {
        QRCode code = new QRCode(TEST_CODE_TEXT);
        QRCodeReader reader = new QRCodeReader();

        Bitmap image;
        image = code.toBitmap(WIDTH, HEIGHT);

        int[] pixels = new int[image.getWidth() * image.getHeight()];
        image.getPixels(pixels, 0, image.getWidth(), 0, 0, image.getWidth(), image.getHeight());
        BinaryBitmap binaryBitmap = new BinaryBitmap(new HybridBinarizer(new RGBLuminanceSource(image.getWidth(), image.getHeight(), pixels)));

        String decodedTextFromBitmap;
        try {
            decodedTextFromBitmap = reader.decode(binaryBitmap).getText();
        } catch (Exception e) {
            throw new Exception();
        }

        assertEquals(decodedTextFromBitmap, code.getCodeText());
    }
}
