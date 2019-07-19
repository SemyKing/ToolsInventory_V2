package com.gmail.grigorij.backend.database.facades;

import com.gmail.grigorij.backend.database.DatabaseManager;
import com.gmail.grigorij.backend.entities.inventory.HierarchyType;
import com.gmail.grigorij.backend.entities.inventory.InventoryEntity;
import com.gmail.grigorij.utils.ProjectConstants;
import com.vaadin.flow.data.provider.hierarchy.TreeData;

import javax.persistence.NoResultException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class ToolFacade {

	private InventoryEntity rootCategory;
	private List<InventoryEntity> emptyList;

	private static ToolFacade mInstance;
	private ToolFacade() {
		rootCategory  = InventoryEntity.getEmptyTool();
		rootCategory.setName(ProjectConstants.ROOT_CATEGORY);

		emptyList = new ArrayList<>();
	}
	public static ToolFacade getInstance() {
		if (mInstance == null) {
			mInstance = new ToolFacade();
		}
		return mInstance;
	}

	public InventoryEntity getRootCategory() {
		return rootCategory;
	}

	public List<InventoryEntity> getEmptyList() {
		return emptyList;
	}


	public List<InventoryEntity> getAll() {
		List<InventoryEntity> tools;
		try {
			tools = DatabaseManager.getInstance().createEntityManager().createNamedQuery("Tool.getAll", InventoryEntity.class)
					.getResultList();
		} catch (NoResultException nre) {
			tools = null;
		}
		return tools;
	}

	public List<InventoryEntity> getAllInCompany(long companyId) {
		List<InventoryEntity> tools;
		try {
			tools = DatabaseManager.getInstance().createEntityManager().createNamedQuery("Tool.getAllInCompany", InventoryEntity.class)
					.setParameter("company_id_var", companyId)
					.getResultList();
		} catch (NoResultException nre) {
			tools = null;
		}
		return tools;
	}

//	public List<Tool> getAllCategoriesList() {
//		List<Tool> categories = new ArrayList<>(getAll());
//		categories.removeIf((Tool inventory) -> inventory.getHierarchyType().equals(HierarchyType.TOOL));
//		return categories;
//	}

//	public List<Tool> getAllCategoriesWithRoot() {
//		List<Tool> categories = new ArrayList<>(getAll());
//		categories.removeIf((Tool inventory) -> inventory.getHierarchyType().equals(HierarchyType.TOOL));
//		categories.add(0, rootCategory);
//		return categories;
//	}

	public List<InventoryEntity> getAllCategoriesInCompany(long companyId) {
		List<InventoryEntity> categories = new ArrayList<>(getAllInCompany(companyId));
		categories.removeIf((InventoryEntity tool) -> tool.getHierarchyType().equals(HierarchyType.TOOL));
		return categories;
	}

	public List<InventoryEntity> getAllCategoriesInCompanyWithRoot(long companyId) {
		List<InventoryEntity> categories = new ArrayList<>(getAllInCompany(companyId));
		categories.removeIf((InventoryEntity tool) -> tool.getHierarchyType().equals(HierarchyType.TOOL));
		categories.add(0, rootCategory);
		return categories;
	}

	public List<InventoryEntity> getAllToolsOnly() {
		List<InventoryEntity> toolsOnly = new ArrayList<>(getAll());
		toolsOnly.removeIf((InventoryEntity tool) -> tool.getHierarchyType().equals(HierarchyType.CATEGORY));
		return toolsOnly;
	}

	public List<InventoryEntity> getAllToolsInCompanyOnly(long companyId) {
		List<InventoryEntity> categories = new ArrayList<>(getAllInCompany(companyId));
		categories.removeIf((InventoryEntity tool) -> tool.getHierarchyType().equals(HierarchyType.CATEGORY));
		return categories;
	}

	public TreeData<InventoryEntity> getSortedToolsAndCategoriesByCompany(long companyId) {

		TreeData<InventoryEntity> data = new TreeData<>();
		List<InventoryEntity> toolsAndCategories = getAllInCompany(companyId);
		toolsAndCategories.sort(Comparator.comparing(InventoryEntity::getLevel).thenComparing(InventoryEntity::getName));

		// add root level items
//		data.addItems(null, categories);

		// add children for the root level items
//		categories.forEach(category -> data.addItems(category, category.getChildren()));
		toolsAndCategories.forEach(tool -> data.addItem(tool.getParentCategory(), tool));



		/*
		List must be sorted -> Parent must be added before child
		*/
//		toolsAndCategoriesInCompany.sort(Comparator.comparing(Tool::getLevel).thenComparing(Tool::getName));

		/*
		Add data to grid
		 */
//	    toolsAndCategoriesInCompany.forEach(inventory -> grid.getTreeData().addItem(inventory.getParentCategory(), inventory));

		return data;
	}


	public boolean insert(InventoryEntity tool) {
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

	public boolean update(InventoryEntity tool) {
		System.out.println();
		System.out.println("Tool UPDATE");
		if (tool == null) {
			return false;
		}

		InventoryEntity toolInDatabase = null;

		if (tool.getId() != null) {
			toolInDatabase = DatabaseManager.getInstance().find(InventoryEntity.class, tool.getId());
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

	public boolean remove(InventoryEntity tool) {
		System.out.println();
		System.out.println("Tool REMOVE");
		if (tool == null) {
			return false;
		}

		InventoryEntity toolInDatabase = DatabaseManager.getInstance().find(InventoryEntity.class, tool.getId());

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
