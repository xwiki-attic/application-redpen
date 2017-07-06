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
package org.xwiki.contrib.redpen.internal;

import java.io.StringReader;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.slf4j.Logger;
import org.xwiki.component.annotation.Component;
import org.xwiki.contrib.redpen.CheckerSyntaxConverter;
import org.xwiki.rendering.converter.ConversionException;
import org.xwiki.rendering.converter.Converter;
import org.xwiki.rendering.renderer.printer.DefaultWikiPrinter;
import org.xwiki.rendering.renderer.printer.WikiPrinter;
import org.xwiki.rendering.syntax.Syntax;

/**
 * This component is the main Syntax Converter. It converts strings mainly into plain text
 *
 * @version $Id: $
 * @since 1.0
 */
@Component
@Singleton
@Named("Syntaxconverter")
public class RedPenSyntaxConverter implements CheckerSyntaxConverter
{
    @Inject
    private Logger logger;

    @Inject
    private Converter converter;

    private String syntaxConvert(String in, Syntax type)
    {

        String res;
        try {

            WikiPrinter printer = new DefaultWikiPrinter();
            converter.convert((new StringReader(in)), type, Syntax.PLAIN_1_0, printer);
            res = printer.toString();
        } catch (ConversionException c) {
            res = in;
            this.logger.error(c.getMessage());
        }

        return res;
    }

    /**
     * @param input String obtained from XWiki document in XWiki2.1 syntax
     * @return String in plain format
     */
    public String inputConverter(String input)
    {
        return syntaxConvert(input, Syntax.XWIKI_2_1);
    }
}
