package com.epharmacy.services;

import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.*;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.epharmacy.models.Order;
import com.epharmacy.models.OrderItem;

import java.io.File;
import java.time.format.DateTimeFormatter;

/**
 * PDF Invoice generator using iText 7.
 * Uses the Decorator pattern's final total as the displayed amount.
 */
public class PDFService {

    private static final DeviceRgb ACCENT = new DeviceRgb(0, 120, 215);

    /**
     * Generates a PDF invoice for the given order.
     * @param order     the order (with items loaded)
     * @param outputPath  absolute file path for the PDF
     */
    public void generateInvoice(Order order, String outputPath) throws Exception {
        File dir = new File(outputPath).getParentFile();
        if (dir != null && !dir.exists()) dir.mkdirs();

        PdfWriter   writer = new PdfWriter(outputPath);
        PdfDocument pdf    = new PdfDocument(writer);
        Document    doc    = new Document(pdf);

        // ── Header ──────────────────────────────────────────────────
        Paragraph header = new Paragraph("Smart E-Pharmacy System")
                .setFontSize(22).setBold().setFontColor(ACCENT)
                .setTextAlignment(TextAlignment.CENTER);
        doc.add(header);

        doc.add(new Paragraph("TAX INVOICE")
                .setFontSize(14).setBold().setTextAlignment(TextAlignment.CENTER)
                .setFontColor(ColorConstants.DARK_GRAY));

        doc.add(new Paragraph(" "));

        // ── Order Info ───────────────────────────────────────────────
        doc.add(new Paragraph("Order #" + order.getId())
                .setFontSize(12).setBold());
        doc.add(new Paragraph("Patient: " + (order.getPatientName() != null ? order.getPatientName() : "N/A")));
        doc.add(new Paragraph("Payment: " + order.getPaymentMethod()));
        if (order.getCreatedAt() != null) {
            doc.add(new Paragraph("Date: " + order.getCreatedAt()
                    .format(DateTimeFormatter.ofPattern("dd MMM yyyy HH:mm"))));
        }
        doc.add(new Paragraph("Status: " + order.getStatus()).setBold());
        doc.add(new Paragraph(" "));

        // ── Items Table ──────────────────────────────────────────────
        Table table = new Table(UnitValue.createPercentArray(new float[]{3, 1, 1.5f, 1.5f}))
                .useAllAvailableWidth();

        // Header row
        String[] headers = {"Medicine", "Qty", "Unit Price", "Subtotal"};
        for (String h : headers) {
            Cell cell = new Cell().add(new Paragraph(h).setBold())
                    .setBackgroundColor(ACCENT)
                    .setFontColor(ColorConstants.WHITE)
                    .setTextAlignment(TextAlignment.CENTER);
            table.addHeaderCell(cell);
        }

        // Data rows
        for (OrderItem item : order.getItems()) {
            table.addCell(item.getMedicineName());
            table.addCell(new Cell().add(new Paragraph(String.valueOf(item.getQuantity())))
                    .setTextAlignment(TextAlignment.CENTER));
            table.addCell(new Cell().add(new Paragraph(String.format("$%.2f", item.getUnitPrice())))
                    .setTextAlignment(TextAlignment.RIGHT));
            table.addCell(new Cell().add(new Paragraph(String.format("$%.2f", item.getSubtotal())))
                    .setTextAlignment(TextAlignment.RIGHT));
        }

        doc.add(table);
        doc.add(new Paragraph(" "));

        // ── Total ────────────────────────────────────────────────────
        doc.add(new Paragraph(String.format("TOTAL: $%.2f", order.getTotalAmount()))
                .setBold().setFontSize(14).setFontColor(ACCENT)
                .setTextAlignment(TextAlignment.RIGHT));

        doc.add(new Paragraph("\n\nThank you for using Smart E-Pharmacy!")
                .setItalic().setTextAlignment(TextAlignment.CENTER)
                .setFontColor(ColorConstants.GRAY));

        doc.close();
        System.out.println("[PDF] Invoice saved: " + outputPath);
    }
}
