package org.rmt2.api.handlers.admin.task;

import org.dto.TaskDto;
import org.dto.adapter.orm.ProjectObjectFactory;
import org.rmt2.jaxb.ProjectCriteriaGroup;
import org.rmt2.jaxb.RecordTrackingType;
import org.rmt2.jaxb.TaskCriteriaType;
import org.rmt2.jaxb.TaskType;
import org.rmt2.util.RecordTrackingTypeBuilder;
import org.rmt2.util.projecttracker.admin.TaskTypeBuilder;

import com.RMT2Base;

/**
 * A factory for converting Task project tracker administration related JAXB
 * objects to DTO and vice versa.
 * 
 * @author Roy Terrell.
 * 
 */
public class TaskJaxbDtoFactory extends RMT2Base {

    /**
     * Creates an instance of <i>TaskDto</i> using a valid
     * <i>ProjectCriteriaGroup</i> JAXB object.
     * 
     * @param jaxbObj
     *            an instance of {@link ProjectCriteriaGroup}
     * @return an instance of {@link TaskDto}
     */
    public static final TaskDto createTaskDtoCriteriaInstance(ProjectCriteriaGroup criteria) {

        if (criteria == null || criteria.getTaskCriteria() == null) {
            return null;
        }
        TaskCriteriaType jaxbObj = criteria.getTaskCriteria();
        TaskDto dto = ProjectObjectFactory.createTaskDtoInstance(null);
        if (jaxbObj.getTaskId() != null) {
            dto.setTaskId(jaxbObj.getTaskId().intValue());
        }
        // No using billable as selection criteria for now.
        // if (jaxbObj.getBillable() != null) {
        // dto.setTaskBillable(jaxbObj.getBillable().intValue());
        // }
        if (jaxbObj.getDescription() != null) {
            dto.setTaskDescription(jaxbObj.getDescription());
        }
        return dto;
    }
    
    /**
     * Created an instance of TaskDto from an TaskType object
     * 
     * @param jaxbObj
     *            an instance of {@link TaskType}
     * @return an instance of {@link TaskDto}
     */
    public static final TaskDto createTaskDtoInstance(TaskType jaxbObj) {
        if (jaxbObj == null) {
            return null;
        }
        TaskDto dto = ProjectObjectFactory.createTaskDtoInstance(null);
        if (jaxbObj.getTaskId() != null) {
            dto.setTaskId(jaxbObj.getTaskId().intValue());
        }
        if (jaxbObj.getBillable() != null) {
            dto.setTaskBillable(jaxbObj.getBillable().intValue());
        }
        if (jaxbObj.getDescription() != null) {
            dto.setTaskDescription(jaxbObj.getDescription());
        }
        return dto;
    }
    
    /**
     * Created an instance of TaskType from an TaskDto object
     * 
     * @param dto
     *            an instance of {@link TaskDto}
     * @return an instance of {@link TaskType}
     */
    public static final TaskType createTaskJaxbInstance(TaskDto dto) {
        if (dto == null) {
            return null;
        }
        RecordTrackingType tracking = RecordTrackingTypeBuilder.Builder.create()
                .withDateCreated(dto.getDateCreated())
                .withDateUpdate(dto.getDateUpdated())
                .withUserId(dto.getUpdateUserId())
                .build();

        TaskType tt = TaskTypeBuilder.Builder.create()
                .withTaskId(dto.getTaskId())
                .withTaskName(dto.getTaskDescription())
                .withBillableFlag(dto.getTaskBillable())
                .withRecordTracking(tracking)
                .build();

        return tt;
    }

}
