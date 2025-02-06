// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0
package aws.example.mediastore;

import com.amazonaws.client.builder.AwsClientBuilder.EndpointConfiguration;
import com.amazonaws.regions.DefaultAwsRegionProviderChain;
import com.amazonaws.services.mediastore.AWSMediaStore;
import com.amazonaws.services.mediastore.AWSMediaStoreClientBuilder;
import com.amazonaws.services.mediastore.model.DescribeContainerRequest;
import com.amazonaws.services.mediastore.model.DescribeContainerResult;
import com.amazonaws.services.mediastore.model.AWSMediaStoreException;
import com.amazonaws.services.mediastoredata.AWSMediaStoreData;
import com.amazonaws.services.mediastoredata.AWSMediaStoreDataClientBuilder;
import com.amazonaws.services.mediastoredata.model.Item;
import com.amazonaws.services.mediastoredata.model.ListItemsRequest;
import com.amazonaws.services.mediastoredata.model.ListItemsResult;
import java.util.List;

/**
 * List objects and folders within an AWS Elemental MediaStore container.
 *
 * This code expects that you have AWS credentials set up per:
 * http://docs.aws.amazon.com/java-sdk/latest/developer-guide/setup-credentials.html
 */
public class ListItems {
    public static void main(String[] args) {
        final String USAGE = "\n" +
                "To run this example, supply the name of a container and an optional path!\n" +
                "\n" +
                "Ex: ListItems <container-name> [path]\n";

        if (args.length < 1) {
            System.out.println(USAGE);
            System.exit(1);
        }

        final String containerName = args[0];
        String path = "";
        if (args.length > 1) {
            path = args[1];
        }

        System.out.format("Objects in MediaStore container %s, path '%s':\n", containerName, path);

        final String endpoint = getContainerEndpoint(containerName);
        if (endpoint == null || endpoint.isEmpty()) {
            System.err.println("Could not determine container endpoint!");
            System.exit(1);
        }

        final String region = new DefaultAwsRegionProviderChain().getRegion();
        final EndpointConfiguration endpointConfig = new EndpointConfiguration(endpoint, region);

        final AWSMediaStoreData mediastoredata = AWSMediaStoreDataClientBuilder
                .standard()
                .withEndpointConfiguration(endpointConfig)
                .build();
        final ListItemsRequest request = new ListItemsRequest()
                .withPath(path);

        ListItemsResult result = mediastoredata.listItems(request);
        List<Item> items = result.getItems();
        for (Item i : items) {
            System.out.printf("* (%s)\t%s\n", i.getType(), i.getName());
        }
    }

    public static String getContainerEndpoint(String name) {
        final AWSMediaStore mediastore = AWSMediaStoreClientBuilder.defaultClient();
        final DescribeContainerRequest request = new DescribeContainerRequest()
                .withContainerName(name.trim());
        try {
            final DescribeContainerResult result = mediastore.describeContainer(request);
            return result.getContainer().getEndpoint();
        } catch (AWSMediaStoreException e) {
            System.err.println(e.getErrorMessage());
        }
        return null;
    }
}
