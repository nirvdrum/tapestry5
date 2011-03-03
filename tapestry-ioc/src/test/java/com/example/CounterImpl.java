// Copyright 2010 The Apache Software Foundation
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.example;

import org.apache.tapestry5.ioc.ReloadAware;

public class CounterImpl implements Counter, ReloadAware
{
    {
        ReloadAwareModule.counterInstantiations++;
    }

    private int count;

    public boolean shutdownImplementationForReload()
    {
        ReloadAwareModule.counterReloads++;

        return true;
    }

    public synchronized int increment()
    {
        return ++count;
    }

}