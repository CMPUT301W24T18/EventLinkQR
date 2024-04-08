package com.example.eventlinkqr;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mockStatic;

import android.graphics.Bitmap;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Test class for QRCodeGenerator.
 * This class performs unit tests on the QRCodeGenerator's ability to generate QR code images.
 */

@ExtendWith(MockitoExtension.class)
public class QRCodeGeneratorTest {
    private static final int WIDTH = 32;
    private static final int HEIGHT = 32;
    private QRCode code;
    @Mock
    private Bitmap mockBitmap;

    /**
     * Sets up the QRCode instance for testing.
     */
    @BeforeEach
    public void setup() {
        code = new QRCode("test", QRCode.CHECK_IN_TYPE, "event");
    }

    /**
     * Tests the conversion of a QRCode object to a Bitmap image.
     * Validates if the generated Bitmap matches the expected mock Bitmap for a given width and height.
     * @throws QRCodeGeneratorException if there's an error in generating the QR code image.
     */
    @Test
    public void testImageFromQRCode() throws QRCodeGeneratorException {
        try (MockedStatic<Bitmap> mockedBitmap = mockStatic(Bitmap.class)) {
            mockedBitmap.when(() -> Bitmap.createBitmap(WIDTH, HEIGHT, Bitmap.Config.RGB_565))
                    .thenReturn(mockBitmap);
            Bitmap image = code.toBitmap(WIDTH, HEIGHT);
            assertEquals(image, mockBitmap);
        }
    }
}
