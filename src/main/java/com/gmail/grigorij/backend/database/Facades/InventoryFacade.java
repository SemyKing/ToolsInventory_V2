package com.gmail.grigorij.backend.database.facades;

import com.gmail.grigorij.backend.database.DatabaseManager;
import com.gmail.grigorij.backend.entities.inventory.InventoryItem;
import com.gmail.grigorij.backend.enums.inventory.InventoryHierarchyType;
import com.gmail.grigorij.ui.utils.UIUtils;
import com.gmail.grigorij.utils.OperationStatus;
import com.gmail.grigorij.utils.ProjectConstants;

import javax.persistence.NoResultException;
import java.util.*;


public class InventoryFacade {

	private InventoryItem rootCategory;

	private static InventoryFacade mInstance;
	private InventoryFacade() {
		rootCategory = new InventoryItem();
		rootCategory.setName(ProjectConstants.ROOT_CATEGORY);
	}
	public static InventoryFacade getInstance() {
		if (mInstance == null) {
			mInstance = new InventoryFacade();
		}
		return mInstance;
	}

	public InventoryItem getRootCategory() {
		return rootCategory;
	}


	public List<InventoryItem> getAllInCompany(long companyId) {
		List<InventoryItem> allInCompany;
		try {
			allInCompany = DatabaseManager.getInstance().createEntityManager().createNamedQuery("getAllInCompany", InventoryItem.class)
					.setParameter("company_id_var", companyId)
					.getResultList();
		} catch (NoResultException nre) {
			allInCompany = null;
		}
		return allInCompany;
	}

	public List<InventoryItem> getAllCategoriesInCompany(long companyId) {
		List<InventoryItem> categories;
		try {
			categories = DatabaseManager.getInstance().createEntityManager().createNamedQuery("getAllCategoriesInCompany", InventoryItem.class)
					.setParameter("company_id_var", companyId)
					.setParameter("type_var", InventoryHierarchyType.CATEGORY)
					.getResultList();
		} catch (NoResultException nre) {
			categories = null;
		}
		return categories;
	}

	public List<InventoryItem> getAllToolsInCompany(long companyId) {
		List<InventoryItem> tools;
		try {
			tools = DatabaseManager.getInstance().createEntityManager().createNamedQuery("getAllToolsInCompany", InventoryItem.class)
					.setParameter("company_id_var", companyId)
					.setParameter("type_var", InventoryHierarchyType.TOOL)
					.getResultList();
		} catch (NoResultException nre) {
			tools = null;
		}
		return tools;
	}

	public List<InventoryItem> getAllToolsInUseByUser(long userId) {
		List<InventoryItem> tools;
		try {
			tools = DatabaseManager.getInstance().createEntityManager().createNamedQuery("getAllToolsInUseByUser", InventoryItem.class)
					.setParameter("user_id_var", userId)
					.setParameter("type_var", InventoryHierarchyType.TOOL)
					.getResultList();
		} catch (NoResultException nre) {
			tools = null;
		}
		return tools;
	}

	public List<InventoryItem> getAllToolsReservedByUser(long userId) {
		List<InventoryItem> tools;
		try {
			tools = DatabaseManager.getInstance().createEntityManager().createNamedQuery("getAllToolsReservedByUser", InventoryItem.class)
					.setParameter("user_id_var", userId)
					.setParameter("type_var", InventoryHierarchyType.TOOL)
					.getResultList();
		} catch (NoResultException nre) {
			tools = null;
		}
		return tools;
	}

	public List<InventoryItem> getAllByHierarchyType(InventoryHierarchyType type) {
		List<InventoryItem> tools;
		try {
			tools = DatabaseManager.getInstance().createEntityManager().createNamedQuery("getAllByHierarchyType", InventoryItem.class)
					.setParameter("type_var", type)
					.getResultList();
		} catch (NoResultException nre) {
			tools = null;
		}
		return tools;
	}

	public InventoryItem getToolById(Long id) {
		InventoryItem tool;
		try {
			tool = DatabaseManager.getInstance().createEntityManager().createNamedQuery("getToolById", InventoryItem.class)
					.setParameter("id_var", id)
					.getSingleResult();
		} catch (NoResultException nre) {
			tool = null;
		}
		return tool;
	}

	public InventoryItem getToolByCode(String code) {
		InventoryItem tool;
		try {
			tool = DatabaseManager.getInstance().createEntityManager().createNamedQuery("getToolByCode", InventoryItem.class)
					.setParameter("code_var", code)
					.getSingleResult();
		} catch (NoResultException nre) {
			tool = null;
		}
		return tool;
	}

	public Long getToolsCount() {
		Long toolsCount;
		try {
			toolsCount = DatabaseManager.getInstance().createEntityManager().createNamedQuery("getToolsCount", Long.class)
					.setParameter("type_var", InventoryHierarchyType.TOOL)
					.getSingleResult();
		} catch (NoResultException nre) {
			toolsCount = 0L;
		}
		return toolsCount;
	}


	private String getItemType(InventoryItem item) {
		if (item.getInventoryHierarchyType() == null) {
			return "NULL INVENTORY_HIERARCHY_TYPE";
		}
		return item.getInventoryHierarchyType().toString().toUpperCase();
	}


	public boolean insert(InventoryItem item) {
		if (item == null){
			System.err.println(this.getClass().getSimpleName() + " -> INSERT NULL INVENTORY_ITEM");
			return false;
		}

		try {
			DatabaseManager.getInstance().insert(item);
		} catch (Exception e) {
			System.err.println(this.getClass().getSimpleName() + " -> " + getItemType(item) + " INSERT FAIL");
			e.printStackTrace();
			return false;
		}
		return true;
	}

	public boolean update(InventoryItem item) {
		if (item == null) {
			System.err.println(this.getClass().getSimpleName() + " -> UPDATE NULL INVENTORY_ITEM");
			return false;
		}

		InventoryItem itemInDatabase = null;

		if (item.getId() != null) {
			itemInDatabase = DatabaseManager.getInstance().find(InventoryItem.class, item.getId());
		}

		try {
			if (itemInDatabase == null) {
				return insert(item);
			} else {
				DatabaseManager.getInstance().update(item);
			}
		} catch (Exception e) {
			System.err.println(this.getClass().getSimpleName() + " -> " + getItemType(item) + " UPDATE FAIL");
			e.printStackTrace();
			return false;
		}
		return true;
	}

	public void remove(InventoryItem item, OperationStatus status) {
		if (item == null) {
			System.err.println(this.getClass().getSimpleName() + " -> REMOVE NULL INVENTORY_ITEM");
			status.onFail("InventoryItem you are trying to remove is NULL", UIUtils.NotificationType.WARNING);
			return;
		}

		InventoryItem itemInDatabase = null;

		if (item.getId() != null) {
			itemInDatabase = DatabaseManager.getInstance().find(InventoryItem.class, item.getId());
		}

		try {
			if (itemInDatabase != null) {
				DatabaseManager.getInstance().remove(itemInDatabase);

				status.onSuccess("InventoryItem REMOVED successful", UIUtils.NotificationType.SUCCESS);
			} else {
				System.err.println(this.getClass().getSimpleName() + " -> " + getItemType(item) + ": '" + item.getName() + "'NOT FOUND IN DATABASE");
				status.onFail("InventoryItem not found in database", UIUtils.NotificationType.INFO);
			}
		} catch (Exception e) {
			System.out.println(this.getClass().getSimpleName() + " -> INVENTORY_ITEM REMOVE FAIL");
			status.onFail("InventoryItem REMOVE fail", UIUtils.NotificationType.ERROR);
			e.printStackTrace();
		}
	}
}
