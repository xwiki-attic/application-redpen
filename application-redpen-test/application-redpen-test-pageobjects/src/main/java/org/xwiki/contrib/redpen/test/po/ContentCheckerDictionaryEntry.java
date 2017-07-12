/*
 * See the NOTICE file distributed with this work for additional
 * information regarding copyright ownership.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.xwiki.contrib.redpen.test.po;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.xwiki.test.ui.po.InlinePage;

/**
 * Represents keying in an entry into the dictionary.
 *
 * @version $Id: $
 * @since 1.0
 */
public class ContentCheckerDictionaryEntry extends InlinePage
{
    @FindBy(id = "Content Checker.DictionaryCode.DictionaryEntryClass_0_EntryType")
    private WebElement entryTypeElement;

    @FindBy(id = "Content Checker.DictionaryCode.DictionaryEntryClass_1_entry")
    private WebElement entryElement;

    @FindBy(id = "Content checker.DictionaryCode.DictionaryEntryClass_2_suggestion")
    private WebElement suggestionElement;

    /**
     * @param i Represents the choice made on static list
     */
    public void setType(int i)
    {
        String answer;
        switch(i) {
            case 1:
                answer = "Invalid";
                break;
            case 2:
                answer = "Suggestion";
                break;
            default:
                answer = "";
                break;
        }
        this.entryTypeElement.clear();
        this.entryTypeElement.sendKeys(answer);
    }

    /**
     * @param entry Represents the entry to be keyed in
     */
    public void setEntry(String entry)
    {
        this.entryElement.clear();
        this.entryElement.sendKeys(entry);
    }

    /**
     * @param suggestion represents the correction to an expression if entry type is Suggestion
     */
    public void setSuggestion(String suggestion)
    {
        this.suggestionElement.clear();
        this.suggestionElement.sendKeys(suggestion);
    }
}
