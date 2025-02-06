// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0
 // Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0
#include <aws/core/Aws.h>
#include <aws/neptune/NeptuneClient.h>
#include <aws/neptune/model/DeleteDBClusterRequest.h>
#include <aws/neptune/model/DeleteDBClusterResult.h>
#include <iostream>

/**
 * Deletes a Neptune db cluster based on command line input
 */

int main(int argc, char **argv)
{
  if (argc != 2)
  {
    std::cout << "Usage: delete_db_cluster <db_cluster_identifier>";
    return 1;
  }
  Aws::SDKOptions options;
  Aws::InitAPI(options);
  {
    Aws::String db_cluster_identifier(argv[1]);
    Aws::Neptune::NeptuneClient neptune;

    Aws::Neptune::Model::DeleteDBClusterRequest ddbc_req;
    ddbc_req.SetDBClusterIdentifier(db_cluster_identifier);

    auto ddbc_out = neptune.DeleteDBCluster(ddbc_req);

    if (ddbc_out.IsSuccess())
    {
      std::cout << "Successfully deleted neptune db cluster " << std::endl;
    }

    else
    {
      std::cout << "Error deleting neptune db cluster " << ddbc_out.GetError().GetMessage()
        << std::endl;
    }
  }

  Aws::ShutdownAPI(options);
  return 0;
}
