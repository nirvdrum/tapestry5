// Copyright 2006, 2007 The Apache Software Foundation
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

package org.apache.tapestry.internal.services;

/**
 * It represents a component invocation target for a page link. It is passed to an
 * {@link org.apache.tapestry.internal.services.ActionLinkHandler} by both the
 * {@link org.apache.tapestry.test.pagelevel.PageTester} and the real Tapestry code
 * {@link org.apache.tapestry.internal.services.PageRenderDispatcher} in order to invoke a page
 * link.
 */
public class PageLinkTarget implements InvocationTarget
{

    private final String _pageName;

    public PageLinkTarget(String pageName)
    {
        _pageName = pageName;

    }

    public String getPath()
    {
        return _pageName.toLowerCase();
    }

    public String getPageName()
    {
        return _pageName;
    }

}
