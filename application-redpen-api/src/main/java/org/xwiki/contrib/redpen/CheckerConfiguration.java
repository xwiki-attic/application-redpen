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

import java.util.List;
import java.util.Map;

import org.xwiki.component.annotation.Role;
import org.xwiki.model.reference.DocumentReference;

import cc.redpen.config.ValidatorConfiguration;

/**
 * Provides methods to toggle and retrieve configuration settings for document checkers.
 *
 * @version $Id: $
 * @since 1.0
 */
@Role
public interface CheckerConfiguration
{
    /**
     * Enumeration of error or warning message to be provided when validated against a particular setting.
     */
    enum SeverityLevel
    {
        ERROR,
        WARNING
    }
    /**
     * @param source Document Reference of the page content checker is running in
     * @return List of Document References where content checker is not allowed to run in
     */
    List<DocumentReference> getExceptionList(DocumentReference source);

    /**
     * @param source Document Reference of page content checker is running in
     * @return List of Document References where content checker is to be run in
     */
    List<DocumentReference> getInclusionList(DocumentReference source);

    /**
     * @return configuration parameters for document checker
     */
    List<ValidatorConfiguration> getValidationSettings();

    /**
     * @return boolean setting for whether automatic validation will start
     */
    boolean willStart();

    /**
     * @return severity level of validators as Map<String,SeverityLevel>
     */
    Map<String, SeverityLevel> getSeverityLevels();

}
