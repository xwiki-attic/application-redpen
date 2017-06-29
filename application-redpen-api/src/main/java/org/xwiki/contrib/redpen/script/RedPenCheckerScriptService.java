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

package org.xwiki.contrib.redpen.script;

/**
 * Created by DeSheng on 14/6/2017.
 */


import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.xwiki.component.annotation.Component;
import org.xwiki.contrib.redpen.ContentChecker;
import org.xwiki.script.service.ScriptService;

import cc.redpen.RedPenException;

/**
 * This allows ProofReaderComponent method to be used within a Velocity or Groovy script.
 * @version $Id: $
 * @since 1.0
 */

@Component
@Named("redpen")
@Singleton
public class RedPenCheckerScriptService implements ScriptService
{
    @Inject
    @Named("redpenchecker")
    private ContentChecker proofreader;

    /**
     *
     * @param input in plain string format
     * @return output XML formatted string
     * @throws RedPenException if RedPen object fails to instantiate
     */
    public String validate(String input)
    {
        return this.proofreader.validate(input);
    }
}
