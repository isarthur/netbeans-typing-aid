/*
 * Copyright 2021 Arthur Sadykov.
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
package com.github.isarthur.netbeans.editor.typingaid.insertvisitor.impl;

import com.github.isarthur.netbeans.editor.typingaid.abbreviation.api.Abbreviation;
import com.github.isarthur.netbeans.editor.typingaid.codefragment.api.CodeFragment;
import com.github.isarthur.netbeans.editor.typingaid.insertvisitor.api.AbstractCodeFragmentInsertVisitor;
import com.github.isarthur.netbeans.editor.typingaid.request.api.CodeCompletionRequest;
import com.github.isarthur.netbeans.editor.typingaid.util.JavaSourceMaker;
import com.github.isarthur.netbeans.editor.typingaid.util.JavaSourceUtilities;
import com.sun.source.tree.ExpressionTree;
import com.sun.source.tree.MethodTree;
import com.sun.source.tree.ModifiersTree;
import com.sun.source.tree.Tree;
import com.sun.source.tree.VariableTree;
import org.netbeans.api.java.lexer.JavaTokenId;
import org.netbeans.api.java.source.WorkingCopy;
import org.netbeans.api.lexer.Token;
import org.netbeans.api.lexer.TokenSequence;

/**
 *
 * @author Arthur Sadykov
 */
public class MethodCodeFragmentInsertVisitor extends AbstractCodeFragmentInsertVisitor {

    @Override
    protected Tree getOriginalTree(CodeFragment codeFragment, CodeCompletionRequest request) {
        switch (codeFragment.getKind()) {
            case ABSTRACT_MODIFIER:
            case FINAL_MODIFIER:
            case NATIVE_MODIFIER:
            case STATIC_MODIFIER:
            case PRIVATE_MODIFIER:
            case PROTECTED_MODIFIER:
            case PUBLIC_MODIFIER:
            case STRICTFP_MODIFIER:
            case SYNCHRONIZED_MODIFIER:
                WorkingCopy copy = request.getWorkingCopy();
                TokenSequence<?> tokens = copy.getTokenHierarchy().tokenSequence();
                Abbreviation abbreviation = request.getAbbreviation();
                tokens.move(abbreviation.getStartOffset());
                while (tokens.movePrevious() && tokens.token().id() == JavaTokenId.WHITESPACE) {
                }
                Token<?> token = tokens.token();
                if (token != null && JavaSourceUtilities.isModifier(token.id())) {
                    MethodTree originalTree = (MethodTree) request.getCurrentTree();
                    return originalTree.getModifiers();
                }
                return request.getCurrentTree();
            default:
                return request.getCurrentTree();
        }
    }

    @Override
    protected Tree getNewTree(CodeFragment codeFragment, Tree tree, CodeCompletionRequest request) {
        MethodTree originalTree;
        switch (codeFragment.getKind()) {
            case ABSTRACT_MODIFIER:
            case FINAL_MODIFIER:
            case NATIVE_MODIFIER:
            case STATIC_MODIFIER:
            case PRIVATE_MODIFIER:
            case PROTECTED_MODIFIER:
            case PUBLIC_MODIFIER:
            case STRICTFP_MODIFIER:
            case SYNCHRONIZED_MODIFIER:
                ModifiersTree originalModifiersTree = (ModifiersTree) getOriginalTree(codeFragment, request);
                ModifiersTree modifiersTree = (ModifiersTree) tree;
                return JavaSourceMaker.makeModifiersTree(
                        originalModifiersTree, modifiersTree.getFlags().iterator().next(), request);
            case INNER_TYPE:
            case TYPE:
                originalTree = (MethodTree) getOriginalTree(codeFragment, request);
                if (JavaSourceUtilities.isInsideMethodParameterTreeSpan(request)) {
                    int insertIndex = JavaSourceUtilities.findInsertIndexForMethodParameter(originalTree);
                    return JavaSourceMaker.makeMethodTree(originalTree, insertIndex, (VariableTree) tree, request);
                } else if (JavaSourceUtilities.isInsideThrowsTreeSpan(request)) {
                    return JavaSourceMaker.makeMethodTree(originalTree, (ExpressionTree) tree, request);
                } else {
                    throw new RuntimeException("Wrong position for type completion in method declaration."); //NOI18N
                }
            case THROWS_KEYWORD:
                originalTree = (MethodTree) getOriginalTree(codeFragment, request);
                return JavaSourceMaker.makeMethodTree(originalTree, (ExpressionTree) tree, request);
            default:
                originalTree = (MethodTree) getOriginalTree(codeFragment, request);
                int insertIndex = JavaSourceUtilities.findInsertIndexForMethodParameter(originalTree);
                return JavaSourceMaker.makeMethodTree(originalTree, insertIndex, (VariableTree) tree, request);
        }
    }
}
