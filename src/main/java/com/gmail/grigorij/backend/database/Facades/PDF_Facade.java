package com.gmail.grigorij.backend.database.facades;

import com.gmail.grigorij.backend.database.DatabaseManager;
import com.gmail.grigorij.backend.database.entities.PDF_Report;
import com.gmail.grigorij.backend.database.entities.PDF_Template;
import com.gmail.grigorij.utils.ProjectConstants;

import javax.persistence.NoResultException;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;


public class PDF_Facade {

	private static PDF_Facade mInstance;
	private PDF_Facade() {}
	public static PDF_Facade getInstance() {
		if (mInstance == null) {
			mInstance = new PDF_Facade();
		}
		return mInstance;
	}

	private List<PDF_Report> getAllPDF_Reports() {
		List<PDF_Report> pdf_entities;
		try {
			pdf_entities = DatabaseManager.getInstance().createEntityManager().createNamedQuery(PDF_Report.QUERY_ALL, PDF_Report.class)
					.getResultList();
		} catch (NoResultException nre) {
			pdf_entities = null;
		}
		return pdf_entities;
	}

	public PDF_Report getPDF_ReportByName(String name) {
		PDF_Report pdf_entity;
		try {
			pdf_entity = DatabaseManager.getInstance().createEntityManager().createNamedQuery(PDF_Report.QUERY_BY_NAME, PDF_Report.class)
					.setParameter(ProjectConstants.VAR1, name)
					.getSingleResult();
		} catch (NoResultException nre) {
			pdf_entity = null;
		}
		return pdf_entity;
	}



	private List<PDF_Template> getAllPDF_Templates() {
		List<PDF_Template> pdf_reportTemplates;
		try {
			pdf_reportTemplates = DatabaseManager.getInstance().createEntityManager().createNamedQuery(PDF_Template.QUERY_ALL, PDF_Template.class)
					.getResultList();
		} catch (NoResultException nre) {
			pdf_reportTemplates = null;
		}
		return pdf_reportTemplates;
	}

	public PDF_Template getPDF_TemplateByCompany(long companyId) {
		PDF_Template pdfTemplate;
		try {
			pdfTemplate = DatabaseManager.getInstance().createEntityManager().createNamedQuery(PDF_Template.QUERY_BY_COMPANY_ID, PDF_Template.class)
					.setParameter(ProjectConstants.ID_VAR, companyId)
					.getSingleResult();
		} catch (NoResultException nre) {
			pdfTemplate = null;
		}
		return pdfTemplate;
	}


	private void deleteOldPDFs() {
		List<PDF_Report> pdf_entities = getAllPDF_Reports();

		for (PDF_Report pdf_entity : pdf_entities) {
			if (getDateDifference(pdf_entity.getDate()) > 1) {
				remove(pdf_entity);
			}
		}
	}

	private long getDateDifference(Date timeStamp) {
		Date now = new Date();
		long diff = timeStamp.getTime() - now.getTime();

		System.out.println("DIFFERENCE: " + TimeUnit.MILLISECONDS.toHours(diff));

		return TimeUnit.MILLISECONDS.toHours(diff);
	}


	public boolean insert(PDF_Report pdf_report) {
		if (pdf_report == null) {
			System.err.println(this.getClass().getSimpleName() + " -> INSERT NULL PDF REPORT");
			return false;
		}

		try {
			DatabaseManager.getInstance().insert(pdf_report);
		} catch (Exception e) {
			System.err.println(this.getClass().getSimpleName() + " -> PDF REPORT INSERT FAIL");
			e.printStackTrace();
			return false;
		}

//		deleteOldPDFs();

		return true;
	}

	public boolean insert(PDF_Template pdfTemplate) {
		if (pdfTemplate == null) {
			System.err.println(this.getClass().getSimpleName() + " -> INSERT NULL PDF TEMPLATE");
			return false;
		}

		try {
			DatabaseManager.getInstance().insert(pdfTemplate);
		} catch (Exception e) {
			System.err.println(this.getClass().getSimpleName() + " -> PDF TEMPLATE INSERT FAIL");
			e.printStackTrace();
			return false;
		}

		return true;
	}

	public boolean update(PDF_Report pdf_report) {
		if (pdf_report == null) {
			System.err.println(this.getClass().getSimpleName() + " -> UPDATE NULL PDF REPORT");
			return false;
		}

		PDF_Report pdf_reportInDatabase = null;

		if (pdf_report.getId() != null) {
			pdf_reportInDatabase = DatabaseManager.getInstance().find(PDF_Report.class, pdf_report.getId());
		}
		try {
			if (pdf_reportInDatabase == null) {
				return insert(pdf_report);
			} else {
				DatabaseManager.getInstance().update(pdf_report);
			}
		} catch (Exception e) {
			System.err.println(this.getClass().getSimpleName() + " -> PDF REPORT UPDATE FAIL");
			e.printStackTrace();
			return false;
		}
		return true;
	}

	public boolean update(PDF_Template pdfTemplate) {
		if (pdfTemplate == null) {
			System.err.println(this.getClass().getSimpleName() + " -> UPDATE NULL PDF TEMPLATE");
			return false;
		}

		PDF_Template pdfTemplateInDatabase = null;

		if (pdfTemplate.getId() != null) {
			pdfTemplateInDatabase = DatabaseManager.getInstance().find(PDF_Template.class, pdfTemplate.getId());
		}
		try {
			if (pdfTemplateInDatabase == null) {
				return insert(pdfTemplate);
			} else {
				DatabaseManager.getInstance().update(pdfTemplate);
			}
		} catch (Exception e) {
			System.err.println(this.getClass().getSimpleName() + " -> PDF TEMPLATE UPDATE FAIL");
			e.printStackTrace();
			return false;
		}
		return true;
	}


	private boolean remove(PDF_Report pdf_entity) {
		if (pdf_entity == null) {
			System.err.println(this.getClass().getSimpleName() + " -> REMOVE NULL PDF");
			return false;
		}

		PDF_Report pdf_entityInDatabase = null;

		if (pdf_entity.getId() != null) {
			pdf_entityInDatabase = DatabaseManager.getInstance().find(PDF_Report.class, pdf_entity.getId());
		}

		try {
			if (pdf_entityInDatabase != null) {
				DatabaseManager.getInstance().remove(pdf_entity);
			} else {
				System.err.println(this.getClass().getSimpleName() + " -> PDF NOT FOUND IN DATABASE");
				return false;
			}
		} catch (Exception e) {
			System.out.println(this.getClass().getSimpleName() + " -> PDF REMOVE FAIL");
			e.printStackTrace();
			return false;
		}
		return true;
	}
}
