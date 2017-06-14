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

import org.xwiki.component.annotation.Role;

import cc.redpen.RedPenException;

/**
 * Created by DeSheng on 13/6/2017.
 */

/**
 * Passes in any string object and instantiates a RedPen object to validate it.
 * @version $Id: $
 * @since 1.0
 */

@Role
public interface ProofReader
{
    /**
     *
     * @param input any String type
     * @return output in the form of json or xml string
     * @throws RedPenException if redpen object fails to be instantiated */
    String renderValidation(String input) throws RedPenException;

}
