﻿// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0
﻿using System;
using Amazon.Polly;
using Amazon.Polly.Model;

namespace PollySamples1
{
    class ListLexiconsSample
    {
        public static void ListLexicons()
        {
            var client = new AmazonPollyClient();

            var listLexiconsRequest = new ListLexiconsRequest();

            try
            {
                String nextToken;
                do
                {
                    var listLexiconsResponse = client.ListLexicons(listLexiconsRequest);
                    nextToken = listLexiconsResponse.NextToken;
                    listLexiconsResponse.NextToken = nextToken;

                    Console.WriteLine("All voices: ");
                    foreach (var lexiconDescription in listLexiconsResponse.Lexicons)
                    {
                        var attributes = lexiconDescription.Attributes;
                        Console.WriteLine("Name: " + lexiconDescription.Name
                            + ", Alphabet: " + attributes.Alphabet
                            + ", LanguageCode: " + attributes.LanguageCode
                            + ", LastModified: " + attributes.LastModified
                            + ", LexemesCount: " + attributes.LexemesCount
                            + ", LexiconArn: " + attributes.LexiconArn
                            + ", Size: " + attributes.Size);
                    }
                } while (nextToken != null);
            }
            catch (Exception e)
            {
                Console.WriteLine("Exception caught: " + e.Message);
            }
        }
    }
}
