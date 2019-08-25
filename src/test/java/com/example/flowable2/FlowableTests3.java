package com.example.flowable2;


import lombok.extern.slf4j.Slf4j;
import org.flowable.engine.ProcessEngine;
import org.flowable.engine.ProcessEngines;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.repository.ProcessDefinition;
import org.flowable.engine.repository.ProcessDefinitionQuery;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.engine.runtime.ProcessInstanceQuery;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashMap;
import java.util.List;

/**
 * 流程启动的时候动态的给任务负责人（assignee）赋值
 *
 * 主要有两种方式
 *  UEL-value
 *  设置监听器
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
public class FlowableTests3 {

    private ProcessEngine processEngine;

    //0.获取工作流的默认引擎对象
    @Before
    public void init() {
        processEngine = ProcessEngines.getDefaultProcessEngine();
    }

    @Test
    public void test1(){
        RuntimeService runtimeService = processEngine.getRuntimeService();

        //设置assignee的值
        HashMap<String,Object> aggs = new HashMap<>();
        aggs.put("agg1","甲");
        aggs.put("agg2","乙");
        aggs.put("agg3","丙");

        ProcessInstance problem2 = runtimeService.startProcessInstanceByKey("problem2", aggs);
        System.out.println(problem2.getBusinessKey());

    }


}
