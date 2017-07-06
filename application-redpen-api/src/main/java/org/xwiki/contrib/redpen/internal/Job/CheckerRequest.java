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
package org.xwiki.contrib.redpen.internal.Job;

import org.xwiki.model.reference.DocumentReference;

/**
 * Contains Job status of CheckerJob.
 * @version $Id: $
 * @since 1.0
 */

public class CheckerRequest extends org.xwiki.job.AbstractRequest
{

    private static final String PROPERTY_PAGE_LIST = "checker.pageList";

    private static final String PROPERTY_USER_REFERENCE = "user.reference";

    private static final String PROPERTY_CHECK_RIGHTS = "checkrights";

    /**
     * @return List of spaces user wants to run checker on
     */
    public String getSpaceList()
    {
        return getProperty(PROPERTY_PAGE_LIST);
    }

    /**
     * @param spaceList sets it according to what the user wants
     */
    public void setSpaceList(String spaceList)
    {
        setProperty(PROPERTY_PAGE_LIST, spaceList);
    }

    /**
     * @return boolean indicating whether user has rights to proofread the document
     */
    public boolean isCheckRights()
    {
        return getProperty(PROPERTY_CHECK_RIGHTS, true);
    }

    /**
     * @param checkRights retrieves the rights settings of the user
     */
    public void setCheckRights(boolean checkRights)
    {
        setProperty(PROPERTY_CHECK_RIGHTS, checkRights);
    }

    /**
     * @return the reference pointing to the user starting the checker job
     */
    public DocumentReference getUserReference()
    {
        return getProperty(PROPERTY_USER_REFERENCE);
    }

    /**
     * @param userReference sets the user reference to the person initiating the checker job
     */
    public void setUserReference(DocumentReference userReference)
    {
        setProperty(PROPERTY_USER_REFERENCE, userReference);
    }
}
