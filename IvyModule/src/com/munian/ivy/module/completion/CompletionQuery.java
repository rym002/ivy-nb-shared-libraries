/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */
package com.munian.ivy.module.completion;

import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.JTextComponent;
import org.apache.ivy.Ivy;
import org.apache.ivyde.common.completion.CodeCompletionProposal;
import org.apache.ivyde.common.completion.IvyCodeCompletionProcessor;
import org.netbeans.api.lexer.Token;
import org.netbeans.api.lexer.TokenHierarchy;
import org.netbeans.api.lexer.TokenSequence;
import org.netbeans.api.xml.lexer.XMLTokenId;
import org.netbeans.spi.editor.completion.CompletionResultSet;
import org.netbeans.spi.editor.completion.support.AsyncCompletionQuery;

/**
 * Copied from ivybeans
 * @author xavier
 */
public class CompletionQuery extends AsyncCompletionQuery {

    private JTextComponent component;
    private Ivy ivy;
    private IvyCodeCompletionProcessor processor;
    private String projectName;

    /**
     * Creates a new instance of CompletionQuery
     */
    public CompletionQuery(Ivy ivy, IvyCodeCompletionProcessor processor, String projectName) {
        this.ivy = ivy;
        this.processor = processor;
        this.projectName = projectName;
    }

  
    @Override
    protected void prepareQuery(JTextComponent component) {
        this.component = component;
    }

    @Override
    protected void query(CompletionResultSet resultSet, Document doc, int caretOffset) {
        try {
            if (ivy == null || !isIvyFile(doc)) {
                return;
            }
            CodeCompletionProposal[] proposals = processor.computeCompletionProposals(
                    processor.getModel().newIvyFile(
                    projectName, doc.getText(0, doc.getLength()), caretOffset),
                    caretOffset);
            for (int i = 0; i < proposals.length; i++) {
                CodeCompletionProposal proposal = proposals[i];
                resultSet.addItem(new IvyCompletionItem(
                        proposal.getReplacementOffset(),
                        proposal.getReplacementString(),
                        proposal.getCursorPosition()));
            }
        } catch (BadLocationException e) {
        } finally {
            resultSet.finish();
        }
    }

    private boolean isIvyFile(Document document) {
        try {
            return processor.getModel().getRootIvyTag().getName().equals(getDocRoot(document));
        } catch (Exception ex) {
            return false;
        }
    }

    /**
     * Finds the root element of the xml document.
     */
    public static String getDocRoot(Document document) {
        TokenHierarchy th = TokenHierarchy.get(document);
        TokenSequence ts = th.tokenSequence();
        while (ts.moveNext()) {
            Token nextToken = ts.token();
            if (nextToken.id() == XMLTokenId.TAG) {
                String tagName = nextToken.text().toString();
                if (tagName.startsWith("<")) {
                    return tagName.substring(1, tagName.length());
                }
            }
        }
        return null;
    }
}
