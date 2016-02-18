/* Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.camunda.bpm.integrationtest.functional.context;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

import org.camunda.bpm.application.InvocationContext;
import org.camunda.bpm.engine.impl.util.ClockUtil;
import org.camunda.bpm.engine.runtime.EventSubscription;
import org.camunda.bpm.engine.runtime.Execution;
import org.camunda.bpm.engine.runtime.Job;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.integrationtest.functional.context.beans.NoOpJavaDelegate;
import org.camunda.bpm.integrationtest.functional.context.beans.SignalableTask;
import org.camunda.bpm.integrationtest.util.AbstractFoxPlatformIntegrationTest;
import org.camunda.bpm.integrationtest.util.TestContainer;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.OperateOnDeployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Checks if the process application is invoked with an invocation context.
 */
@RunWith(Arquillian.class)
public class InvocationContextTest extends AbstractFoxPlatformIntegrationTest {

  @Deployment(name = "pa")
  public static WebArchive createDeployment() {
    return ShrinkWrap.create(WebArchive.class, "pa.war")
        .addAsResource("META-INF/processes.xml")
        .addClass(AbstractFoxPlatformIntegrationTest.class)
        .addClass(TestContainer.class)
        .addClass(ProcessApplicationWithInvocationContext.class)
        .addClass(NoOpJavaDelegate.class)
        .addClass(SignalableTask.class)
        .addAsResource("org/camunda/bpm/integrationtest/functional/context/InvocationContextTest-timer.bpmn")
        .addAsResource("org/camunda/bpm/integrationtest/functional/context/InvocationContextTest-message.bpmn")
        .addAsResource("org/camunda/bpm/integrationtest/functional/context/InvocationContextTest-signalTask.bpmn");
  }

  @After
  public void cleanUp() {
    ClockUtil.reset();
  }

  @Test
  @OperateOnDeployment("pa")
  public void testInvokeProcessApplicationWithContextOnStart() {

    ProcessInstance pi = runtimeService.startProcessInstanceByKey("messageProcess");

    InvocationContext invocationContext = ProcessApplicationWithInvocationContext.getInvocationContext();
    assertThat(invocationContext, is(notNullValue()));
    assertThat(invocationContext.getExecution(), is(notNullValue()));
    assertThat(invocationContext.getExecution().getId(), is(pi.getId()));
  }

  @Test
  @OperateOnDeployment("pa")
  public void testInvokeProcessApplicationWithContextOnAsyncExecution() {

    runtimeService.startProcessInstanceByKey("timerProcess");
    ProcessApplicationWithInvocationContext.clearInvocationContext();

    Job timer = managementService.createJobQuery().timers().singleResult();
    assertThat(timer, is(notNullValue()));

    ClockUtil.setCurrentTime(timer.getDuedate());
    waitForJobExecutorToProcessAllJobs();

    InvocationContext invocationContext = ProcessApplicationWithInvocationContext.getInvocationContext();
    assertThat(invocationContext, is(notNullValue()));
    assertThat(invocationContext.getExecution(), is(notNullValue()));
    assertThat(invocationContext.getExecution().getId(), is(timer.getExecutionId()));
  }

  @Test
  @OperateOnDeployment("pa")
  public void testInvokeProcessApplicationWithContextOnMessageReceived() {

    ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("messageProcess");
    ProcessApplicationWithInvocationContext.clearInvocationContext();

    EventSubscription messageSubscription = runtimeService.createEventSubscriptionQuery().eventType("message").processInstanceId(processInstance.getId()).singleResult();
    assertThat(messageSubscription, is(notNullValue()));

    runtimeService.messageEventReceived(messageSubscription.getEventName(), messageSubscription.getExecutionId());

    InvocationContext invocationContext = ProcessApplicationWithInvocationContext.getInvocationContext();
    assertThat(invocationContext, is(notNullValue()));
    assertThat(invocationContext.getExecution(), is(notNullValue()));
    assertThat(invocationContext.getExecution().getId(), is(messageSubscription.getExecutionId()));
  }

  @Test
  @OperateOnDeployment("pa")
  public void testInvokeProcessApplicationWithContextOnSignalTask() {

    runtimeService.startProcessInstanceByKey("signalableProcess");
    ProcessApplicationWithInvocationContext.clearInvocationContext();

    Execution execution = runtimeService.createExecutionQuery().activityId("waitingTask").singleResult();
    assertThat(execution, is(notNullValue()));

    runtimeService.signal(execution.getId());

    InvocationContext invocationContext = ProcessApplicationWithInvocationContext.getInvocationContext();
    assertThat(invocationContext, is(notNullValue()));
    assertThat(invocationContext.getExecution(), is(notNullValue()));
    assertThat(invocationContext.getExecution().getId(), is(execution.getId()));
  }

}
