package at.ac.wu.ai.camunda_demo_eng;


import org.camunda.bpm.engine.delegate.BpmnError;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;

/**
 * @author Saimir Bala
 *
 */
public class MusicSubscriptionActivationDelegate implements JavaDelegate {

	@Override
	public void execute(DelegateExecution execution) throws Exception {
		String serviceTaskId = execution.getCurrentActivityId();
		String serviceTaskName = execution.getCurrentActivityName();
	    String processInstanceId = execution.getProcessInstanceId();
	    System.out.println("Executing Process Instance [" + processInstanceId + "] Service [" + serviceTaskName + "]");
	    
	    if ("load-customer-data".equals(serviceTaskId))
	      loadCustomerData(execution);
	    else if ("collect-prepayment".equals(serviceTaskId))
	      collectPrepayment(execution);		
	}

	private void collectPrepayment(DelegateExecution execution) {
		boolean contractCustomer = (boolean) execution.getVariable("contractCustomer");
	    boolean overdueNoticeOpen = (boolean) execution.getVariable("overdueNoticeOpen");
	    Object kreditkarte =  execution.getVariable("creditcard");
	    
	    if (kreditkarte == null && (overdueNoticeOpen || !contractCustomer))
	      throw new BpmnError("prepayment-not-possible");
	}

	
	/**
	 * Assign values variables that will be used by the decision table.
	 * @param execution
	 */
	private void loadCustomerData(DelegateExecution execution) {
		Object customer = execution.getVariable("customer");
	    if (customer == null) {
	      execution.setVariable("contractCustomer", true);
	      execution.setVariable("customerCategory", "A");
	      execution.setVariable("overdueNoticeOpen", false);
	    } else if ("1".equals(customer.toString())) {
	      execution.setVariable("contractCustomer", true);
	      execution.setVariable("customerCategory", "A");
	      execution.setVariable("overdueNoticeOpen", true);
	    } else if ("2".equals(customer.toString())) {
	      execution.setVariable("contractCustomer", true);
	      execution.setVariable("customerCategory", "B");
	      execution.setVariable("overdueNoticeOpen", true);
	    } else {
	      execution.setVariable("contractCustomer", false);
	      execution.setVariable("customerCategory", "A");
	      execution.setVariable("overdueNoticeOpen", false);
	    }
	}
}
