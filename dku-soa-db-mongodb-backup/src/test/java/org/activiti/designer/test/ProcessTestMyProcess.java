package org.activiti.designer.test;

import static org.junit.Assert.assertNotNull;

import java.io.FileInputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.activiti.engine.HistoryService;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngineConfiguration;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.history.HistoricDetail;
import org.activiti.engine.history.HistoricVariableInstance;
import org.activiti.engine.history.HistoricVariableUpdate;
import org.activiti.engine.impl.history.HistoryLevel;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.test.ActivitiRule;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProcessTestMyProcess {
	private static final Logger LOGGER = LoggerFactory.getLogger(ProcessTestMyProcess.class);

	private String filename = ProcessTestMyProcess.class.getClassLoader().getResource("diagrams/transaction.bpmn")
			.getFile();

	// @Rule
	// public ActivitiRule activitiRule = new ActivitiRule();
	private RuntimeService runtimeService;
	private HistoryService historyService;
	private RepositoryService repositoryService;

	@Before
	public void before() {

		ProcessEngine processEngine = ProcessEngineConfiguration.createProcessEngineConfigurationFromResourceDefault()
				.setHistoryLevel(HistoryLevel.FULL).buildProcessEngine();

		runtimeService = processEngine.getRuntimeService();
		historyService = processEngine.getHistoryService();
		repositoryService = processEngine.getRepositoryService();
	}

	@Test
	public void startProcess() throws Exception {

		repositoryService.createDeployment().addInputStream("transaction.bpmn20.xml", new FileInputStream(filename))
				.deploy();
		Map<String, Object> variableMap = new HashMap<String, Object>();
		variableMap.put("rollback", "false");
		variableMap.put("from_balance", 3000);
		variableMap.put("to_balance", 1000);
		variableMap.put("amount", 100);
		variableMap.put("as", "100");

		ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("transactionProcess", variableMap);
		assertNotNull(processInstance.getId());
		System.out.println("id " + processInstance.getId() + " " + processInstance.getProcessDefinitionId());

		System.out.println(processInstance.isEnded());

		List<HistoricDetail> list = historyService.createHistoricDetailQuery()
				.processInstanceId(processInstance.getId()).list();
		for (HistoricDetail detail : list) {
			HistoricVariableUpdate hv = (HistoricVariableUpdate) detail;
			System.out.println(hv.getValue());
		}
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