package com.gmail.grigorij.backend.database.facades;

import com.gmail.grigorij.backend.database.DatabaseManager;
import com.gmail.grigorij.backend.database.entities.Category;
import com.gmail.grigorij.backend.database.entities.Tool;
import com.gmail.grigorij.utils.ProjectConstants;

import javax.persistence.NoResultException;
import java.util.List;


public class InventoryFacade {

	private static InventoryFacade mInstance;
	private InventoryFacade() {}
	public static InventoryFacade getInstance() {
		if (mInstance == null) {
			mInstance = new InventoryFacade();
		}
		return mInstance;
	}

	public List<Tool> getAllTools() {
		List<Tool> tools;
		try {
			tools = DatabaseManager.getInstance().createEntityManager().createNamedQuery(Tool.QUERY_ALL, Tool.class)
					.getResultList();
		} catch (NoResultException nre) {
			tools = null;
		}
		return tools;
	}

	public List<Tool> getAllActiveTools() {
		List<Tool> tools;
		try {
			tools = DatabaseManager.getInstance().createEntityManager().createNamedQuery(Tool.QUERY_ALL, Tool.class)
					.getResultList();
			tools.removeIf(Tool::isDeleted);
		} catch (NoResultException nre) {
			tools = null;
		}
		return tools;
	}

	public List<Tool> getAllToolsInCompany(long companyId) {
		List<Tool> tools;
		try {
			tools = DatabaseManager.getInstance().createEntityManager().createNamedQuery(Tool.QUERY_ALL_BY_COMPANY_ID, Tool.class)
					.setParameter(ProjectConstants.ID_VAR, companyId)
					.getResultList();
		} catch (NoResultException nre) {
			tools = null;
		}
		return tools;
	}

	public List<Tool> getAllActiveToolsInCompany(long companyId) {
		List<Tool> tools;
		try {
			tools = DatabaseManager.getInstance().createEntityManager().createNamedQuery(Tool.QUERY_ALL_BY_COMPANY_ID, Tool.class)
					.setParameter(ProjectConstants.ID_VAR, companyId)
					.getResultList();
			tools.removeIf(Tool::isDeleted);
		} catch (NoResultException nre) {
			tools = null;
		}
		return tools;
	}

	public List<Category> getAllActiveCategoriesInCompany(long companyId) {
		List<Category> categories;
		try {
			categories = DatabaseManager.getInstance().createEntityManager().createNamedQuery(Category.QUERY_ALL_BY_COMPANY_ID, Category.class)
					.setParameter(ProjectConstants.ID_VAR, companyId)
					.getResultList();
			categories.removeIf(Category::isDeleted);
		} catch (NoResultException nre) {
			categories = null;
		}
		return categories;
	}

	public List<Tool> getAllToolsByCurrentUserId(long userId) {
		List<Tool> tools;
		try {
			tools = DatabaseManager.getInstance().createEntityManager().createNamedQuery(Tool.QUERY_ALL_BY_CURRENT_USER, Tool.class)
					.setParameter(ProjectConstants.ID_VAR, userId)
					.getResultList();
		} catch (NoResultException nre) {
			tools = null;
		}
		return tools;
	}

	public List<Tool> getAllToolsByReservedUserId(long userId) {
		List<Tool> tools;
		try {
			tools = DatabaseManager.getInstance().createEntityManager().createNamedQuery(Tool.QUERY_ALL_BY_RESERVED_USER, Tool.class)
					.setParameter(ProjectConstants.ID_VAR, userId)
					.getResultList();
		} catch (NoResultException nre) {
			tools = null;
		}
		return tools;
	}

	public Tool getToolById(Long id) {
		Tool tool;
		try {
			tool = DatabaseManager.getInstance().createEntityManager().createNamedQuery(Tool.QUERY_BY_ID, Tool.class)
					.setParameter(ProjectConstants.ID_VAR, id)
					.getSingleResult();
		} catch (NoResultException nre) {
			tool = null;
		}
		return tool;
	}

	// TODO: LIST OF TOOLS IF MULTIPLE
	public Tool getToolByCode(String code) {
		Tool tool;
		try {
			tool = DatabaseManager.getInstance().createEntityManager().createNamedQuery(Tool.QUERY_BY_CODE_VAR, Tool.class)
					.setParameter(ProjectConstants.VAR1, code)
					.getSingleResult();
		} catch (NoResultException nre) {
			tool = null;
		}
		return tool;
	}


	public boolean insert(Tool tool) {
		if (tool == null){
			System.err.println(this.getClass().getSimpleName() + " -> INSERT NULL INVENTORY_ITEM");
			return false;
		}

		try {
			DatabaseManager.getInstance().insert(tool);
		} catch (Exception e) {
			System.err.println(this.getClass().getSimpleName() + " -> TOOL INSERT FAIL");
			e.printStackTrace();
			return false;
		}
		return true;
	}

	public boolean insert(Category category) {
		if (category == null){
			System.err.println(this.getClass().getSimpleName() + " -> INSERT NULL CATEGORY");
			return false;
		}

		try {
			DatabaseManager.getInstance().insert(category);
		} catch (Exception e) {
			System.err.println(this.getClass().getSimpleName() + " ->  CATEGORY INSERT FAIL");
			e.printStackTrace();
			return false;
		}
		return true;
	}

	public boolean update(Tool tool) {
		if (tool == null) {
			System.err.println(this.getClass().getSimpleName() + " -> UPDATE NULL INVENTORY_ITEM");
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
			System.err.println(this.getClass().getSimpleName() + " -> TOOL UPDATE FAIL");
			e.printStackTrace();
			return false;
		}
		return true;
	}

	public boolean update(Category category) {
		if (category == null) {
			System.err.println(this.getClass().getSimpleName() + " -> UPDATE NULL CATEGORY");
			return false;
		}

		Category categoryInDatabase = null;

		if (category.getId() != null) {
			categoryInDatabase = DatabaseManager.getInstance().find(Category.class, category.getId());
		}

		try {
			if (categoryInDatabase == null) {
				return insert(category);
			} else {
				DatabaseManager.getInstance().update(category);
			}
		} catch (Exception e) {
			System.err.println(this.getClass().getSimpleName() + " -> CATEGORY UPDATE FAIL");
			e.printStackTrace();
			return false;
		}
		return true;
	}

	private void remove(Tool tool) {
		if (tool == null) {
			System.err.println(this.getClass().getSimpleName() + " -> REMOVE NULL TOOL");
			return;
		}

		Tool toolInDatabase = null;

		if (tool.getId() != null) {
			toolInDatabase = DatabaseManager.getInstance().find(Tool.class, tool.getId());
		}

		try {
			if (toolInDatabase != null) {
				DatabaseManager.getInstance().remove(toolInDatabase);
			} else {
				System.err.println(this.getClass().getSimpleName() + " -> TOOL NOT FOUND IN DATABASE");
			}
		} catch (Exception e) {
			System.out.println(this.getClass().getSimpleName() + " -> TOOL REMOVE FAIL");
			e.printStackTrace();
		}
	}
}
