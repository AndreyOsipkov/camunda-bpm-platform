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
package org.camunda.bpm.engine.impl.persistence.deploy.cache;

import org.camunda.bpm.engine.impl.persistence.entity.ProcessDefinitionEntity;

import static org.camunda.bpm.engine.impl.util.EnsureUtil.ensureNotNull;

/**
 * @author: Johannes Heinemann
 */
public class ProcessCacheErrorChecker implements CacheErrorChecker<ProcessDefinitionEntity> {

  @Override
  public void checkInvalidDefinitionId(String definitionId) {
    ensureNotNull("Invalid process definition id", "processDefinitionId", definitionId);
  }

  @Override
  public void checkDefinitionFound(String definitionId, ProcessDefinitionEntity definition) {
    ensureNotNull("no deployed process definition found with id '" + definitionId + "'", "processDefinition", definition);
  }

  @Override
  public void checkInvalidDefinitionByKey(String definitionKey, ProcessDefinitionEntity definition) {
    ensureNotNull("no processes deployed with key '" + definitionKey + "'", "processDefinition", definition);
  }

  @Override
  public void checkInvalidDefinitionByKeyAndTenantId(String definitionKey, String tenantId, ProcessDefinitionEntity definition) {
    ensureNotNull("no processes deployed with key '" + definitionKey + "' and tenant-id '" + tenantId + "'", "processDefinition", definition);
  }

  @Override
  public void checkInvalidDefinitionByKeyVersionAndTenantId(String definitionKey, Integer definitionVersion, String tenantId, ProcessDefinitionEntity definition) {
    ensureNotNull("no processes deployed with key = '" + definitionKey + "', version = '" + definitionVersion
        + "' and tenant-id = '" + tenantId + "'", "processDefinition", definition);
  }

  @Override
  public void checkInvalidDefinitionByDeploymentAndKey(String deploymentId, String definitionKey, ProcessDefinitionEntity definition) {
    ensureNotNull("no processes deployed with key = '" + definitionKey + "' in deployment = '" + deploymentId + "'", "processDefinition", definition);
  }

  @Override
  public void checkInvalidDefinitionWasCached(String deploymentId, String definitionId, ProcessDefinitionEntity definition) {
    ensureNotNull("deployment '" + deploymentId + "' didn't put process definition '" + definitionId + "' in the cache", "cachedProcessDefinition", definition);
  }
}
