package com.example.pdfgenerate.service;

import com.example.pdfgenerate.Model.PdfRequest;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.element.Cell;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.file.Files;

@Service
public class PdfService {

    private static final String PDF_DIRECTORY = "pdf-storage/";

    public String generatePdf(PdfRequest request) throws Exception {
        // Create storage directory if not exists
        File directory = new File(PDF_DIRECTORY);
        if (!directory.exists()) directory.mkdir();

        String pdfFileName = PDF_DIRECTORY + request.getSeller() + "_" + request.getBuyer() + ".pdf";
        File pdfFile = new File(pdfFileName);

        // If PDF already exists, return the file path without regenerating
        if (pdfFile.exists()) return pdfFile.getAbsolutePath();

        try (PdfWriter writer = new PdfWriter(new FileOutputStream(pdfFile));
                PdfDocument pdfDocument = new PdfDocument(writer);
                Document document = new Document(pdfDocument)){

            // Add seller and buyer information
            document.add(new Paragraph("Invoice")
                    .setBold().setFontSize(18));
            document.add(new Paragraph("Seller: " + request.getSeller()));
            document.add(new Paragraph("Seller GSTIN: " + request.getSellerGstin()));
            document.add(new Paragraph("Seller Address: " + request.getSellerAddress()));
            document.add(new Paragraph("Buyer: " + request.getBuyer()));
            document.add(new Paragraph("Buyer GSTIN: " + request.getBuyerGstin()));
            document.add(new Paragraph("Buyer Address: " + request.getBuyerAddress()));
            document.add(new Paragraph("\n"));

            Table table = new Table(4);
            table.addCell(new Cell().add(new Paragraph("Item Name")));
            table.addCell(new Cell().add(new Paragraph("Quantity")));
            table.addCell(new Cell().add(new Paragraph("Rate")));
            table.addCell(new Cell().add(new Paragraph("Amount")));

            for (PdfRequest.Item item : request.getItems()) {
                table.addCell(new Cell().add(new Paragraph(item.getName())));
                
                // Assuming quantity is a String, convert it appropriately
                table.addCell(new Cell().add(new Paragraph(item.getQuantity())));
                
                // Add rate and amount as Paragraph
                table.addCell(new Cell().add(new Paragraph(String.valueOf(item.getRate()))));
                table.addCell(new Cell().add(new Paragraph(String.valueOf(item.getAmount()))));
            }

            document.add(table);
        }
        return pdfFile.getAbsolutePath();
    }

    public byte[] getPdf(String fileName) throws Exception {
        return Files.readAllBytes(new File(PDF_DIRECTORY + fileName).toPath());
    }
}