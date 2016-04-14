package org.activiti.designer.test;

import static org.junit.Assert.assertNotNull;

import java.io.FileInputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.activiti.engine.HistoryService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.history.HistoricVariableInstance;
import org.activiti.engine.runtime.ProcessInstance;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

//@RunWith(SpringJUnit4ClassRunner.class)
// @ContextConfiguration(locations = "classpath*:srping/*.xml")
public class ProcessTestMyProcess {
	private static final Logger LOGGER = LoggerFactory.getLogger(ProcessTestMyProcess.class);

	private String filename = ProcessTestMyProcess.class.getClassLoader().getResource("diagrams/transaction.bpmn")
			.getFile();

	// @Rule
	// public ActivitiRule activitiRule = new ActivitiRule();
	// @Autowired
	private RuntimeService runtimeService;
	// @Autowired
	private HistoryService historyService;
	// @Autowired
	private RepositoryService repositoryService;

	@Before
	public void before() {
		ApplicationContext context = new ClassPathXmlApplicationContext("spring/*.xml");
		System.out.println("context");
	}

	@Test
	public void startProcess() throws Exception {
		repositoryService.createDeployment().addInputStream("transaction.bpmn20.xml", new FileInputStream(filename))
				.deploy();

		Map<String, Object> variableMap = new HashMap<String, Object>();
		variableMap.put("rollback", false);
		variableMap.put("from_balance", 3000);
		variableMap.put("to_balance", 1000);
		variableMap.put("amount", 100);
		variableMap.put("error_event", false);

		ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("transactionProcess", variableMap);
		assertNotNull(processInstance.getId());
		System.out.println("id " + processInstance.getId() + " " + processInstance.getProcessDefinitionId());

		System.out.println(String.format("流程实例是否结束:\t%s", processInstance.isEnded()));

		/////////////////////////////

		List<HistoricVariableInstance> hlist = historyService.createHistoricVariableInstanceQuery()
				.processInstanceId(processInstance.getId()).list();

		for (HistoricVariableInstance pInstance : hlist) {
			LOGGER.debug("--------------");
			System.out.println(String.format("key:%s\tvalue:%s\t:%s", pInstance.getVariableName(), pInstance.getValue(),
					pInstance.getVariableTypeName()));
		}
	}
}