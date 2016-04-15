package org.activiti.designer.test;

import static org.junit.Assert.assertNotNull;

import java.io.FileInputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;

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
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.client.RestTemplate;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath*:/spring/*.xml")
public class ProcessTestMyProcess {
	private static final Logger LOGGER = LoggerFactory.getLogger(ProcessTestMyProcess.class);

	private String filename = ProcessTestMyProcess.class.getClassLoader().getResource("diagrams/transaction.bpmn")
			.getFile();

	// @Rule
	// public ActivitiRule activitiRule = new ActivitiRule();
	@Resource(name = "runtimeService")
	private RuntimeService runtimeService;
	@Resource(name = "historyService")
	private HistoryService historyService;
	@Resource(name = "repositoryService")
	private RepositoryService repositoryService;

	@Resource
	private RestTemplate restTemplate;

	public static void main(String[] args) {
		String url = "http://gturnquist-quoters.cfapps.io/api/random";
		RestTemplate rest = new RestTemplate();
		@SuppressWarnings("unchecked")
		HashMap<String, String> res = rest.getForObject(url, HashMap.class, new Object[] {});

		Set<String> keys = res.keySet();
		for (String key : keys) {
			System.out.println(String.format("key:%s\tvalue:%s", key, res.get(key)));
		}
	}

	@Before
	public void before() {
		// ApplicationContext context = new
		// ClassPathXmlApplicationContext("spring/*.xml");
		// repositoryService = (RepositoryService)
		// context.getBean("repositoryService");
		// System.out.println("context");
	}

	@Test
	public void startProcess() throws Exception {
		repositoryService.createDeployment().addInputStream("transaction.bpmn20.xml", new FileInputStream(filename))
				.deploy();

		String res = restTemplate.getForObject("http://gturnquist-quoters.cfapps.io/api/random", String.class,
				new Object[] {});
		System.out.println(res);

		Map<String, Object> variableMap = new HashMap<String, Object>();
		variableMap.put("rollback", false);
		variableMap.put("from_balance", 3000);
		variableMap.put("to_balance", 1000);
		variableMap.put("amount", 100);
		variableMap.put("error_event", false);
		variableMap.put("res", false);

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