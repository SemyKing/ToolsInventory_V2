package com.gmail.grigorij.backend.database.entities;

import com.gmail.grigorij.backend.database.enums.WeekSelector;
import com.gmail.grigorij.backend.database.enums.tools.ToolParameter;
import com.gmail.grigorij.utils.ProjectConstants;

import javax.persistence.*;
import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.List;


@Entity
@Table(name = "pdf_report_templates")
@NamedQueries({
		@NamedQuery(name= PDF_Template.QUERY_ALL,
				query="SELECT pdf_template FROM PDF_Template pdf_template"),

		@NamedQuery(name= PDF_Template.QUERY_BY_COMPANY_ID,
				query="SELECT pdf_template FROM PDF_Template pdf_template WHERE pdf_template.company.id = :" + ProjectConstants.ID_VAR)
})
public class PDF_Template extends EntityPojo {

	public static final String QUERY_ALL = "get_all_pdf_report_templates";
	public static final String QUERY_BY_COMPANY_ID = "get_pdf_report_template_by_id";

	@OneToOne
	private Company company;

	@Embedded
	@ElementCollection
	private List<PDF_Column> pdfColumns;

	@Column(columnDefinition = "text")
	private String signatureText = "Sample Normal Text";

	private boolean showDate = false;

	@Column(columnDefinition = "text")
	private String contrastText = "Sample Contrast Text";

	private Float normalTextFontSize = 15f;
	private Float contrastTextFontSize = 14f;

	@Enumerated(EnumType.STRING)
	private WeekSelector weekSelector = WeekSelector.NEXT_WEEK;

	@Enumerated(EnumType.STRING)
	private DayOfWeek dayOfWeek = DayOfWeek.MONDAY;


	public PDF_Template() {
		pdfColumns = new ArrayList<>();
		pdfColumns.add(new PDF_Column(ToolParameter.NUMBERS));
		pdfColumns.add(new PDF_Column(ToolParameter.NAME));
		pdfColumns.add(new PDF_Column(ToolParameter.BARCODE));
	}

	public PDF_Template(PDF_Template other) {
		this.company = other.company;
		this.pdfColumns = other.pdfColumns;
		this.signatureText = other.signatureText;
		this.contrastText = other.contrastText;
		this.normalTextFontSize = other.normalTextFontSize;
		this.contrastTextFontSize = other.contrastTextFontSize;
		this.showDate = other.showDate;
		this.weekSelector = other.weekSelector;
		this.dayOfWeek = other.dayOfWeek;
	}


	public Company getCompany() {
		return company;
	}
	public void setCompany(Company company) {
		this.company = company;
	}

	public List<PDF_Column> getPdfColumns() {
		return pdfColumns;
	}
	public void setPdfColumns(List<PDF_Column> pdfColumns) {
		this.pdfColumns = pdfColumns;
	}

	public String getSignatureText() {
		return signatureText;
	}
	public void setSignatureText(String signatureText) {
		this.signatureText = signatureText;
	}

	public String getContrastText() {
		return contrastText;
	}
	public void setContrastText(String contrastText) {
		this.contrastText = contrastText;
	}

	public Float getNormalTextFontSize() {
		return normalTextFontSize;
	}
	public void setNormalTextFontSize(Float normalTextFontSize) {
		this.normalTextFontSize = normalTextFontSize;
	}

	public Float getContrastTextFontSize() {
		return contrastTextFontSize;
	}
	public void setContrastTextFontSize(Float contrastTextFontSize) {
		this.contrastTextFontSize = contrastTextFontSize;
	}

	public boolean isShowDate() {
		return showDate;
	}
	public void setShowDate(boolean showDate) {
		this.showDate = showDate;
	}

	public WeekSelector getWeekSelector() {
		return weekSelector;
	}
	public void setWeekSelector(WeekSelector weekSelector) {
		this.weekSelector = weekSelector;
	}

	public DayOfWeek getDayOfWeek() {
		return dayOfWeek;
	}
	public void setDayOfWeek(DayOfWeek dayOfWeek) {
		this.dayOfWeek = dayOfWeek;
	}



	@Embeddable
	public static class PDF_Column {

		@Enumerated(EnumType.STRING)
		private ToolParameter parameter;

		private Float userSetWidth;


		public PDF_Column() {}

		PDF_Column(ToolParameter parameter) {
			this.parameter = parameter;
			this.userSetWidth = parameter.getPrefWidth();
		}

		public PDF_Column(PDF_Column other) {
			this.parameter = other.parameter;
			this.userSetWidth = other.userSetWidth;
		}

		public ToolParameter getParameter() {
			return parameter;
		}
		public void setParameter(ToolParameter parameter) {
			this.parameter = parameter;
		}

		public Float getUserSetWidth() {
			return userSetWidth;
		}
		public void setUserSetWidth(Float userSetWidth) {
			this.userSetWidth = userSetWidth;
		}
	}
}
