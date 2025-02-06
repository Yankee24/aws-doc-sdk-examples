// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0
 // Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0
#include <aws/core/Aws.h>
#include <aws/neptune/NeptuneClient.h>
#include <aws/neptune/model/CreateDBClusterRequest.h>
#include <aws/neptune/model/CreateDBClusterResult.h>
#include <iostream>

/**
 * Creates a Neptune db cluster based on command line input
 */

int main(int argc, char **argv)
{
  if (argc != 2)
  {
    std::cout << "Usage: create_db_cluster <db_cluster_identifier>";
    return 1;
  }
  Aws::SDKOptions options;
  Aws::InitAPI(options);
  {
    Aws::String db_cluster_identifier(argv[1]);
    Aws::Neptune::NeptuneClient neptune;

    Aws::Neptune::Model::CreateDBClusterRequest cdbc_req;
    cdbc_req.SetDBClusterIdentifier(db_cluster_identifier);
    cdbc_req.SetEngine("neptune");

    auto cdbc_out = neptune.CreateDBCluster(cdbc_req);

    if (cdbc_out.IsSuccess())
    {
      std::cout << "Successfully created neptune db cluster " << std::endl;
    }

    else
    {
      std::cout << "Error creating neptune db cluster " << cdbc_out.GetError().GetMessage()
        << std::endl;
    }
  }

  Aws::ShutdownAPI(options);
  return 0;
}
