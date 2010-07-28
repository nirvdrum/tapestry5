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

package org.apache.tapestry5.internal.services.ajax;

import java.util.Collections;
import java.util.List;

import org.apache.tapestry5.Asset;
import org.apache.tapestry5.ComponentResources;
import org.apache.tapestry5.internal.InternalConstants;
import org.apache.tapestry5.internal.services.DocumentLinker;
import org.apache.tapestry5.internal.services.javascript.JSStackPathConstructor;
import org.apache.tapestry5.internal.test.InternalBaseTestCase;
import org.apache.tapestry5.ioc.internal.util.CollectionFactory;
import org.apache.tapestry5.ioc.util.IdAllocator;
import org.apache.tapestry5.json.JSONArray;
import org.apache.tapestry5.json.JSONObject;
import org.apache.tapestry5.services.javascript.InitializationPriority;
import org.apache.tapestry5.services.javascript.JSStack;
import org.apache.tapestry5.services.javascript.JSStackSource;
import org.apache.tapestry5.services.javascript.JSSupport;
import org.apache.tapestry5.services.javascript.StylesheetLink;
import org.apache.tapestry5.services.javascript.StylesheetOptions;
import org.testng.annotations.Test;

public class JSSupportImplTest extends InternalBaseTestCase
{
    @Test
    public void allocate_id_from_resources()
    {
        ComponentResources resources = mockComponentResources();

        train_getId(resources, "tracy");

        replay();

        JSSupport jss = new JSSupportImpl(null, null, null);

        assertEquals(jss.allocateClientId(resources), "tracy");
        assertEquals(jss.allocateClientId(resources), "tracy_0");
        assertEquals(jss.allocateClientId(resources), "tracy_1");

        verify();
    }

    @Test
    public void commit_with_no_javascript()
    {
        JSSupportImpl jss = new JSSupportImpl(null, null, null);

        jss.commit();
    }

    @Test
    public void no_stack_or_dom_loading_callback_in_partial_mode()
    {
        DocumentLinker linker = mockDocumentLinker();

        linker.addScript(InitializationPriority.NORMAL, "doSomething();");

        replay();

        JSSupportImpl jss = new JSSupportImpl(linker, null, null, new IdAllocator(), true);

        jss.addScript("doSomething();");

        jss.commit();

        verify();
    }

    @Test
    public void adding_script_will_add_stack()
    {
        DocumentLinker linker = mockDocumentLinker();
        JSStackSource stackSource = mockJavascriptStackSource();
        JSStackPathConstructor pathConstructor = mockJavascriptStackPathConstructor();
        trainForCoreStack(linker, stackSource, pathConstructor);

        linker.addScript(InitializationPriority.IMMEDIATE, "stackInit();");
        linker.addScript(InitializationPriority.NORMAL, "doSomething();");

        replay();

        JSSupportImpl jss = new JSSupportImpl(linker, stackSource, pathConstructor);

        jss.addScript("doSomething();");

        jss.commit();

        verify();
    }

    private void trainForEmptyCoreStack(DocumentLinker linker, JSStackSource stackSource,
            JSStackPathConstructor pathConstructor)
    {
        JSStack stack = mockJavascriptStack();

        List<String> libraryPaths = Collections.emptyList();
        List<StylesheetLink> stylesheets = Collections.emptyList();

        expect(stackSource.getStack(InternalConstants.CORE_STACK_NAME)).andReturn(stack);
        expect(pathConstructor.constructPathsForJavaScriptStack(InternalConstants.CORE_STACK_NAME)).andReturn(
                libraryPaths);
        expect(stack.getStylesheets()).andReturn(stylesheets);

        expect(stack.getInitialization()).andReturn(null);
    }

    private void trainForCoreStack(DocumentLinker linker, JSStackSource stackSource,
            JSStackPathConstructor pathConstructor)
    {
        JSStack stack = mockJavascriptStack();

        StylesheetLink stylesheetLink = new StylesheetLink("style.css");

        expect(stackSource.getStack(InternalConstants.CORE_STACK_NAME)).andReturn(stack);
        expect(pathConstructor.constructPathsForJavaScriptStack(InternalConstants.CORE_STACK_NAME)).andReturn(
                CollectionFactory.newList("stack1.js", "stack2.js"));
        expect(stack.getStylesheets()).andReturn(CollectionFactory.newList(stylesheetLink));

        expect(stack.getInitialization()).andReturn("stackInit();");

        linker.addScriptLink("stack1.js");
        linker.addScriptLink("stack2.js");
        linker.addStylesheetLink(stylesheetLink);
    }

    protected final JSStack mockJavascriptStack()
    {
        return newMock(JSStack.class);
    }

    protected final JSStackPathConstructor mockJavascriptStackPathConstructor()
    {
        return newMock(JSStackPathConstructor.class);
    }

    protected final JSStackSource mockJavascriptStackSource()
    {
        return newMock(JSStackSource.class);
    }

    @Test
    public void add_script_passes_thru_to_document_linker()
    {
        DocumentLinker linker = mockDocumentLinker();
        JSStackSource stackSource = mockJavascriptStackSource();
        JSStackPathConstructor pathConstructor = mockJavascriptStackPathConstructor();
        trainForEmptyCoreStack(linker, stackSource, pathConstructor);

        linker.addScript(InitializationPriority.IMMEDIATE, "doSomething();");

        replay();

        JSSupportImpl jss = new JSSupportImpl(linker, stackSource, pathConstructor);

        jss.addScript(InitializationPriority.IMMEDIATE, "doSomething();");

        verify();
    }

    @Test
    public void import_library()
    {
        DocumentLinker linker = mockDocumentLinker();
        JSStackSource stackSource = mockJavascriptStackSource();
        JSStackPathConstructor pathConstructor = mockJavascriptStackPathConstructor();
        trainForEmptyCoreStack(linker, stackSource, pathConstructor);

        Asset library = mockAsset("mylib.js");

        linker.addScriptLink("mylib.js");

        replay();

        JSSupportImpl jss = new JSSupportImpl(linker, stackSource, pathConstructor);

        jss.importJavascriptLibrary(library);

        jss.commit();

        verify();
    }

    @Test
    public void import_stack()
    {
        DocumentLinker linker = mockDocumentLinker();
        JSStackSource stackSource = mockJavascriptStackSource();
        JSStackPathConstructor pathConstructor = mockJavascriptStackPathConstructor();

        trainForCoreStack(linker, stackSource, pathConstructor);

        JSStack stack = mockJavascriptStack();

        StylesheetLink stylesheetLink = new StylesheetLink("stack.css");

        expect(stackSource.getStack("custom")).andReturn(stack);
        expect(pathConstructor.constructPathsForJavaScriptStack("custom")).andReturn(
                CollectionFactory.newList("stack.js"));
        expect(stack.getStylesheets()).andReturn(CollectionFactory.newList(stylesheetLink));

        expect(stack.getInitialization()).andReturn("customInit();");

        linker.addScriptLink("stack.js");
        linker.addStylesheetLink(stylesheetLink);

        linker.addScript(InitializationPriority.IMMEDIATE, "stackInit();");
        linker.addScript(InitializationPriority.IMMEDIATE, "customInit();");

        replay();

        JSSupportImpl jss = new JSSupportImpl(linker, stackSource, pathConstructor);

        jss.importStack("custom");

        // Duplicate calls are ignored.
        jss.importStack("Custom");

        jss.commit();

        verify();
    }

    @Test
    public void duplicate_imported_libraries_are_filtered()
    {
        DocumentLinker linker = mockDocumentLinker();
        JSStackSource stackSource = mockJavascriptStackSource();
        JSStackPathConstructor pathConstructor = mockJavascriptStackPathConstructor();
        trainForEmptyCoreStack(linker, stackSource, pathConstructor);

        Asset library1 = mockAsset("mylib1.js");
        Asset library2 = mockAsset("mylib2.js");

        linker.addScriptLink("mylib1.js");
        linker.addScriptLink("mylib2.js");

        replay();

        JSSupportImpl jss = new JSSupportImpl(linker, stackSource, pathConstructor);

        jss.importJavascriptLibrary(library1);
        jss.importJavascriptLibrary(library2);
        jss.importJavascriptLibrary(library1);

        jss.commit();

        verify();
    }

    @Test
    public void initialize_calls_are_aggregated_within_priority()
    {
        DocumentLinker linker = mockDocumentLinker();
        JSStackSource stackSource = mockJavascriptStackSource();
        JSStackPathConstructor pathConstructor = mockJavascriptStackPathConstructor();
        trainForEmptyCoreStack(linker, stackSource, pathConstructor);

        JSONObject spec1 = new JSONObject("clientId", "chuck");
        JSONObject spec2 = new JSONObject("clientId", "fred");

        JSONObject aggregated = new JSONObject().put("setup", new JSONArray(spec1, spec2));

        linker.setInitialization(InitializationPriority.IMMEDIATE, aggregated);

        replay();

        JSSupportImpl jss = new JSSupportImpl(linker, stackSource, pathConstructor);

        jss.addInitializerCall(InitializationPriority.IMMEDIATE, "setup", spec1);
        jss.addInitializerCall(InitializationPriority.IMMEDIATE, "setup", spec2);

        jss.commit();

        verify();
    }

    @Test
    public void init_with_string()
    {

        DocumentLinker linker = mockDocumentLinker();
        JSStackSource stackSource = mockJavascriptStackSource();
        JSStackPathConstructor pathConstructor = mockJavascriptStackPathConstructor();
        trainForEmptyCoreStack(linker, stackSource, pathConstructor);

        JSONObject aggregated = new JSONObject().put("setup", new JSONArray("chuck", "charley"));

        linker.setInitialization(InitializationPriority.IMMEDIATE, aggregated);

        replay();

        JSSupportImpl jss = new JSSupportImpl(linker, stackSource, pathConstructor);

        jss.addInitializerCall(InitializationPriority.IMMEDIATE, "setup", "chuck");
        jss.addInitializerCall(InitializationPriority.IMMEDIATE, "setup", "charley");

        jss.commit();

        verify();
    }

    @Test
    public void default_for_init_string_is_normal_priority()
    {
        DocumentLinker linker = mockDocumentLinker();
        JSStackSource stackSource = mockJavascriptStackSource();
        JSStackPathConstructor pathConstructor = mockJavascriptStackPathConstructor();
        trainForEmptyCoreStack(linker, stackSource, pathConstructor);

        JSONObject aggregated = new JSONObject().put("setup", new JSONArray().put("chuck"));

        linker.setInitialization(InitializationPriority.NORMAL, aggregated);

        replay();

        JSSupportImpl jss = new JSSupportImpl(linker, stackSource, pathConstructor);

        jss.addInitializerCall("setup", "chuck");

        jss.commit();

        verify();
    }

    @Test
    public void import_stylesheet_as_asset()
    {
        DocumentLinker linker = mockDocumentLinker();
        Asset stylesheet = mockAsset("style.css");

        StylesheetLink link = new StylesheetLink("style.css");
        linker.addStylesheetLink(link);

        replay();

        JSSupportImpl jss = new JSSupportImpl(linker, null, null);

        jss.importStylesheet(stylesheet);

        jss.commit();

        verify();
    }

    @Test
    public void duplicate_stylesheet_ignored_first_media_wins()
    {
        DocumentLinker linker = mockDocumentLinker();
        StylesheetOptions options = new StylesheetOptions("print");

        linker.addStylesheetLink(new StylesheetLink("style.css", options));

        replay();

        JSSupportImpl jss = new JSSupportImpl(linker, null, null);

        jss.importStylesheet(new StylesheetLink("style.css", options));
        jss.importStylesheet(new StylesheetLink("style.css", new StylesheetOptions("hologram")));

        jss.commit();

        verify();
    }
}