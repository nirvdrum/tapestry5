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

package org.apache.tapestry.beaneditor;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Used to attach validation constraints directly to a property (either the getter or the setter
 * method). The annotation value is a comma seperated list of <em>validation constraints</em>,
 * each one identifying a validator type (such as "required", "minlength") and optionally, a
 * constraint value. Most validators need a constraint value, which is seperated from the type by an
 * equals size (i.e., "maxlength=30").
 */
@Target(METHOD)
@Retention(RUNTIME)
@Documented
public @interface Validate {
    String value();
}
