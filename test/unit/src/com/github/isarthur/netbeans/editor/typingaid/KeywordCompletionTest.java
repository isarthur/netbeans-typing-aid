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
package com.github.isarthur.netbeans.editor.typingaid;

import com.github.isarthur.netbeans.editor.typingaid.settings.Settings;
import com.github.isarthur.netbeans.editor.typingaid.spi.CodeFragment;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.lang.ref.WeakReference;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import javax.swing.JEditorPane;
import javax.swing.SwingUtilities;
import javax.swing.text.Document;
import javax.swing.text.EditorKit;
import junit.framework.Test;
import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNotNull;
import static org.junit.Assert.assertArrayEquals;
import org.netbeans.api.java.lexer.JavaTokenId;
import org.netbeans.api.java.source.JavaSource;
import org.netbeans.api.lexer.Language;
import org.netbeans.junit.NbTestCase;
import org.netbeans.junit.NbModuleSuite;
import org.netbeans.modules.editor.NbEditorKit;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;

/**
 *
 * @author: Arthur Sadykov
 */
public class KeywordCompletionTest extends NbTestCase {

    private static final String JAVA_MIME_TYPE = "text/x-java";
    private static final String MIME_TYPE = "mimeType";
    private static final String JAVA_CLUSTER = "java";
    private static final String IDE_CLUSTER = "ide";
    private static final String EXTIDE_CLUSTER = "extide";
    private static final String TEST_FILE = "Test.java";
    private JavaAbbreviationHandler handler;
    private JavaAbbreviation abbreviation;
    private JEditorPane editor;
    private FileObject testFile;
    private Document document;
    private boolean keyword;
    private boolean modifier;
    private boolean primitiveType;
    private boolean externalType;
    private boolean internalType;
    private boolean importedType;
    private boolean resourceVariable;
    private boolean exceptionParameter;
    private boolean enumConstant;
    private boolean parameter;
    private boolean field;
    private boolean localVariable;
    private boolean staticFieldAccess;
    private boolean localMethodInvocation;
    private boolean chainedMethodInvocation;
    private boolean staticMethodInvocation;
    private boolean methodInvocation;

    public KeywordCompletionTest(String testName) {
        super(testName);
    }

    public static Test suite() {
        return NbModuleSuite.createConfiguration(KeywordCompletionTest.class)
                .clusters(EXTIDE_CLUSTER)
                .clusters(IDE_CLUSTER)
                .clusters(JAVA_CLUSTER)
                .gui(false)
                .suite();
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        clearWorkDir();
        testFile = FileUtil.toFileObject(getWorkDir()).createData(TEST_FILE);
        EditorKit kit = new NbEditorKit();
        editor = new JEditorPane();
        SwingUtilities.invokeAndWait(() -> editor.setEditorKit(kit));
        document = editor.getDocument();
        document.putProperty(Document.StreamDescriptionProperty, testFile);
        document.putProperty(MIME_TYPE, JAVA_MIME_TYPE);
        document.putProperty(Language.class, JavaTokenId.language());
        document.putProperty(JavaSource.class, new WeakReference<>(JavaSource.forFileObject(testFile)));
        JavaSourceHelper helper = new JavaSourceHelper(editor);
        handler = new JavaAbbreviationHandler(helper);
        abbreviation = JavaAbbreviation.getInstance();
        storeSettings();
        setConfigurationForKeywordCompletion();
    }

    private void storeSettings() {
        methodInvocation = Settings.getSettingForMethodInvocation();
        staticMethodInvocation = Settings.getSettingForStaticMethodInvocation();
        chainedMethodInvocation = Settings.getSettingForChainedMethodInvocation();
        localMethodInvocation = Settings.getSettingForLocalMethodInvocation();
        staticFieldAccess = Settings.getSettingForStaticFieldAccess();
        localVariable = Settings.getSettingForLocalVariable();
        field = Settings.getSettingForField();
        parameter = Settings.getSettingForParameter();
        enumConstant = Settings.getSettingForEnumConstant();
        exceptionParameter = Settings.getSettingForExceptionParameter();
        resourceVariable = Settings.getSettingForResourceVariable();
        internalType = Settings.getSettingForInternalType();
        externalType = Settings.getSettingForExternalType();
        importedType = Settings.getSettingForImportedType();
        keyword = Settings.getSettingForKeyword();
        modifier = Settings.getSettingForModifier();
        primitiveType = Settings.getSettingForPrimitiveType();
    }

    private void setConfigurationForKeywordCompletion() {
        Settings.setSettingForMethodInvocation(false);
        Settings.setSettingForStaticMethodInvocation(false);
        Settings.setSettingForChainedMethodInvocation(false);
        Settings.setSettingForLocalMethodInvocation(false);
        Settings.setSettingForStaticFieldAccess(false);
        Settings.setSettingForLocalVariable(false);
        Settings.setSettingForField(false);
        Settings.setSettingForParameter(false);
        Settings.setSettingForEnumConstant(false);
        Settings.setSettingForExceptionParameter(false);
        Settings.setSettingForResourceVariable(false);
        Settings.setSettingForInternalType(false);
        Settings.setSettingForExternalType(false);
        Settings.setSettingForImportedType(false);
        Settings.setSettingForKeyword(true);
        Settings.setSettingForModifier(false);
        Settings.setSettingForPrimitiveType(false);
    }

    public void testAssertKeywordCompletion() throws IOException {
        doAbbreviationInsert(
                "a",
                "class Test {\n"
                + "    void test() {\n"
                + "        |\n"
                + "    }\n"
                + "}",
                "class Test {\n"
                + "    void test() {\n"
                + "        assert true : \"\";\n"
                + "        \n"
                + "    }\n"
                + "}",
                Collections.singletonList("assert true : \"\";"));
    }

    public void testBreakKeywordCompletionInForLoop() throws IOException {
        doAbbreviationInsert(
                "b",
                "class Test {\n"
                + "    void test() {\n"
                + "        for (int i = 0; i < 10; i++) {\n"
                + "            |\n"
                + "        }\n"
                + "    }\n"
                + "}",
                "class Test {\n"
                + "    void test() {\n"
                + "        for (int i = 0; i < 10; i++) {\n"
                + "            break;\n"
                + "            \n"
                + "        }\n"
                + "    }\n"
                + "}",
                Arrays.asList("break;"));
    }

    public void testBreakKeywordCompletionInWhileLoop() throws IOException {
        doAbbreviationInsert(
                "b",
                "class Test {\n"
                + "    void test() {\n"
                + "        while (true) {\n"
                + "            |\n"
                + "        }\n"
                + "    }\n"
                + "}",
                "class Test {\n"
                + "    void test() {\n"
                + "        while (true) {\n"
                + "            break;\n"
                + "            \n"
                + "        }\n"
                + "    }\n"
                + "}",
                Arrays.asList("break;"));
    }

    public void testBreakKeywordCompletionInDoWhileLoop() throws IOException {
        doAbbreviationInsert(
                "b",
                "class Test {\n"
                + "    void test() {\n"
                + "        do {\n"
                + "            |\n"
                + "        } while (true);\n"
                + "    }\n"
                + "}",
                "class Test {\n"
                + "    void test() {\n"
                + "        do {\n"
                + "            break;\n"
                + "            \n"
                + "        } while (true);\n"
                + "    }\n"
                + "}",
                Arrays.asList("break;"));
    }

    public void testBreakKeywordCompletionInSwitchStatement() throws IOException {
        doAbbreviationInsert(
                "b",
                "class Test {\n"
                + "    int count = 10;\n"
                + "    void test() {\n"
                + "        switch (count) {\n"
                + "            case 0:\n"
                + "                count = 0;\n"
                + "                |\n"
                + "        }\n"
                + "    }\n"
                + "}",
                "class Test {\n"
                + "    int count = 10;\n"
                + "    void test() {\n"
                + "        switch (count) {\n"
                + "            case 0:\n"
                + "                count = 0;\n"
                + "            break;\n"
                + "                \n"
                + "        }\n"
                + "    }\n"
                + "}",
                Collections.singletonList("break;"));
    }

    public void testContinueKeywordCompletionInForLoop() throws IOException {
        doAbbreviationInsert(
                "c",
                "class Test {\n"
                + "    void test() {\n"
                + "        for (int i = 0; i < 10; i++) {\n"
                + "            |\n"
                + "        }\n"
                + "    }\n"
                + "}",
                "class Test {\n"
                + "    void test() {\n"
                + "        for (int i = 0; i < 10; i++) {\n"
                + "            \n"
                + "        }\n"
                + "    }\n"
                + "}",
                Arrays.asList("class", "continue"));
    }

    public void testContinueKeywordCompletionInWhileLoop() throws IOException {
        doAbbreviationInsert(
                "c",
                "class Test {\n"
                + "    void test() {\n"
                + "        while (true) {\n"
                + "            |\n"
                + "        }\n"
                + "    }\n"
                + "}",
                "class Test {\n"
                + "    void test() {\n"
                + "        while (true) {\n"
                + "            \n"
                + "        }\n"
                + "    }\n"
                + "}",
                Arrays.asList("class", "continue"));
    }

    public void testContinueKeywordCompletionInDoWhileLoop() throws IOException {
        doAbbreviationInsert(
                "c",
                "class Test {\n"
                + "    void test() {\n"
                + "        do {\n"
                + "            |\n"
                + "        } while (true);\n"
                + "    }\n"
                + "}",
                "class Test {\n"
                + "    void test() {\n"
                + "        do {\n"
                + "            \n"
                + "        } while (true);\n"
                + "    }\n"
                + "}",
                Arrays.asList("class", "continue"));
    }

    public void testWhileKeywordCompletion() throws IOException {
        doAbbreviationInsert(
                "w",
                "class Test {\n"
                + "    void test() {\n"
                + "        |\n"
                + "    }\n"
                + "}",
                "class Test {\n"
                + "    void test() {\n"
                + "        while (true) {\n"
                + "        }\n"
                + "        \n"
                + "    }\n"
                + "}",
                Collections.singletonList("while (true) {" + System.lineSeparator() + "}"));
    }

    public void testDoWhileKeywordCompletion() throws IOException {
        doAbbreviationInsert(
                "d",
                "class Test {\n"
                + "    void test() {\n"
                + "        |\n"
                + "    }\n"
                + "}",
                "class Test {\n"
                + "    void test() {\n"
                + "        do {\n"
                + "        } while (true);\n"
                + "        \n"
                + "    }\n"
                + "}",
                Collections.singletonList("do {" + System.lineSeparator() + "} while (true);"));
    }

    public void testForKeywordCompletion() throws IOException {
        doAbbreviationInsert(
                "f",
                "class Test {\n"
                + "    void test() {\n"
                + "        |\n"
                + "    }\n"
                + "}",
                "class Test {\n"
                + "    void test() {\n"
                + "        for (int i = 0; i < 10; i++) {\n"
                + "        }\n"
                + "        \n"
                + "    }\n"
                + "}",
                Collections.singletonList("for (int i = 0; i < 10; i++) {" + System.lineSeparator() + "}"));
    }

    public void testTryKeywordCompletion() throws IOException {
        doAbbreviationInsert(
                "t",
                "class Test {\n"
                + "    void test() {\n"
                + "        |\n"
                + "    }\n"
                + "}",
                "class Test {\n"
                + "    void test() {\n"
                + "        \n"
                + "    }\n"
                + "}",
                Arrays.asList("throw", "try"));
    }

    public void testCatchKeywordCompletion() throws IOException {
        doAbbreviationInsert(
                "c",
                "class Test {\n"
                + "    void test() {\n"
                + "        try {\n"
                + "        } |catch (IndexOutOfBoundsException e) {\n"
                + "        }\n"
                + "    }\n"
                + "}",
                "class Test {\n"
                + "    void test() {\n"
                + "        try {\n"
                + "        } catch (IndexOutOfBoundsException e) {\n"
                + "        } catch (Exception e) {\n"
                + "        }\n"
                + "    }\n"
                + "}",
                Collections.singletonList(" catch (Exception e) {" + System.lineSeparator() + "}"));
    }

    public void testFinallyKeywordCompletion() throws IOException {
        doAbbreviationInsert(
                "f",
                "class Test {\n"
                + "    void test() {\n"
                + "        try {\n"
                + "        } |catch (IndexOutOfBoundsException e) {\n"
                + "        }\n"
                + "    }\n"
                + "}",
                "class Test {\n"
                + "    void test() {\n"
                + "        try {\n"
                + "        } catch (IndexOutOfBoundsException e) {\n"
                + "        } finally {\n"
                + "        }\n"
                + "    }\n"
                + "}",
                Collections.singletonList("{" + System.lineSeparator() + "}"));
    }

    public void testThrowKeywordCompletion() throws IOException {
        doAbbreviationInsert(
                "t",
                "class Test {\n"
                + "    void test() {\n"
                + "        |\n"
                + "    }\n"
                + "}",
                "class Test {\n"
                + "    void test() {\n"
                + "        \n"
                + "    }\n"
                + "}",
                Arrays.asList("throw", "try"));
    }

    public void testImplementsKeywordCompletionForClass() throws IOException {
        doAbbreviationInsert(
                "i",
                "class Test |{\n"
                + "}",
                "class Test implements  {\n"
                + "}",
                Collections.singletonList(""));
    }

    public void testImplementsKeywordCompletionForEnum() throws IOException {
        doAbbreviationInsert(
                "i",
                "enum Test |{\n"
                + "}",
                "enum Test implements  {\n"
                + "}",
                Collections.singletonList(""));
    }

    public void testInterfaceKeywordCompletionInClass() throws IOException {
        doAbbreviationInsert(
                "i",
                "class Test {\n"
                + "    |\n"
                + "}",
                "class Test {\n"
                + "\n"
                + "    interface Interface {\n"
                + "    }\n"
                + "    \n"
                + "}",
                Collections.singletonList(
                        System.lineSeparator()
                        + "interface Interface {"
                        + System.lineSeparator()
                        + "}"
                ));
    }

    public void testInterfaceKeywordCompletionInInterface() throws IOException {
        doAbbreviationInsert(
                "i",
                "interface Test {\n"
                + "    |\n"
                + "}",
                "interface Test {\n"
                + "\n"
                + "    interface Interface {\n"
                + "    }\n"
                + "    \n"
                + "}",
                Collections.singletonList(
                        System.lineSeparator()
                        + "interface Interface {"
                        + System.lineSeparator()
                        + "}"));
    }

    public void testInterfaceKeywordCompletionInEnum() throws IOException {
        doAbbreviationInsert(
                "i",
                "enum Test {\n"
                + "    TEST;\n"
                + "    |\n"
                + "}",
                "enum Test {\n"
                + "    TEST;\n"
                + "\n"
                + "    interface Interface {\n"
                + "    }\n"
                + "    \n"
                + "}",
                Collections.singletonList(
                        System.lineSeparator()
                        + "interface Interface {"
                        + System.lineSeparator()
                        + "}"
                ));
    }

    public void testInterfaceKeywordCompletionInCompilationUnit() throws IOException {
        doAbbreviationInsert(
                "i",
                "class Test {\n"
                + "}\n"
                + "|",
                "class Test {\n"
                + "}\n",
                Arrays.asList("import", "interface"));
    }

    public void testClassKeywordCompletionInClass() throws IOException {
        doAbbreviationInsert(
                "c",
                "class Test {\n"
                + "    |\n"
                + "}",
                "class Test {\n"
                + "\n"
                + "    class Class {\n"
                + "    }\n"
                + "    \n"
                + "}",
                Collections.singletonList(
                        System.lineSeparator()
                        + "class Class {"
                        + System.lineSeparator()
                        + "}"));
    }

    public void testClassKeywordCompletionInInterface() throws IOException {
        doAbbreviationInsert(
                "c",
                "interface Test {\n"
                + "    |\n"
                + "}",
                "interface Test {\n"
                + "\n"
                + "    class Class {\n"
                + "    }\n"
                + "    \n"
                + "}",
                Collections.singletonList(
                        System.lineSeparator()
                        + "class Class {"
                        + System.lineSeparator()
                        + "}"));
    }

    public void testClassKeywordCompletionInEnum() throws IOException {
        doAbbreviationInsert(
                "c",
                "enum Test {\n"
                + "    MIDDLE;\n"
                + "    |\n"
                + "}",
                "enum Test {\n"
                + "    MIDDLE;\n"
                + "\n"
                + "    class Class {\n"
                + "    }\n"
                + "    \n"
                + "}",
                Collections.singletonList(
                        System.lineSeparator()
                        + "class Class {"
                        + System.lineSeparator()
                        + "}"));
    }

    public void testClassKeywordCompletionInCompilationUnit() throws IOException {
        doAbbreviationInsert(
                "c",
                "class Test {\n"
                + "}\n"
                + "|",
                "class Test {\n"
                + "}\n"
                + "\n"
                + "class Class {\n"
                + "}\n",
                Collections.singletonList(
                        System.lineSeparator()
                        + "class Class {"
                        + System.lineSeparator()
                        + "}"));
    }

    public void testClassKeywordCompletionInBlock() throws IOException {
        doAbbreviationInsert(
                "c",
                "class Test {\n"
                + "    void test() {\n"
                + "        |\n"
                + "    }\n"
                + "}",
                "class Test {\n"
                + "    void test() {\n"
                + "\n"
                + "        class Class {\n"
                + "        }\n"
                + "        \n"
                + "    }\n"
                + "}",
                Collections.singletonList(
                        System.lineSeparator()
                        + "class Class {"
                        + System.lineSeparator()
                        + "}"));
    }

    public void testEnumKeywordCompletionInClass() throws IOException {
        doAbbreviationInsert(
                "e",
                "class Test {\n"
                + "    |\n"
                + "}",
                "class Test {\n"
                + "\n"
                + "    enum Enum {\n"
                + "    }\n"
                + "    \n"
                + "}",
                Collections.singletonList(
                        System.lineSeparator()
                        + "enum Enum {"
                        + System.lineSeparator()
                        + ";"
                        + System.lineSeparator()
                        + "}"));
    }

    public void testEnumKeywordCompletionInInterface() throws IOException {
        doAbbreviationInsert(
                "e",
                "interface Test {\n"
                + "    |\n"
                + "}",
                "interface Test {\n"
                + "\n"
                + "    enum Enum {\n"
                + "    }\n"
                + "    \n"
                + "}",
                Collections.singletonList(
                        System.lineSeparator()
                        + "enum Enum {"
                        + System.lineSeparator()
                        + ";"
                        + System.lineSeparator()
                        + "}"
                ));
    }

    public void testEnumKeywordCompletionInEnum() throws IOException {
        doAbbreviationInsert(
                "e",
                "enum Test {\n"
                + "    |\n"
                + "}",
                "enum Test {\n"
                + "\n"
                + "    enum Enum {\n"
                + "    }\n"
                + "    \n"
                + "}",
                Collections.singletonList(
                        System.lineSeparator()
                        + "enum Enum {"
                        + System.lineSeparator()
                        + ";"
                        + System.lineSeparator()
                        + "}"));
    }

    public void testEnumKeywordCompletionInCompilationUnit() throws IOException {
        doAbbreviationInsert(
                "e",
                "class Test {\n"
                + "}\n"
                + "|",
                "class Test {\n"
                + "}\n"
                + "\n"
                + "enum Enum {\n"
                + "}\n",
                Collections.singletonList(
                        System.lineSeparator()
                        + "enum Enum {"
                        + System.lineSeparator()
                        + ";"
                        + System.lineSeparator()
                        + "}"
                ));
    }

    public void testExtendsKeywordCompletionForClass() throws IOException {
        doAbbreviationInsert(
                "e",
                "class Test |{\n"
                + "}",
                "class Test extends  {\n"
                + "}",
                Collections.singletonList(""));
    }

    public void testExtendsKeywordCompletionForInterface() throws IOException {
        doAbbreviationInsert(
                "e",
                "interface Test |{\n"
                + "}",
                "interface Test extends  {\n"
                + "}",
                Collections.singletonList(""));
    }

    public void testElseKeywordCompletion() throws IOException {
        doAbbreviationInsert(
                "e",
                "class Test {\n"
                + "    void test() {\n"
                + "        if |(true) {\n"
                + "        }\n"
                + "    }\n"
                + "}",
                "class Test {\n"
                + "    void test() {\n"
                + "        if (true) {\n"
                + "        } else {\n"
                + "        }\n"
                + "    }\n"
                + "}",
                Collections.singletonList("{" + System.lineSeparator() + "}"));
    }

    public void testVoidKeywordCompletionInClass() throws IOException {
        doAbbreviationInsert(
                "v",
                "class Test {\n"
                + "    |\n"
                + "}",
                "class Test {\n"
                + "\n"
                + "    void method() {\n"
                + "    }\n"
                + "    \n"
                + "}",
                Collections.singletonList(
                        System.lineSeparator()
                        + "void method() {"
                        + System.lineSeparator()
                        + "}"));
    }

    public void testVoidKeywordCompletionInInterface() throws IOException {
        doAbbreviationInsert(
                "v",
                "interface Test {\n"
                + "    |\n"
                + "}",
                "interface Test {\n"
                + "    void method();\n"
                + "    \n"
                + "}",
                Collections.singletonList("void method();"));
    }

    public void testVoidKeywordCompletionInEnum() throws IOException {
        doAbbreviationInsert(
                "v",
                "enum Test {\n"
                + "    TEST;\n"
                + "    |\n"
                + "}",
                "enum Test {\n"
                + "    TEST;\n"
                + "\n"
                + "    void method() {\n"
                + "    }\n"
                + "    \n"
                + "}",
                Collections.singletonList(
                        System.lineSeparator()
                        + "void method() {"
                        + System.lineSeparator()
                        + "}"));
    }

    public void testImportKeywordCompletion() throws IOException {
        doAbbreviationInsert(
                "i",
                "|\n"
                + "class Test {\n"
                + "}",
                "\n"
                + "class Test {\n"
                + "}",
                Arrays.asList("import", "interface"));
    }

    public void testReturnKeywordCompletionInBlockTree() throws IOException {
        doAbbreviationInsert(
                "r",
                "class Test {\n"
                + "    int size() {\n"
                + "        |\n"
                + "    }\n"
                + "}",
                "class Test {\n"
                + "    int size() {\n"
                + "        return 0;\n"
                + "        \n"
                + "    }\n"
                + "}",
                Collections.singletonList("return 0;"));
    }

    public void testReturnKeywordCompletionInSwitchTree() throws IOException {
        doAbbreviationInsert(
                "r",
                "class Test {\n"
                + "    int test() {\n"
                + "        switch (0) {\n"
                + "            case 0:\n"
                + "                |\n"
                + "        }\n"
                + "    }\n"
                + "}",
                "class Test {\n"
                + "    int test() {\n"
                + "        switch (0) {\n"
                + "            case 0:\n"
                + "            return 0;\n"
                + "                \n"
                + "        }\n"
                + "    }\n"
                + "}",
                Collections.singletonList("return 0;"));
    }

    private void doAbbreviationInsert(String abbrev, String code, String golden, List<String> proposals)
            throws IOException {
        int caretOffset = code.indexOf('|');
        String text = code.substring(0, caretOffset) + code.substring(caretOffset + 1);
        editor.setText(text);
        editor.setCaretPosition(caretOffset);
        try ( OutputStream out = testFile.getOutputStream();  Writer writer = new OutputStreamWriter(out)) {
            writer.append(text);
        }
        abbreviation.setStartOffset(caretOffset);
        for (int i = 0; i < abbrev.length(); i++) {
            abbreviation.append(abbrev.charAt(i));
        }
        List<CodeFragment> codeFragments = handler.process(abbreviation);
        assertNotNull(codeFragments);
        assertArrayEquals(proposals.toArray(), codeFragments.stream().map(fragment -> fragment.toString()).toArray());
        assertEquals(golden, testFile.asText());
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        abbreviation.reset();
        revertSettings();
    }

    private void revertSettings() {
        Settings.setSettingForMethodInvocation(methodInvocation);
        Settings.setSettingForStaticMethodInvocation(staticMethodInvocation);
        Settings.setSettingForChainedMethodInvocation(chainedMethodInvocation);
        Settings.setSettingForLocalMethodInvocation(localMethodInvocation);
        Settings.setSettingForStaticFieldAccess(staticFieldAccess);
        Settings.setSettingForLocalVariable(localVariable);
        Settings.setSettingForField(field);
        Settings.setSettingForParameter(parameter);
        Settings.setSettingForEnumConstant(enumConstant);
        Settings.setSettingForExceptionParameter(exceptionParameter);
        Settings.setSettingForResourceVariable(resourceVariable);
        Settings.setSettingForInternalType(internalType);
        Settings.setSettingForExternalType(externalType);
        Settings.setSettingForImportedType(importedType);
        Settings.setSettingForKeyword(keyword);
        Settings.setSettingForModifier(modifier);
        Settings.setSettingForPrimitiveType(primitiveType);
    }
}
