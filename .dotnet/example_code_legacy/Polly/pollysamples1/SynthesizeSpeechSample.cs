﻿// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0
﻿using System;
using System.Collections.Generic;
using System.IO;
using Amazon.Polly;
using Amazon.Polly.Model;

namespace PollySamples1
{
    class SynthesizeSpeechSample
    {
        public static void SynthesizeSpeech()
        {
            var client = new AmazonPollyClient();
            String outputFileName = "speech.mp3";

            var synthesizeSpeechRequest = new SynthesizeSpeechRequest()
            {
                OutputFormat = OutputFormat.Mp3,
                VoiceId = VoiceId.Joanna,
                Text = "This is a sample text to be synthesized."
            };

            try
            {
                using (var outputStream = new FileStream(outputFileName, FileMode.Create, FileAccess.Write))
                {
                    var synthesizeSpeechResponse = client.SynthesizeSpeech(synthesizeSpeechRequest);
                    byte[] buffer = new byte[2 * 1024];
                    int readBytes;

                    var inputStream = synthesizeSpeechResponse.AudioStream;
                    while ((readBytes = inputStream.Read(buffer, 0, 2 * 1024)) > 0)
                        outputStream.Write(buffer, 0, readBytes);
                }
            }
            catch (Exception e)
            {
                Console.WriteLine("Exception caught: " + e.Message);
            }
        }
    }
}
