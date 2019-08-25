package com.example.flowable2;

import lombok.extern.slf4j.Slf4j;
import org.flowable.engine.*;
import org.flowable.engine.history.HistoricActivityInstance;
import org.flowable.engine.history.HistoricActivityInstanceQuery;
import org.flowable.engine.history.HistoricProcessInstance;
import org.flowable.engine.history.HistoricProcessInstanceQuery;
import org.flowable.engine.repository.Deployment;
import org.flowable.engine.repository.DeploymentBuilder;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.engine.runtime.ProcessInstanceQuery;
import org.flowable.task.api.Task;
import org.flowable.task.api.TaskQuery;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.CollectionUtils;

import java.io.InputStream;
import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.zip.ZipInputStream;

@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
public class Flowable2ApplicationTests {

    private ProcessEngine processEngine;

    //0.获取工作流的默认引擎对象
    @Before
    public void init() {
        processEngine = ProcessEngines.getDefaultProcessEngine();
    }



    //以bpmn和png文件将流程定义部署到相关的表中
    @Test
    public void defintionDeployToTableWithFile(){


        InputStream is = this.getClass().getClassLoader().getResourceAsStream("diagram/problem.zip");

        ZipInputStream zipInputStream = new ZipInputStream(is);


        //获取流程部署对象
        RepositoryService repositoryService = processEngine.getRepositoryService();

        //部署
        Deployment deployment = repositoryService.createDeployment().addZipInputStream(zipInputStream).name("问题跟踪流程").deploy();

        log.info(deployment.getName());
        log.info(deployment.getId());

    }

    //以zip(bpmn和png文件)压缩包将流程定义部署到相关的表中
    @Test
    public void defintionDeployToTableWtihZip(){

        //获取流程部署对象
        RepositoryService repositoryService = processEngine.getRepositoryService();

        //部署
        Deployment deployment = repositoryService.createDeployment().addClasspathResource("diagram/problem.bpmn")
                .addClasspathResource("diagram/problem.png")
                .name("问题跟踪流程").deploy();

        log.info(deployment.getName());
        log.info(deployment.getId());

    }


    //流程的启动
    @Test
    public void definitionGetInstance(){

        //获取流程启动的对象
        RuntimeService runtimeService = processEngine.getRuntimeService();

        //根据流程定义的key创建流程启动的实例（key是xml对应process节点的ID属性或者ACT_RE_PROCDEF表中KYE_字段的值）
        ProcessInstance problem = runtimeService.startProcessInstanceByKey("problem");

        log.info(problem.getDeploymentId()+"--流程部署的ID");// null
        log.info(problem.getProcessDefinitionId()+"--流程定义的ID");// problem:1:4  对应ACT_RE_PROCDEF表中的ID_
        log.info(problem.getId()+"--流程实例ID"); //2501  对应ACT_HI_PROCINST表中ID_的值
        log.info(problem.getActivityId()+"--活动ID"); //null


    }

    //查询当前用户的任务列表
    @Test
    public void getUserTasks(){

        //获取任务服务对象
        TaskService taskService = processEngine.getTaskService();
        //根据流程定义的key和负责人assignee，查询当前用户的任务列表
        List<Task> list = taskService.createTaskQuery().processDefinitionKey("problem").taskAssignee("张三").list();

        for (Task task : list) {
            log.info(task.getProcessDefinitionId()+"--流程实例ID");
            log.info(task.getId()+"--任务ID");
            log.info(task.getAssignee()+"--任务负责人");
            log.info(task.getName()+"--任务名称");
            log.info("-----------------------------");
        }
    }

    //张三处理当前的任务
    @Test
    public void executeTasks(){
        //查询张三当前的任务列表
        TaskService taskService = processEngine.getTaskService();
        TaskQuery taskQuery = taskService.createTaskQuery().processDefinitionKey("problem").taskAssignee("张三");
        List<Task> list = taskQuery.list();

        //张三处理任务
        taskService.complete(list.get(0).getId());

        //查询里斯当前任务
        List<Task> lisilist = taskService.createTaskQuery().processDefinitionKey("problem").taskAssignee("里斯").list();
        for (Task task : lisilist) {
            log.info(task.getId()+"--任务ID");
            log.info(task.getAssignee()+"--任务负责人");
            log.info(task.getName()+"--任务名称");
            log.info("-----------------------------");
        }

    }



    //里斯处理当前的任务
    @Test
    public void executeLTasks(){
        //查询里斯当前的任务列表
        TaskService taskService = processEngine.getTaskService();
        TaskQuery taskQuery = taskService.createTaskQuery().processDefinitionKey("problem").taskAssignee("里斯");
        List<Task> list = taskQuery.list();

        //里斯处理任务
        for (Task task : list) {
            taskService.complete(task.getId());
        }


        //查询里斯当前任务
        List<Task> lisilist = taskService.createTaskQuery().processDefinitionKey("problem").taskAssignee("里斯").list();
        for (Task task : lisilist) {
            log.info(task.getId()+"--任务ID");
            log.info(task.getAssignee()+"--任务负责人");
            log.info(task.getName()+"--任务名称");
            log.info("-----------------------------");
        }

    }

    //王五处理当前的任务
    @Test
    public void executeWTasks(){
        //查询王五当前的任务列表
        TaskService taskService = processEngine.getTaskService();
        TaskQuery taskQuery = taskService.createTaskQuery().processDefinitionKey("problem").taskAssignee("王五");
        List<Task> list = taskQuery.list();

        //王五处理任务
        for (Task task : list) {
            taskService.complete(task.getId());
        }


        //查询王五当前任务
        List<Task> lisilist = taskService.createTaskQuery().processDefinitionKey("problem").taskAssignee("王五").list();
        for (Task task : lisilist) {
            log.info(task.getId()+"--任务ID");
            log.info(task.getAssignee()+"--任务负责人");
            log.info(task.getName()+"--任务名称");
            log.info("-----------------------------");
        }

    }

    //根据流程实例查询历史记录
    @Test
    public void queryHistory(){
        HistoryService historyService = processEngine.getHistoryService();
        HistoricActivityInstanceQuery historicActivityInstanceQuery = historyService.createHistoricActivityInstanceQuery();

        //获取流程实例ID
//        HistoricProcessInstanceQuery historicProcessInstanceQuery = processEngine.getHistoryService().createHistoricProcessInstanceQuery().processInstanceBusinessKey("problem");
//        List<HistoricProcessInstance> list1 = historicProcessInstanceQuery.list();
//        String id = list1.get(0).getId();


        List<HistoricActivityInstance> list = historicActivityInstanceQuery.processInstanceId("2501").orderByHistoricActivityInstanceStartTime().asc().list();
        for (HistoricActivityInstance historicActivityInstance : list) {
            log.info(historicActivityInstance.getActivityId());
            log.info(historicActivityInstance.getActivityName());
        }


    }


}
