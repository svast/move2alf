package eu.xenit.move2alf.core;

import eu.xenit.move2alf.classloading.ClasspathScanner;
import eu.xenit.move2alf.classloading.SystemClasspathScanner;
import eu.xenit.move2alf.core.action.ClassInfo;
import eu.xenit.move2alf.core.action.ClassInfoModel;
import eu.xenit.move2alf.pipeline.actions.Action;
import eu.xenit.move2alf.web.controller.destination.DestinationTypeController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;

/**
 * User: Thijs Lemmens (tlemmens@xenit.eu)
 * Date: 6/11/13
 * Time: 1:59 PM
 */
public abstract class AbstractClassInfoService {
    private static final Logger logger = LoggerFactory.getLogger(AbstractClassInfoService.class);
    protected final Map<String, ClassInfoModel> classActionClassInfoMap = new HashMap<String, ClassInfoModel>();
    protected final Map<String, ClassInfoModel> idClassMapping = new HashMap<String, ClassInfoModel>();
    protected final Map<String, DestinationTypeController> destinationTypeMap = new HashMap<String, DestinationTypeController>();
    private final Map<String, List<ClassInfoModel>> categoryClassInfoMapping = new HashMap<String, List<ClassInfoModel>>();

    protected ClasspathScanner classpathScanner = new SystemClasspathScanner();

    protected void scanForClasses(String basePackage) {
        final Set<Class<?>> matchingClasses = classpathScanner.findMatchingClasses(basePackage, getTargetType(), Action.class.getClassLoader());
        for (Class<?> clazz : matchingClasses) {
            ClassInfo actionInfo = clazz.getAnnotation(ClassInfo.class);
            ClassInfoModel classInfo;
            if (actionInfo == null) {
                classInfo = new ClassInfoModel(clazz.getCanonicalName(), ConfigurableObject.CAT_DEFAULT, clazz, "");
            } else {
                classInfo = new ClassInfoModel(actionInfo.classId(), actionInfo.category(), clazz, actionInfo.description());
            }
            registerClassInfo(classInfo);
        }
    }

    protected abstract Class<?> getTargetType();

    protected void reset() {
        classActionClassInfoMap.clear();
        idClassMapping.clear();
        categoryClassInfoMapping.clear();
        destinationTypeMap.clear();
    }

    private void refresh() {
        if (!classpathScanner.isStatic()) {
            reset();
            scanForClasses("");
        }
    }

    public ClassInfoModel getClassInfoModel(String classId) {
        refresh();
        return idClassMapping.get(classId);
    }

    public String getClassId(Class clazz) {
        final ClassInfoModel classInfoModel = classActionClassInfoMap.get(clazz.getCanonicalName());
        if (classInfoModel == null) {
            return clazz.getCanonicalName();
        }
        return classInfoModel.getClassId();
    }

    protected void registerClassInfo(ClassInfoModel classInfo) {
        logger.debug(String.format("registering classInfo: %s,%s,%s", classInfo.getClassId(), classInfo.getCategory(), classInfo.getDescription()));
        addClassInfoToCategory(classInfo.getCategory(), classInfo);
        idClassMapping.put(classInfo.getClassId(), classInfo);
        classActionClassInfoMap.put(classInfo.getClazz().getCanonicalName(), classInfo);
    }

    private void addClassInfoToCategory(String category, ClassInfoModel actionClassInfo) {
        List<ClassInfoModel> classInfos;
        if (categoryClassInfoMapping.containsKey(category)) {
            classInfos = categoryClassInfoMapping.get(category);
        } else {
            classInfos = new ArrayList<ClassInfoModel>();
            categoryClassInfoMapping.put(category, classInfos);
        }
        classInfos.add(actionClassInfo);
    }

    public List<ClassInfoModel> getClassesForCategory(String category) {
        refresh();
        return categoryClassInfoMapping.get(category);
    }

    public List<ClassInfoModel> getAllClassInfoModels() {
        refresh();
        List<ClassInfoModel> classInfoModels = new ArrayList<ClassInfoModel>();
        for (List<ClassInfoModel> models : categoryClassInfoMapping.values()) {
            classInfoModels.addAll(models);
        }
        return classInfoModels;
    }

    public DestinationTypeController getDestinationType(String classId) {
        return destinationTypeMap.get(classId);
    }

    @Autowired
    public void setClasspathScanner(ClasspathScanner classpathScanner) {
        this.classpathScanner = classpathScanner;
    }
}
