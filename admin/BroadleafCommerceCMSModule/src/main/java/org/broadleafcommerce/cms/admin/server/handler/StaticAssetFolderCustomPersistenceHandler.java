package org.broadleafcommerce.cms.admin.server.handler;

import com.anasoft.os.daofusion.cto.client.CriteriaTransferObject;
import org.broadleafcommerce.cms.admin.client.datasource.file.StaticAssetsFolderTreeDataSourceFactory;
import org.broadleafcommerce.cms.file.domain.StaticAssetFolder;
import org.broadleafcommerce.cms.file.domain.StaticAssetFolderImpl;
import org.broadleafcommerce.cms.file.service.StaticAssetService;
import org.broadleafcommerce.openadmin.client.dto.*;
import org.broadleafcommerce.openadmin.client.service.ServiceException;
import org.broadleafcommerce.openadmin.server.dao.DynamicEntityDao;
import org.broadleafcommerce.openadmin.server.service.handler.CustomPersistenceHandlerAdapter;
import org.broadleafcommerce.openadmin.server.service.persistence.module.InspectHelper;
import org.broadleafcommerce.openadmin.server.service.persistence.module.RecordHelper;

import javax.annotation.Resource;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by jfischer
 */
public class StaticAssetFolderCustomPersistenceHandler extends CustomPersistenceHandlerAdapter {

    @Resource(name="blStaticAssetService")
	protected StaticAssetService staticAssetService;

    @Override
    public Boolean canHandleInspect(PersistencePackage persistencePackage) {
        return persistencePackage.getCustomCriteria() != null && persistencePackage.getCustomCriteria().length > 0 && persistencePackage.getCustomCriteria()[0].equals("assetFolderUi");
    }

    @Override
    public Boolean canHandleFetch(PersistencePackage persistencePackage) {
        return canHandleInspect(persistencePackage);
    }

    @Override
    public Boolean canHandleAdd(PersistencePackage persistencePackage) {
        return canHandleInspect(persistencePackage);
    }

    @Override
    public Boolean canHandleRemove(PersistencePackage persistencePackage) {
        return canHandleInspect(persistencePackage);
    }

    @Override
    public Boolean canHandleUpdate(PersistencePackage persistencePackage) {
        return canHandleInspect(persistencePackage);
    }

    @Override
    public DynamicResultSet inspect(PersistencePackage persistencePackage, Map<String, FieldMetadata> metadataOverrides, DynamicEntityDao dynamicEntityDao, InspectHelper helper) throws ServiceException {
        String ceilingEntityFullyQualifiedClassname = persistencePackage.getCeilingEntityFullyQualifiedClassname();
		try {
			PersistencePerspective persistencePerspective = persistencePackage.getPersistencePerspective();
			Map<MergedPropertyType, Map<String, FieldMetadata>> allMergedProperties = new HashMap<MergedPropertyType, Map<String, FieldMetadata>>();
			Class<?>[] entityClasses = dynamicEntityDao.getAllPolymorphicEntitiesFromCeiling(StaticAssetFolder.class);
			Map<String, FieldMetadata> mergedProperties = dynamicEntityDao.getMergedProperties(
				StaticAssetFolder.class.getName(),
				entityClasses,
				(ForeignKey) persistencePerspective.getPersistencePerspectiveItems().get(PersistencePerspectiveItemType.FOREIGNKEY),
				persistencePerspective.getAdditionalNonPersistentProperties(),
				persistencePerspective.getAdditionalForeignKeys(),
				MergedPropertyType.PRIMARY,
				persistencePerspective.getPopulateToOneFields(),
				persistencePerspective.getIncludeFields(),
				persistencePerspective.getExcludeFields(),
				null,
				""
			);
            String[] keys = mergedProperties.keySet().toArray(new String[]{});
			for (String key : keys) {
                FieldMetadata temp = mergedProperties.get(key);
                if (!temp.getInheritedFromType().equals(StaticAssetFolderImpl.class.getName())) {
                    mergedProperties.remove(key);
                }
            }
			allMergedProperties.put(MergedPropertyType.PRIMARY, mergedProperties);
			ClassMetadata mergedMetadata = helper.getMergedClassMetadata(entityClasses, allMergedProperties);
			DynamicResultSet results = new DynamicResultSet(mergedMetadata, null, null);

			return results;
		} catch (Exception e) {
			throw new ServiceException("Unable to retrieve inspection results for " + ceilingEntityFullyQualifiedClassname, e);
		}
    }

    @Override
    public void remove(PersistencePackage persistencePackage, DynamicEntityDao dynamicEntityDao, RecordHelper helper) throws ServiceException {
		Entity entity = persistencePackage.getEntity();
        try {
			PersistencePerspective persistencePerspective = persistencePackage.getPersistencePerspective();
			Class<?>[] entityClasses = dynamicEntityDao.getAllPolymorphicEntitiesFromCeiling(StaticAssetFolder.class);
			Map<String, FieldMetadata> adminProperties = helper.getSimpleMergedProperties(StaticAssetFolder.class.getName(), persistencePerspective, entityClasses);
			Object primaryKey = helper.getPrimaryKey(entity, adminProperties);
			StaticAssetFolder adminInstance = (StaticAssetFolder) dynamicEntityDao.retrieve(Class.forName(entity.getType()[0]), primaryKey);

            staticAssetService.deleteStaticAssetFolder(adminInstance);
		} catch (Exception e) {
			throw new ServiceException("Unable to remove entity for " + entity.getType()[0], e);
		}
    }

    @Override
    public Entity add(PersistencePackage persistencePackage, DynamicEntityDao dynamicEntityDao, RecordHelper helper) throws ServiceException {
        Entity entity  = persistencePackage.getEntity();
		try {
			PersistencePerspective persistencePerspective = persistencePackage.getPersistencePerspective();
			StaticAssetFolder adminInstance = (StaticAssetFolder) Class.forName(entity.getType()[0]).newInstance();
			Class<?>[] entityClasses = dynamicEntityDao.getAllPolymorphicEntitiesFromCeiling(StaticAssetFolder.class);
			Map<String, FieldMetadata> adminProperties = helper.getSimpleMergedProperties(StaticAssetFolder.class.getName(), persistencePerspective, entityClasses);
			adminInstance = (StaticAssetFolder) helper.createPopulatedInstance(adminInstance, entity, adminProperties, false);

            adminInstance = staticAssetService.addStaticAssetFolder(adminInstance, adminInstance.getParentFolder());

			Entity adminEntity = helper.getRecord(adminProperties, adminInstance, null, null);

			return adminEntity;
		} catch (Exception e) {
			throw new ServiceException("Unable to add entity for " + entity.getType()[0], e);
		}
    }

    @Override
    public DynamicResultSet fetch(PersistencePackage persistencePackage, CriteriaTransferObject cto, DynamicEntityDao dynamicEntityDao, RecordHelper helper) throws ServiceException {
        String ceilingEntityFullyQualifiedClassname = persistencePackage.getCeilingEntityFullyQualifiedClassname();
        try {
            String parentCategoryId = cto.get(StaticAssetsFolderTreeDataSourceFactory.parentFolderForeignKey).getFilterValues().length==0?null:cto.get(StaticAssetsFolderTreeDataSourceFactory.parentFolderForeignKey).getFilterValues()[0];
            StaticAssetFolder pageOrFolder = null;
            if (parentCategoryId != null) {
                pageOrFolder = staticAssetService.findStaticAssetById(Long.valueOf(parentCategoryId));
            }
            List<StaticAssetFolder> folders = staticAssetService.findStaticAssetFolderChildFolders(pageOrFolder);
            List<Serializable> convertedList = new ArrayList<Serializable>();
            convertedList.addAll(folders);

            PersistencePerspective persistencePerspective = persistencePackage.getPersistencePerspective();
            Map<String, FieldMetadata> pageProperties = getMergedProperties(StaticAssetFolder.class, dynamicEntityDao, persistencePerspective.getPopulateToOneFields(), persistencePerspective.getIncludeFields(), persistencePerspective.getExcludeFields(), persistencePerspective.getAdditionalForeignKeys());

            Entity[] entities = helper.getRecords(pageProperties, convertedList);

            DynamicResultSet response = new DynamicResultSet(entities, entities.length);

            return response;
        } catch (Exception e) {
            throw new ServiceException("Unable to perform fetch for entity: "+ceilingEntityFullyQualifiedClassname, e);
        }
    }

    protected Map<String, FieldMetadata> getMergedProperties(Class<?> ceilingEntityFullyQualifiedClass, DynamicEntityDao dynamicEntityDao, Boolean populateManyToOneFields, String[] includeManyToOneFields, String[] excludeManyToOneFields, ForeignKey[] additionalForeignKeys) throws ClassNotFoundException, SecurityException, IllegalArgumentException, NoSuchMethodException, IllegalAccessException, InvocationTargetException {
		Class<?>[] entities = dynamicEntityDao.getAllPolymorphicEntitiesFromCeiling(ceilingEntityFullyQualifiedClass);
		Map<String, FieldMetadata> mergedProperties = dynamicEntityDao.getMergedProperties(
			ceilingEntityFullyQualifiedClass.getName(),
			entities,
			null,
			new String[]{},
			additionalForeignKeys,
			MergedPropertyType.PRIMARY,
			populateManyToOneFields,
			includeManyToOneFields,
			excludeManyToOneFields,
			null,
			""
		);

		return mergedProperties;
	}
}
