/*
 * Copyright 2020 Arthur Sadykov.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.isarthur.netbeans.editor.typingaid.collector.linker;

import com.github.isarthur.netbeans.editor.typingaid.collector.Collector;
import com.github.isarthur.netbeans.editor.typingaid.collector.Collector.Kind;
import com.github.isarthur.netbeans.editor.typingaid.collector.StaticFieldAccessForImportedTypesCollector;
import com.github.isarthur.netbeans.editor.typingaid.collector.StaticMethodInvocationForImportedTypesCollector;
import com.github.isarthur.netbeans.editor.typingaid.collector.StaticFieldAccessCollector;
import com.github.isarthur.netbeans.editor.typingaid.collector.MethodInvocationCollector;
import com.github.isarthur.netbeans.editor.typingaid.collector.StaticMethodInvocationCollector;
import com.github.isarthur.netbeans.editor.typingaid.preferences.Preferences;

/**
 *
 * @author Arthur Sadykov
 */
public class CompoundLinker extends Linker {

    public CompoundLinker(Collector collector) {
        super(collector);
    }

    @Override
    public Collector link() {
        if (Preferences.getMethodInvocationFlag()) {
            collectors.add(collector.getKind() == Kind.METHOD_INVOCATION ? collector : new MethodInvocationCollector());
        }
        if (Preferences.getStaticMethodInvocationFlag()) {
            if (Preferences.getStaticMethodInvocationImportedTypesFlag()) {
                collectors.add(collector.getKind() == Kind.STATIC_METHOD_INVOCATION_FOR_IMPORTED_TYPES
                        ? collector : new StaticMethodInvocationForImportedTypesCollector());
            } else {
                collectors.add(collector.getKind() == Kind.STATIC_METHOD_INVOCATION
                        ? collector : new StaticMethodInvocationCollector());
            }
        }
        if (Preferences.getStaticFieldAccessFlag()) {
            if (Preferences.getStaticFieldAccessImportedTypesFlag()) {
                collectors.add(collector.getKind() == Kind.STATIC_FIELD_ACCESS_FOR_IMPORTED_TYPES
                        ? collector : new StaticFieldAccessForImportedTypesCollector());
            } else {
                collectors.add(collector.getKind() == Kind.STATIC_FIELD_ACCESS
                        ? collector : new StaticFieldAccessCollector());
            }
        }
        for (int i = 0; i < collectors.size() - 1; i++) {
            collectors.get(i).setNext(collectors.get(i + 1));
        }
        return collectors.isEmpty() ? null : collectors.get(0);
    }
}