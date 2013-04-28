package com.evolveum.midpoint.model.sync;

import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertNotNull;
import static com.evolveum.midpoint.test.IntegrationTestTools.displayTestTile;
import java.io.File;
import java.util.Collection;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ContextConfiguration;
import org.testng.annotations.Test;

import com.evolveum.midpoint.model.AbstractInternalModelIntegrationTest;
import com.evolveum.midpoint.model.test.AbstractModelIntegrationTest;
import com.evolveum.midpoint.prism.PrismObject;
import com.evolveum.midpoint.prism.delta.ItemDelta;
import com.evolveum.midpoint.prism.delta.PropertyDelta;
import com.evolveum.midpoint.prism.util.PrismTestUtil;
import com.evolveum.midpoint.repo.api.RepositoryService;
import com.evolveum.midpoint.schema.result.OperationResult;
import com.evolveum.midpoint.task.api.Task;
import com.evolveum.midpoint.xml.ns._public.common.common_2a.ResourceType;
import com.evolveum.midpoint.xml.ns._public.common.common_2a.ShadowType;
import com.evolveum.midpoint.xml.ns._public.common.common_2a.UserType;
import com.evolveum.prism.xml.ns._public.query_2.QueryType;

@ContextConfiguration(locations = {"classpath:ctx-model-test-main.xml"})
@DirtiesContext(classMode = ClassMode.AFTER_CLASS)
public class TestCorrelationConfiramtionEvaluator extends AbstractInternalModelIntegrationTest{
  
	private static final String TEST_DIR = "src/test/resources/sync";
	private static final String CORRELATION_OR_FILTER = TEST_DIR + "/correlation-or-filter.xml";
	
	@Autowired(required=true)
	private RepositoryService repositoryService;
	
	@Autowired(required = true)
	private CorrelationConfirmationEvaluator evaluator;
	
	

	@Override
	public void initSystem(Task initTask, OperationResult initResult) throws Exception {
		
	}
	
	@Test
	public void testCorrelationOrFilter() throws Exception{
		String TEST_NAME = "testCorrelationOrFilter";
		displayTestTile(this, TEST_NAME);
		
		Task task = taskManager.createTaskInstance(TEST_NAME);
		OperationResult result = task.getResult();
		
		importObjectFromFile(USER_JACK_FILENAME);
			
		PrismObject<UserType> userType = repositoryService.getObject(UserType.class, USER_JACK_OID, result);
		//assert jack
		assertNotNull(userType);
			
		ShadowType shadow = parseObjectType(new File(ACCOUNT_SHADOW_JACK_DUMMY_FILENAME), ShadowType.class);
		
		QueryType query = PrismTestUtil.unmarshalObject(new File(CORRELATION_OR_FILTER), QueryType.class);
		
		ResourceType resourceType = parseObjectType(new File(RESOURCE_DUMMY_FILENAME), ResourceType.class);
		List<PrismObject<UserType>> matchedUsers = evaluator.findUsersByCorrelationRule(shadow, query, resourceType, result);
		
		assertNotNull("Correlation evaluator returned null collection of matched users.", matchedUsers);
		assertEquals("Found more than one user.", 1, matchedUsers.size());
		
		PrismObject<UserType> jack = matchedUsers.get(0);
		assertUser(jack, "c0c010c0-d34d-b33f-f00d-111111111111", "jack", "Jack Sparrow", "Jack", "Sparrow");
		
	}
}