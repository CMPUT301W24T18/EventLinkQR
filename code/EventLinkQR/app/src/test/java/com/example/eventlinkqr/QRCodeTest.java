package com.example.eventlinkqr;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mockStatic;

import android.graphics.Bitmap;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Test class for QRCode.
 * Conducts unit tests on QRCode's methods to ensure proper functionality, including conversion to Bitmap and data retrieval.
 */
@ExtendWith(MockitoExtension.class)
public class QRCodeTest {
    private static final String TEST_CODE_TEXT = "https://test.com";
    private static final int WIDTH = 32;
    private static final int HEIGHT = 32;
    private QRCode code;
    @Mock
    private Bitmap mockBitmap;

    /**
     * Sets up the QRCode instance with a test QR code text for testing.
     */
    @BeforeEach
    public void setup() {
        code = new QRCode(TEST_CODE_TEXT, QRCode.CHECK_IN_TYPE, "event");
    }

    /**
     * Tests if the getCodeText method correctly returns the QR code's text.
     */
    @Test
    public void testGetCodeText() {
        assertEquals(code.getCodeText(), TEST_CODE_TEXT);
    }

    /**
     * Tests the conversion of QRCode to a Bitmap.
     * Ensures that the QRCode's toBitmap method generates the expected Bitmap.
     * @throws QRCodeGeneratorException if there's an error in generating the QR code image.
     */
    @Test
    public void testToBitmap() throws QRCodeGeneratorException {
        try (MockedStatic<QRCodeGenerator> mockedQRCodeGen = mockStatic(QRCodeGenerator.class)) {
            mockedQRCodeGen.when(() -> QRCodeGenerator.imageFromQRCode(eq(code), eq(WIDTH), eq(HEIGHT)))
                    .thenReturn(mockBitmap);
            Bitmap image = code.toBitmap(WIDTH, HEIGHT);
            assertEquals(image, mockBitmap);
        }
    }

    /**
     * Tests if QRCode's toBitmap method correctly handles exceptions.
     * Ensures that an exception is thrown when trying to convert an invalid QR code to a Bitmap.
     */
    @Test
    public void testToBitmapThrows() {
        QRCode emptyCode = new QRCode("", QRCode.CHECK_IN_TYPE, "event");

        try (MockedStatic<QRCodeGenerator> mockedQRCodeGen = mockStatic(QRCodeGenerator.class)) {
            mockedQRCodeGen.when(() -> QRCodeGenerator.imageFromQRCode(eq(emptyCode), eq(WIDTH), eq(HEIGHT)))
                            .thenThrow(QRCodeGeneratorException.class);
            assertThrows(QRCodeGeneratorException.class, () -> emptyCode.toBitmap(WIDTH, HEIGHT));
        }
    }
}
