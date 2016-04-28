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

package org.camunda.bpm.engine.test.api.identity;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

import java.util.List;

import org.camunda.bpm.engine.IdentityService;
import org.camunda.bpm.engine.identity.Tenant;
import org.camunda.bpm.engine.identity.TenantQuery;
import org.camunda.bpm.engine.test.ProcessEngineRule;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

public class TenantQueryTest {

  @Rule
  public ProcessEngineRule engineRule = new ProcessEngineRule(true);

  protected IdentityService identityService;

  @Before
  public void setUp() {
    identityService = engineRule.getIdentityService();

    createTenant("tenant1", "Tenant 1");
    createTenant("tenant2", "Tenant 2");
  }

  @Test
  public void queryById() {
    TenantQuery query = identityService.createTenantQuery().tenantId("tenant1");

    assertThat(query.count(), is(1L));
    assertThat(query.list().size(), is(1));

    Tenant tenant = query.singleResult();
    assertThat(tenant, is(notNullValue()));
    assertThat(tenant.getName(), is("Tenant 1"));
  }

  @Test
  public void queryByNonExistingId() {
    TenantQuery query = identityService.createTenantQuery().tenantId("nonExisting");

    assertThat(query.count(), is(0L));
  }

  @Test
  public void queryByIdIn() {
    TenantQuery query = identityService.createTenantQuery();

    assertThat(query.tenantIdIn("non", "existing").count(), is(0L));
    assertThat(query.tenantIdIn("tenant1", "tenant2").count(), is(2L));
  }

  @Test
  public void queryByName() {
    TenantQuery query = identityService.createTenantQuery();

    assertThat(query.tenantName("nonExisting").count(), is(0L));
    assertThat(query.tenantName("Tenant 1").count(), is(1L));
    assertThat(query.tenantName("Tenant 2").count(), is(1L));
  }

  @Test
  public void queryByNameLike() {
    TenantQuery query = identityService.createTenantQuery();

    assertThat(query.tenantNameLike("%nonExisting%").count(), is(0L));
    assertThat(query.tenantNameLike("%Tenant 1%").count(), is(1L));
    assertThat(query.tenantNameLike("%Tenant%").count(), is(2L));
  }

  @Test
  public void queryOrderById() {
    // ascending
    List<Tenant> tenants = identityService.createTenantQuery().orderByTenantId().asc().list();
    assertThat(tenants.size(), is(2));

    assertThat(tenants.get(0).getId(), is("tenant1"));
    assertThat(tenants.get(1).getId(), is("tenant2"));

    // descending
    tenants = identityService.createTenantQuery().orderByTenantId().desc().list();

    assertThat(tenants.get(0).getId(), is("tenant2"));
    assertThat(tenants.get(1).getId(), is("tenant1"));
  }

  @Test
  public void queryOrderByName() {
    // ascending
    List<Tenant> tenants = identityService.createTenantQuery().orderByTenantName().asc().list();
    assertThat(tenants.size(), is(2));

    assertThat(tenants.get(0).getName(), is("Tenant 1"));
    assertThat(tenants.get(1).getName(), is("Tenant 2"));

    // descending
    tenants = identityService.createTenantQuery().orderByTenantName().desc().list();

    assertThat(tenants.get(0).getName(), is("Tenant 2"));
    assertThat(tenants.get(1).getName(), is("Tenant 1"));
  }

  protected Tenant createTenant(String id, String name) {
    Tenant tenant = engineRule.getIdentityService().newTenant(id);
    tenant.setName(name);
    identityService.saveTenant(tenant);

    return tenant;
  }

  @After
  public void tearDown() throws Exception {
    identityService.deleteTenant("tenant1");
    identityService.deleteTenant("tenant2");
  }

}