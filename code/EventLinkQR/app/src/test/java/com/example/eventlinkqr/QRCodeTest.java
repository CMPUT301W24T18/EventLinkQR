package com.example.eventlinkqr;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mockStatic;

import android.graphics.Bitmap;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class QRCodeTest {
    private static final String TEST_CODE_TEXT = "https://test.com";
    private static final int WIDTH = 32;
    private static final int HEIGHT = 32;
    private QRCode code;
    @Mock
    private Bitmap mockBitmap;

    @BeforeEach
    public void setup() {
        code = new QRCode(TEST_CODE_TEXT, QRCode.CHECK_IN_TYPE, "event");
    }

    @Test
    public void testGetCodeText() {
        assertEquals(code.getCodeText(), TEST_CODE_TEXT);
    }

    @Test
    public void testToBitmap() throws QRCodeGeneratorException {
        try (MockedStatic<QRCodeGenerator> mockedQRCodeGen = mockStatic(QRCodeGenerator.class)) {
            mockedQRCodeGen.when(() -> QRCodeGenerator.imageFromQRCode(eq(code), eq(WIDTH), eq(HEIGHT)))
                    .thenReturn(mockBitmap);
            Bitmap image = code.toBitmap(WIDTH, HEIGHT);
            assertEquals(image, mockBitmap);
        }
    }

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
