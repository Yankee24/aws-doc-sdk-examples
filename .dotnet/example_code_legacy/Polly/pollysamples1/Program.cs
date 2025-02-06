﻿// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0
﻿using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace PollySamples1
{
    class Program
    {
        static void Main(string[] args)
        {
            PutLexiconSample.PutLexicon();
            GetLexiconSample.GetLexicon();
            ListLexiconsSample.ListLexicons();
            DeleteLexiconSample.DeleteLexicon();
            DescribeVoicesSample.DescribeVoices();
            SynthesizeSpeechMarksSample.SynthesizeSpeechMarks();
            SynthesizeSpeechSample.SynthesizeSpeech();
        }
    }
}
