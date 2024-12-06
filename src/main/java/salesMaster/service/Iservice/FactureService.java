package salesMaster.service.Iservice;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import salesMaster.dao.entities.Facture;
import salesMaster.dao.entities.LigneDeVente;
import salesMaster.dao.entities.Vente;
import salesMaster.dao.reposetories.IGestionFacture;
import salesMaster.dao.reposetories.IGestionVente;

@Service
public class FactureService {

    @Value("${facture.pdf.directory}")
    private String pdfDirectory;

    @Autowired
    private IGestionFacture factureRepository;

    @Autowired
    private IGestionVente venteRepository;

    public Facture generateFacture(Long venteId) {
        Vente vente = venteRepository.findById(venteId)
                .orElseThrow(() -> new IllegalArgumentException("Vente non trouvée pour ID: " + venteId));

        Facture facture = new Facture();
        facture.setDateFacturation(new Date());
        facture.setMontantTotal(vente.getTotal());
        facture.setVente(vente);

        facture = factureRepository.save(facture);

        try {
            String pdfPath = generatePdf(facture);
            facture.setPdf(pdfPath);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Erreur lors de la génération du PDF de la facture.");
        }

        return factureRepository.save(facture);
    }

    private String generatePdf(Facture facture) throws FileNotFoundException, DocumentException {
        File directory = new File(pdfDirectory);
        if (!directory.exists()) {
            directory.mkdirs();
        }

        String pdfPath = pdfDirectory + "/facture_" + facture.getFactureId() + ".pdf";

        Document document = new Document();
        try {
            PdfWriter.getInstance(document, new FileOutputStream(pdfPath));
            document.open();

            // Set the document margins and page size
            document.setMargins(36, 36, 36, 36);

            // Add Cover Page Content
            addCoverPage(document);

            // Add Company Header
            addCompanyHeader(document);

            // Add Invoice Title
            addInvoiceTitle(document);

            // Facture details
            addFactureDetails(document, facture);

            // Table for sale lines
            addSaleLinesTable(document, facture);

            // Add Footer
            addFooter(document);

            // Close document
            document.close();
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Erreur lors de l'écriture du fichier PDF.");
        }

        return pdfPath;
    }

    private void addCoverPage(Document document) throws DocumentException {
        Font coverFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 30, BaseColor.BLACK);
        Paragraph coverTitle = new Paragraph("INVOICE", coverFont);
        coverTitle.setAlignment(Paragraph.ALIGN_CENTER);
        document.add(coverTitle);

        document.add(new Paragraph(" "));

        Font coverSubtitleFont = FontFactory.getFont(FontFactory.HELVETICA, 12, BaseColor.GRAY);
        Paragraph coverSubtitle = new Paragraph("Thank you for your business!", coverSubtitleFont);
        coverSubtitle.setAlignment(Paragraph.ALIGN_CENTER);
        document.add(coverSubtitle);

        document.add(new Paragraph(" "));
        document.add(new Paragraph(" ")); // Add extra space before next section
    }

    private void addCompanyHeader(Document document) throws DocumentException {
        Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, BaseColor.BLACK);
        Paragraph companyInfo = new Paragraph("INFOMAKER\nInformatique: Logiciels, Progiciels\nAdresse: Rue Sebou, Immeuble 10. Appt 05 - Rabat\nPhone: 0675709229\nEmail: contact@klmsystem.ma", headerFont);
        companyInfo.setAlignment(Paragraph.ALIGN_LEFT);
        document.add(companyInfo);

        document.add(new Paragraph(" "));
        document.add(new Paragraph(" "));
    }

    private void addInvoiceTitle(Document document) throws DocumentException {
        Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 22, BaseColor.BLACK);
        Paragraph title = new Paragraph("Invoice Informations", titleFont);
        title.setAlignment(Paragraph.ALIGN_CENTER);
        document.add(title);

        document.add(new Paragraph(" "));
    }

    private void addFactureDetails(Document document, Facture facture) throws DocumentException {
        Font detailsFont = FontFactory.getFont(FontFactory.HELVETICA, 12, BaseColor.BLACK);
        Paragraph details = new Paragraph(String.format(
            "Facture ID: %d  |  Date: %s  |  Total: %.2f$  |  Client: %s",
            facture.getFactureId(),
            facture.getDateFacturation(),
            facture.getMontantTotal(),
            facture.getVente().getClient().getNom()
        ), detailsFont);
        details.setAlignment(Paragraph.ALIGN_LEFT);
        document.add(details);

        document.add(new Paragraph(" "));
    }

    private void addSaleLinesTable(Document document, Facture facture) throws DocumentException {
        PdfPTable table = new PdfPTable(4);
        table.setWidthPercentage(100);
        table.setSpacingBefore(10f);
        table.setSpacingAfter(10f);
        table.setWidths(new float[]{4f, 2f, 2f, 2f}); // Adjust column widths

        // Table header
        PdfPCell header = new PdfPCell(new Paragraph("Détails de la vente", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, BaseColor.WHITE)));
        header.setColspan(4);
        header.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
        header.setBackgroundColor(BaseColor.DARK_GRAY);
        table.addCell(header);

        // Column headers
        table.addCell(createHeaderCell("Produit"));
        table.addCell(createHeaderCell("Quantité"));
        table.addCell(createHeaderCell("Prix unitaire"));
        table.addCell(createHeaderCell("Total"));

        // Add sale lines to the table
        if (facture.getVente().getLignesDeVente() != null) {
            for (LigneDeVente ligne : facture.getVente().getLignesDeVente()) {
                table.addCell(ligne.getProduit().getNom());
                table.addCell(String.valueOf(ligne.getQuantite()));
                table.addCell(String.format("%.2f", ligne.getPrixUnitaire()));
                table.addCell(String.format("%.2f", ligne.getQuantite() * ligne.getPrixUnitaire()));
            }
        } else {
            PdfPCell noItems = new PdfPCell(new Paragraph("Aucune ligne de vente.", FontFactory.getFont(FontFactory.HELVETICA, 12, BaseColor.BLACK)));
            noItems.setColspan(4);
            noItems.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
            noItems.setBackgroundColor(BaseColor.LIGHT_GRAY);
            table.addCell(noItems);
        }

        document.add(table);
    }

    private PdfPCell createHeaderCell(String text) {
        Font font = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, BaseColor.WHITE);
        PdfPCell cell = new PdfPCell(new Paragraph(text, font));
        cell.setBackgroundColor(BaseColor.DARK_GRAY);
        cell.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
        return cell;
    }

    private void addFooter(Document document) throws DocumentException {
        Font footerFont = FontFactory.getFont(FontFactory.HELVETICA, 10, BaseColor.GRAY);
        Paragraph footer = new Paragraph("Thank you for your business. For inquiries, please contact us at contact@klmsystem.ma", footerFont);
        footer.setAlignment(Paragraph.ALIGN_CENTER);
        document.add(new Paragraph(" ")); // Adding space before footer
        document.add(footer);
    }
}
