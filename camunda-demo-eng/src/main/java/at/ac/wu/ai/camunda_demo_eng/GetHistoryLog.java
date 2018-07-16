package at.ac.wu.ai.camunda_demo_eng;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;

import org.apache.commons.math3.distribution.PoissonDistribution;
import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.ProcessEngineServices;
import org.camunda.bpm.engine.ProcessEngines;
import org.camunda.bpm.engine.history.HistoricActivityInstance;
import org.camunda.bpm.engine.task.Task;
import org.camunda.bpm.engine.variable.Variables;

import com.opencsv.CSVWriter;



public class GetHistoryLog {
	
	final static double arrivalRate = 2; //requests per unit of time
	final static long timeUnit = 1; // milliseconds
	final static String outFile = "event-log.csv";
	final static int numberOfRuns = 10;

	public static void main(String[] args) throws InterruptedException, IOException {
		
		ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
		System.out.println("Current engine: " +processEngine.getName());
		
		processEngine.getRepositoryService()
		.createDeployment()
		.addClasspathResource("music-subscription-activation.bpmn")
		.addClasspathResource("acertain-prepayment-necessity.dmn")
		.deploy();
		
		
//		long seed = 17399225432L; // Fixed seed means same results every time 
		
		PoissonDistribution arrivalsDistribution = new PoissonDistribution(arrivalRate);
		
		simultatePoissonRandomConfiguration(arrivalsDistribution, processEngine);
				
		exportHistory(processEngine);
	}

	private static void simultatePoissonRandomConfiguration(PoissonDistribution arrivalsDistribution, ProcessEngineServices processEngine) {
		
		for(int i=0; i<numberOfRuns; i++){
			int n = arrivalsDistribution.sample();
			//start n instances of the process
			
			for(;n>0;n--){
				processEngine.getRuntimeService().startProcessInstanceByKey("music-subscription-activation", Variables.createVariables().putValue("customer", "C"));
				Task t = processEngine.getTaskService().createTaskQuery().singleResult();
				processEngine.getTaskService().complete(t.getId(), Variables.createVariables().putValue("creditcard", "xxx"));
			}
			try {
				Thread.sleep(timeUnit);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	
//		for(int i=0; i<numberOfRuns; i++){
//			processEngine.getRuntimeService().startProcessInstanceByKey("music-subscription-activation");
//			
//			processEngine.getRuntimeService().startProcessInstanceByKey("music-subscription-activation", Variables.createVariables().putValue("customer", "C"));
//			
//			Task t = processEngine.getTaskService().createTaskQuery().singleResult();
//			
//			processEngine.getTaskService().complete(t.getId(), Variables.createVariables().putValue("creditcard", "xxx"));
//			
//		}
//		

	}

	/**
	 * @param processEngine
	 * @throws IOException
	 */
	private static void exportHistory(ProcessEngine processEngine) throws IOException {
		List<HistoricActivityInstance> l = processEngine.getHistoryService().createHistoricActivityInstanceQuery().list();
		
		String header[] = new String[]{
				"id", "activitId", "activityName", "activityType", 
				"assignee", "calledCaseInstanceId", "calledProcessInstanceId", "durationInMillis",
				"startTime", "endTime", "processDefinitionId", "processDefinitionKey",
				"processInstanceId", "taskId", "isCanceled", "isCompeletedScope"
				};
		
		CSVWriter csvWriter = new CSVWriter(new FileWriter(new File(outFile), false));
		csvWriter.writeNext(header);
				
		for (HistoricActivityInstance historicActivityInstance : l) {
			
//			System.out.println(historicActivityInstance);
			
			if(historicActivityInstance.getActivityType().contains("Error") || 
					historicActivityInstance.getActivityType().contains("Gateway") ||
					historicActivityInstance.getActivityType().contains("Event"))
				continue;
			
			String[] row = new String[16];
			
			row[0] = historicActivityInstance.getId();
			row[1] = historicActivityInstance.getActivityId();
			row[2] = historicActivityInstance.getActivityName();
			row[3] = historicActivityInstance.getActivityType();
			row[4] = historicActivityInstance.getAssignee();
			row[5] = historicActivityInstance.getCalledCaseInstanceId();
			row[6] = historicActivityInstance.getCalledProcessInstanceId();
			row[7] = historicActivityInstance.getDurationInMillis().toString();
			DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
			row[8] = df.format(historicActivityInstance.getStartTime());
			row[9] = df.format(historicActivityInstance.getEndTime());
			row[10] = historicActivityInstance.getProcessDefinitionId();
			row[11] = historicActivityInstance.getProcessDefinitionKey();
			row[12] = historicActivityInstance.getProcessInstanceId();
			row[13] = historicActivityInstance.getTaskId();
			row[14] = historicActivityInstance.isCanceled()+"";
			row[15] = historicActivityInstance.isCompleteScope()+"";
			
			csvWriter.writeNext(row);
		}	
		csvWriter.flush();
		csvWriter.close();
		System.out.println("Done. Results written into: "+outFile);
	}
}
