// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package main

import (
	"github.com/aws/aws-sdk-go/aws"
	"github.com/aws/aws-sdk-go/aws/session"
	"github.com/aws/aws-sdk-go/service/cloudwatch"

	"fmt"
	"os"
)

func main() {
	if len(os.Args) != 4 {
		fmt.Println("You must supply an instance name, value, and alarm name")
		os.Exit(1)
	}

	instance := os.Args[1]
	value := os.Args[2]
	name := os.Args[3]

	// Initialize a session that the SDK uses to load
	// credentials from the shared credentials file ~/.aws/credentials
	// and configuration from the shared configuration file ~/.aws/config.
	sess := session.Must(session.NewSessionWithOptions(session.Options{
		SharedConfigState: session.SharedConfigEnable,
	}))

	// Create new CloudWatch client.
	svc := cloudwatch.New(sess)

	// Create a metric alarm that reboots an instance if its CPU utilization is
	// greater than 70.0%.
	_, err := svc.PutMetricAlarm(&cloudwatch.PutMetricAlarmInput{
		AlarmName:          aws.String(name),
		ComparisonOperator: aws.String(cloudwatch.ComparisonOperatorGreaterThanThreshold),
		EvaluationPeriods:  aws.Int64(1),
		MetricName:         aws.String("CPUUtilization"),
		Namespace:          aws.String("AWS/EC2"),
		Period:             aws.Int64(60),
		Statistic:          aws.String(cloudwatch.StatisticAverage),
		Threshold:          aws.Float64(70.0),
		ActionsEnabled:     aws.Bool(true),
		AlarmDescription:   aws.String("Alarm when server CPU exceeds 70%"),
		Unit:               aws.String(cloudwatch.StandardUnitSeconds),

		// This is apart of the default workflow actions. This one will reboot the instance, if the
		// alarm is triggered.
		AlarmActions: []*string{
			aws.String(fmt.Sprintf("arn:aws:swf:us-east-1:%s:action/actions/AWS_EC2.InstanceId.Reboot/1.0", instance)),
		},
		Dimensions: []*cloudwatch.Dimension{
			{
				Name:  aws.String("InstanceId"),
				Value: aws.String(value),
			},
		},
	})
	if err != nil {
		fmt.Println("Error", err)
		return
	}

	// This will enable the alarm to our instance.
	result, err := svc.EnableAlarmActions(&cloudwatch.EnableAlarmActionsInput{
		AlarmNames: []*string{
			aws.String(name),
		},
	})
	if err != nil {
		fmt.Println("Error", err)
		return
	}

	fmt.Println("Alarm action enabled", result)
}
