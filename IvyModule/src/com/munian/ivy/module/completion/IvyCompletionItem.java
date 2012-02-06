package com.munian.ivy.module.completion;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import javax.swing.ImageIcon;
import javax.swing.text.BadLocationException;
import javax.swing.text.JTextComponent;
import javax.swing.text.Position;
import org.apache.ivy.util.XMLHelper;
import org.netbeans.api.editor.completion.Completion;
import org.netbeans.editor.BaseDocument;
import org.netbeans.spi.editor.completion.CompletionItem;
import org.netbeans.spi.editor.completion.CompletionTask;
import org.netbeans.spi.editor.completion.support.CompletionUtilities;

/**
 * Copied from ivybeans
 * @author xavier
 */
public class IvyCompletionItem implements CompletionItem {

    protected int substitutionOffset;
    protected int newCaretOffset = -1;
    protected String text;

    protected IvyCompletionItem(int substitutionOffset) {
        this.substitutionOffset = substitutionOffset;
    }

    public IvyCompletionItem(int substitutionOffset, String text) {
        this.substitutionOffset = substitutionOffset;
        this.text = text;
    }

    public IvyCompletionItem(int substitutionOffset, String text, int newCaretOffset) {
        this.substitutionOffset = substitutionOffset;
        this.text = text;
        this.newCaretOffset = newCaretOffset;
    }

    public void defaultAction(JTextComponent component) {
        if (component != null) {
            Completion.get().hideDocumentation();
            Completion.get().hideCompletion();
            int caretOffset = component.getSelectionEnd();
            substituteText(component, substitutionOffset, caretOffset - substitutionOffset, null);
            if (newCaretOffset != -1) {
                component.setCaretPosition(substitutionOffset + newCaretOffset);
            }
        }
    }

    protected void substituteText(JTextComponent c, final int offset, final int len, String toAdd) {
        final BaseDocument doc = (BaseDocument) c.getDocument();
        final CharSequence prefix = getSubstitutionText();
        final StringBuffer textBuffer = new StringBuffer(prefix.toString());
        if (toAdd != null) {
            textBuffer.append(toAdd);
        }

        doc.runAtomic(new Runnable() {

            public void run() {
                try {
                    Position position = doc.createPosition(offset);
                    doc.remove(offset, len);
                    doc.insertString(position.getOffset(), textBuffer.toString(), null);
                } catch (BadLocationException ble) {
                    // nothing can be done to update
                }
            }
        });
    }

    protected CharSequence getSubstitutionText() {
        return getInsertPrefix();
    }

    public void processKeyEvent(KeyEvent evt) {
    }

    public int getPreferredWidth(Graphics g, Font defaultFont) {
        return CompletionUtilities.getPreferredWidth(getLeftHtmlText(),
                getRightHtmlText(), g, defaultFont);
    }

    public void render(Graphics g, Font defaultFont, Color defaultColor,
            Color backgroundColor, int width, int height, boolean selected) {
        CompletionUtilities.renderHtml(getIcon(), getLeftHtmlText(),
                getRightHtmlText(), g, defaultFont, defaultColor, width, height, selected);
    }

    public CompletionTask createDocumentationTask() {
        return null;
    }

    public CompletionTask createToolTipTask() {
        return null;
    }

    public boolean instantSubstitution(JTextComponent component) {
        defaultAction(component);
        return true;
    }

    protected String getLeftHtmlText() {
        return XMLHelper.escape(text);
    }

    protected String getRightHtmlText() {
        return null;
    }

    protected ImageIcon getIcon() {
        return null;
    }

    public int getSortPriority() {
        return 300;
    }

    public CharSequence getSortText() {
        return getSubstitutionText();
    }

    public CharSequence getInsertPrefix() {
        return text;
    }
}
