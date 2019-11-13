//package com.gmail.grigorij.backend.database.entities.embeddable;
//
//import com.gmail.grigorij.backend.database.enums.WeekSelector;
//import com.gmail.grigorij.backend.database.enums.tools.ToolParameter;
//
//import javax.persistence.*;
//import java.time.DayOfWeek;
//import java.util.ArrayList;
//import java.util.List;
//
//
//@Embeddable
//public class PDF_ReportingTemplate {
//
//	@Embedded
//	@ElementCollection
//	private List<ReportingColumn> reportingColumns;
//
//	@Column(columnDefinition = "text")
//	private String signatureText = "";
//
//	private boolean showDate = true;
//
//	@Column(columnDefinition = "text")
//	private String contrastText = "";
//
//	@Enumerated(EnumType.STRING)
//	private WeekSelector weekSelector = WeekSelector.NEXT_WEEK;
//
//	@Enumerated(EnumType.STRING)
//	private DayOfWeek dayOfWeek = DayOfWeek.MONDAY;
//
//
//	public PDF_ReportingTemplate() {
//		reportingColumns = new ArrayList<>();
//		reportingColumns.add(new ReportingColumn(ToolParameter.NUMBERS));
//		reportingColumns.add(new ReportingColumn(ToolParameter.NAME));
//		reportingColumns.add(new ReportingColumn(ToolParameter.BARCODE));
//	}
//
//	public PDF_ReportingTemplate(PDF_ReportingTemplate other) {
//		this.reportingColumns = other.reportingColumns;
//		this.signatureText = other.signatureText;
//		this.contrastText = other.contrastText;
//		this.showDate = other.showDate;
//		this.weekSelector = other.weekSelector;
//		this.dayOfWeek = other.dayOfWeek;
//	}
//
//
//	public List<ReportingColumn> getReportingColumns() {
//		return reportingColumns;
//	}
//	public void setReportingColumns(List<ReportingColumn> reportingColumns) {
//		this.reportingColumns = reportingColumns;
//	}
//
//	public String getSignatureText() {
//		return signatureText;
//	}
//	public void setSignatureText(String signatureText) {
//		this.signatureText = signatureText;
//	}
//
//	public String getContrastText() {
//		return contrastText;
//	}
//	public void setContrastText(String contrastText) {
//		this.contrastText = contrastText;
//	}
//
//	public WeekSelector getWeekSelector() {
//		return weekSelector;
//	}
//	public void setWeekSelector(WeekSelector weekSelector) {
//		this.weekSelector = weekSelector;
//	}
//
//	public DayOfWeek getDayOfWeek() {
//		return dayOfWeek;
//	}
//	public void setDayOfWeek(DayOfWeek dayOfWeek) {
//		this.dayOfWeek = dayOfWeek;
//	}
//
//	public boolean isShowDate() {
//		return showDate;
//	}
//	public void setShowDate(boolean showDate) {
//		this.showDate = showDate;
//	}
//
//
//
//	@Embeddable
//	public static class ReportingColumn {
//
//		@Enumerated(EnumType.STRING)
//		private ToolParameter parameter;
//
//		private Float userSetWidth;
//
//
//		public ReportingColumn() {}
//
//		public ReportingColumn(ToolParameter parameter) {
//			this.parameter = parameter;
//			this.userSetWidth = parameter.getPrefWidth();
//		}
//
//
//		public ToolParameter getParameter() {
//			return parameter;
//		}
//		public void setParameter(ToolParameter parameter) {
//			this.parameter = parameter;
//		}
//
//		public Float getUserSetWidth() {
//			return userSetWidth;
//		}
//		public void setUserSetWidth(Float userSetWidth) {
//			this.userSetWidth = userSetWidth;
//		}
//	}
//}
