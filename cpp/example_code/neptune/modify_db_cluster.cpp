// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0
 // Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0
#include <aws/core/Aws.h>
#include <aws/neptune/NeptuneClient.h>
#include <aws/neptune/model/ModifyDBClusterRequest.h>
#include <aws/neptune/model/ModifyDBClusterResult.h>
#include <iostream>

/**
 * Modifies a Neptune db cluster based on command line input
 */

int main(int argc, char **argv)
{
  if (argc != 2)
  {
    std::cout << "Usage: modify_db_cluster <db_cluster_identifier>";
    return 1;
  }
  Aws::SDKOptions options;
  Aws::InitAPI(options);
  {
    Aws::String db_cluster_identifier(argv[1]);
    Aws::Neptune::NeptuneClient neptune;

    Aws::Neptune::Model::ModifyDBClusterRequest mdbc_req;
    mdbc_req.SetDBClusterIdentifier(db_cluster_identifier);

    auto mdbc_out = neptune.ModifyDBCluster(mdbc_req);

    if (mdbc_out.IsSuccess())
    {
      std::cout << "Successfully modified neptune db cluster " << std::endl;
    }

    else
    {
      std::cout << "Error modifying neptune db cluster " << mdbc_out.GetError().GetMessage()
        << std::endl;
    }
  }

  Aws::ShutdownAPI(options);
  return 0;
}
