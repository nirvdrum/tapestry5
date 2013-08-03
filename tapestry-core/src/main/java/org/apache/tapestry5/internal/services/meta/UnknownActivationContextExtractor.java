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

package org.apache.tapestry5.internal.services.meta;

import org.apache.tapestry5.MetaDataConstants;
import org.apache.tapestry5.annotations.ContentType;
import org.apache.tapestry5.annotations.UnknownActivationContextCheck;
import org.apache.tapestry5.model.MutableComponentModel;
import org.apache.tapestry5.services.meta.MetaDataExtractor;

public class UnknownActivationContextExtractor implements MetaDataExtractor<UnknownActivationContextCheck>
{
    public void extractMetaData(MutableComponentModel model, UnknownActivationContextCheck annotation)
    {
		model.setMeta(MetaDataConstants.UNKNOWN_ACTIVATION_CONTEXT_CHECK, Boolean.toString(annotation.value()));
    }
}
