package group.artifact;

import java.awt.geom.Point2D;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import software.amazon.awssdk.awscore.client.builder.AwsClientBuilder;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.*;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.DynamoDbException;
import software.amazon.awssdk.services.dynamodb.model.GetItemRequest;
import software.amazon.awssdk.services.dynamodb.model.ListTablesRequest;
import software.amazon.awssdk.services.dynamodb.model.ListTablesResponse;
import software.amazon.awssdk.services.dynamodb.model.QueryRequest;
import software.amazon.awssdk.services.dynamodb.model.QueryResponse;

public class LocationFetcher {
	private DynamoDbClient ddb;
	private final static String TABLE_NAME = "new_table_with_distance";

	public LocationQueryResult getLocationofDevice(String mac) {
		HashMap<String, AttributeValue> key_to_get = new HashMap<String, AttributeValue>();
		key_to_get.put("macaddress", AttributeValue.builder().s(mac).build());
		GetItemRequest request = null;
		request = GetItemRequest.builder().key(key_to_get).tableName(TABLE_NAME).build();
		try {
			Map<String, AttributeValue> returned_item = ddb.getItem(request).item();
			System.out.println(returned_item.keySet());
			System.out.println(returned_item.get("node_1"));
			if (returned_item != null) {
				String locationX = returned_item.get("locationX").n();
				String locationY = returned_item.get("locationY").n();
				return new LocationQueryResult(mac,
						new Point2D.Float(Float.parseFloat(locationX), Float.parseFloat(locationY)));
			} else {
				System.out.format("No item found with the key %s!\n", mac);
				return null;
			}
		} catch (DynamoDbException e) {
			System.err.println(e.getMessage());
			return null;
		}
	}

	public LocationFetcher() {
		ddb = DynamoDbClient.create();
	}
}
