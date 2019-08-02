package com.gmail.grigorij.backend.database.facades;

import com.gmail.grigorij.backend.database.DatabaseManager;
import com.gmail.grigorij.backend.entities.inventory.InventoryEntity;
import com.gmail.grigorij.backend.entities.inventory.InventoryHierarchyType;
import com.gmail.grigorij.ui.utils.UIUtils;
import com.gmail.grigorij.utils.OperationStatus;
import com.gmail.grigorij.utils.ProjectConstants;
import com.vaadin.flow.data.provider.hierarchy.TreeData;

import javax.persistence.NoResultException;
import java.util.*;


public class InventoryFacade {

	private InventoryEntity rootCategory;

	private static InventoryFacade mInstance;
	private InventoryFacade() {
		rootCategory = new InventoryEntity();
		rootCategory.setName(ProjectConstants.ROOT_CATEGORY);
	}
	public static InventoryFacade getInstance() {
		if (mInstance == null) {
			mInstance = new InventoryFacade();
		}
		return mInstance;
	}

	public InventoryEntity getRootCategory() {
		return rootCategory;
	}


	private List<InventoryEntity> getAllInCompany(long companyId) {
		List<InventoryEntity> allInCompany;
		try {
			allInCompany = DatabaseManager.getInstance().createEntityManager().createNamedQuery("getAllInCompany", InventoryEntity.class)
					.setParameter("company_id_var", companyId)
					.getResultList();
		} catch (NoResultException nre) {
			allInCompany = null;
		}
		return allInCompany;
	}


	public List<InventoryEntity> getAllCategoriesInCompany(long companyId) {
		List<InventoryEntity> categories;
		try {
			categories = DatabaseManager.getInstance().createEntityManager().createNamedQuery("getAllCategoriesInCompany", InventoryEntity.class)
					.setParameter("company_id_var", companyId)
					.setParameter("type_var", InventoryHierarchyType.CATEGORY)
					.getResultList();
		} catch (NoResultException nre) {
			categories = null;
		}
		return categories;
	}


	public List<InventoryEntity> getAllByHierarchyType(InventoryHierarchyType type) {
		List<InventoryEntity> tools;
		try {
			tools = DatabaseManager.getInstance().createEntityManager().createNamedQuery("getAllByHierarchyType", InventoryEntity.class)
					.setParameter("type_var", type)
					.getResultList();
		} catch (NoResultException nre) {
			tools = null;
		}
		return tools;
	}


	public List<InventoryEntity> getAllToolsInCompany(long companyId) {
		List<InventoryEntity> tools;
		try {
			tools = DatabaseManager.getInstance().createEntityManager().createNamedQuery("getAllToolsInCompany", InventoryEntity.class)
					.setParameter("company_id_var", companyId)
					.setParameter("type_var", InventoryHierarchyType.TOOL)
					.getResultList();
		} catch (NoResultException nre) {
			tools = null;
		}
		return tools;
	}


	public InventoryEntity getToolById(Long id) {
		InventoryEntity tool;
		try {
			tool = DatabaseManager.getInstance().createEntityManager().createNamedQuery("getToolById", InventoryEntity.class)
					.setParameter("id_var", id)
					.getSingleResult();
		} catch (NoResultException nre) {
			tool = null;
		}
		return tool;
	}


	public InventoryEntity getToolByCode(String code) {
		InventoryEntity tool;
		try {
			tool = DatabaseManager.getInstance().createEntityManager().createNamedQuery("getToolByCode", InventoryEntity.class)
					.setParameter("code_var", code)
					.getSingleResult();
		} catch (NoResultException nre) {
			tool = null;
		}
		return tool;
	}



	private TreeData<InventoryEntity> treeData;

	public TreeData<InventoryEntity> getTreeDataInCompany(long companyId) {
		List<InventoryEntity> toolsAndCategories = getAllInCompany(companyId);

		//List must be sorted -> Parent must be added before child
		toolsAndCategories.sort(Comparator.comparing(InventoryEntity::getLevel));

		treeData = new TreeData<>();

		toolsAndCategories.forEach(item -> {
			treeData.addItem(item.getParentCategory(), item);
		});


		return treeData;
	}

	private void refreshTreeData(long companyId) {
		List<InventoryEntity> toolsAndCategories = getAllInCompany(companyId);
		toolsAndCategories.sort(Comparator.comparing(InventoryEntity::getLevel));

		treeData.clear();

		toolsAndCategories.forEach(item -> {
			treeData.addItem(item.getParentCategory(), item);
		});
	}



	public boolean insert(InventoryEntity ie) {
		System.out.println();
		System.out.println("DB, InventoryEntity INSERT");
		if (ie == null){
			return false;
		}

		try {
			DatabaseManager.getInstance().insert(ie);
		} catch (Exception e) {
			System.out.println("DB, InventoryEntity INSERT fail");
			e.printStackTrace();
			return false;
		}
		System.out.println("DB, InventoryEntity INSERT successful");
		return true;
	}

	public boolean update(InventoryEntity ie) {
		System.out.println();
		System.out.println("DB, InventoryEntity UPDATE");
		if (ie == null) {
			return false;
		}

		InventoryEntity ieInDatabase = null;

		if (ie.getId() != null) {
			ieInDatabase = DatabaseManager.getInstance().find(InventoryEntity.class, ie.getId());
		}

		try {
			if (ieInDatabase == null) {
				return insert(ie);
			} else {
				DatabaseManager.getInstance().update(ie);

//				refreshTreeData(ie.getCompany().getId());
			}
		} catch (Exception e) {
			System.out.println("DB, InventoryEntity UPDATE fail");
			e.printStackTrace();
			return false;
		}
		System.out.println("DB, InventoryEntity UPDATE successful");
		return true;
	}

	public void remove(InventoryEntity ie, OperationStatus status) {
		System.out.println();
		System.out.println("DB, InventoryEntity REMOVE");
		if (ie == null) {
			status.onFail("InventoryEntity you are trying to remove is NULL", UIUtils.NotificationType.WARNING);
			return;
		}

		InventoryEntity ieInDatabase = DatabaseManager.getInstance().find(InventoryEntity.class, ie.getId());

		try {
			if (ieInDatabase != null) {
				DatabaseManager.getInstance().remove(ieInDatabase);

				status.onSuccess("InventoryEntity REMOVED successful", UIUtils.NotificationType.SUCCESS);
			} else {
				status.onFail("InventoryEntity not found in database", UIUtils.NotificationType.INFO);
			}
		} catch (Exception e) {
			e.printStackTrace();

			status.onFail("InventoryEntity REMOVE fail", UIUtils.NotificationType.ERROR);
		}
	}
}
