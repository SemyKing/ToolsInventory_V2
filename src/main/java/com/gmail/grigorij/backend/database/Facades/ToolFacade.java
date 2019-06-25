package com.gmail.grigorij.backend.database.facades;

import com.gmail.grigorij.backend.database.DatabaseManager;
import com.gmail.grigorij.backend.entities.tool.HierarchyType;
import com.gmail.grigorij.backend.entities.tool.Tool;

import javax.persistence.NoResultException;
import java.util.ArrayList;
import java.util.List;

public class ToolFacade {

	private static ToolFacade mInstance;
	private ToolFacade() {}
	public static ToolFacade getInstance() {
		if (mInstance == null) {
			mInstance = new ToolFacade();
		}
		return mInstance;
	}


	public List<Tool> getAllToolsList() {
		List<Tool> tools;
		try {
			tools = DatabaseManager.getInstance().createEntityManager().createNamedQuery("Tool.getAllToolsList", Tool.class)
					.getResultList();
		} catch (NoResultException nre) {
			tools = null;
		}
		return tools;
	}

	public List<Tool> getAllToolsListInCompany(long companyId) {
		List<Tool> tools;
		try {
			tools = DatabaseManager.getInstance().createEntityManager().createNamedQuery("Tool.getAllToolsListInCompany", Tool.class)
					.setParameter("id", companyId)
					.getResultList();
		} catch (NoResultException nre) {
			tools = null;
		}
		return tools;
	}

	public List<Tool> getAllCategoriesList() {
		List<Tool> categories = new ArrayList<>(getAllToolsList());
		categories.removeIf((Tool tool) -> tool.getHierarchyType().equals(HierarchyType.TOOL));
		return categories;
	}



	public boolean insert(Tool tool) {
		System.out.println();
		System.out.println("Tool INSERT");
		if (tool == null){
			return false;
		}

		try {
			DatabaseManager.getInstance().insert(tool);
		} catch (Exception e) {
			System.out.println("Tool INSERT fail");
			e.printStackTrace();
			return false;
		}
		System.out.println("Tool INSERT successful");
		return true;
	}

	public boolean update(Tool tool) {
		System.out.println();
		System.out.println("Tool UPDATE");
		if (tool == null) {
			return false;
		}

		Tool toolInDatabase = null;

		if (tool.getId() != null) {
			toolInDatabase = DatabaseManager.getInstance().find(Tool.class, tool.getId());
		}

		try {
			if (toolInDatabase == null) {
				return insert(tool);
			} else {
				DatabaseManager.getInstance().update(tool);
			}
		} catch (Exception e) {
			System.out.println("Tool UPDATE fail");
			e.printStackTrace();
			return false;
		}
		System.out.println("Tool UPDATE successful");
		return true;
	}


	public boolean remove(Tool tool) {
		System.out.println();
		System.out.println("Tool REMOVE");
		if (tool == null) {
			return false;
		}

		Tool toolInDatabase = DatabaseManager.getInstance().find(Tool.class, tool.getId());

		try {
			if (toolInDatabase != null) {
				DatabaseManager.getInstance().remove(tool);
			}
		} catch (Exception e) {
			System.out.println("Tool REMOVE fail");
			e.printStackTrace();
			return false;
		}
		System.out.println("Tool REMOVE successful");
		return true;
	}
}
