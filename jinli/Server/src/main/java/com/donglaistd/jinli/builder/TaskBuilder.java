package com.donglaistd.jinli.builder;

import com.donglaistd.jinli.Constant;
import com.donglaistd.jinli.Jinli;
import com.donglaistd.jinli.config.ConditionType;
import com.donglaistd.jinli.config.TaskConfig;
import com.donglaistd.jinli.database.entity.Task;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class TaskBuilder {

    private final Map<Long, TaskConfig> dailyTaskConfigMap;
    private final Map<Long, TaskConfig> signInTaskConfigMap;
    private final Map<Long, TaskConfig> permanentTaskConfigMap;

    public TaskBuilder(@Value("${jinli.config.task.file.path}") final String configFilePath) throws FileNotFoundException, JsonProcessingException {
        File file = new File(configFilePath);
        assert file.exists();
        var jsonObject = new JSONObject(new JSONTokener(new FileInputStream(file)));
        dailyTaskConfigMap = generateTaskConfigMap(jsonObject, "dailyTaskConfigs");
        signInTaskConfigMap = generateTaskConfigMap(jsonObject, "signInTaskConfigs");
        permanentTaskConfigMap = generateTaskConfigMap(jsonObject, "permanentTaskConfigs");
    }

    private Map<Long, TaskConfig> generateTaskConfigMap(JSONObject jsonObject, String dailyTaskModules) throws JsonProcessingException {
        Map<Long, TaskConfig> taskLists = new HashMap<>();
        var configs = new ObjectMapper().readValue(jsonObject.getJSONArray(dailyTaskModules).toString(), TaskConfig[].class);
        for (var config : configs) {
            taskLists.put(config.getTaskId(), config);
        }
        return Collections.unmodifiableMap(taskLists);
    }

    public Map<Long, TaskConfig> getDailyTaskConfigMap() {
        return dailyTaskConfigMap;
    }

    public Map<Long, TaskConfig> getSignInTaskConfigMap() {
        return signInTaskConfigMap;
    }

    public Map<Long, TaskConfig> getPermanentTaskConfigMap() {
        return permanentTaskConfigMap;
    }

    public List<TaskConfig> getTaskConfigByCondition(ConditionType conditionType, Constant.TaskType taskType){
        switch (taskType){
            case TASK_DAILY:
                return this.dailyTaskConfigMap.values().stream().filter(taskConfig -> taskConfig.getCondition().getConditionType()
                        .equals(conditionType)).collect(Collectors.toList());
            case TASK_PERMANENT:
                return this.permanentTaskConfigMap.values().stream().filter(taskConfig -> taskConfig.getCondition().getConditionType()
                        .equals(conditionType)).collect(Collectors.toList());
        }
        return new ArrayList<>(0);
    }

    public List<Jinli.TaskInfo> toTaskInfo(List<Task> taskList) {
        List<Jinli.TaskInfo> taskInfos = new ArrayList<>();
        taskList.forEach(task -> taskInfos.add(task.toProto()));
        return taskInfos;
    }

    public TaskConfig getPermanentTaskByCondition(ConditionType conditionType){
        return getPermanentTaskConfigMap().values().stream().filter(
                taskConfig -> taskConfig.getCondition().getConditionType().equals(conditionType))
                .findFirst().orElse(null);
    }
}
