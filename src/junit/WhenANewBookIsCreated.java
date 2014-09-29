package junit;


import static com.lordofthejars.nosqlunit.mongodb.MongoDbRule.MongoDbRuleBuilder.newMongoDbRule;
import mongo.MongoDBManager;
import mongo.WeiboDao;
import mongo.model.Weibo;

import org.junit.Rule;
import org.junit.Test;

import com.lordofthejars.nosqlunit.annotation.ShouldMatchDataSet;
import com.lordofthejars.nosqlunit.annotation.UsingDataSet;
import com.lordofthejars.nosqlunit.core.LoadStrategyEnum;
import com.lordofthejars.nosqlunit.mongodb.ManagedMongoDb;
import com.lordofthejars.nosqlunit.mongodb.MongoDbRule;

public class WhenANewBookIsCreated {


	@Rule
    public MongoDbRule mongoDbRule = newMongoDbRule().defaultManagedMongoDb("test");

	@Test
	//@UsingDataSet(locations="initialData.json", loadStrategy=LoadStrategyEnum.CLEAN_INSERT)
	@ShouldMatchDataSet(location="expectedData.json")
	public void book_should_be_inserted_into_repository() {
		WeiboDao wdao = new WeiboDao("test");
		Weibo weibo = new Weibo("unitNosql",1);
		wdao.save(weibo.toDBObject());
	}

}
