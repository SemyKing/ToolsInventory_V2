package com.gmail.grigorij.utils.pdf;

import com.gmail.grigorij.backend.database.entities.PDF_Template;
import com.gmail.grigorij.backend.database.entities.Tool;
import com.gmail.grigorij.backend.database.entities.User;
import com.gmail.grigorij.backend.database.entities.embeddable.PDF_Column;
import com.gmail.grigorij.backend.database.enums.tools.ToolParameter;
import com.gmail.grigorij.backend.database.facades.InventoryFacade;
import com.gmail.grigorij.ui.utils.UIUtils;
import com.gmail.grigorij.utils.DateConverter;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.vaadin.flow.component.notification.NotificationVariant;
import org.apache.commons.io.output.ByteArrayOutputStream;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class PDF_Constructor {

	private Document document;
	private ByteArrayOutputStream out = new ByteArrayOutputStream();

	private Font normalFont;
	private Font boldFont;


	private PDF_Template pdfTemplate;
	private List<User> users;
	private HashMap<User, List<Tool>> toolsInUseByUser;

	private boolean error;


	public PDF_Constructor(PDF_Template pdfTemplate, List<User> users) {
		this.pdfTemplate = pdfTemplate;
		this.users = users;

		error = false;


		if (pdfTemplate.getPdfColumns().size() <= 0) {
			System.err.println("REPORTING COLUMNS NOT SET");
			UIUtils.showNotification("Columns for Reporting are not configured", NotificationVariant.LUMO_PRIMARY);
			error = true;
			return;
		}

		long totalWidth = 0;

		for (PDF_Column column : pdfTemplate.getPdfColumns()) {
			totalWidth += column.getUserSetWidth();
		}

		document = new Document(totalWidth > 10 ? PageSize.A4.rotate() : PageSize.A4,
				15f, 15f, 15f, 15f);

		try {
			normalFont = FontFactory.getFont(FontFactory.COURIER, pdfTemplate.getNormalTextFontSize(), BaseColor.BLACK);
		} catch (Exception e) {
			normalFont = FontFactory.getFont(FontFactory.COURIER, 14, BaseColor.BLACK);
		}

		try {
			boldFont = FontFactory.getFont(FontFactory.COURIER_BOLD, pdfTemplate.getContrastTextFontSize(), BaseColor.BLACK);
		} catch (Exception e) {
			boldFont = FontFactory.getFont(FontFactory.COURIER_BOLD, 13, BaseColor.BLACK);
		}



		try {
			PdfWriter.getInstance(document, out);
		} catch (DocumentException e) {
			System.err.println("PdfWriter.getInstance error");
			e.printStackTrace();
		}

		constructUserList();

		constructReportLayout();
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

	private void constructReportLayout() {
		document.open();

		boolean newPage = false;

		for (User user : toolsInUseByUser.keySet()) {

			if (newPage) {
				try {
					document.add(Chunk.NEXTPAGE);
				} catch (DocumentException e) {
					System.err.println("PDF new page add error");
					e.printStackTrace();
				}
			}

			PdfPTable documentHeaderTable = new PdfPTable(2);
			documentHeaderTable.setWidthPercentage(100);
			documentHeaderTable.setSpacingBefore(5f);
			documentHeaderTable.setSpacingAfter(5f);

			constructDocumentHeader(documentHeaderTable, user.getFullName());

			try {
				document.add(documentHeaderTable);
			} catch (DocumentException e) {
				System.err.println("PDF add document header table error");
				e.printStackTrace();
			}


			PdfPTable toolsTable = new PdfPTable(pdfTemplate.getPdfColumns().size());
			toolsTable.setWidthPercentage(100);
			toolsTable.setSpacingBefore(15f);
			toolsTable.setSpacingAfter(15f);

			constructTableHeader(toolsTable);

			try {
				toolsTable.setWidths(constructColumnWidths());
			} catch (DocumentException e) {
				System.err.println("toolsTable set columns widths error");
				e.printStackTrace();
			}

			constructTableData(toolsTable, user);

			try {
				document.add(toolsTable);
			} catch (DocumentException e) {
				System.err.println("PDF add tools table error");
				e.printStackTrace();
			}


			PdfPTable signatureTable = new PdfPTable(1);
			signatureTable.setWidthPercentage(100);
			signatureTable.setSpacingBefore(15f);
			signatureTable.setSpacingAfter(15f);

			constructSignatureBox(signatureTable);

			try {
				document.add(signatureTable);
			} catch (DocumentException e) {
				System.err.println("pdf add signature table error");
				e.printStackTrace();
			}

			newPage = true;
		}

		try {
			document.close();
		} catch (Exception e) {
			System.err.println("user has no tools, document close exception");
			error = true;
		}
	}

	private void constructDocumentHeader(PdfPTable documentHeaderTable, String userFullName) {
		PdfPCell nameCell = new PdfPCell();
		nameCell.setBorderWidth(0);
		nameCell.setPhrase(new Phrase(userFullName, boldFont));
		documentHeaderTable.addCell(nameCell);

		PdfPCell dateCell = new PdfPCell();
		dateCell.setBorderWidth(0);
		dateCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
		dateCell.setPhrase(new Phrase(DateConverter.localDateToString(LocalDate.now()), boldFont));
		documentHeaderTable.addCell(dateCell);
	}

	private void constructTableHeader(PdfPTable toolsTable) {
		for (PDF_Column column : pdfTemplate.getPdfColumns()) {
			PdfPCell header = new PdfPCell();
			header.setBackgroundColor(BaseColor.LIGHT_GRAY);
			header.setBorderWidth(1f);
			header.setPhrase(new Phrase(column.getParameter().getName()));
//			header.setVerticalAlignment(Element.ALIGN_CENTER);
			toolsTable.addCell(header);
		}
	}

	private float[] constructColumnWidths() {
		float[] columnWidths = new float[pdfTemplate.getPdfColumns().size()];

		for (int i = 0; i < pdfTemplate.getPdfColumns().size(); i++) {

			float width;

			if (pdfTemplate.getPdfColumns().get(i).getUserSetWidth() > 0) {
				width = pdfTemplate.getPdfColumns().get(i).getUserSetWidth();
			} else {
				width = pdfTemplate.getPdfColumns().get(i).getParameter().getPrefWidth();
			}

			columnWidths[i] = width;
		}

		return columnWidths;
	}

	private void constructTableData(PdfPTable toolsTable, User user) {
		int toolCounter = 1;

		//CHECK THAT ALL CONFIGURED PARAMETERS ARE PRESENT
		int parameterCounter;


		for (Tool tool : toolsInUseByUser.get(user)) {
			parameterCounter = 0;

			for (PDF_Column column : pdfTemplate.getPdfColumns()) {
				// NUMBER OF TOOL
				if (column.getParameter().equals(ToolParameter.NUMBERS)) {
					toolsTable.addCell(constructCell(new Phrase(String.valueOf(toolCounter), normalFont)));
					parameterCounter++;
				}
				// TOOL NAME
				if (column.getParameter().equals(ToolParameter.NAME)) {
					toolsTable.addCell(constructCell(new Phrase(tool.getName(), normalFont)));
					parameterCounter++;
				}
				// TOOL BARCODE
				if (column.getParameter().equals(ToolParameter.BARCODE)) {
					toolsTable.addCell(constructCell(new Phrase(tool.getBarcode(), normalFont)));
					parameterCounter++;
				}
				// TOOL RF_CODE
				if (column.getParameter().equals(ToolParameter.RF_CODE)) {
					toolsTable.addCell(constructCell(new Phrase(tool.getRF_Code(), normalFont)));
					parameterCounter++;
				}
				// TOOL SERIAL_NUMBER
				if (column.getParameter().equals(ToolParameter.SERIAL_NUMBER)) {
					toolsTable.addCell(constructCell(new Phrase(tool.getSerialNumber(), normalFont)));
					parameterCounter++;
				}
				// TOOL_INFO
				if (column.getParameter().equals(ToolParameter.TOOL_INFO)) {
					toolsTable.addCell(constructCell(new Phrase(tool.getToolInfo(), normalFont)));
					parameterCounter++;
				}
				// TOOL MANUFACTURER
				if (column.getParameter().equals(ToolParameter.MANUFACTURER)) {
					toolsTable.addCell(constructCell(new Phrase(tool.getManufacturer(), normalFont)));
					parameterCounter++;
				}
				// TOOL MODEL
				if (column.getParameter().equals(ToolParameter.MODEL)) {
					toolsTable.addCell(constructCell(new Phrase(tool.getModel(), normalFont)));
					parameterCounter++;
				}
				// TOOL MODEL
				if (column.getParameter().equals(ToolParameter.CURRENT_LOCATION)) {
					toolsTable.addCell(constructCell(new Phrase(tool.getCurrentLocationString(), normalFont)));
					parameterCounter++;
				}
				// USAGE_STATUS
				if (column.getParameter().equals(ToolParameter.USAGE_STATUS)) {
					toolsTable.addCell(constructCell(new Phrase(tool.getUsageStatusString(), normalFont)));
					parameterCounter++;
				}
				// CURRENT_USER
				if (column.getParameter().equals(ToolParameter.CURRENT_USER)) {
					toolsTable.addCell(constructCell(new Phrase(tool.getCurrentUserString(), normalFont)));
					parameterCounter++;
				}
				// RESERVED_USER
				if (column.getParameter().equals(ToolParameter.RESERVED_USER)) {
					toolsTable.addCell(constructCell(new Phrase(tool.getReservedUserString(), normalFont)));
					parameterCounter++;
				}
				// DATE_BOUGHT
				if (column.getParameter().equals(ToolParameter.DATE_BOUGHT)) {
					toolsTable.addCell(constructCell(new Phrase(DateConverter.localDateToString(tool.getDateBought()), normalFont)));
					parameterCounter++;
				}
				// DATE_NEXT_MAINTENANCE
				if (column.getParameter().equals(ToolParameter.DATE_NEXT_MAINTENANCE)) {
					toolsTable.addCell(constructCell(new Phrase(DateConverter.localDateToString(tool.getDateNextMaintenance()), normalFont)));
					parameterCounter++;
				}
				// PRICE
				if (column.getParameter().equals(ToolParameter.PRICE)) {
					toolsTable.addCell(constructCell(new Phrase(String.valueOf(tool.getPrice()), normalFont)));
					parameterCounter++;
				}
				// GUARANTEE
				if (column.getParameter().equals(ToolParameter.GUARANTEE)) {
					toolsTable.addCell(constructCell(new Phrase(String.valueOf(tool.getGuarantee_months()), normalFont)));
					parameterCounter++;
				}
			}

			if (parameterCounter != pdfTemplate.getPdfColumns().size()) {
				System.err.println("SOME OF THE REPORTING TOOL PARAMETERS NOT ADDED TO REPORT TABLE");
				System.err.println("COLUMNS:    " + pdfTemplate.getPdfColumns().size());
				System.err.println("PARAMETERS: " + parameterCounter);
			}

			toolCounter++;
		}
	}

	private PdfPCell constructCell(Phrase phrase, int... alignment) {
		PdfPCell cell = new PdfPCell(phrase);

		int align = Element.ALIGN_MIDDLE;
		if (alignment.length > 0) {
			align = alignment[0];
		}

		cell.setVerticalAlignment(align);
		return cell;
	}


	//TODO: TRANSLATE
	private void constructSignatureBox(PdfPTable signatureTable) {
		Phrase phrase = new Phrase();
		phrase.add(new Paragraph(pdfTemplate.getSignatureText(), normalFont));
		signatureTable.addCell(constructCell(phrase));

		phrase = new Phrase();
		phrase.add(Chunk.NEWLINE);
		phrase.add(new Paragraph("Place & Date:", normalFont));
		phrase.add(Chunk.NEWLINE);
		phrase.add(Chunk.NEWLINE);
		signatureTable.addCell(constructCell(phrase));

		phrase = new Phrase();
		phrase.add(Chunk.NEWLINE);
		phrase.add(new Paragraph("Signature:", normalFont));
		phrase.add(Chunk.NEWLINE);
		phrase.add(Chunk.NEWLINE);
		signatureTable.addCell(constructCell(phrase));

		phrase = new Phrase();
//		phrase.add(Chunk.NEWLINE);
		phrase.add(new Paragraph(constructReturnText(), boldFont));
//		phrase.add(Chunk.NEWLINE);
		signatureTable.addCell(constructCell(phrase));
	}

	private String constructReturnText() {
		String returnText = pdfTemplate.getContrastText();

		if (pdfTemplate.isShowDate()) {
			LocalDate localDate = LocalDate.now();
			DayOfWeek dayOfWeek = pdfTemplate.getDayOfWeek();

			switch (pdfTemplate.getWeekSelector()) {
				case THIS_WEEK:
					localDate = LocalDate.now().with(TemporalAdjusters.nextOrSame(dayOfWeek));
					break;
				case NEXT_WEEK:
					localDate = LocalDate.now().with(TemporalAdjusters.next(dayOfWeek));
					break;
				case AFTER_TWO_WEEKS:
					localDate = LocalDate.now().with(TemporalAdjusters.next(dayOfWeek));
					localDate = localDate.with(TemporalAdjusters.next(dayOfWeek));
					break;
				case AFTER_THREE_WEEKS:
					localDate = LocalDate.now().with(TemporalAdjusters.next(dayOfWeek));
					localDate = localDate.with(TemporalAdjusters.next(dayOfWeek));
					localDate = localDate.with(TemporalAdjusters.next(dayOfWeek));
					break;
				case AFTER_FOUR_WEEKS:
					localDate = LocalDate.now().with(TemporalAdjusters.next(dayOfWeek));
					localDate = localDate.with(TemporalAdjusters.next(dayOfWeek));
					localDate = localDate.with(TemporalAdjusters.next(dayOfWeek));
					localDate = localDate.with(TemporalAdjusters.next(dayOfWeek));
					break;
			}

			returnText += " " + DateConverter.localDateToString(localDate);
		}

		return returnText;
	}


	public byte[] getPDF_ByteArray() {
		return out.toByteArray();
	}

	public boolean hasErrors() {
		return error;
	}
}
