// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package com.example.cloudfront;

// snippet-start:[cloudfront.java2.createbucketpolicy.import]
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutBucketPolicyResponse;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.net.URL;
// snippet-end:[cloudfront.java2.createbucketpolicy.import]

// snippet-start:[cloudfront.java2.createbucketpolicy.main]
public class CreateBucketPolicy {
    final private static Logger logger = LoggerFactory.getLogger(CreateBucketPolicy.class);

    public static String createBucketPolicy(String bucketName, String accountId, String distributionId) {

        URL resource = CreateBucketPolicy.class.getClassLoader().getResource("bucketAccessPolicy.json");
        String fileName = resource.getPath();

        String replacedPolicy;
        JSONParser parser = new JSONParser();
        try (Reader reader = new FileReader(fileName)) {

            JSONObject root = (JSONObject) parser.parse(reader);
            JSONArray statement = (JSONArray) root.get("Statement");
            JSONObject firstObject = (JSONObject) statement.get(0);
            firstObject.put("Resource", "arn:aws:s3:::" + bucketName + "/*");
            JSONObject condition = (JSONObject) firstObject.get("Condition");
            JSONObject stringEquals = (JSONObject) condition.get("StringEquals");
            stringEquals.put("AWS:SourceArn", "arn:aws:cloudfront::" + accountId + ":distribution/" + distributionId);
            replacedPolicy = root.toJSONString();

        } catch (IOException | ParseException e) {
            throw new RuntimeException(e);
        }
        logger.info("Bucket access police created");
        return replacedPolicy;
    }

    public static void uploadBucketPolicy(S3Client s3Client, String bucketName, String bucketAccessPolicy) {
        PutBucketPolicyResponse response = s3Client.putBucketPolicy(b -> b
                .bucket(bucketName)
                .policy(bucketAccessPolicy));

        if (response.sdkHttpResponse().isSuccessful()) {
            logger.info("Bucket access policy successfully uploaded");
        }
    }
    // snippet-end:[cloudfront.java2.createbucketpolicy.main]
}
