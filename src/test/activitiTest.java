package test;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.activiti.bpmn.BpmnAutoLayout;
import org.activiti.bpmn.model.ActivitiListener;
import org.activiti.bpmn.model.BpmnModel;
import org.activiti.bpmn.model.EndEvent;
import org.activiti.bpmn.model.ExclusiveGateway;
import org.activiti.bpmn.model.Process;
import org.activiti.bpmn.model.SequenceFlow;
import org.activiti.bpmn.model.StartEvent;
import org.activiti.bpmn.model.UserTask;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.runtime.ProcessInstance;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.mxgraph.view.mxEdgeStyle;
import com.zml.oa.dao.IJdbcDao;
import com.zml.oa.entity.ProcessDefine;
import com.zml.oa.entity.ProcessModel;

/**
 * 动态创建流程信息
 * @author zhao
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath*:/applicationContext.xml", "classpath*:/springMVC.xml" })
public class activitiTest {
	public static String PROCESSID = "process_test_1";

    @Autowired
    protected RepositoryService repositoryService;
    
	@Autowired
	protected RuntimeService runtimeService;
	
	@Autowired
	private IJdbcDao jdbcDao;
	
	@Test
	public void activiti() throws Exception,IOException {
		String sql = "select * from t_process_model where id = :id";
		Map<String, Object> paramMap = new HashMap<String, Object>();  
	    paramMap.put("id", 1);  
	    List<Map<String, Object>> list = this.jdbcDao.find(sql, paramMap);
		for(Map<String, Object> map : list){
			ProcessModel model = (ProcessModel) this.convertMap(new ProcessModel().getClass(), map);
			String sql2 = "select * from t_process_define where modelId = :modelId";
			paramMap.clear();
			paramMap.put("modelId", model.getId());
			List<Map<String, Object>> procDeflist = this.jdbcDao.find(sql2, paramMap);
			for(Map<String, Object> procDefMap : list){
				ProcessDefine proceDef = (ProcessDefine) this.convertMap(new ProcessDefine().getClass(), procDefMap);
				
			}
			
		}
		
		
		/*BpmnModel model = new BpmnModel();
		Process process = new Process();
		
		process.setId(PROCESSID);
		process.setName("动态流程测试");
		
		process.addFlowElement(createStartEvent());
		process.addFlowElement(createUserTask("userTask1", "用户任务1"));
		process.addFlowElement(createExclusiveGateway("gateway1"));
		process.addFlowElement(createUserTask("userTask2", "用户任务2"));
		process.addFlowElement(createExclusiveGateway("gateway2"));
		process.addFlowElement(createUserTask("userTask3", "用户任务3"));
		process.addFlowElement(createEndEvent());
		
		process.addFlowElement(createSequenceFlow("startEvent", "userTask1", "flow1", "", ""));
		process.addFlowElement(createSequenceFlow("userTask1", "gateway1", "flow2", "", ""));
		process.addFlowElement(createSequenceFlow("gateway1", "userTask2", "flow3", "同意", "${isPass}"));
		process.addFlowElement(createSequenceFlow("gateway1", "userTask3", "flow4", "不同意", "${!isPass}"));
		process.addFlowElement(createSequenceFlow("userTask2", "endEvent", "flow5", "", ""));
		process.addFlowElement(createSequenceFlow("userTask3", "gateway2", "flow6", "", ""));
		process.addFlowElement(createSequenceFlow("gateway2", "userTask1", "flow7", "同意", "${reApply}"));
		process.addFlowElement(createSequenceFlow("gateway2", "endEvent", "flow8", "结束", "${!reApply}"));
		
		model.addProcess(process);
		
		// 生成流程图片信息
		BpmnAutoLayout bpmnAutoLayout = new BpmnAutoLayout(model);
		bpmnAutoLayout.execute();
		// 部署流程
		Deployment deployment = this.repositoryService.createDeployment().addBpmnModel("processTest.bpmn", model).name("动态流程测试").deploy();
		
		// 启动流程
		ProcessInstance processInstance = this.runtimeService.startProcessInstanceByKey(PROCESSID); 
		
		// 导出流程图片
		InputStream processDiagram = this.repositoryService.getProcessDiagram(processInstance.getProcessDefinitionId());  
		FileUtils.copyInputStreamToFile(processDiagram, new File("D:/deployments/"+PROCESSID+".png"));  
		
		// 导出流程文件(BPMN xml)
		InputStream processBpmn = this.repositoryService.getResourceAsStream(deployment.getId(), PROCESSID+".bpmn");  
		FileUtils.copyInputStreamToFile(processBpmn,new File("D:/deployments/"+PROCESSID+".bpmn"));*/
		
	}
	
	/**
	 * 创建开始节点
	 * @return
	 */
	protected static StartEvent createStartEvent() {
		StartEvent startEvent = new StartEvent();
		startEvent.setId("startEvent");
		startEvent.setName("start");
		startEvent.setInitiator("startUserId");
		return startEvent;
	}
	
	/**
	 * 创建结束节点
	 * @return
	 */
	protected static EndEvent createEndEvent() {
		EndEvent endEvent = new EndEvent();
		endEvent.setId("endEvent");
		endEvent.setName("end");
		return endEvent;
	}
	
	/**
	 * 创建用户任务节点
	 * @param id
	 * @param name
	 * @return
	 */
	protected static UserTask createUserTask(String id, String name) {
		List<ActivitiListener> taskListeners = new ArrayList<ActivitiListener>();
		ActivitiListener listener = new ActivitiListener();
		listener.setId("");
		listener.setEvent("create");
		listener.setImplementationType("delegateExpression");
		listener.setImplementation("${userTaskListener}");
		taskListeners.add(listener);
		
		UserTask userTask = new UserTask();
		userTask.setId(id);
		userTask.setName(name);
		userTask.setTaskListeners(taskListeners);
		userTask.setDocumentation("");		// 说明
		return userTask;
	}
	
	/**
	 * 创建节点间的连线
	 * @param from
	 * @param to
	 * @param id
	 * @param name
	 * @param conditionExpression
	 * @return
	 */
	protected static SequenceFlow createSequenceFlow(String from, String to,String id,String name,String conditionExpression) {
		SequenceFlow flow = new SequenceFlow();
		flow.setId(id);
		flow.setName(name);
		flow.setSourceRef(from);
		flow.setTargetRef(to);
		if(StringUtils.isNotBlank(conditionExpression)) {
			flow.setConditionExpression(conditionExpression);
		}
		return flow;
	}
	
	/**
	 * 创建排他网关
	 * @param id
	 * @return
	 */
	protected static ExclusiveGateway createExclusiveGateway(String id) {
		ExclusiveGateway gateway = new ExclusiveGateway();
		gateway.setId(id);
		return gateway;
	}
	
    public static Object convertMap(Class type, Map map)
            throws IntrospectionException, IllegalAccessException,
            InstantiationException, InvocationTargetException {
        BeanInfo beanInfo = Introspector.getBeanInfo(type); // 获取类属性
        Object obj = type.newInstance(); // 创建 JavaBean 对象

        // 给 JavaBean 对象的属性赋值
        PropertyDescriptor[] propertyDescriptors =  beanInfo.getPropertyDescriptors();
        for (int i = 0; i< propertyDescriptors.length; i++) {
            PropertyDescriptor descriptor = propertyDescriptors[i];
            String propertyName = descriptor.getName().toUpperCase();

            if (map.containsKey(propertyName)) {
                // 下面一句可以 try 起来，这样当一个属性赋值失败的时候就不会影响其他属性赋值。
                Object value = map.get(propertyName);

                Object[] args = new Object[1];
                args[0] = value;

                descriptor.getWriteMethod().invoke(obj, args);
            }
        }
        return obj;
    }
}