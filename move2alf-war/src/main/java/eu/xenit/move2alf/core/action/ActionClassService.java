package eu.xenit.move2alf.core.action;

import eu.xenit.move2alf.core.ConfigurableObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AssignableTypeFilter;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: thijs
 * Date: 5/16/13
 * Time: 11:34 AM
 * This class keeps a register of all Move2AlfAction subclasses and keeps them in the right categories.
 */
@Service
public class ActionClassService {

    private static final Logger logger = LoggerFactory.getLogger(ActionClassService.class);
    public ActionClassService(){

    }

    private Map<String, ActionClassInfo> idClassMapping = new HashMap<String, ActionClassInfo>();
    private Map<String, List<ActionClassInfo>> categoryClassInfoMapping = new HashMap<String, List<ActionClassInfo>>();
    private Map<Class, ActionClassInfo> classActionClassInfoMap = new HashMap<Class, ActionClassInfo>();

    private void scanForClasses(String basePackage) {
        ClassPathScanningCandidateComponentProvider provider = new ClassPathScanningCandidateComponentProvider(
                false);
        provider.addIncludeFilter(new AssignableTypeFilter(Move2AlfAction.class));
        Set<BeanDefinition> components = provider.findCandidateComponents(basePackage);
        for (BeanDefinition component : components) {
            try {
                Class clazz = Class.forName(component.getBeanClassName());
                ActionInfo actionInfo = (ActionInfo) clazz.getAnnotation(ActionInfo.class);
                ActionClassInfo classInfo = null;
                if(actionInfo == null){
                    classInfo = new ActionClassInfo(clazz.getCanonicalName(), ConfigurableObject.CAT_DEFAULT, clazz, "");
                } else {
                    classInfo = new ActionClassInfo(actionInfo.classId(), actionInfo.category(), clazz, actionInfo.description());
                }
                idClassMapping.put(classInfo.getClassId(), classInfo);
                addClassInfoToCategory(classInfo.getCategory(), classInfo);
                classActionClassInfoMap.put(clazz, classInfo);
            } catch (ClassNotFoundException e) {
                logger.error("The scanned class was not found. Should be impossible.", e);
            }
        }
    }

    private void addClassInfoToCategory(String category, ActionClassInfo actionClassInfo){
        List<ActionClassInfo> classInfos = null;
        if(categoryClassInfoMapping.containsKey(category)){
            classInfos = categoryClassInfoMapping.get(category);
        } else {
            classInfos = new ArrayList<ActionClassInfo>();
            categoryClassInfoMapping.put(category, classInfos);
        }
        classInfos.add(actionClassInfo);
    }

    @PostConstruct
    public void init() {
        scanForClasses("eu.xenit");
    }

    public List<ActionClassInfo> getClassesForCategory(String category){
        return categoryClassInfoMapping.get(category);
    }

    public ActionClassInfo getActionClassInfo(String classId){
        return idClassMapping.get(classId);
    }

    public String getClassId(Class clazz){
        if(!classActionClassInfoMap.containsKey(clazz))
            return clazz.getCanonicalName();
        return classActionClassInfoMap.get(clazz).getClassId();
    }
}
