package com.example.flowable2;


import lombok.extern.slf4j.Slf4j;
import org.flowable.engine.ProcessEngine;
import org.flowable.engine.ProcessEngines;
import org.flowable.engine.repository.ProcessDefinition;
import org.flowable.engine.repository.ProcessDefinitionQuery;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.engine.runtime.ProcessInstanceQuery;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

/**
 * 将项目业务和flowable进行整合测试
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
public class FlowableTests2 {

    private ProcessEngine processEngine;

    //0.获取工作流的默认引擎对象
    @Before
    public void init() {
        processEngine = ProcessEngines.getDefaultProcessEngine();
    }

    //启动流程实例将项目业务表的ID添加到flowable中去
    @Test
    public void test1(){
        /**
         * param1:流程定义的key
         * param2:业务标的识 bussinesskey（和项目业务的表中的主键ID关联）
         */
        ProcessInstance processInstance = processEngine.getRuntimeService().startProcessInstanceByKey("problem", "1");

        System.out.println(processInstance.getBusinessKey());

    }

    /**
     * 将全部流程实例挂起和激活
     *（一个流程定义下面可以有多个流程实例，一个流程实例下面有多个任务实例
     *  这里也可以理解为将一个流程定义挂起，那么此流程定义下面的流程实例也就挂起了；
     * ）
     * 应用背景：
     *           新老业务流程交替
     */

    @Test
    public void test2(){
        ProcessDefinitionQuery processDefinitionQuery = processEngine.getRepositoryService().createProcessDefinitionQuery()
                .processDefinitionKey("problem");

        //获取流程定义对象
        ProcessDefinition processDefinition = processDefinitionQuery.singleResult();

        //获取当前流程定义的状态
        boolean suspended = processDefinition.isSuspended();

        if (suspended){
            processEngine.getRepositoryService().activateProcessDefinitionById(processDefinition.getId(),true,null);
            System.out.println(processDefinition.getId()+"激活");
        } else {
            //flase即为激活状态
            //激活状态则挂起
            processEngine.getRepositoryService().suspendProcessDefinitionById(processDefinition.getId(),true,null);
            System.out.println(processDefinition.getId()+"挂起");
        }

    }

    /**
     * 单个流程实例挂起和激活
     *
     * 经过测试将某个流程实例挂起，然后在执行这个流程的某个任务，会抛出异常（不能执行被挂请的异常）
     */

    @Test
    public void test3(){
        ProcessInstanceQuery processInstanceQuery = processEngine.getRuntimeService().createProcessInstanceQuery()
                .processDefinitionKey("problem");

        //获取流程定义对象
        List<ProcessInstance> list = processInstanceQuery.list();
        ProcessInstance processInstance = list.get(0);

        //获取当前流程定义的状态
        boolean suspended = processInstance.isSuspended();

        if (suspended){
            processEngine.getRuntimeService().activateProcessInstanceById(processInstance.getId());
            System.out.println(processInstance.getId()+"激活");
        } else {
            //flase即为激活状态
            //激活状态则挂起
            processEngine.getRuntimeService().suspendProcessInstanceById(processInstance.getId());
            System.out.println(processInstance.getId()+"挂起");
        }

    }
}
