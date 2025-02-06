// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0
package com.example.pinpoint;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.pinpoint.AmazonPinpoint;
import com.amazonaws.services.pinpoint.AmazonPinpointClientBuilder;
import com.amazonaws.services.pinpoint.model.CreateSegmentRequest;
import com.amazonaws.services.pinpoint.model.CreateSegmentResult;
import com.amazonaws.services.pinpoint.model.AttributeDimension;
import com.amazonaws.services.pinpoint.model.AttributeType;
import com.amazonaws.services.pinpoint.model.RecencyDimension;
import com.amazonaws.services.pinpoint.model.SegmentBehaviors;
import com.amazonaws.services.pinpoint.model.SegmentDemographics;
import com.amazonaws.services.pinpoint.model.SegmentDimensions;
import com.amazonaws.services.pinpoint.model.SegmentLocation;
import com.amazonaws.services.pinpoint.model.SegmentResponse;
import com.amazonaws.services.pinpoint.model.WriteSegmentRequest;

import java.util.HashMap;
import java.util.Map;

public class CreateSegment {

        public static void main(String[] args) {
                final String USAGE = "\n" +
                                "CreateSegment - create a segment \n\n" +
                                "Usage: CreateSegment <appId>\n\n" +
                                "Where:\n" +
                                "  appId - the ID the application to create a segment for.\n\n";

                if (args.length < 1) {
                        System.out.println(USAGE);
                        System.exit(1);
                }
                String appId = args[0];

                AmazonPinpoint pinpoint = AmazonPinpointClientBuilder.standard().withRegion(Regions.US_EAST_1).build();

                SegmentResponse result = createSegment(pinpoint, appId);
                System.out.println("Segment " + result.getName() + " created.");
                System.out.println(result.getSegmentType());
        }

        public static SegmentResponse createSegment(AmazonPinpoint client, String appId) {
                Map<String, AttributeDimension> segmentAttributes = new HashMap<>();
                segmentAttributes.put("Team", new AttributeDimension().withAttributeType(AttributeType.INCLUSIVE)
                                .withValues("Lakers"));

                SegmentBehaviors segmentBehaviors = new SegmentBehaviors();
                SegmentDemographics segmentDemographics = new SegmentDemographics();
                SegmentLocation segmentLocation = new SegmentLocation();

                RecencyDimension recencyDimension = new RecencyDimension();
                recencyDimension.withDuration("DAY_30").withRecencyType("ACTIVE");
                segmentBehaviors.setRecency(recencyDimension);

                SegmentDimensions dimensions = new SegmentDimensions()
                                .withAttributes(segmentAttributes)
                                .withBehavior(segmentBehaviors)
                                .withDemographic(segmentDemographics)
                                .withLocation(segmentLocation);

                WriteSegmentRequest writeSegmentRequest = new WriteSegmentRequest()
                                .withName("MySegment").withDimensions(dimensions);

                CreateSegmentRequest createSegmentRequest = new CreateSegmentRequest()
                                .withApplicationId(appId).withWriteSegmentRequest(writeSegmentRequest);

                CreateSegmentResult createSegmentResult = client.createSegment(createSegmentRequest);

                System.out.println("Segment ID: " + createSegmentResult.getSegmentResponse().getId());

                return createSegmentResult.getSegmentResponse();
        }

}
