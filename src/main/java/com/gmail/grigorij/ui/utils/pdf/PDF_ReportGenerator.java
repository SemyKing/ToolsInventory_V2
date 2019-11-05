package com.gmail.grigorij.ui.utils.pdf;

import com.gmail.grigorij.backend.database.entities.Tool;
import com.gmail.grigorij.backend.database.entities.User;
import com.gmail.grigorij.backend.database.facades.InventoryFacade;
import com.gmail.grigorij.ui.utils.UIUtils;
import com.gmail.grigorij.utils.DateConverter;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Stream;

public class PDF_ReportGenerator {

	private final float TABLE_SPACING = 15f;
	private final float DOCUMENT_MARGIN = 20f;

	private final float NO_COLUMN_WIDTH = 0.5f;
	private final float TOOL_NAME_COLUMN_WIDTH = 4f;
	private final float STATUS_COLUMN_WIDTH = 1f;
	private final float BARCODE_COLUMN_WIDTH = 3f;

	private final Font normalFont;
	private final Font boldFont;

	private File PDF_File;

	private Document document;

	private List<User> users;
	private HashMap<User, List<Tool>> toolsInUseByUser;


	public PDF_ReportGenerator(List<User> users) {
		this.users = users;

		document = new Document(PageSize.A4, DOCUMENT_MARGIN, DOCUMENT_MARGIN, DOCUMENT_MARGIN, DOCUMENT_MARGIN);
		normalFont = FontFactory.getFont(FontFactory.COURIER, 14, BaseColor.BLACK);
		boldFont = FontFactory.getFont(FontFactory.COURIER_BOLD, 13, BaseColor.BLACK);

		try {
			PDF_File = File.createTempFile("temp_report_" + System.currentTimeMillis(), ".pdf");
		} catch (IOException e) {
			System.err.println("temp PDF file creation error");
			e.printStackTrace();
		}

		if (PDF_File == null) {
			System.err.println("temp PDF file is null");
			UIUtils.showNotification("Could not create report", UIUtils.NotificationType.ERROR);
			return;
		}

		try {
			PdfWriter.getInstance(document, new FileOutputStream(PDF_File));
		} catch (FileNotFoundException | DocumentException e) {
			System.err.println("PdfWriter.getInstance error");
			e.printStackTrace();
		}

		constructUserList();

		constructPDF_Report();
	}


	private void constructUserList() {
		toolsInUseByUser = new HashMap<>();

		for (User user : users) {
			List<Tool> toolsInUse = InventoryFacade.getInstance().getAllToolsByCurrentUserId(user.getId());

			if (toolsInUse.size() > 0) {
				toolsInUseByUser.put(user, new ArrayList<>(toolsInUse));
			}
		}
	}

	private void constructPDF_Report() {
		document.open();

		long toolCounter;
		boolean newPage = false;

		for (User user : toolsInUseByUser.keySet()) {
			toolCounter = 1;

			if (newPage) {
				try {
					document.add(Chunk.NEXTPAGE);
				} catch (DocumentException e) {
					System.err.println("pdf new page add error");
					e.printStackTrace();
				}
			}

			try {
				document.add(new Paragraph(user.getFullName(), boldFont));
			} catch (DocumentException e) {
				System.err.println("pdf paragraph1 add error");
				e.printStackTrace();
			}

			PdfPTable toolsTable = new PdfPTable(4);
			toolsTable.setWidthPercentage(100);
			toolsTable.setSpacingBefore(TABLE_SPACING);
			toolsTable.setSpacingAfter(TABLE_SPACING);
			addTableHeader(toolsTable);

			float[] columnWidths = {NO_COLUMN_WIDTH, TOOL_NAME_COLUMN_WIDTH, STATUS_COLUMN_WIDTH, BARCODE_COLUMN_WIDTH};

			try {
				toolsTable.setWidths(columnWidths);
			} catch (DocumentException e) {
				System.err.println("toolsTable set columns widths error");
				e.printStackTrace();
			}


			for (Tool tool : toolsInUseByUser.get(user)) {
				PdfPCell cell1 = new PdfPCell(new Paragraph(String.valueOf(toolCounter)));
				cell1.setVerticalAlignment(Element.ALIGN_CENTER);
				toolsTable.addCell(cell1);

				PdfPCell cell2 = new PdfPCell(new Paragraph(tool.getName()));
				cell2.setVerticalAlignment(Element.ALIGN_CENTER);
				toolsTable.addCell(cell2);

				PdfPCell cell3 = new PdfPCell(new Paragraph(tool.getUsageStatusString()));
				cell3.setVerticalAlignment(Element.ALIGN_CENTER);
				toolsTable.addCell(cell3);

				PdfPCell cell4 = new PdfPCell(new Paragraph(tool.getBarcode()));
				cell4.setVerticalAlignment(Element.ALIGN_CENTER);
				toolsTable.addCell(cell4);

				toolCounter++;
			}

			try {
				document.add(toolsTable);
			} catch (DocumentException e) {
				System.err.println("pdf add tools table error");
				e.printStackTrace();
			}

			try {
				document.add(new Paragraph(DateConverter.localDateToString(LocalDate.now()), boldFont));
			} catch (DocumentException e) {
				System.err.println("pdf paragraph2 add error");
				e.printStackTrace();
			}

			PdfPTable signatureTable = new PdfPTable(1);
			signatureTable.setWidthPercentage(100);
			signatureTable.setSpacingBefore(TABLE_SPACING);
			signatureTable.setSpacingAfter(TABLE_SPACING);
			constructSignatureBox(signatureTable);

			try {
				document.add(signatureTable);
			} catch (DocumentException e) {
				System.err.println("pdf add signature table error");
				e.printStackTrace();
			}

			newPage = true;
		}

		document.close();
	}

	private void addTableHeader(PdfPTable table) {
		Stream.of("No.", "Tool", "Status", "Barcode")
				.forEach(columnTitle -> {
					PdfPCell header = new PdfPCell();
					header.setBackgroundColor(BaseColor.LIGHT_GRAY);
					header.setBorderWidth(1);
					header.setPhrase(new Phrase(columnTitle));
					header.setVerticalAlignment(Element.ALIGN_CENTER);
					table.addCell(header);
				});
	}

	//TODO: DYNAMIC FOR EACH COMPANY
	private void constructSignatureBox(PdfPTable signatureTable) {
		Phrase phrase = new Phrase();
		phrase.add(new Paragraph("Allekirjoituksellani vakuutan, että luetellut työkalut ovat tällä hetkellä hallussani. " +
				"Työkalut, jotka eivät ole hallussani, on vedetty ylitse. " +
				"Jos tästä listauksesta puuttuu työkalujani, ne on lueteltu tämän lomakkeen kääntöpuolella (työkalun nimi ja viivakoodi, " +
				"jos se on saatavilla).", normalFont));
		phrase.add(Chunk.NEWLINE);
		phrase.add(Chunk.NEWLINE);
		phrase.add(Chunk.NEWLINE);
		phrase.add(new Paragraph("Paikka ja päiväys:______________________________", normalFont));
		phrase.add(Chunk.NEWLINE);
		phrase.add(Chunk.NEWLINE);
		phrase.add(new Paragraph("Allekirjoitus:__________________________________", normalFont));
		phrase.add(Chunk.NEWLINE);
		phrase.add(Chunk.NEWLINE);
		phrase.add(new Paragraph("Palauta listaus työnjohtajalle Ma " + DateConverter.localDateToString(LocalDate.now().with(TemporalAdjusters.next(DayOfWeek.MONDAY))) +
				" klo 10:00 mennessä", boldFont));
		phrase.add(Chunk.NEWLINE);
		phrase.add(Chunk.NEWLINE);

		signatureTable.addCell(phrase);
	}


	public File getPDF_File() {
		return PDF_File;
	}

	public byte[] getPDFByteArray() {
		try {
			return Files.readAllBytes(PDF_File.toPath());
		} catch (IOException e) {
			e.printStackTrace();
		}
		return new byte[0];
	}
}
