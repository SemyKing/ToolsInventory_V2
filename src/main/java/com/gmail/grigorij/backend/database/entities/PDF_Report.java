package com.gmail.grigorij.backend.database.entities;

import com.gmail.grigorij.utils.ProjectConstants;

import javax.persistence.Entity;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import java.util.Date;

/**
 * Alternative method to view PDF reports by saving them in database and retrieving via URL Route
 */

@Entity
@Table(name = "pdf_reports")
@NamedQueries({
		@NamedQuery(name= PDF_Report.QUERY_ALL,
				query="SELECT pdf FROM PDF_Report pdf"),

		@NamedQuery(name= PDF_Report.QUERY_BY_NAME,
				query="SELECT pdf FROM PDF_Report pdf WHERE pdf.name = :" + ProjectConstants.VAR1)
})
public class PDF_Report extends EntityPojo {

	public static final String QUERY_ALL = "get_all_pdf_reports";
	public static final String QUERY_BY_NAME = "get_pdf_report_by_name";


	private String name = "";

	private byte[] bytes;

	private Date date;


	public PDF_Report() {
		date = new Date();
		name = "pdf_report_" + System.currentTimeMillis();
	}


	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}

	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}

	public byte[] getBytes() {
		return bytes;
	}
	public void setBytes(byte[] bytes) {
		this.bytes = bytes;
	}
}
