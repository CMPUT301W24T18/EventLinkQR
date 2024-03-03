package com.example.eventlinkqr;

import android.graphics.Bitmap;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

/** Class for generating QR code images from QR code objects. This class
 * consists of only static methods. */
public class QRCodeGenerator {
    /** Static writer for creating QR Codes */
    private static final QRCodeWriter writer = new QRCodeWriter();

    /**
     * @param code The QR code object to be converted to an image
     * @return An image of containing the QR code
     * @throws QRCodeGeneratorException When an error occurs during encoding
     */
    public static Bitmap imageFromQRCode(QRCode code, int width, int height) throws QRCodeGeneratorException {
        BitMatrix matrix;
        try {
            matrix = writer.encode(code.getCodeText(), BarcodeFormat.QR_CODE, width, height);
        } catch (WriterException | IllegalArgumentException e) {
            throw new QRCodeGeneratorException();
        }

        return bitMatrixToBitmap(matrix);
    }

    /**
     * Convert a BitMatrix object to a Bitmap
     * @param bitMatrix The BitMatrix resulting from encoding information to a QR code
     * @return An image of the QR code encoded in the BitMatrix
     */
    private static Bitmap bitMatrixToBitmap(BitMatrix bitMatrix) {
        // OpenAI, 2024, ChatGPT, BitMatrix to Bitmap
        int width = bitMatrix.getWidth();
        int height = bitMatrix.getHeight();
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                bitmap.setPixel(x, y, bitMatrix.get(x, y) ? 0xFF000000 : 0xFFFFFFFF); // Black or white
            }
        }

        return bitmap;
    }
}
