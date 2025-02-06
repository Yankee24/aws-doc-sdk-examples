// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

#include <aws/core/Aws.h>
#include <aws/core/utils/Outcome.h>
#include <aws/codecommit/CodeCommitClient.h>
#include <aws/codecommit/model/DeleteBranchRequest.h>
#include <aws/codecommit/model/DeleteBranchResult.h>
#include <aws/codecommit/model/RepositoryMetadata.h>
#include <iostream>

/**
 * Deletes a branch of repository based on command line inputs
 */

int main(int argc, char ** argv)
{
  if (argc != 3)
  {
    std::cout << "Usage: delete_branch <repository_name> <branch_name>"
              << std::endl;
    return 1;
  }

  Aws::SDKOptions options;
  Aws::InitAPI(options);
  {
    Aws::String repository_name(argv[1]);
    Aws::String branch_name(argv[2]);

    Aws::CodeCommit::CodeCommitClient codecommit;

    Aws::CodeCommit::Model::DeleteBranchRequest db_req;

    db_req.SetRepositoryName(repository_name);
    db_req.SetBranchName(branch_name);

    auto db_out = codecommit.DeleteBranch(db_req);

    if (db_out.IsSuccess())
    {
      std::cout << "Successfully deleted branch from repository" << std::endl;
    }
    else
    {
      std::cout << "Error deleting branch" << db_out.GetError().GetMessage()
                << std::endl;
    }
  }

  Aws::ShutdownAPI(options);
  return 0;
}
