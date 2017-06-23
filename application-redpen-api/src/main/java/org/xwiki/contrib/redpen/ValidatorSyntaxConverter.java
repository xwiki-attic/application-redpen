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


package org.xwiki.contrib.redpen;
/**
 * Created by DeSheng on 19/6/2017.
 */

import org.xwiki.component.annotation.Role;

/**
 * Converts syntax of document checkers.
 * @version $Id: $
 * @since 1.0
 */
@Role
public interface ValidatorSyntaxConverter
{
    /**
     *
     * @param input String obtained from XWiki document in XWiki2.1 syntax
     * @return String in plain format
     */
    String inputConverter(String input);

    /**
     *
     * @param output document checker results
     * @return String in plain format
     */
    String outputConverter(String output);
}
