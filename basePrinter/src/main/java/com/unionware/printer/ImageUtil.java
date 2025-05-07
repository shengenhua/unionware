package com.unionware.printer;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.graphics.pdf.PdfRenderer;
import android.os.ParcelFileDescriptor;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class ImageUtil {
    public static ArrayList<Bitmap> pdfToBitmap(File pdfFile, int angle) {
        ArrayList<Bitmap> bitmaps = new ArrayList<>();
        double bitmapRatio = 3.5;
        /*try (ParcelFileDescriptor open = ParcelFileDescriptor.open(pdfFile, ParcelFileDescriptor.MODE_READ_ONLY)) {
            PdfRenderer renderer = new PdfRenderer(open);
            Document document = Document.openDocument(pdfFile.getAbsolutePath());
            Bitmap bitmap;
            final int pageCount = renderer.getPageCount();
            for (int i = 0; i < pageCount; i++) {
                try (PdfRenderer.Page page = renderer.openPage(i)) {
                    int width = (int) bitmapRatio * page.getWidth();
                    Page dPage = document.loadPage(i);
                    Matrix matrix = AndroidDrawDevice.fitPageWidth(dPage, width);
                    bitmap = AndroidDrawDevice.drawPage(dPage, matrix);
                    //ZR668/ZR638 打印机 允许设置打印角度 0度 90度 180度
                    if (angle > 0) {
                        bitmaps.add(getRotateBitmap(bitmap, angle * 90));
                    } else
                        bitmaps.add(bitmap);
                    // close the page
                    page.close();
                }
            }
            // close the renderer
            renderer.close();
            open.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }*/
        return convertPdfToBitmaps(pdfFile,0,0);
    }

    // 获得旋转角度之后的位图对象
    public static Bitmap getRotateBitmap(Bitmap bitmap, float rotateDegree) {
        android.graphics.Matrix matrix = new android.graphics.Matrix(); // 创建操作图片用的矩阵对象
        matrix.postRotate(rotateDegree); // 执行图片的旋转动作
        // 创建并返回旋转后的位图对象
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
                bitmap.getHeight(), matrix, false);
    }


    public static ArrayList<Bitmap> convertPdfToBitmaps(File file, int printWidth, int printHeight) {
        ArrayList<Bitmap> bitmaps = new ArrayList<>();
        ParcelFileDescriptor parcelFileDescriptor = null;
        try {
            parcelFileDescriptor = ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY);
            PdfRenderer pdfRenderer = new PdfRenderer(parcelFileDescriptor);
            for (int i = 0; i < pdfRenderer.getPageCount(); i++) {
                PdfRenderer.Page page = pdfRenderer.openPage(i);
//                Bitmap bitmap = Bitmap.createBitmap(printWidth, printHeight, Bitmap.Config.ARGB_4444);
                Bitmap bitmap = Bitmap.createBitmap(page.getWidth(), page.getHeight(), Bitmap.Config.ARGB_4444);
                page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY);
                page.close();
                bitmaps.add(convertToGrayscale(bitmap));
            }
            return bitmaps;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } finally {
            if (parcelFileDescriptor != null) {
                try {
                    parcelFileDescriptor.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static Bitmap convertPdfToBitmap(File file, int pageNumber, int printWidth, int printHeight) {
        ParcelFileDescriptor parcelFileDescriptor = null;
        try {
            parcelFileDescriptor = ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY);
            PdfRenderer pdfRenderer = new PdfRenderer(parcelFileDescriptor);
            if (pageNumber < 0 || pageNumber >= pdfRenderer.getPageCount()) {
                return null;
            }
            PdfRenderer.Page page = pdfRenderer.openPage(pageNumber);
            Bitmap bitmap = Bitmap.createBitmap(printWidth, printHeight, Bitmap.Config.ARGB_4444);
            page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY);
            page.close();

            return convertToGrayscale(bitmap);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } finally {
            if (parcelFileDescriptor != null) {
                try {
                    parcelFileDescriptor.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static Bitmap convertToGrayscale(Bitmap bitmap) {
        Bitmap grayscaleBitmap = Bitmap.createBitmap(bitmap.getWidth(),
                bitmap.getHeight(),
//                Bitmap.Config.RGBA_F16);
                Bitmap.Config.ARGB_4444);
        Canvas canvas = new Canvas(grayscaleBitmap);
        Paint paint = new Paint();
        ColorMatrix colorMatrix = new ColorMatrix();
        colorMatrix.setSaturation(0); // 设置饱和度为0，即转为灰度
        ColorMatrixColorFilter colorFilter = new ColorMatrixColorFilter(colorMatrix);
        paint.setColorFilter(colorFilter);
        canvas.drawBitmap(bitmap, 0, 0, paint);
        return grayscaleBitmap;
    }
}
