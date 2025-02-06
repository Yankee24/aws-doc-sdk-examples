// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0
package com.example.dynamodbasync;

// snippet-start:[dynamodb.java2.async_pagination.complete]
// snippet-start:[dynamodb.java2.async_pagination.import]
import io.reactivex.rxjava3.core.Flowable;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import reactor.core.publisher.Flux;
import software.amazon.awssdk.core.async.SdkPublisher;
import software.amazon.awssdk.services.dynamodb.DynamoDbAsyncClient;
import software.amazon.awssdk.services.dynamodb.model.ListTablesRequest;
import software.amazon.awssdk.services.dynamodb.model.ListTablesResponse;
import software.amazon.awssdk.services.dynamodb.paginators.ListTablesPublisher;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
// snippet-end:[dynamodb.java2.async_pagination.import]

// snippet-start:[dynamodb.java2.async_pagination.main]
public class AsyncPagination {

    public static void main(String[] args) throws InterruptedException, ExecutionException {

        final String USAGE = "\n" +
                "Usage:\n" +
                "    AsynPagination <type>\n\n" +
                "Where:\n" +
                "    type - the type of pagination. (auto, manual or default) \n\n" +
                "Example:\n" +
                "    AsynPagination auto\n";

        if (args.length < 1) {
            System.out.println(USAGE);
            System.exit(1);
        }

        String method = args[0];

        switch (method.toLowerCase()) {
            case "manual":
                ManualPagination();
                break;
            case "auto":
                AutoPagination();
                AutoPaginationOnCollection();
                useThirdPartySubscriber();
                useThirdPartySubscriber_Reactor();
                break;
            default:
                ManualPagination();
                AutoPagination();
                AutoPaginationOnCollection();
                useThirdPartySubscriber_Reactor();
        }
    }

    private static void ManualPagination() throws InterruptedException {
        System.out.println("running ManualPagination...\n");

        // Creates a default async client with credentials and regions loaded from the
        // environment
        DynamoDbAsyncClient client = DynamoDbAsyncClient.create();
        CompletableFuture<ListTablesResponse> response = client.listTables(ListTablesRequest.builder()
                .build());
        // Map the response to another CompletableFuture containing just the table names
        CompletableFuture<List<String>> tableNames = response.thenApply(ListTablesResponse::tableNames);
        // When future is complete (either successfully or in error) handle the response
        tableNames.whenComplete((tables, err) -> {
            if (tables != null) {
                tables.forEach(System.out::println);
            } else {
                // Handle error
                err.printStackTrace();
            }
        });

        Thread.sleep(3_000);
    }

    private static void AutoPagination() throws InterruptedException, ExecutionException {
        System.out.println("running AutoPagination...\n");

        // snippet-start:[dynamodb.java2.async_pagination.pagesclient]
        // Creates a default client with credentials and region loaded from the
        // environment.
        final DynamoDbAsyncClient asyncClient = DynamoDbAsyncClient.create();

        ListTablesRequest listTablesRequest = ListTablesRequest.builder().limit(3).build();
        ListTablesPublisher publisher = asyncClient.listTablesPaginator(listTablesRequest);
        // snippet-end:[dynamodb.java2.async_pagination.pagesclient]
        // snippet-start:[dynamodb.java2.async_pagination.pagesforeach]
        // Use a Consumer for simple use cases.
        CompletableFuture<Void> future = publisher.subscribe(
                response -> response.tableNames()
                        .forEach(System.out::println));
        // snippet-end:[dynamodb.java2.async_pagination.pagesforeach]

        future.get();

        // snippet-start:[dynamodb.java2.async_pagination.pagessubscribe]
        // A Subscription represents a one-to-one life-cycle of a Subscriber subscribing
        // to a Publisher.
        publisher.subscribe(new Subscriber<ListTablesResponse>() {
            // Maintain a reference to the subscription object, which is required to request
            // data from the publisher.
            private Subscription subscription;

            @Override
            public void onSubscribe(Subscription s) {
                subscription = s;
                // Request method should be called to demand data. Here we request a single
                // page.
                subscription.request(1);
            }

            @Override
            public void onNext(ListTablesResponse response) {
                response.tableNames().forEach(System.out::println);
                // After you process the current page, call the request method to signal that
                // you are ready for next page.
                subscription.request(1);
            }

            @Override
            public void onError(Throwable t) {
                // Called when an error has occurred while processing the requests.
            }

            @Override
            public void onComplete() {
                // This indicates all the results are delivered and there are no more pages
                // left.
            }
        });
        // snippet-end:[dynamodb.java2.async_pagination.pagessubscribe]

        // Since the code above is non-blocking, make sure your application doesn't end
        // immediately.
        // For this example, I am using Thread.sleep to wait for all pages to get
        // delivered.
        Thread.sleep(3_000);
    }

    private static void AutoPaginationOnCollection() throws InterruptedException, ExecutionException {
        System.out.println("running AutoPagination - iterating on item collection...\n");
        // snippet-start:[dynamodb.java2.async_pagination.asyncclient]
        // Create a default client with credentials and region loaded from the
        // environment.
        final DynamoDbAsyncClient asyncClient = DynamoDbAsyncClient.create();

        ListTablesRequest listTablesRequest = ListTablesRequest.builder().limit(3).build();
        ListTablesPublisher listTablesPublisher = asyncClient.listTablesPaginator(listTablesRequest);
        SdkPublisher<String> publisher = listTablesPublisher.tableNames();
        // snippet-end:[dynamodb.java2.async_pagination.asyncclient]

        // snippet-start:[dynamodb.java2.async_pagination.foreach]
        // Use a Consumer.
        CompletableFuture<Void> future = publisher.subscribe(System.out::println);
        future.get();
        // snippet-end:[dynamodb.java2.async_pagination.foreach]

        // snippet-start:[dynamodb.java2.async_pagination.subscriber]
        // Use a Subscriber.
        publisher.subscribe(new Subscriber<String>() {
            private Subscription subscription;

            @Override
            public void onSubscribe(Subscription s) {
                subscription = s;
                subscription.request(1);
            }

            @Override
            public void onNext(String tableName) {
                System.out.println(tableName);
                subscription.request(1);
            }

            @Override
            public void onError(Throwable t) {
            }

            @Override
            public void onComplete() {
            }
        });
        // snippet-end:[dynamodb.java2.async_pagination.subscriber]

        // Since the code above is non-blocking, make sure your application doesn't end
        // immediately.
        // For this example, I use Thread.sleep to wait for all pages to get delivered.
        Thread.sleep(3_000);
    }

    private static void useThirdPartySubscriber() {
        System.out.println("running AutoPagination - using third party subscriber...\n");
        // snippet-start:[dynamodb.java2.async_pagination.async]
        DynamoDbAsyncClient asyncClient = DynamoDbAsyncClient.create();
        ListTablesPublisher publisher = asyncClient.listTablesPaginator(ListTablesRequest.builder()
                .build());

        // The Flowable class has many helper methods that work with
        // an implementation of an org.reactivestreams.Publisher.
        List<String> tables = Flowable.fromPublisher(publisher)
                .flatMapIterable(ListTablesResponse::tableNames)
                .toList()
                .blockingGet();
        System.out.println(tables);
        // snippet-end:[dynamodb.java2.async_pagination.async]
    }

    private static void useThirdPartySubscriber_Reactor() {
        System.out.println("running AutoPagination - using third party subscriber...\n");

        DynamoDbAsyncClient asyncClient = DynamoDbAsyncClient.create();
        ListTablesPublisher publisher = asyncClient.listTablesPaginator(ListTablesRequest.builder()
                .build());

        // The Flux class has many helper methods that work with any reactive streams
        // compatible publisher implementation
        List<String> tables = Flux.from(publisher)
                .flatMapIterable(ListTablesResponse::tableNames)
                .collectList()
                .block();
        System.out.println(tables);
    }
}
// snippet-end:[dynamodb.java2.async_pagination.main]
// snippet-end:[dynamodb.java2.async_pagination.complete]
