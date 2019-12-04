package com.gmail.grigorij.backend.database.entities;

import com.gmail.grigorij.backend.database.entities.embeddable.PDF_Column;
import com.gmail.grigorij.backend.database.enums.WeekSelector;
import com.gmail.grigorij.backend.database.enums.tools.ToolParameter;
import com.gmail.grigorij.utils.ProjectConstants;

import javax.persistence.*;
import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.List;


@Entity
@Table(name = "pdf_templates")
@NamedQueries({
		@NamedQuery(name= PDF_Template.QUERY_ALL,
				query="SELECT pdf_template FROM PDF_Template pdf_template"),

		@NamedQuery(name= PDF_Template.QUERY_BY_COMPANY_ID,
				query="SELECT pdf_template FROM PDF_Template pdf_template WHERE pdf_template.company.id = :" + ProjectConstants.ID_VAR)
})
public class PDF_Template extends EntityPojo {

	public static final String QUERY_ALL = "get_all_pdf_templates";
	public static final String QUERY_BY_COMPANY_ID = "get_pdf_template_by_id";

	@OneToOne(mappedBy = "pdf_template")
	private Company company;

	@Embedded
	@ElementCollection
	private List<PDF_Column> pdfColumns = new ArrayList<>();

	@Column(columnDefinition = "text")
	private String normalText = "Sample Normal Text";

	@Column(columnDefinition = "text")
	private String contrastText = "Sample Contrast Text";

	private Float normalTextFontSize = 15f;
	private Float contrastTextFontSize = 14f;

	private boolean showDate = false;

	@Enumerated(EnumType.STRING)
	private WeekSelector weekSelector = WeekSelector.NEXT_WEEK;

	@Enumerated(EnumType.STRING)
	private DayOfWeek dayOfWeek = DayOfWeek.MONDAY;


	public PDF_Template() {
		pdfColumns.add(new PDF_Column(ToolParameter.NUMBERS));
		pdfColumns.add(new PDF_Column(ToolParameter.NAME));
		pdfColumns.add(new PDF_Column(ToolParameter.BARCODE));
	}

	public PDF_Template(PDF_Template other) {
		this.company = other.company;

		for (PDF_Column column : other.pdfColumns) {
			this.pdfColumns.add(new PDF_Column(column));
		}

		this.normalText = other.normalText;
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

	public String getNormalText() {
		return normalText;
	}
	public void setNormalText(String normalText) {
		this.normalText = normalText;
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
}
