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

/**
 * @author: Johannes Heinemann
 */
public class ProcessDefinitionCache extends ResourceDefinitionCache<ProcessDefinitionEntity> {


  public ProcessDefinitionCache(CacheFactory factory, int cacheCapacity, CacheDeployer cacheDeployer) {
    super(factory, cacheCapacity, cacheDeployer);
  }

  @Override
  protected CacheErrorChecker<ProcessDefinitionEntity> createErrorChecker() {
    return new ProcessCacheErrorChecker();
  }

  @Override
  protected DefinitionManagerFactory<ProcessDefinitionEntity> createDefinitionManagerFactory() {
    return new ProcessDefinitionManagerFactory();
  }

  public ProcessDefinitionEntity resolveDefinition(ProcessDefinitionEntity processDefinition) {
    ProcessDefinitionEntity entity = super.resolveDefinition(processDefinition);
    if (entity != null) {
      entity.updateModifiedFieldsFromEntity(processDefinition);
    }
    return entity;
  }
}
