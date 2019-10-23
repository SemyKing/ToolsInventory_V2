package com.gmail.grigorij.backend.database.facades;

import com.gmail.grigorij.backend.database.DatabaseManager;
import com.gmail.grigorij.backend.entities.inventory.InventoryItem;
import com.gmail.grigorij.backend.enums.inventory.InventoryHierarchyType;
import com.gmail.grigorij.utils.ProjectConstants;

import javax.persistence.NoResultException;
import java.util.*;


public class InventoryFacade {

	private InventoryItem rootCategory;

	private static InventoryFacade mInstance;
	private InventoryFacade() {
		rootCategory = new InventoryItem();
		rootCategory.setName(ProjectConstants.ROOT_CATEGORY);
		rootCategory.setInventoryHierarchyType(InventoryHierarchyType.CATEGORY);
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


//	public List<InventoryItem> getAll() {
//		List<InventoryItem> all;
//		try {
//			all = DatabaseManager.getInstance().createEntityManager().createNamedQuery(InventoryItem.QUERY_ALL, InventoryItem.class)
//					.getResultList();
//		} catch (NoResultException nre) {
//			all = null;
//		}
//		return all;
//	}

	public List<InventoryItem> getAllByHierarchyType(InventoryHierarchyType type) {
		List<InventoryItem> tools;
		try {
			tools = DatabaseManager.getInstance().createEntityManager().createNamedQuery(InventoryItem.QUERY_ALL_BY_TYPE, InventoryItem.class)
					.setParameter(InventoryItem.VAR, type)
					.getResultList();
		} catch (NoResultException nre) {
			tools = null;
		}
		return tools;
	}

	public List<InventoryItem> getAllInCompany(long companyId) {
		List<InventoryItem> allInCompany;
		try {
			allInCompany = DatabaseManager.getInstance().createEntityManager().createNamedQuery(InventoryItem.QUERY_ALL_BY_COMPANY, InventoryItem.class)
					.setParameter(InventoryItem.ID_VAR, companyId)
					.getResultList();
		} catch (NoResultException nre) {
			allInCompany = null;
		}
		return allInCompany;
	}

	public List<InventoryItem> getAllInCompanyByType(long companyId, InventoryHierarchyType type) {
		List<InventoryItem> categories;
		try {
			categories = DatabaseManager.getInstance().createEntityManager().createNamedQuery(InventoryItem.QUERY_ALL_BY_COMPANY_BY_TYPE, InventoryItem.class)
					.setParameter(InventoryItem.ID_VAR, companyId)
					.setParameter(InventoryItem.VAR, type)
					.getResultList();
		} catch (NoResultException nre) {
			categories = null;
		}
		return categories;
	}

	public List<InventoryItem> getAllToolsByCurrentUser(long userId) {
		List<InventoryItem> tools;
		try {
			tools = DatabaseManager.getInstance().createEntityManager().createNamedQuery(InventoryItem.QUERY_ALL_BY_CURRENT_USER, InventoryItem.class)
					.setParameter(InventoryItem.ID_VAR, userId)
					.getResultList();
		} catch (NoResultException nre) {
			tools = null;
		}
		return tools;
	}

	public List<InventoryItem> getAllToolsByReservedUser(long userId) {
		List<InventoryItem> tools;
		try {
			tools = DatabaseManager.getInstance().createEntityManager().createNamedQuery(InventoryItem.QUERY_ALL_BY_RESERVED_USER, InventoryItem.class)
					.setParameter(InventoryItem.ID_VAR, userId)
					.getResultList();
		} catch (NoResultException nre) {
			tools = null;
		}
		return tools;
	}


	public InventoryItem getById(Long id) {
		InventoryItem tool;
		try {
			tool = DatabaseManager.getInstance().createEntityManager().createNamedQuery(InventoryItem.QUERY_BY_ID, InventoryItem.class)
					.setParameter(InventoryItem.ID_VAR, id)
					.getSingleResult();
		} catch (NoResultException nre) {
			tool = null;
		}
		return tool;
	}

	// TODO: LIST OF TOOLS IF MULTIPLE
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

	// LOCAL HELPER
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

	private void remove(InventoryItem item) {
		if (item == null) {
			System.err.println(this.getClass().getSimpleName() + " -> REMOVE NULL INVENTORY_ITEM");
			return;
		}

		InventoryItem itemInDatabase = null;

		if (item.getId() != null) {
			itemInDatabase = DatabaseManager.getInstance().find(InventoryItem.class, item.getId());
		}

		try {
			if (itemInDatabase != null) {
				DatabaseManager.getInstance().remove(itemInDatabase);
			} else {
				System.err.println(this.getClass().getSimpleName() + " -> " + getItemType(item) + ": '" + item.getName() + "'NOT FOUND IN DATABASE");
			}
		} catch (Exception e) {
			System.out.println(this.getClass().getSimpleName() + " -> INVENTORY_ITEM REMOVE FAIL");
			e.printStackTrace();
		}
	}
}
