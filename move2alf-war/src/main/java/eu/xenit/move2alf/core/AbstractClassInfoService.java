package eu.xenit.move2alf.core;

import eu.xenit.move2alf.core.action.ClassInfo;
import eu.xenit.move2alf.core.action.ClassInfoModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;

import java.util.*;

/**
 * User: Thijs Lemmens (tlemmens@xenit.eu)
 * Date: 6/11/13
 * Time: 1:59 PM
 */
public abstract class AbstractClassInfoService {

    private static final Logger logger = LoggerFactory.getLogger(AbstractClassInfoService.class);
    protected Map<Class, ClassInfoModel> classActionClassInfoMap = new HashMap<Class, ClassInfoModel>();
    protected Map<String, ClassInfoModel> idClassMapping = new HashMap<String, ClassInfoModel>();
    private Map<String, List<ClassInfoModel>> categoryClassInfoMapping = new HashMap<String, List<ClassInfoModel>>();

    protected void scanForClasses(String basePackage) {
        ClassPathScanningCandidateComponentProvider provider = new ClassPathScanningCandidateComponentProvider(
                false);
        addFilters(provider);
        Set<BeanDefinition> components = provider.findCandidateComponents(basePackage);
        for (BeanDefinition component : components) {
            try {
                Class clazz = Class.forName(component.getBeanClassName());
                ClassInfo actionInfo = (ClassInfo) clazz.getAnnotation(ClassInfo.class);
                ClassInfoModel classInfo = null;
                if(actionInfo == null){
                    classInfo = new ClassInfoModel(clazz.getCanonicalName(), ConfigurableObject.CAT_DEFAULT, clazz, "");
                } else {
                    classInfo = new ClassInfoModel(actionInfo.classId(), actionInfo.category(), clazz, actionInfo.description());
                }
                registerClassInfo(classInfo);
            } catch (ClassNotFoundException e) {
                logger.error("The scanned class was not found. Should be impossible.", e);
            }
        }
    }

    protected abstract void addFilters(ClassPathScanningCandidateComponentProvider provider);

    public ClassInfoModel getClassInfoModel(String classId){
        return idClassMapping.get(classId);
    }

    public String getClassId(Class clazz){
        if(!classActionClassInfoMap.containsKey(clazz))
            return clazz.getCanonicalName();
        return classActionClassInfoMap.get(clazz).getClassId();
    }

    protected void registerClassInfo(ClassInfoModel classInfo){
        addClassInfoToCategory(classInfo.getCategory(), classInfo);
        idClassMapping.put(classInfo.getClassId(), classInfo);
        classActionClassInfoMap.put(classInfo.getClazz(), classInfo);
    }

    private void addClassInfoToCategory(String category, ClassInfoModel actionClassInfo){
        List<ClassInfoModel> classInfos = null;
        if(categoryClassInfoMapping.containsKey(category)){
            classInfos = categoryClassInfoMapping.get(category);
        } else {
            classInfos = new ArrayList<ClassInfoModel>();
            categoryClassInfoMapping.put(category, classInfos);
        }
        classInfos.add(actionClassInfo);
    }

    public List<ClassInfoModel> getClassesForCategory(String category){
        return categoryClassInfoMapping.get(category);
    }

    public List<ClassInfoModel> getAllClasInfoModels(){
        List<ClassInfoModel> classInfoModels = new ArrayList<ClassInfoModel>();
        for(List<ClassInfoModel> models: categoryClassInfoMapping.values()){
            classInfoModels.addAll(models);
        }
        return classInfoModels;
    }
}
