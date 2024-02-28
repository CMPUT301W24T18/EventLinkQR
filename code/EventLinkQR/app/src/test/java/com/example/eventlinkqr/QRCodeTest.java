package com.example.eventlinkqr;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import org.junit.jupiter.api.*;
import org.mockito.MockedStatic;


public class QRCodeTest {
    private static final String TEST_CODE_TEXT = "https://test.com";
    private static final int WIDTH = 32;
    private static final int HEIGHT = 32;
    private QRCode code;

    @BeforeEach
    public void setup() {
        code = new QRCode(TEST_CODE_TEXT);
    }

    @Test
    public void testGetCodeText() {
        assertEquals(code.getCodeText(), TEST_CODE_TEXT);
    }

    @Test
    public void testToBitmap() throws QRCodeGeneratorException {
        try (MockedStatic<Bitmap> mockedBitmap = mockStatic(Bitmap.class)) {
            mockedBitmap.when(() -> Bitmap.createBitmap(WIDTH, HEIGHT, Bitmap.Config.RGB_565))
                    .thenReturn(mock(Bitmap.class));
            Bitmap image = code.toBitmap(WIDTH, HEIGHT);
            assertNotNull(image);
        }
    }

    @Test
    public void testToBitmapThrows() {
        QRCode emptyCode = new QRCode("");

        assertThrows(QRCodeGeneratorException.class, () -> emptyCode.toBitmap(WIDTH, HEIGHT));
    }
}
