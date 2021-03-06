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
package com.github.isarthur.netbeans.editor.typingaid.abbreviation.impl;

import com.github.isarthur.netbeans.editor.typingaid.abbreviation.api.Abbreviation;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Arthur Sadykov
 */
@ServiceProvider(service = Abbreviation.class)
public class JavaAbbreviation implements Abbreviation {

    private int startOffset;
    private final StringBuffer buffer;

    public JavaAbbreviation() {
        this.startOffset = -1;
        this.buffer = new StringBuffer();
    }

    public JavaAbbreviation(String content, int offset) {
        this.buffer = new StringBuffer(content);
        this.startOffset = offset;
    }

    @Override
    public int getStartOffset() {
        return startOffset;
    }

    @Override
    public boolean isSimple() {
        return !getContent().contains("."); //NOI18N
    }

    @Override
    public void setStartOffset(int startOffset) {
        this.startOffset = startOffset;
    }

    @Override
    public int getEndOffset() {
        return startOffset + buffer.length();
    }

    @Override
    public JavaAbbreviation append(char character) {
        buffer.append(character);
        return this;
    }

    @Override
    public void reset() {
        buffer.setLength(0);
        startOffset = -1;
    }

    @Override
    public boolean isEmpty() {
        return buffer.length() == 0;
    }

    @Override
    public String getContent() {
        return buffer.toString();
    }

    @Override
    public void setContent(String content) {
        buffer.setLength(0);
        buffer.append(content);
    }

    @Override
    public void delete() {
        if (!isEmpty()) {
            buffer.delete(buffer.length() - 1, buffer.length());
        }
    }

    @Override
    public int length() {
        return buffer.length();
    }

    @Override
    public String getScope() {
        String content = getContent();
        int dotIndex = content.indexOf('.');
        if (dotIndex > 0) {
            return content.substring(0, dotIndex);
        }
        return content;
    }

    @Override
    public String getIdentifier() {
        String content = getContent();
        int dotIndex = content.indexOf('.');
        if (dotIndex > 0) {
            return content.substring(dotIndex + 1);
        }
        return content;
    }

    @Override
    public String toString() {
        String content = getContent();
        int dotIndex = content.indexOf('.');
        if (dotIndex > 0) {
            return content.substring(dotIndex + 1);
        }
        return content;
    }
}
