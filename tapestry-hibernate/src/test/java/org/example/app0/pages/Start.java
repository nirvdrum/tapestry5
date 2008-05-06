// Copyright 2007 The Apache Software Foundation
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package org.example.app0.pages;

import org.apache.tapestry.annotations.InjectPage;
import org.apache.tapestry.ioc.annotations.Inject;
import org.example.app0.entities.User;
import org.example.app0.services.UserDAO;

public class Start
{

    @InjectPage
    private CommitAfterDemo commitAfterDemo;

    @Inject
    private UserDAO userDAO;

    Object onActionFromCommitAfter()
    {
        User user = new User();

        user.setFirstName("Diane");

        userDAO.add(user);

        commitAfterDemo.setUser(user);

        return commitAfterDemo;
    }
}
