package org.activiti.designer.test;

import static org.junit.Assert.*;

import javax.annotation.Resource;

import org.activiti.engine.HistoryService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.runtime.ProcessInstance;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({ "classpath*:/spring/*.xml" })
public class CamelProcessTest {
	@Resource(name = "runtimeService")
	private RuntimeService runtimeService;
	@Resource(name = "historyService")
	private HistoryService historyService;
	@Resource(name = "repositoryService")
	private RepositoryService repositoryService;

	@Test
	public void test() {
		Deployment deploy = repositoryService.createDeployment().addClasspathResource("diagrams/camelProcess.bpmn")
				.deploy();

		ProcessInstance process = runtimeService.startProcessInstanceByKey("camelProcess");
		System.out.println(process.isEnded());

	}

}
