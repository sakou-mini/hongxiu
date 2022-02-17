package com.donglaistd.jinli.database.dao;

import com.donglaistd.jinli.Constant;
import com.donglaistd.jinli.database.entity.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

import static com.donglaistd.jinli.Constant.TaskStatus.*;

@Service
@Transactional
public class TaskDaoService {

    @Autowired
    private TaskDaoRepository taskDaoRepository;
    @Autowired
    private MongoTemplate mongoTemplate;
    @Value("${beginning.of.week.offset}")
    private long beginningOfWeekOffset;


    public List<Task> findDailyTasksByOwnerToday(String id) {
        var query = new Query();
        query.addCriteria(Criteria.where("ownerId").is(id));
        query.addCriteria(Criteria.where("taskType").is(Constant.TaskType.TASK_DAILY));
        query.addCriteria(new Criteria().orOperator(Criteria.where("taskStatus").is(TASK_STATUS_ACTIVE), Criteria.where("taskStatus").is(TASK_STATUS_FINISHED)));
        var beginningToday = LocalDate.now().atStartOfDay();
        query.addCriteria(Criteria.where("createDate").gte(beginningToday));
        return mongoTemplate.find(query, Task.class);
    }

    public List<Task> findDailyTasksByOwnerTodayAgo(String id){
        var query = new Query();
        query.addCriteria(Criteria.where("ownerId").is(id));
        query.addCriteria(Criteria.where("taskType").is(Constant.TaskType.TASK_DAILY));
        query.addCriteria(new Criteria().orOperator(Criteria.where("taskStatus").is(TASK_STATUS_ACTIVE), Criteria.where("taskStatus").is(TASK_STATUS_FINISHED)));
        var beginningToday = LocalDate.now().atStartOfDay();
        query.addCriteria(Criteria.where("createDate").lt(beginningToday));
        return mongoTemplate.find(query, Task.class);
    }

    public Task save(Task task){
        return taskDaoRepository.save(task);
    }

    public List<Task> saveAll(List<Task> dailyTasks) {
        return taskDaoRepository.saveAll(dailyTasks);
    }

    public List<Task> findWeeklyTasksByOwnerThisWeek(String id) {
        var query = new Query();
        query.addCriteria(Criteria.where("ownerId").is(id));
        query.addCriteria(Criteria.where("taskType").is(Constant.TaskType.TASK_SIGN_IN));
        query.addCriteria(new Criteria().orOperator(Criteria.where("taskStatus").is(TASK_STATUS_ACTIVE), Criteria.where("taskStatus").is(TASK_STATUS_FINISHED)));
        LocalDate now = LocalDate.now();
        query.addCriteria(Criteria.where("createDate").gte(now.minusDays(now.getDayOfWeek().getValue() - 1).atStartOfDay().plusSeconds(beginningOfWeekOffset / 1000)))
                .with(Sort.by(Sort.Direction.ASC, "taskId"));
        return mongoTemplate.find(query, Task.class);
    }

    public List<Task> findWeeklyTasksByOwnerThisWeekAgo(String id) {
        var query = new Query();
        query.addCriteria(Criteria.where("ownerId").is(id));
        query.addCriteria(Criteria.where("taskType").is(Constant.TaskType.TASK_SIGN_IN));
        query.addCriteria(new Criteria().orOperator(Criteria.where("taskStatus").is(TASK_STATUS_ACTIVE), Criteria.where("taskStatus").is(TASK_STATUS_FINISHED)));
        LocalDate now = LocalDate.now();
        query.addCriteria(Criteria.where("createDate").lt(now.minusDays(now.getDayOfWeek().getValue() - 1).atStartOfDay().plusSeconds(beginningOfWeekOffset / 1000)));
        return mongoTemplate.find(query, Task.class);
    }

    public List<Task> findAllPermanentTasksByOwner(String id) {
        var query = new Query();
        query.addCriteria(Criteria.where("ownerId").is(id));
        query.addCriteria(Criteria.where("taskType").is(Constant.TaskType.TASK_PERMANENT));
        query.addCriteria(new Criteria().orOperator(Criteria.where("taskStatus").is(TASK_STATUS_ACTIVE), Criteria.where("taskStatus").is(TASK_STATUS_FINISHED)));
        return mongoTemplate.find(query, Task.class);
    }

    public List<Task> findAllByIds(List<String> ids){
        return taskDaoRepository.findAllByIdIn(ids);
    }

    public List<Task> findActivePermanentTasksByOwnerAndTaskId(String ownerId, List<Long> taskConfigIds){
        var query = new Query();
        query.addCriteria(Criteria.where("ownerId").is(ownerId))
                .addCriteria(Criteria.where("taskType").is(Constant.TaskType.TASK_PERMANENT))
                .addCriteria(Criteria.where("taskId").in(taskConfigIds))
                .addCriteria(Criteria.where("taskStatus").is(TASK_STATUS_ACTIVE));
        return mongoTemplate.find(query, Task.class);
    }

    public List<Task> findActiveDailyTasksByOwnerAndTaskId(String ownerId, List<Long> taskConfigIds){
        var query = new Query();
        var beginningToday = LocalDate.now().atStartOfDay();
        query.addCriteria(Criteria.where("ownerId").is(ownerId))
                .addCriteria(Criteria.where("taskId").in(taskConfigIds))
                .addCriteria(Criteria.where("taskType").is(Constant.TaskType.TASK_DAILY))
                .addCriteria(Criteria.where("taskStatus").is(TASK_STATUS_ACTIVE))
                .addCriteria(Criteria.where("createDate").gte(beginningToday));;
        return mongoTemplate.find(query, Task.class);
    }
}