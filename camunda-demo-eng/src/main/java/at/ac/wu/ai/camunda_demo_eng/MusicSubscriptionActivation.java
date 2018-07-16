package at.ac.wu.ai.camunda_demo_eng;

import org.camunda.bpm.application.ProcessApplication;
import org.camunda.bpm.application.impl.ServletProcessApplication;

/**
 * @author Saimir Bala
 *
 */
@ProcessApplication("Music Subscription Activation")
public class MusicSubscriptionActivation extends ServletProcessApplication {

	public MusicSubscriptionActivation() {
	}
}
